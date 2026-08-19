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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.fennec.jgit.api.TreeResult;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Drives the component against a repository whose branch has no commits at all —
 * the state every repository is in right after it was created, and the state a
 * consumer that reads before it writes meets on its first boot.
 * <p>
 * An unborn branch is empty, not broken: listing it yields nothing, reading from it
 * reports the file as missing, and the first commit is simply parentless.
 */
public class GitServiceImplEmptyRepositoryTest {

	@TempDir
	Path tempDir;

	private Path repoDir;
	private Git origin;
	private GitServiceImpl service;

	@BeforeEach
	public void before() throws Exception {
		repoDir = tempDir.resolve("empty.git");
		Files.createDirectories(repoDir);
		origin = Git.init().setBare(true).setDirectory(repoDir.toFile()).setInitialBranch("main").call();

		service = new GitServiceImpl();
		service.activate(new TestGitConfig(repoDir.toString()));
	}

	@AfterEach
	public void after() {
		if (service != null) {
			service.deactivate();
		}
		if (origin != null) {
			origin.close();
		}
	}

	@Test
	public void testListingAnUnbornBranchIsEmpty() {
		TreeResult result = service.getFiles();

		assertThat(result.getFiles()).isEmpty();
		assertThat(result.getEntries()).isEmpty();
		assertThat(result.getCommitId()).as("no commit to report").isNull();
		assertThat(service.getFiles("models").getFiles()).isEmpty();
	}

	@Test
	public void testReadingFromAnUnbornBranchReportsTheFileAsMissing() {
		assertThatThrownBy(() -> service.readLatestFile("anything")) //
				.isInstanceOf(GitFileNotFoundException.class) //
				.hasMessageContaining("anything") //
				.hasMessageContaining("refs/heads/main");
		assertThatThrownBy(() -> service.loadLatestFile("anything", new ByteArrayOutputStream()))
				.isInstanceOf(GitFileNotFoundException.class);
	}

	@Test
	public void testNothingExistsInAnUnbornBranch() {
		assertThat(service.exists(null, "anything")).isFalse();
		assertThat(service.blobId(null, "anything")).isEmpty();
	}

	@Test
	public void testMetadataOfAnUnbornBranch() throws Exception {
		assertThat(service.getBranch()).isEqualTo("main");
		assertThat(service.getBranches()).isEmpty();
		assertThat(service.getLog()).isEmpty();
	}

	/** Reading before writing is the whole point: both have to work, in that order. */
	@Test
	public void testTheFirstCommitMakesTheBranch() throws Exception {
		assertThat(service.getFiles().getFiles()).isEmpty();

		String commitId = service.writeFile("models/sensor.ecore", "<ecore/>".getBytes(StandardCharsets.UTF_8),
				"first");

		TreeResult result = service.getFiles();
		assertThat(result.getCommitId()).isEqualTo(commitId);
		assertThat(result.getFiles()).containsExactly("models/sensor.ecore");
		assertThat(read("models/sensor.ecore")).isEqualTo("<ecore/>");
		assertThat(service.getLog()).extracting(RevCommit::getFullMessage).containsExactly("first");
		assertThat(service.getBranches()).containsExactly("refs/heads/main");
	}

	private String read(String path) throws Exception {
		try (InputStream in = service.readLatestFile(path)) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
