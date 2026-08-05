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
package org.eclipse.fennec.sensinact.mapping.atlas.impl;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.model.atlas.scope.api.AtlasProperties;
import org.eclipse.fennec.model.atlas.scope.api.ReadableRegistryView;
import org.eclipse.fennec.model.atlas.scope.api.ReadableScopeService;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

/**
 * Reads {@link ProviderMapping} and {@link MappingProfile} instances from a Model Atlas
 * scope and registers each as an OSGi service, so the whiteboards of
 * {@code ProviderMappingRegistryImpl} and {@code MappingProfileRegistryImpl} pick them up.
 * <p>
 * The atlas scope is selected through the {@code atlasScope.target} configuration
 * property (e.g. {@code (atlas.scope=iot)}), matching the {@link ReadableScopeService}
 * services published by the atlas rest client. All atlas I/O runs on a private executor;
 * activation never blocks on the network.
 *
 * @since 07/2026
 */
@Component(name = AtlasMappingSourceComponent.PID, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = AtlasMappingSourceConfig.class, factory = true)
// The mappings are registered via BundleContext.registerService (one service per atlas
// object), so DS does not declare the capabilities - the resolver needs them spelled out.
@Capability(namespace = "osgi.service", attribute = { "objectClass:List<String>=\"org.eclipse.fennec.sensinact.model.mapping.ProviderMapping\"", "uses:=\"org.eclipse.fennec.sensinact.model.mapping\"" })
@Capability(namespace = "osgi.service", attribute = { "objectClass:List<String>=\"org.eclipse.fennec.sensinact.model.mapping.MappingProfile\"", "uses:=\"org.eclipse.fennec.sensinact.model.mapping\"" })
public class AtlasMappingSourceComponent {

	public static final String PID = "org.eclipse.fennec.sensinact.mapping.atlas";
	public static final String PROP_MID = "sensinact.mapping.mid";
	public static final String PROP_PROFILE_ID = "sensinact.mapping.profile.id";
	public static final String PROP_OBJECT_ID = "atlas.object.id";

	private static final Logger logger = Logger.getLogger(AtlasMappingSourceComponent.class.getName());

	private final BundleContext ctx;
	private final ReadableScopeService<EObject> scopeService;
	private final AtlasMappingSourceConfig config;
	private final ScheduledExecutorService executor;
	private final Map<String, Held> registrations = new HashMap<>();
	private volatile boolean active = true;

	private record Held(EObject object, ServiceRegistration<?> registration) {
	}

