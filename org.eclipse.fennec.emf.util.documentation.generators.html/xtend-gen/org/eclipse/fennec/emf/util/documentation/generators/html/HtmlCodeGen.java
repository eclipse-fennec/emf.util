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

import java.util.List;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToClassDiagramCodeGenerator;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationCodeGenerator;
import org.eclipse.fennec.emf.util.documentation.generators.apis.EcoreToDocumentationOptions;
import org.eclipse.fennec.emf.util.documentation.generators.mermaid.MermaidClassDiagramCodeGen;

@SuppressWarnings("all")
public class HtmlCodeGen implements EcoreToDocumentationCodeGenerator {
  private EcoreToClassDiagramCodeGenerator classDiagramCodeGen;

  private String docStart = ((((("<html>\n" + 
    "\t<head>\n") + 
    "\t\t<script src=\'https://unpkg.com/mermaid@8.5.2/dist/mermaid.min.js\'></script>\n") + 
    "\t\t<script type=\'module\' src=\'https://md-block.verou.me/md-block.js\'></script>\n") + 
    "\t</head>\n") + 
    "\t<body>");

  private String docEnd = "\t</body>\n</html>";

  private String diagramStartSyntax = "";

  private String diagramEndSyntax = "";

  private String mdBlockStart = "\t\t<md-block>";

  private String mdBlockEnd = "\t\t</md-block>";

  /**
   * Picks the class diagram generator for the given mode - and clears it for a mode without a
   * diagram, so that an instance reused across modes does not keep the diagram of an earlier call.
   * The indent is the one the surrounding template expects for the diagram block.
   */
  private void configureClassDiagram(final EcoreToDocumentationOptions mode, final String indent) {
    boolean _equals = EcoreToDocumentationOptions.HTML_WITH_MERMAID_CLASS_DIAGRAM.equals(mode);
    if (_equals) {
      MermaidClassDiagramCodeGen _mermaidClassDiagramCodeGen = new MermaidClassDiagramCodeGen();
      this.classDiagramCodeGen = _mermaidClassDiagramCodeGen;
      this.diagramStartSyntax = (indent + "<div class=\'mermaid\'>");
      this.diagramEndSyntax = (indent + "</div>");
    } else {
      this.classDiagramCodeGen = null;
      this.diagramStartSyntax = "";
      this.diagramEndSyntax = "";
    }
  }

  public CharSequence generateHtmlDoc(final EPackage epackage, final EcoreToDocumentationOptions mode) {
    CharSequence _xblockexpression = null;
    {
      this.configureClassDiagram(mode, "\t\t\t");
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(this.docStart);
      _builder.newLineIfNotEmpty();
      _builder.append(this.mdBlockStart);
      _builder.newLineIfNotEmpty();
      _builder.append("# Package: ");
      String _name = epackage.getName();
      _builder.append(_name);
      _builder.newLineIfNotEmpty();
      _builder.append("\n");
      _builder.newLineIfNotEmpty();
      _builder.append(this.mdBlockEnd);
      _builder.newLineIfNotEmpty();
      {
        if ((this.classDiagramCodeGen != null)) {
          _builder.append(this.mdBlockStart);
          _builder.newLineIfNotEmpty();
          _builder.append("## Class Diagram");
          _builder.newLine();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
          _builder.append(this.mdBlockEnd);
          _builder.newLineIfNotEmpty();
          _builder.append(this.diagramStartSyntax);
          _builder.newLineIfNotEmpty();
          String _replace = this.classDiagramCodeGen.generateClassDiagram(epackage).toString().replace("<", "&lt").replace(">", "&gt");
          _builder.append(_replace);
          _builder.newLineIfNotEmpty();
          _builder.append(this.diagramEndSyntax);
          _builder.newLineIfNotEmpty();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
        }
      }
      CharSequence _eClassifierOverview = this.toEClassifierOverview(epackage.getEClassifiers());
      _builder.append(_eClassifierOverview);
      _builder.newLineIfNotEmpty();
      _builder.append(this.docEnd);
      _builder.newLineIfNotEmpty();
      _xblockexpression = _builder;
    }
    return _xblockexpression;
  }

