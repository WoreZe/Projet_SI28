import {BaseApiUrlInterceptor} from "./base-api-url.interceptor";
import {RefreshTokenInterceptor} from "./refresh-token.interceptor";

export const httpInterceptors = [
  BaseApiUrlInterceptor.intercept(),
  RefreshTokenInterceptor.intercept()
]
