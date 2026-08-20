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
    @Reference(target = "(repo=git@github.com:eclipse-fennec/emf.util.git)")
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
| `privateKey` | `""` | Path to an SSH private key. SSH remotes only; empty means no key. |
| `privateKeyPassphrase` | `""` | Passphrase of that key, if it is encrypted. |
| `knownHosts` | `""` | Path to an OpenSSH `known_hosts` for host-key verification; empty falls back to `~/.ssh/known_hosts`. |
| `username` | `""` | User name for an `http(s)://` remote. |
| `password` | `""` | Password or access token for that user. |

With the OSGi configurator (see the `org.eclipse.fennec.jgit.config` bundle for a working
example), reading the secrets from the environment:

```json
{
  ":configurator:resource-version": 1,

  "GitConfig~models": {
    "repo": "git@github.com:eclipse-fennec/emf.util.git",
    "branch": "main",
    "privateKey": "$[env:SSH_PRIVATE_KEY;default=]",
    "privateKeyPassphrase": "$[env:SSH_PRIVATE_KEY_PWD;default=]",
    "knownHosts": "$[env:SSH_KNOWN_HOSTS;default=]"
  }
}
```

> Every property except `repo` is optional. An **empty `privateKey` means no key**, which is
> what the substitution above yields when the variable is unset: the SSH stack is then not
> built at all, and anonymous `git://`, `http(s)://` and on-disk repositories work without
> configuring anything SSH-related.

### Remote or on disk

The `repo` value decides which of two quite different modes the service runs in:

| `repo` looks like | What the service does |
|---|---|
| `git://…`, `git@host:path`, `ssh://…`, `http://…`, `https://…` | Builds an **in-memory mirror** and fetches the branch into it on activation. `fetch()` refreshes it, `push()` sends commits to the remote. |
| anything else | Opens the repository **on disk** at that path (relative paths resolve against the process working directory). `fetch()` and `push()` are no-ops — there is no remote. |

The scheme has to match in full: a directory named `gitmodels` is a directory, not a URL.

The path has to *be* the repository: either a bare repository, or a working tree with its
`.git` directly in it. Nothing above it is considered, and a path that is neither makes
activation fail with a message naming the resolved path:

```
Configured repo 'models' resolves to /srv/fennec/models, which is not a git repository:
the directory does not exist. Create one with 'git init --bare /srv/fennec/models', point
repo at an existing repository, or use a remote URL.
```

The search is bounded on purpose. Walking up the file system would find the repository that
*contains* a mistyped or not-yet-created path — the service would come up looking healthy and
write every commit into that repository instead.

The mirror holds no working tree at all; it is the object database plus the branch ref. That is
what makes the same code work for both modes, and it is why writing behaves as described below.
A fetch lands in remote-tracking refs (`refs/remotes/origin/<branch>`) and the configured branch
follows from there, so `getBranches()` shows the remote's branches next to the local one.

## Reading

```java
TreeResult tree = git.getFiles();              // every file of the branch head
TreeResult models = git.getFiles("models");    // only below that prefix
tree.getCommitId();                            // the commit the listing came from
tree.getFiles();                               // repository-relative paths
tree.getEntries();                             // the same, as (path, blobId) records
tree.getBlobId("models/sensor.ecore");         // that file's blob id, or null

git.exists(null, "models/sensor.ecore");       // without reading the content
git.blobId(null, "models/sensor.ecore");       // Optional<String>

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
a file either, and is reported the same way. `exists(commitId, path)` answers the same question
without reading anything and without an exception, which is what a store checking before every
read or delete wants.

A **branch with no commits** — a repository someone just created, the state of a first boot — is
empty, not broken: `getFiles()` returns an empty listing whose `getCommitId()` is `null`,
`getLog()` is empty, `exists(…)` is `false`, and a read reports the file as missing. The first
commit written to it is simply parentless.

For a remote, reads are served from the mirror as it was last fetched. Call `fetch()` to catch
up with the remote; nothing else refreshes it.

```java
git.fetch();
TreeResult current = git.getFiles();
```

### Blob ids

Every listed file carries the id of the blob holding its content — git's own content hash,
computed while the tree is walked, so it costs nothing extra:

```java
String etag = git.blobId(null, path).orElseThrow();
```

It identifies **that file's content and nothing else**, which makes it the value to use for an
HTTP `ETag`, for change detection between two commits, and for deduplication. A commit id is not
a substitute: it changes on every commit, so using it as an ETag invalidates every cached
representation whenever any unrelated file is written.

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

**A request that changes nothing writes no commit.** Storing content that is already stored, or
deleting a path that is not there, leaves the branch where it is and returns the *unchanged*
head — the way `git commit` refuses a commit without `--allow-empty`. Otherwise every idempotent
write would add a history entry that records no change and move the tip, which defeats both an
audit trail and change detection based on comparing tips. A caller that wants the entry anyway
asks for it:

```java
git.commit(CommitRequest.builder("nightly checkpoint")
        .put("models/sensor.ecore", unchangedBytes)
        .allowEmpty(true)
        .build());
