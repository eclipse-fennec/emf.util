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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Self-describing (de)serialization: the root type travels in the stream")
class SelfDescribingTest {

	private ShopModel model;
	private ProtobufSchema schema;

	@BeforeEach
	void setUp() {
		model = new ShopModel();
		schema = ProtobufSchema.forPackage(model.pkg);
	}

	private EObject sampleCategory() {
		EObject category = model.newCategory();
		category.eSet(model.categoryName, "Food");
		return category;
	}

	@Test
	@DisplayName("reader reconstructs the root EClass from the stream, no EClass argument needed")
	void reconstructsRootTypeWithoutEClass() {
		byte[] bytes = schema.writer().toBytes(sampleCategory());

		EObject read = schema.reader().fromBytes(bytes);

		assertThat(read.eClass()).isEqualTo(model.category);
		assertThat(read.eGet(model.categoryName)).isEqualTo("Food");
	}

	@Test
	@DisplayName("readSelfDescribing resolves the root via the context's package registry (no bound schema)")
	void resolvesRootViaRegistry() {
		byte[] bytes = schema.writer().toBytes(sampleCategory());

		EPackageRegistryImpl registry = new EPackageRegistryImpl();
		registry.put(ShopModel.NS_URI, model.pkg);
		ProtobufContext ctx = ProtobufContext.defaults().withPackageRegistry(registry);

		EObject read = ProtobufReader.readSelfDescribing(bytes, ctx);

		assertThat(read.eClass()).isEqualTo(model.category);
		assertThat(read.eGet(model.categoryName)).isEqualTo("Food");
	}

	@Test
	@DisplayName("the self-describing frame is larger than the bare message (it carries the type header)")
	void frameAddsATypeHeaderOverTheBareMessage() {
		EObject category = sampleCategory();

		byte[] framed = schema.writer().toBytes(category);
		byte[] bare = schema.writer().toBareBytes(category);

		assertThat(framed.length).isGreaterThan(bare.length);
	}

	@Test
	@DisplayName("the bare API still round-trips when the type is supplied explicitly")
	void bareRoundTripWithExplicitType() {
		byte[] bare = schema.writer().toBareBytes(sampleCategory());

		EObject read = schema.reader().fromBareBytes(bare, model.category);

		assertThat(read.eClass()).isEqualTo(model.category);
		assertThat(read.eGet(model.categoryName)).isEqualTo("Food");
	}

	@Test
	@DisplayName("readSelfDescribing fails clearly when the root's package is not registered")
	void failsForUnregisteredPackage() {
		byte[] bytes = schema.writer().toBytes(sampleCategory());
		ProtobufContext ctx = ProtobufContext.defaults().withPackageRegistry(new EPackageRegistryImpl());

		assertThatThrownBy(() -> ProtobufReader.readSelfDescribing(bytes, ctx))
				.isInstanceOf(ProtobufException.class)
				.hasMessageContaining(ShopModel.NS_URI);
	}
}
