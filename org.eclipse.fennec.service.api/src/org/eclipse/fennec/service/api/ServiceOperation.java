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

import org.eclipse.emf.ecore.EClass;

/**
 * A protocol-neutral view of a callable service operation: its name and the request/response
 * {@link EClass}es. Concrete importers expose richer, protocol-specific descriptors (e.g. a SOAP
 * operation with its message elements, a gRPC method with its full method name + streaming kind)
 * that implement this interface, so a {@link ServiceClient} can treat them uniformly.
 * <p>
 * Deliberately decision-neutral w.r.t. whether the canonical service model is an Ecore
 * {@code EOperation} or a DDSR {@code ServiceInterface} — this is only the client-facing view.
 */
public interface ServiceOperation {

	/** The operation name. */
	String name();

	/** The request message type, or {@code null} if the operation takes no (resolvable) request. */
	EClass requestType();

	/** The response message type, or {@code null} if the operation returns nothing (resolvable). */
	EClass responseType();
}
