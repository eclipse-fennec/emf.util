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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Profile Admin</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Defines the admin service structure for a profile.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresLocation <em>Requires Location</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresFriendlyName <em>Requires Friendly Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileAdmin()
 * @model
 * @generated
 */
@ProviderType
public interface ProfileAdmin extends ProfileService {
	/**
	 * Returns the value of the '<em><b>Requires Location</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether location information is required for this profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Requires Location</em>' attribute.
	 * @see #setRequiresLocation(boolean)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileAdmin_RequiresLocation()
	 * @model default="false"
	 * @generated
	 */
	boolean isRequiresLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresLocation <em>Requires Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Requires Location</em>' attribute.
	 * @see #isRequiresLocation()
	 * @generated
	 */
	void setRequiresLocation(boolean value);

	/**
	 * Returns the value of the '<em><b>Requires Friendly Name</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether friendly name is required for this profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Requires Friendly Name</em>' attribute.
	 * @see #setRequiresFriendlyName(boolean)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileAdmin_RequiresFriendlyName()
	 * @model default="false"
	 * @generated
	 */
	boolean isRequiresFriendlyName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresFriendlyName <em>Requires Friendly Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Requires Friendly Name</em>' attribute.
	 * @see #isRequiresFriendlyName()
	 * @generated
	 */
	void setRequiresFriendlyName(boolean value);

} // ProfileAdmin
