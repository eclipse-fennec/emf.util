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
package org.eclipse.fennec.jgit.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.eclipse.fennec.jgit.exceptions.GitConflictException;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.fennec.jgit.exceptions.GitPushException;
import org.eclipse.fennec.jgit.exceptions.GitWriteException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface GitService {

	/**
	 * Lists every file of the head of the configured branch.
	 *
	 * @return the listing; empty, with a {@code null} commit id, if the branch has
	 *         no commits yet
	 */
	TreeResult getFiles();

	/**
	 * Lists the files of the head of the configured branch below the given path
	 * prefix.
	 *
	 * @return the listing; empty, with a {@code null} commit id, if the branch has
	 *         no commits yet
	 */
	TreeResult getFiles(String prefix);

	/**
	 * Tells whether a file exists, without reading its content.
	 *
	 * @param commitId the commit to look in, or {@code null} for the head of the
	 *                 configured branch
	 * @param path     the repository-relative path of the file
	 * @return {@code true} if that commit has a file at that path; {@code false} for
	 *         a path that is absent, is a directory, or when the branch has no
	 *         commits yet
	 */
	boolean exists(String commitId, String path);

	/**
	 * Reads the id of the blob holding a file's content, without reading the content
	 * itself.
	 * <p>
	 * The blob id is git's own content hash of exactly that file, so it identifies
	 * the content and nothing else — unlike a commit id, which changes whenever any
	 * file changes.
	 *
	 * @param commitId the commit to look in, or {@code null} for the head of the
	 *                 configured branch
	 * @param path     the repository-relative path of the file
	 * @return the blob id, or empty if there is no file at that path in that commit
	 */
	Optional<String> blobId(String commitId, String path);

	/**
	 * Writes the content of the file as of the head of the configured branch.
	 *
	 * @throws GitFileNotFoundException if the file is not in that commit; an absent
	 *                                  file is not the same as an empty one
	 */
	void loadLatestFile(String file, OutputStream out) throws RevisionSyntaxException, IOException;

	/**
	 * @return the branches of this repository, as fully qualified ref names; for a
	 *         remote that includes the remote-tracking ones
	 */
	List<String> getBranches();

	/**
	 * @return the history of the configured branch, newest first; empty if the
	 *         branch has no commits yet
	 */
	Iterable<RevCommit> getLog() throws GitAPIException;

	/**
	 * Reads the content of the file as of the head of the configured branch.
	 *
	 * @throws GitFileNotFoundException if the file is not in that commit; an absent
	 *                                  file is not the same as an empty one
	 */
	InputStream readLatestFile(String file);

	/**
	 * Writes the content of the file as of the given commit, or the head of the
	 * configured branch when {@code commitId} is {@code null}.
	 *
	 * @throws GitFileNotFoundException if the file is not in that commit; an absent
	 *                                  file is not the same as an empty one
	 */
	void loadFile(String commitId, String file, OutputStream out);

	/**
	 * Reads the content of the file as of the given commit, or the head of the
	 * configured branch when {@code commitId} is {@code null}.
	 *
	 * @throws GitFileNotFoundException if the file is not in that commit; an absent
	 *                                  file is not the same as an empty one
	 */
	InputStream readFile(String commitId, String file);

	String getBranch();

	String getGitUrl();

	/**
	 * The remote this service talks to: the URL configured as {@code remote} for a
	 * repository on disk, or {@link #getGitUrl()} itself when {@code repo} is a URL
	 * and the repository is an in-memory mirror of it.
	 *
	 * @return the remote's URL, or {@code null} for a repository on disk that stands
	 *         alone — in which case {@link #push()}, {@link #fetch()} and
	 *         {@link #resetToRemote()} do nothing and {@link #getRemoteHead()} is
	 *         {@code null}
	 */
	String getRemoteUrl();

	/**
	 * Brings the mirror of a remote up to date: the remote-tracking refs are
	 * updated, and the configured branch follows if that is a fast-forward.
	 * <p>
	 * A branch carrying local commits the remote does not have is <em>not</em>
	 * moved, so a fetch never discards unpushed work. Reconciling a diverged branch
	 * is the caller's decision: read the remote's side at {@link #getRemoteHead()},
	 * then {@link #resetToRemote()} and re-apply the changes.
	 * <p>
	 * Does nothing for a repository on disk, which has no remote.
	 */
	void fetch();

	/**
	 * The remote's copy of the configured branch as far as this service knows it:
	 * what the last {@link #fetch()} saw, or what the last successful
	 * {@link #push()} put there — so a commit that has been pushed reads as
	 * pushed, and a commit that has not reads as local.
	 *
	 * @return the commit id, or {@code null} for a repository on disk or a remote
	 *         that does not have that branch
	 */
	String getRemoteHead();

	/**
	 * Moves the configured branch to the remote's copy of it, as of the last
	 * {@link #fetch()}, <em>discarding any local commit that is not on the
	 * remote</em>.
	 * <p>
	 * This is the deliberate way out of a rejected push: fetch, read what the remote
	 * has, reset, then re-apply the changes on top and push again. Nothing else in
	 * this service throws local commits away.
	 * <p>
	 * Does nothing for a repository on disk, which has no remote.
	 *
	 * @return the id of the commit the branch now points at, or {@code null} if
	 *         there is none
	 * @throws GitWriteException if the branch was never fetched, or the ref could
	 *                           not be moved
	 */
	String resetToRemote();

	/**
	 * @return
	 */
	String getRef();

	/**
	 * Applies all changes of the request as a single commit on the configured
	 * branch.
	 * <p>
	 * The commit is built directly against the object database, so it works the
	 * same for the in-memory mirror of a remote repository and for a local one. For
	 * a local repository with a working tree that means the branch moves but the
	 * working tree and index are left untouched.
	 * <p>
	 * The commit stays local unless {@code pushOnCommit} is configured or the
	 * request asks for a push; see {@link #push()}.
	 *
	 * A request that changes nothing — deleting an absent path, writing content that
	 * is already stored — does not produce a commit, unless it asks for one with
	 * {@code allowEmpty}.
	 *
	 * @param request the changes to apply, must not be {@code null}
	 * @return the id of the new commit, or of the unchanged head if the request
	 *         changed nothing
	 * @throws GitConflictException if the branch moved concurrently
	 * @throws GitPushException     if the commit was written but the push that
	 *                              followed it failed; the change is recorded and a
	 *                              later {@link #push()} completes it
	 * @throws GitWriteException    if the commit could not be written, in which case
	 *                              nothing was recorded at all
	 */
	String commit(CommitRequest request);

	/**
	 * Commits the given content as the single file at the given path.
	 *
	 * @return the id of the new commit, or of the unchanged head if the content is
	 *         already stored
	 */
	String writeFile(String path, byte[] content, String message);

	/**
	 * Commits the given content as the single file at the given path. The stream is
	 * read fully and closed.
	 *
	 * @return the id of the new commit, or of the unchanged head if the content is
	 *         already stored
	 */
	String writeFile(String path, InputStream content, String message);

	/**
	 * Commits the removal of the file at the given path.
	 *
	 * @return the id of the new commit, or of the unchanged head if the path does
	 *         not exist — removing something that is not there is not an error, and
	 *         does not produce a commit
	 */
	String deleteFile(String path, String message);

	/**
	 * Pushes the configured branch to the remote. Does nothing for a local
	 * repository, which has no remote to push to.
	 *
	 * @throws GitConflictException if the remote rejected the push as
	 *                              non-fast-forward, i.e. it carries commits this
	 *                              repository does not have; {@link #fetch()},
	 *                              reconcile against {@link #getRemoteHead()}, then
	 *                              {@link #resetToRemote()} and re-apply
	 * @throws GitPushException     if the push failed for any other reason; the
	 *                              commits stay where they are and pushing again is
	 *                              worth trying
	 */
	void push();

}
