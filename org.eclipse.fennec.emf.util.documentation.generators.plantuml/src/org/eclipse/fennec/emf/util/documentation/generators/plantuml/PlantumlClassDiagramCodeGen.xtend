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
package org.eclipse.fennec.emf.util.documentation.generators.plantuml;

import java.util.List
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.EMap
import org.eclipse.emf.ecore.EAnnotation
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EModelElement
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToClassDiagramCodeGenerator

class PlantumlClassDiagramCodeGen implements EcoreToClassDiagramCodeGenerator{
	def toPlantumlClassDiagram(EPackage epackage)
	'''
«startPlantumlClassDiagram()»
«toEClassifierDiagram(epackage.EClassifiers)»
«endPlantumlClassDiagram()»
	'''
	
	def startPlantumlClassDiagram()
	'''
@startuml
	'''
	
	def endPlantumlClassDiagram()
	'''
@enduml
	'''
	
	
	def toEClassifierDiagram(EList<EClassifier> eclassifiers) {
var List<EClass> classes = eclassifiers.filter[ec | ec instanceof EClass].map[ec | ec as EClass].toList;	
var List<EEnum> enums = eclassifiers.filter[ec | ec instanceof EEnum].map[ec | ec as EEnum].toList;	
'''
«FOR eclass: classes»
«toClassDiagram(eclass)»
«ENDFOR»
«FOR enumerator: enums»
«toEnumerator(enumerator)»
«ENDFOR»
'''
}
	
	def toClassDiagram(EClass eclass) 
	'''
«IF eclass.isInterface»
«" "»interface «eclass.name» {
«ELSEIF eclass.isAbstract»	
«" "»abstract class «eclass.name» {
«ELSE»	
«" "»class «eclass.name» {
«ENDIF»	
«toClassMembers(eclass)»
}
«"\n"»
«toOnTopDescription(eclass)»
«"\n"»
«toClassReferences(eclass)»
«"\n"»
«IF !eclass.ESuperTypes.isEmpty»
«toSuperTypes(eclass)»
«"\n"»
«ENDIF»	
	'''
	
	def toClassDiagram(EClassifier eclassifier) 
	'''
«IF eclassifier instanceof EClass»
«toClassDiagram(eclassifier as EClass)»
«ELSEIF eclassifier instanceof EEnum»	
«toEnumerator(eclassifier as EEnum)»
«ENDIF»	
	'''
	
	def toClassMembers(EClass eclass)
	'''
«FOR attribute: eclass.EAttributes»
«" "»«attribute.name» : «attribute.EType.name» 
«ENDFOR»	
	'''
	
	def toClassReferences(EClass eclass)
	'''
«FOR ref: eclass.EReferences»
«toClassRef(eclass, ref)»
«ENDFOR»
	'''

	def toClassRef(EClass eclass, EReference ref)  {
		var multiplicity = extractMultiplicity(ref.lowerBound, ref.upperBound)
		var isInRefModel = isInRefModel(ref, eclass)
		'''
«IF isInRefModel»
«createRefPackage(ref)»
«"\n"»
«" "»«eclass.name» «' ..> '» «'\"'»«multiplicity»«'\"'» «ref.EType.name»«' : '»«ref.name»
«ELSE»
«" "»«eclass.name» «' --> '» «'\"'»«multiplicity»«'\"'» «ref.EType.name»«' : '»«ref.name»
«ENDIF»	
«"\n"»
		'''
	}
	
	def createRefPackage(EReference ref)
	'''
«" "»package "«ref.EType.EPackage.nsURI»" #DDDDDD {
«" "»«ref.EType.name» : «ref.name»
}
	'''
	
	def boolean isInRefModel(EReference ref, EClass containerClass) {
		if(ref.EType.EPackage.name === containerClass.EPackage.name) {
			return false
		} else {
			return true
		}
	}

	def String extractMultiplicity(int lowerBound, int upperBound) {
		if(lowerBound == 0 && upperBound == -1) {
			return "*"
		}		
		else if(lowerBound == 1 && upperBound == -1) {
			return "1..*"
		}
		return ((String.valueOf(lowerBound) + ".." +  (String.valueOf(upperBound))))
	}



	def toSuperTypes(EClass eclass)
	'''
«FOR parent: eclass.ESuperTypes»
«" "»«eclass.name» ..> «parent.name»
«ENDFOR»	
	'''
	
	def toEnumerator(EEnum enumerator)
	'''
«" "»enum «enumerator.name» {
«FOR value: enumerator.ELiterals»
«" "»«value.name»
«ENDFOR»
}
«"\n"»
«toOnTopDescription(enumerator)»
«"\n"»
	'''
	
	def toOnTopDescription(EClassifier eclassifier) {
		var description = toModelElementDescription(eclassifier)
		if(description !== "None.") {
			'''
note top of «eclassifier.name»
«" "»«description»
end note
			'''
		}
	}
	
	def String toModelElementDescription(EModelElement element) {
		for(EAnnotation annotation : element.EAnnotations) {
			var EMap<String, String> details = annotation.details;
			if(details.containsKey("documentation")) {
				var descr = details.get("documentation").replace("\n", "").trim;
				if(descr.length > 30) {
					var splitStr = descr.split(" ");
					var counter = 0;
					descr = "";
					for(sp : splitStr) {
						counter += sp.length;
						descr += " " + sp;
						if(counter > 30) {
							descr += " \n";
							counter = 0;
						}						
					}
				}
				return descr.trim;
			}
			
		}	
		return "None."	
	}
	
	override generateClassDiagram(EPackage ePackage) {
		toPlantumlClassDiagram(ePackage)
	}
	
	override generateClassDiagram(EClass eClass) {
		'''
«startPlantumlClassDiagram()»
«toClassDiagram(eClass)»
«endPlantumlClassDiagram()»
		'''
	}
	
	override generateClassDiagram(EClassifier eClassifier) {
		'''
«startPlantumlClassDiagram()»
«toClassDiagram(eClassifier)»
«endPlantumlClassDiagram()»
		'''
	}
	
}