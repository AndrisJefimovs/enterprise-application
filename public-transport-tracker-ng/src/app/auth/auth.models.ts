export interface LoginRequest {
    identifier: string;
    identifierType: 'username' | 'email';
    password: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface RefreshRequest {
    refreshToken: string;
}

export interface AuthResponse {
    token: string;
    refreshToken: string;
    statusCode: number;
    statusMessage: string;
}

export interface RegisterResponse {
    statusCode: number;
    statusMessage: string;
}