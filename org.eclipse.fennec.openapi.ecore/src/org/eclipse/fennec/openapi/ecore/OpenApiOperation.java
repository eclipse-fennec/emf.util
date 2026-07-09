/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.openapi.ecore;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.fennec.service.api.ServiceOperation;

/**
 * An OpenAPI path operation resolved against the imported Ecore. {@link #requestType()} is
 * either the JSON body {@link EClass} directly (body-only operations) or a <b>synthetic</b>
 * request EClass whose features carry an {@link OpenApiAnnotations#KEY_IN in} annotation
 * (path/query/header/cookie parameters + an optional {@code body} containment). The HTTP
 * binding ({@link #httpMethod()}, {@link #pathTemplate()}) is what a client needs to invoke it.
 *
 * @param name          operationId, or {@code <method>_<path>} when absent
 * @param httpMethod    upper-case HTTP method (GET, POST, …)
 * @param pathTemplate  the path as declared, incl. {@code {param}} placeholders
 * @param requestType   body EClass, synthetic request EClass, or {@code null}
 * @param responseType  the 2xx JSON response EClass (array: its item EClass), or {@code null}
 * @param responseMany  whether the 2xx response is a JSON array of {@link #responseType()}
 * @see OpenApiImporter
 */
public record OpenApiOperation(String name, String httpMethod, String pathTemplate,
		EClass requestType, EClass responseType, boolean responseMany) implements ServiceOperation {
}
