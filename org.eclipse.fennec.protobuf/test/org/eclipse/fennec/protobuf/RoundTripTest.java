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

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EObject <-> protobuf round-trip")
class RoundTripTest {

	private ShopModel model;
	private ProtobufSchema schema;

	@BeforeEach
	void setUp() {
		model = new ShopModel();
		schema = ProtobufSchema.forPackage(model.pkg);
	}

	private EObject roundTrip(EObject source) {
		byte[] bytes = schema.writer().toBytes(source);
		return schema.reader().fromBytes(bytes, source.eClass());
	}

	@Test
	@DisplayName("round-trips scalar attributes of every kind")
	void scalars() {
		EObject product = model.newProduct();
		product.eSet(model.productName, "Milk");
		product.eSet(model.productPrice, 1.29d);
		product.eSet(model.productCount, 42);
		product.eSet(model.productCode, new byte[] { 1, 2, 3, 4 });
		product.eSet(model.productWeight, new BigDecimal("1.250"));
		product.eSet(model.productStatus, model.statusValue("DISCONTINUED"));

		EObject read = roundTrip(product);

		assertThat(read.eGet(model.productName)).isEqualTo("Milk");
		assertThat(read.eGet(model.productPrice)).isEqualTo(1.29d);
		assertThat(read.eGet(model.productCount)).isEqualTo(42);
		assertThat((byte[]) read.eGet(model.productCode)).containsExactly(1, 2, 3, 4);
		assertThat(read.eGet(model.productWeight)).isEqualTo(new BigDecimal("1.250"));
		assertThat(model.factory.convertToString(model.status, read.eGet(model.productStatus)))
				.isEqualTo("DISCONTINUED");
	}

	@Test
	@DisplayName("round-trips repeated attributes")
	void repeated() {
		EObject product = model.newProduct();
		@SuppressWarnings("unchecked")
		List<String> tags = (List<String>) product.eGet(model.productTags);
		tags.add("dairy");
		tags.add("fresh");

		EObject read = roundTrip(product);

		@SuppressWarnings("unchecked")
		List<String> readTags = (List<String>) read.eGet(model.productTags);
		assertThat(readTags).containsExactly("dairy", "fresh");
	}

	@Test
	@DisplayName("preserves unset vs. set-to-default (proto3 presence)")
	void presence() {
		EObject onlyName = model.newProduct();
		onlyName.eSet(model.productName, "Bread");
		EObject read = roundTrip(onlyName);
		assertThat(read.eIsSet(model.productPrice)).isFalse();
		assertThat(read.eIsSet(model.productCount)).isFalse();

		EObject zeroCount = model.newProduct();
		zeroCount.eSet(model.productCount, 0); // set, but to the default value
		EObject readZero = roundTrip(zeroCount);
		assertThat(readZero.eIsSet(model.productCount)).isTrue();
		assertThat(readZero.eGet(model.productCount)).isEqualTo(0);
	}

	@Test
	@DisplayName("round-trips containment (embedded messages)")
	void containment() {
		EObject category = model.newCategory();
		category.eSet(model.categoryName, "Food");
		EObject milk = model.newProduct();
		milk.eSet(model.productName, "Milk");
		milk.eSet(model.productPrice, 1.29d);
		EObject bread = model.newProduct();
		bread.eSet(model.productName, "Bread");
		@SuppressWarnings("unchecked")
		List<EObject> products = (List<EObject>) category.eGet(model.categoryProducts);
		products.add(milk);
		products.add(bread);

		EObject read = roundTrip(category);

		assertThat(read.eGet(model.categoryName)).isEqualTo("Food");
		@SuppressWarnings("unchecked")
		List<EObject> readProducts = (List<EObject>) read.eGet(model.categoryProducts);
		assertThat(readProducts).hasSize(2);
		assertThat(readProducts.get(0).eGet(model.productName)).isEqualTo("Milk");
		assertThat(readProducts.get(0).eGet(model.productPrice)).isEqualTo(1.29d);
		assertThat(readProducts.get(1).eGet(model.productName)).isEqualTo("Bread");
	}

	@Test
	@DisplayName("stores non-containment references as a resolvable proxy URI")
	void nonContainmentReference() {
		EObject category = model.newCategory();
		category.eSet(model.categoryName, "Food");
		EObject product = model.newProduct();
		product.eSet(model.productName, "Milk");
		product.eSet(model.productCategory, category);

		String expectedUri = EcoreUtil.getURI(category).toString();
		EObject read = roundTrip(product);

		EObject ref = (EObject) read.eGet(model.productCategory);
		assertThat(ref.eIsProxy()).isTrue();
		assertThat(((InternalEObject) ref).eProxyURI().toString()).isEqualTo(expectedUri);
	}
}
