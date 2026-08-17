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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.sensinact.mapping.ValueMapper;
import org.eclipse.fennec.sensinact.mapping.ValueMappingException;
import org.eclipse.fennec.sensinact.model.mapping.AdminMapping;
import org.eclipse.fennec.sensinact.model.mapping.FeatureMapping;
import org.eclipse.fennec.sensinact.model.mapping.MappingProfile;
import org.eclipse.fennec.sensinact.model.mapping.NameMapping;
import org.eclipse.fennec.sensinact.model.mapping.ProviderMapping;
import org.eclipse.fennec.sensinact.model.mapping.ProviderStrategy;
import org.eclipse.fennec.sensinact.model.mapping.ReferenceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ResourceMapping;
import org.eclipse.fennec.sensinact.model.mapping.ServiceMapping;
import org.eclipse.fennec.sensinact.model.mapping.TimestampMapping;
import org.eclipse.sensinact.core.twin.SensinactDigitalTwin;
import org.eclipse.sensinact.core.twin.SensinactProvider;
import org.eclipse.sensinact.core.twin.SensinactResource;
import org.eclipse.sensinact.core.twin.SensinactService;
import org.eclipse.sensinact.gateway.geojson.Coordinates;
import org.eclipse.sensinact.gateway.geojson.GeoJsonObject;
import org.eclipse.sensinact.gateway.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link ValueMapper} that transforms EObject instances into SensiNact provider values
 * using ProviderMapping configurations.
 * 
 * @author Mark Hoffmann
 * @since 31.07.2025
 */
public class ValueMapperImpl implements ValueMapper {

	private static final Logger logger = LoggerFactory.getLogger(ValueMapperImpl.class);

	private final SensinactDigitalTwin twin;
	private final ProviderMapping mapping;
	private final String providerModel;

	/**
	 * Creates a new ValueMapper instance with custom function registry.
	 *
	 * @param twin The SensiNact digital twin to update with mapped values
	 * @param mapping The ProviderMapping configuration defining the transformation rules
	 * @param functionRegistry Registry of function services keyed by functionId
	 */
	public ValueMapperImpl(SensinactDigitalTwin twin, ProviderMapping mapping) {
		this.twin = requireNonNull(twin, "Digital twin cannot be null");
		this.mapping = requireNonNull(mapping, "Provider mapping cannot be null");
		this.providerModel = determineProviderModel(mapping);
	}

	@Override
	public void mapInstance(EObject sourceInstance) throws ValueMappingException {
		requireNonNull(sourceInstance, "Source instance cannot be null");

		ValidationResult validation = validateInstance(sourceInstance);
		if (!validation.isValid()) {
			throw new ValueMappingException("Instance validation failed: " + validation.getErrors());
		}

		// Determine provider-level timestamp once
		Instant providerTimestamp = determineTimestamp(sourceInstance, null);
		String providerId = determineProviderId(sourceInstance);
		try {
			SensinactProvider provider = twin.getProvider(providerModel, providerId);
			if (provider == null) {
				provider = twin.createProvider(providerModel, providerId, providerTimestamp);
			}


			for (ServiceMapping serviceMapping : mapping.getServices()) {
				// Determine the source object for this service (may be from a collection)
				EObject serviceSource = getServiceSource(sourceInstance, serviceMapping);

				// Service timestamp takes precedence, fallback to provider timestamp
				Instant serviceTimestamp = determineTimestamp(serviceSource, serviceMapping, providerTimestamp);
				mapServiceResources(serviceSource, serviceMapping, provider, serviceTimestamp);
			}

			// Map admin service if configured
			if (mapping.getAdmin() != null) {
				// Determine the source object for admin service (may be from a collection)
				EObject adminSource = getServiceSource(sourceInstance, mapping.getAdmin());

				// Admin timestamp takes precedence, fallback to provider timestamp
				Instant adminTimestamp = determineTimestamp(adminSource, mapping.getAdmin(), providerTimestamp);
				mapAdminService(adminSource, mapping.getAdmin(), provider, adminTimestamp);
			}

			logger.debug("Successfully mapped instance {} to provider {}",
					sourceInstance.eClass().getName(), providerModel);

		} catch (Exception e) {
			throw new ValueMappingException("Failed to map instance to provider " + providerModel, e);
		}
	}

