/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.emf.util.documentation.generators.apis;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIOException;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Covers the default methods of {@link EcoreToDocumentationService}, which every generator
 * component inherits: the in-memory generation a caller gets without a filesystem, and the file
 * path expressed in terms of it — where a generated document lands, how it is named, and that
 * concurrent callers do not tear each other's documents apart.
 *
 * <p>
 * The rendering is abstract here; the stub implementation only contributes a fixed document plus
 * the file extension and the mode-to-folder mapping a real component would.
 */
@DisplayName("EcoreToDocumentationService")
class EcoreToDocumentationServiceTest {

	private static final String DOCUMENTATION = "generated documentation";

	@TempDir
	Path outputRoot;

	private EcoreToDocumentationService service;
	private EPackage zoo;

	@BeforeEach
	void setUp() {
		service = new StubDocumentationService(DOCUMENTATION);
		zoo = zooPackage();
	}

	@Test
	@DisplayName("a package document is named after the package, in the folder for the mode")
	void writesPackageDocumentation() throws IOException {
		File file = service.generateOutputFile(zoo, DOCUMENTATION, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW,
				outputRoot.toString());

		assertThat(file).hasName("zoo.txt").hasContent(DOCUMENTATION);
		assertThat(file.toPath().getParent()).isEqualTo(outputRoot.resolve("plain"));
	}

	@Test
	@DisplayName("the mode decides the output folder")
	void modeDecidesOutputFolder() throws IOException {
		File file = service.generateOutputFile(zoo, DOCUMENTATION,
				EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM, outputRoot.toString());

		assertThat(file.toPath().getParent()).isEqualTo(outputRoot.resolve("plain_mermaid"));
	}

