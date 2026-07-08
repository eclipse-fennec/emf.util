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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;

import com.google.protobuf.ByteString;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;

/**
 * Maps Ecore data types to Protobuf scalar types and converts attribute values
 * between the EMF and Protobuf representations. Enums are handled through the
 * owning feature's {@code EFactory} so the mapping works for dynamic models too.
 */
final class TypeMapper {

	private TypeMapper() {
	}

	/**
	 * The Protobuf scalar type for a non-enum {@link EDataType}. Everything the
	 * runtime cannot represent natively (BigInteger/BigDecimal, dates, custom data
	 * types) is carried losslessly as its {@code EFactory} string form.
	 */
	static FieldDescriptorProto.Type scalarType(EDataType dt) {
		Class<?> ic = dt.getInstanceClass();
		if (ic == boolean.class || ic == Boolean.class) {
			return FieldDescriptorProto.Type.TYPE_BOOL;
		}
		if (ic == int.class || ic == Integer.class || ic == short.class || ic == Short.class
				|| ic == byte.class || ic == Byte.class || ic == char.class || ic == Character.class) {
			return FieldDescriptorProto.Type.TYPE_INT32;
		}
		if (ic == long.class || ic == Long.class) {
			return FieldDescriptorProto.Type.TYPE_INT64;
		}
		if (ic == float.class || ic == Float.class) {
			return FieldDescriptorProto.Type.TYPE_FLOAT;
		}
		if (ic == double.class || ic == Double.class) {
			return FieldDescriptorProto.Type.TYPE_DOUBLE;
		}
		if (ic == byte[].class) {
			return FieldDescriptorProto.Type.TYPE_BYTES;
		}
		return FieldDescriptorProto.Type.TYPE_STRING;
	}

	/** Converts an EMF attribute value to the object expected by {@code DynamicMessage.setField}. */
	static Object toProto(FieldDescriptor fd, EAttribute attr, Object value) {
		switch (fd.getJavaType()) {
		case BOOLEAN:
			return value;
		case INT:
			return value instanceof Character c ? Integer.valueOf(c) : Integer.valueOf(((Number) value).intValue());
		case LONG:
			return Long.valueOf(((Number) value).longValue());
		case FLOAT:
			return Float.valueOf(((Number) value).floatValue());
		case DOUBLE:
			return Double.valueOf(((Number) value).doubleValue());
		case STRING:
			return stringify(attr.getEAttributeType(), value);
		case BYTE_STRING:
			return ByteString.copyFrom((byte[]) value);
		case ENUM:
			return toEnumValue(fd, attr.getEAttributeType(), value);
		default:
			throw new ProtobufException("Unsupported attribute type " + fd.getJavaType() + " for " + attr.getName());
		}
	}

	/** Converts a Protobuf field value back to the EMF attribute representation. */
	static Object fromProto(FieldDescriptor fd, EAttribute attr, Object value) {
		switch (fd.getJavaType()) {
		case BOOLEAN:
		case FLOAT:
		case DOUBLE:
			return value;
		case INT:
			return coerceInt(attr.getEAttributeType(), ((Number) value).intValue());
		case LONG:
			return value;
		case STRING:
			return destringify(attr.getEAttributeType(), (String) value);
		case BYTE_STRING:
			return ((ByteString) value).toByteArray();
		case ENUM:
			return fromEnumValue(attr.getEAttributeType(), (EnumValueDescriptor) value);
		default:
			throw new ProtobufException("Unsupported attribute type " + fd.getJavaType() + " for " + attr.getName());
		}
	}

	private static Object toEnumValue(FieldDescriptor fd, EDataType enumType, Object value) {
		String name = enumType.getEPackage().getEFactoryInstance().convertToString(enumType, value);
		EnumValueDescriptor evd = fd.getEnumType().findValueByName(name);
		if (evd == null) {
			throw new ProtobufException("No protobuf enum value '" + name + "' in " + fd.getEnumType().getName());
		}
		return evd;
	}

	private static Object fromEnumValue(EDataType enumType, EnumValueDescriptor value) {
		return enumType.getEPackage().getEFactoryInstance().createFromString(enumType, value.getName());
	}

	private static String stringify(EDataType dt, Object value) {
		if (dt.getInstanceClass() == String.class) {
			return (String) value;
		}
		return dt.getEPackage().getEFactoryInstance().convertToString(dt, value);
	}

	private static Object destringify(EDataType dt, String value) {
		if (dt.getInstanceClass() == String.class) {
			return value;
		}
		return dt.getEPackage().getEFactoryInstance().createFromString(dt, value);
	}

	/** Narrows an int32 back to the feature's Java type (short/byte/char) where needed. */
	private static Object coerceInt(EDataType dt, int value) {
		Class<?> ic = dt.getInstanceClass();
		if (ic == short.class || ic == Short.class) {
			return Short.valueOf((short) value);
		}
		if (ic == byte.class || ic == Byte.class) {
			return Byte.valueOf((byte) value);
		}
		if (ic == char.class || ic == Character.class) {
			return Character.valueOf((char) value);
		}
		return Integer.valueOf(value);
	}
}
