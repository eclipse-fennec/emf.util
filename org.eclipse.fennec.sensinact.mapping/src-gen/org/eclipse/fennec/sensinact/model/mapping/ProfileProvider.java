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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Profile Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Defines the target provider structure that mappings must conform to.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getProviderId <em>Provider Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getServices <em>Services</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getAdmin <em>Admin</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileProvider()
 * @model
 * @generated
 */
@ProviderType
public interface ProfileProvider extends EObject {
	/**
	 * Returns the value of the '<em><b>Provider Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The expected provider ID structure (can contain placeholders).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Provider Id</em>' attribute.
	 * @see #setProviderId(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileProvider_ProviderId()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getProviderId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getProviderId <em>Provider Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider Id</em>' attribute.
	 * @see #getProviderId()
	 * @generated
	 */
	void setProviderId(String value);

	/**
	 * Returns the value of the '<em><b>Services</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.sensinact.model.mapping.ProfileService}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The services that must be present in mappings using this profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Services</em>' containment reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileProvider_Services()
	 * @model containment="true"
	 * @generated
	 */
	EList<ProfileService> getServices();

	/**
	 * Returns the value of the '<em><b>Admin</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The admin service structure for this profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Admin</em>' containment reference.
	 * @see #setAdmin(ProfileAdmin)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileProvider_Admin()
	 * @model containment="true"
	 * @generated
	 */
	ProfileAdmin getAdmin();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getAdmin <em>Admin</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Admin</em>' containment reference.
	 * @see #getAdmin()
	 * @generated
	 */
	void setAdmin(ProfileAdmin value);

} // ProfileProvider
