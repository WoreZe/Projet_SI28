import {
  HttpContextToken,
  HttpEvent,
  HttpHandler,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from "../../env";

export class BaseApiUrlInterceptor {
  static disableInterceptor = new HttpContextToken(() => false);

  constructor() {
  }

  static intercept(): HttpInterceptorFn {
    return (request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> => {
      if (request.context.get(BaseApiUrlInterceptor.disableInterceptor)) return next(request);
      request = request.clone({
        url: `${environment.api}${request.url}`
      });
      return next(request);
    }
  }

  static disable(request: HttpRequest<unknown>) {
    return request.clone({
      context: request.context.set(BaseApiUrlInterceptor.disableInterceptor, true)
    });
  }
}
