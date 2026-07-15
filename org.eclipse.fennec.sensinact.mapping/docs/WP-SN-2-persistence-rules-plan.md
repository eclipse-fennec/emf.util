# WP-SN-2 — Reusable persistence rules in the SensiNact mapping model (plan)

> **Status:** design agreed, not yet implemented. This is the reference plan; update it as
> the design evolves. Created 2026-07-15.

## Goal

Give operators declarative control over **what the SensiNact history provider persists**,
configured directly in the SensiNact mapping at the **resource level**, using **reusable**
rules.

Today the history (timescale) provider is notified of *every* value change on *every*
provider/service/resource triple and stores them all. We want to decide — per resource —
whether a given change is actually worth storing, and how long to keep it.

## Runtime architecture (context; separate follow-up WP)

A **Notification Proxy** sits between SensiNact's change notifications and the history
provider:

1. It receives all resource value-change notifications.
2. For each, it looks up the corresponding SensiNact mapping (`ProviderMappingRegistry` →
   the `ResourceMapping` for that provider/service/resource).
3. It reads the resource's rule and decides whether to **forward** the notification
   downstream to the history provider or **drop** it.

**WP-SN-2 (this plan) is model-only** — the Ecore just has to *carry* the rules. The proxy
is a later WP. Two things to spike when we get there:

- Exactly how the timescale provider subscribes to notifications, so the proxy can sit
  strictly upstream of it.
- Whether deletion/cleanup has a command path — `history-api` currently exposes only the
  read side (`HistoricalQueries`).

## Scope for this first cut

- **Change rules** (forward/drop decision) **and deletion rules** (retention/cleanup) — both
  from the start.
- **One change rule + one deletion rule per resource** — single references, not combinations.
- Change-rule behaviours to start (one concrete subtype each):
  - **percentage** — store only if the value changed by X %
  - **absolute**   — store only if the value changed by X
  - **count**      — store 1 value every N notifications
  - **time**       — store at most once per interval (time-based throttle)
- Deletion-rule behaviour to start: retention age + cleanup cadence.

## Containment vs non-containment (the WP's two references, resolved)

Two orthogonal concerns:

- **Ownership → containment.** Every `EObject` needs exactly one container to be serialized.
  The rules are *owned* by a registry object via **containment**; they are written once,
  there.
- **Binding → non-containment.** A `ResourceMapping` *uses* a rule via a **non-containment**
  reference (a pointer, many-to-one). This is what lets temperature, humidity and water all
  point at the *same* rule instance. Containment here would make an object attachable only
  once, defeating reuse.

This mirrors the existing `MappingProfile` pattern (separate root, referenced
non-containment from `ProviderMapping.profile`).

## Ecore additions

Added to `model/sensinact-mapping.ecore` (package
`org.eclipse.fennec.sensinact.model.mapping`, nsURI unchanged
`https://fennec.eclipse.org/sensinact/core/mapping/1.0`).

### Durations: amount + unit (no custom datatype)

Durations are modelled as an **integer amount + a `DurationUnit` enum** (e.g. `5` + `MINUTES`,
`90` + `DAYS`), not as a custom `java.time.Duration` datatype.

```
enum DurationUnit { MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS }   ← names match java.util.concurrent.TimeUnit
```

**Why not an `EDuration` datatype:** a custom datatype needs `createFromString`/`convertToString`
bodies (EMF's default can't parse `java.time` types — they have no `String` constructor or
`valueOf(String)`). Those bodies must be hand-written as `@generated NOT`, which relies on
EMF JMerge preserving them across regeneration. In this repo's headless build, bnd's
`-generate` runs EMF codegen with **JMerge failing to initialize**
(`MalformedURLException` in `JControlModel.initialize`), so it regenerates *without* merge and
silently reverts `@generated NOT` edits. An `EInteger` amount + an `EEnum` unit are native EMF
types that serialize with **zero custom code**, sidestepping the whole problem. The runtime
proxy converts with `java.util.concurrent.TimeUnit.valueOf(unit.getName()).toMillis(amount)`.

### New root container (standalone, own XMI file — max reuse)

```
PersistenceRuleRegistry
  changeRules   : ChangeRule   [0..*]   (containment)
  deletionRules : DeletionRule [0..*]   (containment)
```

