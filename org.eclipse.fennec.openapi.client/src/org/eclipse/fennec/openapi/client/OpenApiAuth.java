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

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.eclipse.fennec.model.openapi.OAuthFlow;
import org.eclipse.fennec.model.openapi.SecurityScheme;
import org.eclipse.fennec.model.openapi.SecuritySchemeType;
import org.eclipse.fennec.service.api.ServiceInvocationException;

import tools.jackson.databind.json.JsonMapper;

/**
 * Credentials for the security schemes an OpenAPI document declares. Register on the
 * {@link OpenApiServiceClient#withAuth(String, OpenApiAuth) client} under the scheme's name;
 * the client applies them per operation according to its effective
 * {@linkplain org.eclipse.fennec.openapi.ecore.OpenApiOperation#security() security requirements}
 * — <b>where</b> the credential goes (header/query/cookie, parameter name, token URL) always
 * comes from the document's scheme declaration, only the secret comes from the caller.
 * <p>
 * v1 flavors: {@link #apiKey}, {@link #basic}, {@link #bearer(Supplier)} (bring-your-own token,
 * e.g. a pre-obtained OAuth2/OIDC token) and {@link #clientCredentials} (machine-to-machine
 * OAuth2, token fetched from the flow's {@code tokenUrl} and cached until expiry). The
 * interactive {@code authorization_code} flow is deliberately out of scope for a headless
 * client — obtain the token elsewhere and pass it via {@code bearer}.
 */
public abstract class OpenApiAuth {

	OpenApiAuth() {
	}

	/** Where the client lets a credential write itself onto the outgoing request. */
	interface Target {
		void header(String name, String value);

		void query(String name, String value);

		void cookie(String name, String value);

		HttpClient http();
	}

	/**
	 * Applies the credential to the outgoing request.
	 *
	 * @param schemeName the name the scheme is declared under (for error messages)
	 * @param scheme     the declared scheme the credential must satisfy
	 * @param scopes     the scopes the operation's security requirement demands
	 * @param target     sink for the header/query/cookie the credential decides on
	 */
	abstract void apply(String schemeName, SecurityScheme scheme, List<String> scopes, Target target);

	/** An API key, placed in the header, query parameter or cookie the scheme declares ({@code apiKey}). */
	public static OpenApiAuth apiKey(String key) {
		return new ApiKey(key);
	}

	/** HTTP Basic authentication ({@code http}/{@code basic}). */
	public static OpenApiAuth basic(String username, String password) {
		return new Basic(username, password);
	}

	/** A fixed bearer token ({@code http}/{@code bearer}, {@code oauth2}, {@code openIdConnect}). */
	public static OpenApiAuth bearer(String token) {
		return bearer(() -> token);
	}

	/**
	 * A bearer token asked from the supplier on every request — refresh stays with the caller
	 * ({@code http}/{@code bearer}, {@code oauth2}, {@code openIdConnect}).
	 */
	public static OpenApiAuth bearer(Supplier<String> token) {
		return new Bearer(token);
	}

	/**
	 * The OAuth2 {@code client_credentials} flow against the token URL the scheme's flow
	 * declares; the access token is cached and re-fetched shortly before {@code expires_in}
	 * runs out ({@code oauth2}).
	 */
	public static OpenApiAuth clientCredentials(String clientId, String clientSecret) {
		return new ClientCredentials(clientId, clientSecret);
	}

	// --- flavors --------------------------------------------------------------------------------

	private static final class ApiKey extends OpenApiAuth {
		private final String key;

		ApiKey(String key) {
			this.key = key;
		}

		@Override
		void apply(String schemeName, SecurityScheme scheme, List<String> scopes, Target target) {
			if (scheme.getType() != SecuritySchemeType.API_KEY) {
				throw mismatch("apiKey", schemeName, scheme);
			}
			String parameter = scheme.getName();
			if (parameter == null || parameter.isBlank()) {
				throw new ServiceInvocationException(
						"Security scheme '" + schemeName + "' declares no apiKey parameter name");
			}
			switch (scheme.getIn()) {
			case QUERY -> target.query(parameter, key);
			case COOKIE -> target.cookie(parameter, key);
			case HEADER -> target.header(parameter, key);
			}
		}
	}

	private static final class Basic extends OpenApiAuth {
		private final String username;
		private final String password;

		Basic(String username, String password) {
			this.username = username;
			this.password = password;
		}

