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


import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.fennec.emf.osgi.annotation.provide.EPackage;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/OCL/Import ecore='http://www.eclipse.org/emf/2002/Ecore'"
 * @generated
 */
@ProviderType
@EPackage(uri = MappingPackage.eNS_URI, genModel = "/model/sensinact-mapping.genmodel", genModelSourceLocations = {"model/sensinact-mapping.genmodel","org.eclipse.fennec.sensinact.mapping/model/sensinact-mapping.genmodel"}, ecore = "/model/sensinact-mapping.ecore", ecoreSourceLocations = "/model/sensinact-mapping.ecore")
public interface MappingPackage extends org.eclipse.emf.ecore.EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "mapping";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "https://fennec.eclipse.org/sensinact/core/mapping/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "mapping";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	MappingPackage eINSTANCE = org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl <em>Profile</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getMappingProfile()
	 * @generated
	 */
	int MAPPING_PROFILE = 0;

	/**
	 * The feature id for the '<em><b>Profile Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE__PROFILE_ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE__DESCRIPTION = 2;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE__VERSION = 3;

	/**
	 * The feature id for the '<em><b>Provider Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE__PROVIDER_STRATEGY = 4;

	/**
	 * The feature id for the '<em><b>Provider</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE__PROVIDER = 5;

	/**
	 * The number of structural features of the '<em>Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE_FEATURE_COUNT = 6;

	/**
	 * The number of operations of the '<em>Profile</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_PROFILE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileProviderImpl <em>Profile Provider</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileProviderImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileProvider()
	 * @generated
	 */
	int PROFILE_PROVIDER = 1;

	/**
	 * The feature id for the '<em><b>Provider Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_PROVIDER__PROVIDER_ID = 0;

	/**
	 * The feature id for the '<em><b>Services</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_PROVIDER__SERVICES = 1;

	/**
	 * The feature id for the '<em><b>Admin</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_PROVIDER__ADMIN = 2;

	/**
	 * The number of structural features of the '<em>Profile Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_PROVIDER_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Profile Provider</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_PROVIDER_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileServiceImpl <em>Profile Service</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileServiceImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileService()
	 * @generated
	 */
	int PROFILE_SERVICE = 2;

	/**
	 * The feature id for the '<em><b>Service Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_SERVICE__SERVICE_ID = 0;

	/**
	 * The feature id for the '<em><b>Service Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_SERVICE__SERVICE_NAME = 1;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_SERVICE__REQUIRED = 2;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_SERVICE__RESOURCES = 3;

	/**
	 * The number of structural features of the '<em>Profile Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_SERVICE_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Profile Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_SERVICE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl <em>Profile Resource</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileResource()
	 * @generated
	 */
	int PROFILE_RESOURCE = 3;

	/**
	 * The feature id for the '<em><b>Resource Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_RESOURCE__RESOURCE_ID = 0;

	/**
	 * The feature id for the '<em><b>Resource Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_RESOURCE__RESOURCE_NAME = 1;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_RESOURCE__REQUIRED = 2;

	/**
	 * The feature id for the '<em><b>Expected Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_RESOURCE__EXPECTED_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Expected Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_RESOURCE__EXPECTED_UNIT = 4;

	/**
	 * The number of structural features of the '<em>Profile Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_RESOURCE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Profile Resource</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_RESOURCE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileAdminImpl <em>Profile Admin</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileAdminImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileAdmin()
	 * @generated
	 */
	int PROFILE_ADMIN = 4;

	/**
	 * The feature id for the '<em><b>Service Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN__SERVICE_ID = PROFILE_SERVICE__SERVICE_ID;

	/**
	 * The feature id for the '<em><b>Service Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN__SERVICE_NAME = PROFILE_SERVICE__SERVICE_NAME;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN__REQUIRED = PROFILE_SERVICE__REQUIRED;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN__RESOURCES = PROFILE_SERVICE__RESOURCES;

	/**
	 * The feature id for the '<em><b>Requires Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN__REQUIRES_LOCATION = PROFILE_SERVICE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Requires Friendly Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME = PROFILE_SERVICE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Profile Admin</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN_FEATURE_COUNT = PROFILE_SERVICE_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Profile Admin</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROFILE_ADMIN_OPERATION_COUNT = PROFILE_SERVICE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingImpl <em>Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getMapping()
	 * @generated
	 */
	int MAPPING = 10;

	/**
	 * The feature id for the '<em><b>Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__MID = 0;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__TIMESTAMP = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__NAME = 2;

	/**
	 * The feature id for the '<em><b>Fq Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING__FQ_MID = 3;

	/**
	 * The number of structural features of the '<em>Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_FEATURE_COUNT = 4;

	/**
	 * The operation id for the '<em>Map Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING___MAP_ID__FUNCTION_EOBJECT = 0;

	/**
	 * The operation id for the '<em>Map Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING___MAP_NAME__FUNCTION_EOBJECT = 1;

	/**
	 * The number of operations of the '<em>Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MAPPING_OPERATION_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl <em>Provider Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProviderMapping()
	 * @generated
	 */
	int PROVIDER_MAPPING = 5;

	/**
	 * The feature id for the '<em><b>Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__MID = MAPPING__MID;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__TIMESTAMP = MAPPING__TIMESTAMP;

	/**
	 * The feature id for the '<em><b>Name</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__NAME = MAPPING__NAME;

	/**
	 * The feature id for the '<em><b>Fq Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__FQ_MID = MAPPING__FQ_MID;

	/**
	 * The feature id for the '<em><b>Provider Classes</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__PROVIDER_CLASSES = MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Services</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__SERVICES = MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Admin</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__ADMIN = MAPPING_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Provider Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__PROVIDER_TIMESTAMP = MAPPING_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Profile</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING__PROFILE = MAPPING_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Provider Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 5;

	/**
	 * The operation id for the '<em>Map Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING___MAP_ID__FUNCTION_EOBJECT = MAPPING___MAP_ID__FUNCTION_EOBJECT;

	/**
	 * The operation id for the '<em>Map Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING___MAP_NAME__FUNCTION_EOBJECT = MAPPING___MAP_NAME__FUNCTION_EOBJECT;

	/**
	 * The number of operations of the '<em>Provider Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROVIDER_MAPPING_OPERATION_COUNT = MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ServiceMappingImpl <em>Service Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ServiceMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getServiceMapping()
	 * @generated
	 */
	int SERVICE_MAPPING = 6;

	/**
	 * The feature id for the '<em><b>Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING__MID = MAPPING__MID;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING__TIMESTAMP = MAPPING__TIMESTAMP;

	/**
	 * The feature id for the '<em><b>Name</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING__NAME = MAPPING__NAME;

	/**
	 * The feature id for the '<em><b>Fq Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING__FQ_MID = MAPPING__FQ_MID;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING__RESOURCES = MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Referenced Resource</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING__REFERENCED_RESOURCE = MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Temporary Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING__TEMPORARY_RESOURCES = MAPPING_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Service Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING_FEATURE_COUNT = MAPPING_FEATURE_COUNT + 3;

	/**
	 * The operation id for the '<em>Map Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING___MAP_ID__FUNCTION_EOBJECT = MAPPING___MAP_ID__FUNCTION_EOBJECT;

	/**
	 * The operation id for the '<em>Map Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING___MAP_NAME__FUNCTION_EOBJECT = MAPPING___MAP_NAME__FUNCTION_EOBJECT;

	/**
	 * The number of operations of the '<em>Service Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_MAPPING_OPERATION_COUNT = MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl <em>Resource Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getResourceMapping()
	 * @generated
	 */
	int RESOURCE_MAPPING = 7;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__EANNOTATIONS = EcorePackage.EATTRIBUTE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__NAME = EcorePackage.EATTRIBUTE__NAME;

	/**
	 * The feature id for the '<em><b>Ordered</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__ORDERED = EcorePackage.EATTRIBUTE__ORDERED;

	/**
	 * The feature id for the '<em><b>Unique</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__UNIQUE = EcorePackage.EATTRIBUTE__UNIQUE;

	/**
	 * The feature id for the '<em><b>Lower Bound</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__LOWER_BOUND = EcorePackage.EATTRIBUTE__LOWER_BOUND;

	/**
	 * The feature id for the '<em><b>Upper Bound</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__UPPER_BOUND = EcorePackage.EATTRIBUTE__UPPER_BOUND;

	/**
	 * The feature id for the '<em><b>Many</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__MANY = EcorePackage.EATTRIBUTE__MANY;

	/**
	 * The feature id for the '<em><b>Required</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__REQUIRED = EcorePackage.EATTRIBUTE__REQUIRED;

	/**
	 * The feature id for the '<em><b>EType</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__ETYPE = EcorePackage.EATTRIBUTE__ETYPE;

	/**
	 * The feature id for the '<em><b>EGeneric Type</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__EGENERIC_TYPE = EcorePackage.EATTRIBUTE__EGENERIC_TYPE;

	/**
	 * The feature id for the '<em><b>Changeable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__CHANGEABLE = EcorePackage.EATTRIBUTE__CHANGEABLE;

	/**
	 * The feature id for the '<em><b>Volatile</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__VOLATILE = EcorePackage.EATTRIBUTE__VOLATILE;

	/**
	 * The feature id for the '<em><b>Transient</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__TRANSIENT = EcorePackage.EATTRIBUTE__TRANSIENT;

	/**
	 * The feature id for the '<em><b>Default Value Literal</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__DEFAULT_VALUE_LITERAL = EcorePackage.EATTRIBUTE__DEFAULT_VALUE_LITERAL;

	/**
	 * The feature id for the '<em><b>Default Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__DEFAULT_VALUE = EcorePackage.EATTRIBUTE__DEFAULT_VALUE;

	/**
	 * The feature id for the '<em><b>Unsettable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__UNSETTABLE = EcorePackage.EATTRIBUTE__UNSETTABLE;

	/**
	 * The feature id for the '<em><b>Derived</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__DERIVED = EcorePackage.EATTRIBUTE__DERIVED;

	/**
	 * The feature id for the '<em><b>EContaining Class</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__ECONTAINING_CLASS = EcorePackage.EATTRIBUTE__ECONTAINING_CLASS;

	/**
	 * The feature id for the '<em><b>ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__ID = EcorePackage.EATTRIBUTE__ID;

	/**
	 * The feature id for the '<em><b>EAttribute Type</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__EATTRIBUTE_TYPE = EcorePackage.EATTRIBUTE__EATTRIBUTE_TYPE;

	/**
	 * The feature id for the '<em><b>Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__UNIT = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Unit Feature</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__UNIT_FEATURE = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value Feature</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__VALUE_FEATURE = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__TIMESTAMP = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__MID = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Extra Metadata</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__EXTRA_METADATA = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Description Mapping</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__DESCRIPTION_MAPPING = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Change Rule</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__CHANGE_RULE = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Deletion Rule</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING__DELETION_RULE = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 8;

	/**
	 * The number of structural features of the '<em>Resource Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING_FEATURE_COUNT = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 9;

	/**
	 * The operation id for the '<em>Get EAnnotation</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING___GET_EANNOTATION__STRING = EcorePackage.EATTRIBUTE___GET_EANNOTATION__STRING;

	/**
	 * The operation id for the '<em>Get Feature ID</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING___GET_FEATURE_ID = EcorePackage.EATTRIBUTE___GET_FEATURE_ID;

	/**
	 * The operation id for the '<em>Get Container Class</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING___GET_CONTAINER_CLASS = EcorePackage.EATTRIBUTE___GET_CONTAINER_CLASS;

	/**
	 * The operation id for the '<em>Map Unit</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING___MAP_UNIT__EOBJECT_FUNCTION = EcorePackage.EATTRIBUTE_OPERATION_COUNT + 0;

	/**
	 * The number of operations of the '<em>Resource Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RESOURCE_MAPPING_OPERATION_COUNT = EcorePackage.EATTRIBUTE_OPERATION_COUNT + 1;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl <em>Admin Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getAdminMapping()
	 * @generated
	 */
	int ADMIN_MAPPING = 8;

	/**
	 * The feature id for the '<em><b>Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__MID = SERVICE_MAPPING__MID;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__TIMESTAMP = SERVICE_MAPPING__TIMESTAMP;

	/**
	 * The feature id for the '<em><b>Name</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__NAME = SERVICE_MAPPING__NAME;

	/**
	 * The feature id for the '<em><b>Fq Mid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__FQ_MID = SERVICE_MAPPING__FQ_MID;

	/**
	 * The feature id for the '<em><b>Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__RESOURCES = SERVICE_MAPPING__RESOURCES;

	/**
	 * The feature id for the '<em><b>Referenced Resource</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__REFERENCED_RESOURCE = SERVICE_MAPPING__REFERENCED_RESOURCE;

	/**
	 * The feature id for the '<em><b>Temporary Resources</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__TEMPORARY_RESOURCES = SERVICE_MAPPING__TEMPORARY_RESOURCES;

	/**
	 * The feature id for the '<em><b>Friendly Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__FRIENDLY_NAME = SERVICE_MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Friendly Name Feature</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__FRIENDLY_NAME_FEATURE = SERVICE_MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Provider Package</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__PROVIDER_PACKAGE = SERVICE_MAPPING_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Latitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__LATITUDE = SERVICE_MAPPING_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Longitude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__LONGITUDE = SERVICE_MAPPING_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Elevation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__ELEVATION = SERVICE_MAPPING_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Latitude Ref</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__LATITUDE_REF = SERVICE_MAPPING_FEATURE_COUNT + 6;

	/**
	 * The feature id for the '<em><b>Longitude Ref</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__LONGITUDE_REF = SERVICE_MAPPING_FEATURE_COUNT + 7;

	/**
	 * The feature id for the '<em><b>Elevation Ref</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING__ELEVATION_REF = SERVICE_MAPPING_FEATURE_COUNT + 8;

	/**
	 * The number of structural features of the '<em>Admin Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING_FEATURE_COUNT = SERVICE_MAPPING_FEATURE_COUNT + 9;

	/**
	 * The operation id for the '<em>Map Id</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING___MAP_ID__FUNCTION_EOBJECT = SERVICE_MAPPING___MAP_ID__FUNCTION_EOBJECT;

	/**
	 * The operation id for the '<em>Map Name</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING___MAP_NAME__FUNCTION_EOBJECT = SERVICE_MAPPING___MAP_NAME__FUNCTION_EOBJECT;

	/**
	 * The number of operations of the '<em>Admin Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ADMIN_MAPPING_OPERATION_COUNT = SERVICE_MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.MetadataMappingImpl <em>Metadata Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MetadataMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getMetadataMapping()
	 * @generated
	 */
	int METADATA_MAPPING = 9;

	/**
	 * The number of structural features of the '<em>Metadata Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_MAPPING_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Metadata Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int METADATA_MAPPING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl <em>Feature Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getFeatureMapping()
	 * @generated
	 */
	int FEATURE_MAPPING = 11;

	/**
	 * The feature id for the '<em><b>Function Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MAPPING__FUNCTION_ID = 0;

	/**
	 * The feature id for the '<em><b>Feature Path</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MAPPING__FEATURE_PATH = 1;

	/**
	 * The feature id for the '<em><b>Collection Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MAPPING__COLLECTION_INDEX = 2;

	/**
	 * The feature id for the '<em><b>Collection Filter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MAPPING__COLLECTION_FILTER = 3;

	/**
	 * The number of structural features of the '<em>Feature Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MAPPING_FEATURE_COUNT = 4;

	/**
	 * The number of operations of the '<em>Feature Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FEATURE_MAPPING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.TimestampMappingImpl <em>Timestamp Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.TimestampMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getTimestampMapping()
	 * @generated
	 */
	int TIMESTAMP_MAPPING = 12;

	/**
	 * The feature id for the '<em><b>Function Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING__FUNCTION_ID = FEATURE_MAPPING__FUNCTION_ID;

	/**
	 * The feature id for the '<em><b>Feature Path</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING__FEATURE_PATH = FEATURE_MAPPING__FEATURE_PATH;

	/**
	 * The feature id for the '<em><b>Collection Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING__COLLECTION_INDEX = FEATURE_MAPPING__COLLECTION_INDEX;

	/**
	 * The feature id for the '<em><b>Collection Filter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING__COLLECTION_FILTER = FEATURE_MAPPING__COLLECTION_FILTER;

	/**
	 * The feature id for the '<em><b>Strategy</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING__STRATEGY = FEATURE_MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING__TIMESTAMP = FEATURE_MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Hint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING__HINT = FEATURE_MAPPING_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Timestamp Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING_FEATURE_COUNT = FEATURE_MAPPING_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Timestamp Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIMESTAMP_MAPPING_OPERATION_COUNT = FEATURE_MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.NameMappingImpl <em>Name Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.NameMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getNameMapping()
	 * @generated
	 */
	int NAME_MAPPING = 13;

	/**
	 * The feature id for the '<em><b>Function Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_MAPPING__FUNCTION_ID = FEATURE_MAPPING__FUNCTION_ID;

	/**
	 * The feature id for the '<em><b>Feature Path</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_MAPPING__FEATURE_PATH = FEATURE_MAPPING__FEATURE_PATH;

	/**
	 * The feature id for the '<em><b>Collection Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_MAPPING__COLLECTION_INDEX = FEATURE_MAPPING__COLLECTION_INDEX;

	/**
	 * The feature id for the '<em><b>Collection Filter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_MAPPING__COLLECTION_FILTER = FEATURE_MAPPING__COLLECTION_FILTER;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_MAPPING__NAME = FEATURE_MAPPING_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Name Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_MAPPING_FEATURE_COUNT = FEATURE_MAPPING_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Name Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_MAPPING_OPERATION_COUNT = FEATURE_MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ValueMappingImpl <em>Value Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ValueMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getValueMapping()
	 * @generated
	 */
	int VALUE_MAPPING = 14;

	/**
	 * The feature id for the '<em><b>Function Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_MAPPING__FUNCTION_ID = FEATURE_MAPPING__FUNCTION_ID;

	/**
	 * The feature id for the '<em><b>Feature Path</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_MAPPING__FEATURE_PATH = FEATURE_MAPPING__FEATURE_PATH;

	/**
	 * The feature id for the '<em><b>Collection Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_MAPPING__COLLECTION_INDEX = FEATURE_MAPPING__COLLECTION_INDEX;

	/**
	 * The feature id for the '<em><b>Collection Filter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_MAPPING__COLLECTION_FILTER = FEATURE_MAPPING__COLLECTION_FILTER;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_MAPPING__VALUE = FEATURE_MAPPING_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Value Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_MAPPING_FEATURE_COUNT = FEATURE_MAPPING_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Value Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALUE_MAPPING_OPERATION_COUNT = FEATURE_MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.StringToStringMapImpl <em>String To String Map</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.StringToStringMapImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getStringToStringMap()
	 * @generated
	 */
	int STRING_TO_STRING_MAP = 15;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP__KEY = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP__VALUE = 1;

	/**
	 * The number of structural features of the '<em>String To String Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>String To String Map</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_STRING_MAP_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl <em>Reference Mapping</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getReferenceMapping()
	 * @generated
	 */
	int REFERENCE_MAPPING = 16;

	/**
	 * The feature id for the '<em><b>Function Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__FUNCTION_ID = FEATURE_MAPPING__FUNCTION_ID;

	/**
	 * The feature id for the '<em><b>Feature Path</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__FEATURE_PATH = FEATURE_MAPPING__FEATURE_PATH;

	/**
	 * The feature id for the '<em><b>Collection Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__COLLECTION_INDEX = FEATURE_MAPPING__COLLECTION_INDEX;

	/**
	 * The feature id for the '<em><b>Collection Filter</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__COLLECTION_FILTER = FEATURE_MAPPING__COLLECTION_FILTER;

	/**
	 * The feature id for the '<em><b>Filter</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__FILTER = FEATURE_MAPPING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__EXCLUDE = FEATURE_MAPPING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Reference Mappings</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__REFERENCE_MAPPINGS = FEATURE_MAPPING_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Target EClass</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING__TARGET_ECLASS = FEATURE_MAPPING_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Reference Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING_FEATURE_COUNT = FEATURE_MAPPING_FEATURE_COUNT + 4;

	/**
	 * The number of operations of the '<em>Reference Mapping</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REFERENCE_MAPPING_OPERATION_COUNT = FEATURE_MAPPING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleRegistryImpl <em>Persistence Rule Registry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleRegistryImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getPersistenceRuleRegistry()
	 * @generated
	 */
	int PERSISTENCE_RULE_REGISTRY = 17;

	/**
	 * The feature id for the '<em><b>Change Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE_REGISTRY__CHANGE_RULES = 0;

	/**
	 * The feature id for the '<em><b>Deletion Rules</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE_REGISTRY__DELETION_RULES = 1;

	/**
	 * The number of structural features of the '<em>Persistence Rule Registry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE_REGISTRY_FEATURE_COUNT = 2;

	/**
	 * The number of operations of the '<em>Persistence Rule Registry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE_REGISTRY_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleImpl <em>Persistence Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getPersistenceRule()
	 * @generated
	 */
	int PERSISTENCE_RULE = 18;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE__DESCRIPTION = 2;

	/**
	 * The number of structural features of the '<em>Persistence Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE_FEATURE_COUNT = 3;

	/**
	 * The number of operations of the '<em>Persistence Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERSISTENCE_RULE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ChangeRuleImpl <em>Change Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ChangeRuleImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getChangeRule()
	 * @generated
	 */
	int CHANGE_RULE = 19;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_RULE__ID = PERSISTENCE_RULE__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_RULE__NAME = PERSISTENCE_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_RULE__DESCRIPTION = PERSISTENCE_RULE__DESCRIPTION;

	/**
	 * The number of structural features of the '<em>Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_RULE_FEATURE_COUNT = PERSISTENCE_RULE_FEATURE_COUNT + 0;

	/**
	 * The number of operations of the '<em>Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CHANGE_RULE_OPERATION_COUNT = PERSISTENCE_RULE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.PercentageChangeRuleImpl <em>Percentage Change Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.PercentageChangeRuleImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getPercentageChangeRule()
	 * @generated
	 */
	int PERCENTAGE_CHANGE_RULE = 20;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERCENTAGE_CHANGE_RULE__ID = CHANGE_RULE__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERCENTAGE_CHANGE_RULE__NAME = CHANGE_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERCENTAGE_CHANGE_RULE__DESCRIPTION = CHANGE_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Percentage</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERCENTAGE_CHANGE_RULE__PERCENTAGE = CHANGE_RULE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Percentage Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERCENTAGE_CHANGE_RULE_FEATURE_COUNT = CHANGE_RULE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Percentage Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PERCENTAGE_CHANGE_RULE_OPERATION_COUNT = CHANGE_RULE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.AbsoluteChangeRuleImpl <em>Absolute Change Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.AbsoluteChangeRuleImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getAbsoluteChangeRule()
	 * @generated
	 */
	int ABSOLUTE_CHANGE_RULE = 21;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSOLUTE_CHANGE_RULE__ID = CHANGE_RULE__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSOLUTE_CHANGE_RULE__NAME = CHANGE_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSOLUTE_CHANGE_RULE__DESCRIPTION = CHANGE_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Delta</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSOLUTE_CHANGE_RULE__DELTA = CHANGE_RULE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Absolute Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSOLUTE_CHANGE_RULE_FEATURE_COUNT = CHANGE_RULE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Absolute Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ABSOLUTE_CHANGE_RULE_OPERATION_COUNT = CHANGE_RULE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.CountChangeRuleImpl <em>Count Change Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.CountChangeRuleImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getCountChangeRule()
	 * @generated
	 */
	int COUNT_CHANGE_RULE = 22;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COUNT_CHANGE_RULE__ID = CHANGE_RULE__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COUNT_CHANGE_RULE__NAME = CHANGE_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COUNT_CHANGE_RULE__DESCRIPTION = CHANGE_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>N</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COUNT_CHANGE_RULE__N = CHANGE_RULE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Count Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COUNT_CHANGE_RULE_FEATURE_COUNT = CHANGE_RULE_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Count Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COUNT_CHANGE_RULE_OPERATION_COUNT = CHANGE_RULE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.TimeThrottleChangeRuleImpl <em>Time Throttle Change Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.TimeThrottleChangeRuleImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getTimeThrottleChangeRule()
	 * @generated
	 */
	int TIME_THROTTLE_CHANGE_RULE = 23;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_THROTTLE_CHANGE_RULE__ID = CHANGE_RULE__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_THROTTLE_CHANGE_RULE__NAME = CHANGE_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_THROTTLE_CHANGE_RULE__DESCRIPTION = CHANGE_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Interval</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_THROTTLE_CHANGE_RULE__INTERVAL = CHANGE_RULE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Interval Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT = CHANGE_RULE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Time Throttle Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_THROTTLE_CHANGE_RULE_FEATURE_COUNT = CHANGE_RULE_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Time Throttle Change Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TIME_THROTTLE_CHANGE_RULE_OPERATION_COUNT = CHANGE_RULE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl <em>Deletion Rule</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getDeletionRule()
	 * @generated
	 */
	int DELETION_RULE = 24;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__ID = PERSISTENCE_RULE__ID;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__NAME = PERSISTENCE_RULE__NAME;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__DESCRIPTION = PERSISTENCE_RULE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Retention</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__RETENTION = PERSISTENCE_RULE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Retention Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__RETENTION_UNIT = PERSISTENCE_RULE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Cleanup Interval</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__CLEANUP_INTERVAL = PERSISTENCE_RULE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Cleanup Interval Unit</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__CLEANUP_INTERVAL_UNIT = PERSISTENCE_RULE_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Max Count</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE__MAX_COUNT = PERSISTENCE_RULE_FEATURE_COUNT + 4;

	/**
	 * The number of structural features of the '<em>Deletion Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE_FEATURE_COUNT = PERSISTENCE_RULE_FEATURE_COUNT + 5;

	/**
	 * The number of operations of the '<em>Deletion Rule</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DELETION_RULE_OPERATION_COUNT = PERSISTENCE_RULE_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy <em>Timestamp Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getTimestampStrategy()
	 * @generated
	 */
	int TIMESTAMP_STRATEGY = 25;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy <em>Provider Strategy</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProviderStrategy()
	 * @generated
	 */
	int PROVIDER_STRATEGY = 26;

	/**
	 * The meta object id for the '{@link org.eclipse.fennec.sensinact.model.mapping.DurationUnit <em>Duration Unit</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getDurationUnit()
	 * @generated
	 */
	int DURATION_UNIT = 27;

	/**
	 * The meta object id for the '<em>EInstant</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.time.Instant
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getEInstant()
	 * @generated
	 */
	int EINSTANT = 28;

	/**
	 * The meta object id for the '<em>Java String Function</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.function.Function
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getJavaStringFunction()
	 * @generated
	 */
	int JAVA_STRING_FUNCTION = 29;

	/**
	 * The meta object id for the '<em>Java Instant Function</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.function.Function
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getJavaInstantFunction()
	 * @generated
	 */
	int JAVA_INSTANT_FUNCTION = 30;

	/**
	 * The meta object id for the '<em>Java Object Function</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.function.Function
	 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getJavaObjectFunction()
	 * @generated
	 */
	int JAVA_OBJECT_FUNCTION = 31;


	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile <em>Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Profile</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingProfile
	 * @generated
	 */
	EClass getMappingProfile();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProfileId <em>Profile Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Profile Id</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProfileId()
	 * @see #getMappingProfile()
	 * @generated
	 */
	EAttribute getMappingProfile_ProfileId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getName()
	 * @see #getMappingProfile()
	 * @generated
	 */
	EAttribute getMappingProfile_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getDescription()
	 * @see #getMappingProfile()
	 * @generated
	 */
	EAttribute getMappingProfile_Description();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getVersion()
	 * @see #getMappingProfile()
	 * @generated
	 */
	EAttribute getMappingProfile_Version();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProviderStrategy <em>Provider Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Provider Strategy</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProviderStrategy()
	 * @see #getMappingProfile()
	 * @generated
	 */
	EAttribute getMappingProfile_ProviderStrategy();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProvider <em>Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Provider</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingProfile#getProvider()
	 * @see #getMappingProfile()
	 * @generated
	 */
	EReference getMappingProfile_Provider();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider <em>Profile Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Profile Provider</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileProvider
	 * @generated
	 */
	EClass getProfileProvider();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getProviderId <em>Provider Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Provider Id</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getProviderId()
	 * @see #getProfileProvider()
	 * @generated
	 */
	EAttribute getProfileProvider_ProviderId();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getServices <em>Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Services</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getServices()
	 * @see #getProfileProvider()
	 * @generated
	 */
	EReference getProfileProvider_Services();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getAdmin <em>Admin</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Admin</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileProvider#getAdmin()
	 * @see #getProfileProvider()
	 * @generated
	 */
	EReference getProfileProvider_Admin();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileService <em>Profile Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Profile Service</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileService
	 * @generated
	 */
	EClass getProfileService();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileService#getServiceId <em>Service Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Service Id</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileService#getServiceId()
	 * @see #getProfileService()
	 * @generated
	 */
	EAttribute getProfileService_ServiceId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileService#getServiceName <em>Service Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Service Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileService#getServiceName()
	 * @see #getProfileService()
	 * @generated
	 */
	EAttribute getProfileService_ServiceName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileService#isRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileService#isRequired()
	 * @see #getProfileService()
	 * @generated
	 */
	EAttribute getProfileService_Required();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileService#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resources</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileService#getResources()
	 * @see #getProfileService()
	 * @generated
	 */
	EReference getProfileService_Resources();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource <em>Profile Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Profile Resource</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileResource
	 * @generated
	 */
	EClass getProfileResource();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceId <em>Resource Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Resource Id</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceId()
	 * @see #getProfileResource()
	 * @generated
	 */
	EAttribute getProfileResource_ResourceId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceName <em>Resource Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Resource Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getResourceName()
	 * @see #getProfileResource()
	 * @generated
	 */
	EAttribute getProfileResource_ResourceName();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#isRequired <em>Required</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Required</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileResource#isRequired()
	 * @see #getProfileResource()
	 * @generated
	 */
	EAttribute getProfileResource_Required();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedType <em>Expected Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Expected Type</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedType()
	 * @see #getProfileResource()
	 * @generated
	 */
	EReference getProfileResource_ExpectedType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedUnit <em>Expected Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Expected Unit</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileResource#getExpectedUnit()
	 * @see #getProfileResource()
	 * @generated
	 */
	EAttribute getProfileResource_ExpectedUnit();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin <em>Profile Admin</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Profile Admin</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin
	 * @generated
	 */
	EClass getProfileAdmin();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresLocation <em>Requires Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Requires Location</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresLocation()
	 * @see #getProfileAdmin()
	 * @generated
	 */
	EAttribute getProfileAdmin_RequiresLocation();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresFriendlyName <em>Requires Friendly Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Requires Friendly Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin#isRequiresFriendlyName()
	 * @see #getProfileAdmin()
	 * @generated
	 */
	EAttribute getProfileAdmin_RequiresFriendlyName();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping <em>Provider Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Provider Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderMapping
	 * @generated
	 */
	EClass getProviderMapping();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getProviderClasses <em>Provider Classes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Provider Classes</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getProviderClasses()
	 * @see #getProviderMapping()
	 * @generated
	 */
	EReference getProviderMapping_ProviderClasses();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getServices <em>Services</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Services</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getServices()
	 * @see #getProviderMapping()
	 * @generated
	 */
	EReference getProviderMapping_Services();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getAdmin <em>Admin</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Admin</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getAdmin()
	 * @see #getProviderMapping()
	 * @generated
	 */
	EReference getProviderMapping_Admin();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#isProviderTimestamp <em>Provider Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Provider Timestamp</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#isProviderTimestamp()
	 * @see #getProviderMapping()
	 * @generated
	 */
	EAttribute getProviderMapping_ProviderTimestamp();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getProfile <em>Profile</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Profile</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderMapping#getProfile()
	 * @see #getProviderMapping()
	 * @generated
	 */
	EReference getProviderMapping_Profile();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping <em>Service Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Service Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ServiceMapping
	 * @generated
	 */
	EClass getServiceMapping();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getResources <em>Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Resources</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getResources()
	 * @see #getServiceMapping()
	 * @generated
	 */
	EReference getServiceMapping_Resources();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getReferencedResource <em>Referenced Resource</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Referenced Resource</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getReferencedResource()
	 * @see #getServiceMapping()
	 * @generated
	 */
	EReference getServiceMapping_ReferencedResource();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getTemporaryResources <em>Temporary Resources</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Temporary Resources</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ServiceMapping#getTemporaryResources()
	 * @see #getServiceMapping()
	 * @generated
	 */
	EReference getServiceMapping_TemporaryResources();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping <em>Resource Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Resource Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping
	 * @generated
	 */
	EClass getResourceMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getUnit <em>Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unit</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getUnit()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EAttribute getResourceMapping_Unit();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getUnitFeature <em>Unit Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Unit Feature</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getUnitFeature()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EReference getResourceMapping_UnitFeature();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getValueFeature <em>Value Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Value Feature</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getValueFeature()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EReference getResourceMapping_ValueFeature();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getTimestamp <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Timestamp</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getTimestamp()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EReference getResourceMapping_Timestamp();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getMid <em>Mid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mid</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getMid()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EAttribute getResourceMapping_Mid();

	/**
	 * Returns the meta object for the map '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getExtraMetadata <em>Extra Metadata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Extra Metadata</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getExtraMetadata()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EReference getResourceMapping_ExtraMetadata();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getDescriptionMapping <em>Description Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Description Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getDescriptionMapping()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EReference getResourceMapping_DescriptionMapping();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getChangeRule <em>Change Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Change Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getChangeRule()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EReference getResourceMapping_ChangeRule();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getDeletionRule <em>Deletion Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Deletion Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#getDeletionRule()
	 * @see #getResourceMapping()
	 * @generated
	 */
	EReference getResourceMapping_DeletionRule();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#mapUnit(org.eclipse.emf.ecore.EObject, java.util.function.Function) <em>Map Unit</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Map Unit</em>' operation.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ResourceMapping#mapUnit(org.eclipse.emf.ecore.EObject, java.util.function.Function)
	 * @generated
	 */
	EOperation getResourceMapping__MapUnit__EObject_Function();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping <em>Admin Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Admin Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping
	 * @generated
	 */
	EClass getAdminMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getFriendlyName <em>Friendly Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Friendly Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getFriendlyName()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EAttribute getAdminMapping_FriendlyName();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getFriendlyNameFeature <em>Friendly Name Feature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Friendly Name Feature</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getFriendlyNameFeature()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EReference getAdminMapping_FriendlyNameFeature();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getProviderPackage <em>Provider Package</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Provider Package</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getProviderPackage()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EReference getAdminMapping_ProviderPackage();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLatitude <em>Latitude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Latitude</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLatitude()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EAttribute getAdminMapping_Latitude();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLongitude <em>Longitude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Longitude</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLongitude()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EAttribute getAdminMapping_Longitude();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getElevation <em>Elevation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Elevation</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getElevation()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EAttribute getAdminMapping_Elevation();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLatitudeRef <em>Latitude Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Latitude Ref</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLatitudeRef()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EReference getAdminMapping_LatitudeRef();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLongitudeRef <em>Longitude Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Longitude Ref</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getLongitudeRef()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EReference getAdminMapping_LongitudeRef();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getElevationRef <em>Elevation Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Elevation Ref</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AdminMapping#getElevationRef()
	 * @see #getAdminMapping()
	 * @generated
	 */
	EReference getAdminMapping_ElevationRef();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.MetadataMapping <em>Metadata Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Metadata Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.MetadataMapping
	 * @generated
	 */
	EClass getMetadataMapping();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping <em>Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.Mapping
	 * @generated
	 */
	EClass getMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getMid <em>Mid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mid</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.Mapping#getMid()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_Mid();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getTimestamp <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Timestamp</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.Mapping#getTimestamp()
	 * @see #getMapping()
	 * @generated
	 */
	EReference getMapping_Timestamp();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.Mapping#getName()
	 * @see #getMapping()
	 * @generated
	 */
	EReference getMapping_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#getFqMid <em>Fq Mid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fq Mid</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.Mapping#getFqMid()
	 * @see #getMapping()
	 * @generated
	 */
	EAttribute getMapping_FqMid();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#mapId(java.util.function.Function, org.eclipse.emf.ecore.EObject) <em>Map Id</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Map Id</em>' operation.
	 * @see org.eclipse.fennec.sensinact.model.mapping.Mapping#mapId(java.util.function.Function, org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getMapping__MapId__Function_EObject();

	/**
	 * Returns the meta object for the '{@link org.eclipse.fennec.sensinact.model.mapping.Mapping#mapName(java.util.function.Function, org.eclipse.emf.ecore.EObject) <em>Map Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Map Name</em>' operation.
	 * @see org.eclipse.fennec.sensinact.model.mapping.Mapping#mapName(java.util.function.Function, org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	EOperation getMapping__MapName__Function_EObject();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping <em>Feature Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Feature Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.FeatureMapping
	 * @generated
	 */
	EClass getFeatureMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getFunctionId <em>Function Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Function Id</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getFunctionId()
	 * @see #getFeatureMapping()
	 * @generated
	 */
	EAttribute getFeatureMapping_FunctionId();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getFeaturePath <em>Feature Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Feature Path</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getFeaturePath()
	 * @see #getFeatureMapping()
	 * @generated
	 */
	EReference getFeatureMapping_FeaturePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionIndex <em>Collection Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Collection Index</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionIndex()
	 * @see #getFeatureMapping()
	 * @generated
	 */
	EAttribute getFeatureMapping_CollectionIndex();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionFilter <em>Collection Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Collection Filter</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.FeatureMapping#getCollectionFilter()
	 * @see #getFeatureMapping()
	 * @generated
	 */
	EAttribute getFeatureMapping_CollectionFilter();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping <em>Timestamp Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Timestamp Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampMapping
	 * @generated
	 */
	EClass getTimestampMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getStrategy <em>Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Strategy</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getStrategy()
	 * @see #getTimestampMapping()
	 * @generated
	 */
	EAttribute getTimestampMapping_Strategy();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getTimestamp <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Timestamp</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getTimestamp()
	 * @see #getTimestampMapping()
	 * @generated
	 */
	EAttribute getTimestampMapping_Timestamp();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getHint <em>Hint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Hint</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampMapping#getHint()
	 * @see #getTimestampMapping()
	 * @generated
	 */
	EAttribute getTimestampMapping_Hint();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.NameMapping <em>Name Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Name Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.NameMapping
	 * @generated
	 */
	EClass getNameMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.NameMapping#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.NameMapping#getName()
	 * @see #getNameMapping()
	 * @generated
	 */
	EAttribute getNameMapping_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ValueMapping <em>Value Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Value Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ValueMapping
	 * @generated
	 */
	EClass getValueMapping();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ValueMapping#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ValueMapping#getValue()
	 * @see #getValueMapping()
	 * @generated
	 */
	EAttribute getValueMapping_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To String Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String To String Map</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="org.eclipse.emf.ecore.EString"
	 *        valueDataType="org.eclipse.emf.ecore.EString"
	 * @generated
	 */
	EClass getStringToStringMap();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMap()
	 * @generated
	 */
	EAttribute getStringToStringMap_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToStringMap()
	 * @generated
	 */
	EAttribute getStringToStringMap_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping <em>Reference Mapping</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Reference Mapping</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping
	 * @generated
	 */
	EClass getReferenceMapping();

	/**
	 * Returns the meta object for the reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getFilter <em>Filter</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Filter</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getFilter()
	 * @see #getReferenceMapping()
	 * @generated
	 */
	EReference getReferenceMapping_Filter();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#isExclude <em>Exclude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exclude</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#isExclude()
	 * @see #getReferenceMapping()
	 * @generated
	 */
	EAttribute getReferenceMapping_Exclude();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getReferenceMappings <em>Reference Mappings</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Reference Mappings</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getReferenceMappings()
	 * @see #getReferenceMapping()
	 * @generated
	 */
	EReference getReferenceMapping_ReferenceMappings();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getTargetEClass <em>Target EClass</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Target EClass</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping#getTargetEClass()
	 * @see #getReferenceMapping()
	 * @generated
	 */
	EReference getReferenceMapping_TargetEClass();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry <em>Persistence Rule Registry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Persistence Rule Registry</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry
	 * @generated
	 */
	EClass getPersistenceRuleRegistry();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry#getChangeRules <em>Change Rules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Change Rules</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry#getChangeRules()
	 * @see #getPersistenceRuleRegistry()
	 * @generated
	 */
	EReference getPersistenceRuleRegistry_ChangeRules();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry#getDeletionRules <em>Deletion Rules</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Deletion Rules</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry#getDeletionRules()
	 * @see #getPersistenceRuleRegistry()
	 * @generated
	 */
	EReference getPersistenceRuleRegistry_DeletionRules();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.PersistenceRule <em>Persistence Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Persistence Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PersistenceRule
	 * @generated
	 */
	EClass getPersistenceRule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.PersistenceRule#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PersistenceRule#getId()
	 * @see #getPersistenceRule()
	 * @generated
	 */
	EAttribute getPersistenceRule_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.PersistenceRule#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PersistenceRule#getName()
	 * @see #getPersistenceRule()
	 * @generated
	 */
	EAttribute getPersistenceRule_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.PersistenceRule#getDescription <em>Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PersistenceRule#getDescription()
	 * @see #getPersistenceRule()
	 * @generated
	 */
	EAttribute getPersistenceRule_Description();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.ChangeRule <em>Change Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Change Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ChangeRule
	 * @generated
	 */
	EClass getChangeRule();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.PercentageChangeRule <em>Percentage Change Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Percentage Change Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PercentageChangeRule
	 * @generated
	 */
	EClass getPercentageChangeRule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.PercentageChangeRule#getPercentage <em>Percentage</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Percentage</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.PercentageChangeRule#getPercentage()
	 * @see #getPercentageChangeRule()
	 * @generated
	 */
	EAttribute getPercentageChangeRule_Percentage();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.AbsoluteChangeRule <em>Absolute Change Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Absolute Change Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AbsoluteChangeRule
	 * @generated
	 */
	EClass getAbsoluteChangeRule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.AbsoluteChangeRule#getDelta <em>Delta</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Delta</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.AbsoluteChangeRule#getDelta()
	 * @see #getAbsoluteChangeRule()
	 * @generated
	 */
	EAttribute getAbsoluteChangeRule_Delta();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.CountChangeRule <em>Count Change Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Count Change Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.CountChangeRule
	 * @generated
	 */
	EClass getCountChangeRule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.CountChangeRule#getN <em>N</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>N</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.CountChangeRule#getN()
	 * @see #getCountChangeRule()
	 * @generated
	 */
	EAttribute getCountChangeRule_N();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule <em>Time Throttle Change Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Time Throttle Change Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule
	 * @generated
	 */
	EClass getTimeThrottleChangeRule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getInterval <em>Interval</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Interval</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getInterval()
	 * @see #getTimeThrottleChangeRule()
	 * @generated
	 */
	EAttribute getTimeThrottleChangeRule_Interval();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getIntervalUnit <em>Interval Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Interval Unit</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule#getIntervalUnit()
	 * @see #getTimeThrottleChangeRule()
	 * @generated
	 */
	EAttribute getTimeThrottleChangeRule_IntervalUnit();

	/**
	 * Returns the meta object for class '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule <em>Deletion Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Deletion Rule</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DeletionRule
	 * @generated
	 */
	EClass getDeletionRule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetention <em>Retention</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Retention</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetention()
	 * @see #getDeletionRule()
	 * @generated
	 */
	EAttribute getDeletionRule_Retention();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetentionUnit <em>Retention Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Retention Unit</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getRetentionUnit()
	 * @see #getDeletionRule()
	 * @generated
	 */
	EAttribute getDeletionRule_RetentionUnit();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupInterval <em>Cleanup Interval</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cleanup Interval</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupInterval()
	 * @see #getDeletionRule()
	 * @generated
	 */
	EAttribute getDeletionRule_CleanupInterval();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupIntervalUnit <em>Cleanup Interval Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Cleanup Interval Unit</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getCleanupIntervalUnit()
	 * @see #getDeletionRule()
	 * @generated
	 */
	EAttribute getDeletionRule_CleanupIntervalUnit();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getMaxCount <em>Max Count</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max Count</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DeletionRule#getMaxCount()
	 * @see #getDeletionRule()
	 * @generated
	 */
	EAttribute getDeletionRule_MaxCount();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy <em>Timestamp Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Timestamp Strategy</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy
	 * @generated
	 */
	EEnum getTimestampStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy <em>Provider Strategy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Provider Strategy</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy
	 * @generated
	 */
	EEnum getProviderStrategy();

	/**
	 * Returns the meta object for enum '{@link org.eclipse.fennec.sensinact.model.mapping.DurationUnit <em>Duration Unit</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Duration Unit</em>'.
	 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
	 * @generated
	 */
	EEnum getDurationUnit();

	/**
	 * Returns the meta object for data type '{@link java.time.Instant <em>EInstant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>EInstant</em>'.
	 * @see java.time.Instant
	 * @model instanceClass="java.time.Instant"
	 * @generated
	 */
	EDataType getEInstant();

	/**
	 * Returns the meta object for data type '{@link java.util.function.Function <em>Java String Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Java String Function</em>'.
	 * @see java.util.function.Function
	 * @model instanceClass="java.util.function.Function&lt;org.eclipse.emf.ecore.EObject, java.lang.String&gt;" serializeable="false"
	 * @generated
	 */
	EDataType getJavaStringFunction();

	/**
	 * Returns the meta object for data type '{@link java.util.function.Function <em>Java Instant Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Java Instant Function</em>'.
	 * @see java.util.function.Function
	 * @model instanceClass="java.util.function.Function&lt;org.eclipse.emf.ecore.EObject, java.time.Instant&gt;" serializeable="false"
	 * @generated
	 */
	EDataType getJavaInstantFunction();

	/**
	 * Returns the meta object for data type '{@link java.util.function.Function <em>Java Object Function</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Java Object Function</em>'.
	 * @see java.util.function.Function
	 * @model instanceClass="java.util.function.Function&lt;org.eclipse.emf.ecore.EObject, ?&gt;" serializeable="false"
	 * @generated
	 */
	EDataType getJavaObjectFunction();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	MappingFactory getMappingFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl <em>Profile</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingProfileImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getMappingProfile()
		 * @generated
		 */
		EClass MAPPING_PROFILE = eINSTANCE.getMappingProfile();

		/**
		 * The meta object literal for the '<em><b>Profile Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_PROFILE__PROFILE_ID = eINSTANCE.getMappingProfile_ProfileId();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_PROFILE__NAME = eINSTANCE.getMappingProfile_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_PROFILE__DESCRIPTION = eINSTANCE.getMappingProfile_Description();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_PROFILE__VERSION = eINSTANCE.getMappingProfile_Version();

		/**
		 * The meta object literal for the '<em><b>Provider Strategy</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING_PROFILE__PROVIDER_STRATEGY = eINSTANCE.getMappingProfile_ProviderStrategy();

		/**
		 * The meta object literal for the '<em><b>Provider</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPING_PROFILE__PROVIDER = eINSTANCE.getMappingProfile_Provider();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileProviderImpl <em>Profile Provider</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileProviderImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileProvider()
		 * @generated
		 */
		EClass PROFILE_PROVIDER = eINSTANCE.getProfileProvider();

		/**
		 * The meta object literal for the '<em><b>Provider Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_PROVIDER__PROVIDER_ID = eINSTANCE.getProfileProvider_ProviderId();

		/**
		 * The meta object literal for the '<em><b>Services</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROFILE_PROVIDER__SERVICES = eINSTANCE.getProfileProvider_Services();

		/**
		 * The meta object literal for the '<em><b>Admin</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROFILE_PROVIDER__ADMIN = eINSTANCE.getProfileProvider_Admin();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileServiceImpl <em>Profile Service</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileServiceImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileService()
		 * @generated
		 */
		EClass PROFILE_SERVICE = eINSTANCE.getProfileService();

		/**
		 * The meta object literal for the '<em><b>Service Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_SERVICE__SERVICE_ID = eINSTANCE.getProfileService_ServiceId();

		/**
		 * The meta object literal for the '<em><b>Service Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_SERVICE__SERVICE_NAME = eINSTANCE.getProfileService_ServiceName();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_SERVICE__REQUIRED = eINSTANCE.getProfileService_Required();

		/**
		 * The meta object literal for the '<em><b>Resources</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROFILE_SERVICE__RESOURCES = eINSTANCE.getProfileService_Resources();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl <em>Profile Resource</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileResourceImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileResource()
		 * @generated
		 */
		EClass PROFILE_RESOURCE = eINSTANCE.getProfileResource();

		/**
		 * The meta object literal for the '<em><b>Resource Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_RESOURCE__RESOURCE_ID = eINSTANCE.getProfileResource_ResourceId();

		/**
		 * The meta object literal for the '<em><b>Resource Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_RESOURCE__RESOURCE_NAME = eINSTANCE.getProfileResource_ResourceName();

		/**
		 * The meta object literal for the '<em><b>Required</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_RESOURCE__REQUIRED = eINSTANCE.getProfileResource_Required();

		/**
		 * The meta object literal for the '<em><b>Expected Type</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROFILE_RESOURCE__EXPECTED_TYPE = eINSTANCE.getProfileResource_ExpectedType();

		/**
		 * The meta object literal for the '<em><b>Expected Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_RESOURCE__EXPECTED_UNIT = eINSTANCE.getProfileResource_ExpectedUnit();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProfileAdminImpl <em>Profile Admin</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProfileAdminImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProfileAdmin()
		 * @generated
		 */
		EClass PROFILE_ADMIN = eINSTANCE.getProfileAdmin();

		/**
		 * The meta object literal for the '<em><b>Requires Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_ADMIN__REQUIRES_LOCATION = eINSTANCE.getProfileAdmin_RequiresLocation();

		/**
		 * The meta object literal for the '<em><b>Requires Friendly Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME = eINSTANCE.getProfileAdmin_RequiresFriendlyName();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl <em>Provider Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ProviderMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProviderMapping()
		 * @generated
		 */
		EClass PROVIDER_MAPPING = eINSTANCE.getProviderMapping();

		/**
		 * The meta object literal for the '<em><b>Provider Classes</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROVIDER_MAPPING__PROVIDER_CLASSES = eINSTANCE.getProviderMapping_ProviderClasses();

		/**
		 * The meta object literal for the '<em><b>Services</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROVIDER_MAPPING__SERVICES = eINSTANCE.getProviderMapping_Services();

		/**
		 * The meta object literal for the '<em><b>Admin</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROVIDER_MAPPING__ADMIN = eINSTANCE.getProviderMapping_Admin();

		/**
		 * The meta object literal for the '<em><b>Provider Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROVIDER_MAPPING__PROVIDER_TIMESTAMP = eINSTANCE.getProviderMapping_ProviderTimestamp();

		/**
		 * The meta object literal for the '<em><b>Profile</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROVIDER_MAPPING__PROFILE = eINSTANCE.getProviderMapping_Profile();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ServiceMappingImpl <em>Service Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ServiceMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getServiceMapping()
		 * @generated
		 */
		EClass SERVICE_MAPPING = eINSTANCE.getServiceMapping();

		/**
		 * The meta object literal for the '<em><b>Resources</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_MAPPING__RESOURCES = eINSTANCE.getServiceMapping_Resources();

		/**
		 * The meta object literal for the '<em><b>Referenced Resource</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_MAPPING__REFERENCED_RESOURCE = eINSTANCE.getServiceMapping_ReferencedResource();

		/**
		 * The meta object literal for the '<em><b>Temporary Resources</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICE_MAPPING__TEMPORARY_RESOURCES = eINSTANCE.getServiceMapping_TemporaryResources();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl <em>Resource Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ResourceMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getResourceMapping()
		 * @generated
		 */
		EClass RESOURCE_MAPPING = eINSTANCE.getResourceMapping();

		/**
		 * The meta object literal for the '<em><b>Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESOURCE_MAPPING__UNIT = eINSTANCE.getResourceMapping_Unit();

		/**
		 * The meta object literal for the '<em><b>Unit Feature</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_MAPPING__UNIT_FEATURE = eINSTANCE.getResourceMapping_UnitFeature();

		/**
		 * The meta object literal for the '<em><b>Value Feature</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_MAPPING__VALUE_FEATURE = eINSTANCE.getResourceMapping_ValueFeature();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_MAPPING__TIMESTAMP = eINSTANCE.getResourceMapping_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Mid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RESOURCE_MAPPING__MID = eINSTANCE.getResourceMapping_Mid();

		/**
		 * The meta object literal for the '<em><b>Extra Metadata</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_MAPPING__EXTRA_METADATA = eINSTANCE.getResourceMapping_ExtraMetadata();

		/**
		 * The meta object literal for the '<em><b>Description Mapping</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_MAPPING__DESCRIPTION_MAPPING = eINSTANCE.getResourceMapping_DescriptionMapping();

		/**
		 * The meta object literal for the '<em><b>Change Rule</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_MAPPING__CHANGE_RULE = eINSTANCE.getResourceMapping_ChangeRule();

		/**
		 * The meta object literal for the '<em><b>Deletion Rule</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference RESOURCE_MAPPING__DELETION_RULE = eINSTANCE.getResourceMapping_DeletionRule();

		/**
		 * The meta object literal for the '<em><b>Map Unit</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation RESOURCE_MAPPING___MAP_UNIT__EOBJECT_FUNCTION = eINSTANCE.getResourceMapping__MapUnit__EObject_Function();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl <em>Admin Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.AdminMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getAdminMapping()
		 * @generated
		 */
		EClass ADMIN_MAPPING = eINSTANCE.getAdminMapping();

		/**
		 * The meta object literal for the '<em><b>Friendly Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADMIN_MAPPING__FRIENDLY_NAME = eINSTANCE.getAdminMapping_FriendlyName();

		/**
		 * The meta object literal for the '<em><b>Friendly Name Feature</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADMIN_MAPPING__FRIENDLY_NAME_FEATURE = eINSTANCE.getAdminMapping_FriendlyNameFeature();

		/**
		 * The meta object literal for the '<em><b>Provider Package</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADMIN_MAPPING__PROVIDER_PACKAGE = eINSTANCE.getAdminMapping_ProviderPackage();

		/**
		 * The meta object literal for the '<em><b>Latitude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADMIN_MAPPING__LATITUDE = eINSTANCE.getAdminMapping_Latitude();

		/**
		 * The meta object literal for the '<em><b>Longitude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADMIN_MAPPING__LONGITUDE = eINSTANCE.getAdminMapping_Longitude();

		/**
		 * The meta object literal for the '<em><b>Elevation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ADMIN_MAPPING__ELEVATION = eINSTANCE.getAdminMapping_Elevation();

		/**
		 * The meta object literal for the '<em><b>Latitude Ref</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADMIN_MAPPING__LATITUDE_REF = eINSTANCE.getAdminMapping_LatitudeRef();

		/**
		 * The meta object literal for the '<em><b>Longitude Ref</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADMIN_MAPPING__LONGITUDE_REF = eINSTANCE.getAdminMapping_LongitudeRef();

		/**
		 * The meta object literal for the '<em><b>Elevation Ref</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference ADMIN_MAPPING__ELEVATION_REF = eINSTANCE.getAdminMapping_ElevationRef();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.MetadataMappingImpl <em>Metadata Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MetadataMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getMetadataMapping()
		 * @generated
		 */
		EClass METADATA_MAPPING = eINSTANCE.getMetadataMapping();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.MappingImpl <em>Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getMapping()
		 * @generated
		 */
		EClass MAPPING = eINSTANCE.getMapping();

		/**
		 * The meta object literal for the '<em><b>Mid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__MID = eINSTANCE.getMapping_Mid();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPING__TIMESTAMP = eINSTANCE.getMapping_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference MAPPING__NAME = eINSTANCE.getMapping_Name();

		/**
		 * The meta object literal for the '<em><b>Fq Mid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MAPPING__FQ_MID = eINSTANCE.getMapping_FqMid();

		/**
		 * The meta object literal for the '<em><b>Map Id</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation MAPPING___MAP_ID__FUNCTION_EOBJECT = eINSTANCE.getMapping__MapId__Function_EObject();

		/**
		 * The meta object literal for the '<em><b>Map Name</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation MAPPING___MAP_NAME__FUNCTION_EOBJECT = eINSTANCE.getMapping__MapName__Function_EObject();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl <em>Feature Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.FeatureMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getFeatureMapping()
		 * @generated
		 */
		EClass FEATURE_MAPPING = eINSTANCE.getFeatureMapping();

		/**
		 * The meta object literal for the '<em><b>Function Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_MAPPING__FUNCTION_ID = eINSTANCE.getFeatureMapping_FunctionId();

		/**
		 * The meta object literal for the '<em><b>Feature Path</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FEATURE_MAPPING__FEATURE_PATH = eINSTANCE.getFeatureMapping_FeaturePath();

		/**
		 * The meta object literal for the '<em><b>Collection Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_MAPPING__COLLECTION_INDEX = eINSTANCE.getFeatureMapping_CollectionIndex();

		/**
		 * The meta object literal for the '<em><b>Collection Filter</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FEATURE_MAPPING__COLLECTION_FILTER = eINSTANCE.getFeatureMapping_CollectionFilter();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.TimestampMappingImpl <em>Timestamp Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.TimestampMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getTimestampMapping()
		 * @generated
		 */
		EClass TIMESTAMP_MAPPING = eINSTANCE.getTimestampMapping();

		/**
		 * The meta object literal for the '<em><b>Strategy</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIMESTAMP_MAPPING__STRATEGY = eINSTANCE.getTimestampMapping_Strategy();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIMESTAMP_MAPPING__TIMESTAMP = eINSTANCE.getTimestampMapping_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Hint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIMESTAMP_MAPPING__HINT = eINSTANCE.getTimestampMapping_Hint();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.NameMappingImpl <em>Name Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.NameMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getNameMapping()
		 * @generated
		 */
		EClass NAME_MAPPING = eINSTANCE.getNameMapping();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAME_MAPPING__NAME = eINSTANCE.getNameMapping_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ValueMappingImpl <em>Value Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ValueMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getValueMapping()
		 * @generated
		 */
		EClass VALUE_MAPPING = eINSTANCE.getValueMapping();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALUE_MAPPING__VALUE = eINSTANCE.getValueMapping_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.StringToStringMapImpl <em>String To String Map</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.StringToStringMapImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getStringToStringMap()
		 * @generated
		 */
		EClass STRING_TO_STRING_MAP = eINSTANCE.getStringToStringMap();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_STRING_MAP__KEY = eINSTANCE.getStringToStringMap_Key();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_STRING_MAP__VALUE = eINSTANCE.getStringToStringMap_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl <em>Reference Mapping</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ReferenceMappingImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getReferenceMapping()
		 * @generated
		 */
		EClass REFERENCE_MAPPING = eINSTANCE.getReferenceMapping();

		/**
		 * The meta object literal for the '<em><b>Filter</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_MAPPING__FILTER = eINSTANCE.getReferenceMapping_Filter();

		/**
		 * The meta object literal for the '<em><b>Exclude</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute REFERENCE_MAPPING__EXCLUDE = eINSTANCE.getReferenceMapping_Exclude();

		/**
		 * The meta object literal for the '<em><b>Reference Mappings</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_MAPPING__REFERENCE_MAPPINGS = eINSTANCE.getReferenceMapping_ReferenceMappings();

		/**
		 * The meta object literal for the '<em><b>Target EClass</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference REFERENCE_MAPPING__TARGET_ECLASS = eINSTANCE.getReferenceMapping_TargetEClass();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleRegistryImpl <em>Persistence Rule Registry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleRegistryImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getPersistenceRuleRegistry()
		 * @generated
		 */
		EClass PERSISTENCE_RULE_REGISTRY = eINSTANCE.getPersistenceRuleRegistry();

		/**
		 * The meta object literal for the '<em><b>Change Rules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PERSISTENCE_RULE_REGISTRY__CHANGE_RULES = eINSTANCE.getPersistenceRuleRegistry_ChangeRules();

		/**
		 * The meta object literal for the '<em><b>Deletion Rules</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PERSISTENCE_RULE_REGISTRY__DELETION_RULES = eINSTANCE.getPersistenceRuleRegistry_DeletionRules();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleImpl <em>Persistence Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.PersistenceRuleImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getPersistenceRule()
		 * @generated
		 */
		EClass PERSISTENCE_RULE = eINSTANCE.getPersistenceRule();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_RULE__ID = eINSTANCE.getPersistenceRule_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_RULE__NAME = eINSTANCE.getPersistenceRule_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERSISTENCE_RULE__DESCRIPTION = eINSTANCE.getPersistenceRule_Description();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.ChangeRuleImpl <em>Change Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.ChangeRuleImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getChangeRule()
		 * @generated
		 */
		EClass CHANGE_RULE = eINSTANCE.getChangeRule();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.PercentageChangeRuleImpl <em>Percentage Change Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.PercentageChangeRuleImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getPercentageChangeRule()
		 * @generated
		 */
		EClass PERCENTAGE_CHANGE_RULE = eINSTANCE.getPercentageChangeRule();

		/**
		 * The meta object literal for the '<em><b>Percentage</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PERCENTAGE_CHANGE_RULE__PERCENTAGE = eINSTANCE.getPercentageChangeRule_Percentage();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.AbsoluteChangeRuleImpl <em>Absolute Change Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.AbsoluteChangeRuleImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getAbsoluteChangeRule()
		 * @generated
		 */
		EClass ABSOLUTE_CHANGE_RULE = eINSTANCE.getAbsoluteChangeRule();

		/**
		 * The meta object literal for the '<em><b>Delta</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ABSOLUTE_CHANGE_RULE__DELTA = eINSTANCE.getAbsoluteChangeRule_Delta();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.CountChangeRuleImpl <em>Count Change Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.CountChangeRuleImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getCountChangeRule()
		 * @generated
		 */
		EClass COUNT_CHANGE_RULE = eINSTANCE.getCountChangeRule();

		/**
		 * The meta object literal for the '<em><b>N</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COUNT_CHANGE_RULE__N = eINSTANCE.getCountChangeRule_N();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.TimeThrottleChangeRuleImpl <em>Time Throttle Change Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.TimeThrottleChangeRuleImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getTimeThrottleChangeRule()
		 * @generated
		 */
		EClass TIME_THROTTLE_CHANGE_RULE = eINSTANCE.getTimeThrottleChangeRule();

		/**
		 * The meta object literal for the '<em><b>Interval</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIME_THROTTLE_CHANGE_RULE__INTERVAL = eINSTANCE.getTimeThrottleChangeRule_Interval();

		/**
		 * The meta object literal for the '<em><b>Interval Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT = eINSTANCE.getTimeThrottleChangeRule_IntervalUnit();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl <em>Deletion Rule</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.DeletionRuleImpl
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getDeletionRule()
		 * @generated
		 */
		EClass DELETION_RULE = eINSTANCE.getDeletionRule();

		/**
		 * The meta object literal for the '<em><b>Retention</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETION_RULE__RETENTION = eINSTANCE.getDeletionRule_Retention();

		/**
		 * The meta object literal for the '<em><b>Retention Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETION_RULE__RETENTION_UNIT = eINSTANCE.getDeletionRule_RetentionUnit();

		/**
		 * The meta object literal for the '<em><b>Cleanup Interval</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETION_RULE__CLEANUP_INTERVAL = eINSTANCE.getDeletionRule_CleanupInterval();

		/**
		 * The meta object literal for the '<em><b>Cleanup Interval Unit</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETION_RULE__CLEANUP_INTERVAL_UNIT = eINSTANCE.getDeletionRule_CleanupIntervalUnit();

		/**
		 * The meta object literal for the '<em><b>Max Count</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute DELETION_RULE__MAX_COUNT = eINSTANCE.getDeletionRule_MaxCount();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy <em>Timestamp Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getTimestampStrategy()
		 * @generated
		 */
		EEnum TIMESTAMP_STRATEGY = eINSTANCE.getTimestampStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy <em>Provider Strategy</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getProviderStrategy()
		 * @generated
		 */
		EEnum PROVIDER_STRATEGY = eINSTANCE.getProviderStrategy();

		/**
		 * The meta object literal for the '{@link org.eclipse.fennec.sensinact.model.mapping.DurationUnit <em>Duration Unit</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.fennec.sensinact.model.mapping.DurationUnit
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getDurationUnit()
		 * @generated
		 */
		EEnum DURATION_UNIT = eINSTANCE.getDurationUnit();

		/**
		 * The meta object literal for the '<em>EInstant</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.time.Instant
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getEInstant()
		 * @generated
		 */
		EDataType EINSTANT = eINSTANCE.getEInstant();

		/**
		 * The meta object literal for the '<em>Java String Function</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.function.Function
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getJavaStringFunction()
		 * @generated
		 */
		EDataType JAVA_STRING_FUNCTION = eINSTANCE.getJavaStringFunction();

		/**
		 * The meta object literal for the '<em>Java Instant Function</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.function.Function
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getJavaInstantFunction()
		 * @generated
		 */
		EDataType JAVA_INSTANT_FUNCTION = eINSTANCE.getJavaInstantFunction();

		/**
		 * The meta object literal for the '<em>Java Object Function</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.function.Function
		 * @see org.eclipse.fennec.sensinact.model.mapping.impl.MappingPackageImpl#getJavaObjectFunction()
		 * @generated
		 */
		EDataType JAVA_OBJECT_FUNCTION = eINSTANCE.getJavaObjectFunction();

	}

} //MappingPackage
