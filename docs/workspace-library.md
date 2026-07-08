# Consuming Fennec EMF Util

The utilities in this repository are published two ways, so you can consume them
either inside a bnd/OSGi workspace or as plain Maven/Gradle dependencies.

## As a bnd workspace library (`fennecUtil`)

`emf.util` ships a bnd workspace library, `org.eclipse.fennec.util.workspace.library`,
that carries the emf.util bundles **and their full Maven dependency closure**. Enabling it
wires a read-only Maven repository into your workspace so every emf.util bundle (and what it
needs) resolves.

1. Add the library artifact to your workspace's Maven index (`cnf/ext/central.mvn`, or
   wherever your project indexes Maven dependencies):

   ```
   org.eclipse.fennec.util:org.eclipse.fennec.util.workspace.library:<version>
   ```

2. Enable the library in your workspace bnd config (`cnf/build.bnd` or `cnf/ext/*.bnd`):

   ```
   -library: fennecUtil
   ```

3. Reference the bundles you need on a bundle's `-buildpath` (and, for runtime, its
   `-runrequires`):

   ```
   -buildpath: \
       org.eclipse.fennec.protobuf;version=latest
   ```

`fennecUtil` currently provides:

| Bundle | Purpose |
|---|---|
| `org.eclipse.fennec.protobuf` | EMF ⇄ Protobuf core (plain Java) |
| `org.eclipse.fennec.protobuf.osgi` | Protobuf `Resource.Factory` OSGi service |

## As a plain Maven / Gradle dependency

The cores are ordinary Java libraries and do **not** require OSGi. Depend on the bundle
directly — for example the EMF ⇄ Protobuf core:

```gradle
dependencies {
    implementation 'org.eclipse.fennec.util:org.eclipse.fennec.protobuf:<version>'
}
```

This pulls in `protobuf-java` and EMF transitively. Add
`org.eclipse.fennec.util:org.eclipse.fennec.protobuf.osgi` as well only if you want the
OSGi `Resource.Factory` service registered in a Fennec EMF OSGi runtime.

## Versions

Releases are published to Maven Central from the `main` branch; snapshots from the
`snapshot` branch. Use a released version where possible; snapshots move.

See the [EMF ⇄ Protobuf guide](/guides/protobuf) and its [examples](/examples/protobuf)
for how to use the API once it is on your path.
