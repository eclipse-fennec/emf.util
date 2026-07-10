# EMF ⇄ OpenAPI / REST

> **Status: v1.** Implemented and tested (incl. against the public Swagger petstore); the API may
> still evolve.

Turn an **OpenAPI 3** document into an EMF model and **call its operations** over HTTP — request
and response are `EObject`s of the imported types. Ideal for interop: point at a service's
`openapi.json`, get typed operations, and invoke them (or drive them from a generated form).

- **Input:** an OpenAPI 3 document (JSON or YAML).
- **Output:** the `components/schemas` as an `EPackage`, the path operations as
  [`OpenApiOperation`](/guides/service-clients)s, and a `ServiceClient` to invoke them.
- **Round-trips:** JSON request/response bodies via the Fennec codec.

The schema→Ecore conversion is done by the **Fennec codec** (`org.eclipse.fennec.codec.jsonschema`
via `org.eclipse.fennec.codec.openapi`); this utility links the *operations* to those EClasses and
adds the HTTP client — mirroring the SOAP/WSDL utility.

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
model.schemasPackage();   // EPackage generated from components/schemas (EClass per schema)
model.operations();       // List<OpenApiOperation>
model.operation("addPet");
model.registerInto(resourceSet);   // register the generated packages for (de)serialization
model.diagnostics();      // unresolved inline schemas, non-JSON content, repaired refs, …
```

Each `OpenApiOperation` carries the HTTP binding (`httpMethod()`, `pathTemplate()`) and:

- **`requestType()`** — for a **body-only** operation, the body `EClass` itself; for an operation
  **with parameters**, a **synthetic request `EClass`** `<Name>Request` whose features carry an
  `in=path|query|header|cookie|body` annotation (source `http://eclipse.org/fennec/openapi`). A
  required parameter has `lowerBound = 1`. So every request is *one* `EObject` — perfect for a form.
- **`responseType()`** / **`responseMany()`** — the first `2xx` (or `default`) `application/json`
  schema; a JSON array resolves to its item `EClass` with `responseMany() == true`.

### How the client uses the request

`OpenApiServiceClient` reads the `in=` annotations to build the call: `path` values fill
`{placeholders}`, `query`/`header`/`cookie` go where they belong, and the `body` feature (or the
whole request, for body-only operations) is serialized as JSON. The response JSON is deserialized
into `responseType()`. Payloads are serialized **without** the EMF `_type` discriminator, so the
wire is plain interop JSON.

- `withHeader(name, value)` adds a header to every request.
- `unwrap(HttpClient.class)` gives you the native client.
- An HTTP error or a JSON parse failure becomes a `ServiceInvocationException`.

## Authentication

The importer reads `components/securitySchemes` (`model.securitySchemes()`) and each operation's
**effective** security requirements (`operation.security()` — its own `security`, falling back to
the document's global one). On the client you register **credentials per scheme name**; *where*
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

`clientCredentials` POSTs to the flow's declared `tokenUrl` (client id/secret via HTTP Basic,
the requirement's scopes as `scope`), caches the `access_token` and re-fetches shortly before
`expires_in` runs out. The interactive `authorization_code` flow is deliberately out of scope for
a headless client — obtain the token elsewhere and pass it via `OpenApiAuth.bearer(...)`.

## Scope / limitations (v1)

- **Request-response, `application/json`, single-object responses.** An array response is imported
  (`responseMany`) but the client rejects it for now; streaming/SSE is out of scope.
- **`$ref` schemas only.** Inline (anonymous) request/response schemas are recorded as diagnostics,
  not resolved. RPC-style and non-JSON content are skipped with a diagnostic.
- **Dynamic-mode client** (JDK `HttpClient` + codec). A Jersey/`codec.rest` variant and a typed
  proxy are on the [service-clients](/guides/service-clients) roadmap.
- **Codec version:** requires a fennecCodec snapshot from 2026-07-10 or later — older ones left
  schema-to-schema `$ref` features untyped ([emf.codec#43](https://github.com/eclipse-fennec/emf.codec/issues/43))
  and dropped the scheme names of security requirements
  ([emf.codec#44](https://github.com/eclipse-fennec/emf.codec/issues/44)). A genuinely
  unresolvable `$ref` is degraded to `EObject`/`EString` with a diagnostic (safety net).
- **Auth v1:** `apiKey`, `http` basic/bearer, `oauth2` `client_credentials` (or a pre-obtained
  bearer token). No `authorization_code`/`implicit` flows, no `openIdConnect` discovery.

## Dependencies

The importer needs the Fennec codec bundles (`org.eclipse.fennec.codec.openapi`,
`…codec.jsonschema`, `…codec`, `…openapi.model`) + Jackson; the client adds `org.eclipse.fennec.codec`
for JSON marshalling. All are provided by the `fennecCodec` bnd library. Note the
`org.eclipse.fennec.model.metadata` bundle exists in two versions — pin `[0.1,0.2)`.
