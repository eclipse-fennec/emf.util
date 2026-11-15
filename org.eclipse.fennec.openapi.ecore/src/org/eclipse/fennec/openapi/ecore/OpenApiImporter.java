/**
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.fennec.openapi.ecore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jsonschema.model.Schema;
import org.eclipse.fennec.codec.jsonschema.model.SimpleType;
import org.eclipse.fennec.codec.openapi.oas.OasResourceFactoryImpl;
import org.eclipse.fennec.codec.openapi.oas.OasVersion;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.yaml.YamlFormatProvider;
import org.eclipse.fennec.model.oas.Components;
import org.eclipse.fennec.model.oas.MediaType;
import org.eclipse.fennec.model.oas.OasPackage;
import org.eclipse.fennec.model.oas.OpenAPI;
import org.eclipse.fennec.model.oas.Operation;
import org.eclipse.fennec.model.oas.Parameter;
import org.eclipse.fennec.model.oas.ParameterLocation;
import org.eclipse.fennec.model.oas.ParameterStyle;
import org.eclipse.fennec.model.oas.PathItem;
import org.eclipse.fennec.model.oas.RequestBody;
import org.eclipse.fennec.model.oas.Response;
import org.eclipse.fennec.model.oas.SecurityRequirement;
import org.eclipse.fennec.model.oas.SecurityScheme;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Derives {@link OpenApiOperation}s (plus their request/response {@link EClass}es) from an
 * OpenAPI 3.0, 3.1 or 3.2 description — the REST counterpart of the WSDL/gRPC importers. The
 * heavy lifting is done by the Fennec codec: every version is read into its {@code oas} model
 * ({@code org.eclipse.fennec.model.oas}), which derives the Ecore of the schemas during load
 * ({@code components/schemas} → {@link Components#getSchemasPackage()}, inline schemas → its
 * {@code inline} sub-package) and links every use site to its classifier
 * ({@link Schema#getClassifier()}), every Reference Object to its {@code target} and every
 * operation to its HTTP {@code method}. This class only <b>links</b> the path operations to those
 * classifiers:
 * <ul>
 * <li>body-only operation → {@code requestType} is the body EClass itself;</li>
 * <li>operation with parameters (its own and the path item's) → a <b>synthetic request EClass</b>
 * ({@code <Name>Request}) whose features carry {@code in=path|query|header|cookie} annotations, plus
 * an optional {@code body} containment reference — so a form UI / client can treat every request
 * uniformly as one EObject;</li>
 * <li>response → the first 2xx (or {@code default}) {@code application/json} schema; a JSON array
 * resolves to its item EClass with {@link OpenApiOperation#responseMany()}; a scalar schema
 * ({@code string}, {@code integer}, {@code number}, {@code boolean}, or an array of them — also
 * as {@code text/plain}) to its {@link OpenApiOperation#responseDataType() data type};</li>
 * <li>security → {@code components/securitySchemes} is exposed on the model as-is; each
 * operation carries its effective requirements (own {@code security}, else the global one).</li>
 * </ul>
 * Non-JSON content, {@code querystring} parameters (3.2) and webhooks are skipped (diagnostics).
 */
public final class OpenApiImporter {

	private static final String JSON = "application/json";
	private static final String TEXT = "text/plain";
	/** nsURI given to the generated schemas package when the pipeline leaves it unset. */
	private static final String SCHEMAS_NS = "http://eclipse.org/fennec/openapi/generated/schemas";
	private static final String REQUESTS_NS = "http://eclipse.org/fennec/openapi/generated/requests";
	/** Annotation source the codec's JSON-Schema converter stamps on generated features. */
	private static final String JSONSCHEMA_SOURCE = "http://fennec.eclipse.org/jsonschema";
	/** The top-level {@code openapi} member of a YAML description (unindented). */
	private static final Pattern YAML_VERSION = Pattern
			.compile("(?m)^[\"']?openapi[\"']?\\s*:\\s*[\"']?([^\"'\\s#]+)");
	private static final JsonMapper MAPPER = JsonMapper.builder().build();

	private final List<String> diagnostics = new ArrayList<>();
	private final Map<String, SecurityScheme> securitySchemes = new LinkedHashMap<>();
	private List<Map<String, List<String>>> globalSecurity = List.of();
	private EPackage schemasPackage;
	private EPackage requestsPackage;

