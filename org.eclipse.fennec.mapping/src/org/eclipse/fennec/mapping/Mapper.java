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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.model.mapping.IdentityResolution;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.Slot;

/**
 * Applies a mapping to source objects: which values a source offers to which target objects
 * ({@link #map}), and writing one into a target ({@link #apply}).
 * <p>
 * It holds no target objects and triggers nothing - when a source is mapped and where its targets
 * come from is the consumer's business (a stream reacts to changes, an indexer pulls). Thread-safe.
 */
public final class Mapper {

	private final ValueExtractor extractor;
	private final IdentityResolver identities;

	/**
	 * @param extractor produces the values
	 * @param identities resolves target ids
	 */
	public Mapper(ValueExtractor extractor, IdentityResolver identities) {
		this.extractor = requireNonNull(extractor, "ValueExtractor is required");
		this.identities = requireNonNull(identities, "IdentityResolver is required");
	}

	/**
	 * @return the extractor
	 */
	public ValueExtractor extractor() {
		return extractor;
	}

	/**
	 * @return the identity resolver
	 */
	public IdentityResolver identities() {
		return identities;
	}

	/**
	 * The values a source object offers under a mapping: one per slot reading the source's class and
	 * per target id the source resolves to. A slot whose value source yields nothing contributes nothing.
	 *
	 * @param mapping the mapping
	 * @param source the source object
	 * @return the values, in slot order and, within a slot, in target order
	 */
	public List<MappedValue> map(Mapping mapping, EObject source) {
		requireNonNull(mapping, "Mapping is required");
		requireNonNull(source, "Source object is required");
		List<Slot> slots = Mappings.slotsReading(mapping, source.eClass());
		if (slots.isEmpty()) {
			return List.of();
		}
		IdentityResolution resolution = Mappings.identityFor(mapping, source.eClass()).orElse(null);
		List<String> targetIds = identities.resolve(resolution, source);
		if (targetIds.isEmpty()) {
			return List.of();
		}
		List<MappedValue> values = new ArrayList<>();
		for (Slot slot : slots) {
			Optional<Object> value = extractor.extract(slot, source);
			if (value.isPresent()) {
				for (String targetId : targetIds) {
					values.add(new MappedValue(mapping, slot, targetId, value.get(), source));
				}
			}
		}
		return values;
	}

	/**
	 * Writes a value into a target object: target converter, coercion, then {@code eSet}. Named
	 * slots (no target feature) cannot be written into an EObject and are refused.
	 *
	 * @param value the value
	 * @param target the target object; its class must have the slot's target feature
	 * @return whether the target was changed
	 * @throws IllegalArgumentException if the slot is named or the target lacks the feature
	 */
	public boolean apply(MappedValue value, EObject target) {
		requireNonNull(value, "MappedValue is required");
		requireNonNull(target, "Target object is required");
		EStructuralFeature feature = value.slot().getTargetFeature();
		if (feature == null) {
			throw new IllegalArgumentException("Slot '" + value.slot().getName() + "' has no target feature");
		}
		if (!target.eClass().getEAllStructuralFeatures().contains(feature)) {
			throw new IllegalArgumentException(
					"Target " + target.eClass().getName() + " has no feature '" + feature.getName() + "'");
		}
		Optional<Object> written = extractor.toTarget(value.slot(), value.value());
		if (written.isEmpty()) {
			return false;
		}
		target.eSet(feature, written.get());
		return true;
	}
}
