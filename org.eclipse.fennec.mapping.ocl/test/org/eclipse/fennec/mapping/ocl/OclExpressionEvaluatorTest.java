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
package org.eclipse.fennec.mapping.ocl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.m2x.ocl.engine.OclEngines;
import org.eclipse.fennec.m2x.ocl.engine.OclLruExpressionCache;
import org.eclipse.fennec.m2x.ocl.parser.OclParserSupport;
import org.eclipse.fennec.mapping.ConverterRegistry;
import org.eclipse.fennec.mapping.FunctionRegistry;
import org.eclipse.fennec.mapping.IdentityResolver;
import org.eclipse.fennec.mapping.MappedValue;
import org.eclipse.fennec.mapping.Mapper;
import org.eclipse.fennec.mapping.ValueExtractor;
import org.eclipse.fennec.model.mapping.IdentityResolution;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.MappingFactory;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;
import org.junit.jupiter.api.Test;

/**
 * OCL against the real Fennec engine: what an expression reads, what each way of failing yields
 * (always no value), and an expression as value and as identity key in a mapping.
 */
class OclExpressionEvaluatorTest {

	private final EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
	private final EClass pool = EcoreFactory.eINSTANCE.createEClass();
	private final EAttribute poolId = attribute(pool, "id", EcorePackage.Literals.ESTRING);
	private final EAttribute currentTemperature = attribute(pool, "currentTemperature", EcorePackage.Literals.EDOUBLE);
	private final EClass station = EcoreFactory.eINSTANCE.createEClass();
	private final EAttribute stationId = attribute(station, "id", EcorePackage.Literals.ESTRING);
	private final EAttribute site = attribute(station, "site", EcorePackage.Literals.ESTRING);
	private final EClass reading = EcoreFactory.eINSTANCE.createEClass();
	private final EAttribute kind = attribute(reading, "kind", EcorePackage.Literals.ESTRING);
	private final EAttribute value = attribute(reading, "value", EcorePackage.Literals.EDOUBLE);
	private final EReference readings = EcoreFactory.eINSTANCE.createEReference();

	private final OclLruExpressionCache cache = OclLruExpressionCache.ofSize(64);
	private final OclExpressionEvaluator evaluator = new OclExpressionEvaluator(
			OclEngines.create(new OclParserSupport(), cache));

	{
		ePackage.setName("pool");
		ePackage.setNsPrefix("pool");
		ePackage.setNsURI("https://example.org/pool");
		pool.setName("Pool");
		station.setName("Station");
		reading.setName("Reading");
		poolId.setID(true);
		stationId.setID(true);
		readings.setName("readings");
		readings.setEType(reading);
		readings.setContainment(true);
		readings.setUpperBound(-1);
		station.getEStructuralFeatures().add(readings);
		ePackage.getEClassifiers().addAll(List.of(pool, station, reading));
	}

	@Test
	void anExpressionReadsTheSourceObject() {
		EObject s = station("s1", "pool-1", "temp", 20.0, "ph", 7.4, "temp", 22.0);

		assertThat(evaluator.evaluate("self.readings->size()", s)).contains(3);
		assertThat(evaluator.evaluate("self.readings->select(r | r.kind = 'temp')->collect(r | r.value)->sum() / 2", s))
				.hasValueSatisfying(v -> assertThat(((Number) v).doubleValue()).isEqualTo(21.0));
		assertThat(evaluator.evaluate("self.readings->select(r | r.kind = 'ph')->first().value", s)).contains(7.4);
	}

	@Test
	void blankOrMissingInputYieldsNothing() {
		EObject s = station("s1", "pool-1");
		assertThat(evaluator.evaluate(null, s)).isEmpty();
		assertThat(evaluator.evaluate("  ", s)).isEmpty();
		assertThat(evaluator.evaluate("self.site", null)).isEmpty();
	}

