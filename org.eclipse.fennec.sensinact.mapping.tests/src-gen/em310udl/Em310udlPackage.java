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
package em310udl;


import lorawan.LorawanPackage;

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
 * @see em310udl.Em310udlFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = Em310udlPackage.eNS_URI, genModel = "/model/lora-test.genmodel", genModelSourceLocations = {"model/lora-test.genmodel","org.eclipse.fennec.sensinact.mapping.tests/model/lora-test.genmodel"}, ecore = "/model/em310udl-message.ecore", ecoreSourceLocations = "/model/em310udl-message.ecore")
public interface Em310udlPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "em310udl";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.example.org/lorawan/specific/em310udl";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "em310udl";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	Em310udlPackage eINSTANCE = em310udl.impl.Em310udlPackageImpl.init();

	/**
	 * The meta object id for the '{@link em310udl.impl.DecodedObjectImpl <em>Decoded Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see em310udl.impl.DecodedObjectImpl
	 * @see em310udl.impl.Em310udlPackageImpl#getDecodedObject()
	 * @generated
	 */
	int DECODED_OBJECT = 0;

	/**
	 * The feature id for the '<em><b>Distance</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__DISTANCE = 0;

	/**
	 * The feature id for the '<em><b>Position</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__POSITION = 1;

	/**
	 * The feature id for the '<em><b>Battery</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__BATTERY = 2;

	/**
	 * The number of structural features of the '<em>Decoded Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Decoded Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link em310udl.impl.EM310UDLUplinkImpl <em>EM310UDL Uplink</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see em310udl.impl.EM310UDLUplinkImpl
	 * @see em310udl.impl.Em310udlPackageImpl#getEM310UDLUplink()
	 * @generated
	 */
	int EM310UDL_UPLINK = 1;

	/**
	 * The feature id for the '<em><b>Deduplication Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__DEDUPLICATION_ID = LorawanPackage.UPLINK_MESSAGE__DEDUPLICATION_ID;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__TIME = LorawanPackage.UPLINK_MESSAGE__TIME;

	/**
	 * The feature id for the '<em><b>Adr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__ADR = LorawanPackage.UPLINK_MESSAGE__ADR;

	/**
	 * The feature id for the '<em><b>Dr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__DR = LorawanPackage.UPLINK_MESSAGE__DR;

	/**
	 * The feature id for the '<em><b>FCnt</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__FCNT = LorawanPackage.UPLINK_MESSAGE__FCNT;

	/**
	 * The feature id for the '<em><b>FPort</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__FPORT = LorawanPackage.UPLINK_MESSAGE__FPORT;

	/**
	 * The feature id for the '<em><b>Confirmed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__CONFIRMED = LorawanPackage.UPLINK_MESSAGE__CONFIRMED;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__DATA = LorawanPackage.UPLINK_MESSAGE__DATA;

	/**
	 * The feature id for the '<em><b>Device Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__DEVICE_INFO = LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO;

	/**
	 * The feature id for the '<em><b>Rx Info</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__RX_INFO = LorawanPackage.UPLINK_MESSAGE__RX_INFO;

	/**
	 * The feature id for the '<em><b>Tx Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__TX_INFO = LorawanPackage.UPLINK_MESSAGE__TX_INFO;

	/**
	 * The feature id for the '<em><b>Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK__OBJECT = LorawanPackage.UPLINK_MESSAGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>EM310UDL Uplink</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK_FEATURE_COUNT = LorawanPackage.UPLINK_MESSAGE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>EM310UDL Uplink</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EM310UDL_UPLINK_OPERATION_COUNT = LorawanPackage.UPLINK_MESSAGE_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link em310udl.DecodedObject <em>Decoded Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Decoded Object</em>'.
	 * @see em310udl.DecodedObject
	 * @generated
	 */
	EClass getDecodedObject();

	/**
	 * Returns the meta object for the attribute '{@link em310udl.DecodedObject#getDistance <em>Distance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Distance</em>'.
	 * @see em310udl.DecodedObject#getDistance()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Distance();

	/**
	 * Returns the meta object for the attribute '{@link em310udl.DecodedObject#getPosition <em>Position</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Position</em>'.
	 * @see em310udl.DecodedObject#getPosition()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Position();

	/**
	 * Returns the meta object for the attribute '{@link em310udl.DecodedObject#getBattery <em>Battery</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Battery</em>'.
	 * @see em310udl.DecodedObject#getBattery()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Battery();

	/**
	 * Returns the meta object for class '{@link em310udl.EM310UDLUplink <em>EM310UDL Uplink</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>EM310UDL Uplink</em>'.
	 * @see em310udl.EM310UDLUplink
	 * @generated
	 */
	EClass getEM310UDLUplink();

	/**
	 * Returns the meta object for the containment reference '{@link em310udl.EM310UDLUplink#getObject <em>Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Object</em>'.
	 * @see em310udl.EM310UDLUplink#getObject()
	 * @see #getEM310UDLUplink()
	 * @generated
	 */
	EReference getEM310UDLUplink_Object();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	Em310udlFactory getEm310udlFactory();

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
		 * The meta object literal for the '{@link em310udl.impl.DecodedObjectImpl <em>Decoded Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see em310udl.impl.DecodedObjectImpl
		 * @see em310udl.impl.Em310udlPackageImpl#getDecodedObject()
		 * @generated
		 */
		EClass DECODED_OBJECT = eINSTANCE.getDecodedObject();

		/**
		 * The meta object literal for the '<em><b>Distance</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__DISTANCE = eINSTANCE.getDecodedObject_Distance();

		/**
		 * The meta object literal for the '<em><b>Position</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__POSITION = eINSTANCE.getDecodedObject_Position();

		/**
		 * The meta object literal for the '<em><b>Battery</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__BATTERY = eINSTANCE.getDecodedObject_Battery();

		/**
		 * The meta object literal for the '{@link em310udl.impl.EM310UDLUplinkImpl <em>EM310UDL Uplink</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see em310udl.impl.EM310UDLUplinkImpl
		 * @see em310udl.impl.Em310udlPackageImpl#getEM310UDLUplink()
		 * @generated
		 */
		EClass EM310UDL_UPLINK = eINSTANCE.getEM310UDLUplink();

		/**
		 * The meta object literal for the '<em><b>Object</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference EM310UDL_UPLINK__OBJECT = eINSTANCE.getEM310UDLUplink_Object();

	}

} //Em310udlPackage
