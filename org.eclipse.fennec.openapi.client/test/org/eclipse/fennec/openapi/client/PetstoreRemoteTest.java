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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.openapi.ecore.OpenApiOperation;
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
		model = OpenApiImporter.fromJson(fetchDocumentOrSkip());
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

		// the cross-schema $refs are repaired (Pet.category -> Category, Pet.tags -> Tag)
		EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
		assertThat(pet.getEStructuralFeature("category").getEType().getName()).isEqualTo("Category");
		assertThat(pet.getEStructuralFeature("tags").getEType().getName()).isEqualTo("Tag");
	}

	@Test
	@DisplayName("reads a seeded pet via the synthetic path-parameter request")
	void readsSeededPet() {
		OpenApiOperation getPet = (OpenApiOperation) model.operation("getPetById");
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)) {
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
	void createAndReadPet() {
		try (OpenApiServiceClient client = new OpenApiServiceClient(BASE, model)) {
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

	// --- helpers -----------------------------------------------------------------------------

	private static EObject petIdRequest(OpenApiOperation getPet, long id) {
		EObject request = EcoreUtil.create(getPet.requestType());
		request.eSet(getPet.requestType().getEStructuralFeature("petId"),
				coerce(getPet.requestType(), "petId", id));
		return request;
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

	private static byte[] fetchDocumentOrSkip() {
		try (HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
			HttpResponse<byte[]> response = http.send(
					HttpRequest.newBuilder(URI.create(BASE + "/openapi.json"))
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
