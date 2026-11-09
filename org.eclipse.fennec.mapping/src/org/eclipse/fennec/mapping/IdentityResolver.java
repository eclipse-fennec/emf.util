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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.mapping.IdentityResolution;

/**
 * Answers which target object(s) a source object feeds, and caches the answer.
 * <p>
 * Three cases: an identity resolution without resolver (or none at all) means the source object's
 * id is the target id; a resolver that is a feature path or an OCL expression reads a key from the
 * source object; a resolver that is a function asks anything else - a table, a device registry.
 * A resolver that yields a collection feeds several targets (fan-out); an element that is an
 * {@link EObject} stands for its id.
 * <p>
 * Answers of cacheable resolutions are kept per (resolution, source id) until invalidated: when the
 * mapping changes, or explicitly when a device is moved. Thread-safe.
 */
public final class IdentityResolver {

	/** The id of an object as the mapping sees it: the EMF id attribute ({@link EcoreUtil#getID}). */
	public static final Function<EObject, String> EMF_ID = EcoreUtil::getID;

	private final ValueExtractor extractor;
	private final Function<EObject, String> objectIds;
	private final Map<IdentityResolution, Map<String, List<String>>> cache = new ConcurrentHashMap<>();

	/**
	 * @param extractor evaluates resolvers
	 * @param objectIds the id of an object; {@link #EMF_ID} by default
	 */
	public IdentityResolver(ValueExtractor extractor, Function<EObject, String> objectIds) {
		this.extractor = requireNonNull(extractor, "ValueExtractor is required");
		this.objectIds = requireNonNull(objectIds, "Object id function is required");
	}

	/**
	 * @param extractor evaluates resolvers; object ids are EMF ids
	 */
	public IdentityResolver(ValueExtractor extractor) {
		this(extractor, EMF_ID);
	}

	/**
	 * @param object an object
	 * @return its id, or {@code null} when it has none
	 */
	public String idOf(EObject object) {
		return objectIds.apply(object);
	}

	/**
	 * Resolves the target ids of a source object.
	 *
	 * @param resolution the identity resolution for the source's class, or {@code null} when the
	 *        mapping declares none (then the source id is the target id)
	 * @param source the source object
	 * @return the target ids; empty when the source has no id or the resolver yields nothing
	 */
	public List<String> resolve(IdentityResolution resolution, EObject source) {
		requireNonNull(source, "Source object is required");
		String sourceId = idOf(source);
		if (resolution == null || resolution.getResolver() == null) {
			return sourceId == null ? List.of() : List.of(sourceId);
		}
		if (!resolution.isCacheable() || sourceId == null) {
			return compute(resolution, source);
		}
		Map<String, List<String>> answers = cache.computeIfAbsent(resolution, r -> new ConcurrentHashMap<>());
		List<String> cached = answers.get(sourceId);
		if (cached != null) {
			return cached;
		}
		List<String> computed = compute(resolution, source);
		if (!computed.isEmpty()) {
			answers.put(sourceId, computed);
		}
		return computed;
	}

	/**
	 * Forgets every cached answer for one source object, e.g. after the device was moved.
	 *
	 * @param resolution the resolution
	 * @param sourceId the source id
	 */
	public void invalidate(IdentityResolution resolution, String sourceId) {
		Map<String, List<String>> answers = cache.get(resolution);
		if (answers != null) {
			answers.remove(sourceId);
		}
	}

	/**
	 * Forgets every cached answer of a resolution, e.g. after its mapping changed.
	 *
	 * @param resolution the resolution
	 */
	public void invalidate(IdentityResolution resolution) {
		cache.remove(resolution);
	}

	/** Forgets every cached answer. */
	public void invalidateAll() {
		cache.clear();
	}

	private List<String> compute(IdentityResolution resolution, EObject source) {
		return extractor.extract(resolution.getResolver(), source, null).map(this::toIds).orElse(List.of());
	}

	private List<String> toIds(Object value) {
		if (value instanceof Collection<?> collection) {
			List<String> ids = new ArrayList<>(collection.size());
			for (Object element : collection) {
				String id = toId(element);
				if (id != null) {
					ids.add(id);
				}
			}
			return List.copyOf(ids);
		}
		String id = toId(value);
		return id == null ? List.of() : List.of(id);
	}

	private String toId(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof EObject eObject) {
			return idOf(eObject);
		}
		String id = String.valueOf(value);
		return id.isBlank() ? null : id;
	}
}
