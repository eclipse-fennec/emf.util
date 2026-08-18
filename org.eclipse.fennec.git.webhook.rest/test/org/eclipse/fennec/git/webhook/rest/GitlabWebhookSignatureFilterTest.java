/*
 * ******************************************************************
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
 * ******************************************************************
 */
package org.eclipse.fennec.git.webhook.rest;

import static org.eclipse.fennec.git.webhook.rest.GitlabWebhookSignatureFilter.GITLAB_EVENT_HEADER;
import static org.eclipse.fennec.git.webhook.rest.GitlabWebhookSignatureFilter.GITLAB_TOKEN_HEADER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

/**
 * Unit tests for {@link GitlabWebhookSignatureFilter}: token verification,
 * fail-closed behaviour and push-event gating. The GitLab check is header-only, so the
 * filter must not read the entity stream to decide — it only drains it once the request
 * is rejected, so the container can keep the connection alive.
 */
class GitlabWebhookSignatureFilterTest {

	private static final String TOKEN = "tok3n";

	@Test
	void validToken_passes() throws IOException {
		ContainerRequestContext ctx = gitlabCtx("Push Hook", TOKEN);
		filter(TOKEN, true).filter(ctx);
		verify(ctx, never()).abortWith(any());
		verify(ctx, never()).getEntityStream();
	}

	@Test
	void invalidToken_rejected401() throws IOException {
		ContainerRequestContext ctx = gitlabCtx("Push Hook", "wrong");
		filter(TOKEN, true).filter(ctx);
		assertEquals(401, capturedStatus(ctx));
	}

	@Test
	void missingToken_rejected401() throws IOException {
		ContainerRequestContext ctx = gitlabCtx("Push Hook", null);
		filter(TOKEN, true).filter(ctx);
		assertEquals(401, capturedStatus(ctx));
	}

	@Test
	void tokenNotConfigured_failClosed401() throws IOException {
		ContainerRequestContext ctx = gitlabCtx("Push Hook", TOKEN);
		filter("", true).filter(ctx);
		assertEquals(401, capturedStatus(ctx));
	}

	@Test
	void tokenNotConfigured_requireDisabled_passes() throws IOException {
		ContainerRequestContext ctx = gitlabCtx("Push Hook", null);
		filter("", false).filter(ctx);
		verify(ctx, never()).abortWith(any());
	}

	@Test
	void nonPushEvent_acknowledged200() throws IOException {
		ContainerRequestContext ctx = gitlabCtx("Tag Push Hook", TOKEN);
		filter(TOKEN, true).filter(ctx);
		assertEquals(200, capturedStatus(ctx));
	}

	@Test
	void rejectedRequest_bodyIsDrained() throws IOException {
		ContainerRequestContext ctx = gitlabCtx("Push Hook", "wrong");
		ByteArrayInputStream body = new ByteArrayInputStream(
				"{\"object_kind\":\"push\"}".getBytes(StandardCharsets.UTF_8));
		when(ctx.hasEntity()).thenReturn(true);
		when(ctx.getEntityStream()).thenReturn(body);

		filter(TOKEN, true).filter(ctx);

		assertEquals(401, capturedStatus(ctx));
		// A body left half-read makes the container close the connection after the response,
		// which costs the provider the next delivery pipelined onto it.
		assertEquals(0, body.available(), "rejected request left its body unread");
	}

	@Test
	void missingEventHeader_rejected400() throws IOException {
		ContainerRequestContext ctx = gitlabCtx(null, TOKEN);
		filter(TOKEN, true).filter(ctx);
		assertEquals(400, capturedStatus(ctx));
	}

	// --- helpers ------------------------------------------------------------

	private static GitlabWebhookSignatureFilter filter(String gitlabToken, boolean require) {
		GitlabWebhookSignatureFilter f = new GitlabWebhookSignatureFilter();
		f.activate(config(gitlabToken, require));
		return f;
	}

	private static ContainerRequestContext gitlabCtx(String event, String token) {
		ContainerRequestContext ctx = mock(ContainerRequestContext.class);
		when(ctx.getHeaderString(GITLAB_EVENT_HEADER)).thenReturn(event);
		when(ctx.getHeaderString(GITLAB_TOKEN_HEADER)).thenReturn(token);
		return ctx;
	}

	private static int capturedStatus(ContainerRequestContext ctx) {
		ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
		verify(ctx).abortWith(captor.capture());
		return captor.getValue().getStatus();
	}

	private static GitlabWebhookSignatureFilter.Config config(String gitlabToken, boolean require) {
		return new GitlabWebhookSignatureFilter.Config() {
			@Override
			public Class<? extends Annotation> annotationType() {
				return GitlabWebhookSignatureFilter.Config.class;
			}

			@Override
			public String gitlabToken() {
				return gitlabToken;
			}

			@Override
			public boolean requireSignature() {
				return require;
			}
		};
	}
}
