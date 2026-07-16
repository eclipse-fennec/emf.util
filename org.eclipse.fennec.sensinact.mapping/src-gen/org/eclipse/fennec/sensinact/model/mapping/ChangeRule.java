/*
 * Copyright (c) 2012 - 2025 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *      Mark Hoffmann - initial API and implementation
 */
package org.eclipse.fennec.sensinact.model.mapping;

import org.osgi.annotation.versioning.ProviderType;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Change Rule</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Decides whether a resource value change is forwarded to the history provider.
 * Each concrete subtype carries exactly the parameter it needs; comparisons are made against
 * the last stored value, and the first value for a resource is always stored.
 * <!-- end-model-doc -->
 *
 *
 * @see org.eclipse.fennec.sensinact.model.mapping.MappingPackage#getChangeRule()
 * @model abstract="true"
 * @generated
 */
@ProviderType
public interface ChangeRule extends PersistenceRule {
} // ChangeRule
