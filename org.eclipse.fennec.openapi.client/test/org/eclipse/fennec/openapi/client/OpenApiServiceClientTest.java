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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.service.api.HttpStatusException;
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
			      },
			      "get": {
			        "operationId": "listPets",
			        "responses": { "200": { "description": "ok", "content": { "application/json": {
			          "schema": { "type": "array", "items": { "$ref": "#/components/schemas/Pet" } } } } } }
			      }
			    },
			    "/login": { "get": { "operationId": "loginUser",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "string" } } } } } } },
			    "/count": { "get": { "operationId": "countPets",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "integer", "format": "int32" } } } } } } },
			    "/tags": { "get": { "operationId": "listTags",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "array", "items": { "type": "string" } } } } } } } },
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
	private final AtomicReference<String> responseBody = new AtomicReference<>(PET_JSON);
	private final AtomicReference<String> responseContentType = new AtomicReference<>("application/json");

	@BeforeEach
	void setUp() throws IOException {
		model = OpenApiImporter.fromJson(DOC.getBytes(StandardCharsets.UTF_8));

		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/", exchange -> {
			lastMethod.set(exchange.getRequestMethod());
			lastPathAndQuery.set(exchange.getRequestURI().toString());
			lastBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			byte[] body = responseBody.get().getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", responseContentType.get());
			exchange.sendResponseHeaders(status.get(), body.length == 0 ? -1 : body.length);
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
	@DisplayName("an HTTP error status becomes an HttpStatusException with status and body (#97)")
	void errorStatusBecomesException() {
		status.set(404);
		responseBody.set("{\"message\":\"Pet not found\"}");
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			EClass requestType = model.operation("getPet").requestType();
			EObject request = EcoreUtil.create(requestType);
			request.eSet(requestType.getEStructuralFeature("petId"), 7L);

			assertThatThrownBy(() -> client.invoke("getPet", request))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("404")
					.isInstanceOfSatisfying(HttpStatusException.class, e -> {
						assertThat(e.status()).isEqualTo(404);
						assertThat(e.operationName()).isEqualTo("getPet");
						assertThat(e.bodyText()).isEqualTo("{\"message\":\"Pet not found\"}");
						assertThat(e.contentType()).contains("application/json");
					});
			assertThat(client.unwrap(HttpClient.class)).isPresent();
		}
	}

	@Test
	@DisplayName("an array answer is read element by element, in order (#95)")
	void arrayAnswer() {
		responseBody.set("[{\"id\":1,\"name\":\"Rex\"},{\"id\":2,\"name\":\"Bello\"}]");
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			List<EObject> pets = client.invokeMany("listPets", null);

			assertThat(lastMethod.get()).isEqualTo("GET");
			assertThat(lastPathAndQuery.get()).isEqualTo("/pets");
			assertThat(pets).extracting(pet -> pet.eClass().getName()).containsExactly("Pet", "Pet");
			assertThat(pets).extracting(pet -> String.valueOf(pet.eGet(pet.eClass().getEStructuralFeature("name"))))
					.containsExactly("Rex", "Bello");
		}
	}

	@Test
	@DisplayName("an empty array answer (or an empty body) is an empty list (#95)")
	void emptyArrayAnswer() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			responseBody.set("[]");
			assertThat(client.invokeMany("listPets", null)).isEmpty();
			responseBody.set("");
			assertThat(client.invokeMany("listPets", null)).isEmpty();
		}
	}

	@Test
	@DisplayName("invoke and invokeMany point at each other for the wrong kind of answer (#95)")
	void wrongInvokeFlavour() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			assertThatThrownBy(() -> client.invoke("listPets", null))
					.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("invokeMany");
			EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
			assertThatThrownBy(() -> client.invokeMany("createPet", EcoreUtil.create(pet)))
					.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("invoke(");
			assertThat(lastMethod.get()).as("no request was sent").isNull();
		}
	}

	@Test
	@DisplayName("a scalar answer is read from JSON, typed by its schema (#96)")
	void scalarAnswerFromJson() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			responseBody.set("\"session 4711\"");
			assertThat(client.invokeValue("loginUser", null)).isEqualTo("session 4711");
			responseBody.set("17");
			assertThat(client.invokeValue("countPets", null)).isEqualTo(17);
			responseBody.set("[\"cat\",\"dog\"]");
			assertThat(client.invokeValue("listTags", null)).isEqualTo(List.of("cat", "dog"));
		}
	}

	@Test
	@DisplayName("a scalar answer that is not JSON is read as plain text (#96)")
	void scalarAnswerFromPlainText() {
		responseContentType.set("text/plain");
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			responseBody.set("logged in user session:4711");
			assertThat(client.invokeValue("loginUser", null)).isEqualTo("logged in user session:4711");
			responseBody.set("17\n");
			assertThat(client.invokeValue("countPets", null)).isEqualTo(17);
			responseBody.set("");
			assertThat(client.invokeValue("loginUser", null)).isNull();
		}
	}

	@Test
	@DisplayName("a scalar that does not fit the schema type fails clearly (#96)")
	void scalarMismatch() {
		responseBody.set("\"many\"");
		try (OpenApiServiceClient client = new OpenApiServiceClient(baseUri(), model)) {
			assertThatThrownBy(() -> client.invokeValue("countPets", null))
					.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("many");
			assertThatThrownBy(() -> client.invoke("loginUser", null))
					.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("invokeValue");
			assertThatThrownBy(() -> client.invokeMany("listTags", null))
					.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("invokeValue");
			assertThatThrownBy(() -> client.invokeValue("listPets", null))
					.isInstanceOf(ServiceInvocationException.class).hasMessageContaining("invokeMany");
		}
	}
}
