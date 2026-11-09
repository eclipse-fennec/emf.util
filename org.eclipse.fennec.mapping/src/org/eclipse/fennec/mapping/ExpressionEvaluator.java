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
package org.eclipse.fennec.mapping;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Evaluates the expression of a value source - OCL, with {@code self} bound to the source object.
 * <p>
 * Nothing in this bundle implements it, so the engine resolves without an expression language;
 * an evaluator is handed to the {@link ValueExtractor} (or arrives as a service). Without one, an
 * expression produces nothing, which is what an unreadable feature path does as well.
 * <p>
 * Implementations must be thread-safe: one evaluator serves every source object.
 */
@ConsumerType
public interface ExpressionEvaluator {

	/**
	 * Evaluates an expression.
	 *
	 * @param expression the expression text; blank means none was asked for
	 * @param self the source object
	 * @return the computed value; empty when the expression cannot be parsed, fails, or yields
	 *         {@code null} or {@code invalid} - one unmappable slot must not cost the others
	 */
	Optional<Object> evaluate(String expression, EObject self);

	/**
	 * Evaluates an expression with further variables bound beside {@code self}, e.g. the new and
	 * the last value of a change rule.
	 * <p>
	 * The default supports no variables: without any it evaluates as {@link #evaluate(String, EObject)},
	 * with some it produces nothing, because an expression naming them could not be evaluated faithfully.
	 *
	 * @param expression the expression text
	 * @param self the source object
	 * @param variables further variables by name, never {@code null}
	 * @return the computed value, or empty
	 */
	default Optional<Object> evaluate(String expression, EObject self, Map<String, Object> variables) {
		return variables.isEmpty() ? evaluate(expression, self) : Optional.empty();
	}
}
