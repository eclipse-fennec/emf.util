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
package org.eclipse.fennec.jgit.exceptions;

import org.eclipse.fennec.jgit.api.CommitRequest;
import org.eclipse.fennec.jgit.api.GitService;

/**
 * Thrown when the commit was written but could not be sent to the remote.
 * <p>
 * This is the half of {@link GitWriteException} where <em>the change is not lost</em>:
 * the commit is in the object database and on the branch, and a later
 * {@link GitService#push()} would complete it. A plain {@code GitWriteException} from
 * {@link GitService#commit(CommitRequest)} means the opposite — the object database
 * write failed and the branch never moved, so nothing was recorded.
 * <p>
 * The distinction is the caller's to act on: retrying a push is cheap and likely to
 * succeed once the remote is reachable again, while a failed commit has to be redone
 * from the content. A rejected push is not this exception but
 * {@link GitConflictException}, which says more: the remote has moved on, so the
 * commit has to be rebuilt before it can be sent.
 */
public class GitPushException extends GitWriteException {

	private static final long serialVersionUID = 1L;

	public GitPushException(String message) {
		super(message);
	}

	public GitPushException(String message, Throwable cause) {
		super(message, cause);
	}
}
