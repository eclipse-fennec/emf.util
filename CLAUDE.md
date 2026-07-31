# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this repository is

`emf.util` — "Utilities and commons for Fennec EMF OSGi", part of the
[Eclipse Fennec](https://projects.eclipse.org/projects/technology.fennec) project (EPL-2.0).

It is a **home for several small, independent utility libraries** around EMF, built as a
**bnd/OSGi + Gradle hybrid workspace** (scaffolded from the `fennec-gradle` template).
Design principle: every utility has a **plain-Java core usable without OSGi** (well
unit-tested), plus an **optional OSGi layer** (a `Resource.Factory`, DS components) for
the [Fennec EMF OSGi](https://github.com/eclipse-fennec/emf.osgi) runtime.

**Bundle naming.** Each utility lives in one or more sub-projects (a directory with a
`bnd.bnd`), named `org.eclipse.fennec.<name>` (core; plain-JUnit tests live in its `test/`
folder), `.<name>.osgi` (OSGi services), `.<name>.osgi.tests` (OSGi integration tests). The
first utility is **EMF ⇄ Protobuf** — bundles `org.eclipse.fennec.protobuf` (core) and
`org.eclipse.fennec.protobuf.osgi` (see below).

## Build & test

Requires **Java 21** (CI also builds on 25). bnd toolchain on the **7.4.0 snapshot**
(`gradle.properties`, from the bndtools snapshot repo) — the released 7.3.0 has a `-pom`
snapshot regression that breaks bundle jars; move to 7.4.0 proper once released. Always
use the Gradle wrapper.

```bash
./gradlew clean build            # compile + unit tests + coverage checks
./gradlew build testOSGi         # full build incl. OSGi integration tests (bnd launcher)
./gradlew perfTest               # @Tag("perf") tests only (excluded from `build`)
./gradlew :<bundle>:test --tests '*ClassName*'   # single test class/method
./gradlew codeCoverageReport     # aggregate JaCoCo across all bundles (xml + html)
```

- Unit tests: JUnit 5 + Mockito + AssertJ. OSGi tests run in a real framework via the
  bnd launcher (`*.bndrun`).
- **`coverageFloorBundles`** in `build.gradle` is a 30%-instruction JaCoCo tripwire wired
  into `check` (currently `org.eclipse.fennec.protobuf`); add each utility's core bundle as
  it gains plain-JUnit tests.
- License headers (EPL-2.0) are enforced in CI (`apache/skywalking-eyes`, config
  `.licenserc.yaml`). Many file types are exempt (see `paths-ignore`, incl.
  `**/src-gen/**`, `**/generated/**`, `cnf/**`).

## Documentation site

VitePress site in `docs-site/`, published to `https://eclipse-fennec.github.io/emf.util/<branch>/`.

- **Source of truth is `docs/`** (hand-written markdown). `docs-site/sync-guides.mjs`
  copies an **allowlist** (`docs-site/guides.mjs`: `GUIDES` → user manual, `EXAMPLES` →
  examples) into `docs-site/docs/{guides,examples}/` before each build; links to
  non-published docs are rewritten to GitHub blob URLs. The generated dirs are git-ignored.
- Build: `cd docs-site && npm ci && npm run docs:build` (or `docs:dev` for live preview).
  Not wired into Gradle. `docs-site` is in `bnd_exclude` so bnd does not treat it as a bundle.
- Deployed by `.github/workflows/docs.yml` on push to `snapshot`/`main` (paths-filtered).

## Architecture & conventions

- **Two build systems, one workspace.** Gradle (`build.gradle`, `settings.gradle`) and bnd
  (`cnf/`) operate over the same tree. `cnf/ext/fennec.bnd` is the additive bnd config.
- **Project coordinates are single-source in `gradle.properties`** (`github_org`,
  `github_repository`, `maven_group_id=org.eclipse.fennec.util`). bnd imports the same file
  via `-include`. Never hardcode these.
- **Runtime/OSGi dependencies come from Maven Central via bnd**, listed in
  `cnf/ext/central.mvn` — NOT Gradle. Gradle `dependencies` are test-only (auto-applied to
  every subproject). Fennec libraries are enabled through `-library:` in `fennec.bnd`.
  `protobuf-java` is not yet in `central.mvn` — add it when building the Protobuf utility.
- **New bundle = new sub-project directory** with a `bnd.bnd`; the bnd workspace plugin
  sweeps it into the graph. Root Gradle applies `java` + `jacoco` to every subproject.
- **`org.eclipse.fennec.util.workspace.library`** is a `-resourceonly` bnd *library*
  (`bnd.library=fennecUtil`) that publishes the emf.util bundles + their Maven dependency
  closure. Downstream Fennec workspaces consume everything here by adding the artifact to
  their `central.mvn` and enabling `-library: fennecUtil`. When a bundle is added/changed,
  re-resolve its closure: `./gradlew :org.eclipse.fennec.util.workspace.library:resolve.required`
  (updates `required.bndrun`), which feeds the generated `fennecUtil.maven` index.
- **Generated code** (`src-gen*`, `generated/`) is not hand-edited. For EMF
  `.ecore`/`.genmodel` work, ask the user rather than hand-writing model code.
- **Eclipse import.** Projects are imported into bndtools as a bnd workspace; bnd/Gradle
  generate the `.project`/`.classpath` on sync.

## Branch flow, releases & CI hardening

- **Flow:** PRs merge into **`snapshot`** (snapshot artifacts published from there);
  releases are cut from the protected **`main`** branch. Releases publish to Maven Central
  (Sonatype), driven by GitHub Actions (`-releaserepo` is intentionally not overridden).
- **Workflows** (`.github/workflows/`). `snapshot` (push to `snapshot`) and `release`
  (push to `main`) are **orchestrators** with gated jobs — `license → build (+testOSGi
  +publish) → docs → deploy` via `needs:` — so a failed license check or build stops the
  publish AND the docs deploy. `build` runs on PRs + feature branches; `license` runs on
  PRs + feature branches (the orchestrators run it as their first job on `main`/`snapshot`
  to avoid a duplicate); `docs` is `workflow_dispatch`-only (the auto docs build/deploy is a
  job in the orchestrators); plus `scorecard` (OpenSSF) and `dependency-review`.
- **Hardening (in-repo):** top-level `permissions: contents: read` with minimal per-job
  escalation, `step-security/harden-runner`, all actions **SHA-pinned** with `# vX.Y.Z`
  comments, `concurrency` groups. `.github/dependabot.yml` keeps github-actions, gradle,
  and the docs-site npm deps current (it updates the pinned SHAs too).
- **Org-level** settings (branch protection on `main`/`snapshot`, secret scanning) live in
  the separate EF-managed `eclipse-fennec/.eclipsefdn` otterdog repo, not here.

## EMF ⇄ Protobuf (first utility)

Bundles: `org.eclipse.fennec.protobuf` (runtime core, plain Java), `org.eclipse.fennec.protobuf.ecore`
(the descriptor→Ecore **importer**, a separate design-time bundle), `org.eclipse.fennec.protobuf.osgi`
(the `Resource.Factory` service) and `org.eclipse.fennec.protobuf.osgi.tests` (a `testOSGi`
integration test that launches Felix and proves the factory registers + `.protobin` load/save
works through a Fennec `ResourceSet`). In the core, the descriptor/(de)serialization API is in
package `org.eclipse.fennec.protobuf` and `ProtobufResource` + `ProtobufResourceFactory` live in
`org.eclipse.fennec.protobuf.resource`; the importer (`ProtobufImporter` + `ImportOptions`) is the
`org.eclipse.fennec.protobuf.ecore` package in its own bundle. Package exports/versions are driven
by `@Export`/`@Version` on `package-info.java` (not `Export-Package` in `bnd.bnd`); the `.osgi`
bundle is the exception — it `Export-Package`s (inlines) the runtime packages so it is
self-contained/standalone (no separate core bundle needed at runtime), mirroring SOAP.

Schema-driven (de)serialization of EMF **instances** to Google Protocol Buffers, with
descriptors built **from the `EPackage`** via `DescriptorProtos` + `Descriptors.FileDescriptor`
+ `DynamicMessage` (no `protoc`, no generated classes). Scope: instance (de)serialization
**+ `.proto` schema export** **+ descriptor→Ecore import**. `.proto` *text* import is out of
scope (protobuf-java has no text parser), but `ProtobufImporter.fromDescriptorSet(byte[])`
derives dynamic `EPackage`s from a compiled `FileDescriptorSet` (`protoc --include_imports
--descriptor_set_out`) — for interop / bootstrapping. It is deliberately **structural** and
lossy (flat EClasses, every message field → containment, `int32`/`string` not re-widened,
`uint64`→`ELong`); field numbers are preserved as annotations. Key mapping decisions (see
`docs/protobuf-user-guide.md`):

- **Field numbers** from an `EAnnotation` (source `http://eclipse.org/fennec/protobuf`,
  key `fieldNumber`) — wire-stable; `getFeatureID()` is deliberately NOT used.
- Containment `EReference` → embedded message; non-containment → URI reference; `isMany` →
  `repeated`; unset-vs-default → proto3 `optional`; `EBigInteger`/`EBigDecimal` → `string`.
- Polymorphism / cross-package / subpackage / `EObject` refs → an `EObjectAny`/`EObjectRef`
  wrapper carrying a type discriminator (strategy `NAME` default, `URI`, `NUMERIC`;
  `smartCompression` on), resolved via the package registry; the nested object is serialized
  against its own package's schema. Config via `ProtobufContext` / `ProtobufResource` options
  or `EAnnotation` — feature filters `ignore`/`ignoreWrite`/`ignoreRead`/`forceWrite`/`forceRead`.
  Property names + annotation source (`http://eclipse.org/fennec/protobuf`) mirror the Fennec Codec.
- OSGi integration = a `Resource.Factory` annotated `@Component` + `@EMFConfigurator(
  configuratorType = RESOURCE_FACTORY, fileExtension=…, contentType=…)`, depending only on
  `org.eclipse.fennec.emf.osgi.api`; the `DefaultResourceFactoryRegistryComponent`
  whiteboard binds it automatically.

## SOAP / WSDL (second utility)

Bundles: `org.eclipse.fennec.soap` (runtime core, plain Java), `org.eclipse.fennec.soap.ecore`
(the WSDL/XSD→Ecore **converter**, a separate design-time bundle), `org.eclipse.fennec.soap.osgi`
(the `Resource.Factory` service) and `org.eclipse.fennec.soap.osgi.tests`. Same shape as Protobuf:
an importer plus a `Resource` that (de)serializes payloads — here through a **SOAP 1.1 envelope as
XML**. See `docs/soap-user-guide.md`.

- **Reuses the SOAP model**, does not rebuild it: bundle `org.xmlsoap.model` (group
  `org.eclipse.fennec.models`, package `org.xmlsoap.schemas.envelope`, SOAP 1.1), consumed via
  the already-enabled `fennecEMFModels` bnd library. The `Body` payload rides in its wildcard
  **`FeatureMap`** (`xsd:any`, lax) — not a typed reference.
- **Converter is its own bundle `org.eclipse.fennec.soap.ecore`** (package
  `org.eclipse.fennec.soap.ecore`) — deliberately split from the runtime because it needs
  `org.eclipse.xsd`, whose *greedy* optional `Require-Bundle` on `org.eclipse.core.runtime` does not
  resolve in a minimal Felix. So the runtime (`org.eclipse.fennec.soap`) has **no XSD dependency at
  all** and resolves cleanly; only consumers doing design-time WSDL import pull in the converter +
  `org.eclipse.xsd`. Dependency direction: `soap.ecore` → `soap` (for `SoapException`); the runtime
  never depends on the converter. (The workspace library ships only the runtime bundles, not the
  converter.)
- **`WsdlImporter`** (`fromWsdl`/`fromXsd` → `WsdlModel` with `EPackage`s + `SoapOperation`s). Types
  come from EMF's **`XSDEcoreBuilder`** (whose `XSDResourceImpl` also extracts schemas embedded in
  `<wsdl:types>`); the generated Ecore carries `ExtendedMetaData` for XML round-tripping. WSDL
  *operation* semantics aren't modelled by EMF, so a light DOM parse of `portType`/`operation` +
  `message`/`part` resolves each operation to its request/response `EClass` (document/literal).
  Attribute types are the EMF XML datatypes (`String`/`Int`/… from `org.eclipse.emf.ecore.xml.type`),
  not the Ecore `E*` types.
- **`SoapResource` / `SoapResourceFactory`** (`org.eclipse.fennec.soap.resource`, extension
  `.soap`, content type `application/soap+xml`): `save` wraps the content EObjects (copies) in an
  `Envelope`/`Body` and serializes via an EMF `XMLResource` **with `OPTION_EXTENDED_META_DATA`**
  (essential — otherwise EMF ignores the XSD element/namespace metadata); `load` unwraps `Body`
  content back into the contents. An incoming SOAP `Fault` is recorded via `getErrors()` and thrown
  as `IOException`. The carrier `XMLResource` shares the resource set's package registry (payload
  `EPackage`s must be registered — `WsdlModel.registerInto(resourceSet)`); the envelope package is
  auto-registered.
