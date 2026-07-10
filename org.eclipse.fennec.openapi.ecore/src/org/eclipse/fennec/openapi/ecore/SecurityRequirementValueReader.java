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

import java.io.IOException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.fennec.codec.value.CodecReaderContext;
import org.eclipse.fennec.codec.value.ReferenceValueReader;
import org.eclipse.fennec.model.openapi.OpenApiFactory;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.SecurityRequirement;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

/**
 * Reads an OpenAPI security requirement object — {@code {"<scheme>": ["scope", …], …}} — into a
 * {@link SecurityRequirement}. The generic codec deserializer cannot map it: the JSON object's
 * field names are <b>dynamic scheme names</b>, while the model wraps them in a {@code schemes}
 * EMap feature, so the keys are silently dropped. This reader belongs upstream in the codec's
 * {@code OpenApiResourceFactoryImpl} registry (bound via {@code valueReaderName} annotations on
 * the {@code security} features); until that ships, {@link OpenApiImporter} registers it and
 * binds it through load options.
 */
class SecurityRequirementValueReader implements ReferenceValueReader<SecurityRequirement> {

	/** The name the {@code security} features are bound to via {@code valueReaderName}. */
	static final String NAME = "securityRequirement";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean canHandle(EReference reference) {
		return OpenApiPackage.Literals.SECURITY_REQUIREMENT.isSuperTypeOf(reference.getEReferenceType());
	}

	@Override
	public SecurityRequirement read(CodecReaderContext ctx, EReference reference) throws IOException {
		JsonParser parser = ctx.getParser();
		if (parser.currentToken() != JsonToken.START_OBJECT) {
			ctx.addWarning("Security requirement is not a JSON object: " + parser.currentToken());
			parser.skipChildren();
			return null;
		}
		SecurityRequirement requirement = OpenApiFactory.eINSTANCE.createSecurityRequirement();
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String scheme = parser.currentName();
			parser.nextToken();
			EList<String> scopes = new BasicEList<>();
			if (parser.currentToken() == JsonToken.START_ARRAY) {
				while (parser.nextToken() != JsonToken.END_ARRAY) {
					scopes.add(parser.getString());
				}
			} else {
				ctx.addWarning("Scopes of security scheme '" + scheme + "' are not a JSON array");
				parser.skipChildren();
			}
			requirement.getSchemes().put(scheme, scopes);
		}
		return requirement;
	}
}
