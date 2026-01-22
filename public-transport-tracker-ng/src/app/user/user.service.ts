import { HttpClient, HttpResponse } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { IUser } from "./model/user";
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class UserService {

    private readonly API: string = environment.apiBaseUrl + "/api/v1/users"

    private users: IUser[] = []

    constructor(private httpClient: HttpClient) {};

    public getUsers(): Observable<IUser[]> {
        return this.httpClient.get<IUser[]>(this.API);
    }

    public getUser(userId: number): Observable<IUser> {
        return this.httpClient.get<IUser>(this.API + "/" + String(userId));
    }

    public updateUser(user: IUser): Observable<HttpResponse<IUser>> {
        return this.httpClient.patch<IUser>(this.API + "/" + user.id, user, { observe: 'response' });
    }

    public createUser(user: IUser): Observable<HttpResponse<IUser>> {
        return this.httpClient.post<IUser>(this.API, user, { observe: 'response' });
    }

    public deleteUser(userId: number): Observable<HttpResponse<IUser>> {
        return this.httpClient.delete<IUser>(this.API + "/" + String(userId), { observe: 'response' });
    }

}