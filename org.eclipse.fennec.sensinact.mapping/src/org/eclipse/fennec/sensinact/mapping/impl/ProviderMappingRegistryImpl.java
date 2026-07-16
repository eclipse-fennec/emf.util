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
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping.impl;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EClass;
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
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Whiteboard that collects {@link ProviderMapping} services
 * @author Mark Hoffmann
 * @since 04.07.2025
 */
@Component(immediate = true, configurationPid = "sensinact.southbound.emf.mapping", configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ProviderMappingRegistryImpl implements ProviderMappingRegistry {

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
	 * @see org.gecko.emf.sensinact.model.ProviderMappingRegistry#registerModelMapping(org.eclipse.sensinact.mapping.ProviderMapping)
	 */
	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
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
