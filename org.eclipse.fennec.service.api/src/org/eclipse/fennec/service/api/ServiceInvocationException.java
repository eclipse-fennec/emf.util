/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.service.api;

/**
 * Thrown when a {@link ServiceClient} invocation fails — transport error, protocol fault
 * (SOAP {@code Fault}, gRPC status, HTTP error), or (de)serialization failure. The cause carries
 * the protocol-specific detail.
 */
public class ServiceInvocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceInvocationException(String message) {
		super(message);
	}

	public ServiceInvocationException(String message, Throwable cause) {
		super(message, cause);
	}
}
