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
