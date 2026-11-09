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
package org.eclipse.fennec.mapping;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.model.mapping.IdentityResolution;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.MappingFactory;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;

/**
 * The reference scenario of emf.streaming as a dynamic model:
 * <ul>
 * <li>{@code Pool} (id, name, currentTemperature: EDouble, currentPh: EDouble, depth: EInt)</li>
 * <li>{@code TempSensor} (id, poolId, value: EDouble) - finds its pool by a key</li>
 * <li>{@code PhSensor} (id, reading: EDouble) - finds its pool through a function</li>
 * <li>{@code Station} (id, readings: Reading[*] containment) and {@code Reading} (kind: Kind, value: EDouble)
 * - a collection to select from</li>
 * </ul>
 */
final class PoolFixture {

	static final String NS_URI = "https://example.org/pool";

	final EPackage ePackage;
	final EClass pool;
	final EAttribute poolId;
	final EAttribute poolName;
	final EAttribute currentTemperature;
	final EAttribute currentPh;
	final EAttribute depth;
	final EClass tempSensor;
	final EAttribute tempSensorId;
	final EAttribute tempSensorPoolId;
	final EAttribute tempSensorValue;
	final EClass phSensor;
	final EAttribute phSensorId;
	final EAttribute phSensorReading;
	final EClass station;
	final EAttribute stationId;
	final EReference stationReadings;
	final EClass reading;
	final EAttribute readingKind;
	final EAttribute readingValue;
	final EEnum kind;

	PoolFixture() {
		ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName("pool");
		ePackage.setNsPrefix("pool");
		ePackage.setNsURI(NS_URI);

		kind = EcoreFactory.eINSTANCE.createEEnum();
		kind.setName("Kind");
		kind.getELiterals().add(literal("TEMPERATURE", 0));
		kind.getELiterals().add(literal("PH", 1));

		pool = eClass("Pool");
		poolId = id(pool);
		poolName = attribute(pool, "name", EcorePackage.Literals.ESTRING);
		currentTemperature = attribute(pool, "currentTemperature", EcorePackage.Literals.EDOUBLE);
		currentPh = attribute(pool, "currentPh", EcorePackage.Literals.EDOUBLE);
		depth = attribute(pool, "depth", EcorePackage.Literals.EINT);

		tempSensor = eClass("TempSensor");
		tempSensorId = id(tempSensor);
		tempSensorPoolId = attribute(tempSensor, "poolId", EcorePackage.Literals.ESTRING);
		tempSensorValue = attribute(tempSensor, "value", EcorePackage.Literals.EDOUBLE);

		phSensor = eClass("PhSensor");
		phSensorId = id(phSensor);
		phSensorReading = attribute(phSensor, "reading", EcorePackage.Literals.EDOUBLE);

		reading = eClass("Reading");
		readingKind = attribute(reading, "kind", kind);
		readingValue = attribute(reading, "value", EcorePackage.Literals.EDOUBLE);

		station = eClass("Station");
		stationId = id(station);
		stationReadings = EcoreFactory.eINSTANCE.createEReference();
		stationReadings.setName("readings");
		stationReadings.setEType(reading);
		stationReadings.setContainment(true);
		stationReadings.setUpperBound(-1);
		station.getEStructuralFeatures().add(stationReadings);

		ePackage.getEClassifiers().add(kind);
	}

	EObject pool(String id) {
		EObject object = EcoreUtil.create(pool);
		object.eSet(poolId, id);
		return object;
	}

	EObject tempSensor(String id, String poolKey, double value) {
		EObject object = EcoreUtil.create(tempSensor);
		object.eSet(tempSensorId, id);
		object.eSet(tempSensorPoolId, poolKey);
		object.eSet(tempSensorValue, value);
		return object;
	}

	EObject phSensor(String id, double value) {
		EObject object = EcoreUtil.create(phSensor);
		object.eSet(phSensorId, id);
		object.eSet(phSensorReading, value);
		return object;
	}

	@SuppressWarnings("unchecked")
	EObject station(String id, Object... kindValuePairs) {
		EObject object = EcoreUtil.create(station);
		object.eSet(stationId, id);
		List<EObject> readings = (List<EObject>) object.eGet(stationReadings);
		for (int i = 0; i < kindValuePairs.length; i += 2) {
			EObject r = EcoreUtil.create(reading);
			r.eSet(readingKind, kind.getEEnumLiteral((String) kindValuePairs[i]).getInstance());
			r.eSet(readingValue, kindValuePairs[i + 1]);
			readings.add(r);
		}
		return object;
	}

	static ValueSource path(EStructuralFeature... features) {
		ValueSource source = MappingFactory.eINSTANCE.createValueSource();
		source.getFeaturePath().addAll(List.of(features));
		return source;
	}

	static ValueSource function(String id, EStructuralFeature... features) {
		ValueSource source = path(features);
		source.setFunctionId(id);
		return source;
	}

	static ValueSource expression(String text) {
		ValueSource source = MappingFactory.eINSTANCE.createValueSource();
		source.setExpression(text);
		return source;
	}

	static Slot slot(EStructuralFeature target, EClass source, ValueSource value) {
		Slot slot = MappingFactory.eINSTANCE.createSlot();
		slot.setTargetFeature(target);
		slot.setSourceClass(source);
		slot.setValue(value);
		return slot;
	}

	static IdentityResolution identity(EClass source, ValueSource resolver) {
		IdentityResolution identity = MappingFactory.eINSTANCE.createIdentityResolution();
		identity.setSourceClass(source);
		identity.setResolver(resolver);
		return identity;
	}

	/**
	 * The reference mapping: temperature from TempSensor.value (pool found by poolId), pH from
	 * PhSensor.reading (pool found by the function 'ph-to-pool'), name from the pool itself.
	 */
	Mapping poolMapping() {
		Mapping mapping = MappingFactory.eINSTANCE.createMapping();
		mapping.setId("pool-streams");
		mapping.setTarget(pool);
		mapping.getIdentities().add(identity(tempSensor, path(tempSensorPoolId)));
		mapping.getIdentities().add(identity(phSensor, function("ph-to-pool")));
		mapping.getSlots().add(slot(currentTemperature, tempSensor, path(tempSensorValue)));
		mapping.getSlots().add(slot(currentPh, phSensor, path(phSensorReading)));
		return mapping;
	}

	private EClass eClass(String name) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(name);
		ePackage.getEClassifiers().add(eClass);
		return eClass;
	}

	private static EAttribute id(EClass owner) {
		EAttribute id = attribute(owner, "id", EcorePackage.Literals.ESTRING);
		id.setID(true);
		return id;
	}

	private static EAttribute attribute(EClass owner, String name, EClassifier type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}

	private static EEnumLiteral literal(String name, int value) {
		EEnumLiteral literal = EcoreFactory.eINSTANCE.createEEnumLiteral();
		literal.setName(name);
		literal.setLiteral(name.toLowerCase());
		literal.setValue(value);
		return literal;
	}
}
