/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.protobuf.resource.ProtobufResource;
import org.eclipse.fennec.protobuf.resource.ProtobufResourceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Proves that the {@code org.eclipse.fennec.protobuf.osgi} bundle contributes the
 * Protobuf {@code Resource.Factory} as an OSGi service and that it is wired into a
 * Fennec {@link ResourceSet}, so {@code .protobin} resources load/save through the
 * ordinary EMF API inside a running framework.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Protobuf Resource.Factory OSGi registration")
@RequireEMF
public class ProtobufResourceIntegrationTest {

	@Test
	@DisplayName("factory is registered as a service, bound to the protobin extension")
	void factoryRegisteredAsService(
			@InjectService(filter = "(emf.fileExtension=protobin)", timeout = 5000) Resource.Factory factory) {
		assertNotNull(factory);
		assertInstanceOf(ProtobufResourceFactory.class, factory);
	}

	@Test
	@DisplayName("round-trips a .protobin resource through a Fennec ResourceSet")
	void roundTripThroughFennecResourceSet(@InjectService(timeout = 5000) ResourceSet resourceSet) throws Exception {
		EPackage pkg = buildModel();
		resourceSet.getPackageRegistry().put(pkg.getNsURI(), pkg);
		EClass itemClass = (EClass) pkg.getEClassifier("Item");
		EAttribute nameAttr = (EAttribute) itemClass.getEStructuralFeature("name");
		EFactory factory = pkg.getEFactoryInstance();

		// The Fennec ResourceSet must resolve our service-registered factory by extension.
		Resource out = resourceSet.createResource(URI.createURI("mem:/item.protobin"));
		assertInstanceOf(ProtobufResource.class, out);

		EObject item = factory.create(itemClass);
		item.eSet(nameAttr, "Widget");
		out.getContents().add(item);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		out.save(baos, null);

		Resource in = resourceSet.createResource(URI.createURI("mem:/item-2.protobin"));
		in.load(new ByteArrayInputStream(baos.toByteArray()), null);

		assertEquals(1, in.getContents().size());
		assertEquals("Widget", in.getContents().get(0).eGet(nameAttr));
	}

	private static EPackage buildModel() {
		EcoreFactory ef = EcoreFactory.eINSTANCE;
		EcorePackage ec = EcorePackage.eINSTANCE;

		EPackage pkg = ef.createEPackage();
		pkg.setName("it");
		pkg.setNsURI("http://example.org/it");
		pkg.setNsPrefix("it");

		EClass item = ef.createEClass();
		item.setName("Item");
		EAttribute name = ef.createEAttribute();
		name.setName("name");
		name.setEType(ec.getEString());
		EAttribute value = ef.createEAttribute();
		value.setName("value");
		value.setEType(ec.getEInt());
		item.getEStructuralFeatures().add(name);
		item.getEStructuralFeatures().add(value);
		pkg.getEClassifiers().add(item);

		return pkg;
	}
}
