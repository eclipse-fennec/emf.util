/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.plantuml;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pins the PlantUML syntax the generator emits for an {@link EPackage} and for a single
 * classifier. Assertions run against a whitespace-normalized rendering (runs of spaces and tabs
 * collapsed): the indentation of the Xtend templates is cosmetic for PlantUML, the tokens and
 * their order are not.
 */
@DisplayName("PlantUML class diagram generator")
class PlantumlClassDiagramCodeGenTest {

	private PlantumlClassDiagramCodeGen codeGen;
	private EPackage zoo;

	@BeforeEach
	void setUp() {
		codeGen = new PlantumlClassDiagramCodeGen();
		zoo = DocGenTestModel.zoo();
	}

	@Test
	@DisplayName("a package diagram is wrapped in @startuml/@enduml")
	void packageDiagramIsWrapped() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).startsWith("@startuml");
		assertThat(diagram.trim()).endsWith("@enduml");
	}

	@Test
	@DisplayName("classes, abstract classes and interfaces use the matching keyword")
	void rendersClassifierKeywords() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains(" class Zoo {").contains(" abstract class Named {")
				.contains(" interface Feedable {");
	}

	@Test
	@DisplayName("attributes render as name : type members")
	void rendersAttributes() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains(" age : EInt").contains(" food : Food").contains(" name : EString");
	}

	@Test
	@DisplayName("references render as associations carrying the multiplicity")
	void rendersReferencesWithMultiplicity() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		// 0..* collapses to *, 1..* keeps its lower bound, anything else is spelled out
		assertThat(diagram).contains("Animal --> \"*\" Toy : toys").contains("Zoo --> \"1..*\" Animal : animals");
	}

	@Test
	@DisplayName("a reference into another package renders a grey package box plus a dependency")
	void crossPackageReferenceRendersPackageBox() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains(" package \"" + DocGenTestModel.STAFF_NS_URI + "\" #DDDDDD {\n Keeper : keeper\n}")
				.contains("Zoo ..> \"0..1\" Keeper : keeper");
	}

	@Test
	@DisplayName("super types render as dependencies from the sub class")
	void rendersSuperTypes() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains("Animal ..> Named").contains("Animal ..> Feedable");
	}

	@Test
	@DisplayName("an enum renders with its literals")
	void rendersEnumWithLiterals() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains(" enum Food {\n MEAT\n PLANTS\n}");
	}

	@Test
	@DisplayName("documented classifiers get a note, undocumented ones do not")
	void rendersDocumentationAsNote() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains("note top of Food\n What an animal eats.\nend note").contains("note top of Animal");
		assertThat(diagram).doesNotContain("note top of Zoo").doesNotContain("note top of Toy");
	}

	@Test
	@DisplayName("a documentation longer than 30 characters is wrapped across lines")
	void wrapsLongDocumentation() {
		String note = noteOf(normalize(codeGen.generateClassDiagram(zoo)), "Animal");

		assertThat(note.lines()).hasSizeGreaterThan(1);
		// all the words survive the wrapping, only the line breaks are added
		assertThat(note.replaceAll("\\s+", " ").trim()).isEqualTo(DocGenTestModel.LONG_DOCUMENTATION);
	}

	@Test
	@DisplayName("a single EClassifier renders as a diagram of its own")
	void rendersSingleClassifier() {
		String diagram = normalize(codeGen.generateClassDiagram(DocGenTestModel.classifier(zoo, "Animal")));

		assertThat(diagram).startsWith("@startuml").contains(" class Animal {").contains(" age : EInt")
				.doesNotContain("class Zoo");
		assertThat(diagram.trim()).endsWith("@enduml");
	}

	@Test
	@DisplayName("a single EEnum renders as an enum")
	void rendersSingleEnum() {
		String diagram = normalize(codeGen.generateClassDiagram(DocGenTestModel.classifier(zoo, "Food")));

		assertThat(diagram).contains(" enum Food {\n MEAT\n PLANTS\n}");
	}

	@Test
	@DisplayName("the deprecated EClass overload renders the same diagram as the EClassifier one")
	void deprecatedEClassOverloadMatchesClassifierOverload() {
		String viaEClass = normalize(codeGen.generateClassDiagram(DocGenTestModel.eClass(zoo, "Animal")));
		String viaEClassifier = normalize(codeGen.generateClassDiagram(DocGenTestModel.classifier(zoo, "Animal")));

		assertThat(viaEClass).isEqualTo(viaEClassifier);
	}

	/** @return the body of the {@code note top of <name>} block, without its delimiters */
	private static String noteOf(String diagram, String classifierName) {
		int start = diagram.indexOf("note top of " + classifierName);
		assertThat(start).as("note for %s", classifierName).isNotNegative();
		start = diagram.indexOf('\n', start) + 1;
		return diagram.substring(start, diagram.indexOf("end note", start));
	}

	/** Collapses runs of spaces/tabs and trims line ends, so assertions ignore template indentation. */
	private static String normalize(CharSequence generated) {
		return generated.toString().replace("\r\n", "\n").replaceAll("[ \t]+", " ").replaceAll(" +\n", "\n");
	}
}
