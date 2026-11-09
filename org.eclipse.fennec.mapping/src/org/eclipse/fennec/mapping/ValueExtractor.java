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

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.mapping.impl.CollectionFilter;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;

/**
 * Produces values from source objects - the value ladder every mapping consumer shares.
 * <p>
 * The three stages, each its own method so a consumer can stop in between (streaming stores the
 * value after {@link #extract} and writes the target only later):
 * <ol>
 * <li>{@link #produce}: the first declared producer answers - {@code expression} (OCL), else
 * {@code functionId} (on the whole source object, or on what the feature path found), else the
 * {@code featurePath}. It is no fallback chain: a declared producer that yields nothing means the
 * source carries no value.</li>
 * <li>{@link #extract}: when the producer yields nothing, the {@code constant} answers, parsed into
 * the target type and not converted; otherwise {@code converterId} post-processes the produced
 * value.</li>
 * <li>{@link #toTarget}: {@code targetConverterId}, then coercion to the type of the slot's target
 * feature.</li>
 * </ol>
 * Semantics follow the Fennec event atlas mapping, from which this ladder was extracted.
 * <p>
 * Thread-safe when the registries and the evaluator are.
 */
public final class ValueExtractor {

	private static final Logger LOGGER = Logger.getLogger(ValueExtractor.class.getName());

	private final FunctionRegistry functions;
	private final ConverterRegistry converters;
	private final ExpressionEvaluator evaluator;

	/**
	 * @param functions the functions value sources may name
	 * @param converters the converters value sources and slots may name
	 * @param evaluator the expression language; {@code null} when none is deployed - expressions then produce nothing
	 */
	public ValueExtractor(FunctionRegistry functions, ConverterRegistry converters, ExpressionEvaluator evaluator) {
		this.functions = requireNonNull(functions, "FunctionRegistry is required");
		this.converters = requireNonNull(converters, "ConverterRegistry is required");
		this.evaluator = evaluator;
	}

	/**
	 * An extractor with empty registries and no expression language - paths and constants only.
	 *
	 * @return the extractor
	 */
	public static ValueExtractor basic() {
		return new ValueExtractor(new FunctionRegistry(), new ConverterRegistry(), null);
	}

	/**
	 * Stage 1: the raw value of the first declared producer.
	 *
	 * @param source the value source
	 * @param self the source object
	 * @return the raw value, or empty
	 */
	public Optional<Object> produce(ValueSource source, EObject self) {
		requireNonNull(source, "ValueSource is required");
		requireNonNull(self, "Source object is required");
		List<EStructuralFeature> path = source.getFeaturePath();
		String expression = source.getExpression();
		if (isSet(expression)) {
			if (evaluator == null) {
				LOGGER.fine(() -> "Expression without ExpressionEvaluator, no value: " + expression);
				return Optional.empty();
			}
			return evaluator.evaluate(expression, self);
		}
		String functionId = source.getFunctionId();
		if (isSet(functionId)) {
			if (path.isEmpty()) {
				return functions.apply(functionId, self);
			}
			Optional<Object> input = followPathForFunction(source, self, path);
			return input.isEmpty() ? Optional.empty() : functions.apply(functionId, input.get());
		}
		if (path.isEmpty()) {
			return Optional.empty();
		}
		return followPath(source, self, path);
	}

	/**
	 * Stages 1 and 2: the value a source object offers, before it is written anywhere.
	 *
	 * @param source the value source
	 * @param self the source object
	 * @param targetType the type a constant is parsed into; {@code null} keeps the constant a string
	 * @return the value, or empty
	 */
	public Optional<Object> extract(ValueSource source, EObject self, EClassifier targetType) {
		Optional<Object> raw = produce(source, self);
		if (raw.isEmpty()) {
			return constant(source.getConstant(), targetType);
		}
		return converters.convert(source.getConverterId(), raw.get());
	}

	/**
	 * Stages 1 and 2 for a slot: the constant is parsed into the type of the slot's target feature.
	 *
	 * @param slot the slot
	 * @param self the source object
	 * @return the value, or empty
	 */
	public Optional<Object> extract(Slot slot, EObject self) {
		requireNonNull(slot, "Slot is required");
		return extract(slot.getValue(), self, targetType(slot));
	}

