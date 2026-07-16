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

import java.util.Map;

import org.eclipse.emf.ecore.EObject;

/**
 * Maps EObject instances to SensiNact provider values using ProviderMapping configurations.
 * This interface provides the transformation layer between domain-specific ECore model instances
 * and the standardized SensiNact provider/service/resource structure.
 * 
 * The ValueMapper is configured with a digital twin and provider mapping, then can process
 * multiple EObject instances using the same configuration.
 * 
 * @author Mark Hoffmann
 * @since 31.07.2025
 */
public interface ValueMapper {

    /**
     * Maps an EObject instance to SensiNact provider values and updates the configured digital twin.
     * Uses the ProviderMapping configuration that was set during ValueMapper creation.
     * 
     * @param sourceInstance The EObject instance containing the source data
     * @throws ValueMappingException if the mapping fails due to invalid paths, type conversion errors, or other issues
     */
    void mapInstance(EObject sourceInstance) throws ValueMappingException;

    /**
     * Extracts resource values from an EObject instance using the configured ProviderMapping.
     * This method extracts the values but doesn't update the twin - useful for inspection or testing.
     * 
     * @param sourceInstance The EObject instance containing the source data
     * @return A map where keys are resource paths (service.resource) and values are the extracted values
     */
    Map<String, Object> mapResourceValues(EObject sourceInstance);

    /**
     * Validates that an EObject instance is compatible with the configured ProviderMapping.
     * 
     * @param sourceInstance The EObject instance to validate
     * @return A ValidationResult containing any errors or warnings
     */
    ValidationResult validateInstance(EObject sourceInstance);

    /**
     * Result of instance validation containing errors and warnings.
     */
    interface ValidationResult {
        boolean isValid();
        java.util.List<String> getErrors();
        java.util.List<String> getWarnings();
    }
}
