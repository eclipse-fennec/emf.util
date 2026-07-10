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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.codec.jsonschema.v2.value.EPackageValueReader;
import org.eclipse.fennec.codec.openapi.OpenApiResourceImpl;
import org.eclipse.fennec.codec.openapi.OpenApiSchemasValueWriter;
import org.eclipse.fennec.codec.openapi.OperationValueReader;
import org.eclipse.fennec.codec.resource.CodecResource;
import org.eclipse.fennec.codec.util.MetadataServiceFactory;
import org.eclipse.fennec.codec.value.CodecValueRegistry;
import org.eclipse.fennec.model.openapi.Components;
import org.eclipse.fennec.model.openapi.MediaType;
import org.eclipse.fennec.model.openapi.OpenAPI;
import org.eclipse.fennec.model.openapi.OpenApiPackage;
import org.eclipse.fennec.model.openapi.Operation;
import org.eclipse.fennec.model.openapi.Parameter;
import org.eclipse.fennec.model.openapi.PathItem;
import org.eclipse.fennec.model.openapi.RequestBody;
import org.eclipse.fennec.model.openapi.Response;
import org.eclipse.fennec.model.openapi.Schema;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.eclipse.fennec.model.openapi.SecurityRequirement;
import org.eclipse.fennec.model.openapi.SecurityScheme;

/**
 * Derives {@link OpenApiOperation}s (plus their request/response {@link EClass}es) from an
 * OpenAPI 3 document — the REST counterpart of the WSDL/gRPC importers. The heavy lifting is
 * done by the Fennec codec: loading the document yields the {@code openapi.model} EMF
 * representation, and {@code components/schemas} is converted to an {@link EPackage} by the
 * JSON-Schema pipeline during load. This class only <b>links</b> the path operations to those
 * EClasses:
 * <ul>
 * <li>body-only operation → {@code requestType} is the body EClass itself;</li>
 * <li>operation with parameters → a <b>synthetic request EClass</b> ({@code <Name>Request}) whose
 * features carry {@code in=path|query|header|cookie} annotations, plus an optional {@code body}
 * containment reference — so a form UI / client can treat every request uniformly as one EObject;</li>
 * <li>response → the first 2xx (or {@code default}) {@code application/json} schema; a JSON array
 * resolves to its item EClass with {@link OpenApiOperation#responseMany()};</li>
 * <li>security → {@code components/securitySchemes} is exposed on the model as-is; each
 * operation carries its effective requirements (own {@code security}, else the global one).</li>
 * </ul>
 * Inline (unnamed) schemas and non-JSON content are recorded as diagnostics, not resolved (v1).
 */
public final class OpenApiImporter {

	private static final String JSON = "application/json";
	/** nsURI given to the generated schemas package when the pipeline leaves it unset. */
	private static final String SCHEMAS_NS = "http://eclipse.org/fennec/openapi/generated/schemas";
	private static final String REQUESTS_NS = "http://eclipse.org/fennec/openapi/generated/requests";
	/** Annotation source the codec's JSON-Schema converter stamps on generated features. */
	private static final String JSONSCHEMA_SOURCE = "http://fennec.eclipse.org/jsonschema";

	private final List<String> diagnostics = new ArrayList<>();
	private final Map<String, SecurityScheme> securitySchemes = new LinkedHashMap<>();
	private List<Map<String, List<String>>> globalSecurity = List.of();
	private EPackage schemasPackage;
	private EPackage requestsPackage;

	private OpenApiImporter() {
	}

	/** Imports an OpenAPI 3 document in JSON. */
	public static OpenApiModel fromJson(byte[] document) {
		return doImport(document, "json");
	}

	/** Imports an OpenAPI 3 document in JSON from a stream. */
	public static OpenApiModel fromJson(InputStream document) {
		return fromJson(readAll(document));
	}

	/** Imports an OpenAPI 3 document in YAML. */
	public static OpenApiModel fromYaml(byte[] document) {
		return doImport(document, "yaml");
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
					.forEach(entry -> securitySchemes.put(entry.getKey(), entry.getValue()));
		}
		globalSecurity = requirements("document", openApi.getSecurity());

