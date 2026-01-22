import { Injectable } from "@angular/core";
import { LocationService } from "../core/location.service";
import { Observable, switchMap } from "rxjs";
import { HttpClient, HttpParams } from "@angular/common/http";
import { ITrip } from "./model/trip";
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})
export class TripService {

    private readonly API: string = environment.apiBaseUrl + "/api/v1/trips";

    constructor(
        private locationService: LocationService,
        private httpClient: HttpClient
    )
    {}

    public getNearbyTrips(): Observable<ITrip[]> {
        return this.locationService.getLocation().pipe(
            switchMap(location => {
                const params = new HttpParams()
                    .set('latitude', location.latitude.toString())
                    .set('longitude', location.longitude.toString());

                return this.httpClient.get<ITrip[]>(this.API + '/nearby', { params });
            })
        );
    }

}