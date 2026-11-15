/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.openapi.client.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.fennec.openapi.client.OpenApiAuth;
import org.eclipse.fennec.openapi.client.OpenApiServiceClient;
import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A caller outside the client package applies the configured credentials to a request it builds
 * itself (#100) — deliberately in another package, so it compiles against the public API only.
 */
@DisplayName("OpenApiAuth: applied to a request built outside the client (#100)")
class ExternalAuthenticationTest {

	private static final String DOC = """
			{
			  "openapi": "3.0.3",
			  "info": { "title": "Auth", "version": "1.0" },
			  "security": [ { "api_key": [] } ],
			  "paths": {
			    "/a": { "get": { "operationId": "keyed", "responses": { "200": { "description": "ok" } } } },
			    "/b": { "get": { "operationId": "bearer", "security": [ { "token": [] } ],
			      "responses": { "200": { "description": "ok" } } } }
			  },
			  "components": { "securitySchemes": {
			    "api_key": { "type": "apiKey", "name": "api_key", "in": "query" },
			    "token": { "type": "http", "scheme": "bearer" }
			  } }
			}
			""";

	/** Records what the credentials write. */
	private static final class Recorder implements OpenApiAuth.Target {
		final Map<String, String> headers = new LinkedHashMap<>();
		final Map<String, String> query = new LinkedHashMap<>();
		final Map<String, String> cookies = new LinkedHashMap<>();

		@Override
		public void header(String name, String value) {
			headers.put(name, value);
		}

		@Override
		public void query(String name, String value) {
			query.put(name, value);
		}

		@Override
		public void cookie(String name, String value) {
			cookies.put(name, value);
		}

		@Override
		public HttpClient http() {
			return null; // no token request for apiKey/bearer
		}
	}

	private final OpenApiModel model = OpenApiImporter.fromJson(DOC.getBytes(StandardCharsets.UTF_8));

	@Test
	@DisplayName("the client applies the credentials an operation demands, as invoke would")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis; try-with-resources closes the client
	void authenticateThroughTheClient() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(URI.create("http://localhost"), model)
				.withAuth("api_key", OpenApiAuth.apiKey("k-1"))
				.withAuth("token", OpenApiAuth.bearer("t-1"))) {
			Recorder keyed = new Recorder();
			client.authenticate(client.operation("keyed"), keyed);
			assertThat(keyed.query).containsExactly(Map.entry("api_key", "k-1"));
			assertThat(keyed.headers).isEmpty();

			Recorder bearer = new Recorder();
			client.authenticate(client.operation("bearer"), bearer);
			assertThat(bearer.headers).containsExactly(Map.entry("Authorization", "Bearer t-1"));
		}
	}

	@Test
	@DisplayName("missing credentials fail as they would on invoke")
	void missingCredentials() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(URI.create("http://localhost"), model)) {
			assertThatThrownBy(() -> client.authenticate(client.operation("keyed"), new Recorder()))
					.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("withAuth");
		}
	}

	@Test
	@DisplayName("a credential applies itself to a known scheme directly")
	void applyDirectly() {
		Recorder target = new Recorder();
		OpenApiAuth.apiKey("k-2").apply("api_key", model.securityScheme("api_key"), List.of(), target);
		assertThat(target.query).containsExactly(Map.entry("api_key", "k-2"));
	}
}
