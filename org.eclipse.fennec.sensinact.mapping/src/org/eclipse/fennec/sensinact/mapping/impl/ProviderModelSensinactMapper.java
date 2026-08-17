/*
 * ******************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 * ******************************************************************
 */
package org.eclipse.fennec.sensinact.mapping.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.sensinact.mapping.MappingProfileRegistry;
import org.eclipse.fennec.sensinact.mapping.SensinactMapperConstants;
import org.eclipse.fennec.sensinact.model.mapping.AdminMapping;
import org.eclipse.fennec.sensinact.model.mapping.Mapping;
import org.eclipse.fennec.sensinact.model.mapping.MappingFactory;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.NameMapping;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy;
import org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ResourceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ServiceMapping;
import org.eclipse.sensinact.core.emf.model.EMFModel;
import org.eclipse.sensinact.core.emf.model.SensinactEMFModelManager;
import org.eclipse.sensinact.core.emf.twin.SensinactEMFDigitalTwin;
import org.eclipse.sensinact.core.model.Model;
import org.eclipse.sensinact.core.model.Resource;
import org.eclipse.sensinact.core.model.ResourceBuilder;
import org.eclipse.sensinact.core.model.ResourceType;
import org.eclipse.sensinact.core.model.Service;
import org.eclipse.sensinact.core.twin.SensinactProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This mapper translates the {@link Mapping} instances into the sensinact core model
 * @author Mark Hoffmann
 * @since 16.07.2025
 */
public class ProviderModelSensinactMapper {
	
	/**
	 * GenModel annotation source URI.
	 */
	private static final String GENMODEL_ANNOTATION = "http://www.eclipse.org/emf/2002/GenModel";

	/**
	 * Documentation detail key in GenModel annotations.
	 */
	private static final String DOCUMENTATION_KEY = "documentation";
	
	

	private static final Logger logger = LoggerFactory.getLogger(ProviderModelSensinactMapper.class);
	private final SensinactEMFModelManager modelManager;
	private final SensinactEMFDigitalTwin twin;
	private final MappingProfileRegistry profileRegistry;

	/**
	 * Factory to create mapping instance
	 */
	public static class Factory {

		private MappingProfileRegistry profileRegistry;

		/**
		 * Creates a new instance.
		 */
		public Factory(final MappingProfileRegistry profileRegistry) {
			this.profileRegistry = profileRegistry;
		}

		public ProviderModelSensinactMapper createMapper(final SensinactEMFDigitalTwin twin, final SensinactEMFModelManager modelManager) {
			return new ProviderModelSensinactMapper(twin, modelManager, profileRegistry);
		}
	}

	private ProviderModelSensinactMapper(final SensinactEMFDigitalTwin twin, final SensinactEMFModelManager modelManager, 
			final MappingProfileRegistry profileRegistry) {
		this.twin = twin;
		this.modelManager = modelManager;
		this.profileRegistry = profileRegistry;
	}

	/**
	 * Registers a {@link ProviderMapping} into sensinact
	 * @param providerMapping the {@link ProviderMapping}
	 */
	public void registerModelMapping(ProviderMapping providerMapping) {
		if (isNull(providerMapping)) {
			return;
		}

		// Validate against profile if one is referenced
		if (nonNull(providerMapping.getProfile()) && nonNull(profileRegistry)) {
			MappingProfileRegistry.ValidationResult validationResult = profileRegistry.validateMapping(providerMapping);
			if (!validationResult.isValid()) {
				StringBuilder errorMessage = new StringBuilder("Mapping validation failed for provider '")
						.append(displayName(providerMapping))
						.append("':\n");

				for (String error : validationResult.getErrors()) {
					errorMessage.append("  - ").append(error).append("\n");
				}

				if (!validationResult.getWarnings().isEmpty()) {
					errorMessage.append("Warnings:\n");
					for (String warning : validationResult.getWarnings()) {
						errorMessage.append("  - ").append(warning).append("\n");
					}
				}

				throw new IllegalArgumentException(errorMessage.toString());
			}

			// Log warnings if any
			if (!validationResult.getWarnings().isEmpty()) {
				String providerName = displayName(providerMapping);
				logger.warn("Mapping validation warnings for provider '{}':", providerName);
				for (String warning : validationResult.getWarnings()) {
					logger.warn("  - {}", warning);
				}
			}
		}

		mapProvider(providerMapping);

		MappingProfile profile = providerMapping.getProfile();
		String actualProviderId = determineProviderId(providerMapping);
		String profileInfo = profile != null ? 
				" (using profile: " + profile.getProfileId() + " v" + profile.getVersion() + 
				", strategy: " + profile.getProviderStrategy() + ")" : "";

		String providerName = displayName(providerMapping);
		logger.info("Model for provider '{}' → '{}' successfully registered{}.", providerName, actualProviderId, profileInfo);
	}

