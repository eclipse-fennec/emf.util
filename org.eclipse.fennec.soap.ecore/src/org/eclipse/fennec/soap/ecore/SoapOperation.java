/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap.ecore;

import javax.xml.namespace.QName;

import org.eclipse.emf.ecore.EClass;

/**
 * A WSDL {@code portType} operation, resolved against the imported Ecore: the request
 * and response message elements (as XML {@link QName}s) and the {@link EClass}es they map
 * to. Either {@code EClass} may be {@code null} if the element could not be resolved (e.g.
 * an RPC-style part referencing a type rather than a global element — out of scope for v1).
 *
 * @see WsdlImporter
 */
public final class SoapOperation {

	private final String name;
	private final QName requestElement;
	private final QName responseElement;
	private final EClass requestType;
	private final EClass responseType;

	SoapOperation(String name, QName requestElement, QName responseElement, EClass requestType, EClass responseType) {
		this.name = name;
		this.requestElement = requestElement;
		this.responseElement = responseElement;
		this.requestType = requestType;
		this.responseType = responseType;
	}

	/** The operation name (from {@code <portType><operation name="…">}). */
	public String name() {
		return name;
	}

	/** The request message's global-element {@link QName}, or {@code null} if the operation has no input. */
	public QName requestElement() {
		return requestElement;
	}

	/** The response message's global-element {@link QName}, or {@code null} if the operation has no output. */
	public QName responseElement() {
		return responseElement;
	}

	/** The {@link EClass} the request element maps to, or {@code null} if unresolved. */
	public EClass requestType() {
		return requestType;
	}

	/** The {@link EClass} the response element maps to, or {@code null} if unresolved. */
	public EClass responseType() {
		return responseType;
	}

	@Override
	public String toString() {
		return "SoapOperation[" + name + " " + requestElement + " -> " + responseElement + "]";
	}
}
