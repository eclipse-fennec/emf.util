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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * Creates {@link MofResource}s, so a resource set yields the imported packages of a DMTF MOF
 * file the same way {@code XSDResourceFactoryImpl} does for XML Schema:
 *
 * <pre>
 * resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("mof", new MofResourceFactory());
 * Resource r = resourceSet.getResource(URI.createFileURI("cim_schema_2.56.0.mof"), true);
 * </pre>
 */
public class MofResourceFactory extends ResourceFactoryImpl {

	/** The file extension of MOF documents. */
	public static final String EXTENSION = "mof";

	private final ImportOptions importOptions;

	public MofResourceFactory() {
		this(null);
	}

	/** A factory whose resources import with the given options ({@code null}: defaults). */
	public MofResourceFactory(ImportOptions importOptions) {
		this.importOptions = importOptions;
	}

	@Override
	public Resource createResource(URI uri) {
		return new MofResource(uri, importOptions);
	}
}
