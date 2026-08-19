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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The files of one commit, as returned by {@link GitService#getFiles()}.
 */
public class TreeResult {

	private final String commitId;
	private final List<FileEntry> entries;
	/** Built on first lookup; a listing is usually consumed as a list, not by path. */
	private volatile Map<String, String> blobIds;

	/**
	 * Builds a result carrying paths only. The blob id of every entry is
	 * {@code null}; prefer {@link #of(String, List)}.
	 *
	 * @param commitId the commit the listing was taken from, or {@code null} for a
	 *                 branch without commits
	 * @param files    the repository-relative paths
	 */
	public TreeResult(String commitId, List<String> files) {
		this.commitId = commitId;
		List<FileEntry> converted = new ArrayList<>(files.size());
		for (String file : files) {
			converted.add(new FileEntry(file, null));
		}
		this.entries = Collections.unmodifiableList(converted);
	}

	private TreeResult(String commitId, List<FileEntry> entries, boolean copy) {
		this.commitId = commitId;
		this.entries = Collections.unmodifiableList(copy ? new ArrayList<>(entries) : entries);
	}

	/**
	 * @param commitId the commit the listing was taken from, or {@code null} for a
	 *                 branch without commits
	 * @param entries  the files of that commit, with their blob ids
	 */
	public static TreeResult of(String commitId, List<FileEntry> entries) {
		Objects.requireNonNull(entries, "entries");
		return new TreeResult(commitId, entries, true);
	}

	/**
	 * @return the commit the listing was taken from, or {@code null} if the branch
	 *         has no commits yet — in which case the listing is empty
	 */
	public String getCommitId() {
		return commitId;
	}

	/**
	 * @return the repository-relative paths of all listed files
	 */
	public List<String> getFiles() {
		List<String> files = new ArrayList<>(entries.size());
		for (FileEntry entry : entries) {
			files.add(entry.path());
		}
		return files;
	}

	/**
	 * @return the listed files with their blob ids
	 */
	public List<FileEntry> getEntries() {
		return entries;
	}

	/**
	 * @param path a repository-relative path
	 * @return the id of the blob holding that file's content, or {@code null} if the
	 *         path is not in this listing (or the listing carries no blob ids)
	 */
	public String getBlobId(String path) {
		Map<String, String> ids = blobIds;
		if (ids == null) {
			ids = new HashMap<>();
			for (FileEntry entry : entries) {
				ids.put(entry.path(), entry.blobId());
			}
			blobIds = ids;
		}
		return ids.get(path);
	}

}
