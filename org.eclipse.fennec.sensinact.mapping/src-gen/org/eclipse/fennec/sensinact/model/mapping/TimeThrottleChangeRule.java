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
 * A representation of the model object '<em><b>Time Throttle Change Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Store at most once per interval; changes arriving sooner than 'interval' 'intervalUnit'
 * after the last stored value are dropped (e.g. interval=5, intervalUnit=MINUTES).
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getInterval <em>Interval</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getIntervalUnit <em>Interval Unit</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getTimeThrottleChangeRule()
 * @model
 * @generated
 */
@ProviderType
public interface TimeThrottleChangeRule extends ChangeRule {
	/**
	 * Returns the value of the '<em><b>Interval</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interval</em>' attribute.
	 * @see #setInterval(Integer)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getTimeThrottleChangeRule_Interval()
	 * @model required="true"
	 * @generated
	 */
	Integer getInterval();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getInterval <em>Interval</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Interval</em>' attribute.
	 * @see #getInterval()
	 * @generated
	 */
	void setInterval(Integer value);

	/**
	 * Returns the value of the '<em><b>Interval Unit</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.sensinact.model.mapping.DurationUnit}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interval Unit</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @see #setIntervalUnit(DurationUnit)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getTimeThrottleChangeRule_IntervalUnit()
	 * @model required="true"
	 * @generated
	 */
	DurationUnit getIntervalUnit();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getIntervalUnit <em>Interval Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Interval Unit</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @see #getIntervalUnit()
	 * @generated
	 */
	void setIntervalUnit(DurationUnit value);

} // TimeThrottleChangeRule
