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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;

/**
 * 
 * @author ilenia
 * @since Oct 19, 2022
 */
public interface EcoreToDocumentationCodeGenerator {
	
	CharSequence generateDocumentation(EPackage ePackage, EcoreToDocumentationOptions docGenOption);
		
	/**
	 * @param eClass
	 * @param docGenOption
	 * @return
	 * @deprecated use {@link generateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions docGenOption)} instead
	 */
	@Deprecated
	CharSequence generateDocumentation(EClass eClass, EcoreToDocumentationOptions docGenOption);
	
	CharSequence generateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions docGenOption);

}
