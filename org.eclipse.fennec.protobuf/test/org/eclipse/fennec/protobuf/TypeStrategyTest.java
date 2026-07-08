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

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Type discriminator strategies")
class TypeStrategyTest {

	private ZooModel model;
	private ProtobufSchema schema;
	private ProtobufContext readCtx;

	@BeforeEach
	void setUp() {
		model = new ZooModel();
		schema = ProtobufSchema.forPackage(model.pkg);
		EPackageRegistryImpl registry = new EPackageRegistryImpl();
		registry.put(ZooModel.NS_URI, model.pkg);
		// The reader resolves any strategy's discriminator; a registry covers the URI form.
		readCtx = ProtobufContext.defaults().withPackageRegistry(registry);
	}

	private EObject shelterWithDog() {
		EObject rex = model.create(model.dog);
		rex.eSet(model.animalName, "Rex");
		rex.eSet(model.dogBreed, "Labrador");
		EObject shelter = model.create(model.shelter);
		@SuppressWarnings("unchecked")
		List<EObject> animals = (List<EObject>) shelter.eGet(model.shelterAnimals);
		animals.add(rex);
		return shelter;
	}

	private void assertRoundTrips(byte[] bytes) {
		EObject read = schema.reader(readCtx).fromBytes(bytes, model.shelter);
		@SuppressWarnings("unchecked")
		List<EObject> animals = (List<EObject>) read.eGet(model.shelterAnimals);
		assertThat(animals).hasSize(1);
		assertThat(animals.get(0).eClass()).isEqualTo(model.dog);
		assertThat(animals.get(0).eGet(model.animalName)).isEqualTo("Rex");
		assertThat(animals.get(0).eGet(model.dogBreed)).isEqualTo("Labrador");
	}

	@Test
	@DisplayName("NAME, URI and NUMERIC all round-trip (reader is strategy-agnostic)")
	void allStrategiesRoundTrip() {
		EObject shelter = shelterWithDog();
		for (ProtobufTypeStrategy strategy : ProtobufTypeStrategy.values()) {
			byte[] bytes = schema.writer(ProtobufContext.defaults().withTypeStrategy(strategy)).toBytes(shelter);
			assertRoundTrips(bytes);
		}
	}

	@Test
	@DisplayName("NAME + smartCompression produces a smaller payload than full URI")
	void nameIsMoreCompactThanUri() {
		EObject shelter = shelterWithDog();
		byte[] name = schema.writer(ProtobufContext.defaults()
				.withTypeStrategy(ProtobufTypeStrategy.NAME)).toBytes(shelter);
		byte[] uri = schema.writer(ProtobufContext.defaults()
				.withTypeStrategy(ProtobufTypeStrategy.URI).withSmartCompression(false)).toBytes(shelter);

		assertThat(name.length).isLessThan(uri.length);
	}

	@Test
	@DisplayName("honours a package-level typeStrategy annotation as the default")
	void packageAnnotationDefault() {
		EAnnotation a = EcoreFactory.eINSTANCE.createEAnnotation();
		a.setSource(ProtobufAnnotations.SOURCE);
		a.getDetails().put(ProtobufAnnotations.KEY_TYPE_STRATEGY, "URI");
		a.getDetails().put(ProtobufAnnotations.KEY_SMART_COMPRESSION, "false");
		model.pkg.getEAnnotations().add(a);

		ProtobufSchema annotated = ProtobufSchema.forPackage(model.pkg);
		EObject shelter = shelterWithDog();

		byte[] byAnnotation = annotated.writer().toBytes(shelter); // default from annotation -> URI
		byte[] byName = annotated.writer(ProtobufContext.defaults()
				.withTypeStrategy(ProtobufTypeStrategy.NAME)).toBytes(shelter); // explicit override

		assertThat(byAnnotation.length).isGreaterThan(byName.length);
	}
}
