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

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.ProfileResource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Profile Resource</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl#getResourceId <em>Resource Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl#getResourceName <em>Resource Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl#getExpectedType <em>Expected Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl#getExpectedUnit <em>Expected Unit</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProfileResourceImpl extends MinimalEObjectImpl.Container implements ProfileResource {
	/**
	 * The default value of the '{@link #getResourceId() <em>Resource Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourceId()
	 * @generated
	 * @ordered
	 */
	protected static final String RESOURCE_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getResourceId() <em>Resource Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourceId()
	 * @generated
	 * @ordered
	 */
	protected String resourceId = RESOURCE_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getResourceName() <em>Resource Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourceName()
	 * @generated
	 * @ordered
	 */
	protected static final String RESOURCE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getResourceName() <em>Resource Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getResourceName()
	 * @generated
	 * @ordered
	 */
	protected String resourceName = RESOURCE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #isRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequired()
	 * @generated
	 * @ordered
	 */
	protected static final boolean REQUIRED_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isRequired() <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequired()
	 * @generated
	 * @ordered
	 */
	protected boolean required = REQUIRED_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExpectedType() <em>Expected Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedType()
	 * @generated
	 * @ordered
	 */
	protected EDataType expectedType;

	/**
	 * The default value of the '{@link #getExpectedUnit() <em>Expected Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedUnit()
	 * @generated
	 * @ordered
	 */
	protected static final String EXPECTED_UNIT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExpectedUnit() <em>Expected Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpectedUnit()
	 * @generated
	 * @ordered
	 */
	protected String expectedUnit = EXPECTED_UNIT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProfileResourceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.PROFILE_RESOURCE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResourceId(String newResourceId) {
		String oldResourceId = resourceId;
		resourceId = newResourceId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROFILE_RESOURCE__RESOURCE_ID, oldResourceId, resourceId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getResourceName() {
		return resourceName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setResourceName(String newResourceName) {
		String oldResourceName = resourceName;
		resourceName = newResourceName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROFILE_RESOURCE__RESOURCE_NAME, oldResourceName, resourceName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRequired(boolean newRequired) {
		boolean oldRequired = required;
		required = newRequired;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROFILE_RESOURCE__REQUIRED, oldRequired, required));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getExpectedType() {
		if (expectedType != null && expectedType.eIsProxy()) {
			InternalEObject oldExpectedType = (InternalEObject)expectedType;
			expectedType = (EDataType)eResolveProxy(oldExpectedType);
			if (expectedType != oldExpectedType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, MappingPackage.PROFILE_RESOURCE__EXPECTED_TYPE, oldExpectedType, expectedType));
			}
		}
		return expectedType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType basicGetExpectedType() {
		return expectedType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpectedType(EDataType newExpectedType) {
		EDataType oldExpectedType = expectedType;
		expectedType = newExpectedType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROFILE_RESOURCE__EXPECTED_TYPE, oldExpectedType, expectedType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getExpectedUnit() {
		return expectedUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setExpectedUnit(String newExpectedUnit) {
		String oldExpectedUnit = expectedUnit;
		expectedUnit = newExpectedUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROFILE_RESOURCE__EXPECTED_UNIT, oldExpectedUnit, expectedUnit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_ID:
				return getResourceId();
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_NAME:
				return getResourceName();
			case MappingPackage.PROFILE_RESOURCE__REQUIRED:
				return isRequired();
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_TYPE:
				if (resolve) return getExpectedType();
				return basicGetExpectedType();
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_UNIT:
				return getExpectedUnit();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_ID:
				setResourceId((String)newValue);
				return;
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_NAME:
				setResourceName((String)newValue);
				return;
			case MappingPackage.PROFILE_RESOURCE__REQUIRED:
				setRequired((Boolean)newValue);
				return;
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_TYPE:
				setExpectedType((EDataType)newValue);
				return;
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_UNIT:
				setExpectedUnit((String)newValue);
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
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_ID:
				setResourceId(RESOURCE_ID_EDEFAULT);
				return;
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_NAME:
				setResourceName(RESOURCE_NAME_EDEFAULT);
				return;
			case MappingPackage.PROFILE_RESOURCE__REQUIRED:
				setRequired(REQUIRED_EDEFAULT);
				return;
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_TYPE:
				setExpectedType((EDataType)null);
				return;
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_UNIT:
				setExpectedUnit(EXPECTED_UNIT_EDEFAULT);
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
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_ID:
				return RESOURCE_ID_EDEFAULT == null ? resourceId != null : !RESOURCE_ID_EDEFAULT.equals(resourceId);
			case MappingPackage.PROFILE_RESOURCE__RESOURCE_NAME:
				return RESOURCE_NAME_EDEFAULT == null ? resourceName != null : !RESOURCE_NAME_EDEFAULT.equals(resourceName);
			case MappingPackage.PROFILE_RESOURCE__REQUIRED:
				return required != REQUIRED_EDEFAULT;
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_TYPE:
				return expectedType != null;
			case MappingPackage.PROFILE_RESOURCE__EXPECTED_UNIT:
				return EXPECTED_UNIT_EDEFAULT == null ? expectedUnit != null : !EXPECTED_UNIT_EDEFAULT.equals(expectedUnit);
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
		result.append(" (resourceId: ");
		result.append(resourceId);
		result.append(", resourceName: ");
		result.append(resourceName);
		result.append(", required: ");
		result.append(required);
		result.append(", expectedUnit: ");
		result.append(expectedUnit);
		result.append(')');
		return result.toString();
	}

} //ProfileResourceImpl
