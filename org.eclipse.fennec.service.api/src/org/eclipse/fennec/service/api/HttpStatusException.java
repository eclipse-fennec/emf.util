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

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * An invocation answered by an HTTP-based transport with an error status ({@code >= 400}). Carries
 * the status, the response body and its content type, so a caller can map statuses to declared
 * errors (e.g. {@code 404} → {@code NotFound}) without parsing the message. Protocol faults that
 * travel <em>inside</em> a body (a SOAP {@code Fault}, a gRPC status) are a different matter and
 * get their own subclasses.
 */
public class HttpStatusException extends ServiceInvocationException {

	private static final long serialVersionUID = 1L;

	private final String operationName;
	private final int status;
	private final byte[] body;
	private final String contentType;

	/**
	 * @param message       the exception message
	 * @param operationName the invoked operation
	 * @param status        the HTTP status code
	 * @param body          the response body ({@code null} = empty)
	 * @param contentType   the response's {@code Content-Type}, or {@code null}
	 */
	public HttpStatusException(String message, String operationName, int status, byte[] body, String contentType) {
		super(message);
		this.operationName = operationName;
		this.status = status;
		this.body = body == null ? new byte[0] : body.clone();
		this.contentType = contentType;
	}

	/** The operation whose invocation failed. */
	public String operationName() {
		return operationName;
	}

	/** The HTTP status code ({@code >= 400}). */
	public int status() {
		return status;
	}

	/** A copy of the raw response body (empty if there was none). */
	public byte[] body() {
		return body.clone();
	}

	/** The response body decoded as UTF-8 (empty if there was none). */
	public String bodyText() {
		return new String(body, StandardCharsets.UTF_8);
	}

	/** The response's {@code Content-Type}, if it declared one. */
	public Optional<String> contentType() {
		return Optional.ofNullable(contentType);
	}
}
