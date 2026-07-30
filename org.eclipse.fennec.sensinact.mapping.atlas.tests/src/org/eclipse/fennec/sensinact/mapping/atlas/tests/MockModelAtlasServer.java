/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping.atlas.tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Minimal in-process Model Atlas: one scope {@code iot} with an object registry
 * {@code mappings} (holding the weather ProviderMapping XMI) and a {@code schema}
 * registry (serving the dwd-weather EPackage as XMI). Serves exactly the REST surface
 * the atlas rest client touches for a read-only LAZY setup.
 */
public final class MockModelAtlasServer implements AutoCloseable {

	private static final String SCOPE_LIST = "{\"scopes\":[{\"name\":\"iot\"}]}";
	private static final String SCOPE_INFO = """
			{"name":"iot","registries":[
			  {"name":"mappings","type":"OTHER"},
			  {"name":"schema","type":"SCHEMA"}
			]}""";
	private static final String MAPPING_LIST = "{\"metadata\":[{\"objectId\":\"dwd-weather\"},{\"objectId\":\"dwd-weather-reports\"}]}";

	private final HttpServer server;
	private final byte[] mappingXmi;
	private final byte[] reportsMappingXmi;
	private final byte[] weatherEcore;

	public MockModelAtlasServer() throws IOException {
		mappingXmi = readResource("/data/WeatherProviderMapping.xmi");
		reportsMappingXmi = readResource("/data/WeatherReportsProviderMapping.xmi");
		weatherEcore = readResource("/data/dwd-weather.ecore");
		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/", this::handle);
		server.start();
	}

	public String baseUri() {
		return "http://localhost:" + server.getAddress().getPort();
	}

	@Override
	public void close() {
		server.stop(0);
	}

	private void handle(HttpExchange exchange) throws IOException {
		String path = exchange.getRequestURI().getPath();
		String query = exchange.getRequestURI().getQuery();
		switch (path) {
		case "/scopes" -> respond(exchange, "application/json", SCOPE_LIST.getBytes(StandardCharsets.UTF_8));
		case "/scopes/iot" -> respond(exchange, "application/json", SCOPE_INFO.getBytes(StandardCharsets.UTF_8));
		case "/iot/registries/mappings" ->
			respond(exchange, "application/json", MAPPING_LIST.getBytes(StandardCharsets.UTF_8));
		case "/iot/registries/mappings/content" -> {
			switch (String.valueOf(queryParam(query, "objectId"))) {
			case "dwd-weather" -> respond(exchange, "application/xmi", mappingXmi);
			case "dwd-weather-reports" -> respond(exchange, "application/xmi", reportsMappingXmi);
			default -> respond(exchange, 404);
			}
		}
		case "/iot/schema" -> {
			// metadata-first nsURI resolution (RemoteEPackageProvider.resolve): the client asks
			// for the authoritative origin before fetching content; 204 = not visible here
			if ("http://cdc.dwd.de/common/weather".equals(queryParam(query, "nsUri"))) {
				respond(exchange, "application/json",
						"{\"scope\":\"iot\",\"registry\":\"schema\",\"stage\":\"released\",\"version\":\"1.0.0\"}"
								.getBytes(StandardCharsets.UTF_8));
			} else {
				respond(exchange, 204);
			}
		}
		case "/iot/schema/content" -> {
			if ("http://cdc.dwd.de/common/weather".equals(queryParam(query, "nsUri"))) {
				respond(exchange, "application/xmi", weatherEcore);
			} else {
				respond(exchange, 404);
			}
		}
		default -> {
			System.err.println("[mock-atlas] 404 for " + exchange.getRequestMethod() + " " + path
					+ (query != null ? "?" + query : ""));
			respond(exchange, 404);
		}
		}
	}

	private void respond(HttpExchange exchange, String contentType, byte[] body) throws IOException {
		// ETag/304 revalidation is load-bearing: the client's cache returns the SAME EPackage
		// instance on 304, which keeps EClass identity stable across its resolution paths
		// (mapping proxy resolution vs. lazy nsURI resolve). The real atlas sends ETags too.
		String etag = "\"" + Integer.toHexString(java.util.Arrays.hashCode(body)) + "\"";
		String ifNoneMatch = exchange.getRequestHeaders().getFirst("If-None-Match");
		exchange.getResponseHeaders().set("ETag", etag);
		if (etag.equals(ifNoneMatch)) {
			exchange.sendResponseHeaders(304, -1);
			exchange.close();
			return;
		}
		exchange.getResponseHeaders().set("Content-Type", contentType);
		if ("HEAD".equals(exchange.getRequestMethod())) {
			exchange.sendResponseHeaders(200, -1);
			exchange.close();
			return;
		}
		exchange.sendResponseHeaders(200, body.length);
		try (OutputStream out = exchange.getResponseBody()) {
			out.write(body);
		}
	}

	private void respond(HttpExchange exchange, int status) throws IOException {
		exchange.sendResponseHeaders(status, -1);
		exchange.close();
	}

	/** {@link java.net.URI#getQuery()} is already decoded; values contain no {@code &}. */
	private static String queryParam(String query, String name) {
		if (query == null) {
			return null;
		}
		for (String pair : query.split("&")) {
			int eq = pair.indexOf('=');
			if (eq > 0 && name.equals(pair.substring(0, eq))) {
				return pair.substring(eq + 1);
			}
		}
		return null;
	}

	private static byte[] readResource(String name) throws IOException {
		try (InputStream in = MockModelAtlasServer.class.getResourceAsStream(name)) {
			if (in == null) {
				throw new IOException("Missing test resource " + name);
			}
			return in.readAllBytes();
		}
	}
}
