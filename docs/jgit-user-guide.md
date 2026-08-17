# Git repositories (JGit)

> **Status: prototype.** Reading and writing are implemented and covered by plain-JUnit and
> OSGi integration tests, but the API is still settling and may change without a version bump.

Read from and write to a git repository — one on disk or one on a remote server — through an
OSGi service, without a working tree and without shelling out to `git`. Backed by
[JGit](https://www.eclipse.org/jgit/).

- **Read:** list the files of a commit, read their content, list branches and history.
- **Write:** commit one or many files (or delete them) in a single commit, then push.
- **Transports:** anonymous `git://`, SSH with a private key, and `http(s)://` with a token.

The typical use is a service that keeps models, configuration or mappings in a git repository
and needs to load them at runtime and write changes back.

## Quick start

A `GitService` is created per configuration, so first configure one (factory PID `GitConfig`),
then have it injected:

```java
@Component
public class ModelStore {

    // Configuration properties are propagated as service properties, so a target filter
    // picks one repository when several are configured.
    @Reference(target = "(repo=git@github.com:de-jena/upd-models.git)")
    private GitService git;

    public byte[] loadModel(String path) throws IOException {
        try (InputStream in = git.readLatestFile(path)) {   // "models/sensor.ecore"
            return in.readAllBytes();
        }
    }

    public void publishModel(String path, byte[] content) {
        git.writeFile(path, content, "publish " + path);    // commits locally
        git.push();                                         // sends it to the remote
    }
}
```

## Configuring

The component is a **factory component** (`configurationPolicy = REQUIRE`, factory PID
`GitConfig`): every configuration produces one `GitService` service. Nothing is registered
until a configuration exists.

| Property | Default | Meaning |
|---|---|---|
| `repo` | — | Repository URL or path on disk. Decides everything below (see next section). |
| `branch` | `main` | The branch the service reads from and writes to. |
| `authorName` | `Fennec Git Service` | Author/committer recorded on commits. |
| `authorEmail` | `fennec@eclipse.org` | Author/committer e-mail. |
| `pushOnCommit` | `false` | Push after every commit instead of on an explicit `push()`. |
| `privateKey` | — | Path to an SSH private key. SSH remotes only. |
| `privateKeyPassphrase` | — | Passphrase of that key, if it is encrypted. |
| `knownHosts` | `""` | Path to an OpenSSH `known_hosts` for host-key verification; empty falls back to `~/.ssh/known_hosts`. |
| `username` | `""` | User name for an `http(s)://` remote. |
| `password` | `""` | Password or access token for that user. |

With the OSGi configurator (see the `org.eclipse.fennec.jgit.config` bundle for a working
example), reading the secrets from the environment:

```json
{
  ":configurator:resource-version": 1,

  "GitConfig~models": {
    "repo": "git@github.com:de-jena/upd-models.git",
    "branch": "main",
    "privateKey": "$[env:SSH_PRIVATE_KEY;default=]",
    "privateKeyPassphrase": "$[env:SSH_PRIVATE_KEY_PWD;default=]",
    "knownHosts": "$[env:SSH_KNOWN_HOSTS;default=]"
  }
}
```

> `privateKey` and `privateKeyPassphrase` are declared without a default, so a metatype-driven
> configuration UI marks them as required. Leave them out entirely for anonymous and `http(s)`
> remotes — the SSH stack is only built when a key is configured.

### Remote or on disk

The `repo` value decides which of two quite different modes the service runs in:

| `repo` looks like | What the service does |
|---|---|
| `git://…`, `git@host:path`, `ssh://…`, `http://…`, `https://…` | Builds an **in-memory mirror** and fetches the branch into it on activation. `fetch()` refreshes it, `push()` sends commits to the remote. |
| anything else | Opens the repository **on disk** at that path (relative paths resolve against the process working directory). `fetch()` and `push()` are no-ops — there is no remote. |

The scheme has to match in full: a directory named `gitmodels` is a directory, not a URL.

The mirror holds no working tree at all; it is the object database plus the branch ref. That is
what makes the same code work for both modes, and it is why writing behaves as described below.

## Reading

```java
TreeResult tree = git.getFiles();              // every file of the branch head
TreeResult models = git.getFiles("models");    // only below that prefix
tree.getCommitId();                            // the commit the listing came from
tree.getFiles();                               // repository-relative paths

try (InputStream in = git.readLatestFile("models/sensor.ecore")) { … }
git.loadLatestFile("models/sensor.ecore", outputStream);

// The same, as of a particular commit rather than the branch head:
try (InputStream in = git.readFile(commitId, "models/sensor.ecore")) { … }
git.loadFile(commitId, "models/sensor.ecore", outputStream);

git.getBranches();   // ["refs/heads/main"]
git.getLog();        // Iterable<RevCommit>, newest first
git.getBranch();     // the configured branch
git.getRef();        // "refs/heads/<branch>"
git.getGitUrl();     // the configured repo
```

A file that is not in the tree raises **`GitFileNotFoundException`** — an absent file and an
empty file are different things, and the read methods distinguish them. A directory path is not
a file either, and is reported the same way.

For a remote, reads are served from the mirror as it was last fetched. Call `fetch()` to catch
up with the remote; nothing else refreshes it.

```java
git.fetch();
TreeResult current = git.getFiles();
```

## Writing

A commit is built directly against the object database: the index is assembled in memory from
the tree of the current branch head, the changes are applied to it, and the branch ref is moved
to the new commit. No working tree is involved.

**Consequence for a repository on disk:** the branch moves but the working tree and the index
are *not* updated, so a `git status` in that directory will show it as behind. If you need the
files on disk to follow, check the branch out yourself afterwards.

```java
// One file.
String commitId = git.writeFile("models/sensor.ecore", bytes, "publish sensor model");
String fromStream = git.writeFile("models/sensor.ecore", inputStream, "…");   // read and closed
String removed = git.deleteFile("models/obsolete.ecore", "drop obsolete model");

// Several changes, one commit — the usual case when saving a whole resource set.
String batch = git.commit(CommitRequest.builder("republish models")
        .put("models/sensor.ecore", sensorBytes)
        .put("models/gateway.ecore", gatewayBytes)
        .delete("models/obsolete.ecore")
        .deleteTree("models/legacy")
        .build());
```

Each call returns the id of the new commit. Changes are applied in the order they were added, so
a later change to the same path wins. Deleting something that is not there is not an error.

The author comes from the configuration; a single commit can override it:

```java
git.commit(CommitRequest.builder("imported by hand")
        .put("models/sensor.ecore", bytes)
        .author("Erika Mustermann", "em@example.com")
        .build());
```

## Pushing

Committing and pushing are **two separate calls** by default: a commit is local until `push()`
sends the branch to the remote. That lets you build up several commits and publish them
together, and it keeps a failing network call out of the commit path.

```java
git.writeFile("models/a.ecore", a, "add a");
git.writeFile("models/b.ecore", b, "add b");
git.push();                                    // both commits, one transfer
```

Two ways to change that:

```java
// Configuration: push after every commit.
"pushOnCommit": true

// Per commit, either way, regardless of the configured default.
git.commit(CommitRequest.builder("urgent").put(path, bytes).push(true).build());
git.commit(CommitRequest.builder("later").put(path, bytes).push(false).build());
```

`push()` on a repository on disk is a no-op, so a caller does not have to know which kind of
repository it was configured against.

### When the remote has moved on

If the remote carries commits the mirror does not have, it rejects the push as non-fast-forward
and you get a **`GitConflictException`**. The service does not retry on its own — only the
caller knows how to reconcile the content. The recovery is fetch, rebuild, push:

```java
try {
    git.push();
} catch (GitConflictException e) {
    git.fetch();                      // mirror catches up with the remote
    byte[] merged = reconcile(git.readLatestFile(path), myContent);
    git.writeFile(path, merged, "republish after remote change");
    git.push();
}
```

`fetch()` overwrites the mirror's branch with the remote's, so a commit that lost the race is
discarded — read what you need out of it before fetching, or rebuild it afterwards as above.

The same exception is raised if the branch moves underneath a commit locally: a commit is always
built on the head that was current when it started, and the ref is only moved if it is still
there.

## Errors

All of these are unchecked and live in `org.eclipse.fennec.jgit.exceptions`.

| Exception | Raised when |
|---|---|
| `GitFileNotFoundException` | A requested file is not in the tree of that commit. |
| `GitConflictException` | The branch moved concurrently, or the remote rejected the push as non-fast-forward. Fetch and retry. |
| `GitWriteException` | Any other failure while committing or pushing. `GitConflictException` extends it. |

## Bundles & dependencies

| Bundle | Contains |
|---|---|
| `org.eclipse.fennec.jgit` | The `GitService` API (`…jgit.api`), the exceptions (`…jgit.exceptions`) and the DS component implementing them. |
| `org.eclipse.fennec.jgit.config` | An example configuration bundle (resource-only, OSGi configurator) plus a `launch.bndrun`. |
| `org.eclipse.fennec.jgit.test` | OSGi integration tests: local repository, anonymous `git://` and SSH transports. |

Requires Java 21, `org.eclipse.jgit` and — for SSH remotes — `org.eclipse.jgit.ssh.apache`
with Apache MINA sshd, which parses OpenSSH-format and ed25519 keys. Unlike the other utilities
in this workspace, the implementation is a private package: the service is consumed through
OSGi, not constructed directly.

## Scope / limitations

- **One branch per configuration.** No branch creation, checkout or merge; configure a second
  service for a second branch.
- **No merge or rebase.** Conflicting writes are reported, not resolved.
- **The working tree of an on-disk repository is never updated** by a commit (see above).
- **`file://` URLs are not recognised** as remotes — use the plain path instead.
- Reads of a remote are served from the last `fetch()`; there is no polling or background
  refresh.
