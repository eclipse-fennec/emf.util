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

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Profile Admin</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileAdminImpl#isRequiresLocation <em>Requires Location</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileAdminImpl#isRequiresFriendlyName <em>Requires Friendly Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ProfileAdminImpl extends ProfileServiceImpl implements ProfileAdmin {
	/**
	 * The default value of the '{@link #isRequiresLocation() <em>Requires Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequiresLocation()
	 * @generated
	 * @ordered
	 */
	protected static final boolean REQUIRES_LOCATION_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRequiresLocation() <em>Requires Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequiresLocation()
	 * @generated
	 * @ordered
	 */
	protected boolean requiresLocation = REQUIRES_LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #isRequiresFriendlyName() <em>Requires Friendly Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequiresFriendlyName()
	 * @generated
	 * @ordered
	 */
	protected static final boolean REQUIRES_FRIENDLY_NAME_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isRequiresFriendlyName() <em>Requires Friendly Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isRequiresFriendlyName()
	 * @generated
	 * @ordered
	 */
	protected boolean requiresFriendlyName = REQUIRES_FRIENDLY_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ProfileAdminImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.PROFILE_ADMIN;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isRequiresLocation() {
		return requiresLocation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRequiresLocation(boolean newRequiresLocation) {
		boolean oldRequiresLocation = requiresLocation;
		requiresLocation = newRequiresLocation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROFILE_ADMIN__REQUIRES_LOCATION, oldRequiresLocation, requiresLocation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isRequiresFriendlyName() {
		return requiresFriendlyName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRequiresFriendlyName(boolean newRequiresFriendlyName) {
		boolean oldRequiresFriendlyName = requiresFriendlyName;
		requiresFriendlyName = newRequiresFriendlyName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME, oldRequiresFriendlyName, requiresFriendlyName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MappingPackage.PROFILE_ADMIN__REQUIRES_LOCATION:
				return isRequiresLocation();
			case MappingPackage.PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME:
				return isRequiresFriendlyName();
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
			case MappingPackage.PROFILE_ADMIN__REQUIRES_LOCATION:
				setRequiresLocation((Boolean)newValue);
				return;
			case MappingPackage.PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME:
				setRequiresFriendlyName((Boolean)newValue);
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
			case MappingPackage.PROFILE_ADMIN__REQUIRES_LOCATION:
				setRequiresLocation(REQUIRES_LOCATION_EDEFAULT);
				return;
			case MappingPackage.PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME:
				setRequiresFriendlyName(REQUIRES_FRIENDLY_NAME_EDEFAULT);
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
			case MappingPackage.PROFILE_ADMIN__REQUIRES_LOCATION:
				return requiresLocation != REQUIRES_LOCATION_EDEFAULT;
			case MappingPackage.PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME:
				return requiresFriendlyName != REQUIRES_FRIENDLY_NAME_EDEFAULT;
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
		result.append(" (requiresLocation: ");
		result.append(requiresLocation);
		result.append(", requiresFriendlyName: ");
		result.append(requiresFriendlyName);
		result.append(')');
		return result.toString();
	}

} //ProfileAdminImpl
