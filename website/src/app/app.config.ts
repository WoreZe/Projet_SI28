import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';

import { routes } from './app.routes';
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {httpInterceptors} from "./services/interceptors/http.interceptors";
import {MonacoEditorModule} from "ngx-monaco-editor-v2";

export const appConfig: ApplicationConfig = {
  providers: [provideRouter(routes),
    provideAnimations(),
    provideHttpClient(withInterceptors(httpInterceptors)),
    importProvidersFrom(MonacoEditorModule.forRoot())
  ]
};
