/**
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
package org.eclipse.fennec.sensinact.model.mapping.impl;

import java.time.Instant;

import java.util.Map;

import java.util.function.Function;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.fennec.sensinact.model.mapping.AbsoluteChangeRule;
import org.eclipse.fennec.sensinact.model.mapping.AdminMapping;
import org.eclipse.fennec.sensinact.model.mapping.ChangeRule;
import org.eclipse.fennec.sensinact.model.mapping.CountChangeRule;
import org.eclipse.fennec.sensinact.model.mapping.DeletionRule;
import org.eclipse.fennec.sensinact.model.mapping.DurationUnit;
import org.eclipse.fennec.sensinact.model.mapping.FeatureMapping;
import org.eclipse.fennec.sensinact.model.mapping.Mapping;
import org.eclipse.fennec.sensinact.model.mapping.MappingFactory;
import org.eclipse.fennec.sensinact.model.mapping.MappingPackage;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.MetadataMapping;
import org.eclipse.fennec.sensinact.model.mapping.NameMapping;
import org.eclipse.fennec.sensinact.model.mapping.PercentageChangeRule;
import org.eclipse.fennec.sensinact.model.mapping.PersistenceRule;
import org.eclipse.fennec.sensinact.model.mapping.PersistenceRuleRegistry;
import org.eclipse.fennec.sensinact.model.mapping.ProfileAdmin;
import org.eclipse.fennec.sensinact.model.mapping.ProfileProvider;
import org.eclipse.fennec.sensinact.model.mapping.ProfileResource;
import org.eclipse.fennec.sensinact.model.mapping.ProfileService;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy;
import org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ResourceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ServiceMapping;
import org.eclipse.fennec.sensinact.model.mapping.TimeThrottleChangeRule;
import org.eclipse.fennec.sensinact.model.mapping.TimestampMapping;
import org.eclipse.fennec.sensinact.model.mapping.TimestampStrategy;
import org.eclipse.fennec.sensinact.model.mapping.ValueMapping;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MappingPackageImpl extends EPackageImpl implements MappingPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mappingProfileEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass profileProviderEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass profileServiceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass profileResourceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass profileAdminEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass providerMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serviceMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass resourceMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass adminMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass metadataMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass mappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass featureMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass timestampMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass nameMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass valueMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringToStringMapEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass referenceMappingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass persistenceRuleRegistryEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass persistenceRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass changeRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass percentageChangeRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass absoluteChangeRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass countChangeRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass timeThrottleChangeRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass deletionRuleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum timestampStrategyEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum providerStrategyEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum durationUnitEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType eInstantEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType javaStringFunctionEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType javaInstantFunctionEDataType = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType javaObjectFunctionEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private MappingPackageImpl() {
		super(eNS_URI, MappingFactory.eINSTANCE);
	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link MappingPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static MappingPackage init() {
		if (isInited) return (MappingPackage)EPackage.Registry.INSTANCE.getEPackage(MappingPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredMappingPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		MappingPackageImpl theMappingPackage = registeredMappingPackage instanceof MappingPackageImpl ? (MappingPackageImpl)registeredMappingPackage : new MappingPackageImpl();

		isInited = true;

		// Create package meta-data objects
		theMappingPackage.createPackageContents();

		// Initialize created meta-data
		theMappingPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theMappingPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(MappingPackage.eNS_URI, theMappingPackage);
		return theMappingPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMappingProfile() {
		return mappingProfileEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappingProfile_ProfileId() {
		return (EAttribute)mappingProfileEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappingProfile_Name() {
		return (EAttribute)mappingProfileEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappingProfile_Description() {
		return (EAttribute)mappingProfileEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappingProfile_Version() {
		return (EAttribute)mappingProfileEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMappingProfile_ProviderStrategy() {
		return (EAttribute)mappingProfileEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMappingProfile_Provider() {
		return (EReference)mappingProfileEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProfileProvider() {
		return profileProviderEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileProvider_ProviderId() {
		return (EAttribute)profileProviderEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProfileProvider_Services() {
		return (EReference)profileProviderEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProfileProvider_Admin() {
		return (EReference)profileProviderEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProfileService() {
		return profileServiceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileService_ServiceId() {
		return (EAttribute)profileServiceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileService_ServiceName() {
		return (EAttribute)profileServiceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileService_Required() {
		return (EAttribute)profileServiceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProfileService_Resources() {
		return (EReference)profileServiceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProfileResource() {
		return profileResourceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileResource_ResourceId() {
		return (EAttribute)profileResourceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileResource_ResourceName() {
		return (EAttribute)profileResourceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileResource_Required() {
		return (EAttribute)profileResourceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProfileResource_ExpectedType() {
		return (EReference)profileResourceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileResource_ExpectedUnit() {
		return (EAttribute)profileResourceEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProfileAdmin() {
		return profileAdminEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileAdmin_RequiresLocation() {
		return (EAttribute)profileAdminEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProfileAdmin_RequiresFriendlyName() {
		return (EAttribute)profileAdminEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getProviderMapping() {
		return providerMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProviderMapping_ProviderClasses() {
		return (EReference)providerMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProviderMapping_Services() {
		return (EReference)providerMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProviderMapping_Admin() {
		return (EReference)providerMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getProviderMapping_ProviderTimestamp() {
		return (EAttribute)providerMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getProviderMapping_Profile() {
		return (EReference)providerMappingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getServiceMapping() {
		return serviceMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServiceMapping_Resources() {
		return (EReference)serviceMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServiceMapping_ReferencedResource() {
		return (EReference)serviceMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getServiceMapping_TemporaryResources() {
		return (EReference)serviceMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getResourceMapping() {
		return resourceMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResourceMapping_Unit() {
		return (EAttribute)resourceMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceMapping_UnitFeature() {
		return (EReference)resourceMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceMapping_ValueFeature() {
		return (EReference)resourceMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceMapping_Timestamp() {
		return (EReference)resourceMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getResourceMapping_Mid() {
		return (EAttribute)resourceMappingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceMapping_ExtraMetadata() {
		return (EReference)resourceMappingEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceMapping_DescriptionMapping() {
		return (EReference)resourceMappingEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceMapping_ChangeRule() {
		return (EReference)resourceMappingEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getResourceMapping_DeletionRule() {
		return (EReference)resourceMappingEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getResourceMapping__MapUnit__EObject_Function() {
		return resourceMappingEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAdminMapping() {
		return adminMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdminMapping_FriendlyName() {
		return (EAttribute)adminMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAdminMapping_FriendlyNameFeature() {
		return (EReference)adminMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAdminMapping_ProviderPackage() {
		return (EReference)adminMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdminMapping_Latitude() {
		return (EAttribute)adminMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdminMapping_Longitude() {
		return (EAttribute)adminMappingEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAdminMapping_Elevation() {
		return (EAttribute)adminMappingEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAdminMapping_LatitudeRef() {
		return (EReference)adminMappingEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAdminMapping_LongitudeRef() {
		return (EReference)adminMappingEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getAdminMapping_ElevationRef() {
		return (EReference)adminMappingEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMetadataMapping() {
		return metadataMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getMapping() {
		return mappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapping_Mid() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMapping_Timestamp() {
		return (EReference)mappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getMapping_Name() {
		return (EReference)mappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getMapping_FqMid() {
		return (EAttribute)mappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMapping__MapId__Function_EObject() {
		return mappingEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getMapping__MapName__Function_EObject() {
		return mappingEClass.getEOperations().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFeatureMapping() {
		return featureMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureMapping_FunctionId() {
		return (EAttribute)featureMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getFeatureMapping_FeaturePath() {
		return (EReference)featureMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureMapping_CollectionIndex() {
		return (EAttribute)featureMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFeatureMapping_CollectionFilter() {
		return (EAttribute)featureMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTimestampMapping() {
		return timestampMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimestampMapping_Strategy() {
		return (EAttribute)timestampMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimestampMapping_Timestamp() {
		return (EAttribute)timestampMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimestampMapping_Hint() {
		return (EAttribute)timestampMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNameMapping() {
		return nameMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNameMapping_Name() {
		return (EAttribute)nameMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getValueMapping() {
		return valueMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getValueMapping_Value() {
		return (EAttribute)valueMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getStringToStringMap() {
		return stringToStringMapEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringToStringMap_Key() {
		return (EAttribute)stringToStringMapEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getStringToStringMap_Value() {
		return (EAttribute)stringToStringMapEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getReferenceMapping() {
		return referenceMappingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceMapping_Filter() {
		return (EReference)referenceMappingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getReferenceMapping_Exclude() {
		return (EAttribute)referenceMappingEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceMapping_ReferenceMappings() {
		return (EReference)referenceMappingEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getReferenceMapping_TargetEClass() {
		return (EReference)referenceMappingEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPersistenceRuleRegistry() {
		return persistenceRuleRegistryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceRuleRegistry_ChangeRules() {
		return (EReference)persistenceRuleRegistryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getPersistenceRuleRegistry_DeletionRules() {
		return (EReference)persistenceRuleRegistryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPersistenceRule() {
		return persistenceRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceRule_Id() {
		return (EAttribute)persistenceRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceRule_Name() {
		return (EAttribute)persistenceRuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPersistenceRule_Description() {
		return (EAttribute)persistenceRuleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getChangeRule() {
		return changeRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPercentageChangeRule() {
		return percentageChangeRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPercentageChangeRule_Percentage() {
		return (EAttribute)percentageChangeRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getAbsoluteChangeRule() {
		return absoluteChangeRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getAbsoluteChangeRule_Delta() {
		return (EAttribute)absoluteChangeRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getCountChangeRule() {
		return countChangeRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getCountChangeRule_N() {
		return (EAttribute)countChangeRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTimeThrottleChangeRule() {
		return timeThrottleChangeRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimeThrottleChangeRule_Interval() {
		return (EAttribute)timeThrottleChangeRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTimeThrottleChangeRule_IntervalUnit() {
		return (EAttribute)timeThrottleChangeRuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getDeletionRule() {
		return deletionRuleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeletionRule_Retention() {
		return (EAttribute)deletionRuleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeletionRule_RetentionUnit() {
		return (EAttribute)deletionRuleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeletionRule_CleanupInterval() {
		return (EAttribute)deletionRuleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeletionRule_CleanupIntervalUnit() {
		return (EAttribute)deletionRuleEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getDeletionRule_MaxCount() {
		return (EAttribute)deletionRuleEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getTimestampStrategy() {
		return timestampStrategyEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getProviderStrategy() {
		return providerStrategyEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getDurationUnit() {
		return durationUnitEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getEInstant() {
		return eInstantEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getJavaStringFunction() {
		return javaStringFunctionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getJavaInstantFunction() {
		return javaInstantFunctionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getJavaObjectFunction() {
		return javaObjectFunctionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MappingFactory getMappingFactory() {
		return (MappingFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		mappingProfileEClass = createEClass(MAPPING_PROFILE);
		createEAttribute(mappingProfileEClass, MAPPING_PROFILE__PROFILE_ID);
		createEAttribute(mappingProfileEClass, MAPPING_PROFILE__NAME);
		createEAttribute(mappingProfileEClass, MAPPING_PROFILE__DESCRIPTION);
		createEAttribute(mappingProfileEClass, MAPPING_PROFILE__VERSION);
		createEAttribute(mappingProfileEClass, MAPPING_PROFILE__PROVIDER_STRATEGY);
		createEReference(mappingProfileEClass, MAPPING_PROFILE__PROVIDER);

		profileProviderEClass = createEClass(PROFILE_PROVIDER);
		createEAttribute(profileProviderEClass, PROFILE_PROVIDER__PROVIDER_ID);
		createEReference(profileProviderEClass, PROFILE_PROVIDER__SERVICES);
		createEReference(profileProviderEClass, PROFILE_PROVIDER__ADMIN);

		profileServiceEClass = createEClass(PROFILE_SERVICE);
		createEAttribute(profileServiceEClass, PROFILE_SERVICE__SERVICE_ID);
		createEAttribute(profileServiceEClass, PROFILE_SERVICE__SERVICE_NAME);
		createEAttribute(profileServiceEClass, PROFILE_SERVICE__REQUIRED);
		createEReference(profileServiceEClass, PROFILE_SERVICE__RESOURCES);

		profileResourceEClass = createEClass(PROFILE_RESOURCE);
		createEAttribute(profileResourceEClass, PROFILE_RESOURCE__RESOURCE_ID);
		createEAttribute(profileResourceEClass, PROFILE_RESOURCE__RESOURCE_NAME);
		createEAttribute(profileResourceEClass, PROFILE_RESOURCE__REQUIRED);
		createEReference(profileResourceEClass, PROFILE_RESOURCE__EXPECTED_TYPE);
		createEAttribute(profileResourceEClass, PROFILE_RESOURCE__EXPECTED_UNIT);

		profileAdminEClass = createEClass(PROFILE_ADMIN);
		createEAttribute(profileAdminEClass, PROFILE_ADMIN__REQUIRES_LOCATION);
		createEAttribute(profileAdminEClass, PROFILE_ADMIN__REQUIRES_FRIENDLY_NAME);

		providerMappingEClass = createEClass(PROVIDER_MAPPING);
		createEReference(providerMappingEClass, PROVIDER_MAPPING__PROVIDER_CLASSES);
		createEReference(providerMappingEClass, PROVIDER_MAPPING__SERVICES);
		createEReference(providerMappingEClass, PROVIDER_MAPPING__ADMIN);
		createEAttribute(providerMappingEClass, PROVIDER_MAPPING__PROVIDER_TIMESTAMP);
		createEReference(providerMappingEClass, PROVIDER_MAPPING__PROFILE);

		serviceMappingEClass = createEClass(SERVICE_MAPPING);
		createEReference(serviceMappingEClass, SERVICE_MAPPING__RESOURCES);
		createEReference(serviceMappingEClass, SERVICE_MAPPING__REFERENCED_RESOURCE);
		createEReference(serviceMappingEClass, SERVICE_MAPPING__TEMPORARY_RESOURCES);

		resourceMappingEClass = createEClass(RESOURCE_MAPPING);
		createEAttribute(resourceMappingEClass, RESOURCE_MAPPING__UNIT);
		createEReference(resourceMappingEClass, RESOURCE_MAPPING__UNIT_FEATURE);
		createEReference(resourceMappingEClass, RESOURCE_MAPPING__VALUE_FEATURE);
		createEReference(resourceMappingEClass, RESOURCE_MAPPING__TIMESTAMP);
		createEAttribute(resourceMappingEClass, RESOURCE_MAPPING__MID);
		createEReference(resourceMappingEClass, RESOURCE_MAPPING__EXTRA_METADATA);
		createEReference(resourceMappingEClass, RESOURCE_MAPPING__DESCRIPTION_MAPPING);
		createEReference(resourceMappingEClass, RESOURCE_MAPPING__CHANGE_RULE);
		createEReference(resourceMappingEClass, RESOURCE_MAPPING__DELETION_RULE);
		createEOperation(resourceMappingEClass, RESOURCE_MAPPING___MAP_UNIT__EOBJECT_FUNCTION);

		adminMappingEClass = createEClass(ADMIN_MAPPING);
		createEAttribute(adminMappingEClass, ADMIN_MAPPING__FRIENDLY_NAME);
		createEReference(adminMappingEClass, ADMIN_MAPPING__FRIENDLY_NAME_FEATURE);
		createEReference(adminMappingEClass, ADMIN_MAPPING__PROVIDER_PACKAGE);
		createEAttribute(adminMappingEClass, ADMIN_MAPPING__LATITUDE);
		createEAttribute(adminMappingEClass, ADMIN_MAPPING__LONGITUDE);
		createEAttribute(adminMappingEClass, ADMIN_MAPPING__ELEVATION);
		createEReference(adminMappingEClass, ADMIN_MAPPING__LATITUDE_REF);
		createEReference(adminMappingEClass, ADMIN_MAPPING__LONGITUDE_REF);
		createEReference(adminMappingEClass, ADMIN_MAPPING__ELEVATION_REF);

		metadataMappingEClass = createEClass(METADATA_MAPPING);

		mappingEClass = createEClass(MAPPING);
		createEAttribute(mappingEClass, MAPPING__MID);
		createEReference(mappingEClass, MAPPING__TIMESTAMP);
		createEReference(mappingEClass, MAPPING__NAME);
		createEAttribute(mappingEClass, MAPPING__FQ_MID);
		createEOperation(mappingEClass, MAPPING___MAP_ID__FUNCTION_EOBJECT);
		createEOperation(mappingEClass, MAPPING___MAP_NAME__FUNCTION_EOBJECT);

		featureMappingEClass = createEClass(FEATURE_MAPPING);
		createEAttribute(featureMappingEClass, FEATURE_MAPPING__FUNCTION_ID);
		createEReference(featureMappingEClass, FEATURE_MAPPING__FEATURE_PATH);
		createEAttribute(featureMappingEClass, FEATURE_MAPPING__COLLECTION_INDEX);
		createEAttribute(featureMappingEClass, FEATURE_MAPPING__COLLECTION_FILTER);

		timestampMappingEClass = createEClass(TIMESTAMP_MAPPING);
		createEAttribute(timestampMappingEClass, TIMESTAMP_MAPPING__STRATEGY);
		createEAttribute(timestampMappingEClass, TIMESTAMP_MAPPING__TIMESTAMP);
		createEAttribute(timestampMappingEClass, TIMESTAMP_MAPPING__HINT);

		nameMappingEClass = createEClass(NAME_MAPPING);
		createEAttribute(nameMappingEClass, NAME_MAPPING__NAME);

		valueMappingEClass = createEClass(VALUE_MAPPING);
		createEAttribute(valueMappingEClass, VALUE_MAPPING__VALUE);

		stringToStringMapEClass = createEClass(STRING_TO_STRING_MAP);
		createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__KEY);
		createEAttribute(stringToStringMapEClass, STRING_TO_STRING_MAP__VALUE);

		referenceMappingEClass = createEClass(REFERENCE_MAPPING);
		createEReference(referenceMappingEClass, REFERENCE_MAPPING__FILTER);
		createEAttribute(referenceMappingEClass, REFERENCE_MAPPING__EXCLUDE);
		createEReference(referenceMappingEClass, REFERENCE_MAPPING__REFERENCE_MAPPINGS);
		createEReference(referenceMappingEClass, REFERENCE_MAPPING__TARGET_ECLASS);

		persistenceRuleRegistryEClass = createEClass(PERSISTENCE_RULE_REGISTRY);
		createEReference(persistenceRuleRegistryEClass, PERSISTENCE_RULE_REGISTRY__CHANGE_RULES);
		createEReference(persistenceRuleRegistryEClass, PERSISTENCE_RULE_REGISTRY__DELETION_RULES);

		persistenceRuleEClass = createEClass(PERSISTENCE_RULE);
		createEAttribute(persistenceRuleEClass, PERSISTENCE_RULE__ID);
		createEAttribute(persistenceRuleEClass, PERSISTENCE_RULE__NAME);
		createEAttribute(persistenceRuleEClass, PERSISTENCE_RULE__DESCRIPTION);

		changeRuleEClass = createEClass(CHANGE_RULE);

		percentageChangeRuleEClass = createEClass(PERCENTAGE_CHANGE_RULE);
		createEAttribute(percentageChangeRuleEClass, PERCENTAGE_CHANGE_RULE__PERCENTAGE);

		absoluteChangeRuleEClass = createEClass(ABSOLUTE_CHANGE_RULE);
		createEAttribute(absoluteChangeRuleEClass, ABSOLUTE_CHANGE_RULE__DELTA);

		countChangeRuleEClass = createEClass(COUNT_CHANGE_RULE);
		createEAttribute(countChangeRuleEClass, COUNT_CHANGE_RULE__N);

		timeThrottleChangeRuleEClass = createEClass(TIME_THROTTLE_CHANGE_RULE);
		createEAttribute(timeThrottleChangeRuleEClass, TIME_THROTTLE_CHANGE_RULE__INTERVAL);
		createEAttribute(timeThrottleChangeRuleEClass, TIME_THROTTLE_CHANGE_RULE__INTERVAL_UNIT);

		deletionRuleEClass = createEClass(DELETION_RULE);
		createEAttribute(deletionRuleEClass, DELETION_RULE__RETENTION);
		createEAttribute(deletionRuleEClass, DELETION_RULE__RETENTION_UNIT);
		createEAttribute(deletionRuleEClass, DELETION_RULE__CLEANUP_INTERVAL);
		createEAttribute(deletionRuleEClass, DELETION_RULE__CLEANUP_INTERVAL_UNIT);
		createEAttribute(deletionRuleEClass, DELETION_RULE__MAX_COUNT);

		// Create enums
		timestampStrategyEEnum = createEEnum(TIMESTAMP_STRATEGY);
		providerStrategyEEnum = createEEnum(PROVIDER_STRATEGY);
		durationUnitEEnum = createEEnum(DURATION_UNIT);

		// Create data types
		eInstantEDataType = createEDataType(EINSTANT);
		javaStringFunctionEDataType = createEDataType(JAVA_STRING_FUNCTION);
		javaInstantFunctionEDataType = createEDataType(JAVA_INSTANT_FUNCTION);
		javaObjectFunctionEDataType = createEDataType(JAVA_OBJECT_FUNCTION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		profileAdminEClass.getESuperTypes().add(this.getProfileService());
		providerMappingEClass.getESuperTypes().add(this.getMapping());
		serviceMappingEClass.getESuperTypes().add(this.getMapping());
		resourceMappingEClass.getESuperTypes().add(ecorePackage.getEAttribute());
		adminMappingEClass.getESuperTypes().add(this.getServiceMapping());
		timestampMappingEClass.getESuperTypes().add(this.getFeatureMapping());
		nameMappingEClass.getESuperTypes().add(this.getFeatureMapping());
		valueMappingEClass.getESuperTypes().add(this.getFeatureMapping());
		referenceMappingEClass.getESuperTypes().add(this.getFeatureMapping());
		changeRuleEClass.getESuperTypes().add(this.getPersistenceRule());
		percentageChangeRuleEClass.getESuperTypes().add(this.getChangeRule());
		absoluteChangeRuleEClass.getESuperTypes().add(this.getChangeRule());
		countChangeRuleEClass.getESuperTypes().add(this.getChangeRule());
		timeThrottleChangeRuleEClass.getESuperTypes().add(this.getChangeRule());
		deletionRuleEClass.getESuperTypes().add(this.getPersistenceRule());

		// Initialize classes, features, and operations; add parameters
		initEClass(mappingProfileEClass, MappingProfile.class, "MappingProfile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMappingProfile_ProfileId(), ecorePackage.getEString(), "profileId", null, 1, 1, MappingProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappingProfile_Name(), ecorePackage.getEString(), "name", null, 1, 1, MappingProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappingProfile_Description(), ecorePackage.getEString(), "description", null, 0, 1, MappingProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappingProfile_Version(), ecorePackage.getEString(), "version", "1.0", 0, 1, MappingProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMappingProfile_ProviderStrategy(), this.getProviderStrategy(), "providerStrategy", "SEPARATE", 0, 1, MappingProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMappingProfile_Provider(), this.getProfileProvider(), null, "provider", null, 1, 1, MappingProfile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(profileProviderEClass, ProfileProvider.class, "ProfileProvider", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProfileProvider_ProviderId(), ecorePackage.getEString(), "providerId", null, 1, 1, ProfileProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProfileProvider_Services(), this.getProfileService(), null, "services", null, 0, -1, ProfileProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProfileProvider_Admin(), this.getProfileAdmin(), null, "admin", null, 0, 1, ProfileProvider.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(profileServiceEClass, ProfileService.class, "ProfileService", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProfileService_ServiceId(), ecorePackage.getEString(), "serviceId", null, 1, 1, ProfileService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfileService_ServiceName(), ecorePackage.getEString(), "serviceName", null, 0, 1, ProfileService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfileService_Required(), ecorePackage.getEBoolean(), "required", "true", 0, 1, ProfileService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProfileService_Resources(), this.getProfileResource(), null, "resources", null, 0, -1, ProfileService.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(profileResourceEClass, ProfileResource.class, "ProfileResource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProfileResource_ResourceId(), ecorePackage.getEString(), "resourceId", null, 1, 1, ProfileResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfileResource_ResourceName(), ecorePackage.getEString(), "resourceName", null, 0, 1, ProfileResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfileResource_Required(), ecorePackage.getEBoolean(), "required", "true", 0, 1, ProfileResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProfileResource_ExpectedType(), ecorePackage.getEDataType(), null, "expectedType", null, 0, 1, ProfileResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfileResource_ExpectedUnit(), ecorePackage.getEString(), "expectedUnit", null, 0, 1, ProfileResource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(profileAdminEClass, ProfileAdmin.class, "ProfileAdmin", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProfileAdmin_RequiresLocation(), ecorePackage.getEBoolean(), "requiresLocation", "false", 0, 1, ProfileAdmin.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProfileAdmin_RequiresFriendlyName(), ecorePackage.getEBoolean(), "requiresFriendlyName", "false", 0, 1, ProfileAdmin.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(providerMappingEClass, ProviderMapping.class, "ProviderMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getProviderMapping_ProviderClasses(), ecorePackage.getEClass(), null, "providerClasses", null, 0, -1, ProviderMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProviderMapping_Services(), this.getServiceMapping(), null, "services", null, 0, -1, ProviderMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProviderMapping_Admin(), this.getAdminMapping(), null, "admin", null, 0, 1, ProviderMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProviderMapping_ProviderTimestamp(), ecorePackage.getEBoolean(), "providerTimestamp", "false", 0, 1, ProviderMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getProviderMapping_Profile(), this.getMappingProfile(), null, "profile", null, 0, 1, ProviderMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(serviceMappingEClass, ServiceMapping.class, "ServiceMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getServiceMapping_Resources(), this.getResourceMapping(), null, "resources", null, 0, -1, ServiceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServiceMapping_ReferencedResource(), this.getReferenceMapping(), null, "referencedResource", null, 0, 1, ServiceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServiceMapping_TemporaryResources(), this.getResourceMapping(), null, "temporaryResources", null, 0, -1, ServiceMapping.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(resourceMappingEClass, ResourceMapping.class, "ResourceMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getResourceMapping_Unit(), ecorePackage.getEString(), "unit", null, 0, 1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceMapping_UnitFeature(), ecorePackage.getEStructuralFeature(), null, "unitFeature", null, 0, -1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceMapping_ValueFeature(), ecorePackage.getEStructuralFeature(), null, "valueFeature", null, 0, -1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceMapping_Timestamp(), this.getTimestampMapping(), null, "timestamp", null, 1, 1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getResourceMapping_Mid(), ecorePackage.getEString(), "mid", null, 1, 1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceMapping_ExtraMetadata(), this.getStringToStringMap(), null, "extraMetadata", null, 0, -1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceMapping_DescriptionMapping(), this.getNameMapping(), null, "descriptionMapping", null, 0, 1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getResourceMapping_ChangeRule(), this.getChangeRule(), null, "changeRule", null, 0, 1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getResourceMapping_DeletionRule(), this.getDeletionRule(), null, "deletionRule", null, 0, 1, ResourceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = initEOperation(getResourceMapping__MapUnit__EObject_Function(), null, "mapUnit", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "eobject", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getJavaStringFunction(), "unitFunction", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(adminMappingEClass, AdminMapping.class, "AdminMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAdminMapping_FriendlyName(), ecorePackage.getEString(), "friendlyName", null, 0, 1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAdminMapping_FriendlyNameFeature(), ecorePackage.getEStructuralFeature(), null, "friendlyNameFeature", null, 0, -1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAdminMapping_ProviderPackage(), ecorePackage.getEPackage(), null, "providerPackage", null, 0, 1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAdminMapping_Latitude(), ecorePackage.getEDoubleObject(), "latitude", null, 0, 1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAdminMapping_Longitude(), ecorePackage.getEDoubleObject(), "longitude", null, 0, 1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getAdminMapping_Elevation(), ecorePackage.getEDoubleObject(), "elevation", null, 0, 1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAdminMapping_LatitudeRef(), ecorePackage.getEStructuralFeature(), null, "latitudeRef", null, 0, -1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAdminMapping_LongitudeRef(), ecorePackage.getEStructuralFeature(), null, "longitudeRef", null, 0, -1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getAdminMapping_ElevationRef(), ecorePackage.getEStructuralFeature(), null, "elevationRef", null, 0, -1, AdminMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(metadataMappingEClass, MetadataMapping.class, "MetadataMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(mappingEClass, Mapping.class, "Mapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getMapping_Mid(), ecorePackage.getEString(), "mid", null, 1, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMapping_Timestamp(), this.getTimestampMapping(), null, "timestamp", null, 0, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getMapping_Name(), this.getNameMapping(), null, "name", null, 1, 1, Mapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getMapping_FqMid(), ecorePackage.getEString(), "fqMid", null, 0, 1, Mapping.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		op = initEOperation(getMapping__MapId__Function_EObject(), null, "mapId", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getJavaStringFunction(), "idFunction", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "eobject", 1, 1, IS_UNIQUE, IS_ORDERED);

		op = initEOperation(getMapping__MapName__Function_EObject(), null, "mapName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getJavaStringFunction(), "nameFunction", 1, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEObject(), "eobject", 1, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(featureMappingEClass, FeatureMapping.class, "FeatureMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFeatureMapping_FunctionId(), ecorePackage.getEString(), "functionId", null, 0, 1, FeatureMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getFeatureMapping_FeaturePath(), ecorePackage.getEStructuralFeature(), null, "featurePath", null, 0, -1, FeatureMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureMapping_CollectionIndex(), ecorePackage.getEInt(), "collectionIndex", "0", 0, 1, FeatureMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getFeatureMapping_CollectionFilter(), ecorePackage.getEString(), "collectionFilter", null, 0, 1, FeatureMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(timestampMappingEClass, TimestampMapping.class, "TimestampMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTimestampMapping_Strategy(), this.getTimestampStrategy(), "strategy", null, 0, 1, TimestampMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTimestampMapping_Timestamp(), this.getEInstant(), "timestamp", null, 0, 1, TimestampMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTimestampMapping_Hint(), ecorePackage.getEString(), "hint", null, 0, 1, TimestampMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(nameMappingEClass, NameMapping.class, "NameMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNameMapping_Name(), ecorePackage.getEString(), "name", null, 0, 1, NameMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(valueMappingEClass, ValueMapping.class, "ValueMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getValueMapping_Value(), ecorePackage.getEString(), "value", null, 0, 1, ValueMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringToStringMapEClass, Map.Entry.class, "StringToStringMap", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringToStringMap_Key(), ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStringToStringMap_Value(), ecorePackage.getEString(), "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(referenceMappingEClass, ReferenceMapping.class, "ReferenceMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getReferenceMapping_Filter(), ecorePackage.getEAttribute(), null, "filter", null, 0, -1, ReferenceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getReferenceMapping_Exclude(), ecorePackage.getEBoolean(), "exclude", "true", 0, 1, ReferenceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReferenceMapping_ReferenceMappings(), this.getReferenceMapping(), null, "referenceMappings", null, 0, -1, ReferenceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getReferenceMapping_TargetEClass(), ecorePackage.getEClass(), null, "targetEClass", null, 0, 1, ReferenceMapping.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(persistenceRuleRegistryEClass, PersistenceRuleRegistry.class, "PersistenceRuleRegistry", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPersistenceRuleRegistry_ChangeRules(), this.getChangeRule(), null, "changeRules", null, 0, -1, PersistenceRuleRegistry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPersistenceRuleRegistry_DeletionRules(), this.getDeletionRule(), null, "deletionRules", null, 0, -1, PersistenceRuleRegistry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(persistenceRuleEClass, PersistenceRule.class, "PersistenceRule", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPersistenceRule_Id(), ecorePackage.getEString(), "id", null, 1, 1, PersistenceRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceRule_Name(), ecorePackage.getEString(), "name", null, 0, 1, PersistenceRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPersistenceRule_Description(), ecorePackage.getEString(), "description", null, 0, 1, PersistenceRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(changeRuleEClass, ChangeRule.class, "ChangeRule", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(percentageChangeRuleEClass, PercentageChangeRule.class, "PercentageChangeRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPercentageChangeRule_Percentage(), ecorePackage.getEDoubleObject(), "percentage", null, 1, 1, PercentageChangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(absoluteChangeRuleEClass, AbsoluteChangeRule.class, "AbsoluteChangeRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getAbsoluteChangeRule_Delta(), ecorePackage.getEDoubleObject(), "delta", null, 1, 1, AbsoluteChangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(countChangeRuleEClass, CountChangeRule.class, "CountChangeRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getCountChangeRule_N(), ecorePackage.getEIntegerObject(), "n", null, 1, 1, CountChangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(timeThrottleChangeRuleEClass, TimeThrottleChangeRule.class, "TimeThrottleChangeRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTimeThrottleChangeRule_Interval(), ecorePackage.getEIntegerObject(), "interval", null, 1, 1, TimeThrottleChangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTimeThrottleChangeRule_IntervalUnit(), this.getDurationUnit(), "intervalUnit", null, 1, 1, TimeThrottleChangeRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(deletionRuleEClass, DeletionRule.class, "DeletionRule", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getDeletionRule_Retention(), ecorePackage.getEIntegerObject(), "retention", null, 1, 1, DeletionRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeletionRule_RetentionUnit(), this.getDurationUnit(), "retentionUnit", null, 1, 1, DeletionRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeletionRule_CleanupInterval(), ecorePackage.getEIntegerObject(), "cleanupInterval", null, 0, 1, DeletionRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeletionRule_CleanupIntervalUnit(), this.getDurationUnit(), "cleanupIntervalUnit", null, 0, 1, DeletionRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getDeletionRule_MaxCount(), ecorePackage.getEIntegerObject(), "maxCount", null, 0, 1, DeletionRule.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(timestampStrategyEEnum, TimestampStrategy.class, "TimestampStrategy");
		addEEnumLiteral(timestampStrategyEEnum, TimestampStrategy.NOW);
		addEEnumLiteral(timestampStrategyEEnum, TimestampStrategy.FEATURE);
		addEEnumLiteral(timestampStrategyEEnum, TimestampStrategy.FUNCTION);

		initEEnum(providerStrategyEEnum, ProviderStrategy.class, "ProviderStrategy");
		addEEnumLiteral(providerStrategyEEnum, ProviderStrategy.SEPARATE);
		addEEnumLiteral(providerStrategyEEnum, ProviderStrategy.UNIFIED);

		initEEnum(durationUnitEEnum, DurationUnit.class, "DurationUnit");
		addEEnumLiteral(durationUnitEEnum, DurationUnit.MILLISECONDS);
		addEEnumLiteral(durationUnitEEnum, DurationUnit.SECONDS);
		addEEnumLiteral(durationUnitEEnum, DurationUnit.MINUTES);
		addEEnumLiteral(durationUnitEEnum, DurationUnit.HOURS);
		addEEnumLiteral(durationUnitEEnum, DurationUnit.DAYS);

		// Initialize data types
		initEDataType(eInstantEDataType, Instant.class, "EInstant", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(javaStringFunctionEDataType, Function.class, "JavaStringFunction", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.function.Function<org.eclipse.emf.ecore.EObject, java.lang.String>");
		initEDataType(javaInstantFunctionEDataType, Function.class, "JavaInstantFunction", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.function.Function<org.eclipse.emf.ecore.EObject, java.time.Instant>");
		initEDataType(javaObjectFunctionEDataType, Function.class, "JavaObjectFunction", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.function.Function<org.eclipse.emf.ecore.EObject, ?>");

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/OCL/Import
		createImportAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/OCL/Import</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createImportAnnotations() {
		String source = "http://www.eclipse.org/OCL/Import";
		addAnnotation
		  (this,
		   source,
		   new String[] {
			   "ecore", "http://www.eclipse.org/emf/2002/Ecore"
		   });
	}

} //MappingPackageImpl
