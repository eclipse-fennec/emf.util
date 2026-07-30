# SensiNact mapping

> **Status: in development.** The core mapping engine and its OSGi integration are
> implemented and tested; the metamodel and options described here may still evolve.

Turn instances of a **domain-specific EMF model** into **Eclipse SensiNact digital-twin
providers** — declaratively, with an XMI file, instead of hand-written transformation code.
A mapping says *"for objects of this EClass, build a provider with these services and
resources, taking each value from this feature path."* The SensiNact southbound registers
the mapping as a service; every incoming instance is then projected onto the twin
automatically.

- **Input:** any EMF model instance (an `EObject`) plus a mapping XMI.
- **Output:** a SensiNact provider / service / resource structure, updated in the digital twin.
- **No code:** mappings are configuration — hot-reloadable, validated against the EMF
  metamodel, and reusable across device types.

The mapping metamodel lives in the bundle `org.eclipse.fennec.sensinact.mapping`
(EMF package `org.eclipse.fennec.sensinact.model.mapping`, nsURI
`https://fennec.eclipse.org/sensinact/core/mapping/1.0`).

## Quick start

A minimal mapping: for every `EM310UDLUplink` build a provider named from a device field,
with one `battery/level` resource read from a nested feature path.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<mapping:ProviderMapping
    xmi:version="2.0"
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
    xmlns:mapping="https://fennec.eclipse.org/sensinact/core/mapping/1.0"
    mid="em310udl-battery-sensor">

  <!-- Provider id (falls back to the static name if the feature is empty) -->
  <name name="EM310UDL Battery Sensor">
    <featurePath xsi:type="ecore:EAttribute"
        href="https://eclipse.org/fennec/lorawan#//UplinkMessage/deduplicationId"/>
  </name>

  <!-- Which source EClass(es) this mapping applies to -->
  <providerClasses href="http://www.example.org/lorawan/specific/em310udl#//EM310UDLUplink"/>

  <services mid="battery">
    <name name="Battery"/>
    <resources name="Battery Level" unit="V" mid="level">
      <eType xsi:type="ecore:EDataType"
          href="http://www.eclipse.org/emf/2002/Ecore#//EDouble"/>
      <valueFeature xsi:type="ecore:EReference"
          href="http://www.example.org/lorawan/specific/em310udl#//EM310UDLUplink/object"/>
      <valueFeature xsi:type="ecore:EAttribute"
          href="http://www.example.org/lorawan/specific/em310udl#//DecodedObject/battery"/>
    </resources>
  </services>
</mapping:ProviderMapping>
```

Load, register, and let the southbound apply it (the payload EPackages must be registered
in the same runtime):

```java
// 1. Load the mapping XMI (any EMF ResourceSet that has the mapping package registered)
Resource r = resourceSet.createResource(URI.createURI("em310udl-battery-mapping.xmi"));
r.load(null);
ProviderMapping mapping = (ProviderMapping) r.getContents().get(0);

// 2. Register it with the mapping registry (an OSGi service)
mappingRegistry.registerModelMapping(mapping);   // creates the provider model in the twin

// 3. Values are projected onto the twin whenever you map an instance
gatewayThread.execute(new AbstractSensinactCommand<Void>() {
    protected Promise<Void> call(SensinactDigitalTwin twin, SensinactModelManager mm, PromiseFactory pf) {
        ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, mapping);
        mapper.mapInstance(uplink);   // uplink is an EM310UDLUplink EObject
        return pf.resolved(null);
    }
});
```

In an OSGi deployment you normally don't call `ValueMapper` yourself — you register each
`ProviderMapping` (and, optionally, `MappingProfile`) as a service and the southbound
whiteboard does the rest. See [Runtime & OSGi](#runtime--osgi).

## The XMI skeleton

Every mapping file is an instance of one of two root types:

- **`ProviderMapping`** — a mapping (the common case).
- **`MappingProfile`** — a reusable target-structure template (see [Profiles](#mapping-profiles)).

The root element must declare the mapping namespace. The `ecore` namespace is needed
because feature paths point at Ecore elements by URI:

```xml
<mapping:ProviderMapping
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore"
    xmlns:mapping="https://fennec.eclipse.org/sensinact/core/mapping/1.0"
    mid="…">
```

> The namespace **prefix** is arbitrary — the test data uses both `mapping:` and
> `sensinactMapping:`. What matters is that the namespace URI is exactly
> `https://fennec.eclipse.org/sensinact/core/mapping/1.0`.

### Feature paths — the core mechanism

