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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.protobuf.Descriptors.FieldDescriptor;

@DisplayName("Inheritance and polymorphism")
class InheritanceTest {

	private ZooModel model;
	private ProtobufSchema schema;

	@BeforeEach
	void setUp() {
		model = new ZooModel();
		schema = ProtobufSchema.forPackage(model.pkg);
	}

	private EObject roundTrip(EObject source) {
		byte[] bytes = schema.writer().toBytes(source);
		return schema.reader().fromBytes(bytes);
	}

	@Test
	@DisplayName("polymorphic containment uses the EObjectAny wrapper")
	void polymorphicFieldsUseWrapper() {
		FieldDescriptor animals = schema.descriptorFor(model.shelter).findFieldByName("animals");
		assertThat(animals.getType()).isEqualTo(FieldDescriptor.Type.MESSAGE);
		assertThat(animals.getMessageType().getName()).isEqualTo(ProtobufSchema.ANY_MESSAGE);
		assertThat(schema.toProtoSource()).contains("message EObjectAny");
	}

	@Test
	@DisplayName("round-trips a polymorphic containment list preserving concrete subtypes")
	void polymorphicContainmentMany() {
		EObject rex = model.create(model.dog);
		rex.eSet(model.animalName, "Rex");
		rex.eSet(model.dogBreed, "Labrador");
		EObject mimi = model.create(model.cat);
		mimi.eSet(model.animalName, "Mimi");
		mimi.eSet(model.catIndoor, true);

		EObject shelter = model.create(model.shelter);
		@SuppressWarnings("unchecked")
		List<EObject> animals = (List<EObject>) shelter.eGet(model.shelterAnimals);
		animals.add(rex);
		animals.add(mimi);

		EObject read = roundTrip(shelter);
		@SuppressWarnings("unchecked")
		List<EObject> readAnimals = (List<EObject>) read.eGet(model.shelterAnimals);

		assertThat(readAnimals).hasSize(2);
		assertThat(readAnimals.get(0).eClass()).isEqualTo(model.dog);
		assertThat(readAnimals.get(0).eGet(model.animalName)).isEqualTo("Rex");
		assertThat(readAnimals.get(0).eGet(model.dogBreed)).isEqualTo("Labrador");
		assertThat(readAnimals.get(1).eClass()).isEqualTo(model.cat);
		assertThat(readAnimals.get(1).eGet(model.animalName)).isEqualTo("Mimi");
		assertThat(readAnimals.get(1).eGet(model.catIndoor)).isEqualTo(true);
	}

	@Test
	@DisplayName("round-trips a multi-level subtype with inherited + own features")
	void multiLevelInheritance() {
		EObject bud = model.create(model.puppy);
		bud.eSet(model.animalName, "Bud");   // from Animal
		bud.eSet(model.dogBreed, "Beagle");  // from Dog
		bud.eSet(model.puppyWeeks, 6);       // own

		EObject shelter = model.create(model.shelter);
		shelter.eSet(model.shelterFavorite, bud);

		EObject read = roundTrip(shelter);
		EObject favorite = (EObject) read.eGet(model.shelterFavorite);

		assertThat(favorite.eClass()).isEqualTo(model.puppy);
		assertThat(favorite.eGet(model.animalName)).isEqualTo("Bud");
		assertThat(favorite.eGet(model.dogBreed)).isEqualTo("Beagle");
		assertThat(favorite.eGet(model.puppyWeeks)).isEqualTo(6);
	}

	@Test
	@DisplayName("round-trips a subtype at the root with inherited features")
	void subtypeAtRoot() {
		EObject dog = model.create(model.dog);
		dog.eSet(model.animalName, "Fido");
		dog.eSet(model.dogBreed, "Poodle");

		EObject read = roundTrip(dog);

		assertThat(read.eClass()).isEqualTo(model.dog);
		assertThat(read.eGet(model.animalName)).isEqualTo("Fido");
		assertThat(read.eGet(model.dogBreed)).isEqualTo("Poodle");
	}

	@Test
	@DisplayName("polymorphic non-containment reference keeps the concrete target type")
	void polymorphicNonContainment() {
		EObject volunteer = model.create(model.volunteer);
		volunteer.eSet(model.caretakerId, "V-1");
		EObject shelter = model.create(model.shelter);
		shelter.eSet(model.shelterOnDuty, volunteer);

		EObject read = roundTrip(shelter);
		EObject onDuty = (EObject) read.eGet(model.shelterOnDuty);

		// The proxy is typed as the actual subtype (Volunteer), not the abstract Caretaker.
		assertThat(onDuty.eClass()).isEqualTo(model.volunteer);
		assertThat(onDuty.eIsProxy()).isTrue();
		assertThat(((InternalEObject) onDuty).eProxyURI()).isNotNull();
	}
}
