import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ILoginRequest, IRegisterRequest } from './model/request';
import { IAuthResponse, IRegisterResponse } from './model/response';
import { BehaviorSubject, Observable, of, tap } from 'rxjs';
import { TokenService } from '../core/token.service';
import { IUser } from '../user/model/user';
import { UserService } from '../user/user.service';

const USER_ID_KEY: string = 'USER_ID';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
  
    private readonly API: string = 'http://localhost:8080/auth';

    private userSubject = new BehaviorSubject<IUser|null>(null);
    readonly user$ = this.userSubject.asObservable();

    constructor(
        private http: HttpClient,
        private tokenService: TokenService,
        private userService: UserService
    ) {}

    public init(): void {
        const token = this.tokenService.getToken();

        if (!token) {
            this.clearState();
            return;
        }

        if (this.getId()) {
            this.loadCurrentUser().subscribe({
                error: () => this.clearState()
            });
        }
    }

    public loadCurrentUser(): Observable<IUser|null> {
        const userId = this.getId();

        if (!userId) {
            this.userSubject.next(null);
            return of(null);
        }

        return this.userService.getUser(userId).pipe(
            tap(user => this.userSubject.next(user))
        );
    }

    public get user(): IUser | null {
        return this.userSubject.value;
    }

    public isAuthenticated(): boolean {
        return !!this.user;
    }

    public getId(): number | null {
        let userId: string | null = localStorage.getItem(USER_ID_KEY);
        if (userId) {
            return Number(userId);
        }
        return null;
    }

    // authentication

    public get permissions(): string[] {
        return this.user?.permissions ?? [];
    }

    public hasPermission(permission: string): boolean {
        return this.user?.permissions?.includes(permission) ?? false;
    }

    public hasAnyPermission(...permissions: string[]): boolean {
        return permissions.some(p => this.hasPermission(p));
    }

    public hasAllPermissions(...permissions: string[]): boolean {
        return permissions.every(p => this.hasPermission(p));
    }

    // login and registration

    public login(req: ILoginRequest): Observable<IAuthResponse> {
        return this.http.post<IAuthResponse>(`${this.API}/login`, req)
            .pipe(
                tap(res => {
                    if (res.statusCode === 0) {
                        this.tokenService.saveTokens(res.token!, res.refreshToken!);
                        localStorage.setItem(USER_ID_KEY, String(res.userId!));
                        this.loadCurrentUser().subscribe();
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
        this.clearState();
    }

    private clearState(): void {
        this.userSubject.next(null);
    }

}
