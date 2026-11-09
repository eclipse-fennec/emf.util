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

import static java.util.Objects.requireNonNull;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.Slot;

/**
 * One value a source object offers to one slot of one target object, after extraction and before
 * it is written into the target.
 *
 * @param mapping the mapping
 * @param slot the slot
 * @param targetId the id of the target object
 * @param value the extracted value (stages 1 and 2 of the {@link ValueExtractor})
 * @param source the source object it came from
 */
public record MappedValue(Mapping mapping, Slot slot, String targetId, Object value, EObject source) {

	/**
	 * Canonical constructor.
	 */
	public MappedValue {
		requireNonNull(mapping, "Mapping is required");
		requireNonNull(slot, "Slot is required");
		requireNonNull(targetId, "Target id is required");
		requireNonNull(value, "Value is required");
		requireNonNull(source, "Source object is required");
	}
}
