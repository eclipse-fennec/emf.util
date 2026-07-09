/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.protobuf.ecore.ProtobufImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.protobuf.Api;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Duration;
import com.google.protobuf.Struct;
import com.google.protobuf.Timestamp;

/**
 * Imports the Google "well-known types" — real, standardized schemas shipped inside
 * protobuf-java — to exercise the importer against complex, representative descriptors:
 * maps, {@code oneof}, message recursion, multi-file dependency graphs and enums. The
 * runtime {@link FileDescriptor}s are serialized to a {@link FileDescriptorSet} exactly as
 * {@code protoc --include_imports --descriptor_set_out} would, so no {@code protoc} or
 * network access is needed.
 */
@DisplayName("ProtobufImporter against Google well-known types")
class WellKnownTypesImportTest {

	// --- helpers ---------------------------------------------------------------------------

	/** Serializes descriptors + all transitive dependencies into a FileDescriptorSet (like --include_imports). */
	private static byte[] descriptorSet(FileDescriptor... roots) {
		Map<String, FileDescriptorProto> files = new LinkedHashMap<>();
		for (FileDescriptor root : roots) {
			collect(root, files);
		}
		FileDescriptorSet.Builder set = FileDescriptorSet.newBuilder();
		files.values().forEach(set::addFile);
		return set.build().toByteArray();
	}

	private static void collect(FileDescriptor file, Map<String, FileDescriptorProto> out) {
		if (out.containsKey(file.getName())) {
			return;
		}
		for (FileDescriptor dependency : file.getDependencies()) {
			collect(dependency, out);
		}
		out.put(file.getName(), file.toProto());
	}

	private static EPackage protobufPackage(List<EPackage> packages) {
		return packages.stream().filter(p -> "http://google.protobuf".equals(p.getNsURI())).findFirst()
				.orElseThrow(() -> new AssertionError("no google.protobuf package in " + packages));
	}

	private static EClass eClass(EPackage pkg, String name) {
		assertThat(pkg.getEClassifier(name)).as("EClass '%s'", name).isInstanceOf(EClass.class);
		return (EClass) pkg.getEClassifier(name);
	}

	private static EStructuralFeature feature(EClass eClass, String name) {
		EStructuralFeature f = eClass.getEStructuralFeature(name);
		assertThat(f).as("feature '%s' on %s", name, eClass.getName()).isNotNull();
		return f;
	}

	// --- tests -----------------------------------------------------------------------------

	@Test
	@DisplayName("struct.proto: map, recursion, oneof presence and the NullValue enum")
	void structProto() {
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(
				descriptorSet(Struct.getDescriptor().getFile()));
		EPackage pkg = protobufPackage(packages);
		assertThat(pkg.getName()).isEqualTo("protobuf");

		// Struct.fields: map<string, Value> -> containment (many) ref to the synthetic entry EClass.
		EClass struct = eClass(pkg, "Struct");
		EReference fields = (EReference) feature(struct, "fields");
		assertThat(fields.isContainment()).isTrue();
		assertThat(fields.isMany()).isTrue();
		EClass entry = (EClass) fields.getEType();
		assertThat(entry.getName()).isEqualTo("Struct_FieldsEntry");
		assertThat(((EAttribute) feature(entry, "key")).getEType().getName()).isEqualTo("EString");
		assertThat(((EReference) feature(entry, "value")).getEType()).isSameAs(eClass(pkg, "Value"));

		// Value: a oneof — members flatten to individual fields, all with presence -> unsettable.
		EClass value = eClass(pkg, "Value");
		EAttribute numberValue = (EAttribute) feature(value, "number_value");
		assertThat(numberValue.getEType().getName()).isEqualTo("EDouble");
		assertThat(numberValue.isUnsettable()).isTrue();
		assertThat(((EReference) feature(value, "struct_value")).getEType()).isSameAs(struct); // recursion resolves
		assertThat(((EReference) feature(value, "list_value")).getEType()).isSameAs(eClass(pkg, "ListValue"));
		assertThat(((EAttribute) feature(value, "null_value")).getEType()).isInstanceOf(EEnum.class);

		// ListValue.values: repeated Value -> many containment ref back to Value.
		EReference values = (EReference) feature(eClass(pkg, "ListValue"), "values");
		assertThat(values.isMany()).isTrue();
		assertThat(values.getEType()).isSameAs(value);

		EEnum nullValue = (EEnum) pkg.getEClassifier("NullValue");
		assertThat(nullValue.getELiterals()).extracting(EEnumLiteral::getName).contains("NULL_VALUE");
	}

