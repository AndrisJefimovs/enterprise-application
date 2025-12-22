export interface IUser {
    id: number,
    username?: string,
    email: string | null,
    password?: string | null,
    roleIds?: number[],
    createdAt?: string | null,
    updatedAt?: string | null,
    refreshVersion?: number | null
}