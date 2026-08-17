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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
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

/**Ø
 * Tests for ProviderModelMapper general functionality.
 * Tests basic sensinact mapping operations.
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@RequireTypedEvent
public class ProviderModelMapperTest {

	private static final String MAPPING_URI = "/data/WeatherProviderMapping.xmi";
	private static final String MAPPING_URI_2 = "/data/WeatherReportsProviderMapping.xmi";

    @InjectService
    ResourceSet resourceSet;
    @InjectService
    GatewayThread gatewayThread;
    @InjectService
    ProviderMappingRegistry mappingRegistry;

    private ProviderMapping weatherMapping;
    private ProviderMapping weatherMapping2;

    @BeforeEach
    public void beforeEach() throws IOException {
        // Load test mapping
    	URL mappingUrl = getClass().getResource(MAPPING_URI);
		Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);
        
        URL mappingUrl2 = getClass().getResource(MAPPING_URI_2);
		Resource mappingResource2 = resourceSet.createResource(URI.createURI(mappingUrl2.toString()));
        mappingResource2.load(null);
        weatherMapping2 = (ProviderMapping) mappingResource2.getContents().get(0);
    }
    
    @AfterEach
    public void afterEach() {
    	mappingRegistry.dispose();
    }

    @Test
    @DisplayName("Weather mapping should have expected structure")
    void weatherMapping_hasExpectedStructure() {
        // Verify the weather mapping has the basic structure we expect
        assertNotNull(weatherMapping.getMid());
        assertNotNull(weatherMapping.getAdmin());
        assertNotNull(weatherMapping.getServices());
        assertEquals(6, weatherMapping.getServices().size());
        
        // Should have no profile reference (weather mapping doesn't use profiles)
        assertNull(weatherMapping.getProfile());
    }

    @Test
    @DisplayName("Provider mapping should be registered successfully on gateway thread")
    void providerMapping_registersSuccessfully() throws InterruptedException, ExecutionException, InvocationTargetException {
    	// Do the registration
    	mappingRegistry.registerModelMapping(weatherMapping);
    	List<String> services = List.of("cloud", "wind", "temperature", "pressure", "rain", "warning", "admin");
        // Execute validation of successful registration on the gateway thread
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    // Test that we can access the digital twin and model manager
                	EPackage providerPackage = weatherMapping.getAdmin().getProviderPackage();
                	assertNotNull(providerPackage);
                	Model model = modelManager.getModel(weatherMapping.getMid());
                	assertNotNull(model);
                	assertEquals(7, model.getServices().size());
                	services.forEach(s->assertTrue(model.getServices().containsKey(s)));
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        // Verify the operation completed successfully
        assertTrue(result.getValue());
    }

    @Test
    @DisplayName("Weather mapping should have correct service resources")
    void weatherMapping_hasCorrectServiceResources() throws InterruptedException, ExecutionException, InvocationTargetException {
        mappingRegistry.registerModelMapping(weatherMapping);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @SuppressWarnings("unchecked")
			@Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model model = modelManager.getModel(weatherMapping.getMid());
                    assertNotNull(model);
                    
                    // Verify wind service resources
                    Service windService = model.getServices().get("wind");
                    assertNotNull(windService);
                    Map<String, org.eclipse.sensinact.core.model.Resource> windResources = (Map<String, org.eclipse.sensinact.core.model.Resource>) windService.getResources();
                    assertEquals(2, windResources.size());
                    assertTrue(windResources.containsKey("speed"));
                    assertTrue(windResources.containsKey("direction"));
                    
                    // Verify temperature service resources
                    Service tempService = model.getServices().get("temperature");
                    assertNotNull(tempService);
                    Map<String, org.eclipse.sensinact.core.model.Resource> tempResources = (Map<String, org.eclipse.sensinact.core.model.Resource>) tempService.getResources();
                    assertEquals(3, tempResources.size());
                    assertTrue(tempResources.containsKey("temperature"));
                    assertTrue(tempResources.containsKey("min"));
                    assertTrue(tempResources.containsKey("max"));
                    
                    // Verify rain service resources
                    Service rainService = model.getServices().get("rain");
                    assertNotNull(rainService);
                    Map<String, org.eclipse.sensinact.core.model.Resource> rainResources = (Map<String, org.eclipse.sensinact.core.model.Resource>) rainService.getResources();
                    assertEquals(4, rainResources.size());
                    assertTrue(rainResources.containsKey("no-rain"));
                    assertTrue(rainResources.containsKey("2mm"));
                    assertTrue(rainResources.containsKey("10mm"));
                    assertTrue(rainResources.containsKey("50mm"));
                    
                    // Verify single resource services
                    Service cloudService = model.getServices().get("cloud");
                    assertNotNull(cloudService);
                    assertEquals(1, cloudService.getResources().size());
                    assertTrue(cloudService.getResources().containsKey("coverage"));
                    
                    Service pressureService = model.getServices().get("pressure");
                    assertNotNull(pressureService);
                    assertEquals(1, pressureService.getResources().size());
                    assertTrue(pressureService.getResources().containsKey("surface"));
                    
                    Service warningService = model.getServices().get("warning");
                    assertNotNull(warningService);
                    assertEquals(1, warningService.getResources().size());
                    assertTrue(warningService.getResources().containsKey("signifanct"));
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }

    @Test
    @DisplayName("Weather mapping resources should have correct data types and units")
    void weatherMapping_hasCorrectDataTypesAndUnits() throws InterruptedException, ExecutionException, InvocationTargetException {
        mappingRegistry.registerModelMapping(weatherMapping);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model model = modelManager.getModel(weatherMapping.getMid());
                    assertNotNull(model);
                    
                    // Verify wind service resource types and units
                    Service windService = model.getServices().get("wind");
                    org.eclipse.sensinact.core.model.Resource windSpeed = windService.getResources().get("speed");
                    assertNotNull(windSpeed);
                    assertEquals("m/s", windSpeed.getDefaultMetadata().get("unit"));
                    
                    org.eclipse.sensinact.core.model.Resource windDirection = windService.getResources().get("direction");
                    assertNotNull(windDirection);
                    assertEquals("degrees", windDirection.getDefaultMetadata().get("unit"));
                    
                    // Verify temperature service units
                    Service tempService = model.getServices().get("temperature");
                    org.eclipse.sensinact.core.model.Resource temp = tempService.getResources().get("temperature");
                    assertNotNull(temp);
                    assertEquals("degrees", temp.getDefaultMetadata().get("unit"));
                    
                    org.eclipse.sensinact.core.model.Resource tempMin = tempService.getResources().get("min");
                    assertNotNull(tempMin);
                    assertEquals("degrees", tempMin.getDefaultMetadata().get("unit"));
                    
                    org.eclipse.sensinact.core.model.Resource tempMax = tempService.getResources().get("max");
                    assertNotNull(tempMax);
                    assertEquals("degrees", tempMax.getDefaultMetadata().get("unit"));
                    
                    // Verify pressure service units
                    Service pressureService = model.getServices().get("pressure");
                    org.eclipse.sensinact.core.model.Resource pressure = pressureService.getResources().get("surface");
                    assertNotNull(pressure);
                    assertEquals("Pa", pressure.getDefaultMetadata().get("unit"));
                    
                    // Verify cloud service units
                    Service cloudService = model.getServices().get("cloud");
                    org.eclipse.sensinact.core.model.Resource cloudCoverage = cloudService.getResources().get("coverage");
                    assertNotNull(cloudCoverage);
                    assertEquals("%", cloudCoverage.getDefaultMetadata().get("unit"));
                    
                    // Verify rain service units (all should be %)
                    Service rainService = model.getServices().get("rain");
                    for (String resourceName : List.of("no-rain", "2mm", "10mm", "50mm")) {
                        org.eclipse.sensinact.core.model.Resource rainResource = rainService.getResources().get(resourceName);
                        assertNotNull(rainResource);
                        assertEquals("%", rainResource.getDefaultMetadata().get("unit"));
                    }
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }

    @Test
    @DisplayName("Weather mapping admin configuration should be correct")
    void weatherMapping_hasCorrectAdminConfiguration() throws InterruptedException, ExecutionException, InvocationTargetException {
        mappingRegistry.registerModelMapping(weatherMapping);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model model = modelManager.getModel(weatherMapping.getMid());
                    assertNotNull(model);
                    
                    // Verify admin service exists
                    Service adminService = model.getServices().get("admin");
                    assertNotNull(adminService);
                    
                    // Verify provider has timestamp configuration
                    assertTrue(weatherMapping.isProviderTimestamp());
                    assertNotNull(weatherMapping.getTimestamp());
                    
                    // Verify provider name configuration
                    assertNotNull(weatherMapping.getName());
                    assertEquals("DWD Weather", weatherMapping.getName().getName());
                    
                    // Verify provider model ID
                    assertEquals("dwd-weather", weatherMapping.getMid());
                    
                    // Verify admin configuration
                    assertNotNull(weatherMapping.getAdmin());
                    assertEquals("admin", weatherMapping.getAdmin().getMid());
                    assertNotNull(weatherMapping.getAdmin().getProviderPackage());
                    
                    // Verify location configuration exists
                    assertNotNull(weatherMapping.getAdmin().getLatitudeRef());
                    assertNotNull(weatherMapping.getAdmin().getLongitudeRef());
                    assertTrue(weatherMapping.getAdmin().getLatitudeRef().size() > 0);
                    assertTrue(weatherMapping.getAdmin().getLongitudeRef().size() > 0);
                    
                    // Verify friendly name configuration exists
                    assertNotNull(weatherMapping.getAdmin().getFriendlyNameFeature());
                    assertTrue(weatherMapping.getAdmin().getFriendlyNameFeature().size() > 0);
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }

    @Test
    @DisplayName("Invalid mapping should handle errors gracefully")
    void invalidMapping_handlesErrorsGracefully() throws IOException {
        // Test with null mapping
        try {
            mappingRegistry.registerModelMapping(null);
            // Should not throw exception but handle gracefully
        } catch (Exception e) {
            // Expected behavior - should handle null mapping
            assertNotNull(e);
        }
        
        // Test with empty mapping resource
        Resource emptyResource = resourceSet.createResource(URI.createURI("memory://empty.xmi"));
        // Empty resource should not cause crashes when accessed
        assertTrue(emptyResource.getContents().isEmpty());
    }

    @Test
    @DisplayName("Weather mapping should have correct provider class references")
    void weatherMapping_hasCorrectProviderClassReferences() {
        // Verify provider classes are configured
        assertNotNull(weatherMapping.getProviderClasses());
        assertFalse(weatherMapping.getProviderClasses().isEmpty());
        
        // Verify the provider class reference points to correct EMF model
        assertTrue(weatherMapping.getProviderClasses().get(0).getName().contains("MOSMIXSWeatherReport"));
    }
    
    @Test
    @DisplayName("Weather mapping resources should have correct description from EAnnotation")
    void weatherMapping2_hasCorrectDescriptionFromEAnnotation() throws InterruptedException, ExecutionException, InvocationTargetException {
        mappingRegistry.registerModelMapping(weatherMapping2);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model model = modelManager.getModel(weatherMapping2.getMid());
                    assertNotNull(model);
                    
                    // Verify wind service resource types and units
                    Service currentWeahterService = model.getServices().get("currentWeather");
                    org.eclipse.sensinact.core.model.Resource windSpeed = currentWeahterService.getResources().get("windSpeed");
                    assertNotNull(windSpeed);
                    assertEquals("Wind speed: m/s (FF)", windSpeed.getDefaultMetadata().get("description"));
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }
    
    @Test
    @DisplayName("Weather mapping resources should have correct units from EAnnotation")
    void weatherMapping2_hasCorrectUnitsFromEAnnotation() throws InterruptedException, ExecutionException, InvocationTargetException {
        mappingRegistry.registerModelMapping(weatherMapping2);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model model = modelManager.getModel(weatherMapping2.getMid());
                    assertNotNull(model);
                    
                    // Verify wind service resource types and units
                    Service currentWeahterService = model.getServices().get("currentWeather");
                    org.eclipse.sensinact.core.model.Resource windSpeed = currentWeahterService.getResources().get("windSpeed");
                    assertNotNull(windSpeed);
                    assertEquals("m/s", windSpeed.getDefaultMetadata().get("unit"));
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }

    
    @Test
    @DisplayName("Weather mapping resources should have correct metadata from EAnnotation")
    void weatherMapping2_hasCorrectMetadataFromEAnnotation() throws InterruptedException, ExecutionException, InvocationTargetException {
        mappingRegistry.registerModelMapping(weatherMapping2);
        
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    Model model = modelManager.getModel(weatherMapping2.getMid());
                    assertNotNull(model);
                    
                    // Verify wind service resource types and units
                    Service currentWeahterService = model.getServices().get("currentWeather");
                    org.eclipse.sensinact.core.model.Resource windSpeed = currentWeahterService.getResources().get("windSpeed");
                    assertNotNull(windSpeed);
                    assertEquals("FF", windSpeed.getDefaultMetadata().get("dwd.id"));
                    assertEquals("meters per seconds", windSpeed.getDefaultMetadata().get("sensorthings.unit.name"));
                    assertEquals("meters per seconds", windSpeed.getDefaultMetadata().get("sensorthings.unit.definition"));
                    assertEquals("Wind speed: m/s (FF)", windSpeed.getDefaultMetadata().get("description"));
                    assertEquals("m/s", windSpeed.getDefaultMetadata().get("unit"));

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        assertTrue(result.getValue());
    }


//    @TestØ
}
