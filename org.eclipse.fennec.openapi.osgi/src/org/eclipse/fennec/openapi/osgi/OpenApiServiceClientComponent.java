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
package org.eclipse.fennec.openapi.osgi;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.openapi.client.OpenApiAuth;
import org.eclipse.fennec.openapi.client.OpenApiServiceClient;
import org.eclipse.fennec.openapi.ecore.OpenApiImporter;
import org.eclipse.fennec.openapi.ecore.OpenApiModel;
import org.eclipse.fennec.service.api.ServiceClient;
import org.eclipse.fennec.service.api.ServiceOperation;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Publishes a configuration-driven OpenAPI {@link ServiceClient} as an OSGi service. This is the
 * client counterpart of the {@code Resource.Factory} components in the Protobuf/SOAP {@code .osgi}
 * bundles: instead of a resource factory it registers a ready-to-invoke {@code ServiceClient}, one
 * per ConfigurationAdmin factory configuration.
 * <p>
 * On activation the component fetches the OpenAPI 3 document named by {@link Config#documentUrl()},
 * imports it via {@link OpenApiImporter}, and builds an {@link OpenApiServiceClient} bound to the
 * framework's shared {@link MetadataWhiteboard}. Because the importer stamps the generated
 * {@code schemas}/{@code requests} packages with <em>constant</em> nsURIs and the whiteboard keys
 * package metadata by nsURI, the component first rewrites those nsURIs to be unique to this
 * configuration ({@link #isolate}) — otherwise a second OpenAPI client would silently reuse the
 * first document's metadata. The packages are unregistered again on deactivation.
 * <p>
 * Credentials are supplied through {@link Config#auth()} (see its documentation for the syntax).
 * The published service is DS-managed; consumers obtain it via a service reference and must
 * <b>not</b> {@link #close()} it — its lifecycle follows the configuration.
 */
@Component(name = "OpenApiServiceClient", service = ServiceClient.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = OpenApiServiceClientComponent.Config.class, factory = true)
public class OpenApiServiceClientComponent implements ServiceClient {

	/**
	 * Configuration for a single OpenAPI service client.
	 */
	@ObjectClassDefinition(name = "Fennec OpenAPI Service Client",
			description = "A ServiceClient that invokes the operations of one OpenAPI 3 document over HTTP.")
	public @interface Config {

		@AttributeDefinition(name = "Name", required = false,
				description = "Stable, human-chosen name for this client. Published as the 'name'"
						+ " service property (ServiceClient.PROP_NAME) for consumers to select or label"
						+ " the client, and used instead of the service.pid as the namespace-isolation"
						+ " token, keeping the generated nsURIs stable across restarts. Must be unique"
						+ " among clients sharing a MetadataWhiteboard.")
		String name() default "";

		@AttributeDefinition(name = "Document URL",
				description = "URL the OpenAPI 3 document is fetched from (http, https or file).")
		String documentUrl();

		@AttributeDefinition(name = "Base URI",
				description = "Base URI of the target service, e.g. https://api.example.com/v1.")
		String baseUri();

		@AttributeDefinition(name = "Document format", description = "Format of the document: json or yaml.")
		String format() default "json";

		@AttributeDefinition(name = "Authentication", required = false,
				description = "Credentials, one entry per declared security scheme, in the form"
						+ " '<scheme>=<type>:<params>'. Leave <scheme> empty for a document with a single"
						+ " scheme. Types: 'apiKey:<key>', 'bearer:<token>', 'basic:<user>:<password>',"
						+ " 'clientCredentials:<clientId>:<clientSecret>'.")
		String[] auth() default {};
	}

	@Reference
	private MetadataWhiteboard metadata;

	private volatile OpenApiServiceClient delegate;
	private final List<EPackage> registered = new ArrayList<>();

	@Activate
	void activate(Config config, Map<String, Object> properties) {
		byte[] document = fetch(config.documentUrl());
		OpenApiModel model = "yaml".equalsIgnoreCase(config.format())
				? OpenApiImporter.fromYaml(document)
				: OpenApiImporter.fromJson(document);

		isolate(model, isolationToken(config, properties));

		OpenApiServiceClient client = new OpenApiServiceClient(URI.create(config.baseUri()), model, metadata);
		applyAuth(client, config.auth());
		this.delegate = client;
	}

	@Deactivate
	void deactivate() {
		OpenApiServiceClient client = this.delegate;
		this.delegate = null;
		if (client != null) {
			client.close();
		}
		for (EPackage ePackage : registered) {
			metadata.unregisterPackage(ePackage);
		}
		registered.clear();
	}

	// --- ServiceClient (delegation) --------------------------------------------------------------

	@Override
	public List<? extends ServiceOperation> operations() {
		return delegate.operations();
	}

	@Override
	public ServiceOperation operation(String name) {
		return delegate.operation(name);
	}

	@Override
	public EObject invoke(ServiceOperation operation, EObject request) {
		return delegate.invoke(operation, request);
	}

	@Override
	public <T> Optional<T> unwrap(Class<T> nativeType) {
		return delegate.unwrap(nativeType);
	}

	/** No-op: this service is DS-managed; its lifecycle follows the configuration, not the caller. */
	@Override
	public void close() {
		// intentionally empty — see class documentation
	}

	// --- helpers ---------------------------------------------------------------------------------

	/**
	 * Rewrites the generated packages' nsURIs to be unique to this configuration, so that clients
	 * sharing the framework {@link MetadataWhiteboard} do not collide on the importer's constant
	 * nsURIs. The rewritten packages are recorded for unregistration on deactivation.
	 */
	private void isolate(OpenApiModel model, String token) {
		reNamespace(model.schemasPackage(), token);
		reNamespace(model.requestsPackage(), token);
	}

	/**
	 * The configured {@link Config#name() name} if present — stable across restarts (a pid from a
	 * programmatically created factory configuration is not) — else the {@code service.pid}, else
	 * the document URL.
	 */
	private static String isolationToken(Config config, Map<String, Object> properties) {
		if (config.name() != null && !config.name().isBlank()) {
			return config.name();
		}
		Object pid = properties.get("service.pid");
		return pid != null ? pid.toString() : config.documentUrl();
	}

	private void reNamespace(EPackage ePackage, String token) {
		if (ePackage != null && ePackage.getNsURI() != null) {
			ePackage.setNsURI(ePackage.getNsURI() + "/" + token);
			registered.add(ePackage);
		}
	}

	private void applyAuth(OpenApiServiceClient client, String[] entries) {
		for (String entry : entries) {
			if (entry == null || entry.isBlank()) {
				continue;
			}
			int equals = entry.indexOf('=');
			if (equals < 0) {
				throw new IllegalArgumentException(
						"Bad auth entry (expected '<scheme>=<type>:<params>'): " + entry);
			}
			String scheme = entry.substring(0, equals).trim();
			OpenApiAuth credentials = parseAuth(entry.substring(equals + 1).trim());
			if (scheme.isEmpty()) {
				client.withAuth(credentials);
			} else {
				client.withAuth(scheme, credentials);
			}
		}
	}

	private OpenApiAuth parseAuth(String spec) {
		int colon = spec.indexOf(':');
		String type = colon < 0 ? spec : spec.substring(0, colon);
		String params = colon < 0 ? "" : spec.substring(colon + 1);
		switch (type) {
		case "apiKey":
			return OpenApiAuth.apiKey(params);
		case "bearer":
			return OpenApiAuth.bearer(params);
		case "basic":
			return OpenApiAuth.basic(before(params, spec), after(params, spec));
		case "clientCredentials":
			return OpenApiAuth.clientCredentials(before(params, spec), after(params, spec));
		default:
			throw new IllegalArgumentException("Unknown auth type '" + type
					+ "' — expected apiKey, bearer, basic or clientCredentials");
		}
	}

	private static String before(String params, String spec) {
		int colon = requireColon(params, spec);
		return params.substring(0, colon);
	}

	private static String after(String params, String spec) {
		int colon = requireColon(params, spec);
		return params.substring(colon + 1);
	}

	private static int requireColon(String params, String spec) {
		int colon = params.indexOf(':');
		if (colon < 0) {
			throw new IllegalArgumentException("Auth entry '" + spec
					+ "' needs two ':'-separated parameters (e.g. basic:<user>:<password>)");
		}
		return colon;
	}

	private static byte[] fetch(String documentUrl) {
		try (InputStream in = URI.create(documentUrl).toURL().openStream()) {
			return in.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException("Could not fetch the OpenAPI document from " + documentUrl, e);
		}
	}
}
