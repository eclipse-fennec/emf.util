/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.markdown;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pins the Markdown document the generator produces for an {@link EPackage} and for a single
 * classifier: the section structure, the field/reference/literal tables, the documentation taken
 * from the {@code documentation} annotation, and which class-diagram flavour each
 * {@link EcoreToDocumentationOptions} mode embeds.
 *
 * <p>
 * A generator instance keeps the diagram flavour of the last call, so every test starts from a
 * fresh one — which is also how the OSGi components use it.
 */
@DisplayName("Markdown documentation generator")
class MarkdownCodeGenTest {

	private MarkdownCodeGen codeGen;
	private EPackage zoo;

	@BeforeEach
	void setUp() {
		codeGen = new MarkdownCodeGen();
		zoo = DocGenTestModel.zoo();
	}

	@Test
	@DisplayName("a package document is headed by the package name and the overview sections")
	void packageDocumentHasSections() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).startsWith("# Package: zoo").contains("## Classes Overview").contains("### Class: Animal")
				.contains("### Class: Zoo").contains("## Enumerators Overview").contains("### Enumerator: Food");
	}

	@Test
	@DisplayName("attributes and references render as tables with type, bounds and documentation")
	void rendersFeatureTables() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).contains("| Name| Type| Bounds| Description|").contains("|name|EString|0..1|The display name.|")
				.contains("|age|EInt|1..1|None.|").contains("|animals|Animal|1..-1|None.|")
				.contains("|keeper|Keeper|0..1|None.|");
	}

	@Test
	@DisplayName("a class without features gets a None. placeholder instead of a table")
	void rendersPlaceholderForEmptyFeatureLists() {
		String doc = normalize(codeGen.generateDocumentation(DocGenTestModel.bare(),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).contains("### Class: Empty");
		assertThat(doc).contains("#### Fields\n\nNone.").contains("#### References\n\nNone.");
	}

	@Test
	@DisplayName("a package without enums says so in the enumerators section")
	void rendersPlaceholderForEmptyEnumeratorSection() {
		String doc = normalize(codeGen.generateDocumentation(DocGenTestModel.bare(),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).contains("## Enumerators Overview\n\nNone.");
	}

	@Test
	@DisplayName("an enum renders its documentation and a table of its literals")
	void rendersEnumerator() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).contains("### Enumerator: Food").contains("What an animal eats.")
				.contains("| Literal| Description|").contains("|MEAT|None.|").contains("|PLANTS|None.|");
	}

	@Test
	@DisplayName("the mermaid mode embeds the class diagram in a mermaid fence")
	void mermaidModeEmbedsMermaidDiagram() {
		String doc = normalize(codeGen.generateDocumentation(zoo,
				EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM));

		assertThat(doc).contains("## Class Diagram").contains("```mermaid").contains("classDiagram")
				.contains("Zoo --> \"1..*\" Animal : animals").doesNotContain("@startuml");
		// every class section repeats its own diagram
		assertThat(doc).contains("#### Class Diagram");
	}

	@Test
	@DisplayName("the plantuml mode embeds the class diagram in a plantuml fence")
	void plantumlModeEmbedsPlantumlDiagram() {
		String doc = normalize(codeGen.generateDocumentation(zoo,
				EcoreToDocumentationOptions.MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM));

		assertThat(doc).contains("```plantuml").contains("@startuml").contains("@enduml").doesNotContain("classDiagram");
	}

	@Test
	@DisplayName("the overview-only mode embeds no class diagram at all")
	void overviewOnlyModeEmbedsNoDiagram() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).doesNotContain("```").doesNotContain("## Class Diagram").doesNotContain("#### Class Diagram");
	}

	@Test
	@DisplayName("a reused generator drops the diagram of the previous call")
	void doesNotKeepDiagramOfPreviousCall() {
		codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM);

		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).doesNotContain("```").doesNotContain("## Class Diagram").doesNotContain("classDiagram");
	}

	@Test
	@DisplayName("a reused generator switches the diagram flavour with the mode")
	void switchesDiagramFlavourWithTheMode() {
		codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM);

		String doc = normalize(
				codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM));

		assertThat(doc).contains("```plantuml").contains("@startuml").doesNotContain("```mermaid")
				.doesNotContain("classDiagram");
	}

	@Test
	@DisplayName("a single EClass renders only that class' section")
	void rendersSingleClass() {
		String doc = normalize(codeGen.generateDocumentation(DocGenTestModel.classifier(zoo, "Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).contains("### Class: Animal").contains(DocGenTestModel.LONG_DOCUMENTATION)
				.contains("|age|EInt|1..1|None.|").doesNotContain("# Package:").doesNotContain("### Class: Zoo");
	}

	@Test
	@DisplayName("a single EEnum renders an enumerator section")
	void rendersSingleEnum() {
		String doc = normalize(codeGen.generateDocumentation(DocGenTestModel.classifier(zoo, "Food"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(doc).contains("### Enumerator: Food").contains("|MEAT|None.|").doesNotContain("### Class:");
	}

	@Test
	@DisplayName("the deprecated EClass overload renders the same section as the EClassifier one")
	void deprecatedEClassOverloadMatchesClassifierOverload() {
		String viaEClass = normalize(codeGen.generateDocumentation(DocGenTestModel.eClass(zoo, "Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));
		String viaEClassifier = normalize(new MarkdownCodeGen().generateDocumentation(
				DocGenTestModel.classifier(zoo, "Animal"), EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW));

		assertThat(viaEClass).isEqualTo(viaEClassifier);
	}

	/** Collapses runs of spaces/tabs and trims line ends, so assertions ignore template indentation. */
	private static String normalize(CharSequence generated) {
		return generated.toString().replace("\r\n", "\n").replaceAll("[ \t]+", " ").replaceAll(" +\n", "\n");
	}
}
