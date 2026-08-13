# ⚠️ Deprecated — moved to event.atlas

This project has moved to the
[`eclipse-fennec/event.atlas`](https://github.com/eclipse-fennec/event.atlas) repository (renamed).
All further development happens there; this copy is frozen and will be removed in a
future cleanup.

| | old (here) | new (event.atlas) |
|---|---|---|
| Project / BSN | `org.eclipse.fennec.sensinact.mapping` | `org.eclipse.fennec.event.atlas.mapping` |
| Engine packages | `org.eclipse.fennec.sensinact.mapping*` | `org.eclipse.fennec.event.atlas.mapping*` |
| Model package | `org.eclipse.fennec.sensinact.model.mapping` | `org.eclipse.fennec.event.atlas.model.mapping` |
| Metamodel nsURI | `https://fennec.eclipse.org/sensinact/core/mapping/1.0` | `https://fennec.eclipse.org/event.atlas/mapping/1.0` |
| Maven coordinates | `org.eclipse.fennec.util:org.eclipse.fennec.sensinact.mapping` | `org.eclipse.fennec.event.atlas:org.eclipse.fennec.event.atlas.mapping` |

**Existing mapping XMIs must switch the nsURI in their root element** to load against the
new bundle (`PackageNotFoundException` otherwise); nothing else about the files changes.
The user guide lives in event.atlas at `docs/sensinact-mapping-user-guide.md`.
