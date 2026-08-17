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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.GitService;
import org.eclipse.fennec.jgit.api.TreeResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.Property;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Exercises the write side of {@link GitService} against a real local repository in
 * a running framework: commit, batch commit, delete, and the configured author.
 * <p>
 * Uses its own repository directory and its own configuration so that the commits
 * written here cannot disturb the read assertions of {@link GitServiceTest}.
 */
@RequireConfigurationAdmin
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
@ExtendWith(BundleContextExtension.class)
@WithFactoryConfiguration(factoryPid = "GitRepositoryConfig", location = "?", name = "writeRepo", properties = {
		@Property(key = "directory", value = "testRepoWrite"), //
		@Property(key = "branch", value = "main") })
@WithFactoryConfiguration(factoryPid = "GitConfig", location = "?", name = "writeGit", properties = {
		@Property(key = "repo", value = "testRepoWrite"), //
		@Property(key = "branch", value = "main"), //
		@Property(key = "authorName", value = "Erika Mustermann"), //
		@Property(key = "authorEmail", value = "em@example.com") })
public class GitServiceWriteTest {

	private static final String FILE_CONTENT = "fooBar";

	private GitRepositoryService repo;

	@BeforeEach
	public void before(@InjectService(cardinality = 0) ServiceAware<GitRepositoryService> repoAware)
			throws InterruptedException, GitAPIException, IOException {
		// Seed the repository with one ordinary working-tree commit, so the write path is
		// exercised on top of an existing history rather than on an empty repository.
		repo = repoAware.waitForService(5000);
		assertThat(repo).isNotNull();
		Files.writeString(Paths.get("testRepoWrite/test"), FILE_CONTENT);
		repo.addFilePattern("test");
		repo.commit("Hans Wurst", "hw@example.com", "add test");
	}

	/**
	 * The configuration is propagated to the service properties, which is how a consumer
	 * picks one repository out of several with a reference target filter such as
	 * {@code (repo=…)}. Documented in the user guide, so it is pinned here.
	 */
	@Test
	public void testConfigurationIsVisibleAsServiceProperties(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware) throws Exception {
		gsAware.waitForService(5000l);

		ServiceReference<GitService> reference = gsAware.getServiceReference();
		assertThat(reference.getProperty("repo")).isEqualTo("testRepoWrite");
		assertThat(reference.getProperty("branch")).isEqualTo("main");
		assertThat(reference.getProperty("authorName")).isEqualTo("Erika Mustermann");
	}

	@Test
	public void testWriteFile(@InjectService(cardinality = 0) ServiceAware<GitService> gsAware) throws Exception {
		GitService service = gsAware.waitForService(5000l);

		String commitId = service.writeFile("written.txt", "written".getBytes(StandardCharsets.UTF_8), "add written");

		assertThat(commitId).isNotBlank();
		TreeResult result = service.getFiles();
		assertThat(result.getCommitId()).isEqualTo(commitId);
		assertThat(result.getFiles()).containsExactlyInAnyOrder("test", "written.txt");
		assertThat(read(service, "written.txt")).isEqualTo("written");
		// The commit written in core is a proper commit on the branch, visible to the reader.
		assertThat(read(service, "test")).isEqualTo(FILE_CONTENT);
	}

	@Test
	public void testWriteFileFromStream(@InjectService(cardinality = 0) ServiceAware<GitService> gsAware)
			throws Exception {
		GitService service = gsAware.waitForService(5000l);

		InputStream content = new ByteArrayInputStream("streamed".getBytes(StandardCharsets.UTF_8));
		service.writeFile("models/deep/streamed.txt", content, "add streamed");

		assertThat(service.getFiles("models").getFiles()).containsExactly("models/deep/streamed.txt");
		assertThat(read(service, "models/deep/streamed.txt")).isEqualTo("streamed");
	}

	@Test
	public void testBatchCommitAndDelete(@InjectService(cardinality = 0) ServiceAware<GitService> gsAware)
			throws Exception {
		GitService service = gsAware.waitForService(5000l);

		service.commit(CommitRequest.builder("populate") //
				.put("a.txt", "a".getBytes(StandardCharsets.UTF_8)) //
				.put("b.txt", "b".getBytes(StandardCharsets.UTF_8)) //
				.build());
		assertThat(service.getFiles().getFiles()).containsExactlyInAnyOrder("test", "a.txt", "b.txt");

		// One commit that both removes and replaces - the whole point of the batch API.
		service.commit(CommitRequest.builder("rework") //
				.delete("a.txt") //
				.put("b.txt", "b2".getBytes(StandardCharsets.UTF_8)) //
				.build());

		assertThat(service.getFiles().getFiles()).containsExactlyInAnyOrder("test", "b.txt");
		assertThat(read(service, "b.txt")).isEqualTo("b2");
	}

	@Test
	public void testDeleteFile(@InjectService(cardinality = 0) ServiceAware<GitService> gsAware) throws Exception {
		GitService service = gsAware.waitForService(5000l);

		service.writeFile("gone.txt", "gone".getBytes(StandardCharsets.UTF_8), "add");
		assertThat(service.getFiles().getFiles()).contains("gone.txt");

		service.deleteFile("gone.txt", "remove");

		assertThat(service.getFiles().getFiles()).containsExactly("test");
	}

	@Test
	public void testConfiguredAuthorIsRecorded(@InjectService(cardinality = 0) ServiceAware<GitService> gsAware)
			throws Exception {
		GitService service = gsAware.waitForService(5000l);

		service.writeFile("authored.txt", "x".getBytes(StandardCharsets.UTF_8), "authored commit");

		RevCommit head = service.getLog().iterator().next();
		assertThat(head.getFullMessage()).isEqualTo("authored commit");
		assertThat(head.getAuthorIdent()).extracting(PersonIdent::getName, PersonIdent::getEmailAddress)
				.containsExactly("Erika Mustermann", "em@example.com");
	}

	@Test
	public void testRequestAuthorOverridesConfiguration(
			@InjectService(cardinality = 0) ServiceAware<GitService> gsAware) throws Exception {
		GitService service = gsAware.waitForService(5000l);

		service.commit(CommitRequest.builder("overridden") //
				.put("overridden.txt", "x".getBytes(StandardCharsets.UTF_8)) //
				.author("Hans Wurst", "hw@example.com") //
				.build());

		assertThat(service.getLog().iterator().next().getAuthorIdent())
				.extracting(PersonIdent::getName, PersonIdent::getEmailAddress)
				.containsExactly("Hans Wurst", "hw@example.com");
	}

	@Test
	public void testPushOnLocalRepositoryIsANoop(@InjectService(cardinality = 0) ServiceAware<GitService> gsAware)
			throws Exception {
		GitService service = gsAware.waitForService(5000l);
		service.writeFile("local.txt", "x".getBytes(StandardCharsets.UTF_8), "local only");

		// A local repository has no remote; pushing must be a logged no-op rather than a failure,
		// so callers do not have to know which kind of repository they were configured against.
		service.push();

		assertThat(service.getFiles().getFiles()).contains("local.txt");
	}

	private String read(GitService service, String path) throws IOException {
		try (InputStream in = service.readLatestFile(path)) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
