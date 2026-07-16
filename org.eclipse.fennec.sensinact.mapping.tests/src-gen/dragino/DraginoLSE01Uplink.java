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
package dragino;

import lorawan.UplinkMessage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>LSE01 Uplink</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link dragino.DraginoLSE01Uplink#getObject <em>Object</em>}</li>
 * </ul>
 *
 * @see dragino.DraginoPackage#getDraginoLSE01Uplink()
 * @model
 * @generated
 */
@ProviderType
public interface DraginoLSE01Uplink extends UplinkMessage {
	/**
	 * Returns the value of the '<em><b>Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Object</em>' containment reference.
	 * @see #setObject(DecodedObject)
	 * @see dragino.DraginoPackage#getDraginoLSE01Uplink_Object()
	 * @model containment="true"
	 * @generated
	 */
	DecodedObject getObject();

	/**
	 * Sets the value of the '{@link dragino.DraginoLSE01Uplink#getObject <em>Object</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Object</em>' containment reference.
	 * @see #getObject()
	 * @generated
	 */
	void setObject(DecodedObject value);

} // DraginoLSE01Uplink
