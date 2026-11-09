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
 * Post-processes a produced value: a unit conversion, a scaling, a parse. A mapping selects a
 * converter by naming its id in {@code ValueSource.converterId} (applied to the produced value)
 * or in {@code Slot.targetConverterId} (applied when the value is written into the target). In
 * OSGi a converter is a whiteboard service carrying the {@link #CONVERTER_ID} property, without
 * OSGi it is registered with a {@link ConverterRegistry}.
 * <p>
 * Implementations must be thread-safe and should be free of side effects.
 */
@ConsumerType
@FunctionalInterface
public interface ValueConverter {

	/** Service property naming the id this converter answers to. */
	String CONVERTER_ID = "fennec.mapping.converter.id";

	/**
	 * @param value the produced value, never {@code null}
	 * @return the converted value, or {@code null} to drop it
	 * @throws RuntimeException if the value cannot be converted; the value is dropped and the failure logged
	 */
	Object convert(Object value);
}