	private OpenApiImporter() {
	}

	/** Imports an OpenAPI 3.0, 3.1 or 3.2 description in JSON. */
	public static OpenApiModel fromJson(byte[] document) {
		return doImport(document, "json");
	}

	/** Imports an OpenAPI 3.0, 3.1 or 3.2 description in JSON from a stream. */
	public static OpenApiModel fromJson(InputStream document) {
		return fromJson(readAll(document));
	}

	/** Imports an OpenAPI 3.0, 3.1 or 3.2 description in YAML. */
	public static OpenApiModel fromYaml(byte[] document) {
		return doImport(document, "yaml");
	}

	/** Imports an OpenAPI 3.0, 3.1 or 3.2 description in YAML from a stream. */
	public static OpenApiModel fromYaml(InputStream document) {
		return fromYaml(readAll(document));
	}

	private static OpenApiModel doImport(byte[] document, String extension) {
		if (document == null) {
			throw new IllegalArgumentException("document must not be null");
		}
		return new OpenApiImporter().run(document, extension);
	}

	private OpenApiModel run(byte[] document, String extension) {
		OpenAPI openApi = load(document, extension);
		schemasPackage = schemasPackage(openApi);
		if (openApi.getComponents() != null) {
			openApi.getComponents().getSecuritySchemes()
					.forEach(entry -> securitySchemes.put(entry.getKey(), target(entry.getValue())));
		}
		globalSecurity = requirements("document", openApi.getSecurity());

		List<OpenApiOperation> operations = new ArrayList<>();
		for (Map.Entry<String, PathItem> path : openApi.getPaths()) {
			PathItem item = target(path.getValue());
			if (item == null) {
				diagnostics.add("Path '" + path.getKey() + "': unresolved $ref '" + path.getValue().getRef() + "'");
				continue;
			}
			for (Operation operation : item.getAllOperations()) {
				operation(operations, path.getKey(), item, operation);
			}
		}
		if (!openApi.getWebhooks().isEmpty()) {
			diagnostics.add("webhooks are not imported (" + openApi.getWebhooks().size()
					+ "): they are called by the API, not by a client");
		}
		return new OpenApiModel(openApi, schemasPackage, requestsPackage, securitySchemes, operations, diagnostics);
	}

	// --- document loading (codec pipeline) ---------------------------------------------------

