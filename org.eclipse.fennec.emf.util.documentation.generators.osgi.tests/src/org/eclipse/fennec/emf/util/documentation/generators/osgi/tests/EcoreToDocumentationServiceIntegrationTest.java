/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.osgi.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Proves that the HTML and the Markdown component each contribute an
 * {@link EcoreToDocumentationService} to a running framework, that a caller can pick one by media
 * type, and that generating a document works there.
 *
 * <p>
 * The rendering itself is covered by the plain-JUnit tests of the generators; what only a
 * framework can show is the wiring: the components keep their generator stack (the Xtend runtime,
 * Guava, the Mermaid/PlantUML class-diagram generators) out of their own bundle and import it, so
 * a missing or mis-versioned import shows up here and nowhere else.
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@DisplayName("Ecore documentation generator OSGi registration")
public class EcoreToDocumentationServiceIntegrationTest {

	private Path outputRoot;

	@BeforeEach
	void setUp() throws IOException {
		outputRoot = Files.createTempDirectory("fennec-doc-generators");
	}

	@AfterEach
	void tearDown() throws IOException {
		if (outputRoot != null && Files.exists(outputRoot)) {
			try (var paths = Files.walk(outputRoot)) {
				paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
			}
		}
	}

	@Test
	@DisplayName("both generator components are registered, one per media type")
	void bothComponentsAreRegistered(
			@InjectService(cardinality = 2, timeout = 5000) List<EcoreToDocumentationService> services) {
		assertEquals(2, services.size(), "expected the HTML and the Markdown component");

		assertNotNull(serviceFor(services, "text/markdown"));
		assertNotNull(serviceFor(services, "text/html"));
		assertTrue(services.stream().noneMatch(service -> service.canHandleMediaType("application/pdf")),
				"no component should claim a media type it cannot render");
	}

	@Test
	@DisplayName("each component advertises what it can produce, and rejects the rest")
	void advertisesSupportedModes(
			@InjectService(cardinality = 2, timeout = 5000) List<EcoreToDocumentationService> services) {
		EcoreToDocumentationService html = serviceFor(services, "text/html");
		EcoreToDocumentationService markdown = serviceFor(services, "text/markdown");

		assertTrue(html.getSupportedModes().contains(EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM));
		assertFalse(html.getSupportedModes().contains(EcoreToDocumentationOptions.HTML_WITH_PLANTUML_CLASS_DIAGRAM),
				"HTML cannot render a PlantUML diagram, so it must not claim the mode");
		assertTrue(markdown.getSupportedModes().contains(EcoreToDocumentationOptions.MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM));

		// a caller asking for a mode the format cannot produce finds out, instead of being served a
		// document that silently lacks the diagram it asked for
		assertThrows(IllegalArgumentException.class, () -> html.generateDocumentation(zooPackage(),
				EcoreToDocumentationOptions.HTML_WITH_PLANTUML_CLASS_DIAGRAM));
	}

	@Test
	@DisplayName("a document is generated in memory, without an output folder")
	void generatesInMemory(
			@InjectService(cardinality = 2, timeout = 5000) List<EcoreToDocumentationService> services) {
		EPackage zoo = zooPackage();

		String markdown = serviceFor(services, "text/markdown")
				.generateDocumentation(zoo, EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM).toString();
		String html = serviceFor(services, "text/html")
				.generateDocumentation(zoo, EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW).toString();

		assertTrue(markdown.contains("# Package: zoo"), "expected the package heading");
		assertTrue(markdown.contains("```mermaid"), "expected an embedded mermaid diagram");
		assertTrue(html.startsWith("<html>"), "expected an HTML page");
		// a caller that only wants the bytes needs no writable directory at all
		assertTrue(isEmpty(outputRoot), "nothing should have been written");
	}

	@Test
	@DisplayName("the markdown component generates a document inside the framework")
	void markdownComponentGenerates(
			@InjectService(cardinality = 2, timeout = 5000) List<EcoreToDocumentationService> services)
			throws IOException {
		EcoreToDocumentationService markdown = serviceFor(services, "text/markdown");
		assertEquals(".md", markdown.getOutputFileExtension());

		OutputStream generated = markdown.doGenerateDocumentation(zooPackage(),
				EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM, outputRoot.toString());

		Path written = outputRoot
				.resolve(markdown.getOutputFolder(EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM))
				.resolve("zoo.md");
		assertTrue(Files.exists(written), () -> written + " should have been written");
		String content = Files.readString(written);
		assertTrue(content.contains("# Package: zoo"), "expected the package heading");
		// the embedded diagram proves the Mermaid generator is wired into the component's bundle
		assertTrue(content.contains("```mermaid"), "expected an embedded mermaid diagram");
		assertTrue(content.contains("### Class: Animal"), "expected the class section");
		assertEquals(content, generated.toString(), "the returned stream should carry the written document");
	}

	@Test
	@DisplayName("the html component generates a page inside the framework")
	void htmlComponentGenerates(
			@InjectService(cardinality = 2, timeout = 5000) List<EcoreToDocumentationService> services)
			throws IOException {
		EcoreToDocumentationService html = serviceFor(services, "text/html");
		assertEquals(".html", html.getOutputFileExtension());

		html.doGenerateDocumentation(zooPackage().getEClassifier("Animal"),
				EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW, outputRoot.toString());

		Path written = outputRoot.resolve(html.getOutputFolder(EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW))
				.resolve("zoo_Animal.html");
		assertTrue(Files.exists(written), () -> written + " should have been written");
		String content = Files.readString(written);
		assertTrue(content.startsWith("<html>"), "expected an HTML page");
		assertTrue(content.contains("### Class: Animal"), "expected the class section");
		assertFalse(content.contains("class='mermaid'"), "the overview-only mode embeds no diagram");
	}

	private static boolean isEmpty(Path directory) {
		try (var entries = Files.list(directory)) {
			return entries.findAny().isEmpty();
		} catch (IOException e) {
			throw new AssertionError("could not read " + directory, e);
		}
	}

	private static EcoreToDocumentationService serviceFor(List<EcoreToDocumentationService> services, String mediaType) {
		return services.stream().filter(service -> service.canHandleMediaType(mediaType)).findFirst()
				.orElseThrow(() -> new AssertionError("no EcoreToDocumentationService for " + mediaType));
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
