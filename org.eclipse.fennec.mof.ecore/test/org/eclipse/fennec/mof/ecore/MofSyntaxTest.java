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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("MofImporter: syntax variants (WMI exports), lenient mode and errors")
class MofSyntaxTest {

	@TempDir
	Path dir;

	/** Shaped like the output of WMI's ManagementClass.GetText(TextFormat.Mof) and wbem/*.mof files. */
	private static final String WMI = """
			#pragma autorecover
			#pragma classflags("forceupdate")
			#pragma namespace ("\\\\\\\\.\\\\root\\\\cimv2")

			[Abstract, Locale(0x409) : ToInstance, UUID("{8502C4C1-5FBB-11D2-AAC1-006008C78BC7}")]
			class CIM_ManagedSystemElement
			{
				[read : ToSubclass, MaxLen(64) : ToSubclass, Description("A short textual "
				 "description.") : Amended ToSubclass] string Caption;
				[key, read] string Name;
			};

			[dynamic : ToInstance, provider("CIMWin32") : ToInstance, SupportsUpdate]
			class Win32_Service : CIM_ManagedSystemElement
			{
				[read, Values{"Running", "Stopped"} : Amended, ValueMap{"1", "2"}] uint32 State;
				[read] object Settings;
				[read] object History[];
				[read, CIMTYPE("object:Win32_Service")] string EmbeddedSelf;
				[Implemented] void StopService();
				[Implemented, static] uint32 Create([in] string Name, [in, out] Win32_Service ref Peer);
			};

			class __SystemThing
			{
				string Path;
			};

			instance of __Win32Provider as $P
			{
				Name = "CIMWin32";
				ClsId = "{d63a5850-8f16-11cf-9f47-00aa00bf345c}";
				Nested = { "a", "b" };
			};
			""";

	@Test
	@DisplayName("WMI flavors, provider qualifiers, void/object types, pragmas and instances")
	void wmiStyle() {
		MofImport result = MofImporter.importText(WMI, ImportOptions.defaults());
		assertThat(result.warnings()).isEmpty();
		assertThat(result.packages()).extracting(p -> p.getName()).containsExactly("CIM", "Win32", "Default");

		EClass element = result.getEClass("CIM_ManagedSystemElement");
		assertThat(element.isAbstract()).isTrue();
		assertThat(MofAnnotations.qualifier(element, "Locale")).isEqualTo("1033");
		assertThat(EcoreUtil.getDocumentation(element.getEStructuralFeature("Caption")))
				.isEqualTo("A short textual description.");
		assertThat(MofAnnotations.isSet(element.getEStructuralFeature("Caption"), "read")).isTrue();
		assertThat(MofAnnotations.keys(element)).containsExactly("Name");

		EClass service = result.getEClass("Win32_Service");
		assertThat(service.getESuperTypes()).containsExactly(element);
		assertThat(service.getEPackage().getName()).isEqualTo("Win32");
		assertThat(MofAnnotations.qualifier(service, "provider")).isEqualTo("CIMWin32");
		assertThat(MofAnnotations.qualifierValues(service.getEStructuralFeature("State"), "Values"))
				.containsExactly("Running", "Stopped");

		EReference settings = (EReference) service.getEStructuralFeature("Settings");
		assertThat(settings.isContainment()).isTrue();
		assertThat(settings.getEType()).isEqualTo(EcorePackage.Literals.EOBJECT);
		EReference history = (EReference) service.getEStructuralFeature("History");
		assertThat(history.isMany()).isTrue();
		assertThat(history.isUnique()).as("containment must be unique in Ecore").isTrue();

		EOperation stop = service.getEOperations().get(0);
		assertThat(stop.getEType()).isNull();
		EOperation create = service.getEOperations().get(1);
		assertThat(create.getEParameters().get(1).getEType()).isSameAs(service);
		assertThat(MofAnnotations.isSet(create.getEParameters().get(1), "out")).isTrue();

		assertThat(result.getEClass("__SystemThing").getEPackage().getName()).isEqualTo("Default");
		assertThat(result.getEClass("__Win32Provider")).isNull();
	}