### Rule types (one concrete subtype per behaviour)

```
abstract PersistenceRule
  id          : EString [1]  iD=true   ← so a resource can href it
  name        : EString
  description : EString

abstract ChangeRule extends PersistenceRule       "forward to history only if…"

  PercentageChangeRule   extends ChangeRule
    percentage : EDouble [1]     → |new − lastStored| / |lastStored| · 100 ≥ percentage

  AbsoluteChangeRule     extends ChangeRule
    delta      : EDouble [1]     → |new − lastStored| ≥ delta

  CountChangeRule        extends ChangeRule
    n          : EIntegerObject [1]                 → forward 1 of every N notifications

  TimeThrottleChangeRule extends ChangeRule
    interval     : EIntegerObject [1]               → forward only if now − lastStored ≥ interval
    intervalUnit : DurationUnit   [1]

DeletionRule extends PersistenceRule              "purge history data when…"
  retention           : EIntegerObject [1]    delete data older than retention·retentionUnit
  retentionUnit       : DurationUnit   [1]
  cleanupInterval     : EIntegerObject        how often the cleanup runs (with its unit)
  cleanupIntervalUnit : DurationUnit
  maxCount            : EIntegerObject        (optional) keep at most N samples
```

**Why concrete subtypes (not a single class + discriminator enum):** each subtype carries
exactly its own required field, so invalid/contradictory states are *unrepresentable* (there
is no `delta` on a `PercentageChangeRule`). That removes the need for bespoke enum↔attribute
validation entirely — the only remaining check ("required field present") is generic, free
EMF validation. The proxy discriminates with a trivial `instanceof`; the per-triple runtime
state it needs (last-stored value, counter, last-store time) lives in the proxy, not on the
shared rule. Adding a fifth behaviour later is one new subclass, touching nothing existing.

> A single `ChangeRule` + `ChangeRuleType` enum was considered. It would allow the proxy to
> switch on one field, but it *permits* under-specified/contradictory rules and needs a
> custom validator to compensate. Subtypes give the same discrimination more safely, so the
> enum is **not** used. (Decision recorded to avoid re-opening.)

`DeletionRule` is a single behaviour (retention-based) so it is one concrete class with no
subtypes; subclass it later if more strategies appear.

### Change to `ResourceMapping`

```
changeRule   : ChangeRule   [0..1]     (non-containment, resolveProxies=true)
deletionRule : DeletionRule [0..1]     (non-containment, resolveProxies=true)
```

(`ResourceMapping` already extends `EAttribute`; adding references is fine.)

## Validation

Structural and generic — no per-type logic:

- Each subtype's parameter is **required** (`lowerBound=1`), so the wrong field cannot be
  set and the right field's absence is a standard EMF diagnostic.
- Run `Diagnostician.validate` (or a small null-check) at mapping registration — same
  fail-fast style as `MappingProfileRegistry` — to turn a missing value into a friendly
  error at creation time rather than a surprise in the proxy.

## Intended rule semantics (for the proxy WP; record as annotations/doc)

Change rules:

- Compare against the **last forwarded/stored** value, not the last seen value, so slow
  drift accumulates correctly.
- The **first** value for a triple is always stored (no baseline yet).
- `PercentageChangeRule` guards `lastStored == 0` (treat any nonzero move as ∞ % → store).
- `CountChangeRule` keeps a counter per provider/service/resource triple; `n = 1` stores
  every change.
- `TimeThrottleChangeRule` forwards only if `now − lastStored ≥ interval` (drops the
  in-between changes; it does **not** by itself force a store when nothing changes — a
  periodic sampler would need a scheduler, out of scope here).
- **Non-numeric resources** (string/boolean): percentage/absolute don't apply → proxy falls
  back to store-on-change (count/time still work).

Deletion rules:

- `retention` — the proxy (or a scheduled cleanup job) purges history rows older than the
  age. Cadence from `cleanupInterval`; optional `maxCount` caps sample count per triple.
