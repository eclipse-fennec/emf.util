# GitHub issue sketches — adopt the emf.osgi EObject registry

Companion to [model-atlas-source-generalization-plan.md](model-atlas-source-generalization-plan.md)
(Revision 5, 2026-08-05). Issues 1–4 are the work; 5 is optional; 6 goes to
**eclipse-fennec/emf.osgi**, not this repo. Copy each block below into a GitHub issue;
the checklists double as time-tracking granularity. Delete this file (or mark the
sketches with the real issue numbers) once filed.

Suggested order: 1 → 2 → 3 → 4 (5/6 anytime). 2 can start once 1's bundle skeleton
builds; 3 needs 1 + 2.

> **Status 2026-08-05: issues 1–4 are done** (implemented with commit `af7e3ee`,
> "solves issue#22"). The blocks below are kept as the record of scope and acceptance,
> not as open work. `./gradlew clean build testOSGi` is green, and the **live test
> against the jena atlas passed on the new pipeline** (migrated configs in
> `…atlas.local.config`). Issue 6 was fixed upstream the same day; issue 7 has landed
> too — the consumed `org.eclipse.fennec.emf.osgi.eobject.registry` 1.1.0-SNAPSHOT
> declares the two `osgi.service` capabilities, so `…atlas.runtime/launch.bndrun`
> resolves without the `-runprovidedcapabilities` workaround. Only optional issue 5
> remains.

---

## Issue 0 (parent): Adopt the emf.osgi EObject registry for the sensinact mapping atlas integration

**Labels:** enhancement

emf.osgi shipped the generic named EObject registry
(`org.eclipse.fennec.emf.osgi.eobject.registry`, eclipse-fennec/emf.osgi#72–#77,
snapshot `1.1.0-SNAPSHOT`; guide: `docs/eobject-registry-guide.md` in emf.osgi). This
parent tracks moving our Model Atlas integration onto it, per
`docs/model-atlas-source-generalization-plan.md` (Revision 5):

- the atlas sync engine becomes a generic **writer client** bundle
  `org.eclipse.fennec.model.atlas.eobject.provider` (emf.util first, model.atlas later);
- the sensinact mapping registries become **registry listeners** (facades over the named
  registries `sensinact-mappings` / `sensinact-profiles`);
- per-object `ProviderMapping`/`MappingProfile` OSGi services, the `@Capability`
  declarations and the `org.eclipse.fennec.sensinact.mapping.atlas` bundle are retired;
- local mapping files become first-class via `FileEObjectProvider` as each registry's
  initial provider.

Sub-issues:

- [x] Atlas writer client bundle (`model.atlas.eobject.provider`)
- [x] Sensinact facades over named registries; retire `sensinact.mapping.atlas`
- [x] Integration tests + runtime configuration migration + live test
- [x] Documentation
- [ ] #__ *(optional)* Metadata-bridge anchor resolver for provider mappings
- [x] *(upstream)* ~~ship `eobject.registry` in the `fennecEMF` library~~ — fixed in emf.osgi 2026-08-05, no issue needed
- [x] *(upstream)* ~~declare `osgi.service` capabilities for the registry faces~~ — landed in emf.osgi 2026-08-05, no workaround needed

---

## Issue 1: Atlas writer client — new bundle `org.eclipse.fennec.model.atlas.eobject.provider`

**Labels:** enhancement
**Estimate:** ~2–3 d
**Status:** done (2026-08-05)

Extract the sync engine of `AtlasMappingSourceComponent` into a reusable, generic bundle
that pushes atlas content into a named emf.osgi EObject registry through
`EObjectRegistryWriter`. Plain-Java engine + thin DS factory component; no sensinact
dependency, no per-object services. Design: plan Revision 5, section "New bundle".

**Tasks**

- [x] Wiring: ~~pin the registry GAV in `cnf/ext/central.mvn`~~ — obsolete: the
      `fennecEMF` library ships the registry bundles since the upstream fix
      (2026-08-05); nothing to pin, just verify resolution
- [x] Bundle skeleton: `bnd.bnd` (`-buildpath`: `model.atlas.scope.api`,
      `emf.osgi.eobject.registry`, `-library: enableEMF`), `@Export`/`@Version`
      `package-info.java`
- [x] `AtlasObjectSync` (plain Java, `AutoCloseable`): scheduling moved verbatim
      (private executor, initial load → retry → refresh); package-private ctor taking
      the `ScheduledExecutorService` for tests
