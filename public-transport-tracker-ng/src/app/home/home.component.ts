import { Component, OnInit } from "@angular/core";
import { AuthService } from "../auth/auth.service";
import { Router } from "@angular/router";
import { NearbyTripsComponent } from "../trip/nearby-trips/nearby-trips.component";

@Component({
    selector: 'app-home',
    standalone: true,
    templateUrl: './home.component.html',
    styleUrl: './home.component.css',
    imports: [NearbyTripsComponent]
})
export class HomeComponent implements OnInit {

    public logedIn: boolean = false;

    constructor(
        private authService: AuthService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.logedIn = this.authService.isAuthenticated();
    }


    public login(): void {
        this.router.navigate(["/login"]);
    }

    public logout(): void {
        this.authService.logout();
        this.logedIn = false;
    }

    public profile(): void {
        this.router.navigate([`/user/${this.authService.getId()}`]);
    }
}