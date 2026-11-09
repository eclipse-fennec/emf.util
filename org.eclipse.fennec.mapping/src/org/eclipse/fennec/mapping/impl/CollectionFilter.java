/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.mapping.impl;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * The parsed {@code collectionFilter} of a value source: {@code path.to.feature=value}, the value
 * optionally in single or double quotes. An element matches when following the path from it
 * yields a value whose string form (an enum's name) equals the literal.
 * <p>
 * Same syntax and semantics as the collection filter of the Fennec event atlas mapping.
 */
public final class CollectionFilter {

	private final String[] path;
	private final String expected;

	private CollectionFilter(String[] path, String expected) {
		this.path = path;
		this.expected = expected;
	}

	/**
	 * Parses a filter expression.
	 *
	 * @param expression the expression; may be {@code null} or blank
	 * @return the filter, or empty when none is set or it is malformed
	 */
	public static Optional<CollectionFilter> parse(String expression) {
		if (expression == null || expression.isBlank()) {
			return Optional.empty();
		}
		int equals = expression.indexOf('=');
		if (equals < 1) {
			return Optional.empty();
		}
		String[] path = expression.substring(0, equals).trim().split("\\.");
		for (String segment : path) {
			if (segment.isBlank()) {
				return Optional.empty();
			}
		}
		return Optional.of(new CollectionFilter(path, unquote(expression.substring(equals + 1).trim())));
	}

	/**
	 * @param expression a filter expression
	 * @return whether the expression is blank or well-formed
	 */
	public static boolean isWellFormed(String expression) {
		return expression == null || expression.isBlank() || parse(expression).isPresent();
	}

	/**
	 * @param collection the collection
	 * @return the first matching element, or empty
	 */
	public Optional<Object> select(List<?> collection) {
		for (Object element : collection) {
			if (matches(element)) {
				return Optional.of(element);
			}
		}
		return Optional.empty();
	}

	/**
	 * @param element an element of a collection
	 * @return whether it matches
	 */
	public boolean matches(Object element) {
		Object value = element;
		for (String segment : path) {
			if (!(value instanceof EObject current)) {
				return false;
			}
			EStructuralFeature feature = current.eClass().getEStructuralFeature(segment);
			if (feature == null) {
				return false;
			}
			value = current.eGet(feature);
		}
		return value != null && expected.equals(literal(value));
	}

	private static String literal(Object value) {
		return value instanceof Enumerator enumerator ? enumerator.getName() : String.valueOf(value);
	}

	private static String unquote(String literal) {
		if (literal.length() > 1 && (literal.startsWith("\"") && literal.endsWith("\"")
				|| literal.startsWith("'") && literal.endsWith("'"))) {
			return literal.substring(1, literal.length() - 1);
		}
		return literal;
	}

	@Override
	public String toString() {
		return String.join(".", path) + "=" + expected;
	}
}
