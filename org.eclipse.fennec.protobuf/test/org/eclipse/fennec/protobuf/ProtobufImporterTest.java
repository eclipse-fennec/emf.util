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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.protobuf.ecore.ProtobufImporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.MessageOptions;
import com.google.protobuf.DescriptorProtos.OneofDescriptorProto;

@DisplayName("ProtobufImporter: FileDescriptorSet -> Ecore (structural)")
class ProtobufImporterTest {

	private static EStructuralFeature feature(EClass eClass, String name) {
		EStructuralFeature f = eClass.getEStructuralFeature(name);
		assertThat(f).as("feature '%s' on %s", name, eClass.getName()).isNotNull();
		return f;
	}

	private static EClass eClass(EPackage pkg, String name) {
		assertThat(pkg.getEClassifier(name)).as("EClass '%s'", name).isInstanceOf(EClass.class);
		return (EClass) pkg.getEClassifier(name);
	}

	@Test
	@DisplayName("round-trips our own descriptors back to an equivalent (flattened) Ecore model")
	void roundTripFromOurDescriptors() {
		ShopModel model = new ShopModel();
		FileDescriptorProto proto = ProtobufSchema.forPackage(model.pkg).fileProto();
		byte[] set = FileDescriptorSet.newBuilder().addFile(proto).build().toByteArray();

		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(set);

		assertThat(packages).hasSize(1);
		EPackage pkg = packages.get(0);
		assertThat(pkg.getName()).isEqualTo("shop");
		assertThat(pkg.getNsURI()).isEqualTo("http://shop"); // default: "http://" + proto package

		EClass category = eClass(pkg, "Category");
		EAttribute name = (EAttribute) feature(category, "name");
		assertThat(name.getEType().getName()).isEqualTo("EString");
		assertThat(ProtobufAnnotations.explicitFieldNumber(name)).isEqualTo(1); // field numbers preserved

		EReference products = (EReference) feature(category, "products");
		assertThat(products.isContainment()).isTrue();
		assertThat(products.isMany()).isTrue();
		assertThat(products.getEType()).isSameAs(eClass(pkg, "Product"));
		assertThat(ProtobufAnnotations.explicitFieldNumber(products)).isEqualTo(2);

		EClass product = eClass(pkg, "Product");
		assertThat(((EAttribute) feature(product, "price")).getEType().getName()).isEqualTo("EDouble");
		// unsettable proto3 optional scalar round-trips as an unsettable attribute.
		EAttribute count = (EAttribute) feature(product, "count");
		assertThat(count.getEType().getName()).isEqualTo("EInt");
		assertThat(count.isUnsettable()).isTrue();
		assertThat(((EAttribute) feature(product, "code")).getEType().getName()).isEqualTo("EByteArray");
		assertThat(((EAttribute) feature(product, "tags")).isMany()).isTrue();

		EEnum status = (EEnum) pkg.getEClassifier("Status");
		assertThat(status.getELiterals()).extracting(l -> l.getName()).contains("ACTIVE", "DISCONTINUED");
	}

	@Test
	@DisplayName("documents the structural losses: BigDecimal->string, non-containment ref->string")
	void structuralLosses() {
		ShopModel model = new ShopModel();
		byte[] set = FileDescriptorSet.newBuilder()
				.addFile(ProtobufSchema.forPackage(model.pkg).fileProto()).build().toByteArray();

		EPackage pkg = ProtobufImporter.fromDescriptorSet(set).get(0);
		EClass product = eClass(pkg, "Product");

		// EBigDecimal was exported as a string -> comes back as EString (cannot be widened).
		assertThat(((EAttribute) feature(product, "weight")).getEType().getName()).isEqualTo("EString");
		// A non-containment reference was exported as its URI string -> becomes a plain EString attribute.
		assertThat(feature(product, "category")).isInstanceOf(EAttribute.class);
		assertThat(((EAttribute) feature(product, "category")).getEType().getName()).isEqualTo("EString");
	}

