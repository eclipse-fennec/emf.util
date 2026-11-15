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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.openapi.ecore.OpenApiOperation;
import org.eclipse.fennec.service.api.HttpStatusException;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Validates the client design against the <b>public</b> Swagger petstore
 * ({@code petstore3.swagger.io}) — a real OpenAPI 3 service. Tagged {@code remote}: requires
 * internet + third-party uptime, excluded from the normal build — run via
 * {@code ./gradlew remoteTest}. Server-side outages are skipped (assumptions), not failed:
 * at the time of writing the public petstore's <em>write</em> path intermittently answers
 * HTTP 500 even for textbook-correct curl requests, while reads of the seeded pets work.
 */
@Tag("remote")
@DisplayName("OpenApiServiceClient against the public Swagger petstore")
class PetstoreRemoteTest {

	private static final URI BASE = URI.create("https://petstore3.swagger.io/api/v3");

	private static OpenApiModel model;

	@BeforeAll
	static void importLiveDocument() {
		model = OpenApiImporter.fromJson(fetchDocumentOrSkip("/openapi.json"));
	}

	@Test
	@DisplayName("the live document imports: schemas resolved, operations linked")
	void importsLiveDocument() {
		assertThat(model.schemasPackage().getEClassifier("Pet")).isNotNull();

		OpenApiOperation addPet = (OpenApiOperation) model.operation("addPet");
		assertThat(addPet.httpMethod()).isEqualTo("POST");
		assertThat(addPet.requestType().getName()).isEqualTo("Pet");     // body-only
		assertThat(addPet.responseType().getName()).isEqualTo("Pet");

		OpenApiOperation getPet = (OpenApiOperation) model.operation("getPetById");
		assertThat(getPet.requestType().getName()).isEqualTo("GetPetByIdRequest"); // synthetic
		assertThat(getPet.requestType().getEStructuralFeature("petId")).isNotNull();
		assertThat(getPet.responseType().getName()).isEqualTo("Pet");

		// security is imported: the petstore declares api_key + petstore_auth,
		// and getPetById accepts either (two requirement alternatives)
		assertThat(model.securitySchemes()).containsKeys("api_key", "petstore_auth");
		assertThat(getPet.security()).hasSize(2);

		// the cross-schema $refs are repaired (Pet.category -> Category, Pet.tags -> Tag)
		EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
		assertThat(pet.getEStructuralFeature("category").getEType().getName()).isEqualTo("Category");
		assertThat(pet.getEStructuralFeature("tags").getEType().getName()).isEqualTo("Tag");
	}

