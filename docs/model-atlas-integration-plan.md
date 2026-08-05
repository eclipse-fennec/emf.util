# Plan: Model Atlas client integration for sensinact mappings

Status: implemented (2026-07-28) — all five steps done on branch `model_atlas_client_integration`;
the end-to-end OSGi IT (`org.eclipse.fennec.sensinact.mapping.atlas.tests`) is green.
Live test against a real (jena) atlas verified green 2026-07-30 — see
[Live-test verification](#live-test-verification-2026-07-30-green-end-to-end) at the bottom.
The generalization follow-up is planned separately — see
[Follow-up (2026-08-04)](#follow-up-2026-08-04-generalization-plan-settled--see-the-separate-plan-doc).
Deviation found while testing: the `.tests` bndrun additionally needs
`bnd.identity;id='org.apache.aries.typedevent.bus'` — the resolver does not pull the typed-event
bus on its own, and without it the sensinact `GatewayThread` (and thus `ProviderMappingRegistry`)
never starts.

## Context

The sensinact mapping utility already lives in this repo (`org.eclipse.fennec.sensinact.mapping`): a generated mapping metamodel (nsURI `https://fennec.eclipse.org/sensinact/core/mapping/1.0`) plus whiteboard registries — `ProviderMappingRegistryImpl.registerModelMapping(ProviderMapping)` and `MappingProfileRegistryImpl.addProfile(MappingProfile)` are `@Reference(MULTIPLE, DYNAMIC)`, so any OSGi service of those types is picked up automatically. **But nothing in the repo actually loads mapping XMIs** — consumers (e.g. the urban-data-platform backend, which loads them manually from bundle-embedded files) must supply them.

Goal: pull the mapping XMI instances **and** the sensor-model EPackages they reference from a remote **Model Atlas**, using the atlas REST client from the `model.atlas` workspace — consumed as **local jars** for now (not yet published to Maven). All work happens in fennec-emf.util; the urban-data-platform is only the motivating use case and is not touched.

> **Update 2026-07-31:** the local-jar setup is history — model.atlas now publishes to
> Maven Central snapshots (group `org.eclipse.fennec.model.atlas`). The four client GAVs
> (`0.1.0-SNAPSHOT`) live in `cnf/ext/central.mvn`; the `cnf/local` LocalIndexedRepo and
> the committed jars were removed. Step 1 below is kept for history only.

Decisions already made:

- Work only in this repo.
- **Reuse `org.eclipse.fennec.model.atlas.rest.client.osgi`** (its `AtlasClientComponent`, factory PID `org.eclipse.fennec.model.atlas.rest.client`, publishes per-scope `ReadableScopeService<EObject>` services + remote EPackages as the EMF-OSGi `EPackageConfigurator` trio) rather than driving the plain client API ourselves.

Verified load-bearing facts:

- The atlas client loads instance XMI into a ResourceSet whose package registry is `AtlasDelegatingPackageRegistry(EPackage.Registry.INSTANCE, remote)` (`ModelAtlasClientImpl.java:178` in model.atlas) — **local-first on the EMF global registry**. The generated `MappingConfigurationComponent` puts `MappingPackage` into `INSTANCE`, so fetched mappings instantiate through the *generated* EFactory and are castable to `ProviderMapping` — provided the mapping package is registered before the first fetch.
- Sensor-model `href`s in the mapping XMI (e.g. `http://cdc.dwd.de/common/weather#//…`) resolve via fetch-on-miss from the atlas — no start-order problem for sensor EPackages.
- `rest.client.osgi` imports `org.eclipse.fennec.emf.osgi.configurator [1.0,1.1)`; this workspace's `org.eclipse.fennec.emf.osgi.api` 0.1.2-SNAPSHOT exports exactly 1.0 — compatible.
- `jakarta.ws.rs-api` 3.1.0 and the `jakartaREST` bnd-library backing artifact (`org.eclipse.osgitech.rest.bnd.library:1.2.3`, `cnf/ext/central.mvn:181`) are already indexed; SPI-Fly is already in central.mvn (the atlas `rest.client.impl` requires the `osgi.serviceloader.registrar` extender for its `ModelAtlasClientFactory` ServiceLoader provider).
- Jackson: the atlas `rest.client.impl` has been moved to Jackson **3** (`tools.jackson.*`), which this workspace already provides (via the fennecCodec library) — no extra Jackson dependency needed. The committed local jars must be built from that state of model.atlas.
- All four needed jars exist at `<model.atlas>/<bsn>/generated/<bsn>.jar` (never use `build/libs/` — those are empty stubs from the plain Gradle `java` plugin).

## Step 1 — Local jar repository (`cnf/local`) *(superseded 2026-07-31 — client now on Maven Central snapshots, see update note above)*

No writable/local repo exists today (`cnf/build.bnd` has only `-fixupmessages`; the comment in `cnf/ext/fennec.bnd:1-7` claiming the base template provides Local/Release repos is stale). Add to **`cnf/build.bnd`**:

```
# Local repository for artifacts not (yet) on Maven Central — currently the
# Model Atlas client bundles (built from the model.atlas workspace).
# Jars + index are committed. Remove once model.atlas publishes to Maven.
-plugin.0.Local: \
	aQute.bnd.deployer.repository.LocalIndexedRepo; \
		name = Local; \
		pretty = true; \
		local = ${build}/local
```

Commit into **`cnf/local/<bsn>/<bsn>-<version>.jar`** (+ generated `index.xml`) the four bundles from `<model.atlas>/<bsn>/generated/`:

1. `org.eclipse.fennec.model.atlas.scope.api` (0.1.0.\*-SNAPSHOT — `ReadableScopeService`, `AtlasProperties`)
2. `org.eclipse.fennec.model.atlas.rest.client.api` (0.1.0.\*-SNAPSHOT)
3. `org.eclipse.fennec.model.atlas.rest.client.impl` (0.1.0.\*-SNAPSHOT)
4. `org.eclipse.fennec.model.atlas.rest.client.osgi` (0.1.0.\*-SNAPSHOT — the DS front-end; its version was aligned to the 0.1.0 snapshot of the other client bundles)

Refreshing the jars during development is a manual step (rebuild model.atlas, drop the new jars into the Local repo / bndtools Repositories view, commit `cnf/local/**`). Also fix the stale comment in `cnf/ext/fennec.bnd`. No `.gitignore` change needed (`cnf/local` is not ignored).

## Step 2 — Runtime dependency closure

- **`cnf/ext/fennec.bnd`**: append `jakartaREST` to the `-library:` list → brings the Jersey 3.1.3 / HK2 / osgitech.rest repo (provides the `jakarta.ws.rs.client.ClientBuilder` service publisher `org.eclipse.osgitech.rest` required by `AtlasClientComponent`). Same mechanism model.atlas itself uses.
- No Jackson addition needed: the atlas client now uses Jackson 3 (`tools.jackson.*`), already available in this workspace via the fennecCodec library.

## Step 3 — New bridge bundle `org.eclipse.fennec.sensinact.mapping.atlas`

Pure OSGi glue (no plain-Java core → no `.osgi` suffix, matching the sensinact strand's naming). All `Private-Package` (no `@Export`).

```
org.eclipse.fennec.sensinact.mapping.atlas/
  bnd.bnd                       (-workingset SensiNact, -library enableEMF; buildpath:
                                 slf4j.api, org.eclipse.fennec.sensinact.mapping;version=snapshot,
                                 org.eclipse.fennec.model.atlas.scope.api;version=latest)
  src/org/eclipse/fennec/sensinact/mapping/atlas/impl/
    AtlasMappingSourceComponent.java
    AtlasMappingSourceConfig.java    (@ObjectClassDefinition)
    package-info.java
```

**`AtlasMappingSourceComponent`** — `@Component(configurationPolicy = REQUIRE) @Designate(factory = true)`, PID `org.eclipse.fennec.sensinact.mapping.atlas`:

- Mandatory static `@Reference(name = "atlasScope") ReadableScopeService<EObject>` — the deployer selects the scope via `atlasScope.target=(atlas.scope=<scope>)` (the atlas publisher stamps `atlas.scope`/`atlas.stage`/`atlas.base.uri` service properties). If the atlas client config goes away, DS tears the bridge down and all published mappings vanish; on return it reloads. No hand-rolled service tracking.
- Config surface:

| property | type | default | meaning |
|---|---|---|---|
| `atlasScope.target` | reference target | — | selects the atlas scope service |
| `registries` | String[] | `["mappings"]` | atlas registry name(s) to read |
| `object.ids` | String[] | `[]` (= all via `listObjectIds`) | explicit allowlist |
| `stage` | String | `""` (final stage) | read via `registryView(registry, stage)` |
| `refresh.interval.ms` | long | `0` (off) | periodic re-fetch + service swap |
| `retry.interval.ms` | long | `30000` | back-off while initial load fails |

- **Activate**: (1) touch `MappingPackage.eINSTANCE` *first* — pins the generated metamodel in `EPackage.Registry.INSTANCE` so atlas-fetched mappings use the generated EFactory regardless of bundle start order; (2) single-thread `ScheduledExecutorService`, submit `loadAll()` — activation never blocks SCR on network I/O; total failure → warn + retry after `retry.interval.ms`.
- **`loadAll()`**: per registry, resolve ids (`object.ids` or `listObjectIds()`), `get(id)` each; `instanceof ProviderMapping` → validate (`getMid()` set, `providerClasses` resolved / not `eIsProxy()` — an unresolved proxy means the sensor EPackage couldn't be fetched: log error, skip) → `ctx.registerService(ProviderMapping.class, …)`; `instanceof MappingProfile` → register as `MappingProfile`; anything else → warn + skip. Per-object failures never abort the batch. Service properties: `sensinact.mapping.mid`, `atlas.remote=true`, `atlas.scope`, `atlas.registry`, `atlas.object.id`, (`atlas.stage`).
- **Refresh (v1)**: default off = load-on-activate only. When enabled: re-list/re-`get`; the client's ETag/304 path returns the identical cached instance when unchanged, so `held != fetched` detects change → register-new-then-unregister-old (whiteboard sees remove+add); removed ids unregistered; errors during refresh keep current registrations (no flapping on transient outages).
- **Deactivate**: shut down executor, unregister everything.

## Step 4 — Test bundle `org.eclipse.fennec.sensinact.mapping.atlas.tests`

OSGi IT with a **mock Model Atlas** on a JDK `HttpServer` (pattern from `org.eclipse.fennec.openapi.osgi.tests`; needs `-runsystempackages: com.sun.net.httpserver`). Files: `bnd.bnd`, `test.bndrun`, the per-project `build.gradle` resolve-shim (verbatim CLAUDE.md gotcha), `MockModelAtlasServer.java`, `AtlasMappingSourceIT.java`.

Fixtures referenced from the sibling bundle, not copied:

```
-includeresource: \
	data/WeatherProviderMapping.xmi=${workspace}/org.eclipse.fennec.sensinact.mapping.tests/data/WeatherProviderMapping.xmi,\
	data/dwd-weather.ecore=${workspace}/org.eclipse.fennec.sensinact.mapping.tests/model/dwd-weather.ecore
```

Mock endpoints: `GET /scopes` → `{"scopes":["iot"]}`; `GET|HEAD /scopes/iot` (+ETag; registry `mappings` typed `OTHER` — not `SCHEMA`, the client refuses reading SCHEMA registries as objects); `GET /iot/registries/mappings` (lists objectId `dwd-weather`); `GET /iot/registries/mappings/content?objectId=dwd-weather` → the mapping XMI as `application/xmi`; `GET /iot/schema/content?nsUri=http://cdc.dwd.de/common/weather` → `dwd-weather.ecore` bytes (`.ecore` is XMI).

The runtime deliberately **excludes `org.gecko.weather.model`**, so the test proves both paths: mapping XMI from the atlas *and* the sensor EPackage resolved fetch-on-miss from the atlas. Flow: start mock → ConfigAdmin factory config `org.eclipse.fennec.model.atlas.rest.client` (`base.uri=http://localhost:<port>`, `mode=LAZY`, `drift.check.interval.ms=0`) → factory config `org.eclipse.fennec.sensinact.mapping.atlas` (`atlasScope.target=(atlas.scope=iot)`) → await `ProviderMapping` service with `sensinact.mapping.mid=dwd-weather` (proves the generated-EFactory cast + provenance props) → assert `providerClasses[0]` is a resolved dynamic `MOSMIXSWeatherReport` EClass → await `ProviderMappingRegistry` (sensinact gateway runbundles as in `org.eclipse.fennec.sensinact.mapping.tests/test.bndrun`) and assert `getProviderMapping(eClass)` returns the mapping (whiteboard fired, twin registration ran).

`test.bndrun` `-runrequires`: the two new bundles + `org.eclipse.fennec.model.atlas.rest.client.osgi` + `org.eclipse.osgitech.rest` + `org.eclipse.fennec.emf.osgi.component` + `org.eclipse.sensinact.gateway.core.impl` + `org.apache.felix.configadmin`; Felix 7.0.5, JavaSE-21; resolved closure generated via `resolve.test`.

## Step 5 — Docs & housekeeping

- `docs/sensinact-mapping-user-guide.md`: new section "Loading mappings from a Model Atlas" — both ConfigAdmin configs, config/property tables, local-first metamodel rule, refresh semantics. (Page already in the `docs-site/guides.mjs` allowlist — no sync change.)
- `CLAUDE.md`: add the missing sensinact strand section + atlas integration notes (local-jar caveat `cnf/local`, manual jar refresh, `jakartaREST` library) and the hard rule: **atlas bundles + bridge stay OUT of `org.eclipse.fennec.util.workspace.library`** until model.atlas publishes real Maven GAVs (the library re-exports its closure as a `.maven` GAV index — local jars can't survive that).
- No `coverageFloorBundles` change (no plain-JUnit core — consistent with `protobuf.osgi`/`soap.osgi`). EPL-2.0 headers on all new `.java`.
- Leave the three currently uncommitted `org.eclipse.fennec.openapi.*/bnd.bnd` edits untouched (unrelated to this task).

## Verification

```bash
./gradlew clean build
./gradlew :org.eclipse.fennec.sensinact.mapping.atlas.tests:resolve.test   # inspect closure
./gradlew build testOSGi                                                   # new IT green, existing suites stay green
./gradlew :org.eclipse.fennec.util.workspace.library:resolve.required      # must be a no-op diff
```

## Risks

| risk | mitigation |
|---|---|
| Mapping fetched as dynamic EObject → whiteboard cast fails | Bridge pins `MappingPackage.eINSTANCE` before first fetch; atlas registry is local-first on `EPackage.Registry.INSTANCE` (verified). IT asserts the cast. |
| Unresolved sensor-model proxies | Per-object `eIsProxy` validation, skip + log; retry loop for total failure. |
| jakartaREST repo version drift (e.g. old servlet-api) | model.atlas' own osgi.tests closure proves a working combination; committed resolved `-runbundles` pin it. |
| Activation blocking SCR on network | All atlas I/O on a private executor. |
| Stale instances on atlas change | v1 default static load; optional poll uses the client's ETag/304 same-instance guarantee for cheap change detection. |

## Follow-ups (done 2026-07-29): live chain + data ingress

Everything below was verified live against the jena atlas runtime (`model.atlas` local-jena,
REST at `localhost:8086/atlas/rest`, scope `jena`, object registry `sensinactmapping`).

- **XMI migration + metamodel fixes.** `WeatherReportsProviderMapping.xmi` migrated off the
  removed service-level `collectionFeature`/`collectionIndex` to the selector-style
  `referencedResource` (see the user guide's collections section). Two engine/metamodel
  defects found on the way: `ServiceMapping.referencedResource` was non-containment (inline
  `ReferenceMapping`s were dangling → `DanglingHREFException` on every re-serialization) —
  now `containment="true"`; and the registration-side resource generator ignored
  `exclude="false"` with an empty filter (generated ALL attributes instead of none) — now
  aligned with `ValueMapperImpl.getFilteredAttributes`.
- **Atlas upload gotcha.** `xsi:schemaLocation` with a relative ecore reference crashes the
  atlas-side reader (`AIOOBE Index -2` in codec.rest `XMLURIHandler.resolve()`); upload
  mapping XMIs without schemaLocation and as `application/xmi`. *(Resolved: fixed in
  fennec-codec on 2026-07-30 — workaround only needed against older atlas server builds.)*
  Stage flow verified: draft uploads stay invisible, transition to `release`
  triggers registration, deletion unregisters.
- **Data ingress: `InstancePusher`.** New service (separate from `ProviderMappingRegistry`
  by design — no API change): `pushInstance(EObject)` resolves mappings by the instance's
  exact EClass and applies them on the `GatewayThread`. Covered by `InstancePusherTest`
  (mapping.tests) and an end-to-end IT in atlas.tests (mock atlas serves mapping + ecore,
  dynamic instance from the atlas-fetched EPackage, values asserted in the twin).
- **Live smoke test.** `org.eclipse.fennec.sensinact.mapping.atlas.test.component`:
  `WeatherReportsSimulator` (PID `sensinact.mapping.atlas.test.simulator`) — reworked
  (same day) from building EObjects to rendering a raw **XMI payload** (template in the
  bundle, multi-root with `xmi:id` refs since `WeatherReports.reports` is non-containment)
  so the full storyline is live-testable from a cold, empty atlas: payload → fresh
  `ResourceSet` from `ResourceSetFactory` → model unknown = log + drop; after uploading
  ecore + mapping to the atlas, the next tick deserializes and pushes. XMI loading stays in
  the test component by design — the production bundles keep the EObject-only
  `InstancePusher` face. Runtime (`atlas.runtime/launch.bndrun`) gained northbound
  `gogo-shell` + `session-impl` (+ `sensinact.session.manager` → `ALLOW_ALL`) for `sna:`
  verification. Northbound REST was tried and deliberately dropped (pulls
  query-handler-impl, resource.selector.impl, esri.geometry, tools.jackson jakarta-rs + two
  configs incl. the required `JakartarsServletWhiteboardRuntimeComponent`) — gogo is enough
  for a test runtime.
- **Client behavior learned while making the raw-XMI path work** (all verified against the
  rest.client sources / by IT):
  - The atlas client registers a `ResourceSetConfigurator`; atlas-aware nsURI resolution
    only applies to resource sets **created while the client is active** → ingress code
    must create a fresh `ResourceSet` per payload via `ResourceSetFactory`, never hold one
    from before the client existed.
  - Standalone nsURI resolution (`RemoteEPackageProvider.resolve`) is **metadata-first**:
    `GET /{scope}/schema?nsUri=…` (204 = not visible from that scope) and only then
    `GET /{scope}/schema/content?nsUri=…`. The mock atlas needed the metadata endpoint.
  - The **ETag/304 revalidation is load-bearing for EClass identity**: on `304` the client
    returns the *cached* `EPackage` instance, so the mapping's provider classes and a
    later payload resolve to the same instance. A server (or mock) that omits `ETag`
    breaks identity — every fetch builds a new package and the registry lookup silently
    misses. `MockModelAtlasServer` now sends ETags and honors `If-None-Match`.

## Live-test verification (2026-07-30): green end-to-end

The live smoke test (jena atlas, `WeatherReportsSimulator`; client drift check at 10 s,
source refresh at 60 s — see `atlas.local.config/configs/config.json`) now runs the full
storyline clean: cold empty atlas → upload weather ecore + mapping → simulator payloads
deserialized against the atlas-fetched model and pushed into the twin. The remaining
blockers were on the model.atlas side, both fixed there on branch `atlas_client_fixes`
(commit `f8e27fe`, issues model.atlas#160/#161):

- **#160 (client — drift watcher blind to fetch-on-miss packages).** `DriftWatcher`
  compared only the *provider cache* against the server; `EPackage`s held by the
  fetch-on-miss registries, the delegating registry or the publisher were never
  drift-checked, so schema changes/removals on the atlas never reached a running gateway.
  New `DriftListener.heldNsUris()` default method — the watcher now acts on the union of
  the provider cache and everything registered listeners report as held. Also: an
  exception in one scope no longer aborts the whole drift check, and scheduled-check
  failures are logged at WARNING instead of FINE.
- **#161 (server — writer detached the served instance).** The ecore message-body writer
  serialized the *live* `EPackage` instance by putting it into the response resource,
  detaching it on the server — after the first schema fetch, later fetches/revalidations
  saw a corrupted instance. The writer now serializes an `EcoreUtil.copy` and discards
  the response resource (same fix in `JsonSchemaMessageBodyReaderWriter`).

The four `cnf/local` client jars were refreshed from that build (2026-07-30); on
2026-07-31 the local jars were replaced entirely by the Maven Central snapshot GAVs
(see the update note at the top). Client snapshots must be from a build ≥ 2026-07-31 —
older ones lack the `heldNsUris()` drift coverage and the client-side `osgi.service`
capabilities, and an older atlas *server* still carries the detach-on-serve bug. The `xsi:schemaLocation`/AIOOBE upload gotcha above was
fixed separately the same day in fennec-codec's `codec.rest` — the strip-before-upload
workaround is only needed against older atlas server builds.

## Follow-up (2026-08-04): generalization plan settled — see the separate plan doc

The generalization question this plan left open (is the source component's connection
pattern worth extracting?) is answered in
[model-atlas-source-generalization-plan.md](model-atlas-source-generalization-plan.md),
revised 2026-08-04 into a two-level shape:

- **Target architecture (Revision 2, decided):** a generic, **named EObject registry in
  emf.osgi** (`org.eclipse.fennec.emf.osgi.eobject.registry`) that whiteboard
  `EObjectProvider`s feed and consumers query/listen to — individual EObjects are no
  longer published as OSGi services there. Tracked as GitHub issues in
  eclipse-fennec/emf.osgi. Long-term, `ProviderMappingRegistry`/`MappingProfileRegistry`
  become thin typed facades over one named registry instance and the
  `sensinact.mapping.atlas` component dissolves into atlas-provider configuration.
- **Interim step in this repo (Revision 3, part 1 of that doc):** extract the sync engine
  of `AtlasMappingSourceComponent` into a **plain-Java** bundle
  `org.eclipse.fennec.model.atlas.eobject.provider` (named for its future role and home:
  the atlas `EObjectProvider` implementation, to be moved to the model.atlas repo)
  feeding an `AtlasObjectSink`; the sensinact component shrinks to a thin adapter that
  owns service publication (PID, OCD, `@Capability`s, register-before-unregister) with
  **no behavior or config change** — everything this plan built keeps working unchanged
  until the emf.osgi registry ships.

Status: **implemented 2026-08-05** — but not in the interim shape sketched above:
emf.osgi shipped the registry first (issues #72–#77), so the interim service-publishing
adapter was skipped entirely (generalization plan **Revision 5**). Final state: the
engine lives in `org.eclipse.fennec.model.atlas.eobject.provider` as a **writer client**
of the named registry `sensinact-mappings`, the facades are `EObjectRegistryListener`
whiteboard services, and the `org.eclipse.fennec.sensinact.mapping.atlas` bundle (its
factory PID and per-object services) is retired — the config shape changed to the
three-config wiring, see the user guide. The Felix IT was rewritten accordingly
(`AtlasRegistryIntegrationTest`); the live test against the jena atlas uses the migrated
`local.config` configs.
