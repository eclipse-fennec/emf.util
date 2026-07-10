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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.resource.CodecResourceFactory;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.openapi.SecurityScheme;
import org.eclipse.fennec.openapi.ecore.OpenApiAnnotations;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.openapi.ecore.OpenApiOperation;
import org.eclipse.fennec.service.api.ServiceClient;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.eclipse.fennec.service.api.ServiceOperation;

/**
 * A {@link ServiceClient} that invokes OpenAPI operations over HTTP. The request is one
 * {@link EObject}: either the JSON body itself (body-only operations) or the importer's
 * synthetic request EClass, whose {@code in=path|query|header|cookie|body} feature annotations
 * drive URL templating, query string, headers and body. JSON payloads are (de)serialized
 * through the Fennec codec; the transport is the JDK {@link HttpClient}.
 * <p>
 * v1: request-response, {@code application/json}, single-object responses (an
 * {@linkplain OpenApiOperation#responseMany() array response} is rejected with a clear error).
 * Authentication: register {@link OpenApiAuth} credentials per declared scheme via
 * {@link #withAuth(String, OpenApiAuth)} (or {@link #withAuth(OpenApiAuth)} when the document
 * declares exactly one scheme) — applied per operation according to its effective security
 * requirements. Escape hatches: {@link #withHeader} for static headers, {@link #unwrap(Class)}
 * for the native client.
 */
public final class OpenApiServiceClient implements ServiceClient {

	private final java.net.URI baseUri;
	private final OpenApiModel model;
	private final CodecResourceFactory codecFactory;
	private final HttpClient http;
	private final Map<String, String> defaultHeaders = new LinkedHashMap<>();
	private final Map<String, OpenApiAuth> auth = new LinkedHashMap<>();

	public OpenApiServiceClient(java.net.URI baseUri, OpenApiModel model) {
		this.baseUri = baseUri;
		this.model = model;
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		if (model.schemasPackage() != null) {
			whiteboard.registerPackage(model.schemasPackage());
		}
		if (model.requestsPackage() != null) {
			whiteboard.registerPackage(model.requestsPackage());
		}
		this.codecFactory = new CodecResourceFactory(whiteboard);
		this.http = HttpClient.newHttpClient();
	}

	/** Adds a header sent with every request. */
	public OpenApiServiceClient withHeader(String name, String value) {
		defaultHeaders.put(name, value);
		return this;
	}

	/** Registers credentials for a security scheme the document declares. */
	public OpenApiServiceClient withAuth(String schemeName, OpenApiAuth credentials) {
		if (model.securityScheme(schemeName) == null) {
			throw new IllegalArgumentException("Unknown security scheme '" + schemeName
					+ "' — the document declares " + model.securitySchemes().keySet());
		}
		auth.put(schemeName, credentials);
		return this;
	}

	/** Convenience: registers credentials for the document's <b>single</b> declared scheme. */
	public OpenApiServiceClient withAuth(OpenApiAuth credentials) {
		if (model.securitySchemes().size() != 1) {
			throw new IllegalStateException("The document declares " + model.securitySchemes().keySet()
					+ " — name the scheme via withAuth(schemeName, credentials)");
		}
		return withAuth(model.securitySchemes().keySet().iterator().next(), credentials);
	}

	@Override
	public List<? extends ServiceOperation> operations() {
		return model.operations();
	}

	@Override
	public ServiceOperation operation(String name) {
		return model.operation(name);
	}

	@Override
	public EObject invoke(ServiceOperation operation, EObject request) {
		if (!(operation instanceof OpenApiOperation op)) {
			throw new ServiceInvocationException("Not an OpenAPI operation: " + operation);
		}
		if (op.responseMany()) {
			throw new ServiceInvocationException("Operation '" + op.name()
					+ "' returns a JSON array — not supported in v1 (single-object responses only)");
		}
		HttpRequest httpRequest = buildRequest(op, request);
		HttpResponse<byte[]> response;
		try {
			response = http.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServiceInvocationException("Call interrupted: " + op.name(), e);
		} catch (IOException e) {
			throw new ServiceInvocationException("Transport failed for " + op.name(), e);
		}
		if (response.statusCode() >= 400) {
			throw new ServiceInvocationException("Operation '" + op.name() + "' failed: HTTP "
					+ response.statusCode() + " — " + snippet(response.body()));
		}
		if (op.responseType() == null || response.body().length == 0) {
			return null;
		}
		return unmarshal(response.body(), op);
	}

	// --- request building --------------------------------------------------------------------

