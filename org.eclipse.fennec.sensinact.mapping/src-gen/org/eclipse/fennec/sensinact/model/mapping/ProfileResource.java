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

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Profile Resource</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Defines a resource structure that mappings must conform to.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceId <em>Resource Id</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceName <em>Resource Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#isRequired <em>Required</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedType <em>Expected Type</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedUnit <em>Expected Unit</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileResource()
 * @model
 * @generated
 */
@ProviderType
public interface ProfileResource extends EObject {
	/**
	 * Returns the value of the '<em><b>Resource Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The expected resource ID.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Resource Id</em>' attribute.
	 * @see #setResourceId(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileResource_ResourceId()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getResourceId();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceId <em>Resource Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource Id</em>' attribute.
	 * @see #getResourceId()
	 * @generated
	 */
	void setResourceId(String value);

	/**
	 * Returns the value of the '<em><b>Resource Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The expected resource display name.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Resource Name</em>' attribute.
	 * @see #setResourceName(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileResource_ResourceName()
	 * @model
	 * @generated
	 */
	String getResourceName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceName <em>Resource Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Resource Name</em>' attribute.
	 * @see #getResourceName()
	 * @generated
	 */
	void setResourceName(String value);

	/**
	 * Returns the value of the '<em><b>Required</b></em>' attribute.
	 * The default value is <code>"true"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Whether this resource is required in mappings using this profile.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Required</em>' attribute.
	 * @see #setRequired(boolean)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileResource_Required()
	 * @model default="true"
	 * @generated
	 */
	boolean isRequired();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#isRequired <em>Required</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Required</em>' attribute.
	 * @see #isRequired()
	 * @generated
	 */
	void setRequired(boolean value);

	/**
	 * Returns the value of the '<em><b>Expected Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The expected data type for this resource.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expected Type</em>' reference.
	 * @see #setExpectedType(EDataType)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileResource_ExpectedType()
	 * @model
	 * @generated
	 */
	EDataType getExpectedType();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedType <em>Expected Type</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expected Type</em>' reference.
	 * @see #getExpectedType()
	 * @generated
	 */
	void setExpectedType(EDataType value);

	/**
	 * Returns the value of the '<em><b>Expected Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The expected unit for this resource (if applicable).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Expected Unit</em>' attribute.
	 * @see #setExpectedUnit(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getProfileResource_ExpectedUnit()
	 * @model
	 * @generated
	 */
	String getExpectedUnit();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedUnit <em>Expected Unit</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Expected Unit</em>' attribute.
	 * @see #getExpectedUnit()
	 * @generated
	 */
	void setExpectedUnit(String value);

} // ProfileResource
