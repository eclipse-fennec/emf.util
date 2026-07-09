/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap;

/**
 * Unchecked exception thrown when a WSDL/XSD cannot be mapped to Ecore, or when
 * SOAP envelope (de)serialization fails.
 */
public class SoapException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SoapException(String message) {
		super(message);
	}

	public SoapException(String message, Throwable cause) {
		super(message, cause);
	}
}