	@Test
	@DisplayName("UTF-16LE files with BOM (as written by WMI) are decoded")
	void utf16() throws IOException {
		Path file = dir.resolve("wmi.mof");
		byte[] body = WMI.getBytes(StandardCharsets.UTF_16LE);
		byte[] withBom = new byte[body.length + 2];
		withBom[0] = (byte) 0xFF;
		withBom[1] = (byte) 0xFE;
		System.arraycopy(body, 0, withBom, 2, body.length);
		Files.write(file, withBom);
		assertThat(MofImporter.importFrom(file).getEClass("Win32_Service")).isNotNull();
	}

	@Test
	@DisplayName("strict mode fails on undeclared superclasses and REF targets, listing all of them")
	void strictUnresolved() {
		String mof = """
				class ACME_Port : CIM_NetworkPort { ACME_Thing REF Owner; };
				""";
		assertThatThrownBy(() -> MofImporter.importText(mof, ImportOptions.defaults()))
				.isInstanceOf(MofException.class).hasMessageContaining("Unresolved MOF types (2)")
				.hasMessageContaining("superclass CIM_NetworkPort is not declared")
				.hasMessageContaining("type ACME_Thing is not declared");
	}

	@Test
	@DisplayName("lenient mode imports anyway: no supertype, EObject-typed reference, warnings")
	void lenientUnresolved() {
		String mof = """
				class ACME_Port : CIM_NetworkPort { ACME_Thing REF Owner; };
				""";
		MofImport result = MofImporter.importText(mof, ImportOptions.defaults().withStrict(false));
		EClass port = result.getEClass("ACME_Port");
		assertThat(port.getESuperTypes()).isEmpty();
		assertThat(MofAnnotations.qualifier(port, MofAnnotations.UNRESOLVED_SUPERCLASS)).isEqualTo("CIM_NetworkPort");
		EReference owner = (EReference) port.getEStructuralFeature("Owner");
		assertThat(owner.getEType()).isEqualTo(EcorePackage.Literals.EOBJECT);
		assertThat(MofAnnotations.qualifier(owner, MofAnnotations.UNRESOLVED_TYPE)).isEqualTo("ACME_Thing");
		assertThat(result.warnings()).hasSize(2);
	}

	@Test
	@DisplayName("requesting an undeclared class in a subset is an error in strict mode")
	void unknownRequestedClass() {
		assertThatThrownBy(() -> MofImporter.importText("class A_B { };",
				ImportOptions.defaults().withClasses(List.of("A_Missing")))).isInstanceOf(MofException.class)
				.hasMessageContaining("Requested class a_missing is not declared");
	}

	@Test
	@DisplayName("a second declaration of a class (case-insensitive) is ignored with a warning")
	void duplicates() {
		MofImport result = MofImporter.importText("""
				class A_X { string One; };
				class a_x { string Two; };
				""", ImportOptions.defaults());
		assertThat(result.getEClass("A_X").getEStructuralFeature("One")).isNotNull();
		assertThat(result.warnings()).singleElement().asString().contains("declared again");
	}

	@Test
	@DisplayName("re-declaring an inherited property without Override is treated as override, with a warning")
	void implicitOverride() {
		MofImport result = MofImporter.importText("""
				class A_Base { string Name; };
				class A_Sub : A_Base { [MaxLen(10)] string name; };
				""", ImportOptions.defaults());
		EClass sub = result.getEClass("A_Sub");
		assertThat(sub.getEStructuralFeatures()).isEmpty();
		assertThat(MofAnnotations.qualifier(sub, "override.Name.MaxLen")).isEqualTo("10");
		assertThat(result.warnings()).singleElement().asString().contains("without Override");
	}

	@Test
	@DisplayName("MOF 3 association keyword is a class with the Association qualifier")
	void mof3Association() {
		MofImport result = MofImporter.importText("""
				class A_Thing { [Key] string Id; };
				association A_Link { A_Thing REF Left; A_Thing REF Right[]; };
				""", ImportOptions.defaults());
		EClass link = result.getEClass("A_Link");
		assertThat(MofAnnotations.isSet(link, "Association")).isTrue();
		assertThat(link.getEStructuralFeature("Right").getUpperBound()).isEqualTo(-1);
	}

