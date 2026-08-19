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

import java.io.File;
import java.util.List;

import org.eclipse.fennec.jgit.api.GitConfig;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.sshd.JGitKeyCache;
import org.eclipse.jgit.transport.sshd.KeyPasswordProvider;
import org.eclipse.jgit.transport.sshd.SshdSessionFactoryBuilder;

/**
 * Everything that touches {@code org.eclipse.jgit.transport.sshd}, kept in a class of
 * its own.
 * <p>
 * Not a matter of taste: the JVM resolves the types named in a class's fields and
 * method signatures when it loads the class, and verifying a method body can pull in
 * the types it assigns as well. As long as any of that named the sshd API, the Apache
 * MINA sshd stack was a hard requirement for loading {@link GitServiceImpl} at all —
 * including for a consumer that only ever opens a repository on disk. Confining it to
 * a separate class moves the requirement to the first call of {@link #create(GitConfig)},
 * which only happens when a private key is actually configured.
 */
final class SshdSessionFactoryProvider {

	private SshdSessionFactoryProvider() {
	}

	/**
	 * Builds an Apache MINA sshd session factory that authenticates with exactly the
	 * configured private key. The key is supplied as a lazily-loaded identity path (so
	 * OpenSSH-format and ed25519 keys are parsed by MINA/BouncyCastle), and its passphrase
	 * comes from {@link ConfiguredKeyPasswordProvider}. Host-key verification and the
	 * default known-hosts location (~/.ssh) are left at MINA's defaults.
	 */
	static SshSessionFactory create(GitConfig config) {
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
				.setKeyPasswordProvider(cp -> new ConfiguredKeyPasswordProvider(config));
		// Verify the server host key against the configured known_hosts file; when none is
		// configured, MINA falls back to its default (~/.ssh/known_hosts).
		String knownHosts = config.knownHosts();
		if (knownHosts != null && !knownHosts.isBlank()) {
			File knownHostsFile = new File(knownHosts);
			builder.setDefaultKnownHostsFiles(sshDir -> List.of(knownHostsFile.toPath()));
		}
		return builder.build(new JGitKeyCache());
	}

	/**
	 * Supplies the configured passphrase to the Apache MINA sshd backend when it
	 * loads an encrypted private key. A single fixed passphrase is configured, so
	 * a failed load is never retried (that would only reuse the same passphrase).
	 */
	private static final class ConfiguredKeyPasswordProvider implements KeyPasswordProvider {

		private final GitConfig config;

		ConfiguredKeyPasswordProvider(GitConfig config) {
			this.config = config;
		}

		@Override
		public char[] getPassphrase(URIish uri, int attempt) {
			String passphrase = config.privateKeyPassphrase();
			// Blank means the key is not encrypted; handing MINA an empty passphrase is the
			// same thing to it, but a key that does have one must not be answered with " ".
			return passphrase == null || passphrase.isBlank() ? new char[0] : passphrase.toCharArray();
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
}