		@Override
		void apply(String schemeName, SecurityScheme scheme, List<String> scopes, Target target) {
			if (scheme.getType() != SecuritySchemeType.HTTP || !"basic".equalsIgnoreCase(scheme.getScheme())) {
				throw mismatch("basic", schemeName, scheme);
			}
			target.header("Authorization", "Basic " + Base64.getEncoder()
					.encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8)));
		}
	}

	private static final class Bearer extends OpenApiAuth {
		private final Supplier<String> token;

		Bearer(Supplier<String> token) {
			this.token = token;
		}

		@Override
		void apply(String schemeName, SecurityScheme scheme, List<String> scopes, Target target) {
			boolean accepted = switch (scheme.getType()) {
			case HTTP -> "bearer".equalsIgnoreCase(scheme.getScheme());
			case OAUTH2, OPEN_ID_CONNECT -> true;
			case API_KEY -> false;
			};
			if (!accepted) {
				throw mismatch("bearer", schemeName, scheme);
			}
			target.header("Authorization", "Bearer " + token.get());
		}
	}

	private static final class ClientCredentials extends OpenApiAuth {
		private static final JsonMapper MAPPER = JsonMapper.builder().build();
		/** Refetch this many seconds before the token actually expires. */
		private static final long EXPIRY_MARGIN_SECONDS = 30;

		private final String clientId;
		private final String clientSecret;
		private String token;
		private long expiresAtNanos;

		ClientCredentials(String clientId, String clientSecret) {
			this.clientId = clientId;
			this.clientSecret = clientSecret;
		}

		@Override
		synchronized void apply(String schemeName, SecurityScheme scheme, List<String> scopes, Target target) {
			if (scheme.getType() != SecuritySchemeType.OAUTH2) {
				throw mismatch("clientCredentials", schemeName, scheme);
			}
			OAuthFlow flow = scheme.getFlows() == null ? null : scheme.getFlows().getClientCredentials();
			if (flow == null || flow.getTokenUrl() == null || flow.getTokenUrl().isBlank()) {
				throw new ServiceInvocationException("Security scheme '" + schemeName
						+ "' declares no clientCredentials flow with a tokenUrl");
			}
			if (token == null || System.nanoTime() >= expiresAtNanos) {
				fetch(schemeName, flow.getTokenUrl(), scopes, target.http());
			}
			target.header("Authorization", "Bearer " + token);
		}

		private void fetch(String schemeName, String tokenUrl, List<String> scopes, HttpClient http) {
			StringBuilder form = new StringBuilder("grant_type=client_credentials");
			if (!scopes.isEmpty()) {
				form.append("&scope=").append(URLEncoder.encode(String.join(" ", scopes), StandardCharsets.UTF_8));
			}
			HttpRequest request = HttpRequest.newBuilder(URI.create(tokenUrl))
					.header("Content-Type", "application/x-www-form-urlencoded")
					.header("Authorization", "Basic " + Base64.getEncoder()
							.encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)))
					.POST(BodyPublishers.ofString(form.toString()))
					.build();
			HttpResponse<byte[]> response;
			try {
				response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new ServiceInvocationException("Token request for scheme '" + schemeName + "' interrupted", e);
			} catch (IOException e) {
				throw new ServiceInvocationException("Token request for scheme '" + schemeName + "' failed", e);
			}
			if (response.statusCode() >= 400) {
				throw new ServiceInvocationException("Token request for scheme '" + schemeName + "' failed: HTTP "
						+ response.statusCode() + " — " + new String(response.body(), StandardCharsets.UTF_8));
			}
			Map<?, ?> body = MAPPER.readValue(response.body(), Map.class);
			Object accessToken = body.get("access_token");
			if (accessToken == null) {
				throw new ServiceInvocationException(
						"Token response for scheme '" + schemeName + "' carries no access_token");
			}
			token = accessToken.toString();
			expiresAtNanos = body.get("expires_in") instanceof Number expiresIn
					? System.nanoTime() + TimeUnit.SECONDS.toNanos(expiresIn.longValue() - EXPIRY_MARGIN_SECONDS)
					: Long.MAX_VALUE;
		}
	}

	private static ServiceInvocationException mismatch(String credentials, String schemeName, SecurityScheme scheme) {
		return new ServiceInvocationException("Credentials of type '" + credentials
				+ "' cannot satisfy security scheme '" + schemeName + "' (type "
				+ scheme.getType().getLiteral()
				+ (scheme.getScheme() == null ? "" : ", scheme " + scheme.getScheme()) + ")");
	}
}
