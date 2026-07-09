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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.protobuf.ProtobufAnnotations;
import org.eclipse.fennec.protobuf.ProtobufException;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Derives Ecore {@link EPackage}s from Protobuf descriptors — the inverse of the
 * {@code EPackage}→descriptor mapping, for <b>interop and bootstrapping</b>: take a foreign
 * {@code .proto} compiled to a {@code FileDescriptorSet}
 * ({@code protoc --include_imports --descriptor_set_out=…}) and get a dynamic EMF model
 * you can use or persist as {@code .ecore}. Text {@code .proto} is not parsed
 * (protobuf-java has no text parser); the input is always compiled descriptors.
 * <p>
 * The mapping is intentionally <b>structural</b> (a plain {@code .proto} carries no EMF
 * semantics), so it is lossy by design:
 * <ul>
 * <li>proto3 has no inheritance → all {@link EClass}es are flat.</li>
 * <li>every message-typed field becomes a <b>containment</b> {@link EReference} — there is
 * no reference/URI/proxy distinction to recover.</li>
 * <li>{@code int32}/{@code string} cannot be widened back to the {@code short}/{@code char}/
 * {@code BigDecimal}/date/custom types a forward export may have collapsed into them.</li>
 * <li>unsigned 64-bit ({@code uint64}/{@code fixed64}) maps to {@code ELong} and may overflow
 * for values above {@link Long#MAX_VALUE}.</li>
 * <li>the {@code EObjectAny}/{@code EObjectRef} wrapper messages of a Fennec export come back
 * as ordinary {@link EClass}es (annotation-aware, lossless import is not part of this).</li>
 * </ul>
 * Field numbers are preserved as {@code fieldNumber} annotations so a subsequent export is
 * wire-stable. {@code map<k,v>} fields become a containment reference to the synthetic entry
 * {@link EClass} (its {@code key}/{@code value} features); {@code oneof} members are flattened
 * to individual optional fields.
 */
public final class ProtobufImporter {

	private final ImportOptions options;
	private final Map<String, EPackage> packages = new LinkedHashMap<>();
	private final Map<String, EClass> classesByFullName = new HashMap<>();
	private final Map<String, EEnum> enumsByFullName = new HashMap<>();
	private final List<GrpcService> services = new ArrayList<>();

	private ProtobufImporter(ImportOptions options) {
		this.options = options;
	}

	/** Derives Ecore from a {@code FileDescriptorSet} using the {@linkplain ImportOptions#defaults() defaults}. */
	public static List<EPackage> fromDescriptorSet(byte[] descriptorSet) {
		return fromDescriptorSet(descriptorSet, ImportOptions.defaults());
	}

	/**
	 * Derives one {@link EPackage} per proto {@code package} from a {@code FileDescriptorSet}
	 * (as produced by {@code protoc --descriptor_set_out}). Use {@code --include_imports} so
	 * every referenced dependency is present, or the build fails with a clear error.
	 */
	public static List<EPackage> fromDescriptorSet(byte[] descriptorSet, ImportOptions options) {
		return importFrom(descriptorSet, options).packages();
	}

	/** Imports packages <em>and</em> gRPC services from a {@code FileDescriptorSet} (defaults). */
	public static ProtobufImport importFrom(byte[] descriptorSet) {
		return importFrom(descriptorSet, ImportOptions.defaults());
	}

	/**
	 * Imports packages <em>and</em> gRPC services from a {@code FileDescriptorSet}. The services
	 * are the {@code service}/{@code method} definitions resolved to the imported request/response
	 * {@link EClass}es — decision-neutral raw material for a later {@code EOperation}/DDSR projection.
	 */
	public static ProtobufImport importFrom(byte[] descriptorSet, ImportOptions options) {
		if (descriptorSet == null) {
			throw new IllegalArgumentException("descriptorSet must not be null");
		}
		return new ProtobufImporter(options == null ? ImportOptions.defaults() : options).run(descriptorSet);
	}

	private ProtobufImport run(byte[] descriptorSet) {
		FileDescriptorSet set;
		try {
			set = FileDescriptorSet.parseFrom(descriptorSet);
		} catch (InvalidProtocolBufferException e) {
			throw new ProtobufException("Input is not a valid protobuf FileDescriptorSet", e);
		}
		Map<String, FileDescriptor> files = buildFiles(set);
		// Two passes so cross-file / cross-package type references resolve: declare all
		// classifiers first, then wire fields; services last (need the resolved EClasses).
		for (FileDescriptor file : files.values()) {
			declare(file);
		}
		for (FileDescriptor file : files.values()) {
			wire(file);
		}
		for (FileDescriptor file : files.values()) {
			extractServices(file);
		}
		return new ProtobufImport(new ArrayList<>(packages.values()), services);
	}

	/** Extracts the {@code service}/{@code method} definitions, resolving input/output to imported EClasses. */
	private void extractServices(FileDescriptor file) {
		for (ServiceDescriptor service : file.getServices()) {
			List<GrpcMethod> methods = new ArrayList<>();
			for (MethodDescriptor method : service.getMethods()) {
				methods.add(new GrpcMethod(
						method.getName(),
						service.getFullName() + "/" + method.getName(),
						classesByFullName.get(method.getInputType().getFullName()),
						classesByFullName.get(method.getOutputType().getFullName()),
						method.isClientStreaming(),
						method.isServerStreaming()));
			}
			services.add(new GrpcService(service.getName(), service.getFullName(), methods));
		}
	}

	// --- descriptor resolution -------------------------------------------------------------

	/** Builds the runtime {@link FileDescriptor}s, resolving each file's dependencies first. */
	private static Map<String, FileDescriptor> buildFiles(FileDescriptorSet set) {
		Map<String, FileDescriptorProto> protos = new LinkedHashMap<>();
		for (FileDescriptorProto proto : set.getFileList()) {
			protos.put(proto.getName(), proto);
		}
		Map<String, FileDescriptor> built = new LinkedHashMap<>();
		for (FileDescriptorProto proto : set.getFileList()) {
			buildFile(proto, protos, built, new LinkedHashSet<>());
		}
		return built;
	}

	private static FileDescriptor buildFile(FileDescriptorProto proto, Map<String, FileDescriptorProto> protos,
			Map<String, FileDescriptor> built, Set<String> building) {
		FileDescriptor done = built.get(proto.getName());
		if (done != null) {
			return done;
		}
		if (!building.add(proto.getName())) {
			throw new ProtobufException("Cyclic proto import involving '" + proto.getName() + "'");
		}
		List<FileDescriptor> deps = new ArrayList<>();
		for (String dependency : proto.getDependencyList()) {
			FileDescriptorProto depProto = protos.get(dependency);
			if (depProto == null) {
				throw new ProtobufException("Missing dependency '" + dependency + "' for '" + proto.getName()
						+ "' — regenerate the set with protoc --include_imports");
			}
			deps.add(buildFile(depProto, protos, built, building));
		}
		try {
			FileDescriptor file = FileDescriptor.buildFrom(proto, deps.toArray(new FileDescriptor[0]));
			built.put(proto.getName(), file);
			building.remove(proto.getName());
			return file;
		} catch (DescriptorValidationException e) {
			throw new ProtobufException("Could not build descriptors for '" + proto.getName() + "': "
					+ e.getMessage(), e);
		}
	}

	// --- pass 1: declare classifiers -------------------------------------------------------

	private void declare(FileDescriptor file) {
		String protoPackage = file.getPackage();
		EPackage ePackage = packageFor(protoPackage);
		for (Descriptor message : file.getMessageTypes()) {
			declareMessage(message, ePackage, protoPackage);
		}
		for (EnumDescriptor eEnum : file.getEnumTypes()) {
			declareEnum(eEnum, ePackage, protoPackage);
		}
	}

	private void declareMessage(Descriptor message, EPackage ePackage, String protoPackage) {
		EClass eClass = EcoreFactory.eINSTANCE.createEClass();
		eClass.setName(localName(message.getFullName(), protoPackage));
		ePackage.getEClassifiers().add(eClass);
		classesByFullName.put(message.getFullName(), eClass);
		// Nested messages / enums (incl. synthetic map-entry types) become top-level classifiers.
		for (Descriptor nested : message.getNestedTypes()) {
			declareMessage(nested, ePackage, protoPackage);
		}
		for (EnumDescriptor nestedEnum : message.getEnumTypes()) {
			declareEnum(nestedEnum, ePackage, protoPackage);
		}
	}

	private void declareEnum(EnumDescriptor eEnum, EPackage ePackage, String protoPackage) {
		EEnum enumeration = EcoreFactory.eINSTANCE.createEEnum();
		enumeration.setName(localName(eEnum.getFullName(), protoPackage));
		for (EnumValueDescriptor value : eEnum.getValues()) {
			EEnumLiteral literal = EcoreFactory.eINSTANCE.createEEnumLiteral();
			literal.setName(value.getName());
			literal.setLiteral(value.getName());
			literal.setValue(value.getNumber());
			enumeration.getELiterals().add(literal);
		}
		ePackage.getEClassifiers().add(enumeration);
		enumsByFullName.put(eEnum.getFullName(), enumeration);
	}

	// --- pass 2: wire fields ---------------------------------------------------------------

	private void wire(FileDescriptor file) {
		for (Descriptor message : file.getMessageTypes()) {
			wireMessage(message);
		}
	}

	private void wireMessage(Descriptor message) {
		EClass eClass = classesByFullName.get(message.getFullName());
		for (FieldDescriptor field : message.getFields()) {
			eClass.getEStructuralFeatures().add(feature(field));
		}
		for (Descriptor nested : message.getNestedTypes()) {
			wireMessage(nested);
		}
	}

	private EStructuralFeature feature(FieldDescriptor field) {
		EStructuralFeature feature = switch (field.getType()) {
		case MESSAGE -> {
			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			ref.setContainment(true);
			ref.setEType(classesByFullName.get(field.getMessageType().getFullName()));
			yield ref;
		}
		case ENUM -> attribute(enumsByFullName.get(field.getEnumType().getFullName()));
		case GROUP -> throw new ProtobufException("proto2 groups are not supported (field " + field.getName() + ")");
		default -> attribute(scalarType(field));
		};
		feature.setName(featureName(field.getName()));
		if (field.isRepeated()) {
			feature.setUpperBound(-1);
		} else if (feature instanceof EAttribute && field.hasPresence()) {
			// proto3 explicit presence (optional scalar / oneof member) -> EMF unset-vs-default.
			feature.setUnsettable(true);
		}
		if (options.fieldNumberAnnotations()) {
			ProtobufAnnotations.setFieldNumber(feature, field.getNumber());
		}
		return feature;
	}

	private static EAttribute attribute(EClassifier type) {
		EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
		attribute.setEType(type);
		return attribute;
	}

	/** The Ecore data type for a scalar field — the inverse of the Ecore→Protobuf scalar mapping. */
	private static EClassifier scalarType(FieldDescriptor field) {
		EcorePackage ecore = EcorePackage.eINSTANCE;
		return switch (field.getType()) {
		case BOOL -> ecore.getEBoolean();
		case INT32, SINT32, SFIXED32 -> ecore.getEInt();
		case UINT32, FIXED32 -> ecore.getELong(); // unsigned 32-bit exceeds EInt's range
		case INT64, UINT64, SINT64, FIXED64, SFIXED64 -> ecore.getELong();
		case FLOAT -> ecore.getEFloat();
		case DOUBLE -> ecore.getEDouble();
		case STRING -> ecore.getEString();
		case BYTES -> ecore.getEByteArray();
		default -> throw new ProtobufException("Unsupported protobuf field type " + field.getType()
				+ " for field " + field.getName());
		};
	}

	// --- naming ----------------------------------------------------------------------------

	private EPackage packageFor(String protoPackage) {
		return packages.computeIfAbsent(protoPackage, p -> {
			EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
			String simple = p.isEmpty() ? "model" : p.substring(p.lastIndexOf('.') + 1);
			ePackage.setName(simple);
			ePackage.setNsPrefix(simple);
			ePackage.setNsURI(options.nsUri().apply(p));
			return ePackage;
		});
	}

	/** The classifier's name relative to its package, with nested-type separators flattened to {@code _}. */
	private static String localName(String fullName, String protoPackage) {
		String local = protoPackage.isEmpty() ? fullName : fullName.substring(protoPackage.length() + 1);
		return local.replace('.', '_');
	}

	private String featureName(String protoName) {
		if (!options.camelCaseNames() || protoName.indexOf('_') < 0) {
			return protoName;
		}
		StringBuilder sb = new StringBuilder(protoName.length());
		boolean upper = false;
		for (int i = 0; i < protoName.length(); i++) {
			char ch = protoName.charAt(i);
			if (ch == '_') {
				upper = true;
			} else {
				sb.append(upper ? Character.toUpperCase(ch) : ch);
				upper = false;
			}
		}
		return sb.toString();
	}
}
