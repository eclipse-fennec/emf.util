/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap.ecore;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.soap.resource.SoapResource;
import org.eclipse.fennec.soap.resource.SoapResourceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xmlsoap.schemas.envelope.EnvelopePackage;

/**
 * End-to-end: import a WSDL, then round-trip a request of the imported type through a SOAP
 * envelope. Spans the importer ({@link WsdlImporter}) and the runtime
 * ({@link SoapResource}) — this bundle depends on both.
 */
@DisplayName("SOAP round-trip of a WSDL-imported payload")
class SoapRoundTripTest {

	private static final String WSDL = """
			<?xml version="1.0" encoding="UTF-8"?>
			<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
			                  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
			                  xmlns:st="http://example.org/stock"
			                  xmlns:tns="http://example.org/stock/wsdl"
			                  targetNamespace="http://example.org/stock/wsdl">
			  <wsdl:types>
			    <xsd:schema targetNamespace="http://example.org/stock" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
			      <xsd:element name="GetStock">
			        <xsd:complexType><xsd:sequence>
			          <xsd:element name="symbol" type="xsd:string"/>
			        </xsd:sequence></xsd:complexType>
			      </xsd:element>
			    </xsd:schema>
			  </wsdl:types>
			  <wsdl:message name="GetStockInput"><wsdl:part name="p" element="st:GetStock"/></wsdl:message>
			  <wsdl:portType name="P"><wsdl:operation name="GetStock">
			    <wsdl:input message="tns:GetStockInput"/>
			  </wsdl:operation></wsdl:portType>
			</wsdl:definitions>
			""";

	private WsdlModel model;
	private EClass requestType;

	@BeforeEach
	void setUp() {
		model = WsdlImporter.fromWsdl(WSDL.getBytes(StandardCharsets.UTF_8));
		requestType = model.operation("GetStock").requestType();
	}

	private ResourceSet newResourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(SoapResource.FILE_EXTENSION, new SoapResourceFactory());
		rs.getPackageRegistry().put(EnvelopePackage.eNS_URI, EnvelopePackage.eINSTANCE);
		model.registerInto(rs);
		return rs;
	}

	@Test
	@DisplayName("wraps a request payload in a SOAP envelope and round-trips it")
	void roundTripsARequest() throws IOException {
		Resource out = newResourceSet().createResource(URI.createURI("req.soap"));
		EObject request = EcoreUtil.create(requestType);
		request.eSet(requestType.getEStructuralFeature("symbol"), "IBM");
		out.getContents().add(request);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		out.save(baos, null);
		String xml = baos.toString(StandardCharsets.UTF_8);
		assertThat(xml).contains("Envelope").contains("Body").contains("GetStock").contains("IBM");

		Resource in = newResourceSet().createResource(URI.createURI("req.soap"));
		in.load(new ByteArrayInputStream(baos.toByteArray()), null);

		assertThat(in.getContents()).hasSize(1);
		EObject payload = in.getContents().get(0);
		assertThat(payload.eClass()).isSameAs(requestType);
		assertThat(payload.eGet(requestType.getEStructuralFeature("symbol"))).isEqualTo("IBM");
	}
}
