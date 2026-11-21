/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.mof.ecore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.fennec.mof.ecore.MofAst.ClassDecl;
import org.eclipse.fennec.mof.ecore.MofAst.Literal;
import org.eclipse.fennec.mof.ecore.MofAst.Method;
import org.eclipse.fennec.mof.ecore.MofAst.Parameter;
import org.eclipse.fennec.mof.ecore.MofAst.Property;
import org.eclipse.fennec.mof.ecore.MofAst.Qualifier;
import org.eclipse.fennec.mof.ecore.MofAst.QualifierDecl;
import org.eclipse.fennec.mof.ecore.MofAst.Type;
import org.eclipse.fennec.mof.ecore.MofAst.Value;
import org.eclipse.fennec.mof.ecore.MofLexer.Kind;
import org.eclipse.fennec.mof.ecore.MofLexer.Token;

/**
 * Recursive-descent parser for one DMTF MOF compilation unit (DSP0004 class syntax).
 * <p>
 * Understood: class declarations (with alias and superclass), qualifier lists including WMI
 * flavor suffixes ({@code Description("…") : Amended ToSubclass}), properties, {@code REF}
 * properties, fixed and variable arrays (also the MOF 3 {@code type[] name} form), methods and
 * parameters, default values, qualifier declarations, and {@code #pragma} directives — of which
 * only {@code include} has an effect (reported to the include callback). Instance declarations
 * ({@code instance of …}, {@code value of …}) are parsed and skipped. The MOF 3 keywords
 * {@code association} and {@code indication} are accepted as classes; {@code structure},
 * {@code enumeration} and {@code interface} are rejected with a clear error.
 */
final class MofParser {

	private final List<Token> tokens;
	private final String file;
	private final Consumer<String> onInclude;
	private int pos;

	final List<ClassDecl> classes = new ArrayList<>();
	final List<QualifierDecl> qualifierDecls = new ArrayList<>();

	private MofParser(List<Token> tokens, String file, Consumer<String> onInclude) {
		this.tokens = tokens;
		this.file = file;
		this.onInclude = onInclude;
	}

	/**
	 * Parses {@code src}. Every {@code #pragma include} is reported to {@code onInclude} at the
	 * point it occurs (the caller resolves and parses it).
	 */
	static MofParser parse(String src, String file, Consumer<String> onInclude) {
		MofParser p = new MofParser(MofLexer.tokenize(src, file), file, onInclude);
		p.parseUnit();
		return p;
	}

	// --- token helpers -------------------------------------------------------------------------

	private Token peek() {
		return tokens.get(pos);
	}

	private Token peek(int offset) {
		return tokens.get(Math.min(pos + offset, tokens.size() - 1));
	}

	private Token next() {
		Token t = tokens.get(pos);
		if (t.kind() != Kind.EOF) {
			pos++;
		}
		return t;
	}

	private MofException error(Token at, String message) {
		return new MofException(file + ":" + at.line() + ":" + at.column() + ": " + message + " (found '"
				+ at.text() + "')");
	}

	private Token expectPunct(char c) {
		Token t = next();
		if (!t.isPunct(c)) {
			throw error(t, "expected '" + c + "'");
		}
		return t;
	}

	private boolean acceptPunct(char c) {
		if (peek().isPunct(c)) {
			pos++;
			return true;
		}
		return false;
	}

	private String expectIdent(String what) {
		Token t = next();
		if (t.kind() != Kind.IDENT) {
			throw error(t, "expected " + what);
		}
		return t.text();
	}

	private void expectKeyword(String keyword) {
		Token t = next();
		if (!t.isKeyword(keyword)) {
			throw error(t, "expected '" + keyword + "'");
		}
	}

	// --- grammar -------------------------------------------------------------------------------

	private void parseUnit() {
		while (peek().kind() != Kind.EOF) {
			if (peek().isPunct('#')) {
				pragma();
				continue;
			}
			if (acceptPunct(';')) {
				continue; // stray separator
			}
			List<Qualifier> qualifiers = qualifierListOpt();
			Token t = peek();
			if (t.isKeyword("class")) {
				next();
				classDecl(qualifiers, null);
			} else if (t.isKeyword("association") || t.isKeyword("indication")) {
				next();
				classDecl(qualifiers, t.text());
			} else if (t.isKeyword("qualifier")) {
				next();
				qualifierDecl();
			} else if (t.isKeyword("instance") || (t.isKeyword("value") && peek(1).isKeyword("of"))) {
				next();
				skipInstance();
			} else if (t.isKeyword("structure") || t.isKeyword("enumeration") || t.isKeyword("interface")) {
				throw error(t, "MOF 3 " + t.text().toLowerCase() + " declarations are not supported");
			} else {
				throw error(t, "expected a class, qualifier or instance declaration");
			}
		}
	}

