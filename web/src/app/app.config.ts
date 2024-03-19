import { ApplicationConfig, inject } from '@angular/core';
import { provideHttpClient, withInterceptors, HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from './auth.service';

const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth: AuthService | null = inject(AuthService)
  if(auth?.isLogged()) {
    req = req.clone({
      setHeaders: {
        'Authorization': 'Basic ' + auth.getToken(),
      }
    })
  }
  return next(req)
}

export const appConfig: ApplicationConfig = {
  providers: [provideHttpClient(
    withInterceptors([authInterceptor])
  )]
};
