/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.html;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pins the HTML page the generator produces for an {@link EPackage} and for a single classifier:
 * the page skeleton, the {@code md-block} sections carrying Markdown, and the Mermaid diagram
 * which — living inside HTML — has its angle brackets escaped.
 *
 * <p>
 * A generator instance keeps the diagram flavour of the last call, so every test starts from a
 * fresh one — which is also how the OSGi component uses it.
 */
@DisplayName("HTML documentation generator")
class HtmlCodeGenTest {

	private HtmlCodeGen codeGen;
	private EPackage zoo;

	@BeforeEach
	void setUp() {
		codeGen = new HtmlCodeGen();
		zoo = DocGenTestModel.zoo();
	}

	@Test
	@DisplayName("a package document is a complete HTML page pulling in mermaid and md-block")
	void rendersHtmlPage() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).startsWith("<html>").contains("mermaid.min.js").contains("md-block.js").contains("<body>")
				.contains("<md-block>").contains("</md-block>");
		assertThat(doc.trim()).endsWith("</body>\n</html>");
	}

	@Test
	@DisplayName("the markdown blocks carry the package, class and enumerator sections")
	void rendersSections() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).contains("# Package: zoo").contains("## Classes Overview").contains("### Class: Animal")
				.contains("## Enumerators Overview").contains("### Enumerator: Food");
	}

	@Test
	@DisplayName("attributes and references render as tables with type, bounds and documentation")
	void rendersFeatureTables() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).contains("| Name| Type| Bounds| Description|").contains("|name|EString|0..1|The display name.|")
				.contains("|age|EInt|1..1|None.|").contains("|keeper|Keeper|0..1|None.|");
		assertThat(doc).contains("| Literal| Description|").contains("|MEAT|None.|");
	}

	@Test
	@DisplayName("a class without features gets a None. placeholder instead of a table")
	void rendersPlaceholderForEmptyFeatureLists() {
		String doc = normalize(
				codeGen.generateDocumentation(DocGenTestModel.bare(), EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).contains("### Class: Empty").contains("#### Fields\n\nNone.")
				.contains("#### References\n\nNone.").contains("## Enumerators Overview");
	}

	@Test
	@DisplayName("the mermaid mode embeds the diagram in a div, with its angle brackets escaped")
	void mermaidModeEmbedsEscapedDiagram() {
		String doc = normalize(codeGen.generateDocumentation(zoo,
				EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM));

		assertThat(doc).contains("## Class Diagram").contains("<div class='mermaid'>").contains("classDiagram")
				.contains("Zoo --&gt \"1..*\" Animal : animals").contains("&lt&ltenumeration&gt&gt");
		// the diagram block keeps the indentation of the page template
		assertThat(raw(zoo, EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM))
				.contains("\t\t\t<div class='mermaid'>").contains("\t\t\t</div>");
		// the raw mermaid arrow would be read as markup by the browser
		assertThat(doc).doesNotContain("Zoo --> \"1..*\" Animal");
	}

	@Test
	@DisplayName("the overview-only mode embeds no class diagram at all")
	void overviewOnlyModeEmbedsNoDiagram() {
		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).doesNotContain("class='mermaid'").doesNotContain("classDiagram")
				.doesNotContain("## Class Diagram").doesNotContain("#### Class Diagram");
	}

	@Test
	@DisplayName("a reused generator drops the diagram of the previous call")
	void doesNotKeepDiagramOfPreviousCall() {
		codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM);

		String doc = normalize(codeGen.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).doesNotContain("class='mermaid'").doesNotContain("classDiagram")
				.doesNotContain("## Class Diagram");
	}

	@Test
	@DisplayName("a single EClass renders a page holding only that class' section")
	void rendersSingleClass() {
		String doc = normalize(codeGen.generateDocumentation(DocGenTestModel.classifier(zoo, "Animal"),
				EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).startsWith("<html>").contains("### Class: Animal").contains(DocGenTestModel.LONG_DOCUMENTATION)
				.doesNotContain("# Package:").doesNotContain("### Class: Zoo");
	}

	@Test
	@DisplayName("a single EEnum renders a page holding an enumerator section")
	void rendersSingleEnum() {
		String doc = normalize(codeGen.generateDocumentation(DocGenTestModel.classifier(zoo, "Food"),
				EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(doc).startsWith("<html>").contains("### Enumerator: Food").contains("|MEAT|None.|")
				.doesNotContain("### Class:");
	}

	@Test
	@DisplayName("the deprecated EClass overload renders the same page as the EClassifier one")
	@SuppressWarnings("deprecation")
	void deprecatedEClassOverloadMatchesClassifierOverload() {
		String viaEClass = normalize(codeGen.generateDocumentation(DocGenTestModel.eClass(zoo, "Animal"),
				EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));
		String viaEClassifier = normalize(new HtmlCodeGen().generateDocumentation(
				DocGenTestModel.classifier(zoo, "Animal"), EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW));

		assertThat(viaEClass).isEqualTo(viaEClassifier);
	}

	private static String raw(EPackage ePackage, EcoreToDocumentationOptions mode) {
		return new HtmlCodeGen().generateDocumentation(ePackage, mode).toString();
	}

	/** Collapses runs of spaces/tabs and trims line ends, so assertions ignore template indentation. */
	private static String normalize(CharSequence generated) {
		return generated.toString().replace("\r\n", "\n").replaceAll("[ \t]+", " ").replaceAll(" +\n", "\n");
	}
}