	/**
	 * Returns a human-readable provider name for log and error messages: the static mapping
	 * name if set, otherwise the mid. A NameMapping may carry only a feature path, in which
	 * case the name is resolvable per instance only, not at registration time.
	 * @param providerMapping the {@link ProviderMapping}
	 * @return the display name; never {@code null} for valid mappings
	 */
	private static String displayName(ProviderMapping providerMapping) {
		NameMapping name = providerMapping.getName();
		return name != null && name.getName() != null ? name.getName() : providerMapping.getMid();
	}

	/**
	 * Un-registers a {@link ProviderMapping} from sensinact
	 * @param providerMapping the {@link ProviderMapping}
	 */
	public void unregisterModelMapping(ProviderMapping providerMapping) {
		if (nonNull(providerMapping)) {
			String actualProviderId = determineProviderId(providerMapping);
			modelManager.deleteModel(actualProviderId);
			logger.info("Model successfully unregistered for '{}'.", actualProviderId);
		}
	}

	static Class<?> boxClass(Class<?> unboxed) {
		if (unboxed.isPrimitive()) {
			return switch (unboxed.getName()) {
				case "float": {
					yield Float.class;
				}
				case "byte": {
					yield Byte.class;
				}
				case "char": {
					yield Character.class;
				}
				case "short": {
					yield Short.class;
				}
				case "int": {
					yield Integer.class;
				}
				case "double": {
					yield Double.class;
				}
				case "boolean": {
					yield Boolean.class;
				}
				case "long": {
					yield Long.class;
				}
				default:
					throw new IllegalArgumentException("Unexpected value: " + unboxed.getName());
				};
		}
		return unboxed;
	}

	/**
	 * Maps a provider
	 * @param providerMapping the {@link ProviderMapping}
	 */
	private void mapProvider(ProviderMapping providerMapping) {
		// Determine the actual provider ID based on the strategy
		String actualProviderId = determineProviderId(providerMapping);

		SensinactProvider provider = twin.getProvider(actualProviderId);
		
		if (isNull(provider)) {
			AdminMapping adminMapping = providerMapping.getAdmin();
			requireNonNull(adminMapping);
			
//			EPackage providerPackage = adminMapping.getProviderPackage();
			EMFModel model = modelManager.getModel(actualProviderId);
			if (isNull(model)) {
				model = modelManager.createModel(actualProviderId).build();
			}
//			mapService(model, adminMapping);
//			mapAdminService(model, adminMapping);

			for (ServiceMapping serviceMapping : providerMapping.getServices()) {
				mapService(model, serviceMapping);
			}
//			twin.createProvider(providerPackage.getNsURI(), providerPackage.getName(), providerMapping.getMid());
		}
	}
	
//	/**
//	 * Maps the admin service
//	 * @param model the {@link Model}
//	 * @param adminMapping the {@link AdminMapping}
//	 */
//	private Service mapAdminService(EMFModel model, AdminMapping adminMapping) {
//		requireNonNull(adminMapping);
//		final Instant timestamp = Instant.now();
//		Service admin = mapService(model, adminMapping);
//		EClass adminClass = (EClass) model.getModelEClass().getEStructuralFeature("admin").getEType();
//		EAttribute friendlyNameAttr = (EAttribute) adminClass.getEStructuralFeature("friendlyName");
//		friendlyNameAttr.setDefaultValueLiteral("test");
//		if (nonNull(adminMapping.getFriendlyName())) {
//			admin.createResource("friendlyName").
//			withResourceType(ResourceType.PROPERTY).
//			withType(String.class).
//			withInitialValue(adminMapping.getFriendlyName(), timestamp).
//			build();
//		}
//		final GeoJsonObject location = lookupLocation(adminMapping);
//		if (nonNull(location)) {
//			EAttribute locationAttr = (EAttribute) adminClass.getEStructuralFeature("location");
//			locationAttr.setDefaultValue(location);
//			admin.createResource("location").
//			withResourceType(ResourceType.PROPERTY).
//			withType(GeoJsonObject.class).
//			withInitialValue(location, timestamp).
//			build();
//		}
//		return admin;
//	}


