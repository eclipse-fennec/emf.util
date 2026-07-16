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

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Profile</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Defines a reusable provider structure template that multiple mappings can conform to.
 * This enables different domain models to map to the same target provider structure.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProfileId <em>Profile Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getDescription <em>Description</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getVersion <em>Version</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProviderStrategy <em>Provider Strategy</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProvider <em>Provider</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMappingProfile()
 * @model
 * @generated
 */
@ProviderType
public interface MappingProfile extends EObject {
	/**
	 * Returns the value of the '<em><b>Profile Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Unique identifier for this mapping profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Profile Id</em>' attribute.
	 * @see #setProfileId(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMappingProfile_ProfileId()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getProfileId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProfileId <em>Profile Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Profile Id</em>' attribute.
	 * @see #getProfileId()
	 * @generated
	 */
	void setProfileId(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Human-readable name for this mapping profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMappingProfile_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Optional description of this mapping profile's purpose and usage.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Description</em>' attribute.
	 * @see #setDescription(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMappingProfile_Description()
	 * @model
	 * @generated
	 */
	String getDescription();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getDescription <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Description</em>' attribute.
	 * @see #getDescription()
	 * @generated
	 */
	void setDescription(String value);

	/**
	 * Returns the value of the '<em><b>Version</b></em>' attribute.
	 * The default value is <code>"1.0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Version of this mapping profile for compatibility tracking.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Version</em>' attribute.
	 * @see #setVersion(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMappingProfile_Version()
	 * @model default="1.0"
	 * @generated
	 */
	String getVersion();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getVersion <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Version</em>' attribute.
	 * @see #getVersion()
	 * @generated
	 */
	void setVersion(String value);

	/**
	 * Returns the value of the '<em><b>Provider Strategy</b></em>' attribute.
	 * The default value is <code>"SEPARATE"</code>.
	 * The literals are from the enumeration {@link org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Defines how providers are created when multiple mappings use this profile.
	 * SEPARATE: Each mapping creates its own provider instance (default).
	 * UNIFIED: All mappings using this profile contribute to a single unified provider.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Provider Strategy</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy
	 * @see #setProviderStrategy(ProviderStrategy)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMappingProfile_ProviderStrategy()
	 * @model default="SEPARATE"
	 * @generated
	 */
	ProviderStrategy getProviderStrategy();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProviderStrategy <em>Provider Strategy</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider Strategy</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy
	 * @see #getProviderStrategy()
	 * @generated
	 */
	void setProviderStrategy(ProviderStrategy value);

	/**
	 * Returns the value of the '<em><b>Provider</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The provider structure definition for this profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Provider</em>' containment reference.
	 * @see #setProvider(ProfileProvider)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMappingProfile_Provider()
	 * @model containment="true" required="true"
	 * @generated
	 */
	ProfileProvider getProvider();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProvider <em>Provider</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider</em>' containment reference.
	 * @see #getProvider()
	 * @generated
	 */
	void setProvider(ProfileProvider value);

} // MappingProfile
