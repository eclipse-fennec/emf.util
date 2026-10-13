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
package org.eclipse.fennec.emf.util.documentation.generators.html;

import java.util.List
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.EMap
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EEnumLiteral
import org.eclipse.emf.ecore.EModelElement
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToClassDiagramCodeGenerator
import org.eclipse.fennec.emf.util.documentation.generators.mermaid.MermaidClassDiagramCodeGen
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationCodeGenerator

class HtmlCodeGen implements EcoreToDocumentationCodeGenerator {
	
	EcoreToClassDiagramCodeGenerator classDiagramCodeGen;
	String docStart = "<html>\n"+
	"\t<head>\n"+
	"\t\t<script src='https://unpkg.com/mermaid@8.5.2/dist/mermaid.min.js'></script>\n" +
	"\t\t<script type='module' src='https://md-block.verou.me/md-block.js'></script>\n" +
    "\t</head>\n" +
    "\t<body>";
	String docEnd = "\t</body>\n</html>";
	String diagramStartSyntax = "";
	String diagramEndSyntax = "";
    String mdBlockStart = "\t\t<md-block>";
	String mdBlockEnd = "\t\t</md-block>";
	
	/**
	 * Picks the class diagram generator for the given mode - and clears it for a mode without a
	 * diagram, so that an instance reused across modes does not keep the diagram of an earlier call.
	 * The indent is the one the surrounding template expects for the diagram block.
	 */
	def private void configureClassDiagram(EcoreToDocumentationOptions mode, String indent) {
		if(EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM.equals(mode)) {
			classDiagramCodeGen = new MermaidClassDiagramCodeGen();
			diagramStartSyntax = indent + "<div class='mermaid'>";
			diagramEndSyntax = indent + "</div>";
		}
		else {
			classDiagramCodeGen = null;
			diagramStartSyntax = "";
			diagramEndSyntax = "";
		}
	}
	
	def generateHtmlDoc(EPackage epackage, EcoreToDocumentationOptions mode) {
		configureClassDiagram(mode, "\t\t\t");
			'''
«docStart»
«mdBlockStart»
# Package: «epackage.name»
«"\n"»
«mdBlockEnd»
«IF classDiagramCodeGen !== null»
«mdBlockStart»
## Class Diagram
«"\n"»
«mdBlockEnd»
«diagramStartSyntax»
«classDiagramCodeGen.generateClassDiagram(epackage).toString().replace("<","&lt").replace(">", "&gt")»
«diagramEndSyntax»
«"\n"»
«ENDIF»
«toEClassifierOverview(epackage.EClassifiers)»
«docEnd»
	'''
	}

	
	def toEClassifierOverview(EList<EClassifier> eclassifiers) {
var List<EClass> classes = eclassifiers.filter[ec | ec instanceof EClass].map[ec | ec as EClass].toList;	
var List<EEnum> enums = eclassifiers.filter[ec | ec instanceof EEnum].map[ec | ec as EEnum].toList;	
'''
«IF !classes.isEmpty»
«mdBlockStart»
## Classes Overview
«"\n"»
«mdBlockEnd»
«FOR eclass: classes»
«toClassOverview(eclass)»
«"\n"»
«"\n"»
«ENDFOR»
«"\n"»
«ENDIF»
«mdBlockStart»
## Enumerators Overview
«"\n"»
«mdBlockEnd»
«IF enums.empty»
«mdBlockStart»
None.
«"\n"»
«mdBlockEnd»
«ELSE»
«FOR enumerator: enums»
«toEnumOverview(enumerator)»
«"\n"»
«"\n"»
«ENDFOR»
«ENDIF»
'''
}
	
	
	def toEnumOverview(EEnum enumerator)
	'''
«mdBlockStart»
### Enumerator: «enumerator.name»
«"\n"»
#### Description
«"\n"»
«toModelElementDescription(enumerator)»
«"\n"»
#### Literals
«"\n"»
«toLiteralOverview(enumerator.ELiterals)»
«mdBlockEnd»
	'''	

	def toLiteralOverview(EList<EEnumLiteral> literals) 
	'''
«IF literals.empty»
None.
«ELSE»
| Literal| Description|
| -----| -----------|
«FOR literal: literals»
|«literal.name»|«toModelElementDescription(literal)»|
«ENDFOR»
«ENDIF»
	'''

	def toClassOverview(EClass eclass)
	'''
«mdBlockStart»
### Class: «eclass.name»
«"\n"»
#### Description
«"\n"»
«toModelElementDescription(eclass)»
«"\n"»
«mdBlockEnd»
«IF classDiagramCodeGen !== null»
«mdBlockStart»
#### Class Diagram
«"\n"»
«mdBlockEnd»
«diagramStartSyntax»
«classDiagramCodeGen.generateClassDiagram(eclass).toString().replace("<","&lt").replace(">", "&gt")»
«diagramEndSyntax»
«"\n"»
«ENDIF»
«mdBlockStart»
#### Fields
«"\n"»
«toClassStructuralFeaturesDescription(eclass.EAttributes)»
«"\n"»
#### References
«"\n"»
«toClassStructuralFeaturesDescription(eclass.EReferences)»
«"\n"»
«mdBlockEnd»
	'''
	
	
		def <T extends EStructuralFeature> toClassStructuralFeaturesDescription(EList<T> features) {
		'''
«IF features.empty»
None.
«ELSE»
| Name| Type| Bounds| Description|
| -----| ----| ------| -----------|
«FOR feature: features»
|«feature.name»|«feature.EType.name»|«feature.lowerBound»..«feature.upperBound»|«toModelElementDescription(feature)»|
«ENDFOR»
«ENDIF»
	'''	
		}
	
	
	def String toModelElementDescription(EModelElement element) {
		for(EAnnotation annotation : element.EAnnotations) {
			var EMap<String, String> details = annotation.details;
			if(details.containsKey("documentation")) {
				return details.get("documentation").trim;
			}
			
		}	
		return "None."	
	}
	
	override generateDocumentation(EPackage ePackage, EcoreToDocumentationOptions docGenOption) {
		generateHtmlDoc(ePackage, docGenOption);
	}
	
	override generateDocumentation(EClass eClass, EcoreToDocumentationOptions docGenOption) {
		configureClassDiagram(docGenOption, "");
		'''
«docStart»
«toClassOverview(eClass)»
«docEnd»
		'''
		
	}
	
	override generateDocumentation(EClassifier eClassifier, EcoreToDocumentationOptions docGenOption) {
		configureClassDiagram(docGenOption, "");
		'''
«docStart»
«IF eClassifier instanceof EClass»
«toClassOverview(eClassifier as EClass)»
«ELSEIF eClassifier instanceof EEnum»	
«toEnumOverview(eClassifier as EEnum)»
«ENDIF»	
«docEnd»
		'''
	}
	
}