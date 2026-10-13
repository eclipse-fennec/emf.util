/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.mermaid;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * The dynamic Ecore model the documentation generators are rendered against.
 *
 * <p>
 * It is built in memory (no {@code .ecore} file, no registry) and deliberately carries one
 * instance of every construct the generators special-case: an abstract class, an interface, a
 * concrete class with super types, attributes, containment and cross-package references with the
 * three multiplicities that {@code extractMultiplicity} distinguishes, an {@code EEnum}, model
 * elements with and without a {@code documentation} annotation, and a long documentation text
 * (the PlantUML generator wraps anything over 30 characters).
 */
final class DocGenTestModel {

	/** Source of the annotation EMF itself uses for model documentation. */
	static final String GENMODEL_SOURCE = "http://www.eclipse.org/emf/2002/GenModel";

	static final String ZOO_NS_URI = "http://example.org/zoo";
	static final String STAFF_NS_URI = "http://example.org/staff";

	/** Longer than the 30 characters after which the PlantUML generator wraps a note. */
	static final String LONG_DOCUMENTATION = "An animal kept in the zoo, with a name, an age and a favourite food";

	private DocGenTestModel() {
	}

	/**
	 * @return the {@code zoo} package; its {@code Zoo} class references the {@code Keeper} class of
	 *         a second ({@code staff}) package, which is what the generators render as a reference
	 *         to another model
	 */
	static EPackage zoo() {
		EcoreFactory factory = EcoreFactory.eINSTANCE;

		EPackage staff = factory.createEPackage();
		staff.setName("staff");
		staff.setNsPrefix("staff");
		staff.setNsURI(STAFF_NS_URI);
		EClass keeper = factory.createEClass();
		keeper.setName("Keeper");
		staff.getEClassifiers().add(keeper);

		EPackage zoo = factory.createEPackage();
		zoo.setName("zoo");
		zoo.setNsPrefix("zoo");
		zoo.setNsURI(ZOO_NS_URI);

		EEnum food = factory.createEEnum();
		food.setName("Food");
		food.getELiterals().add(literal(factory, "MEAT", 0));
		food.getELiterals().add(literal(factory, "PLANTS", 1));
		document(factory, food, "What an animal eats.");

		EClass named = factory.createEClass();
		named.setName("Named");
		named.setAbstract(true);
		EAttribute name = attribute(factory, "name", EcorePackage.Literals.ESTRING, 0, 1);
		document(factory, name, "The display name.");
		named.getEStructuralFeatures().add(name);

		EClass feedable = factory.createEClass();
		feedable.setName("Feedable");
		feedable.setInterface(true);
		feedable.setAbstract(true);

		EClass toy = factory.createEClass();
		toy.setName("Toy");

		EClass animal = factory.createEClass();
		animal.setName("Animal");
		animal.getESuperTypes().add(named);
		animal.getESuperTypes().add(feedable);
		animal.getEStructuralFeatures().add(attribute(factory, "age", EcorePackage.Literals.EINT, 1, 1));
		animal.getEStructuralFeatures().add(attribute(factory, "food", food, 0, 1));
		// 0..* -> rendered as "*"
		animal.getEStructuralFeatures().add(reference(factory, "toys", toy, 0, -1, true));
		document(factory, animal, LONG_DOCUMENTATION);

		EClass zooClass = factory.createEClass();
		zooClass.setName("Zoo");
		// 1..* -> rendered as "1..*"
		zooClass.getEStructuralFeatures().add(reference(factory, "animals", animal, 1, -1, true));
		// 0..1 across packages -> rendered as a reference to another model
		zooClass.getEStructuralFeatures().add(reference(factory, "keeper", keeper, 0, 1, false));

		zoo.getEClassifiers().add(named);
		zoo.getEClassifiers().add(feedable);
		zoo.getEClassifiers().add(animal);
		zoo.getEClassifiers().add(toy);
		zoo.getEClassifiers().add(zooClass);
		zoo.getEClassifiers().add(food);
		return zoo;
	}

	/** @return a package holding a single bare class, without enums or documentation */
	static EPackage bare() {
		EcoreFactory factory = EcoreFactory.eINSTANCE;
		EPackage bare = factory.createEPackage();
		bare.setName("bare");
		bare.setNsPrefix("bare");
		bare.setNsURI("http://example.org/bare");
		EClass empty = factory.createEClass();
		empty.setName("Empty");
		bare.getEClassifiers().add(empty);
		return bare;
	}

	static EClassifier classifier(EPackage ePackage, String name) {
		EClassifier classifier = ePackage.getEClassifier(name);
		if (classifier == null) {
			throw new IllegalArgumentException("no classifier " + name + " in " + ePackage.getName());
		}
		return classifier;
	}

	static EClass eClass(EPackage ePackage, String name) {
		return (EClass) classifier(ePackage, name);
	}

	private static EAttribute attribute(EcoreFactory factory, String name, EClassifier type, int lower, int upper) {
		EAttribute attribute = factory.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		attribute.setLowerBound(lower);
		attribute.setUpperBound(upper);
		return attribute;
	}

	private static EReference reference(EcoreFactory factory, String name, EClass type, int lower, int upper,
			boolean containment) {
		EReference reference = factory.createEReference();
		reference.setName(name);
		reference.setEType(type);
		reference.setLowerBound(lower);
		reference.setUpperBound(upper);
		reference.setContainment(containment);
		return reference;
	}

	private static EEnumLiteral literal(EcoreFactory factory, String name, int value) {
		EEnumLiteral literal = factory.createEEnumLiteral();
		literal.setName(name);
		literal.setValue(value);
		return literal;
	}

	private static void document(EcoreFactory factory, EModelElement element, String documentation) {
		EAnnotation annotation = factory.createEAnnotation();
		annotation.setSource(GENMODEL_SOURCE);
		annotation.getDetails().put("documentation", documentation);
		element.getEAnnotations().add(annotation);
	}
}
