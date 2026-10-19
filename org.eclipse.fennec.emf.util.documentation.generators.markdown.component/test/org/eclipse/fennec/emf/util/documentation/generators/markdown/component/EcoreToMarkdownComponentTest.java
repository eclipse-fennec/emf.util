/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.markdown.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Covers the Markdown component's half of the {@code EcoreToDocumentationService} contract: the
 * media type it answers for, the file extension and the mode-to-folder mapping, and that
 * generating actually writes the document and hands back its bytes. The Markdown itself is pinned
 * by the generator's own tests.
 */
@DisplayName("EcoreToMarkdownComponent")
class EcoreToMarkdownComponentTest {

	@TempDir
	Path outputRoot;

	private EcoreToMarkdownComponent component;
	private EPackage zoo;

	@BeforeEach
	void setUp() {
		component = new EcoreToMarkdownComponent();
		zoo = zooPackage();
	}

	@Test
	@DisplayName("answers for text/markdown only")
	void handlesMarkdownMediaType() {
		assertThat(component.canHandleMediaType("text/markdown")).isTrue();
		assertThat(component.canHandleMediaType("text/html")).isFalse();
		assertThat(component.canHandleMediaType("")).isFalse();
		assertThat(component.canHandleMediaType(null)).isFalse();
	}

	@Test
	@DisplayName("writes .md files")
	void usesMarkdownExtension() {
		assertThat(component.getOutputFileExtension()).isEqualTo(".md");
	}

	@ParameterizedTest(name = "{0} -> {1}")
	@DisplayName("every supported mode maps to an output folder of its own")
	@CsvSource({ "MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM, md_mermaid", "MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM, md_plantuml",
			"ONLY_MARKDOWN_CLASS_OVERVIEW, md" })
	void mapsModeToOutputFolder(EcoreToDocumentationOptions mode, String folder) {
		assertThat(component.getOutputFolder(mode)).isEqualTo(folder);
	}

	@Test
	@DisplayName("advertises the modes it can produce")
	void advertisesSupportedModes() {
		assertThat(component.getSupportedModes()).containsExactlyInAnyOrder(EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW,
				EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM,
				EcoreToDocumentationOptions.MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM);
	}

	@Test
	@DisplayName("a mode it cannot produce is rejected, not answered with a document missing the diagram")
	void rejectsUnsupportedModes() {
		for (EcoreToDocumentationOptions mode : EcoreToDocumentationOptions.values()) {
			if (component.getSupportedModes().contains(mode)) {
				continue;
			}
			assertThatIllegalArgumentException().as("package, %s", mode)
					.isThrownBy(() -> component.generateDocumentation(zoo, mode));
			assertThatIllegalArgumentException().as("classifier, %s", mode)
					.isThrownBy(() -> component.generateDocumentation(zoo.getEClassifier("Animal"), mode));
			assertThatIllegalArgumentException().as("file, %s", mode)
					.isThrownBy(() -> component.doGenerateDocumentation(zoo, mode, outputRoot.toString()));
		}
		assertThat(outputRoot).isEmptyDirectory();
	}

	@Test
	@DisplayName("a rejected mode cannot overwrite the document of a mode sharing its folder")
	void rejectedModeLeavesTheExistingDocumentAlone() throws IOException {
		component.doGenerateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThatIllegalArgumentException().isThrownBy(
				() -> component.doGenerateDocumentation(zoo, EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM, outputRoot.toString()));

		assertThat(outputRoot.resolve("md").resolve("zoo.md")).exists().content()
				.contains("### Class: Animal");
		assertThat(Files.list(outputRoot.resolve("md"))).hasSize(1);
	}

	@Test
	@DisplayName("a package is generated in memory, without touching the filesystem")
	void generatesPackageInMemory() {
		CharSequence documentation = component.generateDocumentation(zoo,
				EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM);

		assertThat(documentation).asString().contains("# Package: zoo").contains("```mermaid")
				.contains("### Class: Animal");
		assertThat(outputRoot).isEmptyDirectory();
	}

	@Test
	@DisplayName("a classifier is generated in memory, without touching the filesystem")
	void generatesClassifierInMemory() {
		CharSequence documentation = component.generateDocumentation(zoo.getEClassifier("Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW);

		assertThat(documentation).asString().contains("### Class: Animal").doesNotContain("```");
		assertThat(outputRoot).isEmptyDirectory();
	}

	@Test
	@DisplayName("generating a package writes the document and returns its content")
	void generatesPackageDocumentation() throws IOException {
		OutputStream generated = component.doGenerateDocumentation(zoo,
				EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM, outputRoot.toString());

		Path written = outputRoot.resolve("md_mermaid").resolve("zoo.md");
		assertThat(written).exists().content().contains("# Package: zoo").contains("```mermaid")
				.contains("### Class: Animal");
		assertThat(generated.toString()).isEqualTo(Files.readString(written));
		// the file is what in-memory generation produces, written out
		assertThat(Files.readString(written)).isEqualTo(component
				.generateDocumentation(zoo, EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM).toString());
	}

	@Test
	@DisplayName("generating a classifier writes a document of its own")
	void generatesClassifierDocumentation() throws IOException {
		component.doGenerateDocumentation(zoo.getEClassifier("Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThat(outputRoot.resolve("md").resolve("zoo_Animal.md")).exists().content().contains("### Class: Animal")
				.doesNotContain("```");
	}

	@Test
	@DisplayName("the deprecated EClass overload writes the same document")
	@SuppressWarnings("deprecation")
	void generatesClassDocumentationThroughDeprecatedOverload() throws IOException {
		component.doGenerateDocumentation((EClass) zoo.getEClassifier("Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThat(outputRoot.resolve("md").resolve("zoo_Animal.md")).exists().content().contains("### Class: Animal");
	}

	private static EPackage zooPackage() {
		EcoreFactory factory = EcoreFactory.eINSTANCE;
		EPackage zoo = factory.createEPackage();
		zoo.setName("zoo");
		zoo.setNsPrefix("zoo");
		zoo.setNsURI("http://example.org/zoo");
		EClass animal = factory.createEClass();
		animal.setName("Animal");
		EAttribute name = factory.createEAttribute();
		name.setName("name");
		name.setEType(EcorePackage.Literals.ESTRING);
		animal.getEStructuralFeatures().add(name);
		zoo.getEClassifiers().add(animal);
		return zoo;
	}
}
