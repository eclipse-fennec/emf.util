# EMF ⇄ OpenAPI / REST

> **Status: v1.** Implemented and tested (incl. against the public Swagger petstore); the API may
> still evolve.

Turn an **OpenAPI 3.0, 3.1 or 3.2** description into an EMF model and **call its operations** over HTTP — request
and response are `EObject`s of the imported types. Ideal for interop: point at a service's
`openapi.json`, get typed operations, and invoke them (or drive them from a generated form).

- **Input:** an OpenAPI 3.0, 3.1 or 3.2 description (JSON or YAML) — the version is taken from its
  `openapi` member; Swagger 2.0 is rejected.
- **Output:** the `components/schemas` as an `EPackage`, the path operations as
  [`OpenApiOperation`](/guides/service-clients)s, and a `ServiceClient` to invoke them.
- **Round-trips:** JSON request/response bodies via the Fennec codec.

The schema→Ecore conversion is done by the **Fennec codec** (`org.eclipse.fennec.codec.jsonschema`
via `org.eclipse.fennec.codec.openapi`): every version is read into the codec's **`oas` model**
(`org.eclipse.fennec.model.oas`), which keeps the schemas as JSON Schema, derives their Ecore and
links each use site to its classifier (`Schema.getClassifier()`), each `$ref` to its target and each
operation to its HTTP method. This utility links the *operations* to those classifiers and adds the
HTTP client — mirroring the SOAP/WSDL utility. Importer, client and caller share one set of
classes, for every version.

## Quick start

```java
byte[] doc = Files.readAllBytes(Path.of("petstore.openapi.json"));
OpenApiModel model = OpenApiImporter.fromJson(doc);          // or fromYaml(...)

try (OpenApiServiceClient client = new OpenApiServiceClient(
        URI.create("https://host/api/v3"), model)) {

    // body-only POST: the request IS the body
    EClass pet = (EClass) model.schemasPackage().getEClassifier("Pet");
    EObject rex = EcoreUtil.create(pet);
    rex.eSet(pet.getEStructuralFeature("name"), "Rex");
    EObject created = client.invoke("addPet", rex);

    // GET with parameters: fill the synthetic request EClass
    OpenApiOperation getPet = model.operation("getPetById");
    EObject req = EcoreUtil.create(getPet.requestType());
    req.eSet(getPet.requestType().getEStructuralFeature("petId"), 42L);
    EObject fetched = client.invoke(getPet, req);
}
```

## Importing: OpenAPI → operations

`OpenApiImporter.fromJson(...)` / `fromYaml(...)` returns an `OpenApiModel`:

```java
model.document();         // the codec's oas model (OpenAPI 3.0, 3.1 and 3.2 alike)
model.schemasPackage();   // EPackage generated from components/schemas (EClass per schema),
                          // inline schemas in its sub-package "inline"
model.generatedPackages(); // schemas + sub-packages + requests: what a registry has to know
model.operations();       // List<OpenApiOperation>
model.operation("addPet");
model.registerInto(resourceSet);   // register the generated packages for (de)serialization
model.diagnostics();      // codec warnings, non-JSON content, skipped parameters, repaired refs, …
```

Each `OpenApiOperation` carries the HTTP binding (`httpMethod()`, `pathTemplate()`) and:

