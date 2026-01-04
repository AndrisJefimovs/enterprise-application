import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';

@Component({
    selector: 'app-welcome',
    standalone: true,
    imports: [
        RouterLink
    ],
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css']
})
export class LoginComponent {

    constructor(
        private router: Router,
        private authService: AuthService
    )
    {}

    public continueWithoutAccount(): void {
        this.authService.logout();
        this.router.navigate(["/"]);
    }
}
