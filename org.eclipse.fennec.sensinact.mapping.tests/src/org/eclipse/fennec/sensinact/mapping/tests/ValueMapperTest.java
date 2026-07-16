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
 *     Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.Instant;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
import org.eclipse.fennec.sensinact.mapping.ValueMapper;
import org.eclipse.fennec.sensinact.mapping.ValueMapperFactory;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.sensinact.core.command.AbstractSensinactCommand;
import org.eclipse.sensinact.core.command.GatewayThread;
import org.eclipse.sensinact.core.model.SensinactModelManager;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;
import org.eclipse.sensinact.core.twin.SensinactProvider;
import org.eclipse.sensinact.core.twin.SensinactResource;
import org.eclipse.sensinact.core.twin.SensinactService;
import org.eclipse.sensinact.core.twin.TimedValue;
import org.gecko.weather.model.weather.GeoPosition;
import org.gecko.weather.model.weather.MOSMIXSWeatherReport;
import org.gecko.weather.model.weather.Station;
import org.gecko.weather.model.weather.W1W2;
import org.gecko.weather.model.weather.WMOWeatherCodeType;
import org.gecko.weather.model.weather.WeatherFactory;
import org.gecko.weather.model.weather.WeatherReports;
import org.gecko.weather.model.weather.WeatherStation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.typedevent.annotations.RequireTypedEvent;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

import dragino.DraginoLSE01Uplink;

