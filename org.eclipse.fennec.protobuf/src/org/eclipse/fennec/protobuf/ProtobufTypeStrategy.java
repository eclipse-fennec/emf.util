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

/**
 * How the concrete {@link org.eclipse.emf.ecore.EClass EClass} of a polymorphic /
 * cross-package / {@code EObject}-typed object is written into the type-discriminated
 * wrapper. Mirrors the Fennec Codec {@code typeStrategy} vocabulary (payload-relevant
 * subset). With {@code smartCompression}, a target in the reference's own package is
 * written in the compact same-package form and cross-package targets fall back to the
 * fully-qualified URI.
 * <p>
 * The reader resolves any of these forms without needing to know which strategy the
 * writer used.
 */
public enum ProtobufTypeStrategy {

	/** Simple {@code EClass} name for same-package targets, full URI cross-package. Compact and safe. */
	NAME,

	/** Always the full EClass URI ({@code nsURI#//Name}); most portable, largest. */
	URI,

	/** Numeric {@code classifierID} (compact) — fragile across model evolution as IDs may shift. */
	NUMERIC;

	/** The default: {@link #NAME}. */
	public static final ProtobufTypeStrategy DEFAULT = NAME;

	public static ProtobufTypeStrategy from(String value, ProtobufTypeStrategy fallback) {
		if (value == null || value.isBlank()) {
			return fallback;
		}
		try {
			return valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ProtobufException("Unknown protobuf type strategy '" + value + "' (expected NAME, URI or NUMERIC)");
		}
	}
}
