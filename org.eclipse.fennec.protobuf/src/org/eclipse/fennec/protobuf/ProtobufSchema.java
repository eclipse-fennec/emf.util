/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.protobuf;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

/**
 * Protobuf schema derived from a single {@link EPackage}. The Ecore→Protobuf
 * descriptors are built by {@link DescriptorFactory}; this class owns the runtime
 * {@link FileDescriptor}, resolves per-{@link EClass} message descriptors, and
 * hands out a {@link #writer()} / {@link #reader()} for instance (de)serialization
 * plus a {@code .proto} {@linkplain #toProtoSource() source export}.
 * <p>
 * <b>Inheritance / polymorphism.</b> A message flattens all (inherited + own)
 * features of its {@link EClass}, so a subtype round-trips its full feature set.
 * A containment reference whose declared type is abstract or has subtypes is
 * emitted as an {@link #ANY_MESSAGE} wrapper carrying the actual type name plus
 * the object's own bytes; polymorphic non-containment references use an
 * {@link #REF_MESSAGE} wrapper carrying the actual type name plus the target URI.
 * Monomorphic references stay natural nested messages / plain URI strings.
 * <p>
 * <b>Scope.</b> One {@code EPackage} per schema; containment targets must be in
 * the same package. Reuse via {@link ProtobufSchemaCache}.
 *
 * @see ProtobufAnnotations for the field-number contract.
 */
public final class ProtobufSchema {

	/** Wrapper message for polymorphic containment: {@code {string eClass; bytes data;}}. */
	static final String ANY_MESSAGE = "EObjectAny";
	/** Wrapper message for polymorphic non-containment references: {@code {string eClass; string uri;}}. */
	static final String REF_MESSAGE = "EObjectRef";
	static final String FIELD_ECLASS = "eClass";
	static final String FIELD_DATA = "data";
	static final String FIELD_URI = "uri";

	private final EPackage ePackage;
	private final String protoPackage;
	private final FileDescriptorProto fileProto;
	private final FileDescriptor fileDescriptor;
	private final Map<EClass, Descriptor> messageByClass = new HashMap<>();
	private final ProtobufContext defaultContext;

	private ProtobufSchema(EPackage ePackage) {
		this.ePackage = ePackage;
		this.fileProto = new DescriptorFactory(ePackage).build();
		this.protoPackage = fileProto.getPackage();
		try {
			this.fileDescriptor = FileDescriptor.buildFrom(fileProto, new FileDescriptor[0]);
		} catch (DescriptorValidationException e) {
			throw new ProtobufException("Could not build protobuf descriptors for package " + protoPackage
					+ ": " + e.getMessage(), e);
		}
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (c instanceof EClass eClass) {
				messageByClass.put(eClass, fileDescriptor.findMessageTypeByName(eClass.getName()));
			}
		}
		// Package-level annotation provides the default payload configuration for the
		// no-argument writer()/reader(); an explicit ProtobufContext overrides it.
		this.defaultContext = ProtobufContext.defaults()
				.withTypeStrategy(ProtobufAnnotations.typeStrategy(ePackage, ProtobufTypeStrategy.DEFAULT))
				.withSmartCompression(ProtobufAnnotations.smartCompression(ePackage, true));
	}

	/** Derives a schema from an {@link EPackage}. The result is immutable; cache it per package. */
	public static ProtobufSchema forPackage(EPackage ePackage) {
		if (ePackage == null) {
			throw new IllegalArgumentException("ePackage must not be null");
		}
		return new ProtobufSchema(ePackage);
	}

	public EPackage ePackage() {
		return ePackage;
	}

	public FileDescriptor fileDescriptor() {
		return fileDescriptor;
	}

	FileDescriptorProto fileProto() {
		return fileProto;
	}

	/** The Protobuf message descriptor for an {@link EClass} of this package. */
	public Descriptor descriptorFor(EClass eClass) {
		if (eClass == null) {
			throw new IllegalArgumentException("eClass must not be null");
		}
		Descriptor d = messageByClass.get(eClass);
		if (d == null) {
			throw new ProtobufException("EClass " + eClass.getName() + " is not part of package " + protoPackage);
		}
		return d;
	}

	/** Resolves an {@link EClass} of this package by its (simple) name. */
	EClass eClassByName(String name) {
		EClassifier c = ePackage.getEClassifier(name);
		if (!(c instanceof EClass eClass)) {
			throw new ProtobufException("No EClass '" + name + "' in package " + protoPackage);
		}
		return eClass;
	}

	Descriptor anyDescriptor() {
		return fileDescriptor.findMessageTypeByName(ANY_MESSAGE);
	}

	Descriptor refDescriptor() {
		return fileDescriptor.findMessageTypeByName(REF_MESSAGE);
	}

	public ProtobufWriter writer() {
		return writer(defaultContext);
	}

	public ProtobufWriter writer(ProtobufContext context) {
		return new ProtobufWriter(this, context);
	}

	public ProtobufReader reader() {
		return reader(defaultContext);
	}

	public ProtobufReader reader(ProtobufContext context) {
		return new ProtobufReader(this, context);
	}

	/** Renders the derived descriptors as proto3 source text. */
	public String toProtoSource() {
		return ProtoSourceWriter.render(fileProto);
	}
}
