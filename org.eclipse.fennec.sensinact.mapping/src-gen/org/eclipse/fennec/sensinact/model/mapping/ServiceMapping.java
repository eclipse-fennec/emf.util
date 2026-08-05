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

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Service Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getResources <em>Resources</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getReferencedResource <em>Referenced Resource</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getTemporaryResources <em>Temporary Resources</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getServiceMapping()
 * @model
 * @generated
 */
@ProviderType
public interface ServiceMapping extends Mapping {
	/**
	 * Returns the value of the '<em><b>Resources</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Resources</em>' containment reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getServiceMapping_Resources()
	 * @model containment="true"
	 * @generated
	 */
	EList<ResourceMapping> getResources();

	/**
	 * Returns the value of the '<em><b>Referenced Resource</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Referenced Resource</em>' containment reference.
	 * @see #setReferencedResource(ReferenceMapping)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getServiceMapping_ReferencedResource()
	 * @model containment="true"
	 * @generated
	 */
	ReferenceMapping getReferencedResource();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getReferencedResource <em>Referenced Resource</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Referenced Resource</em>' containment reference.
	 * @see #getReferencedResource()
	 * @generated
	 */
	void setReferencedResource(ReferenceMapping value);

	/**
	 * Returns the value of the '<em><b>Temporary Resources</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Temporary Resources</em>' containment reference list.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getServiceMapping_TemporaryResources()
	 * @model containment="true" transient="true"
	 * @generated
	 */
	EList<ResourceMapping> getTemporaryResources();

} // ServiceMapping