- Execution depends on the history provider exposing a purge/cleanup path — see
  [open questions](#deferred--open-questions).

## Example XMI

**`persistence-rules.xmi`** — rules contained in the registry (subtype via `xsi:type`):

```xml
<mapping:PersistenceRuleRegistry
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:mapping="https://fennec.eclipse.org/sensinact/core/mapping/1.0">
  <changeRules xsi:type="mapping:AbsoluteChangeRule"     id="abs-0.5"     delta="0.5"/>
  <changeRules xsi:type="mapping:PercentageChangeRule"   id="pct-10"      percentage="10.0"/>
  <changeRules xsi:type="mapping:CountChangeRule"        id="every-100"   n="100"/>
  <changeRules xsi:type="mapping:TimeThrottleChangeRule" id="throttle-5m" interval="5" intervalUnit="MINUTES"/>
  <deletionRules id="keep-90d" retention="90" retentionUnit="DAYS" cleanupInterval="1" cleanupIntervalUnit="DAYS"/>
</mapping:PersistenceRuleRegistry>
```

**In a `ProviderMapping`** — resources reference shared rules (non-containment):

```xml
<resources mid="temperature" changeRule="persistence-rules.xmi#abs-0.5"
                             deletionRule="persistence-rules.xmi#keep-90d"> … </resources>
<resources mid="humidity"    changeRule="persistence-rules.xmi#pct-10"
                             deletionRule="persistence-rules.xmi#keep-90d"> … </resources>
<resources mid="water"       changeRule="persistence-rules.xmi#throttle-5m"
                             deletionRule="persistence-rules.xmi#keep-90d"> … </resources>
```

## Implementation steps

1. Edit `model/sensinact-mapping.ecore`: add the `DurationUnit` enum,
   `PersistenceRuleRegistry`, `PersistenceRule` (abstract), `ChangeRule` (abstract), the
   four change subtypes (`PercentageChangeRule`, `AbsoluteChangeRule`, `CountChangeRule`,
   `TimeThrottleChangeRule`), and `DeletionRule`; add `changeRule` + `deletionRule` refs to
   `ResourceMapping`. **[done]**
2. Sync `model/sensinact-mapping.genmodel` and regenerate `src-gen`. **[done — user
   regenerated in the IDE after reloading the genmodel from the ecore]**
   - **Gotchas from prior renames:** do **not** change the nsURI; keep any `<%FQN%>`
     operation-body tokens fully-qualified against
     `org.eclipse.fennec.sensinact.model.mapping`.
   - No `@generated NOT` is needed: durations are `EInteger` + `DurationUnit` enum, both
     natively serialized. (This is deliberate — JMerge fails in the headless build and does
     **not** preserve `@generated NOT`; see the durations design note above.)
3. Add example XMIs under `model/examples/`: `persistence-rules.xmi` (the registry) +
   `EcoWittPersistenceMapping.xmi` (temperature/humidity/rain each binding a shared rule via
   cross-file `href`). **[done]**
4. Tests (`PersistenceRuleRoundTripTest`, green): durations (amount + `DurationUnit`)
   round-trip through XMI; a shared rule referenced by several resources resolves to the
   **same** instance still owned by the registry. **[done]**
5. Add a "Persistence rules" section to `docs/sensinact-mapping-user-guide.md` (+ reference
   table + overview table wiring). **[done]**

Remaining for a later pass:
- **Validation front** — run `Diagnostician.validate` (or a null-check on the required rule
  parameter) at mapping registration in `ProviderMappingRegistry` for a friendly error. The
  required parameters are already structural (`lowerBound=1`), but nothing runs validation yet.
- **Notification proxy** (separate runtime WP) — consume the rules to forward/drop
  notifications and to purge history per retention.

## Deferred / open questions

- **Combining rules per resource** (e.g. "≥ 0.5 **or** every 100th") — would change
  `changeRule` to a multi-valued reference with AND/OR semantics. Single ref for now.
- **Provider/service-level defaults** with resource-level override — not in scope; binding
  is resource-level only.
- **More deletion strategies** — if retention-only is not enough, add `DeletionRule`
  subtypes (mirroring the change-rule design).
- **Deletion execution path** — depends on whether the history provider exposes a
  purge/cleanup command; `history-api` currently exposes only the read side
  (`HistoricalQueries`). Needs a spike in the runtime WP.
- **Periodic sampling** (force a store every interval even with no change) —
  `TimeThrottleChangeRule` only throttles existing changes; true sampling needs a scheduler,
  out of scope.
