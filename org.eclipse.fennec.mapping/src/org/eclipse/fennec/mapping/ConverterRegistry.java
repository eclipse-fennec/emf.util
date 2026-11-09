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

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The converters a mapping can name, by id, and what happens when one cannot answer.
 * <p>
 * An <em>unknown</em> id is a configuration gap: the value passes through unconverted and the id
 * is logged once, so a typo does not silently empty a slot. A converter that throws or returns
 * {@code null} has no result to offer: the value is dropped.
 * <p>
 * Thread-safe. In OSGi the whiteboard registers and unregisters the converter services here; it is a
 * subclass so that it can be the registered service itself.
 */
public class ConverterRegistry {

	private static final Logger LOGGER = Logger.getLogger(ConverterRegistry.class.getName());

	private final Map<String, ValueConverter> converters = new ConcurrentHashMap<>();
	private final Set<String> reportedUnknown = ConcurrentHashMap.newKeySet();

	/**
	 * Registers a converter, replacing one registered under the same id.
	 *
	 * @param id the id a mapping names
	 * @param converter the converter
	 */
	public void register(String id, ValueConverter converter) {
		requireNonNull(id, "Converter id is required");
		requireNonNull(converter, "Converter is required");
		converters.put(id, converter);
		reportedUnknown.remove(id);
	}

	/**
	 * Removes a converter, if it is still the one registered under the id.
	 *
	 * @param id the id
	 * @param converter the converter to remove
	 */
	public void unregister(String id, ValueConverter converter) {
		requireNonNull(id, "Converter id is required");
		converters.remove(id, converter);
	}

	/**
	 * @param id the id
	 * @return whether a converter answers to the id
	 */
	public boolean contains(String id) {
		return id != null && converters.containsKey(id);
	}

	/**
	 * Converts a value with the converter the id names.
	 *
	 * @param id the id; blank means no conversion and returns the value unchanged
	 * @param value the value
	 * @return the value to use, or empty to drop it
	 */
	public Optional<Object> convert(String id, Object value) {
		if (value == null) {
			return Optional.empty();
		}
		if (id == null || id.isBlank()) {
			return Optional.of(value);
		}
		ValueConverter converter = converters.get(id);
		if (converter == null) {
			if (reportedUnknown.add(id)) {
				LOGGER.warning(() -> "No converter registered for id '" + id + "' - values pass unconverted");
			}
			return Optional.of(value);
		}
		try {
			return Optional.ofNullable(converter.convert(value));
		} catch (RuntimeException e) {
			LOGGER.log(Level.WARNING, e, () -> "Converter '" + id + "' failed on " + value + " - value dropped");
			return Optional.empty();
		}
	}
}
