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

import java.time.Instant;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Timestamp Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getStrategy <em>Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getHint <em>Hint</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getTimestampMapping()
 * @model
 * @generated
 */
@ProviderType
public interface TimestampMapping extends FeatureMapping {
	/**
	 * Returns the value of the '<em><b>Strategy</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy
	 * @see #setStrategy(TimestampStrategy)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getTimestampMapping_Strategy()
	 * @model
	 * @generated
	 */
	TimestampStrategy getStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getStrategy <em>Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Strategy</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy
	 * @see #getStrategy()
	 * @generated
	 */
	void setStrategy(TimestampStrategy value);

	/**
	 * Returns the value of the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timestamp</em>' attribute.
	 * @see #setTimestamp(Instant)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getTimestampMapping_Timestamp()
	 * @model dataType="org.eclipse.fennec.sensinact.model.mapping.EInstant"
	 * @generated
	 */
	Instant getTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getTimestamp <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timestamp</em>' attribute.
	 * @see #getTimestamp()
	 * @generated
	 */
	void setTimestamp(Instant value);

	/**
	 * Returns the value of the '<em><b>Hint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A hint like a time-format pattern when converting a timestamp
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Hint</em>' attribute.
	 * @see #setHint(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getTimestampMapping_Hint()
	 * @model
	 * @generated
	 */
	String getHint();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getHint <em>Hint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Hint</em>' attribute.
	 * @see #getHint()
	 * @generated
	 */
	void setHint(String value);

} // TimestampMapping
