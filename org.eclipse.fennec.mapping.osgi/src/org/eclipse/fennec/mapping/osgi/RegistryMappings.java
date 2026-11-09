/********************************************************************
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
 ********************************************************************/
package org.eclipse.fennec.mapping.osgi;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistry;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryEntry;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener;
import org.eclipse.fennec.emf.osgi.metadata.MetadataService;
import org.eclipse.fennec.emf.osgi.model.metadata.PackageMetadata;
import org.eclipse.fennec.mapping.InMemoryMappingRegistry;
import org.eclipse.fennec.mapping.MappingRegistry;
import org.eclipse.fennec.model.mapping.Mapping;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * The {@link MappingRegistry} of a framework, fed by an emf.osgi {@link EObjectRegistry}
 * (requirement MAP-12): every {@link Mapping} the registry holds - from files, from an atlas - is
 * validated and offered here under its id; one that does not validate is logged and left out.
 * <p>
 * The EObject registry is mandatory: it is published only once its initial load completed, so this
 * component never answers from a half-loaded registry. A registry entry carrying
 * {@code emf.fingerprint} reaches only the model version with that fingerprint (requirement MAP-13);
 * the metadata service, also mandatory, names the fingerprint of the mapping's target package. By default it is the registry named
 * {@value #DEFAULT_REGISTRY}; a configuration (PID {@value #PID}, optional) can name another through
 * {@code registry.target}. The mappings are read, never changed.
 */
@Component(name = RegistryMappings.PID, service = MappingRegistry.class)
@Designate(ocd = RegistryMappings.Config.class)
public class RegistryMappings implements MappingRegistry, EObjectRegistryListener {

	/** The configuration PID and component name. */
	public static final String PID = "FennecMappingRegistry";

	/** The name of the EObject registry used when no configuration says otherwise. */
	public static final String DEFAULT_REGISTRY = "fennec-mappings";

	static final String DEFAULT_TARGET = "(emf.eobject.registry.name=" + DEFAULT_REGISTRY + ")";

	private static final Logger LOGGER = Logger.getLogger(RegistryMappings.class.getName());

	/**
	 * Optional configuration.
	 */
	@ObjectClassDefinition(name = "Fennec mapping registry", description = "Mappings from an emf.osgi EObject registry")
	public @interface Config {

		/**
		 * @return the filter selecting the EObject registry
		 */
		@AttributeDefinition(description = "Filter selecting the EObject registry that holds the mappings")
		String registry_target() default DEFAULT_TARGET;
	}

	private final EObjectRegistry registry;
	private final Function<EPackage, String> fingerprints;
	private final InMemoryMappingRegistry mappings = new InMemoryMappingRegistry();
	private final Map<String, String> idsByKey = new ConcurrentHashMap<>();

	/**
	 * Starts listening; the registry replays its current content.
	 *
	 * @param registry the EObject registry holding the mappings
	 * @param metadata names the fingerprint of a model version
	 */
	@Activate
	public RegistryMappings(@Reference(name = "registry", target = DEFAULT_TARGET) EObjectRegistry registry,
			@Reference(name = "metadata") MetadataService metadata) {
		this(registry, (Function<EPackage, String>) ePackage -> metadata.getPackageMetadata(ePackage)
				.map(PackageMetadata::getModelFingerprint).orElse(null));
	}

	private RegistryMappings(EObjectRegistry registry, Function<EPackage, String> fingerprints) {
		this.registry = requireNonNull(registry, "EObjectRegistry is required");
		this.fingerprints = requireNonNull(fingerprints, "Fingerprint function is required");
		registry.addListener(this);
	}

	/**
	 * Plain-Java factory: the result listens from now on; {@link #deactivate()} stops it.
	 *
	 * @param registry the EObject registry holding the mappings
	 * @param fingerprints the fingerprint of a package, {@code null} when it is unknown
	 * @return the mapping registry
	 */
	public static RegistryMappings create(EObjectRegistry registry, Function<EPackage, String> fingerprints) {
		return new RegistryMappings(registry, fingerprints);
	}

	/**
	 * Stops listening.
	 */
	@Deactivate
	public void deactivate() {
		registry.removeListener(this);
	}

	@Override
	public void entryAdded(EObjectRegistryEntry entry) {
		if (!(entry.object() instanceof Mapping mapping)) {
			return;
		}
		if (mapping.getId() == null || mapping.getId().isBlank()) {
			LOGGER.warning(() -> "Mapping under key '" + entry.key() + "' of registry " + registry.getName()
					+ " has no id - left out");
			return;
		}
		Object bound = entry.properties().get(EMFNamespaces.EMF_MODEL_FINGERPRINT);
		if (bound != null && mapping.getTarget() != null) {
			String actual = fingerprints.apply(mapping.getTarget().getEPackage());
			if (!String.valueOf(bound).equals(actual)) {
				LOGGER.info(() -> "Mapping '" + mapping.getId() + "' is bound to model version " + bound + ", the live one is "
						+ actual + " - left out");
				return;
			}
		}
		try {
			mappings.register(mapping);
			idsByKey.put(entry.key(), mapping.getId());
		} catch (IllegalArgumentException e) {
			LOGGER.warning(() -> "Mapping under key '" + entry.key() + "' of registry " + registry.getName()
					+ " left out: " + e.getMessage());
		}
	}

	@Override
	public void entryUpdated(EObjectRegistryEntry entry, EObjectRegistryEntry oldEntry) {
		entryRemoved(oldEntry);
		entryAdded(entry);
	}

	@Override
	public void entryRemoved(EObjectRegistryEntry entry) {
		String id = idsByKey.remove(entry.key());
		if (id != null) {
			mappings.unregister(id);
		}
	}

	@Override
	public Optional<Mapping> get(String id) {
		return mappings.get(id);
	}

	@Override
	public Collection<Mapping> all() {
		return mappings.all();
	}

	@Override
	public List<Mapping> forTarget(EClass target) {
		return mappings.forTarget(target);
	}

	@Override
	public List<Mapping> forSource(EClass source) {
		return mappings.forSource(source);
	}

	@Override
	public void addListener(Listener listener) {
		mappings.addListener(listener);
	}

	@Override
	public void removeListener(Listener listener) {
		mappings.removeListener(listener);
	}
}
