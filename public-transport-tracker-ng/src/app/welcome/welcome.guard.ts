import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { WelcomeService } from './welcome.service';

@Injectable({
    providedIn: 'root'
})
export class WelcomeGuard implements CanActivate {

    constructor(
        private welcomeService: WelcomeService,
        private router: Router
    ) {}

    canActivate(): boolean {
        // can activate if user id is set
        if (!this.welcomeService.hasVisited()) {
            this.router.navigate(['/welcome']);
            return false;
        }
        return true;
    }

}