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
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.sensinact.mapping.MappingProfileRegistry;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.sensinact.core.command.AbstractSensinactCommand;
import org.eclipse.sensinact.core.command.GatewayThread;
import org.eclipse.sensinact.core.model.Model;
import org.eclipse.sensinact.core.model.SensinactModelManager;
import org.eclipse.sensinact.core.model.Service;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.typedevent.annotations.RequireTypedEvent;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * Tests for MappingProfile functionality using battery sensor examples.
 * Tests profile-based mapping with unified provider strategy.
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@RequireTypedEvent
public class BatteryProfileTest {

    private static final String BATTERY_PROFILE_URI = "/data/battery/battery-sensor-profile.xmi";
    private static final String DRAGINO_MAPPING_URI = "/data/battery/dragino-battery-mapping.xmi";
    private static final String EM310_MAPPING_URI = "/data/battery/em310udl-battery-mapping.xmi";

//    @InjectService(filter = "(&(emf.name=lorawan)(emf.name=dragino)(emf.name=em310udl))")
    @InjectService
    ResourceSet resourceSet;
    @InjectService
    GatewayThread gatewayThread;
    @InjectService
    ProviderMappingRegistry mappingRegistry;
    @InjectService
    MappingProfileRegistry profileRegistry;

    private MappingProfile batteryProfile;
    private ProviderMapping draginoMapping;
    private ProviderMapping em310Mapping;

    @BeforeEach
    public void beforeEach() throws IOException {
        // Load battery profile
    	URL profileUrl = getClass().getResource(BATTERY_PROFILE_URI);
        Resource profileResource = resourceSet.createResource(URI.createURI(profileUrl.toString()));
        profileResource.load(null);
        batteryProfile = (MappingProfile) profileResource.getContents().get(0);
        
        // Load Dragino mapping
        URL draginoUrl = getClass().getResource(DRAGINO_MAPPING_URI);
        Resource draginoResource = resourceSet.createResource(URI.createURI(draginoUrl.toString()));
        draginoResource.load(null);
        draginoMapping = (ProviderMapping) draginoResource.getContents().get(0);
        
        // Load EM310 mapping
        URL em310Url = getClass().getResource(EM310_MAPPING_URI);
        Resource em310Resource = resourceSet.createResource(URI.createURI(em310Url.toString()));
        em310Resource.load(null);
        em310Mapping = (ProviderMapping) em310Resource.getContents().get(0);
    }
    
    @AfterEach
    public void afterEach() {
        mappingRegistry.dispose();
        profileRegistry.dispose();
    }

    @Test
    @DisplayName("Battery profile should have unified provider strategy")
    void batteryProfile_hasUnifiedProviderStrategy() {
        // Verify the battery profile uses unified strategy
        assertNotNull(batteryProfile.getProviderStrategy());
        assertEquals("UNIFIED", batteryProfile.getProviderStrategy().getLiteral());
        
        // Verify profile basic structure
        assertNotNull(batteryProfile.getProfileId());
        assertEquals("battery-sensor", batteryProfile.getProfileId());
        assertNotNull(batteryProfile.getName());
        assertEquals("Battery Sensor Profile", batteryProfile.getName());
        
        // Verify profile provider structure
        assertNotNull(batteryProfile.getProvider());
        assertEquals("battery-sensor", batteryProfile.getProvider().getProviderId());
    }

    @Test
    @DisplayName("Both mappings should reference the battery profile")
    void mappings_referenceBatteryProfile() {
        // Verify Dragino mapping references profile
        assertNotNull(draginoMapping.getProfile());
        assertEquals("battery-sensor", draginoMapping.getProfile().getProfileId());
        
        // Verify EM310 mapping references profile
        assertNotNull(em310Mapping.getProfile());
        assertEquals("battery-sensor", em310Mapping.getProfile().getProfileId());
        
        // Both should use the same provider ID from profile
        assertEquals("dragino-battery-sensor", draginoMapping.getMid());
        assertEquals("em310udl-battery-sensor", em310Mapping.getMid());
    }

    @Test
    @DisplayName("Profile registration should succeed")
    void profileRegistration_succeeds() throws InterruptedException, ExecutionException, InvocationTargetException {
        // Register the profile
        profileRegistry.registerProfile(batteryProfile);
        
        // Verify profile is registered
        assertTrue(profileRegistry.isCompatible("battery-sensor", "1.0"));
        
        // Verify we can retrieve the profile
        Optional<MappingProfile> retrievedProfile = profileRegistry.getProfile("battery-sensor");
        assertTrue(retrievedProfile.isPresent());
        assertEquals("battery-sensor", retrievedProfile.get().getProfileId());
    }

