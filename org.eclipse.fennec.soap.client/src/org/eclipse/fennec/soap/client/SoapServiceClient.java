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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.service.api.ServiceClient;
import org.eclipse.fennec.service.api.ServiceInvocationException;
import org.eclipse.fennec.service.api.ServiceOperation;
import org.eclipse.fennec.soap.ecore.WsdlModel;
import org.eclipse.fennec.soap.resource.SoapResource;
import org.eclipse.fennec.soap.resource.SoapResourceFactory;
import org.xmlsoap.schemas.envelope.EnvelopePackage;

/**
 * A {@link ServiceClient} that invokes SOAP 1.1 operations over HTTP. Requests/responses are
 * (de)serialized through the SOAP envelope {@link SoapResource}; the transport is the JDK
 * {@link HttpClient}. Built from a {@link WsdlModel} (its operations + the imported payload
 * {@code EPackage}s) and an endpoint URL.
 * <p>
 * v1: request-response, document/literal, {@code application/soap+xml} via {@code text/xml}. The
 * {@code SOAPAction} header defaults to empty and can be set via {@link #withSoapAction}. Streaming
 * and WS-* are out of scope — drop to {@link #unwrap(Class) the native HttpClient} for those.
 */
public final class SoapServiceClient implements ServiceClient {

	private final java.net.URI endpoint;
	private final WsdlModel model;
	private final ResourceSet resourceSet;
	private final HttpClient http;
	private Function<ServiceOperation, String> soapAction = op -> "";

	public SoapServiceClient(java.net.URI endpoint, WsdlModel model) {
		this.endpoint = endpoint;
		this.model = model;
		this.resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(SoapResource.FILE_EXTENSION, new SoapResourceFactory());
		resourceSet.getPackageRegistry().put(EnvelopePackage.eNS_URI, EnvelopePackage.eINSTANCE);
		model.registerInto(resourceSet);
		this.http = HttpClient.newHttpClient();
	}

	/** Sets how the {@code SOAPAction} header is derived per operation (default: empty). */
	public SoapServiceClient withSoapAction(Function<ServiceOperation, String> soapAction) {
		if (soapAction != null) {
			this.soapAction = soapAction;
		}
		return this;
	}

	@Override
	public List<? extends ServiceOperation> operations() {
		return model.operations();
	}

	@Override
	public ServiceOperation operation(String name) {
		return model.operation(name);
	}

	@Override
	public EObject invoke(ServiceOperation operation, EObject request) {
		byte[] requestXml = marshal(request);
		HttpResponse<byte[]> response;
		try {
			HttpRequest httpRequest = HttpRequest.newBuilder(endpoint)
					.header("Content-Type", "text/xml; charset=utf-8")
					.header("SOAPAction", soapAction.apply(operation))
					.POST(HttpRequest.BodyPublishers.ofByteArray(requestXml))
					.build();
			response = http.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ServiceInvocationException("SOAP call interrupted: " + operation.name(), e);
		} catch (IOException e) {
			throw new ServiceInvocationException("SOAP transport failed for " + operation.name(), e);
		}
		// A SOAP Fault typically arrives as HTTP 500 + a Fault envelope; unmarshal surfaces it.
		return unmarshal(response.body(), operation);
	}

	private byte[] marshal(EObject request) {
		Resource resource = resourceSet.createResource(URI.createURI("mem:/soap-request." + SoapResource.FILE_EXTENSION));
		resource.getContents().add(EcoreUtil.copy(request));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			resource.save(out, null);
		} catch (IOException e) {
			throw new ServiceInvocationException("Could not build the SOAP request envelope", e);
		} finally {
			resourceSet.getResources().remove(resource);
		}
		return out.toByteArray();
	}

	private EObject unmarshal(byte[] body, ServiceOperation operation) {
		Resource resource = resourceSet.createResource(URI.createURI("mem:/soap-response." + SoapResource.FILE_EXTENSION));
		try {
			resource.load(new ByteArrayInputStream(body), null);
		} catch (IOException e) {
			throw new ServiceInvocationException("SOAP fault or unreadable response for " + operation.name()
					+ ": " + e.getMessage(), e);
		} finally {
			resourceSet.getResources().remove(resource);
		}
		return resource.getContents().isEmpty() ? null : resource.getContents().get(0);
	}

	@Override
	public <T> Optional<T> unwrap(Class<T> nativeType) {
		return nativeType.isInstance(http) ? Optional.of(nativeType.cast(http)) : Optional.empty();
	}

	@Override
	public void close() {
		http.close();
	}
}
