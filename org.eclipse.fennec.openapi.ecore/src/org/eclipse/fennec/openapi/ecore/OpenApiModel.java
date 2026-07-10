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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.SecurityScheme;

/**
 * The result of importing an OpenAPI document: the parsed {@link OpenAPI} model, the
 * {@link EPackage} generated from {@code components/schemas} (by the codec's JSON-Schema
 * pipeline), the synthetic requests package (parameter-folding EClasses), the declared
 * {@code components/securitySchemes} (by name, declaration order) and the resolved
 * {@link OpenApiOperation}s (each carrying its effective
 * {@linkplain OpenApiOperation#security() security requirements}).
 *
 * @see OpenApiImporter
 */
public record OpenApiModel(OpenAPI document, EPackage schemasPackage, EPackage requestsPackage,
		Map<String, SecurityScheme> securitySchemes, List<OpenApiOperation> operations,
		List<String> diagnostics) {

	public OpenApiModel {
		securitySchemes = Collections.unmodifiableMap(new LinkedHashMap<>(securitySchemes));
		operations = List.copyOf(operations);
		diagnostics = List.copyOf(diagnostics);
	}

	/** The declared security scheme with the given name, or {@code null}. */
	public SecurityScheme securityScheme(String name) {
		return securitySchemes.get(name);
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
