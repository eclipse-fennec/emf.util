/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.openapi.ecore;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.model.openapi.OpenAPI;

/**
 * The result of importing an OpenAPI document: the parsed {@link OpenAPI} model, the
 * {@link EPackage} generated from {@code components/schemas} (by the codec's JSON-Schema
 * pipeline), the synthetic requests package (parameter-folding EClasses) and the resolved
 * {@link OpenApiOperation}s.
 *
 * @see OpenApiImporter
 */
public record OpenApiModel(OpenAPI document, EPackage schemasPackage, EPackage requestsPackage,
		List<OpenApiOperation> operations, List<String> diagnostics) {

	public OpenApiModel {
		operations = List.copyOf(operations);
		diagnostics = List.copyOf(diagnostics);
	}

	/** The operation with the given {@link OpenApiOperation#name() name}, or {@code null}. */
	public OpenApiOperation operation(String name) {
		for (OpenApiOperation op : operations) {
			if (op.name().equals(name)) {
				return op;
			}
		}
		return null;
	}

	/** Registers the generated packages into a {@link ResourceSet}'s package registry. */
	public void registerInto(ResourceSet resourceSet) {
		if (schemasPackage != null && schemasPackage.getNsURI() != null) {
			resourceSet.getPackageRegistry().put(schemasPackage.getNsURI(), schemasPackage);
		}
		if (requestsPackage != null && requestsPackage.getNsURI() != null) {
			resourceSet.getPackageRegistry().put(requestsPackage.getNsURI(), requestsPackage);
		}
	}
}
