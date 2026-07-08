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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;

/**
 * Serializes EMF objects to Protobuf wire bytes using the descriptors of a
 * {@link ProtobufSchema} and the configuration in a {@link ProtobufContext}.
 * <p>
 * A contained object whose reference is monomorphic-local is written inline with the
 * declared-type descriptor; otherwise it is wrapped in {@link ProtobufSchema#ANY_MESSAGE}
 * carrying a type discriminator (encoded per the context's {@link ProtobufTypeStrategy})
 * plus the object's own bytes — serialized against <em>its</em> package's schema, so
 * cross-package / subpackage / {@code EObject} / subtype targets round-trip. Only features
 * for which {@code eIsSet} is {@code true} (and that are configured to be written) are
 * emitted.
 */
public final class ProtobufWriter {

	private final ProtobufSchema schema;
	private final ProtobufContext context;

	ProtobufWriter(ProtobufSchema schema, ProtobufContext context) {
		this.schema = schema;
		this.context = context;
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
			if (!ProtobufAnnotations.writes(f) || !object.eIsSet(f)) {
				continue;
			}
			FieldDescriptor fd = descriptor.findFieldByName(f.getName());
			if (fd == null) {
				continue;
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
		EClass declared = ref.getEReferenceType();
		boolean message = fd.getType() == FieldDescriptor.Type.MESSAGE;
		if (ref.isContainment()) {
			boolean any = message && ProtobufSchema.ANY_MESSAGE.equals(fd.getMessageType().getName());
			if (ref.isMany()) {
				for (Object child : (List<?>) value) {
					EObject c = (EObject) child;
					checkSameDocument(c);
					builder.addRepeatedField(fd, any ? any(c, declared) : build(c, fd.getMessageType()));
				}
			} else {
				EObject c = (EObject) value;
				checkSameDocument(c);
				builder.setField(fd, any ? any(c, declared) : build(c, fd.getMessageType()));
			}
		} else if (ref.isMany()) {
			for (Object target : (List<?>) value) {
				builder.addRepeatedField(fd, message ? ref((EObject) target, declared) : reference((EObject) target));
			}
		} else {
			builder.setField(fd, message ? ref((EObject) value, declared) : reference((EObject) value));
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

	/** Wraps a contained object as {@code EObjectAny{eClass=discriminator, data=<own bytes>}}. */
	private DynamicMessage any(EObject child, EClass declared) {
		Descriptor any = schema.anyDescriptor();
		byte[] data = schemaFor(child.eClass().getEPackage()).writer(context).build(child).toByteArray();
		return DynamicMessage.newBuilder(any)
				.setField(any.findFieldByName(ProtobufSchema.FIELD_ECLASS), context.discriminator(child.eClass(), declared))
				.setField(any.findFieldByName(ProtobufSchema.FIELD_DATA), ByteString.copyFrom(data))
				.build();
	}

	/** Wraps a non-containment target as {@code EObjectRef{eClass=discriminator, uri}}. */
	private DynamicMessage ref(EObject target, EClass declared) {
		Descriptor ref = schema.refDescriptor();
		return DynamicMessage.newBuilder(ref)
				.setField(ref.findFieldByName(ProtobufSchema.FIELD_ECLASS), context.discriminator(target.eClass(), declared))
				.setField(ref.findFieldByName(ProtobufSchema.FIELD_URI), reference(target))
				.build();
	}

	private ProtobufSchema schemaFor(EPackage ePackage) {
		return ePackage == schema.ePackage() ? schema : context.schemaFor(ePackage);
	}

	/** Containment is intra-resource by definition; a foreign-resource child is unsupported. */
	private void checkSameDocument(EObject child) {
		Resource ctx = context.contextResource();
		if (ctx != null && child.eResource() != null && child.eResource() != ctx) {
			throw new ProtobufException("Cross-document containment is not supported: "
					+ child.eClass().getName() + " is contained via a reference but lives in another resource ("
					+ child.eResource().getURI() + ")");
		}
	}

	private String reference(EObject target) {
		Resource targetResource = target.eResource();
		Resource ctx = context.contextResource();
		if (ctx != null && targetResource == ctx) {
			// same-resource target -> relative fragment, resolved against the loading resource
			return "#" + targetResource.getURIFragment(target);
		}
		return EcoreUtil.getURI(target).toString();
	}
}
