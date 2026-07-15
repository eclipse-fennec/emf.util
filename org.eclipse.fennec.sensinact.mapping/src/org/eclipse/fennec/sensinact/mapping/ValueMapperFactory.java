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
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping;

import org.eclipse.fennec.sensinact.mapping.impl.ValueMapperImpl;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;

/**
 * Factory for creating {@link ValueMapper} instances.
 *
 * @author Mark Hoffmann
 * @since 31.07.2025
 */
public class ValueMapperFactory {

    /**
     * Creates a new ValueMapper instance configured for the specified provider mapping and digital twin.
     *
     * @param twin The SensiNact digital twin to update with mapped values
     * @param mapping The ProviderMapping configuration defining the transformation rules
     * @return A configured ValueMapper instance
     * @throws IllegalArgumentException if twin or mapping is null
     */
    public static ValueMapper createValueMapper(SensinactDigitalTwin twin, ProviderMapping mapping) {
        return new ValueMapperImpl(twin, mapping);
    }
}
