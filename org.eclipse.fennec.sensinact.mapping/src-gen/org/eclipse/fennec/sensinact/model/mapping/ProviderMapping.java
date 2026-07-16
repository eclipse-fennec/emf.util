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

import org.eclipse.emf.ecore.EClass;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Provider Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getProviderClasses <em>Provider Classes</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getServices <em>Services</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getAdmin <em>Admin</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#isProviderTimestamp <em>Provider Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getProfile <em>Profile</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProviderMapping()
 * @model
 * @generated
 */
@ProviderType
public interface ProviderMapping extends Mapping {
	/**
	 * Returns the value of the '<em><b>Provider Classes</b></em>' reference list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.EClass}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Provider Classes</em>' reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProviderMapping_ProviderClasses()
	 * @model
	 * @generated
	 */
	EList<EClass> getProviderClasses();

	/**
	 * Returns the value of the '<em><b>Services</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Services</em>' containment reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProviderMapping_Services()
	 * @model containment="true"
	 * @generated
	 */
	EList<ServiceMapping> getServices();

	/**
	 * Returns the value of the '<em><b>Admin</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Admin</em>' containment reference.
	 * @see #setAdmin(AdminMapping)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProviderMapping_Admin()
	 * @model containment="true"
	 * @generated
	 */
	AdminMapping getAdmin();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getAdmin <em>Admin</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Admin</em>' containment reference.
	 * @see #getAdmin()
	 * @generated
	 */
	void setAdmin(AdminMapping value);

	/**
	 * Returns the value of the '<em><b>Provider Timestamp</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * If set to true, this means the provider timestamp is the same for all services and resources. Updates occur not resource individual.
	 * This is usually the case when a provider reflects a more complex structure that contains many data / resources.
	 * The default is false.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Provider Timestamp</em>' attribute.
	 * @see #setProviderTimestamp(boolean)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProviderMapping_ProviderTimestamp()
	 * @model default="false"
	 * @generated
	 */
	boolean isProviderTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#isProviderTimestamp <em>Provider Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider Timestamp</em>' attribute.
	 * @see #isProviderTimestamp()
	 * @generated
	 */
	void setProviderTimestamp(boolean value);

	/**
	 * Returns the value of the '<em><b>Profile</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional reference to a mapping profile that defines the target provider structure.
	 * When set, this mapping must conform to the profile's defined structure.
	 * Multiple mappings can reference the same profile to ensure consistent target structures.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Profile</em>' reference.
	 * @see #setProfile(MappingProfile)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProviderMapping_Profile()
	 * @model
	 * @generated
	 */
	MappingProfile getProfile();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getProfile <em>Profile</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Profile</em>' reference.
	 * @see #getProfile()
	 * @generated
	 */
	void setProfile(MappingProfile value);

} // ProviderMapping
