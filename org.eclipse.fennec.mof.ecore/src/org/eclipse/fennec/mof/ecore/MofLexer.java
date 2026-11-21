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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer for DMTF MOF (DSP0004). Skips {@code //} and {@code /* *}{@code /} comments and
 * backslash line continuations,
 * decodes string/char escapes and normalizes integer literals (hex {@code 0x1F}, binary
 * {@code 101b}, octal {@code 017}) to decimal text.
 */
final class MofLexer {

	enum Kind {
		IDENT, STRING, CHAR, INTEGER, REAL, ALIAS, PUNCT, EOF
	}

	record Token(Kind kind, String text, int line, int column) {

		boolean isPunct(char c) {
			return kind == Kind.PUNCT && text.length() == 1 && text.charAt(0) == c;
		}

		boolean isKeyword(String keyword) {
			return kind == Kind.IDENT && text.equalsIgnoreCase(keyword);
		}
	}

	private final String src;
	private final String file;
	private int pos;
	private int line = 1;
	private int lineStart;

	private MofLexer(String src, String file) {
		this.src = src;
		this.file = file;
	}

	static List<Token> tokenize(String src, String file) {
		MofLexer lexer = new MofLexer(src, file);
		List<Token> tokens = new ArrayList<>();
		Token t;
		do {
			t = lexer.next();
			tokens.add(t);
		} while (t.kind() != Kind.EOF);
		return tokens;
	}

	private MofException error(String message, int l, int col) {
		return new MofException(file + ":" + l + ":" + col + ": " + message);
	}

	private char peek(int offset) {
		int i = pos + offset;
		return i < src.length() ? src.charAt(i) : '\0';
	}

	private void advance() {
		if (src.charAt(pos) == '\n') {
			line++;
			lineStart = pos + 1;
		}
		pos++;
	}

	private void skipTrivia() {
		while (pos < src.length()) {
			char c = src.charAt(pos);
			if (Character.isWhitespace(c) || c == '\uFEFF') {
				advance();
			} else if (c == '\\' && isLineContinuation()) {
				advance(); // C-style line continuation, found in some WMI MOF files
			} else if (c == '/' && peek(1) == '/') {
				while (pos < src.length() && src.charAt(pos) != '\n') {
					advance();
				}
			} else if (c == '/' && peek(1) == '*') {
				int l = line;
				int col = pos - lineStart + 1;
				advance();
				advance();
				while (pos < src.length() && !(src.charAt(pos) == '*' && peek(1) == '/')) {
					advance();
				}
				if (pos >= src.length()) {
					throw error("unterminated comment", l, col);
				}
				advance();
				advance();
			} else {
				return;
			}
		}
	}

	/** A backslash followed only by blanks up to the end of the line. */
	private boolean isLineContinuation() {
		int i = pos + 1;
		while (i < src.length() && (src.charAt(i) == ' ' || src.charAt(i) == '\t' || src.charAt(i) == '\r')) {
			i++;
		}
		return i >= src.length() || src.charAt(i) == '\n';
	}

	private Token next() {
		skipTrivia();
		int l = line;
		int col = pos - lineStart + 1;
		if (pos >= src.length()) {
			return new Token(Kind.EOF, "<end of file>", l, col);
		}
		char c = src.charAt(pos);
		if (c == '"') {
			return new Token(Kind.STRING, readQuoted('"', l, col), l, col);
		}
		if (c == '\'') {
			return new Token(Kind.CHAR, readQuoted('\'', l, col), l, col);
		}
		if (c == '$' && isIdentStart(peek(1))) {
			advance();
			return new Token(Kind.ALIAS, "$" + readIdent(), l, col);
		}
		if (isIdentStart(c)) {
			return new Token(Kind.IDENT, readIdent(), l, col);
		}
		if (Character.isDigit(c) || ((c == '-' || c == '+' || c == '.') && (Character.isDigit(peek(1))
				|| (peek(1) == '.' && Character.isDigit(peek(2)))))) {
			return readNumber(l, col);
		}
		advance();
		return new Token(Kind.PUNCT, String.valueOf(c), l, col);
	}

	private static boolean isIdentStart(char c) {
		return c == '_' || Character.isLetter(c);
	}

	private String readIdent() {
		int start = pos;
		while (pos < src.length() && (src.charAt(pos) == '_' || Character.isLetterOrDigit(src.charAt(pos)))) {
			advance();
		}
		return src.substring(start, pos);
	}

	private String readQuoted(char quote, int l, int col) {
		StringBuilder sb = new StringBuilder();
		advance(); // opening quote
		while (true) {
			if (pos >= src.length() || src.charAt(pos) == '\n') {
				throw error("unterminated " + (quote == '"' ? "string" : "char") + " literal", l, col);
			}
			char c = src.charAt(pos);
			if (c == quote) {
				advance();
				return sb.toString();
			}
			if (c == '\\') {
				advance();
				if (pos >= src.length()) {
					throw error("unterminated escape", l, col);
				}
				char e = src.charAt(pos);
				advance();
				switch (e) {
				case 'b' -> sb.append('\b');
				case 't' -> sb.append('\t');
				case 'n' -> sb.append('\n');
				case 'f' -> sb.append('\f');
				case 'r' -> sb.append('\r');
				case 'x', 'X' -> {
					int start = pos;
					while (pos < src.length() && pos - start < 4 && isHex(src.charAt(pos))) {
						advance();
					}
					if (pos == start) {
						throw error("invalid \\x escape", l, col);
					}
					sb.append((char) Integer.parseInt(src.substring(start, pos), 16));
				}
				case '"', '\'', '\\' -> sb.append(e);
				// not a DSP0004 escape: keep it verbatim (WMI MOF files write Windows paths this way)
				default -> sb.append('\\').append(e);
				}
			} else {
				sb.append(c);
				advance();
			}
		}
	}

	private static boolean isHex(char c) {
		return Character.digit(c, 16) >= 0;
	}

	private Token readNumber(int l, int col) {
		int start = pos;
		if (src.charAt(pos) == '-' || src.charAt(pos) == '+') {
			advance();
		}
		if (src.charAt(pos) == '0' && (peek(1) == 'x' || peek(1) == 'X')) {
			advance();
			advance();
			int digits = pos;
			while (pos < src.length() && isHex(src.charAt(pos))) {
				advance();
			}
			if (pos == digits) {
				throw error("invalid hex literal", l, col);
			}
			return integer(src.substring(start, digits - 2), src.substring(digits, pos), 16, l, col);
		}
		while (pos < src.length() && (Character.isLetterOrDigit(src.charAt(pos)) || src.charAt(pos) == '.'
				|| ((src.charAt(pos) == '+' || src.charAt(pos) == '-')
						&& (src.charAt(pos - 1) == 'e' || src.charAt(pos - 1) == 'E')))) {
			advance();
		}
		String text = src.substring(start, pos);
		String sign = text.startsWith("-") || text.startsWith("+") ? text.substring(0, 1) : "";
		String body = text.substring(sign.length());
		if (body.matches("[0-9]*\\.[0-9]+([eE][+-]?[0-9]+)?|[0-9]+([eE][+-]?[0-9]+)|[0-9]+\\.[0-9]*([eE][+-]?[0-9]+)?")) {
			return new Token(Kind.REAL, sign.equals("-") ? text : body, l, col);
		}
		if (body.matches("[01]+[bB]")) {
			return integer(sign, body.substring(0, body.length() - 1), 2, l, col);
		}
		if (body.matches("0[0-7]+")) {
			return integer(sign, body.substring(1), 8, l, col);
		}
		if (body.matches("[0-9]+")) {
			return integer(sign, body, 10, l, col);
		}
		throw error("invalid numeric literal '" + text + "'", l, col);
	}

	private static Token integer(String sign, String digits, int radix, int l, int col) {
		BigInteger value = new BigInteger(digits, radix);
		if (sign.equals("-")) {
			value = value.negate();
		}
		return new Token(Kind.INTEGER, value.toString(), l, col);
	}
}
