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
import java.util.Locale;

/**
 * The parsed form of DMTF MOF class declarations — the input of the Ecore mapping. Only what
 * the importer maps is kept; instance declarations are parsed and dropped.
 */
final class MofAst {

	private MofAst() {
	}

	/** A literal constant. {@code text} is the decoded value (strings unescaped, integers in decimal). */
	record Literal(Kind kind, String text) {

		enum Kind {
			STRING, CHAR, INTEGER, REAL, BOOLEAN, NULL, ALIAS
		}
	}

	/**
	 * A qualifier or initializer value: either a single {@link Literal} or an array
	 * ({@code isArray}, the elements in {@code elements}).
	 */
	record Value(Literal scalar, List<Literal> elements) {

		static Value of(Literal l) {
			return new Value(l, null);
		}

		static Value array(List<Literal> elements) {
			return new Value(null, List.copyOf(elements));
		}

		boolean isArray() {
			return elements != null;
		}
	}

	/** A qualifier as written: {@code Key}, {@code MaxLen(64)}, {@code ValueMap {"0", "1"}}. */
	record Qualifier(String name, Value value) {

		boolean is(String qualifierName) {
			return name.equalsIgnoreCase(qualifierName);
		}

		/** Boolean qualifiers written without a value ({@code [Key]}) mean {@code true}. */
		boolean isTrue() {
			if (value == null) {
				return true;
			}
			Literal l = value.scalar();
			return l != null && l.kind() == Literal.Kind.BOOLEAN && Boolean.parseBoolean(l.text());
		}
	}

	/**
	 * A data type: a CIM intrinsic ({@code uint32}, {@code string}, {@code datetime}, …) or, if
	 * {@code ref} is set, a reference to the class {@code name}. {@code array} is {@code -1} for
	 * a scalar, {@code 0} for a variable-length array, {@code n} for a fixed-size array.
	 */
	record Type(String name, boolean ref, int array) {

		boolean isArray() {
			return array >= 0;
		}

		String lowerName() {
			return name.toLowerCase(Locale.ROOT);
		}

		/** The type as written in MOF, e.g. {@code uint16[]}, {@code CIM_ManagedElement REF}. */
		String display() {
			String base = ref ? name + " REF" : name;
			if (array < 0) {
				return base;
			}
			return base + "[" + (array > 0 ? Integer.toString(array) : "") + "]";
		}
	}

	record Property(List<Qualifier> qualifiers, Type type, String name, Value defaultValue) {
	}

	record Parameter(List<Qualifier> qualifiers, Type type, String name) {
	}

	record Method(List<Qualifier> qualifiers, Type returnType, String name, List<Parameter> parameters) {
	}

	/** A class (also association / indication) declaration. {@code location} is {@code file:line}. */
	record ClassDecl(List<Qualifier> qualifiers, String name, String superclass, List<Property> properties,
			List<Method> methods, String location) {
	}

	/** A {@code Qualifier} declaration — only the name and whether the type is an array matter. */
	record QualifierDecl(String name, Type type) {
	}
}