	@Test
	@DisplayName("imports a foreign proto: unsigned/64-bit scalars, repeated, map and proto3 optional")
	void foreignScalarsMapAndPresence() {
		// A hand-built descriptor for types our own exporter never emits.
		DescriptorProto entry = DescriptorProto.newBuilder()
				.setName("CountsEntry")
				.setOptions(MessageOptions.newBuilder().setMapEntry(true))
				.addField(FieldDescriptorProto.newBuilder().setName("key").setNumber(1)
						.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_STRING))
				.addField(FieldDescriptorProto.newBuilder().setName("value").setNumber(2)
						.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_INT32))
				.build();

		DescriptorProto thing = DescriptorProto.newBuilder()
				.setName("Thing")
				.addField(FieldDescriptorProto.newBuilder().setName("u").setNumber(1)
						.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_UINT32))
				.addField(FieldDescriptorProto.newBuilder().setName("big").setNumber(2)
						.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_SFIXED64))
				.addField(FieldDescriptorProto.newBuilder().setName("tags").setNumber(3)
						.setLabel(Label.LABEL_REPEATED).setType(Type.TYPE_STRING))
				.addField(FieldDescriptorProto.newBuilder().setName("counts").setNumber(4)
						.setLabel(Label.LABEL_REPEATED).setType(Type.TYPE_MESSAGE).setTypeName(".ext.Thing.CountsEntry"))
				// proto3 explicit presence needs a synthetic oneof.
				.addField(FieldDescriptorProto.newBuilder().setName("flag").setNumber(5)
						.setLabel(Label.LABEL_OPTIONAL).setType(Type.TYPE_BOOL)
						.setProto3Optional(true).setOneofIndex(0))
				.addOneofDecl(OneofDescriptorProto.newBuilder().setName("_flag"))
				.addNestedType(entry)
				.build();

		FileDescriptorProto file = FileDescriptorProto.newBuilder()
				.setName("ext.proto").setSyntax("proto3").setPackage("ext")
				.addMessageType(thing)
				.build();
		byte[] set = FileDescriptorSet.newBuilder().addFile(file).build().toByteArray();

		EPackage pkg = ProtobufImporter.fromDescriptorSet(set).get(0);
		assertThat(pkg.getNsURI()).isEqualTo("http://ext");

		EClass t = eClass(pkg, "Thing");
		assertThat(((EAttribute) feature(t, "u")).getEType().getName()).isEqualTo("ELong");   // unsigned 32 widened
		assertThat(((EAttribute) feature(t, "big")).getEType().getName()).isEqualTo("ELong");
		assertThat(feature(t, "tags").isMany()).isTrue();
		assertThat(((EAttribute) feature(t, "flag")).isUnsettable()).isTrue();

		// map<string,int32> -> containment reference (many) to the synthetic entry EClass.
		EReference counts = (EReference) feature(t, "counts");
		assertThat(counts.isContainment()).isTrue();
		assertThat(counts.isMany()).isTrue();
		EClass entryClass = eClass(pkg, "Thing_CountsEntry");
		assertThat(counts.getEType()).isSameAs(entryClass);
		assertThat(((EAttribute) feature(entryClass, "key")).getEType().getName()).isEqualTo("EString");
		assertThat(((EAttribute) feature(entryClass, "value")).getEType().getName()).isEqualTo("EInt");
	}

	@Test
	@DisplayName("fails clearly when a referenced dependency is not included in the set")
	void missingDependencyFailsClearly() {
		FileDescriptorProto file = FileDescriptorProto.newBuilder()
				.setName("a.proto").setSyntax("proto3").setPackage("a")
				.addDependency("missing.proto")
				.build();
		byte[] set = FileDescriptorSet.newBuilder().addFile(file).build().toByteArray();

		assertThatThrownBy(() -> ProtobufImporter.fromDescriptorSet(set))
				.isInstanceOf(ProtobufException.class)
				.hasMessageContaining("missing.proto")
				.hasMessageContaining("--include_imports");
	}
}
