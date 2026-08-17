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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.TreeResult;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Drives the component against a real repository on disk, without OSGi: the
 * configuration is handed in the way Declarative Services would, and every method of
 * the service is exercised end to end.
 */
public class GitServiceImplLocalTest {

	private static final String FILE_CONTENT = "fooBar";

	@TempDir
	Path tempDir;

	private Path repoDir;
	private Git origin;
	private GitServiceImpl service;
	private RevCommit firstCommit;

	@BeforeEach
	public void before() throws Exception {
		repoDir = tempDir.resolve("repo");
		Files.createDirectories(repoDir);
		origin = Git.init().setDirectory(repoDir.toFile()).setInitialBranch("main").call();
		Files.writeString(repoDir.resolve("test"), FILE_CONTENT);
		Files.createDirectories(repoDir.resolve("models"));
		Files.writeString(repoDir.resolve("models/sensor.ecore"), "<ecore/>");
		origin.add().addFilepattern(".").call();
		firstCommit = origin.commit().setAuthor("Hans Wurst", "hw@example.com").setMessage("initial").call();

		service = new GitServiceImpl();
		service.activate(config());
	}

	private TestGitConfig config() {
		return new TestGitConfig(repoDir.toString());
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

	// --- reading -----------------------------------------------------------------

	@Test
	public void testMetadata() {
		assertThat(service.getBranch()).isEqualTo("main");
		assertThat(service.getRef()).isEqualTo("refs/heads/main");
		assertThat(service.getGitUrl()).isEqualTo(repoDir.toString());
	}

	@Test
	public void testGetBranches() {
		assertThat(service.getBranches()).containsExactly("refs/heads/main");
	}

	@Test
	public void testGetLog() throws Exception {
		assertThat(service.getLog()).extracting(RevCommit::getFullMessage).containsExactly("initial");
	}

	@Test
	public void testGetFiles() {
		TreeResult result = service.getFiles();

		assertThat(result.getCommitId()).isEqualTo(firstCommit.getName());
		assertThat(result.getFiles()).containsExactlyInAnyOrder("test", "models/sensor.ecore");
	}

	@Test
	public void testGetFilesWithPrefix() {
		assertThat(service.getFiles("models").getFiles()).containsExactly("models/sensor.ecore");
	}

	@Test
	public void testReadLatestFile() throws Exception {
		assertThat(read("test")).isEqualTo(FILE_CONTENT);
	}

	@Test
	public void testLoadLatestFile() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		service.loadLatestFile("test", out);

		assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo(FILE_CONTENT);
	}

	@Test
	public void testReadFileAtAnEarlierCommit() throws Exception {
		service.writeFile("test", "changed".getBytes(StandardCharsets.UTF_8), "change test");

		assertThat(read("test")).isEqualTo("changed");
		try (InputStream in = service.readFile(firstCommit.getName(), "test")) {
			assertThat(new String(in.readAllBytes(), StandardCharsets.UTF_8)).isEqualTo(FILE_CONTENT);
		}
	}

	@Test
	public void testLoadFileAtAnEarlierCommit() throws Exception {
		service.writeFile("test", "changed".getBytes(StandardCharsets.UTF_8), "change test");

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		service.loadFile(firstCommit.getName(), "test", out);

		assertThat(out.toString(StandardCharsets.UTF_8)).isEqualTo(FILE_CONTENT);
	}

	@Test
	public void testReadingAMissingFileFails() {
		assertThatThrownBy(() -> service.readLatestFile("does/not/exist"))
				.isInstanceOf(GitFileNotFoundException.class) //
				.hasMessageContaining("does/not/exist") //
				.hasMessageContaining("refs/heads/main");
	}

	@Test
	public void testLoadingAMissingFileFails() {
		assertThatThrownBy(() -> service.loadLatestFile("does/not/exist", new ByteArrayOutputStream()))
				.isInstanceOf(GitFileNotFoundException.class);
		assertThatThrownBy(() -> service.loadFile(firstCommit.getName(), "nope", new ByteArrayOutputStream()))
				.isInstanceOf(GitFileNotFoundException.class) //
				.hasMessageContaining(firstCommit.getName());
	}

	/**
	 * An empty file exists, and reading it yields nothing — which is exactly why a
	 * missing file may not also yield nothing.
	 */
	@Test
	public void testAnEmptyFileIsNotAMissingFile() throws Exception {
		service.writeFile("empty.txt", new byte[0], "add an empty file");

		assertThat(read("empty.txt")).isEmpty();
		assertThatThrownBy(() -> service.readLatestFile("absent.txt"))
				.isInstanceOf(GitFileNotFoundException.class);
	}

	/**
	 * A file that was deleted is gone, not empty — the case that made the old
	 * behaviour actively misleading.
	 */
	@Test
	public void testReadingADeletedFileFails() {
		service.deleteFile("test", "remove test");

		assertThatThrownBy(() -> service.readLatestFile("test")).isInstanceOf(GitFileNotFoundException.class);
	}

	/**
	 * A directory is not a file. The path filter also lets the content of a directory
	 * of that name through, so without an exact-match check this used to hand back the
	 * content of the first file inside it.
	 */
	@Test
	public void testReadingADirectoryFails() {
		assertThatThrownBy(() -> service.readLatestFile("models")) //
				.isInstanceOf(GitFileNotFoundException.class);
	}