	/**
	 * Determines the provider ID to use based on the mapping strategy.
	 * For UNIFIED strategy, uses the profile's providerId.
	 * For SEPARATE strategy, uses the mapping's own ID.
	 * 
	 * @param providerMapping the provider mapping
	 * @return the provider ID to use
	 */
	private String determineProviderId(ProviderMapping providerMapping) {
		MappingProfile profile = providerMapping.getProfile();
		if (profile != null && profile.getProviderStrategy() == ProviderStrategy.UNIFIED) {
			// Use the profile's providerId for unified strategy
			return profile.getProvider().getProviderId();
		} else {
			// Use the mapping's own ID for separate strategy (default)
			return providerMapping.getFqMid();
		}
	}

	/**
	 * Maps into a sensinact {@link Service}
	 * @param model the {@link Model}
	 * @param serviceMapping the {@link ServiceMapping}
	 */
	private Service mapService(Model model, ServiceMapping serviceMapping) {
		Service service = model.getServices().get(serviceMapping.getMid());
		if (isNull(service)) {
			service = model.createService(serviceMapping.getMid()).build();
		}

		// Auto-generate ResourceMappings from ReferenceMapping if configured
		if (serviceMapping.getReferencedResource() != null) {
			generateResourcesFromReferenceMapping(serviceMapping);
		}

		for (ResourceMapping resourceMapping : serviceMapping.getResources()) {
			mapResource(service, resourceMapping);
		}
		for (ResourceMapping resourceMapping : serviceMapping.getTemporaryResources()) {
			mapResource(service, resourceMapping);
		}
		return service;
	}

	/**
	 * Generates ResourceMapping objects from a ReferenceMapping configuration.
	 * This auto-generates resources for all attributes of the referenced type.
	 *
	 * @param serviceMapping the service mapping containing the ReferenceMapping
	 */
	private void generateResourcesFromReferenceMapping(ServiceMapping serviceMapping) {
		ReferenceMapping refMapping = serviceMapping.getReferencedResource();

		// Determine the target EClass
		EClass targetClass = null;

		// First priority: Check if targetEClass is explicitly set
		if (refMapping.getTargetEClass() != null) {
			targetClass = refMapping.getTargetEClass();
			logger.debug("Using explicitly specified targetEClass: {} for service {}",
					targetClass.getName(), serviceMapping.getMid());
		} else {
			// Second priority: Infer from the feature path using the helper method
			targetClass = extractTargetEClassFromFeaturePath(
					refMapping.getFeaturePath(),
					"service " + serviceMapping.getMid());

			if (targetClass == null) {
				return; // extractTargetEClassFromFeaturePath already logged the reason
			}

			logger.debug("Inferred targetEClass from feature path: {} for service {}",
					targetClass.getName(), serviceMapping.getMid());
		}

		// Generate resources recursively, passing the base feature path from the ReferenceMapping
		generateResourcesFromEClass(targetClass, refMapping, serviceMapping.getTemporaryResources(), "",
				new ArrayList<>(refMapping.getFeaturePath()), false);
	}

