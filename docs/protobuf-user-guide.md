# EMF ⇄ Protobuf

> **Status: v1.** The EMF ⇄ Protobuf utility is implemented and unit-tested; the
> API may still evolve.

Serialize and deserialize EMF model **instances** to [Google Protocol
Buffers](https://protobuf.dev/) — with the Protobuf descriptors derived **directly
from an `EPackage`**. No `protoc`, no generated classes: the utility uses the
protobuf-java runtime's dynamic descriptor API (`DescriptorProtos`,
`Descriptors.FileDescriptor`, `DynamicMessage`).

The core is plain Java (no OSGi, no Jackson). An optional OSGi bundle contributes a
`Resource.Factory` service so a Protobuf resource can be registered in the Fennec
EMF OSGi runtime.

## What it does

- **Descriptor derivation** — `EPackage` → `FileDescriptorProto` →
  `Descriptors.FileDescriptor`. This is the shared foundation.
- **Instance (de)serialization** — `EObject` ⇄ Protobuf wire bytes via
  `DynamicMessage`.
- **Schema export** — render the derived descriptors as a `.proto` (proto3) text
  file, for consumers on other platforms.

> Importing a hand-written `.proto` **schema** back into Ecore is out of scope: the
> protobuf-java runtime ships no `.proto` text parser, so that would require an
> external parser and is a separate, larger feature.

## Type mapping

| Ecore `EDataType` | Protobuf type |
|---|---|
| `EBoolean` / `EBooleanObject` | `bool` |
| `EInt` / `EIntegerObject`, `EShort`, `EByte` | `int32` |
| `ELong` / `ELongObject` | `int64` |
| `EFloat` / `EFloatObject` | `float` |
| `EDouble` / `EDoubleObject` | `double` |
| `EString` | `string` |
| `EByteArray` | `bytes` |
| `EBigInteger`, `EBigDecimal` | `string` (lossless decimal/text form) |
| `EEnum` | Protobuf `enum` |
| other `EDataType` | `string` via the type's EFactory `convertToString` |

Multiplicity: `isMany()` features become `repeated`. In proto3, single-valued
features that need EMF's *unset vs. default* distinction are emitted with the
`optional` keyword (field presence).

## References and structure

- **Containment `EReference`** → embedded message (the contained object is nested).
- **Non-containment `EReference`** → a reference message carrying the target's URI
  fragment / id, **not** the object itself (avoids duplicating or cycling the graph).
- **Inheritance (`ESuperTypes`)** → Protobuf has no inheritance; the derived message
  is flattened to include inherited features. Polymorphic containment can be modelled
  with `oneof`.
- **Multiple `EPackage`s** → one `FileDescriptor` each, wired via the `dependencies`
  array (dependencies built first). Cross-package cycles are not representable and are
  reported as an error.

## Field numbers

Protobuf identifies fields by **number**, and those numbers must be **stable across
model evolution** for wire compatibility. Ecore has no field numbers, and
`getFeatureID()` is not wire-stable (it shifts when features are reordered) and can be
`0` (protobuf starts at `1`). Therefore:

- The **field number is read from an `EAnnotation`** on the feature, source
  `http://www.eclipse.org/fennec/protobuf`, key `fieldNumber`.
- If the annotation is absent, a number is assigned deterministically and a warning is
  emitted — fine for round-tripping within one process, **not** for long-term wire
  compatibility.

```xml
<eStructuralFeatures xsi:type="ecore:EAttribute" name="price" ...>
  <eAnnotations source="http://www.eclipse.org/fennec/protobuf">
    <details key="fieldNumber" value="3"/>
  </eAnnotations>
</eStructuralFeatures>
```

## Usage (plain Java)

```java
// Derive descriptors once per EPackage (cache the result).
ProtobufSchema schema = ProtobufSchema.forPackage(myEPackage);

// Serialize an EObject to protobuf bytes.
byte[] bytes = schema.writer().toBytes(myEObject);

// Deserialize back into an EObject.
EObject copy = schema.reader().fromBytes(bytes, myEClass);

// Export the schema as a .proto text file.
String proto = schema.toProtoSource();
```

## Usage (OSGi)

The `org.eclipse.fennec.protobuf.osgi` bundle registers the `Resource.Factory` as a
service, bound to a file extension / content type. It is picked up automatically by the
Fennec `DefaultResourceFactoryRegistryComponent`, so any `ResourceSet` from the Fennec
runtime can load/save `.protobin` resources:

```java
@Component(name = "ProtobufResourceFactory", service = Resource.Factory.class)
@EMFConfigurator(
    configuratorName = "protobuf",
    configuratorType = ConfiguratorType.RESOURCE_FACTORY,
    fileExtension = { ProtobufResource.FILE_EXTENSION },
    contentType = { ProtobufResource.CONTENT_TYPE })
public class ProtobufResourceFactoryComponent extends ProtobufResourceFactory {
}
```

The component just extends the plain-Java `ProtobufResourceFactory` from the core; consumers
depend only on `org.eclipse.fennec.emf.osgi.api` plus EMF and OSGi DS.

## Reusing schemas (performance)

Deriving the descriptors for a package is deterministic but not free. When you
(de)serialize many objects, reuse a `ProtobufSchemaCache` instead of calling
`ProtobufSchema.forPackage(...)` repeatedly:

```java
ProtobufSchemaCache cache = new ProtobufSchemaCache();
byte[] bytes = cache.get(ePackage).writer().toBytes(myEObject);
```

For the OSGi/Resource path, share one cache across a `ResourceSet` by putting it in
the load/save options under `ProtobufResource.OPTION_SCHEMA_CACHE`:

```java
ProtobufSchemaCache cache = new ProtobufSchemaCache();
resourceSet.getLoadOptions().put(ProtobufResource.OPTION_SCHEMA_CACHE, cache);
resourceSet.getSaveOptions().put(ProtobufResource.OPTION_SCHEMA_CACHE, cache);
```

If a dynamic package changes shape at runtime, `invalidate(ePackage)` (or `clear()`)
the cache.

See the [examples](/examples/protobuf) for an end-to-end walk-through.
