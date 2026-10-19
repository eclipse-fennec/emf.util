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
package org.eclipse.fennec.emf.util.documentation.generators.html.component;

import java.util.Set;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService;
import org.eclipse.fennec.emf.util.documentation.generators.html.HtmlCodeGen;
import org.osgi.service.component.annotations.Component;

@Component(name="EcoreToHtmlComponent", service=EcoreToDocumentationService.class)
public class EcoreToHtmlComponent implements EcoreToDocumentationService {
	
	private static final String HTML_FILE_EXTENSION = ".html";
	private static final String HTML_OUTPUT_FOLDER = "html";
	private static final String HTML_WITH_MERMAID_OUTPUT_FOLDER = "html_mermaid";
	private static final String HTML_MEDIA_TYPE = "text/html";
	/**
	 * PlantUML is deliberately absent: a PlantUML class diagram is source the browser does nothing
	 * with, and rendering it would need a renderer this page does not load. Asking for it is
	 * rejected rather than answered with a diagram-less document.
	 */
	private static final Set<EcoreToDocumentationOptions> SUPPORTED_MODES = Set.of(
			EcoreToDocumentationOptions.ONLY_HTML_CLASS_OVERVIEW,
			EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM);

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#canHandleMediaType(java.lang.String)
	 */
	@Override
	public boolean canHandleMediaType(String mediaType) {
		return HTML_MEDIA_TYPE.equals(mediaType);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#getOutputFileExtension()
	 */
	@Override
	public String getOutputFileExtension() {
		return HTML_FILE_EXTENSION;
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
		case HTML_WITH_MERMAID_CLASS_DIAGRAM:
			return HTML_WITH_MERMAID_OUTPUT_FOLDER;
		case ONLY_HTML_CLASS_OVERVIEW: default:
			return HTML_OUTPUT_FOLDER;
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#generateDocumentation(org.eclipse.emf.ecore.EPackage, org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions)
	 */
	@Override
	public CharSequence generateDocumentation(EPackage ePackage, EcoreToDocumentationOptions mode) {
		requireSupported(mode);
		return new HtmlCodeGen().generateDocumentation(ePackage, mode);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationService#generateDocumentation(org.eclipse.emf.ecore.EClassifier, org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions)
	 */
	@Override
	public CharSequence generateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions mode) {
		requireSupported(mode);
		return new HtmlCodeGen().generateDocumentation(eClassifier, mode);
	}

}
