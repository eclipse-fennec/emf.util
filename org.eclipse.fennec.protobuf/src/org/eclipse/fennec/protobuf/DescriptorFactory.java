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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumDescriptorProto;
import com.google.protobuf.DescriptorProtos.EnumOptions;
import com.google.protobuf.DescriptorProtos.EnumValueDescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.OneofDescriptorProto;

/**
 * Builds a {@link FileDescriptorProto} from an {@link EPackage} — the Ecore→Protobuf
 * mapping rules (type mapping, field numbering, enum handling, polymorphism
 * wrappers, name validation) live here. {@link ProtobufSchema} owns the resulting
 * runtime descriptors and the (de)serialization facade.
 */
final class DescriptorFactory {

	private static final Pattern PROTO_IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

	private final EPackage ePackage;
	private final String protoPackage;

	DescriptorFactory(EPackage ePackage) {
		this.ePackage = ePackage;
		this.protoPackage = (ePackage.getName() == null || ePackage.getName().isBlank()) ? "model"
				: ePackage.getName();
	}

	FileDescriptorProto build() {
		validateName(protoPackage, "package");
		reserveWrapperNames();

		FileDescriptorProto.Builder file = FileDescriptorProto.newBuilder()
				.setName(protoPackage + ".proto")
				.setSyntax("proto3")
				.setPackage(protoPackage);

		boolean[] wrappers = scanWrapperUsage();
		if (wrappers[0]) {
			file.addMessageType(buildWrapper(ProtobufSchema.ANY_MESSAGE, ProtobufSchema.FIELD_DATA,
					FieldDescriptorProto.Type.TYPE_BYTES));
		}
		if (wrappers[1]) {
			file.addMessageType(buildWrapper(ProtobufSchema.REF_MESSAGE, ProtobufSchema.FIELD_URI,
					FieldDescriptorProto.Type.TYPE_STRING));
		}
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (c instanceof EEnum eEnum) {
				file.addEnumType(buildEnum(eEnum));
			}
		}
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (c instanceof EClass eClass) {
				file.addMessageType(buildMessage(eClass));
			}
		}
		return file.build();
	}

	private boolean isLocal(EClassifier c) {
		return c != null && c.getEPackage() == ePackage;
	}

	/**
	 * A local type is polymorphic (needs a type-discriminated wrapper) if it is
	 * abstract/an interface or has a concrete subtype in this package.
	 */
	private boolean polymorphic(EClass type) {
		if (!isLocal(type)) {
			return false;
		}
		if (type.isAbstract() || type.isInterface()) {
			return true;
		}
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (c instanceof EClass x && x != type && !x.isAbstract() && !x.isInterface()
					&& x.getEAllSuperTypes().contains(type)) {
				return true;
			}
		}
		return false;
	}

	private String typeRef(String simpleName) {
		return "." + protoPackage + "." + simpleName;
	}

	/** Returns {@code [usesAny, usesRef]} across all references in the package. */
	private boolean[] scanWrapperUsage() {
		boolean any = false;
		boolean ref = false;
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (!(c instanceof EClass eClass)) {
				continue;
			}
			for (EStructuralFeature f : eClass.getEAllStructuralFeatures()) {
				if (f instanceof EReference r && polymorphic(r.getEReferenceType())) {
					any |= r.isContainment();
					ref |= !r.isContainment();
				}
			}
		}
		return new boolean[] { any, ref };
	}

	private DescriptorProto buildWrapper(String name, String secondField, FieldDescriptorProto.Type secondType) {
		return DescriptorProto.newBuilder().setName(name)
				.addField(FieldDescriptorProto.newBuilder().setName(ProtobufSchema.FIELD_ECLASS).setNumber(1)
						.setType(FieldDescriptorProto.Type.TYPE_STRING)
						.setLabel(FieldDescriptorProto.Label.LABEL_OPTIONAL))
				.addField(FieldDescriptorProto.newBuilder().setName(secondField).setNumber(2)
						.setType(secondType).setLabel(FieldDescriptorProto.Label.LABEL_OPTIONAL))
				.build();
	}

	private DescriptorProto buildMessage(EClass eClass) {
		validateName(eClass.getName(), "EClass");
		DescriptorProto.Builder msg = DescriptorProto.newBuilder().setName(eClass.getName());
		List<EStructuralFeature> features = eClass.getEAllStructuralFeatures();
		Map<EStructuralFeature, Integer> numbers = assignFieldNumbers(eClass, features);

		for (EStructuralFeature f : features) {
			validateName(f.getName(), "feature");
			FieldDescriptorProto.Builder field = FieldDescriptorProto.newBuilder()
					.setName(f.getName())
					.setNumber(numbers.get(f));
			boolean many = f.isMany();
			field.setLabel(many ? FieldDescriptorProto.Label.LABEL_REPEATED
					: FieldDescriptorProto.Label.LABEL_OPTIONAL);
			boolean scalarPresence;

			if (f instanceof EReference ref) {
				scalarPresence = configureReference(eClass, ref, field);
			} else {
				EAttribute attr = (EAttribute) f;
				EDataType dt = attr.getEAttributeType();
				if (dt instanceof EEnum && isLocal(dt)) {
					field.setType(FieldDescriptorProto.Type.TYPE_ENUM).setTypeName(typeRef(dt.getName()));
				} else {
					field.setType(TypeMapper.scalarType(dt));
				}
				scalarPresence = !many;
			}

			if (scalarPresence) {
				// proto3 explicit presence: a synthetic oneof per optional scalar,
				// so unset-vs-default round-trips (mirrors EMF eIsSet).
				int oneofIndex = msg.getOneofDeclCount();
				msg.addOneofDecl(OneofDescriptorProto.newBuilder().setName("_" + f.getName()));
				field.setOneofIndex(oneofIndex);
				field.setProto3Optional(true);
			}
			msg.addField(field);
		}
		return msg.build();
	}

	/** Sets the field type for a reference; returns whether the field needs scalar presence. */
	private boolean configureReference(EClass owner, EReference ref, FieldDescriptorProto.Builder field) {
		EClass target = ref.getEReferenceType();
		if (ref.isContainment()) {
			if (!isLocal(target)) {
				throw new ProtobufException("Cross-package containment is not supported: "
						+ owner.getName() + "." + ref.getName() + " -> " + target.getName());
			}
			String message = polymorphic(target) ? ProtobufSchema.ANY_MESSAGE : target.getName();
			field.setType(FieldDescriptorProto.Type.TYPE_MESSAGE).setTypeName(typeRef(message));
			return false; // message fields carry presence intrinsically
		}
		// non-containment: URI reference; polymorphic targets keep the actual type
		if (polymorphic(target)) {
			field.setType(FieldDescriptorProto.Type.TYPE_MESSAGE).setTypeName(typeRef(ProtobufSchema.REF_MESSAGE));
			return false;
		}
		field.setType(FieldDescriptorProto.Type.TYPE_STRING);
		return !ref.isMany();
	}

	private Map<EStructuralFeature, Integer> assignFieldNumbers(EClass eClass, List<EStructuralFeature> features) {
		Map<EStructuralFeature, Integer> numbers = new LinkedHashMap<>();
		Set<Integer> used = new HashSet<>();
		for (EStructuralFeature f : features) {
			Integer explicit = ProtobufAnnotations.explicitFieldNumber(f);
			if (explicit != null) {
				if (!used.add(explicit)) {
					throw new ProtobufException("Duplicate protobuf field number " + explicit + " in message "
							+ eClass.getName() + " (feature " + f.getName() + ")");
				}
				numbers.put(f, explicit);
			}
		}
		int next = 1;
		for (EStructuralFeature f : features) {
			if (numbers.containsKey(f)) {
				continue;
			}
			while (used.contains(next)) {
				next++;
			}
			numbers.put(f, next);
			used.add(next);
		}
		return numbers;
	}

	private EnumDescriptorProto buildEnum(EEnum eEnum) {
		validateName(eEnum.getName(), "EEnum");
		EnumDescriptorProto.Builder builder = EnumDescriptorProto.newBuilder().setName(eEnum.getName());
		List<EEnumLiteral> literals = eEnum.getELiterals();

		EEnumLiteral zero = literals.stream().filter(l -> l.getValue() == 0).findFirst().orElse(null);
		if (zero == null) {
			// proto3 requires the first enum value to be 0.
			builder.addValue(EnumValueDescriptorProto.newBuilder()
					.setName(eEnum.getName() + "_UNSPECIFIED").setNumber(0));
		}

		List<EEnumLiteral> ordered = new ArrayList<>();
		if (zero != null) {
			ordered.add(zero);
		}
		for (EEnumLiteral l : literals) {
			if (l != zero) {
				ordered.add(l);
			}
		}

		Set<Integer> seen = new HashSet<>();
		boolean alias = false;
		for (EEnumLiteral l : ordered) {
			validateName(l.getName(), "enum literal");
			if (!seen.add(l.getValue())) {
				alias = true;
			}
			builder.addValue(EnumValueDescriptorProto.newBuilder().setName(l.getName()).setNumber(l.getValue()));
		}
		if (alias) {
			builder.setOptions(EnumOptions.newBuilder().setAllowAlias(true));
		}
		return builder.build();
	}

	private void reserveWrapperNames() {
		for (EClassifier c : ePackage.getEClassifiers()) {
			if (ProtobufSchema.ANY_MESSAGE.equals(c.getName()) || ProtobufSchema.REF_MESSAGE.equals(c.getName())) {
				throw new ProtobufException("Classifier name '" + c.getName()
						+ "' is reserved by the protobuf mapping");
			}
		}
	}

	private static void validateName(String name, String kind) {
		if (name == null || !PROTO_IDENTIFIER.matcher(name).matches()) {
			throw new ProtobufException("Illegal " + kind + " name '" + name
					+ "' — must be a valid protobuf identifier [A-Za-z_][A-Za-z0-9_]*");
		}
	}
}
