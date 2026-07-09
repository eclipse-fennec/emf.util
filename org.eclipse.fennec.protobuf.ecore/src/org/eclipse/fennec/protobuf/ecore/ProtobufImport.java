/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf.ecore;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * The result of importing a {@code FileDescriptorSet}: the derived {@link EPackage}s (one per
 * proto package) plus the {@link GrpcService}s extracted from the descriptor's {@code service}
 * definitions. The services reference the {@link org.eclipse.emf.ecore.EClass}es of the packages,
 * so both belong together.
 *
 * @see ProtobufImporter#importFrom(byte[])
 */
public record ProtobufImport(List<EPackage> packages, List<GrpcService> services) {

	public ProtobufImport {
		packages = List.copyOf(packages);
		services = List.copyOf(services);
	}

	/** The imported {@link EPackage} for a namespace URI, or {@code null}. */
	public EPackage packageForNsURI(String nsURI) {
		for (EPackage p : packages) {
			if (p.getNsURI() != null && p.getNsURI().equals(nsURI)) {
				return p;
			}
		}
		return null;
	}

	/** The service with the given simple name, or {@code null}. */
	public GrpcService service(String name) {
		for (GrpcService s : services) {
			if (s.name().equals(name)) {
				return s;
			}
		}
		return null;
	}
}
