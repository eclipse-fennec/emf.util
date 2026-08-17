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
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
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
		assertThat(service.getBranches()).contains("refs/heads/main");
		assertThat(service.getFiles().getFiles()).containsExactly("test");
		assertThat(read("test")).isEqualTo(FILE_CONTENT);
	}

	@Test
	public void testFetchPicksUpNewRemoteCommits() throws Exception {
		commitOnRemote("added.txt", "added");

		assertThat(service.getFiles().getFiles()).as("not visible before the fetch").containsExactly("test");

		service.fetch();

		assertThat(service.getFiles().getFiles()).containsExactlyInAnyOrder("test", "added.txt");
		assertThat(read("added.txt")).isEqualTo("added");
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

	@Test
	public void testPushRejectedByAMovedRemoteIsAConflict() throws Exception {
		// The remote moves on behind the service's back; the commit built on the stale mirror
		// would drop that work, so the remote refuses it.
		commitOnRemote("remote.txt", "remote");
		ObjectId remoteHead = remoteHead();

		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");

		assertThatThrownBy(() -> service.push()) //
				.isInstanceOf(GitConflictException.class) //
				.hasMessageContaining("fetch and retry");
		assertThat(remoteHead()).as("remote left untouched by the rejected push").isEqualTo(remoteHead);
	}

	/** The recovery the conflict message tells the caller to perform actually works. */
	@Test
	public void testFetchAndRetryAfterAConflict() throws Exception {
		commitOnRemote("remote.txt", "remote");
		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		assertThatThrownBy(() -> service.push()).isInstanceOf(GitConflictException.class);

		service.fetch();
		String retried = service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine, again");
		service.push();

		assertThat(remoteHead().getName()).isEqualTo(retried);
		assertThat(remoteContent("mine.txt")).isEqualTo("mine");
		assertThat(remoteContent("remote.txt")).as("the remote's own work survived").isEqualTo("remote");
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
}
