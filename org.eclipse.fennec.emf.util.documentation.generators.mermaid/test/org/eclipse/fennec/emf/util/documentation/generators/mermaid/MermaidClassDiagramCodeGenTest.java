/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.mermaid;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pins the Mermaid class-diagram syntax the generator emits for an {@link EPackage} and for a
 * single classifier. Assertions run against a whitespace-normalized rendering (runs of spaces and
 * tabs collapsed): the indentation of the Xtend templates is cosmetic for Mermaid, the tokens and
 * their order are not.
 */
@DisplayName("Mermaid class diagram generator")
class MermaidClassDiagramCodeGenTest {

	private MermaidClassDiagramCodeGen codeGen;
	private EPackage zoo;

	@BeforeEach
	void setUp() {
		codeGen = new MermaidClassDiagramCodeGen();
		zoo = DocGenTestModel.zoo();
	}

	@Test
	@DisplayName("a package diagram opens with the classDiagram keyword")
	void packageDiagramStartsWithClassDiagram() {
		assertThat(normalize(codeGen.generateClassDiagram(zoo))).startsWith("classDiagram");
	}

	@Test
	@DisplayName("a class renders with its attributes, typed by the attribute's type name")
	void rendersClassWithAttributes() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains(" class Animal {").contains(" EInt age").contains(" Food food")
				.contains(" class Named {").contains(" EString name");
	}

	@Test
	@DisplayName("abstract classes and interfaces carry the matching stereotype")
	void rendersAbstractAndInterfaceStereotypes() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains("class Feedable {\n <<interface>>").contains("class Named {\n <<abstract>>");
		// Animal is neither, so it gets no stereotype
		assertThat(diagram).contains("class Animal {\n EInt age");
	}

	@Test
	@DisplayName("references render as associations carrying the multiplicity")
	void rendersReferencesWithMultiplicity() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		// 0..* collapses to *, 1..* keeps its lower bound, anything else is spelled out
		assertThat(diagram).contains("Animal --> \"*\" Toy : toys").contains("Zoo --> \"1..*\" Animal : animals");
	}

	@Test
	@DisplayName("a reference into another package renders as a dependency, not an association")
	void crossPackageReferenceRendersAsDependency() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains("Zoo ..> \"0..1\" Keeper : keeper");
	}

	@Test
	@DisplayName("super types render as dependencies from the sub class")
	void rendersSuperTypes() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains("Animal ..> Named").contains("Animal ..> Feedable");
	}

	@Test
	@DisplayName("an enum renders with the enumeration stereotype and its literals")
	void rendersEnumWithLiterals() {
		String diagram = normalize(codeGen.generateClassDiagram(zoo));

		assertThat(diagram).contains("class Food {\n <<enumeration>>\n MEAT\n PLANTS\n}");
	}

	@Test
	@DisplayName("a single EClassifier renders stand-alone, headed by the classDiagram keyword")
	void rendersSingleClassifier() {
		String diagram = normalize(codeGen.generateClassDiagram(DocGenTestModel.classifier(zoo, "Animal")));

		assertThat(diagram).startsWith("classDiagram").contains(" class Animal {").contains(" EInt age")
				.doesNotContain("class Zoo");
	}

	@Test
	@DisplayName("a single EEnum renders as an enumeration")
	void rendersSingleEnum() {
		String diagram = normalize(codeGen.generateClassDiagram(DocGenTestModel.classifier(zoo, "Food")));

		assertThat(diagram).startsWith("classDiagram").contains("class Food {\n <<enumeration>>\n MEAT");
	}

	@Test
	@DisplayName("the deprecated EClass overload renders the same diagram as the EClassifier one")
	void deprecatedEClassOverloadMatchesClassifierOverload() {
		String viaEClass = normalize(codeGen.generateClassDiagram(DocGenTestModel.eClass(zoo, "Animal")));
		String viaEClassifier = normalize(codeGen.generateClassDiagram(DocGenTestModel.classifier(zoo, "Animal")));

		assertThat(viaEClass).isEqualTo(viaEClassifier);
	}

	@Test
	@DisplayName("a class without attributes still renders a body")
	void rendersClassWithoutAttributes() {
		String diagram = normalize(codeGen.generateClassDiagram(DocGenTestModel.bare()));

		assertThat(diagram).startsWith("classDiagram").contains(" class Empty {");
	}

	/** Collapses runs of spaces/tabs and trims line ends, so assertions ignore template indentation. */
	private static String normalize(CharSequence generated) {
		return generated.toString().replace("\r\n", "\n").replaceAll("[ \t]+", " ").replaceAll(" +\n", "\n");
	}
}
