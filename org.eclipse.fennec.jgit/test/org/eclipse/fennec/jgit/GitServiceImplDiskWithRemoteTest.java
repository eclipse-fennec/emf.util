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
 * The third mode: a repository <em>on disk</em> that is also mirrored to a remote.
 * <p>
 * The two older modes each give up one half of what a production store needs — an
 * on-disk repository is durable but isolated, an in-memory mirror talks to a remote
 * but loses everything when the process dies. Configuring {@code repo} as a path and
 * {@code remote} as a URL is both: the commit is on the volume the moment it is
 * written, and it is pushed upstream as well.
 */
public class GitServiceImplDiskWithRemoteTest {

	private static final String FILE_CONTENT = "fooBar";

	@TempDir
	Path tempDir;

	private Path servedDir;
	private Git served;
	private Daemon daemon;
	private String url;
	private Path localDir;
	private GitServiceImpl service;

	@BeforeEach
	public void before() throws Exception {
		servedDir = tempDir.resolve("served");
		Files.createDirectories(servedDir);
		served = Git.init().setDirectory(servedDir.toFile()).setInitialBranch("main").call();
		Files.writeString(servedDir.resolve("test"), FILE_CONTENT);
		served.add().addFilepattern("test").call();
		served.commit().setAuthor("Hans Wurst", "hw@example.com").setMessage("initial").call();

		// A real remote would be bare; here the served working tree is simply allowed to fall
		// behind the branch that is pushed to it.
		StoredConfig servedConfig = served.getRepository().getConfig();
		servedConfig.setString("receive", null, "denyCurrentBranch", "ignore");
		servedConfig.save();

		daemon = new Daemon(new InetSocketAddress("127.0.0.1", 0));
		enable(daemon.getService("upload-pack"));
		enable(daemon.getService("receive-pack"));
		daemon.setRepositoryResolver((req, name) -> served.getRepository());
		daemon.start();
		url = "git://127.0.0.1:" + daemon.getAddress().getPort() + "/served";

		// The local side is a bare repository on a "volume": no working tree, and the
		// service is the only thing writing to it.
		localDir = tempDir.resolve("store.git");
		Files.createDirectories(localDir);
		Git.init().setBare(true).setDirectory(localDir.toFile()).setInitialBranch("main").call().close();

		service = activate(config());
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

	private TestGitConfig config() {
		return new TestGitConfig(localDir.toString()).remote(url);
	}

	private GitServiceImpl activate(TestGitConfig config) throws Exception {
		GitServiceImpl activated = new GitServiceImpl();
		activated.activate(config);
		return activated;
	}

	// --- the two halves together --------------------------------------------------

	/** The local repository starts out empty, so what it serves has to come from the remote. */
	@Test
	public void testActivationFetchesTheRemoteIntoTheOnDiskRepository() throws Exception {
		assertThat(service.getGitUrl()).as("the repository is the one on disk").isEqualTo(localDir.toString());
		assertThat(service.getRemoteUrl()).as("and it mirrors to the remote").isEqualTo(url);
		assertThat(service.getFiles().getFiles()).containsExactly("test");
		assertThat(read("test")).isEqualTo(FILE_CONTENT);
		assertThat(service.getRemoteHead()).isEqualTo(remoteHead().getName());
	}

	@Test
	public void testCommitIsWrittenToDiskAndPushedToTheRemote() throws Exception {
		service.deactivate();
		service = activate(config().pushOnCommit(true));

		String commitId = service.writeFile("models/a.ecore", "<a/>".getBytes(StandardCharsets.UTF_8), "add a");

		assertThat(remoteHead().getName()).as("sent upstream").isEqualTo(commitId);
		assertThat(localHead()).as("and recorded on the volume").isEqualTo(commitId);
		assertThat(service.getRemoteHead()).isEqualTo(commitId);
	}

	/**
	 * The reason this mode exists: an unpushed commit must survive the process. The
	 * in-memory mirror loses it, the volume does not.
	 */
	@Test
	public void testAnUnpushedCommitSurvivesARestart() throws Exception {
		String commitId = service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		assertThat(remoteHead().getName()).as("not pushed").isNotEqualTo(commitId);

		service.deactivate();
		service = activate(config());

		assertThat(service.getFiles().getCommitId()).isEqualTo(commitId);
		assertThat(read("mine.txt")).isEqualTo("mine");
		assertThat(service.getRemoteHead()).as("still only local").isNotEqualTo(commitId);

		service.push();

		assertThat(remoteHead().getName()).isEqualTo(commitId);
	}

	/**
	 * Durability must not depend on the network: the objects are on the volume, so an
	 * unreachable remote may cost the update but must not keep the service from coming
	 * up.
	 */
	@Test
	public void testAnUnreachableRemoteStillActivatesAgainstTheVolume() throws Exception {
		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		service.deactivate();
		daemon.stop();

		service = activate(config());

		assertThat(read("mine.txt")).isEqualTo("mine");
	}

	// --- talking to the remote ----------------------------------------------------

	@Test
	public void testFetchFastForwardsTheOnDiskBranch() throws Exception {
		commitOnRemote("remote.txt", "remote");

		service.fetch();

		assertThat(service.getFiles().getFiles()).contains("remote.txt");
		assertThat(localHead()).isEqualTo(remoteHead().getName());
	}

	/** #46 holds here too: a fetch reads the remote, it does not discard local work. */
	@Test
	public void testFetchKeepsUnpushedCommitsOnDisk() throws Exception {
		String mine = service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		commitOnRemote("remote.txt", "remote");

		service.fetch();

		assertThat(localHead()).as("branch left where it was").isEqualTo(mine);
		assertThat(service.getRemoteHead()).isEqualTo(remoteHead().getName());
		assertThat(read(service.getRemoteHead(), "remote.txt")).as("both sides readable").isEqualTo("remote");
	}

	@Test
	public void testResetToRemoteDiscardsLocalCommitsOnDisk() throws Exception {
		service.writeFile("mine.txt", "mine".getBytes(StandardCharsets.UTF_8), "mine");
		commitOnRemote("remote.txt", "remote");
		service.fetch();

		String head = service.resetToRemote();

		assertThat(head).isEqualTo(remoteHead().getName());
		assertThat(localHead()).isEqualTo(remoteHead().getName());
		assertThat(service.getFiles().getFiles()).containsExactlyInAnyOrder("test", "remote.txt");
	}

	// --- configuration ------------------------------------------------------------

	/** No remote configured is the plain on-disk mode, and has to stay that way. */
	@Test
	public void testWithoutARemoteThereIsNoRemote() throws Exception {
		service.deactivate();
		service = activate(new TestGitConfig(localDir.toString()));

		assertThat(service.getRemoteUrl()).isNull();
		assertThat(service.getRemoteHead()).isNull();
		service.push(); // a no-op, not a failure
		service.fetch();
	}

	/** A URL repo already names its remote; a second one would be ambiguous. */
	@Test
	public void testAUrlRepoWithASecondRemoteIsRejected() {
		assertThatThrownBy(() -> activate(new TestGitConfig(url).remote("git://127.0.0.1:1/other"))) //
				.isInstanceOf(IllegalStateException.class) //
				.hasMessageContaining("remote") //
				.hasMessageContaining(url);
	}

	@Test
	public void testARemoteThatIsNotAUrlIsRejected() {
		assertThatThrownBy(() -> activate(new TestGitConfig(localDir.toString()).remote("/srv/some/path"))) //
				.isInstanceOf(IllegalStateException.class) //
				.hasMessageContaining("/srv/some/path");
	}

	/** Blank means unset (#42), the same as for every other configured value. */
	@Test
	public void testABlankRemoteIsNoRemote() throws Exception {
		service.deactivate();
		service = activate(new TestGitConfig(localDir.toString()).remote("   "));

		assertThat(service.getRemoteUrl()).isNull();
		assertThat(service.getRemoteHead()).isNull();
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

	/** Read straight from the volume, not through the service. */
	private String localHead() throws IOException {
		try (Repository onDisk = Git.open(localDir.toFile()).getRepository()) {
			ObjectId head = onDisk.resolve("refs/heads/main");
			return head == null ? null : head.getName();
		}
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