		List<OpenApiOperation> operations = new ArrayList<>();
		if (openApi.getPaths() != null) {
			for (Map.Entry<String, PathItem> path : openApi.getPaths().entrySet()) {
				PathItem item = path.getValue();
				operation(operations, "GET", path.getKey(), item.getGet());
				operation(operations, "PUT", path.getKey(), item.getPut());
				operation(operations, "POST", path.getKey(), item.getPost());
				operation(operations, "DELETE", path.getKey(), item.getDelete());
				operation(operations, "OPTIONS", path.getKey(), item.getOptions());
				operation(operations, "HEAD", path.getKey(), item.getHead());
				operation(operations, "PATCH", path.getKey(), item.getPatch());
				operation(operations, "TRACE", path.getKey(), item.getTrace());
			}
		}
		return new OpenApiModel(openApi, schemasPackage, requestsPackage, securitySchemes, operations, diagnostics);
	}

	// --- document loading (codec pipeline) ---------------------------------------------------

	/**
	 * Loads the document through the codec's OpenAPI pipeline. The resource is assembled here
	 * (instead of {@code OpenApiResourceFactoryImpl}) so the {@link SecurityRequirementValueReader}
	 * can join the value registry; the {@code security} features are bound to it via the
	 * {@code ClassName.featureName → valueReaderName} load options. Once the reader ships in the
	 * codec's own factory, this collapses back to {@code new OpenApiResourceFactoryImpl()}.
	 */
	private OpenAPI load(byte[] document, String extension) {
		MetadataWhiteboard whiteboard = MetadataServiceFactory.create();
		whiteboard.registerPackage(OpenApiPackage.eINSTANCE);
		CodecValueRegistry registry = new CodecValueRegistry();
		registry.register(new OperationValueReader());
		registry.register(new EPackageValueReader());
		registry.register(new OpenApiSchemasValueWriter());
		registry.register(new SecurityRequirementValueReader());
		Resource resource = new OpenApiResourceImpl(URI.createURI("import://openapi." + extension),
				whiteboard, registry);

		Map<String, Object> options = new LinkedHashMap<>();
		options.put(CodecResource.CODEC_ROOT_TYPE, OpenApiPackage.Literals.OPEN_API);
		Map<String, Object> securityReader = Map.of("valueReaderName", SecurityRequirementValueReader.NAME);
		options.put("OpenAPI.security", securityReader);
		options.put("Operation.security", securityReader);
		try {
			resource.load(new ByteArrayInputStream(document), options);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not parse the OpenAPI document", e);
		}
		if (resource.getContents().isEmpty() || !(resource.getContents().get(0) instanceof OpenAPI openApi)) {
			throw new IllegalArgumentException("Document did not parse to an OpenAPI model");
		}
		return openApi;
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
		repairDanglingRefs(generated);
		return generated;
	}

	/**
	 * The codec's JSON-Schema converter leaves a schema-to-schema {@code $ref} feature with a
	 * {@code null} eType (recording the ref only as an annotation) — which downstream consumers
	 * (e.g. the metadata service) trip over. Re-wire those features against the classifiers of
	 * the same package; an unresolvable ref degrades to {@code EObject}/{@code EString} with a
	 * diagnostic instead of a broken model.
	 */
	private void repairDanglingRefs(EPackage generated) {
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
				EClassifier target = null;
				if (ref != null && !ref.isBlank()) {
					target = generated.getEClassifier(capitalize(ref.substring(ref.lastIndexOf('/') + 1)));
				}
				if (target != null) {
					feature.setEType(target);
				} else {
					feature.setEType(feature instanceof EReference
							? EcorePackage.eINSTANCE.getEObject()
							: EcorePackage.eINSTANCE.getEString());
					diagnostics.add("Schema feature " + eClass.getName() + "." + feature.getName()
							+ ": unresolved $ref '" + ref + "' — typed as "
							+ feature.getEType().getName());
				}
			}
		}
	}

	// --- operation linking -------------------------------------------------------------------

	private void operation(List<OpenApiOperation> target, String method, String path, Operation operation) {
		if (operation == null) {
			return;
		}
		String name = operation.getOperationId() == null || operation.getOperationId().isBlank()
				? fallbackName(method, path)
				: operation.getOperationId();

		EClass body = bodyType(name, operation.getRequestBody());
		EClass request = requestType(name, operation.getParameters(), body);

		EClass response = null;
		boolean many = false;
		Schema responseSchema = responseSchema(operation);
		if (responseSchema != null) {
			if ("array".equals(responseSchema.getType()) && responseSchema.getItems() != null) {
				response = resolve(name, responseSchema.getItems());
				many = response != null;
			} else {
				response = resolve(name, responseSchema);
			}
		}
		target.add(new OpenApiOperation(name, method, path, request, response, many, security(name, operation)));
	}

	/**
	 * The operation's effective security: its own {@code security} when declared, otherwise the
	 * document's global one. (EMF cannot distinguish an absent list from an explicit
	 * {@code security: []} — both fall back to the global requirements.)
	 */
	private List<Map<String, List<String>>> security(String operation, Operation declared) {
		if (declared.getSecurity().isEmpty()) {
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
		if (requestBody == null || requestBody.getContent() == null) {
			return null;
		}
		MediaType json = requestBody.getContent().get(JSON);
		if (json == null) {
			if (!requestBody.getContent().isEmpty()) {
				diagnostics.add("Operation '" + operation + "': request body has no " + JSON + " content (v1 skips it)");
			}
			return null;
		}
		return resolve(operation, json.getSchema());
	}

	/**
	 * The request type: the body EClass itself when there are no parameters, otherwise a synthetic
	 * {@code <Name>Request} EClass folding the parameters (annotated {@code in=…}) + optional body.
	 */
	private EClass requestType(String operation, List<Parameter> parameters, EClass body) {
		if (parameters == null || parameters.isEmpty()) {
			return body;
		}
		EClass request = EcoreFactory.eINSTANCE.createEClass();
		request.setName(capitalize(operation) + "Request");
		for (Parameter parameter : parameters) {
			if (parameter.getName() == null || parameter.getIn() == null) {
				diagnostics.add("Operation '" + operation + "': parameter without name/in skipped");
				continue;
			}
			EAttribute attribute = EcoreFactory.eINSTANCE.createEAttribute();
			attribute.setName(parameter.getName());
			attribute.setEType(parameterType(parameter.getSchema()));
			attribute.setLowerBound(parameter.isRequired() ? 1 : 0);
			OpenApiAnnotations.setIn(attribute, parameter.getIn().getLiteral());
			request.getEStructuralFeatures().add(attribute);
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

	/** The 2xx (preferring 200/201) or {@code default} JSON response schema, or {@code null}. */
	private Schema responseSchema(Operation operation) {
		if (operation.getResponses() == null) {
			return null;
		}
		Response chosen = null;
		for (String key : new String[] { "200", "201" }) {
			chosen = operation.getResponses().get(key);
			if (chosen != null) {
				break;
			}
		}
		if (chosen == null) {
			for (Map.Entry<String, Response> entry : operation.getResponses().entrySet()) {
				if (entry.getKey() != null && entry.getKey().startsWith("2")) {
					chosen = entry.getValue();
					break;
				}
			}
		}
		if (chosen == null) {
			chosen = operation.getResponses().get("default");
		}
		if (chosen == null || chosen.getContent() == null) {
			return null;
		}
		MediaType json = chosen.getContent().get(JSON);
		return json == null ? null : json.getSchema();
	}

	/** Resolves a schema's {@code $ref} against the generated schemas package. */
	private EClass resolve(String operation, Schema schema) {
		if (schema == null) {
			return null;
		}
		String ref = schema.getRef();
		if (ref == null || ref.isBlank()) {
			if (schema.getType() != null && !"array".equals(schema.getType())) {
				diagnostics.add("Operation '" + operation + "': inline schema not resolved (v1 resolves $ref only)");
			}
			return null;
		}
		if (schemasPackage == null) {
			diagnostics.add("Operation '" + operation + "': " + ref + " referenced but the document has no components/schemas");
			return null;
		}
		String name = capitalize(ref.substring(ref.lastIndexOf('/') + 1));
		EClassifier classifier = schemasPackage.getEClassifier(name);
		if (!(classifier instanceof EClass eClass)) {
			diagnostics.add("Operation '" + operation + "': " + ref + " did not resolve to an EClass");
			return null;
		}
		return eClass;
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

	/** The Ecore data type for an OpenAPI parameter schema type. */
	private static EClassifier parameterType(Schema schema) {
		EcorePackage ecore = EcorePackage.eINSTANCE;
		String type = schema == null ? null : schema.getType();
		if (type == null) {
			return ecore.getEString();
		}
		return switch (type) {
		case "integer" -> ecore.getELong();
		case "number" -> ecore.getEDouble();
		case "boolean" -> ecore.getEBoolean();
		default -> ecore.getEString();
		};
	}

	private static String fallbackName(String method, String path) {
		return method.toLowerCase() + path.replaceAll("[{}]", "").replace('/', '_');
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
