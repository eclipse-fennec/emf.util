# EMF ⇄ gRPC — Examples

> **Status: v1.** These examples illustrate `org.eclipse.fennec.grpc` — the gRPC client and
> server on top of the [EMF ⇄ Protobuf](/guides/protobuf) utility and the
> [service client API](/guides/service-clients). Unary calls; no `protoc`-generated stubs
> anywhere.

## From `.proto` to operations

Everything starts from a **compiled descriptor set** — the one artifact `protoc` can produce
for any proto file, no code generation involved:

```bash
protoc --include_imports --descriptor_set_out=greet.pb greet.proto
```

```proto
syntax = "proto3";
package greet;

message GreetRequest { string name = 1; }
message GreetReply   { string message = 1; }

service Greeter {
  rpc Greet (GreetRequest) returns (GreetReply);
}
```

Importing it yields the message types as `EPackage`s **and** the services as callable
operations:

```java
byte[] set = Files.readAllBytes(Path.of("greet.pb"));
ProtobufImport model = ProtobufImporter.importFrom(set);

model.packages();                     // EPackage per proto package ("greet" -> nsURI http://greet)
GrpcService greeter = model.service("Greeter");
GrpcMethod greet = greeter.method("Greet");
greet.fullMethodName();               // "greet.Greeter/Greet"
greet.requestType();                  // the GreetRequest EClass
greet.streaming();                    // UNARY
```

## Calling a service (client)

`GrpcServiceClient` invokes the imported methods over any `io.grpc` `Channel` — the transport
is your choice (Netty shown here):

```java
ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()               // or TLS
        .build();

try (GrpcServiceClient client = new GrpcServiceClient(channel, model)
        .withDeadline(Duration.ofSeconds(10))) {

    EClass requestType = model.service("Greeter").method("Greet").requestType();
    EObject request = EcoreUtil.create(requestType);
    request.eSet(requestType.getEStructuralFeature("name"), "Rex");

    EObject reply = client.invoke("greet.Greeter/Greet", request);   // or just "Greet"
    reply.eGet(reply.eClass().getEStructuralFeature("message"));     // "Hello Rex"
}
```

A gRPC error status surfaces as a `ServiceInvocationException` carrying the status code
(`NOT_FOUND — no greeting for ghosts`). `close()` shuts the channel down.

## Serving a service (server)

`GrpcServiceServer` turns the same imported service into a **transport-agnostic**
`ServerServiceDefinition` — the handlers are plain `EObject -> EObject` functions:

```java
ServerServiceDefinition greeter = GrpcServiceServer.forService(model.service("Greeter"))
        .unary("Greet", request -> {
            String name = String.valueOf(request.eGet(
                    request.eClass().getEStructuralFeature("name")));
            EClass replyType = model.service("Greeter").method("Greet").responseType();
            EObject reply = EcoreUtil.create(replyType);
            reply.eSet(replyType.getEStructuralFeature("message"), "Hello " + name);
            return reply;
        })
        .definition();

Server server = NettyServerBuilder.forPort(50051)
        .addService(greeter)
        .build().start();
```

- A handler may throw a `StatusRuntimeException` to control the wire status
  (`Status.NOT_FOUND.withDescription(...).asRuntimeException()`); any other exception becomes
  `INTERNAL`.
- Methods without a registered handler answer `UNIMPLEMENTED`.
- The same definition runs on the in-process transport (tests) or — once the experimental
  `grpc-servlet-jakarta` path is validated — inside a servlet container.

## Testing without a network

The in-process transport wires client and server through the same JVM — no ports, no Netty:

```java
String name = InProcessServerBuilder.generateName();
Server server = InProcessServerBuilder.forName(name).directExecutor()
        .addService(definition).build().start();
ManagedChannel channel = InProcessChannelBuilder.forName(name).directExecutor().build();

try (GrpcServiceClient client = new GrpcServiceClient(channel, model)) {
    // exercise handlers exactly as over the wire
}
```

This is how `org.eclipse.fennec.grpc`'s own tests run; `NettyRoundTripTest` additionally proves
the identical code over a real HTTP/2 connection on localhost.

## OSGi deployment note

The upstream grpc-java jars ship **no OSGi metadata** — at OSGi runtime the `io.grpc.*`
packages come from the gecko wrap bundles: `io.grpc.core` (api/core/stub/protobuf/util merged;
`io.grpc` is a split package upstream) and `io.grpc.netty` (the unshaded transport; the
official `io.netty.*` jars are OSGi bundles already). The client discovers the transport via
grpc's built-in `Class.forName` fallback — no SPI-Fly required.
