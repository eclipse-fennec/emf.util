/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore.generator;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.mof.ecore.ImportOptions;
import org.eclipse.fennec.mof.ecore.MofImport;
import org.eclipse.fennec.mof.ecore.MofImporter;

/**
 * Writes a DMTF MOF schema as files: one {@code .ecore} per derived package and, optionally, a
 * {@code .genmodel} for code generation. The import itself is {@link MofImporter}; this class adds
 * what every consumer would otherwise write around it — package naming, saving, and initialising
 * the GenModel — so that a model can be regenerated from its original schema with one command.
 * <p>
 * Same input, same options → byte-identical files: classifiers keep the declaration order of the
 * MOF, and nothing time- or environment-dependent is written.
 * <p>
 * Command line, see {@link #usage()}:
 *
 * <pre>
 * java -cp &lt;classpath&gt; org.eclipse.fennec.mof.ecore.generator.MofGenerator \
 *      --classes classes.txt --package cim --ns-uri https://example.org/cim/2 \
 *      --genmodel --model-plugin org.example.cim.model --base-package org.example \
 *      cim_schema_2.56.0.mof model/
 * </pre>
 */
public final class MofGenerator {

	/** Source of the GenModel annotation (documentation, generator settings). */
	public static final String GENMODEL = GenModelPackage.eNS_URI;

	private MofGenerator() {
	}

	/**
	 * Generator settings beyond {@link ImportOptions}. Without {@link #withGenModel(boolean)} only
	 * the {@code .ecore} files are written.
	 */
	public static final class Options {

		private ImportOptions importOptions = ImportOptions.defaults();
		private boolean genModel;
		private String modelName;
		private String modelPluginId;
		private String modelDirectory;
		private String basePackage;
		private String complianceLevel = "21.0";
		private String copyrightText;
		private boolean adapterFactory = true;

		public static Options defaults() {
			return new Options();
		}

		/** The options passed to {@link MofImporter}: classes, package naming, strictness. */
		public Options withImportOptions(ImportOptions importOptions) {
			if (importOptions != null) {
				this.importOptions = importOptions;
			}
			return this;
		}

		/** Also write a {@code .genmodel} and the GenModel settings into the {@code .ecore}. */
		public Options withGenModel(boolean genModel) {
			this.genModel = genModel;
			return this;
		}

		/**
		 * GenModel {@code modelName}; also the base name of the {@code .genmodel} file (in lower
		 * case). Default: the name of the first package.
		 */
		public Options withModelName(String modelName) {
			this.modelName = modelName;
			return this;
		}

		/** GenModel {@code modelPluginID}, e.g. the bundle symbolic name. */
		public Options withModelPluginId(String modelPluginId) {
			this.modelPluginId = modelPluginId;
			return this;
		}

		/** GenModel {@code modelDirectory}. Default {@code /<modelPluginId>/src-gen}. */
		public Options withModelDirectory(String modelDirectory) {
			this.modelDirectory = modelDirectory;
			return this;
		}

		/** GenPackage {@code basePackage}: the Java package the model packages are put under. */
		public Options withBasePackage(String basePackage) {
			this.basePackage = basePackage;
			return this;
		}

		/** GenModel {@code complianceLevel}, e.g. {@code "21.0"} (default) or {@code "17.0"}. */
		public Options withComplianceLevel(String complianceLevel) {
			if (complianceLevel != null) {
				this.complianceLevel = complianceLevel.contains(".") ? complianceLevel : complianceLevel + ".0";
			}
			return this;
		}

		/**
		 * GenPackage {@code adapterFactory}: whether {@code <Prefix>Switch} and
		 * {@code <Prefix>AdapterFactory} are generated (default {@code true}). Turn it off for
		 * very large packages: the generated switch over all classes exceeds the JVM's 64 KB
		 * method limit (the complete CIM Schema has ≈ 1,600 classes in one package).
		 */
		public Options withAdapterFactory(boolean adapterFactory) {
			this.adapterFactory = adapterFactory;
			return this;
		}

		/** GenModel {@code copyrightText}, the header of every generated file. */
		public Options withCopyrightText(String copyrightText) {
			this.copyrightText = copyrightText;
			return this;
		}
	}

	/** The files written, in order (the {@code .ecore} files, then the {@code .genmodel}). */
	public record Result(List<Path> files, List<String> warnings) {

		public Result {
			files = List.copyOf(files);
			warnings = List.copyOf(warnings);
		}
	}

