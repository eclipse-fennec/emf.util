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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("MofGenerator: MOF schema -> .ecore + .genmodel files")
class MofGeneratorTest {

	@TempDir
	static Path dir;

	static Path schema;

	/** Two schemas (CIM_, PRS_) with a reference across them, as in the CIM Schema distribution. */
	@BeforeAll
	static void writeSchema() throws IOException {
		schema = dir.resolve("schema/test_schema.mof");
		Files.createDirectories(schema.getParent().resolve("Core"));
		Files.writeString(schema, """
				#pragma include ("Core/CIM_ManagedElement.mof")
				#pragma include ("Core/CIM_System.mof")
				#pragma include ("Core/CIM_Unused.mof")
				#pragma include ("Core/PRS_Activity.mof")
				""");
		Files.writeString(schema.resolveSibling("Core/CIM_ManagedElement.mof"), """
				[Abstract, Description ("Root class.")]
				class CIM_ManagedElement {
				   string Caption;
				};
				""");
		Files.writeString(schema.resolveSibling("Core/CIM_System.mof"), """
				class CIM_System : CIM_ManagedElement {
				      [Key, MaxLen (256)]
				   string Name;
				      [Key]
				   string CreationClassName;
				};
				""");
		Files.writeString(schema.resolveSibling("Core/CIM_Unused.mof"), """
				class CIM_Unused : CIM_ManagedElement {
				   uint16 Value;
				};
				""");
		Files.writeString(schema.resolveSibling("Core/PRS_Activity.mof"), """
				class PRS_Activity {
				      [Key]
				   string PRSUniqueName;
				   CIM_System REF Target;
				};
				""");
	}

	private static ResourceSet resourceSet() {
		ResourceSet rs = new ResourceSetImpl();
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
		GenModelPackage.eINSTANCE.eClass();
		return rs;
	}

	private static Resource load(ResourceSet rs, Path file) {
		return rs.getResource(URI.createFileURI(file.toAbsolutePath().toString()), true);
	}

	@Test
	@DisplayName("one .ecore per package, references across packages as relative hrefs")
	void ecorePerPackage() throws IOException {
		Path out = dir.resolve("per-package");
		MofGenerator.Result result = MofGenerator.generate(schema, out, MofGenerator.Options.defaults());

		assertThat(result.files()).containsExactly(out.resolve("cim.ecore"), out.resolve("prs.ecore"));
		assertThat(Files.readString(out.resolve("prs.ecore"))).contains("cim.ecore#//CIM_System");

		ResourceSet rs = resourceSet();
		EPackage prs = (EPackage) load(rs, out.resolve("prs.ecore")).getContents().get(0);
		assertThat(prs.getName()).isEqualTo("PRS");
		assertThat(prs.getNsURI()).isEqualTo("urn:mof:PRS");
		EReference target = (EReference) ((EClass) prs.getEClassifier("PRS_Activity")).getEStructuralFeature("Target");
		EcoreUtil.resolveAll(rs);
		assertThat(target.getEReferenceType().eIsProxy()).isFalse();
		assertThat(target.getEReferenceType().getName()).isEqualTo("CIM_System");
		assertThat(Diagnostician.INSTANCE.validate(prs).getSeverity()).isLessThan(Diagnostic.ERROR);
	}

	@Test
	@DisplayName("genmodel: packages, settings, and the settings mirrored in the .ecore")
	void genModel() throws IOException {
		Path out = dir.resolve("genmodel");
		MofGenerator.Options options = MofGenerator.Options.defaults()
				.withGenModel(true)
				.withModelName("Test")
				.withModelPluginId("org.example.test.model")
				.withBasePackage("org.example")
				.withCopyrightText("Copyright header");
		MofGenerator.Result result = MofGenerator.generate(schema, out, options);
		assertThat(result.files()).last().isEqualTo(out.resolve("test.genmodel"));

		ResourceSet rs = resourceSet();
		GenModel genModel = (GenModel) load(rs, out.resolve("test.genmodel")).getContents().get(0);
		assertThat(genModel.getModelName()).isEqualTo("Test");
		assertThat(genModel.getModelPluginID()).isEqualTo("org.example.test.model");
		assertThat(genModel.getModelDirectory()).isEqualTo("/org.example.test.model/src-gen");
		assertThat(genModel.getComplianceLevel().getLiteral()).isEqualTo("21.0");
		assertThat(genModel.isOSGiCompatible()).isTrue();
		assertThat(genModel.getCopyrightText()).isEqualTo("Copyright header");
		assertThat(genModel.getForeignModel()).containsExactly("cim.ecore", "prs.ecore");
		assertThat(genModel.getGenPackages()).extracting(GenPackage::getBasePackage)
				.containsExactly("org.example", "org.example");
		assertThat(genModel.getGenPackages()).allSatisfy(gp -> assertThat(gp.getEcorePackage().eIsProxy()).isFalse());

		EPackage cim = genModel.getGenPackages().get(0).getEcorePackage();
		assertThat(EcoreUtil.getAnnotation(cim, MofGenerator.GENMODEL, "basePackage")).isEqualTo("org.example");
		assertThat(EcoreUtil.getAnnotation(cim, MofGenerator.GENMODEL, "complianceLevel")).isEqualTo("21.0");
		assertThat(EcoreUtil.getAnnotation(cim, MofGenerator.GENMODEL, "copyrightText")).isEqualTo("Copyright header");
		// (GenModel's own EValidator needs the Eclipse runtime, so the genmodel is not validated here.)
	}

