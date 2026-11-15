/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.openapi.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** The style/explode table of the OpenAPI 3 specification, for primitives and arrays (#98). */
@DisplayName("ParameterSerializer: OpenAPI style/explode")
class ParameterSerializerTest {

	private static final List<String> AB = List.of("a", "b");

	@Test
	@DisplayName("query: form (exploded by default), spaceDelimited, pipeDelimited")
	void query() {
		assertThat(ParameterSerializer.query("tags", AB, null, null)).isEqualTo("tags=a&tags=b");
		assertThat(ParameterSerializer.query("tags", AB, "form", true)).isEqualTo("tags=a&tags=b");
		assertThat(ParameterSerializer.query("tags", AB, "form", false)).isEqualTo("tags=a,b");
		assertThat(ParameterSerializer.query("tags", AB, "spaceDelimited", false)).isEqualTo("tags=a%20b");
		assertThat(ParameterSerializer.query("tags", AB, "pipeDelimited", false)).isEqualTo("tags=a%7Cb");
		assertThat(ParameterSerializer.query("tags", AB, "pipeDelimited", true)).isEqualTo("tags=a&tags=b");
		assertThat(ParameterSerializer.query("q", List.of("x y&z"), null, null)).isEqualTo("q=x+y%26z");
	}

	@Test
	@DisplayName("path: simple (default), label, matrix")
	void path() {
		assertThat(ParameterSerializer.path("id", AB, null, null)).isEqualTo("a,b");
		assertThat(ParameterSerializer.path("id", List.of("5"), null, null)).isEqualTo("5");
		assertThat(ParameterSerializer.path("id", AB, "label", false)).isEqualTo(".a,b");
		assertThat(ParameterSerializer.path("id", AB, "label", true)).isEqualTo(".a.b");
		assertThat(ParameterSerializer.path("id", AB, "matrix", false)).isEqualTo(";id=a,b");
		assertThat(ParameterSerializer.path("id", AB, "matrix", true)).isEqualTo(";id=a;id=b");
		assertThat(ParameterSerializer.path("id", List.of("x y"), null, null)).isEqualTo("x%20y");
	}

	@Test
	@DisplayName("header: simple; cookie: form")
	void headerAndCookie() {
		assertThat(ParameterSerializer.header("X-Ids", AB, null)).isEqualTo("a,b");
		assertThat(ParameterSerializer.cookie("ids", AB, null, false)).isEqualTo("ids=a,b");
		assertThat(ParameterSerializer.cookie("ids", AB, null, null)).isEqualTo("ids=a; ids=b");
	}

	@Test
	@DisplayName("a style the location does not allow is rejected")
	void unsupportedStyle() {
		assertThatThrownBy(() -> ParameterSerializer.query("filter", AB, "deepObject", true))
				.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("deepObject");
		assertThatThrownBy(() -> ParameterSerializer.path("id", AB, "form", null))
				.isInstanceOf(ServiceInvocationException.class);
		assertThatThrownBy(() -> ParameterSerializer.header("X", AB, "matrix"))
				.isInstanceOf(ServiceInvocationException.class);
	}
}
