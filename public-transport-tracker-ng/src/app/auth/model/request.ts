export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface LoginRequest {
    identifier: string;
    identifierType: 'username' | 'email';
    password: string;
}

export interface RefreshRequest {
    refreshToken: string;
}
