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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The functions a mapping can name, by id, and what happens when one cannot answer.
 * <p>
 * An <em>unknown</em> id produces nothing: a function may be handed the whole source object, and
 * there is no identity answer for that. A function that throws or returns {@code null} produces
 * nothing as well. Either way the value source's constant then answers, if it has one.
 * <p>
 * Thread-safe. In OSGi the whiteboard registers and unregisters the function services here; it is a
 * subclass so that it can be the registered service itself.
 */
public class FunctionRegistry {

	private static final Logger LOGGER = Logger.getLogger(FunctionRegistry.class.getName());

	private final Map<String, ValueFunction> functions = new ConcurrentHashMap<>();

	/**
	 * Registers a function, replacing one registered under the same id.
	 *
	 * @param id the id a mapping names
	 * @param function the function
	 */
	public void register(String id, ValueFunction function) {
		requireNonNull(id, "Function id is required");
		requireNonNull(function, "Function is required");
		functions.put(id, function);
	}

	/**
	 * Removes a function, if it is still the one registered under the id.
	 *
	 * @param id the id
	 * @param function the function to remove
	 */
	public void unregister(String id, ValueFunction function) {
		requireNonNull(id, "Function id is required");
		functions.remove(id, function);
	}

	/**
	 * @param id the id
	 * @return whether a function answers to the id
	 */
	public boolean contains(String id) {
		return id != null && functions.containsKey(id);
	}

	/**
	 * Applies the function the id names.
	 *
	 * @param id the id a value source names; blank means no function and returns the input unchanged
	 * @param input the source object or the value a feature path produced
	 * @return the produced value, or empty
	 */
	public Optional<Object> apply(String id, Object input) {
		if (id == null || id.isBlank()) {
			return Optional.ofNullable(input);
		}
		ValueFunction function = functions.get(id);
		if (function == null) {
			LOGGER.fine(() -> "No function registered for id '" + id + "' - no value produced");
			return Optional.empty();
		}
		try {
			return Optional.ofNullable(function.apply(input));
		} catch (RuntimeException e) {
			LOGGER.log(Level.WARNING, e, () -> "Function '" + id + "' failed - no value produced");
			return Optional.empty();
		}
	}
}
