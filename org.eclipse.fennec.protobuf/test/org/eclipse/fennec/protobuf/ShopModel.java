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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * A small shop model built dynamically (no generated code) — the natural way to
 * exercise a descriptor-from-EPackage tool. Holds handles to every classifier
 * and feature so tests can create and inspect instances.
 */
final class ShopModel {

	static final String NS_URI = "http://example.org/shop";

	final EPackage pkg;
	final EFactory factory;
	final EEnum status;
	final EClass category;
	final EClass product;

	final EAttribute categoryName;   // fieldNumber 1
	final EReference categoryProducts; // fieldNumber 2, containment
	final EAttribute productName;
	final EAttribute productPrice;
	final EAttribute productCount;
	final EAttribute productCode;    // byte[]
	final EAttribute productWeight;  // BigDecimal
	final EAttribute productStatus;  // enum
	final EAttribute productTags;    // repeated string
	final EReference productCategory; // non-containment

	ShopModel() {
		EcoreFactory ef = EcoreFactory.eINSTANCE;
		EcorePackage ec = EcorePackage.eINSTANCE;

		pkg = ef.createEPackage();
		pkg.setName("shop");
		pkg.setNsURI(NS_URI);
		pkg.setNsPrefix("shop");

		status = ef.createEEnum();
		status.setName("Status");
		addLiteral(ef, status, "ACTIVE", 0);
		addLiteral(ef, status, "DISCONTINUED", 1);

		category = ef.createEClass();
		category.setName("Category");
		product = ef.createEClass();
		product.setName("Product");

		categoryName = attr(ef, "name", ec.getEString());
		pinFieldNumber(ef, categoryName, 1);
		categoryProducts = ref(ef, "products", product, true, true);
		pinFieldNumber(ef, categoryProducts, 2);
		category.getEStructuralFeatures().add(categoryName);
		category.getEStructuralFeatures().add(categoryProducts);

		productName = attr(ef, "name", ec.getEString());
		productPrice = attr(ef, "price", ec.getEDouble());
		productCount = attr(ef, "count", ec.getEInt());
		// unsettable: EMF tracks an explicit set-state, so "unset" and
		// "set to the default (0)" are distinguishable — which protobuf field
		// presence (proto3 optional) must preserve across a round-trip.
		productCount.setUnsettable(true);
		productCode = attr(ef, "code", ec.getEByteArray());
		productWeight = attr(ef, "weight", ec.getEBigDecimal());
		productStatus = attr(ef, "status", status);
		productTags = attr(ef, "tags", ec.getEString());
		productTags.setUpperBound(-1);
		productCategory = ref(ef, "category", category, false, false);
		product.getEStructuralFeatures().add(productName);
		product.getEStructuralFeatures().add(productPrice);
		product.getEStructuralFeatures().add(productCount);
		product.getEStructuralFeatures().add(productCode);
		product.getEStructuralFeatures().add(productWeight);
		product.getEStructuralFeatures().add(productStatus);
		product.getEStructuralFeatures().add(productTags);
		product.getEStructuralFeatures().add(productCategory);

		pkg.getEClassifiers().add(status);
		pkg.getEClassifiers().add(category);
		pkg.getEClassifiers().add(product);

		factory = pkg.getEFactoryInstance();
	}

	EObject newProduct() {
		return factory.create(product);
	}

	EObject newCategory() {
		return factory.create(category);
	}

	Object statusValue(String literal) {
		return factory.createFromString(status, literal);
	}

	private static EAttribute attr(EcoreFactory ef, String name, EClassifier type) {
		EAttribute a = ef.createEAttribute();
		a.setName(name);
		a.setEType(type);
		return a;
	}

	private static EReference ref(EcoreFactory ef, String name, EClass type, boolean containment, boolean many) {
		EReference r = ef.createEReference();
		r.setName(name);
		r.setEType(type);
		r.setContainment(containment);
		if (many) {
			r.setUpperBound(-1);
		}
		return r;
	}

	private static void pinFieldNumber(EcoreFactory ef, EStructuralFeature f, int number) {
		EAnnotation a = ef.createEAnnotation();
		a.setSource(ProtobufAnnotations.SOURCE);
		a.getDetails().put(ProtobufAnnotations.KEY_FIELD_NUMBER, String.valueOf(number));
		f.getEAnnotations().add(a);
	}

	private static void addLiteral(EcoreFactory ef, EEnum e, String name, int value) {
		EEnumLiteral l = ef.createEEnumLiteral();
		l.setName(name);
		l.setValue(value);
		e.getELiterals().add(l);
	}
}
