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
package org.eclipse.fennec.sensinact.mapping.atlas.test.component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.PackageNotFoundException;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.sensinact.mapping.InstancePusher;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * Fake southbound for live-testing the full atlas → mapping → sensinact chain, starting
 * from raw sensor data: every {@code period.seconds} it renders a WeatherReports XMI
 * payload (as an external device would send it) and tries to load it through the
 * runtime's {@link ResourceSet}.
 * <p>
 * Package resolution is the runtime's own mechanism: the resource set's package registry
 * resolves the payload's nsURI locally first, then fetch-on-miss through the Model Atlas
 * client. Three outcomes, logged each tick:
 * <ul>
 * <li>the model is unknown (not deployed, not in the atlas) → the payload cannot be
 * deserialized, nothing is handed to the {@link InstancePusher};</li>
 * <li>the model resolves but no mapping is registered → the instance is pushed and 0
 * mappings apply;</li>
 * <li>model and mapping are available (e.g. after uploading both to the atlas and the
 * source's refresh interval passing) → the instance lands in the digital twin.</li>
 * </ul>
 * @author Ilenia Salvadori
 * @since 29.07.2026
 */
@Component(immediate = true, configurationPid = "sensinact.mapping.atlas.test.simulator",
		configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class WeatherReportsSimulator {

	private static final Logger logger = Logger.getLogger(WeatherReportsSimulator.class.getName());

	private static final String TEMPLATE = "/data/weather-reports-template.xmi";
	/** Matches EMF's default EDate XMI serialization. */
	private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	public @interface Config {
		long period_seconds() default 60;
		String station_id() default "10567";
		String station_name() default "GERA";
	}

	@Reference
	private InstancePusher instancePusher;
	@Reference
	private ResourceSetFactory resourceSetFactory;

	private final Random random = new Random();
	private ScheduledExecutorService executor;
	private Config config;
	private String template;

	@Activate
	public void activate(Config config) throws IOException {
		this.config = config;
		try (InputStream in = getClass().getResourceAsStream(TEMPLATE)) {
			if (in == null) {
				throw new IOException("Missing bundle resource " + TEMPLATE);
			}
			template = new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
		executor = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "weather-simulator"));
		executor.scheduleAtFixedRate(this::pushOnce, 2, config.period_seconds(), TimeUnit.SECONDS);
		logger.info(String.format("Weather simulator started: sending WeatherReports XMI for station '%s' every %ss",
				config.station_id(), config.period_seconds()));
	}

	@Deactivate
	public void deactivate() {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

	private void pushOnce() {
		try {
			String xmi = renderPayload();
			List<EObject> roots = loadXmiIntoEObjects(xmi);
			if (roots.isEmpty()) {
				return; // could not deserialize - already logged, nothing to push
			}
			int applied = 0;
			for (EObject root : roots) {
				applied += instancePusher.pushInstance(root);
			}
			if (applied == 0) {
				logger.warning(String.format("WeatherReports payload deserialized (%s objects) but no mapping is "
						+ "registered for any of them - is the mapping in the atlas (and released)?", roots.size()));
			} else {
				logger.info(String.format("Pushed WeatherReports payload - %s mapping(s) applied", applied));
			}
		} catch (Throwable e) {
			// keep the scheduled task alive; log the full trace for diagnosis
			logger.log(Level.SEVERE, "Failed pushing simulated WeatherReports payload", e);
		}
	}

	/**
	 * Loads an XMI payload into a fresh {@link ResourceSet} from the runtime's
	 * {@link ResourceSetFactory}. Creating the resource set per payload matters: the atlas
	 * client contributes a ResourceSetConfigurator, so only resource sets created while the
	 * client is active resolve unknown nsURIs remotely (local-first, then fetch-on-miss).
	 * Returns the root EObjects, or an empty list if the payload cannot be deserialized
	 * because its model is unknown.
	 */
	private List<EObject> loadXmiIntoEObjects(String xmi) {
		ResourceSet resourceSet = resourceSetFactory.createResourceSet();
		Resource resource = resourceSet.createResource(
				URI.createURI("weather-simulator/" + System.nanoTime() + ".xmi"));
		try {
			resource.load(new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8)),
					Collections.emptyMap());
			return List.copyOf(resource.getContents());
		} catch (Exception e) {
			PackageNotFoundException pnf = findPackageNotFound(e);
			if (pnf != null) {
				logger.warning(String.format("Cannot deserialize sensor data: model '%s' is not available "
						+ "(neither deployed nor resolvable via the Model Atlas) - dropping payload",
						pnf.uri()));
			} else {
				logger.log(Level.SEVERE, "Cannot deserialize sensor data payload", e);
			}
			return List.of();
		}
	}

	private static PackageNotFoundException findPackageNotFound(Throwable e) {
		for (Throwable t = e; t != null; t = t.getCause() == t ? null : t.getCause()) {
			if (t instanceof PackageNotFoundException pnf) {
				return pnf;
			}
		}
		return null;
	}

	private String renderPayload() {
		SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
		Date now = new Date();
		float windSpeed = 3.0f + random.nextFloat() * 10.0f;
		return template
				.replace("{{STATION_ID}}", config.station_id())
				.replace("{{STATION_NAME}}", config.station_name())
				.replace("{{TS0}}", format.format(now))
				.replace("{{TS1}}", format.format(new Date(now.getTime() + TimeUnit.HOURS.toMillis(3))))
				.replace("{{WIND0}}", Float.toString(windSpeed))
				.replace("{{WIND1}}", Float.toString(windSpeed + random.nextFloat() * 3.0f))
				.replace("{{DIR0}}", Float.toString(random.nextFloat() * 360.0f))
				.replace("{{DIR1}}", Float.toString(random.nextFloat() * 360.0f))
				.replace("{{TEMP0}}", Float.toString(288.15f + random.nextFloat() * 5.0f))
				.replace("{{TEMP1}}", Float.toString(289.15f + random.nextFloat() * 5.0f))
				.replace("{{CLOUD0}}", Float.toString(random.nextFloat() * 100.0f))
				.replace("{{CLOUD1}}", Float.toString(random.nextFloat() * 100.0f))
				.replace("{{PRESSURE0}}", Float.toString(100_000.0f + random.nextFloat() * 3_000.0f))
				.replace("{{PRESSURE1}}", Float.toString(100_000.0f + random.nextFloat() * 3_000.0f));
	}

}
