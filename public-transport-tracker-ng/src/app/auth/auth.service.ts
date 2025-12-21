import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ILoginRequest, IRegisterRequest } from './model/request';
import { IAuthResponse, IRegisterResponse } from './model/response';
import { Observable, tap } from 'rxjs';
import { TokenService } from '../core/token.service';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
  
    private readonly API = 'http://localhost:8080/auth';

    constructor(
        private http: HttpClient,
        private tokenService: TokenService
    ) {}

    login(req: ILoginRequest): Observable<IAuthResponse> {
        return this.http.post<IAuthResponse>(`${this.API}/login`, req)
            .pipe(
                tap(res => {
                    if (res.statusCode === 0) {
                        this.tokenService.saveTokens(res.token, res.refreshToken);
                    }
                })
            );
    }

    register(req: IRegisterRequest): Observable<IRegisterResponse> {
        return this.http.post<IRegisterResponse>(`${this.API}/register`, req);
    }

    refreshToken(): Observable<IAuthResponse> {
        return this.http.post<IAuthResponse>(`${this.API}/refresh`, {
            refreshToken: this.tokenService.getRefreshToken()
        });
    }

    logout(): void {
        this.tokenService.clear();
    }

}