	@Override
	public Map<String, Object> mapResourceValues(EObject sourceInstance) {
		requireNonNull(sourceInstance, "Source instance cannot be null");

		Map<String, Object> values = new LinkedHashMap<>();

		for (ServiceMapping serviceMapping : mapping.getServices()) {
			try {
				// Determine the source object for this service (may be from a collection)
				EObject serviceSource = getServiceSource(sourceInstance, serviceMapping);

				// Add auto-generated resources from ReferenceMapping if configured
				if (serviceMapping.getReferencedResource() != null) {
					try {
						ReferenceMapping refMapping = serviceMapping.getReferencedResource();
						EObject referencedObject = getReferencedSource(sourceInstance, refMapping,
								"service: " + serviceMapping.getMid());

						List<EStructuralFeature> attributesToMap = getFilteredAttributes(referencedObject.eClass(), refMapping);

						for (EStructuralFeature attribute : attributesToMap) {
							String resourcePath = serviceMapping.getMid() + "." + attribute.getName();
							try {
								Object value = referencedObject.eGet(attribute);
								if (value != null) {
									values.put(resourcePath, value);
								}
							} catch (Exception e) {
								logger.warn("Failed to extract auto-generated value for resource path {}: {}",
										resourcePath, e.getMessage());
							}
						}
					} catch (ValueMappingException e) {
						logger.warn("Failed to get referenced source for {}: {}", serviceMapping.getMid(), e.getMessage());
					}
				}

				// Add explicitly defined resources
				for (ResourceMapping resourceMapping : serviceMapping.getResources()) {
					String resourcePath = serviceMapping.getMid() + "." + resourceMapping.getMid();

					try {
						Optional<Object> value = extractValue(serviceSource, resourceMapping);
						if (value.isPresent()) {
							values.put(resourcePath, value.get());
						}
					} catch (Exception e) {
						logger.warn("Failed to extract value for resource path {}: {}", resourcePath, e.getMessage());
					}
				}
			} catch (ValueMappingException e) {
				logger.warn("Failed to get service source for {}: {}", serviceMapping.getMid(), e.getMessage());
			}
		}

		return values;
	}

	@Override
	public ValidationResult validateInstance(EObject sourceInstance) {
		requireNonNull(sourceInstance, "Source instance cannot be null");

		ValidationResultImpl result = new ValidationResultImpl();

		// Check if instance type matches any of the expected provider classes
		EClass instanceClass = sourceInstance.eClass();
		boolean typeMatches = mapping.getProviderClasses().stream()
				.anyMatch(providerClass -> providerClass.isSuperTypeOf(instanceClass) ||
						providerClass.equals(instanceClass));

		if (!typeMatches) {
			result.addError("Instance type " + instanceClass.getName() +
					" does not match any expected provider classes");
		}

		// Validate feature paths for all resources
		for (ServiceMapping serviceMapping : mapping.getServices()) {
			// Validate ReferenceMapping if present (preferred approach)
			if (serviceMapping.getReferencedResource() != null) {
				validateReferenceMapping(sourceInstance, serviceMapping.getReferencedResource(),
						"service: " + serviceMapping.getMid(), result);
			}
//			// Validate legacy collection configuration if present
//			else if (serviceMapping.getCollectionFeature() != null) {
//				validateCollectionFeature(sourceInstance, serviceMapping, result);
//			}

			// Get the service source for validation (may be from a collection)
			EObject serviceSource;
			try {
				serviceSource = getServiceSource(sourceInstance, serviceMapping);
			} catch (ValueMappingException e) {
				result.addError("Failed to get service source for " + serviceMapping.getMid() + ": " + e.getMessage());
				continue; // Skip validating resources if we can't get the source
			}

			for (ResourceMapping resourceMapping : serviceMapping.getResources()) {
				validateResourceMapping(serviceSource, resourceMapping, serviceMapping.getMid(), result);
			}
		}

		return result;
	}

	/**
	 * Validates a ReferenceMapping configuration.
	 */
	private void validateReferenceMapping(EObject sourceInstance,
			ReferenceMapping referenceMapping,
			String context, ValidationResultImpl result) {

		List<EStructuralFeature> featurePath = referenceMapping.getFeaturePath();

		// Check that feature path is not empty
		if (featurePath == null || featurePath.isEmpty()) {
			result.addError(String.format("ReferenceMapping has no feature path (%s)", context));
			return;
		}

		// Try to navigate the feature path
		try {
			Object targetValue = navigateFeaturePath(sourceInstance, featurePath, context);

			// If target is a collection, validate index bounds
			if (targetValue instanceof List<?>) {
				List<?> collection = (List<?>) targetValue;
				int index = referenceMapping.getCollectionIndex();

				if (collection.isEmpty()) {
					result.addWarning(String.format("Referenced collection is empty (%s)", context));
				} else if (index < 0 || index >= collection.size()) {
					result.addError(String.format("Collection index %d out of bounds for collection of size %d (%s)",
						index, collection.size(), context));
				} else {
					Object element = collection.get(index);
					if (!(element instanceof EObject)) {
						result.addError(String.format("Collection element at index %d is not an EObject (%s)",
							index, context));
					}
				}
			} else if (!(targetValue instanceof EObject)) {
				result.addError(String.format("ReferenceMapping target is not an EObject (%s)", context));
			}

		} catch (ValueMappingException e) {
			result.addError(String.format("Failed to navigate ReferenceMapping (%s): %s", context, e.getMessage()));
		}
	}