- **`requestType()`** — for a **body-only** operation, the body `EClass` itself; for an operation
  **with parameters** (its own and its path item's; `$ref`s resolved), a **synthetic request `EClass`** `<Name>Request` whose features carry an
  `in=path|query|header|cookie|body` annotation (source `http://eclipse.org/fennec/openapi`). A
  required parameter has `lowerBound = 1`; an `array` parameter is a **many-valued** attribute of its
  item type, and a declared `style`/`explode` is kept as annotation detail. So every request is
  *one* `EObject` — perfect for a form.
- **`responseType()`** / **`responseMany()`** — the first `2xx` (or `default`) `application/json`
  schema; a JSON array resolves to its item `EClass` with `responseMany() == true`.
- **`responseDataType()`** — for a **scalar** answer (`string`, `integer`, `number`, `boolean`, or
  an array of them; also as `text/plain`) its Ecore data type, as the codec maps it: `EString`;
  `EIntegerObject` for `int32`, else `ELongObject`; `EFloatObject` for `float`, else
  `EDoubleObject`; `EBooleanObject`. `responseType()` is then `null`. Parameters are typed the same
  way.

Request and response types are the codec's classifiers — for a `$ref` the component's `EClass`,
for an **inline** object schema its class in the `inline` sub-package. 3.1/3.2 schema constructs
(type arrays instead of `nullable`, `const`, numeric `exclusiveMinimum`/`exclusiveMaximum`,
`examples`, `$ref` with siblings) come from the codec's JSON Schema mapping and need nothing
special on the client side.

### How the client uses the request

`OpenApiServiceClient` reads the `in=` annotations to build the call: `path` values fill
`{placeholders}`, `query`/`header`/`cookie` go where they belong, and the `body` feature (or the
whole request, for body-only operations) is serialized as JSON. The response JSON is deserialized
into `responseType()`. Payloads are serialized **without** the EMF `_type` discriminator, so the
wire is plain interop JSON.

Parameters follow OpenAPI's `style`/`explode` rules: query `form` (exploded by default —
`tags=a&tags=b`), `spaceDelimited`, `pipeDelimited`; path `simple` (`a,b`), `label`, `matrix`;
header `simple`; cookie `form`. An empty optional array is not sent.

The answer decides the method:

```java
EObject pet        = client.invoke("getPetById", request);        // one object
List<EObject> pets = client.invokeMany("findPetsByStatus", req);  // JSON array of objects, in order
Object token       = client.invokeValue("loginUser", request);    // scalar (JSON or plain text);
                                                                  // a List for an array of scalars
```

Calling the wrong one fails before any HTTP and names the right one.

- `withHeader(name, value)` adds a header to every request.
- `unwrap(HttpClient.class)` gives you the native client.
- An HTTP error status (`>= 400`) becomes an **`HttpStatusException`** (a
  `ServiceInvocationException`) carrying `status()`, `body()`/`bodyText()` and `contentType()` —
  map statuses to declared errors without parsing the message. A transport or JSON parse failure
  becomes a plain `ServiceInvocationException`.

## Authentication

The importer reads `components/securitySchemes` (`model.securitySchemes()`) and each operation's
**effective** security requirements (`operation.security()` — its own `security`, falling back to
the document's global one; an explicit `security: []` lifts the global requirement). On the client you register **credentials per scheme name**; *where*
the credential goes (header/query/cookie, parameter name, token URL) always comes from the
document, only the secret comes from you:

```java
client.withAuth("api_key", OpenApiAuth.apiKey("secret"))               // apiKey: header/query/cookie per scheme
      .withAuth("basic_auth", OpenApiAuth.basic("scott", "tiger"))     // http/basic
      .withAuth("bearer_auth", OpenApiAuth.bearer(tokenSupplier))      // http/bearer, oauth2, openIdConnect
      .withAuth("oauth", OpenApiAuth.clientCredentials(id, secret));   // oauth2 client_credentials
// document declares exactly one scheme? then simply:
client.withAuth(OpenApiAuth.apiKey("secret"));
```

Per invocation the client picks the **first requirement alternative** whose schemes all have
registered credentials (an empty alternative — anonymous allowed — matches trivially) and applies
them. A required but unregistered scheme fails fast with a `ServiceInvocationException` *before*
any HTTP; so does a credential that cannot satisfy the scheme's type (e.g. `apiKey` credentials
on an `http/basic` scheme).

Building a request yourself (for what the client does not cover)? Apply the same credentials
with `client.authenticate(operation, target)` — it picks the requirement alternative exactly as
`invoke` does — or, for one known scheme, `credentials.apply(schemeName, scheme, scopes, target)`;
`target` is your `OpenApiAuth.Target` (header/query/cookie sink plus the `HttpClient` for token
requests).

`clientCredentials` POSTs to the flow's declared `tokenUrl` (client id/secret via HTTP Basic,
the requirement's scopes as `scope`), caches the `access_token` and re-fetches shortly before
`expires_in` runs out. The interactive `authorization_code` flow is deliberately out of scope for
a headless client — obtain the token elsewhere and pass it via `OpenApiAuth.bearer(...)`.

## OSGi

Bundle `org.eclipse.fennec.openapi.osgi` publishes a **configuration-driven** `ServiceClient` as
an OSGi service — the client counterpart of the Protobuf/SOAP `Resource.Factory` components. Each
ConfigurationAdmin **factory** configuration for PID `OpenApiServiceClient` imports one document and
registers one ready-to-invoke `ServiceClient`:

