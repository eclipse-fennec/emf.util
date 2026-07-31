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


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see lorawan.LorawanFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = LorawanPackage.eNS_URI, fingerprint = "fp1:5dbdc146c48d0acdc5eeaa4ca40bd77f95258561dc24ac97738b053f0cf97826", genModel = "/model/lora-test.genmodel", genModelSourceLocations = {"model/lora-test.genmodel","org.eclipse.fennec.sensinact.mapping.tests/model/lora-test.genmodel"}, ecore = "/model/lorawan-uplink.ecore", ecoreSourceLocations = "/model/lorawan-uplink.ecore")
public interface LorawanPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "lorawan";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/lorawan";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "lorawan";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LorawanPackage eINSTANCE = lorawan.impl.LorawanPackageImpl.init();

	/**
	 * The meta object id for the '{@link lorawan.impl.UplinkMessageImpl <em>Uplink Message</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.UplinkMessageImpl
	 * @see lorawan.impl.LorawanPackageImpl#getUplinkMessage()
	 * @generated
	 */
	int UPLINK_MESSAGE = 0;

	/**
	 * The feature id for the '<em><b>Deduplication Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__DEDUPLICATION_ID = 0;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__TIME = 1;

	/**
	 * The feature id for the '<em><b>Adr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__ADR = 2;

	/**
	 * The feature id for the '<em><b>Dr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__DR = 3;

	/**
	 * The feature id for the '<em><b>FCnt</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__FCNT = 4;

	/**
	 * The feature id for the '<em><b>FPort</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__FPORT = 5;

	/**
	 * The feature id for the '<em><b>Confirmed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__CONFIRMED = 6;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__DATA = 7;

	/**
	 * The feature id for the '<em><b>Device Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__DEVICE_INFO = 8;

	/**
	 * The feature id for the '<em><b>Rx Info</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__RX_INFO = 9;

	/**
	 * The feature id for the '<em><b>Tx Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE__TX_INFO = 10;

	/**
	 * The number of structural features of the '<em>Uplink Message</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE_FEATURE_COUNT = 11;

	/**
	 * The number of operations of the '<em>Uplink Message</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UPLINK_MESSAGE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link lorawan.impl.DeviceInfoImpl <em>Device Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.DeviceInfoImpl
	 * @see lorawan.impl.LorawanPackageImpl#getDeviceInfo()
	 * @generated
	 */
	int DEVICE_INFO = 1;

	/**
	 * The feature id for the '<em><b>Tenant Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__TENANT_ID = 0;

	/**
	 * The feature id for the '<em><b>Tenant Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__TENANT_NAME = 1;

	/**
	 * The feature id for the '<em><b>Application Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__APPLICATION_ID = 2;

	/**
	 * The feature id for the '<em><b>Application Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__APPLICATION_NAME = 3;

	/**
	 * The feature id for the '<em><b>Device Profile Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__DEVICE_PROFILE_ID = 4;

	/**
	 * The feature id for the '<em><b>Device Profile Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__DEVICE_PROFILE_NAME = 5;

	/**
	 * The feature id for the '<em><b>Device Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__DEVICE_NAME = 6;

	/**
	 * The feature id for the '<em><b>Dev Eui</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__DEV_EUI = 7;

	/**
	 * The feature id for the '<em><b>Dev Addr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__DEV_ADDR = 8;

	/**
	 * The feature id for the '<em><b>Tags</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO__TAGS = 9;

	/**
	 * The number of structural features of the '<em>Device Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO_FEATURE_COUNT = 10;

	/**
	 * The number of operations of the '<em>Device Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_INFO_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link lorawan.impl.TagsImpl <em>Tags</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.TagsImpl
	 * @see lorawan.impl.LorawanPackageImpl#getTags()
	 * @generated
	 */
	int TAGS = 2;

	/**
	 * The feature id for the '<em><b>Dev type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAGS__DEV_TYPE = 0;

	/**
	 * The number of structural features of the '<em>Tags</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAGS_FEATURE_COUNT = 1;

	/**
	 * The number of operations of the '<em>Tags</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TAGS_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link lorawan.impl.RxInfoImpl <em>Rx Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.RxInfoImpl
	 * @see lorawan.impl.LorawanPackageImpl#getRxInfo()
	 * @generated
	 */
	int RX_INFO = 3;

	/**
	 * The feature id for the '<em><b>Gateway Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__GATEWAY_ID = 0;

	/**
	 * The feature id for the '<em><b>Uplink Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__UPLINK_ID = 1;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__TIME = 2;

	/**
	 * The feature id for the '<em><b>Rssi</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__RSSI = 3;

	/**
	 * The feature id for the '<em><b>Snr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__SNR = 4;

	/**
	 * The feature id for the '<em><b>Channel</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__CHANNEL = 5;

	/**
	 * The feature id for the '<em><b>Context</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__CONTEXT = 6;

	/**
	 * The feature id for the '<em><b>Location</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__LOCATION = 7;

	/**
	 * The feature id for the '<em><b>Metadata</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO__METADATA = 8;

	/**
	 * The number of structural features of the '<em>Rx Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO_FEATURE_COUNT = 9;

	/**
	 * The number of operations of the '<em>Rx Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RX_INFO_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link lorawan.impl.LocationImpl <em>Location</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.LocationImpl
	 * @see lorawan.impl.LorawanPackageImpl#getLocation()
	 * @generated
	 */
	int LOCATION = 4;

	/**
	 * The feature id for the '<em><b>Latitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCATION__LATITUDE = 0;

	/**
	 * The feature id for the '<em><b>Longitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCATION__LONGITUDE = 1;

	/**
	 * The feature id for the '<em><b>Altitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCATION__ALTITUDE = 2;

	/**
	 * The number of structural features of the '<em>Location</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCATION_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Location</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCATION_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link lorawan.impl.MetadataImpl <em>Metadata</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.MetadataImpl
	 * @see lorawan.impl.LorawanPackageImpl#getMetadata()
	 * @generated
	 */
	int METADATA = 5;

	/**
	 * The feature id for the '<em><b>Region name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__REGION_NAME = 0;

	/**
	 * The feature id for the '<em><b>Region common name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA__REGION_COMMON_NAME = 1;

	/**
	 * The number of structural features of the '<em>Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Metadata</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link lorawan.impl.TxInfoImpl <em>Tx Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.TxInfoImpl
	 * @see lorawan.impl.LorawanPackageImpl#getTxInfo()
	 * @generated
	 */
	int TX_INFO = 6;

	/**
	 * The feature id for the '<em><b>Frequency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TX_INFO__FREQUENCY = 0;

	/**
	 * The feature id for the '<em><b>Modulation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TX_INFO__MODULATION = 1;

	/**
	 * The feature id for the '<em><b>Lora</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TX_INFO__LORA = 2;

	/**
	 * The number of structural features of the '<em>Tx Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TX_INFO_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Tx Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TX_INFO_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link lorawan.impl.LoraInfoImpl <em>Lora Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see lorawan.impl.LoraInfoImpl
	 * @see lorawan.impl.LorawanPackageImpl#getLoraInfo()
	 * @generated
	 */
	int LORA_INFO = 7;

	/**
	 * The feature id for the '<em><b>Bandwidth</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LORA_INFO__BANDWIDTH = 0;

	/**
	 * The feature id for the '<em><b>Spreading Factor</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LORA_INFO__SPREADING_FACTOR = 1;

	/**
	 * The feature id for the '<em><b>Code Rate</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LORA_INFO__CODE_RATE = 2;

	/**
	 * The number of structural features of the '<em>Lora Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LORA_INFO_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Lora Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LORA_INFO_OPERATION_COUNT = 0;


	/**
	 * Returns the meta object for class '{@link lorawan.UplinkMessage <em>Uplink Message</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Uplink Message</em>'.
	 * @see lorawan.UplinkMessage
	 * @generated
	 */
	EClass getUplinkMessage();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#getDeduplicationId <em>Deduplication Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Deduplication Id</em>'.
	 * @see lorawan.UplinkMessage#getDeduplicationId()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_DeduplicationId();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#getTime <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time</em>'.
	 * @see lorawan.UplinkMessage#getTime()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_Time();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#isAdr <em>Adr</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Adr</em>'.
	 * @see lorawan.UplinkMessage#isAdr()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_Adr();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#getDr <em>Dr</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dr</em>'.
	 * @see lorawan.UplinkMessage#getDr()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_Dr();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#getFCnt <em>FCnt</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>FCnt</em>'.
	 * @see lorawan.UplinkMessage#getFCnt()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_FCnt();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#getFPort <em>FPort</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>FPort</em>'.
	 * @see lorawan.UplinkMessage#getFPort()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_FPort();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#isConfirmed <em>Confirmed</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Confirmed</em>'.
	 * @see lorawan.UplinkMessage#isConfirmed()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_Confirmed();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.UplinkMessage#getData <em>Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data</em>'.
	 * @see lorawan.UplinkMessage#getData()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EAttribute getUplinkMessage_Data();

	/**
	 * Returns the meta object for the containment reference '{@link lorawan.UplinkMessage#getDeviceInfo <em>Device Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Device Info</em>'.
	 * @see lorawan.UplinkMessage#getDeviceInfo()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EReference getUplinkMessage_DeviceInfo();

	/**
	 * Returns the meta object for the containment reference list '{@link lorawan.UplinkMessage#getRxInfo <em>Rx Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Rx Info</em>'.
	 * @see lorawan.UplinkMessage#getRxInfo()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EReference getUplinkMessage_RxInfo();

	/**
	 * Returns the meta object for the containment reference '{@link lorawan.UplinkMessage#getTxInfo <em>Tx Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Tx Info</em>'.
	 * @see lorawan.UplinkMessage#getTxInfo()
	 * @see #getUplinkMessage()
	 * @generated
	 */
	EReference getUplinkMessage_TxInfo();

	/**
	 * Returns the meta object for class '{@link lorawan.DeviceInfo <em>Device Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Device Info</em>'.
	 * @see lorawan.DeviceInfo
	 * @generated
	 */
	EClass getDeviceInfo();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getTenantId <em>Tenant Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tenant Id</em>'.
	 * @see lorawan.DeviceInfo#getTenantId()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_TenantId();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getTenantName <em>Tenant Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Tenant Name</em>'.
	 * @see lorawan.DeviceInfo#getTenantName()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_TenantName();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getApplicationId <em>Application Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Application Id</em>'.
	 * @see lorawan.DeviceInfo#getApplicationId()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_ApplicationId();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getApplicationName <em>Application Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Application Name</em>'.
	 * @see lorawan.DeviceInfo#getApplicationName()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_ApplicationName();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getDeviceProfileId <em>Device Profile Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Device Profile Id</em>'.
	 * @see lorawan.DeviceInfo#getDeviceProfileId()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_DeviceProfileId();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getDeviceProfileName <em>Device Profile Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Device Profile Name</em>'.
	 * @see lorawan.DeviceInfo#getDeviceProfileName()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_DeviceProfileName();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getDeviceName <em>Device Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Device Name</em>'.
	 * @see lorawan.DeviceInfo#getDeviceName()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_DeviceName();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getDevEui <em>Dev Eui</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dev Eui</em>'.
	 * @see lorawan.DeviceInfo#getDevEui()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_DevEui();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.DeviceInfo#getDevAddr <em>Dev Addr</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dev Addr</em>'.
	 * @see lorawan.DeviceInfo#getDevAddr()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EAttribute getDeviceInfo_DevAddr();

	/**
	 * Returns the meta object for the containment reference '{@link lorawan.DeviceInfo#getTags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Tags</em>'.
	 * @see lorawan.DeviceInfo#getTags()
	 * @see #getDeviceInfo()
	 * @generated
	 */
	EReference getDeviceInfo_Tags();

	/**
	 * Returns the meta object for class '{@link lorawan.Tags <em>Tags</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tags</em>'.
	 * @see lorawan.Tags
	 * @generated
	 */
	EClass getTags();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.Tags#getDev_type <em>Dev type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dev type</em>'.
	 * @see lorawan.Tags#getDev_type()
	 * @see #getTags()
	 * @generated
	 */
	EAttribute getTags_Dev_type();

	/**
	 * Returns the meta object for class '{@link lorawan.RxInfo <em>Rx Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Rx Info</em>'.
	 * @see lorawan.RxInfo
	 * @generated
	 */
	EClass getRxInfo();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.RxInfo#getGatewayId <em>Gateway Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Gateway Id</em>'.
	 * @see lorawan.RxInfo#getGatewayId()
	 * @see #getRxInfo()
	 * @generated
	 */
	EAttribute getRxInfo_GatewayId();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.RxInfo#getUplinkId <em>Uplink Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uplink Id</em>'.
	 * @see lorawan.RxInfo#getUplinkId()
	 * @see #getRxInfo()
	 * @generated
	 */
	EAttribute getRxInfo_UplinkId();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.RxInfo#getTime <em>Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Time</em>'.
	 * @see lorawan.RxInfo#getTime()
	 * @see #getRxInfo()
	 * @generated
	 */
	EAttribute getRxInfo_Time();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.RxInfo#getRssi <em>Rssi</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rssi</em>'.
	 * @see lorawan.RxInfo#getRssi()
	 * @see #getRxInfo()
	 * @generated
	 */
	EAttribute getRxInfo_Rssi();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.RxInfo#getSnr <em>Snr</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Snr</em>'.
	 * @see lorawan.RxInfo#getSnr()
	 * @see #getRxInfo()
	 * @generated
	 */
	EAttribute getRxInfo_Snr();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.RxInfo#getChannel <em>Channel</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Channel</em>'.
	 * @see lorawan.RxInfo#getChannel()
	 * @see #getRxInfo()
	 * @generated
	 */
	EAttribute getRxInfo_Channel();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.RxInfo#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Context</em>'.
	 * @see lorawan.RxInfo#getContext()
	 * @see #getRxInfo()
	 * @generated
	 */
	EAttribute getRxInfo_Context();

	/**
	 * Returns the meta object for the containment reference '{@link lorawan.RxInfo#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Location</em>'.
	 * @see lorawan.RxInfo#getLocation()
	 * @see #getRxInfo()
	 * @generated
	 */
	EReference getRxInfo_Location();

	/**
	 * Returns the meta object for the containment reference '{@link lorawan.RxInfo#getMetadata <em>Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Metadata</em>'.
	 * @see lorawan.RxInfo#getMetadata()
	 * @see #getRxInfo()
	 * @generated
	 */
	EReference getRxInfo_Metadata();

	/**
	 * Returns the meta object for class '{@link lorawan.Location <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Location</em>'.
	 * @see lorawan.Location
	 * @generated
	 */
	EClass getLocation();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.Location#getLatitude <em>Latitude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Latitude</em>'.
	 * @see lorawan.Location#getLatitude()
	 * @see #getLocation()
	 * @generated
	 */
	EAttribute getLocation_Latitude();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.Location#getLongitude <em>Longitude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Longitude</em>'.
	 * @see lorawan.Location#getLongitude()
	 * @see #getLocation()
	 * @generated
	 */
	EAttribute getLocation_Longitude();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.Location#getAltitude <em>Altitude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Altitude</em>'.
	 * @see lorawan.Location#getAltitude()
	 * @see #getLocation()
	 * @generated
	 */
	EAttribute getLocation_Altitude();

	/**
	 * Returns the meta object for class '{@link lorawan.Metadata <em>Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata</em>'.
	 * @see lorawan.Metadata
	 * @generated
	 */
	EClass getMetadata();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.Metadata#getRegion_name <em>Region name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Region name</em>'.
	 * @see lorawan.Metadata#getRegion_name()
	 * @see #getMetadata()
	 * @generated
	 */
	EAttribute getMetadata_Region_name();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.Metadata#getRegion_common_name <em>Region common name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Region common name</em>'.
	 * @see lorawan.Metadata#getRegion_common_name()
	 * @see #getMetadata()
	 * @generated
	 */
	EAttribute getMetadata_Region_common_name();

	/**
	 * Returns the meta object for class '{@link lorawan.TxInfo <em>Tx Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Tx Info</em>'.
	 * @see lorawan.TxInfo
	 * @generated
	 */
	EClass getTxInfo();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.TxInfo#getFrequency <em>Frequency</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Frequency</em>'.
	 * @see lorawan.TxInfo#getFrequency()
	 * @see #getTxInfo()
	 * @generated
	 */
	EAttribute getTxInfo_Frequency();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.TxInfo#getModulation <em>Modulation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Modulation</em>'.
	 * @see lorawan.TxInfo#getModulation()
	 * @see #getTxInfo()
	 * @generated
	 */
	EAttribute getTxInfo_Modulation();

	/**
	 * Returns the meta object for the containment reference '{@link lorawan.TxInfo#getLora <em>Lora</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Lora</em>'.
	 * @see lorawan.TxInfo#getLora()
	 * @see #getTxInfo()
	 * @generated
	 */
	EReference getTxInfo_Lora();

	/**
	 * Returns the meta object for class '{@link lorawan.LoraInfo <em>Lora Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Lora Info</em>'.
	 * @see lorawan.LoraInfo
	 * @generated
	 */
	EClass getLoraInfo();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.LoraInfo#getBandwidth <em>Bandwidth</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bandwidth</em>'.
	 * @see lorawan.LoraInfo#getBandwidth()
	 * @see #getLoraInfo()
	 * @generated
	 */
	EAttribute getLoraInfo_Bandwidth();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.LoraInfo#getSpreadingFactor <em>Spreading Factor</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Spreading Factor</em>'.
	 * @see lorawan.LoraInfo#getSpreadingFactor()
	 * @see #getLoraInfo()
	 * @generated
	 */
	EAttribute getLoraInfo_SpreadingFactor();

	/**
	 * Returns the meta object for the attribute '{@link lorawan.LoraInfo#getCodeRate <em>Code Rate</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Code Rate</em>'.
	 * @see lorawan.LoraInfo#getCodeRate()
	 * @see #getLoraInfo()
	 * @generated
	 */
	EAttribute getLoraInfo_CodeRate();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	LorawanFactory getLorawanFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link lorawan.impl.UplinkMessageImpl <em>Uplink Message</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.UplinkMessageImpl
		 * @see lorawan.impl.LorawanPackageImpl#getUplinkMessage()
		 * @generated
		 */
		EClass UPLINK_MESSAGE = eINSTANCE.getUplinkMessage();

		/**
		 * The meta object literal for the '<em><b>Deduplication Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__DEDUPLICATION_ID = eINSTANCE.getUplinkMessage_DeduplicationId();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__TIME = eINSTANCE.getUplinkMessage_Time();

		/**
		 * The meta object literal for the '<em><b>Adr</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__ADR = eINSTANCE.getUplinkMessage_Adr();

		/**
		 * The meta object literal for the '<em><b>Dr</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__DR = eINSTANCE.getUplinkMessage_Dr();

		/**
		 * The meta object literal for the '<em><b>FCnt</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__FCNT = eINSTANCE.getUplinkMessage_FCnt();

		/**
		 * The meta object literal for the '<em><b>FPort</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__FPORT = eINSTANCE.getUplinkMessage_FPort();

		/**
		 * The meta object literal for the '<em><b>Confirmed</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__CONFIRMED = eINSTANCE.getUplinkMessage_Confirmed();

		/**
		 * The meta object literal for the '<em><b>Data</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UPLINK_MESSAGE__DATA = eINSTANCE.getUplinkMessage_Data();

		/**
		 * The meta object literal for the '<em><b>Device Info</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UPLINK_MESSAGE__DEVICE_INFO = eINSTANCE.getUplinkMessage_DeviceInfo();

		/**
		 * The meta object literal for the '<em><b>Rx Info</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UPLINK_MESSAGE__RX_INFO = eINSTANCE.getUplinkMessage_RxInfo();

		/**
		 * The meta object literal for the '<em><b>Tx Info</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UPLINK_MESSAGE__TX_INFO = eINSTANCE.getUplinkMessage_TxInfo();

		/**
		 * The meta object literal for the '{@link lorawan.impl.DeviceInfoImpl <em>Device Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.DeviceInfoImpl
		 * @see lorawan.impl.LorawanPackageImpl#getDeviceInfo()
		 * @generated
		 */
		EClass DEVICE_INFO = eINSTANCE.getDeviceInfo();

		/**
		 * The meta object literal for the '<em><b>Tenant Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__TENANT_ID = eINSTANCE.getDeviceInfo_TenantId();

		/**
		 * The meta object literal for the '<em><b>Tenant Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__TENANT_NAME = eINSTANCE.getDeviceInfo_TenantName();

		/**
		 * The meta object literal for the '<em><b>Application Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__APPLICATION_ID = eINSTANCE.getDeviceInfo_ApplicationId();

		/**
		 * The meta object literal for the '<em><b>Application Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__APPLICATION_NAME = eINSTANCE.getDeviceInfo_ApplicationName();

		/**
		 * The meta object literal for the '<em><b>Device Profile Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__DEVICE_PROFILE_ID = eINSTANCE.getDeviceInfo_DeviceProfileId();

		/**
		 * The meta object literal for the '<em><b>Device Profile Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__DEVICE_PROFILE_NAME = eINSTANCE.getDeviceInfo_DeviceProfileName();

		/**
		 * The meta object literal for the '<em><b>Device Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__DEVICE_NAME = eINSTANCE.getDeviceInfo_DeviceName();

		/**
		 * The meta object literal for the '<em><b>Dev Eui</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__DEV_EUI = eINSTANCE.getDeviceInfo_DevEui();

		/**
		 * The meta object literal for the '<em><b>Dev Addr</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DEVICE_INFO__DEV_ADDR = eINSTANCE.getDeviceInfo_DevAddr();

		/**
		 * The meta object literal for the '<em><b>Tags</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEVICE_INFO__TAGS = eINSTANCE.getDeviceInfo_Tags();

		/**
		 * The meta object literal for the '{@link lorawan.impl.TagsImpl <em>Tags</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.TagsImpl
		 * @see lorawan.impl.LorawanPackageImpl#getTags()
		 * @generated
		 */
		EClass TAGS = eINSTANCE.getTags();

		/**
		 * The meta object literal for the '<em><b>Dev type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TAGS__DEV_TYPE = eINSTANCE.getTags_Dev_type();

		/**
		 * The meta object literal for the '{@link lorawan.impl.RxInfoImpl <em>Rx Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.RxInfoImpl
		 * @see lorawan.impl.LorawanPackageImpl#getRxInfo()
		 * @generated
		 */
		EClass RX_INFO = eINSTANCE.getRxInfo();

		/**
		 * The meta object literal for the '<em><b>Gateway Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RX_INFO__GATEWAY_ID = eINSTANCE.getRxInfo_GatewayId();

		/**
		 * The meta object literal for the '<em><b>Uplink Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RX_INFO__UPLINK_ID = eINSTANCE.getRxInfo_UplinkId();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RX_INFO__TIME = eINSTANCE.getRxInfo_Time();

		/**
		 * The meta object literal for the '<em><b>Rssi</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RX_INFO__RSSI = eINSTANCE.getRxInfo_Rssi();

		/**
		 * The meta object literal for the '<em><b>Snr</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RX_INFO__SNR = eINSTANCE.getRxInfo_Snr();

		/**
		 * The meta object literal for the '<em><b>Channel</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RX_INFO__CHANNEL = eINSTANCE.getRxInfo_Channel();

		/**
		 * The meta object literal for the '<em><b>Context</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RX_INFO__CONTEXT = eINSTANCE.getRxInfo_Context();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RX_INFO__LOCATION = eINSTANCE.getRxInfo_Location();

		/**
		 * The meta object literal for the '<em><b>Metadata</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RX_INFO__METADATA = eINSTANCE.getRxInfo_Metadata();

		/**
		 * The meta object literal for the '{@link lorawan.impl.LocationImpl <em>Location</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.LocationImpl
		 * @see lorawan.impl.LorawanPackageImpl#getLocation()
		 * @generated
		 */
		EClass LOCATION = eINSTANCE.getLocation();

		/**
		 * The meta object literal for the '<em><b>Latitude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCATION__LATITUDE = eINSTANCE.getLocation_Latitude();

		/**
		 * The meta object literal for the '<em><b>Longitude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCATION__LONGITUDE = eINSTANCE.getLocation_Longitude();

		/**
		 * The meta object literal for the '<em><b>Altitude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCATION__ALTITUDE = eINSTANCE.getLocation_Altitude();

		/**
		 * The meta object literal for the '{@link lorawan.impl.MetadataImpl <em>Metadata</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.MetadataImpl
		 * @see lorawan.impl.LorawanPackageImpl#getMetadata()
		 * @generated
		 */
		EClass METADATA = eINSTANCE.getMetadata();

		/**
		 * The meta object literal for the '<em><b>Region name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METADATA__REGION_NAME = eINSTANCE.getMetadata_Region_name();

		/**
		 * The meta object literal for the '<em><b>Region common name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute METADATA__REGION_COMMON_NAME = eINSTANCE.getMetadata_Region_common_name();

		/**
		 * The meta object literal for the '{@link lorawan.impl.TxInfoImpl <em>Tx Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.TxInfoImpl
		 * @see lorawan.impl.LorawanPackageImpl#getTxInfo()
		 * @generated
		 */
		EClass TX_INFO = eINSTANCE.getTxInfo();

		/**
		 * The meta object literal for the '<em><b>Frequency</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TX_INFO__FREQUENCY = eINSTANCE.getTxInfo_Frequency();

		/**
		 * The meta object literal for the '<em><b>Modulation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TX_INFO__MODULATION = eINSTANCE.getTxInfo_Modulation();

		/**
		 * The meta object literal for the '<em><b>Lora</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TX_INFO__LORA = eINSTANCE.getTxInfo_Lora();

		/**
		 * The meta object literal for the '{@link lorawan.impl.LoraInfoImpl <em>Lora Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see lorawan.impl.LoraInfoImpl
		 * @see lorawan.impl.LorawanPackageImpl#getLoraInfo()
		 * @generated
		 */
		EClass LORA_INFO = eINSTANCE.getLoraInfo();

		/**
		 * The meta object literal for the '<em><b>Bandwidth</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LORA_INFO__BANDWIDTH = eINSTANCE.getLoraInfo_Bandwidth();

		/**
		 * The meta object literal for the '<em><b>Spreading Factor</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LORA_INFO__SPREADING_FACTOR = eINSTANCE.getLoraInfo_SpreadingFactor();

		/**
		 * The meta object literal for the '<em><b>Code Rate</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LORA_INFO__CODE_RATE = eINSTANCE.getLoraInfo_CodeRate();

	}

} //LorawanPackage