	/**
	 * Validates that a collection feature is properly configured and accessible.
	 * This is the legacy validation approach for backward compatibility.
	 */
//	private void validateCollectionFeature(EObject sourceInstance, ServiceMapping serviceMapping,
//			ValidationResultImpl result) {
//		EStructuralFeature collectionFeature = serviceMapping.getCollectionFeature();
//
//		// Check that the feature exists on the source instance
//		if (!sourceInstance.eClass().getEAllStructuralFeatures().contains(collectionFeature)) {
//			result.addError(String.format("Collection feature '%s' not found on instance type %s (service: %s)",
//				collectionFeature.getName(), sourceInstance.eClass().getName(), serviceMapping.getMid()));
//			return;
//		}
//
//		// Try to access the collection
//		try {
//			Object collectionValue = sourceInstance.eGet(collectionFeature);
//
//			if (!(collectionValue instanceof List<?>)) {
//				result.addError(String.format("Collection feature '%s' is not a list (service: %s)",
//					collectionFeature.getName(), serviceMapping.getMid()));
//				return;
//			}
//
//			List<?> collection = (List<?>) collectionValue;
//			int index = serviceMapping.getCollectionIndex();
//
//			if (collection.isEmpty()) {
//				result.addWarning(String.format("Collection feature '%s' is empty (service: %s)",
//					collectionFeature.getName(), serviceMapping.getMid()));
//			} else if (index < 0 || index >= collection.size()) {
//				result.addError(String.format("Collection index %d out of bounds for collection '%s' of size %d (service: %s)",
//					index, collectionFeature.getName(), collection.size(), serviceMapping.getMid()));
//			} else {
//				Object element = collection.get(index);
//				if (!(element instanceof EObject)) {
//					result.addError(String.format("Collection element at index %d is not an EObject (service: %s)",
//						index, serviceMapping.getMid()));
//				}
//			}
//		} catch (Exception e) {
//			result.addError(String.format("Failed to access collection feature '%s' (service: %s): %s",
//				collectionFeature.getName(), serviceMapping.getMid(), e.getMessage()));
//		}
//	}

	/**
	 * Extracts a value from an EObject instance using a ResourceMapping configuration.
	 */
	private Optional<Object> extractValue(EObject sourceInstance, ResourceMapping resourceMapping) {
		List<EStructuralFeature> featurePath = resourceMapping.getValueFeature();
		Optional<Object> rawValue = getRawValue(sourceInstance, featurePath);

		if (!rawValue.isPresent()) {
			return Optional.empty();
		}

		// Convert the raw value using the resource's type information
		EDataType targetType = resourceMapping.getEAttributeType();
		if (targetType != null) {
			try {
				Object convertedValue = convertValue(rawValue.get(), targetType);
				return Optional.of(convertedValue);
			} catch (Exception e) {
				logger.warn("Failed to convert value {} to type {}: {}", 
						rawValue.get(), targetType.getName(), e.getMessage());
				return Optional.empty();
			}
		}

		return rawValue;
	}

	/**
	 * Maps all resources for a specific service.
	 * Handles both explicitly defined resources and automatically generated resources from ReferenceMapping.
	 *
	 * Note: Auto-generated ResourceMappings from ReferenceMapping are created during model registration
	 * by ProviderModelSensinactMapper and are already present in serviceMapping.getResources().
	 */
	private void mapServiceResources(EObject sourceInstance, ServiceMapping serviceMapping,
			SensinactProvider provider, Instant timestamp) throws ValueMappingException {

		SensinactService service = provider.getServices().get(serviceMapping.getMid());
		if (service == null) {
			throw new ValueMappingException("Service not found: " + serviceMapping.getMid());
		}

		// Map all resources (both auto-generated and explicitly defined)
		// Auto-generated resources are already in the list thanks to ProviderModelSensinactMapper
		for (ResourceMapping resourceMapping : serviceMapping.getResources()) {
			mapSingleResource(sourceInstance, resourceMapping, service, timestamp);
		}
//		Go over the temporary ResourceMapping. These are the ones automatically created when the referencedResource is set
		for (ResourceMapping resourceMapping : serviceMapping.getTemporaryResources()) {
			mapSingleResource(sourceInstance, resourceMapping, service, timestamp);
		}
	}


	

	
	/**
	 * Returns the list of attributes to map based on ReferenceMapping filter configuration.
	 *
	 * @param eClass The EClass of the referenced object
	 * @param refMapping The ReferenceMapping with filter configuration
	 * @return List of attributes that should be mapped
	 */
	private List<EStructuralFeature> getFilteredAttributes(EClass eClass,
			ReferenceMapping refMapping) {

		// Get all attributes from the EClass
		List<EAttribute> allAttributes = eClass.getEAllAttributes();
		List<EAttribute> filterList = refMapping.getFilter();
		boolean exclude = refMapping.isExclude();

		// If no filter is specified
		if (filterList == null || filterList.isEmpty()) {
			// exclude=true (default): include all attributes
			// exclude=false: include nothing (no attributes specified)
			return exclude ? new java.util.ArrayList<>(allAttributes) : Collections.emptyList();
		}

		List<EStructuralFeature> result = new java.util.ArrayList<>();

		if (exclude) {
			// exclude=true: include all attributes EXCEPT those in filter
			for (EStructuralFeature attr : allAttributes) {
				if (!filterList.contains(attr)) {
					result.add(attr);
				}
			}
		} else {
			// exclude=false: include ONLY those attributes in filter
			for (EStructuralFeature attr : filterList) {
				if (allAttributes.contains(attr)) {
					result.add(attr);
				}
			}
		}

		return result;
	}



	

