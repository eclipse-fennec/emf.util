/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.openapi.client;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.fennec.service.api.ServiceInvocationException;

/**
 * OpenAPI 3 parameter serialization ({@code style}/{@code explode}) for primitive values and
 * arrays of them. Defaults: {@code simple} for path and header, {@code form} for query and
 * cookie; {@code explode} defaults to {@code true} for {@code form}, {@code false} otherwise.
 * Every method answers the <b>already encoded</b> fragment.
 * <p>
 * Supported: path {@code simple}/{@code label}/{@code matrix}, query
 * {@code form}/{@code spaceDelimited}/{@code pipeDelimited}, header {@code simple}, cookie
 * {@code form}. {@code deepObject} (objects only) and a style the location does not allow are
 * rejected.
 */
final class ParameterSerializer {

	static final String FORM = "form";
	static final String SIMPLE = "simple";
	static final String LABEL = "label";
	static final String MATRIX = "matrix";
	static final String SPACE_DELIMITED = "spaceDelimited";
	static final String PIPE_DELIMITED = "pipeDelimited";

	private ParameterSerializer() {
	}

	/**
	 * The path segment replacing {@code {name}}: {@code simple} {@code a,b}; {@code label}
	 * {@code .a,b} / exploded {@code .a.b}; {@code matrix} {@code ;name=a,b} / exploded
	 * {@code ;name=a;name=b}.
	 */
	static String path(String name, List<String> values, String style, Boolean explode) {
		String s = style == null ? SIMPLE : style;
		boolean x = explode(s, explode);
		Function<String, String> enc = ParameterSerializer::encodePath;
		return switch (s) {
		case SIMPLE -> join(values, ",", enc);
		case LABEL -> "." + join(values, x ? "." : ",", enc);
		case MATRIX -> x
				? values.stream().map(v -> ";" + enc.apply(name) + "=" + enc.apply(v)).collect(Collectors.joining())
				: ";" + enc.apply(name) + "=" + join(values, ",", enc);
		default -> throw unsupported(name, "path", s);
		};
	}

	/**
	 * The query fragment ({@code name=…} pairs joined by {@code &}): {@code form} exploded
	 * {@code name=a&name=b} / {@code name=a,b}; {@code spaceDelimited} {@code name=a%20b};
	 * {@code pipeDelimited} {@code name=a%7Cb}. An exploded {@code spaceDelimited}/{@code pipeDelimited}
	 * is written like an exploded {@code form}.
	 */
	static String query(String name, List<String> values, String style, Boolean explode) {
		String s = style == null ? FORM : style;
		boolean x = explode(s, explode);
		Function<String, String> enc = ParameterSerializer::encodeQuery;
		String delimiter = switch (s) {
		case FORM -> ",";
		case SPACE_DELIMITED -> "%20";
		case PIPE_DELIMITED -> "%7C";
		default -> throw unsupported(name, "query", s);
		};
		if (x) {
			return values.stream().map(v -> enc.apply(name) + "=" + enc.apply(v)).collect(Collectors.joining("&"));
		}
		return enc.apply(name) + "=" + join(values, delimiter, enc);
	}

	/** The header value: {@code simple} {@code a,b} (unencoded). */
	static String header(String name, List<String> values, String style) {
		String s = style == null ? SIMPLE : style;
		if (!SIMPLE.equals(s)) {
			throw unsupported(name, "header", s);
		}
		return String.join(",", values);
	}

	/** The cookie pairs: {@code form} {@code name=a,b} / exploded {@code name=a; name=b}. */
	static String cookie(String name, List<String> values, String style, Boolean explode) {
		String s = style == null ? FORM : style;
		if (!FORM.equals(s)) {
			throw unsupported(name, "cookie", s);
		}
		if (explode(s, explode)) {
			return values.stream().map(v -> name + "=" + v).collect(Collectors.joining("; "));
		}
		return name + "=" + String.join(",", values);
	}

	private static boolean explode(String style, Boolean declared) {
		return declared != null ? declared : FORM.equals(style);
	}

	private static String join(List<String> values, String delimiter, Function<String, String> encoder) {
		return values.stream().map(encoder).collect(Collectors.joining(delimiter));
	}

	/** Form encoding, as for any query component. */
	static String encodeQuery(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8);
	}

	/** Percent encoding for a path segment: a space is {@code %20} (a {@code +} would be literal there). */
	static String encodePath(String value) {
		return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
	}

	private static ServiceInvocationException unsupported(String name, String in, String style) {
		return new ServiceInvocationException("Parameter '" + name + "' (in " + in + "): style '" + style
				+ "' is not supported for this location");
	}
}
