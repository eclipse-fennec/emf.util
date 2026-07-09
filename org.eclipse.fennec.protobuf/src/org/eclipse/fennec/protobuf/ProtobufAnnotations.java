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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * The Ecore {@code EAnnotation} contract for the EMF↔Protobuf mapping. A single
 * annotation source carries all configuration; detail keys are the bare property
 * names — the same convention the Fennec Codec uses (its source is
 * {@code http://eclipse.org/fennec/codec}).
 *
 * <pre>{@code
 * <eAnnotations source="http://eclipse.org/fennec/protobuf">
 *   <details key="fieldNumber" value="3"/>
 *   <details key="ignore" value="true"/>
 * </eAnnotations>
 * }</pre>
 *
 * <p>Feature-level flags mirror the codec's feature filters:
 * <ul>
 * <li>{@code fieldNumber} — pin the (wire-stable) protobuf field number.</li>
 * <li>{@code ignore} — drop the feature entirely (no field, never (de)serialized).</li>
 * <li>{@code ignoreWrite} / {@code ignoreRead} — keep the field but skip on write / read.</li>
 * <li>{@code forceWrite} / {@code forceRead} — include an otherwise-skipped transient
 * feature.</li>
 * </ul>
 * Package/global flags: {@code typeStrategy} (NAME|URI|NUMERIC) and
 * {@code smartCompression} (true|false).
 */
public final class ProtobufAnnotations {

	/** Annotation source URI (mirrors the codec's {@code http://eclipse.org/fennec/codec}). */
	public static final String SOURCE = "http://eclipse.org/fennec/protobuf";

	public static final String KEY_FIELD_NUMBER = "fieldNumber";
	public static final String KEY_IGNORE = "ignore";
	public static final String KEY_IGNORE_WRITE = "ignoreWrite";
	public static final String KEY_IGNORE_READ = "ignoreRead";
	public static final String KEY_FORCE_WRITE = "forceWrite";
	public static final String KEY_FORCE_READ = "forceRead";
	public static final String KEY_TYPE_STRATEGY = "typeStrategy";
	public static final String KEY_SMART_COMPRESSION = "smartCompression";

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

	/**
	 * Pins the (wire-stable) protobuf field number on a feature, creating the mapping
	 * {@link EAnnotation} if needed. The inverse of {@link #explicitFieldNumber} — used
	 * when deriving an Ecore model from Protobuf descriptors so a later export keeps the
	 * original field numbers.
	 *
	 * @throws ProtobufException if {@code number} is not a positive integer
	 */
	public static void setFieldNumber(EStructuralFeature feature, int number) {
		if (number < 1) {
			throw new ProtobufException("Invalid " + KEY_FIELD_NUMBER + " " + number
					+ " on feature " + feature.getName() + " (expected a positive integer)");
		}
		annotation(feature).getDetails().put(KEY_FIELD_NUMBER, Integer.toString(number));
	}

	/** Whether the feature is represented by a protobuf field at all. */
	public static boolean describes(EStructuralFeature f) {
		if (flag(f, KEY_IGNORE)) {
			return false;
		}
		return !f.isTransient() || flag(f, KEY_FORCE_WRITE) || flag(f, KEY_FORCE_READ);
	}

	/** Whether the feature's value is written. */
	public static boolean writes(EStructuralFeature f) {
		if (flag(f, KEY_IGNORE) || flag(f, KEY_IGNORE_WRITE)) {
			return false;
		}
		return flag(f, KEY_FORCE_WRITE) || (f.isChangeable() && !f.isTransient());
	}

	/** Whether the feature's value is read back. */
	public static boolean reads(EStructuralFeature f) {
		if (flag(f, KEY_IGNORE) || flag(f, KEY_IGNORE_READ) || !f.isChangeable()) {
			return false;
		}
		return flag(f, KEY_FORCE_READ) || !f.isTransient();
	}

	/** Package/global type-discriminator strategy from an annotation, or {@code fallback}. */
	public static ProtobufTypeStrategy typeStrategy(EModelElement element, ProtobufTypeStrategy fallback) {
		return ProtobufTypeStrategy.from(detail(element, KEY_TYPE_STRATEGY), fallback);
	}

	/** Package/global smart-compression flag from an annotation, or {@code fallback}. */
	public static boolean smartCompression(EModelElement element, boolean fallback) {
		String raw = detail(element, KEY_SMART_COMPRESSION);
		return raw == null ? fallback : Boolean.parseBoolean(raw);
	}

	private static boolean flag(EModelElement element, String key) {
		return Boolean.parseBoolean(detail(element, key));
	}

	/** Returns the mapping annotation on {@code element}, creating (and attaching) it if absent. */
	private static EAnnotation annotation(EModelElement element) {
		EAnnotation a = element.getEAnnotation(SOURCE);
		if (a == null) {
			a = EcoreFactory.eINSTANCE.createEAnnotation();
			a.setSource(SOURCE);
			element.getEAnnotations().add(a);
		}
		return a;
	}

	static String detail(EModelElement element, String key) {
		if (element == null || element.getEAnnotation(SOURCE) == null) {
			return null;
		}
		return element.getEAnnotation(SOURCE).getDetails().get(key);
	}
}
