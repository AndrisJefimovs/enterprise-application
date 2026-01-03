import { Component, OnInit } from "@angular/core";
import { UserService } from "../user.service";
import { IUser } from "../model/user";
import { RouterLink } from "@angular/router";
import { DatePipe } from "@angular/common";
import { AuthService } from "../../auth/auth.service";

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

    public users: IUser[] = [];

    constructor(
        private userService: UserService,
        public authService: AuthService
    ) {}

    ngOnInit(): void {
        this.userService.getUsers().subscribe({
            next: (data) => { this.users = data.sort((a, b) => a.id! - b.id!) },
            error: (err) => {} // just to prevent error in console
        });
    }

    public delete(userId: number): void {
        this.userService.deleteUser(userId).subscribe({
            next: (res) => {
                this.users = this.users.filter(
                    user => user.id !== res.body?.id
                )
            },
            error: (err) => {} // dont throw error to console
        });
    }
}