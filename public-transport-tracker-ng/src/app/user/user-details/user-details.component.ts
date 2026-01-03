import { DatePipe } from "@angular/common";
import { Component, Input, OnInit } from "@angular/core";
import { UserService } from "../user.service";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { IUser } from "../model/user";
import { AuthService } from "../../auth/auth.service";

@Component({
    selector: 'app-user-details',
    standalone: true,
    providers: [UserService],
    templateUrl: './user-details.component.html',
    styleUrl: './user-details.component.css',
    imports: [
        DatePipe,
        RouterLink
    ]
})
export class UserDetailsComponent implements OnInit {
    
    @Input() public userId?: number;
    
    public user?: IUser;
    public creator?: IUser;
    public error?: string;

    constructor(
        private router: Router,
        private route: ActivatedRoute,
        private userService: UserService,
        public authService: AuthService
    ) {}

    ngOnInit(): void {
        // if component is embedded
        if (this.userId) {
            this.loadUser(this.userId);
        }
        else {
            this.route.paramMap.subscribe(params => {
                const id = params.get('id');
                if (id) {
                    this.loadUser(Number(id));
                }
            });
        }
    }

    private loadUser(id: number) {
        this.userService.getUser(id).subscribe({
            next: (user) => {
                this.user = user;
                if (user.createdBy !== null) {
                    this.userService.getUser(user.createdBy!).subscribe({
                        next: (creator) => {
                            this.creator = creator;
                        },
                        error: (err) => {
                            // just to prevent error message in console
                        }
                    })
                }
            },
            error: (err) => {
                this.router.navigate(["/users"])
            }
        });
    }

    public deleteUser(): void {
        this.userService.deleteUser(this.user!.id!).subscribe({
            next: (res) => {
                this.router.navigate(['/users']);
            },
            error: (err) => {} // dont throw error to console
        });
    }
}