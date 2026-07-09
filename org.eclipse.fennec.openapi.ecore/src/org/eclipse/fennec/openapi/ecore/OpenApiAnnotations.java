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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * The Ecore {@code EAnnotation} contract for the OpenAPI mapping (mirrors the protobuf/SOAP
 * annotation helpers). On a synthetic request {@link org.eclipse.emf.ecore.EClass}, each feature
 * carries where its value goes on the wire: {@code in = path | query | header | cookie | body}.
 */
public final class OpenApiAnnotations {

	/** Annotation source URI. */
	public static final String SOURCE = "http://eclipse.org/fennec/openapi";

	/** Feature detail: where the parameter travels — {@code path|query|header|cookie|body}. */
	public static final String KEY_IN = "in";

	public static final String IN_PATH = "path";
	public static final String IN_QUERY = "query";
	public static final String IN_HEADER = "header";
	public static final String IN_COOKIE = "cookie";
	public static final String IN_BODY = "body";

	private OpenApiAnnotations() {
	}

	/** The {@code in} location of a synthetic-request feature, or {@code null}. */
	public static String in(EStructuralFeature feature) {
		EAnnotation a = feature.getEAnnotation(SOURCE);
		return a == null ? null : a.getDetails().get(KEY_IN);
	}

	/** Stamps the {@code in} location on a synthetic-request feature (importer side). */
	public static void setIn(EStructuralFeature feature, String in) {
		annotation(feature).getDetails().put(KEY_IN, in);
	}

	private static EAnnotation annotation(EModelElement element) {
		EAnnotation a = element.getEAnnotation(SOURCE);
		if (a == null) {
			a = EcoreFactory.eINSTANCE.createEAnnotation();
			a.setSource(SOURCE);
			element.getEAnnotations().add(a);
		}
		return a;
	}
}
