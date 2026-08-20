/*
 * ******************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 * ******************************************************************
 */
package org.eclipse.fennec.jgit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.RefUpdate.Result;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FS;
import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.FileEntry;
import org.eclipse.fennec.jgit.api.GitConfig;
import org.eclipse.fennec.jgit.api.GitService;
import org.eclipse.fennec.jgit.api.TreeResult;
import org.eclipse.fennec.jgit.exceptions.GitConflictException;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.fennec.jgit.exceptions.GitPushException;
import org.eclipse.fennec.jgit.exceptions.GitWriteException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

@Component(configurationPid = "GitConfig", configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true)
@Designate(ocd = GitConfig.class, factory = true)
public class GitServiceImpl implements GitService{
	private static final Logger logger = System.getLogger(GitService.class.getName());
	/**
	 * Where a fetch puts the remote's branches. The remote is configured as a URL,
	 * not as a named remote, so there is exactly one and it is called "origin".
	 */
	private static final String REMOTE_PREFIX = "refs/remotes/origin/";

	private GitConfig config;
	private Repository repo;
	private Git git;
	private FetchCommand fetchCmd;
	/**
	 * Declared as the core jgit type on purpose: a field type is resolved when the
	 * class is loaded, so naming {@code SshdSessionFactory} here would make
	 * {@code org.eclipse.jgit.ssh.apache} — and the Apache MINA sshd stack behind it
	 * — mandatory for every consumer, including one that only ever opens a
	 * repository on disk. The sshd types live in {@link SshdSessionFactoryProvider},
	 * which is not loaded until a configured key makes it necessary.
	 */
	private SshSessionFactory sshSessionFactory;
	private CredentialsProvider credentialsProvider;
	private GitCommitWriter writer;
	/** Serializes everything that moves the branch or talks to the remote. */
	private final Object writeLock = new Object();

	@Activate
	public void activate(GitConfig config) throws IOException, GitAPIException {
		logger.log(Level.INFO, "Start git service with repo {0}", config.repo());
		this.config = config;
		DfsRepositoryDescription repoDesc = new DfsRepositoryDescription();
		if (isRemote(config.repo())) {
			repo = new InMemoryRepository.Builder() //
					.setInitialBranch(config.branch()).setRepositoryDescription(repoDesc) //
					.setFS(FS.detect()).build();
			git = new Git(repo);
			// A remote/in-memory repo must be populated by fetching from config.repo().
			// Local on-disk repos already carry their objects, so they are not fetched.
			fetchCmd = git.fetch();
			fetchCmd.setRemote(config.repo());
			// Into remote-tracking refs, never straight onto the local branches: a fetch that
			// force-writes refs/heads/* discards every commit that has not been pushed yet.
			fetchCmd.setRefSpecs(new RefSpec("+refs/heads/*:" + REMOTE_PREFIX + "*"));
			// Only build the SSH backend when a private key is configured; anonymous git:// and
			// https:// remotes need no SSH stack at all. The backend is the Apache MINA sshd
			// session factory, which (unlike the legacy JSch one) parses OpenSSH-format and
			// ed25519 keys. It is only attached to actual SshTransports.
			// Blank counts as "no key": the shipped example configuration reads the path from
			// an environment variable and leaves it empty when that is unset, and an empty
			// identity path would be offered to every SSH transport.
			if (config.privateKey() != null && !config.privateKey().isBlank()) {
				sshSessionFactory = buildSshSessionFactory();
			}
			// https remotes authenticate with user name and token instead. Reading a public repo
			// needs no credentials, pushing to it does.
			if (config.username() != null && !config.username().isBlank()) {
				credentialsProvider = new UsernamePasswordCredentialsProvider(config.username(),
						config.password() == null ? "" : config.password());
			}
			configureTransport(fetchCmd);
			fetchCmd.call();
			syncBranchWithRemote();
		} else {
			repo = openOnDisk();
			git = new Git(repo);
		}
		repo.getObjectDatabase();
		writer = new GitCommitWriter(repo);
	}