    @Test
    @DisplayName("Unified provider strategy should create single provider for both mappings")
    void unifiedProviderStrategy_createsSingleProvider() throws InterruptedException, ExecutionException, InvocationTargetException {
        // Register profile first
        profileRegistry.registerProfile(batteryProfile);
        
        // Register both mappings
        mappingRegistry.registerModelMapping(draginoMapping);
        mappingRegistry.registerModelMapping(em310Mapping);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    // Should have only one model with ID "battery-sensor"
                    Model batteryModel = modelManager.getModel("battery-sensor");
                    assertNotNull(batteryModel);
                    
                    // Verify the unified provider has the expected structure
                    Map<String, ? extends Service> services = batteryModel.getServices();
                    assertNotNull(services);
                    
                    // Should have battery service and admin service
                    assertTrue(services.containsKey("battery"));
                    assertTrue(services.containsKey("admin"));
                    
                    // Verify battery service structure
                    Service batteryService = services.get("battery");
                    assertNotNull(batteryService);
                    Map<String, ? extends org.eclipse.sensinact.core.model.Resource> resources = batteryService.getResources();
                    assertNotNull(resources);
                    assertTrue(resources.containsKey("level"));
                    
                    // Verify resource metadata
                    org.eclipse.sensinact.core.model.Resource levelResource = resources.get("level");
                    assertNotNull(levelResource);
                    assertEquals("V", levelResource.getDefaultMetadata().get("unit"));
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }

    @Test
    @DisplayName("Profile compliance validation should succeed for both mappings")
    void profileCompliance_validatesSuccessfully() throws InterruptedException, ExecutionException, InvocationTargetException {
        // Register profile
        profileRegistry.registerProfile(batteryProfile);
        
        // Register mappings - should succeed if compliant
        mappingRegistry.registerModelMapping(draginoMapping);
        mappingRegistry.registerModelMapping(em310Mapping);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    // Verify model exists and has correct structure
                    Model batteryModel = modelManager.getModel("battery-sensor");
                    assertNotNull(batteryModel);
                    
                    // Verify service compliance with profile
                    Service batteryService = batteryModel.getServices().get("battery");
                    assertNotNull(batteryService);
                    
                    // Verify resource compliance
                    org.eclipse.sensinact.core.model.Resource levelResource = batteryService.getResources().get("level");
                    assertNotNull(levelResource);
                    
                    // Verify expected unit from profile
                    assertEquals("V", levelResource.getDefaultMetadata().get("unit"));
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }

    @Test
    @DisplayName("Different vendor mappings should map to same unified provider structure")
    void differentVendorMappings_mapToUnifiedStructure() throws InterruptedException, ExecutionException, InvocationTargetException {
        // Register profile and mappings
        profileRegistry.registerProfile(batteryProfile);
        mappingRegistry.registerModelMapping(draginoMapping);
        mappingRegistry.registerModelMapping(em310Mapping);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    // Get the unified model
                    Model unifiedModel = modelManager.getModel("battery-sensor");
                    assertNotNull(unifiedModel);
                    
                    // Verify it has the structure defined by the profile
                    Service batteryService = unifiedModel.getServices().get("battery");
                    assertNotNull(batteryService);
                    
                    org.eclipse.sensinact.core.model.Resource levelResource = batteryService.getResources().get("level");
                    assertNotNull(levelResource);
                    
                    // Both Dragino (batV) and EM310 (battery) should map to same "level" resource
                    assertEquals("V", levelResource.getDefaultMetadata().get("unit"));
                    assertEquals("Battery Level", levelResource.getDefaultMetadata().get("friendlyName"));
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }

    @Test
    @DisplayName("Registry disposal should clean up properly")
    void registryDisposal_cleansUpProperly() throws InterruptedException, ExecutionException, InvocationTargetException {
        // Register profile and mappings
        profileRegistry.registerProfile(batteryProfile);
        mappingRegistry.registerModelMapping(draginoMapping);
        mappingRegistry.registerModelMapping(em310Mapping);
        
        // Verify they are registered
        assertTrue(profileRegistry.isCompatible("battery-sensor", "1.0"));
        
        Promise<Boolean> registeredResult = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model batteryModel = modelManager.getModel("battery-sensor");
                    return pf.resolved(batteryModel != null);
                } catch (Exception e) {
                    return pf.resolved(false);
                }
            }
        });
        
        assertTrue(registeredResult.getValue());
        
        // Dispose registries
        mappingRegistry.dispose();
        profileRegistry.dispose();
        
        // Verify cleanup
        assertFalse(profileRegistry.isCompatible("battery-sensor", "1.0"));
        
        Promise<Boolean> cleanedUpResult = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model batteryModel = modelManager.getModel("battery-sensor");
                    return pf.resolved(batteryModel == null);
                } catch (Exception e) {
                    return pf.resolved(true);
                }
            }
        });
        
        assertTrue(cleanedUpResult.getValue());
    }
}
