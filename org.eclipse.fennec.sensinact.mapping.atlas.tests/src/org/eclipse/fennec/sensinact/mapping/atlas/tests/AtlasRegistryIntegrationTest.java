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
package org.eclipse.fennec.sensinact.mapping.atlas.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistry;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry;
import org.eclipse.fennec.sensinact.mapping.InstancePusher;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.sensinact.core.command.AbstractSensinactCommand;
import org.eclipse.sensinact.core.command.GatewayThread;
import org.eclipse.sensinact.core.model.SensinactModelManager;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;
import org.eclipse.sensinact.core.twin.SensinactProvider;
import org.eclipse.sensinact.core.twin.TimedValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.cm.Configuration;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.config.InjectConfiguration;
import org.osgi.test.common.annotation.config.WithFactoryConfiguration;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.cm.ConfigurationExtension;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * End-to-end over the emf.osgi EObject registry: a file provider (empty locations)
 * gates the registry's publication, the atlas provider syncs the mapping XMIs served by
 * the {@link MockModelAtlasServer} into it as a writer client, and the sensinact
 * facades pick the entries up through their listener face. The sensor-model EClass is
 * itself fetched from the atlas (the weather model bundle is deliberately absent from
 * this runtime).
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(ConfigurationExtension.class)
public class AtlasRegistryIntegrationTest {

	private static final String CLIENT_PID = "org.eclipse.fennec.model.atlas.rest.client";
	private static final String FILE_PROVIDER_PID = "FileEObjectProvider";
	private static final String REGISTRY_PID = "EObjectRegistry";
	private static final String ATLAS_PROVIDER_PID = "AtlasEObjectProvider";

	private static final String REGISTRY_NAME = "sensinact-mappings";
	private static final String REGISTRY_FILTER = "(emf.eobject.registry.name=" + REGISTRY_NAME + ")";

	/**
	 * The factory configs are injected untriggered ({@code @InjectConfiguration} without
	 * properties, see {@link WithFactoryConfiguration#properties()}) because the client's
	 * {@code base.uri} only exists once the mock atlas has picked its port - each test
	 * updates them itself; the extension deletes them afterwards.
	 */
	private static Dictionary<String, Object> clientProps(MockModelAtlasServer atlas) {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put("base.uri", atlas.baseUri());
		props.put("scope.allow.list", new String[] { "iot" });
		return props;
	}

	/** The registry's initial provider: no local files - a valid, empty initial state. */
	private static Dictionary<String, Object> fileProviderProps() {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put("emf.eobject.provider.name", "test-files");
		return props;
	}

	private static Dictionary<String, Object> registryProps() {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put("name", REGISTRY_NAME);
		props.put("initialProvider.target", "(emf.eobject.provider.name=test-files)");
		return props;
	}

	/**
	 * The atlas provider as a writer client: keys entries by the mapping's {@code mid},
	 * gates each pass on the generated mapping package (the fetched XMIs must not
	 * materialize as dynamic EObjects while the model bundle's configurator has not run
	 * yet), and pushes into the named registry. {@code refreshIntervalMs} 0 = sync once.
	 */
	private static Dictionary<String, Object> atlasProviderProps(long refreshIntervalMs, String... objectIds) {
		Hashtable<String, Object> props = new Hashtable<>();
		props.put("atlasScope.target", "(atlas.scope=iot)");
		props.put("writer.target", REGISTRY_FILTER);
		props.put("emf.eobject.provider.name", "atlas-test");
		props.put("registries", new String[] { "mappings" });
		props.put("key.feature", "mid");
		props.put("required.nsuris", new String[] { MappingPackage.eNS_URI });
		props.put("retry.interval.ms", 500L);
		if (refreshIntervalMs > 0) {
			props.put("refresh.interval.ms", refreshIntervalMs);
		}
		if (objectIds.length > 0) {
			props.put("object.ids", objectIds);
		}
		return props;
	}

