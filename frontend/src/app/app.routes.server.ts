import { RenderMode, ServerRoute } from '@angular/ssr';

// Parameterized routes cannot be prerendered without a list of parameter values.
// These pages load user/product-specific data, so let the browser render them.
export const serverRoutes: ServerRoute[] = [
  {
    path: 'product/:id',
    renderMode: RenderMode.Client
  },
  {
    path: 'category/:categoryName',
    renderMode: RenderMode.Client
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