	@Test
	@DisplayName("reads a seeded pet via the synthetic path-parameter request (api_key auth)")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis; try-with-resources closes the client
	void readsSeededPet() {
		OpenApiOperation getPet = (OpenApiOperation) model.operation("getPetById");
		// getPetById requires api_key OR petstore_auth; "special-key" is the petstore's test key
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)
				.withAuth("api_key", OpenApiAuth.apiKey("special-key"))) {
			EObject pet = null;
			for (long id : List.of(1L, 2L, 3L)) {
				try {
					pet = client.invoke(getPet, petIdRequest(getPet, id));
					break;
				} catch (ServiceInvocationException e) {
					// seeded data is ephemeral; try the next id
				}
			}
			assumeTrue(pet != null, "no seeded pet readable right now");

			assertThat(pet.eClass().getName()).isEqualTo("Pet");
			assertThat(pet.eGet(pet.eClass().getEStructuralFeature("name"))).isNotNull();
		}
	}

	@Test
	@DisplayName("creates a pet and reads it back (skipped while the public write path is down)")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis; try-with-resources closes the client
	void createAndReadPet() {
		// addPet requires petstore_auth (oauth2 implicit — interactive); the petstore accepts any
		// bearer token, which exercises the bearer-on-oauth2 path against a real endpoint
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)
				.withAuth("api_key", OpenApiAuth.apiKey("special-key"))
				.withAuth("petstore_auth", OpenApiAuth.bearer("special-key"))) {
			EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
			EObject rex = EcoreUtil.create(pet);
			long id = System.currentTimeMillis() % 1_000_000_000L;
			rex.eSet(pet.getEStructuralFeature("id"), coerce(pet, "id", id));
			rex.eSet(pet.getEStructuralFeature("name"), "fennec-" + id);
			@SuppressWarnings("unchecked")
			List<Object> urls = (List<Object>) rex.eGet(pet.getEStructuralFeature("photoUrls"));
			urls.add("http://example.org/fennec.png");

			EObject created;
			try {
				created = client.invoke("addPet", rex);
			} catch (ServiceInvocationException e) {
				assumeTrue(!e.getMessage().contains("HTTP 5"),
						"petstore write path is down (500 even for plain curl): " + e.getMessage());
				throw e;
			}
			assertThat(created).isNotNull();
			assertThat(String.valueOf(created.eGet(created.eClass().getEStructuralFeature("name"))))
					.isEqualTo("fennec-" + id);

			OpenApiOperation getPet = (OpenApiOperation) model.operation("getPetById");
			EObject fetched = client.invoke(getPet, petIdRequest(getPet, id));
			assertThat(String.valueOf(fetched.eGet(fetched.eClass().getEStructuralFeature("name"))))
					.isEqualTo("fennec-" + id);
		}
	}

	@Test
	@DisplayName("the live YAML document imports to the same operations as the JSON one (#99)")
	void importsLiveYamlDocument() {
		OpenApiModel yaml = OpenApiImporter.fromYaml(fetchDocumentOrSkip("/openapi.yaml"));
		assertThat(yaml.operations()).extracting(OpenApiOperation::name)
				.containsExactlyInAnyOrderElementsOf(model.operations().stream().map(OpenApiOperation::name).toList());
		assertThat(yaml.operation("findPetsByStatus").responseMany()).isTrue();
	}

	@Test
	@DisplayName("findPetsByStatus answers a list of pets (#95)")
	@SuppressWarnings({ "resource", "unchecked" }) // fluent withAuth(...) chain trips ECJ's resource-leak analysis
	void findsPetsByStatus() {
		OpenApiOperation find = model.operation("findPetsByStatus");
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)
				.withAuth("api_key", OpenApiAuth.apiKey("special-key"))
				.withAuth("petstore_auth", OpenApiAuth.bearer("special-key"))) {
			EObject request = EcoreUtil.create(find.requestType());
			request.eSet(find.requestType().getEStructuralFeature("status"),
					coerceLiteral(find.requestType(), "status", "available"));
			List<EObject> pets = callOrSkip(() -> client.invokeMany(find, request));

			assertThat(pets).allSatisfy(pet -> assertThat(pet.eClass().getName()).isEqualTo("Pet"));
		}
	}

	@Test
	@DisplayName("findPetsByTags sends an array query parameter exploded (#98)")
	@SuppressWarnings({ "resource", "unchecked" }) // fluent withAuth(...) chain trips ECJ's resource-leak analysis
	void findsPetsByTags() {
		OpenApiOperation find = model.operation("findPetsByTags");
		assertThat(find.requestType().getEStructuralFeature("tags").isMany()).isTrue();
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)
				.withAuth("api_key", OpenApiAuth.apiKey("special-key"))
				.withAuth("petstore_auth", OpenApiAuth.bearer("special-key"))) {
			EObject request = EcoreUtil.create(find.requestType());
			((List<Object>) request.eGet(find.requestType().getEStructuralFeature("tags"))).addAll(List.of("tag1", "tag2"));
			List<EObject> pets = callOrSkip(() -> client.invokeMany(find, request));

			assertThat(pets).allSatisfy(pet -> assertThat(pet.eClass().getName()).isEqualTo("Pet"));
		}
	}

	@Test
	@DisplayName("loginUser answers a string (#96)")
	void logsIn() {
		OpenApiOperation login = model.operation("loginUser");
		assertThat(login.responseDataType()).isNotNull();
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)) {
			EObject request = EcoreUtil.create(login.requestType());
			request.eSet(login.requestType().getEStructuralFeature("username"), "fennec");
			request.eSet(login.requestType().getEStructuralFeature("password"), "fennec");
			Object session = callOrSkip(() -> client.invokeValue(login, request));

			assertThat(session).isInstanceOf(String.class);
			assertThat((String) session).isNotBlank();
		}
	}

	@Test
	@DisplayName("an unknown pet answers an HttpStatusException with status and body (#97)")
	@SuppressWarnings("resource") // fluent withAuth(...) chain trips ECJ's resource-leak analysis
	void unknownPetCarriesStatus() {
		OpenApiOperation getPet = model.operation("getPetById");
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)
				.withAuth("api_key", OpenApiAuth.apiKey("special-key"))) {
			HttpStatusException failure = null;
			try {
				client.invoke(getPet, petIdRequest(getPet, 987_654_321L));
			} catch (HttpStatusException e) {
				failure = e;
			}
			assumeTrue(failure != null, "pet 987654321 exists right now");
			assumeTrue(failure.status() < 500, "petstore is down: " + failure.getMessage());
			assertThat(failure.status()).isEqualTo(404);
			assertThat(failure.bodyText()).isNotEmpty();
		}
	}

	// --- helpers -----------------------------------------------------------------------------

	private static EObject petIdRequest(OpenApiOperation getPet, long id) {
		EObject request = EcoreUtil.create(getPet.requestType());
		request.eSet(getPet.requestType().getEStructuralFeature("petId"),
				coerce(getPet.requestType(), "petId", id));
		return request;
	}

	/** Runs a call; a server-side failure (HTTP 5xx) skips the test instead of failing it. */
	private static <T> T callOrSkip(Supplier<T> call) {
		try {
			return call.get();
		} catch (HttpStatusException e) {
			assumeTrue(e.status() < 500, "petstore is down: " + e.getMessage());
			throw e;
		}
	}

	/** An enum-typed feature takes its literal, anything else the string. */
	private static Object coerceLiteral(EClass type, String feature, String literal) {
		if (type.getEStructuralFeature(feature).getEType() instanceof EEnum eEnum) {
			return eEnum.getEEnumLiteralByLiteral(literal).getInstance();
		}
		return literal;
	}

	/** Coerces the id to whatever numeric type the generated feature uses. */
	private static Object coerce(EClass type, String feature, long id) {
		String typeName = type.getEStructuralFeature(feature).getEType().getName();
		return switch (typeName) {
		case "ELong", "ELongObject" -> id;
		case "EInt", "EIntegerObject" -> (int) id;
		case "EBigInteger" -> BigInteger.valueOf(id);
		default -> String.valueOf(id);
		};
	}

	private static byte[] fetchDocumentOrSkip(String path) {
		try (HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
			HttpResponse<byte[]> response = http.send(
					HttpRequest.newBuilder(URI.create(BASE + path))
							.timeout(Duration.ofSeconds(15)).GET().build(),
					HttpResponse.BodyHandlers.ofByteArray());
			assumeTrue(response.statusCode() == 200, "petstore returned HTTP " + response.statusCode());
			return response.body();
		} catch (Exception e) {
			assumeTrue(false, "petstore unreachable: " + e);
			throw new IllegalStateException("unreachable");
		}
	}
}
