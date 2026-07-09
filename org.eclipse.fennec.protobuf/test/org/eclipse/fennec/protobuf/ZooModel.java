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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * A model exercising inheritance constellations: an abstract supertype with two
 * concrete subtypes, a multi-level subtype, polymorphic containment (single and
 * many), and a polymorphic non-containment reference.
 *
 * <pre>
 * Animal (abstract)      name : EString
 *   Dog extends Animal   breed : EString
 *     Puppy extends Dog  weeksOld : EInt
 *   Cat extends Animal   indoor : EBoolean
 * Caretaker (abstract)   id : EString
 *   Volunteer            hours : EInt
 * Shelter                animals : Animal [0..*]  (containment, polymorphic)
 *                        favorite : Animal [0..1] (containment, polymorphic)
 *                        onDuty : Caretaker [0..1] (non-containment, polymorphic)
 * </pre>
 */
final class ZooModel {

	static final String NS_URI = "http://example.org/zoo";

	final EPackage pkg;
	final EClass animal;
	final EClass dog;
	final EClass puppy;
	final EClass cat;
	final EClass caretaker;
	final EClass volunteer;
	final EClass shelter;

	final EAttribute animalName;
	final EAttribute dogBreed;
	final EAttribute puppyWeeks;
	final EAttribute catIndoor;
	final EAttribute caretakerId;
	final EAttribute volunteerHours;
	final EReference shelterAnimals;
	final EReference shelterFavorite;
	final EReference shelterOnDuty;

	ZooModel() {
		EcoreFactory ef = EcoreFactory.eINSTANCE;
		EcorePackage ec = EcorePackage.eINSTANCE;

		pkg = ef.createEPackage();
		pkg.setName("zoo");
		pkg.setNsURI(NS_URI);
		pkg.setNsPrefix("zoo");

		animal = clazz(ef, "Animal", true);
		animalName = attr(ef, "name", ec.getEString());
		animal.getEStructuralFeatures().add(animalName);

		dog = clazz(ef, "Dog", false);
		dog.getESuperTypes().add(animal);
		dogBreed = attr(ef, "breed", ec.getEString());
		dog.getEStructuralFeatures().add(dogBreed);

		puppy = clazz(ef, "Puppy", false);
		puppy.getESuperTypes().add(dog);
		puppyWeeks = attr(ef, "weeksOld", ec.getEInt());
		puppy.getEStructuralFeatures().add(puppyWeeks);

		cat = clazz(ef, "Cat", false);
		cat.getESuperTypes().add(animal);
		catIndoor = attr(ef, "indoor", ec.getEBoolean());
		cat.getEStructuralFeatures().add(catIndoor);

		caretaker = clazz(ef, "Caretaker", true);
		caretakerId = attr(ef, "id", ec.getEString());
		caretaker.getEStructuralFeatures().add(caretakerId);

		volunteer = clazz(ef, "Volunteer", false);
		volunteer.getESuperTypes().add(caretaker);
		volunteerHours = attr(ef, "hours", ec.getEInt());
		volunteer.getEStructuralFeatures().add(volunteerHours);

		shelter = clazz(ef, "Shelter", false);
		shelterAnimals = ref(ef, "animals", animal, true, true);
		shelterFavorite = ref(ef, "favorite", animal, true, false);
		shelterOnDuty = ref(ef, "onDuty", caretaker, false, false);
		shelter.getEStructuralFeatures().add(shelterAnimals);
		shelter.getEStructuralFeatures().add(shelterFavorite);
		shelter.getEStructuralFeatures().add(shelterOnDuty);

		pkg.getEClassifiers().add(animal);
		pkg.getEClassifiers().add(dog);
		pkg.getEClassifiers().add(puppy);
		pkg.getEClassifiers().add(cat);
		pkg.getEClassifiers().add(caretaker);
		pkg.getEClassifiers().add(volunteer);
		pkg.getEClassifiers().add(shelter);
	}

	EObject create(EClass c) {
		return pkg.getEFactoryInstance().create(c);
	}

	private static EClass clazz(EcoreFactory ef, String name, boolean abstrakt) {
		EClass c = ef.createEClass();
		c.setName(name);
		c.setAbstract(abstrakt);
		return c;
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
}
