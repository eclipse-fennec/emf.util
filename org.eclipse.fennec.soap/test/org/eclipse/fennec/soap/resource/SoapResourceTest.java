/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xmlsoap.schemas.envelope.EnvelopePackage;

/**
 * Exercises the {@link SoapResource} envelope plumbing without a payload schema (an empty
 * envelope round-trip and a SOAP Fault). The full import → payload → envelope round-trip is
 * an integration test in the {@code org.eclipse.fennec.soap.ecore} bundle, which depends on
 * both the importer and this runtime.
 */
@DisplayName("SoapResource: envelope plumbing")
class SoapResourceTest {

	private static ResourceSet newResourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(SoapResource.FILE_EXTENSION, new SoapResourceFactory());
		rs.getPackageRegistry().put(EnvelopePackage.eNS_URI, EnvelopePackage.eINSTANCE);
		return rs;
	}

	@Test
	@DisplayName("saves and loads an (empty) SOAP envelope")
	void emptyEnvelopeRoundTrips() throws IOException {
		Resource out = newResourceSet().createResource(URI.createURI("empty.soap"));
		assertThat(out).isInstanceOf(SoapResource.class);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		out.save(baos, null);
		String xml = baos.toString(StandardCharsets.UTF_8);
		assertThat(xml).contains("Envelope").contains("http://schemas.xmlsoap.org/soap/envelope/");

		Resource in = newResourceSet().createResource(URI.createURI("empty-2.soap"));
		in.load(new ByteArrayInputStream(baos.toByteArray()), null);

		assertThat(in.getContents()).isEmpty();
		assertThat(in.getErrors()).isEmpty();
	}

	@Test
	@DisplayName("an incoming SOAP Fault is surfaced as an IOException and a resource error")
	void faultBecomesError() {
		String faultXml = """
				<?xml version="1.0" encoding="UTF-8"?>
				<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
				  <soap:Body>
				    <soap:Fault>
				      <faultcode>soap:Server</faultcode>
				      <faultstring>backend exploded</faultstring>
				    </soap:Fault>
				  </soap:Body>
				</soap:Envelope>
				""";

		Resource in = newResourceSet().createResource(URI.createURI("fault.soap"));

		assertThatThrownBy(() -> in.load(new ByteArrayInputStream(faultXml.getBytes(StandardCharsets.UTF_8)), null))
				.isInstanceOf(IOException.class)
				.hasMessageContaining("backend exploded");
		assertThat(in.getErrors()).isNotEmpty();
	}
}