	/**
	 * Imports {@code entry} (and everything it {@code #pragma include}s) and writes
	 * {@code <package>.ecore} for every derived package — file name in lower case — plus, if
	 * requested, {@code <modelName>.genmodel} into {@code outputDirectory}.
	 *
	 * @throws org.eclipse.fennec.mof.ecore.MofException on a syntax error or, in strict mode, on an
	 *         unresolved superclass or reference type
	 */
	public static Result generate(Path entry, Path outputDirectory, Options options) throws IOException {
		MofImport result = MofImporter.importFrom(entry, options.importOptions);
		List<EPackage> packages = result.packages();
		Files.createDirectories(outputDirectory);

		ResourceSet resourceSet = new ResourceSetImpl();
		Map<String, Object> factories = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
		factories.put("ecore", new EcoreResourceFactoryImpl());
		factories.put("genmodel", new XMIResourceFactoryImpl());

		String modelName = options.modelName != null ? options.modelName
				: packages.isEmpty() ? "model" : packages.get(0).getName();
		if (options.genModel) {
			packages.forEach(p -> annotateGenModelSettings(p, modelName, options));
		}

		// Create every resource before saving any, so references between packages are written as
		// relative hrefs ("prs.ecore#//PRS_Foo") rather than resolved against the nsURI.
		List<Path> files = new ArrayList<>();
		List<Resource> resources = new ArrayList<>();
		for (EPackage p : packages) {
			Path file = outputDirectory.resolve(p.getName().toLowerCase(Locale.ROOT) + ".ecore");
			Resource r = resourceSet.createResource(fileUri(file));
			r.getContents().add(p);
			resources.add(r);
			files.add(file);
		}
		for (Resource r : resources) {
			r.save(saveOptions());
		}

		if (options.genModel) {
			Path file = outputDirectory.resolve(modelName.toLowerCase(Locale.ROOT) + ".genmodel");
			Resource r = resourceSet.createResource(fileUri(file));
			r.getContents().add(genModel(packages, modelName, files, options));
			r.save(saveOptions());
			files.add(file);
		}
		return new Result(files, result.warnings());
	}

	private static GenModel genModel(List<EPackage> packages, String modelName, List<Path> ecoreFiles,
			Options options) {
		GenModelPackage.eINSTANCE.eClass();
		GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
		genModel.initialize(packages);
		genModel.setModelName(modelName);
		if (options.modelPluginId != null) {
			genModel.setModelPluginID(options.modelPluginId);
		}
		String modelDirectory = options.modelDirectory != null ? options.modelDirectory
				: options.modelPluginId != null ? "/" + options.modelPluginId + "/src-gen" : null;
		if (modelDirectory != null) {
			genModel.setModelDirectory(modelDirectory);
		}
		genModel.setImporterID("org.eclipse.emf.importer.ecore");
		GenJDKLevel level = GenJDKLevel.get(options.complianceLevel);
		if (level == null) {
			throw new IllegalArgumentException("unknown compliance level " + options.complianceLevel);
		}
		genModel.setComplianceLevel(level);
		genModel.setRootExtendsClass("org.eclipse.emf.ecore.impl.MinimalEObjectImpl$Container");
		genModel.setOperationReflection(true);
		genModel.setImportOrganizing(true);
		genModel.setOSGiCompatible(true);
		genModel.setSuppressGenModelAnnotations(false);
		genModel.setCopyrightFields(false);
		if (options.copyrightText != null) {
			genModel.setCopyrightText(options.copyrightText);
		}
		for (Path f : ecoreFiles) {
			genModel.getForeignModel().add(f.getFileName().toString());
		}
		for (GenPackage gp : genModel.getGenPackages()) {
			if (options.basePackage != null) {
				gp.setBasePackage(options.basePackage);
			}
			gp.setDisposableProviderFactory(true);
			gp.setAdapterFactory(options.adapterFactory);
		}
		return genModel;
	}

	/**
	 * The settings a GenModel re-import reads back from the {@code .ecore} (the convention of
	 * eclipse-fennec/common.models: a regenerated genmodel must not lose them).
	 */
	private static void annotateGenModelSettings(EPackage p, String modelName, Options options) {
		Map<String, String> settings = new LinkedHashMap<>();
		settings.put("modelName", modelName);
		if (options.basePackage != null) {
			settings.put("basePackage", options.basePackage);
		}
		settings.put("complianceLevel", options.complianceLevel);
		settings.put("oSGiCompatible", "true");
		if (!options.adapterFactory) {
			settings.put("adapterFactory", "false");
		}
		if (options.copyrightText != null) {
			settings.put("copyrightText", options.copyrightText);
		}
		EAnnotation a = p.getEAnnotation(GENMODEL);
		if (a == null) {
			a = EcoreFactory.eINSTANCE.createEAnnotation();
			a.setSource(GENMODEL);
			p.getEAnnotations().add(0, a);
		}
		a.getDetails().putAll(settings);
	}

	private static Map<Object, Object> saveOptions() {
		Map<Object, Object> o = new LinkedHashMap<>();
		o.put(XMLResource.OPTION_ENCODING, "UTF-8");
		o.put(XMLResource.OPTION_LINE_WIDTH, 120);
		// Same bytes on every platform (EMF would otherwise use the platform line separator).
		o.put(Resource.OPTION_LINE_DELIMITER, "\n");
		return o;
	}

	private static URI fileUri(Path file) {
		return URI.createFileURI(file.toAbsolutePath().normalize().toString());
	}

	// ------------------------------------------------------------------ command line

	public static void main(String[] args) {
		System.exit(run(args, System.out, System.err));
	}

