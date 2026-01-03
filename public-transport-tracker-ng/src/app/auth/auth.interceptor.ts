import { inject } from '@angular/core';
import {
    HttpErrorResponse,
    HttpHandlerFn,
    HttpInterceptorFn,
    HttpRequest
} from '@angular/common/http';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';

import { AuthService } from './auth.service';
import { TokenService } from '../core/token.service';
import { IAuthResponse } from './model/response';

export const authInterceptor: HttpInterceptorFn = (
    req: HttpRequest<any>,
    next: HttpHandlerFn
) => {
    const authService = inject(AuthService);
    const tokenService = inject(TokenService);
    const router = inject(Router);

    // prevent refresh loop
    if (req.url.includes('/auth/refresh')) {
        return next(req);
    }

    const accessToken = tokenService.getToken();

    const authReq = accessToken
        ? req.clone({
            setHeaders: {
                Authorization: `Bearer ${accessToken}`
            }
        })
        : req;

    return next(authReq).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
                return throwError(() => error);
            }

            const refreshToken = tokenService.getRefreshToken();

            if (!refreshToken) {
                tokenService.clear();
                router.navigate(['/login']);
                return throwError(() => error);
            }

            // try refresh
            return authService.refreshToken().pipe(
                switchMap((response: IAuthResponse) => {
                    // Business-Fehler trotz HTTP 200
                    if (response.statusCode !== 0 || !response.token) {
                        tokenService.clear();
                        router.navigate(['/login']);
                        return throwError(() => error);
                    }

                    // save new tokens
                    tokenService.saveTokens(
                        response.token,
                        response.refreshToken!
                    );

                    // retry original request
                    const retryReq = req.clone({
                        setHeaders: {
                        Authorization: `Bearer ${response.token}`
                        }
                    });

                    return next(retryReq);
                }),
                catchError((error: HttpErrorResponse) => {
                    if (error.status === 401) {
                        authService.logout();
                        router.navigate(['/login']);
                    }
                    return throwError(() => error);
                })
            );
        })
    );
};