  public CharSequence toEClassifierOverview(final EList<EClassifier> eclassifiers) {
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
        boolean _isEmpty = classes.isEmpty();
        boolean _not = (!_isEmpty);
        if (_not) {
          _builder.append(this.mdBlockStart);
          _builder.newLineIfNotEmpty();
          _builder.append("## Classes Overview");
          _builder.newLine();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
          _builder.append(this.mdBlockEnd);
          _builder.newLineIfNotEmpty();
          {
            for(final EClass eclass : classes) {
              CharSequence _classOverview = this.toClassOverview(eclass);
              _builder.append(_classOverview);
              _builder.newLineIfNotEmpty();
              _builder.append("\n");
              _builder.newLineIfNotEmpty();
              _builder.append("\n");
              _builder.newLineIfNotEmpty();
            }
          }
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.append(this.mdBlockStart);
      _builder.newLineIfNotEmpty();
      _builder.append("## Enumerators Overview");
      _builder.newLine();
      _builder.append("\n");
      _builder.newLineIfNotEmpty();
      _builder.append(this.mdBlockEnd);
      _builder.newLineIfNotEmpty();
      {
        boolean _isEmpty_1 = enums.isEmpty();
        if (_isEmpty_1) {
          _builder.append(this.mdBlockStart);
          _builder.newLineIfNotEmpty();
          _builder.append("None.");
          _builder.newLine();
          _builder.append("\n");
          _builder.newLineIfNotEmpty();
          _builder.append(this.mdBlockEnd);
          _builder.newLineIfNotEmpty();
        } else {
          {
            for(final EEnum enumerator : enums) {
              CharSequence _enumOverview = this.toEnumOverview(enumerator);
              _builder.append(_enumOverview);
              _builder.newLineIfNotEmpty();
              _builder.append("\n");
              _builder.newLineIfNotEmpty();
              _builder.append("\n");
              _builder.newLineIfNotEmpty();
            }
          }
        }
      }
      _xblockexpression = _builder;
    }
    return _xblockexpression;
  }

