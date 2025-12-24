import { HttpClient } from "@angular/common/http";
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

    getUsers(): Observable<IUser[]> {
        return this.httpClient.get<IUser[]>(this.api);
    }

    getUser(userId: number): Observable<IUser> {
        return this.httpClient.get<IUser>(this.api + "/" + String(userId));
    }
}