- [x] Sync semantics: complete pass per atlas registry →
      `writer.sync("<provider>:<atlas-registry>", entries)`; partial pass → granular
      `writer.put` only, **no removals**, pass counts incomplete
- [x] Key derivation: `key.feature` config (mirrors `FileEObjectProvider.featureKeys`),
      fallback = atlas object id; skip + log objects without key value
- [x] Entry properties: `atlas.scope`, `atlas.registry`, `atlas.stage`,
      `atlas.object.id`, `emf.nsURI`
- [x] Required-nsURI gate (`required.nsuris` config): missing + never seen → pass
      incomplete (retry); once resolved → hold instance, `putIfAbsent` per pass
- [x] DS factory component `AtlasEObjectProvider` (`REQUIRE`, `@Designate(factory=true)`,
      OCD): `atlasScope.target`, `emf.eobject.registry.name` (→ `writer.target`),
      `emf.eobject.provider.name`, `registries`, `object.ids`, `stage`, `key.feature`,
      `required.nsuris`, `refresh.interval.ms`, `retry.interval.ms`
- [x] Plain-JUnit tests (mocked scope service, recording writer): source-tag scoping,
      partial-pass fallback, explicit object ids, stage propagation, key extraction,
      nsURI gating, retry→refresh transition, close-during-sync;
      `-testpath: assertj-core;version=latest`
- [x] Add bundle to `coverageFloorBundles` in `build.gradle`
- [x] `./gradlew clean build` green

**Acceptance:** the bundle builds and is fully covered by plain-JUnit tests; nothing in
it references sensinact or `org.osgi.framework`; a transient failure of one atlas
registry never removes entries of another.

---

## Issue 2: Sensinact mapping registries consume named EObject registries; retire the atlas source bundle

**Labels:** enhancement
**Estimate:** ~1–2 d
**Status:** done (2026-08-05)

Replace the per-object service whiteboards in `org.eclipse.fennec.sensinact.mapping`
with `EObjectRegistryListener` whiteboard services over two named registries, and delete
`org.eclipse.fennec.sensinact.mapping.atlas`. Public APIs (`ProviderMappingRegistry`,
`MappingProfileRegistry`) unchanged. Design: plan Revision 5, section "Sensinact side".

**Tasks**

- [x] `ProviderMappingRegistryImpl`: drop the `ProviderMapping` reference whiteboard;
      register as `EObjectRegistryListener` with
      `emf.eobject.registry.name=sensinact-mappings` (name configurable, sensible
      default); keep EClass index + gateway push; `entryUpdated(new, old)` handled
      index-new-then-drop-old
- [x] Move validation from the old atlas component into the listener: type dispatch,
      `mid` non-blank, provider classes present and resolved — skip + log on failure
      (now also covers file-sourced content)
- [x] `MappingProfileRegistryImpl`: same pattern on `sensinact-profiles`
      (`profileId` keys); keep the programmatic register/unregister API
- [x] Delete `org.eclipse.fennec.sensinact.mapping.atlas` (component, config, bundle,
      `@Capability` declarations); remove from `settings.gradle`/workspace as needed
- [x] Adjust `org.eclipse.fennec.sensinact.mapping` `bnd.bnd` (buildpath:
      `emf.osgi.eobject.registry`)
- [x] Unit tests in `mapping.tests`: listener add/update/remove against a plain-Java
      registry (`EObjectRegistries.createRegistry`), validation skips, replay-on-bind
- [x] `./gradlew clean build` green

**Acceptance:** no per-object mapping/profile services anywhere; facades fed exclusively
through registries; validation identical for file- and atlas-sourced content; public
API consumers (`InstancePusherImpl`, mapper) untouched.

---

## Issue 3: Integration tests + runtime configuration migration + live test

**Labels:** test
**Estimate:** ~1–2 d
**Status:** done (2026-08-05) — full build incl. `testOSGi` green; jena live test green

Rewrite the Felix IT for the new pipeline (mock atlas → atlas provider → registries →
facades → sensinact twin) and migrate all deployment configs off the retired factory
PID `org.eclipse.fennec.sensinact.mapping.atlas`.

**Tasks**

- [x] Rewrite `org.eclipse.fennec.sensinact.mapping.atlas.tests` IT: replace
      per-object-service assertions with registry-entry assertions (`atlas.*` +
      `emf.nsURI` entry properties, keys = `mid`/`profileId`); keep the sensinact-level
      invariants (mapping reaches `ProviderMappingRegistry` by EClass, provider classes
      resolve against the atlas, `InstancePusher` round-trip)
