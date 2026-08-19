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
import static org.assertj.core.api.Assertions.tuple;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.FileEntry;
import org.eclipse.fennec.jgit.api.TreeResult;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectInserter;
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

	// --- existence and blob ids --------------------------------------------------

	@Test
	public void testExists() {
		assertThat(service.exists(null, "test")).isTrue();
		assertThat(service.exists(null, "models/sensor.ecore")).isTrue();
		assertThat(service.exists(null, "does/not/exist")).isFalse();
	}

	/** The same distinctions the read methods make, without reading anything. */
	@Test
	public void testWhatDoesNotExist() {
		assertThat(service.exists(null, "models")).as("a directory is not a file").isFalse();
		assertThat(service.exists(null, "tes")).as("a prefix of a file is not a file").isFalse();

		service.deleteFile("test", "remove test");

		assertThat(service.exists(null, "test")).as("a deleted file is gone").isFalse();
		assertThat(service.exists(firstCommit.getName(), "test")).as("but it is still in the older commit").isTrue();
	}

	/** The blob id is git's own content hash, the one {@code git hash-object} prints. */
	@Test
	public void testBlobIdIsTheContentHash() throws Exception {
		String expected = hashObject(FILE_CONTENT);

		assertThat(service.blobId(null, "test")).contains(expected);
		assertThat(service.getFiles().getBlobId("test")).isEqualTo(expected);
		assertThat(service.getFiles().getEntries()) //
				.extracting(FileEntry::path, FileEntry::blobId) //
				.contains(tuple("test", expected));
	}

	@Test
	public void testBlobIdOfAMissingFileIsEmpty() {
		assertThat(service.blobId(null, "does/not/exist")).isEmpty();
		assertThat(service.getFiles().getBlobId("does/not/exist")).isNull();
	}

	/**
	 * Why the blob id is worth having: it identifies the content of one file, so it
	 * survives a commit that touches another one — where the commit id, the only thing
	 * a caller used to get, changes with every write anywhere in the tree.
	 */
	@Test
	public void testBlobIdIsUnchangedByACommitElsewhere() {
		String before = service.getFiles().getBlobId("test");
		String commitBefore = service.getFiles().getCommitId();

		service.writeFile("unrelated.txt", "x".getBytes(StandardCharsets.UTF_8), "unrelated");

		assertThat(service.getFiles().getCommitId()).isNotEqualTo(commitBefore);
		assertThat(service.getFiles().getBlobId("test")).isEqualTo(before);
	}

	@Test
	public void testBlobIdFollowsTheContent() {
		String before = service.getFiles().getBlobId("test");

		service.writeFile("test", "changed".getBytes(StandardCharsets.UTF_8), "change test");

		assertThat(service.getFiles().getBlobId("test")).isNotEqualTo(before) //
				.isEqualTo(hashObject("changed"));
		assertThat(service.blobId(firstCommit.getName(), "test")).contains(before);
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

	/**
	 * An idempotent write is not a change, and a history kept as an audit trail should
	 * not fill up with entries that record nothing.
	 */
	@Test
	public void testWritingTheSameContentAgainWritesNoCommit() throws Exception {
		String first = service.writeFile("same.txt", "x".getBytes(StandardCharsets.UTF_8), "add same");

		String second = service.writeFile("same.txt", "x".getBytes(StandardCharsets.UTF_8), "add same again");

		assertThat(second).isEqualTo(first);
		assertThat(service.getFiles().getCommitId()).isEqualTo(first);
		assertThat(service.getLog()).extracting(RevCommit::getFullMessage).containsExactly("add same", "initial");
	}

	@Test
	public void testDeletingAnAbsentFileWritesNoCommit() throws Exception {
		String head = service.getFiles().getCommitId();

		String result = service.deleteFile("never/was/there", "remove nothing");

		assertThat(result).isEqualTo(head);
		assertThat(service.getLog()).extracting(RevCommit::getFullMessage).containsExactly("initial");
	}

	/** A caller that wants the entry anyway can still have it. */
	@Test
	public void testAnEmptyCommitCanBeAskedFor() throws Exception {
		String head = service.getFiles().getCommitId();

		String marked = service.commit(CommitRequest.builder("mark") //
				.put("test", FILE_CONTENT.getBytes(StandardCharsets.UTF_8)) //
				.allowEmpty(true) //
				.build());

		assertThat(marked).isNotEqualTo(head);
		assertThat(service.getLog()).extracting(RevCommit::getFullMessage).containsExactly("mark", "initial");
	}

	@Test
	public void testCommitWithoutARequestIsRejected() {
		assertThatThrownBy(() -> service.commit(null)).isInstanceOf(NullPointerException.class);
	}

	private RevCommit head() throws Exception {
		return service.getLog().iterator().next();
	}

	/** What {@code git hash-object} would print for that content. */
	private String hashObject(String content) {
		try (ObjectInserter inserter = origin.getRepository().newObjectInserter()) {
			return inserter.idFor(Constants.OBJ_BLOB, content.getBytes(StandardCharsets.UTF_8)).getName();
		}
	}

	private String read(String path) throws IOException {
		try (InputStream in = service.readLatestFile(path)) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