/**
 * OSGi integration tests for ValueMapper functionality.
 * Tests real value extraction, conversion, and mapping to SensiNact resources
 * using actual EMF models and SensiNact services.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@RequireEMF
@RequireTypedEvent
public class ValueMapperTest {

	private static final String DRAGINO_MAPPING_URI = "/data/battery/dragino-battery-mapping_no-profile.xmi";
	private static final String DRAGINO_EXAMPLE_URI = "/data/dragino-example.dragino";
	private static final String DRAGINO_MODEL = "dragino-battery-sensor";

	private static final String WEATHER_MAPPING_URI = "/data/WeatherReportsProviderMapping.xmi";
	private static final String WEATHER_MAPPING_URI_NO_RESOURCES = "/data/WeatherReportsProviderMapping.xmi";
	private static final String WEATHER_MODEL = "dwd-weather-reports";

    @InjectService
    GatewayThread gatewayThread;

    @InjectService
    ProviderMappingRegistry mappingRegistry;

    @InjectService
    ResourceSet resourceSet;

    private ProviderMapping batteryMapping;
    private DraginoLSE01Uplink draginoExample;

    @BeforeEach
    void setUp() throws IOException {
        // Load the Dragino battery mapping configuration
    	URL mappingUrl = getClass().getResource(DRAGINO_MAPPING_URI);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        batteryMapping = (ProviderMapping) mappingResource.getContents().get(0);
        
        // Load the Dragino example data
        URL exampleUrl = getClass().getResource(DRAGINO_EXAMPLE_URI);
        Resource exampleResource = resourceSet.createResource(URI.createURI(exampleUrl.toString()));
        exampleResource.load(null);
        draginoExample = (DraginoLSE01Uplink) exampleResource.getContents().get(0);
        
        // Register the mapping to create the provider model structure
        mappingRegistry.registerModelMapping(batteryMapping);
        
        
    }

    @AfterEach
    void tearDown() {
    	mappingRegistry.dispose();
    }

    @Test
    @DisplayName("ValueMapper should extract resource values from Dragino example")
    void mapResourceValues_withDraginoExample_returnsValues() throws Exception {
        // Execute in SensiNact gateway thread
        Promise<Map<String, Object>> result = gatewayThread.execute(new AbstractSensinactCommand<Map<String, Object>>() {
            @Override
            protected Promise<Map<String, Object>> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, batteryMapping);
                    Map<String, Object> values = mapper.mapResourceValues(draginoExample);
                    return pf.resolved(values);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        Map<String, Object> values = result.getValue();
        
        // Verify values were extracted
        assertNotNull(values, "Values should not be null");
        assertFalse(values.isEmpty(), "Values should not be empty");
        
        // Check for expected battery resource
        assertTrue(values.containsKey("battery.level"), "Battery level should be extracted");
        Object batteryLevel = values.get("battery.level");
        assertNotNull(batteryLevel, "Battery level value should not be null");
        assertTrue(batteryLevel instanceof Double, "Battery level should be Double");
    }

    @Test
    @DisplayName("ValueMapper should validate Dragino example instance")
    void validateInstance_withDraginoExample_returnsValidResult() throws Exception {
        // Execute validation in SensiNact gateway thread
        Promise<ValueMapper.ValidationResult> result = gatewayThread.execute(new AbstractSensinactCommand<ValueMapper.ValidationResult>() {
            @Override
            protected Promise<ValueMapper.ValidationResult> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, batteryMapping);
                    ValueMapper.ValidationResult validationResult = mapper.validateInstance(draginoExample);
                    return pf.resolved(validationResult);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        ValueMapper.ValidationResult validationResult = result.getValue();
        
        // Verify validation result
        assertNotNull(validationResult, "Validation result should not be null");
        assertTrue(validationResult.isValid(), "Dragino example should be valid");
        assertTrue(validationResult.getErrors().isEmpty(), "No validation errors should be present");
    }

    @Test
    @DisplayName("ValueMapper should map Dragino instance to SensiNact provider")
    void mapInstance_withDraginoExample_createsProvider() throws Exception {
    	DraginoLSE01Uplink dragino = EcoreUtil.copy(draginoExample);
    	dragino.setDeduplicationId("test");
    	
    	// Set timestamp 5 minutes in the future to ensure it's newer than provider creation
    	Instant futureTimestamp = Instant.now().plusSeconds(300); // 300 seconds = 5 minutes
    	String futureTimestampString = futureTimestamp.toString();
    	// Update the first rxInfo's time attribute
    	if (!dragino.getRxInfo().isEmpty()) {
    		dragino.getRxInfo().get(0).setTime(futureTimestampString);
    	}
        // Execute mapping in SensiNact gateway thread
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                	assertNull(twin.getProvider(DRAGINO_MODEL, "test"), "No provider exists yet");
                	
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, batteryMapping);
                    // Map the instance
                    mapper.mapInstance(dragino);
                    SensinactProvider provider = twin.getProvider(DRAGINO_MODEL, "test");
                    assertNotNull(provider, "Dragino provider should not be null");
                    SensinactService adminService = provider.getServices().get("admin");
                    assertNotNull(adminService, "Admin service should ne be null");
                    SensinactResource fnResource = adminService.getResources().get("friendlyName");
                    assertNotNull(fnResource, "Friendly name should be set");
                    Promise<TimedValue<?>> value = fnResource.getValue();
                    TimedValue<?> timedValue = value.getValue();
                    // Verify timestamp from mapping configuration (should be the future timestamp we set)
                    assertEquals(futureTimestamp, timedValue.getTimestamp(), 
                    		"Admin friendlyName should use timestamp from provider mapping");
                    assertEquals("Dragino_LSE01", timedValue.getValue().toString());
                    
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        Boolean success = result.getValue();
        assertTrue(success, "Mapping should complete successfully");
    }

    @Test
    @DisplayName("ValueMapper should handle null EObject parameter")
    void mapInstance_withNullEObject_throwsException() throws Exception {
        // Execute in SensiNact gateway thread and expect exception
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, batteryMapping);
                    mapper.mapInstance(null);
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        // Verify the promise failed with NullPointerException
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> result.getValue());
        assertTrue(exception.getCause() instanceof NullPointerException, 
        		"Should throw NullPointerException for null EObject");
    }

    @Test
    @DisplayName("ValueMapper should reject null parameters")
    void createValueMapper_withNullParameters_throwsException() throws Exception {
        // Execute in SensiNact gateway thread
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapperFactory.createValueMapper(null, batteryMapping);
                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, () -> result.getValue());
        assertTrue(exception.getCause() instanceof NullPointerException,
                  "Should throw NullPointerException for null twin");
    }

    // ========== Collection Mapping Tests ==========

    @Test
    @DisplayName("Collection mapping: should map provider name from collection index 0")
    void collectionMapping_providerName_usesFirstReport() throws Exception {
        // Load weather mapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI_NO_RESOURCES);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data with WeatherReports containing multiple reports
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping in SensiNact gateway thread
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    // Verify provider was created with ID from first report
                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Weather provider should be created");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "Provider name mapping from collection should succeed");
    }

    @Test
    @DisplayName("Collection mapping: should map currentWeather service from reports[0]")
    void collectionMapping_currentWeather_usesReportAtIndex0() throws Exception {
        // Load weather mapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI_NO_RESOURCES);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Provider should exist");

                    // Check currentWeather service exists
                    SensinactService currentWeatherService = provider.getServices().get("currentWeather");
                    assertNotNull(currentWeatherService, "currentWeather service should exist");

                    // Verify data comes from reports[0] (windSpeed = 5.0)
                    SensinactResource windSpeedResource = currentWeatherService.getResources().get("windSpeed");
                    assertNotNull(windSpeedResource, "windSpeed resource should exist");

                    Promise<TimedValue<?>> valuePromise = windSpeedResource.getValue();
                    TimedValue<?> timedValue = valuePromise.getValue();
                    assertEquals(5.0f, timedValue.getValue(), "windSpeed should match reports[0]");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "currentWeather mapping from reports[0] should succeed");
    }

    @Test
    @Disabled("This is currently not working because the admin service is treated differently in sensinact")
    @DisplayName("Provider mapping: should map admin service")
    void providerMapping_admin() throws Exception {
        // Load weather mapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI_NO_RESOURCES);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });
        
        // Read mapping
        result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    

                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Provider should exist");
                    
                    // Check currentWeather service exists
                    SensinactService adminService = provider.getServices().get("admin");
                    assertNotNull(adminService, "adminService service should exist");

                    SensinactResource friendlyName = adminService.getResources().get("friendlyName");
                    assertNotNull(friendlyName, "friendlyName resource should exist");

                    Promise<TimedValue<?>> valuePromise = friendlyName.getValue();
                    TimedValue<?> timedValue = valuePromise.getValue();
                    assertEquals("GERA", timedValue.getValue(), "friendlyName should match reports[0]");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "currentWeather mapping from reports[0] should succeed");
    }

    
    @Test
    @DisplayName("Collection mapping: should map forecast3H service from reports[1]")
    void collectionMapping_forecast3H_usesReportAtIndex1() throws Exception {
        // Load weather mapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI_NO_RESOURCES);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Provider should exist");

                    // Check forecast3H service exists
                    SensinactService forecast3HService = provider.getServices().get("forecast3H");
                    assertNotNull(forecast3HService, "forecast3H service should exist");

                    // Verify data comes from reports[1] (windSpeed = 7.5)
                    SensinactResource windSpeedResource = forecast3HService.getResources().get("windSpeed");
                    assertNotNull(windSpeedResource, "windSpeed resource should exist");

                    Promise<TimedValue<?>> valuePromise = windSpeedResource.getValue();
                    TimedValue<?> timedValue = valuePromise.getValue();
                    assertEquals(7.5f, timedValue.getValue(), "windSpeed should match reports[1]");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "forecast3H mapping from reports[1] should succeed");
    }

    @Test
    @DisplayName("Collection mapping: validation should fail if collection index out of bounds")
    void collectionMapping_validation_failsForOutOfBoundsIndex() throws Exception {
        // Load weather mapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI_NO_RESOURCES);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data with only ONE report (but mapping expects index 0 AND 1)
        WeatherReports weatherReports = WeatherFactory.eINSTANCE.createWeatherReports();
        weatherReports.setId("station-10567");
        MOSMIXSWeatherReport report = WeatherFactory.eINSTANCE.createMOSMIXSWeatherReport();
        report.setId("report-0");
        weatherReports.getReports().add(report);

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute validation
        Promise<ValueMapper.ValidationResult> result = gatewayThread.execute(new AbstractSensinactCommand<ValueMapper.ValidationResult>() {
            @Override
            protected Promise<ValueMapper.ValidationResult> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    ValueMapper.ValidationResult validation = mapper.validateInstance(weatherReports);
                    return pf.resolved(validation);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        ValueMapper.ValidationResult validation = result.getValue();
        assertFalse(validation.isValid(), "Validation should fail when collection index 1 is out of bounds");
        assertFalse(validation.getErrors().isEmpty(), "Should have validation errors");
    }

    /**
     * Helper method to create test WeatherReports data with multiple reports.
     */
    private WeatherReports createWeatherReportsTestData() {
        WeatherFactory factory = WeatherFactory.eINSTANCE;

        // Create WeatherReports container
        WeatherReports weatherReports = factory.createWeatherReports();
        weatherReports.setId("station-10567");

        // Create weather station
        WeatherStation weatherStation = factory.createWeatherStation();
        weatherStation.setId("10567");
        weatherStation.setName("GERA");
        GeoPosition location = factory.createGeoPosition();
        location.setLatitude(50.88);
        location.setLongitude(12.13);
        location.setElevation(311);
        weatherStation.setLocation(location);

        // Create first report (current weather) - index 0
        MOSMIXSWeatherReport report0 = factory.createMOSMIXSWeatherReport();
        report0.setId("report-0");
        report0.setTimestamp(new java.util.Date());
        report0.setWeatherStation(weatherStation);

        Station station = factory.createStation();
        station.setName("GERA");
        station.setLocation(location);
        report0.setStation(station);

        report0.setWindSpeed(5.0f);
        report0.setWindDirection(180.0f);
        report0.setTempAboveSurface5(288.15f); // 15°C in Kelvin
        report0.setCloudCoverTotal(50.0f);
        report0.setSurfacePressure(101325.0f);
        
        W1W2 w1w2 = factory.createW1W2();
        w1w2.setW1(WMOWeatherCodeType.W01);
        w1w2.setW2(WMOWeatherCodeType.W05);
        report0.setSignificantWeather6Hours(w1w2);

        weatherReports.getReports().add(report0);

        // Create second report (3H forecast) - index 1
        MOSMIXSWeatherReport report1 = factory.createMOSMIXSWeatherReport();
        report1.setId("report-1");
        report1.setTimestamp(new java.util.Date(System.currentTimeMillis() + 10800000)); // +3 hours
        report1.setWeatherStation(EcoreUtil.copy(weatherStation));
        report1.setStation(EcoreUtil.copy(station));

        report1.setWindSpeed(7.5f); // Different value for forecast
        report1.setWindDirection(200.0f);
        report1.setTempAboveSurface5(290.15f); // 17°C in Kelvin
        report1.setCloudCoverTotal(70.0f);
        report1.setSurfacePressure(101200.0f);
        
        report1.setSignificantWeather6Hours(EcoreUtil.copy(w1w2));

        weatherReports.getReports().add(report1);

        return weatherReports;
    }

   
   

    // ========== Nested Reference Mapping Tests ==========

    @Test
    @DisplayName("Nested ReferenceMapping: should map root level attributes from WeatherReport")
    void nestedReferenceMapping_rootAttributes_areMapped() throws Exception {
        // Load weather mapping with ReferenceMapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                  

                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Provider should exist");

                    SensinactService currentWeatherService = provider.getServices().get("currentWeather");
                    assertNotNull(currentWeatherService, "currentWeather service should exist");

                    // Verify root level attributes are mapped
                    SensinactResource windSpeedResource = currentWeatherService.getResources().get("windSpeed");
                    assertNotNull(windSpeedResource, "windSpeed resource should exist from root level");

                    Promise<TimedValue<?>> valuePromise = windSpeedResource.getValue();
                    TimedValue<?> timedValue = valuePromise.getValue();
                    assertEquals(5.0f, timedValue.getValue(), "windSpeed value should match test data");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "Root level attributes should be mapped successfully");
    }

    @Test
    @DisplayName("Nested ReferenceMapping: should map first level nested reference (signifcantWeather6H.w1 and signifcantWeather6H.w2)")
    void nestedReferenceMapping_firstLevelNesting_isMapped() throws Exception {
        // Load weather mapping with ReferenceMapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {                    

                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Provider should exist");

                    SensinactService currentWeatherService = provider.getServices().get("currentWeather");
                    assertNotNull(currentWeatherService, "currentWeather service should exist");

                    // Verify nested reference attribute is mapped with prefix
                    SensinactResource w1Resource = currentWeatherService.getResources().get("significantWeather6HoursW1");
                    assertNotNull(w1Resource, "significantWeather6HoursW1 resource should exist from double-nested reference");

                    Promise<TimedValue<?>> w1ValuePromise = w1Resource.getValue();
                    TimedValue<?> w1TimedValue = w1ValuePromise.getValue();
                    assertEquals(WMOWeatherCodeType.W01, w1TimedValue.getValue(), "significantWeather6HoursW1 should match test data");

                    SensinactResource w2Resource = currentWeatherService.getResources().get("significantWeather6HoursW2");
                    assertNotNull(w2Resource, "significantWeather6HoursW2 resource should exist from double-nested reference");

                    Promise<TimedValue<?>> w2ValuePromise = w2Resource.getValue();
                    TimedValue<?> w2TimedValue = w2ValuePromise.getValue();
                    assertEquals(WMOWeatherCodeType.W05, w2TimedValue.getValue(), "significantWeather6HoursW2 should match test data");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "First level nested reference should be mapped successfully");
    }


    @Test
    @DisplayName("Nested ReferenceMapping: forecast3H should map from reports[1] without nested references")
    void nestedReferenceMapping_forecast3H_mapsFromSecondReport() throws Exception {
        // Load weather mapping with ReferenceMapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                   

                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Provider should exist");

                    SensinactService forecast3HService = provider.getServices().get("forecast3H");
                    assertNotNull(forecast3HService, "forecast3H service should exist");

                    // Verify attributes from reports[1] are mapped (no nested references for this service)
                    SensinactResource windSpeedResource = forecast3HService.getResources().get("windSpeed");
                    assertNotNull(windSpeedResource, "windSpeed resource should exist");

                    Promise<TimedValue<?>> valuePromise = windSpeedResource.getValue();
                    TimedValue<?> timedValue = valuePromise.getValue();
                    assertEquals(7.5f, timedValue.getValue(), "windSpeed should match reports[1] data");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "forecast3H should map from reports[1] successfully");
    }

    @Test
    @DisplayName("Nested ReferenceMapping: filter should exclude specified attributes")
    void nestedReferenceMapping_filter_excludesAttributes() throws Exception {
        // Load weather mapping with ReferenceMapping
        URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI);
        Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
        mappingResource.load(null);
        ProviderMapping weatherMapping = (ProviderMapping) mappingResource.getContents().get(0);

        // Create test data
        WeatherReports weatherReports = createWeatherReportsTestData();

        // Register mapping
        mappingRegistry.registerModelMapping(weatherMapping);

        // Execute mapping
        Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
            @Override
            protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager, PromiseFactory pf) {
                try {
                   

                    ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, weatherMapping);
                    mapper.mapInstance(weatherReports);

                    SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
                    assertNotNull(provider, "Provider should exist");

                    SensinactService currentWeatherService = provider.getServices().get("currentWeather");
                    assertNotNull(currentWeatherService, "currentWeather service should exist");

                    // Verify excluded attributes are NOT mapped
                    // The filter excludes: id, timestamp from WeatherReport
                    SensinactResource idResource = currentWeatherService.getResources().get("id");
                    assertNull(idResource, "id resource should NOT exist (excluded by filter)");

                    // Note: timestamp is used for the service timestamp, so it's handled differently
                    // but as a resource it should be excluded

                    // Verify included attributes ARE mapped
                    SensinactResource windSpeedResource = currentWeatherService.getResources().get("windSpeed");
                    assertNotNull(windSpeedResource, "windSpeed resource should exist (not excluded)");

                    return pf.resolved(true);
                } catch (Exception e) {
                    return pf.failed(e);
                }
            }
        });

        assertTrue(result.getValue(), "Filter should exclude specified attributes");
    }
}
