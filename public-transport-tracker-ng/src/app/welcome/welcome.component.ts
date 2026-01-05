import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { WelcomeService } from './welcome.service';

@Component({
    selector: 'app-welcome',
    standalone: true,
    imports: [
        RouterLink
    ],
    templateUrl: './welcome.component.html',
    styleUrls: ['./welcome.component.css']
})
export class LoginComponent implements OnInit {

    constructor(
        private router: Router,
        private authService: AuthService,
        private welcomeService: WelcomeService
    ) {}

    // navigate to /home when already visited
    ngOnInit(): void {
        if (!this.welcomeService.hasVisited()) {
            this.welcomeService.setVisited();
        }
    }

    // also do logout
    public continueWithoutAccount(): void {
        this.authService.logout();
        this.router.navigate(["/home"]);
    }
}
