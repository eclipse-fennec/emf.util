/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

/**
 * Encodes/decodes the concrete {@link EClass} of a wrapped (polymorphic /
 * cross-package / {@code EObject}-typed) object into the discriminator string.
 * <p>
 * Encoding depends on the {@link ProtobufTypeStrategy} and whether the target is in
 * the reference's own ("context") package. Decoding is <b>strategy-agnostic</b> —
 * it recognises the form (full URI {@code nsURI#//Name}, {@code nsURI#id}, a bare
 * integer id, or a bare name) — so the reader need not share the writer's strategy.
 */
final class ProtobufType {

	private ProtobufType() {
	}

	static String encode(EClass actual, EClass declared, ProtobufTypeStrategy strategy, boolean smartCompression) {
		boolean samePackage = declared != null && actual.getEPackage() == declared.getEPackage();
		switch (strategy) {
		case NAME:
			return samePackage ? actual.getName() : uri(actual);
		case NUMERIC:
			return samePackage ? Integer.toString(actual.getClassifierID())
					: actual.getEPackage().getNsURI() + "#" + actual.getClassifierID();
		case URI:
		default:
			return (smartCompression && samePackage) ? actual.getName() : uri(actual);
		}
	}

	static EClass decode(String value, EClass declared, EPackage.Registry registry) {
		if (value == null || value.isEmpty()) {
			throw new ProtobufException("Empty type discriminator");
		}
		int schemeSep = value.indexOf("#//");
		if (schemeSep >= 0) { // nsURI#//Name
			String nsURI = value.substring(0, schemeSep);
			String name = value.substring(schemeSep + 3);
			return eClass(pkg(nsURI, registry), name, value);
		}
		if (isInteger(value)) { // bare classifier id in the context package
			return byId(contextPackage(declared), Integer.parseInt(value), value);
		}
		int hash = value.lastIndexOf('#');
		if (hash >= 0 && isInteger(value.substring(hash + 1))) { // nsURI#id
			String nsURI = value.substring(0, hash);
			return byId(pkg(nsURI, registry), Integer.parseInt(value.substring(hash + 1)), value);
		}
		return eClass(contextPackage(declared), value, value); // bare name in the context package
	}

	private static EPackage contextPackage(EClass declared) {
		if (declared == null || declared.getEPackage() == null) {
			throw new ProtobufException("Cannot resolve a bare type discriminator without a declared reference type");
		}
		return declared.getEPackage();
	}

	private static String uri(EClass eClass) {
		// Canonical EClass URI (nsURI#//Name); built explicitly because EcoreUtil.getURI
		// yields a fragment-only URI for dynamic packages that are not in a resource.
		EPackage p = eClass.getEPackage();
		return (p == null ? "" : p.getNsURI()) + "#//" + eClass.getName();
	}

	private static boolean isInteger(String s) {
		if (s.isEmpty()) {
			return false;
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static EPackage pkg(String nsURI, EPackage.Registry registry) {
		EPackage p = registry == null ? null : registry.getEPackage(nsURI);
		if (p == null) {
			throw new ProtobufException("No EPackage registered for nsURI " + nsURI
					+ " (register it in the package registry before (de)serializing)");
		}
		return p;
	}

	private static EClass eClass(EPackage pkg, String name, String value) {
		EClassifier c = pkg.getEClassifier(name);
		if (!(c instanceof EClass eClass)) {
			throw new ProtobufException("Type discriminator '" + value + "' does not resolve to an EClass");
		}
		return eClass;
	}

	private static EClass byId(EPackage pkg, int classifierID, String value) {
		for (EClassifier c : pkg.getEClassifiers()) {
			if (c instanceof EClass eClass && eClass.getClassifierID() == classifierID) {
				return eClass;
			}
		}
		throw new ProtobufException("Type discriminator '" + value + "' does not resolve to an EClass in "
				+ pkg.getNsURI());
	}
}