| Property | Meaning |
| --- | --- |
| `name` | optional stable, human-chosen name — published as the `name` service property (`ServiceClient.PROP_NAME`) so consumers can select/label the client (e.g. an MCP tool bridge deriving tool names); must be unique among clients sharing a `MetadataWhiteboard` |
| `documentUrl` | URL the OpenAPI 3.0/3.1/3.2 description is fetched from (`http`/`https`/`file`) |
| `baseUri` | base URI of the target service, e.g. `https://api.example.com/v1` |
| `format` | `json` (default) or `yaml` |
| `auth` | credentials, one entry per scheme: `<scheme>=<type>:<params>` — `apiKey:<key>`, `bearer:<token>`, `basic:<user>:<pass>`, `clientCredentials:<id>:<secret>` (empty `<scheme>` for a single-scheme document) |

The component binds the framework's shared `MetadataWhiteboard`. Because the generated packages
have nsURIs that depend only on the document (`urn:openapi:<title>:<version>`, the constant
`requests` one) and the whiteboard keys metadata by
nsURI, each configuration first rewrites those nsURIs to be unique to itself — so several OpenAPI
clients can share one whiteboard without colliding; the packages are unregistered on deactivation.
The isolation token is the configured `name` when set (keeping the rewritten nsURIs stable across
restarts), else the `service.pid`.
Consumers `@Reference` the `ServiceClient` and must **not** `close()` it (its lifecycle follows the
configuration). The bundle inlines the `openapi.client`/`openapi.ecore` runtime packages (like the
SOAP `.osgi` bundle) but still needs the codec bundles at runtime.

## Scope / limitations (v1)

- **Request-response, `application/json`** (plus scalar `text/plain` answers); streaming/SSE is
  out of scope.
- **Object bodies.** A request body must be a JSON object (`$ref` or inline); array or scalar
  bodies, RPC-style and non-JSON content are skipped with a diagnostic.
- **OpenAPI 3.2:** the `query` method and `additionalOperations` are imported (any HTTP method);
  `querystring` parameters and streaming `itemSchema` content are skipped with a diagnostic.
  Webhooks (3.1+) are not operations a client calls — only noted in the diagnostics.
- **Parameter styles:** `deepObject` (objects as parameters) is not supported and rejected.
- **YAML** documents are read through the codec's `YamlFormatProvider`
  (`org.eclipse.fennec.codec.yaml`, plus `jackson-dataformat-yaml` and `snakeyaml-engine` at runtime).
- **Dynamic-mode client** (JDK `HttpClient` + codec). A Jersey/`codec.rest` variant and a typed
  proxy are on the [service-clients](/guides/service-clients) roadmap.
- **Codec version:** requires a fennecCodec snapshot with the `oas` model
  ([emf.codec#321](https://github.com/eclipse-fennec/emf.codec/pull/321), 2026-11-15 or later). Even older ones left
  schema-to-schema `$ref` features untyped ([emf.codec#43](https://github.com/eclipse-fennec/emf.codec/issues/43))
  and dropped the scheme names of security requirements
  ([emf.codec#44](https://github.com/eclipse-fennec/emf.codec/issues/44)). A genuinely
  unresolvable `$ref` is degraded to `EObject`/`EString` with a diagnostic (safety net).
- **Auth v1:** `apiKey`, `http` basic/bearer, `oauth2` `client_credentials` (or a pre-obtained
  bearer token). No `authorization_code`/`implicit` flows, no `openIdConnect` discovery.

## Dependencies

The importer needs the Fennec codec bundles (`org.eclipse.fennec.codec.openapi`,
`…codec.jsonschema`, `…codec.jsonschema.model`, `…codec`, `…openapi.model`) + Jackson; the client adds `org.eclipse.fennec.codec`
for JSON marshalling. All are provided by the `fennecCodec` bnd library. The metadata service
(`org.eclipse.fennec.emf.osgi.metadata`) comes with the `fennecEMF` library; outside OSGi,
`MetadataServiceFactory.create()` additionally needs `org.eclipse.fennec.emf.osgi.component.minimal`
on the classpath (it supplies the default `FingerprintService`).