	/**
	 * Maps a single resource value.
	 */
	private void mapSingleResource(EObject sourceInstance, ResourceMapping resourceMapping, 
			SensinactService service, Instant timestamp) throws ValueMappingException {

		SensinactResource resource = service.getResources().get(resourceMapping.getMid());
		
		
		if (resource == null) {
			throw new ValueMappingException("Resource not found: " + resourceMapping.getMid());
		}

		Optional<Object> value = extractValue(sourceInstance, resourceMapping);
		if (value.isPresent()) {
			try {
				resource.setValue(value.get(), timestamp);
				logger.trace("Set resource {}.{} = {}", service.getName(), resource.getName(), value.get());
			} catch (Exception e) {
				throw new ValueMappingException("Failed to set resource value for " + 
						service.getName() + "." + resource.getName(), e);
			}
		} else {
			logger.debug("No value extracted for resource {}.{}", service.getName(), resource.getName());
		}
	}


	/**
	 * Maps admin service values.
	 */
	private void mapAdminService(EObject sourceInstance, AdminMapping adminMapping, 
			SensinactProvider provider, Instant timestamp) throws ValueMappingException {

		SensinactService adminService = provider.getServices().get("admin");
		if (adminService == null) {
			throw new ValueMappingException("Admin service not found");
		}
		mapFriendlyName(sourceInstance, adminMapping, timestamp, adminService);
		mapLocation(sourceInstance, adminMapping, timestamp, adminService);
	}

	/**
	 * @param sourceInstance
	 * @param adminMapping
	 * @param timestamp
	 * @param adminService
	 * @throws ValueMappingException
	 */
	private void mapFriendlyName(EObject sourceInstance, AdminMapping adminMapping, Instant timestamp,
			SensinactService adminService) throws ValueMappingException {
		String friendlyName = adminMapping.getFriendlyName();
		SensinactResource friendlyNameResource = adminService.getResources().get("friendlyName");
		requireNonNull(friendlyNameResource, "No friendly name resource available in admin service");
		// Map friendlyName resource if configured
		if (!adminMapping.getFriendlyNameFeature().isEmpty()) {
			List<EStructuralFeature> featurePath = adminMapping.getFriendlyNameFeature();
			Optional<Object> friendlyNameValue = getRawValue(sourceInstance, featurePath);
			if (friendlyNameValue.isPresent()) {
				friendlyName = (String) friendlyNameValue.get();
			} else {
				logger.debug("No value extracted for admin.friendlyName");
			}
		}
		if (Objects.nonNull(friendlyName)) {
			try {
				friendlyNameResource.setValue(friendlyName, timestamp);
				logger.trace("Set admin.friendlyName = {}", adminMapping.getFriendlyName());
			} catch (Exception e) {
				logger.warn("Failed to set admin friendlyName: {}", e.getMessage());
			}
		}
	}

	/**
	 * @param sourceInstance
	 * @param adminMapping
	 * @param timestamp
	 * @param adminService
	 * @throws ValueMappingException
	 */
	private void mapLocation(EObject sourceInstance, AdminMapping adminMapping, Instant timestamp,
			SensinactService adminService) throws ValueMappingException {
		// Map friendlyName resource if configured
		GeoJsonObject location = lookupLocation(adminMapping);
		SensinactResource locationResource = adminService.getResources().get("location");
		requireNonNull(locationResource, "No location resource available in admin service");
		if (!adminMapping.getLatitudeRef().isEmpty() && 
				!adminMapping.getLongitudeRef().isEmpty()) {
			List<EStructuralFeature> latFeaturePath = adminMapping.getLatitudeRef();
			List<EStructuralFeature> lonFeaturePath = adminMapping.getLongitudeRef();
			Optional<Object> latValue = getRawValue(sourceInstance, latFeaturePath);
			Optional<Object> lonValue = getRawValue(sourceInstance, lonFeaturePath);

			if (latValue.isPresent() && lonValue.isPresent()) {
				try {
					Coordinates coordinates = new Coordinates(toDouble(lonValue.get()), toDouble(latValue.get()));
					final Point point = new Point(coordinates, Collections.emptyList(), Collections.emptyMap());
					location = point;
					logger.trace("Set admin.location = {}", point);
					return;
				} catch (Exception e) {
					throw new ValueMappingException("Failed to set admin location resource", e);
				}
			} else {
				logger.debug("No value extracted for admin.location");
			}
		}
		if (locationResource != null) {
			try {
				locationResource.setValue(location, timestamp);
				logger.trace("Set admin.location = {}", location);
			} catch (Exception e) {
				logger.warn("Failed to set admin location: {}", e.getMessage());
			}
		}
	}

