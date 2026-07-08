/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * (De)serialization configuration + resolution context shared by a
 * {@link ProtobufWriter}/{@link ProtobufReader} across package boundaries.
 * <p>
 * Holds the type-discriminator {@linkplain ProtobufTypeStrategy strategy} and
 * {@code smartCompression} flag (payload knobs, mirroring the Fennec Codec), the
 * {@link ProtobufSchemaCache} used to reach other packages' schemas, the
 * {@link EPackage.Registry} used to resolve cross-package type discriminators, and
 * the {@link Resource} being (de)serialized (for resource-relative references).
 * Defaults: {@code NAME} strategy, {@code smartCompression} on, global package
 * registry.
 */
public final class ProtobufContext {

	private ProtobufTypeStrategy typeStrategy = ProtobufTypeStrategy.DEFAULT;
	private boolean smartCompression = true;
	private ProtobufSchemaCache schemaCache = new ProtobufSchemaCache();
	private EPackage.Registry packageRegistry = EPackage.Registry.INSTANCE;
	private Resource contextResource;

	public static ProtobufContext defaults() {
		return new ProtobufContext();
	}

	public ProtobufContext withTypeStrategy(ProtobufTypeStrategy strategy) {
		this.typeStrategy = strategy == null ? ProtobufTypeStrategy.DEFAULT : strategy;
		return this;
	}

	public ProtobufContext withSmartCompression(boolean smartCompression) {
		this.smartCompression = smartCompression;
		return this;
	}

	public ProtobufContext withSchemaCache(ProtobufSchemaCache schemaCache) {
		if (schemaCache != null) {
			this.schemaCache = schemaCache;
		}
		return this;
	}

	public ProtobufContext withPackageRegistry(EPackage.Registry registry) {
		if (registry != null) {
			this.packageRegistry = registry;
		}
		return this;
	}

	public ProtobufContext withContextResource(Resource resource) {
		this.contextResource = resource;
		return this;
	}

	public ProtobufTypeStrategy typeStrategy() {
		return typeStrategy;
	}

	public boolean smartCompression() {
		return smartCompression;
	}

	public Resource contextResource() {
		return contextResource;
	}

	/** The (memoized) schema for a package — used to reach cross-package types. */
	ProtobufSchema schemaFor(EPackage ePackage) {
		return schemaCache.get(ePackage);
	}

	/** Encodes the concrete type of a wrapped object relative to the declared reference type. */
	String discriminator(EClass actual, EClass declared) {
		return ProtobufType.encode(actual, declared, typeStrategy, smartCompression);
	}

	/** Resolves a type discriminator back to a concrete {@link EClass}. */
	EClass resolveType(String discriminator, EClass declared) {
		return ProtobufType.decode(discriminator, declared, packageRegistry);
	}
}
