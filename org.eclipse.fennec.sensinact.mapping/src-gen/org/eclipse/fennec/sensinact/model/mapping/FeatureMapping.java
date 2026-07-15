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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Feature Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getFunctionId <em>Function Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getFeaturePath <em>Feature Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionIndex <em>Collection Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionFilter <em>Collection Filter</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getFeatureMapping()
 * @model
 * @generated
 */
@ProviderType
public interface FeatureMapping extends EObject {
	/**
	 * Returns the value of the '<em><b>Function Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Function Id</em>' attribute.
	 * @see #setFunctionId(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getFeatureMapping_FunctionId()
	 * @model
	 * @generated
	 */
	String getFunctionId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getFunctionId <em>Function Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Function Id</em>' attribute.
	 * @see #getFunctionId()
	 * @generated
	 */
	void setFunctionId(String value);

	/**
	 * Returns the value of the '<em><b>Feature Path</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Feature Path</em>' reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getFeatureMapping_FeaturePath()
	 * @model
	 * @generated
	 */
	EList<EStructuralFeature> getFeaturePath();

	/**
	 * Returns the value of the '<em><b>Collection Index</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * When a feature in the featurePath is a collection, specifies which element index to use during path traversal. Default is 0 (first element).
	 * This applies to the first collection encountered during feature path traversal.
	 * 
	 * Example: For a path WeatherReports.reports.weatherStation.id where 'reports' is a collection, setting collectionIndex=0 will navigate to reports[0].weatherStation.id, while collectionIndex=1 will navigate to reports[1].weatherStation.id.
	 * 
	 * This attribute is inherited by NameMapping, TimestampMapping, and ValueMapping, allowing consistent collection navigation across all feature path uses.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Collection Index</em>' attribute.
	 * @see #setCollectionIndex(int)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getFeatureMapping_CollectionIndex()
	 * @model default="0"
	 * @generated
	 */
	int getCollectionIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionIndex <em>Collection Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Collection Index</em>' attribute.
	 * @see #getCollectionIndex()
	 * @generated
	 */
	void setCollectionIndex(int value);

	/**
	 * Returns the value of the '<em><b>Collection Filter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional filter expression to select a specific element from the collection instead of using a fixed index. The expression syntax and evaluation mechanism are implementation-specific.
	 * If both collectionIndex and collectionFilter are set, collectionFilter takes precedence.
	 * This allows for more dynamic selection criteria, such as 'select the element with the earliest timestamp' or 'select the element where type=CURRENT'.
	 * This attribute is ignored if collectionFeature is not set.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Collection Filter</em>' attribute.
	 * @see #setCollectionFilter(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getFeatureMapping_CollectionFilter()
	 * @model
	 * @generated
	 */
	String getCollectionFilter();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionFilter <em>Collection Filter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Collection Filter</em>' attribute.
	 * @see #getCollectionFilter()
	 * @generated
	 */
	void setCollectionFilter(String value);

} // FeatureMapping
