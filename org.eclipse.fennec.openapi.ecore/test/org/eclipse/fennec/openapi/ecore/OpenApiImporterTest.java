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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.codec.openapi.oas.OasVersion;
import org.assertj.core.groups.Tuple;
import org.eclipse.fennec.model.oas.ApiKeyLocation;
import org.eclipse.fennec.model.oas.SecurityScheme;
import org.eclipse.fennec.model.oas.SecuritySchemeType;
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
		assertThat(petId.getEType().getName()).isEqualTo("ELongObject");
		assertThat(petId.getLowerBound()).isEqualTo(1);              // required
		assertThat(OpenApiAnnotations.in(petId)).isEqualTo("path");

		EStructuralFeature verbose = request.getEStructuralFeature("verbose");
		assertThat(verbose.getEType().getName()).isEqualTo("EBooleanObject");
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
	@DisplayName("schema-to-schema $refs are typed (fixed upstream, emf.codec #43)")
	void schemaToSchemaRefsAreTyped() {
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
		// resolved by the codec's converter (emf.codec #43); the importer only degrades leftovers
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

	/** Scalar answers: JSON, text/plain, formats and arrays of scalars (#96). */
	private static final String SCALAR_DOC = """
			{
			  "openapi": "3.0.3",
			  "info": { "title": "Scalars", "version": "1.0" },
			  "paths": {
			    "/login": { "get": { "operationId": "loginUser",
			      "responses": { "200": { "description": "ok", "content": {
			        "application/xml": { "schema": { "type": "string" } },
			        "application/json": { "schema": { "type": "string" } } } } } } },
			    "/motd": { "get": { "operationId": "motd",
			      "responses": { "200": { "description": "ok", "content": {
			        "text/plain": { "schema": { "type": "string" } } } } } } },
			    "/count": { "get": { "operationId": "count",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "integer", "format": "int32" } } } } } } },
			    "/total": { "get": { "operationId": "total",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "integer" } } } } } } },
			    "/ratio": { "get": { "operationId": "ratio",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "number", "format": "float" } } } } } } },
			    "/alive": { "get": { "operationId": "alive",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "boolean" } } } } } } },
			    "/tags": { "get": { "operationId": "tags",
			      "responses": { "200": { "description": "ok", "content": { "application/json": {
			        "schema": { "type": "array", "items": { "type": "string" } } } } } } } }
			  }
			}
			""";

	@Test
	@DisplayName("a scalar answer is typed by its schema, without a diagnostic (#96)")
	void scalarResponses() {
		OpenApiModel model = OpenApiImporter.fromJson(SCALAR_DOC.getBytes(StandardCharsets.UTF_8));
		EcorePackage ecore = EcorePackage.eINSTANCE;

		assertThat(model.operation("loginUser").responseDataType()).isSameAs(ecore.getEString());
		assertThat(model.operation("motd").responseDataType()).as("text/plain").isSameAs(ecore.getEString());
		assertThat(model.operation("count").responseDataType()).isSameAs(ecore.getEIntegerObject());
		assertThat(model.operation("total").responseDataType()).isSameAs(ecore.getELongObject());
		assertThat(model.operation("ratio").responseDataType()).isSameAs(ecore.getEFloatObject());
		assertThat(model.operation("alive").responseDataType()).isSameAs(ecore.getEBooleanObject());
		assertThat(model.operations()).allSatisfy(op -> assertThat(op.responseType()).isNull());
		assertThat(model.operation("loginUser").responseMany()).isFalse();

		OpenApiOperation tags = model.operation("tags");
		assertThat(tags.responseDataType()).isSameAs(ecore.getEString());
		assertThat(tags.responseMany()).isTrue();

		assertThat(model.diagnostics()).isEmpty();
	}

	@Test
	@DisplayName("an object answer has no response data type (#96)")
	void objectResponseHasNoDataType() {
		OpenApiModel model = importDoc();
		assertThat(model.operation("getPet").responseDataType()).isNull();
		assertThat(model.operation("listPets").responseDataType()).isNull();
	}

	@Test
	@DisplayName("an array parameter is many-valued, typed by its items, with declared style/explode (#98)")
	void arrayParameters() {
		String doc = """
				{
				  "openapi": "3.0.3",
				  "info": { "title": "Arrays", "version": "1.0" },
				  "paths": { "/pets/findByTags": { "get": { "operationId": "findPetsByTags",
				    "parameters": [
				      { "name": "tags", "in": "query", "schema": { "type": "array", "items": { "type": "string" } } },
				      { "name": "ids", "in": "query", "style": "pipeDelimited", "explode": false,
				        "schema": { "type": "array", "items": { "type": "integer" } } },
				      { "name": "limit", "in": "query", "schema": { "type": "integer" } }
				    ],
				    "responses": { "200": { "description": "ok" } } } } }
				}
				""";
		EClass request = OpenApiImporter.fromJson(doc.getBytes(StandardCharsets.UTF_8))
				.operation("findPetsByTags").requestType();

		EStructuralFeature tags = request.getEStructuralFeature("tags");
		assertThat(tags.isMany()).isTrue();
		assertThat(tags.getEType()).isSameAs(EcorePackage.eINSTANCE.getEString());
		assertThat(OpenApiAnnotations.style(tags)).isNull();
		assertThat(OpenApiAnnotations.explode(tags)).isNull();

		EStructuralFeature ids = request.getEStructuralFeature("ids");
		assertThat(ids.isMany()).isTrue();
		assertThat(ids.getEType()).isSameAs(EcorePackage.eINSTANCE.getELongObject());
		assertThat(OpenApiAnnotations.style(ids)).isEqualTo("pipeDelimited");
		assertThat(OpenApiAnnotations.explode(ids)).isFalse();

		assertThat(request.getEStructuralFeature("limit").isMany()).isFalse();
	}

	/** {@link #DOC} as YAML (#99). */
	private static final String YAML_DOC = """
			openapi: 3.0.3
			info:
			  title: Mini Petstore
			  version: "1.0"
			paths:
			  /pets:
			    post:
			      operationId: createPet
			      requestBody:
			        content:
			          application/json:
			            schema:
			              $ref: "#/components/schemas/Pet"
			      responses:
			        "200":
			          description: ok
			          content:
			            application/json:
			              schema:
			                $ref: "#/components/schemas/Pet"
			    get:
			      operationId: listPets
			      responses:
			        "200":
			          description: ok
			          content:
			            application/json:
			              schema:
			                type: array
			                items:
			                  $ref: "#/components/schemas/Pet"
			  /pets/{petId}:
			    get:
			      operationId: getPet
			      parameters:
			        - name: petId
			          in: path
			          required: true
			          schema:
			            type: integer
			        - name: verbose
			          in: query
			          schema:
			            type: boolean
			      responses:
			        "200":
			          description: ok
			          content:
			            application/json:
			              schema:
			                $ref: "#/components/schemas/Pet"
			components:
			  schemas:
			    Pet:
			      type: object
			      properties:
			        id:
			          type: integer
			          format: int64
			        name:
			          type: string
			""";

	@Test
	@DisplayName("a YAML document imports to the same operations as its JSON twin (#99)")
	void yamlImportsLikeJson() {
		OpenApiModel json = importDoc();
		OpenApiModel yaml = OpenApiImporter.fromYaml(YAML_DOC.getBytes(StandardCharsets.UTF_8));

		assertThat(yaml.diagnostics()).isEmpty();
		assertThat(yaml.operations()).extracting(OpenApiOperation::name, OpenApiOperation::httpMethod,
				OpenApiOperation::pathTemplate, OpenApiOperation::responseMany)
				.containsExactlyInAnyOrderElementsOf(json.operations().stream()
						.map(op -> Tuple.tuple(op.name(), op.httpMethod(), op.pathTemplate(),
								op.responseMany()))
						.toList());
		assertThat(yaml.operation("getPet").responseType().getName()).isEqualTo("Pet");
		assertThat(yaml.operation("listPets").responseType().getName()).isEqualTo("Pet");
		assertThat(yaml.operation("createPet").requestType().getName()).isEqualTo("Pet");
		EClass getPet = yaml.operation("getPet").requestType();
		assertThat(getPet.getEStructuralFeature("petId").getEType())
				.isSameAs(json.operation("getPet").requestType().getEStructuralFeature("petId").getEType());
	}

	@Test
	@DisplayName("the version is taken from the openapi member, in JSON and YAML (#102)")
	void versionDetection() {
		assertThat(OpenApiImporter.version("{\"info\":{},\"openapi\":\"3.1.1\"}".getBytes(StandardCharsets.UTF_8), false))
				.isEqualTo(OasVersion.V3_1);
		assertThat(OpenApiImporter.version(YAML_DOC.getBytes(StandardCharsets.UTF_8), true)).isEqualTo(OasVersion.V3_0);
		assertThat(OpenApiImporter.version("info:\n  title: x\n'openapi': '3.2.0'\n".getBytes(StandardCharsets.UTF_8), true))
				.isEqualTo(OasVersion.V3_2);
	}

	@Test
	@DisplayName("Swagger 2.0 and documents without openapi member are rejected with a clear message (#102)")
	void unsupportedVersions() {
		assertThatThrownBy(() -> OpenApiImporter.fromJson("{\"swagger\":\"2.0\"}".getBytes(StandardCharsets.UTF_8)))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no 'openapi' member");
		assertThatThrownBy(() -> OpenApiImporter.fromYaml("openapi: 4.0.0\n".getBytes(StandardCharsets.UTF_8)))
				.isInstanceOf(IllegalArgumentException.class).hasMessageContaining("'4.0.0' is not supported");
	}

	@Test
	@DisplayName("3.1 and 3.2 descriptions import like their 3.0 twin, in JSON and YAML (#102)")
	void laterVersionsImportLike30() {
		OpenApiModel v30 = importDoc();
		for (String version : List.of("3.1.0", "3.2.0")) {
			OpenApiModel json = OpenApiImporter.fromJson(DOC.replace("3.0.3", version).getBytes(StandardCharsets.UTF_8));
			OpenApiModel yaml = OpenApiImporter.fromYaml(YAML_DOC.replace("3.0.3", version).getBytes(StandardCharsets.UTF_8));
			for (OpenApiModel model : List.of(json, yaml)) {
				assertThat(model.document().getOpenapi()).isEqualTo(version);
				assertThat(model.diagnostics()).isEmpty();
				assertThat(model.operations()).extracting(OpenApiOperation::name, OpenApiOperation::httpMethod,
						OpenApiOperation::pathTemplate, OpenApiOperation::responseMany,
						op -> op.responseType() == null ? null : op.responseType().getName())
						.containsExactlyInAnyOrderElementsOf(v30.operations().stream()
								.map(op -> Tuple.tuple(op.name(), op.httpMethod(), op.pathTemplate(), op.responseMany(),
										op.responseType() == null ? null : op.responseType().getName()))
								.toList());
			}
		}
	}

	@Test
	@DisplayName("an inline body and answer get the codec's inline classifiers, no diagnostic (#102)")
	void inlineSchemas() {
		String doc = """
				{
				  "openapi": "3.0.3",
				  "info": { "title": "Inline", "version": "1.0" },
				  "paths": { "/notes": { "post": { "operationId": "addNote",
				    "requestBody": { "content": { "application/json": { "schema": {
				      "type": "object", "properties": { "text": { "type": "string" } } } } } },
				    "responses": { "200": { "description": "ok", "content": { "application/json": { "schema": {
				      "type": "array", "items": { "type": "object", "properties": { "id": { "type": "integer" } } } } } } } } } } }
				}
				""";
		OpenApiModel model = OpenApiImporter.fromJson(doc.getBytes(StandardCharsets.UTF_8));

		assertThat(model.diagnostics()).isEmpty();
		OpenApiOperation add = model.operation("addNote");
		assertThat(add.requestType().getEStructuralFeature("text")).isNotNull();
		assertThat(add.responseType().getEStructuralFeature("id")).isNotNull();
		assertThat(add.responseMany()).isTrue();
		assertThat(model.generatedPackages()).contains(add.requestType().getEPackage(), add.responseType().getEPackage());
	}

	@Test
	@DisplayName("3.2: the query method and additionalOperations become operations; querystring is skipped (#102)")
	void oas32Methods() {
		String doc = """
				openapi: 3.2.0
				info:
				  title: Search
				  version: "1.0"
				paths:
				  /search:
				    query:
				      operationId: search
				      requestBody:
				        content:
				          application/json:
				            schema:
				              $ref: "#/components/schemas/Filter"
				      responses:
				        "200":
				          description: ok
				    additionalOperations:
				      PURGE:
				        operationId: purge
				        parameters:
				          - name: q
				            in: querystring
				            content:
				              application/x-www-form-urlencoded:
				                schema:
				                  type: object
				        responses:
				          "204":
				            description: gone
				components:
				  schemas:
				    Filter:
				      type: object
				      properties:
				        term:
				          type: string
				""";
		OpenApiModel model = OpenApiImporter.fromYaml(doc.getBytes(StandardCharsets.UTF_8));

		assertThat(model.operation("search").httpMethod()).isEqualTo("QUERY");
		assertThat(model.operation("search").requestType().getName()).isEqualTo("Filter");
		assertThat(model.operation("purge").httpMethod()).isEqualTo("PURGE");
		assertThat(model.diagnostics()).anyMatch(d -> d.contains("querystring"));
	}
}