	@Test
	void mappingAndSensorModelFromAtlasReachTheFacade(
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = FILE_PROVIDER_PID, name = "test", location = "?")) Configuration fileConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = REGISTRY_PID, name = "test", location = "?")) Configuration registryConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = CLIENT_PID, name = "test", location = "?")) Configuration clientConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = ATLAS_PROVIDER_PID, name = "test", location = "?")) Configuration atlasProviderConfig,
			@InjectService(cardinality = 0, filter = REGISTRY_FILTER) ServiceAware<EObjectRegistry> registryAware,
			@InjectService(cardinality = 0) ServiceAware<ProviderMappingRegistry> facadeAware) throws Exception {
		try (MockModelAtlasServer atlas = new MockModelAtlasServer()) {
			fileConfig.update(fileProviderProps());
			registryConfig.update(registryProps());
			clientConfig.update(clientProps(atlas));
			atlasProviderConfig.update(atlasProviderProps(0, "dwd-weather"));

			// gated publication: the service appears once the (empty) initial load is done
			EObjectRegistry registry = registryAware.waitForService(30_000);
			assertNotNull(registry, "the EObjectRegistry service did not appear - gated publication failed");

			EObjectRegistryEntry entry = waitFor(() -> registry.getEntry("dwd-weather").orElse(null),
					Objects::nonNull, 30_000);
			assertNotNull(entry, "no registry entry appeared - atlas sync failed");
			assertEquals("atlas-test:mappings", entry.source(), "entries are scoped per atlas registry");
			assertEquals(Boolean.TRUE, entry.properties().get("atlas.remote"));
			assertEquals("iot", entry.properties().get("atlas.scope"));
			assertEquals("mappings", entry.properties().get("atlas.registry"));
			assertEquals("dwd-weather", entry.properties().get("atlas.object.id"));
			assertEquals(MappingPackage.eNS_URI, entry.properties().get("emf.nsURI"));

			// generated type, not a dynamic EObject - the required-nsURI gate held the pass
			ProviderMapping mapping = (ProviderMapping) entry.object();
			assertEquals("dwd-weather", mapping.getMid());

			EClass providerClass = mapping.getProviderClasses().get(0);
			assertFalse(providerClass.eIsProxy(), "provider class proxy did not resolve against the atlas");
			assertEquals("MOSMIXSWeatherReport", providerClass.getName());
			assertEquals("http://cdc.dwd.de/common/weather", providerClass.getEPackage().getNsURI());

			ProviderMappingRegistry facade = facadeAware.waitForService(30_000);
			assertNotNull(facade);
			List<ProviderMapping> registered = waitFor(() -> facade.getProviderMapping(providerClass),
					list -> list != null && !list.isEmpty(), 10_000);
			assertNotNull(registered, "the listener face did not deliver the mapping to the facade");
			assertTrue(registered.contains(mapping));
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
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = FILE_PROVIDER_PID, name = "test", location = "?")) Configuration fileConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = REGISTRY_PID, name = "test", location = "?")) Configuration registryConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = CLIENT_PID, name = "test", location = "?")) Configuration clientConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = ATLAS_PROVIDER_PID, name = "test", location = "?")) Configuration atlasProviderConfig,
			@InjectService(cardinality = 0, filter = REGISTRY_FILTER) ServiceAware<EObjectRegistry> registryAware,
			@InjectService(cardinality = 0) ServiceAware<ProviderMappingRegistry> facadeAware,
			@InjectService(cardinality = 0) ServiceAware<InstancePusher> pusherAware,
			@InjectService(cardinality = 0) ServiceAware<GatewayThread> gatewayAware) throws Exception {
		try (MockModelAtlasServer atlas = new MockModelAtlasServer()) {
			fileConfig.update(fileProviderProps());
			registryConfig.update(registryProps());
			clientConfig.update(clientProps(atlas));
			atlasProviderConfig.update(atlasProviderProps(0, "dwd-weather-reports"));

			EObjectRegistry registry = registryAware.waitForService(30_000);
			EObjectRegistryEntry entry = waitFor(() -> registry.getEntry("dwd-weather-reports").orElse(null),
					Objects::nonNull, 30_000);
			assertNotNull(entry, "no registry entry appeared - atlas sync failed");
			ProviderMapping mapping = (ProviderMapping) entry.object();

			EClass providerClass = mapping.getProviderClasses().get(0);
			assertEquals("WeatherReports", providerClass.getName());

			ProviderMappingRegistry facade = facadeAware.waitForService(30_000);
			List<ProviderMapping> registered = waitFor(() -> facade.getProviderMapping(providerClass),
					list -> list != null && !list.isEmpty(), 10_000);
			assertNotNull(registered, "the listener face did not deliver the mapping to the facade");

			// Instance built from the SAME (atlas-fetched, dynamic) EPackage the mapping
			// resolved against - EClass identity is what the facade lookup keys on
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
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = FILE_PROVIDER_PID, name = "test", location = "?")) Configuration fileConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = REGISTRY_PID, name = "test", location = "?")) Configuration registryConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = CLIENT_PID, name = "test", location = "?")) Configuration clientConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = ATLAS_PROVIDER_PID, name = "test", location = "?")) Configuration atlasProviderConfig,
			@InjectService(timeout = 5000) ResourceSetFactory resourceSetFactory,
			@InjectService(cardinality = 0, filter = REGISTRY_FILTER) ServiceAware<EObjectRegistry> registryAware,
			@InjectService(cardinality = 0) ServiceAware<ProviderMappingRegistry> facadeAware,
			@InjectService(cardinality = 0) ServiceAware<InstancePusher> pusherAware,
			@InjectService(cardinality = 0) ServiceAware<GatewayThread> gatewayAware) throws Exception {

		// Phase 1: unknown model - the payload cannot be deserialized at all
		assertTrue(loadXmi(resourceSetFactory, UNKNOWN_MODEL_XMI).isEmpty(),
				"a payload of an unknown model must not deserialize");

		try (MockModelAtlasServer atlas = new MockModelAtlasServer()) {
			fileConfig.update(fileProviderProps());
			registryConfig.update(registryProps());
			clientConfig.update(clientProps(atlas));
			atlasProviderConfig.update(atlasProviderProps(0, "dwd-weather-reports"));

			EObjectRegistry registry = registryAware.waitForService(30_000);
			assertNotNull(waitFor(() -> registry.getEntry("dwd-weather-reports").orElse(null), Objects::nonNull,
					30_000), "no registry entry appeared - atlas sync failed");
			InstancePusher pusher = pusherAware.waitForService(30_000);

			// Phase 2: weather model resolves via the atlas; retry while the client warms up.
			// A FRESH resource set per attempt is essential: the atlas client contributes a
			// ResourceSetConfigurator, so only resource sets created while it is active
			// resolve nsURIs remotely.
			List<EObject> roots = waitFor(() -> loadXmi(resourceSetFactory, WEATHER_XMI),
					list -> !list.isEmpty(), 15_000);
			assertNotNull(roots, "weather payload did not deserialize - atlas package resolution failed; last: "
					+ lastLoadFailure.get());

			// Wait until the listener face delivered the mapping to the facade, keyed by the
			// payload's OWN EClass - this is also the EClass-identity proof: the payload's
			// package (published via the lazy registry) and the mapping's providerClasses
			// (resolved by the client) must be the same instance
			EObject weatherReports = roots.get(0);
			ProviderMappingRegistry facade = facadeAware.waitForService(30_000);
			List<ProviderMapping> registered = waitFor(
					() -> facade.getProviderMapping(weatherReports.eClass()),
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
		}
	}

	/**
	 * The source-loss case: an object deleted on the atlas disappears from the registry
	 * (and the facade) on the next refresh pass - via the writer's per-source sync - while
	 * the other entries of the same source stay untouched.
	 */
	@Test
	void refreshRemovesObjectsGoneFromTheAtlas(
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = FILE_PROVIDER_PID, name = "test", location = "?")) Configuration fileConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = REGISTRY_PID, name = "test", location = "?")) Configuration registryConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = CLIENT_PID, name = "test", location = "?")) Configuration clientConfig,
			@InjectConfiguration(withFactoryConfig = @WithFactoryConfiguration(factoryPid = ATLAS_PROVIDER_PID, name = "test", location = "?")) Configuration atlasProviderConfig,
			@InjectService(cardinality = 0, filter = REGISTRY_FILTER) ServiceAware<EObjectRegistry> registryAware,
			@InjectService(cardinality = 0) ServiceAware<ProviderMappingRegistry> facadeAware) throws Exception {
		try (MockModelAtlasServer atlas = new MockModelAtlasServer()) {
			fileConfig.update(fileProviderProps());
			registryConfig.update(registryProps());
			clientConfig.update(clientProps(atlas));
			// list mode (no explicit object ids) - removal must be detected from the listing
			atlasProviderConfig.update(atlasProviderProps(1_000));

			EObjectRegistry registry = registryAware.waitForService(30_000);
			EObjectRegistryEntry entry = waitFor(() -> registry.getEntry("dwd-weather").orElse(null),
					Objects::nonNull, 30_000);
			assertNotNull(entry, "no registry entry appeared - atlas sync failed");
			assertNotNull(waitFor(() -> registry.getEntry("dwd-weather-reports").orElse(null), Objects::nonNull,
					30_000));
			EClass providerClass = ((ProviderMapping) entry.object()).getProviderClasses().get(0);

			ProviderMappingRegistry facade = facadeAware.waitForService(30_000);
			assertNotNull(waitFor(() -> facade.getProviderMapping(providerClass),
					list -> list != null && !list.isEmpty(), 10_000));

			// the object disappears from the atlas - the next complete pass syncs the remainder
			atlas.hideObject("dwd-weather");
			assertNotNull(waitFor(() -> registry.getEntry("dwd-weather").isEmpty() ? Boolean.TRUE : null,
					Objects::nonNull, 15_000), "the gone object was not removed from the registry");
			assertNotNull(waitFor(() -> facade.getProviderMapping(providerClass).isEmpty() ? Boolean.TRUE : null,
					Objects::nonNull, 10_000), "the gone mapping was not dropped by the facade");
			assertTrue(registry.getEntry("dwd-weather-reports").isPresent(),
					"other entries of the same source must stay untouched");
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
