/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
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
package org.eclipse.fennec.sensinact.mapping.atlas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.sensinact.mapping.InstancePusher;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.sensinact.core.command.AbstractSensinactCommand;
import org.eclipse.sensinact.core.command.GatewayThread;
import org.eclipse.sensinact.core.model.SensinactModelManager;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;
import org.eclipse.sensinact.core.twin.SensinactProvider;
import org.eclipse.sensinact.core.twin.TimedValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * End-to-end: a factory config for the atlas rest client plus one for the mapping source
 * turn a mapping XMI served by the {@link MockModelAtlasServer} into a
 * {@link ProviderMapping} OSGi service, whose sensor-model EClass was itself fetched from
 * the atlas (the weather model bundle is deliberately absent from this runtime), and the
 * sensinact whiteboard registry picks it up.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class AtlasMappingSourceIntegrationTest {

	private static final String CLIENT_PID = "org.eclipse.fennec.model.atlas.rest.client";
	private static final String SOURCE_PID = "org.eclipse.fennec.sensinact.mapping.atlas";

	@Test
	void mappingAndSensorModelFromAtlasReachTheRegistry(
			@InjectService(timeout = 5000) ConfigurationAdmin configurationAdmin,
			@InjectService(cardinality = 0) ServiceAware<ProviderMapping> mappingAware,
			@InjectService(cardinality = 0) ServiceAware<ProviderMappingRegistry> registryAware) throws Exception {
		try (MockModelAtlasServer atlas = new MockModelAtlasServer()) {
			Configuration clientConfig = configurationAdmin.createFactoryConfiguration(CLIENT_PID, "?");
			Configuration sourceConfig = configurationAdmin.createFactoryConfiguration(SOURCE_PID, "?");
			try {
				Hashtable<String, Object> clientProps = new Hashtable<>();
				clientProps.put("base.uri", atlas.baseUri());
				clientProps.put("scope.allow.list", new String[] { "iot" });
				clientConfig.update(clientProps);

				Hashtable<String, Object> sourceProps = new Hashtable<>();
				sourceProps.put("atlasScope.target", "(atlas.scope=iot)");
				sourceProps.put("object.ids", new String[] { "dwd-weather" });
				sourceConfig.update(sourceProps);

				ProviderMapping mapping = mappingAware.waitForService(30_000);
				assertNotNull(mapping, "no ProviderMapping service appeared - atlas load failed");
				assertEquals("dwd-weather", mapping.getMid());

				ServiceReference<ProviderMapping> reference = mappingAware.getServiceReference();
				assertEquals(Boolean.TRUE, reference.getProperty("atlas.remote"));
				assertEquals("iot", reference.getProperty("atlas.scope"));
				assertEquals("mappings", reference.getProperty("atlas.registry"));
				assertEquals("dwd-weather", reference.getProperty("atlas.object.id"));
				assertEquals("dwd-weather", reference.getProperty("sensinact.mapping.mid"));

				EClass providerClass = mapping.getProviderClasses().get(0);
				assertFalse(providerClass.eIsProxy(), "provider class proxy did not resolve against the atlas");
				assertEquals("MOSMIXSWeatherReport", providerClass.getName());
				assertEquals("http://cdc.dwd.de/common/weather", providerClass.getEPackage().getNsURI());

				ProviderMappingRegistry registry = registryAware.waitForService(30_000);
				assertNotNull(registry);
				List<ProviderMapping> registered = waitFor(() -> registry.getProviderMapping(providerClass),
						list -> list != null && !list.isEmpty(), 10_000);
				assertNotNull(registered, "the whiteboard did not deliver the mapping to the registry");
				assertTrue(registered.contains(mapping));
			} finally {
				sourceConfig.delete();
				clientConfig.delete();
			}
		}
	}

	/**
	 * End-to-end data push: the WeatherReports mapping (collection selectors via
	 * referencedResource) comes from the atlas, the sensor model is only available
	 * through the atlas, an instance built from that dynamic EPackage is handed to the
	 * {@link InstancePusher}, and the values land in the digital twin.
	 */
	@Test
	void pushedInstanceReachesTheTwinThroughAtlasMapping(
			@InjectService(timeout = 5000) ConfigurationAdmin configurationAdmin,
			@InjectService(cardinality = 0) ServiceAware<ProviderMapping> mappingAware,
			@InjectService(cardinality = 0) ServiceAware<ProviderMappingRegistry> registryAware,
			@InjectService(cardinality = 0) ServiceAware<InstancePusher> pusherAware,
			@InjectService(cardinality = 0) ServiceAware<GatewayThread> gatewayAware) throws Exception {
		try (MockModelAtlasServer atlas = new MockModelAtlasServer()) {
			Configuration clientConfig = configurationAdmin.createFactoryConfiguration(CLIENT_PID, "?");
			Configuration sourceConfig = configurationAdmin.createFactoryConfiguration(SOURCE_PID, "?");
			try {
				Hashtable<String, Object> clientProps = new Hashtable<>();
				clientProps.put("base.uri", atlas.baseUri());
				clientProps.put("scope.allow.list", new String[] { "iot" });
				clientConfig.update(clientProps);

				Hashtable<String, Object> sourceProps = new Hashtable<>();
				sourceProps.put("atlasScope.target", "(atlas.scope=iot)");
				sourceProps.put("object.ids", new String[] { "dwd-weather-reports" });
				sourceConfig.update(sourceProps);

				ProviderMapping mapping = mappingAware.waitForService(30_000);
				assertNotNull(mapping, "no ProviderMapping service appeared - atlas load failed");
				assertEquals("dwd-weather-reports", mapping.getMid());

				EClass providerClass = mapping.getProviderClasses().get(0);
				assertEquals("WeatherReports", providerClass.getName());

				ProviderMappingRegistry registry = registryAware.waitForService(30_000);
				List<ProviderMapping> registered = waitFor(() -> registry.getProviderMapping(providerClass),
						list -> list != null && !list.isEmpty(), 10_000);
				assertNotNull(registered, "the whiteboard did not deliver the mapping to the registry");

				// Instance built from the SAME (atlas-fetched, dynamic) EPackage the mapping
				// resolved against - EClass identity is what the registry lookup keys on
				EObject weatherReports = createWeatherReports(providerClass.getEPackage());

				InstancePusher pusher = pusherAware.waitForService(30_000);
				assertEquals(1, pusher.pushInstance(weatherReports),
						"exactly the atlas mapping should have been applied");

				GatewayThread gatewayThread = gatewayAware.waitForService(30_000);
				Promise<Boolean> verified = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
					@Override
					protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager,
							PromiseFactory pf) {
						try {
							SensinactProvider provider = twin.getProvider("dwd-weather-reports", "10567");
							assertNotNull(provider, "provider id should come from reports[0].weatherStation.id");

							TimedValue<?> current = provider.getServices().get("currentWeather")
									.getResources().get("windSpeed").getValue().getValue();
							assertEquals(5.0f, current.getValue(), "currentWeather reads reports[0]");

							TimedValue<?> forecast = provider.getServices().get("forecast3H")
									.getResources().get("windSpeed").getValue().getValue();
							assertEquals(7.5f, forecast.getValue(), "forecast3H reads reports[1]");

							return pf.resolved(true);
						} catch (Exception e) {
							return pf.failed(e);
						}
					}
				});
				assertTrue(verified.getValue(), "twin verification should succeed");
			} finally {
				sourceConfig.delete();
				clientConfig.delete();
			}
		}
	}

	/**
	 * The raw-sensor-data storyline: an XMI payload arrives and is loaded through the
	 * runtime {@link ResourceSet}, whose package registry resolves nsURIs local-first and
	 * then via the atlas client (fetch-on-miss). A payload with an unknown model cannot be
	 * deserialized (nothing reaches the pusher); the weather payload resolves through the
	 * atlas and lands in the twin.
	 */
	@Test
	void xmiPayloadResolvesModelThroughAtlasAndReachesTheTwin(
			@InjectService(timeout = 5000) ConfigurationAdmin configurationAdmin,
			@InjectService(timeout = 5000) ResourceSetFactory resourceSetFactory,
			@InjectService(cardinality = 0) ServiceAware<ProviderMapping> mappingAware,
			@InjectService(cardinality = 0) ServiceAware<ProviderMappingRegistry> registryAware,
			@InjectService(cardinality = 0) ServiceAware<InstancePusher> pusherAware,
			@InjectService(cardinality = 0) ServiceAware<GatewayThread> gatewayAware) throws Exception {

		// Phase 1: unknown model - the payload cannot be deserialized at all
		assertTrue(loadXmi(resourceSetFactory, UNKNOWN_MODEL_XMI).isEmpty(),
				"a payload of an unknown model must not deserialize");

		try (MockModelAtlasServer atlas = new MockModelAtlasServer()) {
			Configuration clientConfig = configurationAdmin.createFactoryConfiguration(CLIENT_PID, "?");
			Configuration sourceConfig = configurationAdmin.createFactoryConfiguration(SOURCE_PID, "?");
			try {
				Hashtable<String, Object> clientProps = new Hashtable<>();
				clientProps.put("base.uri", atlas.baseUri());
				clientProps.put("scope.allow.list", new String[] { "iot" });
				clientConfig.update(clientProps);

				Hashtable<String, Object> sourceProps = new Hashtable<>();
				sourceProps.put("atlasScope.target", "(atlas.scope=iot)");
				sourceProps.put("object.ids", new String[] { "dwd-weather-reports" });
				sourceConfig.update(sourceProps);

				assertNotNull(mappingAware.waitForService(30_000),
						"no ProviderMapping service appeared - atlas load failed");
				InstancePusher pusher = pusherAware.waitForService(30_000);

				// Phase 2: weather model resolves via the atlas; retry while the client warms up.
				// A FRESH resource set per attempt is essential: the atlas client contributes a
				// ResourceSetConfigurator, so only resource sets created while it is active
				// resolve nsURIs remotely.
				List<EObject> roots = waitFor(() -> loadXmi(resourceSetFactory, WEATHER_XMI),
						list -> !list.isEmpty(), 15_000);
				assertNotNull(roots, "weather payload did not deserialize - atlas package resolution failed; last: "
						+ lastLoadFailure.get());

				// Wait until the whiteboard delivered the mapping to the registry, keyed by the
				// payload's OWN EClass - this is also the EClass-identity proof: the payload's
				// package (published via the lazy registry) and the mapping's providerClasses
				// (resolved by the client) must be the same instance
				EObject weatherReports = roots.get(0);
				ProviderMappingRegistry registry = registryAware.waitForService(30_000);
				List<ProviderMapping> registered = waitFor(
						() -> registry.getProviderMapping(weatherReports.eClass()),
						list -> list != null && !list.isEmpty(), 10_000);
				assertNotNull(registered, "no mapping registered under the payload's EClass - "
						+ "EClass identity between payload package and mapping package is broken");

				int applied = 0;
				for (EObject root : roots) {
					applied += pusher.pushInstance(root);
				}
				assertEquals(1, applied, "the WeatherReports root should match exactly one mapping");

				GatewayThread gatewayThread = gatewayAware.waitForService(30_000);
				Promise<Boolean> verified = gatewayThread.execute(new AbstractSensinactCommand<Boolean>() {
					@Override
					protected Promise<Boolean> call(SensinactDigitalTwin twin, SensinactModelManager modelManager,
							PromiseFactory pf) {
						try {
							SensinactProvider provider = twin.getProvider("dwd-weather-reports", "10567");
							assertNotNull(provider, "provider id should come from reports[0].weatherStation.id");
							TimedValue<?> current = provider.getServices().get("currentWeather")
									.getResources().get("windSpeed").getValue().getValue();
							assertEquals(5.5f, current.getValue(), "currentWeather reads reports[0]");
							TimedValue<?> forecast = provider.getServices().get("forecast3H")
									.getResources().get("windSpeed").getValue().getValue();
							assertEquals(7.5f, forecast.getValue(), "forecast3H reads reports[1]");
							return pf.resolved(true);
						} catch (Exception e) {
							return pf.failed(e);
						}
					}
				});
				assertTrue(verified.getValue(), "twin verification should succeed");
			} finally {
				sourceConfig.delete();
				clientConfig.delete();
			}
		}
	}

	private static final String UNKNOWN_MODEL_XMI = """
			<?xml version="1.0" encoding="UTF-8"?>
			<xmi:XMI xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
			    xmlns:mystery="http://example.com/never/registered/model">
			  <mystery:Whatever xmi:id="w0" value="42"/>
			</xmi:XMI>
			""";

	private static final String WEATHER_XMI = """
			<?xml version="1.0" encoding="UTF-8"?>
			<xmi:XMI xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
			    xmlns:dwdweather="http://cdc.dwd.de/common/weather">
			  <dwdweather:WeatherReports xmi:id="wr" id="simulated-10567" reports="r0 r1"/>
			  <dwdweather:MOSMIXSWeatherReport xmi:id="r0" id="report-current"
			      timestamp="2026-07-29T09:00:00.000+0200" station="st0" weatherStation="ws0"
			      windDirection="180.0" windSpeed="5.5" tempAboveSurface5="288.15"/>
			  <dwdweather:MOSMIXSWeatherReport xmi:id="r1" id="report-forecast-3h"
			      timestamp="2026-07-29T12:00:00.000+0200" station="st0" weatherStation="ws0"
			      windDirection="200.0" windSpeed="7.5" tempAboveSurface5="290.15"/>
			  <dwdweather:WeatherStation xmi:id="ws0" name="GERA" id="10567">
			    <location latitude="50.88" longitude="12.13" elevation="311"/>
			  </dwdweather:WeatherStation>
			  <dwdweather:Station xmi:id="st0" name="GERA">
			    <location latitude="50.88" longitude="12.13" elevation="311"/>
			  </dwdweather:Station>
			</xmi:XMI>
			""";

	private static final java.util.concurrent.atomic.AtomicReference<Throwable> lastLoadFailure = new java.util.concurrent.atomic.AtomicReference<>();

	/** Loads an XMI payload into a fresh ResourceSet; empty list when it cannot deserialize. */
	private static List<EObject> loadXmi(ResourceSetFactory resourceSetFactory, String xmi) {
		ResourceSet resourceSet = resourceSetFactory.createResourceSet();
		try {
			Resource resource = resourceSet.createResource(
					URI.createURI("test-payload/" + System.nanoTime() + ".xmi"));
			resource.load(new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8)), Map.of());
			return List.copyOf(resource.getContents());
		} catch (Exception e) {
			lastLoadFailure.set(e);
			return List.of();
		}
	}

	/** Builds a WeatherReports instance with two MOSMIXSWeatherReports purely dynamically. */
	private static EObject createWeatherReports(EPackage weatherPackage) {
		EObject weatherStation = create(weatherPackage, "WeatherStation");
		set(weatherStation, "id", "10567");
		set(weatherStation, "name", "GERA");
		set(weatherStation, "location", geoPosition(weatherPackage));

		EObject station = create(weatherPackage, "Station");
		set(station, "name", "GERA");
		set(station, "location", geoPosition(weatherPackage));

		EObject report0 = report(weatherPackage, "report-0", new Date(), weatherStation, station, 5.0f);
		EObject report1 = report(weatherPackage, "report-1",
				new Date(System.currentTimeMillis() + 10_800_000L), weatherStation, station, 7.5f);

		EObject weatherReports = create(weatherPackage, "WeatherReports");
		set(weatherReports, "id", "station-10567");
		@SuppressWarnings("unchecked")
		List<EObject> reports = (List<EObject>) weatherReports
				.eGet(weatherReports.eClass().getEStructuralFeature("reports"));
		reports.add(report0);
		reports.add(report1);
		return weatherReports;
	}

	private static EObject report(EPackage weatherPackage, String id, Date timestamp,
			EObject weatherStation, EObject station, float windSpeed) {
		EObject report = create(weatherPackage, "MOSMIXSWeatherReport");
		set(report, "id", id);
		set(report, "timestamp", timestamp);
		set(report, "weatherStation", weatherStation);
		set(report, "station", station);
		set(report, "windSpeed", windSpeed);
		return report;
	}

	private static EObject geoPosition(EPackage weatherPackage) {
		EObject location = create(weatherPackage, "GeoPosition");
		set(location, "latitude", 50.88d);
		set(location, "longitude", 12.13d);
		set(location, "elevation", 311);
		return location;
	}

	private static EObject create(EPackage ePackage, String className) {
		return ePackage.getEFactoryInstance().create((EClass) ePackage.getEClassifier(className));
	}

	private static void set(EObject object, String featureName, Object value) {
		object.eSet(object.eClass().getEStructuralFeature(featureName), value);
	}

	private static <T> T waitFor(Supplier<T> supplier, Predicate<T> condition, long timeoutMs)
			throws InterruptedException {
		long deadline = System.currentTimeMillis() + timeoutMs;
		while (System.currentTimeMillis() < deadline) {
			try {
				T value = supplier.get();
				if (condition.test(value)) {
					return value;
				}
			} catch (Exception e) {
				// registry may not know the EClass yet
			}
			Thread.sleep(200);
		}
		return null;
	}
}
