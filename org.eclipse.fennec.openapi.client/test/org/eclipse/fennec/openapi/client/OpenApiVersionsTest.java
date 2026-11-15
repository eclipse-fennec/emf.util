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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.core.groups.Tuple;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.openapi.ecore.OpenApiOperation;
import org.eclipse.fennec.service.api.HttpStatusException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.sun.net.httpserver.HttpServer;

/**
 * The same petstore as OpenAPI 3.0, 3.1 and 3.2 (#102): it imports to the same operations and
 * calls the same way — objects, arrays, scalars, parameters, API-key auth and HTTP errors. The
 * 3.0 text marks the nullable {@code tag} with {@code nullable: true}, 3.1 and 3.2 with a type
 * array; everything else is the same text.
 */
@DisplayName("OpenAPI 3.0 / 3.1 / 3.2: same petstore, same operations, same calls")
class OpenApiVersionsTest {

	private static final String PET_JSON = "{\"id\":42,\"name\":\"Rex\",\"tag\":null}";

	private static String petstore(String version) {
		String tag = version.startsWith("3.0")
				? "{ \"type\": \"string\", \"nullable\": true }"
				: "{ \"type\": [\"string\", \"null\"] }";
		return """
				{
				  "openapi": "%s",
				  "info": { "title": "Mini Petstore", "version": "1.0" },
				  "security": [ { "api_key": [] } ],
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
				        "parameters": [ { "$ref": "#/components/parameters/limit" } ],
				        "responses": { "200": { "description": "ok", "content": { "application/json": {
				          "schema": { "type": "array", "items": { "$ref": "#/components/schemas/Pet" } } } } } }
				      }
				    },
				    "/count": { "get": { "operationId": "countPets", "security": [],
				      "responses": { "200": { "$ref": "#/components/responses/Count" } } } },
				    "/pets/{petId}": {
				      "parameters": [
				        { "name": "petId", "in": "path", "required": true, "schema": { "type": "integer" } }
				      ],
				      "get": {
				        "operationId": "getPet",
				        "parameters": [ { "name": "verbose", "in": "query", "schema": { "type": "boolean" } } ],
				        "responses": { "200": { "description": "ok", "content": { "application/json": {
				          "schema": { "$ref": "#/components/schemas/Pet" } } } } }
				      }
				    }
				  },
				  "components": {
				    "schemas": {
				      "Pet": { "type": "object", "required": [ "name" ], "properties": {
				        "id": { "type": "integer", "format": "int64" },
				        "name": { "type": "string" },
				        "tag": %s } }
				    },
				    "parameters": {
				      "limit": { "name": "limit", "in": "query", "schema": { "type": "integer", "format": "int32" } }
				    },
				    "responses": {
				      "Count": { "description": "ok", "content": { "application/json": {
				        "schema": { "type": "integer", "format": "int32" } } } }
				    },
				    "securitySchemes": {
				      "api_key": { "type": "apiKey", "name": "X-Api-Key", "in": "header" }
				    }
				  }
				}
				""".formatted(version, tag);
	}

	private HttpServer server;
	private final AtomicReference<String> lastMethod = new AtomicReference<>();
	private final AtomicReference<String> lastPathAndQuery = new AtomicReference<>();
	private final AtomicReference<String> lastApiKey = new AtomicReference<>();
	private final AtomicReference<String> lastBody = new AtomicReference<>();
	private final AtomicReference<Integer> status = new AtomicReference<>(200);
	private final AtomicReference<String> responseBody = new AtomicReference<>(PET_JSON);

