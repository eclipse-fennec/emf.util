# Overview

**Fennec EMF Util** (`org.eclipse.fennec.util`) is a home for small, focused utility
libraries around the [Eclipse Modeling Framework](https://eclipse.dev/modeling/emf/).
Each utility is a separate bundle with a narrow scope, a plain-Java core that works
without the Eclipse platform, and — where useful — an optional OSGi integration layer
for the [Fennec EMF OSGi](https://github.com/eclipse-fennec/emf.osgi) runtime.

## Design principles

- **Plain Java first.** The core of every utility is usable as an ordinary Java 21
  library, dependency-injection friendly, and covered by plain JUnit tests. No OSGi
  is required to use it.
- **OSGi as a thin layer.** OSGi services (Declarative Services components, a
  `Resource.Factory`, configurators) wrap the core; they never leak into it.
- **Small and independent.** Utilities do not depend on each other unless there is a
  real reason to. Each ships its own tests.

## Repository layout

This is a bnd/OSGi + Gradle hybrid workspace. Each utility lives in one or more
bundle sub-projects (a directory containing a `bnd.bnd`):

```
org.eclipse.fennec.<name>            # plain-Java core
org.eclipse.fennec.<name>.tests      # plain JUnit tests (no OSGi)
org.eclipse.fennec.<name>.osgi       # optional OSGi services
org.eclipse.fennec.<name>.osgi.tests # OSGi integration tests (testOSGi)
org.eclipse.fennec.util.workspace.library # bnd library `fennecUtil` (downstream consumption)
docs/                                     # documentation source of truth
docs-site/                                # VitePress site (publishes docs/)
```

Downstream Fennec workspaces consume these utilities by adding the
`org.eclipse.fennec.util:org.eclipse.fennec.util.workspace.library` artifact and enabling
`-library: fennecUtil` (which wires a Maven repository indexed by the bundles and their
dependency closure).

## Building

Requires **Java 21** (CI also builds on 25).

```bash
./gradlew clean build            # compile, unit tests, coverage checks
./gradlew build testOSGi         # full build incl. OSGi integration tests
./gradlew :<bundle>:test --tests '*Name*'   # a single test
```

Dependencies come from Maven Central through bnd (`cnf/ext/central.mvn`), not through
Gradle; the bnd toolchain runs on the **7.4.0 snapshot**.

## Utilities

| Utility | Status | Description |
|---|---|---|
| [EMF ⇄ Protobuf](/guides/protobuf) | v1 | Schema-driven (de)serialization of EMF models to Google Protocol Buffers (+ gRPC service extraction). |
| [EMF ⇄ SOAP / WSDL](/guides/soap) | v1 | WSDL/XSD → Ecore import and SOAP 1.1 envelope (de)serialization of EMF models as XML, incl. a SOAP client. |
| [EMF ⇄ OpenAPI / REST](/guides/openapi) | v1 | OpenAPI 3 → Ecore import and a REST client that invokes operations with EObject request/response. |
| [Service clients](/guides/service-clients) | v1 | One protocol-agnostic `ServiceClient` API over SOAP, OpenAPI (and, planned, gRPC/OData). |

## Documentation & releases

- Docs live in `docs/` and are published with VitePress to
  `https://eclipse-fennec.github.io/emf.util/<version>/`.
- Development flows through pull requests into the `snapshot` branch; releases are
  cut from the protected `main` branch and published to Maven Central.
