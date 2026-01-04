import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class LocationService {

    getLocation(): Observable<GeolocationCoordinates> {
        return new Observable(observer => {
            if (!navigator.geolocation) {
                observer.error('Geolocation not supported');
                return;
            }

            navigator.geolocation.getCurrentPosition(
                position => {
                    observer.next(position.coords);
                    observer.complete();
                },
                error => observer.error(error)
            );
        });
    }
    
}