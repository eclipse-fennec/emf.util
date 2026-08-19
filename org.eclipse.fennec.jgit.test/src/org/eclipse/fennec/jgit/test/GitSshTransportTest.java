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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.sshd.common.config.keys.PublicKeyEntry;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.UploadPack;
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
 * End-to-end test of the Apache MINA sshd backend (issue #2): a {@link GitService}
 * configured against an {@code ssh://} remote must authenticate with the configured
 * private key and read from the repo — across the key formats the legacy JCraft JSch
 * backend could <em>not</em> handle.
 *
 * <p>Everything runs in-process: a MINA {@link SshServer} on an ephemeral port serves a
 * throw-away repo over the git-upload-pack protocol (via JGit's {@link UploadPack}), and
 * accepts any public key. The real {@code GitServiceImpl} is pointed at it through
 * {@link ConfigurationAdmin} (the port is only known at runtime, so {@code
 * @WithFactoryConfiguration} cannot be used). Host-key verification uses a {@code
 * known_hosts} file written after the server starts, exercising the new {@code
 * knownHosts} config option.
 *
 * <p>The four keys are generated per run by {@link TestKeyFiles} and cover: ed25519
 * (OpenSSH format), RSA (OpenSSH format), RSA (classic PEM — the one format JSch
 * supported, guarding against regression), and a passphrase-protected ed25519 key
 * (exercising the configured-passphrase path).
 */
@RequireConfigurationAdmin
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@ExtendWith(BundleContextExtension.class)
public class GitSshTransportTest {

	private static final String FILE_CONTENT = "fooBar";
	private static final String OPENSSH = "-----BEGIN OPENSSH PRIVATE KEY-----";
	private static final String CLASSIC_PEM = "-----BEGIN RSA PRIVATE KEY-----";
	private static final String ENCRYPTED_PKCS8 = "-----BEGIN ENCRYPTED PRIVATE KEY-----";

	private Path repoDir;
	private Path keyFile;
	private Path hostKeyFile;
	private Path knownHostsFile;
	private Git served;
	private Repository servedRepo;
	private SshServer sshd;
	private Configuration configuration;

	@AfterEach
	public void cleanup() throws Exception {
		if (configuration != null) {
			configuration.delete();
		}
		if (sshd != null) {
			sshd.stop(true);
		}
		if (served != null) {
			served.close();
		}
		deleteQuietly(repoDir);
		deleteFileQuietly(keyFile);
		deleteFileQuietly(hostKeyFile);
		deleteFileQuietly(knownHostsFile);
	}

	@Test
	public void testEd25519OpenSshKey(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {
		Path key = TestKeyFiles.writeOpenSsh(TestKeyFiles.ed25519(), null);

		assertIsInFormat(key, OPENSSH);
		// Also proves the cipher check below discriminates rather than always passing.
		assertThat(TestKeyFiles.cipherOf(key)).as("key is unencrypted").isEqualTo("none");
		assertReadableOverSsh(key, null, gsAware, configAdmin);
	}

	@Test
	public void testRsaOpenSshKey(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {
		Path key = TestKeyFiles.writeOpenSsh(TestKeyFiles.rsa(), null);

		assertIsInFormat(key, OPENSSH);
		assertReadableOverSsh(key, null, gsAware, configAdmin);
	}

	@Test
	public void testRsaClassicPemKey(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {
		Path key = TestKeyFiles.writeClassicPem(TestKeyFiles.rsa());

		assertIsInFormat(key, CLASSIC_PEM);
		assertReadableOverSsh(key, null, gsAware, configAdmin);
	}

	@Test
	public void testPassphraseProtectedKey(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {
		Path key = TestKeyFiles.writeOpenSsh(TestKeyFiles.ed25519(), "s3cret");

		assertIsInFormat(key, OPENSSH);
		// An unencrypted key would authenticate just as well, leaving the configured-passphrase
		// path untested; insist the key really is encrypted.
		assertThat(TestKeyFiles.cipherOf(key)).as("key is encrypted").isEqualTo("aes256-ctr");
		assertReadableOverSsh(key, "s3cret", gsAware, configAdmin);
	}

	/**
	 * The format that decides what has to be shipped: OpenSSH and classic PEM keys are
	 * parsed by MINA itself, but an encrypted PKCS#8 blob is handed to BouncyCastle's
	 * {@code org.bouncycastle.pkcs} decryptor - which lives in {@code bcpkix}, not in the
	 * {@code bcprov} that the rest of the SSH stack pulls in. Without that bundle this
	 * fails with a {@code NoClassDefFoundError} at key load, so the test is also what keeps
	 * bcpkix in the workspace library's closure honest.
	 */
	@Test
	public void testEncryptedPkcs8Key(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware,
			@InjectService ConfigurationAdmin configAdmin) throws Exception {
		Path key = TestKeyFiles.writeEncryptedPkcs8(TestKeyFiles.rsa(), "s3cret");

		assertIsInFormat(key, ENCRYPTED_PKCS8);
		assertReadableOverSsh(key, "s3cret", gsAware, configAdmin);
	}

	/**
	 * The generated keys have to be in the formats this test is about, otherwise it would
	 * happily prove that MINA can read back whatever MINA just wrote.
	 */
	private static void assertIsInFormat(Path key, String header) throws Exception {
		assertThat(Files.readString(key)).as("private key format").startsWith(header);
	}

	/**
	 * Serves a one-commit repo over ssh:// with the given private key and asserts the
	 * {@link GitService} authenticates and reads it back.
	 */
	private void assertReadableOverSsh(Path privateKey, String passphrase,
			ServiceAware<GitService> gsAware, ConfigurationAdmin configAdmin) throws Exception {

		keyFile = privateKey;

		// A real repo on disk with a single commit on 'main'.
		repoDir = Files.createTempDirectory("gecko-jgit-ssh");
		served = Git.init().setDirectory(repoDir.toFile()).setInitialBranch("main").call();
		Files.writeString(repoDir.resolve("test"), FILE_CONTENT);
		served.add().addFilepattern("test").call();
		served.commit().setAuthor("Hans Wurst", "hw@example.com").setMessage("add test").call();
		servedRepo = served.getRepository();

		// Start an in-process SSH server on an ephemeral port: generated host key, accept any
		// public key, and serve git-upload-pack for the one repo above.
		hostKeyFile = Files.createTempFile("gecko-jgit-hostkey", "");
		Files.deleteIfExists(hostKeyFile); // let the provider create/populate it
		SimpleGeneratorHostKeyProvider hostKeyProvider = new SimpleGeneratorHostKeyProvider(hostKeyFile);
		sshd = SshServer.setUpDefaultServer();
		sshd.setPort(0);
		sshd.setKeyPairProvider(hostKeyProvider);
		sshd.setPublickeyAuthenticator(AcceptAllPublickeyAuthenticator.INSTANCE);
		sshd.setCommandFactory((channel, command) -> new GitUploadPackCommand(servedRepo));
		sshd.start();
		int port = sshd.getPort();

		// Pin the server's freshly generated host key in a known_hosts file for this port.
		KeyPair hostKey = hostKeyProvider.loadKeys(null).iterator().next();
		knownHostsFile = Files.createTempFile("gecko-jgit-known-hosts", "");
		Files.writeString(knownHostsFile,
				"[127.0.0.1]:" + port + " " + PublicKeyEntry.toString(hostKey.getPublic()) + "\n");

		// Configure the real GitServiceImpl against the ssh:// URL.
		String url = "ssh://tester@127.0.0.1:" + port + "/served";
		configuration = configAdmin.createFactoryConfiguration("GitConfig", "?");
		Dictionary<String, Object> props = new Hashtable<>();
		props.put("repo", url);
		props.put("branch", "main");
		props.put("privateKey", keyFile.toString());
		if (passphrase != null) {
			props.put("privateKeyPassphrase", passphrase);
		}
		props.put("knownHosts", knownHostsFile.toString());
		configuration.update(props);

		GitService service = gsAware.waitForService(15000l);
		assertThat(service).as("GitService activated over ssh:// with key %s", privateKey).isNotNull();
		assertThat(service.getGitUrl()).isEqualTo(url);
		assertThat(service.getBranches()).contains("refs/heads/main");
		assertThat(service.getFiles().getFiles()).contains("test");
	}

	private static void deleteQuietly(Path dir) throws Exception {
		if (dir != null && Files.exists(dir)) {
			FileUtils.delete(dir.toFile(), FileUtils.RECURSIVE | FileUtils.SKIP_MISSING);
		}
	}

	private static void deleteFileQuietly(Path file) throws Exception {
		if (file != null) {
			Files.deleteIfExists(file);
		}
	}

	/**
	 * Minimal git-upload-pack command: pipes the SSH exec channel's streams into JGit's
	 * {@link UploadPack} for the served repo. The requested path is ignored — a single repo
	 * is served — mirroring how {@code GitAnonymousTransportTest} resolves its {@code Daemon}.
	 */
	private static final class GitUploadPackCommand implements Command {

		private final Repository repository;
		private InputStream in;
		private OutputStream out;
		private OutputStream err;
		private ExitCallback exit;
		private Thread worker;

		GitUploadPackCommand(Repository repository) {
			this.repository = repository;
		}

		@Override
		public void setInputStream(InputStream in) {
			this.in = in;
		}

		@Override
		public void setOutputStream(OutputStream out) {
			this.out = out;
		}

		@Override
		public void setErrorStream(OutputStream err) {
			this.err = err;
		}

		@Override
		public void setExitCallback(ExitCallback callback) {
			this.exit = callback;
		}

		@Override
		public void start(ChannelSession channel, Environment env) {
			worker = new Thread(() -> {
				int exitCode = 0;
				try {
					new UploadPack(repository).upload(in, out, err);
					out.flush();
				} catch (Exception e) {
					exitCode = 1;
				} finally {
					if (exit != null) {
						exit.onExit(exitCode);
					}
				}
			}, "git-upload-pack");
			worker.setDaemon(true);
			worker.start();
		}

		@Override
		public void destroy(ChannelSession channel) {
			if (worker != null) {
				worker.interrupt();
			}
		}
	}

}
