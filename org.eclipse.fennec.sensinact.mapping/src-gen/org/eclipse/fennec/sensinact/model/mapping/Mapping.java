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

import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getMid <em>Mid</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getFqMid <em>Fq Mid</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMapping()
 * @model
 * @generated
 */
@ProviderType
public interface Mapping extends EObject {
	/**
	 * Returns the value of the '<em><b>Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mid</em>' attribute.
	 * @see #setMid(String)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMapping_Mid()
	 * @model id="true" required="true"
	 * @generated
	 */
	String getMid();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getMid <em>Mid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mid</em>' attribute.
	 * @see #getMid()
	 * @generated
	 */
	void setMid(String value);

	/**
	 * Returns the value of the '<em><b>Timestamp</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timestamp</em>' containment reference.
	 * @see #setTimestamp(TimestampMapping)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMapping_Timestamp()
	 * @model containment="true"
	 * @generated
	 */
	TimestampMapping getTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getTimestamp <em>Timestamp</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timestamp</em>' containment reference.
	 * @see #getTimestamp()
	 * @generated
	 */
	void setTimestamp(TimestampMapping value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' containment reference.
	 * @see #setName(NameMapping)
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMapping_Name()
	 * @model containment="true" required="true"
	 * @generated
	 */
	NameMapping getName();

	/**
	 * Sets the value of the '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getName <em>Name</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' containment reference.
	 * @see #getName()
	 * @generated
	 */
	void setName(NameMapping value);

	/**
	 * Returns the value of the '<em><b>Fq Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fq Mid</em>' attribute.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getMapping_FqMid()
	 * @model transient="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	String getFqMid();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model idFunctionDataType="org.eclipse.fennec.sensinact.model.mapping.JavaStringFunction" idFunctionRequired="true" eobjectRequired="true"
	 * @generated
	 */
	void mapId(Function<EObject, String> idFunction, EObject eobject);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model nameFunctionDataType="org.eclipse.fennec.sensinact.model.mapping.JavaStringFunction" nameFunctionRequired="true" eobjectRequired="true"
	 * @generated
	 */
	void mapName(Function<EObject, String> nameFunction, EObject eobject);

} // Mapping
