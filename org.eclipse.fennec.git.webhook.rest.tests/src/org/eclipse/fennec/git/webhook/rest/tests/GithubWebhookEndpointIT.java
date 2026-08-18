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
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.awaitEndpointReady;
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.awaitResourceDeployed;
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.githubSignature;
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.payload;
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.post;

import java.net.http.HttpResponse;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.CapturingHandler;
import org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.Delivery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.jakartars.runtime.JakartarsServiceRuntime;
import org.osgi.service.typedevent.TypedEventConstants;
import org.osgi.service.typedevent.TypedEventHandler;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * End-to-end test of the GitHub webhook endpoint: a captured GitHub push delivery is POSTed
 * over HTTP to the real Jakarta RS whiteboard, and the neutral payload is expected on the
 * typed-event topic derived from the repository and branch.
 *
 * <p>The topic is asserted as a literal ({@code fennec/git/webhook/acme_models/draft}) rather
 * than computed with {@code WebhookTopics}: subscribing to it and receiving anything at all
 * proves the resource resolved {@code full_name} out of the JSON, because the resource builds
 * the topic from {@code getRepositoryFullName()}. Computing the expected topic with the same
 * helper the production code uses would make the assertion agree with any sanitization change.
 */
@ExtendWith({ BundleContextExtension.class, ServiceExtension.class })
class GithubWebhookEndpointIT {

	private static final String RESOURCE_NAME = "GithubWebhookResource";
	private static final String PATH = "/github/webhook";
	private static final String EVENT_HEADER = "X-GitHub-Event";
	private static final String SIGNATURE_HEADER = "X-Hub-Signature-256";

	/** Repository {@code acme/models} on {@code refs/heads/draft}, sanitized. */
	private static final String EXPECTED_TOPIC = "fennec/git/webhook/acme_models/draft";

	@InjectService(cardinality = 0)
	ServiceAware<JakartarsServiceRuntime> runtimeAware;

	private CapturingHandler handler;
	private ServiceRegistration<?> handlerRegistration;

	@BeforeEach
	void subscribe(@InjectBundleContext BundleContext context) throws Exception {
		JakartarsServiceRuntime runtime = runtimeAware.waitForService(20_000L);
		assertThat(runtime).as("Jakarta RS whiteboard runtime").isNotNull();
		awaitResourceDeployed(runtime, RESOURCE_NAME);
		// A ping is acknowledged without reaching the bus, so it is a safe readiness probe.
		String ping = "{\"zen\":\"ready?\"}";
		awaitEndpointReady(() -> post(PATH, ping, EVENT_HEADER, "ping", SIGNATURE_HEADER, githubSignature(ping)));

		handler = new CapturingHandler();
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(TypedEventConstants.TYPED_EVENT_TOPICS, EXPECTED_TOPIC);
		handlerRegistration = context.registerService(TypedEventHandler.class, handler, properties);
	}

	@AfterEach
	void unsubscribe() {
		if (handlerRegistration != null) {
			handlerRegistration.unregister();
			handlerRegistration = null;
		}
	}

	@Test
	@DisplayName("A signed GitHub push delivery is parsed and published on the branch topic")
	void signedPushIsPublished() throws Exception {
		String body = payload("github-push.json");

		HttpResponse<String> response = post(PATH, body,
				EVENT_HEADER, "push",
				SIGNATURE_HEADER, githubSignature(body));

		assertThat(response.statusCode()).as("body: %s", response.body()).isEqualTo(200);

		Delivery delivery = handler.poll(10, TimeUnit.SECONDS);
		assertThat(delivery).as("delivery on topic %s", EXPECTED_TOPIC).isNotNull();
		assertThat(delivery.topic()).isEqualTo(EXPECTED_TOPIC);
		assertThat(delivery.payload().getRef()).isEqualTo("refs/heads/draft");
	}

	@Test
	@DisplayName("A wrong signature is rejected, proving the filter is bound to the endpoint")
	void wrongSignatureIsRejected() throws Exception {
		String body = payload("github-push.json");

		HttpResponse<String> response = post(PATH, body,
				EVENT_HEADER, "push",
				SIGNATURE_HEADER, "sha256=deadbeef");

		assertThat(response.statusCode()).isEqualTo(401);
		assertThat(handler.poll(1, TimeUnit.SECONDS)).as("no event on a rejected request").isNull();
	}

	@Test
	@DisplayName("A missing signature header is rejected while a secret is configured")
	void missingSignatureIsRejected() throws Exception {
		HttpResponse<String> response = post(PATH, payload("github-push.json"), EVENT_HEADER, "push");

		assertThat(response.statusCode()).isEqualTo(401);
		assertThat(handler.poll(1, TimeUnit.SECONDS)).isNull();
	}

	@Test
	@DisplayName("The ping handshake is acknowledged and not delivered")
	void pingIsAcknowledged() throws Exception {
		String body = "{\"zen\":\"Keep it logically awesome.\"}";

		HttpResponse<String> response = post(PATH, body,
				EVENT_HEADER, "ping",
				SIGNATURE_HEADER, githubSignature(body));

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(handler.poll(1, TimeUnit.SECONDS)).as("ping must not reach the bus").isNull();
	}

	@Test
	@DisplayName("A request without the event header is a bad request")
	void missingEventHeaderIsBadRequest() throws Exception {
		String body = payload("github-push.json");

		HttpResponse<String> response = post(PATH, body, SIGNATURE_HEADER, githubSignature(body));

		assertThat(response.statusCode()).isEqualTo(400);
	}

	@Test
	@DisplayName("A push without a repository is rejected before it reaches the bus")
	void pushWithoutRepositoryIsBadRequest() throws Exception {
		String body = "{\"ref\":\"refs/heads/draft\"}";

		HttpResponse<String> response = post(PATH, body,
				EVENT_HEADER, "push",
				SIGNATURE_HEADER, githubSignature(body));

		assertThat(response.statusCode()).isEqualTo(400);
		assertThat(handler.poll(1, TimeUnit.SECONDS)).isNull();
	}

	@Test
	@DisplayName("The EMF feature name is not accepted in place of the GitHub wire name")
	void camelCaseRepositoryNameIsNotAccepted() throws Exception {
		// Guards the codec option that makes the ExtendedMetaData names authoritative: with it
		// off, this body parsed and the real GitHub one (full_name) did not.
		String body = "{\"ref\":\"refs/heads/draft\",\"repository\":{\"fullName\":\"acme/models\"}}";

		HttpResponse<String> response = post(PATH, body,
				EVENT_HEADER, "push",
				SIGNATURE_HEADER, githubSignature(body));

		assertThat(response.statusCode()).isEqualTo(400);
	}
}
