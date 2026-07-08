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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ProtobufSchemaCache")
class ProtobufSchemaCacheTest {

	@Test
	@DisplayName("memoizes one schema per package and re-derives after invalidation")
	void memoizes() {
		ProtobufSchemaCache cache = new ProtobufSchemaCache();
		ShopModel model = new ShopModel();

		ProtobufSchema first = cache.get(model.pkg);
		ProtobufSchema second = cache.get(model.pkg);
		assertThat(second).isSameAs(first);
		assertThat(cache.size()).isEqualTo(1);

		cache.invalidate(model.pkg);
		assertThat(cache.size()).isZero();
		assertThat(cache.get(model.pkg)).isNotSameAs(first);

		cache.clear();
		assertThat(cache.size()).isZero();
	}

	@Test
	@DisplayName("distinct packages get distinct schemas")
	void distinctPackages() {
		ProtobufSchemaCache cache = new ProtobufSchemaCache();
		cache.get(new ShopModel().pkg);
		cache.get(new ZooModel().pkg);
		assertThat(cache.size()).isEqualTo(2);
	}
}
