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

import org.eclipse.fennec.sensinact.model.mapping.DeletionRule;
import org.eclipse.fennec.sensinact.model.mapping.DurationUnit;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Deletion Rule</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl#getRetention <em>Retention</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl#getRetentionUnit <em>Retention Unit</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl#getCleanupInterval <em>Cleanup Interval</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl#getCleanupIntervalUnit <em>Cleanup Interval Unit</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl#getMaxCount <em>Max Count</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DeletionRuleImpl extends PersistenceRuleImpl implements DeletionRule {
	/**
	 * The default value of the '{@link #getRetention() <em>Retention</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetention()
	 * @generated
	 * @ordered
	 */
	protected static final Integer RETENTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRetention() <em>Retention</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetention()
	 * @generated
	 * @ordered
	 */
	protected Integer retention = RETENTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getRetentionUnit() <em>Retention Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetentionUnit()
	 * @generated
	 * @ordered
	 */
	protected static final DurationUnit RETENTION_UNIT_EDEFAULT = DurationUnit.MILLISECONDS;

	/**
	 * The cached value of the '{@link #getRetentionUnit() <em>Retention Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRetentionUnit()
	 * @generated
	 * @ordered
	 */
	protected DurationUnit retentionUnit = RETENTION_UNIT_EDEFAULT;

	/**
	 * The default value of the '{@link #getCleanupInterval() <em>Cleanup Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCleanupInterval()
	 * @generated
	 * @ordered
	 */
	protected static final Integer CLEANUP_INTERVAL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCleanupInterval() <em>Cleanup Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCleanupInterval()
	 * @generated
	 * @ordered
	 */
	protected Integer cleanupInterval = CLEANUP_INTERVAL_EDEFAULT;

	/**
	 * The default value of the '{@link #getCleanupIntervalUnit() <em>Cleanup Interval Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCleanupIntervalUnit()
	 * @generated
	 * @ordered
	 */
	protected static final DurationUnit CLEANUP_INTERVAL_UNIT_EDEFAULT = DurationUnit.MILLISECONDS;

	/**
	 * The cached value of the '{@link #getCleanupIntervalUnit() <em>Cleanup Interval Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCleanupIntervalUnit()
	 * @generated
	 * @ordered
	 */
	protected DurationUnit cleanupIntervalUnit = CLEANUP_INTERVAL_UNIT_EDEFAULT;

	/**
	 * The default value of the '{@link #getMaxCount() <em>Max Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxCount()
	 * @generated
	 * @ordered
	 */
	protected static final Integer MAX_COUNT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMaxCount() <em>Max Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMaxCount()
	 * @generated
	 * @ordered
	 */
	protected Integer maxCount = MAX_COUNT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeletionRuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MappingPackage.Literals.DELETION_RULE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getRetention() {
		return retention;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRetention(Integer newRetention) {
		Integer oldRetention = retention;
		retention = newRetention;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.DELETION_RULE__RETENTION, oldRetention, retention));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DurationUnit getRetentionUnit() {
		return retentionUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRetentionUnit(DurationUnit newRetentionUnit) {
		DurationUnit oldRetentionUnit = retentionUnit;
		retentionUnit = newRetentionUnit == null ? RETENTION_UNIT_EDEFAULT : newRetentionUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.DELETION_RULE__RETENTION_UNIT, oldRetentionUnit, retentionUnit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getCleanupInterval() {
		return cleanupInterval;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCleanupInterval(Integer newCleanupInterval) {
		Integer oldCleanupInterval = cleanupInterval;
		cleanupInterval = newCleanupInterval;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.DELETION_RULE__CLEANUP_INTERVAL, oldCleanupInterval, cleanupInterval));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DurationUnit getCleanupIntervalUnit() {
		return cleanupIntervalUnit;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setCleanupIntervalUnit(DurationUnit newCleanupIntervalUnit) {
		DurationUnit oldCleanupIntervalUnit = cleanupIntervalUnit;
		cleanupIntervalUnit = newCleanupIntervalUnit == null ? CLEANUP_INTERVAL_UNIT_EDEFAULT : newCleanupIntervalUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.DELETION_RULE__CLEANUP_INTERVAL_UNIT, oldCleanupIntervalUnit, cleanupIntervalUnit));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Integer getMaxCount() {
		return maxCount;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMaxCount(Integer newMaxCount) {
		Integer oldMaxCount = maxCount;
		maxCount = newMaxCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MappingPackage.DELETION_RULE__MAX_COUNT, oldMaxCount, maxCount));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MappingPackage.DELETION_RULE__RETENTION:
				return getRetention();
			case MappingPackage.DELETION_RULE__RETENTION_UNIT:
				return getRetentionUnit();
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL:
				return getCleanupInterval();
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL_UNIT:
				return getCleanupIntervalUnit();
			case MappingPackage.DELETION_RULE__MAX_COUNT:
				return getMaxCount();
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
			case MappingPackage.DELETION_RULE__RETENTION:
				setRetention((Integer)newValue);
				return;
			case MappingPackage.DELETION_RULE__RETENTION_UNIT:
				setRetentionUnit((DurationUnit)newValue);
				return;
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL:
				setCleanupInterval((Integer)newValue);
				return;
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL_UNIT:
				setCleanupIntervalUnit((DurationUnit)newValue);
				return;
			case MappingPackage.DELETION_RULE__MAX_COUNT:
				setMaxCount((Integer)newValue);
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
			case MappingPackage.DELETION_RULE__RETENTION:
				setRetention(RETENTION_EDEFAULT);
				return;
			case MappingPackage.DELETION_RULE__RETENTION_UNIT:
				setRetentionUnit(RETENTION_UNIT_EDEFAULT);
				return;
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL:
				setCleanupInterval(CLEANUP_INTERVAL_EDEFAULT);
				return;
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL_UNIT:
				setCleanupIntervalUnit(CLEANUP_INTERVAL_UNIT_EDEFAULT);
				return;
			case MappingPackage.DELETION_RULE__MAX_COUNT:
				setMaxCount(MAX_COUNT_EDEFAULT);
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
			case MappingPackage.DELETION_RULE__RETENTION:
				return RETENTION_EDEFAULT == null ? retention != null : !RETENTION_EDEFAULT.equals(retention);
			case MappingPackage.DELETION_RULE__RETENTION_UNIT:
				return retentionUnit != RETENTION_UNIT_EDEFAULT;
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL:
				return CLEANUP_INTERVAL_EDEFAULT == null ? cleanupInterval != null : !CLEANUP_INTERVAL_EDEFAULT.equals(cleanupInterval);
			case MappingPackage.DELETION_RULE__CLEANUP_INTERVAL_UNIT:
				return cleanupIntervalUnit != CLEANUP_INTERVAL_UNIT_EDEFAULT;
			case MappingPackage.DELETION_RULE__MAX_COUNT:
				return MAX_COUNT_EDEFAULT == null ? maxCount != null : !MAX_COUNT_EDEFAULT.equals(maxCount);
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
		result.append(" (retention: ");
		result.append(retention);
		result.append(", retentionUnit: ");
		result.append(retentionUnit);
		result.append(", cleanupInterval: ");
		result.append(cleanupInterval);
		result.append(", cleanupIntervalUnit: ");
		result.append(cleanupIntervalUnit);
		result.append(", maxCount: ");
		result.append(maxCount);
		result.append(')');
		return result.toString();
	}

} //DeletionRuleImpl
