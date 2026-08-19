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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.exceptions.GitConflictException;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the in-core commit path against an {@link InMemoryRepository} — the same
 * kind of working-tree-less repository the service uses to mirror a remote — so the
 * tree building, the parent chain and the ref update are exercised without OSGi and
 * without touching the file system.
 */
public class GitCommitWriterTest {

	private static final String REF = "refs/heads/main";
	private static final PersonIdent AUTHOR = new PersonIdent("Hans Wurst", "hw@example.com");

	private Repository repo;
	private GitCommitWriter writer;

	@BeforeEach
	public void before() {
		repo = new InMemoryRepository(new DfsRepositoryDescription("test"));
		writer = new GitCommitWriter(repo);
	}

	@AfterEach
	public void after() {
		repo.close();
	}

	@Test
	public void testFirstCommitOnEmptyRepository() throws Exception {
		ObjectId commitId = writer.commit(REF, CommitRequest.builder("add test") //
				.put("test", "fooBar".getBytes(StandardCharsets.UTF_8)) //
				.build(), AUTHOR);

		assertThat(repo.resolve(REF)).isEqualTo(commitId);
		assertThat(read("test")).isEqualTo("fooBar");
		assertThat(files()).containsExactly("test");

		RevCommit commit = parse(commitId);
		assertThat(commit.getParentCount()).isZero();
		assertThat(commit.getFullMessage()).isEqualTo("add test");
		assertThat(commit.getAuthorIdent().getName()).isEqualTo("Hans Wurst");
		assertThat(commit.getCommitterIdent().getEmailAddress()).isEqualTo("hw@example.com");
	}

