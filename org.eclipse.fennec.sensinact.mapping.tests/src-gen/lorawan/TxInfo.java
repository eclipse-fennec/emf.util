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
 * A representation of the model object '<em><b>Tx Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link lorawan.TxInfo#getFrequency <em>Frequency</em>}</li>
 *   <li>{@link lorawan.TxInfo#getModulation <em>Modulation</em>}</li>
 *   <li>{@link lorawan.TxInfo#getLora <em>Lora</em>}</li>
 * </ul>
 *
 * @see lorawan.LorawanPackage#getTxInfo()
 * @model
 * @generated
 */
@ProviderType
public interface TxInfo extends EObject {
	/**
	 * Returns the value of the '<em><b>Frequency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Frequency</em>' attribute.
	 * @see #setFrequency(long)
	 * @see lorawan.LorawanPackage#getTxInfo_Frequency()
	 * @model
	 * @generated
	 */
	long getFrequency();

	/**
	 * Sets the value of the '{@link lorawan.TxInfo#getFrequency <em>Frequency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Frequency</em>' attribute.
	 * @see #getFrequency()
	 * @generated
	 */
	void setFrequency(long value);

	/**
	 * Returns the value of the '<em><b>Modulation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Modulation</em>' attribute.
	 * @see #setModulation(String)
	 * @see lorawan.LorawanPackage#getTxInfo_Modulation()
	 * @model
	 * @generated
	 */
	String getModulation();

	/**
	 * Sets the value of the '{@link lorawan.TxInfo#getModulation <em>Modulation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Modulation</em>' attribute.
	 * @see #getModulation()
	 * @generated
	 */
	void setModulation(String value);

	/**
	 * Returns the value of the '<em><b>Lora</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Lora</em>' containment reference.
	 * @see #setLora(LoraInfo)
	 * @see lorawan.LorawanPackage#getTxInfo_Lora()
	 * @model containment="true"
	 * @generated
	 */
	LoraInfo getLora();

	/**
	 * Sets the value of the '{@link lorawan.TxInfo#getLora <em>Lora</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Lora</em>' containment reference.
	 * @see #getLora()
	 * @generated
	 */
	void setLora(LoraInfo value);

} // TxInfo
