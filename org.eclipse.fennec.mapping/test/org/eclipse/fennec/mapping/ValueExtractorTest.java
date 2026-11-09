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
import static org.eclipse.fennec.mapping.PoolFixture.expression;
import static org.eclipse.fennec.mapping.PoolFixture.function;
import static org.eclipse.fennec.mapping.PoolFixture.path;
import static org.eclipse.fennec.mapping.PoolFixture.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The value ladder (requirements MAP-06, MAP-07, STR-07/D-16): producers, constant, converter, target conversion.
 */
class ValueExtractorTest {

	private PoolFixture fx;
	private FunctionRegistry functions;
	private ConverterRegistry converters;
	private final AtomicReference<String> lastExpression = new AtomicReference<>();
	private ValueExtractor extractor;

	@BeforeEach
	void setUp() {
		fx = new PoolFixture();
		functions = new FunctionRegistry();
		converters = new ConverterRegistry();
		ExpressionEvaluator evaluator = (text, self) -> {
			lastExpression.set(text);
			return "null".equals(text) ? Optional.empty() : Optional.of("expr:" + text);
		};
		extractor = new ValueExtractor(functions, converters, evaluator);
	}

	@Test
	void aPathReadsTheValue() {
		EObject sensor = fx.tempSensor("t1", "pool-1", 24.5);
		assertThat(extractor.produce(path(fx.tempSensorValue), sensor)).contains(24.5);
	}

	@Test
	void aPathCrossingACollectionTakesTheIndex() {
		EObject station = fx.station("s1", "TEMPERATURE", 21.0, "PH", 7.2);
		ValueSource second = path(fx.stationReadings, fx.readingValue);
		second.setCollectionIndex(1);

		assertThat(extractor.produce(path(fx.stationReadings, fx.readingValue), station)).contains(21.0);
		assertThat(extractor.produce(second, station)).contains(7.2);
	}

	@Test
	void anIndexOutOfBoundsYieldsNothing() {
		EObject station = fx.station("s1", "TEMPERATURE", 21.0);
		ValueSource source = path(fx.stationReadings, fx.readingValue);
		source.setCollectionIndex(3);
		assertThat(extractor.produce(source, station)).isEmpty();
	}

	@Test
	void aCollectionFilterSelectsByValueAndWinsOverTheIndex() {
		EObject station = fx.station("s1", "TEMPERATURE", 21.0, "PH", 7.2);
		ValueSource ph = path(fx.stationReadings, fx.readingValue);
		ph.setCollectionFilter("kind='PH'");
		ph.setCollectionIndex(0);
		ValueSource none = path(fx.stationReadings, fx.readingValue);
		none.setCollectionFilter("kind=CHLORINE");

		assertThat(extractor.produce(ph, station)).contains(7.2);
		assertThat(extractor.produce(none, station)).isEmpty();
	}

	@Test
	void anEmptyCollectionOrAForeignFeatureYieldsNothing() {
		EObject empty = fx.station("s1");
		EObject sensor = fx.tempSensor("t1", "pool-1", 24.5);

		assertThat(extractor.produce(path(fx.stationReadings, fx.readingValue), empty)).isEmpty();
		assertThat(extractor.produce(path(fx.phSensorReading), sensor)).isEmpty();
	}

	@Test
	void theExpressionWinsAndAPathBesideItIsIgnored() {
		ValueSource source = path(fx.tempSensorValue);
		source.setExpression("self.value * 2");
		source.setFunctionId("double");
		functions.register("double", input -> "function");

		assertThat(extractor.produce(source, fx.tempSensor("t1", "p", 1.0))).contains("expr:self.value * 2");
	}

	@Test
	void anExpressionThatYieldsNothingDoesNotFallBackToThePath() {
		ValueSource source = path(fx.tempSensorValue);
		source.setExpression("null");
		assertThat(extractor.produce(source, fx.tempSensor("t1", "p", 1.0))).isEmpty();
	}

	@Test
	void withoutAnEvaluatorAnExpressionYieldsNothing() {
		ValueSource source = path(fx.tempSensorValue);
		source.setExpression("self.value");
		assertThat(ValueExtractor.basic().produce(source, fx.tempSensor("t1", "p", 1.0))).isEmpty();
	}

	@Test
	void aFunctionWithoutPathGetsTheWholeSource() {
		List<Object> inputs = new ArrayList<>();
		functions.register("id", input -> {
			inputs.add(input);
			return "ok";
		});
		EObject sensor = fx.tempSensor("t1", "p", 1.0);

		assertThat(extractor.produce(function("id"), sensor)).contains("ok");
		assertThat(inputs).containsExactly(sensor);
	}

