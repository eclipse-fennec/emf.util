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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.eclipse.fennec.mapping.PoolFixture.function;
import static org.eclipse.fennec.mapping.PoolFixture.identity;
import static org.eclipse.fennec.mapping.PoolFixture.path;
import static org.eclipse.fennec.mapping.PoolFixture.slot;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.fennec.model.mapping.IdentityResolution;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.MappingFactory;
import org.eclipse.fennec.model.mapping.Slot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The pool scenario end to end, and identity resolution (requirements CAP-02, CAP-03, CAP-04, MAP-05).
 */
class MapperTest {

	private PoolFixture fx;
	private FunctionRegistry functions;
	private Mapper mapper;
	private final AtomicInteger phLookups = new AtomicInteger();

	@BeforeEach
	void setUp() {
		fx = new PoolFixture();
		functions = new FunctionRegistry();
		Map<String, String> phTable = Map.of("ph-7", "pool-1", "ph-8", "pool-2");
		functions.register("ph-to-pool", input -> {
			phLookups.incrementAndGet();
			return phTable.get(new IdentityResolver(ValueExtractor.basic()).idOf((EObject) input));
		});
		ValueExtractor extractor = new ValueExtractor(functions, new ConverterRegistry(), null);
		mapper = new Mapper(extractor, new IdentityResolver(extractor));
	}

	@Test
	void aTemperatureSensorFeedsItsPoolByKey() {
		List<MappedValue> values = mapper.map(fx.poolMapping(), fx.tempSensor("t1", "pool-1", 24.5));

		assertThat(values).singleElement().satisfies(v -> {
			assertThat(v.targetId()).isEqualTo("pool-1");
			assertThat(v.slot().getTargetFeature()).isSameAs(fx.currentTemperature);
			assertThat(v.value()).isEqualTo(24.5);
		});
	}

	@Test
	void aPhSensorFeedsItsPoolThroughAFunction() {
		List<MappedValue> values = mapper.map(fx.poolMapping(), fx.phSensor("ph-8", 7.1));

		assertThat(values).singleElement().satisfies(v -> {
			assertThat(v.targetId()).isEqualTo("pool-2");
			assertThat(v.slot().getTargetFeature()).isSameAs(fx.currentPh);
		});
	}

	@Test
	void bothSensorsEndUpInOnePool() {
		Mapping mapping = fx.poolMapping();
		EObject pool = fx.pool("pool-1");

		for (MappedValue value : mapper.map(mapping, fx.tempSensor("t1", "pool-1", 24.5))) {
			assertThat(mapper.apply(value, pool)).isTrue();
		}
		for (MappedValue value : mapper.map(mapping, fx.phSensor("ph-7", 7.2))) {
			assertThat(mapper.apply(value, pool)).isTrue();
		}

		assertThat(pool.eGet(fx.currentTemperature)).isEqualTo(24.5);
		assertThat(pool.eGet(fx.currentPh)).isEqualTo(7.2);
	}

	@Test
	void theIdentityAnswerIsCachedPerSource() {
		Mapping mapping = fx.poolMapping();
		mapper.map(mapping, fx.phSensor("ph-7", 7.0));
		mapper.map(mapping, fx.phSensor("ph-7", 7.1));
		mapper.map(mapping, fx.phSensor("ph-7", 7.2));

		assertThat(phLookups).hasValue(1);

		IdentityResolution ph = Mappings.identityFor(mapping, fx.phSensor).orElseThrow();
		mapper.identities().invalidate(ph, "ph-7");
		mapper.map(mapping, fx.phSensor("ph-7", 7.3));
		assertThat(phLookups).hasValue(2);
	}

	@Test
	void aNonCacheableIdentityIsAskedEveryTime() {
		Mapping mapping = fx.poolMapping();
		Mappings.identityFor(mapping, fx.phSensor).orElseThrow().setCacheable(false);
		mapper.map(mapping, fx.phSensor("ph-7", 7.0));
		mapper.map(mapping, fx.phSensor("ph-7", 7.1));

		assertThat(phLookups).hasValue(2);
	}

