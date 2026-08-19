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

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.sshd.common.config.keys.writer.openssh.OpenSSHKeyEncryptionContext;
import org.apache.sshd.common.config.keys.writer.openssh.OpenSSHKeyPairResourceWriter;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;

/**
 * Generates throwaway SSH private keys on disk for {@link GitSshTransportTest}, in the
 * formats whose handling is what that test is actually about.
 * <p>
 * The keys are generated per test run rather than committed as fixtures: a repository
 * that carries files looking like private keys trips security scanners, and there is
 * nothing about these keys worth preserving between runs. Each one lives in a temporary
 * file for the duration of a single test and authenticates only to an in-process server.
 */
final class TestKeyFiles {

	private TestKeyFiles() {
	}

	/**
	 * Keys are generated through MINA's own {@code SecurityUtils} rather than through the
	 * JDK directly, so they come from the same security provider the writer below expects.
	 * A plain {@code KeyPairGenerator.getInstance("Ed25519")} yields a SunEC key, which
	 * MINA's OpenSSH writer cannot cast to the BouncyCastle EdDSA type it works with.
	 */
	static KeyPair ed25519() throws Exception {
		return SecurityUtils.getKeyPairGenerator("Ed25519").generateKeyPair();
	}

	static KeyPair rsa() throws Exception {
		KeyPairGenerator generator = SecurityUtils.getKeyPairGenerator("RSA");
		generator.initialize(2048);
		return generator.generateKeyPair();
	}

	/**
	 * Writes the key in the modern OpenSSH format ({@code BEGIN OPENSSH PRIVATE KEY}),
	 * encrypted with the given passphrase when one is supplied.
	 */
	static Path writeOpenSsh(KeyPair keyPair, String passphrase) throws Exception {
		OpenSSHKeyEncryptionContext encryption = null;
		if (passphrase != null) {
			encryption = new OpenSSHKeyEncryptionContext();
			encryption.setPassword(passphrase);
			// The cipher is assembled from name + type + mode; leaving the type unset yields
			// the nonsense "aesnull-ctr", so spell all three out.
			encryption.setCipherName("aes");
			encryption.setCipherType("256");
			encryption.setCipherMode("ctr");
		}
		Path file = keyFile();
		try (OutputStream out = Files.newOutputStream(file)) {
			OpenSSHKeyPairResourceWriter.INSTANCE.writePrivateKey(keyPair, "fennec test key", encryption, out);
		}
		return file;
	}

	/**
	 * Writes an RSA key in the classic PEM format ({@code BEGIN RSA PRIVATE KEY}), the one
	 * the legacy JSch backend could read - kept as a regression guard.
	 * <p>
	 * The JDK hands out PKCS#8, whose {@code privateKey} field already holds the PKCS#1
	 * structure that classic PEM is made of, so unwrapping it is all that is needed.
	 */
	static Path writeClassicPem(KeyPair keyPair) throws Exception {
		byte[] pkcs1 = PrivateKeyInfo.getInstance(keyPair.getPrivate().getEncoded()) //
				.parsePrivateKey().toASN1Primitive().getEncoded();
		String pem = "-----BEGIN RSA PRIVATE KEY-----\n"
				+ Base64.getMimeEncoder(64, new byte[] { '\n' }).encodeToString(pkcs1)
				+ "\n-----END RSA PRIVATE KEY-----\n";
		Path file = keyFile();
		Files.write(file, pem.getBytes(StandardCharsets.UTF_8));
		return file;
	}

	/**
	 * Writes an RSA key as an encrypted PKCS#8 PEM ({@code BEGIN ENCRYPTED PRIVATE KEY}) -
	 * the one format MINA does <em>not</em> parse itself. It hands the blob to
	 * BouncyCastle's PKCS#8 decryptor, which lives in {@code bcpkix}, so this is what makes
	 * that bundle a real runtime requirement rather than a precaution.
	 * <p>
	 * Written with the JDK alone, so nothing here depends on what is being tested. The
	 * algorithm is PKCS#5 v1.5 rather than something modern because
	 * {@link EncryptedPrivateKeyInfo} can only wrap parameters whose name the JDK maps to an
	 * OID, which rules out the PBES2 names - the key is a throwaway that authenticates to an
	 * in-process server for the length of one test, so the weak KDF costs nothing.
	 */
	static Path writeEncryptedPkcs8(KeyPair keyPair, String passphrase) throws Exception {
		byte[] salt = new byte[8];
		SecureRandom.getInstanceStrong().nextBytes(salt);
		String algorithm = "PBEWithMD5AndDES";
		SecretKey secret = SecretKeyFactory.getInstance(algorithm) //
				.generateSecret(new PBEKeySpec(passphrase.toCharArray()));
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, secret, new PBEParameterSpec(salt, 100_000));
		byte[] encrypted = cipher.doFinal(keyPair.getPrivate().getEncoded());
		byte[] der = new EncryptedPrivateKeyInfo(cipher.getParameters(), encrypted).getEncoded();

		String pem = "-----BEGIN ENCRYPTED PRIVATE KEY-----\n"
				+ Base64.getMimeEncoder(64, new byte[] { '\n' }).encodeToString(der)
				+ "\n-----END ENCRYPTED PRIVATE KEY-----\n";
		Path file = keyFile();
		Files.write(file, pem.getBytes(StandardCharsets.UTF_8));
		return file;
	}

	/**
	 * The cipher an OpenSSH-format private key was written with, {@code none} for an
	 * unencrypted one. Reads the header of the blob: the magic {@code openssh-key-v1\0}
	 * is followed by the cipher name as a length-prefixed string.
	 * <p>
	 * Without this, a key that was silently written unencrypted would still authenticate,
	 * and the passphrase path would look tested while never being exercised.
	 */
	static String cipherOf(Path openSshKey) throws Exception {
		String pem = Files.readString(openSshKey);
		String body = pem.replace("-----BEGIN OPENSSH PRIVATE KEY-----", "") //
				.replace("-----END OPENSSH PRIVATE KEY-----", "") //
				.replaceAll("\\s", "");
		ByteBuffer blob = ByteBuffer.wrap(Base64.getDecoder().decode(body));
		byte[] magic = new byte[15]; // "openssh-key-v1\0"
		blob.get(magic);
		byte[] cipher = new byte[blob.getInt()];
		blob.get(cipher);
		return new String(cipher, StandardCharsets.UTF_8);
	}

	private static Path keyFile() throws Exception {
		Path file = Files.createTempFile("fennec-jgit-key", "");
		try {
			Files.setPosixFilePermissions(file, PosixFilePermissions.fromString("rw-------"));
		} catch (UnsupportedOperationException ignored) {
			// non-POSIX filesystem: MINA does not enforce key-file permissions anyway
		}
		return file;
	}
}
