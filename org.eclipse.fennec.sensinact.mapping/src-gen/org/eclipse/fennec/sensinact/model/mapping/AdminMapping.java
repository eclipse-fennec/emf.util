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
package org.eclipse.fennec.sensinact.model.mapping;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Admin Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getFriendlyName <em>Friendly Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getFriendlyNameFeature <em>Friendly Name Feature</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getProviderPackage <em>Provider Package</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLatitude <em>Latitude</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLongitude <em>Longitude</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getElevation <em>Elevation</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLatitudeRef <em>Latitude Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLongitudeRef <em>Longitude Ref</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getElevationRef <em>Elevation Ref</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping()
 * @model
 * @generated
 */
@ProviderType
public interface AdminMapping extends ServiceMapping {
	/**
	 * Returns the value of the '<em><b>Friendly Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Friendly Name</em>' attribute.
	 * @see #setFriendlyName(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_FriendlyName()
	 * @model
	 * @generated
	 */
	String getFriendlyName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getFriendlyName <em>Friendly Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Friendly Name</em>' attribute.
	 * @see #getFriendlyName()
	 * @generated
	 */
	void setFriendlyName(String value);

	/**
	 * Returns the value of the '<em><b>Friendly Name Feature</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Friendly Name Feature</em>' reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_FriendlyNameFeature()
	 * @model
	 * @generated
	 */
	EList<EStructuralFeature> getFriendlyNameFeature();

	/**
	 * Returns the value of the '<em><b>Provider Package</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Provider Package</em>' reference.
	 * @see #setProviderPackage(EPackage)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_ProviderPackage()
	 * @model
	 * @generated
	 */
	EPackage getProviderPackage();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getProviderPackage <em>Provider Package</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider Package</em>' reference.
	 * @see #getProviderPackage()
	 * @generated
	 */
	void setProviderPackage(EPackage value);

	/**
	 * Returns the value of the '<em><b>Latitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Latitude</em>' attribute.
	 * @see #setLatitude(Double)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_Latitude()
	 * @model
	 * @generated
	 */
	Double getLatitude();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLatitude <em>Latitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Latitude</em>' attribute.
	 * @see #getLatitude()
	 * @generated
	 */
	void setLatitude(Double value);

	/**
	 * Returns the value of the '<em><b>Longitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Longitude</em>' attribute.
	 * @see #setLongitude(Double)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_Longitude()
	 * @model
	 * @generated
	 */
	Double getLongitude();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLongitude <em>Longitude</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Longitude</em>' attribute.
	 * @see #getLongitude()
	 * @generated
	 */
	void setLongitude(Double value);

	/**
	 * Returns the value of the '<em><b>Elevation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Elevation</em>' attribute.
	 * @see #setElevation(Double)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_Elevation()
	 * @model
	 * @generated
	 */
	Double getElevation();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getElevation <em>Elevation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Elevation</em>' attribute.
	 * @see #getElevation()
	 * @generated
	 */
	void setElevation(Double value);

	/**
	 * Returns the value of the '<em><b>Latitude Ref</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Latitude Ref</em>' reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_LatitudeRef()
	 * @model
	 * @generated
	 */
	EList<EStructuralFeature> getLatitudeRef();

	/**
	 * Returns the value of the '<em><b>Longitude Ref</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Longitude Ref</em>' reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_LongitudeRef()
	 * @model
	 * @generated
	 */
	EList<EStructuralFeature> getLongitudeRef();

	/**
	 * Returns the value of the '<em><b>Elevation Ref</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EStructuralFeature}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Elevation Ref</em>' reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getAdminMapping_ElevationRef()
	 * @model
	 * @generated
	 */
	EList<EStructuralFeature> getElevationRef();

} // AdminMapping