	/**
	 * Opens the repository the configured path points at, and only that one.
	 * <p>
	 * The search is deliberately bounded to the configured directory: it is either a
	 * git directory itself (a bare repository, or a {@code .git} directory) or a
	 * working tree whose {@code .git} sits directly in it. jgit's own discovery would
	 * otherwise walk up the whole file system, so a path that does not exist — a typo,
	 * an unmounted volume, a directory nobody created yet — would silently bind to
	 * whichever repository happens to contain it further up, and every commit would be
	 * written into that stranger's object database. Failing here is the only outcome
	 * that cannot be mistaken for success.
	 *
	 * @return the opened repository
	 * @throws IOException           if the repository is there but cannot be opened
	 * @throws GitAPIException       if the configured branch is not a valid ref name
	 * @throws IllegalStateException if the configured path is not a repository
	 */
	private Repository openOnDisk() throws IOException, GitAPIException {
		File configured = new File(config.repo()).getAbsoluteFile();
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		// findGitDir() stops when the directory it is about to look at is a ceiling, so
		// the parent is the ceiling: the configured directory itself (and a .git in it,
		// including the file form a linked working tree uses) is considered, its
		// ancestors are not.
		File parent = configured.getParentFile();
		if (parent != null) {
			builder.addCeilingDirectory(parent);
		}
		builder.findGitDir(configured);
		if (builder.getGitDir() == null) {
			throw new IllegalStateException(describeMissingRepository(configured));
		}
		Repository repository = builder.setInitialBranch(config.branch()).build();
		logger.log(Level.INFO, "repo dir {0}", repository.getDirectory());
		return repository;
	}

	/**
	 * Says which path was looked at, what was found there, and how to make it a
	 * repository — the configured value alone is not enough, because relative paths
	 * resolve against a working directory the caller does not choose.
	 */
	private String describeMissingRepository(File configured) {
		String found;
		if (!configured.exists()) {
			found = "the directory does not exist";
		} else if (!configured.isDirectory()) {
			found = "the path is a file, not a directory";
		} else {
			found = "the directory exists but is neither a bare repository nor a working tree with a .git in it";
		}
		return "Configured repo '" + config.repo() + "' resolves to " + configured + ", which is not a git repository: "
				+ found + ". Create one with 'git init --bare " + configured
				+ "', point repo at an existing repository, or use a remote URL.";
	}

	/**
	 * Builds the SSH backend, turning the absence of the optional SSH bundles into an
	 * explanation instead of a {@code NoClassDefFoundError} from somewhere inside the
	 * activation.
	 */
	private SshSessionFactory buildSshSessionFactory() {
		try {
			return SshdSessionFactoryProvider.create(config);
		} catch (LinkageError e) {
			throw new IllegalStateException("privateKey is configured for " + config.repo()
					+ ", but the SSH stack is not available: org.eclipse.jgit.ssh.apache and Apache MINA sshd"
					+ " have to be present to authenticate with a key", e);
		}
	}

	/**
	 * Attaches the configured authentication to a transport command. Fetch and push
	 * share it, so a remote that can be read with the configured credentials can
	 * also be written to.
	 */
	private void configureTransport(TransportCommand<?, ?> command) {
		if (sshSessionFactory != null) {
			command.setTransportConfigCallback(t -> {
				if (t instanceof SshTransport) {
					((SshTransport) t).setSshSessionFactory(sshSessionFactory);
				}
			});
		}
		if (credentialsProvider != null) {
			command.setCredentialsProvider(credentialsProvider);
		}
	}

	/**
	 * The SSH backend, or {@code null} when none was built because no private key is
	 * configured. Visible so a test can assert which of the two happened without
	 * reaching for a real SSH server.
	 */
	SshSessionFactory sshSessionFactory() {
		return sshSessionFactory;
	}

	@Override
	public String getBranch() {
		return config.branch();
	}

	@Override
	public String getRef() {
		return "refs/heads/" + config.branch();
	}
	
	@Override
	public String getGitUrl() {
		return config.repo();
	}
	
	@Override
	public void fetch() {
		if (fetchCmd == null) {
			// Local on-disk repo: no remote to fetch from.
			logger.log(Level.INFO, "Skipping fetch for local repo {0}", config.repo());
			return;
		}
		synchronized (writeLock) {
			try {
				fetchCmd.call();
				syncBranchWithRemote();
			} catch (Exception e) {
				throw new RuntimeException("Fetch failed for " + config.repo(), e);
			}
		}
	}

