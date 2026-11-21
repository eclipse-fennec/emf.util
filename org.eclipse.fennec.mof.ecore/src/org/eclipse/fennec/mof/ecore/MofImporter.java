/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.mof.ecore.MofAst.ClassDecl;
import org.eclipse.fennec.mof.ecore.MofAst.Literal;
import org.eclipse.fennec.mof.ecore.MofAst.Method;
import org.eclipse.fennec.mof.ecore.MofAst.Parameter;
import org.eclipse.fennec.mof.ecore.MofAst.Property;
import org.eclipse.fennec.mof.ecore.MofAst.Qualifier;
import org.eclipse.fennec.mof.ecore.MofAst.QualifierDecl;
import org.eclipse.fennec.mof.ecore.MofAst.Type;
import org.eclipse.fennec.mof.ecore.MofAst.Value;

/**
 * Derives Ecore {@link EPackage}s from <b>DMTF MOF</b> — the <em>Managed Object Format</em> of
 * the Common Information Model (DMTF DSP0004), in which the CIM Schema and the vendor schemas
 * built on it (Microsoft WMI, SMI-S, SMASH/DASH) are published. Not to be confused with OMG MOF
 * (<em>Meta Object Facility</em>).
 * <p>
 * The entry file's {@code #pragma include} directives are followed (relative to the including
 * file, falling back to the entry file's directory), so a whole schema imports from its root
 * file, e.g. {@code cim_schema_2.56.0.mof}. Class declarations are mapped; instance declarations
 * are skipped. The mapping:
 * <ul>
 * <li>class → {@link EClass} with the exact CIM name; {@code Abstract} → abstract; superclass →
 * {@code eSuperTypes}. Associations and indications are ordinary classes (their
 * {@code Association}/{@code Indication} qualifier is kept as annotation).</li>
 * <li>{@code REF} property → non-containment {@link EReference} to the declared class. A WMI
 * {@code object} property → containment reference to {@code EObject}.</li>
 * <li>a property or method a subclass re-declares (CIM {@code Override}) is not created again —
 * Ecore cannot redefine an inherited feature — but recorded on the class as
 * {@code override.<name>} (the narrowed type) plus {@code override.<name>.<Qualifier>}.</li>
 * <li>{@code Key} properties → class annotation {@code keys}, inherited keys first.</li>
 * <li>{@code uint8/16}, {@code sint8/16/32} → {@code EIntegerObject}; {@code uint32/64},
 * {@code sint64} → {@code ELongObject} ({@code uint64} values above {@link Long#MAX_VALUE}
 * overflow); {@code real32/64} → {@code EFloatObject}/{@code EDoubleObject}; {@code boolean} →
 * {@code EBooleanObject}; {@code char16} → {@code ECharacterObject}; {@code string} and
 * {@code datetime} → {@code EString}. Object types keep "not set" apart from 0/false. The CIM
 * type is kept as annotation {@code cimType}.</li>
 * <li>arrays → {@code upperBound = -1} (fixed-size arrays: the size), {@code unique = false}.</li>
 * <li>scalar default values → {@code defaultValueLiteral}; array defaults → annotation
 * {@code default}.</li>
 * <li>methods → {@link EOperation}s with {@link EParameter}s ({@code IN}/{@code OUT} stay
 * qualifiers); {@code void} (WMI) → no return type.</li>
 * <li>{@code Description} → GenModel {@code documentation}; every other qualifier → a detail
 * of the {@value MofAnnotations#SOURCE} annotation (see {@link MofAnnotations}).</li>
 * </ul>
 * Classes go into one {@link EPackage} per schema prefix by default ({@code CIM}, {@code Win32},
 * …); supertypes and references cross packages freely. CIM names are case-insensitive: a second
 * declaration of the same class is ignored with a warning.
 */
public final class MofImporter {

	private static final Map<String, EDataType> DATA_TYPES = new HashMap<>();
	static {
		EcorePackage ec = EcorePackage.eINSTANCE;
		for (String t : List.of("uint8", "sint8", "uint16", "sint16", "sint32")) {
			DATA_TYPES.put(t, ec.getEIntegerObject());
		}
		for (String t : List.of("uint32", "uint64", "sint64")) {
			DATA_TYPES.put(t, ec.getELongObject());
		}
		DATA_TYPES.put("real32", ec.getEFloatObject());
		DATA_TYPES.put("real64", ec.getEDoubleObject());
		DATA_TYPES.put("boolean", ec.getEBooleanObject());
		DATA_TYPES.put("char16", ec.getECharacterObject());
		DATA_TYPES.put("string", ec.getEString());
		DATA_TYPES.put("datetime", ec.getEString());
	}

