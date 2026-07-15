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
package org.eclipse.fennec.sensinact.mapping.impl;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.fennec.sensinact.mapping.MappingProfileRegistry;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin;
import org.eclipse.fennec.sensinact.model.mapping.ProfileProvider;
import org.eclipse.fennec.sensinact.model.mapping.ProfileResource;
import org.eclipse.fennec.sensinact.model.mapping.ProfileService;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.fennec.sensinact.model.mapping.ResourceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ServiceMapping;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * OSGi service implementation for managing mapping profiles.
 * 
 * @author Mark Hoffmann
 * @since 15.07.2025
 */
@Component(immediate = true, service = MappingProfileRegistry.class)
public class MappingProfileRegistryImpl implements MappingProfileRegistry {

    private final Map<String, MappingProfile> profiles = new ConcurrentHashMap<>();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addProfile(MappingProfile profile) {
        requireNonNull(profile);
        requireNonNull(profile.getProfileId());
        profiles.put(profile.getProfileId(), profile);
    }

    public void removeProfile(MappingProfile profile) {
        if (profile != null && profile.getProfileId() != null) {
            profiles.remove(profile.getProfileId());
        }
    }

    @Override
    public Optional<MappingProfile> getProfile(String profileId) {
        requireNonNull(profileId);
        return Optional.ofNullable(profiles.get(profileId));
    }

    @Override
    public List<MappingProfile> getAllProfiles() {
        return new ArrayList<>(profiles.values());
    }

    @Override
    public void registerProfile(MappingProfile profile) {
        requireNonNull(profile);
        requireNonNull(profile.getProfileId());
        
        if (profiles.containsKey(profile.getProfileId())) {
            throw new IllegalArgumentException("Profile with ID '" + profile.getProfileId() + "' already exists");
        }
        
        profiles.put(profile.getProfileId(), profile);
    }

    @Override
    public boolean unregisterProfile(String profileId) {
        requireNonNull(profileId);
        return profiles.remove(profileId) != null;
    }
    
    /* 
     * (non-Javadoc)
     * @see org.gecko.emf.sensinact.model.MappingProfileRegistry#dispose()
     */
    @Override
    @Deactivate
    public void dispose() {
    	synchronized (profiles) {
    		profiles.keySet().forEach(this::unregisterProfile);
    		profiles.clear();
		}
    }

    @Override
    public ValidationResult validateMapping(ProviderMapping mapping) {
        requireNonNull(mapping);
        
        ValidationResultImpl result = new ValidationResultImpl();
        
        // If no profile is referenced, validation passes (backward compatibility)
        if (mapping.getProfile() == null) {
            return result;
        }
        
        MappingProfile profile = mapping.getProfile();
        ProfileProvider profileProvider = profile.getProvider();
        
        // Validate admin service
        if (profileProvider.getAdmin() != null) {
            validateAdminService(mapping, profileProvider.getAdmin(), result);
        }
        
        // Validate services
        validateServices(mapping, profileProvider, result);
        
        return result;
    }

    private void validateAdminService(ProviderMapping mapping, ProfileAdmin profileAdmin, ValidationResultImpl result) {
        if (mapping.getAdmin() == null) {
            if (profileAdmin.isRequired()) {
                result.addError("Admin service is required by profile but not present in mapping");
            }
            return;
        }
        
        // Check location requirement
        if (profileAdmin.isRequiresLocation()) {
            if (mapping.getAdmin().getLatitude() == null && 
                (mapping.getAdmin().getLatitudeRef() == null || mapping.getAdmin().getLatitudeRef().isEmpty())) {
                result.addError("Admin service requires location but latitude is not configured");
            }
            if (mapping.getAdmin().getLongitude() == null && 
                (mapping.getAdmin().getLongitudeRef() == null || mapping.getAdmin().getLongitudeRef().isEmpty())) {
                result.addError("Admin service requires location but longitude is not configured");
            }
        }
        
        // Check friendly name requirement
        if (profileAdmin.isRequiresFriendlyName()) {
            if (mapping.getAdmin().getFriendlyName() == null && 
                (mapping.getAdmin().getFriendlyNameFeature() == null || mapping.getAdmin().getFriendlyNameFeature().isEmpty())) {
                result.addError("Admin service requires friendly name but it is not configured");
            }
        }
    }

    private void validateServices(ProviderMapping mapping, ProfileProvider profileProvider, ValidationResultImpl result) {
        Map<String, ServiceMapping> mappingServices = new ConcurrentHashMap<>();
        
        // Index mapping services by ID
        for (ServiceMapping service : mapping.getServices()) {
            mappingServices.put(service.getMid(), service);
        }
        
        // Validate each required service from profile
        for (ProfileService profileService : profileProvider.getServices()) {
            ServiceMapping mappingService = mappingServices.get(profileService.getServiceId());
            
            if (mappingService == null) {
                if (profileService.isRequired()) {
                    result.addError("Required service '" + profileService.getServiceId() + "' is missing from mapping");
                }
                continue;
            }
            
            // Validate service resources
            validateServiceResources(mappingService, profileService, result);
        }
    }

    private void validateServiceResources(ServiceMapping mappingService, ProfileService profileService, ValidationResultImpl result) {
        Map<String, ResourceMapping> mappingResources = new ConcurrentHashMap<>();
        
        // Index mapping resources by ID
        for (ResourceMapping resource : mappingService.getResources()) {
            mappingResources.put(resource.getMid(), resource);
        }
        
        // Validate each required resource from profile
        for (ProfileResource profileResource : profileService.getResources()) {
            ResourceMapping mappingResource = mappingResources.get(profileResource.getResourceId());
            
            if (mappingResource == null) {
                if (profileResource.isRequired()) {
                    result.addError("Required resource '" + profileResource.getResourceId() + 
                                   "' is missing from service '" + profileService.getServiceId() + "'");
                }
                continue;
            }
            
            // Validate resource type compatibility
            if (profileResource.getExpectedType() != null) {
                if (!profileResource.getExpectedType().equals(mappingResource.getEAttributeType())) {
                    result.addWarning("Resource '" + profileResource.getResourceId() + 
                                    "' has type '" + mappingResource.getEAttributeType().getName() + 
                                    "' but profile expects '" + profileResource.getExpectedType().getName() + "'");
                }
            }
            
            // Validate unit compatibility
            if (profileResource.getExpectedUnit() != null) {
                if (!profileResource.getExpectedUnit().equals(mappingResource.getUnit())) {
                    result.addWarning("Resource '" + profileResource.getResourceId() + 
                                    "' has unit '" + mappingResource.getUnit() + 
                                    "' but profile expects '" + profileResource.getExpectedUnit() + "'");
                }
            }
        }
    }

    @Override
    public boolean isCompatible(String profileId, String version) {
        requireNonNull(profileId);
        requireNonNull(version);
        
        Optional<MappingProfile> profile = getProfile(profileId);
        if (profile.isEmpty()) {
            return false;
        }
        
        // Simple version compatibility check (could be enhanced with semantic versioning)
        return version.equals(profile.get().getVersion());
    }

    /**
     * Implementation of ValidationResult
     */
    private static class ValidationResultImpl implements ValidationResult {
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();

        @Override
        public boolean isValid() {
            return errors.isEmpty();
        }

        @Override
        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        @Override
        public List<String> getWarnings() {
            return Collections.unmodifiableList(warnings);
        }

        public void addError(String error) {
            errors.add(error);
        }

        public void addWarning(String warning) {
            warnings.add(warning);
        }
    }
}
