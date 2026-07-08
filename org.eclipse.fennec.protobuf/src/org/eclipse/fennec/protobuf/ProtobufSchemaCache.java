/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EPackage;

/**
 * Thread-safe cache of {@link ProtobufSchema}s keyed by {@link EPackage}.
 * <p>
 * Deriving the Protobuf descriptors for a package is deterministic but not free,
 * so callers that (de)serialize many objects — or share a {@code ResourceSet} —
 * should reuse one cache. Keys are compared by identity, and a schema is assumed
 * to remain valid for the lifetime of its package's structure; if a
 * (dynamic) package changes shape, {@link #invalidate(EPackage)} it.
 */
public final class ProtobufSchemaCache {

	private final Map<EPackage, ProtobufSchema> schemas = new ConcurrentHashMap<>();

	/** Returns the (memoized) schema for {@code ePackage}, deriving it on first use. */
	public ProtobufSchema get(EPackage ePackage) {
		if (ePackage == null) {
			throw new IllegalArgumentException("ePackage must not be null");
		}
		return schemas.computeIfAbsent(ePackage, ProtobufSchema::forPackage);
	}

	/** Drops the cached schema for a package (e.g. after the package changed shape). */
	public void invalidate(EPackage ePackage) {
		schemas.remove(ePackage);
	}

	/** Clears all cached schemas. */
	public void clear() {
		schemas.clear();
	}

	/** Number of packages currently cached (primarily for diagnostics/tests). */
	public int size() {
		return schemas.size();
	}
}