	/**
	 * Recursively generates ResourceMapping objects from an EClass and ReferenceMapping.
	 *
	 * @param eClass the EClass to extract attributes from
	 * @param refMapping the ReferenceMapping configuration
	 * @param resourceList the list to add generated ResourceMappings to
	 * @param namePrefix prefix for resource names (e.g., "station" for nested references)
	 * @param baseFeaturePath the base feature path from the root object to the current object
	 */
	private void generateResourcesFromEClass(EClass eClass,
			ReferenceMapping refMapping,
			EList<ResourceMapping> resourceList,
			String namePrefix,
			List<EStructuralFeature> baseFeaturePath, boolean nested) {

		// Get filtered attributes
		List<EAttribute> allAttributes = eClass.getEAllAttributes();
		List<EAttribute> filterList = refMapping.getFilter();
		boolean exclude = refMapping.isExclude();

		// Build the list of attributes to map
		List<EAttribute> attributesToMap = new ArrayList<>();

		if (filterList == null || filterList.isEmpty()) {
			// No filter: exclude=true (default) includes all attributes; exclude=false includes
			// none - a selector-only ReferenceMapping that just picks the service source element.
			// Mirrors ValueMapperImpl.getFilteredAttributes.
			if (exclude) {
				attributesToMap.addAll(allAttributes);
			}
		} else {
			if (exclude) {
				// exclude=true: include all attributes EXCEPT those in filter
				for (EAttribute attr : allAttributes) {
					if (!filterList.contains(attr)) {
						attributesToMap.add(attr);
					}
				}
			} else {
				// exclude=false: include ONLY those attributes in filter
				for (EAttribute attr : filterList) {
					if (allAttributes.contains(attr)) {
						attributesToMap.add(attr);
					}
				}
			}
		}

		// Generate ResourceMapping for each attribute
		for (EAttribute attribute : attributesToMap) {
			String resourceName = namePrefix.isEmpty() ?
					attribute.getName() :
					namePrefix + capitalize(attribute.getName());

			ResourceMapping resourceMapping = MappingFactory.eINSTANCE.createResourceMapping();
			resourceMapping.setMid(resourceName);
			resourceMapping.setName(attribute.getName());
			resourceMapping.setEType(attribute.getEAttributeType());
			// Copy, don't move: eAnnotations is a containment reference, so addAll would
			// relocate the annotations out of the shared (globally registered) source EPackage,
			// stripping them for every subsequent mapping registration.
			attribute.getEAnnotations().forEach(a -> resourceMapping.getEAnnotations().add(EcoreUtil.copy(a)));

			// Set the complete feature path: base path + attribute
			// This allows ValueMapperImpl to correctly extract values from the root object
			if(nested) resourceMapping.getValueFeature().addAll(baseFeaturePath);
			resourceMapping.getValueFeature().add(attribute);

			resourceList.add(resourceMapping);
			logger.debug("Auto-generated resource {} from ReferenceMapping", resourceName);
		}

		// Handle nested EReferences if referenceMappings are configured
		if (refMapping.getReferenceMappings() != null && !refMapping.getReferenceMappings().isEmpty()) {
			List<EReference> allReferences = eClass.getEAllReferences();

			for (ReferenceMapping nestedMapping : refMapping.getReferenceMappings()) {
				EClass nestedClass = null;
				EReference targetReference = null;

				// Find the target reference from the feature path
				targetReference = findTargetReference(allReferences, nestedMapping);
				if (targetReference == null) {
					logger.warn("Could not find target EReference for nested ReferenceMapping in {}",
							eClass.getName());
					continue;
				}

				// Check if targetEClass is explicitly set for the nested mapping
				if (nestedMapping.getTargetEClass() != null) {
					nestedClass = nestedMapping.getTargetEClass();
					logger.debug("Using explicitly specified targetEClass: {} for nested reference",
							nestedClass.getName());
				} else {
					// Infer from the feature path using the helper method
					nestedClass = extractTargetEClassFromFeaturePath(
							nestedMapping.getFeaturePath(),
							"nested reference " + targetReference.getName());

					if (nestedClass == null) {
						continue; // extractTargetEClassFromFeaturePath already logged the reason
					}

					logger.debug("Inferred targetEClass from reference: {} for nested reference",
							nestedClass.getName());
				}

				String nestedPrefix = namePrefix.isEmpty() ?
						targetReference.getName() :
						namePrefix + capitalize(targetReference.getName());

				// Build the feature path for the nested reference
				List<EStructuralFeature> nestedFeaturePath =
						new ArrayList<>();
				nestedFeaturePath.add(targetReference);

				// Recursively generate resources from the nested object
				generateResourcesFromEClass(nestedClass, nestedMapping, resourceList, nestedPrefix, nestedFeaturePath, true);
			}
		}
	}

	/**
	 * Finds the EReference that a ReferenceMapping is targeting.
	 */
	private EReference findTargetReference(
			List<EReference> allReferences,
			ReferenceMapping nestedMapping) {

		List<EStructuralFeature> featurePath = nestedMapping.getFeaturePath();
		if (featurePath == null || featurePath.isEmpty()) {
			return null;
		}

		// The first element in the feature path should be the EReference we're looking for
		EStructuralFeature firstFeature = featurePath.get(0);
		if (firstFeature instanceof EReference) {
			EReference targetRef = (EReference) firstFeature;
			// Verify it's in the list of available references
			if (allReferences.contains(targetRef)) {
				return targetRef;
			}
		}

		return null;
	}

