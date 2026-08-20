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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.exceptions.GitConflictException;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.fennec.jgit.exceptions.GitPushException;
import org.eclipse.fennec.jgit.exceptions.GitWriteException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.Daemon;
import org.eclipse.jgit.transport.DaemonService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Drives the component against a remote served over the anonymous git protocol by
 * JGit's own {@link Daemon} — the case where the service holds an in-memory mirror
 * with no working tree at all, and where committing and pushing are two separate
 * things.
 */
public class GitServiceImplRemoteTest {

	private static final String FILE_CONTENT = "fooBar";

	@TempDir
	Path tempDir;

	private Path servedDir;
	private Git served;
	private Daemon daemon;
	private String url;
	private GitServiceImpl service;

	@BeforeEach
	public void before() throws Exception {
		servedDir = tempDir.resolve("served");
		Files.createDirectories(servedDir);
		served = Git.init().setDirectory(servedDir.toFile()).setInitialBranch("main").call();
		Files.writeString(servedDir.resolve("test"), FILE_CONTENT);
		served.add().addFilepattern("test").call();
		served.commit().setAuthor("Hans Wurst", "hw@example.com").setMessage("initial").call();

		// The served repo has a working tree, and git refuses by default to move the branch that
		// is checked out there. A real remote would be bare; here the working tree is simply
		// allowed to fall behind.
		StoredConfig servedConfig = served.getRepository().getConfig();
		servedConfig.setString("receive", null, "denyCurrentBranch", "ignore");
		servedConfig.save();

		// Daemon.start() rebinds the socket, so the port is known only afterwards.
		daemon = new Daemon(new InetSocketAddress("127.0.0.1", 0));
		enable(daemon.getService("upload-pack"));
		enable(daemon.getService("receive-pack"));
		daemon.setRepositoryResolver((req, name) -> served.getRepository());
		daemon.start();
		url = "git://127.0.0.1:" + daemon.getAddress().getPort() + "/served";

		service = new GitServiceImpl();
		service.activate(new TestGitConfig(url));
	}

	private static void enable(DaemonService daemonService) {
		daemonService.setEnabled(true);
		daemonService.setOverridable(false);
	}

	@AfterEach
	public void after() throws Exception {
		if (service != null) {
			service.deactivate();
		}
		if (daemon != null) {
			daemon.stop();
		}
		if (served != null) {
			served.close();
		}
	}

	// --- reading -----------------------------------------------------------------

	@Test
	public void testRemoteIsFetchedIntoMemoryOnActivate() throws Exception {
		assertThat(service.getGitUrl()).isEqualTo(url);
		assertThat(service.getRemoteUrl()).as("the mirrored repo is its own remote").isEqualTo(url);
		assertThat(service.getBranches()).contains("refs/heads/main");
		assertThat(service.getFiles().getFiles()).containsExactly("test");
		assertThat(read("test")).isEqualTo(FILE_CONTENT);
	}

	/**
	 * The mirror has no {@code HEAD} — nothing checks anything out in it — so a history
	 * read has to start from the configured branch rather than from where the repository
	 * thinks it is standing.
	 */
	@Test
	public void testGetLogOnTheMirror() throws Exception {
		assertThat(service.getLog()).extracting(RevCommit::getFullMessage).containsExactly("initial");
	}

	@Test
	public void testFetchPicksUpNewRemoteCommits() throws Exception {
		commitOnRemote("added.txt", "added");

		assertThat(service.getFiles().getFiles()).as("not visible before the fetch").containsExactly("test");

		service.fetch();

		assertThat(service.getFiles().getFiles()).containsExactlyInAnyOrder("test", "added.txt");
		assertThat(read("added.txt")).isEqualTo("added");
	}

