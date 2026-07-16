/**
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * * Contributors:
 * Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.sensinact.mapping.converters;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A registry for managing and looking up {@link TypeConverter}s.
 * It uses a source/target class pair to find the appropriate converter.
 */
public class TypeConverterRegistry {

    // A record used as a key in the registry map for a unique source/target pair.
    private record ConversionPair(Class<?> source, Class<?> target) {}

    private final Map<ConversionPair, TypeConverter<?, ?>> converters = new ConcurrentHashMap<>();

    /**
     * Registers a new type converter.
     * @param converter The converter to register.
     */
    public void register(TypeConverter<?, ?> converter) {
        converters.put(new ConversionPair(converter.getSourceType(), converter.getTargetType()), converter);
    }

    /**
     * Finds a converter that can handle the conversion from the given source type to the target type.
     * @param <S> The source type
     * @param <T> The target type
     * @param sourceType The source class
     * @param targetType The target class
     * @return An {@link Optional} containing the found {@link TypeConverter}, or empty if none is registered.
     */
    @SuppressWarnings("unchecked")
    public <S, T> Optional<TypeConverter<S, T>> find(Class<S> sourceType, Class<T> targetType) {
        // In a more advanced implementation, this could also check for converters
        // for super-classes of the sourceType.
        TypeConverter<?, ?> converter = converters.get(new ConversionPair(sourceType, targetType));
        return Optional.ofNullable((TypeConverter<S, T>) converter);
    }
}
