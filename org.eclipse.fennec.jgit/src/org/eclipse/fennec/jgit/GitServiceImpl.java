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
import java.util.stream.Collectors;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteRefUpdate;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.KeyPasswordProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactory;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FS;
import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.GitConfig;
import org.eclipse.fennec.jgit.api.GitService;
import org.eclipse.fennec.jgit.api.TreeResult;
import org.eclipse.fennec.jgit.exceptions.GitConflictException;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.fennec.jgit.exceptions.GitWriteException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;

@Component(configurationPid = "GitConfig", configurationPolicy = ConfigurationPolicy.REQUIRE, immediate = true)
@Designate(ocd = GitConfig.class, factory = true)
public class GitServiceImpl implements GitService{
	/**
	 * Supplies the configured passphrase to the Apache MINA sshd backend when it
	 * loads an encrypted private key. A single fixed passphrase is configured, so
	 * a failed load is never retried (that would only reuse the same passphrase).
	 */
	private final class ConfiguredKeyPasswordProvider implements KeyPasswordProvider {

		@Override
		public char[] getPassphrase(URIish uri, int attempt) {
			String passphrase = config.privateKeyPassphrase();
			return passphrase == null ? new char[0] : passphrase.toCharArray();
		}

		@Override
		public void setAttempts(int maxNumberOfAttempts) {
			// Only the single configured passphrase is available; retries make no sense.
		}

		@Override
		public boolean keyLoaded(URIish uri, int attempt, Exception error) {
			// Never retry: either the configured passphrase worked or the load fails.
			return false;
		}
	}

	private static final Logger logger = System.getLogger(GitService.class.getName());
	private GitConfig config;
	private Repository repo;
	private Git git;
	private FetchCommand fetchCmd;
	private SshdSessionFactory sshSessionFactory;
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
			fetchCmd.setRefSpecs(new RefSpec("+refs/heads/*:refs/heads/*"));
			// Only build the SSH backend when a private key is configured; anonymous git:// and
			// https:// remotes need no SSH stack at all. The backend is the Apache MINA sshd
			// session factory, which (unlike the legacy JSch one) parses OpenSSH-format and
			// ed25519 keys. It is only attached to actual SshTransports.
			if (config.privateKey() != null) {
				sshSessionFactory = createSshSessionFactory();
			}
			// https remotes authenticate with user name and token instead. Reading a public repo
			// needs no credentials, pushing to it does.
			if (config.username() != null && !config.username().isBlank()) {
				credentialsProvider = new UsernamePasswordCredentialsProvider(config.username(),
						config.password() == null ? "" : config.password());
			}
			configureTransport(fetchCmd);
			fetchCmd.call();
		} else {
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			File gitDir = new File(config.repo());
			repo = builder.setInitialBranch(config.branch()) // set branch
					.findGitDir(gitDir) // scan up the file system tree
					.build();
			logger.log(Level.INFO, "repo dir {0}", repo.getDirectory());
			git = new Git(repo);
		}
		repo.getObjectDatabase();
		writer = new GitCommitWriter(repo);
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
		try {
			fetchCmd.call();
		} catch (Exception e) {
			throw new RuntimeException("Fetch failed for " + config.repo(), e);
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

	/**
	 * Builds an Apache MINA sshd session factory that authenticates with exactly the
	 * configured private key. The key is supplied as a lazily-loaded identity path (so
	 * OpenSSH-format and ed25519 keys are parsed by MINA/BouncyCastle), and its passphrase
	 * comes from {@link ConfiguredKeyPasswordProvider}. Host-key verification and the
	 * default known-hosts location (~/.ssh) are left at MINA's defaults.
	 */
	private SshdSessionFactory createSshSessionFactory() {
		File keyFile = new File(config.privateKey());
		// SshdSessionFactoryBuilder does not default the home directory; build() NPEs on a null
		// one. Use the process user's home (and its .ssh), falling back when user.home is unset
		// (e.g. a bare OSGi launcher). It must be absolute.
		String userHome = System.getProperty("user.home");
		File homeDir = (userHome == null || userHome.isBlank())
				? new File(System.getProperty("java.io.tmpdir")).getAbsoluteFile()
				: new File(userHome);
		SshdSessionFactoryBuilder builder = new SshdSessionFactoryBuilder() //
				.setPreferredAuthentications("publickey") //
				.setHomeDirectory(homeDir) //
				.setSshDirectory(new File(homeDir, ".ssh")) //
				.setDefaultIdentities(sshDir -> List.of(keyFile.toPath())) //
				.setKeyPasswordProvider(cp -> new ConfiguredKeyPasswordProvider());
		// Verify the server host key against the configured known_hosts file; when none is
		// configured, MINA falls back to its default (~/.ssh/known_hosts).
		String knownHosts = config.knownHosts();
		if (knownHosts != null && !knownHosts.isBlank()) {
			File knownHostsFile = new File(knownHosts);
			builder.setDefaultKnownHostsFiles(sshDir -> List.of(knownHostsFile.toPath()));
		}
		return builder.build(new JGitKeyCache());
	}

	@Deactivate
	public void deactivate() {
		if (sshSessionFactory != null) {
			sshSessionFactory.close();
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
		String branch = config.branch();
		ObjectId lastCommitId;
		try {
			lastCommitId = repo.resolve("refs/heads/" + branch);
			try (RevWalk revWalk = new RevWalk(repo); TreeWalk treeWalk = new TreeWalk(repo)) {
				RevCommit commit = revWalk.parseCommit(lastCommitId);
				RevTree tree = commit.getTree();
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
				if(basepath != null) {
					treeWalk.setFilter(PathFilter.create(basepath));
				}
				List<String> files = new ArrayList<>();
				while(treeWalk.next()) {
					files.add(treeWalk.getPathString());
				}
				return new TreeResult(ObjectId.toString(lastCommitId), files);
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
		String branch = config.branch();
		ObjectId theCommitId;
		try {
			if(commitId == null) {
				theCommitId = repo.resolve("refs/heads/" + branch);
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
						ObjectId objectId = treeWalk.getObjectId(0);
						ObjectLoader loader = repo.open(objectId);
						loader.copyTo(out);
						return;
					}
				}
				throw new GitFileNotFoundException(
						file + " does not exist in " + (commitId == null ? getRef() : commitId));
			}
		} catch (GitFileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Unable to load file " + file, e);
		}
	}
	
	@Override
	public List<String> getBranches() {
		try {
			List<Ref> branches = git.branchList().call();
			return branches.stream().map(Ref::getName).collect(Collectors.toList());
		} catch (GitAPIException e) {
			logger.log(Level.ERROR, () -> "Error getting branch list.", e);
			return Collections.emptyList();
		}
	}

	@Override
	public Iterable<RevCommit> getLog() throws GitAPIException {
		return git.log().call();

	}

	@Override
	public String commit(CommitRequest request) {
		Objects.requireNonNull(request, "request");
		synchronized (writeLock) {
			ObjectId commitId = writer.commit(getRef(), request, author(request));
			logger.log(Level.INFO, "Committed {0} change(s) as {1} on {2}", request.getChanges().size(),
					commitId.getName(), getRef());
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
			throw new GitWriteException("Push failed for " + config.repo(), e);
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
					+ " as non-fast-forward, fetch and retry");
		default:
			throw new GitWriteException("Push of " + update.getRemoteName() + " to " + config.repo() + " failed: "
					+ update.getStatus() + (update.getMessage() == null ? "" : " - " + update.getMessage()));
		}
	}
}