	private static final Pattern ENV_REFERENCE = Pattern.compile("%([A-Za-z0-9_]+)%");
	private static final String OBJECT = "object";
	private static final String VOID = "void";

	private final ImportOptions options;
	private final URIConverter converter;
	private final Map<String, ClassDecl> declarations = new LinkedHashMap<>();
	private final Map<String, String> qualifierNames = new HashMap<>();
	private final Set<String> arrayQualifiers = new HashSet<>();
	private final List<String> warnings = new ArrayList<>();
	private final Set<URI> visited = new HashSet<>();
	private URI entry;

	private final Map<String, EPackage> packages = new LinkedHashMap<>();
	private final Map<String, EClass> eClasses = new HashMap<>();
	private final Map<String, ClassInfo> infos = new HashMap<>();

	/** Per class: the inherited and own feature/operation names (lower case → name) and the keys. */
	private record ClassInfo(Map<String, String> features, Map<String, String> operations, List<String> keys) {

		static ClassInfo empty() {
			return new ClassInfo(new LinkedHashMap<>(), new LinkedHashMap<>(), new ArrayList<>());
		}

		ClassInfo copy() {
			return new ClassInfo(new LinkedHashMap<>(features), new LinkedHashMap<>(operations),
					new ArrayList<>(keys));
		}
	}

	private MofImporter(ImportOptions options, URIConverter converter) {
		this.options = options == null ? ImportOptions.defaults() : options;
		this.converter = converter == null ? new ExtensibleURIConverterImpl() : converter;
	}

	/** Imports the MOF file (and everything it includes) with the {@linkplain ImportOptions#defaults() defaults}. */
	public static MofImport importFrom(Path mofFile) {
		return importFrom(mofFile, ImportOptions.defaults());
	}

	/** Imports the MOF file and everything it {@code #pragma include}s. */
	public static MofImport importFrom(Path mofFile, ImportOptions options) {
		if (mofFile == null) {
			throw new IllegalArgumentException("mofFile must not be null");
		}
		return importFrom(URI.createFileURI(mofFile.toAbsolutePath().normalize().toString()), options, null);
	}

	/**
	 * Imports the MOF document at {@code uri}, reading it and its includes through
	 * {@code converter} (a default {@link ExtensibleURIConverterImpl} if {@code null}).
	 */
	public static MofImport importFrom(URI uri, ImportOptions options, URIConverter converter) {
		if (uri == null) {
			throw new IllegalArgumentException("uri must not be null");
		}
		MofImporter importer = new MofImporter(options, converter);
		importer.entry = uri;
		importer.include(uri);
		return importer.map();
	}

	/**
	 * Imports MOF text, e.g. the output of WMI's {@code ManagementClass.GetText(TextFormat.Mof)}.
	 * {@code #pragma include} is not allowed here (there is no base location).
	 */
	public static MofImport importText(String mof, ImportOptions options) {
		if (mof == null) {
			throw new IllegalArgumentException("mof must not be null");
		}
		MofImporter importer = new MofImporter(options, null);
		importer.parse(mof, "<text>", null);
		return importer.map();
	}

	/** Used by {@link MofResource}: the entry document comes as a stream, includes via the converter. */
	static MofImport importStream(InputStream in, URI uri, ImportOptions options, URIConverter converter)
			throws IOException {
		MofImporter importer = new MofImporter(options, converter);
		importer.entry = uri;
		importer.visited.add(uri);
		importer.parse(decode(in.readAllBytes()), display(uri), uri);
		return importer.map();
	}

	// --- reading -------------------------------------------------------------------------------

	private void include(URI uri) {
		if (!visited.add(uri)) {
			return;
		}
		byte[] bytes;
		try (InputStream in = converter.createInputStream(uri)) {
			bytes = in.readAllBytes();
		} catch (IOException e) {
			throw new MofException("Cannot read MOF file " + display(uri) + ": " + e.getMessage(), e);
		}
		parse(decode(bytes), display(uri), uri);
	}

