/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.jgit.exceptions;

import org.eclipse.fennec.jgit.api.GitService;

/**
 * Thrown when a write loses a race against a concurrent update: either the local
 * branch moved between reading the head and updating the ref, or the remote
 * rejected the push as non-fast-forward because it has commits this repository
 * does not know about.
 * <p>
 * The caller is expected to {@link GitService#fetch() fetch}, rebuild its change
 * on top of the new head and retry. The service does not retry on its own,
 * because only the caller can decide how to merge conflicting content.
 */
public class GitConflictException extends GitWriteException {

	private static final long serialVersionUID = 1L;

	public GitConflictException(String message) {
		super(message);
	}

	public GitConflictException(String message, Throwable cause) {
		super(message, cause);
	}
}