	/**
	 * The shipped example configuration reads the key path from an environment
	 * variable and leaves it empty when that is unset, so a blank path is the normal
	 * case for an anonymous or https remote — and must not be offered to the SSH stack
	 * as an identity.
	 */
	@Test
	public void testABlankPrivateKeyIsNoKey() throws Exception {
		service.deactivate();
		service = new GitServiceImpl();

		service.activate(new TestGitConfig(url).privateKey("").privateKeyPassphrase(""));

		// Not merely unused: Apache MINA sshd is not even on this test's class path, so a
		// backend would fail to build here. The configured-key case is covered end to end by
		// the OSGi integration test GitSshTransportTest.
		assertThat(service.sshSessionFactory()).as("no SSH backend for a blank key").isNull();
		assertThat(service.getFiles().getFiles()).containsExactly("test");
	}

	// --- writing -----------------------------------------------------------------

	@Test
	public void testCommitStaysLocalUntilPushed() throws Exception {
		ObjectId remoteHead = remoteHead();

		String commitId = service.writeFile("written.txt", "written".getBytes(StandardCharsets.UTF_8), "add written");

		assertThat(service.getFiles().getFiles()).contains("written.txt");
		assertThat(remoteHead()).as("remote untouched by the commit").isEqualTo(remoteHead);

		service.push();

		assertThat(remoteHead().getName()).isEqualTo(commitId);
		assertThat(remoteContent("written.txt")).isEqualTo("written");
	}

	@Test
	public void testPushOnCommitSendsEveryCommitStraightAway() throws Exception {
		service.deactivate();
		service = new GitServiceImpl();
		service.activate(new TestGitConfig(url).pushOnCommit(true));

		String commitId = service.writeFile("auto.txt", "auto".getBytes(StandardCharsets.UTF_8), "auto push");

		assertThat(remoteHead().getName()).isEqualTo(commitId);
		assertThat(remoteContent("auto.txt")).isEqualTo("auto");
	}

	@Test
	public void testRequestCanForceAPushAgainstTheConfiguredDefault() throws Exception {
		String commitId = service.commit(CommitRequest.builder("forced push") //
				.put("forced.txt", "forced".getBytes(StandardCharsets.UTF_8)) //
				.push(true) //
				.build());

		assertThat(remoteHead().getName()).isEqualTo(commitId);
	}

	@Test
	public void testRequestCanSuppressAPushAgainstTheConfiguredDefault() throws Exception {
		service.deactivate();
		service = new GitServiceImpl();
		service.activate(new TestGitConfig(url).pushOnCommit(true));
		ObjectId remoteHead = remoteHead();

		service.commit(CommitRequest.builder("held back") //
				.put("held.txt", "held".getBytes(StandardCharsets.UTF_8)) //
				.push(false) //
				.build());

		assertThat(remoteHead()).isEqualTo(remoteHead);
	}

	@Test
	public void testSeveralCommitsAreSentByOnePush() throws Exception {
		service.writeFile("one.txt", "1".getBytes(StandardCharsets.UTF_8), "one");
		String second = service.writeFile("two.txt", "2".getBytes(StandardCharsets.UTF_8), "two");

		service.push();

		assertThat(remoteHead().getName()).isEqualTo(second);
		assertThat(remoteContent("one.txt")).isEqualTo("1");
		assertThat(remoteContent("two.txt")).isEqualTo("2");
	}

	@Test
	public void testPushWithNothingToSendIsHarmless() throws Exception {
		service.writeFile("one.txt", "1".getBytes(StandardCharsets.UTF_8), "one");
		service.push();
		ObjectId afterFirstPush = remoteHead();

		service.push(); // up to date, must not be reported as a failure

		assertThat(remoteHead()).isEqualTo(afterFirstPush);
	}

	// --- what the remote is known to have --------------------------------------

