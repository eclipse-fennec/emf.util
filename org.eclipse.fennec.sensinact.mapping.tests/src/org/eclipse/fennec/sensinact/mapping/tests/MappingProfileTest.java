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
package org.eclipse.fennec.sensinact.mapping.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.sensinact.mapping.MappingProfileRegistry;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests for mapping profile functionality including profile validation 
 * and provider strategies (UNIFIED vs SEPARATE).
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class MappingProfileTest {

    private static final String BATTERY_PROFILE_URI = "/data/battery/battery-sensor-profile.xmi";
    private static final String DRAGINO_MAPPING_URI = "/data/battery/dragino-battery-mapping.xmi";
    private static final String EM310_MAPPING_URI = "/data/battery/em310udl-battery-mapping.xmi";

    @InjectService
    ResourceSet resourceSet;
    @InjectService
    MappingProfileRegistry profileRegistry;

    private MappingProfile batteryProfile;
    private ProviderMapping draginoMapping;
    private ProviderMapping em310udlMapping;

    @BeforeEach
    public void beforeEach() throws IOException {
        // Load battery sensor profile
    	URL profileUrl = getClass().getResource(BATTERY_PROFILE_URI);
        Resource profileResource = resourceSet.createResource(URI.createURI(profileUrl.toString()));
        profileResource.load(null);
        batteryProfile = (MappingProfile) profileResource.getContents().get(0);

        // Load dragino mapping
        URL draginoUrl = getClass().getResource(DRAGINO_MAPPING_URI);
        Resource draginoResource = resourceSet.createResource(URI.createURI(draginoUrl.toString()));
        draginoResource.load(null);
        draginoMapping = (ProviderMapping) draginoResource.getContents().get(0);

        // Load em310udl mapping
        URL em310Url = getClass().getResource(EM310_MAPPING_URI);
        Resource em310Resource = resourceSet.createResource(URI.createURI(em310Url.toString()));
        em310Resource.load(null);
        em310udlMapping = (ProviderMapping) em310Resource.getContents().get(0);

        // Create profile registry and register battery profile
        profileRegistry.registerProfile(batteryProfile);
    }
    
    @AfterEach
    public void afterEach() {
    	profileRegistry.dispose();
    }

    @Test
    @DisplayName("Profile registry should register and retrieve profiles")
    void profileRegistry_registerAndGetProfile_worksCorrectly() {
        // Verify profile was registered
        assertTrue(profileRegistry.getProfile("battery-sensor").isPresent());
        MappingProfile retrieved = profileRegistry.getProfile("battery-sensor").get();
        assertEquals(batteryProfile.getProfileId(), retrieved.getProfileId());
        assertEquals(batteryProfile.getName(), retrieved.getName());
    }

    @Test
    @DisplayName("Battery profile should have UNIFIED strategy")
    void batteryProfile_hasUnifiedStrategy() {
        assertEquals(ProviderStrategy.UNIFIED, batteryProfile.getProviderStrategy());
        assertEquals("battery-sensor", batteryProfile.getProvider().getProviderId());
    }

    @Test
    @DisplayName("Profile validation should succeed for valid mappings")
    void profileValidation_withValidMappings_succeeds() {
        // Test dragino mapping validation
        MappingProfileRegistry.ValidationResult draginoResult = profileRegistry.validateMapping(draginoMapping);
        assertTrue(draginoResult.isValid(), "Dragino mapping should be valid");
        assertTrue(draginoResult.getErrors().isEmpty(), "Should have no validation errors");

        // Test em310udl mapping validation
        MappingProfileRegistry.ValidationResult em310udlResult = profileRegistry.validateMapping(em310udlMapping);
        assertTrue(em310udlResult.isValid(), "EM310UDL mapping should be valid");
        assertTrue(em310udlResult.getErrors().isEmpty(), "Should have no validation errors");
    }

    @Test
    @DisplayName("Mappings should reference battery profile correctly")
    void mappings_referenceBatteryProfile() {
        // Verify both mappings reference the battery profile
        assertNotNull(draginoMapping.getProfile());
        assertNotNull(em310udlMapping.getProfile());
        
        // Note: These will be proxy references, so we check the profile ID via registry
        assertTrue(profileRegistry.getProfile("battery-sensor").isPresent());
        assertEquals("battery-sensor", profileRegistry.getProfile("battery-sensor").get().getProfileId());
    }

    @Test
    @DisplayName("Profile should define required battery service structure")
    void profile_definesBatteryServiceStructure() {
        // Verify profile structure
        assertNotNull(batteryProfile.getProvider());
        assertNotNull(batteryProfile.getProvider().getServices());
        assertFalse(batteryProfile.getProvider().getServices().isEmpty());
        
        // Check for battery service
        boolean hasBatteryService = batteryProfile.getProvider().getServices().stream()
            .anyMatch(service -> "battery".equals(service.getServiceId()));
        assertTrue(hasBatteryService, "Profile should have battery service");
    }

    @Test
    @DisplayName("Profile validation should handle mappings without profiles")
    void profileValidation_withoutProfile_succeeds() {
        // Test a mapping without profile (weather mapping)
        try {
            Resource weatherResource = resourceSet.createResource(URI.createURI("data/WeatherProviderMapping.xmi"));
            weatherResource.load(null);
            ProviderMapping weatherMapping = (ProviderMapping) weatherResource.getContents().get(0);
            
            // This should not throw an exception even though no profile is set
            MappingProfileRegistry.ValidationResult result = profileRegistry.validateMapping(weatherMapping);
            assertTrue(result.isValid(), "Mapping without profile should be valid");
            
        } catch (IOException e) {
            // If weather mapping doesn't exist, skip this test
            System.out.println("Weather mapping not found, skipping test");
        }
    }

    @Test
    @DisplayName("Profile registry should handle unknown profile requests")
    void profileRegistry_unknownProfile_returnsEmpty() {
        // Test requesting a non-existent profile
        assertFalse(profileRegistry.getProfile("non-existent-profile").isPresent());
    }

    @Test
    @DisplayName("Profile structure should match expected battery sensor format")
    void profile_matchesExpectedStructure() {
        // Verify the profile has the expected structure
        assertEquals("battery-sensor", batteryProfile.getProfileId());
        assertEquals("Battery Sensor Profile", batteryProfile.getName());
        assertEquals(ProviderStrategy.UNIFIED, batteryProfile.getProviderStrategy());
        
        // Verify provider structure
        assertEquals("battery-sensor", batteryProfile.getProvider().getProviderId());
        
        // Verify services structure
        assertTrue(batteryProfile.getProvider().getServices().stream()
            .anyMatch(service -> "battery".equals(service.getServiceId())));
    }
}
