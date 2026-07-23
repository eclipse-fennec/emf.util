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

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

/**
 * A protocol-agnostic client for invoking modelled service operations. Implementations bind a
 * concrete transport (SOAP over HTTP, gRPC, REST/OData …) but present the same face: pick an
 * {@link ServiceOperation}, hand over a request {@link EObject}, get a response {@link EObject}.
 * Marshalling is delegated to the format's (de)serialization layer (SOAP envelope, Protobuf
 * message, JSON codec); this API only unifies discovery + invocation.
 * <p>
 * The (typically reflective) engine underneath — e.g. an EMF {@code InvocationDelegate} or a DDSR
 * flavor — is an implementation detail behind this facade. Use {@link #unwrap(Class)} to drop to
 * the native client for protocol-specific features (streaming, OData query options, gRPC deadlines).
 */
public interface ServiceClient extends AutoCloseable {

	/**
	 * Recommended OSGi service property under which configuration-driven client components advertise
	 * a stable, human-chosen name for the published client. Consumers use it to select a client by
	 * LDAP filter or to derive stable identifiers from it (e.g. an MCP tool bridge deriving
	 * {@code <clientName>_<operationName>} tool names). Purely a convention — plain-Java use of this
	 * API does not involve it.
	 */
	String PROP_NAME = "name";

	/** The operations this client can invoke. */
	List<? extends ServiceOperation> operations();

	/** The operation with the given {@link ServiceOperation#name() name}, or {@code null}. */
	ServiceOperation operation(String name);

	/**
	 * Invokes {@code operation} with {@code request} (an instance of {@link ServiceOperation#requestType()})
	 * and returns the response as an {@link EObject} (an instance of {@link ServiceOperation#responseType()}),
	 * or {@code null} for a void operation.
	 *
	 * @throws ServiceInvocationException on transport error, protocol fault, or (de)serialization failure
	 */
	EObject invoke(ServiceOperation operation, EObject request);

	/** Convenience: invoke by operation name. */
	default EObject invoke(String operationName, EObject request) {
		ServiceOperation operation = operation(operationName);
		if (operation == null) {
			throw new ServiceInvocationException("No such operation: " + operationName);
		}
		return invoke(operation, request);
	}

	/** Adapts to the underlying native client (e.g. {@code HttpClient}, gRPC {@code ManagedChannel}) for pro-mode use. */
	<T> Optional<T> unwrap(Class<T> nativeType);

	@Override
	void close();
}
