import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering } from '@angular/platform-server';
import { provideServerRouting } from '@angular/ssr';
import { provideRouter } from '@angular/router';

import { appRoutes } from './app.routes';
import { appConfig } from './app.config';
import { serverRoutes } from './app.routes.server';

// Register hybrid rendering rules explicitly. Without provideServerRouting,
// Angular falls back to prerendering the parameterized product route.
const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    provideServerRouting(serverRoutes),
    provideRouter(appRoutes),
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
