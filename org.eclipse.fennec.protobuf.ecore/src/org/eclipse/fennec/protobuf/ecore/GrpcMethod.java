/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf.ecore;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.service.api.ServiceOperation;

/**
 * A gRPC {@code method} extracted from a {@code FileDescriptorSet}, resolved against the
 * imported Ecore: the request/response message {@link EClass}es plus the streaming kind.
 * This is a decision-neutral descriptor — it can later be projected onto an Ecore
 * {@code EOperation} + invocation-delegate binding, or onto a DDSR {@code ServiceOperation}
 * + gRPC flavor; as {@link ServiceOperation} it is directly invokable through a
 * {@code GrpcServiceClient}. Either {@code EClass} may be {@code null} if the message type
 * was not part of the imported set (e.g. a well-known type without {@code --include_imports}).
 *
 * @see GrpcService
 * @see ProtobufImporter
 */
public record GrpcMethod(String name, String fullMethodName, EClass requestType, EClass responseType,
		boolean clientStreaming, boolean serverStreaming) implements ServiceOperation {

	/** The gRPC interaction pattern derived from the client/server streaming flags. */
	public enum StreamingKind {
		UNARY, SERVER_STREAMING, CLIENT_STREAMING, BIDI
	}

	/** The interaction pattern of this method. */
	public StreamingKind streaming() {
		if (clientStreaming && serverStreaming) {
			return StreamingKind.BIDI;
		}
		if (serverStreaming) {
			return StreamingKind.SERVER_STREAMING;
		}
		if (clientStreaming) {
			return StreamingKind.CLIENT_STREAMING;
		}
		return StreamingKind.UNARY;
	}
}