	@Test
	void aFunctionWithPathGetsWhatThePathFound() {
		functions.register("half", input -> (Double) input / 2);
		assertThat(extractor.produce(function("half", fx.tempSensorValue), fx.tempSensor("t1", "p", 9.0))).contains(4.5);
	}

	@Test
	void aFunctionOnAPathEndingOnACollectionGetsTheWholeCollection() {
		List<Object> inputs = new ArrayList<>();
		functions.register("count", input -> {
			inputs.add(input);
			return ((List<?>) input).size();
		});
		EObject station = fx.station("s1", "TEMPERATURE", 21.0, "PH", 7.2);

		assertThat(extractor.produce(function("count", fx.stationReadings), station)).contains(2);
		ValueSource narrowed = function("count", fx.stationReadings);
		narrowed.setCollectionFilter("kind=PH");
		functions.register("count", input -> input instanceof List<?> ? "list" : "element");
		assertThat(extractor.produce(narrowed, station)).contains("element");
	}

	@Test
	void anUnknownOrFailingFunctionYieldsNothing() {
		functions.register("boom", input -> {
			throw new IllegalStateException("boom");
		});
		EObject sensor = fx.tempSensor("t1", "p", 1.0);

		assertThat(extractor.produce(function("missing"), sensor)).isEmpty();
		assertThat(extractor.produce(function("boom"), sensor)).isEmpty();
	}

	@Test
	void theConstantAnswersWhenTheProducerYieldsNothingParsedAndNotConverted() {
		ValueSource source = function("missing");
		source.setConstant("12");
		source.setConverterId("times10");
		converters.register("times10", value -> ((Number) value).doubleValue() * 10);

		assertThat(extractor.extract(source, fx.tempSensor("t1", "p", 1.0), EcorePackage.Literals.EINT)).contains(12);
		assertThat(extractor.extract(source, fx.tempSensor("t1", "p", 1.0), null)).contains("12");
	}

	@Test
	void aConstantThatDoesNotFitTheTypeYieldsNothing() {
		ValueSource constant = PoolFixture.path();
		constant.setConstant("deep");
		assertThat(extractor.extract(constant, fx.pool("p"), EcorePackage.Literals.EINT)).isEmpty();
	}

	@Test
	void theConverterPostProcessesAProducedValue() {
		converters.register("toFahrenheit", value -> (Double) value * 1.8 + 32);
		ValueSource source = path(fx.tempSensorValue);
		source.setConverterId("toFahrenheit");

		assertThat(extractor.extract(source, fx.tempSensor("t1", "p", 100.0), null)).contains(212.0);
	}

	@Test
	void anUnknownConverterPassesAndAFailingOneDrops() {
		converters.register("fail", value -> {
			throw new IllegalArgumentException("no");
		});
		ValueSource unknown = path(fx.tempSensorValue);
		unknown.setConverterId("nope");
		ValueSource failing = path(fx.tempSensorValue);
		failing.setConverterId("fail");
		EObject sensor = fx.tempSensor("t1", "p", 3.0);

		assertThat(extractor.extract(unknown, sensor, null)).contains(3.0);
		assertThat(extractor.extract(failing, sensor, null)).isEmpty();
	}

	@Test
	void theStreamCarriesADoubleAndTheTargetGetsAnInteger() {
		// D-16: a conversion out of the stream into the target, independent of the source converter
		Slot slot = slot(fx.depth, fx.tempSensor, path(fx.tempSensorValue));
		slot.setTargetConverterId("round");
		converters.register("round", value -> Math.round((Double) value));

		Optional<Object> streamValue = extractor.extract(slot, fx.tempSensor("t1", "p", 2.6));
		assertThat(streamValue).contains(2.6);
		assertThat(extractor.toTarget(slot, streamValue.get())).contains(3);
	}

	@Test
	void coercionGoesThroughTheStringFormAndRefusesWhatDoesNotFit() {
		Slot slot = slot(fx.depth, fx.tempSensor, path(fx.tempSensorValue));
		assertThat(extractor.toTarget(slot, "7")).contains(7);
		assertThat(extractor.toTarget(slot, 7)).contains(7);
		assertThat(extractor.toTarget(slot, "seven")).isEmpty();
	}

	@Test
	void enumsAreCoercedByName() {
		Object ph = fx.kind.getEEnumLiteral("PH").getInstance();
		assertThat(ValueExtractor.coerce(ph, EcorePackage.Literals.ESTRING)).isEqualTo("PH");
	}

	@Test
	void namedSlotsAreNotCoerced() {
		Slot named = slot(null, fx.tempSensor, expression("x"));
		named.setName("temperature");
		assertThat(extractor.toTarget(named, 24.5)).contains(24.5);
		assertThat(extractor.extract(named, fx.tempSensor("t1", "p", 1.0))).contains("expr:x");
		assertThat(lastExpression.get()).isEqualTo("x");
	}
}
