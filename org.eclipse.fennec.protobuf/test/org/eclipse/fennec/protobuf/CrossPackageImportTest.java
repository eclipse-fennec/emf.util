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
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Type;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Timestamp;

/**
 * Imports a multi-package schema whose own messages reference <b>another custom package</b>
 * and a <b>well-known type</b> — the path the single-package well-known types cannot cover:
 * several {@link EPackage}s in one set, with cross-package {@link EReference}s resolving to
 * {@link EClass}es in a different package. Descriptors are hand-built plus timestamp.proto
 * from protobuf-java, serialized like {@code protoc --include_imports}.
 */
@DisplayName("ProtobufImporter: cross-package references across several EPackages")
class CrossPackageImportTest {

	private static FieldDescriptorProto scalar(String name, int number, Type type, boolean repeated) {
		return FieldDescriptorProto.newBuilder().setName(name).setNumber(number).setType(type)
				.setLabel(repeated ? Label.LABEL_REPEATED : Label.LABEL_OPTIONAL).build();
	}

	private static FieldDescriptorProto typed(String name, int number, Type type, String typeName) {
		return FieldDescriptorProto.newBuilder().setName(name).setNumber(number).setType(type)
				.setTypeName(typeName).setLabel(Label.LABEL_OPTIONAL).build();
	}

	private static EPackage byNsUri(List<EPackage> packages, String nsURI) {
		return packages.stream().filter(p -> nsURI.equals(p.getNsURI())).findFirst()
				.orElseThrow(() -> new AssertionError("no package " + nsURI + " in " + packages));
	}

	private static EStructuralFeature feature(EClass eClass, String name) {
		EStructuralFeature f = eClass.getEStructuralFeature(name);
		assertThat(f).as("feature '%s' on %s", name, eClass.getName()).isNotNull();
		return f;
	}

	private static byte[] fixture() {
		FileDescriptorProto catalog = FileDescriptorProto.newBuilder()
				.setName("shop/catalog.proto").setSyntax("proto3").setPackage("shop.catalog")
				.addMessageType(DescriptorProto.newBuilder().setName("Product")
						.addField(scalar("sku", 1, Type.TYPE_STRING, false))
						.addField(scalar("price", 2, Type.TYPE_DOUBLE, false)))
				.build();

		EnumDescriptorProto status = EnumDescriptorProto.newBuilder().setName("Status")
				.addValue(EnumValueDescriptorProto.newBuilder().setName("PENDING").setNumber(0))
				.addValue(EnumValueDescriptorProto.newBuilder().setName("SHIPPED").setNumber(1))
				.build();

		FileDescriptorProto orders = FileDescriptorProto.newBuilder()
				.setName("shop/orders.proto").setSyntax("proto3").setPackage("shop.orders")
				.addDependency("shop/catalog.proto")
				.addDependency("google/protobuf/timestamp.proto")
				.addMessageType(DescriptorProto.newBuilder().setName("Order")
						.addField(typed("product", 1, Type.TYPE_MESSAGE, ".shop.catalog.Product"))
						.addField(typed("created_at", 2, Type.TYPE_MESSAGE, ".google.protobuf.Timestamp"))
						.addField(scalar("tags", 3, Type.TYPE_STRING, true))
						.addField(typed("status", 4, Type.TYPE_ENUM, ".shop.orders.Status")))
				.addEnumType(status)
				.build();

		return FileDescriptorSet.newBuilder()
				.addFile(catalog)
				.addFile(orders)
				.addFile(Timestamp.getDescriptor().getFile().toProto())
				.build().toByteArray();
	}

	@Test
	@DisplayName("splits into one EPackage per proto package")
	void producesOnePackagePerProtoPackage() {
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(fixture());

		assertThat(packages).extracting(EPackage::getNsURI)
				.containsExactlyInAnyOrder("http://shop.catalog", "http://shop.orders", "http://google.protobuf");
		assertThat(byNsUri(packages, "http://shop.catalog").getName()).isEqualTo("catalog");
		assertThat(byNsUri(packages, "http://shop.orders").getName()).isEqualTo("orders");
	}

	@Test
	@DisplayName("a containment reference resolves to an EClass in another custom package")
	void crossCustomPackageReference() {
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(fixture());
		EPackage catalog = byNsUri(packages, "http://shop.catalog");
		EPackage orders = byNsUri(packages, "http://shop.orders");

		EClass order = (EClass) orders.getEClassifier("Order");
		EClass product = (EClass) catalog.getEClassifier("Product");

		EReference productRef = (EReference) feature(order, "product");
		assertThat(productRef.isContainment()).isTrue();
		assertThat(productRef.getEType()).isSameAs(product);
		// The target really lives in the *other* package.
		assertThat(productRef.getEType().getEPackage()).isSameAs(catalog).isNotSameAs(orders);
		assertThat(((EAttribute) feature(product, "price")).getEType().getName()).isEqualTo("EDouble");
	}

	@Test
	@DisplayName("a reference to a well-known type resolves into the google.protobuf package")
	void referenceToWellKnownType() {
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(fixture());
		EPackage orders = byNsUri(packages, "http://shop.orders");
		EPackage wellKnown = byNsUri(packages, "http://google.protobuf");

		EClass order = (EClass) orders.getEClassifier("Order");
		EReference createdAt = (EReference) feature(order, "created_at");

		assertThat(createdAt.getEType()).isSameAs(wellKnown.getEClassifier("Timestamp"));
		assertThat(createdAt.getEType().getEPackage()).isSameAs(wellKnown);
	}

	@Test
	@DisplayName("local scalars, repeated and a same-package enum still map correctly")
	void localFeaturesAlongsideCrossPackage() {
		List<EPackage> packages = ProtobufImporter.fromDescriptorSet(fixture());
		EPackage orders = byNsUri(packages, "http://shop.orders");
		EClass order = (EClass) orders.getEClassifier("Order");

		assertThat(feature(order, "tags").isMany()).isTrue();
		EStructuralFeature status = feature(order, "status");
		assertThat(((EAttribute) status).getEType()).isSameAs(orders.getEClassifier("Status"));
		assertThat(((EEnum) orders.getEClassifier("Status")).getELiterals())
				.extracting(l -> l.getName()).containsExactly("PENDING", "SHIPPED");
		// Field numbers survive the import.
		assertThat(ProtobufAnnotations.explicitFieldNumber(feature(order, "created_at"))).isEqualTo(2);
	}
}
