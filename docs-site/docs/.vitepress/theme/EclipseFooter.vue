<script setup lang="ts">
import { withBase } from 'vitepress'

const year = new Date().getFullYear()
</script>

<template>
  <footer class="eclipse-footer">
    <div class="footer-inner">
      <div class="footer-top">
        <a
          href="https://www.eclipse.org"
          target="_blank"
          rel="noreferrer"
          class="ef-logo-link"
        >
          <img
            class="ef-logo ef-logo-light"
            :src="withBase('/eclipse-foundation-grey-orange.svg')"
            alt="Eclipse Foundation"
          />
          <img
            class="ef-logo ef-logo-dark"
            :src="withBase('/eclipse-foundation-white-orange.svg')"
            alt="Eclipse Foundation"
          />
        </a>
        <nav class="ef-links" aria-label="Eclipse Foundation legal links">
          <a href="https://www.eclipse.org" target="_blank" rel="noreferrer">
            Eclipse Foundation
          </a>
          <a href="https://www.eclipse.org/legal/privacy/" target="_blank" rel="noreferrer">
            Privacy Policy
          </a>
          <a href="https://www.eclipse.org/legal/terms-of-use/" target="_blank" rel="noreferrer">
            Terms of Use
          </a>
          <a href="https://www.eclipse.org/legal/compliance/" target="_blank" rel="noreferrer">
            Compliance
          </a>
          <a href="https://www.eclipse.org/legal/" target="_blank" rel="noreferrer">
            Legal Resources
          </a>
        </nav>
      </div>
      <div class="footer-bottom">
        <p>
          Copyright © {{ year }} Contributors to the Eclipse Foundation.
          Released under the Eclipse Public License 2.0.
        </p>
        <p class="trademark">
          Eclipse Fennec™ and the Eclipse Fennec project logo are trademarks of the
          Eclipse Foundation. Eclipse® and the Eclipse logo are registered trademarks
          of the Eclipse Foundation.
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.eclipse-footer {
  border-top: 1px solid var(--vp-c-gutter);
  background: var(--vp-c-bg);
  padding: 32px 24px;
}
.footer-inner {
  max-width: 1152px;
  margin: 0 auto;
}
.footer-top {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 16px;
}
.ef-logo {
  height: 42px;
  width: auto;
}
/* VitePress puts the .dark class on <html>, so the theme-dependent selectors
   stay scoped — `:global(.dark) .ef-logo-light` would silently compile down to
   a bare `.dark` and drop the descendant part. */
.ef-logo-dark {
  display: none;
}
html.dark .ef-logo-light {
  display: none;
}
html.dark .ef-logo-dark {
  display: block;
}
.ef-links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 24px;
}
.ef-links a {
  font-size: 14px;
  font-weight: 500;
  color: var(--vp-c-text-2);
  transition: color 0.25s;
}
.ef-links a:hover {
  color: var(--vp-c-brand-1);
}
.footer-bottom {
  font-size: 13px;
  line-height: 1.6;
  color: var(--vp-c-text-3);
}
.footer-bottom p {
  margin: 0;
}
.trademark {
  margin-top: 4px;
}
@media (max-width: 640px) {
  .footer-top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
