// file: src/app/app.config.server.ts

import { mergeApplicationConfig, ApplicationConfig } from '@angular/core';
import { provideServerRendering } from '@angular/platform-server';
import { provideRouter } from '@angular/router';

import { appRoutes } from './app.routes'; // stesse rotte
import { appConfig } from './app.config'; // config comune client/server (es. provideHttpClient, ecc.)

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    // Forniamo lo stesso router del client
    provideRouter(appRoutes),
  ]
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