	private String determineProviderId(EObject sourceInstance) throws ValueMappingException {
		NameMapping nameMapping = mapping.getName();
		return getNameMapping(nameMapping, sourceInstance);
	}

	/**
	 * @param nameMapping
	 * @param sourceInstance
	 * @return
	 * @throws ValueMappingException 
	 */
	private String getNameMapping(NameMapping nameMapping, EObject sourceInstance) throws ValueMappingException {
		if (nameMapping == null) {
			throw new ValueMappingException("A mapping is needed to determine a name from an instance");
		}

		// First try: Extract value using the name mapping's feature path and collection index
		List<EStructuralFeature> featurePath = nameMapping.getFeaturePath();
		if (featurePath != null && !featurePath.isEmpty()) {
			Optional<Object> rawValue = getRawValue(sourceInstance, nameMapping);
			if (rawValue.isPresent() && rawValue.get() != null) {
				return rawValue.get().toString();
			}
		}

		// Second try: Use the name property as fallback
		if (nameMapping.getName() != null) {
			return nameMapping.getName().toString();
		}

		// Final fallback: use mapping's MID + instance hash
		String fallback = mapping.getMid() + "-" + Integer.toHexString(sourceInstance.hashCode());
		logger.debug("Name mapping extraction failed, using fallback provider ID: {}", fallback);
		return fallback;
	}

	/**
	 * Determines the timestamp for provider-level mapping.
	 */
	private Instant determineTimestamp(EObject sourceInstance, ServiceMapping serviceMapping) {
		return determineTimestamp(sourceInstance, serviceMapping, null);
	}

	/**
	 * Determines the timestamp with proper precedence handling.
	 * 
	 * @param sourceInstance The source EObject instance
	 * @param serviceMapping The service mapping (null for provider-level)  
	 * @param providerTimestamp The provider timestamp to use as fallback (can be null)
	 * @return The determined timestamp
	 */
	private Instant determineTimestamp(EObject sourceInstance, ServiceMapping serviceMapping, Instant providerTimestamp) {
		// 1. Service timestamp takes precedence if available
		if (serviceMapping != null && serviceMapping.getTimestamp() != null) {
			Instant serviceTimestamp = extractTimestamp(sourceInstance, serviceMapping.getTimestamp());
			if (serviceTimestamp != null) {
				return serviceTimestamp;
			}
		}

		// 2. If no service timestamp but provider timestamp exists, use provider timestamp
		if (providerTimestamp != null) {
			return providerTimestamp;
		}

		// 3. Try provider-level timestamp if not already determined
		if (mapping.getTimestamp() != null) {
			Instant extractedProviderTimestamp = extractTimestamp(sourceInstance, mapping.getTimestamp());
			if (extractedProviderTimestamp != null) {
				return extractedProviderTimestamp;
			}
		}

		// 4. Final fallback: current time
		return Instant.now();
	}

	/**
	 * Extracts timestamp from source instance using TimestampMapping configuration.
	 * Similar to getNameMapping logic - first try feature path, then static timestamp value.
	 */
	private Instant extractTimestamp(EObject sourceInstance, TimestampMapping timestampMapping) {
		if (timestampMapping == null) {
			return null;
		}

		// First try: Extract value using the timestamp mapping's feature path and collection index
		List<EStructuralFeature> featurePath = timestampMapping.getFeaturePath();
		if (featurePath != null && !featurePath.isEmpty()) {
			Optional<Object> rawValue = getRawValue(sourceInstance, timestampMapping);
			if (rawValue.isPresent() && rawValue.get() != null) {
				return convertToInstant(rawValue.get(), timestampMapping.getHint());
			}
		}

		// Second try: Use the static timestamp value
		if (timestampMapping.getTimestamp() != null) {
			return timestampMapping.getTimestamp();
		}

		return null;
	}

	/**
	 * Converts a raw timestamp value to Instant, using optional hint for format patterns.
	 */
	private Instant convertToInstant(Object rawValue, String hint) {
		if (rawValue instanceof Instant) {
			return (Instant) rawValue;
		}
		if (rawValue instanceof Date) {
			return ((Date) rawValue).toInstant();
		}

		if (rawValue instanceof Long) {
			// Assume milliseconds since epoch
			return Instant.ofEpochMilli((Long) rawValue);
		}

		if (rawValue instanceof String) {
			String stringValue = (String) rawValue;
			boolean useHint = nonNull(hint) && !hint.trim().isEmpty();
			try {
				// If hint is provided, use it as DateTimeFormatter pattern first
				if (useHint) {
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(hint.trim());
					LocalDateTime localDateTime = LocalDateTime.parse(stringValue, formatter);
					return localDateTime.toInstant(ZoneOffset.UTC);
				} else {
					// Try parsing as ISO instant
					return Instant.parse(stringValue);
				}
			} catch (Exception e) {
				// If hint parsing failed, try ISO parsing as fallback
				if (useHint) {
					try {
						return Instant.parse(stringValue);
					} catch (Exception isoException) {
						logger.warn("Failed to parse timestamp string '{}' using pattern '{}' and as ISO instant: {}", 
								stringValue, hint, e.getMessage());
					}
				} else {
					logger.warn("Failed to parse timestamp string '{}' as ISO instant: {}", 
							stringValue, e.getMessage());
				}
			}
		}

		logger.warn("Cannot convert timestamp value of type {} to Instant: {}", 
				rawValue.getClass().getName(), rawValue);
		return null;
	}

