import DefaultTheme from 'vitepress/theme'
import type { Theme } from 'vitepress'
import { h } from 'vue'
import EclipseFooter from './EclipseFooter.vue'
import './custom.css'

export default {
  extends: DefaultTheme,
  // The built-in themeConfig.footer is suppressed on pages that render a
  // sidebar — which is every /guides/ page — so the Eclipse Foundation footer
  // goes into the layout-bottom slot instead. That slot is rendered on every
  // page. custom.css hides VitePress' own footer so the two never stack.
  Layout: () =>
    h(DefaultTheme.Layout, null, {
      'layout-bottom': () => h(EclipseFooter),
    }),
} satisfies Theme
