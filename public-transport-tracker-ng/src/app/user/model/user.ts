export interface IUser {
    id?: number,
    username?: string | null,
    email?: string | null,
    password?: string | null,
    permissions?: string[] | null,
    createdBy?: number | null,
    createdAt?: string | null,
    updatedAt?: string | null,
    refreshVersion?: number | null,
    loginEnabled?: boolean | null
}