	/**
	 * Determines the provider ID based on the mapping strategy.
	 */
	private String determineProviderModel(ProviderMapping providerMapping) {
		MappingProfile profile = providerMapping.getProfile();
		if (profile != null && profile.getProviderStrategy() == ProviderStrategy.UNIFIED) {
			return profile.getProvider().getProviderId();
		} else {
			return providerMapping.getFqMid();
		}
	}

	/**
	 * Converts a raw value to the target EDataType.
	 */
	private Object convertValue(Object rawValue, EDataType targetType) {
		if (rawValue == null) {
			return null;
		}

		Class<?> targetClass = targetType.getInstanceClass();
		if (targetClass != null && targetClass.isAssignableFrom(rawValue.getClass())) {
			// No conversion needed
			return rawValue;
		}
		//If enumerator names and literals differ then we have a problem with ECoreUtil. 
		//We need to pass the enum name and not the literal.
		if(rawValue instanceof Enumerator eEnum) {
				rawValue = eEnum.getName();
		}
		// Use EMF's built-in conversion
		String stringValue = EcoreUtil.convertToString(targetType, rawValue);
		return EcoreUtil.createFromString(targetType, stringValue);
	}

	/**
	 * Validates a resource mapping against a source instance.
	 */
	private void validateResourceMapping(EObject sourceInstance, ResourceMapping resourceMapping, 
			String serviceName, ValidationResultImpl result) {
		List<EStructuralFeature> featurePath = resourceMapping.getValueFeature();
		if (featurePath == null || featurePath.isEmpty()) {
			result.addWarning("Empty feature path for resource " + serviceName + "." + resourceMapping.getMid());
			return;
		}

		// Try to navigate the path to check if it's valid
		try {
			Optional<Object> value = getRawValue(sourceInstance, featurePath);
			if (!value.isPresent()) {
				result.addWarning("Feature path returns no value for resource " + 
						serviceName + "." + resourceMapping.getMid());
			}
		} catch (Exception e) {
			result.addError("Invalid feature path for resource " + serviceName + "." + 
					resourceMapping.getMid() + ": " + e.getMessage());
		}
	}

	/**
	 * Implementation of ValidationResult.
	 */
	private static class ValidationResultImpl implements ValidationResult {
		private final List<String> errors = new ArrayList<>();
		private final List<String> warnings = new ArrayList<>();

		void addError(String error) {
			errors.add(error);
		}

		void addWarning(String warning) {
			warnings.add(warning);
		}

		@Override
		public boolean isValid() {
			return errors.isEmpty();
		}

		@Override
		public List<String> getErrors() {
			return new ArrayList<>(errors);
		}

		@Override
		public List<String> getWarnings() {
			return new ArrayList<>(warnings);
		}
	}

	/**
	 * Determines the source EObject for a specific service based on collection configuration.
	 * First checks if referencedResource (ReferenceMapping) is set - this is the preferred approach.
	 * Falls back to legacy collectionFeature approach for backward compatibility.
	 * If neither is configured, returns the root source object.
	 *
	 * @param rootSource The root source object
	 * @param serviceMapping The service mapping configuration
	 * @return The source EObject to use for this service
	 * @throws ValueMappingException If collection extraction fails
	 */
	private EObject getServiceSource(EObject rootSource, ServiceMapping serviceMapping)
			throws ValueMappingException {

		// New approach: Check if referencedResource is configured
		if (serviceMapping.getReferencedResource() != null) {
			return getReferencedSource(rootSource, serviceMapping.getReferencedResource(),
					"service: " + serviceMapping.getMid());
		}

		// No collection configuration: use root source directly
		return rootSource;
	}

//	/**
//	 * Legacy implementation for backward compatibility with collectionFeature approach.
//	 */
//	private EObject getServiceSourceLegacy(EObject rootSource, ServiceMapping serviceMapping)
//			throws ValueMappingException {
//
//		// Collection-based mapping: extract the specific element for this service
//		EStructuralFeature collectionFeature = serviceMapping.getCollectionFeature();
//		Object collectionValue = rootSource.eGet(collectionFeature);
//
//		// Ensure the feature is actually a collection
//		if (!(collectionValue instanceof List<?>)) {
//			throw new ValueMappingException(
//				String.format("Collection feature '%s' is not a list (service: %s)",
//					collectionFeature.getName(), serviceMapping.getMid()));
//		}
//
//		List<?> collection = (List<?>) collectionValue;
//
//		// Validate collection is not empty
//		if (collection.isEmpty()) {
//			throw new ValueMappingException(
//				String.format("Collection feature '%s' is empty (service: %s)",
//					collectionFeature.getName(), serviceMapping.getMid()));
//		}
//
//		// Get the index (default is 0)
//		int index = serviceMapping.getCollectionIndex();
//
//		// Check if collectionFilter is set (takes precedence over index)
//		if (serviceMapping.getCollectionFilter() != null &&
//			!serviceMapping.getCollectionFilter().trim().isEmpty()) {
//			// Future extension: evaluate filter expression
//			logger.warn("collectionFilter not yet implemented for service {}, using collectionIndex",
//				serviceMapping.getMid());
//		}
//
//		// Validate index bounds
//		if (index < 0 || index >= collection.size()) {
//			throw new ValueMappingException(
//				String.format("Collection index %d out of bounds for collection '%s' of size %d (service: %s)",
//					index, collectionFeature.getName(), collection.size(), serviceMapping.getMid()));
//		}
//
//		// Extract the element
//		Object element = collection.get(index);
//		if (!(element instanceof EObject)) {
//			throw new ValueMappingException(
//				String.format("Collection element at index %d is not an EObject (service: %s)",
//					index, serviceMapping.getMid()));
//		}
//
//		logger.debug("Service '{}' mapping from collection '{}' at index {}",
//			serviceMapping.getMid(), collectionFeature.getName(), index);
//
//		return (EObject) element;
//	}

