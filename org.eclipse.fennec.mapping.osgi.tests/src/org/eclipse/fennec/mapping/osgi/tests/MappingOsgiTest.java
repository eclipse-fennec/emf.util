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
package org.eclipse.fennec.mapping.osgi.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.mapping.ExpressionEvaluator;
import org.eclipse.fennec.mapping.FunctionRegistry;
import org.eclipse.fennec.mapping.MappingRegistry;
import org.eclipse.fennec.mapping.ValueFunction;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.MappingFactory;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;

/**
 * The mapping in a framework (requirements MAP-09, MAP-12): function whiteboard, the mapping registry
 * on an EObject registry configured through Configuration Admin, the OCL evaluator service.
 */
class MappingOsgiTest {

	@Test
	void aFunctionServiceIsAvailableWhileItIsRegistered(@InjectBundleContext BundleContext context,
			@InjectService(timeout = 5000) FunctionRegistry functions) {
		ServiceRegistration<ValueFunction> registration = context.registerService(ValueFunction.class,
				input -> "hello " + input, dictionary(ValueFunction.FUNCTION_ID, "greet"));

		assertThat(functions.apply("greet", "pool")).contains("hello pool");

		registration.unregister();
		assertThat(functions.contains("greet")).isFalse();
	}

	@Test
	void theOclEvaluatorIsAService(@InjectService(timeout = 5000) ExpressionEvaluator evaluator) {
		EObject self = EcoreFactory.eINSTANCE.createEClass();
		((EClass) self).setName("Pool");

		assertThat(evaluator.evaluate("self.name.size()", self)).contains(4);
	}

	@Test
	void mappingsComeFromTheConfiguredEObjectRegistry(@InjectService(timeout = 5000) ConfigurationAdmin configAdmin,
			@InjectService(timeout = 5000) ResourceSetFactory resourceSets,
			@InjectService(cardinality = 0) ServiceAware<MappingRegistry> registries, @TempDir Path dir)
			throws Exception {
		Path file = dir.resolve("names.xmi");
		save(resourceSets.createResourceSet(), file, mapping("ecore-names"));

		Configuration provider = configAdmin.getFactoryConfiguration("FileEObjectProvider", "mappings", "?");
		provider.update(dictionary("emf.eobject.provider.name", "mapping-files", "locations",
				new String[] { file.toString() }, "key.feature", "id"));
		Configuration registry = configAdmin.getFactoryConfiguration("EObjectRegistry", "mappings", "?");
		registry.update(dictionary("name", "fennec-mappings", "initialProvider.target",
				"(emf.eobject.provider.name=mapping-files)"));
		try {
			MappingRegistry mappings = registries.waitForService(5000);
			assertThat(mappings).as("the mapping registry appears once the EObject registry is loaded").isNotNull();
			Optional<Mapping> mapping = mappings.get("ecore-names");
			assertThat(mapping).isPresent();
			assertThat(mapping.get().getTarget()).isSameAs(EcorePackage.Literals.ENAMED_ELEMENT);
		} finally {
			registry.delete();
			provider.delete();
		}
	}

	private static Mapping mapping(String id) {
		Mapping mapping = MappingFactory.eINSTANCE.createMapping();
		mapping.setId(id);
		mapping.setTarget(EcorePackage.Literals.ENAMED_ELEMENT);
		Slot slot = MappingFactory.eINSTANCE.createSlot();
		slot.setTargetFeature(EcorePackage.Literals.ENAMED_ELEMENT__NAME);
		ValueSource value = MappingFactory.eINSTANCE.createValueSource();
		value.getFeaturePath().add(EcorePackage.Literals.ENAMED_ELEMENT__NAME);
		slot.setValue(value);
		mapping.getSlots().add(slot);
		return mapping;
	}

	private static void save(ResourceSet resourceSet, Path file, EObject content) throws IOException {
		Files.createDirectories(file.getParent());
		Resource resource = resourceSet.createResource(URI.createFileURI(file.toString()));
		resource.getContents().add(content);
		resource.save(Map.of());
	}

	private static Dictionary<String, Object> dictionary(Object... keyValues) {
		Dictionary<String, Object> dictionary = new Hashtable<>();
		for (int i = 0; i < keyValues.length; i += 2) {
			dictionary.put((String) keyValues[i], keyValues[i + 1]);
		}
		return dictionary;
	}
}