	/**
	 * Lets the configured branch follow what was just fetched, as far as that can be
	 * done without losing anything: it is created if it does not exist yet and
	 * fast-forwarded while the remote is ahead of it. A branch that carries local
	 * commits the remote does not have has diverged, and is left where it is —
	 * moving it is {@link #resetToRemote()}, which says so in its name.
	 */
	private void syncBranchWithRemote() throws IOException {
		ObjectId remoteHead = repo.resolve(remoteRef());
		if (remoteHead == null) {
			logger.log(Level.INFO, "{0} has no branch {1} yet", config.repo(), config.branch());
			return;
		}
		ObjectId localHead = repo.resolve(getRef());
		if (remoteHead.equals(localHead)) {
			return;
		}
		if (localHead != null && !isAncestor(localHead, remoteHead)) {
			logger.log(Level.WARNING,
					"{0} has moved on and {1} carries commits that are not on it; leaving the branch at {2}."
							+ " Reconcile against getRemoteHead() and call resetToRemote() to take the remote''s side",
					config.repo(), getRef(), localHead.getName());
			return;
		}
		moveBranchTo(remoteHead, localHead, localHead == null ? "fetch: created" : "fetch: fast-forward");
	}

	/**
	 * Whether the remote's head builds on the local one, i.e. the local branch can
	 * be fast-forwarded onto it without dropping a commit.
	 */
	private boolean isAncestor(ObjectId localHead, ObjectId remoteHead) throws IOException {
		try (RevWalk revWalk = new RevWalk(repo)) {
			return revWalk.isMergedInto(revWalk.parseCommit(localHead), revWalk.parseCommit(remoteHead));
		}
	}

	private void moveBranchTo(ObjectId target, ObjectId previous, String reflogMessage) throws IOException {
		RefUpdate refUpdate = repo.updateRef(getRef());
		refUpdate.setNewObjectId(target);
		refUpdate.setForceUpdate(true);
		refUpdate.setRefLogMessage(reflogMessage, false);
		Result result = refUpdate.update();
		switch (result) {
		case NEW:
		case FAST_FORWARD:
		case FORCED:
		case NO_CHANGE:
			logger.log(Level.INFO, "{0} now at {1} ({2}, was {3})", getRef(), target.getName(), result,
					previous == null ? "unborn" : previous.getName());
			return;
		default:
			throw new GitWriteException("Unable to move " + getRef() + " to " + target.getName() + ": " + result);
		}
	}

	private String remoteRef() {
		return REMOTE_PREFIX + config.branch();
	}

	@Override
	public String getRemoteHead() {
		if (fetchCmd == null) {
			// Local on-disk repo: there is no remote whose head could be reported.
			return null;
		}
		try {
			ObjectId remoteHead = repo.resolve(remoteRef());
			return remoteHead == null ? null : remoteHead.getName();
		} catch (IOException e) {
			throw new RuntimeException("Unable to read " + remoteRef(), e);
		}
	}

	@Override
	public String resetToRemote() {
		synchronized (writeLock) {
			if (fetchCmd == null) {
				// Local on-disk repo: no remote to reset to, the branch is already the truth.
				logger.log(Level.INFO, "Skipping reset for local repo {0}", config.repo());
				return headId();
			}
			try {
				ObjectId remoteHead = repo.resolve(remoteRef());
				if (remoteHead == null) {
					throw new GitWriteException(
							remoteRef() + " is unknown; fetch() before resetting to the remote");
				}
				moveBranchTo(remoteHead, repo.resolve(getRef()), "reset: to " + remoteRef());
				return remoteHead.getName();
			} catch (GitWriteException e) {
				throw e;
			} catch (IOException e) {
				throw new GitWriteException("Unable to reset " + getRef() + " to " + remoteRef(), e);
			}
		}
	}

	/** The commit the configured branch points at, or {@code null} if it has none. */
	private String headId() {
		try {
			ObjectId head = repo.resolve(getRef());
			return head == null ? null : head.getName();
		} catch (IOException e) {
			throw new RuntimeException("Unable to read " + getRef(), e);
		}
	}
	
	static boolean isRemote(String repo) {
		// A remote is a URL (git://, ssh://, http(s)://) or an scp-style git@host:path; anything
		// else is a path on disk. The scheme has to be matched in full: a directory named
		// "gitRepo" or "sshkeys" merely starts with the letters of one and is still a directory.
		return repo.startsWith("git://") //
				|| repo.startsWith("git@") //
				|| repo.startsWith("ssh://") //
				|| repo.startsWith("http://") //
				|| repo.startsWith("https://");
	}

