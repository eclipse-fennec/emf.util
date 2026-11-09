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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.mapping.Mapping;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Where a runtime finds the mappings: by id, by the target class they fill and by the source
 * classes they read. The plain-Java implementation is {@link InMemoryMappingRegistry}; in OSGi the
 * mappings come from the EObject registry of emf.osgi and appear on the metadata of their target
 * class.
 * <p>
 * Mapping ids are unique within a registry.
 */
@ProviderType
public interface MappingRegistry {

	/**
	 * @param id the mapping id
	 * @return the mapping
	 */
	Optional<Mapping> get(String id);

	/**
	 * @return all mappings
	 */
	Collection<Mapping> all();

	/**
	 * @param target the target class
	 * @return the mappings onto that class
	 */
	List<Mapping> forTarget(EClass target);

	/**
	 * @param source the class of a source object
	 * @return the mappings with a slot reading objects of that class (or a superclass), and those
	 *         onto that class (or a superclass) that include unmapped features: their source is the
	 *         target itself
	 */
	List<Mapping> forSource(EClass source);

	/**
	 * @param listener notified of every later change; it is not replayed the current content
	 */
	void addListener(Listener listener);

	/**
	 * @param listener the listener to remove
	 */
	void removeListener(Listener listener);

	/**
	 * Notified when a mapping appears, changes or goes.
	 */
	@FunctionalInterface
	interface Listener {

		/**
		 * @param previous the mapping registered before under the id, or {@code null}
		 * @param current the mapping registered now, or {@code null} when it was removed
		 */
		void changed(Mapping previous, Mapping current);
	}
}
