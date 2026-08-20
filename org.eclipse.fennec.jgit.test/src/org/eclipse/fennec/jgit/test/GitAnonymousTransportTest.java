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
package org.eclipse.fennec.jgit.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.Daemon;
import org.eclipse.jgit.transport.DaemonService;
import org.eclipse.jgit.util.FileUtils;
import org.eclipse.fennec.jgit.api.GitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Regression test for the SSH-only transport fix: a {@link GitService} configured
 * against an anonymous {@code git://} remote must activate and read.
 *
 * <p>Before the fix, {@code GitServiceImpl.activate()} installed a
 * {@code TransportConfigCallback} that <em>unconditionally</em> cast the JGit
 * {@code Transport} to {@code SshTransport}. Over {@code git://} the transport is a
 * {@code TransportGitAnon}, so activation threw a {@code ClassCastException}, the DS
 * component never registered, and {@code waitForService} below would return
 * {@code null}. The fix only attaches the SSH session factory for actual
 * {@code SshTransport}s, so non-SSH remotes fetch with JGit's default transport.
 *
 * <p>The remote is served in-process by JGit's own {@link Daemon} over the anonymous
 * git protocol on an ephemeral port — no external git binary, no container, no TLS or
 * credentials. The config is pushed via {@link ConfigurationAdmin} rather than the
 * {@code @WithFactoryConfiguration} annotation because the port is only known at runtime.
 */
@RequireConfigurationAdmin
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@ExtendWith(BundleContextExtension.class)
public class GitAnonymousTransportTest {

	private static final String FILE_CONTENT = "fooBar";

	private Path repoDir;
	private Path storeDir;
	private Git served;
	private Daemon daemon;
	private Configuration configuration;

	@AfterEach
	public void cleanup() throws Exception {
		if (configuration != null) {
			configuration.delete();
		}
		if (daemon != null) {
			daemon.stop();
		}
		if (served != null) {
			served.close();
		}
		if (repoDir != null && Files.exists(repoDir)) {
			FileUtils.delete(repoDir.toFile(), FileUtils.RECURSIVE | FileUtils.SKIP_MISSING);
		}
		if (storeDir != null && Files.exists(storeDir)) {
			FileUtils.delete(storeDir.toFile(), FileUtils.RECURSIVE | FileUtils.SKIP_MISSING);
		}
	}

	@Test
	public void testFetchOverAnonymousGitProtocol(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {

		String url = serveRepository(false);
		GitService service = configureService(configAdmin, gsAware, url);

		// Would be null if activate() still threw ClassCastException on the non-SSH transport.
		assertThat(service).as("GitService activated over git://").isNotNull();
		assertThat(service.getGitUrl()).isEqualTo(url);
		assertThat(service.getBranches()).contains("refs/heads/main");
		assertThat(service.getFiles().getFiles()).contains("test");
	}

	/**
	 * The write counterpart: a commit written into the in-memory mirror reaches the
	 * remote when, and only when, it is pushed. This is the end-to-end proof that the
	 * in-core commit produces a real, transferable commit and that push is wired
	 * through the same transport as fetch.
	 */
	@Test
	public void testPushOverAnonymousGitProtocol(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {

		String url = serveRepository(true);
		GitService service = configureService(configAdmin, gsAware, url);
		Repository servedRepo = served.getRepository();
		ObjectId remoteHeadBefore = servedRepo.resolve("refs/heads/main");

		String commitId = service.writeFile("pushed.txt", "pushed".getBytes(StandardCharsets.UTF_8), "add pushed");

		// Default configuration: the commit is local to the mirror until push() is called.
		assertThat(service.getFiles().getFiles()).contains("pushed.txt");
		assertThat(servedRepo.resolve("refs/heads/main")).as("remote untouched before push")
				.isEqualTo(remoteHeadBefore);

		service.push();

		assertThat(servedRepo.resolve("refs/heads/main").getName()).as("remote advanced to the new commit")
				.isEqualTo(commitId);
		ObjectId blob = servedRepo.resolve("refs/heads/main:pushed.txt");
		assertThat(blob).as("pushed file present in the remote").isNotNull();
		assertThat(new String(servedRepo.open(blob).getBytes(), StandardCharsets.UTF_8)).isEqualTo("pushed");
	}

	/**
	 * The durable-plus-mirrored mode through a real ConfigAdmin configuration:
	 * {@code repo} is a bare repository on disk, {@code remote} the {@code git://}
	 * URL. This is the deployment shape a production store wants — the commit is on
	 * the volume the instant it is written and it still reaches the upstream host —
	 * and it only works if the added {@code remote} attribute actually arrives at the
	 * component through DS.
	 */
	@Test
	public void testDurableStoreMirroredToTheRemote(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {

		String url = serveRepository(true);
		storeDir = Files.createTempDirectory("gecko-jgit-store");
		Git.init().setBare(true).setDirectory(storeDir.toFile()).setInitialBranch("main").call().close();

		Dictionary<String, Object> props = new Hashtable<>();
		props.put("repo", storeDir.toString());
		props.put("remote", url);
		props.put("branch", "main");
		props.put("pushOnCommit", Boolean.TRUE);
		GitService service = configureService(configAdmin, gsAware, props);

		// The store started empty, so this content can only have come from the remote.
		assertThat(service.getFiles().getFiles()).contains("test");

		String commitId = service.writeFile("stored.txt", "stored".getBytes(StandardCharsets.UTF_8), "add stored");

		Repository servedRepo = served.getRepository();
		assertThat(servedRepo.resolve("refs/heads/main").getName()).as("mirrored upstream").isEqualTo(commitId);
		assertThat(service.getRemoteHead()).as("and known to be there").isEqualTo(commitId);
		try (Repository onDisk = Git.open(storeDir.toFile()).getRepository()) {
			assertThat(onDisk.resolve("refs/heads/main").getName()).as("durable on the volume")
					.isEqualTo(commitId);
		}
	}

	/**
	 * Creates a repository on disk with a single commit on {@code main} and serves it
	 * over the anonymous git protocol on an ephemeral port.
	 *
	 * @param allowPush whether receive-pack is offered, i.e. whether the remote
	 *                  accepts writes
	 * @return the {@code git://} URL of the served repository
	 */
	private String serveRepository(boolean allowPush) throws Exception {
		repoDir = Files.createTempDirectory("gecko-jgit-anon");
		served = Git.init().setDirectory(repoDir.toFile()).setInitialBranch("main").call();
		Files.writeString(repoDir.resolve("test"), FILE_CONTENT);
		served.add().addFilepattern("test").call();
		served.commit().setAuthor("Hans Wurst", "hw@example.com").setMessage("add test").call();

		Repository servedRepo = served.getRepository();
		if (allowPush) {
			// The served repo has a working tree, and git refuses by default to move the branch
			// that is checked out there. A real remote would be bare; here the working tree is
			// simply allowed to fall behind.
			StoredConfig servedConfig = servedRepo.getConfig();
			servedConfig.setString("receive", null, "denyCurrentBranch", "ignore");
			servedConfig.save();
		}

		// Daemon.start() rebinds the socket and updates getAddress(), so the actual port is
		// known only after start().
		daemon = new Daemon(new InetSocketAddress("127.0.0.1", 0));
		enable(daemon.getService("upload-pack"));
		if (allowPush) {
			enable(daemon.getService("receive-pack"));
		}
		daemon.setRepositoryResolver((req, name) -> servedRepo);
		daemon.start();
		return "git://127.0.0.1:" + daemon.getAddress().getPort() + "/served";
	}

	/** Forces the service on regardless of the per-repository daemon configuration. */
	private static void enable(DaemonService service) {
		service.setEnabled(true);
		service.setOverridable(false);
	}

	/** Configures the real GitServiceImpl against the git:// URL (no privateKey). */
	private GitService configureService(ConfigurationAdmin configAdmin, ServiceAware<GitService> gsAware, String url)
			throws Exception {
		Dictionary<String, Object> props = new Hashtable<>();
		props.put("repo", url);
		props.put("branch", "main");
		return configureService(configAdmin, gsAware, props);
	}

	/** Configures the real GitServiceImpl with the given properties. */
	private GitService configureService(ConfigurationAdmin configAdmin, ServiceAware<GitService> gsAware,
			Dictionary<String, Object> props) throws Exception {
		configuration = configAdmin.createFactoryConfiguration("GitConfig", "?");
		configuration.update(props);

		GitService service = gsAware.waitForService(10000l);
		assertThat(service).as("GitService activated for %s", props.get("repo")).isNotNull();
		return service;
	}

}
