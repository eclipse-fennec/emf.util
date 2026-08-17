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
package org.eclipse.fennec.sensinact.mapping.converters;

import java.time.Instant;
import java.util.Date;

/**
 * A concrete {@link TypeConverter} for converting {@link Date} to {@link Instant}.
 */
public class DateToInstantConverter implements TypeConverter<Date, Instant> {

    @Override
    public Class<Date> getSourceType() {
        return Date.class;
    }

    @Override
    public Class<Instant> getTargetType() {
        return Instant.class;
    }

    @Override
    public Instant apply(Date source) {
        if (source == null) {
            return null;
        }
        return source.toInstant();
    }
}