	private void pragma() {
		Token hash = next();
		Token kw = next();
		Token name;
		if (kw.isKeyword("include")) {
			name = kw; // legacy "#include (...)", found in older WMI MOF files
		} else if (kw.isKeyword("pragma")) {
			name = next();
		} else {
			throw error(kw, "expected 'pragma' after '#'");
		}
		if (name.kind() != Kind.IDENT) {
			throw error(name, "expected a pragma name");
		}
		if (!peek().isPunct('(')) {
			return; // e.g. #pragma autorecover
		}
		if (name.isKeyword("include")) {
			next();
			Token path = next();
			if (path.kind() != Kind.STRING) {
				throw error(path, "expected the include file name as a string");
			}
			StringBuilder sb = new StringBuilder(path.text());
			while (peek().kind() == Kind.STRING) {
				sb.append(next().text());
			}
			expectPunct(')');
			onInclude.accept(sb.toString());
		} else {
			skipBalanced('(', ')', hash);
		}
	}

	/** Skips from the opening bracket at the current position to its matching close. */
	private void skipBalanced(char open, char close, Token context) {
		expectPunct(open);
		int depth = 1;
		while (depth > 0) {
			Token t = next();
			if (t.kind() == Kind.EOF) {
				throw error(context, "unbalanced '" + open + "'");
			}
			if (t.isPunct(open)) {
				depth++;
			} else if (t.isPunct(close)) {
				depth--;
			}
		}
	}

	private List<Qualifier> qualifierListOpt() {
		List<Qualifier> qualifiers = new ArrayList<>();
		if (!acceptPunct('[')) {
			return qualifiers;
		}
		if (acceptPunct(']')) {
			return qualifiers;
		}
		do {
			qualifiers.add(qualifier());
		} while (acceptPunct(','));
		expectPunct(']');
		return qualifiers;
	}

	private Qualifier qualifier() {
		String name = expectIdent("a qualifier name");
		Value value = null;
		if (acceptPunct('(')) {
			value = peek().isPunct('{') ? arrayValue() : Value.of(literal());
			expectPunct(')');
		} else if (peek().isPunct('{')) {
			value = arrayValue();
		}
		if (acceptPunct(':')) {
			// flavors: ToSubclass, Restricted, EnableOverride, Translatable, Amended, ToInstance, …
			while (peek().kind() == Kind.IDENT) {
				next();
			}
		}
		return new Qualifier(name, value);
	}

	private Value value() {
		return peek().isPunct('{') ? arrayValue() : Value.of(literal());
	}

	private Value arrayValue() {
		expectPunct('{');
		List<Literal> elements = new ArrayList<>();
		if (!peek().isPunct('}')) {
			do {
				if (peek().isPunct('}')) {
					break; // trailing comma
				}
				elements.add(literal());
			} while (acceptPunct(','));
		}
		expectPunct('}');
		return Value.array(elements);
	}

	private Literal literal() {
		Token t = next();
		switch (t.kind()) {
		case STRING: {
			StringBuilder sb = new StringBuilder(t.text());
			while (peek().kind() == Kind.STRING) {
				sb.append(next().text()); // "a" "b" concatenation
			}
			return new Literal(Literal.Kind.STRING, sb.toString());
		}
		case CHAR:
			return new Literal(Literal.Kind.CHAR, t.text());
		case INTEGER:
			return new Literal(Literal.Kind.INTEGER, t.text());
		case REAL:
			return new Literal(Literal.Kind.REAL, t.text());
		case ALIAS:
			return new Literal(Literal.Kind.ALIAS, t.text());
		case IDENT:
			if (t.isKeyword("true") || t.isKeyword("false")) {
				return new Literal(Literal.Kind.BOOLEAN, t.text().toLowerCase());
			}
			if (t.isKeyword("null")) {
				return new Literal(Literal.Kind.NULL, "null");
			}
			break;
		default:
			break;
		}
		throw error(t, "expected a constant value");
	}

