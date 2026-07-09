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

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.protobuf.ecore.GrpcMethod.StreamingKind;
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

@DisplayName("ProtobufImporter: gRPC service extraction (decision-neutral)")
class GrpcServiceExtractionTest {

	private static DescriptorProto message(String name, String field) {
		return DescriptorProto.newBuilder().setName(name)
				.addField(FieldDescriptorProto.newBuilder().setName(field).setNumber(1)
						.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_STRING))
				.build();
	}

	private static byte[] storeDescriptorSet() {
		ServiceDescriptorProto store = ServiceDescriptorProto.newBuilder()
				.setName("Store")
				.addMethod(MethodDescriptorProto.newBuilder()
						.setName("Get").setInputType(".rpc.GetReq").setOutputType(".rpc.GetResp"))
				.addMethod(MethodDescriptorProto.newBuilder()
						.setName("Watch").setInputType(".rpc.GetReq").setOutputType(".rpc.GetResp")
						.setServerStreaming(true))
				.build();

		FileDescriptorProto file = FileDescriptorProto.newBuilder()
				.setName("rpc.proto").setSyntax("proto3").setPackage("rpc")
				.addMessageType(message("GetReq", "id"))
				.addMessageType(message("GetResp", "value"))
				.addService(store)
				.build();

		return FileDescriptorSet.newBuilder().addFile(file).build().toByteArray();
	}

	@Test
	@DisplayName("extracts services with request/response EClasses and streaming kind")
	void extractsServices() {
		ProtobufImport imp = ProtobufImporter.importFrom(storeDescriptorSet());

		// data types still import as before
		assertThat(imp.packageForNsURI("http://rpc")).isNotNull();

		GrpcService store = imp.service("Store");
		assertThat(store).isNotNull();
		assertThat(store.fullName()).isEqualTo("rpc.Store");
		assertThat(store.methods()).extracting(GrpcMethod::name).containsExactly("Get", "Watch");

		GrpcMethod get = store.method("Get");
		assertThat(get.fullMethodName()).isEqualTo("rpc.Store/Get");
		assertThat(get.streaming()).isEqualTo(StreamingKind.UNARY);
		EClass req = get.requestType();
		EClass resp = get.responseType();
		assertThat(req).isNotNull();
		assertThat(req.getName()).isEqualTo("GetReq");
		assertThat(req.getEStructuralFeature("id")).isNotNull();
		assertThat(resp.getName()).isEqualTo("GetResp");

		assertThat(store.method("Watch").streaming()).isEqualTo(StreamingKind.SERVER_STREAMING);
	}

	@Test
	@DisplayName("the packages-only API is unchanged (no services required)")
	void packagesOnlyStillWorks() {
		assertThat(ProtobufImporter.fromDescriptorSet(storeDescriptorSet()))
				.anyMatch(p -> "http://rpc".equals(p.getNsURI()));
	}
}
