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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;

/**
 * The annotations {@link MofImporter} writes, and helpers to read them back.
 * <p>
 * Every CIM qualifier except {@code Description} (which becomes the GenModel
 * {@code documentation}) is stored as a detail of the {@link #SOURCE} annotation on the
 * element it qualifies, keyed by the qualifier name as declared. Values are encoded as:
 * <ul>
 * <li>boolean qualifiers → {@code "true"}/{@code "false"} ({@code [Key]} is {@code "true"});</li>
 * <li>scalar values → the value itself (strings unquoted, integers in decimal);</li>
 * <li>array values ({@code ValueMap}, {@code Values}, …) → a JSON array, e.g.
 * {@code ["0","2","3"]}; read it with {@link #qualifierValues(EModelElement, String)}.</li>
 * </ul>
 * Besides the qualifiers the importer adds the keys {@link #CIM_TYPE}, {@link #KEYS},
 * {@link #OVERRIDE_PREFIX}{@code <name>}, {@link #DEFAULT}, {@link #UNRESOLVED_SUPERCLASS} and
 * {@link #UNRESOLVED_TYPE}. Qualifier names are matched case-insensitively by the helpers, as
 * in CIM.
 */
public final class MofAnnotations {

	/** Source of the CIM annotation on classes, features, operations and parameters. */
	public static final String SOURCE = "http://schemas.dmtf.org/wbem/cim";

	/** Feature/operation/parameter detail: the CIM type as written, e.g. {@code uint16[]}, {@code datetime}, {@code CIM_ManagedElement REF}. */
	public static final String CIM_TYPE = "cimType";

	/**
	 * Class detail: the comma-separated key properties of the object path, inherited ones
	 * first, e.g. {@code CreationClassName,DeviceID,SystemCreationClassName,SystemName}.
	 */
	public static final String KEYS = "keys";

	/**
	 * Class detail prefix for a property or method a subclass overrides. Ecore cannot redefine an
	 * inherited feature, so the override is kept on the class: {@code override.<name>} holds the
	 * (possibly narrowed) type, {@code override.<name>.<Qualifier>} each qualifier the override
	 * declares.
	 */
	public static final String OVERRIDE_PREFIX = "override.";

	/** Feature detail: the default value of an array property, as JSON array. */
	public static final String DEFAULT = "default";

	/** Class detail (lenient mode only): the declared superclass that was not found. */
	public static final String UNRESOLVED_SUPERCLASS = "unresolvedSuperclass";

	/** Feature detail (lenient mode only): the declared {@code REF} target class that was not found. */
	public static final String UNRESOLVED_TYPE = "unresolvedType";

	private MofAnnotations() {
	}

	/** The raw annotation value of a qualifier (case-insensitive name), or {@code null} if absent. */
	public static String qualifier(EModelElement element, String qualifierName) {
		EAnnotation annotation = element.getEAnnotation(SOURCE);
		if (annotation == null) {
			return null;
		}
		for (Map.Entry<String, String> e : annotation.getDetails()) {
			if (e.getKey().equalsIgnoreCase(qualifierName)) {
				return e.getValue();
			}
		}
		return null;
	}

	/** Whether a boolean qualifier ({@code Key}, {@code Abstract}, {@code Association}, …) is set to {@code true}. */
	public static boolean isSet(EModelElement element, String qualifierName) {
		return "true".equalsIgnoreCase(qualifier(element, qualifierName));
	}

	/**
	 * The values of an array qualifier such as {@code ValueMap} or {@code Values}, in declaration
	 * order; an empty list if absent. A scalar value is returned as a one-element list.
	 */
	public static List<String> qualifierValues(EModelElement element, String qualifierName) {
		String raw = qualifier(element, qualifierName);
		if (raw == null) {
			return List.of();
		}
		return raw.startsWith("[") ? parseJsonArray(raw) : List.of(raw);
	}

	/** The key property names of a class (see {@link #KEYS}); empty for keyless classes. */
	public static List<String> keys(EClass eClass) {
		String raw = qualifier(eClass, KEYS);
		return raw == null || raw.isEmpty() ? List.of() : List.of(raw.split(","));
	}

	/** The original CIM type of a typed element, or {@code null}. */
	public static String cimType(EModelElement element) {
		return qualifier(element, CIM_TYPE);
	}

	// --- encoding ----------------------------------------------------------------------------

	static String toJsonArray(List<MofAst.Literal> elements) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < elements.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			MofAst.Literal l = elements.get(i);
			switch (l.kind()) {
			case INTEGER, REAL, BOOLEAN, NULL -> sb.append(l.text());
			default -> appendJsonString(sb, l.text());
			}
		}
		return sb.append(']').toString();
	}

	private static void appendJsonString(StringBuilder sb, String s) {
		sb.append('"');
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '"' -> sb.append("\\\"");
			case '\\' -> sb.append("\\\\");
			case '\n' -> sb.append("\\n");
			case '\r' -> sb.append("\\r");
			case '\t' -> sb.append("\\t");
			case '\b' -> sb.append("\\b");
			case '\f' -> sb.append("\\f");
			default -> {
				if (c < 0x20) {
					sb.append(String.format("\\u%04x", (int) c));
				} else {
					sb.append(c);
				}
			}
			}
		}
		sb.append('"');
	}

	/** Parses the flat JSON arrays written by {@link #toJsonArray(List)}; non-strings come back as their text. */
	static List<String> parseJsonArray(String json) {
		List<String> out = new ArrayList<>();
		int i = 1; // after '['
		int n = json.length();
		while (i < n) {
			char c = json.charAt(i);
			if (c == ']' ) {
				break;
			}
			if (c == ',' || Character.isWhitespace(c)) {
				i++;
			} else if (c == '"') {
				StringBuilder sb = new StringBuilder();
				i++;
				while (i < n && json.charAt(i) != '"') {
					char d = json.charAt(i);
					if (d == '\\' && i + 1 < n) {
						char e = json.charAt(++i);
						switch (e) {
						case 'n' -> sb.append('\n');
						case 'r' -> sb.append('\r');
						case 't' -> sb.append('\t');
						case 'b' -> sb.append('\b');
						case 'f' -> sb.append('\f');
						case 'u' -> {
							sb.append((char) Integer.parseInt(json.substring(i + 1, i + 5), 16));
							i += 4;
						}
						default -> sb.append(e);
						}
					} else {
						sb.append(d);
					}
					i++;
				}
				out.add(sb.toString());
				i++; // closing quote
			} else {
				int start = i;
				while (i < n && json.charAt(i) != ',' && json.charAt(i) != ']') {
					i++;
				}
				out.add(json.substring(start, i).trim());
			}
		}
		return out;
	}
}
