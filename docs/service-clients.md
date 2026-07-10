# Service clients

> **Status: v1.** Implemented and tested for SOAP, OpenAPI/REST and gRPC; the API may still evolve.

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
| Protobuf / gRPC | `ProtobufImporter` → `GrpcService`/`GrpcMethod` | `GrpcServiceClient` | [Protobuf](/guides/protobuf) |
| OData | *planned* | *planned* | — |

### gRPC (`org.eclipse.fennec.grpc`)

`GrpcServiceClient` invokes the imported `GrpcMethod`s over an `io.grpc` `Channel` — the
request/response `EObject`s travel as **bare Protobuf messages**, (de)serialized by the Fennec
Protobuf runtime against the imported `EPackage`s (no `protoc`, no generated stubs):

```java
ProtobufImport model = ProtobufImporter.importFrom(descriptorSetBytes); // protoc --descriptor_set_out
ManagedChannel channel = NettyChannelBuilder.forAddress("host", 50051).usePlaintext().build();
try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
    EObject reply = client.invoke("greet.Greeter/Greet", request);  // or the simple method name
}
```

- Lookup by **full method name** (`pkg.Service/Method`) or simple name (first service wins on a
  collision). `withDeadline(Duration)` applies a per-call deadline; `close()` shuts the channel
  down when it is a `ManagedChannel`.
- A gRPC error status becomes a `ServiceInvocationException` carrying the status code.
- v1 is **unary only**; streaming methods are rejected with a clear error — `unwrap(Channel.class)`
  for hand-rolled streaming calls.
- The transport is the caller's choice: the bundle only needs `grpc-api`/`grpc-stub`. At OSGi
  runtime the `io.grpc` packages come from the gecko `io.grpc.core`/`io.grpc.netty` wrap bundles
  (grpc-java ships no OSGi metadata upstream).

The bundle also contains the **server side**: `GrpcServiceServer` builds a transport-agnostic
`ServerServiceDefinition` for an imported service from plain `EObject -> EObject` handlers —
add it to a `NettyServerBuilder` (or the in-process transport for tests). See the
[gRPC examples](/examples/grpc) for the full round-trip.

## Two consumer modes

- **Dynamic** (the UI world): you only know the contract at runtime — pick an operation from the
  list, fill a form generated from its request `EClass`, `invoke`. This is what the clients above do
  today.
- **Typed** (the OSGi world): you have a Java interface for the service and want
  `store.getPet(42)` ergonomics — a `java.lang.reflect.Proxy` over your interface, backed by the
  same engine. Planned; needs a small (interface-only) stub, since a nominally-typed language
  cannot hand you a compile-time type for a contract discovered at runtime.

## Scope / limitations (v1)

- **Request-response only.** Streaming / publish-subscribe (gRPC server/client/bidi streaming,
  SSE, OData delta) are not modelled yet — use `unwrap(...)` for those.
- **One request `EObject`** per call (a body, or a synthetic request folding parameters — see the
  OpenAPI guide). Multi-part / multi-response shapes are out of scope.
- The clients are **plain-Java** (JDK `HttpClient`); an OSGi/`@Reference`-based typed variant is on
  the roadmap.
