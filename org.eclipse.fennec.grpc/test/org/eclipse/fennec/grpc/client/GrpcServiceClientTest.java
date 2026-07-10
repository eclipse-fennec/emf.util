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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.protobuf.ProtobufSchema;
import org.eclipse.fennec.protobuf.ecore.ProtobufImport;
import org.eclipse.fennec.protobuf.ecore.ProtobufImporter;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;
import com.google.protobuf.DescriptorProtos.ServiceDescriptorProto;

import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import io.grpc.Server;
import io.grpc.ServerServiceDefinition;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.ServerCalls;

/**
 * Proves the {@link org.eclipse.fennec.service.api.ServiceClient} design end-to-end for gRPC:
 * import a {@code FileDescriptorSet} (services + messages), invoke a unary method over a real
 * gRPC call path (in-process transport — no network, no generated stubs), get typed EObjects
 * back. The test server marshals with the same Fennec Protobuf runtime, so the wire bytes are
 * produced and consumed by two independent schema instances.
 */
@DisplayName("GrpcServiceClient: end-to-end unary invoke against an in-process server")
class GrpcServiceClientTest {

	private ProtobufImport model;
	private EPackage greetPackage;
	private Server server;
	private ManagedChannel channel;

	private static byte[] greeterDescriptorSet() {
		ServiceDescriptorProto greeter = ServiceDescriptorProto.newBuilder()
				.setName("Greeter")
				.addMethod(MethodDescriptorProto.newBuilder()
						.setName("Greet").setInputType(".greet.GreetRequest").setOutputType(".greet.GreetReply"))
				.addMethod(MethodDescriptorProto.newBuilder()
						.setName("Watch").setInputType(".greet.GreetRequest").setOutputType(".greet.GreetReply")
						.setServerStreaming(true))
				.build();

		FileDescriptorProto file = FileDescriptorProto.newBuilder()
				.setName("greet.proto").setSyntax("proto3").setPackage("greet")
				.addMessageType(message("GreetRequest", "name"))
				.addMessageType(message("GreetReply", "message"))
				.addService(greeter)
				.build();

		return FileDescriptorSet.newBuilder().addFile(file).build().toByteArray();
	}

	private static DescriptorProto message(String name, String field) {
		return DescriptorProto.newBuilder().setName(name)
				.addField(FieldDescriptorProto.newBuilder().setName(field).setNumber(1)
						.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_STRING))
				.build();
	}

	@BeforeEach
	void setUp() throws IOException {
		model = ProtobufImporter.importFrom(greeterDescriptorSet());
		greetPackage = model.packageForNsURI("http://greet");

		String name = InProcessServerBuilder.generateName();
		server = InProcessServerBuilder.forName(name).directExecutor()
				.addService(greeterService()).build().start();
		channel = InProcessChannelBuilder.forName(name).directExecutor().build();
	}

	@AfterEach
	void tearDown() {
		server.shutdownNow();
	}

	/**
	 * The test-side Greeter: answers {@code Greet} with "Hello <name>", NOT_FOUND for "ghost".
	 * Marshals through its own {@link ProtobufSchema} — independent of the client's instance.
	 */
	private ServerServiceDefinition greeterService() {
		ProtobufSchema schema = ProtobufSchema.forPackage(greetPackage);
		EClass requestType = (EClass) greetPackage.getEClassifier("GreetRequest");
		EClass replyType = (EClass) greetPackage.getEClassifier("GreetReply");

		MethodDescriptor<EObject, EObject> greet = MethodDescriptor.<EObject, EObject>newBuilder()
				.setType(MethodDescriptor.MethodType.UNARY)
				.setFullMethodName("greet.Greeter/Greet")
				.setRequestMarshaller(marshaller(schema, requestType))
				.setResponseMarshaller(marshaller(schema, replyType))
				.build();

		return ServerServiceDefinition.builder("greet.Greeter")
				.addMethod(greet, ServerCalls.asyncUnaryCall((request, observer) -> {
					String who = String.valueOf(request.eGet(requestType.getEStructuralFeature("name")));
					if ("ghost".equals(who)) {
						observer.onError(Status.NOT_FOUND.withDescription("no greeting for ghosts")
								.asRuntimeException());
						return;
					}
					EObject reply = EcoreUtil.create(replyType);
					reply.eSet(replyType.getEStructuralFeature("message"), "Hello " + who);
					observer.onNext(reply);
					observer.onCompleted();
				}))
				.build();
	}

	private static MethodDescriptor.Marshaller<EObject> marshaller(ProtobufSchema schema, EClass type) {
		return new MethodDescriptor.Marshaller<EObject>() {
			@Override
			public InputStream stream(EObject value) {
				return new ByteArrayInputStream(schema.writer().toBareBytes(value));
			}

			@Override
			public EObject parse(InputStream stream) {
				try {
					return schema.reader().readBare(stream, type);
				} catch (IOException e) {
					throw Status.INTERNAL.withCause(e).asRuntimeException();
				}
			}
		};
	}

	private EObject request(String name) {
		EClass requestType = (EClass) greetPackage.getEClassifier("GreetRequest");
		EObject request = EcoreUtil.create(requestType);
		request.eSet(requestType.getEStructuralFeature("name"), name);
		return request;
	}

	@Test
	@DisplayName("a unary call round-trips EObjects as bare Protobuf messages")
	void unaryCall() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
			EObject reply = client.invoke("Greet", request("Rex"));

			assertThat(reply.eClass().getName()).isEqualTo("GreetReply");
			assertThat(reply.eGet(reply.eClass().getEStructuralFeature("message"))).isEqualTo("Hello Rex");
		}
	}

	@Test
	@DisplayName("operations are discoverable; lookup works by simple and full method name")
	void operationLookup() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
			assertThat(client.operations()).hasSize(2);
			assertThat(client.operation("greet.Greeter/Greet")).isSameAs(client.operation("Greet"));
			assertThat(client.operation("nope")).isNull();

			EObject reply = client.invoke("greet.Greeter/Greet", request("Ada"));
			assertThat(reply.eGet(reply.eClass().getEStructuralFeature("message"))).isEqualTo("Hello Ada");
		}
	}

	@Test
	@DisplayName("a streaming method is rejected with a clear error (v1: unary only)")
	void streamingRejected() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
			assertThatThrownBy(() -> client.invoke("Watch", request("Rex")))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("SERVER_STREAMING")
					.hasMessageContaining("unary");
		}
	}

	@Test
	@DisplayName("a gRPC error status becomes a ServiceInvocationException with the status code")
	void statusBecomesException() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
			assertThatThrownBy(() -> client.invoke("Greet", request("ghost")))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("NOT_FOUND")
					.hasMessageContaining("no greeting for ghosts");
			assertThat(client.unwrap(ManagedChannel.class)).isPresent();
		}
	}
}
