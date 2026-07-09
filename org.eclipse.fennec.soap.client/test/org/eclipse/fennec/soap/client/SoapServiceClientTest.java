/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.eclipse.fennec.soap.ecore.WsdlImporter;
import org.eclipse.fennec.soap.ecore.WsdlModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

/**
 * Proves the {@link org.eclipse.fennec.service.api.ServiceClient} design end-to-end for SOAP:
 * import a WSDL, invoke an operation over HTTP, get the response as an {@link EObject}. Uses a
 * local JDK {@link HttpServer} with a canned SOAP response — reliable, no network. (A tagged
 * test against a public SOAP endpoint would additionally exercise a real service.)
 */
@DisplayName("SoapServiceClient: end-to-end invoke against a local SOAP endpoint")
class SoapServiceClientTest {

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
			        <xsd:complexType><xsd:sequence><xsd:element name="symbol" type="xsd:string"/></xsd:sequence></xsd:complexType>
			      </xsd:element>
			      <xsd:element name="GetStockResponse">
			        <xsd:complexType><xsd:sequence><xsd:element name="price" type="xsd:double"/></xsd:sequence></xsd:complexType>
			      </xsd:element>
			    </xsd:schema>
			  </wsdl:types>
			  <wsdl:message name="GetStockInput"><wsdl:part name="p" element="st:GetStock"/></wsdl:message>
			  <wsdl:message name="GetStockOutput"><wsdl:part name="p" element="st:GetStockResponse"/></wsdl:message>
			  <wsdl:portType name="P"><wsdl:operation name="GetStock">
			    <wsdl:input message="tns:GetStockInput"/><wsdl:output message="tns:GetStockOutput"/>
			  </wsdl:operation></wsdl:portType>
			</wsdl:definitions>
			""";

	private static final String RESPONSE = """
			<?xml version="1.0" encoding="UTF-8"?>
			<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
			  <soap:Body>
			    <st:GetStockResponse xmlns:st="http://example.org/stock"><price>42.5</price></st:GetStockResponse>
			  </soap:Body>
			</soap:Envelope>
			""";

	private static final String FAULT = """
			<?xml version="1.0" encoding="UTF-8"?>
			<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
			  <soap:Body><soap:Fault><faultcode>soap:Server</faultcode><faultstring>boom</faultstring></soap:Fault></soap:Body>
			</soap:Envelope>
			""";

	private HttpServer server;
	private WsdlModel model;
	private EClass requestType;
	private final AtomicReference<String> lastRequestBody = new AtomicReference<>();
	private final AtomicReference<String> reply = new AtomicReference<>(RESPONSE);
	private final AtomicReference<Integer> status = new AtomicReference<>(200);

	@BeforeEach
	void setUp() throws IOException {
		model = WsdlImporter.fromWsdl(WSDL.getBytes(StandardCharsets.UTF_8));
		requestType = model.operation("GetStock").requestType();

		server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
		server.createContext("/svc", exchange -> {
			lastRequestBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
			byte[] body = reply.get().getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "text/xml; charset=utf-8");
			exchange.sendResponseHeaders(status.get(), body.length);
			try (OutputStream os = exchange.getResponseBody()) {
				os.write(body);
			}
		});
		server.start();
	}

	@AfterEach
	void tearDown() {
		server.stop(0);
	}

	private URI endpoint() {
		return URI.create("http://localhost:" + server.getAddress().getPort() + "/svc");
	}

	private EObject request(String symbol) {
		EObject request = EcoreUtil.create(requestType);
		request.eSet(requestType.getEStructuralFeature("symbol"), symbol);
		return request;
	}

	@Test
	@DisplayName("invokes an operation over HTTP and returns the response EObject")
	void invokesOverHttp() {
		try (SoapServiceClient client = new SoapServiceClient(endpoint(), model)) {
			EObject response = client.invoke("GetStock", request("IBM"));

			// request really went out as a SOAP envelope carrying the payload
			assertThat(lastRequestBody.get()).contains("Envelope").contains("GetStock").contains("IBM");

			// response parsed back into a typed EObject
			assertThat(response).isNotNull();
			assertThat(response.eClass().getName()).isEqualTo("GetStockResponseType");
			assertThat(response.eGet(response.eClass().getEStructuralFeature("price"))).isEqualTo(42.5d);

			assertThat(client.unwrap(HttpClient.class)).isPresent();
		}
	}

	@Test
	@DisplayName("surfaces a SOAP Fault as a ServiceInvocationException")
	void faultBecomesException() {
		reply.set(FAULT);
		status.set(500);
		try (SoapServiceClient client = new SoapServiceClient(endpoint(), model)) {
			assertThatThrownBy(() -> client.invoke("GetStock", request("IBM")))
					.isInstanceOf(ServiceInvocationException.class)
					.hasMessageContaining("boom");
		}
	}
}
