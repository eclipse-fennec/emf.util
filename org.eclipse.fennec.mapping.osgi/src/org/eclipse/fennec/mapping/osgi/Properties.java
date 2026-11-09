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

/** Reading service properties. */
final class Properties {

	private Properties() {
	}

	/**
	 * @return the property as non-blank text, else {@code null}
	 */
	static String text(Map<String, Object> properties, String key) {
		Object value = properties == null ? null : properties.get(key);
		if (value == null) {
			return null;
		}
		String text = String.valueOf(value);
		return text.isBlank() ? null : text;
	}
}
