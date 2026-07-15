/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Interface for a provider mapping registry
 * @author Mark Hoffmann
 * @since 04.07.2025
 */
@ProviderType
public interface ProviderMappingRegistry {
	
	/**
	 * Registers a {@link ProviderMapping}
	 * @param providerMapping the mapping to register
	 */
	void registerModelMapping(ProviderMapping providerMapping);

	/**
	 * Unregisteres a {@link ProviderMapping}
	 * @param providerMapping the mapping to un-register
	 */
	void unregisterModelMapping(ProviderMapping providerMapping);
	
	/**
	 * Returns all {@link ProviderMapping} for the given {@link EClass} or an empty {@link List}
	 * @param eclass the {@link EClass} to get the {@link ProviderMapping}'s for. Parameter must not be <code>null</code>
	 * @return List of {@link ProviderMapping} or an empty {@link List}
	 * @throws NullPointerException, if the {@link EClass} parameter is <code>null</code>
	 */
	List<ProviderMapping> getProviderMapping(EClass eclass);
	
	/**
	 * Clears the registry
	 */
	void dispose();
	
}