	/**
	 * Navigates a ReferenceMapping to extract the target EObject.
	 * Handles both single-valued and multi-valued references, applying collection index/filter as needed.
	 *
	 * @param source The source EObject to start navigation from
	 * @param referenceMapping The ReferenceMapping configuration
	 * @param context Context string for error messages (e.g., "service: weather")
	 * @return The target EObject
	 * @throws ValueMappingException If navigation fails
	 */
	private EObject getReferencedSource(EObject source, ReferenceMapping referenceMapping,
			String context) throws ValueMappingException {

		List<EStructuralFeature> featurePath = referenceMapping.getFeaturePath();
		if (featurePath == null || featurePath.isEmpty()) {
			throw new ValueMappingException(
				String.format("ReferenceMapping has no feature path (%s)", context));
		}

		// Navigate to the target (might be a collection or single object)
		Object targetValue = navigateFeaturePath(source, featurePath, context);

		// If it's a collection, apply collection index/filter
		if (targetValue instanceof List<?>) {
			List<?> collection = (List<?>) targetValue;

			if (collection.isEmpty()) {
				throw new ValueMappingException(
					String.format("Referenced collection is empty (%s)", context));
			}

			// Check if collectionFilter is set (takes precedence over index)
			if (referenceMapping.getCollectionFilter() != null &&
				!referenceMapping.getCollectionFilter().trim().isEmpty()) {
				// Future extension: evaluate filter expression
				logger.warn("collectionFilter not yet implemented ({}), using collectionIndex", context);
			}

			int index = referenceMapping.getCollectionIndex();
			if (index < 0 || index >= collection.size()) {
				throw new ValueMappingException(
					String.format("Collection index %d out of bounds for collection of size %d (%s)",
						index, collection.size(), context));
			}

			targetValue = collection.get(index);
			logger.debug("ReferenceMapping: extracted element at index {} ({})", index, context);
		}

		if (!(targetValue instanceof EObject)) {
			throw new ValueMappingException(
				String.format("Referenced target is not an EObject (%s)", context));
		}

		return (EObject) targetValue;
	}

	/**
	 * Navigates a feature path from a source object to retrieve the target value.
	 * Does not handle collection indexing - that's done by the caller.
	 *
	 * @param source The source EObject
	 * @param featurePath The path of features to traverse
	 * @param context Context string for error messages
	 * @return The value at the end of the path
	 * @throws ValueMappingException If navigation fails
	 */
	private Object navigateFeaturePath(EObject source, List<EStructuralFeature> featurePath, String context)
			throws ValueMappingException {

		Object currentValue = source;
		try {
			for (int i = 0; i < featurePath.size(); i++) {
				EStructuralFeature feature = featurePath.get(i);

				if (!(currentValue instanceof EObject)) {
					throw new ValueMappingException(
						String.format("Cannot navigate feature '%s': current value is not an EObject (%s)",
							feature.getName(), context));
				}

				currentValue = ((EObject) currentValue).eGet(feature);

				if (currentValue == null) {
					throw new ValueMappingException(
						String.format("Feature '%s' returned null during path navigation (%s)",
							feature.getName(), context));
				}

				// For intermediate steps, if we encounter a list, we should not be here
				// (ReferenceMapping should only have collections as the final step)
				if (i < featurePath.size() - 1 && currentValue instanceof List<?>) {
					throw new ValueMappingException(
						String.format("Unexpected collection at intermediate step '%s' (%s)",
							feature.getName(), context));
				}
			}
			return currentValue;
		} catch (ValueMappingException e) {
			throw e;
		} catch (Exception e) {
			throw new ValueMappingException(
				String.format("Failed to navigate feature path (%s): %s", context, e.getMessage()), e);
		}
	}

