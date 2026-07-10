/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.grpc.client;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.grpc.BareMarshaller;
import org.eclipse.fennec.protobuf.ProtobufSchema;
import org.eclipse.fennec.protobuf.ecore.GrpcMethod;
import org.eclipse.fennec.protobuf.ecore.GrpcService;
import org.eclipse.fennec.protobuf.ecore.ProtobufImport;
import org.eclipse.fennec.service.api.ServiceClient;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.eclipse.fennec.service.api.ServiceOperation;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ClientCalls;

/**
 * A {@link ServiceClient} that invokes gRPC methods over an {@code io.grpc} {@link Channel}.
 * The operations are the {@link GrpcMethod}s of a {@link ProtobufImport} (compiled
 * {@code FileDescriptorSet} → Ecore + services); request/response {@link EObject}s ride the
 * wire as <b>bare</b> Protobuf messages, (de)serialized by the Fennec Protobuf runtime against
 * the imported {@code EPackage}s — no generated stubs, no {@code DynamicMessage} on the caller
 * side.
 * <p>
 * The transport is whatever built the {@link Channel} (Netty, OkHttp, in-process). {@link #close()}
 * shuts the channel down when it is a {@link ManagedChannel}. v1: <b>unary</b> calls only —
 * streaming methods are rejected with a clear error; drop to the native channel via
 * {@link #unwrap(Class)} for streaming, deadlines per call, or interceptors.
 */
public final class GrpcServiceClient implements ServiceClient {

	private final Channel channel;
	private final ProtobufImport model;
	private final Map<EPackage, ProtobufSchema> schemas = new ConcurrentHashMap<>();
	private final Map<GrpcMethod, MethodDescriptor<EObject, EObject>> descriptors = new ConcurrentHashMap<>();
	private volatile Duration deadline;

	public GrpcServiceClient(Channel channel, ProtobufImport model) {
		this.channel = channel;
		this.model = model;
	}

	/** Sets a deadline applied to every subsequent call (relative, per invocation). */
	public GrpcServiceClient withDeadline(Duration deadline) {
		this.deadline = deadline;
		return this;
	}

	@Override
	public List<? extends ServiceOperation> operations() {
		List<GrpcMethod> operations = new ArrayList<>();
		for (GrpcService service : model.services()) {
			operations.addAll(service.methods());
		}
		return operations;
	}

	/**
	 * Looks the method up by its {@linkplain GrpcMethod#fullMethodName() full method name}
	 * ({@code pkg.Service/Method}) or its simple name — on a simple-name collision across
	 * services, the first service wins; use the full name to disambiguate.
	 */
	@Override
	public ServiceOperation operation(String name) {
		for (GrpcService service : model.services()) {
			for (GrpcMethod method : service.methods()) {
				if (method.fullMethodName().equals(name) || method.name().equals(name)) {
					return method;
				}
			}
		}
		return null;
	}

	@Override
	public EObject invoke(ServiceOperation operation, EObject request) {
		if (!(operation instanceof GrpcMethod method)) {
			throw new ServiceInvocationException("Not a gRPC method: " + operation);
		}
		if (method.streaming() != GrpcMethod.StreamingKind.UNARY) {
			throw new ServiceInvocationException("Method '" + method.fullMethodName() + "' is "
					+ method.streaming() + " — v1 supports unary calls only"
					+ " (unwrap(Channel.class) for streaming)");
		}
		if (method.requestType() == null || method.responseType() == null) {
			throw new ServiceInvocationException("Method '" + method.fullMethodName()
					+ "' has an unresolved message type — compile the descriptor set with --include_imports");
		}
		MethodDescriptor<EObject, EObject> descriptor = descriptors.computeIfAbsent(method, this::descriptor);
		try {
			return ClientCalls.blockingUnaryCall(channel, descriptor, callOptions(), request);
		} catch (StatusRuntimeException e) {
			throw new ServiceInvocationException("Method '" + method.fullMethodName() + "' failed: "
					+ e.getStatus().getCode()
					+ (e.getStatus().getDescription() == null ? "" : " — " + e.getStatus().getDescription()), e);
		}
	}

	private CallOptions callOptions() {
		Duration current = deadline;
		return current == null ? CallOptions.DEFAULT
				: CallOptions.DEFAULT.withDeadlineAfter(current.toNanos(), TimeUnit.NANOSECONDS);
	}

	private MethodDescriptor<EObject, EObject> descriptor(GrpcMethod method) {
		return MethodDescriptor.<EObject, EObject>newBuilder()
				.setType(MethodDescriptor.MethodType.UNARY)
				.setFullMethodName(method.fullMethodName())
				.setRequestMarshaller(marshaller(method.requestType()))
				.setResponseMarshaller(marshaller(method.responseType()))
				.build();
	}

	private MethodDescriptor.Marshaller<EObject> marshaller(EClass type) {
		ProtobufSchema schema = schemas.computeIfAbsent(type.getEPackage(), ProtobufSchema::forPackage);
		return new BareMarshaller(schema, type);
	}

	@Override
	public <T> Optional<T> unwrap(Class<T> nativeType) {
		return nativeType.isInstance(channel) ? Optional.of(nativeType.cast(channel)) : Optional.empty();
	}

	@Override
	public void close() {
		if (channel instanceof ManagedChannel managed) {
			managed.shutdown();
			try {
				managed.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
