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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.Date;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.sensinact.mapping.InstancePusher;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
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
import org.gecko.weather.model.weather.WeatherFactory;
import org.gecko.weather.model.weather.WeatherReports;
import org.gecko.weather.model.weather.WeatherStation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * Tests the {@link InstancePusher} service: an incoming instance is matched against the
 * registry by its EClass and pushed into the digital twin via all matching mappings.
 * @author Ilenia Salvadori
 * @since 29.07.2026
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class InstancePusherTest {

	private static final String WEATHER_MAPPING_URI = "/data/WeatherReportsProviderMapping.xmi";
	private static final String WEATHER_MODEL = "dwd-weather-reports";

	@InjectService
	GatewayThread gatewayThread;

	@InjectService
	ProviderMappingRegistry mappingRegistry;

	@InjectService
	InstancePusher instancePusher;

	@InjectService
	ResourceSet resourceSet;

	@AfterEach
	void tearDown() {
		mappingRegistry.dispose();
	}

	@Test
	@DisplayName("InstancePusher should push a matching instance through the registered mapping")
	void pushInstance_withRegisteredMapping_updatesTwin() throws Exception {
		ProviderMapping weatherMapping = loadWeatherMapping();
		mappingRegistry.registerModelMapping(weatherMapping);

		int applied = instancePusher.pushInstance(createWeatherReports());
		assertEquals(1, applied, "Exactly one mapping should have been applied");

		Promise<Boolean> result = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
			@Override
			protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager,
					PromiseFactory pf) {
				try {
					SensinactProvider provider = twin.getProvider(WEATHER_MODEL, "10567");
					assertNotNull(provider, "Provider should have been created by the push");

					SensinactService currentWeather = provider.getServices().get("currentWeather");
					assertNotNull(currentWeather, "currentWeather service should exist");
					SensinactResource windSpeed = currentWeather.getResources().get("windSpeed");
					assertNotNull(windSpeed, "windSpeed resource should exist");
					TimedValue<?> current = windSpeed.getValue().getValue();
					assertEquals(5.0f, current.getValue(), "currentWeather.windSpeed should come from reports[0]");

					SensinactService forecast = provider.getServices().get("forecast3H");
					assertNotNull(forecast, "forecast3H service should exist");
					TimedValue<?> forecastValue = forecast.getResources().get("windSpeed").getValue().getValue();
					assertEquals(7.5f, forecastValue.getValue(), "forecast3H.windSpeed should come from reports[1]");

					return pf.resolved(true);
				} catch (Exception e) {
					return pf.failed(e);
				}
			}
		});
		assertTrue(result.getValue(), "Twin verification should succeed");
	}

	@Test
	@DisplayName("InstancePusher should return 0 for an instance without a registered mapping")
	void pushInstance_withoutMapping_returnsZero() {
		WeatherStation unmapped = WeatherFactory.eINSTANCE.createWeatherStation();
		unmapped.setId("unmapped");
		unmapped.setName("Unmapped");

		assertEquals(0, instancePusher.pushInstance(unmapped));
	}

	@Test
	@DisplayName("InstancePusher should reject null instances")
	void pushInstance_withNull_throws() {
		assertThrows(NullPointerException.class, () -> instancePusher.pushInstance(null));
	}

	private ProviderMapping loadWeatherMapping() throws Exception {
		URL mappingUrl = getClass().getResource(WEATHER_MAPPING_URI);
		Resource mappingResource = resourceSet.createResource(URI.createURI(mappingUrl.toString()));
		mappingResource.load(null);
		return (ProviderMapping) mappingResource.getContents().get(0);
	}

	private WeatherReports createWeatherReports() {
		WeatherFactory factory = WeatherFactory.eINSTANCE;

		WeatherReports weatherReports = factory.createWeatherReports();
		weatherReports.setId("station-10567");

		WeatherStation weatherStation = factory.createWeatherStation();
		weatherStation.setId("10567");
		weatherStation.setName("GERA");
		GeoPosition location = factory.createGeoPosition();
		location.setLatitude(50.88);
		location.setLongitude(12.13);
		location.setElevation(311);
		weatherStation.setLocation(location);

		Station station = factory.createStation();
		station.setName("GERA");
		station.setLocation(EcoreUtil.copy(location));

		MOSMIXSWeatherReport report0 = factory.createMOSMIXSWeatherReport();
		report0.setId("report-0");
		report0.setTimestamp(new Date());
		report0.setWeatherStation(weatherStation);
		report0.setStation(station);
		report0.setWindSpeed(5.0f);
		report0.setWindDirection(180.0f);
		report0.setTempAboveSurface5(288.15f);
		weatherReports.getReports().add(report0);

		MOSMIXSWeatherReport report1 = factory.createMOSMIXSWeatherReport();
		report1.setId("report-1");
		report1.setTimestamp(new Date(System.currentTimeMillis() + 10_800_000L));
		report1.setWeatherStation(weatherStation);
		report1.setStation(station);
		report1.setWindSpeed(7.5f);
		report1.setWindDirection(200.0f);
		report1.setTempAboveSurface5(290.15f);
		weatherReports.getReports().add(report1);

		return weatherReports;
	}

}
