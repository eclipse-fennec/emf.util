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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.sensinact.model.mapping.DeletionRule;
import org.eclipse.fennec.sensinact.model.mapping.DurationUnit;
import org.eclipse.fennec.sensinact.model.mapping.MappingFactory;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry;
import org.eclipse.fennec.sensinact.model.mapping.ResourceMapping;
import org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies the persistence-rule model: durations (amount + {@link DurationUnit}) round-trip
 * through XMI with plain EMF serialization (no custom datatype conversion), and a shared rule
 * referenced by several resources resolves to the same instance.
 */
public class PersistenceRuleRoundTripTest {

	private static ResourceSet newResourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		rs.getPackageRegistry().put(MappingPackage.eNS_URI, MappingPackage.eINSTANCE);
		return rs;
	}

	@Test
	@DisplayName("Durations (amount + unit) round-trip through XMI")
	void durations_roundTrip() throws Exception {
		MappingFactory f = MappingFactory.eINSTANCE;

		PersistenceRuleRegistry registry = f.createPersistenceRuleRegistry();

		TimeThrottleChangeRule throttle = f.createTimeThrottleChangeRule();
		throttle.setId("throttle-5m");
		throttle.setInterval(5);
		throttle.setIntervalUnit(DurationUnit.MINUTES);
		registry.getChangeRules().add(throttle);

		DeletionRule deletion = f.createDeletionRule();
		deletion.setId("keep-90d");
		deletion.setRetention(90);
		deletion.setRetentionUnit(DurationUnit.DAYS);
		deletion.setCleanupInterval(1);
		deletion.setCleanupIntervalUnit(DurationUnit.DAYS);
		registry.getDeletionRules().add(deletion);

		// Save to XMI
		Resource saved = newResourceSet().createResource(URI.createURI("mem://rules.xmi"));
		saved.getContents().add(registry);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		saved.save(out, Map.of());
		String xmi = out.toString("UTF-8");

		assertTrue(xmi.contains("interval=\"5\""), () -> "expected interval amount in:\n" + xmi);
		assertTrue(xmi.contains("intervalUnit=\"MINUTES\""), () -> "expected interval unit in:\n" + xmi);
		assertTrue(xmi.contains("retention=\"90\""), () -> "expected retention amount in:\n" + xmi);

		// Load back into a fresh resource set
		Resource loaded = newResourceSet().createResource(URI.createURI("mem://rules.xmi"));
		loaded.load(new ByteArrayInputStream(out.toByteArray()), Map.of());
		PersistenceRuleRegistry reg2 = (PersistenceRuleRegistry) loaded.getContents().get(0);

		TimeThrottleChangeRule throttle2 = (TimeThrottleChangeRule) reg2.getChangeRules().get(0);
		DeletionRule deletion2 = (DeletionRule) reg2.getDeletionRules().get(0);

		assertEquals(Integer.valueOf(5), throttle2.getInterval());
		assertEquals(DurationUnit.MINUTES, throttle2.getIntervalUnit());
		assertEquals(Integer.valueOf(90), deletion2.getRetention());
		assertEquals(DurationUnit.DAYS, deletion2.getRetentionUnit());
		assertEquals(Integer.valueOf(1), deletion2.getCleanupInterval());
		assertEquals(DurationUnit.DAYS, deletion2.getCleanupIntervalUnit());
	}

	@Test
	@DisplayName("A shared rule is referenced (not copied) by multiple resources")
	void sharedRule_isReferencedByManyResources() {
		MappingFactory f = MappingFactory.eINSTANCE;

		PersistenceRuleRegistry registry = f.createPersistenceRuleRegistry();
		DeletionRule keep90 = f.createDeletionRule();
		keep90.setId("keep-90d");
		keep90.setRetention(90);
		keep90.setRetentionUnit(DurationUnit.DAYS);
		registry.getDeletionRules().add(keep90);

		ResourceMapping temperature = f.createResourceMapping();
		temperature.setMid("temperature");
		temperature.setDeletionRule(keep90);

		ResourceMapping humidity = f.createResourceMapping();
		humidity.setMid("humidity");
		humidity.setDeletionRule(keep90);

		// non-containment: both resources point at the same instance, still owned by the registry
		assertSame(keep90, temperature.getDeletionRule());
		assertSame(temperature.getDeletionRule(), humidity.getDeletionRule());
		assertSame(registry, keep90.eContainer());
	}
}
