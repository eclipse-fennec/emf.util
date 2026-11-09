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
package org.eclipse.fennec.mapping;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.Slot;

/**
 * Plain-Java {@link MappingRegistry}: mappings are put and removed by code. A mapping is
 * validated when it is registered; one with errors is refused. Registered mappings are not changed
 * (they may be owned by someone else, e.g. an EObject registry). Thread-safe.
 */
public final class InMemoryMappingRegistry implements MappingRegistry {

	private static final Logger LOGGER = Logger.getLogger(InMemoryMappingRegistry.class.getName());

	private final MappingValidator validator;
	private final Map<String, Mapping> mappings = new ConcurrentHashMap<>();
	private final List<Listener> listeners = new CopyOnWriteArrayList<>();

	/**
	 * @param validator checks every registered mapping
	 */
	public InMemoryMappingRegistry(MappingValidator validator) {
		this.validator = requireNonNull(validator, "MappingValidator is required");
	}

	/**
	 * A registry that validates without checking function and converter ids.
	 */
	public InMemoryMappingRegistry() {
		this(new MappingValidator());
	}

	/**
	 * Registers a mapping, replacing one with the same id.
	 *
	 * @param mapping the mapping; needs an id
	 * @return the validation result (warnings may be present)
	 * @throws IllegalArgumentException if the mapping has no id or does not validate
	 */
	public Diagnostic register(Mapping mapping) {
		requireNonNull(mapping, "Mapping is required");
		String id = mapping.getId();
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("A mapping needs an id to be registered");
		}
		Diagnostic diagnostic = validator.validate(mapping);
		if (diagnostic.getSeverity() >= Diagnostic.ERROR) {
			throw new IllegalArgumentException("Mapping '" + id + "' is invalid: " + messages(diagnostic));
		}
		Mapping previous = mappings.put(id, mapping);
		notifyListeners(previous, mapping);
		return diagnostic;
	}

	/**
	 * @param id the id of the mapping to remove
	 * @return the removed mapping
	 */
	public Optional<Mapping> unregister(String id) {
		Mapping removed = mappings.remove(id);
		if (removed != null) {
			notifyListeners(removed, null);
		}
		return Optional.ofNullable(removed);
	}

	@Override
	public Optional<Mapping> get(String id) {
		return Optional.ofNullable(id == null ? null : mappings.get(id));
	}

	@Override
	public Collection<Mapping> all() {
		return List.copyOf(mappings.values());
	}

	@Override
	public List<Mapping> forTarget(EClass target) {
		requireNonNull(target, "Target class is required");
		List<Mapping> result = new ArrayList<>();
		for (Mapping mapping : mappings.values()) {
			if (mapping.getTarget() == target) {
				result.add(mapping);
			}
		}
		return result;
	}

	@Override
	public List<Mapping> forSource(EClass source) {
		requireNonNull(source, "Source class is required");
		List<Mapping> result = new ArrayList<>();
		for (Mapping mapping : mappings.values()) {
			if (Boolean.TRUE.equals(mapping.getIncludeUnmapped()) && Mappings.applies(mapping.getTarget(), source)) {
				result.add(mapping);
				continue;
			}
			for (Slot slot : mapping.getSlots()) {
				if (Mappings.applies(Mappings.sourceClassOf(mapping, slot), source)) {
					result.add(mapping);
					break;
				}
			}
		}
		return result;
	}

	@Override
	public void addListener(Listener listener) {
		listeners.add(requireNonNull(listener, "Listener is required"));
	}

	@Override
	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	private void notifyListeners(Mapping previous, Mapping current) {
		for (Listener listener : listeners) {
			try {
				listener.changed(previous, current);
			} catch (RuntimeException e) {
				LOGGER.log(Level.WARNING, "Mapping registry listener failed", e);
			}
		}
	}

	private static String messages(Diagnostic diagnostic) {
		List<String> messages = new ArrayList<>();
		for (Diagnostic child : diagnostic.getChildren()) {
			if (child.getSeverity() >= Diagnostic.ERROR) {
				messages.add(child.getMessage());
			}
		}
		return String.join("; ", messages);
	}
}
