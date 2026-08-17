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

import org.eclipse.fennec.jgit.exceptions.GitConflictException;
import org.eclipse.fennec.jgit.exceptions.GitFileNotFoundException;
import org.eclipse.fennec.jgit.exceptions.GitWriteException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.revwalk.RevCommit;

public interface GitService {

	TreeResult getFiles();

	TreeResult getFiles(String prefix);

	/**
	 * Writes the content of the file as of the head of the configured branch.
	 *
	 * @throws GitFileNotFoundException if the file is not in that commit; an absent
	 *                                  file is not the same as an empty one
	 */
	void loadLatestFile(String file, OutputStream out) throws RevisionSyntaxException, IOException;

	List<String> getBranches();

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

	void fetch();

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
	 * @param request the changes to apply, must not be {@code null}
	 * @return the id of the new commit
	 * @throws GitConflictException if the branch moved concurrently
	 * @throws GitWriteException    if the commit could not be written
	 */
	String commit(CommitRequest request);

	/**
	 * Commits the given content as the single file at the given path.
	 *
	 * @return the id of the new commit
	 */
	String writeFile(String path, byte[] content, String message);

	/**
	 * Commits the given content as the single file at the given path. The stream is
	 * read fully and closed.
	 *
	 * @return the id of the new commit
	 */
	String writeFile(String path, InputStream content, String message);

	/**
	 * Commits the removal of the file at the given path. A path that does not exist
	 * is silently ignored.
	 *
	 * @return the id of the new commit
	 */
	String deleteFile(String path, String message);

	/**
	 * Pushes the configured branch to the remote. Does nothing for a local
	 * repository, which has no remote to push to.
	 *
	 * @throws GitConflictException if the remote rejected the push as
	 *                              non-fast-forward, i.e. it carries commits this
	 *                              repository does not have; {@link #fetch()} and
	 *                              retry
	 * @throws GitWriteException    if the push failed for any other reason
	 */
	void push();

}