```

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
and you get a **`GitConflictException`**. The service does not reconcile on its own — only the
caller knows how to merge the content. The recovery is fetch, reconcile, reset, re-apply:

```java
try {
    git.push();
} catch (GitConflictException e) {
    git.fetch();                                       // your commit is still there
    String theirs = git.getRemoteHead();               // what the remote has now
    byte[] merged = reconcile(git.readFile(theirs, path), myContent);
    git.resetToRemote();                               // give up the commit that lost
    git.writeFile(path, merged, "republish after remote change");
    git.push();
}
```

**`fetch()` never discards local work.** It updates the remote-tracking refs and lets the branch
follow only while that is a fast-forward; a branch carrying unpushed commits is left where it
is, and the divergence is logged. Both sides are then readable — yours through the normal read
methods, the remote's through `readFile(getRemoteHead(), path)` — so you can decide before
anything is given up.

A push that fails for any other reason — the remote is unreachable, the credentials are
rejected — raises **`GitPushException`**. It is worth distinguishing from a plain
`GitWriteException`: the commit is already written and on the branch, so the work is not lost
and pushing again is all that is needed. A `GitWriteException` from `commit()` means the
opposite, that nothing was recorded at all.

**`resetToRemote()`** is the one operation that throws local commits away, and it says so in its
name: it moves the branch to the remote's copy of it as of the last fetch. Nothing else in this
service does that. On a repository on disk both are no-ops, as `push()` is.

The same `GitConflictException` is raised if the branch moves underneath a commit locally: a
commit is always built on the head that was current when it started, and the ref is only moved
if it is still there. That one is a local race, so the way out is to re-read the head and
re-apply the changes — not to fetch.

## Errors

All of these are unchecked and live in `org.eclipse.fennec.jgit.exceptions`.

| Exception | Raised when |
|---|---|
| `GitFileNotFoundException` | A requested file is not in the tree of that commit. |
| `GitConflictException` | The remote rejected the push as non-fast-forward (fetch, reconcile, `resetToRemote()`, re-apply), or the branch moved underneath a commit locally (re-read the head and re-apply). |
| `GitPushException` | The commit was written but could not be sent to the remote. The change is recorded locally and a later `push()` completes it — retrying is cheap. |
| `GitWriteException` | The commit itself could not be written, so **nothing** was recorded. `GitConflictException` and `GitPushException` both extend it, so an existing catch block still catches everything. |

## Bundles & dependencies

| Bundle | Contains |
|---|---|
| `org.eclipse.fennec.jgit` | The `GitService` API (`…jgit.api`), the exceptions (`…jgit.exceptions`) and the DS component implementing them. |
| `org.eclipse.fennec.jgit.config` | An example configuration bundle (resource-only, OSGi configurator) plus a `launch.bndrun`. |
| `org.eclipse.fennec.jgit.test` | OSGi integration tests: local repository, anonymous `git://` and SSH transports. |

Requires Java 21 and `org.eclipse.jgit`. Everything SSH is **optional** and needed only for
`ssh://` and `git@host:path` remotes: `org.eclipse.jgit.transport.sshd` is an optional import
and the sshd types live in a class that is not loaded unless a `privateKey` is configured, so a
repository on disk or an `http(s)://` remote works without shipping any of it. Configuring a key
while it is absent fails with an explanation, not with a `NoClassDefFoundError`.

### Running against an SSH remote

Because the whole chain hangs off optional imports, an OSGi resolver leaves all of it out unless
it is **asked for by name** — `bnd.identity;id='org.eclipse.jgit.ssh.apache'` (which pulls in
`org.apache.sshd.osgi`, `org.apache.sshd.sftp` and `bcprov`) in the `-runrequires` of the bndrun.
All of it is published by the `fennecUtil` workspace library, so consumers of that library only
have to require it, not hunt for coordinates.

| Also needed | When |
|---|---|
| `bcpkix` + `bcutil` | Only for a private key in **encrypted PKCS#8** form (`-----BEGIN ENCRYPTED PRIVATE KEY-----`). MINA parses OpenSSH-format keys (encrypted or not) and classic PEM itself, but hands PKCS#8 decryption to BouncyCastle's `org.bouncycastle.pkcs`, which is not in `bcprov`. Missing, it fails at key load with `NoClassDefFoundError: org/bouncycastle/pkcs/PKCSException`. |
| `org.osgi.framework.bootdelegation=javax.*` | Always, for SSH. `org.eclipse.jgit.ssh.apache` uses `javax.security.auth.*` without importing it, expecting it on the boot class path; Felix boot-delegates only `java.*` by default. |
| SPI-Fly (`org.apache.aries.spifly.dynamic.bundle` + ASM) | Always, for SSH. JGit and MINA sshd find their providers through `ServiceLoader`. |

Both `org.eclipse.fennec.jgit.config/launch.bndrun` and the OSGi test's `test.bndrun` are
worked examples of the whole list.

Unlike the other utilities in this workspace, the implementation is a private package: the
service is consumed through OSGi, not constructed directly.

## Scope / limitations

- **One branch per configuration.** No branch creation, checkout or merge; configure a second
  service for a second branch.
- **No merge or rebase.** Conflicting writes are reported, not resolved; reconciling content and
  calling `resetToRemote()` is the caller's decision.
- **The working tree of an on-disk repository is never updated** by a commit (see above).
- **`file://` URLs are not recognised** as remotes — use the plain path instead.
- Reads of a remote are served from the last `fetch()`; there is no polling or background
  refresh.
- **The mirror of a remote grows with everything written into it.** It is an in-heap object
  database with no `git gc`, so a long-running writer holds every object it has committed since
  it started. For write-heavy use prefer a repository on disk ([#47]).

[#47]: https://github.com/eclipse-fennec/emf.util/issues/47
