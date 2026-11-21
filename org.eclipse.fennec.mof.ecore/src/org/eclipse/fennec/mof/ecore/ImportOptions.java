/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

/**
 * Configuration for {@link MofImporter}. The defaults put every class into an {@code EPackage}
 * named after its <em>schema</em> — the part of the class name before the first underscore
 * ({@code CIM_EthernetPort} → {@code CIM}, {@code Win32_Process} → {@code Win32}) — and fail
 * on any superclass or {@code REF} target that the imported MOF does not declare.
 */
public final class ImportOptions {

	private Function<String, String> packageName = ImportOptions::schemaOf;
	private Function<String, String> nsUri = p -> "urn:mof:" + p;
	private Function<String, String> nsPrefix = p -> p.toLowerCase(Locale.ROOT);
	private Set<String> classes;
	private boolean strict = true;

	public static ImportOptions defaults() {
		return new ImportOptions();
	}

	/**
	 * The schema of a CIM class name per DSP0004: the part before the first underscore. Names
	 * without one, or starting with it (WMI system classes such as {@code __SystemClass}), get
	 * the schema {@code Default}.
	 */
	public static String schemaOf(String className) {
		int i = className.indexOf('_');
		return i > 0 ? className.substring(0, i) : "Default";
	}

	/**
	 * Maps a CIM class name to the name of the {@code EPackage} it goes into. Default
	 * {@link #schemaOf(String)}; return a constant to import everything into one package.
	 */
	public ImportOptions withPackageName(Function<String, String> packageName) {
		if (packageName != null) {
			this.packageName = packageName;
		}
		return this;
	}

	/** Maps a package name to its nsURI. Default {@code "urn:mof:" + name}. */
	public ImportOptions withNsUri(Function<String, String> nsUri) {
		if (nsUri != null) {
			this.nsUri = nsUri;
		}
		return this;
	}

	/** Maps a package name to its nsPrefix. Default: the name in lower case. */
	public ImportOptions withNsPrefix(Function<String, String> nsPrefix) {
		if (nsPrefix != null) {
			this.nsPrefix = nsPrefix;
		}
		return this;
	}

	/**
	 * Imports only these classes (case-insensitive CIM names) plus their superclasses and,
	 * transitively, every class their {@code REF} properties, {@code REF} parameters and
	 * {@code REF} return types point to. {@code null} or empty imports all classes (default).
	 */
	public ImportOptions withClasses(Collection<String> classNames) {
		if (classNames == null || classNames.isEmpty()) {
			this.classes = null;
		} else {
			this.classes = new LinkedHashSet<>();
			for (String c : classNames) {
				this.classes.add(c.toLowerCase(Locale.ROOT));
			}
		}
		return this;
	}

	/**
	 * Whether an undeclared superclass or {@code REF} target fails the import (default
	 * {@code true}). When lenient, the class is imported without that supertype (annotation
	 * {@code unresolvedSuperclass}) and the reference is typed {@code EObject} (annotation
	 * {@code unresolvedType}); both are reported in {@link MofImport#warnings()}.
	 */
	public ImportOptions withStrict(boolean strict) {
		this.strict = strict;
		return this;
	}

	Function<String, String> packageName() {
		return packageName;
	}

	Function<String, String> nsUri() {
		return nsUri;
	}

	Function<String, String> nsPrefix() {
		return nsPrefix;
	}

	Set<String> classes() {
		return classes;
	}

	boolean strict() {
		return strict;
	}
}
