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

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * The result of importing a WSDL/XSD: the derived Ecore {@link EPackage}s (one per XSD
 * target namespace) plus the resolved {@link SoapOperation}s (WSDL only) and any importer
 * diagnostics. See {@link WsdlImporter}.
 */
public final class WsdlModel {

	private final List<EPackage> packages;
	private final List<SoapOperation> operations;
	private final List<String> diagnostics;

	WsdlModel(List<EPackage> packages, List<SoapOperation> operations, List<String> diagnostics) {
		this.packages = List.copyOf(packages);
		this.operations = List.copyOf(operations);
		this.diagnostics = List.copyOf(diagnostics);
	}

	/** The derived {@link EPackage}s, one per XSD target namespace. */
	public List<EPackage> packages() {
		return packages;
	}

	/** The resolved WSDL operations (empty for a plain XSD import). */
	public List<SoapOperation> operations() {
		return operations;
	}

	/** Importer diagnostics (XSD validation messages, unresolved operation elements). */
	public List<String> diagnostics() {
		return diagnostics;
	}

	/** The imported {@link EPackage} for a namespace URI, or {@code null} if none was derived. */
	public EPackage packageForNsURI(String nsURI) {
		for (EPackage p : packages) {
			if (p.getNsURI() != null && p.getNsURI().equals(nsURI)) {
				return p;
			}
		}
		return null;
	}

	/** The operation with the given name, or {@code null}. */
	public SoapOperation operation(String name) {
		for (SoapOperation op : operations) {
			if (op.name().equals(name)) {
				return op;
			}
		}
		return null;
	}

	/**
	 * Registers all imported packages into a {@link ResourceSet}'s package registry (keyed by
	 * nsURI) so SOAP payloads of these types (de)serialize through that resource set.
	 */
	public void registerInto(ResourceSet resourceSet) {
		for (EPackage p : packages) {
			if (p.getNsURI() != null) {
				resourceSet.getPackageRegistry().put(p.getNsURI(), p);
			}
		}
	}

	/** All classifiers across the imported packages (convenience for tests/inspection). */
	public List<EClassifier> classifiers() {
		return packages.stream().flatMap(p -> p.getEClassifiers().stream()).toList();
	}

	static WsdlModel empty() {
		return new WsdlModel(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
	}
}
