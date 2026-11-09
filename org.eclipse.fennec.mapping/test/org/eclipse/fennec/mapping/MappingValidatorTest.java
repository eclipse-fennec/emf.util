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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.MappingFactory;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Load-time checks and the registry (requirements MAP-03, MAP-05, MAP-08, MAP-12).
 */
class MappingValidatorTest {

	private PoolFixture fx;
	private MappingValidator validator;

	@BeforeEach
	void setUp() {
		fx = new PoolFixture();
		validator = new MappingValidator();
	}

	@Test
	void theReferenceMappingIsValid() {
		Diagnostic diagnostic = validator.validate(fx.poolMapping());
		assertThat(diagnostic.getSeverity()).as(messages(diagnostic).toString()).isEqualTo(Diagnostic.OK);
	}

	@Test
	void aSlotNeedsATargetFeatureOfTheTargetOrAName() {
		Mapping mapping = fx.poolMapping();
		mapping.getSlots().add(slot(fx.tempSensorValue, fx.tempSensor, path(fx.tempSensorValue)));
		mapping.getSlots().add(slot(null, fx.tempSensor, path(fx.tempSensorValue)));

		assertThat(messages(validator.validate(mapping))).containsExactly(
				"slot 2 (value): target feature 'value' is no feature of the target class Pool",
				"slot 3 (?): needs a target feature or a name");
	}

	@Test
	void aTargetFeatureIsFilledByOneSlotOnly() {
		Mapping mapping = fx.poolMapping();
		mapping.getSlots().add(slot(fx.currentTemperature, fx.phSensor, path(fx.phSensorReading)));

		assertThat(messages(validator.validate(mapping)))
				.containsExactly("slot 2 (currentTemperature): target feature 'currentTemperature' is already filled by another slot");
	}

	@Test
	void aPathMustBeAChainTheMetamodelAllows() {
		Mapping mapping = fx.poolMapping();
		mapping.getSlots().add(slot(fx.poolName, fx.tempSensor, path(fx.phSensorReading)));
		mapping.getSlots().add(slot(fx.depth, fx.station, path(fx.stationId, fx.readingValue)));
		mapping.getSlots().add(slot(fx.currentPh, fx.station, path(fx.stationReadings, fx.readingValue)));

		assertThat(messages(validator.validate(mapping))).containsExactly(
				"slot 2 (name): path step 0 'reading' is no feature of TempSensor",
				"slot 3 (depth): path step 0 'id' is an attribute but the path goes on",
				"slot 4 (currentPh): target feature 'currentPh' is already filled by another slot");
	}

	@Test
	void aValueSourceNeedsAProducerOrAConstantAndAWellFormedFilter() {
		Mapping mapping = fx.poolMapping();
		mapping.getSlots().add(slot(fx.poolName, fx.tempSensor, MappingFactory.eINSTANCE.createValueSource()));
		ValueSource filtered = path(fx.stationReadings, fx.readingValue);
		filtered.setCollectionFilter("=PH");
		mapping.getSlots().add(slot(fx.depth, fx.station, filtered));

		assertThat(messages(validator.validate(mapping))).containsExactly(
				"slot 2 (name): the value source declares neither a producer nor a constant",
				"slot 3 (depth): collection filter '=PH' is malformed, expected path.to.feature=value");
	}

	@Test
	void identitiesAreUniquePerSourceAndTargetClass() {
		Mapping mapping = fx.poolMapping();
		mapping.getIdentities().add(identity(fx.tempSensor, path(fx.tempSensorId)));
		mapping.getIdentities().add(identity(null, null));

		assertThat(messages(validator.validate(mapping))).containsExactly(
				"identity 2 (TempSensor): a second identity resolution for the same source and target class",
				"identity 3 (?): needs a source class");
	}

	@Test
	void unknownFunctionsAndConvertersAreWarningsWhenRegistriesAreGiven() {
		FunctionRegistry functions = new FunctionRegistry();
		Mapping mapping = fx.poolMapping();
		Slot slot = mapping.getSlots().get(0);
		slot.setTargetConverterId("round");

		MappingValidator strict = new MappingValidator(functions, new ConverterRegistry());
		Diagnostic diagnostic = strict.validate(mapping);

		assertThat(diagnostic.getSeverity()).isEqualTo(Diagnostic.WARNING);
		assertThat(messages(diagnostic)).containsExactly(
				"slot 0 (currentTemperature), target converter: no converter registered for id 'round'",
				"identity 1 (PhSensor), resolver: no function registered for id 'ph-to-pool'");
		assertThat(strict.isValid(mapping)).isTrue();
	}

	@Test
	void aMappingIncludingUnmappedFeaturesIsFoundForItsTargetAsSource() {
		InMemoryMappingRegistry registry = new InMemoryMappingRegistry();
		Mapping mapping = fx.poolMapping();
		registry.register(mapping);
		assertThat(registry.forSource(fx.pool)).isEmpty();

		mapping.setIncludeUnmapped(true);
		assertThat(registry.forSource(fx.pool)).containsExactly(mapping);
		assertThat(registry.forSource(fx.phSensor)).containsExactly(mapping);
	}

	@Test
	void theRegistryRefusesInvalidMappingsAndFindsByTargetAndSource() {
		InMemoryMappingRegistry registry = new InMemoryMappingRegistry();
		List<String> events = new ArrayList<>();
		registry.addListener((previous, current) -> events.add((previous == null ? "-" : previous.getId()) + ">"
				+ (current == null ? "-" : current.getId())));

		Mapping mapping = fx.poolMapping();
		registry.register(mapping);

		assertThat(registry.get("pool-streams")).containsSame(mapping);
		assertThat(registry.forTarget(fx.pool)).containsExactly(mapping);
		assertThat(registry.forSource(fx.phSensor)).containsExactly(mapping);
		assertThat(registry.forSource(fx.station)).isEmpty();
		assertThat(mapping.getSupportedEClasses()).as("registering does not change the mapping").isEmpty();
		Mappings.normalize(mapping);
		assertThat(mapping.getSupportedEClasses()).containsExactly(fx.tempSensor, fx.phSensor);
		assertThat(mapping.getResultEClasses()).containsExactly(fx.pool);

		Mapping broken = fx.poolMapping();
		broken.setId("broken");
		broken.getSlots().add(slot(null, fx.tempSensor, function("x")));
		assertThatIllegalArgumentException().isThrownBy(() -> registry.register(broken))
				.withMessageContaining("needs a target feature or a name");

		Mapping noId = fx.poolMapping();
		noId.setId(null);
		assertThatIllegalArgumentException().isThrownBy(() -> registry.register(noId));

		registry.unregister("pool-streams");
		assertThat(registry.all()).isEmpty();
		assertThat(events).containsExactly("->pool-streams", "pool-streams>-");
	}

	private static List<String> messages(Diagnostic diagnostic) {
		return diagnostic.getChildren().stream().map(Diagnostic::getMessage).toList();
	}
}
