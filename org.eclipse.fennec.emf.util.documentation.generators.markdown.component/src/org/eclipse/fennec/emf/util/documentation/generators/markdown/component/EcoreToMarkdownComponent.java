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
package org.eclipse.fennec.emf.util.documentation.generators.markdown.component;

import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService;
import org.eclipse.fennec.emf.util.documentation.generators.markdown.MarkdownCodeGen;
import org.osgi.service.component.annotations.Component;

/**
 * 
 * @author ilenia
 * @since Oct 19, 2022
 */
@Component(name="EcoreToMarkdownComponent", service=EcoreToDocumentationService.class)
public class EcoreToMarkdownComponent implements EcoreToDocumentationService {
	
	private static final String MD_FILE_EXTENSION = ".md";
	private static final String MD_OUTPUT_FOLDER = "md";
	private static final String MD_WITH_MERMAID_OUTPUT_FOLDER = "md_mermaid";
	private static final String MD_WITH_PLANTUML_OUTPUT_FOLDER = "md_plantuml";
	private static final String MD_MEDIA_TYPE = "text/markdown";
	private static final Set<EcoreToDocumentationOptions> SUPPORTED_MODES = Set.of(
			EcoreToDocumentationOptions.ONLY_MARKDOWN_CLASS_OVERVIEW,
			EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM,
			EcoreToDocumentationOptions.MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM);
	

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#canHandleMediaType(java.lang.String)
	 */
	@Override
	public boolean canHandleMediaType(String mediaType) {
		return MD_MEDIA_TYPE.equals(mediaType);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#getOutputFileExtension()
	 */
	@Override
	public String getOutputFileExtension() {
		return MD_FILE_EXTENSION;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#getSupportedModes()
	 */
	@Override
	public Set<EcoreToDocumentationOptions> getSupportedModes() {
		return SUPPORTED_MODES;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#getOutputFolder(org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions)
	 */
	@Override
	public String getOutputFolder(EcoreToDocumentationOptions mode) {
		switch(mode) {
		case MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM:
			return MD_WITH_MERMAID_OUTPUT_FOLDER;
		case MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM:
			return MD_WITH_PLANTUML_OUTPUT_FOLDER;
		case ONLY_MARKDOWN_CLASS_OVERVIEW: default:
			return MD_OUTPUT_FOLDER;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#generateDocumentation(org.eclipse.emf.ecore.EPackage, org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions)
	 */
	@Override
	public CharSequence generateDocumentation(EPackage ePackage, EcoreToDocumentationOptions mode) {
		requireSupported(mode);
		return new MarkdownCodeGen().generateDocumentation(ePackage, mode);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#generateDocumentation(org.eclipse.emf.ecore.EClassifier, org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions)
	 */
	@Override
	public CharSequence generateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions mode) {
		requireSupported(mode);
		return new MarkdownCodeGen().generateDocumentation(eClassifier, mode);
	}

}
