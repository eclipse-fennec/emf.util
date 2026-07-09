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

import java.util.List;

/**
 * A gRPC {@code service} extracted from a {@code FileDescriptorSet}: its (fully qualified)
 * name and its {@link GrpcMethod}s. Decision-neutral raw material for both the
 * {@code EOperation}/invocation-delegate path and the DDSR {@code ServiceInterface}/flavor path.
 *
 * @see ProtobufImporter
 */
public record GrpcService(String name, String fullName, List<GrpcMethod> methods) {

	public GrpcService {
		methods = List.copyOf(methods);
	}

	/** The method with the given simple name, or {@code null}. */
	public GrpcMethod method(String name) {
		for (GrpcMethod m : methods) {
			if (m.name().equals(name)) {
				return m;
			}
		}
		return null;
	}
}
