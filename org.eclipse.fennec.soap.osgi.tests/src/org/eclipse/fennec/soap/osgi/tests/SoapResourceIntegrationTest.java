/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.soap.resource.SoapResource;
import org.eclipse.fennec.soap.resource.SoapResourceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Proves that the {@code org.eclipse.fennec.soap.osgi} bundle contributes the SOAP
 * {@code Resource.Factory} as an OSGi service and that it is wired into a Fennec
 * {@link ResourceSet}, so {@code .soap} resources load/save through the ordinary EMF API
 * inside a running framework. Payload-type mapping is covered by the plain-JUnit tests; here
 * we exercise the framework wiring and the envelope plumbing (an empty envelope round-trip,
 * which also proves the SOAP envelope package is available at runtime).
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("SOAP Resource.Factory OSGi registration")
@RequireEMF
public class SoapResourceIntegrationTest {

	@Test
	@DisplayName("factory is registered as a service, bound to the soap extension")
	void factoryRegisteredAsService(
			@InjectService(filter = "(emf.fileExtension=soap)", timeout = 5000) Resource.Factory factory) {
		assertNotNull(factory);
		assertInstanceOf(SoapResourceFactory.class, factory);
	}

	@Test
	@DisplayName("round-trips a .soap envelope through a Fennec ResourceSet")
	void envelopeRoundTripThroughFennecResourceSet(@InjectService(timeout = 5000) ResourceSet resourceSet)
			throws Exception {
		// The Fennec ResourceSet must resolve our service-registered factory by extension.
		Resource out = resourceSet.createResource(URI.createURI("mem:/empty.soap"));
		assertInstanceOf(SoapResource.class, out);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		out.save(baos, null);
		assertTrue(baos.toString(StandardCharsets.UTF_8).contains("Envelope"), "expected a SOAP Envelope");

		Resource in = resourceSet.createResource(URI.createURI("mem:/empty-2.soap"));
		in.load(new ByteArrayInputStream(baos.toByteArray()), null);

		assertEquals(0, in.getContents().size());
		assertTrue(in.getErrors().isEmpty(), "no errors expected for a clean envelope");
	}
}
