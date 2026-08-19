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

/**
 * One file of a tree: its repository-relative path and the id of the blob holding
 * its content.
 * <p>
 * The blob id is git's own content hash of exactly that file. It is stable as long
 * as the content is, which makes it the natural value for an HTTP {@code ETag}, for
 * change detection between two commits and for deduplication. A commit id is not a
 * substitute: it changes on every commit, including ones that touch other files.
 *
 * @param path   the repository-relative path, {@code /}-separated
 * @param blobId the id of the blob holding the content, or {@code null} when the
 *               entry was built without one
 */
public record FileEntry(String path, String blobId) {
}
