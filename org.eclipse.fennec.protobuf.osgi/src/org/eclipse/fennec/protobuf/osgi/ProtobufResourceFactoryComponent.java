/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf.osgi;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.emf.osgi.annotation.ConfiguratorType;
import org.eclipse.fennec.emf.osgi.annotation.provide.EMFConfigurator;
import org.eclipse.fennec.protobuf.resource.ProtobufResource;
import org.eclipse.fennec.protobuf.resource.ProtobufResourceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * Contributes the {@link ProtobufResourceFactory} as an OSGi {@code Resource.Factory}
 * service. {@link EMFConfigurator} emits the {@code emf.fileExtension} /
 * {@code emf.contentType} service properties, so the Fennec
 * {@code DefaultResourceFactoryRegistryComponent} binds it into the
 * {@code Resource.Factory.Registry} automatically. Any {@code ResourceSet}
 * obtained from the Fennec runtime can then load/save {@code .protobin}
 * resources through the ordinary EMF API.
 */
@Component(name = "ProtobufResourceFactory", service = Resource.Factory.class)
@EMFConfigurator(
		configuratorName = "protobuf",
		configuratorType = ConfiguratorType.RESOURCE_FACTORY,
		fileExtension = { ProtobufResource.FILE_EXTENSION },
		contentType = { ProtobufResource.CONTENT_TYPE })
public class ProtobufResourceFactoryComponent extends ProtobufResourceFactory {
}
