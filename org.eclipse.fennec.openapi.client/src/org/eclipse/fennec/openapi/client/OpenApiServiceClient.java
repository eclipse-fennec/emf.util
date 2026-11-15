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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.codec.constants.CodecOptions;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.resource.CodecResourceFactory;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.emf.osgi.metadata.MetadataWhiteboard;
import org.eclipse.fennec.model.oas.SecurityScheme;
import org.eclipse.fennec.openapi.ecore.OpenApiAnnotations;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.openapi.ecore.OpenApiOperation;
import org.eclipse.fennec.service.api.HttpStatusException;
import org.eclipse.fennec.service.api.ServiceClient;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.eclipse.fennec.service.api.ServiceOperation;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * A {@link ServiceClient} that invokes OpenAPI operations over HTTP. The request is one
 * {@link EObject}: either the JSON body itself (body-only operations) or the importer's
 * synthetic request EClass, whose {@code in=path|query|header|cookie|body} feature annotations
 * drive URL templating, query string, headers and body. JSON payloads are (de)serialized
 * through the Fennec codec; the transport is the JDK {@link HttpClient}.
 * <p>
 * Request-response, {@code application/json}. An HTTP error status ({@code >= 400}) is thrown as
 * {@link HttpStatusException} carrying the status and the response body. {@link #invoke} answers a single object; an
 * operation whose answer is a {@linkplain OpenApiOperation#responseMany() JSON array} is invoked
 * through {@link #invokeMany}, which answers the elements in order; one whose answer is a scalar
 * ({@link OpenApiOperation#responseDataType()}) through {@link #invokeValue}.
 * Authentication: register {@link OpenApiAuth} credentials per declared scheme via
 * {@link #withAuth(String, OpenApiAuth)} (or {@link #withAuth(OpenApiAuth)} when the document
 * declares exactly one scheme) — applied per operation according to its effective security
 * requirements. Escape hatches: {@link #withHeader} for static headers, {@link #unwrap(Class)}
 * for the native client.
 */
public final class OpenApiServiceClient implements ServiceClient {

	private static final JsonMapper JSON = JsonMapper.builder().build();

	private final java.net.URI baseUri;
	private final OpenApiModel model;
	private final CodecResourceFactory codecFactory;
	private final HttpClient http;
	private final Map<String, String> defaultHeaders = new LinkedHashMap<>();
	private final Map<String, OpenApiAuth> auth = new LinkedHashMap<>();

	/**
	 * Creates a client backed by a private, isolated {@link MetadataWhiteboard} — the right
	 * choice for plain-Java use, where each client owns its own metadata registry.
	 */
	public OpenApiServiceClient(java.net.URI baseUri, OpenApiModel model) {
		this(baseUri, model, MetadataServiceFactory.create());
	}

	/**
	 * Creates a client backed by the supplied {@link MetadataWhiteboard} — e.g. the one an OSGi
	 * runtime provides as a shared service. The model's generated {@code schemas}/{@code requests}
	 * {@link org.eclipse.emf.ecore.EPackage EPackage}s are registered into it.
	 * <p>
	 * <b>Caller contract when the whiteboard is shared:</b> the importer stamps the generated
	 * packages with <em>constant</em> nsURIs, and {@code registerPackage} keys by nsURI — so two
	 * clients sharing one whiteboard must first give their generated packages nsURIs unique to the
	 * document, or the second silently (de)serializes against the first's metadata. The private
	 * two-arg constructor sidesteps this by never sharing.
	 */
	public OpenApiServiceClient(java.net.URI baseUri, OpenApiModel model, MetadataWhiteboard whiteboard) {
		this.baseUri = baseUri;
		this.model = model;
		model.generatedPackages().forEach(whiteboard::registerPackage);
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
		OpenApiOperation op = openApi(operation);
		if (op.responseDataType() != null) {
			throw new ServiceInvocationException("Operation '" + op.name()
					+ "' returns a scalar — invoke it via invokeValue(...)");
		}
		if (op.responseMany()) {
			throw new ServiceInvocationException("Operation '" + op.name()
					+ "' returns a JSON array — invoke it via invokeMany(...)");
		}
		byte[] body = send(op, request).body();
		if (op.responseType() == null || body.length == 0) {
			return null;
		}
		List<EObject> contents = unmarshal(body, op);
		return contents.isEmpty() ? null : contents.get(0);
	}

	/**
	 * Invokes an operation whose answer is a JSON array of objects
	 * ({@link OpenApiOperation#responseMany()}): each element is read with the operation's
	 * {@link OpenApiOperation#responseType() responseType} as root type, in order. An empty array
	 * (or an empty body) is an empty list.
	 *
	 * @throws ServiceInvocationException if the operation does not answer an array of objects,
	 *                                    or on transport, HTTP or deserialization failure
	 */
	public List<EObject> invokeMany(ServiceOperation operation, EObject request) {
		OpenApiOperation op = openApi(operation);
		if (op.responseDataType() != null) {
			throw new ServiceInvocationException("Operation '" + op.name()
					+ "' returns " + (op.responseMany() ? "an array of scalars" : "a scalar")
					+ " — invoke it via invokeValue(...)");
		}
		if (!op.responseMany() || op.responseType() == null) {
			throw new ServiceInvocationException("Operation '" + op.name()
					+ "' does not return a JSON array of objects — invoke it via invoke(...)");
		}
		byte[] body = send(op, request).body();
		return body.length == 0 ? List.of() : unmarshal(body, op);
	}

	/**
	 * Invokes an operation whose answer is a scalar ({@link OpenApiOperation#responseDataType()}):
	 * the value typed by the schema — {@link String}, {@link Integer}/{@link Long},
	 * {@link Float}/{@link Double}, {@link Boolean} — read from JSON, or from plain text when the
	 * answer is not JSON (also when it is labelled JSON but does not parse). For an array of scalars ({@link OpenApiOperation#responseMany()}) the
	 * answer is a {@code List} of such values, in order. An empty body is {@code null} (an empty
	 * list for an array).
	 *
	 * @throws ServiceInvocationException if the operation does not answer a scalar, or on
	 *                                    transport, HTTP or conversion failure
	 */
	public Object invokeValue(ServiceOperation operation, EObject request) {
		OpenApiOperation op = openApi(operation);
		EDataType type = op.responseDataType();
		if (type == null) {
			throw new ServiceInvocationException("Operation '" + op.name() + "' does not return a scalar — invoke it via "
					+ (op.responseMany() ? "invokeMany(...)" : "invoke(...)"));
		}
		HttpResponse<byte[]> response = send(op, request);
		byte[] body = response.body();
		if (body.length == 0) {
			return op.responseMany() ? List.of() : null;
		}
		String text = new String(body, StandardCharsets.UTF_8);
		if (op.responseMany()) {
			JsonNode json = parseJson(op, text);
			if (!json.isArray()) {
				throw new ServiceInvocationException("Operation '" + op.name() + "' should answer a JSON array, got: "
						+ snippet(body));
			}
			List<Object> values = new ArrayList<>();
			for (JsonNode element : json.values()) {
				values.add(scalar(op, type, element));
			}
			return values;
		}
		// servers label plain text as JSON (the Swagger petstore's loginUser does): not parseable → text
		JsonNode json = isJson(response) ? parseJsonOrNull(text) : null;
		return json == null ? convert(op, type, text) : scalar(op, type, json);
	}

	/** Convenience: {@link #invokeValue(ServiceOperation, EObject)} by operation name. */
	public Object invokeValue(String operationName, EObject request) {
		return invokeValue(operationNamed(operationName), request);
	}

	private static boolean isJson(HttpResponse<?> response) {
		return response.headers().firstValue("Content-Type").map(type -> type.toLowerCase().contains("json"))
				.orElse(false);
	}

	private static JsonNode parseJson(OpenApiOperation op, String text) {
		try {
			return JSON.readTree(text);
		} catch (JacksonException e) {
			throw new ServiceInvocationException("Could not parse the JSON answer of " + op.name(), e);
		}
	}

	private static JsonNode parseJsonOrNull(String text) {
		try {
			return JSON.readTree(text);
		} catch (JacksonException e) {
			return null;
		}
	}

	/** A JSON value converted to the data type; JSON {@code null} is {@code null}. */
	private static Object scalar(OpenApiOperation op, EDataType type, JsonNode node) {
		if (node.isNull()) {
			return null;
		}
		if (!node.isValueNode()) {
			throw new ServiceInvocationException("Operation '" + op.name() + "' should answer a "
					+ type.getName() + ", got: " + node);
		}
		return convert(op, type, node.isString() ? node.stringValue() : node.asString());
	}

	private static Object convert(OpenApiOperation op, EDataType type, String literal) {
		try {
			return EcoreUtil.createFromString(type, type.getInstanceClass() == String.class ? literal : literal.trim());
		} catch (RuntimeException e) {
			throw new ServiceInvocationException("Operation '" + op.name() + "' answered '" + literal
					+ "', which is no " + type.getName(), e);
		}
	}

	/** Convenience: {@link #invokeMany(ServiceOperation, EObject)} by operation name. */
	public List<EObject> invokeMany(String operationName, EObject request) {
		return invokeMany(operationNamed(operationName), request);
	}

	private OpenApiOperation operationNamed(String name) {
		ServiceOperation operation = operation(name);
		if (operation == null) {
			throw new ServiceInvocationException("No such operation: " + name);
		}
		return openApi(operation);
	}

	private static OpenApiOperation openApi(ServiceOperation operation) {
		if (!(operation instanceof OpenApiOperation op)) {
			throw new ServiceInvocationException("Not an OpenAPI operation: " + operation);
		}
		return op;
	}

	/** Sends the request and answers the response; an HTTP error status throws. */
	private HttpResponse<byte[]> send(OpenApiOperation op, EObject request) {
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
			throw new HttpStatusException("Operation '" + op.name() + "' failed: HTTP "
					+ response.statusCode() + " — " + snippet(response.body()), op.name(), response.statusCode(),
					response.body(), response.headers().firstValue("Content-Type").orElse(null));
		}
		return response;
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
				List<String> values = values(request, feature);
				if (values.isEmpty()) {
					continue; // an empty array is not sent
				}
				String name = feature.getName();
				String style = OpenApiAnnotations.style(feature);
				Boolean explode = OpenApiAnnotations.explode(feature);
				switch (in) {
				case OpenApiAnnotations.IN_PATH -> path = path.replace("{" + name + "}",
						ParameterSerializer.path(name, values, style, explode));
				case OpenApiAnnotations.IN_QUERY -> query.append(query.isEmpty() ? "" : "&")
						.append(ParameterSerializer.query(name, values, style, explode));
				case OpenApiAnnotations.IN_HEADER -> headers.put(name, ParameterSerializer.header(name, values, style));
				case OpenApiAnnotations.IN_COOKIE -> headers.merge("Cookie",
						ParameterSerializer.cookie(name, values, style, explode), (a, b) -> a + "; " + b);
				default -> throw new ServiceInvocationException("Unknown parameter location: " + in);
				}
			}
			if (!synthetic) {
				body = request; // body-only operation: the request IS the body
			}
		}

		authenticate(op, target(headers, query));

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

	/** A parameter's value(s) as literals: one for a single-valued feature, each element for an array. */
	private static List<String> values(EObject request, EStructuralFeature feature) {
		Object value = request.eGet(feature);
		List<?> raw = feature.isMany() ? (List<?>) value : Collections.singletonList(value);
		List<String> literals = new ArrayList<>(raw.size());
		for (Object element : raw) {
			literals.add(feature.getEType() instanceof EDataType type
					? EcoreUtil.convertToString(type, element)
					: String.valueOf(element));
		}
		return literals;
	}

	// --- authentication -------------------------------------------------------------------------

	/**
	 * Applies the registered credentials an operation's effective security demands to a request
	 * the caller builds itself — exactly as {@link #invoke} does: the first requirement
	 * alternative whose schemes all have registered credentials wins (an empty alternative —
	 * anonymous allowed — matches trivially, writing nothing).
	 *
	 * @param operation the operation the request is for
	 * @param target    where the credentials are written
	 * @throws ServiceInvocationException if authentication is required but no alternative is
	 *                                    satisfiable with the registered credentials
	 */
	public void authenticate(ServiceOperation operation, OpenApiAuth.Target target) {
		OpenApiOperation op = openApi(operation);
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
				auth.get(requirement.getKey()).apply(requirement.getKey(), scheme, requirement.getValue(), target);
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

	/** Reads the body with the response type as root type; a JSON array yields one object per element. */
	private List<EObject> unmarshal(byte[] body, OpenApiOperation op) {
		Resource resource = codecFactory.createResource(URI.createURI("mem://response.json"));
		Map<String, Object> options = new LinkedHashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, op.responseType());
		try {
			resource.load(new ByteArrayInputStream(body), options);
		} catch (IOException e) {
			throw new ServiceInvocationException("Could not deserialize the response of " + op.name()
					+ " as " + op.responseType().getName(), e);
		}
		return new ArrayList<>(resource.getContents());
	}

	// --- misc ----------------------------------------------------------------------------------

	private static String encode(String value) {
		return ParameterSerializer.encodeQuery(value);
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
