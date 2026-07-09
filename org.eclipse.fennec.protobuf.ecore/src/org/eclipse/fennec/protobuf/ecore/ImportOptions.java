/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf.ecore;

import java.util.function.Function;

/**
 * Configuration for {@link ProtobufImporter} — deriving Ecore from Protobuf
 * {@code FileDescriptor}s. Defaults suit interop / bootstrapping: an {@code nsURI}
 * synthesized from the proto package, {@code fieldNumber} annotations preserved (so a
 * later export stays wire-stable), and proto field names kept verbatim.
 */
public final class ImportOptions {

	private Function<String, String> nsUri = p -> "http://" + (p == null || p.isBlank() ? "model" : p);
	private boolean fieldNumberAnnotations = true;
	private boolean camelCaseNames;

	public static ImportOptions defaults() {
		return new ImportOptions();
	}

	/**
	 * Maps a proto {@code package} to the {@code EPackage} nsURI. Default
	 * {@code "http://" + package} (or {@code "http://model"} when the package is empty).
	 */
	public ImportOptions withNsUri(Function<String, String> nsUri) {
		if (nsUri != null) {
			this.nsUri = nsUri;
		}
		return this;
	}

	/** Whether to pin each field's number as a {@code fieldNumber} annotation (default {@code true}). */
	public ImportOptions withFieldNumberAnnotations(boolean fieldNumberAnnotations) {
		this.fieldNumberAnnotations = fieldNumberAnnotations;
		return this;
	}

	/** Whether to convert {@code snake_case} proto field names to {@code camelCase} (default {@code false}). */
	public ImportOptions withCamelCaseNames(boolean camelCaseNames) {
		this.camelCaseNames = camelCaseNames;
		return this;
	}

	Function<String, String> nsUri() {
		return nsUri;
	}

	boolean fieldNumberAnnotations() {
		return fieldNumberAnnotations;
	}

	boolean camelCaseNames() {
		return camelCaseNames;
	}
}
