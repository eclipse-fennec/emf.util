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
package org.eclipse.fennec.sensinact.mapping.impl;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryConstants;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener;
import org.eclipse.fennec.sensinact.mapping.MappingProfileRegistry;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.sensinact.core.command.GatewayThread;
import org.eclipse.sensinact.core.emf.command.AbstractSensinactEMFCommand;
import org.eclipse.sensinact.core.emf.model.SensinactEMFModelManager;
import org.eclipse.sensinact.core.emf.twin.SensinactEMFDigitalTwin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collects {@link ProviderMapping}s from the named EObject registry
 * {@code sensinact-mappings} (override via the {@code emf.eobject.registry.name}
 * component property): published as an {@link EObjectRegistryListener} whiteboard
 * service, the registry binds it and replays the current content, so late binding is
 * indistinguishable from early binding. Entries are validated here - uniformly for
 * every content source (files, model atlas, ...): non-ProviderMapping content, a blank
 * {@code mid} or missing/unresolved provider classes skip the entry with a log.
 *
 * @author Mark Hoffmann
 * @since 04.07.2025
 */
@Component(immediate = true, configurationPid = "sensinact.southbound.emf.mapping", configurationPolicy = ConfigurationPolicy.OPTIONAL, //
		property = EObjectRegistryConstants.EMF_EOBJECT_REGISTRY_NAME + "=sensinact-mappings")
public class ProviderMappingRegistryImpl implements ProviderMappingRegistry, EObjectRegistryListener {

	private static final Logger logger = LoggerFactory.getLogger(ProviderMappingRegistryImpl.class);

	private final Map<EClass, List<ProviderMapping>> registry = new ConcurrentHashMap<>();
	private ProviderModelSensinactMapper.Factory mapperFactory;
	@Reference
	private GatewayThread gatewayThread;
	@Reference
	private MappingProfileRegistry profileRegistry;

	@Activate
	public void activate() {
		mapperFactory = new ProviderModelSensinactMapper.Factory(profileRegistry);
	}

	@Deactivate
	public void dispose() {
		synchronized (registry) {
			registry.values().stream().flatMap(List::stream).collect(Collectors.toSet()).forEach(this::unregisterModelMapping);
			registry.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener#entryAdded(org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry)
	 */
	@Override
	public void entryAdded(EObjectRegistryEntry entry) {
		validMapping(entry, false).ifPresent(this::registerModelMapping);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener#entryUpdated(org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry, org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry)
	 */
	@Override
	public void entryUpdated(EObjectRegistryEntry entry, EObjectRegistryEntry oldEntry) {
		// the new mapping is registered before the old one is dropped, so lookups never
		// see a gap - the remove-after-add ordering of the former service whiteboard
		validMapping(entry, false).ifPresent(this::registerModelMapping);
		validMapping(oldEntry, true).ifPresent(this::unregisterModelMapping);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener#entryRemoved(org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry)
	 */
	@Override
	public void entryRemoved(EObjectRegistryEntry entry) {
		validMapping(entry, true).ifPresent(this::unregisterModelMapping);
	}

	/**
	 * The validation that guarded the former per-object service registration, now
	 * applied uniformly to every registry source. Quiet mode is used for old/removed
	 * entries: an entry that never passed validation was never registered, so its
	 * removal must neither log again nor reach the gateway.
	 */
	private Optional<ProviderMapping> validMapping(EObjectRegistryEntry entry, boolean quiet) {
		if (!(entry.object() instanceof ProviderMapping mapping)) {
			if (!quiet) {
				logger.warn("Registry entry '{}' is a {} - expected ProviderMapping, skipping", entry.key(),
						entry.object().eClass().getName());
			}
			return Optional.empty();
		}
		if (mapping.getMid() == null || mapping.getMid().isBlank()) {
			if (!quiet) {
				logger.error("ProviderMapping '{}' has no mid - skipping", entry.key());
			}
			return Optional.empty();
		}
		List<EClass> unresolved = mapping.getProviderClasses().stream().filter(EObject::eIsProxy).toList();
		if (mapping.getProviderClasses().isEmpty() || !unresolved.isEmpty()) {
			if (!quiet) {
				logger.error(
						"ProviderMapping '{}' ({}) has missing or unresolved provider classes {} - is the sensor model available? Skipping",
						entry.key(), mapping.getMid(), unresolved);
			}
			return Optional.empty();
		}
		return Optional.of(mapping);
	}

	/*
	 * (non-Javadoc)
	 * @see org.gecko.emf.sensinact.model.ProviderMappingRegistry#registerModelMapping(org.eclipse.sensinact.mapping.ProviderMapping)
	 */
	@Override
	public void registerModelMapping(ProviderMapping mapping) {
		mapping.getProviderClasses().forEach(ec->{
			logger.info("Registering provider mapping for '{}' into registry", mapping.getMid());
			registry.computeIfAbsent(ec, e->new ArrayList<>()).add(mapping);
		});
		Promise<Boolean> execute = gatewayThread.execute(new AbstractSensinactEMFCommand<Boolean>() {

			@Override
			protected Promise<Boolean> call(SensinactEMFDigitalTwin twin, SensinactEMFModelManager mmgr,
					PromiseFactory pf) {
				try {
					logger.debug("Mapping provider '{}' into sensinact", mapping.getMid());
					mapperFactory.createMapper(twin, mmgr).registerModelMapping(mapping);
					return pf.resolved(Boolean.TRUE);
				} catch (Throwable e) {
					logger.debug("Failed registering provider '{}' into sensinact, with error {}", mapping.getMid(), e.getMessage());
					return pf.failed(e);
				}
			}
		});
		try {
			if (nonNull(execute.getFailure())) {
				throw new IllegalStateException(String.format("Error registering provider '%s'", mapping.getMid()), execute.getFailure());
			}
		} catch (InterruptedException e) {
			throw new IllegalStateException(String.format("Error registering provider '%s' while waiting for promise to resolve", mapping.getMid()));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gecko.emf.sensinact.model.ProviderMappingRegistry#unregisterModelMapping(org.eclipse.sensinact.mapping.ProviderMapping)
	 */
	@Override
	public void unregisterModelMapping(ProviderMapping mapping) {
		mapping.getProviderClasses().forEach(ec->{
			logger.debug("Un-registering provider mapping for '{}' into registry", mapping.getMid());
			registry.getOrDefault(ec, Collections.emptyList()).remove(mapping);
		});
		gatewayThread.execute(new AbstractSensinactEMFCommand<Boolean>() {

			@Override
			protected Promise<Boolean> call(SensinactEMFDigitalTwin twin, SensinactEMFModelManager mmgr,
					PromiseFactory pf) {
				try {
					logger.debug("Un-registering provider '{}' from sensinact", mapping.getMid());
					mapperFactory.createMapper(twin, mmgr).unregisterModelMapping(mapping);
					return pf.resolved(Boolean.TRUE);
				} catch (Throwable e) {
					logger.debug("Failed un-registering provider '{}' from sensinact, with error {}", mapping.getMid(), e.getMessage());
					return pf.failed(e);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.gecko.emf.sensinact.model.ProviderMappingRegistry#getPoviderMapping(org.eclipse.emf.ecore.EClass)
	 */
	@Override
	public List<ProviderMapping> getProviderMapping(EClass eclass) {
		requireNonNull(eclass);
		return registry.getOrDefault(eclass, Collections.emptyList());
	}

}
