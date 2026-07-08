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
import java.io.InputStream;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Deserializes Protobuf wire bytes back into EMF objects using the descriptors
 * of a {@link ProtobufSchema}. The expected root {@link EClass} must be supplied
 * by the caller (Protobuf carries no type tag); nested polymorphic objects carry
 * their own type in the {@link ProtobufSchema#ANY_MESSAGE}/{@link ProtobufSchema#REF_MESSAGE}
 * wrappers, so their concrete subtype is reconstructed. Non-containment references
 * are restored as proxies carrying the stored target URI; resolution is left to
 * the {@code ResourceSet}.
 */
public final class ProtobufReader {

	private final ProtobufSchema schema;

	ProtobufReader(ProtobufSchema schema) {
		this.schema = schema;
	}

	/** Parses {@code data} as an instance of {@code eClass}. */
	public EObject fromBytes(byte[] data, EClass eClass) {
		try {
			return read(DynamicMessage.parseFrom(schema.descriptorFor(eClass), data), eClass);
		} catch (InvalidProtocolBufferException e) {
			throw new ProtobufException("Could not parse protobuf bytes as " + eClass.getName(), e);
		}
	}

	/** Parses a stream as an instance of {@code eClass}. */
	public EObject read(InputStream in, EClass eClass) throws IOException {
		return read(DynamicMessage.parseFrom(schema.descriptorFor(eClass), in), eClass);
	}

	@SuppressWarnings("unchecked")
	private EObject read(DynamicMessage message, EClass eClass) {
		if (eClass.isAbstract() || eClass.isInterface()) {
			throw new ProtobufException("Cannot instantiate abstract/interface EClass " + eClass.getName());
		}
		EObject object = EcoreUtil.create(eClass);
		Descriptor descriptor = message.getDescriptorForType();

		for (EStructuralFeature f : eClass.getEAllStructuralFeatures()) {
			if (!f.isChangeable()) {
				continue;
			}
			FieldDescriptor fd = descriptor.findFieldByName(f.getName());
			if (fd == null) {
				continue;
			}
			if (f instanceof EReference ref) {
				readReference(message, fd, ref, object);
			} else {
				EAttribute attr = (EAttribute) f;
				if (attr.isMany()) {
					List<Object> target = (List<Object>) object.eGet(attr);
					for (Object item : (List<?>) message.getField(fd)) {
						target.add(TypeMapper.fromProto(fd, attr, item));
					}
				} else if (message.hasField(fd)) {
					object.eSet(attr, TypeMapper.fromProto(fd, attr, message.getField(fd)));
				}
			}
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	private void readReference(DynamicMessage message, FieldDescriptor fd, EReference ref, EObject object) {
		EClass declared = ref.getEReferenceType();
		boolean isMessage = fd.getType() == FieldDescriptor.Type.MESSAGE;
		if (ref.isContainment()) {
			boolean any = isMessage && ProtobufSchema.ANY_MESSAGE.equals(fd.getMessageType().getName());
			if (ref.isMany()) {
				List<EObject> target = (List<EObject>) object.eGet(ref);
				for (Object msg : (List<?>) message.getField(fd)) {
					target.add(readContained((DynamicMessage) msg, declared, any));
				}
			} else if (message.hasField(fd)) {
				object.eSet(ref, readContained((DynamicMessage) message.getField(fd), declared, any));
			}
		} else if (ref.isMany()) {
			List<EObject> target = (List<EObject>) object.eGet(ref);
			for (Object v : (List<?>) message.getField(fd)) {
				target.add(readReferenceValue(v, declared, isMessage));
			}
		} else if (message.hasField(fd)) {
			object.eSet(ref, readReferenceValue(message.getField(fd), declared, isMessage));
		}
	}

	private EObject readContained(DynamicMessage msg, EClass declared, boolean any) {
		if (!any) {
			return read(msg, declared);
		}
		Descriptor anyDesc = msg.getDescriptorForType();
		String eClassName = (String) msg.getField(anyDesc.findFieldByName(ProtobufSchema.FIELD_ECLASS));
		ByteString data = (ByteString) msg.getField(anyDesc.findFieldByName(ProtobufSchema.FIELD_DATA));
		EClass actual = schema.eClassByName(eClassName);
		try {
			return read(DynamicMessage.parseFrom(schema.descriptorFor(actual), data), actual);
		} catch (InvalidProtocolBufferException e) {
			throw new ProtobufException("Could not parse embedded " + eClassName, e);
		}
	}

	private EObject readReferenceValue(Object value, EClass declared, boolean isMessage) {
		if (!isMessage) {
			return proxy(declared, (String) value);
		}
		DynamicMessage ref = (DynamicMessage) value;
		Descriptor refDesc = ref.getDescriptorForType();
		String eClassName = (String) ref.getField(refDesc.findFieldByName(ProtobufSchema.FIELD_ECLASS));
		String uri = (String) ref.getField(refDesc.findFieldByName(ProtobufSchema.FIELD_URI));
		return proxy(schema.eClassByName(eClassName), uri);
	}

	private static EObject proxy(EClass targetType, String uri) {
		if (targetType.isAbstract() || targetType.isInterface()) {
			throw new ProtobufException("Cannot create a proxy for abstract reference type " + targetType.getName());
		}
		InternalEObject proxy = (InternalEObject) EcoreUtil.create(targetType);
		proxy.eSetProxyURI(URI.createURI(uri));
		return proxy;
	}
}
