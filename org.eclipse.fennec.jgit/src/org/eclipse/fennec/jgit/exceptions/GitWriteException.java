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

/**
 * Thrown when a write to the repository (commit or push) fails.
 */
public class GitWriteException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GitWriteException(String message) {
		super(message);
	}

	public GitWriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
