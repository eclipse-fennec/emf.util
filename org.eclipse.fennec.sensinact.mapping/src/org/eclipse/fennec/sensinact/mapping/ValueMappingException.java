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
package org.eclipse.fennec.sensinact.mapping;

/**
 * Exception thrown when value mapping from EObject instances to SensiNact resources fails.
 * This can occur due to invalid feature paths, type conversion errors, missing values,
 * or other mapping-related issues.
 * 
 * @author Mark Hoffmann
 * @since 31.07.2025
 */
public class ValueMappingException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new ValueMappingException with the specified message.
     * 
     * @param message the detail message
     */
    public ValueMappingException(String message) {
        super(message);
    }

    /**
     * Creates a new ValueMappingException with the specified message and cause.
     * 
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ValueMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new ValueMappingException with the specified cause.
     * 
     * @param cause the cause of this exception
     */
    public ValueMappingException(Throwable cause) {
        super(cause);
    }
}
