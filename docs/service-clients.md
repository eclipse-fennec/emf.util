# Service clients

> **Status: v1.** Implemented and tested for SOAP and OpenAPI/REST; the API may still evolve.

The importers turn a WSDL / OpenAPI / Protobuf descriptor into Ecore **and** a list of callable
**operations**. A `ServiceClient` then *invokes* those operations over the wire — with the same
face regardless of protocol:

```java
try (ServiceClient client = /* a SoapServiceClient / OpenApiServiceClient … */) {
    ServiceOperation op = client.operation("getPet");
    EObject request  = /* an instance of op.requestType() */;
    EObject response = client.invoke(op, request);   // instance of op.responseType()
}
```

Everything is **EMF all the way**: the request and the response are `EObject`s of the imported
types, so the same code (and a generic, form-driven UI) can drive any service — SOAP, REST/OpenAPI,
and later gRPC and OData.

## The API (`org.eclipse.fennec.service.api`)

- **`ServiceOperation`** — a protocol-neutral view of one operation: `name()`, `requestType()`,
  `responseType()` (both `EClass`, either may be `null`). The importers' descriptors
  (`SoapOperation`, `OpenApiOperation`, `GrpcMethod`) implement it.
- **`ServiceClient`** (`AutoCloseable`):
  - `operations()` / `operation(String name)` — discover what can be called;
  - `invoke(ServiceOperation, EObject request) → EObject` (and `invoke(String name, EObject)`);
  - `unwrap(Class<T>)` — drop to the native client (`HttpClient`, gRPC `ManagedChannel`, …) for
    protocol-specific features (auth interceptors, streaming, OData query options);
  - `close()`.
- **`ServiceInvocationException`** — thrown on transport error, protocol fault (SOAP `Fault`,
  gRPC status, HTTP error) or (de)serialization failure.

Marshalling is delegated to each format's (de)serialization layer — the SOAP envelope, the
Protobuf message, the JSON codec — so the client is just discovery + transport + dispatch.

## Available clients

| Protocol | Import → operations | Client | Guide |
|---|---|---|---|
| SOAP 1.1 (WSDL) | `WsdlImporter` → `SoapOperation` | `SoapServiceClient` | [SOAP](/guides/soap) |
| OpenAPI 3 (REST) | `OpenApiImporter` → `OpenApiOperation` | `OpenApiServiceClient` | [OpenAPI](/guides/openapi) |
| Protobuf / gRPC | `ProtobufImporter` → `GrpcService`/`GrpcMethod` | *planned* | [Protobuf](/guides/protobuf) |
| OData | *planned* | *planned* | — |

## Two consumer modes

- **Dynamic** (the UI world): you only know the contract at runtime — pick an operation from the
  list, fill a form generated from its request `EClass`, `invoke`. This is what the clients above do
  today.
- **Typed** (the OSGi world): you have a Java interface for the service and want
  `store.getPet(42)` ergonomics — a `java.lang.reflect.Proxy` over your interface, backed by the
  same engine. Planned; needs a small (interface-only) stub, since a nominally-typed language
  cannot hand you a compile-time type for a contract discovered at runtime.

## Scope / limitations (v1)

- **Request-response only.** Streaming / publish-subscribe (gRPC server-streaming, SSE, OData
  delta) are not modelled yet — use `unwrap(...)` for those.
- **One request `EObject`** per call (a body, or a synthetic request folding parameters — see the
  OpenAPI guide). Multi-part / multi-response shapes are out of scope.
- The clients are **plain-Java** (JDK `HttpClient`); an OSGi/`@Reference`-based typed variant is on
  the roadmap.
