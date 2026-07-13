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

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

/**
 * Proves {@link OpenApiAuth} end-to-end against a local endpoint: the credential lands where
 * the document's security scheme says (header/query/cookie, Authorization), the OAuth2
 * client_credentials token is fetched from the declared tokenUrl and cached, and a required
 * but unregistered scheme fails fast — before any HTTP.
 */
@DisplayName("OpenApiAuth: security schemes applied per operation")
class OpenApiAuthTest {

	private static final String PET_JSON = "{\"id\":1,\"name\":\"Rex\"}";

	/** Every operation is protected by a different scheme; the oauth tokenUrl is filled in per test run. */
	private static final String DOC_TEMPLATE = """
			{
			  "openapi": "3.0.3",
			  "info": { "title": "Auth Petstore", "version": "1.0" },
			  "security": [ { "api_key": [] } ],
			  "paths": {
			    "/pets/header": { "get": { "operationId": "viaApiKeyHeader",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "$ref": "#/components/schemas/Pet" } } } } } } },
			    "/pets/query": { "get": { "operationId": "viaApiKeyQuery",
			      "security": [ { "api_key_query": [] } ],
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "$ref": "#/components/schemas/Pet" } } } } } } },
			    "/pets/basic": { "get": { "operationId": "viaBasic",
			      "security": [ { "basic_auth": [] } ],
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "$ref": "#/components/schemas/Pet" } } } } } } },
			    "/pets/bearer": { "get": { "operationId": "viaBearer",
			      "security": [ { "bearer_auth": [] } ],
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "$ref": "#/components/schemas/Pet" } } } } } } },
			    "/pets/oauth": { "get": { "operationId": "viaOauth",
			      "security": [ { "oauth": [ "write:pets" ] } ],
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "$ref": "#/components/schemas/Pet" } } } } } } },
			    "/pets/open": { "get": { "operationId": "viaAnonymous",
			      "security": [ {}, { "api_key": [] } ],
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "$ref": "#/components/schemas/Pet" } } } } } } }
			  },
			  "components": {
			    "schemas": {
			      "Pet": { "type": "object", "properties": {
			        "id": { "type": "integer", "format": "int64" },
			        "name": { "type": "string" } } }
			    },
			    "securitySchemes": {
			      "api_key": { "type": "apiKey", "name": "X-Api-Key", "in": "header" },
			      "api_key_query": { "type": "apiKey", "name": "api_key", "in": "query" },
			      "basic_auth": { "type": "http", "scheme": "basic" },
			      "bearer_auth": { "type": "http", "scheme": "bearer" },
			      "oauth": { "type": "oauth2", "flows": { "clientCredentials": {
			        "tokenUrl": "${TOKEN_URL}",
			        "scopes": { "write:pets": "modify pets" } } } }
			    }
			  }
			}
			""";

	private HttpServer server;
	private OpenApiModel model;
	private final AtomicReference<String> lastPathAndQuery = new AtomicReference<>();
	private final AtomicReference<Headers> lastHeaders = new AtomicReference<>();
	private final AtomicInteger tokenRequests = new AtomicInteger();
	private final AtomicReference<String> lastTokenRequestBody = new AtomicReference<>();
	private final AtomicReference<String> lastTokenRequestAuth = new AtomicReference<>();

