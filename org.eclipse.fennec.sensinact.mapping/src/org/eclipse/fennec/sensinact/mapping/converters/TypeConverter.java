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

import java.util.function.Function;

/**
 * A functional interface for converting an object of a source type {@code S} to a
 * target type {@code T}.
 *
 * @param <S> The source type
 * @param <T> The target type
 */
public interface TypeConverter<S, T> extends Function<S, T> {

    /**
     * Returns the source type class.
     * @return the source type
     */
    Class<S> getSourceType();

    /**
     * Returns the target type class.
     * @return the target type
     */
    Class<T> getTargetType();

    /**
     * Converts the source object to the target type.
     * @param source the source object to convert
     * @return the converted object
     */
    @Override
    T apply(S source);
}
