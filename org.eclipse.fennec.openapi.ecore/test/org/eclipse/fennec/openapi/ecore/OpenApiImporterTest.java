/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.openapi.ecore;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.openapi.ApiKeyLocation;
import org.eclipse.fennec.model.openapi.SecurityScheme;
import org.eclipse.fennec.model.openapi.SecuritySchemeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("OpenApiImporter: OpenAPI 3 -> operations + EClasses")
class OpenApiImporterTest {

	/** A small petstore: POST /pets (body only) and GET /pets/{petId} (path + query params). */
	static final String DOC = """
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

	private static OpenApiModel importDoc() {
		return OpenApiImporter.fromJson(DOC.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	@DisplayName("schemas become EClasses via the codec pipeline (with an nsURI ensured)")
	void schemasPackage() {
		OpenApiModel model = importDoc();

		assertThat(model.schemasPackage()).isNotNull();
		assertThat(model.schemasPackage().getNsURI()).isNotBlank();
		EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
		assertThat(pet).isNotNull();
		assertThat(pet.getEStructuralFeature("name")).isNotNull();
	}

	@Test
	@DisplayName("a body-only operation uses the body EClass as its request type")
	void bodyOnlyOperation() {
		OpenApiOperation create = importDoc().operation("createPet");

		assertThat(create).isNotNull();
		assertThat(create.httpMethod()).isEqualTo("POST");
		assertThat(create.pathTemplate()).isEqualTo("/pets");
		assertThat(create.requestType().getName()).isEqualTo("Pet");   // no synthetic wrapper
		assertThat(create.responseType().getName()).isEqualTo("Pet");
		assertThat(create.responseMany()).isFalse();
	}

	@Test
	@DisplayName("parameters fold into a synthetic request EClass with in= annotations")
	void parameterOperation() {
		OpenApiOperation get = importDoc().operation("getPet");

		EClass request = get.requestType();
		assertThat(request.getName()).isEqualTo("GetPetRequest");
		assertThat(request.getEPackage()).isSameAs(importDoc().requestsPackage() == null
				? request.getEPackage() : request.getEPackage()); // synthetic package holds it

		EStructuralFeature petId = request.getEStructuralFeature("petId");
		assertThat(petId.getEType().getName()).isEqualTo("ELong");
		assertThat(petId.getLowerBound()).isEqualTo(1);              // required
		assertThat(OpenApiAnnotations.in(petId)).isEqualTo("path");

		EStructuralFeature verbose = request.getEStructuralFeature("verbose");
		assertThat(verbose.getEType().getName()).isEqualTo("EBoolean");
		assertThat(verbose.getLowerBound()).isZero();
		assertThat(OpenApiAnnotations.in(verbose)).isEqualTo("query");

		assertThat(get.responseType().getName()).isEqualTo("Pet");
	}

	@Test
	@DisplayName("an array response resolves to its item EClass with responseMany")
	void arrayResponse() {
		OpenApiOperation list = importDoc().operation("listPets");

		assertThat(list.requestType()).isNull();
		assertThat(list.responseType().getName()).isEqualTo("Pet");
		assertThat(list.responseMany()).isTrue();
	}

	@Test
	@DisplayName("re-wires schema-to-schema $refs the converter leaves untyped")
	void repairsDanglingSchemaRefs() {
		String doc = """
				{
				  "openapi": "3.0.3",
				  "info": { "title": "Refs", "version": "1.0" },
				  "paths": {},
				  "components": { "schemas": {
				    "Pet": { "type": "object", "properties": {
				      "name": { "type": "string" },
				      "category": { "$ref": "#/components/schemas/Category" },
				      "tags": { "type": "array", "items": { "$ref": "#/components/schemas/Tag" } } } },
				    "Category": { "type": "object", "properties": { "label": { "type": "string" } } },
				    "Tag": { "type": "object", "properties": { "label": { "type": "string" } } }
				  } }
				}
				""";
		OpenApiModel model = OpenApiImporter.fromJson(doc.getBytes(StandardCharsets.UTF_8));

		EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
		// without the repair these eTypes are null (the converter records the ref only as an annotation)
		assertThat(pet.getEStructuralFeature("category").getEType())
				.isSameAs(model.schemasPackage().getEClassifier("Category"));
		assertThat(pet.getEStructuralFeature("tags").getEType())
				.isSameAs(model.schemasPackage().getEClassifier("Tag"));
	}

	@Test
	@DisplayName("securitySchemes + global/per-operation security requirements are imported")
	void securitySchemes() {
		String doc = """
				{
				  "openapi": "3.0.3",
				  "info": { "title": "Secured", "version": "1.0" },
				  "security": [ { "api_key": [] } ],
				  "paths": {
				    "/pets": {
				      "get": {
				        "operationId": "listPets",
				        "responses": { "200": { "description": "ok" } }
				      },
				      "post": {
				        "operationId": "createPet",
				        "security": [ { "petstore_auth": [ "write:pets" ] }, { "api_key": [] } ],
				        "responses": { "200": { "description": "ok" } }
				      }
				    }
				  },
				  "components": { "securitySchemes": {
				    "api_key": { "type": "apiKey", "name": "X-Api-Key", "in": "header" },
				    "petstore_auth": { "type": "oauth2", "flows": { "clientCredentials": {
				      "tokenUrl": "https://auth.example.org/token",
				      "scopes": { "write:pets": "modify pets" } } } }
				  } }
				}
				""";
		OpenApiModel model = OpenApiImporter.fromJson(doc.getBytes(StandardCharsets.UTF_8));

		assertThat(model.securitySchemes()).containsOnlyKeys("api_key", "petstore_auth");
		SecurityScheme apiKey = model.securityScheme("api_key");
		assertThat(apiKey.getType()).isEqualTo(SecuritySchemeType.API_KEY);
		assertThat(apiKey.getName()).isEqualTo("X-Api-Key");
		assertThat(apiKey.getIn()).isEqualTo(ApiKeyLocation.HEADER);
		SecurityScheme oauth = model.securityScheme("petstore_auth");
		assertThat(oauth.getType()).isEqualTo(SecuritySchemeType.OAUTH2);
		assertThat(oauth.getFlows().getClientCredentials().getTokenUrl())
				.isEqualTo("https://auth.example.org/token");

		// no own security -> inherits the global requirement
		assertThat(model.operation("listPets").security())
				.containsExactly(Map.of("api_key", List.of()));
		// own security overrides, alternatives + scopes preserved
		assertThat(model.operation("createPet").security()).containsExactly(
				Map.of("petstore_auth", List.of("write:pets")),
				Map.of("api_key", List.of()));
	}

	@Test
	@DisplayName("a requirement referencing an undeclared scheme yields a diagnostic")
	void undeclaredSchemeDiagnostic() {
		String doc = """
				{
				  "openapi": "3.0.3",
				  "info": { "title": "Broken", "version": "1.0" },
				  "security": [ { "ghost": [] } ],
				  "paths": {}
				}
				""";
		OpenApiModel model = OpenApiImporter.fromJson(doc.getBytes(StandardCharsets.UTF_8));

		assertThat(model.securitySchemes()).isEmpty();
		assertThat(model.diagnostics()).anyMatch(d -> d.contains("ghost"));
	}

	@Test
	@DisplayName("a body plus parameters yields a synthetic request with a body containment")
	void bodyPlusParameters() {
		String doc = DOC.replace("\"operationId\": \"createPet\",", """
				"operationId": "createPet",
				"parameters": [ { "name": "dryRun", "in": "query", "schema": { "type": "boolean" } } ],
				""");
		OpenApiOperation create = OpenApiImporter.fromJson(doc.getBytes(StandardCharsets.UTF_8)).operation("createPet");

		EClass request = create.requestType();
		assertThat(request.getName()).isEqualTo("CreatePetRequest");
		EReference body = (EReference) request.getEStructuralFeature("body");
		assertThat(body.isContainment()).isTrue();
		assertThat(body.getEType().getName()).isEqualTo("Pet");
		assertThat(OpenApiAnnotations.in(body)).isEqualTo("body");
	}
}