	/**
	 * {@code getRemoteHead()} is the only thing that answers "is my work on the
	 * remote?", and a readiness check asking it right after a successful push must not
	 * be told the work is still unsent. The remote-tracking ref used to be written by
	 * a fetch alone, so with {@code pushOnCommit} every commit left the two heads
	 * looking permanently diverged.
	 */
	@Test
	public void testPushOnCommitUpdatesTheKnownRemoteHead() throws Exception {
		service.deactivate();
		service = new GitServiceImpl();
		service.activate(new TestGitConfig(url).pushOnCommit(true));

		String commitId = service.writeFile("auto.txt", "auto".getBytes(StandardCharsets.UTF_8), "auto push");

		assertThat(service.getRemoteHead()).as("the pushed commit is what the remote has").isEqualTo(commitId);
		assertThat(service.getRemoteHead()).isEqualTo(remoteHead().getName());
	}

	@Test
	public void testExplicitPushUpdatesTheKnownRemoteHead() throws Exception {
		String commitId = service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		assertThat(service.getRemoteHead()).as("nothing sent yet").isNotEqualTo(commitId);

		service.push();

		assertThat(service.getRemoteHead()).isEqualTo(commitId);
		assertThat(service.getRemoteHead()).isEqualTo(remoteHead().getName());
	}

	/**
	 * The other half of the same question: a commit that was deliberately not pushed
	 * has to keep reading as unsent, or the answer is worthless in the other
	 * direction.
	 */
	@Test
	public void testACommitThatWasNotPushedLeavesTheKnownRemoteHeadBehind() throws Exception {
		String before = service.getRemoteHead();

		String commitId = service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");

		assertThat(service.getRemoteHead()).as("still what the remote had").isEqualTo(before);
		assertThat(service.getRemoteHead()).as("the local commit is not on the remote").isNotEqualTo(commitId);
	}

	/** A push with nothing to send reports UP_TO_DATE, which is also an answer about the remote. */
	@Test
	public void testASecondPushWithNothingToSendKeepsTheKnownRemoteHead() throws Exception {
		String commitId = service.writeFile("one.txt", "1".getBytes(StandardCharsets.UTF_8), "one");
		service.push();

		service.push(); // up to date

		assertThat(service.getRemoteHead()).isEqualTo(commitId);
	}

	/** A rejected push moved nothing on the remote, so it must not move what we think it has. */
	@Test
	public void testARejectedPushLeavesTheKnownRemoteHeadWhereItWas() throws Exception {
		commitOnRemote("remote.txt", "remote");
		String before = service.getRemoteHead();
		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");

		assertThatThrownBy(() -> service.push()).isInstanceOf(GitConflictException.class);

		assertThat(service.getRemoteHead()).isEqualTo(before);
	}

	/**
	 * A commit that was written but could not be sent is not the same failure as a
	 * commit that was never written: the change is recorded and pushing again would
	 * complete it, so a caller can retry cheaply instead of redoing the work — which
	 * it can only decide if the exception says which of the two happened.
	 */
	@Test
	public void testAFailedPushAfterACommitIsAPushFailure() throws Exception {
		daemon.stop(); // the remote goes away between the commit and the push

		assertThatThrownBy(() -> service.commit(CommitRequest.builder("unsendable") //
				.put("mine.txt", "mine".getBytes(StandardCharsets.UTF_8)) //
				.push(true) //
				.build())) //
				.isInstanceOf(GitPushException.class) //
				.isInstanceOf(GitWriteException.class); // the old catch blocks still catch it

		// The commit survived; only the copy to the remote did not happen.
		assertThat(read("mine.txt")).isEqualTo("mine");
		assertThat(service.getLog().iterator().next().getFullMessage()).isEqualTo("unsendable");
	}

	/** A conflict is its own case and must not be flattened into the push failure. */
	@Test
	public void testARejectedPushIsAConflictNotAPlainPushFailure() throws Exception {
		commitOnRemote("remote.txt", "remote");
		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");

		assertThatThrownBy(() -> service.push()) //
				.isInstanceOf(GitConflictException.class) //
				.isNotInstanceOf(GitPushException.class);
	}

