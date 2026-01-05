import { Injectable } from "@angular/core";
import { LocationService } from "../core/location.service";
import { catchError, Observable, of, switchMap } from "rxjs";
import { HttpClient, HttpParams, HttpResponse } from "@angular/common/http";
import { ITrip } from "./model/trip";

@Injectable({
    providedIn: 'root'
})
export class TripService {

    private readonly API: string = "https://ptt.emx-studios.dev/api/v1/trips";

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