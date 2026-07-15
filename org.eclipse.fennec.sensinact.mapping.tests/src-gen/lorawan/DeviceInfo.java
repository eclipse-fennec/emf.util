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
 * A representation of the model object '<em><b>Device Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link lorawan.DeviceInfo#getTenantId <em>Tenant Id</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getTenantName <em>Tenant Name</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getApplicationId <em>Application Id</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getApplicationName <em>Application Name</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getDeviceProfileId <em>Device Profile Id</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getDeviceProfileName <em>Device Profile Name</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getDeviceName <em>Device Name</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getDevEui <em>Dev Eui</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getDevAddr <em>Dev Addr</em>}</li>
 *   <li>{@link lorawan.DeviceInfo#getTags <em>Tags</em>}</li>
 * </ul>
 *
 * @see lorawan.LorawanPackage#getDeviceInfo()
 * @model
 * @generated
 */
@ProviderType
public interface DeviceInfo extends EObject {
	/**
	 * Returns the value of the '<em><b>Tenant Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tenant Id</em>' attribute.
	 * @see #setTenantId(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_TenantId()
	 * @model
	 * @generated
	 */
	String getTenantId();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getTenantId <em>Tenant Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tenant Id</em>' attribute.
	 * @see #getTenantId()
	 * @generated
	 */
	void setTenantId(String value);

	/**
	 * Returns the value of the '<em><b>Tenant Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tenant Name</em>' attribute.
	 * @see #setTenantName(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_TenantName()
	 * @model
	 * @generated
	 */
	String getTenantName();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getTenantName <em>Tenant Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tenant Name</em>' attribute.
	 * @see #getTenantName()
	 * @generated
	 */
	void setTenantName(String value);

	/**
	 * Returns the value of the '<em><b>Application Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Application Id</em>' attribute.
	 * @see #setApplicationId(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_ApplicationId()
	 * @model
	 * @generated
	 */
	String getApplicationId();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getApplicationId <em>Application Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Application Id</em>' attribute.
	 * @see #getApplicationId()
	 * @generated
	 */
	void setApplicationId(String value);

	/**
	 * Returns the value of the '<em><b>Application Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Application Name</em>' attribute.
	 * @see #setApplicationName(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_ApplicationName()
	 * @model
	 * @generated
	 */
	String getApplicationName();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getApplicationName <em>Application Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Application Name</em>' attribute.
	 * @see #getApplicationName()
	 * @generated
	 */
	void setApplicationName(String value);

	/**
	 * Returns the value of the '<em><b>Device Profile Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Device Profile Id</em>' attribute.
	 * @see #setDeviceProfileId(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_DeviceProfileId()
	 * @model
	 * @generated
	 */
	String getDeviceProfileId();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getDeviceProfileId <em>Device Profile Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Device Profile Id</em>' attribute.
	 * @see #getDeviceProfileId()
	 * @generated
	 */
	void setDeviceProfileId(String value);

	/**
	 * Returns the value of the '<em><b>Device Profile Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Device Profile Name</em>' attribute.
	 * @see #setDeviceProfileName(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_DeviceProfileName()
	 * @model
	 * @generated
	 */
	String getDeviceProfileName();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getDeviceProfileName <em>Device Profile Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Device Profile Name</em>' attribute.
	 * @see #getDeviceProfileName()
	 * @generated
	 */
	void setDeviceProfileName(String value);

	/**
	 * Returns the value of the '<em><b>Device Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Device Name</em>' attribute.
	 * @see #setDeviceName(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_DeviceName()
	 * @model
	 * @generated
	 */
	String getDeviceName();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getDeviceName <em>Device Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Device Name</em>' attribute.
	 * @see #getDeviceName()
	 * @generated
	 */
	void setDeviceName(String value);

	/**
	 * Returns the value of the '<em><b>Dev Eui</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dev Eui</em>' attribute.
	 * @see #setDevEui(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_DevEui()
	 * @model
	 * @generated
	 */
	String getDevEui();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getDevEui <em>Dev Eui</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dev Eui</em>' attribute.
	 * @see #getDevEui()
	 * @generated
	 */
	void setDevEui(String value);

	/**
	 * Returns the value of the '<em><b>Dev Addr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dev Addr</em>' attribute.
	 * @see #setDevAddr(String)
	 * @see lorawan.LorawanPackage#getDeviceInfo_DevAddr()
	 * @model
	 * @generated
	 */
	String getDevAddr();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getDevAddr <em>Dev Addr</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dev Addr</em>' attribute.
	 * @see #getDevAddr()
	 * @generated
	 */
	void setDevAddr(String value);

	/**
	 * Returns the value of the '<em><b>Tags</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Tags</em>' containment reference.
	 * @see #setTags(Tags)
	 * @see lorawan.LorawanPackage#getDeviceInfo_Tags()
	 * @model containment="true"
	 * @generated
	 */
	Tags getTags();

	/**
	 * Sets the value of the '{@link lorawan.DeviceInfo#getTags <em>Tags</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Tags</em>' containment reference.
	 * @see #getTags()
	 * @generated
	 */
	void setTags(Tags value);

} // DeviceInfo