	/** Runs the command line; returns the exit code (0 ok, 1 import/IO failure, 2 usage error). */
	static int run(String[] args, PrintStream out, PrintStream err) {
		ImportOptions importOptions = ImportOptions.defaults();
		Options options = Options.defaults();
		List<String> positional = new ArrayList<>();
		String packageName = null;
		String nsUri = null;
		String nsPrefix = null;
		boolean lowerCase = false;
		try {
			for (int i = 0; i < args.length; i++) {
				String a = args[i];
				switch (a) {
				case "-h", "--help" -> {
					out.println(usage());
					return 0;
				}
				case "--classes" -> importOptions.withClasses(readClassList(Path.of(value(args, ++i, a))));
				case "--package" -> packageName = value(args, ++i, a);
				case "--lower-case-packages" -> lowerCase = true;
				case "--ns-uri" -> nsUri = value(args, ++i, a);
				case "--ns-prefix" -> nsPrefix = value(args, ++i, a);
				case "--lenient" -> importOptions.withStrict(false);
				case "--genmodel" -> options.withGenModel(true);
				case "--model-name" -> options.withModelName(value(args, ++i, a));
				case "--model-plugin" -> options.withModelPluginId(value(args, ++i, a));
				case "--model-directory" -> options.withModelDirectory(value(args, ++i, a));
				case "--base-package" -> options.withBasePackage(value(args, ++i, a));
				case "--no-adapter-factory" -> options.withAdapterFactory(false);
				case "--compliance" -> options.withComplianceLevel(value(args, ++i, a));
				case "--copyright" -> options.withCopyrightText(
						Files.readString(Path.of(value(args, ++i, a)), StandardCharsets.UTF_8).strip());
				default -> {
					if (a.startsWith("-")) {
						throw new IllegalArgumentException("unknown option " + a);
					}
					positional.add(a);
				}
				}
			}
			if (positional.size() != 2) {
				throw new IllegalArgumentException("expected <entry.mof> <output-directory>");
			}
		} catch (IllegalArgumentException | IOException e) {
			err.println("error: " + e.getMessage());
			err.println(usage());
			return 2;
		}

		String fixedPackage = packageName;
		boolean lower = lowerCase;
		if (fixedPackage != null) {
			importOptions.withPackageName(c -> fixedPackage);
		} else if (lower) {
			importOptions.withPackageName(c -> ImportOptions.schemaOf(c).toLowerCase(Locale.ROOT));
		}
		if (nsUri != null) {
			String template = nsUri;
			importOptions.withNsUri(p -> template.replace("{package}", p.toLowerCase(Locale.ROOT)));
		}
		if (nsPrefix != null) {
			String template = nsPrefix;
			importOptions.withNsPrefix(p -> template.replace("{package}", p.toLowerCase(Locale.ROOT)));
		}
		options.withImportOptions(importOptions);

		try {
			Result result = generate(Path.of(positional.get(0)), Path.of(positional.get(1)), options);
			result.warnings().forEach(w -> err.println("warning: " + w));
			result.files().forEach(f -> out.println("wrote " + f));
			return 0;
		} catch (IOException | RuntimeException e) {
			err.println("error: " + e.getMessage());
			return 1;
		}
	}

	/**
	 * Reads a class list: one CIM class name per line; blank lines and everything after {@code #}
	 * are ignored.
	 */
	static List<String> readClassList(Path file) throws IOException {
		List<String> names = new ArrayList<>();
		for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
			int hash = line.indexOf('#');
			String name = (hash >= 0 ? line.substring(0, hash) : line).strip();
			if (!name.isEmpty()) {
				names.add(name);
			}
		}
		return names;
	}

	private static String value(String[] args, int i, String option) {
		if (i >= args.length) {
			throw new IllegalArgumentException(option + " needs a value");
		}
		return args[i];
	}

	static String usage() {
		return """
				usage: MofGenerator [options] <entry.mof> <output-directory>

				Writes <package>.ecore for every package derived from the MOF schema (and what it
				#pragma includes), optionally <model>.genmodel.

				Import:
				  --classes <file>         only these classes (one per line, # comments) plus their
				                           superclasses and REF targets; default: all
				  --package <name>         put every class into this one package
				  --lower-case-packages    package per schema prefix, in lower case (cim, prs, …)
				                           instead of CIM, PRS
				  --ns-uri <uri>           nsURI; {package} is replaced by the package name in
				                           lower case. Default urn:mof:<package>
				  --ns-prefix <prefix>     nsPrefix, {package} as above. Default: package in lower case
				  --lenient                import unresolved superclasses/REF targets with a warning
				                           instead of failing
				GenModel:
				  --genmodel               also write <model>.genmodel and the GenModel settings
				                           into the .ecore
				  --model-name <name>      model name and .genmodel file name; default: first package
				  --model-plugin <id>      modelPluginID, e.g. the bundle symbolic name
				  --model-directory <dir>  modelDirectory; default /<model-plugin>/src-gen
				  --base-package <pkg>     Java base package
				  --compliance <level>     Java compliance level; default 21
				  --no-adapter-factory     no <Prefix>Switch/AdapterFactory (needed for very large
				                           packages: the switch exceeds the 64 KB method limit)
				  --copyright <file>       copyright text for generated files""";
	}
}