	@Deactivate
	public void deactivate() {
		// SshdSessionFactory is Closeable, SshSessionFactory is not - and naming the former
		// here would put the SSH stack back into the class's own resolution.
		if (sshSessionFactory instanceof Closeable closeable) {
			try {
				closeable.close();
			} catch (IOException e) {
				logger.log(Level.WARNING, () -> "Unable to close the SSH session factory", e);
			}
		}
		if (repo != null) {
			repo.close();
		}
	}
	
	@Override
	public TreeResult getFiles() {
		return getFiles(null);
	}
	
	@Override
	public TreeResult getFiles(String basepath)  {
		try {
			ObjectId lastCommitId = repo.resolve(getRef());
			if (lastCommitId == null) {
				// A branch without commits is empty, not broken: a repository that was just
				// created is in exactly that state, and listing it must not fail.
				logger.log(Level.DEBUG, "{0} has no commits yet, listing nothing", getRef());
				return TreeResult.of(null, List.of());
			}
			try (RevWalk revWalk = new RevWalk(repo); TreeWalk treeWalk = new TreeWalk(repo)) {
				RevCommit commit = revWalk.parseCommit(lastCommitId);
				RevTree tree = commit.getTree();
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				if(basepath != null) {
					treeWalk.setFilter(PathFilter.create(basepath));
				}
				List<FileEntry> files = new ArrayList<>();
				while(treeWalk.next()) {
					// The blob id is right there in the walk, and it is the only per-file content
					// hash a caller can get without reading the whole file back.
					files.add(new FileEntry(treeWalk.getPathString(), treeWalk.getObjectId(0).getName()));
				}
				return TreeResult.of(ObjectId.toString(lastCommitId), files);
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to list files for basepath " + basepath, e);
		}
	}

	@Override
	public void loadLatestFile(String file, OutputStream out) {
		loadFile(null, file, out);
	}

	@Override
	public InputStream readLatestFile(String file) {
		return readFile(null, file); 
	}

	@Override
	public InputStream readFile(String commitId, String file) {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			loadFile(commitId, file, byteArrayOutputStream);
			ByteArrayInputStream bais = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			return bais;
		} catch (GitFileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Unable to load file " + file, e);
		}
	}

	@Override
	public void loadFile(String commitId, String file, OutputStream out) {
		ObjectId blob = findBlob(commitId, file).orElseThrow(() -> new GitFileNotFoundException(
				file + " does not exist in " + (commitId == null ? getRef() : commitId)));
		try {
			ObjectLoader loader = repo.open(blob);
			loader.copyTo(out);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load file " + file, e);
		}
	}

	@Override
	public boolean exists(String commitId, String file) {
		return findBlob(commitId, file).isPresent();
	}

	@Override
	public Optional<String> blobId(String commitId, String file) {
		return findBlob(commitId, file).map(ObjectId::getName);
	}