	private void parse(String text, String name, URI base) {
		MofParser parser = MofParser.parse(text, name, path -> include(resolveInclude(path, base, name)));
		for (QualifierDecl q : parser.qualifierDecls) {
			String lower = q.name().toLowerCase(Locale.ROOT);
			qualifierNames.putIfAbsent(lower, q.name());
			if (q.type().isArray()) {
				arrayQualifiers.add(lower);
			}
		}
		for (ClassDecl c : parser.classes) {
			ClassDecl previous = declarations.putIfAbsent(c.name().toLowerCase(Locale.ROOT), c);
			if (previous != null) {
				warnings.add("Class " + c.name() + " declared again at " + c.location() + " (first at "
						+ previous.location() + "); the later declaration is ignored");
			}
		}
	}

	private URI resolveInclude(String path, URI base, String from) {
		if (base == null) {
			throw new MofException(from + ": #pragma include(\"" + path + "\") needs a base location");
		}
		String p = expandEnvironment(path).replace('\\', '/');
		URI target;
		if (p.matches("[A-Za-z]:/.*") || p.startsWith("/")) {
			target = URI.createFileURI(p);
		} else {
			String[] segments = p.split("/");
			for (int i = 0; i < segments.length; i++) {
				segments[i] = URI.encodeSegment(segments[i], true);
			}
			URI relative = URI.createHierarchicalURI(segments, null, null);
			target = relative.resolve(base);
			if (!converter.exists(target, null) && entry != null && !entry.equals(base)) {
				URI fromEntry = relative.resolve(entry);
				if (converter.exists(fromEntry, null)) {
					target = fromEntry;
				}
			}
		}
		if (!converter.exists(target, null)) {
			throw new MofException(from + ": included file \"" + path + "\" not found (" + display(target) + ")");
		}
		return target;
	}

	/** Expands {@code %NAME%} environment references, as the WMI MOF compiler does in include paths. */
	private static String expandEnvironment(String path) {
		Matcher m = ENV_REFERENCE.matcher(path);
		StringBuilder sb = new StringBuilder();
		while (m.find()) {
			String value = System.getenv(m.group(1));
			m.appendReplacement(sb, Matcher.quoteReplacement(value != null ? value : m.group()));
		}
		return m.appendTail(sb).toString();
	}

	private static String display(URI uri) {
		return uri.isFile() ? uri.toFileString() : uri.toString();
	}

