# Eclipse Fennec workspace — setup

This workspace was created from the **fennec-gradle** template fragment.

## Set your project coordinates (one place)

All project-specific values live in **`gradle.properties`** at the workspace root and are
the single source of truth. Gradle reads them natively; bnd imports the same file via
`-include: ${workspace}/gradle.properties` in `cnf/ext/fennec.bnd`.

Edit these three entries in `gradle.properties`:

| Property            | Meaning                          | Example                          |
|---------------------|----------------------------------|----------------------------------|
| `github_org`        | GitHub organisation              | `eclipse-fennec`                 |
| `github_repository` | GitHub repository / project name | `org.eclipse.fennec.myproject`   |
| `maven_group_id`    | Maven group id (must start with `org.eclipse.fennec`) | `org.eclipse.fennec` |

These feed:

- **Gradle**: `rootProject.name` (settings.gradle) and the Sonar `projectName` /
  `projectKey` / `organization` (build.gradle).
- **bnd** (`cnf/ext/fennec.bnd`): `github-project`, `github-orga` and `-groupid`.

Optionally adjust `base-version` in `cnf/ext/fennec.bnd` for your project's version.

## Libraries

`cnf/ext/fennec.bnd` enables `-library: fennec, fennecTest, fennecJacoco`, resolved from
Maven Central via `cnf/ext/central.mvn`.
