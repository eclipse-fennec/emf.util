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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;

/**
 * Shared behavior of the per-provider webhook verification filters
 * ({@link GithubWebhookSignatureFilter}, {@link GitlabWebhookSignatureFilter}):
 * request abort/acknowledge helpers, the fail-closed handling of a missing
 * secret, and constant-time secret comparison.
 *
 * <p>Each provider has its own filter component gated on its own configuration
 * PID, so a deployment can expose the GitHub endpoint, the GitLab endpoint,
 * both, or neither — the matching resource is gated on the same PID and
 * additionally requires its filter via {@code osgi.jakartars.extension.select},
 * so an endpoint is never served without its verification filter.
 *
 * @author Data In Motion
 * @since 1.0
 */
abstract class AbstractWebhookSignatureFilter implements ContainerRequestFilter {

	/** Upper bound on the body read away from a rejected delivery (1 MiB). */
	private static final long DRAIN_LIMIT = 1024L * 1024L;

	/**
	 * Handles a request whose provider secret is not configured: rejected with
	 * 401 when {@code requireSignature} is set (fail-closed), let through
	 * otherwise (trusted setups only).
	 */
	protected final void requireOrSkip(ContainerRequestContext ctx, String provider, boolean requireSignature) {
		if (requireSignature) {
			abort(ctx, Response.Status.UNAUTHORIZED, provider + " webhook secret is not configured");
		}
		// else: verification intentionally disabled — let the request through.
	}

	protected final void acknowledgeNonPush(ContainerRequestContext ctx, String event) {
		// Not a push (e.g. GitHub's 'ping' handshake): acknowledge, do not process.
		drainEntity(ctx);
		ctx.abortWith(Response.ok("Ignored non-push event: " + event).build());
	}

	protected final void abort(ContainerRequestContext ctx, Response.Status status, String message) {
		drainEntity(ctx);
		ctx.abortWith(Response.status(status).entity(message).build());
	}

	/**
	 * Reads the request body away before the request is aborted from the filter.
	 *
	 * <p>Aborting leaves the entity unread. The container cannot leave a half-read request
	 * on the wire, so it closes the connection once the response is written — after the
	 * keep-alive response headers have already been flushed. Providers deliver over reused
	 * connections, so the next delivery can go out on a socket the server is closing and
	 * dies with an EOF before any response byte. Draining keeps the connection reusable;
	 * the bytes are discarded either way.
	 *
	 * <p>Capped at {@value #DRAIN_LIMIT} bytes: a rejected delivery is not worth reading an
	 * arbitrarily large body for, and dropping the connection is the right answer to a
	 * client that sends one.
	 */
	private static void drainEntity(ContainerRequestContext ctx) {
		if (!ctx.hasEntity()) {
			return;
		}
		try (InputStream entity = ctx.getEntityStream()) {
			byte[] buffer = new byte[8192];
			for (long drained = 0; drained < DRAIN_LIMIT;) {
				int read = entity.read(buffer);
				if (read < 0) {
					break;
				}
				drained += read;
			}
		} catch (IOException e) {
			// The request is being rejected anyway; an unreadable body changes nothing.
		}
	}

	protected static boolean constantTimeEquals(String expected, String provided) {
		return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),
				provided.getBytes(StandardCharsets.UTF_8));
	}
}