	@Test
	void aSourceWithoutIdentityResolutionFeedsTheTargetWithItsOwnId() {
		Mapping mapping = MappingFactory.eINSTANCE.createMapping();
		mapping.setTarget(fx.pool);
		mapping.getSlots().add(slot(fx.currentTemperature, fx.tempSensor, path(fx.tempSensorValue)));

		assertThat(mapper.map(mapping, fx.tempSensor("same-id", "ignored", 1.0)))
				.extracting(MappedValue::targetId).containsExactly("same-id");
	}

	@Test
	void sourceEqualsTargetNeedsNoSourceClass() {
		Mapping mapping = MappingFactory.eINSTANCE.createMapping();
		mapping.setTarget(fx.pool);
		Slot name = slot(fx.poolName, null, path(fx.poolName));
		mapping.getSlots().add(name);
		EObject pool = fx.pool("pool-9");
		pool.eSet(fx.poolName, "Freibad Ost");

		assertThat(mapper.map(mapping, pool)).singleElement().satisfies(v -> {
			assertThat(v.targetId()).isEqualTo("pool-9");
			assertThat(v.value()).isEqualTo("Freibad Ost");
		});
	}

	@Test
	void oneSensorCanFeedSeveralPools() {
		functions.register("outdoor", input -> List.of("pool-1", "pool-2", "pool-3"));
		Mapping mapping = MappingFactory.eINSTANCE.createMapping();
		mapping.setTarget(fx.pool);
		mapping.getIdentities().add(identity(fx.tempSensor, function("outdoor")));
		mapping.getSlots().add(slot(fx.currentTemperature, fx.tempSensor, path(fx.tempSensorValue)));

		assertThat(mapper.map(mapping, fx.tempSensor("outside", null, 12.0)))
				.extracting(MappedValue::targetId).containsExactly("pool-1", "pool-2", "pool-3");
	}

	@Test
	void anIdentityResolverMayAnswerWithObjects() {
		EObject pool = fx.pool("pool-7");
		functions.register("as-object", input -> pool);
		Mapping mapping = MappingFactory.eINSTANCE.createMapping();
		mapping.setTarget(fx.pool);
		mapping.getIdentities().add(identity(fx.tempSensor, function("as-object")));
		mapping.getSlots().add(slot(fx.currentTemperature, fx.tempSensor, path(fx.tempSensorValue)));

		assertThat(mapper.map(mapping, fx.tempSensor("t", null, 1.0))).extracting(MappedValue::targetId)
				.containsExactly("pool-7");
	}

	@Test
	void noTargetIdOrNoValueMeansNothingIsMapped() {
		Mapping mapping = fx.poolMapping();
		assertThat(mapper.map(mapping, fx.tempSensor("t1", null, 24.5))).isEmpty();
		assertThat(mapper.map(mapping, fx.phSensor("unknown", 7.0))).isEmpty();
		assertThat(mapper.map(mapping, fx.station("s1"))).isEmpty();
	}

	@Test
	void writingANamedSlotOrIntoAForeignClassIsRefused() {
		Mapping mapping = fx.poolMapping();
		MappedValue value = mapper.map(mapping, fx.tempSensor("t1", "pool-1", 24.5)).get(0);
		assertThatIllegalArgumentException().isThrownBy(() -> mapper.apply(value, fx.phSensor("x", 1.0)));

		Slot named = slot(null, fx.tempSensor, path(fx.tempSensorValue));
		named.setName("temperature");
		MappedValue namedValue = new MappedValue(mapping, named, "pool-1", 1.0, fx.tempSensor("t", "pool-1", 1.0));
		assertThatIllegalArgumentException().isThrownBy(() -> mapper.apply(namedValue, fx.pool("pool-1")));
	}
}
