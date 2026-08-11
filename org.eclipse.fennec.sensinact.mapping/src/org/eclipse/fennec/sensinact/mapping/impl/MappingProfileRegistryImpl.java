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

import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryConstants;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi service implementation for managing mapping profiles. Collects
 * {@link MappingProfile}s from the named EObject registry {@code sensinact-profiles}
 * (override via the {@code emf.eobject.registry.name} component property): published as
 * an {@link EObjectRegistryListener} whiteboard service, the registry binds it and
 * replays the current content. Non-MappingProfile content or a missing profileId skips
 * the entry with a log; the programmatic {@link #registerProfile(MappingProfile)} API is
 * unchanged.
 *
 * @author Mark Hoffmann
 * @since 15.07.2025
 */
@Component(immediate = true, service = { MappingProfileRegistry.class, EObjectRegistryListener.class }, //
        property = EObjectRegistryConstants.EMF_EOBJECT_REGISTRY_NAME + "=sensinact-profiles")
public class MappingProfileRegistryImpl implements MappingProfileRegistry, EObjectRegistryListener {

    private static final Logger logger = LoggerFactory.getLogger(MappingProfileRegistryImpl.class);

    private final Map<String, MappingProfile> profiles = new ConcurrentHashMap<>();

    /*
     * (non-Javadoc)
     * @see org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener#entryAdded(org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry)
     */
    @Override
    public void entryAdded(EObjectRegistryEntry entry) {
        validProfile(entry, false).ifPresent(profile -> profiles.put(profile.getProfileId(), profile));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener#entryUpdated(org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry, org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry)
     */
    @Override
    public void entryUpdated(EObjectRegistryEntry entry, EObjectRegistryEntry oldEntry) {
        // index the new profile before dropping the old one, so lookups never see a gap
        validProfile(entry, false).ifPresent(profile -> profiles.put(profile.getProfileId(), profile));
        validProfile(oldEntry, true).ifPresent(old -> profiles.remove(old.getProfileId(), old));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener#entryRemoved(org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry)
     */
    @Override
    public void entryRemoved(EObjectRegistryEntry entry) {
        validProfile(entry, true).ifPresent(profile -> profiles.remove(profile.getProfileId(), profile));
    }

    /**
     * Validation of registry content, applied uniformly to every source. Quiet mode is
     * used for old/removed entries: an entry that never passed validation was never
     * indexed, so its removal must not log again.
     */
    private Optional<MappingProfile> validProfile(EObjectRegistryEntry entry, boolean quiet) {
        if (!(entry.object() instanceof MappingProfile profile)) {
            if (!quiet) {
                logger.warn("Registry entry '{}' is a {} - expected MappingProfile, skipping", entry.key(),
                        entry.object().eClass().getName());
            }
            return Optional.empty();
        }
        if (profile.getProfileId() == null || profile.getProfileId().isBlank()) {
            if (!quiet) {
                logger.error("MappingProfile '{}' has no profileId - skipping", entry.key());
            }
            return Optional.empty();
        }
        return Optional.of(profile);
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
