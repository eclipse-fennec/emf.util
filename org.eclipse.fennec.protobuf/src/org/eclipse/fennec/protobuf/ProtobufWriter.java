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

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;

/**
 * Serializes EMF objects to Protobuf wire bytes using the descriptors of a
 * {@link ProtobufSchema}.
 * <p>
 * The root object is serialized using its own {@code eClass} descriptor. A
 * contained object whose containment reference is monomorphic is serialized with
 * that reference's declared-type descriptor; a polymorphic containment is wrapped
 * in an {@link ProtobufSchema#ANY_MESSAGE} carrying the actual type name and the
 * object's own bytes, so subtypes (including inherited + own features) round-trip.
 * Only features for which {@code eIsSet} is {@code true} are written, mirroring
 * EMF's unset-vs-default distinction.
 */
public final class ProtobufWriter {

	private final ProtobufSchema schema;

	ProtobufWriter(ProtobufSchema schema) {
		this.schema = schema;
	}

	/** Serializes {@code object} to a fresh byte array. */
	public byte[] toBytes(EObject object) {
		return build(object).toByteArray();
	}

	/** Serializes {@code object} to a stream. */
	public void write(EObject object, OutputStream out) throws IOException {
		build(object).writeTo(out);
	}

	/** Serializes {@code object} to a {@link DynamicMessage} using its own class descriptor. */
	public DynamicMessage build(EObject object) {
		if (object == null) {
			throw new IllegalArgumentException("object must not be null");
		}
		return build(object, schema.descriptorFor(object.eClass()));
	}

	private DynamicMessage build(EObject object, Descriptor descriptor) {
		DynamicMessage.Builder builder = DynamicMessage.newBuilder(descriptor);
		for (EStructuralFeature f : object.eClass().getEAllStructuralFeatures()) {
			if (!f.isChangeable() || !object.eIsSet(f)) {
				continue;
			}
			FieldDescriptor fd = descriptor.findFieldByName(f.getName());
			if (fd == null) {
				continue; // feature not present in the declared message
			}
			Object value = object.eGet(f);
			if (value == null) {
				continue;
			}
			if (f instanceof EReference ref) {
				writeReference(builder, fd, ref, value);
			} else {
				writeAttribute(builder, fd, (EAttribute) f, value);
			}
		}
		return builder.build();
	}

	private void writeReference(DynamicMessage.Builder builder, FieldDescriptor fd, EReference ref, Object value) {
		boolean message = fd.getType() == FieldDescriptor.Type.MESSAGE;
		if (ref.isContainment()) {
			boolean any = message && ProtobufSchema.ANY_MESSAGE.equals(fd.getMessageType().getName());
			if (ref.isMany()) {
				for (Object child : (List<?>) value) {
					builder.addRepeatedField(fd, any ? any((EObject) child) : build((EObject) child, fd.getMessageType()));
				}
			} else {
				builder.setField(fd, any ? any((EObject) value) : build((EObject) value, fd.getMessageType()));
			}
		} else if (ref.isMany()) {
			for (Object target : (List<?>) value) {
				builder.addRepeatedField(fd, message ? ref((EObject) target) : reference((EObject) target));
			}
		} else {
			builder.setField(fd, message ? ref((EObject) value) : reference((EObject) value));
		}
	}

	private void writeAttribute(DynamicMessage.Builder builder, FieldDescriptor fd, EAttribute attr, Object value) {
		if (attr.isMany()) {
			for (Object item : (List<?>) value) {
				builder.addRepeatedField(fd, TypeMapper.toProto(fd, attr, item));
			}
		} else {
			builder.setField(fd, TypeMapper.toProto(fd, attr, value));
		}
	}

	/** Wraps a polymorphic contained object as {@code EObjectAny{eClass, data}}. */
	private DynamicMessage any(EObject child) {
		Descriptor any = schema.anyDescriptor();
		byte[] data = build(child, schema.descriptorFor(child.eClass())).toByteArray();
		return DynamicMessage.newBuilder(any)
				.setField(any.findFieldByName(ProtobufSchema.FIELD_ECLASS), child.eClass().getName())
				.setField(any.findFieldByName(ProtobufSchema.FIELD_DATA), ByteString.copyFrom(data))
				.build();
	}

	/** Wraps a polymorphic non-containment target as {@code EObjectRef{eClass, uri}}. */
	private DynamicMessage ref(EObject target) {
		Descriptor ref = schema.refDescriptor();
		return DynamicMessage.newBuilder(ref)
				.setField(ref.findFieldByName(ProtobufSchema.FIELD_ECLASS), target.eClass().getName())
				.setField(ref.findFieldByName(ProtobufSchema.FIELD_URI), reference(target))
				.build();
	}

	private static String reference(EObject target) {
		return EcoreUtil.getURI(target).toString();
	}
}