	private HttpRequest buildRequest(OpenApiOperation op, EObject request) {
		String path = op.pathTemplate();
		StringBuilder query = new StringBuilder();
		Map<String, String> headers = new LinkedHashMap<>(defaultHeaders);
		EObject body = null;

		if (request != null) {
			boolean synthetic = false;
			for (EStructuralFeature feature : request.eClass().getEAllStructuralFeatures()) {
				String in = OpenApiAnnotations.in(feature);
				if (in == null) {
					continue;
				}
				synthetic = true;
				if (OpenApiAnnotations.IN_BODY.equals(in)) {
					body = (EObject) request.eGet(feature);
					continue;
				}
				if (!request.eIsSet(feature) && feature.getLowerBound() == 0) {
					continue; // optional parameter not provided
				}
				String value = String.valueOf(request.eGet(feature));
				switch (in) {
				case OpenApiAnnotations.IN_PATH -> path = path.replace("{" + feature.getName() + "}", encode(value));
				case OpenApiAnnotations.IN_QUERY -> query.append(query.isEmpty() ? "" : "&")
						.append(encode(feature.getName())).append('=').append(encode(value));
				case OpenApiAnnotations.IN_HEADER -> headers.put(feature.getName(), value);
				case OpenApiAnnotations.IN_COOKIE -> headers.merge("Cookie",
						feature.getName() + "=" + value, (a, b) -> a + "; " + b);
				default -> throw new ServiceInvocationException("Unknown parameter location: " + in);
				}
			}
			if (!synthetic) {
				body = request; // body-only operation: the request IS the body
			}
		}

		applyAuth(op, headers, query);

		java.net.URI target = java.net.URI.create(baseUri.toString().replaceAll("/$", "") + path
				+ (query.isEmpty() ? "" : "?" + query));
		HttpRequest.Builder builder = HttpRequest.newBuilder(target);
		headers.forEach(builder::header);
		if (body != null) {
			builder.header("Content-Type", "application/json");
			builder.method(op.httpMethod(), BodyPublishers.ofByteArray(marshal(body)));
		} else {
			builder.method(op.httpMethod(), BodyPublishers.noBody());
		}
		return builder.build();
	}

	// --- authentication -------------------------------------------------------------------------

	/**
	 * Satisfies the operation's effective security: the first requirement alternative whose
	 * schemes all have registered credentials wins (an empty alternative — anonymous allowed —
	 * matches trivially). Required-but-unregistered credentials fail here, before any HTTP.
	 */
	private void applyAuth(OpenApiOperation op, Map<String, String> headers, StringBuilder query) {
		List<Map<String, List<String>>> alternatives = op.security();
		if (alternatives.isEmpty()) {
			return;
		}
		for (Map<String, List<String>> alternative : alternatives) {
			if (!auth.keySet().containsAll(alternative.keySet())) {
				continue;
			}
			for (Map.Entry<String, List<String>> requirement : alternative.entrySet()) {
				SecurityScheme scheme = model.securityScheme(requirement.getKey());
				if (scheme == null) {
					throw new ServiceInvocationException("Operation '" + op.name()
							+ "' requires undeclared security scheme '" + requirement.getKey() + "'");
				}
				auth.get(requirement.getKey()).apply(requirement.getKey(), scheme,
						requirement.getValue(), target(headers, query));
			}
			return;
		}
		throw new ServiceInvocationException("Operation '" + op.name() + "' requires authentication ("
				+ alternatives.stream().map(Map::keySet).toList()
				+ ") — register credentials via withAuth(...)");
	}

	private OpenApiAuth.Target target(Map<String, String> headers, StringBuilder query) {
		return new OpenApiAuth.Target() {
			@Override
			public void header(String name, String value) {
				headers.put(name, value);
			}

			@Override
			public void query(String name, String value) {
				query.append(query.isEmpty() ? "" : "&").append(encode(name)).append('=').append(encode(value));
			}

			@Override
			public void cookie(String name, String value) {
				headers.merge("Cookie", name + "=" + value, (a, b) -> a + "; " + b);
			}

			@Override
			public HttpClient http() {
				return http;
			}
		};
	}

	// --- JSON marshalling (codec) -------------------------------------------------------------

	private byte[] marshal(EObject body) {
		Resource resource = codecFactory.createResource(URI.createURI("mem://request.json"));
		resource.getContents().add(EcoreUtil.copy(body));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			// plain interop JSON: no EMF type discriminator on the wire
			resource.save(out, Map.of(CodecOptions.CODEC_TYPE_INCLUDE, false));
		} catch (IOException e) {
			throw new ServiceInvocationException("Could not serialize the request body", e);
		}
		return out.toByteArray();
	}

	private EObject unmarshal(byte[] body, OpenApiOperation op) {
		Resource resource = codecFactory.createResource(URI.createURI("mem://response.json"));
		Map<String, Object> options = new LinkedHashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, op.responseType());
		try {
			resource.load(new ByteArrayInputStream(body), options);
		} catch (IOException e) {
			throw new ServiceInvocationException("Could not deserialize the response of " + op.name()
					+ " as " + op.responseType().getName(), e);
		}
		return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
	}

	// --- misc ----------------------------------------------------------------------------------

	private static String encode(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	private static String snippet(byte[] body) {
		String text = new String(body, StandardCharsets.UTF_8);
		return text.length() > 200 ? text.substring(0, 200) + "…" : text;
	}

	@Override
	public <T> Optional<T> unwrap(Class<T> nativeType) {
		return nativeType.isInstance(http) ? Optional.of(nativeType.cast(http)) : Optional.empty();
	}

	@Override
	public void close() {
		http.close();
	}
}
