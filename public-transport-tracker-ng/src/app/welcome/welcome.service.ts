import { Injectable } from "@angular/core";

const WELCOME_VISITED_KEY: string = "WELCOME_VISITED";

@Injectable({
    providedIn: 'root'
})
export class WelcomeService {

    public hasVisited(): boolean {
        return localStorage.getItem(WELCOME_VISITED_KEY) === "true";
    }

    public setVisited(): void {
        localStorage.setItem(WELCOME_VISITED_KEY, "true");
    }

}