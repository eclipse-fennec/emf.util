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
package org.eclipse.fennec.mapping.ocl;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.m2x.ocl.api.OclContext;
import org.eclipse.fennec.m2x.ocl.api.OclEngine;
import org.eclipse.fennec.m2x.ocl.api.OclInvalid;
import org.eclipse.fennec.m2x.ocl.api.OclParseException;
import org.eclipse.fennec.mapping.ExpressionEvaluator;

/**
 * The {@link ExpressionEvaluator} of the mapping, backed by the Fennec OCL engine. {@code self} is
 * the source object: {@code self.value * 1.8 + 32}, {@code self.readings->size()},
 * {@code if self.battery < 10 then 'low' else 'ok' endif}.
 * <p>
 * Nothing here throws at the mapping: an expression that does not parse, fails, or yields OCL
 * {@code null} or {@code invalid} produces no value, and each such expression is logged once.
 * <p>
 * Parsing is the engine's business: an engine with an expression cache parses an expression once
 * and remembers a parse failure as well (emf.m2x#420), keyed by the model fingerprint when it uses
 * the fingerprint cache. An engine without a cache parses on every value.
 * <p>
 * Ported from the event atlas mapping ({@code OclExpressionEvaluator}).
 */
public final class OclExpressionEvaluator implements ExpressionEvaluator {

	private static final Logger LOGGER = Logger.getLogger(OclExpressionEvaluator.class.getName());

	private final OclEngine engine;
	private final Set<String> reported = ConcurrentHashMap.newKeySet();

	/**
	 * @param engine the OCL engine; with an expression cache, so an expression is not parsed per value
	 */
	public OclExpressionEvaluator(OclEngine engine) {
		this.engine = requireNonNull(engine, "OclEngine is required");
	}

	@Override
	public Optional<Object> evaluate(String expression, EObject self) {
		return evaluate(expression, self, Map.of());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The variables are OCL external variables; a {@code null} value is OCL null.
	 */
	@Override
	public Optional<Object> evaluate(String expression, EObject self, Map<String, Object> variables) {
		if (expression == null || expression.isBlank() || self == null) {
			return Optional.empty();
		}
		String key = key(expression, self.eClass(), variables);
		try {
			Object result = engine.evaluate(expression, context(self, variables));
			if (result == null) {
				return Optional.empty();
			}
			if (OclInvalid.INSTANCE.equals(result)) {
				report(key, Level.FINE, "Expression '" + expression + "' is invalid on a " + self.eClass().getName(), null);
				return Optional.empty();
			}
			return Optional.of(result);
		} catch (OclParseException e) {
			report(key, Level.WARNING, "Expression '" + expression + "' does not parse against " + self.eClass().getName()
					+ ": " + e.getMessage() + " - it produces no value until the mapping or the model is corrected", null);
			return Optional.empty();
		} catch (RuntimeException e) {
			report(key, Level.WARNING, "Expression '" + expression + "' failed on a " + self.eClass().getName(), e);
			return Optional.empty();
		}
	}

	private static OclContext context(EObject self, Map<String, Object> variables) {
		Resource resource = self.eResource();
		ResourceSet resourceSet = resource == null ? null : resource.getResourceSet();
		return new OclContext(self, null, variables, resourceSet);
	}

	/** Names an expression on a context type with the names of its variables, to log it once. */
	private static String key(String expression, EClass contextType, Map<String, Object> variables) {
		String nsURI = contextType.getEPackage() == null ? "" : contextType.getEPackage().getNsURI();
		return nsURI + '#' + contextType.getName() + new TreeSet<>(variables.keySet()) + '\u0000' + expression;
	}

	private void report(String key, Level level, String message, Exception cause) {
		LOGGER.log(reported.add(key) ? level : Level.FINE, message, cause);
	}
}
