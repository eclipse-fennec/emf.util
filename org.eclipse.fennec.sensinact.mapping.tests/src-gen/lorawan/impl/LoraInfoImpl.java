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

import lorawan.LoraInfo;
import lorawan.LorawanPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Lora Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link lorawan.impl.LoraInfoImpl#getBandwidth <em>Bandwidth</em>}</li>
 *   <li>{@link lorawan.impl.LoraInfoImpl#getSpreadingFactor <em>Spreading Factor</em>}</li>
 *   <li>{@link lorawan.impl.LoraInfoImpl#getCodeRate <em>Code Rate</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LoraInfoImpl extends MinimalEObjectImpl.Container implements LoraInfo {
	/**
	 * The default value of the '{@link #getBandwidth() <em>Bandwidth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBandwidth()
	 * @generated
	 * @ordered
	 */
	protected static final int BANDWIDTH_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getBandwidth() <em>Bandwidth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBandwidth()
	 * @generated
	 * @ordered
	 */
	protected int bandwidth = BANDWIDTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getSpreadingFactor() <em>Spreading Factor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpreadingFactor()
	 * @generated
	 * @ordered
	 */
	protected static final int SPREADING_FACTOR_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getSpreadingFactor() <em>Spreading Factor</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpreadingFactor()
	 * @generated
	 * @ordered
	 */
	protected int spreadingFactor = SPREADING_FACTOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getCodeRate() <em>Code Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCodeRate()
	 * @generated
	 * @ordered
	 */
	protected static final String CODE_RATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCodeRate() <em>Code Rate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCodeRate()
	 * @generated
	 * @ordered
	 */
	protected String codeRate = CODE_RATE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LoraInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LorawanPackage.Literals.LORA_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getBandwidth() {
		return bandwidth;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBandwidth(int newBandwidth) {
		int oldBandwidth = bandwidth;
		bandwidth = newBandwidth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.LORA_INFO__BANDWIDTH, oldBandwidth, bandwidth));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getSpreadingFactor() {
		return spreadingFactor;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSpreadingFactor(int newSpreadingFactor) {
		int oldSpreadingFactor = spreadingFactor;
		spreadingFactor = newSpreadingFactor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.LORA_INFO__SPREADING_FACTOR, oldSpreadingFactor, spreadingFactor));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getCodeRate() {
		return codeRate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCodeRate(String newCodeRate) {
		String oldCodeRate = codeRate;
		codeRate = newCodeRate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.LORA_INFO__CODE_RATE, oldCodeRate, codeRate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LorawanPackage.LORA_INFO__BANDWIDTH:
				return getBandwidth();
			case LorawanPackage.LORA_INFO__SPREADING_FACTOR:
				return getSpreadingFactor();
			case LorawanPackage.LORA_INFO__CODE_RATE:
				return getCodeRate();
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
			case LorawanPackage.LORA_INFO__BANDWIDTH:
				setBandwidth((Integer)newValue);
				return;
			case LorawanPackage.LORA_INFO__SPREADING_FACTOR:
				setSpreadingFactor((Integer)newValue);
				return;
			case LorawanPackage.LORA_INFO__CODE_RATE:
				setCodeRate((String)newValue);
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
			case LorawanPackage.LORA_INFO__BANDWIDTH:
				setBandwidth(BANDWIDTH_EDEFAULT);
				return;
			case LorawanPackage.LORA_INFO__SPREADING_FACTOR:
				setSpreadingFactor(SPREADING_FACTOR_EDEFAULT);
				return;
			case LorawanPackage.LORA_INFO__CODE_RATE:
				setCodeRate(CODE_RATE_EDEFAULT);
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
			case LorawanPackage.LORA_INFO__BANDWIDTH:
				return bandwidth != BANDWIDTH_EDEFAULT;
			case LorawanPackage.LORA_INFO__SPREADING_FACTOR:
				return spreadingFactor != SPREADING_FACTOR_EDEFAULT;
			case LorawanPackage.LORA_INFO__CODE_RATE:
				return CODE_RATE_EDEFAULT == null ? codeRate != null : !CODE_RATE_EDEFAULT.equals(codeRate);
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
		result.append(" (bandwidth: ");
		result.append(bandwidth);
		result.append(", spreadingFactor: ");
		result.append(spreadingFactor);
		result.append(", codeRate: ");
		result.append(codeRate);
		result.append(')');
		return result.toString();
	}

} //LoraInfoImpl
