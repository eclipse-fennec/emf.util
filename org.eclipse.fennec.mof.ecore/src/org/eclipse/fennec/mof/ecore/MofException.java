/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore;

/**
 * Thrown when a DMTF MOF document cannot be parsed, an {@code #pragma include} cannot be
 * resolved, or (in strict mode) a superclass or {@code REF} target is not declared in the
 * imported MOF. Parse errors carry the location as {@code <file>:<line>:<column>}.
 */
public class MofException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MofException(String message) {
		super(message);
	}

	public MofException(String message, Throwable cause) {
		super(message, cause);
	}
}
