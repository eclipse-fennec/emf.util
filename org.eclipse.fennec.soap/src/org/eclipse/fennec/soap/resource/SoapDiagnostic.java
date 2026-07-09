/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.soap.resource;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * A {@link Resource.Diagnostic} recorded on a {@link SoapResource}'s error list when a load
 * or save fails — including an incoming SOAP {@code Fault} — so callers can inspect
 * {@code getErrors()} the standard EMF way in addition to catching the thrown
 * {@code IOException}.
 */
final class SoapDiagnostic implements Resource.Diagnostic {

	private final String message;
	private final String location;

	SoapDiagnostic(String message, String location) {
		this.message = message;
		this.location = location;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int getColumn() {
		return 0;
	}
}
