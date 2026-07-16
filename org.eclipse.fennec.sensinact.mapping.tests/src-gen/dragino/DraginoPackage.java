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
 * @see dragino.DraginoFactory
 * @model kind="package"
 * @generated
 */
@ProviderType
@EPackage(uri = DraginoPackage.eNS_URI, genModel = "/model/lora-test.genmodel", genModelSourceLocations = {"model/lora-test.genmodel","org.eclipse.fennec.sensinact.mapping.tests/model/lora-test.genmodel"}, ecore = "/model/dragino-message.ecore", ecoreSourceLocations = "/model/dragino-message.ecore")
public interface DraginoPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "dragino";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://eclipse.org/fennec/lorawan/dragino";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "dragino";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	DraginoPackage eINSTANCE = dragino.impl.DraginoPackageImpl.init();

	/**
	 * The meta object id for the '{@link dragino.impl.DecodedObjectImpl <em>Decoded Object</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dragino.impl.DecodedObjectImpl
	 * @see dragino.impl.DraginoPackageImpl#getDecodedObject()
	 * @generated
	 */
	int DECODED_OBJECT = 0;

	/**
	 * The feature id for the '<em><b>Bat V</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__BAT_V = 0;

	/**
	 * The feature id for the '<em><b>Temp DS18B20</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__TEMP_DS18B20 = 1;

	/**
	 * The feature id for the '<em><b>Temp SOIL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__TEMP_SOIL = 2;

	/**
	 * The feature id for the '<em><b>Water SOIL f</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__WATER_SOIL_F = 3;

	/**
	 * The feature id for the '<em><b>Conduct SOIL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__CONDUCT_SOIL = 4;

	/**
	 * The feature id for the '<em><b>Iflag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__IFLAG = 5;

	/**
	 * The feature id for the '<em><b>Water SOIL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__WATER_SOIL = 6;

	/**
	 * The feature id for the '<em><b>Temp SOIL f</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__TEMP_SOIL_F = 7;

	/**
	 * The feature id for the '<em><b>Sflag</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__SFLAG = 8;

	/**
	 * The feature id for the '<em><b>Conduct SOIL f</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__CONDUCT_SOIL_F = 9;

	/**
	 * The feature id for the '<em><b>Mod</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT__MOD = 10;

	/**
	 * The number of structural features of the '<em>Decoded Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT_FEATURE_COUNT = 11;

	/**
	 * The number of operations of the '<em>Decoded Object</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DECODED_OBJECT_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link dragino.impl.DraginoLSE01UplinkImpl <em>LSE01 Uplink</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see dragino.impl.DraginoLSE01UplinkImpl
	 * @see dragino.impl.DraginoPackageImpl#getDraginoLSE01Uplink()
	 * @generated
	 */
	int DRAGINO_LSE01_UPLINK = 1;

	/**
	 * The feature id for the '<em><b>Deduplication Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__DEDUPLICATION_ID = LorawanPackage.UPLINK_MESSAGE__DEDUPLICATION_ID;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__TIME = LorawanPackage.UPLINK_MESSAGE__TIME;

	/**
	 * The feature id for the '<em><b>Adr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__ADR = LorawanPackage.UPLINK_MESSAGE__ADR;

	/**
	 * The feature id for the '<em><b>Dr</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__DR = LorawanPackage.UPLINK_MESSAGE__DR;

	/**
	 * The feature id for the '<em><b>FCnt</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__FCNT = LorawanPackage.UPLINK_MESSAGE__FCNT;

	/**
	 * The feature id for the '<em><b>FPort</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__FPORT = LorawanPackage.UPLINK_MESSAGE__FPORT;

	/**
	 * The feature id for the '<em><b>Confirmed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__CONFIRMED = LorawanPackage.UPLINK_MESSAGE__CONFIRMED;

	/**
	 * The feature id for the '<em><b>Data</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__DATA = LorawanPackage.UPLINK_MESSAGE__DATA;

	/**
	 * The feature id for the '<em><b>Device Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__DEVICE_INFO = LorawanPackage.UPLINK_MESSAGE__DEVICE_INFO;

	/**
	 * The feature id for the '<em><b>Rx Info</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__RX_INFO = LorawanPackage.UPLINK_MESSAGE__RX_INFO;

	/**
	 * The feature id for the '<em><b>Tx Info</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__TX_INFO = LorawanPackage.UPLINK_MESSAGE__TX_INFO;

	/**
	 * The feature id for the '<em><b>Object</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK__OBJECT = LorawanPackage.UPLINK_MESSAGE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>LSE01 Uplink</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK_FEATURE_COUNT = LorawanPackage.UPLINK_MESSAGE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>LSE01 Uplink</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DRAGINO_LSE01_UPLINK_OPERATION_COUNT = LorawanPackage.UPLINK_MESSAGE_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link dragino.DecodedObject <em>Decoded Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Decoded Object</em>'.
	 * @see dragino.DecodedObject
	 * @generated
	 */
	EClass getDecodedObject();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getBatV <em>Bat V</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Bat V</em>'.
	 * @see dragino.DecodedObject#getBatV()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_BatV();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getTemp_DS18B20 <em>Temp DS18B20</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Temp DS18B20</em>'.
	 * @see dragino.DecodedObject#getTemp_DS18B20()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Temp_DS18B20();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getTemp_SOIL <em>Temp SOIL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Temp SOIL</em>'.
	 * @see dragino.DecodedObject#getTemp_SOIL()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Temp_SOIL();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getWater_SOIL_f <em>Water SOIL f</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Water SOIL f</em>'.
	 * @see dragino.DecodedObject#getWater_SOIL_f()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Water_SOIL_f();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getConduct_SOIL <em>Conduct SOIL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conduct SOIL</em>'.
	 * @see dragino.DecodedObject#getConduct_SOIL()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Conduct_SOIL();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getI_flag <em>Iflag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Iflag</em>'.
	 * @see dragino.DecodedObject#getI_flag()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_I_flag();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getWater_SOIL <em>Water SOIL</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Water SOIL</em>'.
	 * @see dragino.DecodedObject#getWater_SOIL()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Water_SOIL();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getTemp_SOIL_f <em>Temp SOIL f</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Temp SOIL f</em>'.
	 * @see dragino.DecodedObject#getTemp_SOIL_f()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Temp_SOIL_f();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getS_flag <em>Sflag</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sflag</em>'.
	 * @see dragino.DecodedObject#getS_flag()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_S_flag();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getConduct_SOIL_f <em>Conduct SOIL f</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Conduct SOIL f</em>'.
	 * @see dragino.DecodedObject#getConduct_SOIL_f()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Conduct_SOIL_f();

	/**
	 * Returns the meta object for the attribute '{@link dragino.DecodedObject#getMod <em>Mod</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mod</em>'.
	 * @see dragino.DecodedObject#getMod()
	 * @see #getDecodedObject()
	 * @generated
	 */
	EAttribute getDecodedObject_Mod();

	/**
	 * Returns the meta object for class '{@link dragino.DraginoLSE01Uplink <em>LSE01 Uplink</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>LSE01 Uplink</em>'.
	 * @see dragino.DraginoLSE01Uplink
	 * @generated
	 */
	EClass getDraginoLSE01Uplink();

	/**
	 * Returns the meta object for the containment reference '{@link dragino.DraginoLSE01Uplink#getObject <em>Object</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Object</em>'.
	 * @see dragino.DraginoLSE01Uplink#getObject()
	 * @see #getDraginoLSE01Uplink()
	 * @generated
	 */
	EReference getDraginoLSE01Uplink_Object();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DraginoFactory getDraginoFactory();

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
		 * The meta object literal for the '{@link dragino.impl.DecodedObjectImpl <em>Decoded Object</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dragino.impl.DecodedObjectImpl
		 * @see dragino.impl.DraginoPackageImpl#getDecodedObject()
		 * @generated
		 */
		EClass DECODED_OBJECT = eINSTANCE.getDecodedObject();

		/**
		 * The meta object literal for the '<em><b>Bat V</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__BAT_V = eINSTANCE.getDecodedObject_BatV();

		/**
		 * The meta object literal for the '<em><b>Temp DS18B20</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__TEMP_DS18B20 = eINSTANCE.getDecodedObject_Temp_DS18B20();

		/**
		 * The meta object literal for the '<em><b>Temp SOIL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__TEMP_SOIL = eINSTANCE.getDecodedObject_Temp_SOIL();

		/**
		 * The meta object literal for the '<em><b>Water SOIL f</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__WATER_SOIL_F = eINSTANCE.getDecodedObject_Water_SOIL_f();

		/**
		 * The meta object literal for the '<em><b>Conduct SOIL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__CONDUCT_SOIL = eINSTANCE.getDecodedObject_Conduct_SOIL();

		/**
		 * The meta object literal for the '<em><b>Iflag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__IFLAG = eINSTANCE.getDecodedObject_I_flag();

		/**
		 * The meta object literal for the '<em><b>Water SOIL</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__WATER_SOIL = eINSTANCE.getDecodedObject_Water_SOIL();

		/**
		 * The meta object literal for the '<em><b>Temp SOIL f</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__TEMP_SOIL_F = eINSTANCE.getDecodedObject_Temp_SOIL_f();

		/**
		 * The meta object literal for the '<em><b>Sflag</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__SFLAG = eINSTANCE.getDecodedObject_S_flag();

		/**
		 * The meta object literal for the '<em><b>Conduct SOIL f</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__CONDUCT_SOIL_F = eINSTANCE.getDecodedObject_Conduct_SOIL_f();

		/**
		 * The meta object literal for the '<em><b>Mod</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DECODED_OBJECT__MOD = eINSTANCE.getDecodedObject_Mod();

		/**
		 * The meta object literal for the '{@link dragino.impl.DraginoLSE01UplinkImpl <em>LSE01 Uplink</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see dragino.impl.DraginoLSE01UplinkImpl
		 * @see dragino.impl.DraginoPackageImpl#getDraginoLSE01Uplink()
		 * @generated
		 */
		EClass DRAGINO_LSE01_UPLINK = eINSTANCE.getDraginoLSE01Uplink();

		/**
		 * The meta object literal for the '<em><b>Object</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DRAGINO_LSE01_UPLINK__OBJECT = eINSTANCE.getDraginoLSE01Uplink_Object();

	}

} //DraginoPackage