- [x] Cover the model-bundle-late case (required-nsURI gate) and the
      source-loss/refresh cases end-to-end — source-loss/refresh is covered in the IT
      (`refreshRemovesObjectsGoneFromTheAtlas`); the nsURI gate ended up covered by the
      engine's plain-JUnit test (`requiredNsUriPostponesPassUntilResolvedThenRePins`)
      instead of end-to-end
- [x] Wire the three-config setup in the IT (`FileEObjectProvider~…`,
      `EObjectRegistry~…`, `AtlasEObjectProvider~…`), incl. empty-locations file
      provider as initial provider
- [x] Migrate `org.eclipse.fennec.sensinact.mapping.atlas.local.config/configs/config.json`
- [x] Update `…mapping.atlas.runtime/launch.bndrun`; re-resolve all affected bndruns
      (`resolve.test` gotcha for `testOSGi` applies)
- [x] `./gradlew clean build testOSGi` green
- [x] Live test against the jena atlas (`http://localhost:8086/atlas/rest`) — mappings
      appear, refresh works, atlas outage keeps content

**Acceptance:** full build incl. `testOSGi` green; live test against the jena atlas
reproduces the 2026-07-30 result on the new pipeline.

---

## Issue 4: Documentation

**Labels:** documentation
**Estimate:** ~0.5 d
**Status:** done (2026-08-05)

- [x] `docs/sensinact-mapping-user-guide.md`: atlas section rewritten to the
      three-config wiring; link the emf.osgi eobject-registry guide
- [x] CLAUDE.md: Model Atlas integration section updated (bundle list, config shape,
      retired bundle)
- [x] `docs/model-atlas-integration-plan.md`: close the generalization open question
- [x] `docs/model-atlas-source-generalization-plan.md`: mark Revision 5 done / final
      outcome note
- [x] Check `docs-site/guides.mjs` allowlist still matches

---

## Issue 5 (optional): Metadata-bridge anchor resolver for provider mappings

**Labels:** enhancement
**Estimate:** ~0.5–1 d — pick up when a consumer wants the uniform metadata face

Provide a sensinact `AspectAnchorResolver` (anchors a `ProviderMapping` at its
`getProviderClasses()`) plus a `RegistryMetadataBridge~sensinact-mappings` config, so
mappings are also reachable via
`MetadataService.getClassAspect(eClass, "sensinact.mapping")`. Not functionally needed
today — the facade keeps its own EClass index; note the bridge's one-aspect-per-anchor
limit vs. our `List<ProviderMapping>` per EClass (the registry facade stays the query
face).

---

## Issue 6 (upstream): ~~ship `eobject.registry` in the `fennecEMF` workspace library~~ — RESOLVED 2026-08-05, no issue needed

**Labels:** bug/enhancement

~~`org.eclipse.fennec.emf.osgi.bnd.library.workspace/required.bndrun` does not require
`org.eclipse.fennec.emf.osgi.eobject.registry` (nor `….metadata`), so the generated
`fennecEMF.maven` index omits them and downstream workspaces enabling
`-library: fennecEMF` cannot resolve the new bundles.~~ Fixed directly in emf.osgi
(2026-08-05): the library now ships `eobject.registry` + `eobject.registry.metadata`;
emf.util's interim `central.mvn` pin was removed again. Kept here only as the record of
the finding — file nothing.

---

## Issue 7 (upstream, file in eclipse-fennec/emf.osgi): declare `osgi.service` capabilities for the manually registered registry faces

**Labels:** bug
**Estimate:** ~0.5 h (emf.osgi side)

`EObjectRegistryComponent` registers the `EObjectRegistry` + `EObjectRegistryWriter`
services manually via `BundleContext` (gated publication), so DS does not generate
`Provide-Capability: osgi.service` for them — the bundle only declares the capability
for the DS-published `EObjectProvider`. Any bundle with a DS reference to the registry
or the writer (e.g. an atlas writer client) therefore fails a bndrun resolve with
`-resolve.effective: active`. Fix: two `@Capability(namespace = "osgi.service", ...)`
annotations on `EObjectRegistryComponent` (the exact pattern emf.util's retired
`AtlasMappingSourceComponent` and the model.atlas `rest.client.osgi` already use).
Found 2026-08-05 resolving emf.util's `launch.bndrun`; interim workaround there:
`-runprovidedcapabilities` supplies the two capabilities (marked with a
remove-when-fixed comment). **Status: fix underway in emf.osgi (2026-08-05)** — once it
lands, drop the workaround from `launch.bndrun` and re-resolve.
