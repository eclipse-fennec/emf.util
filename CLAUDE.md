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
  into `check`. It is currently **empty**; add each utility's core bundle as it gains
  plain-JUnit tests.
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
- **Workflows** (`.github/workflows/`): `build` (PRs + feature branches), `snapshot`
  (publish on `snapshot`), `release` (publish on `main`), `license`, `docs`, `scorecard`
  (OpenSSF), `dependency-review`.
- **Hardening (in-repo):** top-level `permissions: contents: read` with minimal per-job
  escalation, `step-security/harden-runner`, all actions **SHA-pinned** with `# vX.Y.Z`
  comments, `concurrency` groups. `.github/dependabot.yml` keeps github-actions, gradle,
  and the docs-site npm deps current (it updates the pinned SHAs too).
- **Org-level** settings (branch protection on `main`/`snapshot`, secret scanning) live in
  the separate EF-managed `eclipse-fennec/.eclipsefdn` otterdog repo, not here.

## EMF ⇄ Protobuf (first utility)

Bundles: `org.eclipse.fennec.protobuf` (core, plain Java), `org.eclipse.fennec.protobuf.osgi`
(the `Resource.Factory` service), and `org.eclipse.fennec.protobuf.osgi.tests` (a `testOSGi`
integration test that launches Felix and proves the factory registers + `.protobin` load/save
works through a Fennec `ResourceSet`). In the core, the descriptor/(de)serialization API is in
package `org.eclipse.fennec.protobuf`, while `ProtobufResource` + `ProtobufResourceFactory`
live in `org.eclipse.fennec.protobuf.resource`. Both packages are exported.

Schema-driven (de)serialization of EMF **instances** to Google Protocol Buffers, with
descriptors built **from the `EPackage`** via `DescriptorProtos` + `Descriptors.FileDescriptor`
+ `DynamicMessage` (no `protoc`, no generated classes). Scope: instance (de)serialization
**+ `.proto` schema export**. `.proto` schema *import* is out of scope (protobuf-java has
no `.proto` text parser). Key mapping decisions (see `docs/protobuf-user-guide.md`):

- **Field numbers** from an `EAnnotation` (source `http://www.eclipse.org/fennec/protobuf`,
  key `fieldNumber`) — wire-stable; `getFeatureID()` is deliberately NOT used.
- Containment `EReference` → embedded message; non-containment → URI-fragment reference;
  inheritance → flattened (polymorphism via `oneof`); `isMany` → `repeated`; unset-vs-default
  → proto3 `optional`. `EBigInteger`/`EBigDecimal` → `string`.
- OSGi integration = a `Resource.Factory` annotated `@Component` + `@EMFConfigurator(
  configuratorType = RESOURCE_FACTORY, fileExtension=…, contentType=…)`, depending only on
  `org.eclipse.fennec.emf.osgi.api`; the `DefaultResourceFactoryRegistryComponent`
  whiteboard binds it automatically.
