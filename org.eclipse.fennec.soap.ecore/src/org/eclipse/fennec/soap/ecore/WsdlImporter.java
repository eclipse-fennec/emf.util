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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.fennec.soap.SoapException;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.ecore.XSDEcoreBuilder;
import org.eclipse.xsd.util.XSDResourceFactoryImpl;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Derives Ecore {@link EPackage}s from a WSDL or XSD — for <b>interop and bootstrapping</b>
 * of SOAP services. The type schemas are turned into Ecore by EMF's
 * {@link XSDEcoreBuilder} (which, for a WSDL, extracts every embedded {@code <xsd:schema>}
 * from {@code <wsdl:types>}); the generated model carries the {@code ExtendedMetaData}
 * annotations EMF needs to (de)serialize it as XML.
 * <p>
 * WSDL <em>semantics</em> (operations, messages, bindings) are not modelled by EMF, so for a
 * WSDL this class additionally does a light DOM parse of {@code portType}/{@code operation}
 * and {@code message}/{@code part} to expose the {@link SoapOperation}s — each resolved to the
 * request/response {@link EClass} via the message part's global element (document/literal
 * style; RPC-style parts that reference a type rather than an element are left unresolved).
 */
public final class WsdlImporter {

	/** WSDL 1.1 namespace. */
	private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

	private WsdlImporter() {
	}

	/** Imports a WSDL document (types + operations). */
	public static WsdlModel fromWsdl(byte[] wsdl) {
		return doImport(wsdl, "wsdl", true);
	}

	/** Imports a WSDL document from a stream. */
	public static WsdlModel fromWsdl(InputStream wsdl) {
		return fromWsdl(readAll(wsdl));
	}

	/** Imports a plain XSD document (types only; no operations). */
	public static WsdlModel fromXsd(byte[] xsd) {
		return doImport(xsd, "xsd", false);
	}

	/** Imports a plain XSD document from a stream. */
	public static WsdlModel fromXsd(InputStream xsd) {
		return fromXsd(readAll(xsd));
	}

	private static WsdlModel doImport(byte[] data, String extension, boolean wsdl) {
		if (data == null) {
			throw new IllegalArgumentException("document must not be null");
		}
		EPackageRegistryImpl registry = new EPackageRegistryImpl();
		ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(registry);
		XSDEcoreBuilder builder = new XSDEcoreBuilder(extendedMetaData);

		for (XSDSchema schema : loadSchemas(data, extension)) {
			builder.generate(schema);
		}

		List<EPackage> packages = new ArrayList<>();
		for (EPackage ePackage : builder.getTargetNamespaceToEPackageMap().values()) {
			if (ePackage == null || ePackage == XMLTypePackage.eINSTANCE || ePackage == XMLNamespacePackage.eINSTANCE) {
				continue; // skip the built-in XML type/namespace packages
			}
			if (ePackage.getNsURI() != null) {
				registry.put(ePackage.getNsURI(), ePackage); // so element QNames resolve below
			}
			packages.add(ePackage);
		}

		List<String> diagnostics = new ArrayList<>();
		if (builder.getDiagnostics() != null) {
			builder.getDiagnostics().forEach(d -> diagnostics.add(d.getMessage()));
		}

		List<SoapOperation> operations = wsdl ? parseOperations(data, extendedMetaData, diagnostics) : List.of();
		return new WsdlModel(packages, operations, diagnostics);
	}

	// --- XSD loading -----------------------------------------------------------------------

	private static List<XSDSchema> loadSchemas(byte[] data, String extension) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getLoadOptions().put(XSDResourceImpl.XSD_TRACK_LOCATION, Boolean.TRUE);
		XSDResourceFactoryImpl factory = new XSDResourceFactoryImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("wsdl", factory);
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xsd", factory);