	@Test
	@DisplayName("parse errors report file, line and column")
	void parseErrors() throws IOException {
		Path file = dir.resolve("broken.mof");
		Files.writeString(file, "class A_X {\n  string Name\n};\n");
		assertThatThrownBy(() -> MofImporter.importFrom(file)).isInstanceOf(MofException.class)
				.hasMessageContaining("broken.mof:3:1").hasMessageContaining("expected ';'");

		assertThatThrownBy(() -> MofImporter.importText("enumeration A_E : string { X };", null))
				.isInstanceOf(MofException.class).hasMessageContaining("not supported");
		assertThatThrownBy(() -> MofImporter.importText("class A_X { string S = \"open; };", null))
				.isInstanceOf(MofException.class).hasMessageContaining("unterminated string");
		assertThatThrownBy(() -> MofImporter.importText("/* never closed", null))
				.isInstanceOf(MofException.class).hasMessageContaining("unterminated comment");
	}

	@Test
	@DisplayName("missing include files and includes without a base location fail clearly")
	void includeErrors() throws IOException {
		Path file = dir.resolve("root.mof");
		Files.writeString(file, "#pragma include (\"Core/Missing.mof\")\n");
		assertThatThrownBy(() -> MofImporter.importFrom(file)).isInstanceOf(MofException.class)
				.hasMessageContaining("\"Core/Missing.mof\" not found");
		assertThatThrownBy(() -> MofImporter.importText("#pragma include (\"x.mof\")", null))
				.isInstanceOf(MofException.class).hasMessageContaining("needs a base location");
	}

	@Test
	@DisplayName("includes resolve relative to the including file, then the entry file; cycles are cut")
	void includeResolution() throws IOException {
		Files.createDirectories(dir.resolve("sub"));
		Files.writeString(dir.resolve("root.mof"), "#pragma include (\"sub/a.mof\")\n");
		// b.mof is next to root.mof, not next to a.mof → falls back to the entry directory
		Files.writeString(dir.resolve("sub/a.mof"), "#pragma include (\"b.mof\")\nclass X_A : X_B { };\n");
		Files.writeString(dir.resolve("b.mof"), "#pragma include (\"root.mof\")\nclass X_B { };\n");
		MofImport result = MofImporter.importFrom(dir.resolve("root.mof"));
		assertThat(result.getEClass("X_A").getESuperTypes()).containsExactly(result.getEClass("X_B"));
	}

	@Test
	@DisplayName("legacy WMI syntax: #include without pragma, backslash line continuation")
	void legacyWmiSyntax() throws IOException {
		Files.writeString(dir.resolve("base.mof"), "class X_Base { };\n");
		Files.writeString(dir.resolve("root.mof"), """
				#include ("base.mof")
				[read, \\
				 Description("continued") : amended, \\
				 ValueMap{"0"}]
				class X_Sub : X_Base { };
				""");
		MofImport result = MofImporter.importFrom(dir.resolve("root.mof"));
		EClass sub = result.getEClass("X_Sub");
		assertThat(sub.getESuperTypes()).containsExactly(result.getEClass("X_Base"));
		assertThat(EcoreUtil.getDocumentation(sub)).isEqualTo("continued");
	}

	@Test
	@DisplayName("lexer: escapes, numeric literal forms, comments")
	void literals() {
		MofImport result = MofImporter.importText("""
				// line comment
				[Description("tab\\tquote\\"backslash\\\\hex\\x0041"), A(0x1F), B(0101b), C(017), D(-2.5),
				 E('c'), F(null), G(TRUE), H{1, 2, }]
				class A_X { };
				""", ImportOptions.defaults());
		EClass x = result.getEClass("A_X");
		assertThat(EcoreUtil.getDocumentation(x)).isEqualTo("tab\tquote\"backslash\\hexA");
		assertThat(MofAnnotations.qualifier(x, "A")).isEqualTo("31");
		assertThat(MofAnnotations.qualifier(x, "B")).isEqualTo("5");
		assertThat(MofAnnotations.qualifier(x, "C")).isEqualTo("15");
		assertThat(MofAnnotations.qualifier(x, "D")).isEqualTo("-2.5");
		assertThat(MofAnnotations.qualifier(x, "E")).isEqualTo("c");
		assertThat(MofAnnotations.qualifier(x, "F")).isEqualTo("null");
		assertThat(MofAnnotations.qualifier(x, "G")).isEqualTo("true");
		assertThat(MofAnnotations.qualifier(x, "H")).isEqualTo("[1,2]");
		assertThat(MofAnnotations.qualifierValues(x, "H")).containsExactly("1", "2");
	}
}
