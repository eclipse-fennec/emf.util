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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.fennec.soap.SoapException;
import org.xmlsoap.schemas.envelope.Body;
import org.xmlsoap.schemas.envelope.DocumentRoot;
import org.xmlsoap.schemas.envelope.Envelope;
import org.xmlsoap.schemas.envelope.EnvelopeFactory;
import org.xmlsoap.schemas.envelope.EnvelopePackage;
import org.xmlsoap.schemas.envelope.Fault;

/**
 * An EMF {@link org.eclipse.emf.ecore.resource.Resource Resource} that carries its payload
 * inside a <b>SOAP 1.1 envelope</b> serialized as XML. The resource contents are the plain
 * payload EObjects (e.g. a request or a response): {@code save} wraps each in
 * {@code <soap:Envelope><soap:Body>…} and writes XML; {@code load} parses the envelope and
 * unwraps the {@code Body} content back into the resource contents.
 * <p>
 * Payload types must come from a schema imported via the {@code org.eclipse.fennec.soap.ecore}
 * {@code WsdlImporter} (so each payload {@link EClass} has a global XML element), and their
 * {@code EPackage}s must be registered in the resource set's package registry. An incoming SOAP {@code Fault} is recorded as a resource error
 * ({@link #getErrors()}) and surfaced as an {@link IOException}.
 * <p>
 * Serialization goes through an EMF {@link XMLResource} that shares this resource set's
 * package registry; payloads are copied into the envelope so the caller's object graph is
 * left untouched (v1: cross-document references between payloads are not preserved).
 */
public class SoapResource extends ResourceImpl {

	/** Conventional file extension for SOAP resources. */
	public static final String FILE_EXTENSION = "soap";

	/** Conventional content type for SOAP 1.1 resources. */
	public static final String CONTENT_TYPE = "application/soap+xml";

	public SoapResource(URI uri) {
		super(uri);
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		try {
			DocumentRoot documentRoot = EnvelopeFactory.eINSTANCE.createDocumentRoot();
			Envelope envelope = EnvelopeFactory.eINSTANCE.createEnvelope();
			Body body = EnvelopeFactory.eINSTANCE.createBody();
			envelope.setBody(body);
			documentRoot.setEnvelope(envelope);

			for (EObject payload : getContents()) {
				EStructuralFeature element = globalElement(payload.eClass());
				if (element == null) {
					throw new SoapException("No global XML element for payload type "
							+ payload.eClass().getName() + " — its EPackage must declare a global element"
							+ " (import the schema via WsdlImporter)");
				}
				// Copy into the envelope so the caller's contents graph stays intact.
				body.getAny().add(element, EcoreUtil.copy(payload));
			}

			XMLResource carrier = newCarrier();
			carrier.getContents().add(documentRoot);
			carrier.save(outputStream, saveOptions(options));
		} catch (RuntimeException e) {
			throw record("save", e);
		}
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		Envelope envelope;
		try {
			XMLResource carrier = newCarrier();
			carrier.load(inputStream, loadOptions(options));
			envelope = findEnvelope(carrier);
		} catch (RuntimeException e) {
			throw record("load", e);
		}
		if (envelope == null) {
			throw record("load", new SoapException("No SOAP Envelope found in the document"));
		}
		Body body = envelope.getBody();
		if (body == null) {
			return;
		}
		List<EObject> payloads = new ArrayList<>();
		for (FeatureMap.Entry entry : body.getAny()) {
			Object value = entry.getValue();
			if (value instanceof Fault fault) {
				String message = "SOAP Fault: " + fault.getFaultcode() + " - " + fault.getFaultstring();
				getErrors().add(new SoapDiagnostic(message, uriString()));
				throw new IOException(message);
			}
			if (value instanceof EObject payload) {
				payloads.add(payload);
			}
		}
		// Moving them into the resource contents detaches them from the (discarded) carrier envelope.
		getContents().addAll(payloads);
	}

	/** The global-element containment feature whose type is {@code type}, from its package's DocumentRoot. */
	private static EStructuralFeature globalElement(EClass type) {
		EPackage ePackage = type.getEPackage();
		if (ePackage == null) {
			return null;
		}
		EClass documentRoot = ExtendedMetaData.INSTANCE.getDocumentRoot(ePackage);
		if (documentRoot == null) {
			return null;
		}
		for (EStructuralFeature feature : documentRoot.getEAllStructuralFeatures()) {
			if (ExtendedMetaData.INSTANCE.getFeatureKind(feature) == ExtendedMetaData.ELEMENT_FEATURE
					&& feature.getEType() == type) {
				return feature;
			}
		}
		return null;
	}

	private static Envelope findEnvelope(XMLResource carrier) {
		for (EObject content : carrier.getContents()) {
			if (content instanceof Envelope envelope) {
				return envelope;
			}
			if (content instanceof DocumentRoot documentRoot && documentRoot.getEnvelope() != null) {
				return documentRoot.getEnvelope();
			}
		}
		return null;
	}

	/** A fresh {@link XMLResource} sharing this resource set's package registry (envelope package guaranteed). */
	private XMLResource newCarrier() {
		ResourceSetImpl carrierSet = new ResourceSetImpl();
		ResourceSet owner = getResourceSet();
		if (owner != null) {
			carrierSet.setPackageRegistry(owner.getPackageRegistry());
		}
		carrierSet.getPackageRegistry().put(EnvelopePackage.eNS_URI, EnvelopePackage.eINSTANCE);
		XMLResource carrier = new XMLResourceImpl(getURI() == null ? URI.createURI("soap:envelope") : getURI());
		carrierSet.getResources().add(carrier);
		return carrier;
	}

	private static Map<Object, Object> saveOptions(Map<?, ?> options) {
		Map<Object, Object> merged = mergedOptions(options);
		merged.putIfAbsent(XMLResource.OPTION_ENCODING, "UTF-8");
		return merged;
	}

	private static Map<Object, Object> loadOptions(Map<?, ?> options) {
		return mergedOptions(options);
	}

	private static Map<Object, Object> mergedOptions(Map<?, ?> options) {
		Map<Object, Object> merged = new HashMap<>();
		if (options != null) {
			options.forEach(merged::put);
		}
		// Honour the XSD-derived ExtendedMetaData (element names/namespaces, DocumentRoot
		// transparency, element-vs-attribute) — without it EMF falls back to Ecore names.
		merged.putIfAbsent(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
		return merged;
	}

	private IOException record(String operation, RuntimeException cause) {
		getErrors().add(new SoapDiagnostic(cause.getMessage(), uriString()));
		return new IOException("Failed to " + operation + " SOAP resource " + getURI(), cause);
	}

	private String uriString() {
		return getURI() == null ? null : getURI().toString();
	}
}
