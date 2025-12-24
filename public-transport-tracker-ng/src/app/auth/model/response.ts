export interface IAuthResponse {
    userId: number | null;
    token: string | null;
    refreshToken: string | null;
    statusCode: number;
    statusMessage: string;
}

export interface IRegisterResponse {
    statusCode: number;
    statusMessage: string;
}
