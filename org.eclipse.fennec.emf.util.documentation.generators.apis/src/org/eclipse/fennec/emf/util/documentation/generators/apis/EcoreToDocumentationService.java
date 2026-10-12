/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.util.documentation.generators.apis;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Renders an Ecore model as documentation in one format (one implementation per media type).
 *
 * <p>
 * An implementation contributes the format: the media type it answers for, the file extension and
 * output folder it uses, and the two {@code generateDocumentation} methods that produce the
 * documentation itself. Everything about files is handled here, in terms of those methods, so a
 * caller that only wants the document — a request handler, say — can take it without a writable
 * directory anywhere in sight.
 */
@ProviderType
public interface EcoreToDocumentationService{

	boolean canHandleMediaType(String mediaType);
	String getOutputFileExtension();
	String getOutputFolder(EcoreToDocumentationOptions mode);

	/**
	 * The modes this generator can produce. {@link EcoreToDocumentationOptions} spans every format,
	 * so each generator implements a subset of it; a caller can ask here instead of guessing which
	 * combination of format and class diagram a given media type supports.
	 * 
	 * @return the supported modes, never empty
	 */
	Set<EcoreToDocumentationOptions> getSupportedModes();

	/**
	 * Rejects a mode this generator cannot produce, rather than quietly generating something else.
	 * Implementations call this before generating.
	 * 
	 * @param mode the requested mode
	 * @throws IllegalArgumentException if {@link #getSupportedModes()} does not contain it
	 */
	default void requireSupported(EcoreToDocumentationOptions mode) {
		if (!getSupportedModes().contains(mode)) {
			throw new IllegalArgumentException(
					mode + " is not supported by " + getClass().getSimpleName() + ", supported are " + getSupportedModes());
		}
	}

	/**
	 * Generates the documentation of a whole package and returns it, without touching the
	 * filesystem.
	 * 
	 * @param ePackage the package to document
	 * @param mode the output mode, which decides whether and how a class diagram is embedded
	 * @return the generated documentation
	 * @throws IllegalArgumentException if the mode is not one of {@link #getSupportedModes()}
	 */
	CharSequence generateDocumentation(EPackage ePackage, EcoreToDocumentationOptions mode);

	/**
	 * Generates the documentation of a single classifier and returns it, without touching the
	 * filesystem.
	 * 
	 * @param eClassifier the classifier to document
	 * @param mode the output mode, which decides whether and how a class diagram is embedded
	 * @return the generated documentation
	 * @throws IllegalArgumentException if the mode is not one of {@link #getSupportedModes()}
	 */
	CharSequence generateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions mode);

	/**
	 * Generates the documentation of a package and writes it to a file below {@code
	 * outputFolderRoot}.
	 * 
	 * @return the generated documentation, as a {@link ByteArrayOutputStream} of UTF-8 bytes
	 * @throws IOException if the file cannot be written
	 */
	default OutputStream doGenerateDocumentation(EPackage ePackage, EcoreToDocumentationOptions mode, String outputFolderRoot) throws IOException {
		CharSequence generatedDoc = generateDocumentation(ePackage, mode);
		generateOutputFile(ePackage, generatedDoc, mode, outputFolderRoot);
		return toOutputStream(generatedDoc);
	}
	
	/**
	 * @param eClass
	 * @param mode
	 * @param outputFolderRoot
	 * @return
	 * @throws IOException
	 * @deprecated use {@link doGenerateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions mode, String outputFolderRoot)} instead
	 */
	@Deprecated
	default OutputStream doGenerateDocumentation(EClass eClass, EcoreToDocumentationOptions mode, String outputFolderRoot) throws IOException {
		return doGenerateDocumentation((EClassifier) eClass, mode, outputFolderRoot);
	}
	
	/**
	 * Generates the documentation of a classifier and writes it to a file below {@code
	 * outputFolderRoot}.
	 * 
	 * @return the generated documentation, as a {@link ByteArrayOutputStream} of UTF-8 bytes
	 * @throws IOException if the file cannot be written
	 */
	default OutputStream doGenerateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions mode, String outputFolderRoot) throws IOException {
		CharSequence generatedDoc = generateDocumentation(eClassifier, mode);
		generateOutputFile(eClassifier, generatedDoc, mode, outputFolderRoot);
		return toOutputStream(generatedDoc);
	}

	default File generateOutputFile(EPackage ePackage, CharSequence generatedDoc, EcoreToDocumentationOptions mode, String outputFolderRoot) throws IOException {
		String outputFileName = ePackage.getName().concat(getOutputFileExtension());
		return doCreateOutputFile(outputFileName, generatedDoc, mode, outputFolderRoot);
	}
	
	/**
	 * @param eClass
	 * @param generatedDoc
	 * @param mode
	 * @param outputFolderRoot
	 * @return
	 * @throws IOException
	 * @deprecated use {@link generateOutputFile(EClassifier eClassifier, CharSequence generatedDoc, EcoreToDocumentationOptions mode, String outputFolderRoot) } instead
	 */
	@Deprecated
	default File generateOutputFile(EClass eClass, CharSequence generatedDoc, EcoreToDocumentationOptions mode, String outputFolderRoot) throws IOException {
		return generateOutputFile((EClassifier) eClass, generatedDoc, mode, outputFolderRoot);
	}
	
	default File generateOutputFile(EClassifier eClassifier, CharSequence generatedDoc, EcoreToDocumentationOptions mode, String outputFolderRoot) throws IOException {
		String outputFileName = eClassifier.getEPackage().getName().concat("_").concat(eClassifier.getName()).concat(getOutputFileExtension());
		return doCreateOutputFile(outputFileName, generatedDoc, mode, outputFolderRoot);
	}
	
	default File doCreateOutputFile(String outputFileName, CharSequence generatedDoc, EcoreToDocumentationOptions mode, String outputFolderRoot) throws IOException {
		Path outputPath = Paths.get(outputFolderRoot, getOutputFolder(mode), outputFileName);
		Files.createDirectories(outputPath.getParent());
		// One call, so that a document is replaced rather than first unlinked and created again:
		// two callers generating the same package would otherwise race over a path that is briefly
		// gone. The charset is pinned because the document is read back as bytes.
		Files.writeString(outputPath, generatedDoc, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
				StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
		return outputPath.toFile();
	}

	private static OutputStream toOutputStream(CharSequence generatedDoc) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.writeBytes(generatedDoc.toString().getBytes(StandardCharsets.UTF_8));
		return outputStream;
	}

}
