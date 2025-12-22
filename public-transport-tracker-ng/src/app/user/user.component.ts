import { Component, OnInit } from "@angular/core";
import { UserService } from "./user.service";
import { IUser } from "./model/user";
import { RouterLink } from "@angular/router";
import { DatePipe } from "@angular/common";

@Component({
    selector: 'app-users',
    standalone: true,
    providers: [UserService],
    templateUrl: './user.component.html',
    styleUrl: './user.component.css',
    imports: [
        RouterLink,
        DatePipe
    ]
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