# EMF ⇄ Protobuf

> **Status: v1.** Implemented and unit-tested; the API may still evolve.

Serialize and deserialize EMF model **instances** to and from
[Google Protocol Buffers](https://protobuf.dev/). The Protobuf descriptors are derived
**directly from an `EPackage`** at runtime — there is **no `protoc` step and no generated
code**. The core is plain Java (no OSGi, no Jackson); an optional OSGi bundle adds a
`Resource.Factory` service so `.protobin` resources load and save through the ordinary EMF
API.

- **Input:** any Ecore model (generated *or* dynamic).
- **Output:** compact binary Protobuf, plus an optional `.proto` (proto3) schema export.
- **Import:** derive a (structural) Ecore model from a compiled `FileDescriptorSet` — see
  [Importing: descriptors → Ecore](#importing-descriptors-ecore).
- **Round-trips:** attributes, enums, containment, references, and inheritance/polymorphism.

## Quick start

Plain Java — derive a schema once per package, then read/write:

```java
ProtobufSchema schema = ProtobufSchema.forPackage(myEPackage);

byte[] bytes = schema.writer().toBytes(myEObject);      // serialize (self-describing)
EObject copy = schema.reader().fromBytes(bytes);        // deserialize — type comes from the stream

String proto = schema.toProtoSource();                  // export a .proto (proto3)
```

Through the EMF `Resource` API (see [Using it](#using-it)):

```java
Resource r = resourceSet.createResource(URI.createFileURI("model.protobin"));
r.getContents().add(myEObject);
r.save(null);   // writes Protobuf
r.load(null);   // reads it back
```

## How it works

Understanding the mapping helps you predict the output and choose the right configuration.

1. **A message per `EClass`.** Each `EClass` becomes one Protobuf message. Its fields are
   **all** structural features returned by `getEAllStructuralFeatures()` — inherited *and*
   own — so the message is *flattened*: a subtype's message contains its supertypes'
   features too. One `FileDescriptor` is built per `EPackage`.
2. **Field numbers.** Protobuf identifies fields by number, and those numbers must be
   **wire-stable**. You pin them with an [annotation](#field-numbers); otherwise a
   deterministic number is auto-assigned (fine within one process, not for long-term
   compatibility).
3. **Attributes → scalars.** Ecore data types map to Protobuf scalars
   (see [Type mapping](#type-mapping)). Enums become Protobuf enums (encoded as their
   **numeric** value). Multi-valued features become `repeated`. Single-valued scalars use
   proto3 `optional` so EMF's *unset vs. default* distinction round-trips.
4. **References.** A containment reference is written *inline* as a nested message; a
   non-containment reference is written as the target's URI. When the concrete type is only
   known at runtime — polymorphism, cross-package/subpackage targets, or `EObject`-typed
   references — the value is wrapped with a small **type discriminator** so the exact type is
   reconstructed on read (see [References and inheritance](#references-and-inheritance)).
5. **Self-describing output.** A bare Protobuf message carries no type tag, so `toBytes`
   frames each root with its `EPackage` nsURI + `EClass` name; `fromBytes(bytes)` reads that
   frame back and reconstructs the exact type — you never pass an `EClass` in. The type is
   resolved against the reader's own package first, then the context's `EPackage.Registry`
   (so cross-package roots need their package registered). The `Resource` builds on the same
   frame per root. If you already know the type and want the bare message, use
   `writer().toBareBytes(obj)` + `reader().fromBareBytes(bytes, eClass)`.

Only features that are actually serialized get a field: transient, derived, and
[`ignore`d](#feature-filters) features are omitted and do not consume a field number.

## Type mapping

| Ecore `EDataType` | Protobuf type |
|---|---|
| `EBoolean` / `EBooleanObject` | `bool` |
| `EInt`/`EIntegerObject`, `EShort`, `EByte`, `EChar` | `int32` |
| `ELong` / `ELongObject` | `int64` |
| `EFloat` / `EFloatObject` | `float` |
| `EDouble` / `EDoubleObject` | `double` |
| `EString` | `string` |
| `EByteArray` | `bytes` |
| `EBigInteger`, `EBigDecimal` | `string` (lossless text form) |
| `EEnum` | Protobuf `enum` (numeric on the wire) |
| any other `EDataType` | `string` via the type's `EFactory` `convertToString`/`createFromString` |

Multiplicity: `isMany()` → `repeated`. Single scalars carry proto3 field presence, so
`eIsSet` round-trips for `unsettable` features. Enums lacking a `0` literal get a synthesized
`<Enum>_UNSPECIFIED = 0` (proto3 requires a zero value).

## References and inheritance

- **Containment** → the contained object is nested inline. For a *monomorphic* target (a
  local, concrete `EClass` with no subtypes) this is a plain nested message.
- **Non-containment** → the target's URI. Targets **in the same resource** use a
  resource-relative fragment (so the graph resolves after reloading under a different URI);
  other targets keep their absolute URI. On read they become proxies, resolved by the
  `ResourceSet`.
- **Inheritance / polymorphism** → messages are flattened (above), so a subtype round-trips
  its full feature set. A containment whose declared type is **abstract or has subtypes** is
  written as an `EObjectAny` wrapper: a **type discriminator** plus the object's own bytes.
  The reader reconstructs the concrete subtype.
- **Cross-package, subpackage & `EObject`-typed references** → also use the wrapper; the
  target is serialized against *its own* package's schema and identified by the discriminator,
  which the reader resolves (via the package registry for URI/numeric forms).
- **Cross-*document* containment** (a containment pointing into another resource) is
  unsupported and reported as a resource error / `ProtobufException`.

The discriminator's encoding is configurable — see
[Type discriminator strategy](#type-discriminator-strategy).

## Using it

### Plain Java

```java
ProtobufSchema schema = ProtobufSchema.forPackage(shopPackage);

byte[] bytes = schema.writer().toBytes(category);
Category restored = (Category) schema.reader().fromBytes(bytes); // type read from the stream

// Streams are supported too:
schema.writer().write(category, outputStream);
EObject read = schema.reader().read(inputStream);

// If the type is known up front you can skip the frame and read a bare message:
byte[] bare = schema.writer().toBareBytes(category);
Category same = (Category) schema.reader().fromBareBytes(bare, ShopPackage.Literals.CATEGORY);
```

### As an EMF `Resource` (load/save by URL)

Register the factory on a `ResourceSet` (done for you in OSGi) and use the normal API:

```java
resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
    .put(ProtobufResource.FILE_EXTENSION, new ProtobufResourceFactory()); // "protobin"

Resource out = resourceSet.createResource(URI.createFileURI("/data/shop.protobin"));
out.getContents().add(category);
out.save(null);

Resource in = resourceSet.createResource(URI.createFileURI("/data/shop.protobin"));
in.load(null);
```

The `EPackage`(s) of the stored objects must be in the resource set's package registry
(`resourceSet.getPackageRegistry().put(nsURI, ePackage)`) or the global registry, so `load`
can resolve the types. A resource may hold multiple root objects, even from different
packages.

### In OSGi

The `org.eclipse.fennec.protobuf.osgi` bundle contributes the factory as a service, bound by
file extension / content type. The Fennec `DefaultResourceFactoryRegistryComponent` wires it
into every `ResourceSet` automatically — no manual registration:

```java
@Component(name = "ProtobufResourceFactory", service = Resource.Factory.class)
@EMFConfigurator(
    configuratorName = "protobuf",
    configuratorType = ConfiguratorType.RESOURCE_FACTORY,
    fileExtension = { ProtobufResource.FILE_EXTENSION },
    contentType = { ProtobufResource.CONTENT_TYPE })
public class ProtobufResourceFactoryComponent extends ProtobufResourceFactory {}
```

Consumers depend only on `org.eclipse.fennec.emf.osgi.api` plus EMF and OSGi DS.

## Configuration

There are two configuration channels, mirroring the Fennec Codec's conventions:

- **Ecore `EAnnotation`s** (source `http://eclipse.org/fennec/protobuf`) — model-level,
  travels with the model. Detail keys are the bare property names.
- **`ProtobufContext` (plain Java) / `Resource` load-save options** — call-site level.

### Field numbers

Pin a wire-stable field number on a feature (recommended whenever the bytes are stored or
exchanged across model versions):

```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="price">
  <eAnnotations source="http://eclipse.org/fennec/protobuf">
    <details key="fieldNumber" value="3"/>
  </eAnnotations>
</eStructuralFeatures>
```

Without a pinned number, numbers are assigned deterministically in feature order — stable for
a given model definition, but they shift if you reorder/insert features, so **pin them for
long-lived data**.

### Type discriminator strategy

Controls how the concrete type of a *wrapped* object (polymorphic / cross-package / `EObject`)
is encoded. Because the reader is **strategy-agnostic** (it recognises any form), this only
affects the *written* payload size:

| Strategy | Same-package target | Cross-package target |
|---|---|---|
| `NAME` (default) | simple name — `Dog` | full URI — `nsURI#//Dog` |
| `URI` | full URI (bare name if `smartCompression`) | full URI |
| `NUMERIC` | classifier id — `1` | `nsURI#3` |

`smartCompression` (default **on**) writes the compact same-package form and only falls back
to the fully-qualified URI across packages. `NUMERIC` is the smallest but is fragile across
model evolution (classifier ids shift), so prefer it only for stable, versioned models.

```java
// plain Java
ProtobufContext ctx = ProtobufContext.defaults()
    .withTypeStrategy(ProtobufTypeStrategy.NUMERIC)
    .withSmartCompression(true);
byte[] bytes = schema.writer(ctx).toBytes(root);
```

```java
// Resource / OSGi — set once on the ResourceSet
resourceSet.getSaveOptions().put(ProtobufResource.OPTION_TYPE_STRATEGY, ProtobufTypeStrategy.NAME);
resourceSet.getSaveOptions().put(ProtobufResource.OPTION_SMART_COMPRESSION, Boolean.TRUE);
```

```xml
<!-- model-level default (plain-Java writer()) — on the EPackage -->
<eAnnotations source="http://eclipse.org/fennec/protobuf">
  <details key="typeStrategy" value="NAME"/>
  <details key="smartCompression" value="true"/>
</eAnnotations>
```

### Feature filters

Per-feature `EAnnotation` details, to drop or force a feature:

| Key | Effect |
|---|---|
| `ignore` | drop entirely — no field, never (de)serialized |
| `ignoreWrite` / `ignoreRead` | keep the field but skip it on write / read |
| `forceWrite` / `forceRead` | include an otherwise-skipped **transient** feature |

### Schema reuse (performance)

Deriving descriptors is deterministic but not free. Reuse a `ProtobufSchemaCache` when
(de)serializing many objects or sharing a `ResourceSet`:

```java
ProtobufSchemaCache cache = new ProtobufSchemaCache();
byte[] bytes = cache.get(ePackage).writer().toBytes(myEObject);
```

```java
// share one cache across a ResourceSet
resourceSet.getLoadOptions().put(ProtobufResource.OPTION_SCHEMA_CACHE, cache);
resourceSet.getSaveOptions().put(ProtobufResource.OPTION_SCHEMA_CACHE, cache);
```

`invalidate(ePackage)` / `clear()` it if a dynamic package changes shape at runtime.

### Configuration reference

| Setting | Annotation key (source `…/fennec/protobuf`) | Option key / context method | Scope | Default |
|---|---|---|---|---|
| Field number | `fieldNumber` | — | feature | auto |
| Drop feature | `ignore` | — | feature | `false` |
| Skip write / read | `ignoreWrite` / `ignoreRead` | — | feature | `false` |
| Force transient | `forceWrite` / `forceRead` | — | feature | `false` |
| Type strategy | `typeStrategy` (package) | `OPTION_TYPE_STRATEGY` / `withTypeStrategy` | global | `NAME` |
| Smart compression | `smartCompression` (package) | `OPTION_SMART_COMPRESSION` / `withSmartCompression` | global | `true` |
| Schema cache | — | `OPTION_SCHEMA_CACHE` / `withSchemaCache` | global | per-resource |
| Package registry | — | `withPackageRegistry` | global | global registry |

The annotation provides the default for the plain-Java `writer()`/`reader()`; an explicit
`ProtobufContext` or `Resource` option overrides it.

## Importing: descriptors → Ecore

For **interop and bootstrapping** — consuming a schema defined elsewhere (a gRPC service,
another team's `.proto`) as an EMF model — `ProtobufImporter` (package
`org.eclipse.fennec.protobuf.ecore`) derives dynamic `EPackage`s from **compiled** Protobuf
descriptors. Text `.proto` is not parsed (protobuf-java has no text parser); compile it first:

```bash
protoc --include_imports --descriptor_set_out=shop.desc shop.proto
```

```java
byte[] descriptorSet = Files.readAllBytes(Path.of("shop.desc"));
List<EPackage> packages = ProtobufImporter.fromDescriptorSet(descriptorSet);
// register / save as .ecore, or feed to a ResourceSet
```

Options (`ProtobufImporter.fromDescriptorSet(bytes, ImportOptions.defaults()…)`): `withNsUri`
(proto package → nsURI; default `http://<package>`), `withFieldNumberAnnotations` (default on),
`withCamelCaseNames` (default off — proto names kept verbatim).

The mapping is one `EPackage` **per proto package**; messages → `EClass`, enums → `EEnum`,
`repeated` → `isMany`, proto3 `optional` → `unsettable`, `map<k,v>` → a containment reference
to the synthetic entry `EClass`. Field numbers are re-pinned as `fieldNumber` annotations, so a
later [export](#quick-start) stays wire-stable.

It is deliberately **structural**, so a plain `.proto` (which carries no EMF semantics) loses:

- **inheritance** — proto3 has none, so every `EClass` is flat;
- **reference semantics** — every message-typed field becomes a *containment* `EReference`
  (no non-containment / URI / proxy distinction to recover);
- **attribute precision** — `int32`/`string` cannot be re-widened to the `short`/`char`/
  `BigDecimal`/date/custom types a forward export collapsed into them; `uint64`/`fixed64` → `ELong`
  (may overflow above `Long.MAX_VALUE`);
- **Fennec wrappers** — a Fennec export's `EObjectAny`/`EObjectRef` messages return as ordinary
  `EClass`es (annotation-aware, lossless import is not implemented).

## Behavior and guarantees

- **Unset vs. default.** For `unsettable` features, an explicit set-to-default value
  round-trips (`eIsSet` preserved) via proto3 presence. For ordinary features, proto3's
  default-omission applies (a value equal to the default is indistinguishable from unset).
- **Transient / derived** features are never written; use `forceWrite`/`forceRead` to include
  a transient one.
- **Name validation.** Message/enum/feature names must be valid Protobuf identifiers; a
  feature-name clash from multiple inheritance is rejected up front with a clear error.
- **Errors** on the `Resource` path are recorded as resource errors (`getErrors()`) and
  surfaced as an `IOException`; the plain-Java API throws `ProtobufException`.

## Limitations

- **`.proto` export fidelity.** Wrapped (polymorphic / cross-package / `EObject`) references
  appear as the `EObjectAny`/`EObjectRef` wrapper rather than an imported message type.
- **Not yet supported:** `FeatureMap`/mixed-content features; `EJavaObject` and custom data
  types without symmetric `convertToString`/`createFromString`; parsing hand-written `.proto`
  *text* (the runtime has no text parser — compile to a `FileDescriptorSet` first and use
  [`ProtobufImporter`](#importing-descriptors-ecore)).
- **Field-number stability** requires pinning (see [Field numbers](#field-numbers)).

## Relationship to Fennec Codec

If you use the [Fennec Codec](https://github.com/eclipse-fennec/emf.codec), the
payload-reducing knobs are here too, with the same names where they translate. Because
Protobuf is binary and field-number-based, much of what the codec configures is **automatic**:

| Fennec Codec | EMF ⇄ Protobuf |
|---|---|
| `typeStrategy` (NAME/URI/NUMERIC) | same — annotation / `OPTION_TYPE_STRATEGY` |
| `smartCompression` | same — but default **on** (codec: off) |
| `serializeDefault` / `serializeEmpty` / `serializeNull` | automatic — proto3 omits defaults/empties; unset (`eIsSet`) features are skipped |
| `enumSerialization = VALUE` | automatic — Protobuf enums are numeric |
| `ignore` / `ignoreWrite` / `ignoreRead` / `forceWrite` / `forceRead` | same (feature annotation) |
| `typeKey` / `refKey` / `*Format` / property-name keys | not applicable — field names are never on the wire |

The annotation source (`http://eclipse.org/fennec/protobuf`) parallels the codec's
(`http://eclipse.org/fennec/codec`), using the same bare detail keys.

See the [examples](/examples/protobuf) for an end-to-end walk-through.
