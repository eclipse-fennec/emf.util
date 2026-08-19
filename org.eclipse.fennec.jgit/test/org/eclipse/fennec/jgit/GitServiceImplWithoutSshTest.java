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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.fennec.jgit.api.GitConfig;
import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Proves that the service loads and runs without {@code org.eclipse.jgit.ssh.apache}
 * on the class path.
 * <p>
 * The SSH backend is only built when a private key is configured, but a guard in the
 * activation cannot help if the class itself names the sshd types: field types and
 * method signatures are resolved when a class is loaded, and verifying a method body
 * can pull in what it assigns. That made the Apache MINA sshd stack and BouncyCastle
 * mandatory for every consumer, including one that only opens a repository on disk.
 * <p>
 * The test reproduces that by loading the component in a class loader that refuses
 * everything in {@code org.eclipse.jgit.transport.sshd}. Reflection is the price of
 * running the same class twice in one JVM; the alternative is a second build with a
 * different class path.
 */
public class GitServiceImplWithoutSshTest {

	private static final String SSHD_PACKAGE = "org.eclipse.jgit.transport.sshd";

	@TempDir
	Path tempDir;

	private Path repoDir;

	@BeforeEach
	public void before() throws Exception {
		repoDir = tempDir.resolve("bare.git");
		Files.createDirectories(repoDir);
		Git.init().setBare(true).setDirectory(repoDir.toFile()).setInitialBranch("main").call().close();
	}

	@Test
	public void testALocalRepositoryNeedsNoSshStack() throws Exception {
		ClassLoader withoutSsh = loaderHiding(SSHD_PACKAGE);
		Class<?> serviceClass = withoutSsh.loadClass(GitServiceImpl.class.getName());
		Object service = serviceClass.getConstructor().newInstance();

		activate(serviceClass, service, config(withoutSsh, repoDir.toString(), null));
		try {
			assertThat(files(serviceClass, service)).isEmpty();

			serviceClass.getMethod("writeFile", String.class, byte[].class, String.class) //
					.invoke(service, "a.txt", "a".getBytes(StandardCharsets.UTF_8), "add a");

			assertThat(files(serviceClass, service)).containsExactly("a.txt");
		} finally {
			serviceClass.getMethod("deactivate").invoke(service);
		}
	}

	/**
	 * The stack is genuinely optional, not silently skipped: asking for key
	 * authentication without it says so, instead of failing somewhere inside the
	 * activation with a bare {@code NoClassDefFoundError}.
	 */
	@Test
	public void testAConfiguredKeyWithoutTheSshStackIsExplained() throws Exception {
		ClassLoader withoutSsh = loaderHiding(SSHD_PACKAGE);
		Class<?> serviceClass = withoutSsh.loadClass(GitServiceImpl.class.getName());
		Object service = serviceClass.getConstructor().newInstance();
		Object config = config(withoutSsh, "ssh://127.0.0.1:1/nowhere.git", "/no/such/key");

		assertThatThrownBy(() -> activate(serviceClass, service, config)) //
				.isInstanceOf(IllegalStateException.class) //
				.hasMessageContaining("privateKey is configured") //
				.hasMessageContaining("org.eclipse.jgit.ssh.apache");
	}

	// --- plumbing ----------------------------------------------------------------

	private void activate(Class<?> serviceClass, Object service, Object config) throws Exception {
		Class<?> configClass = config.getClass().getInterfaces()[0];
		try {
			serviceClass.getMethod("activate", configClass).invoke(service, config);
		} catch (InvocationTargetException e) {
			// Unwrap, so an assertion can look at what the component actually threw.
			throw e.getCause() instanceof Exception cause ? cause : new IllegalStateException(e.getCause());
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> files(Class<?> serviceClass, Object service) throws Exception {
		Object tree = serviceClass.getMethod("getFiles").invoke(service);
		return (List<String>) tree.getClass().getMethod("getFiles").invoke(tree);
	}

	/**
	 * A {@link GitConfig} of the isolated loader's own copy of the annotation, since
	 * that is what its copy of the component expects.
	 */
	private Object config(ClassLoader loader, String repo, String privateKey) throws Exception {
		Class<?> configClass = loader.loadClass(GitConfig.class.getName());
		return Proxy.newProxyInstance(loader, new Class<?>[] { configClass }, (proxy, method, args) -> {
			switch (method.getName()) {
			case "repo":
				return repo;
			case "privateKey":
				return privateKey == null ? "" : privateKey;
			case "annotationType":
				return configClass;
			default:
				return defaultValue(method);
			}
		});
	}

	private Object defaultValue(Method method) {
		Object declared = method.getDefaultValue();
		if (declared != null) {
			return declared;
		}
		if (method.getReturnType() == boolean.class) {
			return Boolean.FALSE;
		}
		return method.getReturnType() == String.class ? "" : null;
	}

	/**
	 * Loads this bundle's own classes afresh — so they are linked against what this
	 * loader can see — while everything else, JGit core included, stays shared with
	 * the parent. Classes of the hidden package are reported as absent.
	 */
	private ClassLoader loaderHiding(String hiddenPackage) {
		ClassLoader parent = getClass().getClassLoader();
		return new ClassLoader(parent) {

			@Override
			protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
				if (name.startsWith(hiddenPackage)) {
					throw new ClassNotFoundException(name + " is hidden by " + GitServiceImplWithoutSshTest.class);
				}
				if (!name.startsWith("org.eclipse.fennec.jgit")) {
					return super.loadClass(name, resolve);
				}
				synchronized (getClassLoadingLock(name)) {
					Class<?> loaded = findLoadedClass(name);
					if (loaded == null) {
						loaded = defineClass(name, bytecodeOf(name), 0, bytecodeOf(name).length);
					}
					if (resolve) {
						resolveClass(loaded);
					}
					return loaded;
				}
			}

			private byte[] bytecodeOf(String name) throws ClassNotFoundException {
				String resource = name.replace('.', '/') + ".class";
				try (InputStream in = parent.getResourceAsStream(resource)) {
					if (in == null) {
						throw new ClassNotFoundException(name);
					}
					return in.readAllBytes();
				} catch (IOException e) {
					throw new ClassNotFoundException(name, e);
				}
			}
		};
	}
}
