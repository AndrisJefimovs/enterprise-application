import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LoginRequest, RegisterRequest } from './model/request';
import { AuthResponse, RegisterResponse } from './model/response';
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

    login(req: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.API}/login`, req)
            .pipe(
                tap(res => {
                    if (res.statusCode === 0) {
                        this.tokenService.saveTokens(res.token, res.refreshToken);
                    }
                })
            );
    }

    register(req: RegisterRequest): Observable<RegisterResponse> {
        return this.http.post<RegisterResponse>(`${this.API}/register`, req);
    }

    refreshToken(): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.API}/refresh`, {
            refreshToken: this.tokenService.getRefreshToken()
        });
    }

    logout(): void {
        this.tokenService.clear();
    }

}