	@Test
	@DisplayName("same input and options -> byte-identical files, LF line ends")
	void deterministic() throws IOException {
		MofGenerator.Options options = MofGenerator.Options.defaults().withGenModel(true).withBasePackage("org.example");
		MofGenerator.Result first = MofGenerator.generate(schema, dir.resolve("run1"), options);
		MofGenerator.Result second = MofGenerator.generate(schema, dir.resolve("run2"),
				MofGenerator.Options.defaults().withGenModel(true).withBasePackage("org.example"));
		for (int i = 0; i < first.files().size(); i++) {
			assertThat(Files.readAllBytes(second.files().get(i)))
					.as(first.files().get(i).getFileName().toString())
					.isEqualTo(Files.readAllBytes(first.files().get(i)));
			assertThat(Files.readString(first.files().get(i))).doesNotContain("\r");
		}
	}

	@Test
	@DisplayName("command line: class list, one package, nsURI, genmodel")
	void commandLine() throws IOException {
		Path classes = dir.resolve("classes.txt");
		Files.writeString(classes, """
				# selection
				CIM_System   # with its superclass
				""");
		Path out = dir.resolve("cli");
		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		int exit = MofGenerator.run(new String[] { "--classes", classes.toString(), "--package", "test",
				"--ns-uri", "https://example.org/{package}/1", "--genmodel", "--base-package", "org.example",
				"--no-adapter-factory", schema.toString(), out.toString() }, print(stdout), print(stderr));

		assertThat(exit).as(stderr.toString(StandardCharsets.UTF_8)).isZero();
		assertThat(stdout.toString(StandardCharsets.UTF_8)).contains("test.ecore", "test.genmodel");
		EPackage p = (EPackage) load(resourceSet(), out.resolve("test.ecore")).getContents().get(0);
		assertThat(p.getNsURI()).isEqualTo("https://example.org/test/1");
		assertThat(p.getEClassifiers()).extracting(c -> c.getName())
				.containsExactlyInAnyOrder("CIM_ManagedElement", "CIM_System");
		assertThat(EcoreUtil.getAnnotation(p, MofGenerator.GENMODEL, "adapterFactory")).isEqualTo("false");
		GenModel genModel = (GenModel) load(resourceSet(), out.resolve("test.genmodel")).getContents().get(0);
		assertThat(genModel.getGenPackages().get(0).isAdapterFactory()).isFalse();
	}

	@Test
	@DisplayName("command line: lower-case packages, nsURI template per package")
	void commandLineLowerCase() {
		Path out = dir.resolve("cli-lower");
		int exit = MofGenerator.run(new String[] { "--lower-case-packages", "--ns-uri", "urn:test:{package}",
				schema.toString(), out.toString() }, print(new ByteArrayOutputStream()),
				print(new ByteArrayOutputStream()));
		assertThat(exit).isZero();
		EPackage prs = (EPackage) load(resourceSet(), out.resolve("prs.ecore")).getContents().get(0);
		assertThat(prs.getName()).isEqualTo("prs");
		assertThat(prs.getNsURI()).isEqualTo("urn:test:prs");
	}

	@Test
	@DisplayName("command line: usage errors exit 2, import errors exit 1")
	void commandLineErrors() throws IOException {
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		assertThat(MofGenerator.run(new String[] { "--bogus" }, print(new ByteArrayOutputStream()), print(stderr)))
				.isEqualTo(2);
		assertThat(stderr.toString(StandardCharsets.UTF_8)).contains("unknown option --bogus", "usage:");

		Path broken = dir.resolve("broken.mof");
		Files.writeString(broken, "class CIM_Broken : CIM_Missing { };\n");
		stderr.reset();
		assertThat(MofGenerator.run(new String[] { broken.toString(), dir.resolve("broken").toString() },
				print(new ByteArrayOutputStream()), print(stderr))).isEqualTo(1);
		assertThat(stderr.toString(StandardCharsets.UTF_8)).contains("CIM_Missing");
	}

	private static PrintStream print(ByteArrayOutputStream bytes) {
		return new PrintStream(bytes, true, StandardCharsets.UTF_8);
	}
}
