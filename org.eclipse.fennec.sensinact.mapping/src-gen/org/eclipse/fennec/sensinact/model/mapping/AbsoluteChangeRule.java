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
 * A representation of the model object '<em><b>Absolute Change Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Store only if the absolute value change since the last stored value is at least 'delta'.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AbsoluteChangeRule#getDelta <em>Delta</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAbsoluteChangeRule()
 * @model
 * @generated
 */
@ProviderType
public interface AbsoluteChangeRule extends ChangeRule {
	/**
	 * Returns the value of the '<em><b>Delta</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Delta</em>' attribute.
	 * @see #setDelta(Double)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAbsoluteChangeRule_Delta()
	 * @model required="true"
	 * @generated
	 */
	Double getDelta();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.AbsoluteChangeRule#getDelta <em>Delta</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Delta</em>' attribute.
	 * @see #getDelta()
	 * @generated
	 */
	void setDelta(Double value);

} // AbsoluteChangeRule
