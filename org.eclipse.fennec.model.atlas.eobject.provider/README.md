# ⚠️ Deprecated — moved to event.atlas

This project has moved to the
[`eclipse-fennec/event.atlas`](https://github.com/eclipse-fennec/event.atlas) repository
(same directory name, same bundle symbolic name and Java packages). All further
development happens there; this copy is frozen and will be removed in a future cleanup.

| | old (here) | new (event.atlas) |
|---|---|---|
| Repository | `eclipse-fennec/emf.util` | `eclipse-fennec/event.atlas` |
| BSN / packages | `org.eclipse.fennec.model.atlas.eobject.provider` | unchanged |
| Maven coordinates | `org.eclipse.fennec.util:org.eclipse.fennec.model.atlas.eobject.provider` | `org.eclipse.fennec.event.atlas:org.eclipse.fennec.model.atlas.eobject.provider` |

Consumers should switch their `central.mvn` / dependency declarations to the new Maven
coordinates; snapshots are published from event.atlas's `snapshot` branch.
