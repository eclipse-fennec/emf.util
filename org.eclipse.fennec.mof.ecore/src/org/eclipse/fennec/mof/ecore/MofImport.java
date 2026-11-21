/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

/**
 * The result of a MOF import: the derived {@link EPackage}s (one per package name, see
 * {@link ImportOptions#withPackageName}) and the warnings collected on the way (duplicate class
 * declarations, unresolved types in lenient mode, implicit overrides).
 *
 * @see MofImporter#importFrom(java.nio.file.Path)
 */
public record MofImport(List<EPackage> packages, List<String> warnings) {

	public MofImport {
		packages = List.copyOf(packages);
		warnings = List.copyOf(warnings);
	}

	/** The package with the given name, or {@code null}. */
	public EPackage getPackage(String name) {
		for (EPackage p : packages) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	/** The {@link EClass} for a CIM class name (case-insensitive, as in CIM), or {@code null}. */
	public EClass getEClass(String cimClassName) {
		for (EPackage p : packages) {
			for (EClassifier c : p.getEClassifiers()) {
				if (c instanceof EClass eClass && c.getName().equalsIgnoreCase(cimClassName)) {
					return eClass;
				}
			}
		}
		return null;
	}

	/** All imported classes, over all packages. */
	public List<EClass> getEClasses() {
		return packages.stream().flatMap(p -> p.getEClassifiers().stream()).filter(EClass.class::isInstance)
				.map(EClass.class::cast).toList();
	}
}
