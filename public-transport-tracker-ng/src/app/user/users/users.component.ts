import { Component, OnInit } from "@angular/core";
import { UserService } from "../user.service";
import { IUser } from "../model/user";
import { RouterLink } from "@angular/router";
import { DatePipe } from "@angular/common";

@Component({
    selector: 'app-users',
    standalone: true,
    providers: [UserService],
    templateUrl: './users.component.html',
    styleUrl: './users.component.css',
    imports: [
        RouterLink,
        DatePipe
    ]
})
export class UsersComponent implements OnInit {

    users: IUser[] = [];

    constructor(
        private userService: UserService
    ) {}

    ngOnInit(): void {
        this.userService.getUsers()
            .subscribe(data => this.users = data);
    }
}