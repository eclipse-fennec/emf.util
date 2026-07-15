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
 * A representation of the model object '<em><b>Rx Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link lorawan.RxInfo#getGatewayId <em>Gateway Id</em>}</li>
 *   <li>{@link lorawan.RxInfo#getUplinkId <em>Uplink Id</em>}</li>
 *   <li>{@link lorawan.RxInfo#getTime <em>Time</em>}</li>
 *   <li>{@link lorawan.RxInfo#getRssi <em>Rssi</em>}</li>
 *   <li>{@link lorawan.RxInfo#getSnr <em>Snr</em>}</li>
 *   <li>{@link lorawan.RxInfo#getChannel <em>Channel</em>}</li>
 *   <li>{@link lorawan.RxInfo#getContext <em>Context</em>}</li>
 *   <li>{@link lorawan.RxInfo#getLocation <em>Location</em>}</li>
 *   <li>{@link lorawan.RxInfo#getMetadata <em>Metadata</em>}</li>
 * </ul>
 *
 * @see lorawan.LorawanPackage#getRxInfo()
 * @model
 * @generated
 */
@ProviderType
public interface RxInfo extends EObject {
	/**
	 * Returns the value of the '<em><b>Gateway Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gateway Id</em>' attribute.
	 * @see #setGatewayId(String)
	 * @see lorawan.LorawanPackage#getRxInfo_GatewayId()
	 * @model
	 * @generated
	 */
	String getGatewayId();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getGatewayId <em>Gateway Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gateway Id</em>' attribute.
	 * @see #getGatewayId()
	 * @generated
	 */
	void setGatewayId(String value);

	/**
	 * Returns the value of the '<em><b>Uplink Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Uplink Id</em>' attribute.
	 * @see #setUplinkId(int)
	 * @see lorawan.LorawanPackage#getRxInfo_UplinkId()
	 * @model
	 * @generated
	 */
	int getUplinkId();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getUplinkId <em>Uplink Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Uplink Id</em>' attribute.
	 * @see #getUplinkId()
	 * @generated
	 */
	void setUplinkId(int value);

	/**
	 * Returns the value of the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time</em>' attribute.
	 * @see #setTime(String)
	 * @see lorawan.LorawanPackage#getRxInfo_Time()
	 * @model
	 * @generated
	 */
	String getTime();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getTime <em>Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time</em>' attribute.
	 * @see #getTime()
	 * @generated
	 */
	void setTime(String value);

	/**
	 * Returns the value of the '<em><b>Rssi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rssi</em>' attribute.
	 * @see #setRssi(int)
	 * @see lorawan.LorawanPackage#getRxInfo_Rssi()
	 * @model
	 * @generated
	 */
	int getRssi();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getRssi <em>Rssi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rssi</em>' attribute.
	 * @see #getRssi()
	 * @generated
	 */
	void setRssi(int value);

	/**
	 * Returns the value of the '<em><b>Snr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Snr</em>' attribute.
	 * @see #setSnr(double)
	 * @see lorawan.LorawanPackage#getRxInfo_Snr()
	 * @model
	 * @generated
	 */
	double getSnr();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getSnr <em>Snr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Snr</em>' attribute.
	 * @see #getSnr()
	 * @generated
	 */
	void setSnr(double value);

	/**
	 * Returns the value of the '<em><b>Channel</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Channel</em>' attribute.
	 * @see #setChannel(int)
	 * @see lorawan.LorawanPackage#getRxInfo_Channel()
	 * @model
	 * @generated
	 */
	int getChannel();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getChannel <em>Channel</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Channel</em>' attribute.
	 * @see #getChannel()
	 * @generated
	 */
	void setChannel(int value);

	/**
	 * Returns the value of the '<em><b>Context</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Context</em>' attribute.
	 * @see #setContext(String)
	 * @see lorawan.LorawanPackage#getRxInfo_Context()
	 * @model
	 * @generated
	 */
	String getContext();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getContext <em>Context</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Context</em>' attribute.
	 * @see #getContext()
	 * @generated
	 */
	void setContext(String value);

	/**
	 * Returns the value of the '<em><b>Location</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location</em>' containment reference.
	 * @see #setLocation(Location)
	 * @see lorawan.LorawanPackage#getRxInfo_Location()
	 * @model containment="true"
	 * @generated
	 */
	Location getLocation();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getLocation <em>Location</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location</em>' containment reference.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(Location value);

	/**
	 * Returns the value of the '<em><b>Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Metadata</em>' containment reference.
	 * @see #setMetadata(Metadata)
	 * @see lorawan.LorawanPackage#getRxInfo_Metadata()
	 * @model containment="true"
	 * @generated
	 */
	Metadata getMetadata();

	/**
	 * Sets the value of the '{@link lorawan.RxInfo#getMetadata <em>Metadata</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Metadata</em>' containment reference.
	 * @see #getMetadata()
	 * @generated
	 */
	void setMetadata(Metadata value);

} // RxInfo
