# DMTF MOF → Ecore

> **Status: v1.** Implemented and unit-tested (class declarations); the API may still evolve.

Import schemas written in **DMTF MOF** — the *Managed Object Format* of the
[Common Information Model](https://www.dmtf.org/standards/cim) (DMTF DSP0004) — as Ecore.
MOF is the canonical source of the DMTF CIM Schema and of the vendor schemas built on it:
Microsoft WMI (`root\cimv2`, `root\StandardCimv2`), SMI-S, SMASH/DASH. Not to be confused
with OMG MOF (*Meta Object Facility*), which Ecore itself covers.

Bundle `org.eclipse.fennec.mof.ecore` — plain Java + EMF, no OSGi needed, no further
dependencies.

- **Input:** a `.mof` file and everything it `#pragma include`s (e.g. the root
  `cim_schema_2.56.0.mof` of a CIM Schema distribution), or MOF text such as WMI's
  `ManagementClass.GetText(TextFormat.Mof)` output.
- **Output:** dynamic `EPackage`s with the CIM class hierarchy, typed association
  references, keys, methods, and all qualifiers as annotations.
- **Why not the WS-CIM XSDs?** DMTF also publishes XSDs, but they describe the WS-Management
  wire format: one namespace per class, no inheritance (every class is flattened), untyped
  `CimReference` instead of the association target, qualifiers as raw XML text. The MOF
  carries the model.

## Quick start

```java
MofImport result = MofImporter.importFrom(Path.of("cim_schema_2.56.0.mof"));

EPackage cim = result.getPackage("CIM");
EClass port = result.getEClass("CIM_EthernetPort");      // case-insensitive, as in CIM
result.warnings().forEach(System.out::println);
```

Through a `ResourceSet`, like `XSDResourceFactoryImpl` for XML Schema:

```java
resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put(MofResourceFactory.EXTENSION, new MofResourceFactory());
Resource r = resourceSet.getResource(URI.createFileURI("cim_schema_2.56.0.mof"), true);
EPackage cim = (EPackage) r.getContents().get(0);
```

The resource is read-only; save the packages as `.ecore` (or generate code from them) if you
need them persisted. Import options can be given to the factory or per load via
`MofResource.OPTION_IMPORT_OPTIONS`. Import warnings appear in `Resource.getWarnings()`.

## Mapping

| CIM | Ecore |
|---|---|
| class, `Abstract` | `EClass` with the **exact CIM name** (e.g. `CIM_EthernetPort`, so WBEM/WMI instances fit without renaming), `abstract` |
| superclass | `eSuperTypes` (across packages) |
| association / indication | ordinary `EClass`; the `Association` / `Indication` qualifier is kept as annotation |
| `REF` property | non-containment `EReference` to the declared class |
| WMI `object` property | containment `EReference` to `EObject` |
| re-declared (`Override`) property or method | not created again — Ecore cannot redefine an inherited feature — but recorded on the subclass: `override.<name>` = the (narrowed) type, `override.<name>.<Qualifier>` = each qualifier of the override |
| `Key` properties | class annotation `keys`, inherited keys first, e.g. `CreationClassName,DeviceID,SystemCreationClassName,SystemName` |
| `uint8/16`, `sint8/16/32` | `EIntegerObject` |
| `uint32/64`, `sint64` | `ELongObject` (`uint64` values above `Long.MAX_VALUE` overflow) |
| `real32/64` | `EFloatObject` / `EDoubleObject` |
| `boolean`, `char16` | `EBooleanObject`, `ECharacterObject` |
| `string`, `datetime` | `EString` (CIM datetime format, also intervals) |
| arrays | `upperBound = -1` (fixed-size `[n]`: `n`), `unique = false` |
| scalar default value | `defaultValueLiteral` (annotation `default` if not representable, e.g. a large `uint64`) |
| array default value | annotation `default` (JSON array) |
| method | `EOperation` with `EParameter`s; WMI `void` → no return type |
| `Description` | GenModel `documentation` |
| every other qualifier (`ValueMap`, `Values`, `Units`, `MaxLen`, `Deprecated`, `IN`, `OUT`, …) | detail of the annotation `http://schemas.dmtf.org/wbem/cim` |

Object types are used throughout so "not set" stays distinct from `0`/`false`. The original
CIM type of every feature, operation and parameter is kept as annotation `cimType`
(`uint16[]`, `datetime`, `CIM_ManagedElement REF`, …), so the `ELongObject` of a `uint32` can
be told apart from an `sint64`.

### Packages

By default every class goes into an `EPackage` named after its **schema** — the part of the
name before the first underscore (`CIM_…` → `CIM`, `Win32_…` → `Win32`, `MSFT_…` → `MSFT`;
WMI system classes such as `__SystemClass` → `Default`). nsURI and nsPrefix default to
`urn:mof:<name>` and the lower-case name.

```java
ImportOptions options = ImportOptions.defaults()
        .withPackageName(className -> "cim")                       // everything in one package
        .withNsUri(pkg -> "https://example.org/models/" + pkg)
        .withNsPrefix(pkg -> pkg);
```

### Reading qualifiers back

`MofAnnotations` has the constants and helpers; qualifier names are matched
case-insensitively, as in CIM. Boolean qualifiers read `"true"`/`"false"` (`[Key]` is
`"true"`), scalar values are stored as they are, array values (`ValueMap`, `Values`, …) as a
JSON array:

```java
EStructuralFeature status = port.getEStructuralFeature("OperationalStatus");
List<String> codes  = MofAnnotations.qualifierValues(status, "ValueMap"); // ["0", "2", "3", …]
List<String> labels = MofAnnotations.qualifierValues(status, "Values");   // ["Unknown", "OK", …]
String units        = MofAnnotations.qualifier(speed, "Units");
List<String> keys   = MofAnnotations.keys(port);
String cimType      = MofAnnotations.cimType(status);                     // "uint16[]"
String narrowed     = MofAnnotations.qualifier(systemDevice, "override.PartComponent"); // "CIM_LogicalDevice REF"
```

## Options

| Option | Default | Effect |
|---|---|---|
| `withClasses(Collection<String>)` | all | import only these classes plus their superclasses and, transitively, the classes their `REF` properties, parameters and return types need |
| `withStrict(boolean)` | `true` | strict: an undeclared superclass or `REF` target fails the import, listing all of them. Lenient: the class is imported without that supertype (annotation `unresolvedSuperclass`), the reference is typed `EObject` (annotation `unresolvedType`), and a warning is reported |
| `withPackageName(Function)` | schema prefix | class name → package name |
| `withNsUri(Function)`, `withNsPrefix(Function)` | `urn:mof:<name>`, lower case | package name → nsURI / nsPrefix |

## Syntax support

- DSP0004 (MOF 2) class syntax: qualifier lists, classes with superclass and alias,
  properties, `REF` properties, fixed and variable arrays, default values, methods and
  parameters, qualifier declarations (used for the canonical spelling of qualifier names and
  to know which qualifiers are arrays).
- `#pragma include` is followed relative to the including file, then relative to the entry
  file; every file is read once. Other pragmas (`locale`, `namespace`, `classflags`, …) are
  ignored.
- Instance declarations (`instance of … as $alias { … };`) are skipped. Importing instances
  is a possible later step.
- WMI dialect: flavor suffixes (`Description("…") : Amended ToSubclass`), `object` and `void`
  types, UTF-16 files with or without BOM, legacy `#include`, backslash line continuation,
  `%VAR%` in include paths, unescaped backslashes in strings. All 267 MOF files of a Windows
  `System32\wbem` directory import.
- MOF 3 (DSP0221): `association`/`indication` keywords and `type[] name` arrays are accepted;
  `structure`, `enumeration` and `interface` declarations are rejected with a clear error.
- CIM names are case-insensitive: a second declaration of the same class is ignored with a
  warning. Parse errors report `file:line:column`.

## Testing against the real CIM Schema

The DMTF CIM Schema is not checked in. To run the acceptance check — the complete schema
imports in strict mode, with no unresolved superclass or reference type, and validates —
download `cim_schema_<version>Final-MOFs.zip` from <https://www.dmtf.org/standards/cim>,
unpack it, and point the opt-in test at the root file:

```bash
MOF_CIM_SCHEMA=/path/to/cim_schema_2.56.0.mof ./gradlew :org.eclipse.fennec.mof.ecore:test --tests '*MofCorpusTest*'
MOF_CORPUS_DIR='C:\Windows\System32\wbem'     ./gradlew :org.eclipse.fennec.mof.ecore:test --tests '*MofCorpusTest*'
```