	@Test
	void voidInvalidAndUnparseableYieldNothing() {
		EObject s = station("s1", null);

		assertThat(evaluator.evaluate("self.site", s)).isEmpty();
		assertThat(evaluator.evaluate("self.readings->first().value", s)).isEmpty();
		assertThat(evaluator.evaluate("self.readings->select(", s)).isEmpty();
		assertThat(evaluator.evaluate("self.noSuchFeature", s)).isEmpty();
	}

	@Test
	void anUnparseableExpressionIsParsedOnceByTheEngine() {
		EObject s = station("s1", "pool-1");
		assertThat(evaluator.evaluate("self.readings->select(", s)).isEmpty();
		long misses = cache.missCount();

		assertThat(evaluator.evaluate("self.readings->select(", s)).isEmpty();
		assertThat(evaluator.evaluate("self.readings->select(", s)).isEmpty();

		assertThat(cache.missCount()).isEqualTo(misses);
		assertThat(cache.hitCount()).isGreaterThanOrEqualTo(2);
	}

	@Test
	void variablesAreBoundBesideSelf() {
		EObject s = station("s1", "pool-1");
		Map<String, Object> variables = new HashMap<>();
		variables.put("value", 24.5);
		variables.put("last", 24.0);

		assertThat(evaluator.evaluate("value - last >= 0.5", s, variables)).contains(true);
		assertThat(evaluator.evaluate("self.site = 'pool-1' and value > 30.0", s, variables)).contains(false);

		variables.put("last", null);
		assertThat(evaluator.evaluate("last.oclIsUndefined()", s, variables)).contains(true);
	}

	@Test
	void anExpressionIsTheValueAndTheIdentityKeyOfAMapping() {
		ValueExtractor extractor = new ValueExtractor(new FunctionRegistry(), new ConverterRegistry(), evaluator);
		Mapper mapper = new Mapper(extractor, new IdentityResolver(extractor));

		Mapping mapping = MappingFactory.eINSTANCE.createMapping();
		mapping.setId("station-to-pool");
		mapping.setTarget(pool);
		IdentityResolution identity = MappingFactory.eINSTANCE.createIdentityResolution();
		identity.setSourceClass(station);
		identity.setResolver(expression("self.site"));
		mapping.getIdentities().add(identity);
		Slot slot = MappingFactory.eINSTANCE.createSlot();
		slot.setTargetFeature(currentTemperature);
		slot.setSourceClass(station);
		slot.setValue(expression("self.readings->select(r | r.kind = 'temp')->first().value"));
		mapping.getSlots().add(slot);

		List<MappedValue> values = mapper.map(mapping, station("s1", "pool-3", "ph", 7.1, "temp", 23.5));
		EObject target = EcoreUtil.create(pool);
		target.eSet(poolId, "pool-3");

		assertThat(values).singleElement().satisfies(v -> {
			assertThat(v.targetId()).isEqualTo("pool-3");
			assertThat(mapper.apply(v, target)).isTrue();
		});
		assertThat(target.eGet(currentTemperature)).isEqualTo(23.5);
	}

	private EObject station(String id, String siteKey, Object... kindValuePairs) {
		EObject s = EcoreUtil.create(station);
		s.eSet(stationId, id);
		s.eSet(site, siteKey);
		@SuppressWarnings("unchecked")
		List<EObject> list = (List<EObject>) s.eGet(readings);
		for (int i = 0; i < kindValuePairs.length; i += 2) {
			EObject r = EcoreUtil.create(reading);
			r.eSet(kind, kindValuePairs[i]);
			r.eSet(value, kindValuePairs[i + 1]);
			list.add(r);
		}
		return s;
	}

	private static ValueSource expression(String text) {
		ValueSource source = MappingFactory.eINSTANCE.createValueSource();
		source.setExpression(text);
		return source;
	}

	private static EAttribute attribute(EClass owner, String name, EClassifier type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setName(name);
		attribute.setEType(type);
		owner.getEStructuralFeatures().add(attribute);
		return attribute;
	}
}
