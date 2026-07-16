/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.sensinact.model.mapping;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Reference Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Defines a reference-based mapping that automatically generates resources from attributes of the referenced type.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getFilter <em>Filter</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#isExclude <em>Exclude</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getReferenceMappings <em>Reference Mappings</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getTargetEClass <em>Target EClass</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getReferenceMapping()
 * @model
 * @generated
 */
@ProviderType
public interface ReferenceMapping extends FeatureMapping {
	/**
	 * Returns the value of the '<em><b>Filter</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EAttribute}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Filter</em>' reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getReferenceMapping_Filter()
	 * @model
	 * @generated
	 */
	EList<EAttribute> getFilter();

	/**
	 * Returns the value of the '<em><b>Exclude</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude</em>' attribute.
	 * @see #setExclude(boolean)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getReferenceMapping_Exclude()
	 * @model default="true"
	 * @generated
	 */
	boolean isExclude();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#isExclude <em>Exclude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exclude</em>' attribute.
	 * @see #isExclude()
	 * @generated
	 */
	void setExclude(boolean value);

	/**
	 * Returns the value of the '<em><b>Reference Mappings</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reference Mappings</em>' containment reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getReferenceMapping_ReferenceMappings()
	 * @model containment="true"
	 * @generated
	 */
	EList<ReferenceMapping> getReferenceMappings();

	/**
	 * Returns the value of the '<em><b>Target EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional specification of the concrete EClass to use for generating resources.
	 * If not set, the declared type of the feature path's target will be used.
	 * This is useful when the feature has a base type but you know instances will be of a specific subtype.
	 * 
	 * Example: If 'reports' is declared as WeatherReport[] but you know instances are MOSMIXWeatherReport,
	 * set targetEClass to MOSMIXWeatherReport to generate resources from its attributes.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Target EClass</em>' reference.
	 * @see #setTargetEClass(EClass)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getReferenceMapping_TargetEClass()
	 * @model
	 * @generated
	 */
	EClass getTargetEClass();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getTargetEClass <em>Target EClass</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target EClass</em>' reference.
	 * @see #getTargetEClass()
	 * @generated
	 */
	void setTargetEClass(EClass value);

} // ReferenceMapping