	/**
	 * Extracts the target EClass from a feature path by examining the last feature's type.
	 * Returns null if the feature path is empty, the target is not an EClass, or the
	 * target belongs to the Ecore EPackage.
	 *
	 * @param featurePath the feature path to analyze
	 * @param contextDescription description for logging (e.g., "service XYZ" or "nested reference")
	 * @return the target EClass or null if not applicable
	 */
	private EClass extractTargetEClassFromFeaturePath(List<EStructuralFeature> featurePath, String contextDescription) {
		if (featurePath == null || featurePath.isEmpty()) {
			logger.warn("Feature path is empty for {}", contextDescription);
			return null;
		}

		// Get the last feature in the path - this determines the target type
		EStructuralFeature lastFeature = featurePath.get(featurePath.size() - 1);
		EClassifier targetType = lastFeature.getEType();

		// Check if target is the Ecore EPackage (which we don't map)
		if (targetType.getEPackage().getName().equals("ecore")) {
			logger.warn("ReferenceMapping target is the Ecore EPackage for {} - not mapping", contextDescription);
			return null;
		}

		// Ensure it's an EClass (not an EDataType)
		if (!(targetType instanceof EClass)) {
			logger.warn("Feature path target is not an EClass for {}", contextDescription);
			return null;
		}

		return (EClass) targetType;
	}

	/**
	 * Capitalizes the first letter of a string (used for building nested resource names).
	 */
	private String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Maps a resource for a sensinact service
	 * @param service the sensinact {@link Service}
	 * @param resourceMapping the {@link ResourceMapping}
	 */
	@SuppressWarnings({ "unchecked" })
	private static Resource mapResource(Service service, ResourceMapping resourceMapping) {
		Resource resource = service.getResources().get(resourceMapping.getMid());
		if (isNull(resource)) {
			ResourceBuilder<Resource, Object> resourceBuilder = service.createResource(resourceMapping.getMid()).
					withResourceType(ResourceType.SENSOR);
			EDataType valueType = resourceMapping.getEAttributeType();
			Class<?> type = valueType.getInstanceClass();
			if (nonNull(type)) {
				resourceBuilder = resourceBuilder.withType((Class<Object>) boxClass(type));
			}
			if (nonNull(resourceMapping.getDefaultValue())) {
				Object defaultValue = EcoreUtil.createFromString(valueType, resourceMapping.getDefaultValue().toString());
				resourceBuilder = resourceBuilder.withInitialValue(defaultValue);
			}
			Map<String, Object> metadata = new HashMap<>();
			if (nonNull(resourceMapping.getUnit())) {
				metadata.put("unit", resourceMapping.getUnit());
			} else {
				metadata.put("unit", extractAnnotationValue(resourceMapping, SensinactMapperConstants.SENSINACT_MAPPING_ANNOTATION_SOURCE, SensinactMapperConstants.SENSINACT_MAPPING_UNIT));
			}
			
			if (nonNull(resourceMapping.getName()) ) {
				metadata.put("friendlyName", resourceMapping.getName());
			}
			
			if (nonNull(resourceMapping.getDescriptionMapping()) ) {
				metadata.put("description", resourceMapping.getDescriptionMapping().getName());
			} else {
				metadata.put("description", extractAnnotationValue(resourceMapping, SensinactMapperConstants.SENSINACT_MAPPING_ANNOTATION_SOURCE, 
						SensinactMapperConstants.SENSINACT_MAPPING_DESCRIPTION).isEmpty() ? 
						extractAnnotationValue(resourceMapping, GENMODEL_ANNOTATION, DOCUMENTATION_KEY) : 
						extractAnnotationValue(resourceMapping, SensinactMapperConstants.SENSINACT_MAPPING_ANNOTATION_SOURCE, SensinactMapperConstants.SENSINACT_MAPPING_DESCRIPTION));
			}
			
			if(!resourceMapping.getExtraMetadata().isEmpty()) {
				resourceMapping.getExtraMetadata().forEach(e -> metadata.put(e.getKey(), e.getValue()));
			} else {
				metadata.putAll(extractAnnotationDetails(resourceMapping, SensinactMapperConstants.SENSINACT_MAPPING_METADATA_ANNOTATION_SOURCE));
			}
			if (!metadata.isEmpty()) {
				resourceBuilder = resourceBuilder.withDefaultMetadata(metadata);
			}
			resource = resourceBuilder.build();
		}
		return resource;
	}
	
	private static String extractAnnotationValue(EAttribute eAttribute, String source, String detailKey) {
		if(eAttribute.getEAnnotation(source) != null) {
			if(eAttribute.getEAnnotation(source).getDetails().containsKey(detailKey)) {
				return eAttribute.getEAnnotation(source).getDetails().get(detailKey);
			}
		}
		return "";
	}
	
	private static Map<String, String> extractAnnotationDetails(EAttribute eAttribute, String source) {
		if(eAttribute.getEAnnotation(source) != null) {
			return eAttribute.getEAnnotation(source).getDetails().map();
		}
		return Collections.emptyMap();
	}

}
