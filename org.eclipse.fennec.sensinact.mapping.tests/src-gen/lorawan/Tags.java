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
package lorawan;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Tags</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link lorawan.Tags#getDev_type <em>Dev type</em>}</li>
 * </ul>
 *
 * @see lorawan.LorawanPackage#getTags()
 * @model
 * @generated
 */
@ProviderType
public interface Tags extends EObject {
	/**
	 * Returns the value of the '<em><b>Dev type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dev type</em>' attribute.
	 * @see #setDev_type(String)
	 * @see lorawan.LorawanPackage#getTags_Dev_type()
	 * @model
	 * @generated
	 */
	String getDev_type();

	/**
	 * Sets the value of the '{@link lorawan.Tags#getDev_type <em>Dev type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dev type</em>' attribute.
	 * @see #getDev_type()
	 * @generated
	 */
	void setDev_type(String value);

} // Tags
