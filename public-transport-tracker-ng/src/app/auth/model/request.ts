export interface IRegisterRequest {
    username: string;
    email: string;
    password: string;
}

export interface ILoginRequest {
    identifier: string;
    identifierType: 'username' | 'email';
    password: string;
}

export interface IRefreshRequest {
    refreshToken: string;
}
