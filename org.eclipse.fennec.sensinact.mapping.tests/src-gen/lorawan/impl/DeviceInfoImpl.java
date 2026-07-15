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

import lorawan.DeviceInfo;
import lorawan.LorawanPackage;
import lorawan.Tags;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Device Info</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getTenantId <em>Tenant Id</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getTenantName <em>Tenant Name</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getApplicationId <em>Application Id</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getApplicationName <em>Application Name</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getDeviceProfileId <em>Device Profile Id</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getDeviceProfileName <em>Device Profile Name</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getDeviceName <em>Device Name</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getDevEui <em>Dev Eui</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getDevAddr <em>Dev Addr</em>}</li>
 *   <li>{@link lorawan.impl.DeviceInfoImpl#getTags <em>Tags</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DeviceInfoImpl extends MinimalEObjectImpl.Container implements DeviceInfo {
	/**
	 * The default value of the '{@link #getTenantId() <em>Tenant Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTenantId()
	 * @generated
	 * @ordered
	 */
	protected static final String TENANT_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTenantId() <em>Tenant Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTenantId()
	 * @generated
	 * @ordered
	 */
	protected String tenantId = TENANT_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getTenantName() <em>Tenant Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTenantName()
	 * @generated
	 * @ordered
	 */
	protected static final String TENANT_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTenantName() <em>Tenant Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTenantName()
	 * @generated
	 * @ordered
	 */
	protected String tenantName = TENANT_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getApplicationId() <em>Application Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApplicationId()
	 * @generated
	 * @ordered
	 */
	protected static final String APPLICATION_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getApplicationId() <em>Application Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApplicationId()
	 * @generated
	 * @ordered
	 */
	protected String applicationId = APPLICATION_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getApplicationName() <em>Application Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApplicationName()
	 * @generated
	 * @ordered
	 */
	protected static final String APPLICATION_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getApplicationName() <em>Application Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getApplicationName()
	 * @generated
	 * @ordered
	 */
	protected String applicationName = APPLICATION_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeviceProfileId() <em>Device Profile Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceProfileId()
	 * @generated
	 * @ordered
	 */
	protected static final String DEVICE_PROFILE_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeviceProfileId() <em>Device Profile Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceProfileId()
	 * @generated
	 * @ordered
	 */
	protected String deviceProfileId = DEVICE_PROFILE_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeviceProfileName() <em>Device Profile Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceProfileName()
	 * @generated
	 * @ordered
	 */
	protected static final String DEVICE_PROFILE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeviceProfileName() <em>Device Profile Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceProfileName()
	 * @generated
	 * @ordered
	 */
	protected String deviceProfileName = DEVICE_PROFILE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDeviceName() <em>Device Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceName()
	 * @generated
	 * @ordered
	 */
	protected static final String DEVICE_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDeviceName() <em>Device Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDeviceName()
	 * @generated
	 * @ordered
	 */
	protected String deviceName = DEVICE_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDevEui() <em>Dev Eui</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDevEui()
	 * @generated
	 * @ordered
	 */
	protected static final String DEV_EUI_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDevEui() <em>Dev Eui</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDevEui()
	 * @generated
	 * @ordered
	 */
	protected String devEui = DEV_EUI_EDEFAULT;

	/**
	 * The default value of the '{@link #getDevAddr() <em>Dev Addr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDevAddr()
	 * @generated
	 * @ordered
	 */
	protected static final String DEV_ADDR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDevAddr() <em>Dev Addr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDevAddr()
	 * @generated
	 * @ordered
	 */
	protected String devAddr = DEV_ADDR_EDEFAULT;

	/**
	 * The cached value of the '{@link #getTags() <em>Tags</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTags()
	 * @generated
	 * @ordered
	 */
	protected Tags tags;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DeviceInfoImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LorawanPackage.Literals.DEVICE_INFO;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTenantId(String newTenantId) {
		String oldTenantId = tenantId;
		tenantId = newTenantId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__TENANT_ID, oldTenantId, tenantId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getTenantName() {
		return tenantName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTenantName(String newTenantName) {
		String oldTenantName = tenantName;
		tenantName = newTenantName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__TENANT_NAME, oldTenantName, tenantName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getApplicationId() {
		return applicationId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setApplicationId(String newApplicationId) {
		String oldApplicationId = applicationId;
		applicationId = newApplicationId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__APPLICATION_ID, oldApplicationId, applicationId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getApplicationName() {
		return applicationName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setApplicationName(String newApplicationName) {
		String oldApplicationName = applicationName;
		applicationName = newApplicationName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__APPLICATION_NAME, oldApplicationName, applicationName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDeviceProfileId() {
		return deviceProfileId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeviceProfileId(String newDeviceProfileId) {
		String oldDeviceProfileId = deviceProfileId;
		deviceProfileId = newDeviceProfileId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_ID, oldDeviceProfileId, deviceProfileId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDeviceProfileName() {
		return deviceProfileName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeviceProfileName(String newDeviceProfileName) {
		String oldDeviceProfileName = deviceProfileName;
		deviceProfileName = newDeviceProfileName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_NAME, oldDeviceProfileName, deviceProfileName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDeviceName(String newDeviceName) {
		String oldDeviceName = deviceName;
		deviceName = newDeviceName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__DEVICE_NAME, oldDeviceName, deviceName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDevEui() {
		return devEui;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDevEui(String newDevEui) {
		String oldDevEui = devEui;
		devEui = newDevEui;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__DEV_EUI, oldDevEui, devEui));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDevAddr() {
		return devAddr;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDevAddr(String newDevAddr) {
		String oldDevAddr = devAddr;
		devAddr = newDevAddr;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__DEV_ADDR, oldDevAddr, devAddr));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Tags getTags() {
		return tags;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTags(Tags newTags, NotificationChain msgs) {
		Tags oldTags = tags;
		tags = newTags;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__TAGS, oldTags, newTags);
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
	public void setTags(Tags newTags) {
		if (newTags != tags) {
			NotificationChain msgs = null;
			if (tags != null)
				msgs = ((InternalEObject)tags).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.DEVICE_INFO__TAGS, null, msgs);
			if (newTags != null)
				msgs = ((InternalEObject)newTags).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - LorawanPackage.DEVICE_INFO__TAGS, null, msgs);
			msgs = basicSetTags(newTags, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LorawanPackage.DEVICE_INFO__TAGS, newTags, newTags));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case LorawanPackage.DEVICE_INFO__TAGS:
				return basicSetTags(null, msgs);
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
			case LorawanPackage.DEVICE_INFO__TENANT_ID:
				return getTenantId();
			case LorawanPackage.DEVICE_INFO__TENANT_NAME:
				return getTenantName();
			case LorawanPackage.DEVICE_INFO__APPLICATION_ID:
				return getApplicationId();
			case LorawanPackage.DEVICE_INFO__APPLICATION_NAME:
				return getApplicationName();
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_ID:
				return getDeviceProfileId();
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_NAME:
				return getDeviceProfileName();
			case LorawanPackage.DEVICE_INFO__DEVICE_NAME:
				return getDeviceName();
			case LorawanPackage.DEVICE_INFO__DEV_EUI:
				return getDevEui();
			case LorawanPackage.DEVICE_INFO__DEV_ADDR:
				return getDevAddr();
			case LorawanPackage.DEVICE_INFO__TAGS:
				return getTags();
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
			case LorawanPackage.DEVICE_INFO__TENANT_ID:
				setTenantId((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__TENANT_NAME:
				setTenantName((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__APPLICATION_ID:
				setApplicationId((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__APPLICATION_NAME:
				setApplicationName((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_ID:
				setDeviceProfileId((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_NAME:
				setDeviceProfileName((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__DEVICE_NAME:
				setDeviceName((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__DEV_EUI:
				setDevEui((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__DEV_ADDR:
				setDevAddr((String)newValue);
				return;
			case LorawanPackage.DEVICE_INFO__TAGS:
				setTags((Tags)newValue);
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
			case LorawanPackage.DEVICE_INFO__TENANT_ID:
				setTenantId(TENANT_ID_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__TENANT_NAME:
				setTenantName(TENANT_NAME_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__APPLICATION_ID:
				setApplicationId(APPLICATION_ID_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__APPLICATION_NAME:
				setApplicationName(APPLICATION_NAME_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_ID:
				setDeviceProfileId(DEVICE_PROFILE_ID_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_NAME:
				setDeviceProfileName(DEVICE_PROFILE_NAME_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__DEVICE_NAME:
				setDeviceName(DEVICE_NAME_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__DEV_EUI:
				setDevEui(DEV_EUI_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__DEV_ADDR:
				setDevAddr(DEV_ADDR_EDEFAULT);
				return;
			case LorawanPackage.DEVICE_INFO__TAGS:
				setTags((Tags)null);
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
			case LorawanPackage.DEVICE_INFO__TENANT_ID:
				return TENANT_ID_EDEFAULT == null ? tenantId != null : !TENANT_ID_EDEFAULT.equals(tenantId);
			case LorawanPackage.DEVICE_INFO__TENANT_NAME:
				return TENANT_NAME_EDEFAULT == null ? tenantName != null : !TENANT_NAME_EDEFAULT.equals(tenantName);
			case LorawanPackage.DEVICE_INFO__APPLICATION_ID:
				return APPLICATION_ID_EDEFAULT == null ? applicationId != null : !APPLICATION_ID_EDEFAULT.equals(applicationId);
			case LorawanPackage.DEVICE_INFO__APPLICATION_NAME:
				return APPLICATION_NAME_EDEFAULT == null ? applicationName != null : !APPLICATION_NAME_EDEFAULT.equals(applicationName);
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_ID:
				return DEVICE_PROFILE_ID_EDEFAULT == null ? deviceProfileId != null : !DEVICE_PROFILE_ID_EDEFAULT.equals(deviceProfileId);
			case LorawanPackage.DEVICE_INFO__DEVICE_PROFILE_NAME:
				return DEVICE_PROFILE_NAME_EDEFAULT == null ? deviceProfileName != null : !DEVICE_PROFILE_NAME_EDEFAULT.equals(deviceProfileName);
			case LorawanPackage.DEVICE_INFO__DEVICE_NAME:
				return DEVICE_NAME_EDEFAULT == null ? deviceName != null : !DEVICE_NAME_EDEFAULT.equals(deviceName);
			case LorawanPackage.DEVICE_INFO__DEV_EUI:
				return DEV_EUI_EDEFAULT == null ? devEui != null : !DEV_EUI_EDEFAULT.equals(devEui);
			case LorawanPackage.DEVICE_INFO__DEV_ADDR:
				return DEV_ADDR_EDEFAULT == null ? devAddr != null : !DEV_ADDR_EDEFAULT.equals(devAddr);
			case LorawanPackage.DEVICE_INFO__TAGS:
				return tags != null;
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
		result.append(" (tenantId: ");
		result.append(tenantId);
		result.append(", tenantName: ");
		result.append(tenantName);
		result.append(", applicationId: ");
		result.append(applicationId);
		result.append(", applicationName: ");
		result.append(applicationName);
		result.append(", deviceProfileId: ");
		result.append(deviceProfileId);
		result.append(", deviceProfileName: ");
		result.append(deviceProfileName);
		result.append(", deviceName: ");
		result.append(deviceName);
		result.append(", devEui: ");
		result.append(devEui);
		result.append(", devAddr: ");
		result.append(devAddr);
		result.append(')');
		return result.toString();
	}

} //DeviceInfoImpl
