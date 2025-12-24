import { DatePipe } from "@angular/common";
import { Component, Input, OnInit } from "@angular/core";
import { UserService } from "../user.service";
import { ActivatedRoute } from "@angular/router";
import { IUser } from "../model/user";

@Component({
    selector: 'app-user-details',
    standalone: true,
    providers: [UserService],
    templateUrl: './user-details.component.html',
    styleUrl: './user-details.component.css',
    imports: [
        DatePipe
    ]
})
export class UserDetailsComponent implements OnInit {
    
    @Input() userId?: number;
    
    user?: IUser;

    constructor(
        private route: ActivatedRoute,
        private userService: UserService
    ) {}

    ngOnInit(): void {
        if (!this.userId) {
            const idFromRoute = this.route.snapshot.paramMap.get('id');
            if (idFromRoute) {
                this.userId = Number(idFromRoute);
            }
        }

        if (this.userId) {
            this.userService.getUser(this.userId)
                .subscribe(data => this.user = data);
        }
    }
}