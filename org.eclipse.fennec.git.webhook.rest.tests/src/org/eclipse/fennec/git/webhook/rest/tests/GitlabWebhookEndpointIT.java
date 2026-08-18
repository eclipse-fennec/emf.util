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
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.GITLAB_TOKEN;
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.awaitEndpointReady;
import static org.eclipse.fennec.git.webhook.rest.tests.WebhookTestSupport.awaitResourceDeployed;
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
 * End-to-end test of the GitLab webhook endpoint. GitLab names the same concepts differently
 * ({@code project.path_with_namespace} rather than {@code repository.full_name}, a plain shared
 * token rather than an HMAC), so the mapping is exercised separately from GitHub's.
 */
@ExtendWith({ BundleContextExtension.class, ServiceExtension.class })
class GitlabWebhookEndpointIT {

	private static final String RESOURCE_NAME = "GitlabWebhookResource";
	private static final String PATH = "/gitlab/webhook";
	private static final String EVENT_HEADER = "X-Gitlab-Event";
	private static final String TOKEN_HEADER = "X-Gitlab-Token";
	private static final String PUSH_EVENT = "Push Hook";

	/** Project {@code acme/models} on {@code refs/heads/draft}, sanitized. */
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
		// A non-push hook is acknowledged without reaching the bus, so it is a safe probe.
		awaitEndpointReady(() -> post(PATH, "{\"object_kind\":\"tag_push\"}",
				EVENT_HEADER, "Tag Push Hook", TOKEN_HEADER, GITLAB_TOKEN));

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
	@DisplayName("A tokened GitLab push hook is parsed and published on the branch topic")
	void tokenedPushIsPublished() throws Exception {
		HttpResponse<String> response = post(PATH, payload("gitlab-push.json"),
				EVENT_HEADER, PUSH_EVENT,
				TOKEN_HEADER, GITLAB_TOKEN);

		assertThat(response.statusCode()).as("body: %s", response.body()).isEqualTo(200);

		Delivery delivery = handler.poll(10, TimeUnit.SECONDS);
		assertThat(delivery).as("delivery on topic %s", EXPECTED_TOPIC).isNotNull();
		assertThat(delivery.topic()).isEqualTo(EXPECTED_TOPIC);
		assertThat(delivery.payload().getRef()).isEqualTo("refs/heads/draft");
	}

	@Test
	@DisplayName("A wrong token is rejected, proving the filter is bound to the endpoint")
	void wrongTokenIsRejected() throws Exception {
		HttpResponse<String> response = post(PATH, payload("gitlab-push.json"),
				EVENT_HEADER, PUSH_EVENT,
				TOKEN_HEADER, "not-the-token");

		assertThat(response.statusCode()).isEqualTo(401);
		assertThat(handler.poll(1, TimeUnit.SECONDS)).isNull();
	}

	@Test
	@DisplayName("A non-push hook is acknowledged and not delivered")
	void nonPushHookIsAcknowledged() throws Exception {
		HttpResponse<String> response = post(PATH, "{\"object_kind\":\"tag_push\"}",
				EVENT_HEADER, "Tag Push Hook",
				TOKEN_HEADER, GITLAB_TOKEN);

		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(handler.poll(1, TimeUnit.SECONDS)).isNull();
	}

	@Test
	@DisplayName("A request without the event header is a bad request")
	void missingEventHeaderIsBadRequest() throws Exception {
		HttpResponse<String> response = post(PATH, payload("gitlab-push.json"), TOKEN_HEADER, GITLAB_TOKEN);

		assertThat(response.statusCode()).isEqualTo(400);
	}
}
