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
package dragino.impl;

import dragino.DecodedObject;
import dragino.DraginoFactory;
import dragino.DraginoLSE01Uplink;
import dragino.DraginoPackage;

import em310udl.Em310udlPackage;

import em310udl.impl.Em310udlPackageImpl;

import lorawan.LorawanPackage;

import lorawan.impl.LorawanPackageImpl;

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
public class DraginoPackageImpl extends EPackageImpl implements DraginoPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass decodedObjectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass draginoLSE01UplinkEClass = null;

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
	 * @see dragino.DraginoPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private DraginoPackageImpl() {
		super(eNS_URI, DraginoFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link DraginoPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static DraginoPackage init() {
		if (isInited) return (DraginoPackage)EPackage.Registry.INSTANCE.getEPackage(DraginoPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredDraginoPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		DraginoPackageImpl theDraginoPackage = registeredDraginoPackage instanceof DraginoPackageImpl ? (DraginoPackageImpl)registeredDraginoPackage : new DraginoPackageImpl();

		isInited = true;

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(Em310udlPackage.eNS_URI);
		Em310udlPackageImpl theEm310udlPackage = (Em310udlPackageImpl)(registeredPackage instanceof Em310udlPackageImpl ? registeredPackage : Em310udlPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(LorawanPackage.eNS_URI);
		LorawanPackageImpl theLorawanPackage = (LorawanPackageImpl)(registeredPackage instanceof LorawanPackageImpl ? registeredPackage : LorawanPackage.eINSTANCE);

		// Create package meta-data objects
		theDraginoPackage.createPackageContents();
		theEm310udlPackage.createPackageContents();
		theLorawanPackage.createPackageContents();

		// Initialize created meta-data
		theDraginoPackage.initializePackageContents();
		theEm310udlPackage.initializePackageContents();
		theLorawanPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theDraginoPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(DraginoPackage.eNS_URI, theDraginoPackage);
		return theDraginoPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDecodedObject() {
		return decodedObjectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_BatV() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Temp_DS18B20() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Temp_SOIL() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Water_SOIL_f() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Conduct_SOIL() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_I_flag() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Water_SOIL() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Temp_SOIL_f() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_S_flag() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Conduct_SOIL_f() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Mod() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDraginoLSE01Uplink() {
		return draginoLSE01UplinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getDraginoLSE01Uplink_Object() {
		return (EReference)draginoLSE01UplinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DraginoFactory getDraginoFactory() {
		return (DraginoFactory)getEFactoryInstance();
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
		decodedObjectEClass = createEClass(DECODED_OBJECT);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__BAT_V);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__TEMP_DS18B20);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__TEMP_SOIL);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__WATER_SOIL_F);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__CONDUCT_SOIL);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__IFLAG);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__WATER_SOIL);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__TEMP_SOIL_F);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__SFLAG);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__CONDUCT_SOIL_F);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__MOD);

		draginoLSE01UplinkEClass = createEClass(DRAGINO_LSE01_UPLINK);
		createEReference(draginoLSE01UplinkEClass, DRAGINO_LSE01_UPLINK__OBJECT);
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

		// Obtain other dependent packages
		LorawanPackage theLorawanPackage = (LorawanPackage)EPackage.Registry.INSTANCE.getEPackage(LorawanPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		draginoLSE01UplinkEClass.getESuperTypes().add(theLorawanPackage.getUplinkMessage());

		// Initialize classes, features, and operations; add parameters
		initEClass(decodedObjectEClass, DecodedObject.class, "DecodedObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDecodedObject_BatV(), ecorePackage.getEDouble(), "batV", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Temp_DS18B20(), ecorePackage.getEString(), "temp_DS18B20", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Temp_SOIL(), ecorePackage.getEDouble(), "temp_SOIL", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Water_SOIL_f(), ecorePackage.getEDouble(), "water_SOIL_f", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Conduct_SOIL(), ecorePackage.getEDouble(), "conduct_SOIL", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_I_flag(), ecorePackage.getEDouble(), "i_flag", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Water_SOIL(), ecorePackage.getEString(), "water_SOIL", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Temp_SOIL_f(), ecorePackage.getEDouble(), "temp_SOIL_f", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_S_flag(), ecorePackage.getEDouble(), "s_flag", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Conduct_SOIL_f(), ecorePackage.getEDouble(), "conduct_SOIL_f", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Mod(), ecorePackage.getEDouble(), "mod", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(draginoLSE01UplinkEClass, DraginoLSE01Uplink.class, "DraginoLSE01Uplink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDraginoLSE01Uplink_Object(), this.getDecodedObject(), null, "object", null, 0, 1, DraginoLSE01Uplink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //DraginoPackageImpl
