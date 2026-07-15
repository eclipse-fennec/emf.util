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
package org.eclipse.fennec.sensinact.model.mapping.util;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.fennec.sensinact.model.mapping.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage
 * @generated
 */
public class MappingSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static MappingPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public MappingSwitch() {
		if (modelPackage == null) {
			modelPackage = MappingPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case MappingPackage.MAPPING_PROFILE: {
				MappingProfile mappingProfile = (MappingProfile)theEObject;
				T result = caseMappingProfile(mappingProfile);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PROFILE_PROVIDER: {
				ProfileProvider profileProvider = (ProfileProvider)theEObject;
				T result = caseProfileProvider(profileProvider);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PROFILE_SERVICE: {
				ProfileService profileService = (ProfileService)theEObject;
				T result = caseProfileService(profileService);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PROFILE_RESOURCE: {
				ProfileResource profileResource = (ProfileResource)theEObject;
				T result = caseProfileResource(profileResource);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PROFILE_ADMIN: {
				ProfileAdmin profileAdmin = (ProfileAdmin)theEObject;
				T result = caseProfileAdmin(profileAdmin);
				if (result == null) result = caseProfileService(profileAdmin);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PROVIDER_MAPPING: {
				ProviderMapping providerMapping = (ProviderMapping)theEObject;
				T result = caseProviderMapping(providerMapping);
				if (result == null) result = caseMapping(providerMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.SERVICE_MAPPING: {
				ServiceMapping serviceMapping = (ServiceMapping)theEObject;
				T result = caseServiceMapping(serviceMapping);
				if (result == null) result = caseMapping(serviceMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.RESOURCE_MAPPING: {
				ResourceMapping resourceMapping = (ResourceMapping)theEObject;
				T result = caseResourceMapping(resourceMapping);
				if (result == null) result = caseEAttribute(resourceMapping);
				if (result == null) result = caseEStructuralFeature(resourceMapping);
				if (result == null) result = caseETypedElement(resourceMapping);
				if (result == null) result = caseENamedElement(resourceMapping);
				if (result == null) result = caseEModelElement(resourceMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.ADMIN_MAPPING: {
				AdminMapping adminMapping = (AdminMapping)theEObject;
				T result = caseAdminMapping(adminMapping);
				if (result == null) result = caseServiceMapping(adminMapping);
				if (result == null) result = caseMapping(adminMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.METADATA_MAPPING: {
				MetadataMapping metadataMapping = (MetadataMapping)theEObject;
				T result = caseMetadataMapping(metadataMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.MAPPING: {
				Mapping mapping = (Mapping)theEObject;
				T result = caseMapping(mapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.FEATURE_MAPPING: {
				FeatureMapping featureMapping = (FeatureMapping)theEObject;
				T result = caseFeatureMapping(featureMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.TIMESTAMP_MAPPING: {
				TimestampMapping timestampMapping = (TimestampMapping)theEObject;
				T result = caseTimestampMapping(timestampMapping);
				if (result == null) result = caseFeatureMapping(timestampMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.NAME_MAPPING: {
				NameMapping nameMapping = (NameMapping)theEObject;
				T result = caseNameMapping(nameMapping);
				if (result == null) result = caseFeatureMapping(nameMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.VALUE_MAPPING: {
				ValueMapping valueMapping = (ValueMapping)theEObject;
				T result = caseValueMapping(valueMapping);
				if (result == null) result = caseFeatureMapping(valueMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.STRING_TO_STRING_MAP: {
				@SuppressWarnings("unchecked") Map.Entry<String, String> stringToStringMap = (Map.Entry<String, String>)theEObject;
				T result = caseStringToStringMap(stringToStringMap);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.REFERENCE_MAPPING: {
				ReferenceMapping referenceMapping = (ReferenceMapping)theEObject;
				T result = caseReferenceMapping(referenceMapping);
				if (result == null) result = caseFeatureMapping(referenceMapping);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PERSISTENCE_RULE_REGISTRY: {
				PersistenceRuleRegistry persistenceRuleRegistry = (PersistenceRuleRegistry)theEObject;
				T result = casePersistenceRuleRegistry(persistenceRuleRegistry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PERSISTENCE_RULE: {
				PersistenceRule persistenceRule = (PersistenceRule)theEObject;
				T result = casePersistenceRule(persistenceRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.CHANGE_RULE: {
				ChangeRule changeRule = (ChangeRule)theEObject;
				T result = caseChangeRule(changeRule);
				if (result == null) result = casePersistenceRule(changeRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.PERCENTAGE_CHANGE_RULE: {
				PercentageChangeRule percentageChangeRule = (PercentageChangeRule)theEObject;
				T result = casePercentageChangeRule(percentageChangeRule);
				if (result == null) result = caseChangeRule(percentageChangeRule);
				if (result == null) result = casePersistenceRule(percentageChangeRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.ABSOLUTE_CHANGE_RULE: {
				AbsoluteChangeRule absoluteChangeRule = (AbsoluteChangeRule)theEObject;
				T result = caseAbsoluteChangeRule(absoluteChangeRule);
				if (result == null) result = caseChangeRule(absoluteChangeRule);
				if (result == null) result = casePersistenceRule(absoluteChangeRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.COUNT_CHANGE_RULE: {
				CountChangeRule countChangeRule = (CountChangeRule)theEObject;
				T result = caseCountChangeRule(countChangeRule);
				if (result == null) result = caseChangeRule(countChangeRule);
				if (result == null) result = casePersistenceRule(countChangeRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.TIME_THROTTLE_CHANGE_RULE: {
				TimeThrottleChangeRule timeThrottleChangeRule = (TimeThrottleChangeRule)theEObject;
				T result = caseTimeThrottleChangeRule(timeThrottleChangeRule);
				if (result == null) result = caseChangeRule(timeThrottleChangeRule);
				if (result == null) result = casePersistenceRule(timeThrottleChangeRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case MappingPackage.DELETION_RULE: {
				DeletionRule deletionRule = (DeletionRule)theEObject;
				T result = caseDeletionRule(deletionRule);
				if (result == null) result = casePersistenceRule(deletionRule);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Profile</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Profile</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMappingProfile(MappingProfile object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Profile Provider</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Profile Provider</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProfileProvider(ProfileProvider object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Profile Service</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Profile Service</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProfileService(ProfileService object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Profile Resource</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Profile Resource</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProfileResource(ProfileResource object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Profile Admin</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Profile Admin</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProfileAdmin(ProfileAdmin object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Provider Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Provider Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseProviderMapping(ProviderMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Service Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Service Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseServiceMapping(ServiceMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Resource Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Resource Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseResourceMapping(ResourceMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Admin Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Admin Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAdminMapping(AdminMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Metadata Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Metadata Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMetadataMapping(MetadataMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseMapping(Mapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Feature Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Feature Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFeatureMapping(FeatureMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Timestamp Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Timestamp Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTimestampMapping(TimestampMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Name Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Name Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNameMapping(NameMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Value Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Value Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseValueMapping(ValueMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>String To String Map</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseStringToStringMap(Map.Entry<String, String> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Reference Mapping</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Reference Mapping</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseReferenceMapping(ReferenceMapping object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Persistence Rule Registry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Persistence Rule Registry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePersistenceRuleRegistry(PersistenceRuleRegistry object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Persistence Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Persistence Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePersistenceRule(PersistenceRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Change Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseChangeRule(ChangeRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Percentage Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Percentage Change Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePercentageChangeRule(PercentageChangeRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Absolute Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Absolute Change Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbsoluteChangeRule(AbsoluteChangeRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Count Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Count Change Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseCountChangeRule(CountChangeRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Time Throttle Change Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Time Throttle Change Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTimeThrottleChangeRule(TimeThrottleChangeRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Deletion Rule</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Deletion Rule</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDeletionRule(DeletionRule object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EModel Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EModel Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEModelElement(EModelElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>ENamed Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>ENamed Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseENamedElement(ENamedElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>ETyped Element</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>ETyped Element</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseETypedElement(ETypedElement object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EStructural Feature</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EStructural Feature</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEStructuralFeature(EStructuralFeature object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EAttribute</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EAttribute</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseEAttribute(EAttribute object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //MappingSwitch
