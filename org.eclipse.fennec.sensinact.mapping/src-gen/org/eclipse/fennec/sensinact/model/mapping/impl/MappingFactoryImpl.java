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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.fennec.sensinact.model.mapping.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MappingFactoryImpl extends EFactoryImpl implements MappingFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static MappingFactory init() {
		try {
			MappingFactory theMappingFactory = (MappingFactory)EPackage.Registry.INSTANCE.getEFactory(MappingPackage.eNS_URI);
			if (theMappingFactory != null) {
				return theMappingFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new MappingFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MappingFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case MappingPackage.MAPPING_PROFILE: return createMappingProfile();
			case MappingPackage.PROFILE_PROVIDER: return createProfileProvider();
			case MappingPackage.PROFILE_SERVICE: return createProfileService();
			case MappingPackage.PROFILE_RESOURCE: return createProfileResource();
			case MappingPackage.PROFILE_ADMIN: return createProfileAdmin();
			case MappingPackage.PROVIDER_MAPPING: return createProviderMapping();
			case MappingPackage.SERVICE_MAPPING: return createServiceMapping();
			case MappingPackage.RESOURCE_MAPPING: return createResourceMapping();
			case MappingPackage.ADMIN_MAPPING: return createAdminMapping();
			case MappingPackage.METADATA_MAPPING: return createMetadataMapping();
			case MappingPackage.MAPPING: return createMapping();
			case MappingPackage.FEATURE_MAPPING: return createFeatureMapping();
			case MappingPackage.TIMESTAMP_MAPPING: return createTimestampMapping();
			case MappingPackage.NAME_MAPPING: return createNameMapping();
			case MappingPackage.VALUE_MAPPING: return createValueMapping();
			case MappingPackage.STRING_TO_STRING_MAP: return (EObject)createStringToStringMap();
			case MappingPackage.REFERENCE_MAPPING: return createReferenceMapping();
			case MappingPackage.PERSISTENCE_RULE_REGISTRY: return createPersistenceRuleRegistry();
			case MappingPackage.PERCENTAGE_CHANGE_RULE: return createPercentageChangeRule();
			case MappingPackage.ABSOLUTE_CHANGE_RULE: return createAbsoluteChangeRule();
			case MappingPackage.COUNT_CHANGE_RULE: return createCountChangeRule();
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE: return createTimeThrottleChangeRule();
			case MappingPackage.DELETION_RULE: return createDeletionRule();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case MappingPackage.TIMESTAMP_STRATEGY:
				return createTimestampStrategyFromString(eDataType, initialValue);
			case MappingPackage.PROVIDER_STRATEGY:
				return createProviderStrategyFromString(eDataType, initialValue);
			case MappingPackage.DURATION_UNIT:
				return createDurationUnitFromString(eDataType, initialValue);
			case MappingPackage.EINSTANT:
				return createEInstantFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case MappingPackage.TIMESTAMP_STRATEGY:
				return convertTimestampStrategyToString(eDataType, instanceValue);
			case MappingPackage.PROVIDER_STRATEGY:
				return convertProviderStrategyToString(eDataType, instanceValue);
			case MappingPackage.DURATION_UNIT:
				return convertDurationUnitToString(eDataType, instanceValue);
			case MappingPackage.EINSTANT:
				return convertEInstantToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MappingProfile createMappingProfile() {
		MappingProfileImpl mappingProfile = new MappingProfileImpl();
		return mappingProfile;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProfileProvider createProfileProvider() {
		ProfileProviderImpl profileProvider = new ProfileProviderImpl();
		return profileProvider;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProfileService createProfileService() {
		ProfileServiceImpl profileService = new ProfileServiceImpl();
		return profileService;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProfileResource createProfileResource() {
		ProfileResourceImpl profileResource = new ProfileResourceImpl();
		return profileResource;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProfileAdmin createProfileAdmin() {
		ProfileAdminImpl profileAdmin = new ProfileAdminImpl();
		return profileAdmin;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ProviderMapping createProviderMapping() {
		ProviderMappingImpl providerMapping = new ProviderMappingImpl();
		return providerMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServiceMapping createServiceMapping() {
		ServiceMappingImpl serviceMapping = new ServiceMappingImpl();
		return serviceMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceMapping createResourceMapping() {
		ResourceMappingImpl resourceMapping = new ResourceMappingImpl();
		return resourceMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AdminMapping createAdminMapping() {
		AdminMappingImpl adminMapping = new AdminMappingImpl();
		return adminMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MetadataMapping createMetadataMapping() {
		MetadataMappingImpl metadataMapping = new MetadataMappingImpl();
		return metadataMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Mapping createMapping() {
		MappingImpl mapping = new MappingImpl();
		return mapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureMapping createFeatureMapping() {
		FeatureMappingImpl featureMapping = new FeatureMappingImpl();
		return featureMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TimestampMapping createTimestampMapping() {
		TimestampMappingImpl timestampMapping = new TimestampMappingImpl();
		return timestampMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NameMapping createNameMapping() {
		NameMappingImpl nameMapping = new NameMappingImpl();
		return nameMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ValueMapping createValueMapping() {
		ValueMappingImpl valueMapping = new ValueMappingImpl();
		return valueMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map.Entry<String, String> createStringToStringMap() {
		StringToStringMapImpl stringToStringMap = new StringToStringMapImpl();
		return stringToStringMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ReferenceMapping createReferenceMapping() {
		ReferenceMappingImpl referenceMapping = new ReferenceMappingImpl();
		return referenceMapping;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PersistenceRuleRegistry createPersistenceRuleRegistry() {
		PersistenceRuleRegistryImpl persistenceRuleRegistry = new PersistenceRuleRegistryImpl();
		return persistenceRuleRegistry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public PercentageChangeRule createPercentageChangeRule() {
		PercentageChangeRuleImpl percentageChangeRule = new PercentageChangeRuleImpl();
		return percentageChangeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public AbsoluteChangeRule createAbsoluteChangeRule() {
		AbsoluteChangeRuleImpl absoluteChangeRule = new AbsoluteChangeRuleImpl();
		return absoluteChangeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public CountChangeRule createCountChangeRule() {
		CountChangeRuleImpl countChangeRule = new CountChangeRuleImpl();
		return countChangeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public TimeThrottleChangeRule createTimeThrottleChangeRule() {
		TimeThrottleChangeRuleImpl timeThrottleChangeRule = new TimeThrottleChangeRuleImpl();
		return timeThrottleChangeRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DeletionRule createDeletionRule() {
		DeletionRuleImpl deletionRule = new DeletionRuleImpl();
		return deletionRule;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TimestampStrategy createTimestampStrategyFromString(EDataType eDataType, String initialValue) {
		TimestampStrategy result = TimestampStrategy.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertTimestampStrategyToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ProviderStrategy createProviderStrategyFromString(EDataType eDataType, String initialValue) {
		ProviderStrategy result = ProviderStrategy.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertProviderStrategyToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DurationUnit createDurationUnitFromString(EDataType eDataType, String initialValue) {
		DurationUnit result = DurationUnit.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDurationUnitToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Instant createEInstantFromString(EDataType eDataType, String initialValue) {
		return (Instant)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertEInstantToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public MappingPackage getMappingPackage() {
		return (MappingPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static MappingPackage getPackage() {
		return MappingPackage.eINSTANCE;
	}

} //MappingFactoryImpl
