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
package org.eclipse.fennec.emf.util.documentation.generators.mermaid;

import java.util.List
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EReference
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToClassDiagramCodeGenerator

class MermaidClassDiagramCodeGen implements EcoreToClassDiagramCodeGenerator{
	
	def toMermaidClassDiagram(EPackage epackage)
	'''
«startMermaidClassDiagram()»
«toEClassifierDiagram(epackage.EClassifiers)»
	'''
	
	def startMermaidClassDiagram()
	'''
classDiagram
	'''
	
	def toEClassifierDiagram(EList<EClassifier> eclassifiers) {
var List<EClass> classes = eclassifiers.filter[ec | ec instanceof EClass].map[ec | ec as EClass].toList;	
var List<EEnum> enums = eclassifiers.filter[ec | ec instanceof EEnum].map[ec | ec as EEnum].toList;	
'''
«FOR eclass: classes»
«toClassDiagram(eclass)»
«"\n"»
«"\n"»
«ENDFOR»
«FOR enumerator: enums»
«toEnumerator(enumerator)»
«"\n"»
«"\n"»
«ENDFOR»
'''
}
	
	
	def toClassDiagram(EClass eclass) 
	'''
«" "»class «eclass.name» {
«IF eclass.isInterface»
«" <<interface>>"»
«ELSEIF eclass.isAbstract»	
«" <<abstract>>"»
«ENDIF»	
«toClassMembers(eclass)»
}
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
«IF eclass.EAttributes.isEmpty»
«" \n"»
«ENDIF»	
«FOR attribute: eclass.EAttributes»
«" "»«attribute.EType.name» «attribute.name»
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
		var refType = extractRefType(ref, eclass)
		var isInRefModel = isInRefModel(ref, eclass)
		'''
«IF isInRefModel»
«" "»«eclass.name» «' ..> '» «'\"'»«multiplicity»«'\"'» «refType»«' : '»«ref.name»
«ELSE»
«" "»«eclass.name» «' --> '» «'\"'»«multiplicity»«'\"'» «refType»«' : '»«ref.name»
«ENDIF»	
		'''
	}
	
	def String extractRefType(EReference ref, EClass containerClass) {
		if(ref.EType.EPackage.name === containerClass.EPackage.name) {
			return ref.EType.name
		} else {
//			TODO this should be the correct form when linking to a reference model (https://github.com/mermaid-js/mermaid/issues/1052)
//			return ref.EType.EPackage.nsURI + "#//" + ref.EType.name;
			return ref.EType.name
		}
	}
	
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
«" "»class «enumerator.name» {
«" <<enumeration>>"»
«FOR value: enumerator.ELiterals»
«" "»«value.name»
«ENDFOR»
}
	'''
	
	override generateClassDiagram(EPackage ePackage) {
		toMermaidClassDiagram(ePackage);
	}
	
	override generateClassDiagram(EClass eClass) {
		'''
«startMermaidClassDiagram()»
«toClassDiagram(eClass)»
		'''
	}
	
	override generateClassDiagram(EClassifier eClassifier) {
		'''
«startMermaidClassDiagram()»
«toClassDiagram(eClassifier)»
		'''
	}
	
}