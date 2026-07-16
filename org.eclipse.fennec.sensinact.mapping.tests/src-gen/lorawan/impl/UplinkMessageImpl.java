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

import java.util.Collection;

import lorawan.DeviceInfo;
import lorawan.LorawanPackage;
import lorawan.RxInfo;
import lorawan.TxInfo;
import lorawan.UplinkMessage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Uplink Message</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getDeduplicationId <em>Deduplication Id</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getTime <em>Time</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#isAdr <em>Adr</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getDr <em>Dr</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getFCnt <em>FCnt</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getFPort <em>FPort</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#isConfirmed <em>Confirmed</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getData <em>Data</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getDeviceInfo <em>Device Info</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getRxInfo <em>Rx Info</em>}</li>
 *   <li>{@link lorawan.impl.UplinkMessageImpl#getTxInfo <em>Tx Info</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class UplinkMessageImpl extends MinimalEObjectImpl.Container implements UplinkMessage {
	/**
	 * The default value of the '{@link #getDeduplicationId() <em>Deduplication Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeduplicationId()
	 * @generated
	 * @ordered
	 */
	protected static final String DEDUPLICATION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeduplicationId() <em>Deduplication Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeduplicationId()
	 * @generated
	 * @ordered
	 */
	protected String deduplicationId = DEDUPLICATION_ID_EDEFAULT;

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
	 * The default value of the '{@link #isAdr() <em>Adr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAdr()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ADR_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAdr() <em>Adr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAdr()
	 * @generated
	 * @ordered
	 */
	protected boolean adr = ADR_EDEFAULT;

	/**
	 * The default value of the '{@link #getDr() <em>Dr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDr()
	 * @generated
	 * @ordered
	 */
	protected static final int DR_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getDr() <em>Dr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDr()
	 * @generated
	 * @ordered
	 */
	protected int dr = DR_EDEFAULT;

	/**
	 * The default value of the '{@link #getFCnt() <em>FCnt</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFCnt()
	 * @generated
	 * @ordered
	 */
	protected static final int FCNT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFCnt() <em>FCnt</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFCnt()
	 * @generated
	 * @ordered
	 */
	protected int fCnt = FCNT_EDEFAULT;

	/**
	 * The default value of the '{@link #getFPort() <em>FPort</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFPort()
	 * @generated
	 * @ordered
	 */
	protected static final int FPORT_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getFPort() <em>FPort</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFPort()
	 * @generated
	 * @ordered
	 */
	protected int fPort = FPORT_EDEFAULT;

	/**
	 * The default value of the '{@link #isConfirmed() <em>Confirmed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfirmed()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CONFIRMED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isConfirmed() <em>Confirmed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isConfirmed()
	 * @generated
	 * @ordered
	 */
	protected boolean confirmed = CONFIRMED_EDEFAULT;

	/**
	 * The default value of the '{@link #getData() <em>Data</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getData()
	 * @generated
	 * @ordered
	 */
	protected static final String DATA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getData() <em>Data</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getData()
	 * @generated
	 * @ordered
	 */
	protected String data = DATA_EDEFAULT;

	/**
	 * The cached value of the '{@link #getDeviceInfo() <em>Device Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceInfo()
	 * @generated
	 * @ordered
	 */
	protected DeviceInfo deviceInfo;

	/**
	 * The cached value of the '{@link #getRxInfo() <em>Rx Info</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRxInfo()
	 * @generated
	 * @ordered
	 */
	protected EList<RxInfo> rxInfo;

	/**
	 * The cached value of the '{@link #getTxInfo() <em>Tx Info</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTxInfo()
	 * @generated
	 * @ordered
	 */
	protected TxInfo txInfo;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UplinkMessageImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LorawanPackage.Literals.UPLINK_MESSAGE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDeduplicationId() {
		return deduplicationId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeduplicationId(String newDeduplicationId) {
		String oldDeduplicationId = deduplicationId;
		deduplicationId = newDeduplicationId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__DEDUPLICATION_ID, oldDeduplicationId, deduplicationId));
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
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__TIME, oldTime, time));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isAdr() {
		return adr;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAdr(boolean newAdr) {
		boolean oldAdr = adr;
		adr = newAdr;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__ADR, oldAdr, adr));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getDr() {
		return dr;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDr(int newDr) {
		int oldDr = dr;
		dr = newDr;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__DR, oldDr, dr));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getFCnt() {
		return fCnt;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFCnt(int newFCnt) {
		int oldFCnt = fCnt;
		fCnt = newFCnt;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__FCNT, oldFCnt, fCnt));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getFPort() {
		return fPort;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setFPort(int newFPort) {
		int oldFPort = fPort;
		fPort = newFPort;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__FPORT, oldFPort, fPort));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setConfirmed(boolean newConfirmed) {
		boolean oldConfirmed = confirmed;
		confirmed = newConfirmed;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__CONFIRMED, oldConfirmed, confirmed));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getData() {
		return data;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setData(String newData) {
		String oldData = data;
		data = newData;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__DATA, oldData, data));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetDeviceInfo(DeviceInfo newDeviceInfo, NotificationChain msgs) {
		DeviceInfo oldDeviceInfo = deviceInfo;
		deviceInfo = newDeviceInfo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO, oldDeviceInfo, newDeviceInfo);
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
	public void setDeviceInfo(DeviceInfo newDeviceInfo) {
		if (newDeviceInfo != deviceInfo) {
			NotificationChain msgs = null;
			if (deviceInfo != null)
				msgs = ((InternalEObject)deviceInfo).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO, null, msgs);
			if (newDeviceInfo != null)
				msgs = ((InternalEObject)newDeviceInfo).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO, null, msgs);
			msgs = basicSetDeviceInfo(newDeviceInfo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO, newDeviceInfo, newDeviceInfo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<RxInfo> getRxInfo() {
		if (rxInfo == null) {
			rxInfo = new EObjectContainmentEList<RxInfo>(RxInfo.class, this, LorawanPackage.UPLINK_MESSAGE__RX_INFO);
		}
		return rxInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TxInfo getTxInfo() {
		return txInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTxInfo(TxInfo newTxInfo, NotificationChain msgs) {
		TxInfo oldTxInfo = txInfo;
		txInfo = newTxInfo;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__TX_INFO, oldTxInfo, newTxInfo);
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
	public void setTxInfo(TxInfo newTxInfo) {
		if (newTxInfo != txInfo) {
			NotificationChain msgs = null;
			if (txInfo != null)
				msgs = ((InternalEObject)txInfo).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.UPLINK_MESSAGE__TX_INFO, null, msgs);
			if (newTxInfo != null)
				msgs = ((InternalEObject)newTxInfo).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.UPLINK_MESSAGE__TX_INFO, null, msgs);
			msgs = basicSetTxInfo(newTxInfo, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.UPLINK_MESSAGE__TX_INFO, newTxInfo, newTxInfo));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO:
				return basicSetDeviceInfo(null, msgs);
			case LorawanPackage.UPLINK_MESSAGE__RX_INFO:
				return ((InternalEList<?>)getRxInfo()).basicRemove(otherEnd, msgs);
			case LorawanPackage.UPLINK_MESSAGE__TX_INFO:
				return basicSetTxInfo(null, msgs);
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
			case LorawanPackage.UPLINK_MESSAGE__DEDUPLICATION_ID:
				return getDeduplicationId();
			case LorawanPackage.UPLINK_MESSAGE__TIME:
				return getTime();
			case LorawanPackage.UPLINK_MESSAGE__ADR:
				return isAdr();
			case LorawanPackage.UPLINK_MESSAGE__DR:
				return getDr();
			case LorawanPackage.UPLINK_MESSAGE__FCNT:
				return getFCnt();
			case LorawanPackage.UPLINK_MESSAGE__FPORT:
				return getFPort();
			case LorawanPackage.UPLINK_MESSAGE__CONFIRMED:
				return isConfirmed();
			case LorawanPackage.UPLINK_MESSAGE__DATA:
				return getData();
			case LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO:
				return getDeviceInfo();
			case LorawanPackage.UPLINK_MESSAGE__RX_INFO:
				return getRxInfo();
			case LorawanPackage.UPLINK_MESSAGE__TX_INFO:
				return getTxInfo();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case LorawanPackage.UPLINK_MESSAGE__DEDUPLICATION_ID:
				setDeduplicationId((String)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__TIME:
				setTime((String)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__ADR:
				setAdr((Boolean)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__DR:
				setDr((Integer)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__FCNT:
				setFCnt((Integer)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__FPORT:
				setFPort((Integer)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__CONFIRMED:
				setConfirmed((Boolean)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__DATA:
				setData((String)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO:
				setDeviceInfo((DeviceInfo)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__RX_INFO:
				getRxInfo().clear();
				getRxInfo().addAll((Collection<? extends RxInfo>)newValue);
				return;
			case LorawanPackage.UPLINK_MESSAGE__TX_INFO:
				setTxInfo((TxInfo)newValue);
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
			case LorawanPackage.UPLINK_MESSAGE__DEDUPLICATION_ID:
				setDeduplicationId(DEDUPLICATION_ID_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__TIME:
				setTime(TIME_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__ADR:
				setAdr(ADR_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__DR:
				setDr(DR_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__FCNT:
				setFCnt(FCNT_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__FPORT:
				setFPort(FPORT_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__CONFIRMED:
				setConfirmed(CONFIRMED_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__DATA:
				setData(DATA_EDEFAULT);
				return;
			case LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO:
				setDeviceInfo((DeviceInfo)null);
				return;
			case LorawanPackage.UPLINK_MESSAGE__RX_INFO:
				getRxInfo().clear();
				return;
			case LorawanPackage.UPLINK_MESSAGE__TX_INFO:
				setTxInfo((TxInfo)null);
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
			case LorawanPackage.UPLINK_MESSAGE__DEDUPLICATION_ID:
				return DEDUPLICATION_ID_EDEFAULT == null ? deduplicationId != null : !DEDUPLICATION_ID_EDEFAULT.equals(deduplicationId);
			case LorawanPackage.UPLINK_MESSAGE__TIME:
				return TIME_EDEFAULT == null ? time != null : !TIME_EDEFAULT.equals(time);
			case LorawanPackage.UPLINK_MESSAGE__ADR:
				return adr != ADR_EDEFAULT;
			case LorawanPackage.UPLINK_MESSAGE__DR:
				return dr != DR_EDEFAULT;
			case LorawanPackage.UPLINK_MESSAGE__FCNT:
				return fCnt != FCNT_EDEFAULT;
			case LorawanPackage.UPLINK_MESSAGE__FPORT:
				return fPort != FPORT_EDEFAULT;
			case LorawanPackage.UPLINK_MESSAGE__CONFIRMED:
				return confirmed != CONFIRMED_EDEFAULT;
			case LorawanPackage.UPLINK_MESSAGE__DATA:
				return DATA_EDEFAULT == null ? data != null : !DATA_EDEFAULT.equals(data);
			case LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO:
				return deviceInfo != null;
			case LorawanPackage.UPLINK_MESSAGE__RX_INFO:
				return rxInfo != null && !rxInfo.isEmpty();
			case LorawanPackage.UPLINK_MESSAGE__TX_INFO:
				return txInfo != null;
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
		result.append(" (deduplicationId: ");
		result.append(deduplicationId);
		result.append(", time: ");
		result.append(time);
		result.append(", adr: ");
		result.append(adr);
		result.append(", dr: ");
		result.append(dr);
		result.append(", fCnt: ");
		result.append(fCnt);
		result.append(", fPort: ");
		result.append(fPort);
		result.append(", confirmed: ");
		result.append(confirmed);
		result.append(", data: ");
		result.append(data);
		result.append(')');
		return result.toString();
	}

} //UplinkMessageImpl
