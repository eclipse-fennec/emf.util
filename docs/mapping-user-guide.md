# Generic mapping

The generic Fennec mapping fills the features of **target objects** from **source objects** —
sensor readings into a pool, a JSON payload into a domain object, one model into another — driven
by a declarative `Mapping` model instead of hand-written glue code. It answers two questions per
source object: *which target object(s) does it feed* (identity resolution) and *which value goes
into which target feature* (the value ladder).

The engine does not decide *when* something is mapped or *where* target objects live: a stream
reacts to changes, an indexer pulls, an OGC Features endpoint maps on request. That is the
consumer's business; the engine is shared by all of them.

| Bundle | Content |
|---|---|
| `org.eclipse.fennec.mapping` | Plain-Java engine: value ladder, identity resolution with cache, validator, registry. No OSGi needed. |
| `org.eclipse.fennec.mapping.ocl` | OCL expressions through the Fennec M2X OCL engine (the only bundle with an expression language). |
| `org.eclipse.fennec.mapping.osgi` | Function/converter whiteboards and the mapping registry on an emf.osgi EObject registry. |

The mapping **model** (`org.eclipse.fennec.mapping.model`, nsURI
`https://eclipse.org/fennec/mapping/1.0.0`) is not part of this repository; it is published by
[fennec.models](https://github.com/eclipse-fennec/fennec.models) and extends the atlas
configuration model's `DataTransformation`. None of the bundles depends on emf.streaming — they
moved here from it (emf.util#84) so that other consumers can use them without the streaming stack.

## The model

```
Mapping (id, target: EClass, includeUnmapped)
 ├─ identities: IdentityResolution*  (sourceClass, targetClass, resolver: ValueSource, cacheable)
 └─ slots: Slot*                     (targetFeature | name, sourceClass, value: ValueSource, targetConverterId)

ValueSource (expression | functionId | featurePath*, collectionIndex, collectionFilter, converterId, constant)
```

- A **slot** fills one feature of the target class (or, without a `targetFeature`, a named
  value). `sourceClass` says which source class it reads; without one the source *is* the target
  class (copying within a type).
- An **identity resolution** says, per source class, how to find the target id. Without one the
  source object's own id is the target id.
- A **value source** describes how a value is produced — see the ladder below.

## The value ladder

`ValueExtractor` produces a slot's value in three stages, each its own method so a consumer can
stop in between (streaming stores the value after stage 2 and writes the target later):

1. **`produce`** — the *first declared* producer answers: `expression` (OCL, `self` = source
   object), else `functionId` (on the whole source object, or on what the feature path found),
   else the `featurePath`. This is **not** a fallback chain: a declared producer that yields
   nothing means the source carries no value.
2. **`extract`** — if the producer yielded nothing, the `constant` answers (parsed into the target
   type, not converted); otherwise `converterId` post-processes the produced value.
3. **`toTarget`** — `targetConverterId`, then coercion to the type of the slot's target feature.

A feature path that ends on a collection can be narrowed with `collectionIndex` or a
`collectionFilter` of the form `path.to.feature=value` (value optionally quoted; an enum compares
by name).

Failure semantics are deliberate:

| Situation | Effect |
|---|---|
| unknown **function** id | no value (the constant answers, if any) |
| function throws / returns `null` | no value |
| unknown **converter** id | value passes **unconverted**, the id is logged once |
| converter throws / returns `null` | value dropped |
| no expression language deployed | expressions yield no value |
| OCL does not parse / fails / yields `null`/`invalid` | no value, logged once per expression |

## Identity resolution

`IdentityResolver` answers which target object(s) a source feeds:

- no resolution or no resolver → the source's id is the target id;
- a resolver with a feature path or OCL expression → a key read from the source;
- a resolver with a function → anything else (a lookup table, a device registry).

A resolver yielding a collection feeds **several** targets (fan-out); an `EObject` element stands
for its id. Answers of `cacheable` resolutions are cached per (resolution, source id) until
`invalidate(…)` — call it when a device is moved, or `invalidateAll()` when the mapping changes.

## Plain Java

```java
FunctionRegistry functions = new FunctionRegistry();
functions.register("ph-to-pool", sensor -> lookupPool((EObject) sensor));

ConverterRegistry converters = new ConverterRegistry();
converters.register("celsius-to-fahrenheit", v -> ((Double) v) * 1.8 + 32);

ExpressionEvaluator ocl = null; // or new OclExpressionEvaluator(engine) from mapping.ocl
ValueExtractor extractor = new ValueExtractor(functions, converters, ocl);
Mapper mapper = new Mapper(extractor, new IdentityResolver(extractor));

// validates on register; new InMemoryMappingRegistry(new MappingValidator(functions, converters))
// also checks that the named function/converter ids exist
InMemoryMappingRegistry registry = new InMemoryMappingRegistry();
Diagnostic diagnostic = registry.register(mapping); // a mapping with errors is refused

for (MappedValue value : mapper.map(mapping, sensorReading)) {
    EObject pool = pools.get(value.targetId());   // where targets live is up to you
    mapper.apply(value, pool);                    // stage 3 + eSet
}
```

`ValueExtractor.basic()` gives an extractor with empty registries and no expression language —
feature paths and constants only.

### Validation

`MappingValidator` turns mistakes into load errors instead of values that silently never arrive:
a slot needs a target feature of the target class (or a name); no two slots fill the same feature;
every value source declares a producer or a constant; feature paths must be chains the metamodel
allows; collection filters must be well-formed; function/converter ids must be known (when
registries are passed); at most one identity resolution per (source class, target class). OCL is
**not** parsed by the validator — the expression language checks it where it is deployed.
`InMemoryMappingRegistry.register` refuses a mapping with errors.

## OCL (`org.eclipse.fennec.mapping.ocl`)

`OclExpressionEvaluator` implements `ExpressionEvaluator` on the M2X `OclEngine`:
`self.value * 1.8 + 32`, `self.readings->size()`,
`if self.battery < 10 then 'low' else 'ok' endif`. Additional variables are passed as OCL external
variables. In OSGi, `OclExpressionEvaluatorComponent` publishes it as an `ExpressionEvaluator`
service once an `OclEngine` service is there; bind the engine to the fingerprint cache
(`expressionCache.target=(cache.name=fingerprint)`) so a changed model is parsed again.

Note that the engine arrives as a *service*: a bndrun must require `org.eclipse.fennec.m2x.ocl.engine`
and `org.eclipse.fennec.m2x.ocl.parser` explicitly (the `fennecUtil` library closure does).

## OSGi (`org.eclipse.fennec.mapping.osgi`)

| Component | Service | Notes |
|---|---|---|
| `FunctionWhiteboard` | `FunctionRegistry` | binds every `ValueFunction` service with property `fennec.mapping.function.id` (dynamic) |
| `ConverterWhiteboard` | `ConverterRegistry` | binds every `ValueConverter` service with property `fennec.mapping.converter.id` (dynamic) |
| `RegistryMappings` (PID `FennecMappingRegistry`) | `MappingRegistry` | mappings from an emf.osgi `EObjectRegistry`, by default the one named `fennec-mappings` |

Registering a function:

```java
@Component(property = ValueFunction.FUNCTION_ID + "=ph-to-pool")
public class PhToPool implements ValueFunction {
    @Override
    public Object apply(Object input) { … }
}
```

`RegistryMappings` validates every `Mapping` the EObject registry holds and offers the valid ones
under their id (an invalid one is logged and left out). The EObject registry is only published
after its initial load, so the mapping registry never answers from a half-loaded state. A registry
entry carrying `emf.fingerprint` only reaches the model version with that fingerprint. Another
registry can be selected through the optional configuration `registry.target`.

Minimal configurator setup — mappings from XMI files:

```json
{
  ":configurator:resource-version": 1,
  ":configurator:symbolic-name": "my.app.mappings",
  ":configurator:version": "1.0.0",
  "FileEObjectProvider~mappings": {
    "emf.eobject.provider.name": "mapping-files",
    "locations": ["mappings/pool.xmi"],
    "key.feature": "id"
  },
  "EObjectRegistry~mappings": {
    "name": "fennec-mappings",
    "initialProvider.target": "(emf.eobject.provider.name=mapping-files)"
  }
}
```

## Consuming

All three bundles are in the [`fennecUtil` workspace library](/guides/consuming), together with
the mapping model, the atlas configuration model, the emf.osgi EObject registry and the M2X OCL
engine. On the build path:

```bnd
-buildpath: \
    org.eclipse.fennec.mapping.model;version=latest,\
    org.eclipse.fennec.mapping;version=latest
```
