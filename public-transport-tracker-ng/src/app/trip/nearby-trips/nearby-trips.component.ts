import { Component, OnInit } from "@angular/core";
import { TripService } from "../trip.service";
import { ITrip } from "../model/trip";

@Component({
    selector: 'app-nearby-trips',
    standalone: true,
    providers: [TripService],
    templateUrl: './nearby-trips.component.html',
    styleUrl: './nearby-trips.component.css',
})
export class NearbyTripsComponent implements OnInit {

    public trips: ITrip[] = [];

    constructor(
        private tripService: TripService
    ) {}

    public ngOnInit(): void {
        this.updateTrips();
    }

    public updateTrips(): void {
        this.tripService.getNearbyTrips().subscribe({
            next: (res) => {
                this.trips = res;
            },
            error: (err) => {}
        })
    }

}