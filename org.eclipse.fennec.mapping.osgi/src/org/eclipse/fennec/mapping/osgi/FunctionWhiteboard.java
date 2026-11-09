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
package org.eclipse.fennec.mapping.osgi;

import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.fennec.mapping.FunctionRegistry;
import org.eclipse.fennec.mapping.ValueFunction;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * The {@link FunctionRegistry} of a framework: every {@link ValueFunction} service carrying the
 * {@value ValueFunction#FUNCTION_ID} property is registered under that id while it is there.
 * <p>
 * No configuration. Functions come and go at any time (dynamic, multiple), so a mapping naming a
 * function that is not there yet produces no value until it arrives.
 */
@Component(service = FunctionRegistry.class)
public class FunctionWhiteboard extends FunctionRegistry {

	private static final Logger LOGGER = Logger.getLogger(FunctionWhiteboard.class.getName());

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	void addFunction(ValueFunction function, Map<String, Object> properties) {
		String id = Properties.text(properties, ValueFunction.FUNCTION_ID);
		if (id == null) {
			LOGGER.warning(() -> "Ignoring " + function.getClass().getName() + " without the '" + ValueFunction.FUNCTION_ID
					+ "' property - no mapping can name it");
			return;
		}
		register(id, function);
	}

	void removeFunction(ValueFunction function, Map<String, Object> properties) {
		String id = Properties.text(properties, ValueFunction.FUNCTION_ID);
		if (id != null) {
			unregister(id, function);
		}
	}
}
