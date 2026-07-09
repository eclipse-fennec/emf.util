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

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cross-package / subpackage / EObject containment")
class CrossPackageTest {

	private static EAttribute attr(String name, EClassifier type) {
		EAttribute a = EcoreFactory.eINSTANCE.createEAttribute();
		a.setName(name);
		a.setEType(type);
		return a;
	}

	private static EClass clazz(EPackage pkg, String name) {
		EClass c = EcoreFactory.eINSTANCE.createEClass();
		c.setName(name);
		pkg.getEClassifiers().add(c);
		return c;
	}

	private static EPackage pkg(String name, String nsURI) {
		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setName(name);
		p.setNsURI(nsURI);
		p.setNsPrefix(name);
		return p;
	}

	private static EReference containment(String name, EClass type) {
		EReference r = EcoreFactory.eINSTANCE.createEReference();
		r.setName(name);
		r.setEType(type);
		r.setContainment(true);
		return r;
	}

	private static EObject roundTrip(EObject source, ProtobufContext ctx) {
		ProtobufSchema schema = ProtobufSchema.forPackage(source.eClass().getEPackage());
		byte[] bytes = schema.writer(ctx).toBytes(source);
		return schema.reader(ctx).fromBytes(bytes);
	}

	@Test
	@DisplayName("round-trips a containment reference into another package")
	void crossPackageContainment() {
		EPackage inner = pkg("inner", "http://example.org/inner");
		EClass item = clazz(inner, "Item");
		EAttribute label = attr("label", EcorePackage.eINSTANCE.getEString());
		item.getEStructuralFeatures().add(label);

		EPackage outer = pkg("outer", "http://example.org/outer");
		EClass container = clazz(outer, "Container");
		EReference itemRef = containment("item", item);
		container.getEStructuralFeatures().add(itemRef);

		EObject theItem = inner.getEFactoryInstance().create(item);
		theItem.eSet(label, "hello");
		EObject theContainer = outer.getEFactoryInstance().create(container);
		theContainer.eSet(itemRef, theItem);

		EObject read = roundTrip(theContainer, ProtobufContext.defaults());
		EObject readItem = (EObject) read.eGet(itemRef);

		assertThat(readItem.eClass()).isEqualTo(item);
		assertThat(readItem.eGet(label)).isEqualTo("hello");
	}

	@Test
	@DisplayName("round-trips a containment reference into a subpackage")
	void subpackageContainment() {
		EPackage root = pkg("root", "http://example.org/root");
		EPackage sub = pkg("sub", "http://example.org/root/sub");
		root.getESubpackages().add(sub);

		EClass part = clazz(sub, "Part");
		EAttribute text = attr("text", EcorePackage.eINSTANCE.getEString());
		part.getEStructuralFeatures().add(text);

		EClass doc = clazz(root, "Doc");
		EReference partRef = containment("part", part);
		doc.getEStructuralFeatures().add(partRef);

		EObject thePart = sub.getEFactoryInstance().create(part);
		thePart.eSet(text, "body");
		EObject theDoc = root.getEFactoryInstance().create(doc);
		theDoc.eSet(partRef, thePart);

		EObject read = roundTrip(theDoc, ProtobufContext.defaults());
		EObject readPart = (EObject) read.eGet(partRef);

		assertThat(readPart.eClass()).isEqualTo(part);
		assertThat(readPart.eGet(text)).isEqualTo("body");
	}

	@Test
	@DisplayName("round-trips an EObject-typed containment reference")
	void eObjectContainment() {
		EPackage pkg = pkg("box", "http://example.org/box");
		EClass payload = clazz(pkg, "Payload");
		EAttribute data = attr("data", EcorePackage.eINSTANCE.getEString());
		payload.getEStructuralFeatures().add(data);

		EClass holder = clazz(pkg, "Holder");
		EReference content = containment("content", EcorePackage.eINSTANCE.getEObject());
		holder.getEStructuralFeatures().add(content);

		// EObject-typed refs carry the full type URI -> the package must be resolvable.
		EPackageRegistryImpl registry = new EPackageRegistryImpl();
		registry.put(pkg.getNsURI(), pkg);
		ProtobufContext ctx = ProtobufContext.defaults().withPackageRegistry(registry);

		EObject thePayload = pkg.getEFactoryInstance().create(payload);
		thePayload.eSet(data, "payload!");
		EObject theHolder = pkg.getEFactoryInstance().create(holder);
		theHolder.eSet(content, thePayload);

		EObject read = roundTrip(theHolder, ctx);
		EObject readContent = (EObject) read.eGet(content);

		assertThat(readContent.eClass()).isEqualTo(payload);
		assertThat(readContent.eGet(data)).isEqualTo("payload!");
	}
}
