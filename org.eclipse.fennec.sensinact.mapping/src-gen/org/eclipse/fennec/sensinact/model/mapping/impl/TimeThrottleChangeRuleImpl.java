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

import org.eclipse.fennec.sensinact.model.mapping.DurationUnit;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Time Throttle Change Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.TimeThrottleChangeRuleImpl#getInterval <em>Interval</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.TimeThrottleChangeRuleImpl#getIntervalUnit <em>Interval Unit</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TimeThrottleChangeRuleImpl extends ChangeRuleImpl implements TimeThrottleChangeRule {
	/**
	 * The default value of the '{@link #getInterval() <em>Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterval()
	 * @generated
	 * @ordered
	 */
	protected static final Integer INTERVAL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInterval() <em>Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterval()
	 * @generated
	 * @ordered
	 */
	protected Integer interval = INTERVAL_EDEFAULT;

	/**
	 * The default value of the '{@link #getIntervalUnit() <em>Interval Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIntervalUnit()
	 * @generated
	 * @ordered
	 */
	protected static final DurationUnit INTERVAL_UNIT_EDEFAULT = DurationUnit.MILLISECONDS;

	/**
	 * The cached value of the '{@link #getIntervalUnit() <em>Interval Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIntervalUnit()
	 * @generated
	 * @ordered
	 */
	protected DurationUnit intervalUnit = INTERVAL_UNIT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected TimeThrottleChangeRuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.TIME_THROTTLE_CHANGE_RULE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getInterval() {
		return interval;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setInterval(Integer newInterval) {
		Integer oldInterval = interval;
		interval = newInterval;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL, oldInterval, interval));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DurationUnit getIntervalUnit() {
		return intervalUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setIntervalUnit(DurationUnit newIntervalUnit) {
		DurationUnit oldIntervalUnit = intervalUnit;
		intervalUnit = newIntervalUnit == null ? INTERVAL_UNIT_EDEFAULT : newIntervalUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT, oldIntervalUnit, intervalUnit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL:
				return getInterval();
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT:
				return getIntervalUnit();
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
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL:
				setInterval((Integer)newValue);
				return;
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT:
				setIntervalUnit((DurationUnit)newValue);
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
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL:
				setInterval(INTERVAL_EDEFAULT);
				return;
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT:
				setIntervalUnit(INTERVAL_UNIT_EDEFAULT);
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
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL:
				return INTERVAL_EDEFAULT == null ? interval != null : !INTERVAL_EDEFAULT.equals(interval);
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT:
				return intervalUnit != INTERVAL_UNIT_EDEFAULT;
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
		result.append(" (interval: ");
		result.append(interval);
		result.append(", intervalUnit: ");
		result.append(intervalUnit);
		result.append(')');
		return result.toString();
	}

} //TimeThrottleChangeRuleImpl
