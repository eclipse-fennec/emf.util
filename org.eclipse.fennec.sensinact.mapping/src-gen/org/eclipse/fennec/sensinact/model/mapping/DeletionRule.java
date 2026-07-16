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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Deletion Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Controls purging of stored history data for a resource.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetention <em>Retention</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetentionUnit <em>Retention Unit</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupInterval <em>Cleanup Interval</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupIntervalUnit <em>Cleanup Interval Unit</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getMaxCount <em>Max Count</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getDeletionRule()
 * @model
 * @generated
 */
@ProviderType
public interface DeletionRule extends PersistenceRule {
	/**
	 * Returns the value of the '<em><b>Retention</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * History data older than this age ('retention' 'retentionUnit', e.g. 90 DAYS) is eligible
	 * for deletion.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Retention</em>' attribute.
	 * @see #setRetention(Integer)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getDeletionRule_Retention()
	 * @model required="true"
	 * @generated
	 */
	Integer getRetention();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetention <em>Retention</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Retention</em>' attribute.
	 * @see #getRetention()
	 * @generated
	 */
	void setRetention(Integer value);

	/**
	 * Returns the value of the '<em><b>Retention Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.sensinact.model.mapping.DurationUnit}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Retention Unit</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @see #setRetentionUnit(DurationUnit)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getDeletionRule_RetentionUnit()
	 * @model required="true"
	 * @generated
	 */
	DurationUnit getRetentionUnit();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetentionUnit <em>Retention Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Retention Unit</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @see #getRetentionUnit()
	 * @generated
	 */
	void setRetentionUnit(DurationUnit value);

	/**
	 * Returns the value of the '<em><b>Cleanup Interval</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * How often the cleanup runs ('cleanupInterval' 'cleanupIntervalUnit'). If unset, the
	 * runtime decides the cadence.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Cleanup Interval</em>' attribute.
	 * @see #setCleanupInterval(Integer)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getDeletionRule_CleanupInterval()
	 * @model
	 * @generated
	 */
	Integer getCleanupInterval();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupInterval <em>Cleanup Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cleanup Interval</em>' attribute.
	 * @see #getCleanupInterval()
	 * @generated
	 */
	void setCleanupInterval(Integer value);

	/**
	 * Returns the value of the '<em><b>Cleanup Interval Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.sensinact.model.mapping.DurationUnit}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Cleanup Interval Unit</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @see #setCleanupIntervalUnit(DurationUnit)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getDeletionRule_CleanupIntervalUnit()
	 * @model
	 * @generated
	 */
	DurationUnit getCleanupIntervalUnit();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupIntervalUnit <em>Cleanup Interval Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Cleanup Interval Unit</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @see #getCleanupIntervalUnit()
	 * @generated
	 */
	void setCleanupIntervalUnit(DurationUnit value);

	/**
	 * Returns the value of the '<em><b>Max Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional cap on the number of samples kept per resource.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Max Count</em>' attribute.
	 * @see #setMaxCount(Integer)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getDeletionRule_MaxCount()
	 * @model
	 * @generated
	 */
	Integer getMaxCount();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getMaxCount <em>Max Count</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max Count</em>' attribute.
	 * @see #getMaxCount()
	 * @generated
	 */
	void setMaxCount(Integer value);

} // DeletionRule
