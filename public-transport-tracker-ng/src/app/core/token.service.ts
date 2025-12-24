import { Injectable } from '@angular/core'

const TOKEN_KEY: string = 'auth_token';
const REFRESH_KEY: string = 'refesch_token';

@Injectable({ providedIn: 'root' })
export class TokenService {
    
    getToken(): string | null {
        return localStorage.getItem(TOKEN_KEY);
    }

    getRefreshToken(): string | null {
        return localStorage.getItem(REFRESH_KEY);    
    }
    
    saveTokens(token: string, refreshToken: string): void {
        localStorage.setItem(TOKEN_KEY, token);
        localStorage.setItem(REFRESH_KEY, refreshToken);
    }

    clear(): void {
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(REFRESH_KEY);
    }

}