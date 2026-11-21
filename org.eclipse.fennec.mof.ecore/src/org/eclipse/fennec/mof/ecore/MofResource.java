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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

/**
 * A read-only {@link org.eclipse.emf.ecore.resource.Resource} whose contents are the
 * {@link org.eclipse.emf.ecore.EPackage}s imported from a DMTF MOF file. {@code #pragma include}s
 * are resolved relative to the resource URI through the resource set's URI converter. Import
 * warnings are reported as {@link #getWarnings() warnings}.
 * <p>
 * Pass {@link #OPTION_IMPORT_OPTIONS} in the load options to override the
 * {@link ImportOptions} given to the {@link MofResourceFactory}.
 */
public class MofResource extends ResourceImpl {

	/** Load option: an {@link ImportOptions} instance. */
	public static final String OPTION_IMPORT_OPTIONS = "mof.importOptions";

	private final ImportOptions importOptions;

	public MofResource(URI uri) {
		this(uri, null);
	}

	public MofResource(URI uri, ImportOptions importOptions) {
		super(uri);
		this.importOptions = importOptions;
	}

	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		ImportOptions effective = importOptions;
		if (options != null && options.get(OPTION_IMPORT_OPTIONS) instanceof ImportOptions o) {
			effective = o;
		}
		MofImport result = MofImporter.importStream(inputStream, getURI(), effective, getURIConverter());
		getContents().addAll(result.packages());
		for (String warning : result.warnings()) {
			getWarnings().add(new Warning(warning));
		}
	}

	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException("MOF export is not supported; save the packages as .ecore instead");
	}

	private final class Warning implements Diagnostic {

		private final String message;

		Warning(String message) {
			this.message = message;
		}

		@Override
		public String getMessage() {
			return message;
		}

		@Override
		public String getLocation() {
			return getURI().toString();
		}

		@Override
		public int getLine() {
			return 0;
		}

		@Override
		public int getColumn() {
			return 0;
		}
	}
}