	@Test
	@DisplayName("a classifier document is named <package>_<classifier>")
	void writesClassifierDocumentation() throws IOException {
		EClassifier animal = zoo.getEClassifier("Animal");

		File file = service.generateOutputFile(animal, DOCUMENTATION,
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThat(file).hasName("zoo_Animal.txt").hasContent(DOCUMENTATION);
	}

	@Test
	@DisplayName("an enum is named the same way as a class")
	void writesEnumDocumentation() throws IOException {
		EClassifier food = zoo.getEClassifier("Food");

		File file = service.generateOutputFile(food, DOCUMENTATION,
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThat(file).hasName("zoo_Food.txt");
	}

	@Test
	@DisplayName("the deprecated EClass overload names the file like the EClassifier one")
	@SuppressWarnings("deprecation")
	void deprecatedEClassOverloadNamesFileAlike() throws IOException {
		EClass animal = (EClass) zoo.getEClassifier("Animal");

		File file = service.generateOutputFile(animal, DOCUMENTATION,
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThat(file).hasName("zoo_Animal.txt");
	}

	@Test
	@DisplayName("the output folders are created on the way")
	void createsMissingDirectories() throws IOException {
		Path root = outputRoot.resolve("does/not/exist/yet");

		File file = service.generateOutputFile(zoo, DOCUMENTATION, EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW,
				root.toString());

		assertThat(file).exists();
		assertThat(file.toPath().getParent()).isEqualTo(root.resolve("plain"));
	}

	@Test
	@DisplayName("re-generating replaces the previous document instead of appending to it")
	void replacesPreviousDocument() throws IOException {
		service.generateOutputFile(zoo, "a first, much longer run", EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW,
				outputRoot.toString());

		File file = service.generateOutputFile(zoo, "second run", EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW,
				outputRoot.toString());

		assertThat(file).hasContent("second run");
		assertThat(Files.list(outputRoot.resolve("plain"))).hasSize(1);
	}

	@Test
	@DisplayName("a root that is a file, not a directory, fails instead of silently writing nowhere")
	void failsOnUnusableOutputRoot() throws IOException {
		Path notADirectory = Files.createFile(outputRoot.resolve("file.txt"));

		assertThatIOException().isThrownBy(() -> service.generateOutputFile(zoo, DOCUMENTATION,
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, notADirectory.toString()));
	}

	@Test
	@DisplayName("a mode the generator cannot produce is rejected, naming the mode and the generator")
	void rejectsUnsupportedMode() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> service.requireSupported(EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW))
				.withMessageContaining("ONLY_HTML_CLASS_OVERVIEW").withMessageContaining("StubDocumentationService");
		assertThatCode(() -> service.requireSupported(EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW))
				.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("a package is generated in memory, without touching the filesystem")
	void generatesPackageInMemory() {
		CharSequence documentation = service.generateDocumentation(zoo,
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW);

		assertThat(documentation).asString().isEqualTo(DOCUMENTATION);
		assertThat(outputRoot).isEmptyDirectory();
	}

	@Test
	@DisplayName("a classifier is generated in memory, without touching the filesystem")
	void generatesClassifierInMemory() {
		CharSequence documentation = service.generateDocumentation(zoo.getEClassifier("Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW);

		assertThat(documentation).asString().isEqualTo(DOCUMENTATION);
		assertThat(outputRoot).isEmptyDirectory();
	}

	@Test
	@DisplayName("the file-writing methods write what the in-memory ones generate, and hand it back")
	void writesWhatItGenerates() throws IOException {
		OutputStream generated = service.doGenerateDocumentation(zoo,
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		Path written = outputRoot.resolve("plain").resolve("zoo.txt");
		assertThat(written).exists().content(UTF_8).isEqualTo(DOCUMENTATION);
		assertThat(generated).isInstanceOf(ByteArrayOutputStream.class);
		assertThat(((ByteArrayOutputStream) generated).toByteArray()).isEqualTo(DOCUMENTATION.getBytes(UTF_8));
	}

	@Test
	@DisplayName("a classifier document is written and handed back the same way")
	void writesClassifierDocumentAndHandsItBack() throws IOException {
		OutputStream generated = service.doGenerateDocumentation(zoo.getEClassifier("Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThat(outputRoot.resolve("plain").resolve("zoo_Animal.txt")).exists().content(UTF_8).isEqualTo(DOCUMENTATION);
		assertThat(generated).hasToString(DOCUMENTATION);
	}

	@Test
	@DisplayName("the deprecated EClass overload writes the same document as the EClassifier one")
	@SuppressWarnings("deprecation")
	void deprecatedEClassOverloadWritesAlike() throws IOException {
		OutputStream generated = service.doGenerateDocumentation((EClass) zoo.getEClassifier("Animal"),
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		assertThat(outputRoot.resolve("plain").resolve("zoo_Animal.txt")).exists().content(UTF_8).isEqualTo(DOCUMENTATION);
		assertThat(generated).hasToString(DOCUMENTATION);
	}

	@Test
	@DisplayName("non-ASCII documentation survives the file round trip")
	void writesNonAsciiAsUtf8() throws IOException {
		String documentation = "Übersicht – Tierpflegerin ☕ 😀 – 文档";
		EcoreToDocumentationService utf8Service = new StubDocumentationService(documentation);

		OutputStream generated = utf8Service.doGenerateDocumentation(zoo,
				EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());

		Path written = outputRoot.resolve("plain").resolve("zoo.txt");
		assertThat(Files.readString(written, UTF_8)).isEqualTo(documentation);
		assertThat(((ByteArrayOutputStream) generated).toByteArray()).isEqualTo(documentation.getBytes(UTF_8));
	}

	@Test
	@DisplayName("callers generating the same package concurrently all get a complete document")
	void concurrentGenerationOfTheSamePackage() throws Exception {
		int threads = 4;
		int rounds = 50;
		Callable<String> generate = () -> {
			OutputStream generated = service.doGenerateDocumentation(zoo,
					EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW, outputRoot.toString());
			return generated.toString();
		};
		List<Callable<String>> calls = new ArrayList<>();
		for (int i = 0; i < threads * rounds; i++) {
			calls.add(generate);
		}

		ExecutorService executor = Executors.newFixedThreadPool(threads);
		try {
			List<Future<String>> results = executor.invokeAll(calls);
			for (Future<String> result : results) {
				// get() rethrows whatever the call threw: a path that is deleted before it is
				// written again makes concurrent callers fail, or hands them an empty document
				assertThat(result.get()).isEqualTo(DOCUMENTATION);
			}
		} finally {
			executor.shutdown();
			executor.awaitTermination(30, TimeUnit.SECONDS);
		}

		assertThat(outputRoot.resolve("plain").resolve("zoo.txt")).content(UTF_8).isEqualTo(DOCUMENTATION);
	}

	private static EPackage zooPackage() {
		EcoreFactory factory = EcoreFactory.eINSTANCE;
		EPackage zoo = factory.createEPackage();
		zoo.setName("zoo");
		zoo.setNsPrefix("zoo");
		zoo.setNsURI("http://example.org/zoo");
		EClass animal = factory.createEClass();
		animal.setName("Animal");
		EEnum food = factory.createEEnum();
		food.setName("Food");
		zoo.getEClassifiers().add(animal);
		zoo.getEClassifiers().add(food);
		return zoo;
	}

	/**
	 * The minimum a component has to contribute: the document, the extension and the folder per
	 * mode. Everything else is inherited from the interface and is what these tests exercise.
	 */
	private static final class StubDocumentationService implements EcoreToDocumentationService {

		private final String documentation;

		StubDocumentationService(String documentation) {
			this.documentation = documentation;
		}

		@Override
		public boolean canHandleMediaType(String mediaType) {
			return "text/plain".equals(mediaType);
		}

		@Override
		public String getOutputFileExtension() {
			return ".txt";
		}

		@Override
		public Set<EcoreToDocumentationOptions> getSupportedModes() {
			return Set.of(EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW,
					EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM);
		}

		@Override
		public String getOutputFolder(EcoreToDocumentationOptions mode) {
			return mode == EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM ? "plain_mermaid" : "plain";
		}

		@Override
		public CharSequence generateDocumentation(EPackage ePackage, EcoreToDocumentationOptions mode) {
			return documentation;
		}

		@Override
		public CharSequence generateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions mode) {
			return documentation;
		}
	}
}
