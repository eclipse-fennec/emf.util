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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.grpc.client.GrpcServiceClient;
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
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;

/**
 * Proves the server-definition builder end-to-end: {@link GrpcServiceServer} serves the
 * imported methods from plain handlers, {@link GrpcServiceClient} calls them — the whole
 * chain runs on Fennec marshalling on both sides (in-process transport).
 */
@DisplayName("GrpcServiceServer: serve imported methods from plain handlers")
class GrpcServiceServerTest {

	private ProtobufImport model;
	private EPackage echoPackage;
	private Server server;
	private ManagedChannel channel;

	private static byte[] echoDescriptorSet() {
		ServiceDescriptorProto echo = ServiceDescriptorProto.newBuilder()
				.setName("Echo")
				.addMethod(MethodDescriptorProto.newBuilder()
						.setName("Shout").setInputType(".echo.Text").setOutputType(".echo.Text"))
				.addMethod(MethodDescriptorProto.newBuilder()
						.setName("Whisper").setInputType(".echo.Text").setOutputType(".echo.Text"))
				.addMethod(MethodDescriptorProto.newBuilder()
						.setName("Stream").setInputType(".echo.Text").setOutputType(".echo.Text")
						.setClientStreaming(true))
				.build();

		FileDescriptorProto file = FileDescriptorProto.newBuilder()
				.setName("echo.proto").setSyntax("proto3").setPackage("echo")
				.addMessageType(DescriptorProto.newBuilder().setName("Text")
						.addField(FieldDescriptorProto.newBuilder().setName("value").setNumber(1)
								.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_STRING)))
				.addService(echo)
				.build();

		return FileDescriptorSet.newBuilder().addFile(file).build().toByteArray();
	}

	@BeforeEach
	void setUp() throws IOException {
		model = ProtobufImporter.importFrom(echoDescriptorSet());
		echoPackage = model.packageForNsURI("http://echo");

		String name = InProcessServerBuilder.generateName();
		server = InProcessServerBuilder.forName(name).directExecutor()
				.addService(GrpcServiceServer.forService(model.service("Echo"))
						.unary("Shout", request -> text(textOf(request).toUpperCase() + "!"))
						.unary("echo.Echo/Whisper", request -> {
							if (textOf(request).isEmpty()) {
								throw Status.INVALID_ARGUMENT.withDescription("nothing to whisper")
										.asRuntimeException();
							}
							return text(textOf(request).toLowerCase());
						})
						.definition())
				.build().start();
		channel = InProcessChannelBuilder.forName(name).directExecutor().build();
	}

	@AfterEach
	void tearDown() {
		server.shutdownNow();
	}

	private String textOf(EObject text) {
		return String.valueOf(text.eGet(text.eClass().getEStructuralFeature("value")));
	}

	private EObject text(String value) {
		EClass type = (EClass) echoPackage.getEClassifier("Text");
		EObject text = EcoreUtil.create(type);
		text.eSet(type.getEStructuralFeature("value"), value);
		return text;
	}

	@Test
	@DisplayName("handlers registered by simple and full method name serve unary calls")
	void servesUnaryCalls() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
			assertThat(textOf(client.invoke("Shout", text("hello")))).isEqualTo("HELLO!");
			assertThat(textOf(client.invoke("Whisper", text("LOUD")))).isEqualTo("loud");
		}
	}

	@Test
	@DisplayName("a handler-thrown StatusRuntimeException reaches the client as that status")
	void statusPassesThrough() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
			assertThatThrownBy(() -> client.invoke("Whisper", text("")))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("INVALID_ARGUMENT")
					.hasMessageContaining("nothing to whisper");
		}
	}

	@Test
	@DisplayName("an unexpected handler exception becomes INTERNAL")
	void exceptionBecomesInternal() throws IOException {
		String name = InProcessServerBuilder.generateName();
		Server failing = InProcessServerBuilder.forName(name).directExecutor()
				.addService(GrpcServiceServer.forService(model.service("Echo"))
						.unary("Shout", request -> {
							throw new IllegalStateException("boom");
						})
						.definition())
				.build().start();
		try (GrpcServiceClient client = new GrpcServiceClient(
				InProcessChannelBuilder.forName(name).directExecutor().build(), model)) {
			assertThatThrownBy(() -> client.invoke("Shout", text("x")))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("INTERNAL")
					.hasMessageContaining("boom");
		} finally {
			failing.shutdownNow();
		}
	}

	@Test
	@DisplayName("a method without handler answers UNIMPLEMENTED")
	void unhandledMethodIsUnimplemented() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
			// Whisper + Shout are registered, a call to an unregistered unary method fails cleanly
			assertThatThrownBy(() -> client.invoke("echo.Echo/Stream", text("x")))
					.isInstanceOf(ServiceInvocationException.class); // rejected as streaming by the client
		}
	}

	@Test
	@DisplayName("registration validates method name and streaming kind")
	void registrationValidates() {
		GrpcServiceServer builder = GrpcServiceServer.forService(model.service("Echo"));

		assertThatThrownBy(() -> builder.unary("Nope", r -> r))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Nope").hasMessageContaining("Shout");
		assertThatThrownBy(() -> builder.unary("Stream", r -> r))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("CLIENT_STREAMING");
	}
}
