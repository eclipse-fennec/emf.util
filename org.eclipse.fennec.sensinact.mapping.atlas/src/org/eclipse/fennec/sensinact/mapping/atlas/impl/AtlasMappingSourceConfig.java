/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping.atlas.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration of an {@link AtlasMappingSourceComponent} instance. The atlas scope to
 * read from is selected via the standard DS reference-target property
 * {@code atlasScope.target}, e.g. {@code (atlas.scope=iot)}.
 *
 * @since 07/2026
 */
@ObjectClassDefinition(name = "SensiNact Mapping - Model Atlas Source", //
		description = "Pulls ProviderMapping and MappingProfile instances from a Model Atlas scope "
				+ "(selected via the atlasScope.target property) and registers them as OSGi services "
				+ "for the sensinact mapping whiteboards.")
public @interface AtlasMappingSourceConfig {

	@AttributeDefinition(name = "Registries", description = "Atlas registry names to read mapping objects from.")
	String[] registries() default { "mappings" };

	@AttributeDefinition(name = "Object ids", required = false, description = "Explicit object ids to load; empty loads every object the registries list.")
	String[] object_ids() default {};

	@AttributeDefinition(name = "Stage", required = false, description = "Atlas stage to read from; empty reads the final stage.")
	String stage() default "";

	@AttributeDefinition(name = "Refresh interval (ms)", required = false, description = "Interval for re-fetching from the atlas and swapping changed mappings; 0 loads once on activation.")
	long refresh_interval_ms() default 0;

	@AttributeDefinition(name = "Retry interval (ms)", required = false, description = "Back-off before retrying while the initial load is incomplete; 0 disables retries.")
	long retry_interval_ms() default 30_000;
}
