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
package org.eclipse.fennec.sensinact.model.mapping.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.fennec.sensinact.model.mapping.FeatureMapping;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Feature Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl#getFunctionId <em>Function Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl#getFeaturePath <em>Feature Path</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl#getCollectionIndex <em>Collection Index</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl#getCollectionFilter <em>Collection Filter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FeatureMappingImpl extends MinimalEObjectImpl.Container implements FeatureMapping {
	/**
	 * The default value of the '{@link #getFunctionId() <em>Function Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFunctionId()
	 * @generated
	 * @ordered
	 */
	protected static final String FUNCTION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFunctionId() <em>Function Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFunctionId()
	 * @generated
	 * @ordered
	 */
	protected String functionId = FUNCTION_ID_EDEFAULT;

	/**
	 * The cached value of the '{@link #getFeaturePath() <em>Feature Path</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFeaturePath()
	 * @generated
	 * @ordered
	 */
	protected EList<EStructuralFeature> featurePath;

	/**
	 * The default value of the '{@link #getCollectionIndex() <em>Collection Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCollectionIndex()
	 * @generated
	 * @ordered
	 */
	protected static final int COLLECTION_INDEX_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getCollectionIndex() <em>Collection Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCollectionIndex()
	 * @generated
	 * @ordered
	 */
	protected int collectionIndex = COLLECTION_INDEX_EDEFAULT;

	/**
	 * The default value of the '{@link #getCollectionFilter() <em>Collection Filter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCollectionFilter()
	 * @generated
	 * @ordered
	 */
	protected static final String COLLECTION_FILTER_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCollectionFilter() <em>Collection Filter</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCollectionFilter()
	 * @generated
	 * @ordered
	 */
	protected String collectionFilter = COLLECTION_FILTER_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FeatureMappingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.FEATURE_MAPPING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getFunctionId() {
		return functionId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFunctionId(String newFunctionId) {
		String oldFunctionId = functionId;
		functionId = newFunctionId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.FEATURE_MAPPING__FUNCTION_ID, oldFunctionId, functionId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<EStructuralFeature> getFeaturePath() {
		if (featurePath == null) {
			featurePath = new EObjectResolvingEList<EStructuralFeature>(EStructuralFeature.class, this, MappingPackage.FEATURE_MAPPING__FEATURE_PATH);
		}
		return featurePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getCollectionIndex() {
		return collectionIndex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCollectionIndex(int newCollectionIndex) {
		int oldCollectionIndex = collectionIndex;
		collectionIndex = newCollectionIndex;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.FEATURE_MAPPING__COLLECTION_INDEX, oldCollectionIndex, collectionIndex));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCollectionFilter() {
		return collectionFilter;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCollectionFilter(String newCollectionFilter) {
		String oldCollectionFilter = collectionFilter;
		collectionFilter = newCollectionFilter;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.FEATURE_MAPPING__COLLECTION_FILTER, oldCollectionFilter, collectionFilter));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MappingPackage.FEATURE_MAPPING__FUNCTION_ID:
				return getFunctionId();
			case MappingPackage.FEATURE_MAPPING__FEATURE_PATH:
				return getFeaturePath();
			case MappingPackage.FEATURE_MAPPING__COLLECTION_INDEX:
				return getCollectionIndex();
			case MappingPackage.FEATURE_MAPPING__COLLECTION_FILTER:
				return getCollectionFilter();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MappingPackage.FEATURE_MAPPING__FUNCTION_ID:
				setFunctionId((String)newValue);
				return;
			case MappingPackage.FEATURE_MAPPING__FEATURE_PATH:
				getFeaturePath().clear();
				getFeaturePath().addAll((Collection<? extends EStructuralFeature>)newValue);
				return;
			case MappingPackage.FEATURE_MAPPING__COLLECTION_INDEX:
				setCollectionIndex((Integer)newValue);
				return;
			case MappingPackage.FEATURE_MAPPING__COLLECTION_FILTER:
				setCollectionFilter((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case MappingPackage.FEATURE_MAPPING__FUNCTION_ID:
				setFunctionId(FUNCTION_ID_EDEFAULT);
				return;
			case MappingPackage.FEATURE_MAPPING__FEATURE_PATH:
				getFeaturePath().clear();
				return;
			case MappingPackage.FEATURE_MAPPING__COLLECTION_INDEX:
				setCollectionIndex(COLLECTION_INDEX_EDEFAULT);
				return;
			case MappingPackage.FEATURE_MAPPING__COLLECTION_FILTER:
				setCollectionFilter(COLLECTION_FILTER_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MappingPackage.FEATURE_MAPPING__FUNCTION_ID:
				return FUNCTION_ID_EDEFAULT == null ? functionId != null : !FUNCTION_ID_EDEFAULT.equals(functionId);
			case MappingPackage.FEATURE_MAPPING__FEATURE_PATH:
				return featurePath != null && !featurePath.isEmpty();
			case MappingPackage.FEATURE_MAPPING__COLLECTION_INDEX:
				return collectionIndex != COLLECTION_INDEX_EDEFAULT;
			case MappingPackage.FEATURE_MAPPING__COLLECTION_FILTER:
				return COLLECTION_FILTER_EDEFAULT == null ? collectionFilter != null : !COLLECTION_FILTER_EDEFAULT.equals(collectionFilter);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (functionId: ");
		result.append(functionId);
		result.append(", collectionIndex: ");
		result.append(collectionIndex);
		result.append(", collectionFilter: ");
		result.append(collectionFilter);
		result.append(')');
		return result.toString();
	}

} //FeatureMappingImpl