	/** A path that merely shares a prefix with an existing file is still missing. */
	@Test
	public void testReadingAPrefixOfAnExistingFileFails() {
		assertThatThrownBy(() -> service.readLatestFile("tes")).isInstanceOf(GitFileNotFoundException.class);
	}

	@Test
	public void testFetchOnALocalRepositoryIsANoop() {
		// No remote to fetch from; the call must not fail, so callers need not know which
		// kind of repository they were configured against.
		service.fetch();

		assertThat(service.getFiles().getFiles()).contains("test");
	}

	// --- writing -----------------------------------------------------------------

	@Test
	public void testWriteFile() throws Exception {
		String commitId = service.writeFile("written.txt", "written".getBytes(StandardCharsets.UTF_8), "add written");

		assertThat(service.getFiles().getCommitId()).isEqualTo(commitId);
		assertThat(service.getFiles().getFiles()).contains("written.txt");
		assertThat(read("written.txt")).isEqualTo("written");
	}

	@Test
	public void testWriteFileFromStream() throws Exception {
		InputStream content = new ByteArrayInputStream("streamed".getBytes(StandardCharsets.UTF_8));

		service.writeFile("streamed.txt", content, "add streamed");

		assertThat(read("streamed.txt")).isEqualTo("streamed");
	}

	@Test
	public void testDeleteFile() {
		service.deleteFile("test", "remove test");

		assertThat(service.getFiles().getFiles()).containsExactly("models/sensor.ecore");
	}

	@Test
	public void testBatchCommit() throws Exception {
		service.commit(CommitRequest.builder("rework") //
				.put("a.txt", "a".getBytes(StandardCharsets.UTF_8)) //
				.delete("test") //
				.deleteTree("models") //
				.build());

		assertThat(service.getFiles().getFiles()).containsExactly("a.txt");
		assertThat(read("a.txt")).isEqualTo("a");
	}

	@Test
	public void testCommitsChainOnTheExistingHistory() throws Exception {
		service.writeFile("one.txt", "1".getBytes(StandardCharsets.UTF_8), "one");
		service.writeFile("two.txt", "2".getBytes(StandardCharsets.UTF_8), "two");

		assertThat(service.getLog()).extracting(RevCommit::getFullMessage).containsExactly("two", "one", "initial");
		assertThat(service.getFiles().getFiles()).containsExactlyInAnyOrder("test", "models/sensor.ecore", "one.txt",
				"two.txt");
	}

	@Test
	public void testConfiguredAuthorIsRecorded() throws Exception {
		service.writeFile("authored.txt", "x".getBytes(StandardCharsets.UTF_8), "authored");

		assertThat(head().getAuthorIdent()).extracting(PersonIdent::getName, PersonIdent::getEmailAddress)
				.containsExactly("Fennec Git Service", "fennec@eclipse.org");
		assertThat(head().getCommitterIdent().getName()).isEqualTo("Fennec Git Service");
	}

	@Test
	public void testConfiguredAuthorCanBeChanged() throws Exception {
		service.deactivate();
		service = new GitServiceImpl();
		service.activate(config().author("Erika Mustermann", "em@example.com"));

		service.writeFile("authored.txt", "x".getBytes(StandardCharsets.UTF_8), "authored");

		assertThat(head().getAuthorIdent()).extracting(PersonIdent::getName, PersonIdent::getEmailAddress)
				.containsExactly("Erika Mustermann", "em@example.com");
	}

	@Test
	public void testRequestAuthorOverridesTheConfiguredOne() throws Exception {
		service.commit(CommitRequest.builder("overridden") //
				.put("a.txt", "a".getBytes(StandardCharsets.UTF_8)) //
				.author("Hans Wurst", "hw@example.com") //
				.build());

		assertThat(head().getAuthorIdent()).extracting(PersonIdent::getName, PersonIdent::getEmailAddress)
				.containsExactly("Hans Wurst", "hw@example.com");
	}

	/**
	 * The documented consequence of writing straight into the object database: the
	 * branch moves, the working tree of a local repository does not follow.
	 */
	@Test
	public void testWorkingTreeIsNotUpdated() {
		service.writeFile("written.txt", "written".getBytes(StandardCharsets.UTF_8), "add written");

		assertThat(service.getFiles().getFiles()).contains("written.txt");
		assertThat(repoDir.resolve("written.txt")).doesNotExist();
	}

	@Test
	public void testPushOnALocalRepositoryIsANoop() {
		service.writeFile("local.txt", "x".getBytes(StandardCharsets.UTF_8), "local only");

		service.push();

		assertThat(service.getFiles().getFiles()).contains("local.txt");
	}

	@Test
	public void testPushOnCommitOnALocalRepositoryIsHarmless() throws Exception {
		service.deactivate();
		service = new GitServiceImpl();
		service.activate(config().pushOnCommit(true));

		service.writeFile("local.txt", "x".getBytes(StandardCharsets.UTF_8), "local only");

		assertThat(service.getFiles().getFiles()).contains("local.txt");
	}

	@Test
	public void testCommitWithoutARequestIsRejected() {
		assertThatThrownBy(() -> service.commit(null)).isInstanceOf(NullPointerException.class);
	}

	private RevCommit head() throws Exception {
		return service.getLog().iterator().next();
	}

	private String read(String path) throws IOException {
		try (InputStream in = service.readLatestFile(path)) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