- **OSGi integration test** `org.eclipse.fennec.soap.osgi.tests` (Felix, via the bnd launcher):
  proves the factory is service-registered and wired into a Fennec `ResourceSet`, plus an empty
  envelope round-trip. Core mapping is covered by plain-JUnit (`WsdlImporterTest`,
  `SoapResourceTest`). **Gotcha:** an `.osgi.tests` bundle needs a tiny per-project `build.gradle`
  that points `testOSGi` at the *resolved* bndrun, else `./gradlew testOSGi` launches the raw
  bndrun and fails to assemble the framework runpath (`NoClassDefFoundError:
  org/osgi/framework/ServiceListener`):
  ```gradle
  def resolveTask = tasks.named("resolve.test") { outputBndrun = layout.buildDirectory.file("test.bndrun") }
  tasks.named("testOSGi") { bndrun = resolveTask.flatMap { it.outputBndrun } }
  ```
  The plain-JUnit test bundles also need `-testpath: assertj-core;version=latest` in their `bnd.bnd`
  for the IDE/bnd compile.

## Service client API (third strand)

`org.eclipse.fennec.service.api` — the **protocol-agnostic client face**: `ServiceClient`
(`operations()`, `invoke(op, EObject) → EObject`, `unwrap(nativeType)`, `close`),
`ServiceOperation` (name + request/response `EClass`), `ServiceInvocationException`. The importers'
operation descriptors implement `ServiceOperation`; per-protocol clients implement `ServiceClient`
(a future `InvocationDelegate`/DDSR-flavor engine is an implementation detail *behind* this face —
see `dim-knowledge-atlas/docs/discussion-service-fabric.md` for the big picture).

