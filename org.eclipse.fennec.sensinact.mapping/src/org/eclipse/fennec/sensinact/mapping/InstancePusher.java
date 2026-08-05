/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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

import org.eclipse.emf.ecore.EObject;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Ingress for domain model instances: looks up the {@link ProviderMappingRegistry} for
 * mappings matching the instance's EClass and pushes the instance into the sensinact
 * digital twin through each of them.
 * <p>
 * The lookup is keyed by the exact {@link org.eclipse.emf.ecore.EClass} instance, so the
 * incoming object must be created from the same EPackage the registered mappings resolved
 * their provider classes against.
 * @author Ilenia Salvadori
 * @since 29.07.2026
 */
@ProviderType
public interface InstancePusher {

	/**
	 * Pushes a domain model instance into the sensinact digital twin using all registered
	 * {@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping}s whose provider
	 * classes match the instance's EClass. Mappings that fail to apply are logged and
	 * skipped; the push is executed synchronously on the sensinact gateway thread.
	 * @param instance the instance to push. Parameter must not be <code>null</code>
	 * @return the number of mappings successfully applied; 0 if no mapping is registered
	 * for the instance's EClass
	 * @throws NullPointerException if the instance parameter is <code>null</code>
	 * @throws IllegalStateException if the gateway execution fails as a whole
	 */
	int pushInstance(EObject instance);

}