	@Test
	public void testSecondCommitKeepsExistingFilesAndChainsParent() throws Exception {
		ObjectId first = writer.commit(REF, CommitRequest.builder("add test") //
				.put("test", "fooBar".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR);
		ObjectId second = writer.commit(REF, CommitRequest.builder("add nested") //
				.put("models/deep/sensor.ecore", "<ecore/>".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR);

		assertThat(files()).containsExactlyInAnyOrder("test", "models/deep/sensor.ecore");
		assertThat(read("test")).isEqualTo("fooBar");
		assertThat(parse(second).getParent(0)).isEqualTo(first);
	}

	@Test
	public void testPutReplacesContent() throws Exception {
		writer.commit(REF, CommitRequest.builder("add") //
				.put("test", "one".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR);
		writer.commit(REF, CommitRequest.builder("replace") //
				.put("test", new ByteArrayInputStream("two".getBytes(StandardCharsets.UTF_8))).build(), AUTHOR);

		assertThat(files()).containsExactly("test");
		assertThat(read("test")).isEqualTo("two");
	}

	@Test
	public void testLastChangeForOnePathWins() throws Exception {
		writer.commit(REF, CommitRequest.builder("batch") //
				.put("test", "one".getBytes(StandardCharsets.UTF_8)) //
				.put("test", "two".getBytes(StandardCharsets.UTF_8)) //
				.build(), AUTHOR);

		assertThat(read("test")).isEqualTo("two");
	}

	@Test
	public void testDeleteFileAndTree() throws Exception {
		writer.commit(REF, CommitRequest.builder("populate") //
				.put("keep", "keep".getBytes(StandardCharsets.UTF_8)) //
				.put("drop", "drop".getBytes(StandardCharsets.UTF_8)) //
				.put("models/a.ecore", "a".getBytes(StandardCharsets.UTF_8)) //
				.put("models/b.ecore", "b".getBytes(StandardCharsets.UTF_8)) //
				.build(), AUTHOR);

		writer.commit(REF, CommitRequest.builder("clean up") //
				.delete("drop") //
				.deleteTree("models") //
				.build(), AUTHOR);

		assertThat(files()).containsExactly("keep");
	}

	@Test
	public void testDeleteOfUnknownPathIsIgnored() throws Exception {
		writer.commit(REF, CommitRequest.builder("add").put("test", "x".getBytes(StandardCharsets.UTF_8)).build(),
				AUTHOR);
		writer.commit(REF, CommitRequest.builder("delete nothing").delete("does/not/exist").build(), AUTHOR);

		assertThat(files()).containsExactly("test");
	}

	@Test
	public void testAddAndDeleteInOneCommit() throws Exception {
		writer.commit(REF, CommitRequest.builder("add").put("old", "x".getBytes(StandardCharsets.UTF_8)).build(),
				AUTHOR);
		writer.commit(REF, CommitRequest.builder("move") //
				.put("new", "x".getBytes(StandardCharsets.UTF_8)) //
				.delete("old") //
				.build(), AUTHOR);

		assertThat(files()).containsExactly("new");
	}

	@Test
	public void testConcurrentBranchUpdateIsRejected() throws Exception {
		ObjectId first = writer.commit(REF,
				CommitRequest.builder("first").put("test", "x".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR);
		ObjectId second = writer.commit(REF,
				CommitRequest.builder("second").put("test2", "y".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR);

		// A writer whose head is out of date: it builds its commit on 'first' while the branch
		// has already moved on to 'second'. Silently taking the ref would drop that commit, so
		// the expected-old-id check has to reject it.
		GitCommitWriter stale = new GitCommitWriter(repo) {
			@Override
			ObjectId resolveHead(String ref) {
				return first;
			}
		};

		assertThatThrownBy(() -> stale.commit(REF,
				CommitRequest.builder("stale").put("test3", "z".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR))
				.isInstanceOf(GitConflictException.class) //
				.hasMessageContaining("re-read the head and re-apply");

		// The losing commit left the branch exactly where it was.
		assertThat(repo.resolve(REF)).isEqualTo(second);
		assertThat(files()).containsExactlyInAnyOrder("test", "test2");
	}

	/**
	 * A request that leaves the tree as it was writes no commit — a commit whose tree
	 * is its parent's records nothing, but is a new object with a new timestamp, so it
	 * moves the branch and shows up in a history kept as an audit trail.
	 */
	@Test
	public void testARequestThatChangesNothingWritesNoCommit() throws Exception {
		ObjectId first = writer.commit(REF,
				CommitRequest.builder("first").put("test", "x".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR);

		ObjectId sameContent = writer.commit(REF,
				CommitRequest.builder("same content").put("test", "x".getBytes(StandardCharsets.UTF_8)).build(),
				AUTHOR);
		ObjectId absentPath = writer.commit(REF, CommitRequest.builder("delete nothing").delete("absent").build(),
				AUTHOR);

		assertThat(sameContent).as("rewriting the same content").isEqualTo(first);
		assertThat(absentPath).as("deleting a path that is not there").isEqualTo(first);
		assertThat(repo.resolve(REF)).isEqualTo(first);
	}

	/** A caller that wants the history entry anyway can still have it. */
	@Test
	public void testAnEmptyCommitCanBeAskedFor() throws Exception {
		ObjectId first = writer.commit(REF,
				CommitRequest.builder("first").put("test", "x".getBytes(StandardCharsets.UTF_8)).build(), AUTHOR);

		ObjectId empty = writer.commit(REF, CommitRequest.builder("mark") //
				.put("test", "x".getBytes(StandardCharsets.UTF_8)) //
				.allowEmpty(true) //
				.build(), AUTHOR);

		assertThat(empty).isNotEqualTo(first);
		assertThat(parse(empty).getTree()).as("same tree, new commit").isEqualTo(parse(first).getTree());
		assertThat(parse(empty).getParent(0)).isEqualTo(first);
	}

	@Test
	public void testEmptyRequestIsRejected() {
		assertThatThrownBy(() -> CommitRequest.builder("nothing").build())
				.isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> CommitRequest.builder("")).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testLeadingSlashIsStripped() throws Exception {
		writer.commit(REF, CommitRequest.builder("add").put("/test", "x".getBytes(StandardCharsets.UTF_8)).build(),
				AUTHOR);

		assertThat(files()).containsExactly("test");
	}

	private RevCommit parse(ObjectId commitId) throws Exception {
		try (RevWalk revWalk = new RevWalk(repo)) {
			return revWalk.parseCommit(commitId);
		}
	}

	private List<String> files() throws Exception {
		List<String> paths = new ArrayList<>();
		try (RevWalk revWalk = new RevWalk(repo); TreeWalk treeWalk = new TreeWalk(repo)) {
			treeWalk.addTree(revWalk.parseCommit(repo.resolve(REF)).getTree());
			treeWalk.setRecursive(true);
			while (treeWalk.next()) {
				paths.add(treeWalk.getPathString());
			}
		}
		return paths;
	}

	private String read(String path) throws Exception {
		try (RevWalk revWalk = new RevWalk(repo);
				TreeWalk treeWalk = TreeWalk.forPath(repo, path, revWalk.parseCommit(repo.resolve(REF)).getTree())) {
			assertThat(treeWalk).as("path %s exists", path).isNotNull();
			return new String(repo.open(treeWalk.getObjectId(0)).getBytes(), StandardCharsets.UTF_8);
		}
	}
}
