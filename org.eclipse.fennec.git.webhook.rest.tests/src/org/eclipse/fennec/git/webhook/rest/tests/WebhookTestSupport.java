/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.git.webhook.rest.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.fennec.git.webhook.model.gitwebhook.WebhookPayload;
import org.osgi.service.jakartars.runtime.JakartarsServiceRuntime;
import org.osgi.service.jakartars.runtime.dto.ApplicationDTO;
import org.osgi.service.jakartars.runtime.dto.FailedResourceDTO;
import org.osgi.service.jakartars.runtime.dto.ResourceDTO;
import org.osgi.service.jakartars.runtime.dto.RuntimeDTO;
import org.osgi.service.typedevent.TypedEventHandler;

/**
 * Shared plumbing for the webhook endpoint integration tests: the base URL of the test
 * whiteboard, an HTTP POST helper, provider signature computation, fixture loading and the
 * whiteboard-deployment guard.
 *
 * <p>The tests deliberately go over HTTP through the real Jakarta RS whiteboard instead of
 * calling the resource method directly. Three defects found in production were invisible to
 * a direct call: a resource dropped from the application because its
 * {@code osgi.jakartars.extension.select} used the wrong property name, a signature filter
 * that registered no service at all (so it never became a whiteboard extension), and the
 * codec resolving JSON keys by EMF feature name instead of the ExtendedMetaData name the
 * providers actually send.
 */
final class WebhookTestSupport {

	/** Matches {@code configs/config.json} (port + {@code jersey.context.path=rest}). */
	static final String BASE_URL = "http://localhost:8199/rest";

	/** Shared secret configured for the GitHub endpoint in {@code configs/config.json}. */
	static final String GITHUB_SECRET = "test-secret";

	/** Shared token configured for the GitLab endpoint in {@code configs/config.json}. */
	static final String GITLAB_TOKEN = "test-token";

	private static final HttpClient CLIENT = HttpClient.newHttpClient();

	private WebhookTestSupport() {
		// static use
	}

	/**
	 * Reads a captured provider delivery from the bundle. Fixtures are real payloads, so the
	 * test pins the providers' wire format rather than our idea of it.
	 */
	static String payload(String name) throws IOException {
		try (InputStream in = WebhookTestSupport.class.getResourceAsStream("/payloads/" + name)) {
			if (in == null) {
				fail("Missing test fixture payloads/" + name);
			}
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	/** POSTs {@code body} to {@code path} with the given headers ({@code name, value, ...}). */
	static HttpResponse<String> post(String path, String body, String... headers) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(BASE_URL + path))
				.header("Content-Type", "application/json")
				.POST(BodyPublishers.ofString(body, StandardCharsets.UTF_8));
		for (int i = 0; i + 1 < headers.length; i += 2) {
			builder.header(headers[i], headers[i + 1]);
		}
		return CLIENT.send(builder.build(), BodyHandlers.ofString());
	}

	/**
	 * GitHub's {@code X-Hub-Signature-256}: {@code sha256=} + HMAC-SHA256 of the exact body
	 * bytes. Computed independently of production code so the test verifies the contract
	 * rather than mirroring the implementation.
	 */
	static String githubSignature(String body) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(GITHUB_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
		return "sha256=" + HexFormat.of().formatHex(mac.doFinal(body.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Waits until the named resource is part of the whiteboard's default application, and
	 * fails with the whiteboard's own reason when it is not.
	 *
	 * <p>Without this, a mis-wired resource shows up only as an opaque 404 from a request,
	 * which says nothing about whether the route is missing, the application is broken or
	 * the payload was rejected. A {@code failureReason} of 5
	 * ({@code FAILURE_REASON_REQUIRED_EXTENSIONS_UNAVAILABLE}) means the resource asked for
	 * an extension the whiteboard could not supply — the signature filter, either because its
	 * {@code osgi.jakartars.extension.select} does not match or because the filter component
	 * registers no service to match against. A 4
	 * ({@code FAILURE_REASON_NOT_AN_EXTENSION_TYPE}) means the filter is registered under a
	 * type the whiteboard does not accept as an extension.
	 */
	static void awaitResourceDeployed(JakartarsServiceRuntime runtime, String resourceName) throws Exception {
		long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20);
		RuntimeDTO dto = null;
		while (System.currentTimeMillis() < deadline) {
			dto = runtime.getRuntimeDTO();
			if (isDeployed(dto, resourceName)) {
				assertThat(dto.failedResourceDTOs)
						.as("resources rejected by the whiteboard")
						.extracting(WebhookTestSupport::describe)
						.isEmpty();
				return;
			}
			Thread.sleep(100L);
		}
		fail("Resource " + resourceName + " was not deployed within 20s. Failed resources: "
				+ (dto == null ? "<no runtime DTO>" : Arrays.toString(
						Arrays.stream(dto.failedResourceDTOs).map(WebhookTestSupport::describe).toArray())));
	}

	/**
	 * Waits until the endpoint actually answers, i.e. stops returning 404.
	 *
	 * <p>{@link #awaitResourceDeployed} is not sufficient on its own: the whiteboard lists a
	 * resource in its DTO before the Jersey container is serving it, and it rebuilds the
	 * application again as the remaining extensions (the codec's readers and filters) arrive.
	 * A request issued in that window gets a transient 404 that has nothing to do with the
	 * behaviour under test.
	 *
	 * <p>{@code probe} must be side-effect free — a non-push event, which every provider
	 * endpoint acknowledges without delivering anything to the event bus.
	 */
	static void awaitEndpointReady(Callable<HttpResponse<String>> probe) throws Exception {
		long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20);
		int status = -1;
		while (System.currentTimeMillis() < deadline) {
			status = probe.call().statusCode();
			if (status != 404) {
				return;
			}
			Thread.sleep(100L);
		}
		fail("Endpoint still answered 404 after 20s (last status " + status + ")");
	}

	private static boolean isDeployed(RuntimeDTO dto, String resourceName) {
		if (dto == null) {
			return false;
		}
		if (contains(dto.defaultApplication, resourceName)) {
			return true;
		}
		for (ApplicationDTO app : dto.applicationDTOs) {
			if (contains(app, resourceName)) {
				return true;
			}
		}
		return false;
	}

	private static boolean contains(ApplicationDTO app, String resourceName) {
		if (app == null || app.resourceDTOs == null) {
			return false;
		}
		for (ResourceDTO resource : app.resourceDTOs) {
			if (resourceName.equals(resource.name)) {
				return true;
			}
		}
		return false;
	}

	private static String describe(FailedResourceDTO failed) {
		return failed.name + " failureReason=" + failed.failureReason;
	}

	/**
	 * Captures the payloads delivered on the subscribed topic. A named class (not a lambda)
	 * so the typed-event bus can read the {@link WebhookPayload} type argument.
	 */
	static final class CapturingHandler implements TypedEventHandler<WebhookPayload> {

		private final LinkedBlockingQueue<Delivery> deliveries = new LinkedBlockingQueue<>();

		@Override
		public void notify(String topic, WebhookPayload event) {
			deliveries.add(new Delivery(topic, event));
		}

		/** Waits for one delivery, or {@code null} when none arrives in time. */
		Delivery poll(long timeout, TimeUnit unit) throws InterruptedException {
			return deliveries.poll(timeout, unit);
		}

		int size() {
			return deliveries.size();
		}
	}

	/** One captured delivery. */
	record Delivery(String topic, WebhookPayload payload) {
	}
}
