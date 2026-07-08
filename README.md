# emf.util

**Utilities and commons for Fennec EMF OSGi** — a home for small, focused libraries
around the [Eclipse Modeling Framework](https://eclipse.dev/modeling/emf/), part of the
[Eclipse Fennec](https://projects.eclipse.org/projects/technology.fennec) project.

Each utility has a plain-Java core that works **without** the Eclipse platform (well
unit-tested, DI-friendly) and, where useful, an optional OSGi integration layer for the
[Fennec EMF OSGi](https://github.com/eclipse-fennec/emf.osgi) runtime.

## Utilities

| Utility | Status | Description |
|---|---|---|
| EMF ⇄ Protobuf | In progress | Schema-driven (de)serialization of EMF models to Google Protocol Buffers — descriptors derived directly from an `EPackage`, no `protoc`, no generated code. |

## Building

Requires **Java 21** (CI also builds on 25). The bnd toolchain runs on the **7.4.0 snapshot**
(7.3.0 is skipped due to an upstream `-pom` snapshot regression — see `gradle.properties`).

```bash
./gradlew clean build            # compile, unit tests, coverage checks
./gradlew build testOSGi         # full build incl. OSGi integration tests
./gradlew perfTest               # slow @Tag("perf") tests (excluded from build)
```

Runtime/OSGi dependencies are resolved from Maven Central through bnd
(`cnf/ext/central.mvn`); Gradle only carries the test-scope dependencies.

## Documentation

User manual and examples are written in `docs/` and published with VitePress to
`https://eclipse-fennec.github.io/emf.util/`.

```bash
cd docs-site && npm ci && npm run docs:dev   # live preview
```

## Contributing

Development flows through pull requests into the **`snapshot`** branch (snapshot
artifacts are published from there). Releases are cut from the protected **`main`**
branch and published to Maven Central. CI is hardened (least-privilege workflow
permissions, SHA-pinned actions, OpenSSF Scorecard, Dependabot, dependency review).

## License

[Eclipse Public License 2.0](LICENSE) — an [Eclipse Fennec](https://projects.eclipse.org/projects/technology.fennec) project.