	/**
	 * Stage 3: the value as it is written into the target - the slot's target converter, then
	 * coercion to the target feature's data type.
	 *
	 * @param slot the slot
	 * @param value the extracted value
	 * @return the value to write, or empty when it is dropped or does not fit the type
	 */
	public Optional<Object> toTarget(Slot slot, Object value) {
		requireNonNull(slot, "Slot is required");
		Optional<Object> converted = converters.convert(slot.getTargetConverterId(), value);
		if (converted.isEmpty()) {
			return converted;
		}
		EClassifier type = targetType(slot);
		if (!(type instanceof EDataType dataType)) {
			return converted;
		}
		try {
			return Optional.ofNullable(coerce(converted.get(), dataType));
		} catch (RuntimeException e) {
			LOGGER.warning(() -> "Value " + converted.get() + " does not fit " + dataType.getName() + ": " + e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * Coerces a value to a data type: unchanged when it already fits, otherwise through its string form.
	 *
	 * @param value the value
	 * @param type the data type
	 * @return the coerced value
	 */
	public static Object coerce(Object value, EDataType type) {
		if (value == null) {
			return null;
		}
		Class<?> instanceClass = boxed(type.getInstanceClass());
		if (instanceClass != null && instanceClass.isInstance(value)) {
			return value;
		}
		Object literalSource = value instanceof Enumerator enumerator ? enumerator.getName() : value;
		String literal = literalSource instanceof String text ? text : String.valueOf(literalSource);
		return EcoreUtil.createFromString(type, literal);
	}

	private Optional<Object> constant(String literal, EClassifier targetType) {
		if (literal == null || literal.isEmpty()) {
			return Optional.empty();
		}
		if (!(targetType instanceof EDataType dataType)) {
			return Optional.of(literal);
		}
		try {
			return Optional.ofNullable(EcoreUtil.createFromString(dataType, literal));
		} catch (RuntimeException e) {
			LOGGER.warning(() -> "Constant '" + literal + "' is no " + dataType.getName() + ": " + e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * The input of a function: what its path found, including a whole collection when the path ends
	 * on one and the source does not narrow it (a filter, or a non-zero index).
	 */
	private Optional<Object> followPathForFunction(ValueSource source, EObject self, List<EStructuralFeature> path) {
		EStructuralFeature last = path.get(path.size() - 1);
		if (!last.isMany() || narrows(source)) {
			return followPath(source, self, path);
		}
		Optional<Object> owner = path.size() == 1 ? Optional.of(self)
				: followPath(source, self, path.subList(0, path.size() - 1));
		if (owner.isEmpty() || !(owner.get() instanceof EObject eObject) || !eObject.eClass().getEAllStructuralFeatures().contains(last)) {
			return Optional.empty();
		}
		return Optional.ofNullable(eObject.eGet(last));
	}

	/**
	 * Walks a feature path; wherever it crosses a collection, the filter (else the index) picks the
	 * element to carry on from. A feature the current object does not have ends the walk without a value.
	 */
	private Optional<Object> followPath(ValueSource source, EObject self, List<EStructuralFeature> path) {
		Optional<CollectionFilter> filter = CollectionFilter.parse(source.getCollectionFilter());
		int index = source.getCollectionIndex();
		Object current = self;
		for (EStructuralFeature feature : path) {
			if (!(current instanceof EObject eObject) || !eObject.eClass().getEAllStructuralFeatures().contains(feature)) {
				return Optional.empty();
			}
			current = eObject.eGet(feature);
			if (current instanceof List<?> list) {
				if (list.isEmpty()) {
					return Optional.empty();
				}
				if (filter.isPresent()) {
					Optional<Object> selected = filter.get().select(list);
					if (selected.isEmpty()) {
						return Optional.empty();
					}
					current = selected.get();
				} else {
					if (index < 0 || index >= list.size()) {
						return Optional.empty();
					}
					current = list.get(index);
				}
			}
		}
		return Optional.ofNullable(current);
	}

	private static boolean narrows(ValueSource source) {
		return source.getCollectionIndex() != 0 || isSet(source.getCollectionFilter());
	}

	private static EClassifier targetType(Slot slot) {
		EStructuralFeature feature = slot.getTargetFeature();
		return feature == null ? null : feature.getEType();
	}

	private static Class<?> boxed(Class<?> type) {
		if (type == null || !type.isPrimitive()) {
			return type;
		}
		if (type == int.class) {
			return Integer.class;
		}
		if (type == long.class) {
			return Long.class;
		}
		if (type == double.class) {
			return Double.class;
		}
		if (type == float.class) {
			return Float.class;
		}
		if (type == boolean.class) {
			return Boolean.class;
		}
		if (type == short.class) {
			return Short.class;
		}
		if (type == byte.class) {
			return Byte.class;
		}
		return Character.class;
	}

	private static boolean isSet(String text) {
		return text != null && !text.isBlank();
	}
}
