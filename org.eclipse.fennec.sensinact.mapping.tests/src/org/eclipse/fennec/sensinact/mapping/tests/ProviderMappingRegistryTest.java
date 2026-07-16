/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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
package org.eclipse.fennec.sensinact.mapping.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.gecko.weather.model.weather.WeatherPackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests just the registration 
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class ProviderMappingRegistryTest {

//	private static final URI WEATHER_REPORT_URI = URI.createURI("data/10567-2025071112.weather");
	private static final String MAPPING_URI = "/data/WeatherProviderMapping.xmi";

	@InjectService
	WeatherPackage weatherPackage;
	@InjectService
	ResourceSet resourceSet;
	@InjectService
	ProviderMappingRegistry registry;
	private ProviderMapping weatherMapping;
//	private MOSMIXSWeatherReport weatherReport;

	@BeforeEach
	public void beforeEach() throws IOException {

		// Load the provider mapping from an XMI file
		URL providerUrl = getClass().getResource(MAPPING_URI);
		Resource providerResource = resourceSet.createResource(URI.createURI(providerUrl.toString()));
		providerResource.load(null);
		weatherMapping = (ProviderMapping) providerResource.getContents().get(0);

		// Register the mapping with the registry
		registry.registerModelMapping(weatherMapping);

		// Load a sample weather report for use in tests
//		Resource reportResource = resourceSet.createResource(WEATHER_REPORT_URI);
//		reportResource.load(null);
//		weatherReport = (MOSMIXSWeatherReport) reportResource.getContents().get(0);
	}
	
	@AfterEach
	public void afterEach() {
		registry.dispose();
	}

	@Test
	@DisplayName("Registry setup should register mapping correctly")
	void registrySetup_registersMapping() {
		// Verify that the mapping loaded in beforeEach is correctly registered
		List<ProviderMapping> mappings = registry.getProviderMapping(weatherPackage.getMOSMIXSWeatherReport());
		assertNotNull(mappings);
		assertFalse(mappings.isEmpty());
		assertEquals(1, mappings.size());
		assertEquals(weatherMapping, mappings.get(0));
	}

	@Test
	@DisplayName("removeProviderMapping() should unregister a mapping")
	void removeProviderMapping_unregistersMapping() {
		// Precondition check
		assertFalse(registry.getProviderMapping(weatherPackage.getMOSMIXSWeatherReport()).isEmpty(), "Precondition: mapping should be present.");

		// Execute
		registry.unregisterModelMapping(weatherMapping);

		// Verify
		assertTrue(registry.getProviderMapping(weatherPackage.getMOSMIXSWeatherReport()).isEmpty(), "Mapping should be removed.");
	}

	@Test
	@DisplayName("getProviderMapping() should throw NullPointerException for null EClass")
	void getProviderMapping_withNullEClass_throwsNPE() {
		assertThrows(NullPointerException.class, () -> registry.getProviderMapping(null));
	}

	@Test
	@DisplayName("getProviderMapping() should return an empty list for an unknown EClass")
	void getProviderMapping_noMappings_returnsEmptyList() {
		// Use an EClass for which no mapping was registered
		List<ProviderMapping> result = registry.getProviderMapping(weatherPackage.getMeasurementWeatherReport());
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

}
