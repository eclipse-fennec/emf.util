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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.mapping.IdentityResolution;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.Slot;

/**
 * Read helpers over the mapping model: which class a slot reads, which slots and which identity
 * resolution apply to a source class, and the atlas view of a mapping.
 */
public final class Mappings {

	private Mappings() {
	}

	/**
	 * @param mapping the mapping
	 * @param slot one of its slots
	 * @return the class the slot reads: its source class, else the mapping's target
	 */
	public static EClass sourceClassOf(Mapping mapping, Slot slot) {
		return slot.getSourceClass() != null ? slot.getSourceClass() : mapping.getTarget();
	}

	/**
	 * @param resolution an identity resolution of the mapping
	 * @param mapping the mapping
	 * @return the target class it resolves to
	 */
	public static EClass targetClassOf(IdentityResolution resolution, Mapping mapping) {
		return resolution.getTargetClass() != null ? resolution.getTargetClass() : mapping.getTarget();
	}

	/**
	 * Whether objects of a class are read by a slot declared for another class: the same class or a subclass.
	 *
	 * @param declared the class a slot or resolution declares; {@code null} matches nothing
	 * @param actual the class of the object at hand
	 * @return whether it applies
	 */
	public static boolean applies(EClass declared, EClass actual) {
		return declared != null && (declared == actual || declared.isSuperTypeOf(actual));
	}

	/**
	 * @param mapping the mapping
	 * @param sourceClass the class of a source object
	 * @return the slots that read objects of that class, in declaration order
	 */
	public static List<Slot> slotsReading(Mapping mapping, EClass sourceClass) {
		requireNonNull(mapping, "Mapping is required");
		requireNonNull(sourceClass, "Source class is required");
		List<Slot> slots = new ArrayList<>();
		for (Slot slot : mapping.getSlots()) {
			if (applies(sourceClassOf(mapping, slot), sourceClass)) {
				slots.add(slot);
			}
		}
		return slots;
	}

	/**
	 * The identity resolution for objects of a source class: the one declared for the most specific
	 * class the source is an instance of.
	 *
	 * @param mapping the mapping
	 * @param sourceClass the class of a source object
	 * @return the resolution, or empty when none is declared (the source id is then the target id)
	 */
	public static Optional<IdentityResolution> identityFor(Mapping mapping, EClass sourceClass) {
		IdentityResolution best = null;
		for (IdentityResolution resolution : mapping.getIdentities()) {
			EClass declared = resolution.getSourceClass();
			if (applies(declared, sourceClass) && (best == null || best.getSourceClass().isSuperTypeOf(declared))) {
				best = resolution;
			}
		}
		return Optional.ofNullable(best);
	}

	/**
	 * @param eClass a class
	 * @return {@code nsURI#//Name}, or the bare name for a class without package
	 */
	public static String uriOf(EClass eClass) {
		if (eClass.getEPackage() == null) {
			return eClass.getName();
		}
		return eClass.getEPackage().getNsURI() + "#//" + eClass.getName();
	}

	/**
	 * Fills the atlas view of a mapping from its content: {@code supportedEClasses} = the classes its
	 * slots read, {@code resultEClasses} = its target. Atlas tooling uses them to discover what a
	 * transformation reads and writes.
	 *
	 * @param mapping the mapping, changed in place
	 * @return the mapping
	 */
	public static Mapping normalize(Mapping mapping) {
		requireNonNull(mapping, "Mapping is required");
		Set<EClass> sources = new LinkedHashSet<>();
		for (Slot slot : mapping.getSlots()) {
			EClass source = sourceClassOf(mapping, slot);
			if (source != null) {
				sources.add(source);
			}
		}
		mapping.getSupportedEClasses().clear();
		mapping.getSupportedEClasses().addAll(sources);
		mapping.getResultEClasses().clear();
		if (mapping.getTarget() != null) {
			mapping.getResultEClasses().add(mapping.getTarget());
		}
		return mapping;
	}
}
