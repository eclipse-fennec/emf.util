/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.grpc.server;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.grpc.BareMarshaller;
import org.eclipse.fennec.protobuf.ProtobufSchema;
import org.eclipse.fennec.protobuf.ecore.GrpcMethod;
import org.eclipse.fennec.protobuf.ecore.GrpcService;

import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.ServerCalls;

/**
 * Builds an {@code io.grpc} {@link ServerServiceDefinition} for an imported {@link GrpcService}
 * from plain {@link UnaryHandler}s — the server-side counterpart of the
 * {@link org.eclipse.fennec.grpc.client.GrpcServiceClient}: request/response {@link EObject}s
 * ride the wire as bare Protobuf messages via the Fennec Protobuf runtime, no {@code protoc},
 * no generated base classes.
 * <p>
 * The definition is <b>transport-agnostic</b> — add it to any {@code io.grpc} server builder
 * (Netty, in-process, servlet):
 *
 * <pre>
 * ServerServiceDefinition greeter = GrpcServiceServer.forService(model.service("Greeter"))
 *         .unary("Greet", request -&gt; makeReply(request))
 *         .definition();
 * Server server = NettyServerBuilder.forPort(50051).addService(greeter).build().start();
 * </pre>
 *
 * Methods without a registered handler are answered by gRPC with {@code UNIMPLEMENTED}. A
 * handler may throw a {@link StatusRuntimeException} to control the wire status; any other
 * exception becomes {@code INTERNAL} with the exception message. v1: unary methods only.
 */
public final class GrpcServiceServer {

	/** Serves one unary method: turn the request {@link EObject} into the response {@link EObject}. */
	@FunctionalInterface
	public interface UnaryHandler {
		EObject handle(EObject request) throws Exception;
	}

	private final GrpcService service;
	private final Map<GrpcMethod, UnaryHandler> handlers = new LinkedHashMap<>();
	private final Map<EPackage, ProtobufSchema> schemas = new ConcurrentHashMap<>();

	private GrpcServiceServer(GrpcService service) {
		this.service = service;
	}

	/** Starts a definition for the imported service. */
	public static GrpcServiceServer forService(GrpcService service) {
		if (service == null) {
			throw new IllegalArgumentException("service must not be null");
		}
		return new GrpcServiceServer(service);
	}

	/** Registers the handler for a unary method (simple or full method name). */
	public GrpcServiceServer unary(String methodName, UnaryHandler handler) {
		GrpcMethod method = method(methodName);
		if (method == null) {
			throw new IllegalArgumentException("Service '" + service.fullName() + "' has no method '"
					+ methodName + "' — declared: " + service.methods().stream().map(GrpcMethod::name).toList());
		}
		if (method.streaming() != GrpcMethod.StreamingKind.UNARY) {
			throw new IllegalArgumentException("Method '" + method.fullMethodName() + "' is "
					+ method.streaming() + " — v1 serves unary methods only");
		}
		if (method.requestType() == null || method.responseType() == null) {
			throw new IllegalArgumentException("Method '" + method.fullMethodName()
					+ "' has an unresolved message type — compile the descriptor set with --include_imports");
		}
		handlers.put(method, handler);
		return this;
	}

	/** The transport-agnostic service definition with all registered handlers. */
	public ServerServiceDefinition definition() {
		ServerServiceDefinition.Builder builder = ServerServiceDefinition.builder(service.fullName());
		for (Map.Entry<GrpcMethod, UnaryHandler> entry : handlers.entrySet()) {
			builder.addMethod(descriptor(entry.getKey()), ServerCalls.asyncUnaryCall(call(entry.getValue())));
		}
		return builder.build();
	}

	private ServerCalls.UnaryMethod<EObject, EObject> call(UnaryHandler handler) {
		return (request, observer) -> {
			try {
				observer.onNext(handler.handle(request));
				observer.onCompleted();
			} catch (StatusRuntimeException e) {
				observer.onError(e);
			} catch (Exception e) {
				observer.onError(Status.INTERNAL.withDescription(String.valueOf(e.getMessage()))
						.withCause(e).asRuntimeException());
			}
		};
	}

	private MethodDescriptor<EObject, EObject> descriptor(GrpcMethod method) {
		return MethodDescriptor.<EObject, EObject>newBuilder()
				.setType(MethodDescriptor.MethodType.UNARY)
				.setFullMethodName(method.fullMethodName())
				.setRequestMarshaller(marshaller(method.requestType()))
				.setResponseMarshaller(marshaller(method.responseType()))
				.build();
	}

	private BareMarshaller marshaller(EClass type) {
		return new BareMarshaller(schemas.computeIfAbsent(type.getEPackage(), ProtobufSchema::forPackage), type);
	}

	private GrpcMethod method(String name) {
		for (GrpcMethod method : service.methods()) {
			if (method.fullMethodName().equals(name) || method.name().equals(name)) {
				return method;
			}
		}
		return null;
	}
}