	private OpenAPI load(byte[] document, String extension) {
		boolean yaml = "yaml".equals(extension);
		OasVersion version = version(document, yaml);
		// the codec reads YAML through its format provider; the JSON syntax is the default
		OasResourceFactoryImpl factory = new OasResourceFactoryImpl(version, yaml ? new YamlFormatProvider() : null);
		Resource resource = factory.createResource(URI.createURI("import://openapi." + extension));
		Map<String, Object> options = new LinkedHashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OasPackage.Literals.OPEN_API);
		try {
			resource.load(new ByteArrayInputStream(document), options);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not parse the OpenAPI document", e);
		}
		if (resource.getContents().isEmpty() || !(resource.getContents().get(0) instanceof OpenAPI openApi)) {
			throw new IllegalArgumentException("Document did not parse to an OpenAPI model");
		}
		// what the codec could not map (unresolved $refs, unconvertible inline schemas, ...)
		resource.getWarnings().forEach(warning -> diagnostics.add("codec: " + warning.getMessage()));
		return openApi;
	}

	/**
	 * The OpenAPI version of a description, from its top-level {@code openapi} member — the codec
	 * reads each version with its own resource.
	 */
	static OasVersion version(byte[] document, boolean yaml) {
		String declared = null;
		if (yaml) {
			Matcher matcher = YAML_VERSION.matcher(new String(document, StandardCharsets.UTF_8));
			declared = matcher.find() ? matcher.group(1) : null;
		} else {
			try {
				JsonNode openapi = MAPPER.readTree(document).get("openapi");
				declared = openapi == null || !openapi.isString() ? null : openapi.asString();
			} catch (JacksonException e) {
				throw new UncheckedIOException("Could not parse the OpenAPI document",
						new IOException(e.getOriginalMessage(), e));
			}
		}
		OasVersion version = OasVersion.of(declared);
		if (version == null) {
			throw new IllegalArgumentException(declared == null
					? "Not an OpenAPI 3 description: no 'openapi' member"
					: "OpenAPI '" + declared + "' is not supported: 3.0, 3.1 and 3.2 are");
		}
		return version;
	}

	/** The EPackage the codec generated from components/schemas (given an nsURI if the pipeline left none). */
	private EPackage schemasPackage(OpenAPI openApi) {
		Components components = openApi.getComponents();
		EPackage generated = components == null ? null : components.getSchemasPackage();
		if (generated == null) {
			return null;
		}
		if (generated.getName() == null || generated.getName().isBlank()) {
			generated.setName("schemas");
		}
		if (generated.getNsPrefix() == null || generated.getNsPrefix().isBlank()) {
			generated.setNsPrefix("schemas");
		}
		if (generated.getNsURI() == null || generated.getNsURI().isBlank()) {
			generated.setNsURI(SCHEMAS_NS);
		}
		ensureTypedFeatures(generated);
		return generated;
	}

	/**
	 * Safety net: schema-to-schema {@code $ref} resolution is done by the codec (emf.codec #43),
	 * but a genuinely unresolvable ref can still leave a feature untyped — which downstream
	 * consumers (e.g. the metadata service) trip over. Degrade those to
	 * {@code EObject}/{@code EString} with a diagnostic instead of a broken model.
	 */
	private void ensureTypedFeatures(EPackage generated) {
		for (EClassifier classifier : generated.getEClassifiers()) {
			if (!(classifier instanceof EClass eClass)) {
				continue;
			}
			for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
				if (feature.getEType() != null) {
					continue;
				}
				EAnnotation a = feature.getEAnnotation(JSONSCHEMA_SOURCE);
				String ref = a == null ? null : a.getDetails().get("ref");
				feature.setEType(feature instanceof EReference
						? EcorePackage.eINSTANCE.getEObject()
						: EcorePackage.eINSTANCE.getEString());
				diagnostics.add("Schema feature " + eClass.getName() + "." + feature.getName()
						+ ": unresolved $ref '" + ref + "' — typed as " + feature.getEType().getName());
			}
		}
		generated.getESubpackages().forEach(this::ensureTypedFeatures);
	}

	// --- operation linking -------------------------------------------------------------------

	private void operation(List<OpenApiOperation> target, String path, PathItem item, Operation operation) {
		String method = operation.getMethod() == null ? null : operation.getMethod().toUpperCase(Locale.ROOT);
		if (method == null) {
			diagnostics.add("Path '" + path + "': operation without HTTP method skipped");
			return;
		}
		String name = operation.getOperationId() == null || operation.getOperationId().isBlank()
				? fallbackName(method, path)
				: operation.getOperationId();

		EClass body = bodyType(name, target(operation.getRequestBody()));
		EClass request = requestType(name, parameters(name, item, operation), body);

		EClass response = null;
		EDataType responseValue = null;
		boolean many = false;
		Schema responseSchema = responseSchema(operation);
		if (responseSchema != null) {
			EClassifier classifier = responseSchema.getClassifier();
			if (classifier instanceof EClass eClass) {
				response = eClass;
			} else if (isValueType(classifier)) {
				responseValue = (EDataType) classifier;
			} else {
				diagnostics.add("Operation '" + name + "': the response schema has no type a client can read");
			}
			many = isArray(responseSchema) && (response != null || responseValue != null);
		}
		target.add(new OpenApiOperation(name, method, path, request, response, many, security(name, operation),
				responseValue));
	}

	/**
	 * The operation's effective security: its own {@code security} when declared — an explicit
	 * {@code security: []} included, which lifts the global requirement — otherwise the document's
	 * global one.
	 */
	private List<Map<String, List<String>>> security(String operation, Operation declared) {
		if (!declared.isSetSecurity()) {
			return globalSecurity;
		}
		return requirements("operation '" + operation + "'", declared.getSecurity());
	}

	/** {@link SecurityRequirement}s as plain immutable maps (scheme name → required scopes). */
	private List<Map<String, List<String>>> requirements(String context, List<SecurityRequirement> declared) {
		List<Map<String, List<String>>> result = new ArrayList<>();
		for (SecurityRequirement requirement : declared) {
			Map<String, List<String>> schemes = new LinkedHashMap<>();
			for (Map.Entry<String, EList<String>> entry : requirement.getSchemes().entrySet()) {
				if (!securitySchemes.containsKey(entry.getKey())) {
					diagnostics.add(context + ": security requirement references undeclared scheme '"
							+ entry.getKey() + "'");
				}
				schemes.put(entry.getKey(), entry.getValue() == null ? List.of() : List.copyOf(entry.getValue()));
			}
			result.add(Collections.unmodifiableMap(schemes));
		}
		return Collections.unmodifiableList(result);
	}

	/** The JSON body EClass of the operation's requestBody, or {@code null}. */
	private EClass bodyType(String operation, RequestBody requestBody) {
		if (requestBody == null) {
			return null;
		}
		MediaType json = target(requestBody.getContent().get(JSON));
		if (json == null) {
			if (!requestBody.getContent().isEmpty()) {
				diagnostics.add("Operation '" + operation + "': request body has no " + JSON + " content (v1 skips it)");
			}
			return null;
		}
		Schema schema = json.getSchema();
		if (schema == null) {
			return null;
		}
		if (!(schema.getClassifier() instanceof EClass eClass)) {
			diagnostics.add("Operation '" + operation + "': request body is not a JSON object (v1 skips it)");
			return null;
		}
		if (isArray(schema)) {
			diagnostics.add("Operation '" + operation + "': request body is a JSON array (v1 skips it)");
			return null;
		}
		return eClass;
	}

	/**
	 * The parameters of an operation: the path item's, overridden by the operation's own of the
	 * same name and location; {@code $ref}s resolved.
	 */
	private List<Parameter> parameters(String operation, PathItem item, Operation declared) {
		Map<String, Parameter> parameters = new LinkedHashMap<>();
		for (List<Parameter> level : List.of(item.getParameters(), declared.getParameters())) {
			for (Parameter parameter : level) {
				Parameter resolved = target(parameter);
				if (resolved == null) {
					diagnostics.add("Operation '" + operation + "': parameter $ref '" + parameter.getRef()
							+ "' unresolved, skipped");
					continue;
				}
				parameters.put(resolved.getIn() + ":" + resolved.getName(), resolved);
			}
		}
		return new ArrayList<>(parameters.values());
	}

	/**
	 * The request type: the body EClass itself when there are no parameters, otherwise a synthetic
	 * {@code <Name>Request} EClass folding the parameters (annotated {@code in=…}, and
	 * {@code style}/{@code explode} when declared; an {@code array} parameter becomes a many-valued
	 * attribute of its item type) + optional body.
	 */
	private EClass requestType(String operation, List<Parameter> parameters, EClass body) {
		if (parameters.isEmpty()) {
			return body;
		}
		EClass request = EcoreFactory.eINSTANCE.createEClass();
		request.setName(capitalize(operation) + "Request");
		for (Parameter parameter : parameters) {
			if (parameter.getName() == null || parameter.getIn() == null || parameter.getIn() == ParameterLocation.NULL) {
				diagnostics.add("Operation '" + operation + "': parameter without name/in skipped");
				continue;
			}
			if (parameter.getIn() == ParameterLocation.QUERYSTRING) {
				diagnostics.add("Operation '" + operation + "': querystring parameter '" + parameter.getName()
						+ "' skipped (v1)");
				continue;
			}
			EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
			attribute.setName(parameter.getName());
			Schema schema = parameter.getSchema();
			attribute.setEType(parameterType(schema));
			if (schema != null && isArray(schema)) {
				// an array parameter: many-valued, typed by its items (serialized per style/explode)
				attribute.setUpperBound(EStructuralFeature.UNBOUNDED_MULTIPLICITY);
			}
			boolean required = Boolean.TRUE.equals(parameter.getRequired()) || parameter.getIn() == ParameterLocation.PATH;
			attribute.setLowerBound(required ? 1 : 0);
			OpenApiAnnotations.setIn(attribute, parameter.getIn().getLiteral());
			if (parameter.getStyle() != null && parameter.getStyle() != ParameterStyle.NULL) {
				OpenApiAnnotations.setStyle(attribute, parameter.getStyle().getLiteral());
			}
			if (parameter.isSetExplode() && parameter.getExplode() != null) {
				OpenApiAnnotations.setExplode(attribute, parameter.getExplode());
			}
			request.getEStructuralFeatures().add(attribute);
		}
		if (request.getEStructuralFeatures().isEmpty()) {
			return body; // every parameter was skipped
		}
		if (body != null) {
			EReference bodyRef = EcoreFactory.eINSTANCE.createEReference();
			bodyRef.setName("body");
			bodyRef.setEType(body);
			bodyRef.setContainment(true);
			OpenApiAnnotations.setIn(bodyRef, OpenApiAnnotations.IN_BODY);
			request.getEStructuralFeatures().add(bodyRef);
		}
		requestsPackage().getEClassifiers().add(request);
		return request;
	}

	/**
	 * The 2xx (preferring 200/201) or {@code default} response schema, or {@code null}: the
	 * {@code application/json} one, else a scalar {@code text/plain} one.
	 */
	private Schema responseSchema(Operation operation) {
		Response chosen = null;
		for (String key : new String[] { "200", "201" }) {
			chosen = operation.getResponses().get(key);
			if (chosen != null) {
				break;
			}
		}
		if (chosen == null) {
			for (Map.Entry<String, Response> entry : operation.getResponses()) {
				if (entry.getKey() != null && entry.getKey().startsWith("2")) {
					chosen = entry.getValue();
					break;
				}
			}
		}
		if (chosen == null) {
			chosen = operation.getResponses().get("default");
		}
		chosen = target(chosen);
		if (chosen == null) {
			return null;
		}
		MediaType json = target(chosen.getContent().get(JSON));
		if (json != null) {
			return json.getSchema();
		}
		MediaType text = target(chosen.getContent().get(TEXT));
		return text != null && text.getSchema() != null && isValueType(text.getSchema().getClassifier())
				? text.getSchema()
				: null;
	}

	// --- helpers -----------------------------------------------------------------------------

	private EPackage requestsPackage() {
		if (requestsPackage == null) {
			requestsPackage = EcoreFactory.eINSTANCE.createEPackage();
			requestsPackage.setName("requests");
			requestsPackage.setNsPrefix("requests");
			requestsPackage.setNsURI(REQUESTS_NS);
		}
		return requestsPackage;
	}

	/** A Reference Object's target (linked by the codec), the object itself when it is none, {@code null} if unresolved. */
	private static <T extends EObject> T target(T object) {
		if (object == null) {
			return null;
		}
		EStructuralFeature ref = object.eClass().getEStructuralFeature("ref");
		EStructuralFeature target = object.eClass().getEStructuralFeature("target");
		if (ref == null || target == null || object.eGet(ref) == null) {
			return object;
		}
		@SuppressWarnings("unchecked")
		T resolved = (T) object.eGet(target);
		return resolved == null ? null : target(resolved);
	}

	/**
	 * Whether a schema is a JSON array — its classifier is then the item type. A nullable union
	 * ({@code anyOf: [<array>, {type: null}]}) counts as the array.
	 */
	private static boolean isArray(Schema schema) {
		if (schema.getType().contains(SimpleType.ARRAY)) {
			return true;
		}
		if (schema.getType().isEmpty() && schema.getItems() != null) {
			return true;
		}
		List<Schema> union = !schema.getAnyOf().isEmpty() ? schema.getAnyOf() : schema.getOneOf();
		if (union.size() == 2) {
			for (int i = 0; i < 2; i++) {
				Schema other = union.get(1 - i);
				if (union.get(i).getType().size() == 1 && union.get(i).getType().get(0) == SimpleType.NULL) {
					return isArray(other);
				}
			}
		}
		return false;
	}

	/** Whether a use site's classifier is a value a client can read: a data type other than "any". */
	private static boolean isValueType(EClassifier classifier) {
		return classifier instanceof EDataType && classifier != EcorePackage.Literals.EJAVA_OBJECT;
	}

	/** The Ecore data type of a parameter: its schema's (item) data type, else {@code EString}. */
	private static EClassifier parameterType(Schema schema) {
		EClassifier classifier = schema == null ? null : schema.getClassifier();
		return isValueType(classifier) ? classifier : EcorePackage.eINSTANCE.getEString();
	}

	private static String fallbackName(String method, String path) {
		return method.toLowerCase(Locale.ROOT) + path.replaceAll("[{}]", "").replace('/', '_');
	}

	private static String capitalize(String name) {
		return name.isEmpty() ? name : Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	private static byte[] readAll(InputStream in) {
		try (in) {
			return in.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException("Could not read the OpenAPI document", e);
		}
	}
}
