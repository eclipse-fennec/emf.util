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
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Proves the {@link org.eclipse.fennec.service.api.ServiceClient} design end-to-end for
 * OpenAPI/REST: import a document, invoke operations over HTTP (body-only POST and a
 * parameterised GET via the synthetic request EClass), get typed EObjects back. Uses a local
 * JDK {@link HttpServer} — reliable, no network. {@code PetstoreRemoteIT} additionally runs
 * against the public petstore ({@code @Tag("remote")}).
 */
@DisplayName("OpenApiServiceClient: end-to-end invoke against a local REST endpoint")
class OpenApiServiceClientTest {

	private static final String PET_JSON = "{\"id\":42,\"name\":\"Rex\"}";

	/** Mini petstore: POST /pets (body only) and GET /pets/{petId} (path + query params). */
	private static final String DOC = """
			{
			  "openapi": "3.0.3",
			  "info": { "title": "Mini Petstore", "version": "1.0" },
			  "paths": {
			    "/pets": {
			      "post": {
			        "operationId": "createPet",
			        "requestBody": { "content": { "application/json": {
			          "schema": { "$ref": "#/components/schemas/Pet" } } } },
			        "responses": { "200": { "description": "ok", "content": { "application/json": {
			          "schema": { "$ref": "#/components/schemas/Pet" } } } } }
			      }
			    },
			    "/pets/{petId}": {
			      "get": {
			        "operationId": "getPet",
			        "parameters": [
			          { "name": "petId", "in": "path", "required": true, "schema": { "type": "integer" } },
			          { "name": "verbose", "in": "query", "schema": { "type": "boolean" } }
			        ],
			        "responses": { "200": { "description": "ok", "content": { "application/json": {
			          "schema": { "$ref": "#/components/schemas/Pet" } } } } }
			      }
			    }
			  },
			  "components": { "schemas": {
			    "Pet": { "type": "object", "properties": {
			      "id": { "type": "integer", "format": "int64" },
			      "name": { "type": "string" } } }
			  } }
			}
			""";

	private HttpServer server;
	private OpenApiModel model;
	private final AtomicReference<String> lastMethod = new AtomicReference<>();
	private final AtomicReference<String> lastPathAndQuery = new AtomicReference<>();
	private final AtomicReference<String> lastBody = new AtomicReference<>();
	private final AtomicReference<Integer> status = new AtomicReference<>(200);

	@BeforeEach
	void setUp() throws IOException {
		model = OpenApiImporter.fromJson(DOC.getBytes(StandardCharsets.UTF_8));

		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/", exchange -> {
			lastMethod.set(exchange.getRequestMethod());
			lastPathAndQuery.set(exchange.getRequestURI().toString());
			lastBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			byte[] body = PET_JSON.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(status.get(), body.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		});
		server.start();
	}

	@AfterEach
	void tearDown() {
		server.stop(0);
	}

	private URI baseUri() {
		return URI.create("http://localhost:" + server.getAddress().getPort());
	}

	@Test
	@DisplayName("POST with a body-only request serializes the EObject as JSON and parses the response")
	void bodyOnlyPost() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
			EObject rex = EcoreUtil.create(pet);
			rex.eSet(pet.getEStructuralFeature("name"), "Rex");

			EObject created = client.invoke("createPet", rex);

			assertThat(lastMethod.get()).isEqualTo("POST");
			assertThat(lastPathAndQuery.get()).isEqualTo("/pets");
			assertThat(lastBody.get()).contains("\"name\"").contains("Rex");

			assertThat(created).isNotNull();
			assertThat(created.eClass().getName()).isEqualTo("Pet");
			assertThat(String.valueOf(created.eGet(created.eClass().getEStructuralFeature("name")))).isEqualTo("Rex");
		}
	}

	@Test
	@DisplayName("GET with path + query parameters via the synthetic request EClass")
	void parameterisedGet() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			EClass requestType = model.operation("getPet").requestType();
			EObject request = EcoreUtil.create(requestType);
			request.eSet(requestType.getEStructuralFeature("petId"), 42L);
			request.eSet(requestType.getEStructuralFeature("verbose"), true);

			EObject pet = client.invoke("getPet", request);

			assertThat(lastMethod.get()).isEqualTo("GET");
			assertThat(lastPathAndQuery.get()).isEqualTo("/pets/42?verbose=true");
			assertThat(lastBody.get()).isEmpty();
			assertThat(pet.eClass().getName()).isEqualTo("Pet");
		}
	}

	@Test
	@DisplayName("an HTTP error status becomes a ServiceInvocationException")
	void errorStatusBecomesException() {
		status.set(404);
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			EClass requestType = model.operation("getPet").requestType();
			EObject request = EcoreUtil.create(requestType);
			request.eSet(requestType.getEStructuralFeature("petId"), 7L);

			assertThatThrownBy(() -> client.invoke("getPet", request))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("404");
			assertThat(client.unwrap(HttpClient.class)).isPresent();
		}
	}
}
