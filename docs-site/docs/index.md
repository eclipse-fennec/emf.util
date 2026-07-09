---
layout: home

hero:
  name: Fennec EMF Util
  text: Utilities and commons for EMF
  tagline: A home for small, focused libraries around the Eclipse Modeling Framework — plain Java first, OSGi-ready.
  image:
    src: /fennec-logo.png
    alt: Eclipse Fennec logo
  actions:
    - theme: brand
      text: User Manual
      link: /guides/overview
    - theme: alt
      text: Examples
      link: /examples/protobuf
    - theme: alt
      text: View on GitHub
      link: https://github.com/eclipse-fennec/emf.util

features:
  - icon: 🧩
    title: EMF ⇄ Protobuf
    details: Schema-driven (de)serialization of EMF models to Google Protocol Buffers, with descriptors derived directly from an EPackage — no generated code. Plus gRPC service extraction.
    link: /guides/protobuf
    linkText: Read the guide
  - icon: 🧼
    title: EMF ⇄ SOAP / WSDL
    details: Import a WSDL/XSD into Ecore and (de)serialize requests/responses through a SOAP 1.1 envelope as XML, with a SOAP client over HTTP.
    link: /guides/soap
    linkText: Read the guide
  - icon: 🌐
    title: EMF ⇄ OpenAPI / REST
    details: Turn an OpenAPI 3 document into Ecore operations and call them over HTTP — request and response are EObjects, JSON via the Fennec codec.
    link: /guides/openapi
    linkText: Read the guide
  - icon: 🔗
    title: Service clients
    details: One protocol-agnostic ServiceClient API — pick an operation, hand over an EObject request, get an EObject response — over SOAP and OpenAPI today, gRPC/OData planned.
    link: /guides/service-clients
    linkText: Read the guide
  - icon: 🔌
    title: OSGi-ready
    details: Every utility has a pure-Java core usable without OSGi, plus optional OSGi services (e.g. a Resource.Factory) for the Fennec EMF OSGi runtime.
  - icon: 📦
    title: Small & focused
    details: Each utility is its own bundle with a narrow scope and its own tests — no monolith, no unnecessary dependencies.
---

## About Fennec EMF Util

Fennec EMF Util (`org.eclipse.fennec.util`) is where various small utility
projects around the [Eclipse Modeling Framework](https://eclipse.dev/modeling/emf/)
live. Each one is a focused library with a clean, plain-Java core that also works
without the Eclipse platform, and — where it makes sense — an OSGi integration
layer for the [Fennec EMF OSGi](https://github.com/eclipse-fennec/emf.osgi) runtime.

Today it covers three integration formats, plus a unifying client layer:

- **[EMF ⇄ Protobuf](/guides/protobuf)** — Protocol Buffer descriptors built directly from an
  `EPackage`, instance (de)serialization via `DynamicMessage` (no `protoc`, no generated classes),
  and gRPC service extraction. See also the [examples](/examples/protobuf).
- **[EMF ⇄ SOAP / WSDL](/guides/soap)** — WSDL/XSD → Ecore import and SOAP 1.1 envelope
  (de)serialization as XML, with a SOAP client.
- **[EMF ⇄ OpenAPI / REST](/guides/openapi)** — OpenAPI 3 → Ecore operations and a REST client.
- **[Service clients](/guides/service-clients)** — one protocol-agnostic `ServiceClient` API over
  the above: pick an operation, pass an `EObject` request, get an `EObject` response.