	/**
	 * Traverses a path of {@link EStructuralFeature}s to retrieve a raw, untyped value.
	 * First checks if a functionId is specified and invokes the registered function.
	 * If no function is found or no functionId is specified, falls back to feature path traversal.
	 * Handles multi-value features by using the collectionIndex from the FeatureMapping.
	 *
	 * @param source         The starting object.
	 * @param featureMapping The feature mapping containing the path and collection index.
	 * @return An {@link Optional} containing the raw value, or empty if not found.
	 */
	private Optional<Object> getRawValue(EObject source, FeatureMapping featureMapping) {
		if (featureMapping == null) {
			return Optional.empty();
		}
		// First priority: Use feature path if available
		List<EStructuralFeature> featurePath = featureMapping.getFeaturePath();
		if (featurePath == null || featurePath.isEmpty()) {
			return Optional.empty();
		}

		int collectionIndex = featureMapping.getCollectionIndex(); // Default is 0

		Object currentValue = source;
		try {
			for (EStructuralFeature feature : featurePath) {
				if (currentValue instanceof EObject) {
					currentValue = ((EObject) currentValue).eGet(feature);

					// Handle multi-value features: use configured collection index
					if (currentValue instanceof EList<?>) {
						List<?> list = (List<?>) currentValue;
						if (list.isEmpty()) {
							return Optional.empty();
						}

						// Use the collectionIndex from FeatureMapping (default 0)
						if (collectionIndex < 0 || collectionIndex >= list.size()) {
							logger.warn("Collection index {} out of bounds for list of size {}, using index 0",
								collectionIndex, list.size());
							collectionIndex = 0;
						}

						currentValue = list.get(collectionIndex);
					}
				} else {
					// The path is invalid if a non-EObject is encountered mid-path.
					return Optional.empty();
				}
			}
			return Optional.ofNullable(currentValue);
		} catch (Exception e) {
			// An error during path traversal (e.g., feature not found)
			logger.error("Error getting raw value for feature path: {} from source {}: {}",
					featurePath, source.eClass().getName(), e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * Legacy method for backward compatibility - uses default collection index of 0.
	 *
	 * @deprecated Use {@link #getRawValue(EObject, FeatureMapping)} instead.
	 */
	@Deprecated
	private Optional<Object> getRawValue(EObject source, List<EStructuralFeature> featurePath) {
		// Create a temporary FeatureMapping-like object for backward compatibility
		// In practice, this should be replaced by proper FeatureMapping usage
		if (featurePath == null || featurePath.isEmpty()) {
			return Optional.empty();
		}

		Object currentValue = source;
		try {
			for (EStructuralFeature feature : featurePath) {
				if (currentValue instanceof EObject) {
					currentValue = ((EObject) currentValue).eGet(feature);

					// Handle multi-value features: take first element (backward compatible behavior)
					if (currentValue instanceof EList<?>) {
						List<?> list = (List<?>) currentValue;
						if (list.isEmpty()) {
							return Optional.empty();
						}
						currentValue = list.get(0);
					}
				} else {
					return Optional.empty();
				}
			}
			return Optional.ofNullable(currentValue);
		} catch (Exception e) {
			logger.error("Error getting raw value for feature path: {} from source {}: {}",
					featurePath, source.eClass().getName(), e.getMessage());
			return Optional.empty();
		}
	}

	/**
	 * Looks up for location data in the {@link AdminMapping} 
	 * @param adminMapping the {@link AdminMapping}
	 * @return the {@link GeoJsonObject} or <code>null</code>
	 */
	private GeoJsonObject lookupLocation(AdminMapping adminMapping) {
		requireNonNull(adminMapping);
		final Object latitude, longitude, altitude;
		latitude = adminMapping.getLatitude();
		if (isNull(latitude)) {
			// No latitude: no location
			return null;
		}

		longitude = adminMapping.getLongitude();
		if (isNull(longitude)) {
			// No longitude: no location
			return null;
		}

		// Altitude is optional
		altitude = adminMapping.getElevation();

		Coordinates coordinates = new Coordinates(toDouble(longitude), toDouble(latitude), toDouble(altitude));
		final Point point = new Point(coordinates, Collections.emptyList(), Collections.emptyMap());
		return point;
	}

	/**
	 * Tries to convert the given object to a double. Returns {@link Double#NaN} in
	 * case of an error.
	 *
	 * @param value Input value
	 * @return The double representation of the given object, or {@link Double#NaN}
	 *         in case of an error
	 */
	static Double toDouble(final Object value) {
		if (value == null) {
			return Double.NaN;
		}

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof CharSequence) {
			return Double.parseDouble(value.toString());
		}

		return Double.NaN;
	}
}
