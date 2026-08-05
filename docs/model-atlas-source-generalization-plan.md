# Plan: Generalize the Atlas mapping source into a reusable atlas EObject-provider bundle

Status: proposed (2026-08-03, revised 2026-08-04) — not started. Follow-up to
[model-atlas-integration-plan.md](model-atlas-integration-plan.md), which left the
generalization question open.

> **Revision 2 (2026-08-04): superseded in shape, kept in substance.** The design
> discussion moved the generalization one level further: instead of a reusable
> *atlas-source engine* that registers each fetched EObject as an OSGi service, the
> target is a generic, **named EObject registry in emf.osgi** that providers (file,
> model atlas, …) feed and consumers query. The engine code and all of its hard-won
> semantics (sync/swap, identity compare, retry/refresh) survive — but as the *atlas
> provider* feeding a registry, not as a service publisher.
> See: [Revision 2 — Generic EObject registry in emf.osgi](#revision-2--generic-eobject-registry-in-emfosgi).

> **Revision 3 (2026-08-04): part 1 reshaped to fit Revision 2.** The extraction below
> still happens in this repo first (fast iteration, proven by the green IT), and still
> moves to the model.atlas repo later — but three things change against the original
> proposal: (1) the bundle is named for its future role,
> **`org.eclipse.fennec.model.atlas.eobject.provider`** — the (future) atlas
> implementation of the emf.osgi `EObjectProvider` SPI — so the later move changes
> nothing for consumers; (2) the engine no longer registers OSGi services itself: it
> syncs into a small **`AtlasObjectSink`** SPI deliberately shaped like the future
> registry-push contract, which makes the engine **plain Java** (no `BundleContext` —
> the original "there is no OSGi-free core to split off" claim died with the service
> registration); (3) per-object service publication — the type dispatch, the service
> properties, register-before-unregister ordering and the `@Capability` declarations —
> becomes the *sensinact adapter's sink implementation*, preserved behaviorally
> unchanged until the emf.osgi registry ships and the adapter dissolves into
> atlas-provider config (Revision 2 bundle cut). The sections below are rewritten in
> this shape; the original service-publishing SPI (`ServiceSpec`/`serviceFor`) is gone.

## Motivation

`org.eclipse.fennec.sensinact.mapping.atlas` solves a problem that is not sensinact-specific:
*read EObjects from a Model Atlas scope and hand each one to a consumer — resiliently, with
refresh and change detection*. Other strands of this repo (and other Fennec consumers) will
want the same connection pattern — e.g. pulling OpenAPI documents, service configurations, or
any other registry content from an atlas. Today they would have to copy ~300 lines of
lifecycle code. Long-term (Revision 2) the consumer is the generic EObject registry in
emf.osgi; today it is the per-object service publication the sensinact whiteboards rely on.

The proposal: extract the sync engine into a reusable bundle behind a sink SPI, and shrink
the sensinact component to a thin adapter that owns the service-publishing sink. **No
behavior change** — existing configurations (factory PID
`org.eclipse.fennec.sensinact.mapping.atlas`, all current properties) keep working untouched,
and the green end-to-end OSGi IT stays as the proof.

## What is generic vs. mapping-specific today

Analysis of `AtlasMappingSourceComponent` (~306 lines):

**Generic — the sync engine.** Nothing in these parts knows about sensinact, and (after
Revision 3) nothing needs to know about OSGi service registration either:

- the `ReadableScopeService<EObject>` handling and the registry / object-id / stage selection;
- the private single-thread executor (activation never blocks on the network);
- the initial-load → retry-until-complete → periodic-refresh scheduling;
- the `sync`/`syncRegistry`/`swap` machinery:
  - change detection by **identity compare** (the atlas client's ETag cache returns the
    identical instance while an object is unchanged on the server),
  - transient per-object / per-registry failures keep the existing state,
  - objects definitively gone from the atlas are removed;
- the local-EPackage re-pinning safety net (see `localPackages` below).

**Publication-specific — stays with the adapter (today's service-publishing sink):**

- the `register()` type dispatch: `ProviderMapping` → validate `mid` → property
  `sensinact.mapping.mid`; `MappingProfile` → validate `profileId` → property
  `sensinact.mapping.profile.id`; anything else is skipped with a warning;
- the base atlas service properties (`atlas.remote`, `atlas.scope`, `atlas.registry`,
  `atlas.stage`, `atlas.object.id`) — derived from the sink callback's `AtlasObjectRef`.
  The constants stay in the sensinact bundle for now; their Revision-2 future is
  provenance metadata on registry entries, an open design point there;
- **register-before-unregister** on change, so whiteboards see remove-after-add — an
  invariant of *service* publication, not of syncing (a registry replaces atomically);
- the two `@Capability(namespace = "osgi.service")` declarations for the resolver;
- the factory PID, the metatype OCD texts, the thread name.

## Proposed design: plain-Java engine + sink SPI + thin DS adapter

DS component annotations do not inherit, so an abstract base component is the wrong tool.
Instead, a plain-Java engine class that concrete DS components *use*:

### New bundle `org.eclipse.fennec.model.atlas.eobject.provider`

Named with the `model.atlas` prefix **deliberately**: its long-term home is the model.atlas
repository (next to `rest.client.osgi`), and `eobject.provider` names its future role — the
atlas implementation of the `EObjectProvider` SPI of emf.osgi's
`org.eclipse.fennec.emf.osgi.eobject.registry` (Revision 2). Keeping the upstream namespace
now means the later move changes nothing for consumers. Until then it lives in this repo for
fast iteration, proven by the existing integration test. The engine is plain Java (no
`org.osgi.framework` dependency — service registration lives in the adapter's sink), so the
bundle is fully unit-testable with plain JUnit in its `test/` folder (repo convention).
Exported package `org.eclipse.fennec.model.atlas.eobject.provider`, API:

- **`AtlasObjectSync`** (`AutoCloseable`) — the engine. Constructor takes
  `ReadableScopeService<EObject>`, an `AtlasSyncSettings`, a `Collection<EPackage>` of local
  packages, and an `AtlasObjectSink`. All sync/swap/refresh/scheduling code moves here
  essentially verbatim. A package-private constructor additionally accepts the
  `ScheduledExecutorService`, so scheduling behavior is deterministically testable.
- **`AtlasSyncSettings`** — record: `registries`, `objectIds`, `stage`,
  `refreshIntervalMs`, `retryIntervalMs`, `threadName`. Each adapter keeps its **own** OCD
  annotation (metatype names and descriptions are domain-specific; OCDs cannot be
  parameterized) and maps it to this record — only the attribute-name *convention*
  (`registries`, `object.ids`, `stage`, `refresh.interval.ms`, `retry.interval.ms`) is shared.
- **`AtlasObjectSink`** — the single SPI a consumer implements, deliberately shaped like
  the push contract the future emf.osgi `EObjectProvider` will feed a registry with:

  ```java
  public interface AtlasObjectSink {
      /** A newly appeared object. */
      void add(AtlasObjectRef ref, EObject object);
      /** A changed object (the atlas returned a new instance for a known ref). */
      void replace(AtlasObjectRef ref, EObject object);
      /** An object definitively gone from the atlas. */
      void remove(AtlasObjectRef ref);
  }
  ```

  with one small record:

  ```java
  public record AtlasObjectRef(String scope, String registry, String stage, String objectId) {}
  ```

  The engine owns *when* the callbacks fire (identity-compare short-circuit, transient
  failures fire nothing, gone objects fire `remove`); the sink owns *what happens* —
  today: service registration with remove-after-add ordering; later: a registry put.

- **`localPackages`** (constructor parameter) — EPackages the engine re-pins into
  `EPackage.Registry.INSTANCE` (`putIfAbsent`) before every sync pass. Deliberately a
  parameter defaulting to empty, not a requirement. In the normal case it is redundant: a
  fennec-generated model bundle's configuration component already puts its package into
  `EPackage.Registry.INSTANCE` on activate, and the atlas client resolves local-first
  against exactly that registry. The hook exists for the cases where that is not enough:
  (1) DS gives no activation ordering between the model bundle's configurator and the
  source — if the first fetch wins the race, objects materialize as dynamic EObjects and
  the type dispatch fails *silently*; (2) the generated configurator removes the package
  on deactivate, so a model-bundle refresh while the source keeps running opens a window
  that the re-pin-per-sync heals; (3) pinning `INSTANCE` is a property of the fennec
  generator template, not an EMF guarantee — a domain model registered only via the
  emf.osgi `EPackageConfigurator` whiteboard is invisible to the atlas client's delegating
  registry. Domains that don't face these cases pass nothing; the sensinact mapping
  adapter passes `MappingPackage.eINSTANCE` because of (1) and (2).

### `AtlasMappingSourceComponent` shrinks to a thin adapter (~80 lines)

Same factory PID, same OCD, same two `@Capability` declarations — these **must** stay with
the bundle that knows the concrete service types, so the resolver story is unchanged
(bndruns still resolve without `skip:="osgi.service"`). The component builds the settings
and a service-publishing `AtlasObjectSink` carrying today's dispatch/validation logic, the
atlas service properties and the register-before-unregister ordering (it is the only place
that still touches `BundleContext`/`ServiceRegistration`), then delegates its lifecycle to
`AtlasObjectSync`. Config shape is untouched; the live-test configurator JSON from
2026-07-30 works as-is. When the emf.osgi registry ships, this adapter is replaced by a
registry-feeding sink (then: `EObjectProvider` implementation) plus config, per the
Revision 2 bundle cut.

### Considered and rejected: whiteboard of handler services

A single generic DS component discovering sink *services* would decouple further, but the
`@Capability` declarations, the per-domain config targeting, and the OCD texts all still
need a domain bundle anyway — so it adds wiring complexity (config-to-sink matching)
without removing any domain code. Library-plus-adapter is the better fit. (Revision 2 does
move to a whiteboard — but one level up, for providers feeding the registry, where the
registry component owns config and capabilities.)

## Steps

1. **Create `org.eclipse.fennec.model.atlas.eobject.provider`** — `bnd.bnd` with
   `-buildpath: org.eclipse.fennec.model.atlas.scope.api;version=latest` + EMF via
   `-library: enableEMF`, `@Export`/`@Version` via `package-info.java` (repo convention).
   No OSGi framework/DS dependency in the exported API. Move the engine logic out of
   `AtlasMappingSourceComponent` into `AtlasObjectSync` + the SPI types above.
2. **Refactor `org.eclipse.fennec.sensinact.mapping.atlas`** to the thin adapter (component
   + service-publishing sink); add the new bundle to its `-buildpath`.
3. **Unit tests for the engine** (plain JUnit, mocked `ReadableScopeService`/
   `ReadableRegistryView` + a recording `AtlasObjectSink` — no `BundleContext`/
   `ServiceRegistration` mocking needed anymore): identity-compare short-circuit,
   add/replace/remove sequencing on change, transient-failure resilience (no callbacks,
   state kept, `complete=false`), gone-object removal, explicit-object-ids mode, stage
   propagation, retry-then-refresh scheduling transition, close-during-sync safety. This is
   a real win: today these semantics are only covered indirectly through the Felix IT. Add
   the bundle to `coverageFloorBundles` in `build.gradle` (repo convention: every core
   bundle with plain-JUnit tests joins the 30% JaCoCo tripwire). `-testpath` needs
   `assertj-core;version=latest` (known gotcha).
4. **Keep `org.eclipse.fennec.sensinact.mapping.atlas.tests` as-is** — it now proves adapter
   + engine end-to-end in Felix against the mock atlas, including the remove-after-add
   ordering that lives in the adapter's sink. Expectation: zero test changes; the bndrun
   may need the new bundle in `-runbundles` after re-resolve.
5. **Verify**: `./gradlew clean build testOSGi`; optionally re-run the live test against the
   jena atlas (launch.bndrun needs the new bundle after re-resolve).
6. **Docs**: update CLAUDE.md (Model Atlas integration section) and the user guide reference
   in `docs/`; note the outcome in `model-atlas-integration-plan.md`'s open-questions list.

## Explicitly out of scope

- **Upstreaming to model.atlas** — the naming prepares for it; the move itself is a later,
  separate decision (needs a model.atlas release cycle and would flip the dependency
  direction for this repo: consume instead of publish).
- **The emf.osgi registry itself** — tracked in eclipse-fennec/emf.osgi (Revision 2 below);
  this refactoring only positions the engine to become its atlas provider.
- **Workspace library** — the atlas bundles (client *and* this new provider) are still not
  in `org.eclipse.fennec.util.workspace.library`; that remains the open decision recorded
  in CLAUDE.md, unchanged by this refactoring.
- **Config-shape changes** — no new properties, no renames.

## Effort & risk

Small and low-risk: the engine code moves nearly verbatim; the only new code is the SPI
surface (~3 small types) and the unit tests. The existing green OSGi IT pins the observable
behavior before/after. Main watch-items: keep the `@Capability` declarations, the
register-before-unregister ordering and the `EPackage` pinning exactly as they are — all
three are load-bearing and easy to lose in a refactoring — and keep the sink boundary free
of OSGi types, or the engine loses the plain-Java property that Revision 2 relies on.

---

# Revision 2 — Generic EObject registry in emf.osgi

Decided 2026-08-04. Extends and partially supersedes the plan above: the reusable piece is
not an atlas-source *engine* but a generic **local EObject registry**; the engine becomes
one of its providers.

## Motivation (revised)

The sensinact mapping registries (`ProviderMappingRegistry`, `MappingProfileRegistry`) are
instances of a general pattern: a connected system holds **authored EObject content**
(provider mappings, mapping profiles, OCL expression libraries, …) locally, keyed by its
own ids, resilient against the content's remote source being unavailable — with a
mechanism to load and synchronize that content from *some* source (file, database, model
atlas). This is not mapping-specific and not atlas-specific, so the registry must not live
in either place.

## Decisions

1. **Home is emf.osgi**, as a sibling of `org.eclipse.fennec.emf.osgi.metadata` —
   proposed bundle/package `org.eclipse.fennec.emf.osgi.eobject.registry`. Rationale: all
   dependency arrows already point there (emf.util and model.atlas both consume emf.osgi);
   it ships automatically via the `fennecEMF` bnd library; the metadata bridge becomes a
   bundle neighbor instead of a cross-repo integration. The model.atlas
   `mgmt.api.EObjectRegistryService` is the *server/management plane* (ObjectMetadata,
   stages, approval, storage back ends) — a different animal; this is the *edge plane*
   holding the actual EObjects.
2. **One registry instance per content domain, configurable.** ConfigAdmin factory
   component (`configurationPolicy = REQUIRE`, `@Designate(factory = true)`); each config
   creates one registry service instance carrying a stable, human-chosen
   `eobject.registry.name` service property (e.g. `sensinact-mappings`, `ocl-cache`) —
   same convention as `ServiceClient.PROP_NAME`. Per-type instances are the convention,
   not enforced by API: the instance is the isolation boundary, not the type. Optional
   declared content types (nsURI/EClass names) as service property for discovery and
   fail-fast validation.
3. **Individual EObjects are NOT published as OSGi services; the registry is.** The
   whiteboard pattern moves one level up: *providers* and *listeners* are whiteboard
   services targeted at a registry by name. This drops the per-object
   ServiceRegistration churn of the original plan.
4. **Provider inversion — no public write API.** The registry component *tracks*
   `EObjectProvider` services (matched via their `eobject.registry.name` property) and
   pulls content from them; providers never look the registry up. This resolves the
   readiness chicken-and-egg (below) and removes the mutable face from the API entirely.
5. **Readiness gating.** The config declares which providers are **initial/required**
   (default: the file provider). The component activates, runs required providers on a
   private executor (activation never blocks — the lesson from the engine plan), and only
   then registers the `EObjectRegistry` service manually via `BundleContext`. Consumers
   referencing the registry therefore never observe a half-loaded one, and DS ordering is
   free. **Dynamic providers** (atlas) bind later and refresh continuously; they never
   gate readiness — otherwise service publication would depend on the network, breaking
   the "hold mappings without the atlas" requirement. Role split: file = initial +
   readiness-gating; atlas = dynamic + trailing.
6. **Plain-Java core is mandatory** (the non-OSGi argument applies here as everywhere):
   registry impl, provider SPI, change listeners and the file-load mechanism are usable
   without OSGi; a bootstrap factory analogous to `MetadataServices.create(…)` wires them.
   The OSGi layer (factory component, whiteboards, delayed registration, metatype) sits on
   top.
7. **Change notification is part of the API** (`EObjectRegistryListener`): with no
   per-object services there is no ServiceTracker to lean on. Listeners can additionally
   be OSGi whiteboard services (targeted by registry name) with a replay of current
   content on late binding — analogous to `MetadataWhiteboard.addMetadataHandler`.
8. **The metadata service is the runtime bridge for model-anchored lookup.** A bridge
   bundle/listener attaches registry content as `AspectEntry` (typeId per domain, e.g.
   `sensinact.mapping`) to `ClassMetadata`, so runtime code holding an `EClass` asks
   `MetadataService.getClassAspect(eClass, typeId)` — one uniform face for mappings, OCL,
   codec aspects. The bridge is bidirectional-aware: registry listener (content changes →
   aspects) **and** `MetadataHandler` (new model version registered → re-contribute
   aspects onto the new fingerprinted tree). Boundaries: id-based lookups
   (`getProfile(profileId)`) and change notification stay with the registry — aspects on
   published metadata trees emit no events and don't update the index. Registry = source
   of truth; metadata = uniform model-anchored read surface.

## Revised bundle cut

| Piece | Home | Content |
|---|---|---|
| `org.eclipse.fennec.emf.osgi.eobject.registry` | emf.osgi | API + plain-Java core: `EObjectRegistry` (read face), `EObjectProvider` SPI, `EObjectRegistryListener`, bootstrap factory; DS factory component with readiness gating; file provider (default initial provider) |
| metadata bridge (same bundle or neighbor) | emf.osgi | registry listener + `MetadataHandler` contributing `AspectEntry`s |
| atlas provider | model.atlas repo (next to `rest.client.osgi`) | `org.eclipse.fennec.model.atlas.eobject.provider` — the engine from part 1, built in emf.util first (Revision 3), moved once the registry exists: sync/swap/retry/identity-compare feeding a named registry through an `EObjectProvider`-shaped sink |
| sensinact facades | emf.util | `ProviderMappingRegistry`/`MappingProfileRegistry` become thin typed facades over one named registry instance; the `sensinact.mapping.atlas` component dissolves into atlas-provider config |

## Implementation route

Tracked as GitHub issues in **eclipse-fennec/emf.osgi** (parent issue + sub-issues: core
API/impl, OSGi factory component + readiness, file provider, metadata bridge, IT + docs +
library wiring). Follow-ups afterwards: atlas provider in the model.atlas repo, sensinact
facade refactoring in emf.util.
