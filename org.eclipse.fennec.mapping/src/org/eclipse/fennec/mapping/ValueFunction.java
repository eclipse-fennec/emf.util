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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Produces a value that a feature path cannot express. A mapping selects a function by naming its
 * id in {@code ValueSource.functionId}; in OSGi a function is a whiteboard service carrying the
 * {@link #FUNCTION_ID} property, without OSGi it is registered with a {@link FunctionRegistry}.
 * <p>
 * What it receives depends on the value source: with no feature path the whole source object,
 * with one the value the path found (a whole collection when the path ends on one and the source
 * does not narrow it). A function runs before the converter. It is also the identity resolver of
 * last resort: given the source object, it returns the target id.
 * <p>
 * Implementations must be thread-safe and should be free of side effects.
 */
@ConsumerType
@FunctionalInterface
public interface ValueFunction {

	/** Service property naming the id this function answers to. */
	String FUNCTION_ID = "fennec.mapping.function.id";

	/**
	 * @param input the source object, or the value the feature path produced
	 * @return the produced value, or {@code null} to produce none
	 * @throws RuntimeException if the input yields no value; treated like {@code null} and logged
	 */
	Object apply(Object input);
}
