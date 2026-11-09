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
/**
 * The plain-Java engine of the generic Fennec mapping: the value ladder ({@link org.eclipse.fennec.mapping.ValueExtractor}),
 * identity resolution ({@link org.eclipse.fennec.mapping.IdentityResolver}), applying a mapping
 * ({@link org.eclipse.fennec.mapping.Mapper}), validation and the registry, plus the extension
 * points functions, converters and the expression language.
 */
@org.osgi.annotation.bundle.Export
@org.osgi.annotation.versioning.Version("0.1.0")
package org.eclipse.fennec.mapping;
