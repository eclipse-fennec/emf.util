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
package org.eclipse.fennec.openapi.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.service.api.ServiceClient;
import org.eclipse.fennec.service.api.ServiceOperation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import com.sun.net.httpserver.HttpServer;

/**
 * Proves that the {@code org.eclipse.fennec.openapi.osgi} bundle turns a ConfigurationAdmin
 * factory configuration into a working {@link ServiceClient} OSGi service. A single embedded
 * {@link HttpServer} serves both the OpenAPI document (fetched by the component on activation)
 * and the {@code ping} endpoint the client then calls — so this exercises the full framework
 * wiring: config → import → codec (de)serialization against the framework
 * {@code MetadataWhiteboard} → HTTP round-trip, all inside a running Felix.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("OpenAPI ServiceClient OSGi registration")
public class OpenApiServiceClientIntegrationTest {

	private static final String OPENAPI_DOCUMENT = """
			{
			  "openapi": "3.0.0",
			  "info": { "title": "Ping", "version": "1.0.0" },
			  "paths": {
			    "/ping": {
			      "get": {
			        "operationId": "ping",
			        "responses": {
			          "200": {
			            "description": "a pong",
			            "content": {
			              "application/json": {
			                "schema": { "$ref": "#/components/schemas/Pong" }
			              }
			            }
			          }
			        }
			      }
			    }
			  },
			  "components": {
			    "schemas": {
			      "Pong": {
			        "type": "object",
			        "properties": { "message": { "type": "string" } }
			      }
			    }
			  }
			}
			""";

	// The ServiceClient is DS-managed: consumers must not close it (its close() is a no-op), so the
	// obtained references are deliberately not wrapped in try-with-resources.
	@SuppressWarnings("resource")
	@Test
	@DisplayName("a factory config publishes a ServiceClient that invokes an operation over HTTP")
	void configPublishesInvokableClient(@InjectBundleContext BundleContext context,
			@InjectService(timeout = 5000) ConfigurationAdmin configurationAdmin) throws Exception {

		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/openapi.json", exchange -> respond(exchange, OPENAPI_DOCUMENT));
		server.createContext("/ping", exchange -> respond(exchange, "{\"message\":\"pong\"}"));
		server.start();
		int port = server.getAddress().getPort();

		Configuration configuration = configurationAdmin.createFactoryConfiguration("OpenApiServiceClient", "?");
		try {
			Dictionary<String, Object> properties = new Hashtable<>();
			properties.put("name", "ping-client");
			properties.put("documentUrl", "http://localhost:" + port + "/openapi.json");
			properties.put("baseUri", "http://localhost:" + port);
			properties.put("format", "json");
			configuration.update(properties);

			ServiceReference<ServiceClient> reference = awaitService(context, 10_000);
			assertNotNull(reference, "the configuration should publish a ServiceClient service");
			assertEquals("ping-client", reference.getProperty(ServiceClient.PROP_NAME),
					"the configured name should be published as the '" + ServiceClient.PROP_NAME
							+ "' service property");

			ServiceClient client = context.getService(reference);
			assertNotNull(client, "the ServiceClient service should be obtainable");

			ServiceOperation ping = client.operation("ping");
			assertNotNull(ping, "the imported document should expose the 'ping' operation");

			EObject response = client.invoke(ping, null);
			assertNotNull(response, "ping should return a Pong");
			Object message = response.eGet(response.eClass().getEStructuralFeature("message"));
			assertEquals("pong", message);

			String nsURI = response.eClass().getEPackage().getNsURI();
			assertTrue(nsURI.endsWith("/ping-client"),
					"the configured name should be the namespace-isolation token, but nsURI was " + nsURI);
		} finally {
			configuration.delete();
			server.stop(0);
		}
	}

	private static void respond(com.sun.net.httpserver.HttpExchange exchange, String body) throws IOException {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, bytes.length);
		try (OutputStream out = exchange.getResponseBody()) {
			out.write(bytes);
		}
	}

	@SuppressWarnings("resource") // the ServiceClient behind the reference is DS-managed; never closed here
	private static ServiceReference<ServiceClient> awaitService(BundleContext context, long timeoutMillis)
			throws InterruptedException {
		long deadline = System.currentTimeMillis() + timeoutMillis;
		while (System.currentTimeMillis() < deadline) {
			ServiceReference<ServiceClient> reference = context.getServiceReference(ServiceClient.class);
			if (reference != null && context.getService(reference) != null) {
				return reference;
			}
			Thread.sleep(100);
		}
		return null;
	}
}
