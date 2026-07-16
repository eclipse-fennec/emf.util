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
 * A representation of the model object '<em><b>Percentage Change Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Store only if the value changed by at least 'percentage' percent relative to the last
 * stored value.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.PercentageChangeRule#getPercentage <em>Percentage</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getPercentageChangeRule()
 * @model
 * @generated
 */
@ProviderType
public interface PercentageChangeRule extends ChangeRule {
	/**
	 * Returns the value of the '<em><b>Percentage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Percentage</em>' attribute.
	 * @see #setPercentage(Double)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getPercentageChangeRule_Percentage()
	 * @model required="true"
	 * @generated
	 */
	Double getPercentage();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.PercentageChangeRule#getPercentage <em>Percentage</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Percentage</em>' attribute.
	 * @see #getPercentage()
	 * @generated
	 */
	void setPercentage(Double value);

} // PercentageChangeRule
