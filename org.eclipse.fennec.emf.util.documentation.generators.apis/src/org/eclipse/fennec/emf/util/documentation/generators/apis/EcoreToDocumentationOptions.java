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

/**
 * 
 * @author ilenia
 * @since Oct 18, 2022
 */
public enum EcoreToDocumentationOptions {
	
	MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM,
	MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM,
	HTML_WITH_MERMAID_CLASS_DIAGRAM,
	HTML_WITH_PLANTUML_CLASS_DIAGRAM,
	ONLY_MARKDOWN_CLASS_OVERVIEW,
	ONLY_HTML_CLASS_OVERVIEW;

}
