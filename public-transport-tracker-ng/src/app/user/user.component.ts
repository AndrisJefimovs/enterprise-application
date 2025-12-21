import { Component, OnInit } from "@angular/core";
import { UserService } from "./user.service";
import { IUser } from "./model/user";

@Component({
    selector: 'app-user',
    standalone: true,
    providers: [UserService],
    templateUrl: './user.component.html',
    styleUrl: './user.component.css'
})
export class UserComponent implements OnInit {

    users: IUser[] = [];

    constructor(
        private userService: UserService
    ) {}

    ngOnInit(): void {
        this.userService.getUsers()
            .subscribe(data => this.users = data);
    }
}