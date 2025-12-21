export interface IAuthResponse {
    token: string;
    refreshToken: string;
    statusCode: number;
    statusMessage: string;
}

export interface IRegisterResponse {
    statusCode: number;
    statusMessage: string;
}
