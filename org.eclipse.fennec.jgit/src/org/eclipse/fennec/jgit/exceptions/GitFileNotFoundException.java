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

/**
 * Thrown when a file that was asked for does not exist in the tree of the commit it
 * was asked for.
 * <p>
 * Reading is otherwise unable to report this: an absent file and an empty file would
 * both come back as no content at all.
 */
public class GitFileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GitFileNotFoundException(String message) {
		super(message);
	}

	public GitFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
