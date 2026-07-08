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

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * The Ecore {@code EAnnotation} contract used to pin Protocol Buffer field
 * numbers on structural features.
 * <p>
 * Protobuf identifies fields by <em>number</em>, and those numbers must stay
 * stable across model evolution for wire compatibility. Ecore has no field
 * numbers, and {@code getFeatureID()} is not wire-stable, so the number is read
 * from an annotation:
 *
 * <pre>{@code
 * <eAnnotations source="http://www.eclipse.org/fennec/protobuf">
 *   <details key="fieldNumber" value="3"/>
 * </eAnnotations>
 * }</pre>
 */
public final class ProtobufAnnotations {

	/** Annotation source URI. */
	public static final String SOURCE = "http://www.eclipse.org/fennec/protobuf";

	/** Detail key carrying the (1-based) protobuf field number. */
	public static final String KEY_FIELD_NUMBER = "fieldNumber";

	private ProtobufAnnotations() {
	}

	/**
	 * Returns the explicitly pinned field number for a feature, or {@code null}
	 * if the feature carries no protobuf field-number annotation.
	 *
	 * @throws ProtobufException if the annotation value is not a positive integer
	 */
	public static Integer explicitFieldNumber(EStructuralFeature feature) {
		String raw = detail(feature, KEY_FIELD_NUMBER);
		if (raw == null) {
			return null;
		}
		try {
			int number = Integer.parseInt(raw.trim());
			if (number < 1) {
				throw new NumberFormatException("must be >= 1");
			}
			return number;
		} catch (NumberFormatException e) {
			throw new ProtobufException("Invalid " + KEY_FIELD_NUMBER + " '" + raw + "' on feature "
					+ feature.getName() + " (expected a positive integer)", e);
		}
	}

	private static String detail(EModelElement element, String key) {
		if (element.getEAnnotation(SOURCE) == null) {
			return null;
		}
		return element.getEAnnotation(SOURCE).getDetails().get(key);
	}
}
