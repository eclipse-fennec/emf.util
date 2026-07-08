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
    details: Schema-driven (de)serialization of EMF models to Google Protocol Buffers, with descriptors derived directly from an EPackage — no generated code. Plain Java, well unit-tested.
    link: /guides/protobuf
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

The first utility is **EMF ⇄ Protobuf**: it builds Protocol Buffer descriptors
directly from an `EPackage` and (de)serializes model instances via
`DynamicMessage` — no `protoc`, no generated classes. See the
[user manual](/guides/protobuf) and the [examples](/examples/protobuf).
