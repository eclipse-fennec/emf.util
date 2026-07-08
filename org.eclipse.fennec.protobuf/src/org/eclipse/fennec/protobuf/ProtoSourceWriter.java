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

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;

/**
 * Renders a {@link FileDescriptorProto} as proto3 source text. The protobuf-java
 * runtime has no {@code .proto} text serializer, so this walks the descriptor
 * model directly.
 */
final class ProtoSourceWriter {

	private ProtoSourceWriter() {
	}

	static String render(FileDescriptorProto file) {
		StringBuilder sb = new StringBuilder();
		sb.append("syntax = \"proto3\";\n\n");
		if (file.hasPackage() && !file.getPackage().isEmpty()) {
			sb.append("package ").append(file.getPackage()).append(";\n\n");
		}
		for (EnumDescriptorProto e : file.getEnumTypeList()) {
			renderEnum(sb, e);
			sb.append('\n');
		}
		for (DescriptorProto m : file.getMessageTypeList()) {
			renderMessage(sb, m);
			sb.append('\n');
		}
		return sb.toString().stripTrailing() + "\n";
	}

	private static void renderMessage(StringBuilder sb, DescriptorProto message) {
		sb.append("message ").append(message.getName()).append(" {\n");
		for (FieldDescriptorProto f : message.getFieldList()) {
			sb.append("  ");
			if (f.getLabel() == FieldDescriptorProto.Label.LABEL_REPEATED) {
				sb.append("repeated ");
			} else if (f.getProto3Optional()) {
				sb.append("optional ");
			}
			sb.append(typeName(f)).append(' ').append(f.getName()).append(" = ").append(f.getNumber()).append(";\n");
		}
		sb.append("}\n");
	}

	private static void renderEnum(StringBuilder sb, EnumDescriptorProto e) {
		sb.append("enum ").append(e.getName()).append(" {\n");
		if (e.hasOptions() && e.getOptions().getAllowAlias()) {
			sb.append("  option allow_alias = true;\n");
		}
		for (EnumValueDescriptorProto v : e.getValueList()) {
			sb.append("  ").append(v.getName()).append(" = ").append(v.getNumber()).append(";\n");
		}
		sb.append("}\n");
	}

	private static String typeName(FieldDescriptorProto f) {
		switch (f.getType()) {
		case TYPE_BOOL:
			return "bool";
		case TYPE_INT32:
			return "int32";
		case TYPE_INT64:
			return "int64";
		case TYPE_FLOAT:
			return "float";
		case TYPE_DOUBLE:
			return "double";
		case TYPE_STRING:
			return "string";
		case TYPE_BYTES:
			return "bytes";
		case TYPE_MESSAGE:
		case TYPE_ENUM:
			String name = f.getTypeName();
			return name.substring(name.lastIndexOf('.') + 1);
		default:
			return f.getType().name();
		}
	}
}