	@Test
	public void testPushRejectedByAMovedRemoteIsAConflict() throws Exception {
		// The remote moves on behind the service's back; the commit built on the stale mirror
		// would drop that work, so the remote refuses it.
		commitOnRemote("remote.txt", "remote");
		ObjectId remoteHead = remoteHead();

		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");

		assertThatThrownBy(() -> service.push()) //
				.isInstanceOf(GitConflictException.class) //
				.hasMessageContaining("resetToRemote");
		assertThat(remoteHead()).as("remote left untouched by the rejected push").isEqualTo(remoteHead);
	}

	/** The recovery the conflict message tells the caller to perform actually works. */
	@Test
	public void testFetchResetAndRetryAfterAConflict() throws Exception {
		commitOnRemote("remote.txt", "remote");
		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		assertThatThrownBy(() -> service.push()).isInstanceOf(GitConflictException.class);

		service.fetch();
		// The remote's side is readable before anything local is given up.
		assertThat(read(service.getRemoteHead(), "remote.txt")).isEqualTo("remote");
		service.resetToRemote();
		String retried = service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine, again");
		service.push();

		assertThat(remoteHead().getName()).isEqualTo(retried);
		assertThat(remoteContent("mine.txt")).isEqualTo("mine");
		assertThat(remoteContent("remote.txt")).as("the remote's own work survived").isEqualTo("remote");
	}

	/**
	 * The one thing the old force-fetching refspec got wrong: a fetch is a read of the
	 * remote, and must not throw away a commit that has not been pushed yet — the
	 * conflict message used to send callers straight into exactly that.
	 */
	@Test
	public void testFetchKeepsUnpushedCommits() throws Exception {
		String mine = service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		commitOnRemote("remote.txt", "remote");

		service.fetch();

		assertThat(service.getFiles().getCommitId()).as("local branch left where it was").isEqualTo(mine);
		assertThat(read("mine.txt")).isEqualTo("mine");
		assertThat(service.getRemoteHead()).as("the remote's side is visible").isEqualTo(remoteHead().getName());
		assertThat(read(service.getRemoteHead(), "remote.txt")).isEqualTo("remote");
	}

	/** Discarding local commits is possible, but only when it is asked for by name. */
	@Test
	public void testResetToRemoteDiscardsLocalCommits() throws Exception {
		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		commitOnRemote("remote.txt", "remote");
		service.fetch();

		String head = service.resetToRemote();

		assertThat(head).isEqualTo(remoteHead().getName());
		assertThat(service.getFiles().getFiles()).containsExactlyInAnyOrder("test", "remote.txt");
		assertThatThrownBy(() -> service.readLatestFile("mine.txt")).isInstanceOf(GitFileNotFoundException.class);
	}

	/** Without a fetch there is nothing to reset to, and saying so beats resetting to nothing. */
	@Test
	public void testResetToRemoteNeedsAFetchedBranch() throws Exception {
		service.deactivate();
		service = new GitServiceImpl();
		service.activate(new TestGitConfig(url).branch("other"));

		assertThatThrownBy(() -> service.resetToRemote()) //
				.isInstanceOf(GitWriteException.class) //
				.hasMessageContaining("refs/remotes/origin/other");
	}

	// --- helpers -----------------------------------------------------------------

	private void commitOnRemote(String path, String content) throws Exception {
		Files.writeString(servedDir.resolve(path), content);
		served.add().addFilepattern(path).call();
		served.commit().setAuthor("Erika Mustermann", "em@example.com").setMessage("remote " + path).call();
	}

	private ObjectId remoteHead() throws IOException {
		return served.getRepository().resolve("refs/heads/main");
	}

	private String remoteContent(String path) throws IOException {
		Repository repository = served.getRepository();
		ObjectId blob = repository.resolve("refs/heads/main:" + path);
		assertThat(blob).as("%s present in the remote", path).isNotNull();
		return new String(repository.open(blob).getBytes(), StandardCharsets.UTF_8);
	}

	private String read(String path) throws IOException {
		try (InputStream in = service.readLatestFile(path)) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private String read(String commitId, String path) throws IOException {
		try (InputStream in = service.readFile(commitId, path)) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