	/**
	 * Looks a single path up in the tree of a commit, without reading the content.
	 * This is what {@link #exists(String, String)} and
	 * {@link #blobId(String, String)} answer with, and what a read starts from.
	 *
	 * @param commitId the commit to look in, or {@code null} for the branch head
	 * @return the id of the blob at that path, or empty if there is none there —
	 *         including when the branch has no commits yet
	 */
	private Optional<ObjectId> findBlob(String commitId, String file) {
		Objects.requireNonNull(file, "file");
		try {
			ObjectId theCommitId;
			if (commitId == null) {
				theCommitId = repo.resolve(getRef());
				if (theCommitId == null) {
					// No commits on the branch yet: nothing is in it, which is not an error.
					return Optional.empty();
				}
			} else {
				theCommitId = ObjectId.fromString(commitId);
			}
			try (RevWalk revWalk = new RevWalk(repo); TreeWalk treeWalk = new TreeWalk(repo)) {
				RevCommit commit = revWalk.parseCommit(theCommitId);
				RevTree tree = commit.getTree();
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(PathFilter.create(file));
				// The filter also lets the content of a directory of that name through, so only an
				// exact hit is the file that was asked for.
				while (treeWalk.next()) {
					if (file.equals(treeWalk.getPathString())) {
						return Optional.of(treeWalk.getObjectId(0));
					}
				}
				return Optional.empty();
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to look up file " + file, e);
		}
	}
	
	@Override
	public List<String> getBranches() {
		try {
			// A mirror keeps the remote's branches as remote-tracking refs and has a local one
			// only for the configured branch, so listing both is what shows the remote's.
			List<Ref> branches = git.branchList() //
					.setListMode(fetchCmd == null ? null : ListMode.ALL) //
					.call();
			return branches.stream().map(Ref::getName).collect(Collectors.toList());
		} catch (GitAPIException e) {
			logger.log(Level.ERROR, () -> "Error getting branch list.", e);
			return Collections.emptyList();
		}
	}

	@Override
	public Iterable<RevCommit> getLog() throws GitAPIException {
		ObjectId head;
		try {
			head = repo.resolve(getRef());
		} catch (IOException e) {
			throw new RuntimeException("Unable to read " + getRef(), e);
		}
		if (head == null) {
			// A branch without commits has an empty history, which is not the same as an
			// error - the branch simply has not been written to yet.
			return List.of();
		}
		try {
			// Walk the configured branch, not HEAD: the in-memory mirror of a remote has no
			// HEAD at all (log() would fail with NoHeadException), and a repository on disk
			// may have a different branch checked out than the one this service was
			// configured for.
			return git.log().add(head).call();
		} catch (IOException e) {
			throw new RuntimeException("Unable to read the history of " + getRef(), e);
		}
	}

	@Override
	public String commit(CommitRequest request) {
		Objects.requireNonNull(request, "request");
		synchronized (writeLock) {
			String before = headId();
			ObjectId commitId = writer.commit(getRef(), request, author(request));
			if (commitId.getName().equals(before)) {
				logger.log(Level.INFO, "{0} change(s) left {1} unchanged at {2}", request.getChanges().size(),
						getRef(), before);
			} else {
				logger.log(Level.INFO, "Committed {0} change(s) as {1} on {2}", request.getChanges().size(),
						commitId.getName(), getRef());
			}
			if (shouldPush(request)) {
				pushInternal();
			}
			return commitId.getName();
		}
	}

	@Override
	public String writeFile(String path, byte[] content, String message) {
		return commit(CommitRequest.builder(message).put(path, content).build());
	}

	@Override
	public String writeFile(String path, InputStream content, String message) {
		return commit(CommitRequest.builder(message).put(path, content).build());
	}

	@Override
	public String deleteFile(String path, String message) {
		return commit(CommitRequest.builder(message).delete(path).build());
	}

	@Override
	public void push() {
		synchronized (writeLock) {
			pushInternal();
		}
	}

	private boolean shouldPush(CommitRequest request) {
		Boolean requested = request.getPush();
		return requested == null ? config.pushOnCommit() : requested.booleanValue();
	}

	/**
	 * Resolves the identity for a commit: what the request asks for, falling back to
	 * the configured author.
	 */
	private PersonIdent author(CommitRequest request) {
		String name = request.getAuthorName() == null ? config.authorName() : request.getAuthorName();
		String email = request.getAuthorEmail() == null ? config.authorEmail() : request.getAuthorEmail();
		return new PersonIdent(name, email);
	}

	private void pushInternal() {
		if (!isRemote(config.repo())) {
			// Local on-disk repo: no remote to push to, the commit is already where it belongs.
			logger.log(Level.INFO, "Skipping push for local repo {0}", config.repo());
			return;
		}
		try {
			PushCommand pushCmd = git.push() //
					.setRemote(config.repo()) //
					.setRefSpecs(new RefSpec(getRef() + ":" + getRef()));
			configureTransport(pushCmd);
			for (PushResult result : pushCmd.call()) {
				for (RemoteRefUpdate update : result.getRemoteUpdates()) {
					checkPushed(update);
				}
			}
		} catch (GitWriteException e) {
			throw e;
		} catch (Exception e) {
			// The commit itself is already written and on the branch; only the copy to the
			// remote failed, which a later push() can still complete.
			throw new GitPushException("Push failed for " + config.repo(), e);
		}
	}

	/**
	 * A push that reaches the remote still reports per-ref whether it was accepted,
	 * so a rejection must be read off the result instead of an exception.
	 */
	private void checkPushed(RemoteRefUpdate update) {
		switch (update.getStatus()) {
		case OK:
		case UP_TO_DATE:
			return;
		case REJECTED_NONFASTFORWARD:
			throw new GitConflictException(config.repo() + " rejected " + update.getRemoteName()
					+ " as non-fast-forward: it carries commits this repository does not have."
					+ " fetch(), reconcile against getRemoteHead(), then resetToRemote() and re-apply the changes");
		default:
			throw new GitPushException("Push of " + update.getRemoteName() + " to " + config.repo() + " failed: "
					+ update.getStatus() + (update.getMessage() == null ? "" : " - " + update.getMessage()));
		}
	}
}
