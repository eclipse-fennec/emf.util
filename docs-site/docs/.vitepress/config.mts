import { defineConfig } from 'vitepress'
import { GUIDES, EXAMPLES } from '../../guides.mjs'

// Per-project docs are served under a versioned sub-path, matching the org
// convention (https://eclipse-fennec.github.io/<repo>/<version>/). The snapshot
// branch publishes to /emf.util/snapshot/; tagged releases / `latest` get added
// once the first release lands.
const version = process.env.DOCS_BRANCH || 'snapshot'
const base = `/emf.util/${version}/`

// Canonical published origin. Links that point OUTSIDE the current docs base
// (other doc versions) must be full URLs — VitePress auto-prepends `base` to any
// root-absolute (`/…`) link, which would otherwise double the path.
const SITE = 'https://eclipse-fennec.github.io/emf.util'

// Version selector. Only `snapshot` is deployed today; keep as data so adding
// `latest` and tagged versions later is a one-liner.
const versions = [{ text: 'snapshot', link: `${SITE}/snapshot/` }]

const guideItems = GUIDES.map((g) => ({ text: g.title, link: `/guides/${g.slug}` }))
const exampleItems = EXAMPLES.map((g) => ({ text: g.title, link: `/examples/${g.slug}` }))

export default defineConfig({
  title: 'Fennec EMF Util',
  description:
    'Utilities and commons for Fennec EMF OSGi — small, focused libraries around the Eclipse Modeling Framework.',
  lang: 'en-US',
  base,
  cleanUrls: true,
  lastUpdated: true,
  ignoreDeadLinks: true,

  markdown: {
    // Shiki has no dedicated 'gradle' grammar; Gradle build files are Groovy.
    languageAlias: { gradle: 'groovy' },
  },

  head: [
    ['link', { rel: 'icon', type: 'image/png', href: `${base}fennec-logo.png` }],
    ['meta', { name: 'theme-color', content: '#c0631c' }],
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:title', content: 'Fennec EMF Util' }],
    [
      'meta',
      {
        property: 'og:description',
        content:
          'Utilities and commons for Fennec EMF OSGi — small, focused EMF libraries.',
      },
    ],
  ],

  themeConfig: {
    logo: '/fennec-logo.png',
    siteTitle: 'Fennec EMF Util',

    nav: [
      { text: 'Home', link: '/' },
      { text: 'User Manual', items: guideItems },
      { text: 'Examples', items: exampleItems },
      { text: `version: ${version}`, items: versions },
    ],

    sidebar: {
      '/guides/': [{ text: 'User Manual', items: guideItems }],
      '/examples/': [{ text: 'Examples', items: exampleItems }],
    },

    socialLinks: [{ icon: 'github', link: 'https://github.com/eclipse-fennec/emf.util' }],

    search: { provider: 'local' },

    editLink: {
      pattern: 'https://github.com/eclipse-fennec/emf.util/edit/main/docs/:path',
      text: 'Edit this page on GitHub',
    },

    footer: {
      message:
        'Released under the EPL-2.0 License. Eclipse Fennec is part of the Eclipse Foundation.',
      copyright: 'Copyright © Eclipse Foundation and contributors',
    },
  },
})
