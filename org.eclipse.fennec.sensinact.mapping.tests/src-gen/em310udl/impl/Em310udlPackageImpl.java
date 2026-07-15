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
package em310udl.impl;

import dragino.DraginoPackage;

import dragino.impl.DraginoPackageImpl;

import em310udl.DecodedObject;
import em310udl.EM310UDLUplink;
import em310udl.Em310udlFactory;
import em310udl.Em310udlPackage;

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
public class Em310udlPackageImpl extends EPackageImpl implements Em310udlPackage {
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
	private EClass em310UDLUplinkEClass = null;

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
	 * @see em310udl.Em310udlPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private Em310udlPackageImpl() {
		super(eNS_URI, Em310udlFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link Em310udlPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static Em310udlPackage init() {
		if (isInited) return (Em310udlPackage)EPackage.Registry.INSTANCE.getEPackage(Em310udlPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredEm310udlPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		Em310udlPackageImpl theEm310udlPackage = registeredEm310udlPackage instanceof Em310udlPackageImpl ? (Em310udlPackageImpl)registeredEm310udlPackage : new Em310udlPackageImpl();

		isInited = true;

		// Obtain or create and register interdependencies
		Object registeredPackage = EPackage.Registry.INSTANCE.getEPackage(DraginoPackage.eNS_URI);
		DraginoPackageImpl theDraginoPackage = (DraginoPackageImpl)(registeredPackage instanceof DraginoPackageImpl ? registeredPackage : DraginoPackage.eINSTANCE);
		registeredPackage = EPackage.Registry.INSTANCE.getEPackage(LorawanPackage.eNS_URI);
		LorawanPackageImpl theLorawanPackage = (LorawanPackageImpl)(registeredPackage instanceof LorawanPackageImpl ? registeredPackage : LorawanPackage.eINSTANCE);

		// Create package meta-data objects
		theEm310udlPackage.createPackageContents();
		theDraginoPackage.createPackageContents();
		theLorawanPackage.createPackageContents();

		// Initialize created meta-data
		theEm310udlPackage.initializePackageContents();
		theDraginoPackage.initializePackageContents();
		theLorawanPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theEm310udlPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(Em310udlPackage.eNS_URI, theEm310udlPackage);
		return theEm310udlPackage;
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
	public EAttribute getDecodedObject_Distance() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Position() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDecodedObject_Battery() {
		return (EAttribute)decodedObjectEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getEM310UDLUplink() {
		return em310UDLUplinkEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getEM310UDLUplink_Object() {
		return (EReference)em310UDLUplinkEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Em310udlFactory getEm310udlFactory() {
		return (Em310udlFactory)getEFactoryInstance();
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
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__DISTANCE);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__POSITION);
		createEAttribute(decodedObjectEClass, DECODED_OBJECT__BATTERY);

		em310UDLUplinkEClass = createEClass(EM310UDL_UPLINK);
		createEReference(em310UDLUplinkEClass, EM310UDL_UPLINK__OBJECT);
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
		em310UDLUplinkEClass.getESuperTypes().add(theLorawanPackage.getUplinkMessage());

		// Initialize classes, features, and operations; add parameters
		initEClass(decodedObjectEClass, DecodedObject.class, "DecodedObject", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDecodedObject_Distance(), ecorePackage.getEDouble(), "distance", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Position(), ecorePackage.getEString(), "position", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDecodedObject_Battery(), ecorePackage.getEDouble(), "battery", null, 0, 1, DecodedObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(em310UDLUplinkEClass, EM310UDLUplink.class, "EM310UDLUplink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getEM310UDLUplink_Object(), this.getDecodedObject(), null, "object", null, 0, 1, EM310UDLUplink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //Em310udlPackageImpl