	private void classDecl(List<Qualifier> qualifiers, String impliedQualifier) {
		Token nameToken = peek();
		String name = expectIdent("a class name");
		if (peek().isKeyword("as")) {
			next();
			Token alias = next();
			if (alias.kind() != Kind.ALIAS) {
				throw error(alias, "expected an alias ($name)");
			}
		}
		String superclass = null;
		if (acceptPunct(':')) {
			superclass = expectIdent("a superclass name");
		}
		if (impliedQualifier != null && qualifiers.stream().noneMatch(q -> q.is(impliedQualifier))) {
			qualifiers.add(0, new Qualifier(Character.toUpperCase(impliedQualifier.charAt(0))
					+ impliedQualifier.substring(1).toLowerCase(), null));
		}
		expectPunct('{');
		List<Property> properties = new ArrayList<>();
		List<Method> methods = new ArrayList<>();
		while (!acceptPunct('}')) {
			feature(properties, methods);
		}
		acceptPunct(';');
		classes.add(new ClassDecl(List.copyOf(qualifiers), name, superclass, properties, methods,
				file + ":" + nameToken.line()));
	}

	private void feature(List<Property> properties, List<Method> methods) {
		List<Qualifier> qualifiers = qualifierListOpt();
		Token start = peek();
		if (start.kind() == Kind.EOF) {
			throw error(start, "unterminated class declaration");
		}
		Type type = typeRef();
		String name = expectIdent("a property or method name");
		if (acceptPunct('(')) {
			List<Parameter> parameters = new ArrayList<>();
			if (!peek().isPunct(')')) {
				do {
					parameters.add(parameter());
				} while (acceptPunct(','));
			}
			expectPunct(')');
			expectPunct(';');
			methods.add(new Method(qualifiers, type, name, parameters));
			return;
		}
		type = arraySuffix(type);
		Value defaultValue = null;
		if (acceptPunct('=')) {
			defaultValue = value();
		}
		expectPunct(';');
		properties.add(new Property(qualifiers, type, name, defaultValue));
	}

	private Parameter parameter() {
		List<Qualifier> qualifiers = qualifierListOpt();
		Type type = typeRef();
		String name = expectIdent("a parameter name");
		type = arraySuffix(type);
		if (acceptPunct('=')) {
			value(); // MOF 3 parameter default — not mapped
		}
		return new Parameter(qualifiers, type, name);
	}

	/** {@code dataType} | {@code className REF}, optionally followed by MOF 3 {@code []}. */
	private Type typeRef() {
		String typeName = expectIdent("a data type or class name");
		boolean ref = false;
		if (peek().isKeyword("ref")) {
			next();
			ref = true;
		}
		int array = -1;
		if (peek().isPunct('[') && (peek(1).isPunct(']') || (peek(1).kind() == Kind.INTEGER && peek(2).isPunct(']')))) {
			array = arrayBrackets();
		}
		return new Type(typeName, ref, array);
	}

	private Type arraySuffix(Type type) {
		if (peek().isPunct('[')) {
			int array = arrayBrackets();
			return new Type(type.name(), type.ref(), array);
		}
		return type;
	}

	private int arrayBrackets() {
		expectPunct('[');
		int size = 0;
		if (peek().kind() == Kind.INTEGER) {
			Token t = next();
			try {
				size = Integer.parseInt(t.text());
			} catch (NumberFormatException e) {
				throw error(t, "invalid array size");
			}
		}
		expectPunct(']');
		return size;
	}

	private void qualifierDecl() {
		String name = expectIdent("a qualifier name");
		expectPunct(':');
		String typeName = expectIdent("a qualifier type");
		int array = -1;
		if (peek().isPunct('[')) {
			array = arrayBrackets();
		}
		qualifierDecls.add(new QualifierDecl(name, new Type(typeName, false, array)));
		// default value, Scope(…), Flavor(…) — not needed for the mapping
		Token start = peek();
		int depth = 0;
		while (true) {
			Token t = next();
			if (t.kind() == Kind.EOF) {
				throw error(start, "unterminated qualifier declaration");
			}
			if (t.isPunct('(') || t.isPunct('{')) {
				depth++;
			} else if (t.isPunct(')') || t.isPunct('}')) {
				depth--;
			} else if (t.isPunct(';') && depth == 0) {
				return;
			}
		}
	}

	private void skipInstance() {
		Token start = peek();
		expectKeyword("of");
		expectIdent("a class name");
		if (peek().isKeyword("as")) {
			next();
			next();
		} else if (peek().kind() == Kind.ALIAS) {
			next();
		}
		skipBalanced('{', '}', start);
		acceptPunct(';');
	}
}