	@Activate
	public AtlasMappingSourceComponent(BundleContext ctx,
			@Reference(name = "atlasScope") ReadableScopeService<EObject> scopeService,
			AtlasMappingSourceConfig config) {
		this.ctx = ctx;
		this.scopeService = scopeService;
		this.config = config;
		executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
			Thread thread = new Thread(runnable, "sensinact-mapping-atlas-" + scopeService.getScopeName());
			thread.setDaemon(true);
			return thread;
		});
		executor.execute(this::initialLoad);
	}

	@Deactivate
	public void deactivate() {
		active = false;
		executor.shutdownNow();
		try {
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		synchronized (registrations) {
			registrations.values().forEach(held -> unregister(held.registration()));
			registrations.clear();
		}
	}

	private void initialLoad() {
		boolean complete;
		try {
			complete = sync();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Initial load from atlas scope " + scopeService.getScopeName() + " failed", e);
			complete = false;
		}
		if (complete) {
			if (config.refresh_interval_ms() > 0) {
				executor.scheduleWithFixedDelay(this::refresh, config.refresh_interval_ms(),
						config.refresh_interval_ms(), TimeUnit.MILLISECONDS);
			}
		} else if (config.retry_interval_ms() > 0) {
			executor.schedule(this::initialLoad, config.retry_interval_ms(), TimeUnit.MILLISECONDS);
		}
	}

	private void refresh() {
		try {
			sync();
		} catch (Exception e) {
			logger.log(Level.WARNING, "Refresh from atlas scope " + scopeService.getScopeName()
					+ " failed - keeping the currently registered mappings", e);
		}
	}

	/**
	 * One full pass over all configured registries. Registers new objects, swaps changed
	 * ones, unregisters objects that are definitively gone. Transient per-object or
	 * per-registry failures keep the existing registrations.
	 *
	 * @return {@code true} if every configured registry and object was processed successfully
	 */
	private boolean sync() {
		// The atlas client resolves nsURIs local-first against EPackage.Registry.INSTANCE;
		// only a locally registered generated package makes fetched objects instances of
		// ProviderMapping/MappingProfile instead of dynamic EObjects.
		EPackage.Registry.INSTANCE.putIfAbsent(MappingPackage.eNS_URI, MappingPackage.eINSTANCE);

		boolean complete = true;
		Set<String> seen = new HashSet<>();
		for (String registry : config.registries()) {
			complete &= syncRegistry(registry, seen);
		}
		synchronized (registrations) {
			Iterator<Entry<String, Held>> it = registrations.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Held> entry = it.next();
				if (!seen.contains(entry.getKey())) {
					unregister(entry.getValue().registration());
					it.remove();
				}
			}
		}
		return complete;
	}

	private boolean syncRegistry(String registry, Set<String> seen) {
		ReadableRegistryView<EObject> view;
		List<String> ids;
		try {
			view = stage().isEmpty() ? scopeService.registryView(registry)
					: scopeService.registryView(registry, stage());
			ids = objectIds().isEmpty() ? view.listObjectIds() : objectIds();
		} catch (Exception e) {
			logger.log(Level.WARNING, String.format(
					"Cannot list objects of atlas registry %s/%s - keeping the currently registered mappings",
					scopeService.getScopeName(), registry), e);
			synchronized (registrations) {
				registrations.keySet().stream().filter(key -> key.startsWith(registry + "/")).forEach(seen::add);
			}
			return false;
		}
		boolean complete = true;
		for (String objectId : ids) {
			String key = registry + "/" + objectId;
			try {
				Optional<EObject> fetched = view.get(objectId);
				if (fetched.isEmpty()) {
					logger.warning(String.format("Object %s is not available in atlas registry %s/%s", objectId,
							scopeService.getScopeName(), registry));
					complete = false;
					continue;
				}
				if (swap(key, registry, objectId, fetched.get())) {
					seen.add(key);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, String.format(
						"Fetching object %s from atlas registry %s/%s failed - keeping the current registration",
						objectId, scopeService.getScopeName(), registry), e);
				seen.add(key);
				complete = false;
			}
		}
		return complete;
	}

	/**
	 * Registers the object if it is new or changed. The atlas client's ETag cache returns
	 * the identical instance while an object is unchanged on the server, so an identity
	 * compare detects change; on change the new service is registered before the old one
	 * is unregistered, so the whiteboards see remove-after-add.
	 */
	private boolean swap(String key, String registry, String objectId, EObject object) {
		synchronized (registrations) {
			if (!active) {
				return false;
			}
			Held held = registrations.get(key);
			if (held != null && held.object() == object) {
				return true;
			}
			ServiceRegistration<?> registration = register(object, registry, objectId);
			if (registration == null) {
				return false;
			}
			registrations.put(key, new Held(object, registration));
			if (held != null) {
				unregister(held.registration());
			}
			return true;
		}
	}

	private ServiceRegistration<?> register(EObject object, String registry, String objectId) {
		if (object instanceof ProviderMapping mapping) {
			if (mapping.getMid() == null || mapping.getMid().isBlank()) {
				logger.severe(String.format("ProviderMapping %s from atlas registry %s/%s has no mid - skipping",
						objectId, scopeService.getScopeName(), registry));
				return null;
			}
			List<EClass> unresolved = mapping.getProviderClasses().stream().filter(EObject::eIsProxy).toList();
			if (mapping.getProviderClasses().isEmpty() || !unresolved.isEmpty()) {
				logger.severe(String.format(
						"ProviderMapping %s (%s) from atlas registry %s/%s has missing or unresolved provider classes %s - is the sensor model available? Skipping",
						objectId, mapping.getMid(), scopeService.getScopeName(), registry, unresolved));
				return null;
			}
			Dictionary<String, Object> props = serviceProperties(registry, objectId);
			props.put(PROP_MID, mapping.getMid());
			return ctx.registerService(ProviderMapping.class, mapping, props);
		}
		if (object instanceof MappingProfile profile) {
			if (profile.getProfileId() == null || profile.getProfileId().isBlank()) {
				logger.severe(String.format("MappingProfile %s from atlas registry %s/%s has no profileId - skipping",
						objectId, scopeService.getScopeName(), registry));
				return null;
			}
			Dictionary<String, Object> props = serviceProperties(registry, objectId);
			props.put(PROP_PROFILE_ID, profile.getProfileId());
			return ctx.registerService(MappingProfile.class, profile, props);
		}
		logger.warning(String.format(
				"Object %s in atlas registry %s/%s is a %s - expected ProviderMapping or MappingProfile, skipping",
				objectId, scopeService.getScopeName(), registry, object.eClass().getName()));
		return null;
	}

	private Dictionary<String, Object> serviceProperties(String registry, String objectId) {
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(AtlasProperties.ATLAS_REMOTE, Boolean.TRUE);
		props.put(AtlasProperties.ATLAS_SCOPE, scopeService.getScopeName());
		props.put(AtlasProperties.ATLAS_REGISTRY, registry);
		props.put(PROP_OBJECT_ID, objectId);
		if (!stage().isEmpty()) {
			props.put(AtlasProperties.ATLAS_STAGE, stage());
		}
		return props;
	}

	private void unregister(ServiceRegistration<?> registration) {
		try {
			registration.unregister();
		} catch (IllegalStateException e) {
			// already unregistered
		}
	}

	private String stage() {
		return config.stage() == null ? "" : config.stage();
	}

	private List<String> objectIds() {
		return config.object_ids() == null ? List.of() : Arrays.asList(config.object_ids());
	}
}