	@BeforeEach
	void setUp() throws IOException {
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/", exchange -> {
			lastMethod.set(exchange.getRequestMethod());
			lastPathAndQuery.set(exchange.getRequestURI().toString());
			lastApiKey.set(exchange.getRequestHeaders().getFirst("X-Api-Key"));
			lastBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			byte[] body = responseBody.get().getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
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

	private OpenApiServiceClient client(OpenApiModel model) {
		return new OpenApiServiceClient(URI.create("http://localhost:" + server.getAddress().getPort()), model)
				.withAuth("api_key", OpenApiAuth.apiKey("secret"));
	}

	private static OpenApiModel importVersion(String version) {
		return OpenApiImporter.fromJson(petstore(version).getBytes(StandardCharsets.UTF_8));
	}

	private static List<Tuple> view(OpenApiModel model) {
		return model.operations().stream()
				.map(op -> Tuple.tuple(op.name(), op.httpMethod(), op.pathTemplate(),
						op.requestType() == null ? null : op.requestType().getName(),
						op.responseType() == null ? null : op.responseType().getName(),
						op.responseMany(), op.responseDataType(), op.security()))
				.toList();
	}

	@Test
	@DisplayName("3.0, 3.1 and 3.2 import to the same operations, without diagnostics")
	void sameOperations() {
		OpenApiModel v30 = importVersion("3.0.3");
		OpenApiModel v31 = importVersion("3.1.0");
		OpenApiModel v32 = importVersion("3.2.0");

		assertThat(v30.diagnostics()).isEmpty();
		assertThat(v31.diagnostics()).isEmpty();
		assertThat(v32.diagnostics()).isEmpty();
		assertThat(view(v31)).isEqualTo(view(v30));
		assertThat(view(v32)).isEqualTo(view(v30));

		OpenApiOperation getPet = v31.operation("getPet");
		// the path item's parameter is folded in with the operation's own
		assertThat(getPet.requestType().getEStructuralFeature("petId")).isNotNull();
		assertThat(getPet.requestType().getEStructuralFeature("verbose")).isNotNull();
		// a referenced parameter and a referenced response resolve
		assertThat(v31.operation("listPets").requestType().getEStructuralFeature("limit")).isNotNull();
		assertThat(v31.operation("countPets").responseDataType()).isNotNull();
		// security: [] lifts the global requirement
		assertThat(v31.operation("countPets").security()).isEmpty();
		assertThat(v31.operation("listPets").security()).containsExactly(Map.of("api_key", List.of()));
	}

	@ParameterizedTest(name = "OpenAPI {0}")
	@ValueSource(strings = { "3.0.3", "3.1.0", "3.2.0" })
	@DisplayName("objects, arrays, scalars, parameters and auth are called the same way")
	void callsTheSameWay(String version) {
		OpenApiModel model = importVersion(version);
		try (OpenApiServiceClient client = client(model)) {
			EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
			EObject rex = EcoreUtil.create(pet);
			rex.eSet(pet.getEStructuralFeature("name"), "Rex");

			EObject created = client.invoke("createPet", rex);
			assertThat(lastMethod.get()).isEqualTo("POST");
			assertThat(lastApiKey.get()).isEqualTo("secret");
			assertThat(lastBody.get()).contains("\"name\"").contains("Rex").doesNotContain("_type");
			assertThat(created.eClass()).isSameAs(pet);
			assertThat(created.eGet(pet.getEStructuralFeature("name"))).isEqualTo("Rex");
			assertThat(created.eGet(pet.getEStructuralFeature("tag"))).isNull();

			EClass getPet = model.operation("getPet").requestType();
			EObject request = EcoreUtil.create(getPet);
			request.eSet(getPet.getEStructuralFeature("petId"), 42L);
			request.eSet(getPet.getEStructuralFeature("verbose"), true);
			assertThat(client.invoke("getPet", request).eClass()).isSameAs(pet);
			assertThat(lastPathAndQuery.get()).isEqualTo("/pets/42?verbose=true");

			responseBody.set("[" + PET_JSON + "," + PET_JSON + "]");
			EClass listPets = model.operation("listPets").requestType();
			EObject limit = EcoreUtil.create(listPets);
			limit.eSet(listPets.getEStructuralFeature("limit"), 2);
			assertThat(client.invokeMany("listPets", limit)).hasSize(2).allSatisfy(p -> assertThat(p.eClass()).isSameAs(pet));
			assertThat(lastPathAndQuery.get()).isEqualTo("/pets?limit=2");

			responseBody.set("7");
			lastApiKey.set(null);
			assertThat(client.invokeValue("countPets", null)).isEqualTo(7);
			assertThat(lastApiKey.get()).as("security: [] sends no credentials").isNull();

			status.set(404);
			responseBody.set("{\"message\":\"Pet not found\"}");
			assertThatThrownBy(() -> client.invoke("getPet", request))
					.isInstanceOfSatisfying(HttpStatusException.class, e -> assertThat(e.status()).isEqualTo(404));
		}
	}

	/**
	 * Constructs only OpenAPI 3.1 (JSON Schema 2020-12) has: type arrays instead of
	 * {@code nullable}, {@code const}, exclusive bounds as numbers, {@code examples}, a
	 * {@code $ref} with sibling keywords — plus inline (unnamed) schemas for body and answer, and
	 * a webhook.
	 */
	private static final String OAS31 = """
			{
			  "openapi": "3.1.0",
			  "info": { "title": "Measurements", "version": "2.0" },
			  "jsonSchemaDialect": "https://json-schema.org/draft/2020-12/schema",
			  "paths": {
			    "/measurements": {
			      "post": {
			        "operationId": "record",
			        "requestBody": { "content": { "application/json": { "schema": {
			          "type": "object",
			          "required": [ "sensor", "value" ],
			          "properties": {
			            "sensor": { "$ref": "#/components/schemas/Sensor", "description": "where it was measured" },
			            "value": { "type": "number", "exclusiveMinimum": 0, "exclusiveMaximum": 1000 },
			            "unit": { "const": "celsius" },
			            "note": { "type": [ "string", "null" ], "examples": [ "calibrated" ] }
			          } } } } },
			        "responses": { "201": { "description": "recorded", "content": { "application/json": { "schema": {
			          "type": "object",
			          "properties": {
			            "id": { "type": "string" },
			            "accepted": { "type": [ "boolean", "null" ] }
			          } } } } } }
			      }
			    }
			  },
			  "webhooks": {
			    "alarm": { "post": { "requestBody": { "content": { "application/json": {
			      "schema": { "$ref": "#/components/schemas/Sensor" } } } },
			      "responses": { "200": { "description": "ok" } } } }
			  },
			  "components": { "schemas": {
			    "Sensor": { "type": "object", "properties": {
			      "name": { "type": "string" },
			      "floor": { "type": [ "integer", "null" ], "minimum": 0 } } }
			  } }
			}
			""";

	@Test
	@DisplayName("3.1-only constructs: inline body/answer get the codec's classifiers and round-trip (#102)")
	void oas31Constructs() {
		OpenApiModel model = OpenApiImporter.fromJson(OAS31.getBytes(StandardCharsets.UTF_8));

		assertThat(model.diagnostics()).singleElement().asString().contains("webhooks");
		OpenApiOperation record = model.operation("record");
		EClass body = record.requestType();
		EClass answer = record.responseType();
		assertThat(body).as("inline request body schema").isNotNull();
		assertThat(answer).as("inline response schema").isNotNull();
		// the classes are the codec's: the inline ones live in the schemas package's sub-package
		assertThat(body.getEPackage().getESuperPackage()).isSameAs(model.schemasPackage());
		assertThat(answer.getEPackage().getESuperPackage()).isSameAs(model.schemasPackage());
		EClass sensor = (EClass) model.schemasPackage().getEClassifier("Sensor");
		assertThat(body.getEStructuralFeature("sensor").getEType()).isSameAs(sensor);

		responseBody.set("{\"id\":\"m-1\",\"accepted\":null}");
		try (OpenApiServiceClient client = new OpenApiServiceClient(
				URI.create("http://localhost:" + server.getAddress().getPort()), model)) {
			EObject measurement = EcoreUtil.create(body);
			EObject kitchen = EcoreUtil.create(sensor);
			kitchen.eSet(sensor.getEStructuralFeature("name"), "kitchen");
			measurement.eSet(body.getEStructuralFeature("sensor"), kitchen);
			measurement.eSet(body.getEStructuralFeature("value"), 21.5);

			EObject recorded = client.invoke("record", measurement);

			assertThat(lastMethod.get()).isEqualTo("POST");
			assertThat(lastBody.get()).contains("kitchen").contains("21.5").doesNotContain("_type");
			assertThat(recorded.eClass()).isSameAs(answer);
			assertThat(recorded.eGet(answer.getEStructuralFeature("id"))).isEqualTo("m-1");
			assertThat(recorded.eGet(answer.getEStructuralFeature("accepted"))).isNull();
		}
	}
}