		Resource resource = resourceSet.createResource(URI.createURI("inmemory-soap-import." + extension));
		try {
			resource.load(new ByteArrayInputStream(data), resourceSet.getLoadOptions());
		} catch (IOException e) {
			throw new SoapException("Could not parse the " + extension.toUpperCase() + " document", e);
		}
		List<XSDSchema> schemas = new ArrayList<>();
		for (EObject content : resource.getContents()) {
			if (content instanceof XSDSchema schema) {
				schemas.add(schema);
			}
		}
		if (schemas.isEmpty()) {
			throw new SoapException("No <xsd:schema> found in the " + extension.toUpperCase() + " document"
					+ (extension.equals("wsdl") ? " (expected inline schemas under <wsdl:types>)" : ""));
		}
		return schemas;
	}

	// --- WSDL operation parsing (light DOM) ------------------------------------------------

	private static List<SoapOperation> parseOperations(byte[] data, ExtendedMetaData extendedMetaData,
			List<String> diagnostics) {
		Document doc = parseXml(data);

		// message local name -> its (first) part's global element QName
		Map<String, QName> messageElement = new HashMap<>();
		NodeList messages = doc.getElementsByTagNameNS(WSDL_NS, "message");
		for (int i = 0; i < messages.getLength(); i++) {
			Element message = (Element) messages.item(i);
			QName element = firstPartElement(message);
			String name = localPart(message.getAttribute("name"));
			if (name != null && element != null) {
				messageElement.put(name, element);
			}
		}

		List<SoapOperation> operations = new ArrayList<>();
		NodeList ops = doc.getElementsByTagNameNS(WSDL_NS, "operation");
		for (int i = 0; i < ops.getLength(); i++) {
			Element op = (Element) ops.item(i);
			if (!isChildOf(op, "portType")) {
				continue; // ignore binding operations
			}
			String opName = op.getAttribute("name");
			QName request = messageElement.get(localPart(childMessage(op, "input")));
			QName response = messageElement.get(localPart(childMessage(op, "output")));
			EClass requestType = resolveElement(extendedMetaData, request);
			EClass responseType = resolveElement(extendedMetaData, response);
			if (request != null && requestType == null) {
				diagnostics.add("Operation '" + opName + "': request element " + request + " did not resolve to an EClass");
			}
			operations.add(new SoapOperation(opName, request, response, requestType, responseType));
		}
		return operations;
	}

	/** The {@code element} QName of a message's first {@code <part element="…">}, or {@code null}. */
	private static QName firstPartElement(Element message) {
		for (Element part : children(message, "part")) {
			String element = part.getAttribute("element");
			if (!element.isEmpty()) {
				return resolveQName(part, element);
			}
		}
		return null;
	}

	/** The {@code message} attribute of an operation's {@code <input>}/{@code <output>} child. */
	private static String childMessage(Element operation, String childName) {
		for (Element child : children(operation, childName)) {
			String message = child.getAttribute("message");
			if (!message.isEmpty()) {
				return message;
			}
		}
		return null;
	}

	private static EClass resolveElement(ExtendedMetaData extendedMetaData, QName element) {
		if (element == null) {
			return null;
		}
		EStructuralFeature feature = extendedMetaData.getElement(element.getNamespaceURI(), element.getLocalPart());
		return feature != null && feature.getEType() instanceof EClass eClass ? eClass : null;
	}

	// --- small DOM helpers -----------------------------------------------------------------

	private static Document parseXml(byte[] data) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			return dbf.newDocumentBuilder().parse(new ByteArrayInputStream(data));
		} catch (Exception e) {
			throw new SoapException("Could not parse the WSDL XML", e);
		}
	}

	private static List<Element> children(Element parent, String localName) {
		List<Element> result = new ArrayList<>();
		NodeList nodes = parent.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node instanceof Element child && WSDL_NS.equals(child.getNamespaceURI())
					&& localName.equals(child.getLocalName())) {
				result.add(child);
			}
		}
		return result;
	}

	private static boolean isChildOf(Element element, String parentLocalName) {
		Node parent = element.getParentNode();
		return parent instanceof Element pe && WSDL_NS.equals(pe.getNamespaceURI())
				&& parentLocalName.equals(pe.getLocalName());
	}

	/** Resolves a {@code prefix:local} attribute value against the namespaces in scope at {@code context}. */
	private static QName resolveQName(Element context, String value) {
		int colon = value.indexOf(':');
		String prefix = colon < 0 ? null : value.substring(0, colon);
		String local = colon < 0 ? value : value.substring(colon + 1);
		String namespace = context.lookupNamespaceURI(prefix);
		return new QName(namespace == null ? XMLConstants.NULL_NS_URI : namespace, local);
	}

	private static String localPart(String qname) {
		if (qname == null || qname.isEmpty()) {
			return null;
		}
		int colon = qname.indexOf(':');
		return colon < 0 ? qname : qname.substring(colon + 1);
	}

	private static byte[] readAll(InputStream in) {
		try (in) {
			return in.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException("Could not read the schema document", e);
		}
	}
}
