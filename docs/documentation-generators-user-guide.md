# Ecore documentation generators

> **Status: v1.** Migrated into this workspace from `geckoprojects.org`; implemented and
> unit-tested, the API may still evolve.

Turn an Ecore model into **human-readable documentation**: a Markdown document or an HTML page
describing an `EPackage` (or a single classifier), optionally with a **class diagram** in
[Mermaid](https://mermaid.js.org/) or [PlantUML](https://plantuml.com/) syntax. The generators are
plain Java (compiled from [Xtend](https://eclipse.dev/Xtext/xtend/) templates) and need nothing
but EMF; an optional OSGi layer registers them as services selected by media type.

- **Input:** any `EPackage` or `EClassifier` — a registered model, a dynamic one, anything EMF
  hands you. No `.ecore` file and no code generation involved.
- **Output:** a `CharSequence` you can serve, store or write out, plus (if you want it) a file on
  disk.

## Quick start

Plain Java, straight from the format you want:

```java
MarkdownCodeGen generator = new MarkdownCodeGen();

CharSequence doc = generator.generateDocumentation(
        ePackage, EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM);

System.out.println(doc);   // # Package: zoo … ```mermaid classDiagram … ### Class: Animal …
```

In OSGi, take the service for the media type you serve and ask it the same question:

```java
@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
private volatile List<EcoreToDocumentationService> generators;

EcoreToDocumentationService service = generators.stream()
        .filter(g -> g.canHandleMediaType("text/markdown"))
        .findFirst().orElseThrow();

CharSequence doc = service.generateDocumentation(ePackage,
        EcoreToDocumentationOptions.MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM);
```

`generateDocumentation` never touches the filesystem — a request handler can answer with the
result directly. If you *do* want files, see [Writing files](#writing-files).

## What is generated

For an `EPackage`, both formats produce the same structure:

- a heading for the package, and — in a diagram mode — one class diagram of the whole package;
- a **Classes Overview**: per `EClass` its documentation, optionally its own class diagram, and
  two tables, *Fields* (the `EAttribute`s) and *References* (the `EReference`s), each with name,
  type, bounds (`lower..upper`, `-1` meaning unbounded) and description;
- an **Enumerators Overview**: per `EEnum` its documentation and a table of its literals.

Anything without documentation reads `None.`, as does an empty table.

The **description** of a model element is the `documentation` detail of its first `EAnnotation`
that has one — the annotation EMF itself writes (source
`http://www.eclipse.org/emf/2002/GenModel`), so a model documented in the Ecore editor carries its
text straight into the generated document.

For a single `EClassifier` the same section is generated on its own, without the package frame
(the HTML generator still wraps it in a complete page).

### Class diagrams

`MermaidClassDiagramCodeGen` and `PlantumlClassDiagramCodeGen` render the diagram, and can also be
used on their own (`generateClassDiagram(ePackage)`):

| | Mermaid | PlantUML |
|---|---|---|
| classes | `class Animal { … }` | `class Animal { … }` |
| abstract / interface | `<<abstract>>` / `<<interface>>` stereotype | `abstract class` / `interface` keyword |
| enums | `<<enumeration>>` + literals | `enum` + literals |
| attributes | `EInt age` | `age : EInt` |
| references | `Zoo --> "1..*" Animal : animals` | same |
| reference into another package | `..>` to the type name | `..>` plus a grey `package "<nsURI>"` box |
| super types | `Animal ..> Named` | same |
| documentation | not rendered | `note top of Animal`, wrapped after ~30 characters |

Multiplicity follows the feature's bounds: `0..*` prints as `*`, `1..*` as `1..*`, anything else
as `lower..upper`. Containment is **not** distinguished from a plain reference.

## Output modes

`EcoreToDocumentationOptions` spans every format, so not every value makes sense for every
generator. Ask the service what it supports — or let it tell you by rejecting the rest:

| Mode | Markdown | HTML |
|---|---|---|
| `ONLY_MARKDOWN_CLASS_OVERVIEW` | ✔ no diagram | — |
| `MARKDOWN_WITH_MERMAID_CLASS_DIAGRAM` | ✔ ` ```mermaid ` fence | — |
| `MARKDOWN_WITH_PLANTUML_CLASS_DIAGRAM` | ✔ ` ```plantuml ` fence | — |
| `ONLY_HTML_CLASS_OVERVIEW` | — | ✔ no diagram |
| `HTML_WITH_MERMAID_CLASS_DIAGRAM` | — | ✔ `<div class='mermaid'>` |
| `HTML_WITH_PLANTUML_CLASS_DIAGRAM` | — | ✖ rejected, see [below](#why-there-is-no-html--plantuml) |

```java
service.getSupportedModes();        // what this generator can produce
service.requireSupported(mode);     // IllegalArgumentException if it cannot
```

A mode outside that set is **rejected with an `IllegalArgumentException`**, naming the mode and the
generator. It is never answered with a document that silently lacks the diagram you asked for, and
it can no longer be written into another mode's file.

### Why there is no HTML + PlantUML

The constant exists because the enum spans both formats, not because the combination was dropped
by accident. The two diagram languages reach a reader differently:

- **Mermaid renders itself in the browser.** The generated page already loads
  `mermaid.min.js`, so a Mermaid diagram in a `<div class='mermaid'>` becomes a picture with no
  further machinery.
- **PlantUML in Markdown is rendered by whoever reads the Markdown** — GitLab, Confluence, an IDE
  preview. The ` ```plantuml ` fence is a message to that tool.

An HTML page has no such downstream renderer: the page *is* the renderer. Rendering PlantUML there
needs either a second, much heavier client-side stack (the TeaVM build of PlantUML plus a Graphviz
layout engine compiled to WebAssembly — megabytes, to draw the diagram Mermaid already draws), a
server-side PlantUML with Graphviz inside a bundle whose premise is "no toolchain", or an
`<img src="…plantuml.com/plantuml/svg/…">` that ships your model's class and attribute names to a
third-party server every time someone opens the page. None of those is a fix for a missing
`if`-branch; all three are product decisions.

So HTML documentation offers Mermaid, and asking it for PlantUML fails fast. If the combination is
ever wanted, adding the value to `getSupportedModes()` is the whole change at the service
boundary.

## Writing files

`doGenerateDocumentation` generates, writes a file **and** returns the document as UTF-8 bytes:

```java
OutputStream generated = service.doGenerateDocumentation(ePackage, mode, "/var/docs");
```

- The path is `<outputFolderRoot>/<folder for the mode>/<name><extension>`, with missing
  directories created on the way.
- **Names:** a package becomes `<package>.md`, a classifier `<package>_<Classifier>.md` (`.html`
  for HTML).
- **Folders:** Markdown writes to `md`, `md_mermaid`, `md_plantuml`; HTML to `html`,
  `html_mermaid` — one per mode, so generating several modes into one root does not make them
  overwrite each other.
- The file is written in **UTF-8**, in one call, replacing whatever was there. Two callers
  generating the same package concurrently therefore both get a complete document; what they
  cannot do is write *different* content to one path and expect a winner.

`generateOutputFile(…)` / `doCreateOutputFile(…)` are available if you want the naming and
placement but your own content.

## Bundles

| Bundle | Contains |
|---|---|
| `org.eclipse.fennec.emf.util.documentation.generators.apis` | `EcoreToDocumentationService`, `EcoreToDocumentationCodeGenerator`, `EcoreToClassDiagramCodeGenerator`, `EcoreToDocumentationOptions` |
| `…generators.markdown` | `MarkdownCodeGen` |
| `…generators.html` | `HtmlCodeGen` |
| `…generators.mermaid` | `MermaidClassDiagramCodeGen` |
| `…generators.plantuml` | `PlantumlClassDiagramCodeGen` |
| `…generators.markdown.component` | the `EcoreToDocumentationService` for `text/markdown` (implementation package is private) |
| `…generators.html.component` | the `EcoreToDocumentationService` for `text/html` |

The generator bundles are plain Java and usable without OSGi. The two `.component` bundles contain
nothing but a Declarative Services component each, registered as `EcoreToDocumentationService`;
pick between them with `canHandleMediaType(…)`.

## Requirements & dependencies

- **Java 21** and EMF (`org.eclipse.emf.ecore`).
- The generators are compiled Xtend, so they need the **Xtend runtime**
  (`org.eclipse.xtext.xbase.lib`, which in turn needs **Guava**) on the classpath. In OSGi that is
  an ordinary `Import-Package`; outside OSGi, put both on your classpath.
- An HTML page fetches `mermaid.min.js` and `md-block.js` from public CDNs when it is **opened**;
  generating it needs no network. A page viewed offline shows its Markdown sections as plain text
  and no rendered diagram.

## Scope / limitations (v1)

- Only `EClass` and `EEnum` are documented. Other classifiers (an `EDataType` that is not an enum)
  are skipped silently.
- A generator instance is **not thread-safe** — it keeps the diagram flavour of the current call in
  a field. Use one per call (the OSGi components do); the `EcoreToDocumentationService`
  implementations themselves are stateless and safe to share.
- Diagrams show names only: no containment marker, no operations, no attribute cardinalities, and
  a reference into another package is drawn by type name (Mermaid cannot link across diagrams).
- The deprecated `EClass` overloads of `generateDocumentation`/`doGenerateDocumentation` are kept
  and delegate to the `EClassifier` ones — same output, byte for byte. New code should use the
  `EClassifier` overloads.
- There is no service property carrying the media type, so selecting a generator means calling
  `canHandleMediaType(…)` rather than filtering on the service registry.
