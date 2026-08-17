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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.eclipse.fennec.jgit.exceptions.GitWriteException;

/**
 * A set of file changes to be applied as a single commit.
 * <p>
 * Changes are collected through {@link #builder(String)} and applied in the order
 * they were added, so a later change to the same path wins. All paths are
 * repository-relative and use {@code /} as separator; a leading {@code /} is
 * stripped.
 *
 * <pre>
 * String commitId = gitService.commit(CommitRequest.builder("update models") //
 * 		.put("models/sensor.ecore", bytes) //
 * 		.delete("models/obsolete.ecore") //
 * 		.build());
 * </pre>
 */
public final class CommitRequest {

	/**
	 * A single file change.
	 */
	public static final class Change {

		public enum Type {
			/** Add the file or replace its content. */
			PUT,
			/** Remove a single file; a path that does not exist is ignored. */
			DELETE,
			/** Remove a directory with everything below it. */
			DELETE_TREE
		}

		private final Type type;
		private final String path;
		private final byte[] content;

		private Change(Type type, String path, byte[] content) {
			this.type = type;
			this.path = path;
			this.content = content;
		}

		public Type getType() {
			return type;
		}

		public String getPath() {
			return path;
		}

		/**
		 * @return the new file content for a {@link Type#PUT}, {@code null} otherwise
		 */
		public byte[] getContent() {
			return content;
		}
	}

	private final String message;
	private final String authorName;
	private final String authorEmail;
	private final Boolean push;
	private final List<Change> changes;

	private CommitRequest(Builder builder) {
		this.message = builder.message;
		this.authorName = builder.authorName;
		this.authorEmail = builder.authorEmail;
		this.push = builder.push;
		this.changes = Collections.unmodifiableList(new ArrayList<>(builder.changes));
	}

	/**
	 * @param message the commit message, must not be blank
	 * @return a builder for a commit carrying the given message
	 */
	public static Builder builder(String message) {
		return new Builder(message);
	}

	public String getMessage() {
		return message;
	}

	/**
	 * @return the author name overriding the configured one, or {@code null}
	 */
	public String getAuthorName() {
		return authorName;
	}

	/**
	 * @return the author e-mail overriding the configured one, or {@code null}
	 */
	public String getAuthorEmail() {
		return authorEmail;
	}

	/**
	 * @return {@code true}/{@code false} to push (or not) regardless of the
	 *         configuration, or {@code null} to follow the configured default
	 */
	public Boolean getPush() {
		return push;
	}

	public List<Change> getChanges() {
		return changes;
	}

	/**
	 * Builder for a {@link CommitRequest}.
	 */
	public static final class Builder {

		private final String message;
		private final List<Change> changes = new ArrayList<>();
		private String authorName;
		private String authorEmail;
		private Boolean push;

		private Builder(String message) {
			if (message == null || message.isBlank()) {
				throw new IllegalArgumentException("A commit message is required");
			}
			this.message = message;
		}

		/**
		 * Adds the file at the given path or replaces its content.
		 */
		public Builder put(String path, byte[] content) {
			Objects.requireNonNull(content, "content");
			changes.add(new Change(Change.Type.PUT, normalize(path), content.clone()));
			return this;
		}

		/**
		 * Adds the file at the given path or replaces its content. The stream is read
		 * fully and closed.
		 */
		public Builder put(String path, InputStream content) {
			Objects.requireNonNull(content, "content");
			try (InputStream in = content) {
				return put(path, in.readAllBytes());
			} catch (IOException e) {
				throw new GitWriteException("Unable to read the content for " + path, e);
			}
		}

		/**
		 * Removes a single file. A path that does not exist is silently ignored.
		 */
		public Builder delete(String path) {
			changes.add(new Change(Change.Type.DELETE, normalize(path), null));
			return this;
		}

		/**
		 * Removes a directory with everything below it. A path that does not exist is
		 * silently ignored.
		 */
		public Builder deleteTree(String path) {
			changes.add(new Change(Change.Type.DELETE_TREE, normalize(path), null));
			return this;
		}

		/**
		 * Overrides the author configured for the service for this commit only.
		 */
		public Builder author(String name, String email) {
			this.authorName = name;
			this.authorEmail = email;
			return this;
		}

		/**
		 * Pushes (or explicitly does not push) after this commit, regardless of the
		 * configured {@code pushOnCommit} default.
		 */
		public Builder push(boolean push) {
			this.push = Boolean.valueOf(push);
			return this;
		}

		public CommitRequest build() {
			if (changes.isEmpty()) {
				throw new IllegalArgumentException("A commit needs at least one change");
			}
			return new CommitRequest(this);
		}

		private static String normalize(String path) {
			if (path == null || path.isBlank()) {
				throw new IllegalArgumentException("A path is required");
			}
			String normalized = path.strip();
			while (normalized.startsWith("/")) {
				normalized = normalized.substring(1);
			}
			if (normalized.isEmpty()) {
				throw new IllegalArgumentException("A path is required");
			}
			return normalized;
		}
	}
}