A **feature path** is an ordered list of EMF structural features that navigates from the
source object to a value. `UplinkMessage → object → battery` becomes two `valueFeature`
entries. Each entry is an `href` into the source `.ecore`:

```xml
<valueFeature xsi:type="ecore:EReference"  href="model.ecore#//EM310UDLUplink/object"/>
<valueFeature xsi:type="ecore:EAttribute" href="model.ecore#//DecodedObject/battery"/>
```

**When is `xsi:type` required?** Only when the containing feature is declared as the
*abstract* `EStructuralFeature` and EMF cannot tell whether you mean an attribute or a
reference. That is the case for `featurePath`, `valueFeature`, `unitFeature`, and the admin
`*Ref` features — always tag those `xsi:type="ecore:EAttribute"` or `"ecore:EReference"`.
References whose declared type is already concrete (`providerClasses`, `targetEClass`,
`filter`, `expectedType`, `providerPackage`, `eType`) don't need it.

All feature paths honour [collection navigation](#collections).

## Provider mapping

`ProviderMapping` is the root of a device mapping. Its attributes and children:

| Element | Meaning |
|---|---|
| `mid` (attr, required) | Mapping id. Also the default provider model name / provider id (see strategy). |
| `providerClasses` (ref, 0..\*) | Source `EClass`es this mapping applies to. The registry keys mappings by these. |
| `providerTimestamp` (attr, default `false`) | If `true`, one timestamp is used for the whole provider (all services/resources update together) instead of per-resource. Use for snapshot-style payloads. |
| `name` (child, required) | The provider name / id — see [Name mapping](#name-mapping). |
| `timestamp` (child) | Provider-level timestamp — see [Timestamps](#timestamps). |
| `services` (child, 0..\*) | The [service mappings](#service-mapping). |
| `admin` (child) | The [admin service](#admin-service). |
| `profile` (ref) | Optional [mapping profile](#mapping-profiles) this mapping conforms to. |

### Name mapping

The `name` (a `NameMapping`) provides the provider name/id. You can give a static `name`,
a feature path, or both — the feature value wins, the static string is the fallback:

```xml
<!-- static only -->
<name name="Weather Station"/>

<!-- from a feature path -->
<name>
  <featurePath xsi:type="ecore:EReference" href="w.ecore#//WeatherReports/reports"/>
  <featurePath xsi:type="ecore:EReference" href="w.ecore#//WeatherReport/weatherStation"/>
  <featurePath xsi:type="ecore:EAttribute" href="w.ecore#//WeatherStation/id"/>
</name>

<!-- feature path with static fallback -->
<name name="Unknown device">
  <featurePath xsi:type="ecore:EAttribute" href="m.ecore#//UplinkMessage/deduplicationId"/>
</name>
```

`name` is also used for service and resource display names — there, only the static `name`
attribute is typically set (`<name name="Battery"/>`).

### Timestamps

A `TimestampMapping` decides the observation time. It carries a `strategy` and an optional
formatting `hint`:

| `strategy` | Behaviour |
|---|---|
| `FEATURE` | Read the time from the timestamp's `featurePath`. |
| `NOW` | Use the current time. |
| `FUNCTION` | Compute it from a registered [function](#custom-functions-advanced). |

```xml
<!-- from the payload -->
<timestamp strategy="FEATURE">
  <featurePath xsi:type="ecore:EAttribute" href="m.ecore#//UplinkMessage/time"/>
</timestamp>

<!-- gateway clock -->
<timestamp strategy="NOW"/>

<!-- parse a non-standard textual timestamp -->
<timestamp strategy="FEATURE" hint="yyyy-MM-dd'T'HH:mm:ssX">
  <featurePath xsi:type="ecore:EAttribute" href="m.ecore#//Reading/observedAt"/>
</timestamp>
```

In practice the engine resolves a timestamp by trying the feature path first, then a static
value, and finally falling back to the current time — so a bare `<timestamp>` with a
feature path behaves like `FEATURE`. `Instant`, `java.util.Date`, epoch-millis `Long`, and
(with a `hint`) formatted strings are all understood.

Timestamps can be set at three levels; the most specific one wins per resource:
provider (`ProviderMapping/timestamp`) → service (`ServiceMapping/timestamp`) →
resource (`ResourceMapping/timestamp`). A resource can also *reference* a timestamp defined
elsewhere in the same file instead of repeating it:

```xml
<timestamp>                                  <!-- provider-level, id //@timestamp -->
  <featurePath xsi:type="ecore:EAttribute" href="m.ecore#//UplinkMessage/time"/>
</timestamp>
…
<resources mid="level" timestamp="//@timestamp"> … </resources>
```

## Service mapping

A `ServiceMapping` (the `services` child) groups resources. It inherits `mid`, `name`, and
`timestamp` from the common mapping type and adds resources:

| Element | Meaning |
|---|---|
| `mid` | Service id (its name in the twin, e.g. `battery`). |
| `name` | Display name. |
| `timestamp` | Service-level timestamp (optional). |
| `resources` (0..\*) | Explicit [resource mappings](#resource-mapping). |
| `referencedResource` | A [ReferenceMapping](#automatic-resource-generation) that auto-generates resources. |

You can mix explicit `resources` and an auto-generating `referencedResource` in the same
service.

### Resource mapping

A `ResourceMapping` (the `resources` child) is one data point. It maps a source value onto
a SensiNact resource:

| Element | Meaning |
|---|---|
| `mid` (required) | Resource id (its name in the twin, e.g. `level`). |
| `name` | Display name → written as the resource's `friendlyName` metadata. |
| `unit` | Unit string → `unit` metadata (falls back to an Ecore annotation, below). |
| `eType` | Target `EDataType` (e.g. `EDouble`). The value is converted to it. |
| `valueFeature` (0..\*) | Feature path to the source value. |
| `unitFeature` (0..\*) | Feature path to a dynamic unit value. |
| `timestamp` | Resource-level timestamp, or a reference to one (`//@timestamp`). |
| `extraMetadata` (0..\*) | Arbitrary `key`/`value` metadata entries. |
| `defaultValueLiteral` (attr) | Initial value before the first reading (parsed against `eType`). |

```xml
<resources name="Battery Level" unit="V" mid="level">
  <eType xsi:type="ecore:EDataType" href="http://www.eclipse.org/emf/2002/Ecore#//EDouble"/>
  <valueFeature xsi:type="ecore:EReference"  href="m.ecore#//EM310UDLUplink/object"/>
  <valueFeature xsi:type="ecore:EAttribute" href="m.ecore#//DecodedObject/battery"/>
  <extraMetadata key="displayFormat" value="%.2f"/>
</resources>
```

### Metadata: where values come from

For each resource the engine assembles default metadata from, in order of precedence:

- **unit** — the `unit` attribute if set, otherwise the source attribute's
  `sensinact.mapping` annotation, key `sensinact.mapping.unit`.
- **friendlyName** — the resource's `name`.
- **description** — the `descriptionMapping` name if set; otherwise the source attribute's
  `sensinact.mapping` annotation (`sensinact.mapping.description`); otherwise the GenModel
  `documentation` annotation.
- **extra metadata** — the `extraMetadata` entries if any; otherwise every detail of the
  source attribute's `sensinact.mapping.metadata` annotation.

This means you can keep most metadata *on the Ecore model* and let it flow through
automatically — especially useful with [auto-generation](#automatic-resource-generation).
Annotate the model attribute:

```xml
<!-- in your domain .ecore -->
<eStructuralFeatures xsi:type="ecore:EAttribute" name="windSpeed" eType="…EFloatObject">
  <eAnnotations source="http://www.eclipse.org/emf/2002/GenModel">
    <details key="documentation" value="Wind speed: m/s (FF)"/>
  </eAnnotations>
  <eAnnotations source="sensinact.mapping">
    <details key="sensinact.mapping.unit" value="m/s"/>
  </eAnnotations>
  <eAnnotations source="sensinact.mapping.metadata">
    <details key="sensorthings.unit.name" value="meters per seconds"/>
  </eAnnotations>
</eStructuralFeatures>
```

The annotation sources and keys are defined in `SensinactMapperConstants`:
`sensinact.mapping` (with `sensinact.mapping.unit` / `sensinact.mapping.description`) and
`sensinact.mapping.metadata`.

## Collections

When a feature path crosses a multi-valued reference, pick the element with
`collectionIndex` (0-based, default `0`). It is available on every feature-path element
(`name`, `timestamp`, `referencedResource`, …):

```xml
<!-- name from reports[0].weatherStation.id -->
<name collectionIndex="0">
  <featurePath xsi:type="ecore:EReference" href="w.ecore#//WeatherReports/reports"/>
  <featurePath xsi:type="ecore:EReference" href="w.ecore#//WeatherReport/weatherStation"/>
  <featurePath xsi:type="ecore:EAttribute" href="w.ecore#//WeatherStation/id"/>
</name>
```

> **`collectionFilter` is reserved but not yet implemented.** The attribute exists on the
> metamodel for a future "select the element where …" expression; today the engine logs a
> warning and falls back to `collectionIndex`.

### Mapping services onto collection elements

A service's `referencedResource` (a `ReferenceMapping`) does double duty: besides
[automatic resource generation](#automatic-resource-generation) it selects the **source
element for the whole service**. Its `featurePath` + `collectionIndex` decide which element
the service's timestamp, explicit `resources`, and (for the admin service) the `*Ref`
features read from. The classic pattern is two services on different indices of the same
collection — current weather from `reports[0]`, the 3-hour forecast from `reports[1]`:

```xml
<services mid="currentWeather">
  <name name="Current Weather"/>
  <timestamp strategy="FEATURE">  <!-- resolved against reports[0] -->
    <featurePath xsi:type="ecore:EAttribute" href="w.ecore#//WeatherReport/timestamp"/>
  </timestamp>

  <!-- selector only: exclude="false" with no filter generates no resources -->
  <referencedResource collectionIndex="0" exclude="false">
    <featurePath xsi:type="ecore:EReference" href="w.ecore#//WeatherReports/reports"/>
  </referencedResource>

  <!-- explicit resources: valueFeature paths are relative to reports[0] -->
  <resources name="windSpeed" unit="m/s" mid="windSpeed">
    <eType xsi:type="ecore:EDataType" href="http://www.eclipse.org/emf/2002/Ecore#//EFloat"/>
    <valueFeature xsi:type="ecore:EAttribute" href="w.ecore#//MOSMIXSWeatherReport/windSpeed"/>
  </resources>
</services>

<services mid="forecast3H">
  <!-- same shape, collectionIndex="1" -->
</services>
```

Without a `referencedResource` the service reads from the root source object. With one, all
feature paths inside the service are relative to the selected element — so resource paths
start at the element type (`MOSMIXSWeatherReport/windSpeed`), not at the container. If the
selected index does not exist in an incoming instance, the whole instance fails validation,
so a two-index mapping requires at least two elements in the collection. The complete
worked example is `model/WeatherReportsProviderMapping.xmi` (its admin service selects
`reports[0]` the same way).

## Automatic resource generation

Typing out one `ResourceMapping` per attribute is tedious for rich types. A
**`ReferenceMapping`** (the `referencedResource` of a service) generates a resource for
every attribute of a target EClass automatically.

```xml
<services mid="currentWeather">
  <name name="Current Weather"/>
  <timestamp strategy="FEATURE">
    <featurePath xsi:type="ecore:EAttribute" href="w.ecore#//WeatherReport/timestamp"/>
  </timestamp>

  <referencedResource xsi:type="mapping:ReferenceMapping"
      collectionIndex="0" exclude="true">
    <!-- path to the collection whose element supplies the attributes -->
    <featurePath xsi:type="ecore:EReference" href="w.ecore#//WeatherReports/reports"/>

    <!-- use this concrete subtype's attributes, not the declared base type -->
    <targetEClass href="w.ecore#//MOSMIXSWeatherReport"/>

    <!-- with exclude="true": skip these; map everything else -->
    <filter xsi:type="ecore:EAttribute" href="w.ecore#//WeatherReport/id"/>
    <filter xsi:type="ecore:EAttribute" href="w.ecore#//WeatherReport/timestamp"/>

    <!-- also generate resources for a nested reference, prefixed by its name -->
    <referenceMappings xsi:type="mapping:ReferenceMapping">
      <featurePath xsi:type="ecore:EReference"
          href="w.ecore#//MOSMIXSWeatherReport/significantWeather6Hours"/>
    </referenceMappings>
  </referencedResource>
</services>
```

**`filter` + `exclude`:**

| `exclude` | `filter` | Result |
|---|---|---|
| `true` (default) | empty | map **all** attributes |
| `true` | listed | map all **except** the listed |
| `false` | listed | map **only** the listed |
| `false` | empty | map **nothing** |

Notes:

- **`targetEClass`** matters when the feature is declared with a base type but instances are
  a subtype — without it only the base attributes are generated.
- **`referenceMappings`** (nested) generates resources from a referenced object's
  attributes, named `<referenceName><AttributeName>` (e.g.
  `significantWeather6HoursWeatherCode`).
- Generated resources inherit metadata from the source attribute's annotations (see
  [Metadata](#metadata-where-values-come-from)), so unit/description/extra come for free.
- Auto-generated resources are kept in the service's transient `temporaryResources`; they
  are regenerated on each registration and processed identically to explicit `resources`.
- The same `referencedResource` also selects the service's source element (see
  [Mapping services onto collection elements](#mapping-services-onto-collection-elements));
  with `exclude="false"` and no filter it acts as a pure selector and generates nothing.

## Admin service

The `admin` child (an `AdminMapping`, a specialized service) carries provider metadata:
friendly name, location, and the provider's EPackage. Because the `admin` feature is already
typed as `AdminMapping`, `xsi:type` is optional.

```xml
<admin mid="admin">
  <name name="Admin"/>

  <!-- friendly name from a feature path -->
  <friendlyNameFeature xsi:type="ecore:EReference" href="m.ecore#//UplinkMessage/deviceInfo"/>
  <friendlyNameFeature xsi:type="ecore:EAttribute" href="m.ecore#//DeviceInfo/deviceProfileName"/>

  <!-- the source EPackage that owns the provider type -->
  <providerPackage href="m.ecore#/"/>

  <!-- location: latitude/longitude/elevation, each a feature path -->
  <latitudeRef  xsi:type="ecore:EReference" href="w.ecore#//WeatherReport/station"/>
  <latitudeRef  xsi:type="ecore:EReference" href="w.ecore#//Station/location"/>
  <latitudeRef  xsi:type="ecore:EAttribute" href="w.ecore#//GeoPosition/latitude"/>
  <longitudeRef xsi:type="ecore:EReference" href="w.ecore#//WeatherReport/station"/>
  <longitudeRef xsi:type="ecore:EReference" href="w.ecore#//Station/location"/>
  <longitudeRef xsi:type="ecore:EAttribute" href="w.ecore#//GeoPosition/longitude"/>
</admin>
```

Location can also be given as static `latitude` / `longitude` / `elevation` attributes
instead of `*Ref` feature paths.

> **Known limitation.** Admin *resource reads* are currently not fully wired through the
> SensiNact admin service — a value is mapped but reading it back does not always return the
> mapped value (there is a disabled test `ValueMapperTest#providerMapping_admin` tracking
> this). Provider name/location handling works; treat custom admin resources as
> experimental for now.

## Mapping profiles

A **`MappingProfile`** declares a *target structure* that one or more mappings conform to —
so different device models (Dragino, EM310, …) can all present the same
`battery-sensor` provider shape. A profile is a separate root object, usually its own file:

```xml
<mapping:MappingProfile
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:mapping="https://fennec.eclipse.org/sensinact/core/mapping/1.0"
    profileId="battery-sensor" name="Battery Sensor Profile"
    providerStrategy="UNIFIED">
  <provider providerId="battery-sensor">
    <services serviceId="battery" serviceName="Battery">
      <resources resourceId="level" resourceName="Battery Level" expectedUnit="V">
        <expectedType href="http://www.eclipse.org/emf/2002/Ecore#//EDouble"/>
      </resources>
    </services>
    <admin serviceId="admin" serviceName="Admin" requiresFriendlyName="true">
      <resources resourceId="friendlyName" resourceName="Device Name" required="false">
        <expectedType href="http://www.eclipse.org/emf/2002/Ecore#//EString"/>
      </resources>
    </admin>
  </provider>
</mapping:MappingProfile>
```

A mapping references the profile by its id (cross-file `href` to the `profileId`):

```xml
<mapping:ProviderMapping mid="em310udl-battery-sensor"> … 
  <profile href="battery-sensor-profile.xmi#battery-sensor"/>
</mapping:ProviderMapping>
```

**`providerStrategy`** governs what happens when several mappings share a profile:

- **`SEPARATE`** (default) — each mapping creates its own provider instance.
- **`UNIFIED`** — all mappings using the profile contribute to a single shared provider.

Profile services and resources carry a `required` flag (default `true`) plus
`expectedType`/`expectedUnit`, which the `MappingProfileRegistry` uses to validate that a
conforming mapping actually provides what the profile demands.

## Custom functions (advanced)

Any feature-path element (name, timestamp, value, …) may set a `functionId` instead of, or
in addition to, a feature path. At runtime the engine looks the id up in a function registry
and applies the registered `Function<EObject, ?>`; if none is found it falls back to feature
path traversal. Use this for values that need computation (unit conversion, composing
fields) that a plain path can't express. Registering functions is done in code against the
southbound's function registry.

## Persistence rules (history control)

> **Status: model support in place; enforcement is a separate upcoming component.** The
> mapping model carries the rules; a *notification proxy* (planned) reads them to decide what
> the SensiNact history provider actually stores.

By default the history provider persists *every* value change of *every* resource. Persistence
rules let you control this **per resource**: how often a change is worth storing, and how long
to keep it. Rules are **reusable** — defined once in a registry and referenced by many
resources.

### The registry (rules are defined once)

Rules live in a `PersistenceRuleRegistry`, usually its own file. They are *contained* here:

```xml
<mapping:PersistenceRuleRegistry
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:mapping="https://fennec.eclipse.org/sensinact/core/mapping/1.0">
  <changeRules xsi:type="mapping:PercentageChangeRule"   id="pct-5"        percentage="5.0"/>
  <changeRules xsi:type="mapping:AbsoluteChangeRule"     id="abs-2"        delta="2.0"/>
  <changeRules xsi:type="mapping:CountChangeRule"        id="every-5"      n="5"/>
  <changeRules xsi:type="mapping:TimeThrottleChangeRule" id="throttle-10m" interval="10" intervalUnit="MINUTES"/>
  <deletionRules id="keep-90d" retention="90"  retentionUnit="DAYS" cleanupInterval="1" cleanupIntervalUnit="DAYS"/>
  <deletionRules id="keep-1y"  retention="365" retentionUnit="DAYS" cleanupInterval="7" cleanupIntervalUnit="DAYS"/>
</mapping:PersistenceRuleRegistry>
```

### Binding rules to resources (shared, not copied)

A `ResourceMapping` references a rule by id via non-containment — so temperature, humidity and
water can all point at the *same* rule instance:

```xml
<resources mid="temperature" unit="F">
  <eType xsi:type="ecore:EDataType" href="http://www.eclipse.org/emf/2002/Ecore#//EDouble"/>
  <valueFeature xsi:type="ecore:EAttribute" href="../ecowitt.ecore#//EcoWittWeather/temperaturOutdoor"/>
  <changeRule   href="persistence-rules.xmi#pct-5"/>
  <deletionRule href="persistence-rules.xmi#keep-1y"/>
</resources>
```

(Working pair: `model/examples/persistence-rules.xmi` + `model/examples/EcoWittPersistenceMapping.xmi`.)

### Change rules — "store this change only if…"

Each is a concrete subtype carrying exactly its parameter. Comparisons are against the **last
stored** value, and the first value of a resource is always stored.

| Type | Parameter | Stores when |
|---|---|---|
| `PercentageChangeRule` | `percentage` | the value moved ≥ *percentage* % vs. the last stored value |
| `AbsoluteChangeRule` | `delta` | the absolute change ≥ *delta* |
| `CountChangeRule` | `n` | one out of every *n* notifications (`n=1` = every change) |
| `TimeThrottleChangeRule` | `interval` + `intervalUnit` | at most once per interval (drops the in-between changes) |

### Deletion rules — retention & cleanup

`DeletionRule` controls purging of stored data: `retention` (+`retentionUnit`) is the age past
which data is deleted; `cleanupInterval` (+`cleanupIntervalUnit`) is how often cleanup runs;
optional `maxCount` caps the number of samples kept.

### Durations

Durations are an **integer amount + a `DurationUnit`** — one of `MILLISECONDS`, `SECONDS`,
`MINUTES`, `HOURS`, `DAYS` (e.g. `interval="10" intervalUnit="MINUTES"`). There is no custom
duration datatype, so these serialize as plain XMI attributes.

## Runtime & OSGi

The engine is plain Java (`ValueMapper` / `ValueMapperFactory`), but the normal deployment
is service-driven:

- Register each `ProviderMapping` as an OSGi service. `ProviderMappingRegistryImpl` is a
  whiteboard `@Component` (config pid `sensinact.southbound.emf.mapping`) that collects them,
  keyed by `providerClasses`, and builds the provider model in the twin on the SensiNact
  `GatewayThread`.
- Register `MappingProfile` services; `MappingProfileRegistryImpl` collects them by
  `profileId` and validates conformance.
- Feed data in through the **`InstancePusher`** service: `pushInstance(EObject)` looks up
  all registered mappings for the instance's EClass and applies each on the gateway thread,
  returning the number of mappings applied (`0` = nothing registered for that EClass;
  individual mapping failures are logged and skipped). This is the intended ingress for
  southbound connectors — receive a payload, deserialize it to an EObject, push.
- The payload EPackages (your domain models) must be registered in the runtime so both the
  mapping XMI and the incoming instances resolve. **The registry lookup keys on EClass
  identity**: incoming instances must be built against the same EPackage instance the
  mappings resolved their `providerClasses` from. For atlas-sourced sensor models that means
  the atlas-fetched package — when in doubt, take it from the mapping itself
  (`mapping.getProviderClasses().get(0).getEPackage()`).

Programmatic use (tests, embedding) goes through the factory, always on the gateway thread:

```java
ValueMapper mapper = ValueMapperFactory.createValueMapper(twin, mapping);

ValueMapper.ValidationResult v = mapper.validateInstance(sourceInstance);
if (!v.isValid()) throw new ValueMappingException(v.getErrors().toString());

mapper.mapInstance(sourceInstance);                 // update the twin
Map<String,Object> values = mapper.mapResourceValues(sourceInstance);  // inspect without updating
```

## Loading mappings from a Model Atlas

Instead of shipping mapping XMIs inside a bundle, they (and the sensor-model EPackages they
reference) can be pulled from a remote [Model Atlas](https://github.com/eclipse-fennec/model.atlas).
Two pieces cooperate:

- the **atlas rest client** (`org.eclipse.fennec.model.atlas.rest.client.osgi`): one factory
  config per atlas instance; it publishes a `ReadableScopeService<EObject>` per scope (service
  property `atlas.scope`) and remote EPackages on demand (fetch-on-miss, so `href`s from a
  mapping XMI into a sensor model resolve without the sensor-model bundle being deployed);
- the **mapping source** (`org.eclipse.fennec.sensinact.mapping.atlas`, this repo): one factory
  config per scope; it reads `ProviderMapping`/`MappingProfile` objects from the configured
  atlas registries and registers each as an OSGi service — from there the whiteboards above
  take over as if the mapping had been registered locally.

```json
{
  "org.eclipse.fennec.model.atlas.rest.client~iot": {
    "base.uri": "http://atlas-host:8080/atlas/rest",
    "scope.allow.list": ["iot"]
  },
  "org.eclipse.fennec.sensinact.mapping.atlas~iot": {
    "atlasScope.target": "(atlas.scope=iot)"
  }
}
```

Mapping-source configuration (factory PID `org.eclipse.fennec.sensinact.mapping.atlas`):

| Property | Default | Meaning |
|---|---|---|
| `atlasScope.target` | — | DS target filter selecting the scope service, e.g. `(atlas.scope=iot)` (add `(atlas.stage=…)` to pin a stage-published client) |
| `registries` | `["mappings"]` | atlas registry name(s) to read objects from |
| `object.ids` | `[]` | explicit allowlist; empty loads everything the registries list |
| `stage` | `""` | explicit atlas stage to read; empty reads the final stage |
| `refresh.interval.ms` | `0` | periodic re-fetch; changed objects are re-registered (whiteboards see remove+add), removed ids unregistered. `0` = load once on activation |
| `retry.interval.ms` | `30000` | back-off while the initial load is incomplete (atlas unreachable, object missing) |

Registered services carry `sensinact.mapping.mid` (or `sensinact.mapping.profile.id`),
`atlas.remote=true`, `atlas.scope`, `atlas.registry`, `atlas.object.id` and, when configured,
`atlas.stage` — so atlas-sourced mappings can be told apart from locally deployed ones.

Notes:

- **The mapping metamodel stays local.** The atlas client resolves nsURIs local-first against
  the global `EPackage.Registry`; the source pins the generated mapping package there before
  the first fetch, so fetched objects are real `ProviderMapping` instances (not dynamic
  EObjects). Sensor models, by contrast, may live only in the atlas.
- All atlas I/O happens on a private thread — activation never blocks, per-object failures are
  logged and skipped, and a transient outage during refresh keeps the current registrations.
- If the atlas client config goes away, the source deactivates and all its mapping services
  vanish; when it returns, everything reloads.
- **Sensor-model drift:** the atlas client's periodic drift check (`drift.check.interval.ms`
  on the rest-client config) also watches EPackages that were resolved fetch-on-miss, so a
  sensor model changed or removed on the atlas is detected in a running gateway. Requires
  model.atlas client *and* server from 2026-07-30 or later (model.atlas#160/#161 — older
  clients never drift-checked fetch-on-miss packages, and an older server corrupts the
  served `EPackage` on the first schema fetch).
- **Uploading mapping XMIs to the atlas:** `POST` to
  `/{scope}/registries/{registry}/stages/{stage}/{objectId}` with content type
  `application/xmi`. (Atlas servers built before 2026-07-30 crashed on a relative
  `xsi:schemaLocation` — `ArrayIndexOutOfBoundsException` in the codec.rest
  `XMLURIHandler`, fixed in fennec-codec — against an older server, strip the
  schemaLocation first and don't upload as `application/xml`.)
- **Deserializing incoming payloads against atlas-resolved models:** create a **fresh
  `ResourceSet` per payload** via the runtime's `ResourceSetFactory` service. The atlas
  client contributes a `ResourceSetConfigurator`, so only resource sets created while the
  client is active resolve unknown nsURIs remotely (local-first, then fetch-on-miss); a
  resource set obtained before the client existed never becomes atlas-aware. A payload
  whose model is in neither place fails with `PackageNotFoundException` — log and drop.
  EClass identity between such payloads and atlas-loaded mappings is guaranteed by the
  client's ETag/304 revalidation (a `304` returns the cached `EPackage` instance).
- End-to-end example (mock atlas over HTTP, sensor EPackage *not* deployed):
  `org.eclipse.fennec.sensinact.mapping.atlas.tests` — including the full raw-data path:
  XMI payload → fresh `ResourceSet` (model resolved through the atlas) → `InstancePusher`
  → twin values, plus the unknown-model case.
- Live smoke test: `org.eclipse.fennec.sensinact.mapping.atlas.test.component`
  (`WeatherReportsSimulator`, config PID `sensinact.mapping.atlas.test.simulator`)
  periodically renders a `WeatherReports` **XMI payload** (as an external device would
  send it), loads it as above and pushes the roots through the `InstancePusher` — the log
  narrates the whole scenario: "model unknown, dropping" while the atlas is empty, "no
  mapping registered" once only the model is uploaded, pushed values once mapping + model
  are released (within the source's refresh interval). Launch via
  `org.eclipse.fennec.sensinact.mapping.atlas.runtime/launch.bndrun` and watch the twin
  with the sensinact gogo commands (`sna:` scope; the runtime includes northbound
  `gogo-shell` + `session-impl` with `sensinact.session.manager` → `auth.policy=ALLOW_ALL`).

## Gotchas

- **Namespace URI must match exactly** — `https://fennec.eclipse.org/sensinact/core/mapping/1.0`.
  A stale URI in an XMI fails to load with `PackageNotFoundException`.
- **Tag `xsi:type` on `EStructuralFeature` refs** (`featurePath`, `valueFeature`,
  `unitFeature`, admin `*Ref`) — otherwise EMF cannot resolve attribute vs. reference.
- **`collectionFilter` is not implemented yet** — use `collectionIndex`.
- **Admin resource reads are incomplete** — see the note under [Admin service](#admin-service).
- **Metadata precedence** — an explicit `unit`/`extraMetadata` on the resource overrides the
  Ecore annotation; leave them off to let model annotations flow through.

## Reference: metamodel at a glance

| Type | Key features |
|---|---|
| `ProviderMapping` | `mid`, `providerClasses`, `providerTimestamp`, `name`, `timestamp`, `services`, `admin`, `profile` |
| `ServiceMapping` | `mid`, `name`, `timestamp`, `resources`, `referencedResource` |
| `ResourceMapping` | `mid`, `name`, `unit`, `eType`, `valueFeature`, `unitFeature`, `timestamp`, `extraMetadata`, `defaultValueLiteral`, `changeRule`, `deletionRule` |
| `AdminMapping` | `friendlyName`/`friendlyNameFeature`, `latitude`/`latitudeRef` (+ longitude, elevation), `providerPackage` |
| `NameMapping` | `name`, `featurePath`, `collectionIndex` |
| `TimestampMapping` | `strategy` (`NOW`/`FEATURE`/`FUNCTION`), `hint`, `timestamp`, `featurePath` |
| `ReferenceMapping` | `featurePath`, `targetEClass`, `filter`, `exclude`, `referenceMappings`, `collectionIndex` |
| `FeatureMapping` (base) | `functionId`, `featurePath`, `collectionIndex`, `collectionFilter` |
| `MappingProfile` | `profileId`, `name`, `version`, `providerStrategy`, `provider` |
| `ProfileProvider` / `ProfileService` / `ProfileResource` / `ProfileAdmin` | `*Id`, `required`, `expectedType`, `expectedUnit`, `requiresLocation`, `requiresFriendlyName` |
| `PersistenceRuleRegistry` | `changeRules` (containment), `deletionRules` (containment) |
| `PercentageChangeRule` / `AbsoluteChangeRule` / `CountChangeRule` / `TimeThrottleChangeRule` | `id`, `name`, `description` + `percentage` / `delta` / `n` / (`interval` + `intervalUnit`) |
| `DeletionRule` | `id`, `retention` + `retentionUnit`, `cleanupInterval` + `cleanupIntervalUnit`, `maxCount` |
