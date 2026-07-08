/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf;

/**
 * Unchecked exception thrown when an EMF model cannot be mapped to Protocol
 * Buffers, or when (de)serialization fails.
 */
public class ProtobufException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProtobufException(String message) {
		super(message);
	}

	public ProtobufException(String message, Throwable cause) {
		super(message, cause);
	}
}
