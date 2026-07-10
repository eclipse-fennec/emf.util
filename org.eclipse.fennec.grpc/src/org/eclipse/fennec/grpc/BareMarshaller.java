/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.grpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.protobuf.ProtobufSchema;

import io.grpc.MethodDescriptor;
import io.grpc.Status;

/**
 * gRPC marshaller for {@link EObject}s: the wire format is the <b>bare</b> Protobuf message of
 * the given {@link EClass} (no type frame), produced and consumed by the Fennec Protobuf
 * runtime — the shared (de)serialization piece of the gRPC client and server.
 *
 * @see org.eclipse.fennec.grpc.client.GrpcServiceClient
 * @see org.eclipse.fennec.grpc.server.GrpcServiceServer
 */
public final class BareMarshaller implements MethodDescriptor.Marshaller<EObject> {

	private final ProtobufSchema schema;
	private final EClass type;

	public BareMarshaller(ProtobufSchema schema, EClass type) {
		this.schema = schema;
		this.type = type;
	}

	/** Convenience: a marshaller over a fresh schema of the {@link EClass}' package. */
	public static BareMarshaller of(EClass type) {
		return new BareMarshaller(ProtobufSchema.forPackage(type.getEPackage()), type);
	}

	@Override
	public InputStream stream(EObject value) {
		return new ByteArrayInputStream(schema.writer().toBareBytes(value));
	}

	@Override
	public EObject parse(InputStream stream) {
		try {
			return schema.reader().readBare(stream, type);
		} catch (IOException e) {
			throw Status.INTERNAL.withDescription("Could not parse a " + type.getName() + " message")
					.withCause(e).asRuntimeException();
		}
	}
}
