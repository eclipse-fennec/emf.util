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
package lorawan.impl;

import lorawan.LorawanPackage;
import lorawan.Metadata;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Metadata</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link lorawan.impl.MetadataImpl#getRegion_name <em>Region name</em>}</li>
 *   <li>{@link lorawan.impl.MetadataImpl#getRegion_common_name <em>Region common name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MetadataImpl extends MinimalEObjectImpl.Container implements Metadata {
	/**
	 * The default value of the '{@link #getRegion_name() <em>Region name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRegion_name()
	 * @generated
	 * @ordered
	 */
	protected static final String REGION_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRegion_name() <em>Region name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRegion_name()
	 * @generated
	 * @ordered
	 */
	protected String region_name = REGION_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getRegion_common_name() <em>Region common name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRegion_common_name()
	 * @generated
	 * @ordered
	 */
	protected static final String REGION_COMMON_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRegion_common_name() <em>Region common name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRegion_common_name()
	 * @generated
	 * @ordered
	 */
	protected String region_common_name = REGION_COMMON_NAME_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MetadataImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LorawanPackage.Literals.METADATA;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRegion_name() {
		return region_name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRegion_name(String newRegion_name) {
		String oldRegion_name = region_name;
		region_name = newRegion_name;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.METADATA__REGION_NAME, oldRegion_name, region_name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRegion_common_name() {
		return region_common_name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRegion_common_name(String newRegion_common_name) {
		String oldRegion_common_name = region_common_name;
		region_common_name = newRegion_common_name;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.METADATA__REGION_COMMON_NAME, oldRegion_common_name, region_common_name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LorawanPackage.METADATA__REGION_NAME:
				return getRegion_name();
			case LorawanPackage.METADATA__REGION_COMMON_NAME:
				return getRegion_common_name();
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
			case LorawanPackage.METADATA__REGION_NAME:
				setRegion_name((String)newValue);
				return;
			case LorawanPackage.METADATA__REGION_COMMON_NAME:
				setRegion_common_name((String)newValue);
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
			case LorawanPackage.METADATA__REGION_NAME:
				setRegion_name(REGION_NAME_EDEFAULT);
				return;
			case LorawanPackage.METADATA__REGION_COMMON_NAME:
				setRegion_common_name(REGION_COMMON_NAME_EDEFAULT);
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
			case LorawanPackage.METADATA__REGION_NAME:
				return REGION_NAME_EDEFAULT == null ? region_name != null : !REGION_NAME_EDEFAULT.equals(region_name);
			case LorawanPackage.METADATA__REGION_COMMON_NAME:
				return REGION_COMMON_NAME_EDEFAULT == null ? region_common_name != null : !REGION_COMMON_NAME_EDEFAULT.equals(region_common_name);
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
		result.append(" (region_name: ");
		result.append(region_name);
		result.append(", region_common_name: ");
		result.append(region_common_name);
		result.append(')');
		return result.toString();
	}

} //MetadataImpl
