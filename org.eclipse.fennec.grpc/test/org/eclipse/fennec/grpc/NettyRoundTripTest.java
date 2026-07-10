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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.grpc.client.GrpcServiceClient;
import org.eclipse.fennec.grpc.server.GrpcServiceServer;
import org.eclipse.fennec.protobuf.ecore.ProtobufImport;
import org.eclipse.fennec.protobuf.ecore.ProtobufImporter;
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
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;

/**
 * The Netty smoke test: the same server definition and client that the in-process tests use,
 * but over a real HTTP/2 connection on localhost — proving the recommended deployment
 * transport ({@code grpc-netty} + the official netty bundles) end-to-end.
 */
@DisplayName("gRPC over Netty: real HTTP/2 round-trip on localhost")
class NettyRoundTripTest {

	private ProtobufImport model;
	private EPackage pingPackage;
	private Server server;
	private ManagedChannel channel;

	private static byte[] pingDescriptorSet() {
		FileDescriptorProto file = FileDescriptorProto.newBuilder()
				.setName("ping.proto").setSyntax("proto3").setPackage("ping")
				.addMessageType(DescriptorProto.newBuilder().setName("Ping")
						.addField(FieldDescriptorProto.newBuilder().setName("payload").setNumber(1)
								.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_STRING)))
				.addService(ServiceDescriptorProto.newBuilder().setName("Pinger")
						.addMethod(MethodDescriptorProto.newBuilder()
								.setName("Ping").setInputType(".ping.Ping").setOutputType(".ping.Ping")))
				.build();
		return FileDescriptorSet.newBuilder().addFile(file).build().toByteArray();
	}

	@BeforeEach
	void setUp() throws IOException {
		model = ProtobufImporter.importFrom(pingDescriptorSet());
		pingPackage = model.packageForNsURI("http://ping");

		server = NettyServerBuilder.forPort(0)
				.addService(GrpcServiceServer.forService(model.service("Pinger"))
						.unary("Ping", request -> ping("pong: " + payloadOf(request)))
						.definition())
				.build().start();
		channel = NettyChannelBuilder.forAddress("localhost", server.getPort())
				.usePlaintext().build();
	}

	@AfterEach
	void tearDown() {
		server.shutdownNow();
	}

	private String payloadOf(EObject ping) {
		return String.valueOf(ping.eGet(ping.eClass().getEStructuralFeature("payload")));
	}

	private EObject ping(String payload) {
		EClass type = (EClass) pingPackage.getEClassifier("Ping");
		EObject ping = EcoreUtil.create(type);
		ping.eSet(type.getEStructuralFeature("payload"), payload);
		return ping;
	}

	@Test
	@DisplayName("unary call over a real Netty HTTP/2 connection")
	void nettyRoundTrip() {
		try (GrpcServiceClient client = new GrpcServiceClient(channel, model)
				.withDeadline(Duration.ofSeconds(10))) {
			EObject reply = client.invoke("ping.Pinger/Ping", ping("hello"));

			assertThat(payloadOf(reply)).isEqualTo("pong: hello");
		}
	}
}
