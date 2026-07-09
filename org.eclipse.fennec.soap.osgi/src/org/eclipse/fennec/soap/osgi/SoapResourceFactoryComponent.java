/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap.osgi;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.emf.osgi.annotation.ConfiguratorType;
import org.eclipse.fennec.emf.osgi.annotation.provide.EMFConfigurator;
import org.eclipse.fennec.soap.resource.SoapResource;
import org.eclipse.fennec.soap.resource.SoapResourceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * Contributes the {@link SoapResourceFactory} as an OSGi {@code Resource.Factory}
 * service. {@link EMFConfigurator} emits the {@code emf.fileExtension} /
 * {@code emf.contentType} service properties, so the Fennec
 * {@code DefaultResourceFactoryRegistryComponent} binds it into the
 * {@code Resource.Factory.Registry} automatically. Any {@code ResourceSet} obtained from the
 * Fennec runtime can then load/save {@code .soap} resources through the ordinary EMF API.
 */
@Component(name = "SoapResourceFactory", service = Resource.Factory.class)
@EMFConfigurator(
		configuratorName = "soap",
		configuratorType = ConfiguratorType.RESOURCE_FACTORY,
		fileExtension = { SoapResource.FILE_EXTENSION },
		contentType = { SoapResource.CONTENT_TYPE })
public class SoapResourceFactoryComponent extends SoapResourceFactory {
}
