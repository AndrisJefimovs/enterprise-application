import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

import { AuthService } from './auth.service';
import { TokenService } from '../core/token.service';

let isRefreshing = false;

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const tokenService = inject(TokenService);
  const authService = inject(AuthService);
  const router = inject(Router);

  if (req.url.endsWith('/auth/register') || req.url.endsWith('/auth/login') || req.url.endsWith('/auth/refresh') || req.url.endsWith('/')) {
    return next(req);
  }

  const token = tokenService.getToken();

  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError(err => {

      if (err.status !== 401) {
        return throwError(() => err);
      }

      if (isRefreshing) {
        return throwError(() => err);
      }

      isRefreshing = true;

      return authService.refreshToken().pipe(
        switchMap(res => {
          isRefreshing = false;

          if (res.statusCode === 0 && res.token) {
            tokenService.saveTokens(res.token, res.refreshToken!);

            return next(
              authReq.clone({
                setHeaders: {
                  Authorization: `Bearer ${res.token}`
                }
              })
            );
          }

          tokenService.clear();
          router.navigate(['/login']);
          return throwError(() => err);
        }),
        catchError(refreshErr => {
          isRefreshing = false;
          tokenService.clear();
          router.navigate(['/login']);
          return throwError(() => refreshErr);
        })
      );
    })
  );
};
