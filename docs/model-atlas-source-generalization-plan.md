# Plan: Generalize the Atlas mapping source into a reusable atlas EObject-provider bundle

Status: **implemented 2026-08-05** (proposed 2026-08-03, revised 2026-08-04, reshaped
same day it was implemented) — Part 2 (the emf.osgi registry) shipped upstream; Part 1
was rewritten below (Revision 5) to target it directly and is done: provider bundle +
facade refactor + IT rewrite + config migration, full `build testOSGi` green, and the
**live test against the jena atlas green on the new pipeline** (migrated configs in
`…atlas.local.config`). Both upstream emf.osgi gaps are closed too: the library-wiring
gap and the missing `osgi.service` capabilities on the registry faces (so no
`-runprovidedcapabilities` workaround is needed in `…atlas.runtime/launch.bndrun`).
Follow-up to
[model-atlas-integration-plan.md](model-atlas-integration-plan.md), which left the
generalization question open.

> **Revision 2 (2026-08-04): superseded in shape, kept in substance.** The design
> discussion moved the generalization one level further: instead of a reusable
> *atlas-source engine* that registers each fetched EObject as an OSGi service, the
> target is a generic, **named EObject registry in emf.osgi** that providers (file,
> model atlas, …) feed and consumers query. The engine code and all of its hard-won
> semantics (sync/swap, identity compare, retry/refresh) survive — but as the *atlas
> provider* feeding a registry, not as a service publisher.
> See: [Revision 2 — Generic EObject registry in emf.osgi](#revision-2--generic-eobject-registry-in-emfosgi--implemented).

> **Revision 3 (2026-08-04): part 1 reshaped to fit Revision 2.** Extraction in this
> repo first, behind a plain-Java engine (`AtlasObjectSync`) and a small
> **`AtlasObjectSink`** SPI shaped like the future registry-push contract; per-object
> service publication (type dispatch, service properties, register-before-unregister,
> `@Capability`) preserved behaviorally unchanged in a thin sensinact adapter until the
> emf.osgi registry ships.

> **Revision 4 (2026-08-04): emf.osgi registry design finalized** (eclipse-fennec/emf.osgi
> issues #72 parent, #73–#77 sub-issues). Read/write split (`EObjectRegistry` /
> `EObjectRegistryWriter`), sources **push** — the pulled `EObjectProvider` SPI exists
> only for the one initial provider per registry; the atlas source is therefore a
> *writer client*. The writer owns the swap semantics (`sync(source, entries)`:
> identity compare, update-before-remove, per-source gone-object removal). Entry shape
> `(key, object, source, properties)` — `source` = the input channel; `atlas.*` values
> and `emf.nsURI`/`emf.fingerprint` become entry properties.

> **Revision 5 (2026-08-05): Part 2 is implemented — the interim step is dropped.**
> emf.osgi shipped the complete registry stack (issues #72–#77, commits
> `2703529…305dbbf`): `org.eclipse.fennec.emf.osgi.eobject.registry` (API, plain-Java
> impl, DS factory component with gated publication, `FileEObjectProvider`),
> `…eobject.registry.metadata` (the metadata bridge with pluggable
> `AspectAnchorResolver`), Felix ITs and the user guide
> `docs/eobject-registry-guide.md` (authoritative usage documentation). The snapshot
> release (`org.eclipse.fennec.emf:…:1.1.0-SNAPSHOT`) is consumable now.
>
> Consequences for Part 1:
>
> 1. **The interim `AtlasObjectSink` SPI + thin service-publishing adapter is
>    cancelled.** It existed only because the registry did not exist yet; building it
>    now would be throwaway work deleted in the very next step. We go straight to the
>    final state of the Revision 4 bundle cut: per-object OSGi services disappear, the
>    `@Capability` declarations and the register-before-unregister invariant go with
>    them.
> 2. **The engine sheds its own diff.** The writer's `sync(source, entries)` owns the
>    swap semantics; the engine calls it once **per atlas registry** with a source tag
>    `<provider-name>:<atlas-registry>` (the per-atlas-registry scope is load-bearing —
>    a transient failure of one atlas registry must not wipe the entries of the
>    others). Partial-failure semantics preserved via a put-only fallback (below).
> 3. **The earlier "no config-shape changes" constraint is lifted** — it belonged to
>    the interim step. The old factory PID `org.eclipse.fennec.sensinact.mapping.atlas`
>    is retired; deployments move to the three-config wiring of the registry guide
>    (file provider + registry + atlas provider). Nothing released depends on the old
>    PID.
> 4. **Wiring gap found (2026-08-05):** the `fennecEMF` bnd library
>    (`org.eclipse.fennec.emf.osgi.bnd.library.workspace`) did **not** ship the
>    `eobject.registry` bundles — its `required.bndrun` didn't require them, so the
>    generated `fennecEMF.maven` index omitted them. **Fixed upstream the same day**
>    (library now ships `eobject.registry` + `eobject.registry.metadata`); the interim
>    `cnf/ext/central.mvn` pin was removed again.
>
> The sections below are rewritten in this shape. Tracked as GitHub issues in
> **eclipse-fennec/emf.util** — sketches in
> [model-atlas-registry-adoption-issues.md](model-atlas-registry-adoption-issues.md).

## Motivation

`org.eclipse.fennec.sensinact.mapping.atlas` solves a problem that is not sensinact-specific:
*read EObjects from a Model Atlas scope and hand each one to a consumer — resiliently, with
refresh and change detection*. Other strands of this repo (and other Fennec consumers) will
want the same connection pattern — e.g. pulling OpenAPI documents, service configurations, or
any other registry content from an atlas. Today they would have to copy ~300 lines of
lifecycle code. The consumer is the generic EObject registry that emf.osgi now ships; the
atlas source becomes its (first) dynamic writer client, reusable by every content domain.

## Where the old component's parts go

Analysis of `AtlasMappingSourceComponent` (~306 lines), final-state mapping:

**The sync engine — moves to the new atlas provider bundle** (nothing in these parts
knows about sensinact or OSGi service registration):

- the `ReadableScopeService<EObject>` handling and the registry / object-id / stage selection;
- the private single-thread executor (activation never blocks on the network);
- the initial-load → retry-until-complete → periodic-refresh scheduling;
- change detection and failure resilience — but the **diff itself is delegated** to
  `EObjectRegistryWriter.sync` (identity compare, update-before-remove, per-source
  removal live there now, once, for every source);
- the local-EPackage safety net — generalized to a config-driven **required-nsURI
  gate** (see below), removing the compile-time dependency on any domain package.

**The publication-specific parts — dissolve:**

- the `register()` type dispatch and validation (`mid` non-blank, provider classes
  resolved) → moves into the **sensinact facades** (listener side), where it now covers
  *every* source — file-provided mappings get the same validation the atlas ones had;
- the `atlas.*` service properties → become **entry properties** (`atlas.scope`,
  `atlas.registry`, `atlas.stage`, `atlas.object.id`), alongside `emf.nsURI` for model
  anchoring — exactly the worked example in the emf.osgi registry guide;
- **register-before-unregister** → obsolete: there are no per-object services anymore;
  `EObjectRegistryListener.entryUpdated(new, old)` delivers both entries in one
  callback, and the facade orders its internal handling (index new before dropping old);
- the two `@Capability(namespace = "osgi.service")` declarations → deleted with the
  services they declared;
- the factory PID + OCD → replaced by the standard three-config wiring
  (`FileEObjectProvider~…`, `EObjectRegistry~…`, atlas provider config).

## Final design (Revision 5)

### New bundle `org.eclipse.fennec.model.atlas.eobject.provider` — the atlas writer client

Named with the `model.atlas` prefix **deliberately**: its long-term home is the
model.atlas repository (next to `rest.client.osgi`); building it here first keeps the
iteration fast and the green IT as proof. It is a **writer client** of the shipped
registry — it implements no SPI (the pulled `EObjectProvider` is reserved for initial
providers, which a network source deliberately never is: registry publication must not
depend on the network).

- **`AtlasObjectSync`** (`AutoCloseable`, plain Java — no `org.osgi.framework`
  dependency, fully unit-testable) — the engine. Constructor takes
  `ReadableScopeService<EObject>`, an `AtlasSyncSettings`, a key function, and the
  `EObjectRegistryWriter`. Scheduling (initial load → retry → refresh on a private
  single-thread executor) moves verbatim from `AtlasMappingSourceComponent`; a
  package-private constructor accepts the `ScheduledExecutorService` for deterministic
  tests.
- **Sync semantics** (the engine's diff machinery is gone — the writer owns it):
  - a **complete** pass over one atlas registry →
    `writer.sync("<provider>:<atlas-registry>", entries)` — the writer's identity
    compare makes unchanged entries (ETag cache returns the identical instance) no-ops,
    its per-source removal drops objects definitively gone from the atlas;
  - a **partial** pass (listing failed, or individual objects failed to fetch) → no
    `sync` for that atlas registry; the successfully fetched objects go in via granular
    `writer.put` (identity compare still applies), **no removals** — preserving the old
    "transient failure keeps the existing state" semantics exactly; the pass counts as
    incomplete and the retry/refresh scheduling reacts as before.
- **Key derivation** — configurable, mirroring `FileEObjectProvider.featureKeys`: a
  `key.feature` config attribute names the id attribute of the fetched objects
  (sensinact: `mid` / `profileId`); unset falls back to the atlas object id. Objects
  without a key value are skipped (logged).
- **Entry properties**: `atlas.scope`, `atlas.registry`, `atlas.stage` (if set),
  `atlas.object.id`, plus `emf.nsURI` of the object's package.
- **Required-nsURI gate** (replaces the `localPackages` re-pin): a `required.nsuris`
  config attribute lists nsURIs whose *generated* packages must be resolvable before a
  pass may run. Per pass: a listed nsURI missing from `EPackage.Registry.INSTANCE` and
  never seen → the pass is treated as incomplete (retry — this closes the activation
  race where the first fetch would materialize dynamic EObjects and the downstream
  type dispatch would fail silently); once resolved, the engine holds the instance and
  `putIfAbsent`s it per pass (healing the window a model-bundle refresh opens when its
  configurator removes the package on deactivate). No compile-time domain dependency —
  it's pure config.
- **DS factory component** (`configurationPolicy = REQUIRE`, `@Designate(factory =
  true)`, component name e.g. `AtlasEObjectProvider`): config = `atlasScope.target`
  (scope selection, as today), `emf.eobject.registry.name` → mapped to the
  `writer.target` reference on `EObjectRegistryWriter`, `emf.eobject.provider.name`
  (the source-tag prefix), `registries`, `object.ids`, `stage`, `key.feature`,
  `required.nsuris`, `refresh.interval.ms`, `retry.interval.ms`. Because the writer
  service only exists after the registry's gated initial load, readiness ordering comes
  free — the component simply isn't satisfied until the registry is up.
- Exported package `org.eclipse.fennec.model.atlas.eobject.provider` via
  `@Export`/`@Version` on `package-info.java` (repo convention). `-buildpath`:
  `org.eclipse.fennec.model.atlas.scope.api`, `org.eclipse.fennec.emf.osgi.eobject.registry`
  (via `cnf/ext/central.mvn`, see the wiring gap above), EMF via `-library: enableEMF`.
- **Plain-JUnit tests** in `test/` (mocked `ReadableScopeService`/`ReadableRegistryView`,
  a recording `EObjectRegistryWriter`): complete-pass sync per atlas registry with the
  right source tag, partial-pass put-only fallback (no removals), explicit-object-ids
  mode, stage propagation, key-feature extraction + skip-on-missing-key,
  required-nsURI gating (incomplete pass, re-pin after resolution),
  retry-then-refresh scheduling transition, close-during-sync safety. Add the bundle to
  `coverageFloorBundles` in `build.gradle`; `-testpath` needs
  `assertj-core;version=latest` (known gotcha).

### Sensinact side: facades over named registries, atlas bundle retired

- **Two registry instances** (per-domain instances are the shipped convention; the key
  conventions differ): `sensinact-mappings` (keys = `mid`) and `sensinact-profiles`
  (keys = `profileId`). Each gets a `FileEObjectProvider` config as its **initial
  provider** — an empty location list is a valid, empty initial state, and a populated
  one gives the "hold mappings without the atlas" file bootstrap for free.
- **`ProviderMappingRegistryImpl`** drops its `ProviderMapping` service whiteboard and
  becomes an **`EObjectRegistryListener`** whiteboard service carrying
  `emf.eobject.registry.name=sensinact-mappings` — the registry component binds it and
  **replays** current content, so late binding is indistinguishable from early binding
  (the #71 bug class stays structurally impossible). It keeps its EClass index and the
  gateway-thread push exactly as today; `entryUpdated(new, old)` is handled
  index-new-then-drop-old. The **validation** moved from the atlas component lives
  here now (type dispatch `ProviderMapping`?, `mid` non-blank, provider classes present
  and resolved — else skip with a log), applied uniformly to file- and atlas-sourced
  content.
- **`MappingProfileRegistryImpl`** same pattern on `sensinact-profiles`; its direct
  `registerProfile`/`unregisterProfile` API stays for programmatic use.
- **Public APIs unchanged**: `ProviderMappingRegistry` / `MappingProfileRegistry` and
  their consumers (`InstancePusherImpl`, `ProviderModelSensinactMapper`) are untouched.
- **`org.eclipse.fennec.sensinact.mapping.atlas` is deleted** (component, config,
  bundle). Deployments migrate: one old factory config becomes (per domain) a
  `FileEObjectProvider~…` + `EObjectRegistry~…` + `AtlasEObjectProvider~…` config —
  see the registry guide's wiring example; `…mapping.atlas.local.config` and the
  runtime's launch configs are updated accordingly.

### Metadata bridge — optional follow-up, not required

The shipped `…eobject.registry.metadata` bridge with a sensinact `AspectAnchorResolver`
(anchoring a mapping at `ProviderMapping.getProviderClasses()` — the guide names exactly
this example) would additionally expose mappings via
`MetadataService.getClassAspect(eClass, "sensinact.mapping")`. The facades keep their own
EClass index, so nothing needs it functionally today; tracked as an optional issue, can
be picked up when a consumer wants the uniform metadata face.

## Steps

Tracked as GitHub issues in **eclipse-fennec/emf.util** (sketches:
[model-atlas-registry-adoption-issues.md](model-atlas-registry-adoption-issues.md)):

1. **Atlas writer client** — snapshot wiring in `central.mvn`, the new provider bundle,
   engine + DS component, plain-JUnit tests, coverage floor.
2. **Sensinact facades** — listeners instead of whiteboards, validation move, retire
   `sensinact.mapping.atlas`.
3. **Integration tests + configuration migration** — rewrite the Felix IT against the
   new pipeline, migrate `local.config` + runtime bndruns, re-run the live test against
   the jena atlas.
4. **Docs** — user guide, CLAUDE.md, plan closure.
5. *(optional)* metadata-bridge anchor resolver; *(upstream)* add the registry bundles
   to the `fennecEMF` library in emf.osgi.

Note on behavior pinning: the current green IT asserts **per-object services**, which
this change removes by design — the IT is rewritten deliberately (issue 3), keeping the
sensinact-level invariants (mapping reaches the registry facade, twin push works,
provider classes resolve against the atlas) as the stable proof.

## Explicitly out of scope

- **Upstreaming to model.atlas** — the naming prepares for it; the move itself is a later,
  separate decision (needs a model.atlas release cycle and would flip the dependency
  direction for this repo: consume instead of publish).
- **Workspace library** — the atlas bundles (client *and* this new provider) are still not
  in `org.eclipse.fennec.util.workspace.library`; that remains the open decision recorded
  in CLAUDE.md, unchanged by this refactoring.
- **Changes to the emf.osgi registry itself** — anything found lacking there is filed as
  an emf.osgi issue, not worked around here (exception: the `central.mvn` snapshot
  coordinates until the library gap is fixed upstream).

## Effort & risk

Moderate, mostly mechanical: the engine's scheduling moves verbatim and *loses* its diff
machinery (the writer owns it); the facades swap one whiteboard kind for another with
replay semantics that are strictly more forgiving. The genuinely new, watch-carefully
parts: the **per-atlas-registry source tag** (wrong scoping wipes sibling registries'
entries on transient failures), the **partial-pass put-only fallback** (accidentally
calling `sync` on a partial pass removes objects that merely failed to fetch), the
**required-nsURI gate** (replaces a load-bearing race fix — the IT must cover the
model-bundle-late case), and the **IT rewrite** (the old per-object-service assertions
are the pinning proof being replaced — port the sensinact-level assertions first, then
delete).

---

# Revision 2 — Generic EObject registry in emf.osgi — ✅ implemented

Decided 2026-08-04, finalized same day; **implemented 2026-08-05** in
eclipse-fennec/emf.osgi (issues #72 parent, #73 core API/impl, #74 OSGi factory
component + gated publication + listener whiteboard, #75 file provider, #76 metadata
bridge, #77 Felix ITs + docs; commits `2703529…305dbbf`). The authoritative usage
documentation is now the emf.osgi guide **`docs/eobject-registry-guide.md`** (published
on its docs site); the decision list below stays as the design record.

Snapshot artifacts (`org.eclipse.fennec.emf`, `1.1.0-SNAPSHOT`):
`org.eclipse.fennec.emf.osgi.eobject.registry`,
`org.eclipse.fennec.emf.osgi.eobject.registry.metadata`. **Not yet in the `fennecEMF`
bnd library** (see Revision 5, point 4).

## Motivation (revised)

The sensinact mapping registries (`ProviderMappingRegistry`, `MappingProfileRegistry`) are
instances of a general pattern: a connected system holds **authored EObject content**
(provider mappings, mapping profiles, OCL expression libraries, …) locally, keyed by its
own ids, resilient against the content's remote source being unavailable — with a
mechanism to load and synchronize that content from *some* source (file, database, model
atlas). This is not mapping-specific and not atlas-specific, so the registry must not live
in either place.

## Decisions (as implemented)

1. **Home is emf.osgi**, as a sibling of `org.eclipse.fennec.emf.osgi.metadata` —
   bundle/package `org.eclipse.fennec.emf.osgi.eobject.registry`. Rationale: all
   dependency arrows already point there (emf.util and model.atlas both consume emf.osgi);
   it ships automatically via the `fennecEMF` bnd library *(the wiring gap in Revision 5
   notwithstanding)*; the metadata bridge becomes a bundle neighbor instead of a
   cross-repo integration. The model.atlas `mgmt.api.EObjectRegistryService` is the
   *server/management plane* (ObjectMetadata, stages, approval, storage back ends) — a
   different animal; this is the *edge plane* holding the actual EObjects.
2. **One registry instance per content domain, configurable.** ConfigAdmin factory
   component (`configurationPolicy = REQUIRE`, `@Designate(factory = true)`, component
   name `EObjectRegistry`); each config creates one registry service instance carrying a
   stable, human-chosen `emf.eobject.registry.name` service property. Per-type instances
   are the convention, not enforced by API. Optional declared content types as service
   property. **No `@Modified`**: any config change restarts the instance — `name` is
   identity by construction.
3. **Individual EObjects are NOT published as OSGi services; the registry is.** Content
   changes flow through registry listeners, not through a ServiceTracker.
4. **Read/write interface split; sources push.** One instance registered under
   `EObjectRegistry` (read face) and `EObjectRegistryWriter` (write face). Dynamic
   sources implement **no SPI** — they reference the writer by registry name and push
   (`put`/`remove`/`sync`). The writer service only exists after initialization, so push
   sources get readiness ordering for free; a dying source leaves its content in place.
5. **String-keyed content, swap semantics in the writer.** Keys unique per registry, key
   convention per domain. Entry record `(key, object, source, properties)` — `source` is
   the input channel and scopes `sync(source, entries)` (identity compare,
   update-before-remove, per-source gone-object removal); model anchoring lives in the
   entry properties under `emf.nsURI`/`emf.fingerprint`. Cross-source key collisions:
   last write wins, adopts the new source, logged.
6. **Readiness gating via one pulled initial provider.** Selected through a
   config-targeted DS reference (`initialProvider.target=(emf.eobject.provider.name=…)`);
   SPI is `CompletableFuture<Void> load(EObjectRegistryWriter)`, run on a private
   executor; both service faces registered via `BundleContext` only on successful
   completion. A failed load is logged and the registry stays unpublished — the absent
   service is the signal. Dynamic sources never gate readiness. Role split: file =
   initial + readiness-gating; atlas = dynamic + trailing.
7. **Plain-Java core** — registry impl, writer, initial-provider SPI, listeners and the
   file provider work without OSGi (`CompletableFuture`, not OSGi `Promise`); bootstrap
   factory `EObjectRegistries.createRegistry(name[, initialProvider])`.
8. **Change notification is part of the API** (`EObjectRegistryListener`, all methods
   default): `entryAdded` / `entryUpdated(new, old)` / `entryRemoved`; add-time **replay**
   of current content; also bindable as whiteboard services targeted by registry name.
   Callbacks run on the writing thread under the registry lock — listeners must be fast
   and must not call back into the writer.
9. **The metadata bridge** (`…eobject.registry.metadata`): registry listener **and**
   `MetadataHandler`, contributing registry content as `AspectEntry` (copy, snapshot
   lookup) onto `ClassMetadata` per live model version; anchor resolution pluggable via
   `AspectAnchorResolver` (default: the content's own EClass; sensinact would anchor at
   `getProviderClasses()`). Id-based lookups and change notification stay with the
   registry.

## Bundle cut (final)

| Piece | Home | Status |
|---|---|---|
| `org.eclipse.fennec.emf.osgi.eobject.registry` (+ `.metadata`, `.itest`) | emf.osgi | ✅ shipped (snapshot) |
| atlas writer client `org.eclipse.fennec.model.atlas.eobject.provider` | emf.util first, later model.atlas | ✅ implemented (2026-08-05) |
| sensinact facades over named registries; `sensinact.mapping.atlas` retired | emf.util | ✅ implemented (2026-08-05) — build + `testOSGi` + jena live test green |