	/** Decodes MOF bytes: BOM-marked UTF-8/UTF-16 (WMI exports), BOM-less UTF-16, UTF-8, else windows-1252. */
	static String decode(byte[] b) {
		if (b.length >= 3 && (b[0] & 0xFF) == 0xEF && (b[1] & 0xFF) == 0xBB && (b[2] & 0xFF) == 0xBF) {
			return new String(b, 3, b.length - 3, StandardCharsets.UTF_8);
		}
		if (b.length >= 2 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xFE) {
			return new String(b, 2, b.length - 2, StandardCharsets.UTF_16LE);
		}
		if (b.length >= 2 && (b[0] & 0xFF) == 0xFE && (b[1] & 0xFF) == 0xFF) {
			return new String(b, 2, b.length - 2, StandardCharsets.UTF_16BE);
		}
		if (b.length >= 2 && b[0] != 0 && b[1] == 0) {
			return new String(b, StandardCharsets.UTF_16LE);
		}
		if (b.length >= 2 && b[0] == 0 && b[1] != 0) {
			return new String(b, StandardCharsets.UTF_16BE);
		}
		try {
			return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(b)).toString();
		} catch (CharacterCodingException e) {
			Charset fallback = Charset.isSupported("windows-1252") ? Charset.forName("windows-1252")
					: StandardCharsets.ISO_8859_1;
			return new String(b, fallback);
		}
	}

	// --- selection and resolution --------------------------------------------------------------

	private static String lower(String s) {
		return s.toLowerCase(Locale.ROOT);
	}

	/** The class names a declaration depends on: superclass, REF targets, embedded class types. */
	private List<String> dependencies(ClassDecl c) {
		List<String> deps = new ArrayList<>();
		if (c.superclass() != null) {
			deps.add(c.superclass());
		}
		for (Property p : c.properties()) {
			addTypeDependency(deps, p.type());
		}
		for (Method m : c.methods()) {
			addTypeDependency(deps, m.returnType());
			for (Parameter p : m.parameters()) {
				addTypeDependency(deps, p.type());
			}
		}
		return deps;
	}

	private static void addTypeDependency(List<String> deps, Type type) {
		String t = type.lowerName();
		if (t.equals(OBJECT) || (!type.ref() && (DATA_TYPES.containsKey(t) || t.equals(VOID)))) {
			return;
		}
		deps.add(type.name());
	}

	private Map<String, ClassDecl> select(List<String> problems) {
		if (options.classes() == null) {
			return declarations;
		}
		Map<String, ClassDecl> selected = new LinkedHashMap<>();
		Deque<String> queue = new ArrayDeque<>();
		for (String requested : options.classes()) {
			if (declarations.containsKey(requested)) {
				queue.add(requested);
			} else {
				problems.add("Requested class " + requested + " is not declared");
			}
		}
		while (!queue.isEmpty()) {
			String name = queue.poll();
			ClassDecl c = declarations.get(name);
			if (c == null || selected.containsKey(name)) {
				continue;
			}
			selected.put(name, c);
			for (String dep : dependencies(c)) {
				queue.add(lower(dep));
			}
		}
		// keep declaration order
		Map<String, ClassDecl> ordered = new LinkedHashMap<>();
		declarations.forEach((k, v) -> {
			if (selected.containsKey(k)) {
				ordered.put(k, v);
			}
		});
		return ordered;
	}

	private MofImport map() {
		List<String> problems = new ArrayList<>();
		Map<String, ClassDecl> selected = select(problems);
		for (ClassDecl c : selected.values()) {
			if (c.superclass() != null && !selected.containsKey(lower(c.superclass()))) {
				problems.add("Class " + c.name() + " (" + c.location() + "): superclass " + c.superclass()
						+ " is not declared");
			}
			Set<String> reported = new HashSet<>();
			for (String dep : dependencies(c)) {
				if (!dep.equalsIgnoreCase(c.superclass()) && !selected.containsKey(lower(dep))
						&& reported.add(lower(dep))) {
					problems.add("Class " + c.name() + " (" + c.location() + "): type " + dep + " is not declared");
				}
			}
		}
		if (!problems.isEmpty()) {
			if (options.strict()) {
				int max = 50;
				StringBuilder sb = new StringBuilder("Unresolved MOF types (" + problems.size() + "):");
				problems.stream().limit(max).forEach(p -> sb.append("\n  ").append(p));
				if (problems.size() > max) {
					sb.append("\n  …");
				}
				throw new MofException(sb.toString());
			}
			warnings.addAll(problems);
		}

		for (ClassDecl c : selected.values()) {
			EClass eClass = EcoreFactory.eINSTANCE.createEClass();
			eClass.setName(c.name());
			eClasses.put(lower(c.name()), eClass);
			packageFor(c.name()).getEClassifiers().add(eClass);
		}
		for (ClassDecl c : topologicalOrder(selected)) {
			mapClass(c);
		}
		return new MofImport(new ArrayList<>(packages.values()), warnings);
	}

	private EPackage packageFor(String className) {
		String name = options.packageName().apply(className);
		return packages.computeIfAbsent(name, n -> {
			EPackage p = EcoreFactory.eINSTANCE.createEPackage();
			p.setName(n);
			p.setNsURI(options.nsUri().apply(n));
			p.setNsPrefix(options.nsPrefix().apply(n));
			return p;
		});
	}

	private List<ClassDecl> topologicalOrder(Map<String, ClassDecl> selected) {
		List<ClassDecl> order = new ArrayList<>();
		Set<String> done = new HashSet<>();
		for (ClassDecl c : selected.values()) {
			Deque<ClassDecl> chain = new ArrayDeque<>();
			Set<String> onChain = new LinkedHashSet<>();
			ClassDecl cur = c;
			while (cur != null && !done.contains(lower(cur.name()))) {
				if (!onChain.add(lower(cur.name()))) {
					throw new MofException("Cyclic superclass chain: " + String.join(" -> ", onChain) + " -> "
							+ cur.name());
				}
				chain.push(cur);
				cur = cur.superclass() == null ? null : selected.get(lower(cur.superclass()));
			}
			while (!chain.isEmpty()) {
				ClassDecl next = chain.pop();
				done.add(lower(next.name()));
				order.add(next);
			}
		}
		return order;
	}

	// --- mapping -------------------------------------------------------------------------------

	private void mapClass(ClassDecl c) {
		EClass eClass = eClasses.get(lower(c.name()));
		ClassInfo info = ClassInfo.empty();
		if (c.superclass() != null) {
			EClass superClass = eClasses.get(lower(c.superclass()));
			if (superClass != null) {
				eClass.getESuperTypes().add(superClass);
				info = infos.get(lower(c.superclass())).copy();
			} else {
				cim(eClass).getDetails().put(MofAnnotations.UNRESOLVED_SUPERCLASS, c.superclass());
			}
		}
		infos.put(lower(c.name()), info);
		for (Qualifier q : c.qualifiers()) {
			if (q.is("Abstract")) {
				eClass.setAbstract(q.isTrue());
			}
		}
		applyQualifiers(eClass, c.qualifiers());

		for (Property p : c.properties()) {
			String key = lower(p.name());
			String inherited = info.features().get(key);
			if (inherited != null) {
				recordOverride(eClass, c, inherited, p.type(), p.qualifiers());
			} else {
				warnOverrideWithoutBase(c, p.name(), p.qualifiers());
				eClass.getEStructuralFeatures().add(property(c, p));
				info.features().put(key, p.name());
				inherited = p.name();
			}
			if (p.qualifiers().stream().anyMatch(q -> q.is("Key") && q.isTrue())
					&& info.keys().stream().noneMatch(k -> k.equalsIgnoreCase(p.name()))) {
				info.keys().add(inherited);
			}
		}
		for (Method m : c.methods()) {
			String key = lower(m.name());
			String inherited = info.operations().get(key);
			if (inherited != null) {
				recordOverride(eClass, c, inherited, m.returnType(), m.qualifiers());
			} else {
				warnOverrideWithoutBase(c, m.name(), m.qualifiers());
				eClass.getEOperations().add(operation(c, m));
				info.operations().put(key, m.name());
			}
		}
		if (!info.keys().isEmpty()) {
			cim(eClass).getDetails().put(MofAnnotations.KEYS, String.join(",", info.keys()));
		}
	}

	private void recordOverride(EClass eClass, ClassDecl c, String inheritedName, Type type, List<Qualifier> qualifiers) {
		if (qualifiers.stream().noneMatch(q -> q.is("Override"))) {
			warnings.add("Class " + c.name() + " (" + c.location() + ") re-declares inherited " + inheritedName
					+ " without Override qualifier; treated as override");
		}
		EAnnotation a = cim(eClass);
		String prefix = MofAnnotations.OVERRIDE_PREFIX + inheritedName;
		a.getDetails().put(prefix, type.display());
		for (Qualifier q : qualifiers) {
			if (!q.is("Override")) {
				a.getDetails().put(prefix + "." + canonical(q.name()), encode(q));
			}
		}
	}

	private void warnOverrideWithoutBase(ClassDecl c, String name, List<Qualifier> qualifiers) {
		if (qualifiers.stream().anyMatch(q -> q.is("Override"))) {
			warnings.add("Class " + c.name() + " (" + c.location() + "): " + name
					+ " has an Override qualifier but nothing inherited to override; imported as own feature");
		}
	}

	private org.eclipse.emf.ecore.EStructuralFeature property(ClassDecl c, Property p) {
		Type type = p.type();
		org.eclipse.emf.ecore.EStructuralFeature feature;
		String t = type.lowerName();
		if (type.ref() || t.equals(OBJECT) || !DATA_TYPES.containsKey(t)) {
			EReference ref = EcoreFactory.eINSTANCE.createEReference();
			// REF → association end; WMI object / MOF 3 embedded class type → containment
			ref.setContainment(!type.ref());
			setClassType(ref, type);
			feature = ref;
		} else {
			EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
			attribute.setEType(DATA_TYPES.get(t));
			feature = attribute;
		}
		feature.setName(p.name());
		setMultiplicity(feature, type);
		cim(feature).getDetails().put(MofAnnotations.CIM_TYPE, type.display());
		if (p.defaultValue() != null) {
			applyDefault(feature, p.defaultValue());
		}
		applyQualifiers(feature, p.qualifiers());
		return feature;
	}

	private EOperation operation(ClassDecl c, Method m) {
		EOperation op = EcoreFactory.eINSTANCE.createEOperation();
		op.setName(m.name());
		Type rt = m.returnType();
		if (!rt.lowerName().equals(VOID) || rt.ref()) {
			setType(op, rt);
			cim(op).getDetails().put(MofAnnotations.CIM_TYPE, rt.display());
		}
		applyQualifiers(op, m.qualifiers());
		for (Parameter p : m.parameters()) {
			EParameter param = EcoreFactory.eINSTANCE.createEParameter();
			param.setName(p.name());
			setType(param, p.type());
			cim(param).getDetails().put(MofAnnotations.CIM_TYPE, p.type().display());
			applyQualifiers(param, p.qualifiers());
			op.getEParameters().add(param);
		}
		return op;
	}

	private void setType(ETypedElement element, Type type) {
		EDataType dataType = type.ref() ? null : DATA_TYPES.get(type.lowerName());
		if (dataType != null) {
			element.setEType(dataType);
		} else {
			setClassType(element, type);
		}
		setMultiplicity(element, type);
	}

	private void setClassType(ETypedElement element, Type type) {
		if (type.lowerName().equals(OBJECT)) {
			element.setEType(EcorePackage.Literals.EOBJECT);
			return;
		}
		EClass target = eClasses.get(type.lowerName());
		if (target != null) {
			element.setEType(target);
		} else {
			element.setEType(EcorePackage.Literals.EOBJECT);
			cim(element).getDetails().put(MofAnnotations.UNRESOLVED_TYPE, type.name());
		}
	}

	private static void setMultiplicity(ETypedElement element, Type type) {
		if (type.isArray()) {
			element.setUpperBound(type.array() > 0 ? type.array() : ETypedElement.UNBOUNDED_MULTIPLICITY);
			// CIM arrays are bags; Ecore requires containment references to be unique
			element.setUnique(element instanceof EReference r && r.isContainment());
		}
	}

	private void applyDefault(org.eclipse.emf.ecore.EStructuralFeature feature, Value value) {
		if (value.isArray()) {
			cim(feature).getDetails().put(MofAnnotations.DEFAULT, MofAnnotations.toJsonArray(value.elements()));
			return;
		}
		Literal l = value.scalar();
		if (l.kind() == Literal.Kind.NULL) {
			return;
		}
		if (feature instanceof EAttribute attribute && l.kind() != Literal.Kind.ALIAS) {
			try {
				EcoreUtil.createFromString(attribute.getEAttributeType(), l.text());
				attribute.setDefaultValueLiteral(l.text());
				return;
			} catch (RuntimeException e) {
				// not representable (e.g. uint64 above Long.MAX_VALUE) — keep it as annotation
			}
		}
		cim(feature).getDetails().put(MofAnnotations.DEFAULT, l.text());
	}

	private String canonical(String qualifierName) {
		return qualifierNames.getOrDefault(lower(qualifierName), qualifierName);
	}

	private String encode(Qualifier q) {
		Value v = q.value();
		if (v == null) {
			return "true";
		}
		if (v.isArray()) {
			return MofAnnotations.toJsonArray(v.elements());
		}
		if (arrayQualifiers.contains(lower(q.name())) && v.scalar().kind() != Literal.Kind.NULL) {
			return MofAnnotations.toJsonArray(List.of(v.scalar()));
		}
		return v.scalar().text();
	}

	private void applyQualifiers(EModelElement element, List<Qualifier> qualifiers) {
		for (Qualifier q : qualifiers) {
			if (q.is("Description") && q.value() != null && !q.value().isArray()) {
				EcoreUtil.setDocumentation(element, q.value().scalar().text());
			} else {
				cim(element).getDetails().put(canonical(q.name()), encode(q));
			}
		}
	}

	private static EAnnotation cim(EModelElement element) {
		EAnnotation a = element.getEAnnotation(MofAnnotations.SOURCE);
		if (a == null) {
			a = EcoreFactory.eINSTANCE.createEAnnotation();
			a.setSource(MofAnnotations.SOURCE);
			element.getEAnnotations().add(a);
		}
		return a;
	}

}