	@BeforeEach
	void setUp() throws IOException {
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/token", exchange -> {
			tokenRequests.incrementAndGet();
			lastTokenRequestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			lastTokenRequestAuth.set(exchange.getRequestHeaders().getFirst("Authorization"));
			respond(exchange.getResponseHeaders(), exchange, "{\"access_token\":\"tok-42\",\"expires_in\":3600}");
		});
		server.createContext("/pets", exchange -> {
			lastPathAndQuery.set(exchange.getRequestURI().toString());
			lastHeaders.set(exchange.getRequestHeaders());
			exchange.getRequestBody().readAllBytes();
			respond(exchange.getResponseHeaders(), exchange, PET_JSON);
		});
		server.start();

		String doc = DOC_TEMPLATE.replace("${TOKEN_URL}", baseUri() + "/token");
		model = OpenApiImporter.fromJson(doc.getBytes(StandardCharsets.UTF_8));
	}

	private static void respond(Headers headers, com.sun.net.httpserver.HttpExchange exchange, String json)
			throws IOException {
		byte[] body = json.getBytes(StandardCharsets.UTF_8);
		headers.add("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, body.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(body);
		}
	}

	@AfterEach
	void tearDown() {
		server.stop(0);
	}

	private URI baseUri() {
		return URI.create("http://localhost:" + server.getAddress().getPort());
	}

	@Test
	@DisplayName("apiKey lands in the header/query parameter the scheme declares")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis; try-with-resources closes the client
	void apiKeyPlacement() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)
				.withAuth("api_key", OpenApiAuth.apiKey("secret-h"))
				.withAuth("api_key_query", OpenApiAuth.apiKey("secret-q"))) {

			client.invoke("viaApiKeyHeader", null); // inherits the global api_key requirement
			assertThat(lastHeaders.get().getFirst("X-Api-Key")).isEqualTo("secret-h");
			assertThat(lastPathAndQuery.get()).isEqualTo("/pets/header");

			client.invoke("viaApiKeyQuery", null);
			assertThat(lastPathAndQuery.get()).isEqualTo("/pets/query?api_key=secret-q");
			assertThat(lastHeaders.get().getFirst("X-Api-Key")).isNull();
		}
	}

	@Test
	@DisplayName("http basic and bearer set the Authorization header")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis; try-with-resources closes the client
	void basicAndBearer() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)
				.withAuth("basic_auth", OpenApiAuth.basic("scott", "tiger"))
				.withAuth("bearer_auth", OpenApiAuth.bearer("my-token"))) {

			client.invoke("viaBasic", null);
			assertThat(lastHeaders.get().getFirst("Authorization")).isEqualTo("Basic "
					+ Base64.getEncoder().encodeToString("scott:tiger".getBytes(StandardCharsets.UTF_8)));

			client.invoke("viaBearer", null);
			assertThat(lastHeaders.get().getFirst("Authorization")).isEqualTo("Bearer my-token");
		}
	}

	@Test
	@DisplayName("client_credentials fetches the token from the declared tokenUrl and caches it")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis; try-with-resources closes the client
	void clientCredentials() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)
				.withAuth("oauth", OpenApiAuth.clientCredentials("my-client", "my-secret"))) {

			client.invoke("viaOauth", null);
			client.invoke("viaOauth", null);

			assertThat(tokenRequests.get()).isEqualTo(1); // cached until expires_in
			assertThat(lastHeaders.get().getFirst("Authorization")).isEqualTo("Bearer tok-42");
			assertThat(lastTokenRequestBody.get())
					.contains("grant_type=client_credentials")
					.contains("scope=write%3Apets"); // the requirement's scopes
			assertThat(lastTokenRequestAuth.get()).isEqualTo("Basic "
					+ Base64.getEncoder().encodeToString("my-client:my-secret".getBytes(StandardCharsets.UTF_8)));
		}
	}

	@Test
	@DisplayName("an anonymous alternative ({} in security) lets the call pass without credentials")
	void anonymousAlternative() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			client.invoke("viaAnonymous", null);

			assertThat(lastPathAndQuery.get()).isEqualTo("/pets/open");
			assertThat(lastHeaders.get().getFirst("X-Api-Key")).isNull();
		}
	}

	@Test
	@DisplayName("required but unregistered credentials fail fast — before any HTTP")
	void missingCredentialsFailFast() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			assertThatThrownBy(() -> client.invoke("viaBasic", null))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("requires authentication")
					.hasMessageContaining("basic_auth");
			assertThat(lastPathAndQuery.get()).isNull(); // nothing hit the server
		}
	}

	@Test
	@DisplayName("credentials that cannot satisfy the scheme type are rejected with a clear error")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis; try-with-resources closes the client
	void mismatchedCredentials() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)
				.withAuth("basic_auth", OpenApiAuth.apiKey("nope"))) {
			assertThatThrownBy(() -> client.invoke("viaBasic", null))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("apiKey")
					.hasMessageContaining("basic_auth");
		}
	}

	@Test
	@DisplayName("withAuth validates the scheme name against the document")
	void unknownSchemeRejected() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			assertThatThrownBy(() -> client.withAuth("ghost", OpenApiAuth.apiKey("x")))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("ghost");
			// several schemes declared: the single-scheme convenience must refuse to guess
			assertThatThrownBy(() -> client.withAuth(OpenApiAuth.apiKey("x")))
					.isInstanceOf(IllegalStateException.class);
		}
	}
}