  public CharSequence toEnumOverview(final EEnum enumerator) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(this.mdBlockStart);
    _builder.newLineIfNotEmpty();
    _builder.append("### Enumerator: ");
    String _name = enumerator.getName();
    _builder.append(_name);
    _builder.newLineIfNotEmpty();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    _builder.append("#### Description");
    _builder.newLine();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    String _modelElementDescription = this.toModelElementDescription(enumerator);
    _builder.append(_modelElementDescription);
    _builder.newLineIfNotEmpty();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    _builder.append("#### Literals");
    _builder.newLine();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    CharSequence _literalOverview = this.toLiteralOverview(enumerator.getELiterals());
    _builder.append(_literalOverview);
    _builder.newLineIfNotEmpty();
    _builder.append(this.mdBlockEnd);
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  public CharSequence toLiteralOverview(final EList<EEnumLiteral> literals) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = literals.isEmpty();
      if (_isEmpty) {
        _builder.append("None.");
        _builder.newLine();
      } else {
        _builder.append("| Literal| Description|");
        _builder.newLine();
        _builder.append("| -----| -----------|");
        _builder.newLine();
        {
          for(final EEnumLiteral literal : literals) {
            _builder.append("|");
            String _name = literal.getName();
            _builder.append(_name);
            _builder.append("|");
            String _modelElementDescription = this.toModelElementDescription(literal);
            _builder.append(_modelElementDescription);
            _builder.append("|");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder;
  }

  public CharSequence toClassOverview(final EClass eclass) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(this.mdBlockStart);
    _builder.newLineIfNotEmpty();
    _builder.append("### Class: ");
    String _name = eclass.getName();
    _builder.append(_name);
    _builder.newLineIfNotEmpty();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    _builder.append("#### Description");
    _builder.newLine();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    String _modelElementDescription = this.toModelElementDescription(eclass);
    _builder.append(_modelElementDescription);
    _builder.newLineIfNotEmpty();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    _builder.append(this.mdBlockEnd);
    _builder.newLineIfNotEmpty();
    {
      if ((this.classDiagramCodeGen != null)) {
        _builder.append(this.mdBlockStart);
        _builder.newLineIfNotEmpty();
        _builder.append("#### Class Diagram");
        _builder.newLine();
        _builder.append("\n");
        _builder.newLineIfNotEmpty();
        _builder.append(this.mdBlockEnd);
        _builder.newLineIfNotEmpty();
        _builder.append(this.diagramStartSyntax);
        _builder.newLineIfNotEmpty();
        String _replace = this.classDiagramCodeGen.generateClassDiagram(eclass).toString().replace("<", "&lt").replace(">", "&gt");
        _builder.append(_replace);
        _builder.newLineIfNotEmpty();
        _builder.append(this.diagramEndSyntax);
        _builder.newLineIfNotEmpty();
        _builder.append("\n");
        _builder.newLineIfNotEmpty();
      }
    }
    _builder.append(this.mdBlockStart);
    _builder.newLineIfNotEmpty();
    _builder.append("#### Fields");
    _builder.newLine();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    CharSequence _classStructuralFeaturesDescription = this.<EAttribute>toClassStructuralFeaturesDescription(eclass.getEAttributes());
    _builder.append(_classStructuralFeaturesDescription);
    _builder.newLineIfNotEmpty();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    _builder.append("#### References");
    _builder.newLine();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    CharSequence _classStructuralFeaturesDescription_1 = this.<EReference>toClassStructuralFeaturesDescription(eclass.getEReferences());
    _builder.append(_classStructuralFeaturesDescription_1);
    _builder.newLineIfNotEmpty();
    _builder.append("\n");
    _builder.newLineIfNotEmpty();
    _builder.append(this.mdBlockEnd);
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  public <T extends EStructuralFeature> CharSequence toClassStructuralFeaturesDescription(final EList<T> features) {
    StringConcatenation _builder = new StringConcatenation();
    {
      boolean _isEmpty = features.isEmpty();
      if (_isEmpty) {
        _builder.append("None.");
        _builder.newLine();
      } else {
        _builder.append("| Name| Type| Bounds| Description|");
        _builder.newLine();
        _builder.append("| -----| ----| ------| -----------|");
        _builder.newLine();
        {
          for(final T feature : features) {
            _builder.append("|");
            String _name = feature.getName();
            _builder.append(_name);
            _builder.append("|");
            String _name_1 = feature.getEType().getName();
            _builder.append(_name_1);
            _builder.append("|");
            int _lowerBound = feature.getLowerBound();
            _builder.append(_lowerBound);
            _builder.append("..");
            int _upperBound = feature.getUpperBound();
            _builder.append(_upperBound);
            _builder.append("|");
            String _modelElementDescription = this.toModelElementDescription(feature);
            _builder.append(_modelElementDescription);
            _builder.append("|");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    return _builder;
  }

  public String toModelElementDescription(final EModelElement element) {
    EList<EAnnotation> _eAnnotations = element.getEAnnotations();
    for (final EAnnotation annotation : _eAnnotations) {
      {
        EMap<String, String> details = annotation.getDetails();
        boolean _containsKey = details.containsKey("documentation");
        if (_containsKey) {
          return details.get("documentation").trim();
        }
      }
    }
    return "None.";
  }

  @Override
  public CharSequence generateDocumentation(final EPackage ePackage, final EcoreToDocumentationOptions docGenOption) {
    return this.generateHtmlDoc(ePackage, docGenOption);
  }

  @Override
  public CharSequence generateDocumentation(final EClass eClass, final EcoreToDocumentationOptions docGenOption) {
    CharSequence _xblockexpression = null;
    {
      this.configureClassDiagram(docGenOption, "");
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(this.docStart);
      _builder.newLineIfNotEmpty();
      CharSequence _classOverview = this.toClassOverview(eClass);
      _builder.append(_classOverview);
      _builder.newLineIfNotEmpty();
      _builder.append(this.docEnd);
      _builder.newLineIfNotEmpty();
      _xblockexpression = _builder;
    }
    return _xblockexpression;
  }

  @Override
  public CharSequence generateDocumentation(final EClassifier eClassifier, final EcoreToDocumentationOptions docGenOption) {
    CharSequence _xblockexpression = null;
    {
      this.configureClassDiagram(docGenOption, "");
      StringConcatenation _builder = new StringConcatenation();
      _builder.append(this.docStart);
      _builder.newLineIfNotEmpty();
      {
        if ((eClassifier instanceof EClass)) {
          CharSequence _classOverview = this.toClassOverview(((EClass) eClassifier));
          _builder.append(_classOverview);
          _builder.newLineIfNotEmpty();
        } else {
          if ((eClassifier instanceof EEnum)) {
            CharSequence _enumOverview = this.toEnumOverview(((EEnum) eClassifier));
            _builder.append(_enumOverview);
            _builder.newLineIfNotEmpty();
          }
        }
      }
      _builder.append(this.docEnd);
      _builder.newLineIfNotEmpty();
      _xblockexpression = _builder;
    }
    return _xblockexpression;
  }
}
