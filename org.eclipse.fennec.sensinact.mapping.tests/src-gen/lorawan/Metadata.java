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
 * A representation of the model object '<em><b>Metadata</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link lorawan.Metadata#getRegion_name <em>Region name</em>}</li>
 *   <li>{@link lorawan.Metadata#getRegion_common_name <em>Region common name</em>}</li>
 * </ul>
 *
 * @see lorawan.LorawanPackage#getMetadata()
 * @model
 * @generated
 */
@ProviderType
public interface Metadata extends EObject {
	/**
	 * Returns the value of the '<em><b>Region name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Region name</em>' attribute.
	 * @see #setRegion_name(String)
	 * @see lorawan.LorawanPackage#getMetadata_Region_name()
	 * @model
	 * @generated
	 */
	String getRegion_name();

	/**
	 * Sets the value of the '{@link lorawan.Metadata#getRegion_name <em>Region name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Region name</em>' attribute.
	 * @see #getRegion_name()
	 * @generated
	 */
	void setRegion_name(String value);

	/**
	 * Returns the value of the '<em><b>Region common name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Region common name</em>' attribute.
	 * @see #setRegion_common_name(String)
	 * @see lorawan.LorawanPackage#getMetadata_Region_common_name()
	 * @model
	 * @generated
	 */
	String getRegion_common_name();

	/**
	 * Sets the value of the '{@link lorawan.Metadata#getRegion_common_name <em>Region common name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Region common name</em>' attribute.
	 * @see #getRegion_common_name()
	 * @generated
	 */
	void setRegion_common_name(String value);

} // Metadata