- **`org.eclipse.fennec.soap.client`** — `SoapServiceClient` (JDK `HttpClient` + `SoapResource`
  marshalling; `withSoapAction`). Tested end-to-end against a local JDK `HttpServer`.
- **`org.eclipse.fennec.openapi.ecore`** — `OpenApiImporter`: loads the document via the
  **fennecCodec pipeline** (`OpenApiResourceFactoryImpl`; `components/schemas` → `EPackage` happens
  during load, read via `components.getSchemasPackage()`), then links path operations to EClasses.
  Parameters fold into a **synthetic request EClass** (`<Name>Request`, features annotated
  `in=path|query|header|cookie|body`, source `http://eclipse.org/fennec/openapi`); body-only ops use
  the body EClass directly; array responses resolve to the item EClass + `responseMany` (client v1
  rejects them). `components/securitySchemes` → `model.securitySchemes()` (raw model objects);
  each operation carries its **effective** requirements (`operation.security()`, own → global
  fallback) as plain `List<Map<schemeName, scopes>>` (alternatives = OR, entries = AND).
  **Requires a fennecCodec snapshot ≥ 2026-07-10**: older ones left schema-to-schema `$ref`
  features untyped (emf.codec#43) and dropped security-requirement scheme names (emf.codec#44) —
  both fixed upstream after emf.util filed the issues; the importer keeps only a safety net
  (`ensureTypedFeatures`: unresolvable `$ref` → `EObject`/`EString` + diagnostic, else
  `MetadataService.registerPackage` NPEs). Note: the `CODEC_FEATURE_VALUE_READERS` runtime option
  is still a no-op placeholder for references (emf.codec#45).
- **`org.eclipse.fennec.openapi.client`** — `OpenApiServiceClient` (JDK `HttpClient` + codec JSON).
  Serializes **without** the EMF type discriminator (`CodecOptions.CODEC_TYPE_INCLUDE=false`) —
  with `_type` on the wire real servers reject the body. Deserializes with
  `CodecResource.CODEC_ROOT_TYPE` = the operation's response EClass. **Auth:** `OpenApiAuth`
  credentials registered per scheme name (`withAuth`; single-scheme convenience overload) —
  `apiKey` / `basic` / `bearer(Supplier)` / `clientCredentials` (token from the flow's `tokenUrl`,
  cached until `expires_in`). Placement (header/query/cookie, names, tokenUrl) comes from the
  document; first satisfiable requirement alternative wins, empty alternative = anonymous OK,
  required-but-unregistered fails fast before HTTP. `authorization_code`/OIDC discovery
  deliberately out of scope (headless).
- **`org.eclipse.fennec.openapi.osgi`** — the first **client** OSGi bundle (Protobuf/SOAP `.osgi`
  register a `Resource.Factory`; OpenAPI has no resource of its own). A **config-driven**
  `@Component(configurationPolicy=REQUIRE) @Designate(factory=true)` implementing `ServiceClient`:
  each ConfigAdmin factory config for PID `OpenApiServiceClient` (`name`/`documentUrl`/`baseUri`/
  `format`/`auth[]`, auth entries `<scheme>=apiKey|bearer|basic|clientCredentials:<params>`) imports
  one document and publishes one `ServiceClient` service. The optional `name` is the stable,
  human-chosen client name (convention constant `ServiceClient.PROP_NAME`, DS auto-propagates it as
  service property; consumers like the emf.osgi-mcp tool bridge, see emf.osgi-mcp#15 / emf.util#11,
  select/label clients by it). It binds the framework's shared
  `MetadataWhiteboard` — added `OpenApiServiceClient(baseUri, model, MetadataWhiteboard)` for this
  (2-arg ctor delegates to it with a private `MetadataServiceFactory.create()`). **Isolation:** the
  importer stamps generated `schemas`/`requests` packages with *constant* nsURIs and
  `registerPackage` keys by nsURI, so the component rewrites them per config before registering
  (token = `name` if set — restart-stable — else `service.pid`; else two clients sharing the
  whiteboard collide); unregistered on deactivate. Inlines
  `openapi.client`/`openapi.ecore` (like SOAP). `.osgi.tests` proves it end-to-end in Felix
  (ConfigAdmin factory config → import → codec (de)serialize → HTTP round-trip; **first repo OSGi
  test that runs the whole codec stack** — resolves via `resolve.test`, needs
  `-runsystempackages: com.sun.net.httpserver`; the codec's `MetadataServiceComponent` +
  `CodecAspectProviderComponent` supply the whiteboard). Sets the pattern for `soap.client`/`grpc`.
- **Metadata home (2026-07-31):** the metadata service moved out of the standalone
  `org.eclipse.fennec.model.metadata` repo into **emf.osgi** — bundle
  `org.eclipse.fennec.emf.osgi.metadata`, package `org.eclipse.fennec.emf.osgi.metadata`
  (`MetadataService`/`MetadataWhiteboard`/`MetadataServices`). It ships with the `fennecEMF`
  library (emf.osgi 1.1.0-SNAPSHOT), so `fennecEMFMetadata` and its central.mvn artifact are
  gone and the old two-sources-same-BSN pin (`[0.1,0.2)`) is obsolete. Two follow-ups:
  the non-OSGi bootstrap `MetadataServiceFactory.create()` reaches `FingerprintHelper`
  (`…emf.osgi.fingerprint.util`, NOT exported by `…emf.osgi.api`), so plain-JUnit projects that
  use it need `org.eclipse.fennec.emf.osgi.component.minimal` on the `-testpath`; and every
  `…emf.osgi.*` buildpath entry must carry an explicit `version=latest` — without it bnd still
  picks the stale 0.1.2 jar from the local m2 and its inlined annotations shadow the 1.1.0 API
  (`@EPackage.fingerprint` / `EMFNamespaces.EMF_MODEL_FINGERPRINT` "not found").
  After bumping the library in `central.mvn`, clear `cnf/cache/<bndversion>/expanded` so the new
  library content is unpacked.
- **Remote tests** (`@Tag("remote")`, excluded from `build`; run `./gradlew remoteTest`): validate
  the design against public endpoints (Swagger petstore). Server-side outages are JUnit
  *assumptions* (skips), not failures. With `api_key`/bearer auth registered, all three petstore
  tests pass (2026-07-10) — including the write path that previously answered HTTP 500.
- **`org.eclipse.fennec.grpc`** — gRPC client **and** server in one runtime bundle (packages
  `…grpc.client`, `…grpc.server`, shared `…grpc.BareMarshaller`). `GrpcServiceClient` over an
  `io.grpc` `Channel`: the operations are the `GrpcMethod`s of a `ProtobufImport` (`GrpcMethod`
  implements `ServiceOperation`); EObjects ride as **bare** Protobuf messages via
  `ProtobufSchema.forPackage(...).writer().toBareBytes(...)` / `.reader().readBare(...)` — no
  protoc, no generated stubs, no DynamicMessage on the caller side. Lookup by full
  (`pkg.Service/Method`) or simple method name; `withDeadline(Duration)`; gRPC status →
  `ServiceInvocationException`; v1 unary only (streaming rejected with clear error).
  `GrpcServiceServer.forService(grpcService).unary(name, handler).definition()` yields a
  **transport-agnostic** `ServerServiceDefinition` (handler `StatusRuntimeException` passes
  through, other exceptions → INTERNAL; unregistered methods → UNIMPLEMENTED). Tested in-process
  (client + server) AND over a real Netty HTTP/2 localhost connection (`NettyRoundTripTest`).
  gRPC-over-servlet (port-sharing with Jetty/whiteboard) is possible via the experimental
  `grpc-servlet-jakarta` but hinges on container HTTP/2 + trailers (h2c prior-knowledge) — not
  validated yet, Netty is the recommended transport. **Dependency decision:** grpc-java jars
  from Central on build/test paths only (they carry NO OSGi metadata; upstream issue #1565 open
  since 2016) — at OSGi runtime the `io.grpc.core` + `io.grpc.netty` wrap bundles from
  `org.gecko.libraries` provide the packages (io.grpc.core merges
  api/core/stub/protobuf/protobuf-lite/util — `io.grpc` is a split package upstream and
  grpc-util's LoadBalancers are ServiceLoader-discovered; the transport is found via grpc's
  Class.forName fallback, no SPI-Fly). The wraps are published as
  `org.geckoprojects.libraries:io.grpc.core`/`io.grpc.netty` (central.sonatype.com snapshots,
  in `central.mvn`); the workspace library requires `org.eclipse.fennec.grpc` + `io.grpc.netty`,
  so consumers get the full closure incl. the official `io.netty.*` bundles +
  guava/gson/jsr305.
- **Next:** OData; the `SoapOperation`/`GrpcService`-`GrpcMethod`/`OpenApiOperation` descriptors
  are the decision-neutral raw material for the later `EOperation`-vs-DDSR projection.
