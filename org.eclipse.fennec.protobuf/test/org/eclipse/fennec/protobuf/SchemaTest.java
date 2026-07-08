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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

@DisplayName("ProtobufSchema descriptor derivation")
class SchemaTest {

	private ShopModel model;
	private ProtobufSchema schema;

	@BeforeEach
	void setUp() {
		model = new ShopModel();
		schema = ProtobufSchema.forPackage(model.pkg);
	}

	@Test
	@DisplayName("honours pinned field numbers from EAnnotations")
	void explicitFieldNumbers() {
		Descriptor cat = schema.descriptorFor(model.category);
		assertThat(cat.findFieldByName("name").getNumber()).isEqualTo(1);
		assertThat(cat.findFieldByName("products").getNumber()).isEqualTo(2);
	}

	@Test
	@DisplayName("auto-assigns sequential numbers where none are pinned")
	void autoFieldNumbers() {
		Descriptor product = schema.descriptorFor(model.product);
		assertThat(product.findFieldByName("name").getNumber()).isEqualTo(1);
		assertThat(product.findFieldByName("price").getNumber()).isEqualTo(2);
		assertThat(product.findFieldByName("count").getNumber()).isEqualTo(3);
		assertThat(product.findFieldByName("category").getNumber()).isEqualTo(8);
	}

	@Test
	@DisplayName("maps Ecore data types to the right protobuf types")
	void typeMapping() {
		Descriptor product = schema.descriptorFor(model.product);
		assertThat(product.findFieldByName("price").getType()).isEqualTo(FieldDescriptor.Type.DOUBLE);
		assertThat(product.findFieldByName("count").getType()).isEqualTo(FieldDescriptor.Type.INT32);
		assertThat(product.findFieldByName("code").getType()).isEqualTo(FieldDescriptor.Type.BYTES);
		assertThat(product.findFieldByName("status").getType()).isEqualTo(FieldDescriptor.Type.ENUM);
		assertThat(product.findFieldByName("weight").getType()).isEqualTo(FieldDescriptor.Type.STRING);
		// non-containment reference is stored as a URI string
		assertThat(product.findFieldByName("category").getType()).isEqualTo(FieldDescriptor.Type.STRING);
		// containment reference is an embedded message
		assertThat(schema.descriptorFor(model.category).findFieldByName("products").getType())
				.isEqualTo(FieldDescriptor.Type.MESSAGE);
	}

	@Test
	@DisplayName("marks repeated and optional fields correctly")
	void labels() {
		Descriptor product = schema.descriptorFor(model.product);
		assertThat(product.findFieldByName("tags").isRepeated()).isTrue();
		assertThat(product.findFieldByName("price").hasPresence()).isTrue();
	}

	@Test
	@DisplayName("renders proto3 source")
	void protoSource() {
		String proto = schema.toProtoSource();
		assertThat(proto).contains("syntax = \"proto3\";");
		assertThat(proto).contains("package shop;");
		assertThat(proto).contains("enum Status {");
		assertThat(proto).contains("ACTIVE = 0;");
		assertThat(proto).contains("message Product {");
		assertThat(proto).contains("repeated string tags = 7;");
		assertThat(proto).contains("optional double price = 2;");
		assertThat(proto).contains("Status status = 6;");
		assertThat(proto).contains("repeated Product products = 2;");
	}

	@Test
	@DisplayName("rejects cross-package containment")
	void crossPackageContainmentRejected() {
		EcoreFactory ef = EcoreFactory.eINSTANCE;
		EClass external = ef.createEClass();
		external.setName("External");
		var otherPkg = ef.createEPackage();
		otherPkg.setName("other");
		otherPkg.setNsURI("http://example.org/other");
		otherPkg.getEClassifiers().add(external);

		var containment = ef.createEReference();
		containment.setName("external");
		containment.setEType(external);
		containment.setContainment(true);
		model.product.getEStructuralFeatures().add(containment);

		assertThatThrownBy(() -> ProtobufSchema.forPackage(model.pkg))
				.isInstanceOf(ProtobufException.class)
				.hasMessageContaining("Cross-package containment");
	}

	@Test
	@DisplayName("rejects duplicate pinned field numbers")
	void duplicateFieldNumbersRejected() {
		EcorePackage ec = EcorePackage.eINSTANCE;
		var dup = EcoreFactory.eINSTANCE.createEAttribute();
		dup.setName("dup");
		dup.setEType(ec.getEString());
		var a = EcoreFactory.eINSTANCE.createEAnnotation();
		a.setSource(ProtobufAnnotations.SOURCE);
		a.getDetails().put(ProtobufAnnotations.KEY_FIELD_NUMBER, "1"); // collides with categoryName
		dup.getEAnnotations().add(a);
		model.category.getEStructuralFeatures().add(dup);

		assertThatThrownBy(() -> ProtobufSchema.forPackage(model.pkg))
				.isInstanceOf(ProtobufException.class)
				.hasMessageContaining("Duplicate protobuf field number");
	}
}
