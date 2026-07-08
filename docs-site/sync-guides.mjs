// Sync the curated, user-facing pages from ../docs into ./docs/{guides,examples}
// for VitePress.
//
// Single source of truth stays <repo>/docs/. Publication is an explicit ALLOWLIST
// (see guides.mjs) — internal dev docs (architecture, test plans, security
// analyses) are deliberately NOT published. Cross-links inside published pages
// that point at a NON-published doc are rewritten to the GitHub blob URL so they
// keep working instead of 404-ing on the site.
import { readFileSync, writeFileSync, mkdirSync, rmSync } from 'node:fs';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { GUIDES, EXAMPLES } from './guides.mjs';

const here = dirname(fileURLToPath(import.meta.url));
const srcDir = join(here, '..', 'docs'); // <repo>/docs — source of truth
const contentRoot = join(here, 'docs'); // VitePress content root

// Branch/ref used for the GitHub blob fallback links (internal docs are browsed
// on GitHub, not published). Passed by CI; defaults to main for local builds.
const branch = process.env.DOCS_BRANCH || 'main';
const blobBase = `https://github.com/eclipse-fennec/emf.util/blob/${branch}/docs`;

const sections = [
  { items: GUIDES, dir: 'guides' },
  { items: EXAMPLES, dir: 'examples' },
];

// A page counts as "published" (internal link -> sibling route) if it appears in
// ANY section; otherwise the link falls back to the GitHub blob URL.
const published = new Map();
for (const { items, dir } of sections) {
  for (const g of items) published.set(g.file, { dir, slug: g.slug });
}

// Rewrite ](target.md...) links: published -> route, others -> GitHub blob.
function rewriteLinks(md) {
  return md.replace(/\]\((\.?\/?)([a-z0-9-]+)\.md(#[^)]*)?\)/gi, (m, _prefix, name, anchor = '') => {
    const file = `${name}.md`;
    if (published.has(file)) {
      const { dir, slug } = published.get(file);
      return `](/${dir}/${slug}${anchor})`;
    }
    return `](${blobBase}/${file}${anchor})`;
  });
}

let total = 0;
for (const { items, dir } of sections) {
  const outDir = join(contentRoot, dir);
  rmSync(outDir, { recursive: true, force: true });
  mkdirSync(outDir, { recursive: true });
  for (const g of items) {
    const md = rewriteLinks(readFileSync(join(srcDir, g.file), 'utf8'));
    writeFileSync(join(outDir, `${g.slug}.md`), md, 'utf8');
    console.log(`synced ${g.file} -> ${dir}/${g.slug}.md`);
    total++;
  }
}

console.log(`Done. ${total} pages (branch=${branch}).`);
