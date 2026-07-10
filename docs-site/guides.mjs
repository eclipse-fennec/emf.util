// The published, user-facing pages (allowlist). Shared by the sync script and the
// VitePress config so the set and its order are defined exactly once.
//   file  — source markdown in ../docs (the single source of truth)
//   slug  — route name under the section
//   title — sidebar / nav label
//
// GUIDES   -> /guides/   (the user manual)
// EXAMPLES -> /examples/ (worked examples)
export const GUIDES = [
  { file: 'overview.md', slug: 'overview', title: 'Overview' },
  { file: 'protobuf-user-guide.md', slug: 'protobuf', title: 'EMF ⇄ Protobuf' },
  { file: 'soap-user-guide.md', slug: 'soap', title: 'EMF ⇄ SOAP / WSDL' },
  { file: 'openapi-user-guide.md', slug: 'openapi', title: 'EMF ⇄ OpenAPI / REST' },
  { file: 'service-clients.md', slug: 'service-clients', title: 'Service clients' },
  { file: 'workspace-library.md', slug: 'consuming', title: 'Consuming (fennecUtil)' },
];

export const EXAMPLES = [
  { file: 'protobuf-examples.md', slug: 'protobuf', title: 'EMF ⇄ Protobuf' },
  { file: 'grpc-examples.md', slug: 'grpc', title: 'EMF ⇄ gRPC' },
];
