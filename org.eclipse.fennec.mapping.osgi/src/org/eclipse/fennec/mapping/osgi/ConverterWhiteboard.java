/********************************************************************
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
 ********************************************************************/
package org.eclipse.fennec.mapping.osgi;

import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.fennec.mapping.ConverterRegistry;
import org.eclipse.fennec.mapping.ValueConverter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * The {@link ConverterRegistry} of a framework: every {@link ValueConverter} service carrying the
 * {@value ValueConverter#CONVERTER_ID} property is registered under that id while it is there.
 * <p>
 * No configuration. Converters come and go at any time (dynamic, multiple); while a named
 * converter is missing, values pass unconverted.
 */
@Component(service = ConverterRegistry.class)
public class ConverterWhiteboard extends ConverterRegistry {

	private static final Logger LOGGER = Logger.getLogger(ConverterWhiteboard.class.getName());

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	void addConverter(ValueConverter converter, Map<String, Object> properties) {
		String id = Properties.text(properties, ValueConverter.CONVERTER_ID);
		if (id == null) {
			LOGGER.warning(() -> "Ignoring " + converter.getClass().getName() + " without the '"
					+ ValueConverter.CONVERTER_ID + "' property - no mapping can name it");
			return;
		}
		register(id, converter);
	}

	void removeConverter(ValueConverter converter, Map<String, Object> properties) {
		String id = Properties.text(properties, ValueConverter.CONVERTER_ID);
		if (id != null) {
			unregister(id, converter);
		}
	}
}
