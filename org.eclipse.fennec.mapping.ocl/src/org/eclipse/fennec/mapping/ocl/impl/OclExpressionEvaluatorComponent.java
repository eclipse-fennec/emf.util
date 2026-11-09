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
package org.eclipse.fennec.mapping.ocl.impl;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.m2x.ocl.api.OclEngine;
import org.eclipse.fennec.mapping.ExpressionEvaluator;
import org.eclipse.fennec.mapping.ocl.OclExpressionEvaluator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The {@link ExpressionEvaluator} of a framework: OCL through the M2X {@link OclEngine} service.
 * <p>
 * The engine service parses with its expression cache, which also remembers parse failures; bind it
 * to the fingerprint cache ({@code expressionCache.target=(cache.name=fingerprint)}) so a changed
 * model is parsed again. No configuration.
 */
@Component(service = ExpressionEvaluator.class)
public class OclExpressionEvaluatorComponent implements ExpressionEvaluator {

	private final OclExpressionEvaluator evaluator;

	/**
	 * @param engine the OCL engine
	 */
	@Activate
	public OclExpressionEvaluatorComponent(@Reference(name = "engine") OclEngine engine) {
		this.evaluator = new OclExpressionEvaluator(engine);
	}

	@Override
	public Optional<Object> evaluate(String expression, EObject self) {
		return evaluator.evaluate(expression, self);
	}

	@Override
	public Optional<Object> evaluate(String expression, EObject self, Map<String, Object> variables) {
		return evaluator.evaluate(expression, self, variables);
	}
}
