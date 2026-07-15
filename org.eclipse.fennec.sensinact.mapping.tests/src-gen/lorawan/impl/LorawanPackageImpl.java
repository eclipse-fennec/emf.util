/**
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

import dragino.DraginoPackage;

import dragino.impl.DraginoPackageImpl;

import em310udl.Em310udlPackage;

import em310udl.impl.Em310udlPackageImpl;

import lorawan.DeviceInfo;
import lorawan.Location;
import lorawan.LoraInfo;
import lorawan.LorawanFactory;
import lorawan.LorawanPackage;
import lorawan.Metadata;
import lorawan.RxInfo;
import lorawan.Tags;
import lorawan.TxInfo;
import lorawan.UplinkMessage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LorawanPackageImpl extends EPackageImpl implements LorawanPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass uplinkMessageEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deviceInfoEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass tagsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass rxInfoEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass locationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass txInfoEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass loraInfoEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see lorawan.LorawanPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private LorawanPackageImpl() {
		super(eNS_URI, LorawanFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link LorawanPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static LorawanPackage init() {
		if (isInited) return (LorawanPackage)EPackage.Registry.INSTANCE.getEPackage(LorawanPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredLorawanPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		LorawanPackageImpl theLorawanPackage = registeredLorawanPackage instanceof LorawanPackageImpl ? (LorawanPackageImpl)registeredLorawanPackage : new LorawanPackageImpl();

		isInited = true;

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(DraginoPackage.eNS_URI);
		DraginoPackageImpl theDraginoPackage = (DraginoPackageImpl)(registeredPackage instanceof DraginoPackageImpl ? registeredPackage : DraginoPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(Em310udlPackage.eNS_URI);
		Em310udlPackageImpl theEm310udlPackage = (Em310udlPackageImpl)(registeredPackage instanceof Em310udlPackageImpl ? registeredPackage : Em310udlPackage.eINSTANCE);

		// Create package meta-data objects
		theLorawanPackage.createPackageContents();
		theDraginoPackage.createPackageContents();
		theEm310udlPackage.createPackageContents();

		// Initialize created meta-data
		theLorawanPackage.initializePackageContents();
		theDraginoPackage.initializePackageContents();
		theEm310udlPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theLorawanPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(LorawanPackage.eNS_URI, theLorawanPackage);
		return theLorawanPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getUplinkMessage() {
		return uplinkMessageEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_DeduplicationId() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_Time() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_Adr() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_Dr() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_FCnt() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_FPort() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_Confirmed() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUplinkMessage_Data() {
		return (EAttribute)uplinkMessageEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getUplinkMessage_DeviceInfo() {
		return (EReference)uplinkMessageEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getUplinkMessage_RxInfo() {
		return (EReference)uplinkMessageEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getUplinkMessage_TxInfo() {
		return (EReference)uplinkMessageEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDeviceInfo() {
		return deviceInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_TenantId() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_TenantName() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_ApplicationId() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_ApplicationName() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_DeviceProfileId() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_DeviceProfileName() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_DeviceName() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_DevEui() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeviceInfo_DevAddr() {
		return (EAttribute)deviceInfoEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDeviceInfo_Tags() {
		return (EReference)deviceInfoEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTags() {
		return tagsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTags_Dev_type() {
		return (EAttribute)tagsEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getRxInfo() {
		return rxInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRxInfo_GatewayId() {
		return (EAttribute)rxInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRxInfo_UplinkId() {
		return (EAttribute)rxInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRxInfo_Time() {
		return (EAttribute)rxInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRxInfo_Rssi() {
		return (EAttribute)rxInfoEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRxInfo_Snr() {
		return (EAttribute)rxInfoEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRxInfo_Channel() {
		return (EAttribute)rxInfoEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getRxInfo_Context() {
		return (EAttribute)rxInfoEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRxInfo_Location() {
		return (EReference)rxInfoEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getRxInfo_Metadata() {
		return (EReference)rxInfoEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLocation() {
		return locationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLocation_Latitude() {
		return (EAttribute)locationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLocation_Longitude() {
		return (EAttribute)locationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLocation_Altitude() {
		return (EAttribute)locationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadata() {
		return metadataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetadata_Region_name() {
		return (EAttribute)metadataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMetadata_Region_common_name() {
		return (EAttribute)metadataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTxInfo() {
		return txInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTxInfo_Frequency() {
		return (EAttribute)txInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTxInfo_Modulation() {
		return (EAttribute)txInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getTxInfo_Lora() {
		return (EReference)txInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLoraInfo() {
		return loraInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLoraInfo_Bandwidth() {
		return (EAttribute)loraInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLoraInfo_SpreadingFactor() {
		return (EAttribute)loraInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLoraInfo_CodeRate() {
		return (EAttribute)loraInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LorawanFactory getLorawanFactory() {
		return (LorawanFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		uplinkMessageEClass = createEClass(UPLINK_MESSAGE);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__DEDUPLICATION_ID);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__TIME);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__ADR);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__DR);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__FCNT);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__FPORT);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__CONFIRMED);
		createEAttribute(uplinkMessageEClass, UPLINK_MESSAGE__DATA);
		createEReference(uplinkMessageEClass, UPLINK_MESSAGE__DEVICE_INFO);
		createEReference(uplinkMessageEClass, UPLINK_MESSAGE__RX_INFO);
		createEReference(uplinkMessageEClass, UPLINK_MESSAGE__TX_INFO);

		deviceInfoEClass = createEClass(DEVICE_INFO);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__TENANT_ID);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__TENANT_NAME);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__APPLICATION_ID);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__APPLICATION_NAME);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__DEVICE_PROFILE_ID);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__DEVICE_PROFILE_NAME);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__DEVICE_NAME);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__DEV_EUI);
		createEAttribute(deviceInfoEClass, DEVICE_INFO__DEV_ADDR);
		createEReference(deviceInfoEClass, DEVICE_INFO__TAGS);

		tagsEClass = createEClass(TAGS);
		createEAttribute(tagsEClass, TAGS__DEV_TYPE);

		rxInfoEClass = createEClass(RX_INFO);
		createEAttribute(rxInfoEClass, RX_INFO__GATEWAY_ID);
		createEAttribute(rxInfoEClass, RX_INFO__UPLINK_ID);
		createEAttribute(rxInfoEClass, RX_INFO__TIME);
		createEAttribute(rxInfoEClass, RX_INFO__RSSI);
		createEAttribute(rxInfoEClass, RX_INFO__SNR);
		createEAttribute(rxInfoEClass, RX_INFO__CHANNEL);
		createEAttribute(rxInfoEClass, RX_INFO__CONTEXT);
		createEReference(rxInfoEClass, RX_INFO__LOCATION);
		createEReference(rxInfoEClass, RX_INFO__METADATA);

		locationEClass = createEClass(LOCATION);
		createEAttribute(locationEClass, LOCATION__LATITUDE);
		createEAttribute(locationEClass, LOCATION__LONGITUDE);
		createEAttribute(locationEClass, LOCATION__ALTITUDE);

		metadataEClass = createEClass(METADATA);
		createEAttribute(metadataEClass, METADATA__REGION_NAME);
		createEAttribute(metadataEClass, METADATA__REGION_COMMON_NAME);

		txInfoEClass = createEClass(TX_INFO);
		createEAttribute(txInfoEClass, TX_INFO__FREQUENCY);
		createEAttribute(txInfoEClass, TX_INFO__MODULATION);
		createEReference(txInfoEClass, TX_INFO__LORA);

		loraInfoEClass = createEClass(LORA_INFO);
		createEAttribute(loraInfoEClass, LORA_INFO__BANDWIDTH);
		createEAttribute(loraInfoEClass, LORA_INFO__SPREADING_FACTOR);
		createEAttribute(loraInfoEClass, LORA_INFO__CODE_RATE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes, features, and operations; add parameters
		initEClass(uplinkMessageEClass, UplinkMessage.class, "UplinkMessage", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUplinkMessage_DeduplicationId(), ecorePackage.getEString(), "deduplicationId", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUplinkMessage_Time(), ecorePackage.getEString(), "time", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUplinkMessage_Adr(), ecorePackage.getEBoolean(), "adr", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUplinkMessage_Dr(), ecorePackage.getEInt(), "dr", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUplinkMessage_FCnt(), ecorePackage.getEInt(), "fCnt", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUplinkMessage_FPort(), ecorePackage.getEInt(), "fPort", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUplinkMessage_Confirmed(), ecorePackage.getEBoolean(), "confirmed", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUplinkMessage_Data(), ecorePackage.getEString(), "data", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUplinkMessage_DeviceInfo(), this.getDeviceInfo(), null, "deviceInfo", null, 1, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUplinkMessage_RxInfo(), this.getRxInfo(), null, "rxInfo", null, 0, -1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getUplinkMessage_TxInfo(), this.getTxInfo(), null, "txInfo", null, 0, 1, UplinkMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deviceInfoEClass, DeviceInfo.class, "DeviceInfo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDeviceInfo_TenantId(), ecorePackage.getEString(), "tenantId", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_TenantName(), ecorePackage.getEString(), "tenantName", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_ApplicationId(), ecorePackage.getEString(), "applicationId", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_ApplicationName(), ecorePackage.getEString(), "applicationName", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_DeviceProfileId(), ecorePackage.getEString(), "deviceProfileId", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_DeviceProfileName(), ecorePackage.getEString(), "deviceProfileName", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_DeviceName(), ecorePackage.getEString(), "deviceName", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_DevEui(), ecorePackage.getEString(), "devEui", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeviceInfo_DevAddr(), ecorePackage.getEString(), "devAddr", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getDeviceInfo_Tags(), this.getTags(), null, "tags", null, 0, 1, DeviceInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tagsEClass, Tags.class, "Tags", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTags_Dev_type(), ecorePackage.getEString(), "dev_type", null, 0, 1, Tags.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(rxInfoEClass, RxInfo.class, "RxInfo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getRxInfo_GatewayId(), ecorePackage.getEString(), "gatewayId", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRxInfo_UplinkId(), ecorePackage.getEInt(), "uplinkId", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRxInfo_Time(), ecorePackage.getEString(), "time", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRxInfo_Rssi(), ecorePackage.getEInt(), "rssi", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRxInfo_Snr(), ecorePackage.getEDouble(), "snr", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRxInfo_Channel(), ecorePackage.getEInt(), "channel", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getRxInfo_Context(), ecorePackage.getEString(), "context", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRxInfo_Location(), this.getLocation(), null, "location", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getRxInfo_Metadata(), this.getMetadata(), null, "metadata", null, 0, 1, RxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(locationEClass, Location.class, "Location", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLocation_Latitude(), ecorePackage.getEDouble(), "latitude", null, 0, 1, Location.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLocation_Longitude(), ecorePackage.getEDouble(), "longitude", null, 0, 1, Location.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLocation_Altitude(), ecorePackage.getEDouble(), "altitude", null, 0, 1, Location.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(metadataEClass, Metadata.class, "Metadata", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMetadata_Region_name(), ecorePackage.getEString(), "region_name", null, 0, 1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMetadata_Region_common_name(), ecorePackage.getEString(), "region_common_name", null, 0, 1, Metadata.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(txInfoEClass, TxInfo.class, "TxInfo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTxInfo_Frequency(), ecorePackage.getELong(), "frequency", null, 0, 1, TxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTxInfo_Modulation(), ecorePackage.getEString(), "modulation", null, 0, 1, TxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTxInfo_Lora(), this.getLoraInfo(), null, "lora", null, 0, 1, TxInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(loraInfoEClass, LoraInfo.class, "LoraInfo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLoraInfo_Bandwidth(), ecorePackage.getEInt(), "bandwidth", null, 0, 1, LoraInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLoraInfo_SpreadingFactor(), ecorePackage.getEInt(), "spreadingFactor", null, 0, 1, LoraInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLoraInfo_CodeRate(), ecorePackage.getEString(), "codeRate", null, 0, 1, LoraInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //LorawanPackageImpl
