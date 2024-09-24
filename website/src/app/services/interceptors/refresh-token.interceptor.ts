import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import {catchError, Observable} from 'rxjs';
import {inject} from "@angular/core";
import {AuthentificationService} from "../authentification.service";

export class RefreshTokenInterceptor {
  //token route :
  static routesCanBeAuth = [
    '/user/*',
    '/instances/*',
  ];
  static intercept(): HttpInterceptorFn {
   return (request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
     const authService = inject(AuthentificationService);
     if (this.needAuth(request.url)) {
       const token = authService.getAccessToken();
       if (token) {
         request = this.addTokenHeader(request, token);
       }
       return next(request).pipe(catchError(error => {
         if (error instanceof HttpErrorResponse && !request.url.includes('auth/') && error.status === 401) {
           authService.signOut();
         }
         throw error;
       }));
     }
     return next(request);
   }
  }

  static needAuth(url: string) {
    for (const route of this.routesCanBeAuth) {
      //convert route to regex
      const regex = new RegExp(route.replace('/*', '/?.*') + '$');
      if (regex.test(url)) return true;
    }
    return false;
  }
  private static addTokenHeader(request: HttpRequest<any>, token: string) {
    return request.clone({headers: request.headers.set("Authorization", 'Bearer ' + token)});
  }
}
