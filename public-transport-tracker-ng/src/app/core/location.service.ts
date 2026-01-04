import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class LocationService {

    private location: GeolocationCoordinates | null = null;

    public getLocation(): GeolocationCoordinates | null {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((position) => {
                this.location = position.coords;
            })
            return this.location;
        }
        return null;
    }

}