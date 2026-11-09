/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.mapping.osgi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistries;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryWriter;
import org.eclipse.fennec.mapping.ValueConverter;
import org.eclipse.fennec.mapping.ValueFunction;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.MappingFactory;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;
import org.junit.jupiter.api.Test;

/**
 * The components without a framework: whiteboards and the registry-fed mapping registry
 * (requirements MAP-09, MAP-12).
 */
class OsgiComponentsTest {

	@Test
	void functionsComeAndGoByTheirIdProperty() {
		FunctionWhiteboard whiteboard = new FunctionWhiteboard();
		ValueFunction twice = input -> ((Double) input) * 2;
		whiteboard.addFunction(twice, Map.of(ValueFunction.FUNCTION_ID, "twice"));
		whiteboard.addFunction(input -> "lost", Map.of());

		assertThat(whiteboard.apply("twice", 2.0)).contains(4.0);

		whiteboard.removeFunction(twice, Map.of(ValueFunction.FUNCTION_ID, "twice"));
		assertThat(whiteboard.contains("twice")).isFalse();
	}

	@Test
	void aRemovedConverterOnlyGoesIfItIsStillTheOneRegistered() {
		ConverterWhiteboard whiteboard = new ConverterWhiteboard();
		ValueConverter first = value -> "first";
		ValueConverter second = value -> "second";
		whiteboard.addConverter(first, Map.of(ValueConverter.CONVERTER_ID, "c"));
		whiteboard.addConverter(second, Map.of(ValueConverter.CONVERTER_ID, "c"));
		whiteboard.removeConverter(first, Map.of(ValueConverter.CONVERTER_ID, "c"));

		assertThat(whiteboard.convert("c", 1)).contains("second");
	}

	@Test
	void theMappingRegistryFollowsTheEObjectRegistry() {
		EObjectRegistryWriter writer = EObjectRegistries.createRegistry(RegistryMappings.DEFAULT_REGISTRY);
		Model model = new Model();
		writer.put("files", "pool", model.mapping("pool-mapping"), Map.of());
		writer.put("files", "not-a-mapping", model.target, Map.of());

		RegistryMappings mappings = RegistryMappings.create(writer.getRegistry(), p -> "fp-live");
		List<String> events = new ArrayList<>();
		mappings.addListener((previous, current) -> events.add(current == null ? "-" + previous.getId() : "+" + current.getId()));

		assertThat(mappings.all()).as("the current content is replayed on creation").extracting(Mapping::getId)
				.containsExactly("pool-mapping");
		assertThat(mappings.forTarget(model.target)).hasSize(1);

		Mapping broken = model.mapping("broken");
		broken.getSlots().get(0).setTargetFeature(null);
		writer.put("files", "broken", broken, Map.of());
		assertThat(mappings.get("broken")).isEmpty();

		writer.remove("files", "pool");
		assertThat(mappings.all()).isEmpty();
		assertThat(events).containsExactly("-pool-mapping");

		writer.put("files", "old", model.mapping("old-version"), Map.of("emf.fingerprint", "fp-old"));
		writer.put("files", "live", model.mapping("live-version"), Map.of("emf.fingerprint", "fp-live"));
		assertThat(mappings.get("old-version")).as("bound to another model version").isEmpty();
		assertThat(mappings.get("live-version")).isPresent();
		writer.remove("files", "live");

		mappings.deactivate();
		writer.put("files", "pool", model.mapping("pool-mapping"), Map.of());
		assertThat(mappings.all()).as("no longer listening").isEmpty();
	}

	/** A one-class model to map onto. */
	private static final class Model {
		final EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		final EClass target = EcoreFactory.eINSTANCE.createEClass();
		final EAttribute value = EcoreFactory.eINSTANCE.createEAttribute();

		Model() {
			ePackage.setName("m");
			ePackage.setNsURI("https://example.org/m");
			target.setName("Pool");
			value.setName("value");
			value.setEType(EcorePackage.Literals.EDOUBLE);
			target.getEStructuralFeatures().add(value);
			ePackage.getEClassifiers().add(target);
		}

		Mapping mapping(String id) {
			Mapping mapping = MappingFactory.eINSTANCE.createMapping();
			mapping.setId(id);
			mapping.setTarget(target);
			Slot slot = MappingFactory.eINSTANCE.createSlot();
			slot.setTargetFeature(value);
			ValueSource source = MappingFactory.eINSTANCE.createValueSource();
			source.getFeaturePath().add(value);
			slot.setValue(source);
			mapping.getSlots().add(slot);
			return mapping;
		}
	}
}
