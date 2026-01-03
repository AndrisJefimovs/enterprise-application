import { HttpClient, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { IUser } from "./model/user";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private readonly api: string = "http://localhost:8080/api/v1/users"

    private users: IUser[] = []

    constructor(private httpClient: HttpClient) {};

    public getUsers(): Observable<IUser[]> {
        return this.httpClient.get<IUser[]>(this.api);
    }

    public getUser(userId: number): Observable<IUser> {
        return this.httpClient.get<IUser>(this.api + "/" + String(userId));
    }

    public updateUser(user: IUser): Observable<HttpResponse<IUser>> {
        return this.httpClient.put<IUser>(this.api + "/" + user.id, user, { observe: 'response' });
    }

    public createUser(user: IUser): Observable<HttpResponse<IUser>> {
        return this.httpClient.post<IUser>(this.api, user, { observe: 'response' });
    }

    public deleteUser(userId: number): Observable<HttpResponse<IUser>> {
        return this.httpClient.delete<IUser>(this.api + "/" + String(userId), { observe: 'response' });
    }

}