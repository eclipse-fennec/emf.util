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

import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * What happens when the configured path is not a repository.
 * <p>
 * The interesting case is not the error text but the one that would otherwise
 * succeed: a path that does not exist inside somebody else's working copy. Discovery
 * that walks up the file system finds that ancestor repository and the service comes
 * up happily, writing every commit into a repository nobody configured. Activation
 * has to fail for both, and it has to say which path it looked at.
 */
public class GitServiceImplMissingRepositoryTest {

	@TempDir
	Path tempDir;

	@Test
	public void testMissingDirectoryFailsNamingThePath() {
		Path missing = tempDir.resolve("not-created-yet");

		assertThatThrownBy(() -> new GitServiceImpl().activate(new TestGitConfig(missing.toString()))) //
				.isInstanceOf(IllegalStateException.class) //
				.hasMessageContaining(missing.toString()) //
				.hasMessageContaining("not a git repository") //
				.hasMessageContaining("does not exist") //
				.hasMessageContaining("git init --bare");
	}

	/**
	 * The same failure, from inside a working copy: the enclosing repository is found
	 * by an unbounded search and must not be used.
	 */
	@Test
	public void testMissingDirectoryInsideAWorkingCopyDoesNotBindToTheEnclosingRepository() throws Exception {
		Path workingCopy = tempDir.resolve("working-copy");
		Files.createDirectories(workingCopy);
		try (Git enclosing = Git.init().setDirectory(workingCopy.toFile()).setInitialBranch("main").call()) {
			Path missing = workingCopy.resolve("models/store");

			assertThatThrownBy(() -> new GitServiceImpl().activate(new TestGitConfig(missing.toString()))) //
					.isInstanceOf(IllegalStateException.class) //
					.hasMessageContaining(missing.toString());
		}
	}

	/** A path that is a file, not a directory, is reported as such. */
	@Test
	public void testAFileIsNotARepository() throws Exception {
		Path file = tempDir.resolve("models.txt");
		Files.writeString(file, "not a repository");

		assertThatThrownBy(() -> new GitServiceImpl().activate(new TestGitConfig(file.toString()))) //
				.isInstanceOf(IllegalStateException.class) //
				.hasMessageContaining(file.toString()) //
				.hasMessageContaining("not a directory");
	}

	@Test
	public void testBareRepositoryIsOpened() throws Exception {
		Path repoDir = tempDir.resolve("models.git");
		Files.createDirectories(repoDir);
		try (Git origin = Git.init().setBare(true).setDirectory(repoDir.toFile()).setInitialBranch("main").call()) {
			GitServiceImpl service = new GitServiceImpl();
			service.activate(new TestGitConfig(repoDir.toString()));

			assertThat(service.getBranch()).isEqualTo("main");
			assertThat(service.getFiles().getFiles()).isEmpty();
			service.deactivate();
		}
	}

	/** A working tree is opened through the {@code .git} directory inside it. */
	@Test
	public void testWorkingTreeRootIsOpened() throws Exception {
		Path repoDir = tempDir.resolve("checkout");
		Files.createDirectories(repoDir);
		try (Git origin = Git.init().setDirectory(repoDir.toFile()).setInitialBranch("main").call()) {
			GitServiceImpl service = new GitServiceImpl();
			service.activate(new TestGitConfig(repoDir.toString()));

			assertThat(service.getFiles().getFiles()).isEmpty();
			service.deactivate();
		}
	}
}
