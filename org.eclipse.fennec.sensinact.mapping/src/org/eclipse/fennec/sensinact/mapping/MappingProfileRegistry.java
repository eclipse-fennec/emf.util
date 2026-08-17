/*
 * ******************************************************************
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
 * ******************************************************************
 */
package org.eclipse.fennec.sensinact.mapping;

import java.util.List;
import java.util.Optional;

import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Registry service for managing mapping profiles.
 * Provides centralized access to mapping profiles and validation functionality.
 * 
 * @author Mark Hoffmann
 * @since 15.07.2025
 */
/**
 * 
 * @author mark
 * @since 24.07.2025
 */
@ProviderType
public interface MappingProfileRegistry {

    /**
     * Retrieves a mapping profile by its ID.
     * 
     * @param profileId the unique identifier of the profile
     * @return the profile if found, empty otherwise
     */
    Optional<MappingProfile> getProfile(String profileId);

    /**
     * Retrieves all available mapping profiles.
     * 
     * @return list of all registered profiles
     */
    List<MappingProfile> getAllProfiles();

    /**
     * Registers a new mapping profile.
     * 
     * @param profile the profile to register
     * @throws IllegalArgumentException if a profile with the same ID already exists
     */
    void registerProfile(MappingProfile profile);

    /**
     * Unregisters a mapping profile.
     * 
     * @param profileId the ID of the profile to unregister
     * @return true if the profile was found and removed, false otherwise
     */
    boolean unregisterProfile(String profileId);

    /**
     * Validates that a provider mapping conforms to its referenced profile.
     * 
     * @param mapping the provider mapping to validate
     * @return validation result containing any errors or warnings
     */
    ValidationResult validateMapping(ProviderMapping mapping);

    /**
     * Checks if a mapping profile is compatible with another version.
     * 
     * @param profileId the profile ID to check
     * @param version the version to check against
     * @return true if compatible, false otherwise
     */
    boolean isCompatible(String profileId, String version);
    
    /**
     * Cleans up this registry 
     */
    void dispose();

    /**
     * Result of profile validation.
     */
    interface ValidationResult {
        /**
         * @return true if validation passed without errors
         */
        boolean isValid();

        /**
         * @return list of validation errors (empty if valid)
         */
        List<String> getErrors();

        /**
         * @return list of validation warnings (may be empty)
         */
        List<String> getWarnings();
    }
}
