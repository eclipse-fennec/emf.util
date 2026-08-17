/*
 * ******************************************************************
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
 * ******************************************************************
 */
package org.eclipse.fennec.sensinact.mapping.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistries;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryListener;
import org.eclipse.fennec.emf.osgi.eobject.registry.EObjectRegistryWriter;
import org.eclipse.fennec.sensinact.mapping.MappingProfileRegistry;
import org.eclipse.fennec.sensinact.mapping.ProviderMappingRegistry;
import org.eclipse.fennec.sensinact.model.mapping.MappingFactory;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.gecko.weather.model.weather.WeatherPackage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * The mapping registries as EObject-registry listener facades: entries pushed through a
 * plain-Java registry's writer reach {@link ProviderMappingRegistry} /
 * {@link MappingProfileRegistry} through the components' whiteboard listener face -
 * including the replay on late binding and the validation that used to guard the
 * per-object service registration of the retired atlas source bundle.
 */
@RequireEMF
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class MappingRegistryListenerTest {

	private static final String MAPPING_URI = "/data/WeatherProviderMapping.xmi";
	private static final String SOURCE = "test-source";

	@InjectService
	WeatherPackage weatherPackage;
	@InjectService
	ResourceSet resourceSet;
	@InjectService(filter = "(emf.eobject.registry.name=sensinact-mappings)")
	EObjectRegistryListener mappingListener;
	@InjectService
	ProviderMappingRegistry mappingRegistry;
	@InjectService(filter = "(emf.eobject.registry.name=sensinact-profiles)")
	EObjectRegistryListener profileListener;
	@InjectService
	MappingProfileRegistry profileRegistry;

	private EObjectRegistryWriter writer;
	private EObjectRegistryListener attachedListener;

	@AfterEach
	public void afterEach() {
		if (writer != null && attachedListener != null) {
			// drain the test source's entries through the listener, then detach
			writer.sync(SOURCE, List.of());
			writer.getRegistry().removeListener(attachedListener);
		}
	}

	private void attach(String registryName, EObjectRegistryListener listener) {
		writer = EObjectRegistries.createRegistry(registryName);
		attachedListener = listener;
		writer.getRegistry().addListener(listener);
	}

	private ProviderMapping loadWeatherMapping() throws IOException {
		URL providerUrl = getClass().getResource(MAPPING_URI);
		Resource providerResource = resourceSet.createResource(URI.createURI(providerUrl.toString()));
		providerResource.load(null);
		return (ProviderMapping) providerResource.getContents().get(0);
	}

	@Test
	@DisplayName("Mapping entries flow add/update/remove through the listener face")
	void mappingEntriesFlowThroughTheRegistry() throws IOException {
		ProviderMapping mapping = loadWeatherMapping();
		EClass providerClass = weatherPackage.getMOSMIXSWeatherReport();
		attach("sensinact-mappings", mappingListener);

		writer.put(SOURCE, mapping.getMid(), mapping, Map.of());
		assertEquals(List.of(mapping), mappingRegistry.getProviderMapping(providerClass));

		// a new instance under the same key replaces the old one - new indexed before old dropped
		ProviderMapping updated = EcoreUtil.copy(mapping);
		writer.put(SOURCE, mapping.getMid(), updated, Map.of());
		assertEquals(List.of(updated), mappingRegistry.getProviderMapping(providerClass));

		// definitively gone from the source
		writer.sync(SOURCE, List.of());
		assertTrue(mappingRegistry.getProviderMapping(providerClass).isEmpty());
	}

	@Test
	@DisplayName("Replay on late binding delivers pre-existing entries")
	void replayDeliversPreExistingMappings() throws IOException {
		ProviderMapping mapping = loadWeatherMapping();
		EClass providerClass = weatherPackage.getMOSMIXSWeatherReport();
		writer = EObjectRegistries.createRegistry("sensinact-mappings");
		writer.put(SOURCE, mapping.getMid(), mapping, Map.of());

		// the listener binds after the content is there - the replay must hand it over
		attachedListener = mappingListener;
		writer.getRegistry().addListener(mappingListener);
		assertEquals(List.of(mapping), mappingRegistry.getProviderMapping(providerClass));
	}

	@Test
	@DisplayName("Invalid mapping entries are skipped by the facade's validation")
	void invalidMappingEntriesAreSkipped() throws IOException {
		ProviderMapping valid = loadWeatherMapping();
		EClass providerClass = weatherPackage.getMOSMIXSWeatherReport();
		attach("sensinact-mappings", mappingListener);

		// wrong type
		writer.put(SOURCE, "wrong-type", EcoreFactory.eINSTANCE.createEObject(), Map.of());
		// no mid
		writer.put(SOURCE, "no-mid", MappingFactory.eINSTANCE.createProviderMapping(), Map.of());
		// mid, but no provider classes (the "sensor model missing" case)
		ProviderMapping noClasses = MappingFactory.eINSTANCE.createProviderMapping();
		noClasses.setMid("no-classes");
		writer.put(SOURCE, "no-classes", noClasses, Map.of());

		assertTrue(mappingRegistry.getProviderMapping(providerClass).isEmpty(),
				"none of the invalid entries may reach the mapping registry");

		// a valid entry still passes after the skipped ones
		writer.put(SOURCE, valid.getMid(), valid, Map.of());
		assertEquals(List.of(valid), mappingRegistry.getProviderMapping(providerClass));
	}

	@Test
	@DisplayName("Profile entries flow add/remove through the listener face")
	void profileEntriesFlowThroughTheRegistry() {
		MappingProfile profile = MappingFactory.eINSTANCE.createMappingProfile();
		profile.setProfileId("listener-test-profile");
		attach("sensinact-profiles", profileListener);

		writer.put(SOURCE, profile.getProfileId(), profile, Map.of());
		assertEquals(profile, profileRegistry.getProfile("listener-test-profile").orElseThrow());

		writer.remove(SOURCE, profile.getProfileId());
		assertTrue(profileRegistry.getProfile("listener-test-profile").isEmpty());
	}

	@Test
	@DisplayName("Profile entries without a profileId are skipped")
	void invalidProfileEntriesAreSkipped() {
		attach("sensinact-profiles", profileListener);
		int before = profileRegistry.getAllProfiles().size();

		writer.put(SOURCE, "no-id", MappingFactory.eINSTANCE.createMappingProfile(), Map.of());
		writer.put(SOURCE, "wrong-type", EcoreFactory.eINSTANCE.createEObject(), Map.of());

		assertEquals(before, profileRegistry.getAllProfiles().size(),
				"invalid entries may not reach the profile registry");
	}
}
