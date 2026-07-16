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

import org.eclipse.emf.ecore.EFactory;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage
 * @generated
 */
@ProviderType
public interface MappingFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MappingFactory eINSTANCE = org.eclipse.fennec.sensinact.model.mapping.impl.MappingFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Profile</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Profile</em>'.
	 * @generated
	 */
	MappingProfile createMappingProfile();

	/**
	 * Returns a new object of class '<em>Profile Provider</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Profile Provider</em>'.
	 * @generated
	 */
	ProfileProvider createProfileProvider();

	/**
	 * Returns a new object of class '<em>Profile Service</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Profile Service</em>'.
	 * @generated
	 */
	ProfileService createProfileService();

	/**
	 * Returns a new object of class '<em>Profile Resource</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Profile Resource</em>'.
	 * @generated
	 */
	ProfileResource createProfileResource();

	/**
	 * Returns a new object of class '<em>Profile Admin</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Profile Admin</em>'.
	 * @generated
	 */
	ProfileAdmin createProfileAdmin();

	/**
	 * Returns a new object of class '<em>Provider Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Provider Mapping</em>'.
	 * @generated
	 */
	ProviderMapping createProviderMapping();

	/**
	 * Returns a new object of class '<em>Service Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Service Mapping</em>'.
	 * @generated
	 */
	ServiceMapping createServiceMapping();

	/**
	 * Returns a new object of class '<em>Resource Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Resource Mapping</em>'.
	 * @generated
	 */
	ResourceMapping createResourceMapping();

	/**
	 * Returns a new object of class '<em>Admin Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Admin Mapping</em>'.
	 * @generated
	 */
	AdminMapping createAdminMapping();

	/**
	 * Returns a new object of class '<em>Metadata Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Metadata Mapping</em>'.
	 * @generated
	 */
	MetadataMapping createMetadataMapping();

	/**
	 * Returns a new object of class '<em>Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Mapping</em>'.
	 * @generated
	 */
	Mapping createMapping();

	/**
	 * Returns a new object of class '<em>Feature Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Feature Mapping</em>'.
	 * @generated
	 */
	FeatureMapping createFeatureMapping();

	/**
	 * Returns a new object of class '<em>Timestamp Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Timestamp Mapping</em>'.
	 * @generated
	 */
	TimestampMapping createTimestampMapping();

	/**
	 * Returns a new object of class '<em>Name Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Name Mapping</em>'.
	 * @generated
	 */
	NameMapping createNameMapping();

	/**
	 * Returns a new object of class '<em>Value Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Value Mapping</em>'.
	 * @generated
	 */
	ValueMapping createValueMapping();

	/**
	 * Returns a new object of class '<em>Reference Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Reference Mapping</em>'.
	 * @generated
	 */
	ReferenceMapping createReferenceMapping();

	/**
	 * Returns a new object of class '<em>Persistence Rule Registry</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Persistence Rule Registry</em>'.
	 * @generated
	 */
	PersistenceRuleRegistry createPersistenceRuleRegistry();

	/**
	 * Returns a new object of class '<em>Percentage Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Percentage Change Rule</em>'.
	 * @generated
	 */
	PercentageChangeRule createPercentageChangeRule();

	/**
	 * Returns a new object of class '<em>Absolute Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Absolute Change Rule</em>'.
	 * @generated
	 */
	AbsoluteChangeRule createAbsoluteChangeRule();

	/**
	 * Returns a new object of class '<em>Count Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Count Change Rule</em>'.
	 * @generated
	 */
	CountChangeRule createCountChangeRule();

	/**
	 * Returns a new object of class '<em>Time Throttle Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Time Throttle Change Rule</em>'.
	 * @generated
	 */
	TimeThrottleChangeRule createTimeThrottleChangeRule();

	/**
	 * Returns a new object of class '<em>Deletion Rule</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Deletion Rule</em>'.
	 * @generated
	 */
	DeletionRule createDeletionRule();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	MappingPackage getMappingPackage();

} //MappingFactory
