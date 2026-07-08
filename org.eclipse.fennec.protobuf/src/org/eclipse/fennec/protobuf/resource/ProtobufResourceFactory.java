/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * Creates {@link ProtobufResource}s. Register it with a {@code ResourceSet} to
 * use Protobuf through the ordinary EMF API:
 *
 * <pre>{@code
 * resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
 *     .put(ProtobufResource.FILE_EXTENSION, new ProtobufResourceFactory());
 * }</pre>
 *
 * The OSGi variant of this factory is contributed as a service by the
 * {@code org.eclipse.fennec.protobuf.osgi} bundle.
 */
public class ProtobufResourceFactory implements Resource.Factory {

	@Override
	public Resource createResource(URI uri) {
		return new ProtobufResource(uri);
	}
}
