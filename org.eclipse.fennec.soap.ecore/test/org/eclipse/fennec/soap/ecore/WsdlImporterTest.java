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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.soap.SoapException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("WsdlImporter: WSDL/XSD -> Ecore + operations")
class WsdlImporterTest {

	private static final String STOCK_NS = "http://example.org/stock";

	/** A minimal document/literal WSDL: two elements, two messages, one operation. */
	private static final String WSDL = """
			<?xml version="1.0" encoding="UTF-8"?>
			<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
			                  xmlns:xsd="http://www.w3.org/2001/XMLSchema"
			                  xmlns:st="http://example.org/stock"
			                  xmlns:tns="http://example.org/stock/wsdl"
			                  targetNamespace="http://example.org/stock/wsdl">
			  <wsdl:types>
			    <xsd:schema targetNamespace="http://example.org/stock"
			                xmlns:xsd="http://www.w3.org/2001/XMLSchema">
			      <xsd:element name="GetStock">
			        <xsd:complexType>
			          <xsd:sequence>
			            <xsd:element name="symbol" type="xsd:string"/>
			          </xsd:sequence>
			        </xsd:complexType>
			      </xsd:element>
			      <xsd:element name="GetStockResponse">
			        <xsd:complexType>
			          <xsd:sequence>
			            <xsd:element name="price" type="xsd:double"/>
			          </xsd:sequence>
			        </xsd:complexType>
			      </xsd:element>
			    </xsd:schema>
			  </wsdl:types>
			  <wsdl:message name="GetStockInput">
			    <wsdl:part name="parameters" element="st:GetStock"/>
			  </wsdl:message>
			  <wsdl:message name="GetStockOutput">
			    <wsdl:part name="parameters" element="st:GetStockResponse"/>
			  </wsdl:message>
			  <wsdl:portType name="StockPortType">
			    <wsdl:operation name="GetStock">
			      <wsdl:input message="tns:GetStockInput"/>
			      <wsdl:output message="tns:GetStockOutput"/>
			    </wsdl:operation>
			  </wsdl:portType>
			</wsdl:definitions>
			""";

	private static WsdlModel importStock() {
		return WsdlImporter.fromWsdl(WSDL.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	@DisplayName("derives an EPackage for the inline schema's target namespace")
	void derivesPackageForSchema() {
		WsdlModel model = importStock();

		EPackage stock = model.packageForNsURI(STOCK_NS);
		assertThat(stock).as("package for %s", STOCK_NS).isNotNull();
		// A global element's anonymous complex type becomes an EClass named <Element>Type,
		// plus the standard EMF XML DocumentRoot.
		assertThat(stock.getEClassifiers()).extracting(c -> c.getName())
				.contains("GetStockType", "GetStockResponseType", "DocumentRoot");
	}

	@Test
	@DisplayName("resolves the operation to its request/response EClasses")
	void resolvesOperation() {
		WsdlModel model = importStock();

		SoapOperation getStock = model.operation("GetStock");
		assertThat(getStock).isNotNull();
		assertThat(getStock.requestElement().getNamespaceURI()).isEqualTo(STOCK_NS);
		assertThat(getStock.requestElement().getLocalPart()).isEqualTo("GetStock");

		EClass request = getStock.requestType();
		EClass response = getStock.responseType();
		assertThat(request).as("request type").isNotNull();
		assertThat(response).as("response type").isNotNull();
		assertThat(request.getName()).isEqualTo("GetStockType");
		// Attribute types are the EMF XML datatypes (org.eclipse.emf.ecore.xml.type).
		assertThat(request.getEStructuralFeature("symbol")).isNotNull();
		assertThat(request.getEStructuralFeature("symbol").getEType().getName()).isEqualTo("String");
		assertThat(response.getEStructuralFeature("price").getEType().getName()).isEqualTo("Double");
	}

	@Test
	@DisplayName("a plain XSD imports its types but has no operations")
	void plainXsd() {
		String xsd = """
				<?xml version="1.0"?>
				<xsd:schema xmlns:xsd="http://www.w3.org/2001/XMLSchema"
				            targetNamespace="http://example.org/plain">
				  <xsd:complexType name="Point">
				    <xsd:sequence>
				      <xsd:element name="x" type="xsd:int"/>
				      <xsd:element name="y" type="xsd:int"/>
				    </xsd:sequence>
				  </xsd:complexType>
				</xsd:schema>
				""";

		WsdlModel model = WsdlImporter.fromXsd(xsd.getBytes(StandardCharsets.UTF_8));

		assertThat(model.operations()).isEmpty();
		EPackage plain = model.packageForNsURI("http://example.org/plain");
		assertThat(plain).isNotNull();
		EClass point = (EClass) plain.getEClassifier("Point");
		assertThat(point).isNotNull();
		assertThat(point.getEStructuralFeature("x").getEType().getName()).isEqualTo("Int");
	}

	@Test
	@DisplayName("a WSDL without inline schemas yields no packages and no operations")
	void noSchemaIsGraceful() {
		String empty = """
				<?xml version="1.0"?>
				<wsdl:definitions xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
				                  targetNamespace="urn:empty"/>
				""";

		WsdlModel model = WsdlImporter.fromWsdl(empty.getBytes(StandardCharsets.UTF_8));

		assertThat(model.packages()).isEmpty();
		assertThat(model.operations()).isEmpty();
	}

	@Test
	@DisplayName("fails clearly when the input is not valid XML")
	void garbageInputFails() {
		assertThatThrownBy(() -> WsdlImporter.fromWsdl("not xml at all".getBytes(StandardCharsets.UTF_8)))
				.isInstanceOf(SoapException.class);
	}
}
