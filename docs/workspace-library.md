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
| `org.eclipse.fennec.protobuf.ecore` | Protobuf descriptor → Ecore importer (design time) |
| `org.eclipse.fennec.soap` | SOAP 1.1 envelope (de)serialization core |
| `org.eclipse.fennec.soap.osgi` | SOAP `Resource.Factory` OSGi service |
| `org.eclipse.fennec.soap.ecore` | WSDL/XSD → Ecore converter (design time, pulls `org.eclipse.xsd`) |
| `org.eclipse.fennec.openapi.ecore` | OpenAPI 3 → Ecore importer |
| `org.eclipse.fennec.openapi.client` | OpenAPI REST `ServiceClient` |
| `org.eclipse.fennec.openapi.osgi` | Config-driven OpenAPI `ServiceClient` OSGi component |
| `org.eclipse.fennec.grpc` | gRPC client + server over EMF instances (with the `io.grpc.netty` wrap bundle) |
| `org.eclipse.fennec.service.api` | The protocol-agnostic `ServiceClient` / `ServiceOperation` API |
| `org.eclipse.fennec.jgit` | `GitService`: reading and writing a local or remote git repository (see the note below) |
| `org.eclipse.fennec.git.webhook.model` | Provider-neutral model of a git push webhook |
| `org.eclipse.fennec.git.github.webhook.model` | GitHub push webhook payload model |
| `org.eclipse.fennec.git.gitlab.webhook.model` | GitLab push webhook payload model |
| `org.eclipse.fennec.git.webhook.rest` | Whiteboard REST endpoints receiving GitHub/GitLab push webhooks |
| `org.eclipse.fennec.sensinact.mapping` | SensiNact mapping metamodel + runtime (see the note below) |

The exact set is the `-runrequires` of
`org.eclipse.fennec.util.workspace.library/required.bndrun`, and the index carries the
resolved closure of those bundles. Two consequences worth knowing:

- **The SensiNact gateway is not in the closure.** `org.eclipse.fennec.sensinact.mapping`
  imports `org.eclipse.sensinact.*` as *optional*, so enabling `fennecUtil` gives you the
  mapping bundle without dragging the SensiNact gateway bundles into every workspace.
  A runtime that actually maps into a digital twin has to supply the gateway itself
  (`org.eclipse.sensinact.gateway.core.*`, e.g. from the sensinact distribution).
  **Deprecated:** the mapping bundle moved to the
  [event.atlas](https://github.com/eclipse-fennec/event.atlas) repository as
  `org.eclipse.fennec.event.atlas.mapping` (Maven group `org.eclipse.fennec.event.atlas`);
  it will be dropped from this library when the frozen copy is removed.
- **The SSH stack for `org.eclipse.fennec.jgit` is in the closure, but you have to require
  it by name.** The bundle imports `org.eclipse.jgit.transport.sshd` *optionally*, so a
  consumer that only reads a repository on disk or over `http(s)://` does not have to ship
  Apache MINA sshd and BouncyCastle at all — and, by the same token, a consumer that wants
  `ssh://` gets nothing unless its bndrun asks:

  ```
  -runrequires: \
      bnd.identity;id='org.eclipse.jgit.ssh.apache',\
      bnd.identity;id='bcpkix'
  ```

  `org.eclipse.jgit.ssh.apache` pulls in `org.apache.sshd.*` and `bcprov`; `bcpkix` (with
  `bcutil`) is only needed for a private key in encrypted PKCS#8 form. See the
  [git guide](/guides/jgit) for the full list, including the framework properties.

- **Not every bundle in the repo is in the library** — the Model Atlas EObject provider
  (`org.eclipse.fennec.model.atlas.eobject.provider`, **deprecated here: moved to the
  [event.atlas](https://github.com/eclipse-fennec/event.atlas) repository**, Maven group
  `org.eclipse.fennec.event.atlas`) and the SOAP client
  (`org.eclipse.fennec.soap.client`) are not required by it, so `-library: fennecUtil`
  alone will not resolve them; index those artifacts explicitly in your own
  `central.mvn` when you need them.

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
