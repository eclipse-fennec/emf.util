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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.fennec.mapping.impl.CollectionFilter;
import org.eclipse.fennec.model.mapping.IdentityResolution;
import org.eclipse.fennec.model.mapping.Mapping;
import org.eclipse.fennec.model.mapping.Slot;
import org.eclipse.fennec.model.mapping.ValueSource;

/**
 * Checks a mapping when it is loaded, so that a mistake is a load error and not a value that
 * silently never arrives:
 * <ul>
 * <li>a slot has a target feature of the target class, or - without one - a name;</li>
 * <li>no two slots fill the same target feature (one source per slot);</li>
 * <li>a slot has a value source; a value source declares a producer or a constant;</li>
 * <li>a feature path is a chain the metamodel allows: each feature belongs to the class reached so
 * far, every feature but the last is a reference;</li>
 * <li>a collection filter is well-formed; function and converter ids are known when registries are given;</li>
 * <li>at most one identity resolution per (source class, target class), each with a source class.</li>
 * </ul>
 * OCL expressions are not parsed here; the expression language checks them where it is deployed.
 */
public final class MappingValidator {

	/** The diagnostic source of every diagnostic this validator produces. */
	public static final String DIAGNOSTIC_SOURCE = "org.eclipse.fennec.mapping";

	private final FunctionRegistry functions;
	private final ConverterRegistry converters;

	/**
	 * A validator that checks function and converter ids against the given registries.
	 *
	 * @param functions the functions, {@code null} to skip that check
	 * @param converters the converters, {@code null} to skip that check
	 */
	public MappingValidator(FunctionRegistry functions, ConverterRegistry converters) {
		this.functions = functions;
		this.converters = converters;
	}

	/**
	 * A validator that does not check function and converter ids.
	 */
	public MappingValidator() {
		this(null, null);
	}

	/**
	 * @param mapping the mapping
	 * @return an OK diagnostic, or one with a child per problem (ERROR, or WARNING for unknown ids)
	 */
	public Diagnostic validate(Mapping mapping) {
		requireNonNull(mapping, "Mapping is required");
		BasicDiagnostic result = new BasicDiagnostic(DIAGNOSTIC_SOURCE, 0, "Mapping " + mapping.getId(),
				new Object[] { mapping });
		validateSlots(result, mapping);
		validateIdentities(result, mapping);
		return result;
	}

	/**
	 * @param mapping the mapping
	 * @return whether it has no error (warnings allowed)
	 */
	public boolean isValid(Mapping mapping) {
		return validate(mapping).getSeverity() < Diagnostic.ERROR;
	}

	private void validateSlots(BasicDiagnostic result, Mapping mapping) {
		EClass target = mapping.getTarget();
		Set<EStructuralFeature> filled = new HashSet<>();
		List<Slot> slots = mapping.getSlots();
		for (int i = 0; i < slots.size(); i++) {
			Slot slot = slots.get(i);
			String where = "slot " + i + " (" + label(slot) + ")";
			EStructuralFeature feature = slot.getTargetFeature();
			if (feature == null) {
				if (slot.getName() == null || slot.getName().isBlank()) {
					error(result, slot, where + ": needs a target feature or a name");
				}
			} else {
				if (target == null || !target.getEAllStructuralFeatures().contains(feature)) {
					error(result, slot, where + ": target feature '" + feature.getName() + "' is no feature of the target class "
							+ (target == null ? "(none)" : target.getName()));
				}
				if (!filled.add(feature)) {
					error(result, slot, where + ": target feature '" + feature.getName() + "' is already filled by another slot");
				}
			}
			EClass source = Mappings.sourceClassOf(mapping, slot);
			if (source == null) {
				error(result, slot, where + ": needs a source class, the mapping has no target to read from");
			}
			if (slot.getValue() == null) {
				error(result, slot, where + ": needs a value source");
			} else {
				validateValueSource(result, slot.getValue(), source, where);
			}
			warnUnknownConverter(result, slot, slot.getTargetConverterId(), where + ", target converter");
		}
	}

	private void validateIdentities(BasicDiagnostic result, Mapping mapping) {
		Set<String> pairs = new HashSet<>();
		List<IdentityResolution> identities = mapping.getIdentities();
		for (int i = 0; i < identities.size(); i++) {
			IdentityResolution identity = identities.get(i);
			EClass source = identity.getSourceClass();
			String where = "identity " + i + " (" + (source == null ? "?" : source.getName()) + ")";
			if (source == null) {
				error(result, identity, where + ": needs a source class");
				continue;
			}
			EClass target = Mappings.targetClassOf(identity, mapping);
			String pair = Mappings.uriOf(source) + "->" + (target == null ? "" : Mappings.uriOf(target));
			if (!pairs.add(pair)) {
				error(result, identity, where + ": a second identity resolution for the same source and target class");
			}
			if (identity.getResolver() != null) {
				validateValueSource(result, identity.getResolver(), source, where + ", resolver");
			}
		}
	}

	private void validateValueSource(BasicDiagnostic result, ValueSource value, EClass start, String where) {
		boolean producer = isSet(value.getExpression()) || isSet(value.getFunctionId()) || !value.getFeaturePath().isEmpty();
		if (!producer && (value.getConstant() == null || value.getConstant().isEmpty())) {
			error(result, value, where + ": the value source declares neither a producer nor a constant");
		}
		EClass current = start;
		List<EStructuralFeature> path = value.getFeaturePath();
		for (int i = 0; i < path.size() && current != null; i++) {
			EStructuralFeature feature = path.get(i);
			if (!current.getEAllStructuralFeatures().contains(feature)) {
				error(result, value, where + ": path step " + i + " '" + (feature == null ? null : feature.getName())
						+ "' is no feature of " + current.getName());
				break;
			}
			boolean last = i == path.size() - 1;
			if (!last) {
				if (!(feature instanceof EReference reference)) {
					error(result, value, where + ": path step " + i + " '" + feature.getName()
							+ "' is an attribute but the path goes on");
					break;
				}
				current = reference.getEReferenceType();
			}
		}
		if (!CollectionFilter.isWellFormed(value.getCollectionFilter())) {
			error(result, value, where + ": collection filter '" + value.getCollectionFilter()
					+ "' is malformed, expected path.to.feature=value");
		}
		if (functions != null && isSet(value.getFunctionId()) && !functions.contains(value.getFunctionId())) {
			warning(result, value, where + ": no function registered for id '" + value.getFunctionId() + "'");
		}
		warnUnknownConverter(result, value, value.getConverterId(), where);
	}

	private void warnUnknownConverter(BasicDiagnostic result, Object subject, String id, String where) {
		if (converters != null && isSet(id) && !converters.contains(id)) {
			warning(result, subject, where + ": no converter registered for id '" + id + "'");
		}
	}

	private static String label(Slot slot) {
		if (slot.getTargetFeature() != null) {
			return slot.getTargetFeature().getName();
		}
		return slot.getName() == null ? "?" : slot.getName();
	}

	private static void error(BasicDiagnostic result, Object subject, String message) {
		result.add(new BasicDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0, message, new Object[] { subject }));
	}

	private static void warning(BasicDiagnostic result, Object subject, String message) {
		result.add(new BasicDiagnostic(Diagnostic.WARNING, DIAGNOSTIC_SOURCE, 0, message, new Object[] { subject }));
	}

	private static boolean isSet(String text) {
		return text != null && !text.isBlank();
	}
}
