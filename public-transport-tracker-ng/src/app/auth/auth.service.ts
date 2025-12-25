import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ILoginRequest, IRegisterRequest } from './model/request';
import { IAuthResponse, IRegisterResponse } from './model/response';
import { Observable, tap } from 'rxjs';
import { TokenService } from '../core/token.service';


const USER_ID_KEY: string = 'USER_ID';


@Injectable({
    providedIn: 'root'
})
export class AuthService {
  
    private readonly API: string = 'http://localhost:8080/auth';

    constructor(
        private http: HttpClient,
        private tokenService: TokenService
    ) {}

    public login(req: ILoginRequest): Observable<IAuthResponse> {
        return this.http.post<IAuthResponse>(`${this.API}/login`, req)
            .pipe(
                tap(res => {
                    if (res.statusCode === 0) {
                        this.tokenService.saveTokens(res.token!, res.refreshToken!);
                        localStorage.setItem(USER_ID_KEY, String(res.userId!));
                    }
                })
            );
    }

    public register(req: IRegisterRequest): Observable<IRegisterResponse> {
        return this.http.post<IRegisterResponse>(`${this.API}/register`, req);
    }

    public refreshToken(): Observable<IAuthResponse> {
        return this.http.post<IAuthResponse>(`${this.API}/refresh`, {
            refreshToken: this.tokenService.getRefreshToken()
        });
    }

    public logout(): void {
        this.tokenService.clear();
        localStorage.removeItem(USER_ID_KEY);
    }

    public getId(): number | null {
        let userId: string | null = localStorage.getItem(USER_ID_KEY);
        if (userId) {
            return Number(userId);
        }
        return null;
    }

}
