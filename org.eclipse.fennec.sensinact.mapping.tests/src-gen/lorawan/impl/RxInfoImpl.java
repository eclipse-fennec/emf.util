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
package lorawan.impl;

import lorawan.Location;
import lorawan.LorawanPackage;
import lorawan.Metadata;
import lorawan.RxInfo;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Rx Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link lorawan.impl.RxInfoImpl#getGatewayId <em>Gateway Id</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getUplinkId <em>Uplink Id</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getTime <em>Time</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getRssi <em>Rssi</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getSnr <em>Snr</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getChannel <em>Channel</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getContext <em>Context</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link lorawan.impl.RxInfoImpl#getMetadata <em>Metadata</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RxInfoImpl extends MinimalEObjectImpl.Container implements RxInfo {
	/**
	 * The default value of the '{@link #getGatewayId() <em>Gateway Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGatewayId()
	 * @generated
	 * @ordered
	 */
	protected static final String GATEWAY_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getGatewayId() <em>Gateway Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGatewayId()
	 * @generated
	 * @ordered
	 */
	protected String gatewayId = GATEWAY_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getUplinkId() <em>Uplink Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUplinkId()
	 * @generated
	 * @ordered
	 */
	protected static final int UPLINK_ID_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getUplinkId() <em>Uplink Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUplinkId()
	 * @generated
	 * @ordered
	 */
	protected int uplinkId = UPLINK_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getTime() <em>Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTime()
	 * @generated
	 * @ordered
	 */
	protected static final String TIME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTime() <em>Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTime()
	 * @generated
	 * @ordered
	 */
	protected String time = TIME_EDEFAULT;

	/**
	 * The default value of the '{@link #getRssi() <em>Rssi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRssi()
	 * @generated
	 * @ordered
	 */
	protected static final int RSSI_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getRssi() <em>Rssi</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRssi()
	 * @generated
	 * @ordered
	 */
	protected int rssi = RSSI_EDEFAULT;

	/**
	 * The default value of the '{@link #getSnr() <em>Snr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSnr()
	 * @generated
	 * @ordered
	 */
	protected static final double SNR_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getSnr() <em>Snr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSnr()
	 * @generated
	 * @ordered
	 */
	protected double snr = SNR_EDEFAULT;

	/**
	 * The default value of the '{@link #getChannel() <em>Channel</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChannel()
	 * @generated
	 * @ordered
	 */
	protected static final int CHANNEL_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getChannel() <em>Channel</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChannel()
	 * @generated
	 * @ordered
	 */
	protected int channel = CHANNEL_EDEFAULT;

	/**
	 * The default value of the '{@link #getContext() <em>Context</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContext()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTEXT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContext() <em>Context</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContext()
	 * @generated
	 * @ordered
	 */
	protected String context = CONTEXT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getLocation() <em>Location</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected Location location;

	/**
	 * The cached value of the '{@link #getMetadata() <em>Metadata</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMetadata()
	 * @generated
	 * @ordered
	 */
	protected Metadata metadata;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RxInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LorawanPackage.Literals.RX_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getGatewayId() {
		return gatewayId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setGatewayId(String newGatewayId) {
		String oldGatewayId = gatewayId;
		gatewayId = newGatewayId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__GATEWAY_ID, oldGatewayId, gatewayId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getUplinkId() {
		return uplinkId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setUplinkId(int newUplinkId) {
		int oldUplinkId = uplinkId;
		uplinkId = newUplinkId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__UPLINK_ID, oldUplinkId, uplinkId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTime() {
		return time;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTime(String newTime) {
		String oldTime = time;
		time = newTime;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__TIME, oldTime, time));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getRssi() {
		return rssi;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRssi(int newRssi) {
		int oldRssi = rssi;
		rssi = newRssi;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__RSSI, oldRssi, rssi));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getSnr() {
		return snr;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSnr(double newSnr) {
		double oldSnr = snr;
		snr = newSnr;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__SNR, oldSnr, snr));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getChannel() {
		return channel;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setChannel(int newChannel) {
		int oldChannel = channel;
		channel = newChannel;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__CHANNEL, oldChannel, channel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContext() {
		return context;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContext(String newContext) {
		String oldContext = context;
		context = newContext;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__CONTEXT, oldContext, context));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Location getLocation() {
		return location;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLocation(Location newLocation, NotificationChain msgs) {
		Location oldLocation = location;
		location = newLocation;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__LOCATION, oldLocation, newLocation);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLocation(Location newLocation) {
		if (newLocation != location) {
			NotificationChain msgs = null;
			if (location != null)
				msgs = ((InternalEObject)location).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.RX_INFO__LOCATION, null, msgs);
			if (newLocation != null)
				msgs = ((InternalEObject)newLocation).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.RX_INFO__LOCATION, null, msgs);
			msgs = basicSetLocation(newLocation, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__LOCATION, newLocation, newLocation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetMetadata(Metadata newMetadata, NotificationChain msgs) {
		Metadata oldMetadata = metadata;
		metadata = newMetadata;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__METADATA, oldMetadata, newMetadata);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMetadata(Metadata newMetadata) {
		if (newMetadata != metadata) {
			NotificationChain msgs = null;
			if (metadata != null)
				msgs = ((InternalEObject)metadata).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.RX_INFO__METADATA, null, msgs);
			if (newMetadata != null)
				msgs = ((InternalEObject)newMetadata).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.RX_INFO__METADATA, null, msgs);
			msgs = basicSetMetadata(newMetadata, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.RX_INFO__METADATA, newMetadata, newMetadata));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LorawanPackage.RX_INFO__LOCATION:
				return basicSetLocation(null, msgs);
			case LorawanPackage.RX_INFO__METADATA:
				return basicSetMetadata(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LorawanPackage.RX_INFO__GATEWAY_ID:
				return getGatewayId();
			case LorawanPackage.RX_INFO__UPLINK_ID:
				return getUplinkId();
			case LorawanPackage.RX_INFO__TIME:
				return getTime();
			case LorawanPackage.RX_INFO__RSSI:
				return getRssi();
			case LorawanPackage.RX_INFO__SNR:
				return getSnr();
			case LorawanPackage.RX_INFO__CHANNEL:
				return getChannel();
			case LorawanPackage.RX_INFO__CONTEXT:
				return getContext();
			case LorawanPackage.RX_INFO__LOCATION:
				return getLocation();
			case LorawanPackage.RX_INFO__METADATA:
				return getMetadata();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case LorawanPackage.RX_INFO__GATEWAY_ID:
				setGatewayId((String)newValue);
				return;
			case LorawanPackage.RX_INFO__UPLINK_ID:
				setUplinkId((Integer)newValue);
				return;
			case LorawanPackage.RX_INFO__TIME:
				setTime((String)newValue);
				return;
			case LorawanPackage.RX_INFO__RSSI:
				setRssi((Integer)newValue);
				return;
			case LorawanPackage.RX_INFO__SNR:
				setSnr((Double)newValue);
				return;
			case LorawanPackage.RX_INFO__CHANNEL:
				setChannel((Integer)newValue);
				return;
			case LorawanPackage.RX_INFO__CONTEXT:
				setContext((String)newValue);
				return;
			case LorawanPackage.RX_INFO__LOCATION:
				setLocation((Location)newValue);
				return;
			case LorawanPackage.RX_INFO__METADATA:
				setMetadata((Metadata)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case LorawanPackage.RX_INFO__GATEWAY_ID:
				setGatewayId(GATEWAY_ID_EDEFAULT);
				return;
			case LorawanPackage.RX_INFO__UPLINK_ID:
				setUplinkId(UPLINK_ID_EDEFAULT);
				return;
			case LorawanPackage.RX_INFO__TIME:
				setTime(TIME_EDEFAULT);
				return;
			case LorawanPackage.RX_INFO__RSSI:
				setRssi(RSSI_EDEFAULT);
				return;
			case LorawanPackage.RX_INFO__SNR:
				setSnr(SNR_EDEFAULT);
				return;
			case LorawanPackage.RX_INFO__CHANNEL:
				setChannel(CHANNEL_EDEFAULT);
				return;
			case LorawanPackage.RX_INFO__CONTEXT:
				setContext(CONTEXT_EDEFAULT);
				return;
			case LorawanPackage.RX_INFO__LOCATION:
				setLocation((Location)null);
				return;
			case LorawanPackage.RX_INFO__METADATA:
				setMetadata((Metadata)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case LorawanPackage.RX_INFO__GATEWAY_ID:
				return GATEWAY_ID_EDEFAULT == null ? gatewayId != null : !GATEWAY_ID_EDEFAULT.equals(gatewayId);
			case LorawanPackage.RX_INFO__UPLINK_ID:
				return uplinkId != UPLINK_ID_EDEFAULT;
			case LorawanPackage.RX_INFO__TIME:
				return TIME_EDEFAULT == null ? time != null : !TIME_EDEFAULT.equals(time);
			case LorawanPackage.RX_INFO__RSSI:
				return rssi != RSSI_EDEFAULT;
			case LorawanPackage.RX_INFO__SNR:
				return snr != SNR_EDEFAULT;
			case LorawanPackage.RX_INFO__CHANNEL:
				return channel != CHANNEL_EDEFAULT;
			case LorawanPackage.RX_INFO__CONTEXT:
				return CONTEXT_EDEFAULT == null ? context != null : !CONTEXT_EDEFAULT.equals(context);
			case LorawanPackage.RX_INFO__LOCATION:
				return location != null;
			case LorawanPackage.RX_INFO__METADATA:
				return metadata != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (gatewayId: ");
		result.append(gatewayId);
		result.append(", uplinkId: ");
		result.append(uplinkId);
		result.append(", time: ");
		result.append(time);
		result.append(", rssi: ");
		result.append(rssi);
		result.append(", snr: ");
		result.append(snr);
		result.append(", channel: ");
		result.append(channel);
		result.append(", context: ");
		result.append(context);
		result.append(')');
		return result.toString();
	}

} //RxInfoImpl