	@Test
	@DisplayName("api.proto: a multi-file dependency graph aggregated into one EPackage")
	void apiProtoDependencyGraph() {
		// api.proto -> type.proto -> {any.proto, source_context.proto}; all in package google.protobuf.
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(
				descriptorSet(Api.getDescriptor().getFile()));
		EPackage pkg = protobufPackage(packages);

		// Types from every file in the graph land in the single google.protobuf package.
		assertThat(pkg.getEClassifier("Api")).isNotNull();          // api.proto
		assertThat(pkg.getEClassifier("Method")).isNotNull();       // api.proto
		assertThat(pkg.getEClassifier("Type")).isNotNull();         // type.proto
		assertThat(pkg.getEClassifier("Field")).isNotNull();        // type.proto
		assertThat(pkg.getEClassifier("SourceContext")).isNotNull();// source_context.proto
		assertThat(pkg.getEClassifier("Any")).isNotNull();          // any.proto

		EClass api = eClass(pkg, "Api");
		EReference methods = (EReference) feature(api, "methods");
		assertThat(methods.isMany()).isTrue();
		assertThat(methods.getEType()).isSameAs(eClass(pkg, "Method"));
		// Cross-file reference within the same package resolves to the shared EClass.
		assertThat(((EReference) feature(api, "source_context")).getEType()).isSameAs(eClass(pkg, "SourceContext"));

		// google.protobuf.Any: type_url (string) + value (bytes).
		EClass any = eClass(pkg, "Any");
		assertThat(((EAttribute) feature(any, "type_url")).getEType().getName()).isEqualTo("EString");
		assertThat(((EAttribute) feature(any, "value")).getEType().getName()).isEqualTo("EByteArray");
	}

	@Test
	@DisplayName("descriptor.proto: the self-describing schema imports intact (nested enums, field numbers)")
	void descriptorProto() {
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(
				descriptorSet(FileDescriptorProto.getDescriptor().getFile()));
		EPackage pkg = protobufPackage(packages);

		assertThat(pkg.getEClassifier("FileDescriptorProto")).isInstanceOf(EClass.class);
		assertThat(pkg.getEClassifier("DescriptorProto")).isInstanceOf(EClass.class);
		assertThat(pkg.getEClassifier("FieldDescriptorProto")).isInstanceOf(EClass.class);
		// A nested enum flattens to <Message>_<Enum>.
		assertThat(pkg.getEClassifier("FieldDescriptorProto_Type")).isInstanceOf(EEnum.class);

		// FileDescriptorProto.name is field #1 — the number is preserved as an annotation.
		EClass fileProto = eClass(pkg, "FileDescriptorProto");
		assertThat(ProtobufAnnotations.explicitFieldNumber(feature(fileProto, "name"))).isEqualTo(1);
		// repeated DescriptorProto message_type -> many containment ref to DescriptorProto.
		EReference messageType = (EReference) feature(fileProto, "message_type");
		assertThat(messageType.isMany()).isTrue();
		assertThat(messageType.getEType()).isSameAs(eClass(pkg, "DescriptorProto"));
	}

	@Test
	@DisplayName("timestamp.proto + duration.proto: int64/int32 scalar mapping")
	void timestampAndDuration() {
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(
				descriptorSet(Timestamp.getDescriptor().getFile(), Duration.getDescriptor().getFile()));
		EPackage pkg = protobufPackage(packages);

		EClass timestamp = eClass(pkg, "Timestamp");
		assertThat(((EAttribute) feature(timestamp, "seconds")).getEType().getName()).isEqualTo("ELong");
		assertThat(((EAttribute) feature(timestamp, "nanos")).getEType().getName()).isEqualTo("EInt");
		assertThat(((EAttribute) feature(eClass(pkg, "Duration"), "seconds")).getEType().getName())
				.isEqualTo("ELong");
	}
}
