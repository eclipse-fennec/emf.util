# EMF ⇄ SOAP / WSDL

> **Status: v1.** Implemented and unit-tested; the API may still evolve.

Turn a WSDL (or XSD) into an EMF model and (de)serialize request/response **instances**
through a **SOAP 1.1 envelope** as XML — no `wsimport`, no generated JAX-WS stubs. The core is
plain Java; an optional OSGi bundle adds a `Resource.Factory` so `.soap` resources load and save
through the ordinary EMF API.

- **Input:** a compiled WSDL/XSD (the importer) and any Ecore model instance (the envelope).
- **Output:** a SOAP 1.1 envelope serialized as XML.
- **Round-trips:** attributes, nested elements, and (via the imported Ecore) the request/response
  message types.

It reuses the existing **SOAP 1.1 envelope model** `org.xmlsoap.model` (package
`org.xmlsoap.schemas.envelope`) rather than defining its own.

## Quick start

```java
// 1. Import the service schema (types + operations) — a bootstrap/design-time step.
WsdlModel model = WsdlImporter.fromWsdl(Files.readAllBytes(Path.of("stock.wsdl")));

// 2. Register the derived packages so payloads (de)serialize through this ResourceSet.
ResourceSet rs = new ResourceSetImpl();
rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
        .put(SoapResource.FILE_EXTENSION, new SoapResourceFactory());   // "soap"
model.registerInto(rs);

// 3. Build a request of the operation's request type and put it in a SOAP resource.
EClass requestType = model.operation("GetStock").requestType();
EObject request = EcoreUtil.create(requestType);
request.eSet(requestType.getEStructuralFeature("symbol"), "IBM");

Resource r = rs.createResource(URI.createURI("mem:/getStock.soap"));
r.getContents().add(request);
r.save(System.out, null);   // <soap:Envelope><soap:Body><st:GetStock><symbol>IBM</symbol>…
```

Loading is the reverse: `r.load(in, null)` parses the envelope and puts the `Body` payload
object(s) back into `r.getContents()`. An incoming SOAP `Fault` is recorded on `r.getErrors()`
and thrown as an `IOException`.

## Importing a WSDL/XSD → Ecore

`WsdlImporter` derives dynamic `EPackage`s from a **compiled** schema. It lives in its **own
bundle `org.eclipse.fennec.soap.ecore`** (package of the same name), separate from the runtime — a
bootstrap/design-time step. Text is parsed as XML, but the *type* mapping is EMF's
`XSDEcoreBuilder`, which also extracts schemas embedded in `<wsdl:types>`.

```java
WsdlModel wsdl = WsdlImporter.fromWsdl(bytes);   // types + operations
WsdlModel xsd  = WsdlImporter.fromXsd(bytes);    // types only (no operations)

wsdl.packages();                 // one EPackage per XSD target namespace
wsdl.operation("GetStock");      // SoapOperation: request/response element QName + EClass
wsdl.registerInto(resourceSet);  // register all packages for (de)serialization
```

Mapping notes:

- One `EPackage` **per XSD target namespace**; a global element's anonymous complex type becomes
  an `EClass` named `<Element>Type`, plus the standard EMF XML `DocumentRoot`.
- Attribute types are the **EMF XML datatypes** (`String`, `Int`, `Double`, … from
  `org.eclipse.emf.ecore.xml.type`), and the generated Ecore carries the `ExtendedMetaData`
  annotations EMF needs to (de)serialize it as XML.
- **WSDL operations** aren't modelled by EMF, so a light DOM parse of
  `portType`/`operation` + `message`/`part` resolves each operation to its request/response
  `EClass` — **document/literal** style (a `<part element="…">`). RPC-style parts that reference a
  *type* rather than a global element are left unresolved (`SoapOperation.requestType()` is `null`).
- Unresolved elements and XSD issues are collected in `WsdlModel.diagnostics()`.

## The SOAP envelope resource

`SoapResource` (extension `.soap`, content type `application/soap+xml`) carries the plain payload
EObjects as its contents:

- **save** builds `<soap:Envelope><soap:Body>…`, adds each payload (a copy, so your graph is
  untouched) to the `Body`'s wildcard `FeatureMap` via its global element, and writes XML.
- **load** parses the envelope and unwraps the `Body` content back into the contents; a
  `<soap:Fault>` becomes a resource error + `IOException`.

Payload types must come from a WSDL/XSD import (so each `EClass` has a global XML element) and
their `EPackage`s must be registered in the resource set's package registry.

## Requirements & dependencies

- Java 21. The importer lives in its **own bundle `org.eclipse.fennec.soap.ecore`** and needs
  **`org.eclipse.xsd`** (EMF). The envelope **runtime** (`org.eclipse.fennec.soap`) has no XSD
  dependency at all, so it resolves in a minimal runtime; add the converter bundle + `org.eclipse.xsd`
  only where you actually run `WsdlImporter` (typically a design-time/bootstrap step).
- Runtime models: `org.xmlsoap.model` (the SOAP envelope) via the `fennecEMFModels` bnd library.

## In OSGi

The `org.eclipse.fennec.soap.osgi` bundle contributes the factory as a service via
`@Component` + `@EMFConfigurator(configuratorType = RESOURCE_FACTORY, fileExtension = "soap",
contentType = "application/soap+xml")`, bound into every Fennec `ResourceSet` automatically by the
`DefaultResourceFactoryRegistryComponent`.

## Scope / limitations (v1)

- **SOAP 1.1** only (the reused envelope model is 1.1); no SOAP 1.2.
- **Document/literal** operations; RPC/encoded style is not resolved to request/response types.
- SOAP **headers** are not read/written in v1 (Body payload + Fault only).
- Text `.proto`-style WSDL/XSD is fine, but cross-file `<import>`/`<include>` resolution beyond the
  inline schemas of one document is out of scope for v1.
