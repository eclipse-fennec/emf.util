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

import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToClassDiagramCodeGenerator;

@SuppressWarnings("all")
public class MermaidClassDiagramCodeGen implements EcoreToClassDiagramCodeGenerator {
  public CharSequence toMermaidClassDiagram(final EPackage epackage) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _startMermaidClassDiagram = this.startMermaidClassDiagram();
    _builder.append(_startMermaidClassDiagram);
    _builder.newLineIfNotEmpty();
    CharSequence _eClassifierDiagram = this.toEClassifierDiagram(epackage.getEClassifiers());
    _builder.append(_eClassifierDiagram);
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  public CharSequence startMermaidClassDiagram() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("classDiagram");
    _builder.newLine();
    return _builder;
  }

  public CharSequence toEClassifierDiagram(final EList<EClassifier> eclassifiers) {
    CharSequence _xblockexpression = null;
    {
      final Function1<EClassifier, Boolean> _function = (EClassifier ec) -> {
        return Boolean.valueOf((ec instanceof EClass));
      };
      final Function1<EClassifier, EClass> _function_1 = (EClassifier ec) -> {
        return ((EClass) ec);
      };
      List<EClass> classes = IterableExtensions.<EClass>toList(IterableExtensions.<EClassifier, EClass>map(IterableExtensions.<EClassifier>filter(eclassifiers, _function), _function_1));
      final Function1<EClassifier, Boolean> _function_2 = (EClassifier ec) -> {
        return Boolean.valueOf((ec instanceof EEnum));
      };
      final Function1<EClassifier, EEnum> _function_3 = (EClassifier ec) -> {
        return ((EEnum) ec);
      };
      List<EEnum> enums = IterableExtensions.<EEnum>toList(IterableExtensions.<EClassifier, EEnum>map(IterableExtensions.<EClassifier>filter(eclassifiers, _function_2), _function_3));
      StringConcatenation _builder = new StringConcatenation();
      {
        for(final EClass eclass : classes) {
          CharSequence _classDiagram = this.toClassDiagram(eclass);
          _builder.append(_classDiagram);
          _builder.newLineIfNotEmpty();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
        }
      }
      {
        for(final EEnum enumerator : enums) {
          CharSequence _enumerator = this.toEnumerator(enumerator);
          _builder.append(_enumerator);
          _builder.newLineIfNotEmpty();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
        }
      }
      _xblockexpression = _builder;
    }
    return _xblockexpression;
  }

  public CharSequence toClassDiagram(final EClass eclass) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(" ");
    _builder.append("class ");
    String _name = eclass.getName();
    _builder.append(_name);
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    {
      boolean _isInterface = eclass.isInterface();
      if (_isInterface) {
        _builder.append(" <<interface>>");
        _builder.newLineIfNotEmpty();
      } else {
        boolean _isAbstract = eclass.isAbstract();
        if (_isAbstract) {
          _builder.append(" <<abstract>>");
          _builder.newLineIfNotEmpty();
        }
      }
    }
    CharSequence _classMembers = this.toClassMembers(eclass);
    _builder.append(_classMembers);
    _builder.newLineIfNotEmpty();
    _builder.append("}");
    _builder.newLine();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    CharSequence _classReferences = this.toClassReferences(eclass);
    _builder.append(_classReferences);
    _builder.newLineIfNotEmpty();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    {
      boolean _isEmpty = eclass.getESuperTypes().isEmpty();
      boolean _not = (!_isEmpty);
      if (_not) {
        CharSequence _superTypes = this.toSuperTypes(eclass);
        _builder.append(_superTypes);
        _builder.newLineIfNotEmpty();
        _builder.append("\n");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }

  public CharSequence toClassDiagram(final EClassifier eclassifier) {
    StringConcatenation _builder = new StringConcatenation();
    {
      if ((eclassifier instanceof EClass)) {
        CharSequence _classDiagram = this.toClassDiagram(((EClass) eclassifier));
        _builder.append(_classDiagram);
        _builder.newLineIfNotEmpty();
      } else {
        if ((eclassifier instanceof EEnum)) {
          CharSequence _enumerator = this.toEnumerator(((EEnum) eclassifier));
          _builder.append(_enumerator);
          _builder.newLineIfNotEmpty();
        }
      }
    }
    return _builder;
  }

  public CharSequence toClassMembers(final EClass eclass) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = eclass.getEAttributes().isEmpty();
      if (_isEmpty) {
        _builder.append(" \n");
        _builder.newLineIfNotEmpty();
      }
    }
    {
      EList<EAttribute> _eAttributes = eclass.getEAttributes();
      for(final EAttribute attribute : _eAttributes) {
        _builder.append(" ");
        String _name = attribute.getEType().getName();
        _builder.append(_name);
        _builder.append(" ");
        String _name_1 = attribute.getName();
        _builder.append(_name_1);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }

  public CharSequence toClassReferences(final EClass eclass) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<EReference> _eReferences = eclass.getEReferences();
      for(final EReference ref : _eReferences) {
        CharSequence _classRef = this.toClassRef(eclass, ref);
        _builder.append(_classRef);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }

  public CharSequence toClassRef(final EClass eclass, final EReference ref) {
    CharSequence _xblockexpression = null;
    {
      String multiplicity = this.extractMultiplicity(ref.getLowerBound(), ref.getUpperBound());
      String refType = this.extractRefType(ref, eclass);
      boolean isInRefModel = this.isInRefModel(ref, eclass);
      StringConcatenation _builder = new StringConcatenation();
      {
        if (isInRefModel) {
          _builder.append(" ");
          String _name = eclass.getName();
          _builder.append(_name);
          _builder.append(" ");
          _builder.append(" ..> ");
          _builder.append(" ");
          _builder.append("\"");
          _builder.append(multiplicity);
          _builder.append("\"");
          _builder.append(" ");
          _builder.append(refType);
          _builder.append(" : ");
          String _name_1 = ref.getName();
          _builder.append(_name_1);
          _builder.newLineIfNotEmpty();
        } else {
          _builder.append(" ");
          String _name_2 = eclass.getName();
          _builder.append(_name_2);
          _builder.append(" ");
          _builder.append(" --> ");
          _builder.append(" ");
          _builder.append("\"");
          _builder.append(multiplicity);
          _builder.append("\"");
          _builder.append(" ");
          _builder.append(refType);
          _builder.append(" : ");
          String _name_3 = ref.getName();
          _builder.append(_name_3);
          _builder.newLineIfNotEmpty();
        }
      }
      _xblockexpression = _builder;
    }
    return _xblockexpression;
  }

  public String extractRefType(final EReference ref, final EClass containerClass) {
    String _name = ref.getEType().getEPackage().getName();
    String _name_1 = containerClass.getEPackage().getName();
    boolean _tripleEquals = (_name == _name_1);
    if (_tripleEquals) {
      return ref.getEType().getName();
    } else {
      return ref.getEType().getName();
    }
  }

  public boolean isInRefModel(final EReference ref, final EClass containerClass) {
    String _name = ref.getEType().getEPackage().getName();
    String _name_1 = containerClass.getEPackage().getName();
    boolean _tripleEquals = (_name == _name_1);
    if (_tripleEquals) {
      return false;
    } else {
      return true;
    }
  }

  public String extractMultiplicity(final int lowerBound, final int upperBound) {
    if (((lowerBound == 0) && (upperBound == (-1)))) {
      return "*";
    } else {
      if (((lowerBound == 1) && (upperBound == (-1)))) {
        return "1..*";
      }
    }
    String _valueOf = String.valueOf(lowerBound);
    String _plus = (_valueOf + "..");
    String _valueOf_1 = String.valueOf(upperBound);
    return (_plus + _valueOf_1);
  }

  public CharSequence toSuperTypes(final EClass eclass) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<EClass> _eSuperTypes = eclass.getESuperTypes();
      for(final EClass parent : _eSuperTypes) {
        _builder.append(" ");
        String _name = eclass.getName();
        _builder.append(_name);
        _builder.append(" ..> ");
        String _name_1 = parent.getName();
        _builder.append(_name_1);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }

  public CharSequence toEnumerator(final EEnum enumerator) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(" ");
    _builder.append("class ");
    String _name = enumerator.getName();
    _builder.append(_name);
    _builder.append(" {");
    _builder.newLineIfNotEmpty();
    _builder.append(" <<enumeration>>");
    _builder.newLineIfNotEmpty();
    {
      EList<EEnumLiteral> _eLiterals = enumerator.getELiterals();
      for(final EEnumLiteral value : _eLiterals) {
        _builder.append(" ");
        String _name_1 = value.getName();
        _builder.append(_name_1);
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append("}");
    _builder.newLine();
    return _builder;
  }

  @Override
  public CharSequence generateClassDiagram(final EPackage ePackage) {
    return this.toMermaidClassDiagram(ePackage);
  }

  @Override
  public CharSequence generateClassDiagram(final EClass eClass) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _startMermaidClassDiagram = this.startMermaidClassDiagram();
    _builder.append(_startMermaidClassDiagram);
    _builder.newLineIfNotEmpty();
    CharSequence _classDiagram = this.toClassDiagram(eClass);
    _builder.append(_classDiagram);
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  @Override
  public CharSequence generateClassDiagram(final EClassifier eClassifier) {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _startMermaidClassDiagram = this.startMermaidClassDiagram();
    _builder.append(_startMermaidClassDiagram);
    _builder.newLineIfNotEmpty();
    CharSequence _classDiagram = this.toClassDiagram(eClassifier);
    _builder.append(_classDiagram);
    _builder.newLineIfNotEmpty();
    return _builder;
  }
}
