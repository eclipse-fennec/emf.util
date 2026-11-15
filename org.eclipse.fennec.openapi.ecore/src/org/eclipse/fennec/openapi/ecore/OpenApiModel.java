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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.SecurityScheme;

/**
 * The result of importing an OpenAPI 3.0, 3.1 or 3.2 description: the parsed {@link OpenAPI} model
 * (the codec's {@code oas} model, for every version), the {@link EPackage} the codec derived from
 * {@code components/schemas} (with the classifiers of inline schemas in its {@code inline}
 * sub-package), the synthetic requests package (parameter-folding EClasses), the declared
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

	/**
	 * The generated packages: the schemas package, its sub-packages (inline schemas) and the
	 * requests package — what a metadata registry or package registry has to know.
	 */
	public List<EPackage> generatedPackages() {
		List<EPackage> packages = new ArrayList<>();
		addWithSubpackages(packages, schemasPackage);
		addWithSubpackages(packages, requestsPackage);
		return packages;
	}

	/** Registers the generated packages into a {@link ResourceSet}'s package registry. */
	public void registerInto(ResourceSet resourceSet) {
		for (EPackage ePackage : generatedPackages()) {
			if (ePackage.getNsURI() != null) {
				resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
			}
		}
	}

	private static void addWithSubpackages(List<EPackage> packages, EPackage ePackage) {
		if (ePackage != null) {
			packages.add(ePackage);
			ePackage.getESubpackages().forEach(sub -> addWithSubpackages(packages, sub));
		}
	}
}
