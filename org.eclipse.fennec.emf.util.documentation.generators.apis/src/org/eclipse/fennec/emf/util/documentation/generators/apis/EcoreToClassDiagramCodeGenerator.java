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
 * @since Oct 18, 2022
 */
public interface EcoreToClassDiagramCodeGenerator {
	
	CharSequence generateClassDiagram(EPackage ePackage);

	
	/**
	 * 
	 * @param eClass
	 * @return
	 * @deprecated use {@link generateClassDiagram(EClassifier eClassifier)} instead
	 */
	@Deprecated()
	CharSequence generateClassDiagram(EClass eClass);
	
	CharSequence generateClassDiagram(EClassifier eClassifier);
		
}
