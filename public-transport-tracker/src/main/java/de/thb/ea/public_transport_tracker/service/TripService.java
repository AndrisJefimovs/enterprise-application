package de.thb.ea.public_transport_tracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Trip;
import de.thb.ea.public_transport_tracker.repository.TripRepository;
import de.thb.ea.public_transport_tracker.service.vbb.VbbService;
import de.thb.ea.public_transport_tracker.service.vbb.model.VbbMovement;

@Service
public class TripService {

    @Autowired
    private VbbService vbbService;

    @Autowired
    private TripRepository tripRepository;


    public Trip getTripByRemoteOriginAndRemoteId(String remoteOrigin, String remoteId) {
        if (remoteId == null || remoteOrigin == null)
            return null;

        Optional<Trip> trip = tripRepository.findTripByRemoteOriginAndRemoteId(remoteOrigin, remoteId);

        if (trip.isPresent())
            return trip.get();

        return null;
    }


    public Trip addNewTrip(Trip trip) {
        if (trip == null)
            return null;

        trip.forgetId();
        try {
            trip = tripRepository.save(trip);
        }
        catch (Exception e) {
            return null;
        }

        return trip;
    }


    public Trip updateTrip(Trip trip) {
        if (trip == null)
            return null;

        try {
            trip = tripRepository.save(trip);
        }
        catch (Exception e) {
            return null;
        }

        return trip;
    }

    
    public List<Trip> getNearbyTrips(double latitude, double longitude, double radius, int n) {
        List<VbbMovement> movements = vbbService.getNearbyMovements(latitude, longitude, radius, n);

        if (movements == null) {
            return null;
        }

        List<Trip> trips = new ArrayList<>();
        for (VbbMovement movement : movements) {
            Trip trip = getTripByRemoteOriginAndRemoteId("vbb", movement.getTripId());
            
            // no trip found in repo
            if (trip == null) {
                trip = addNewTrip(
                    Trip.builder()
                        .remoteId(movement.getTripId())
                        .remoteOrigin("vbb")
                        .direction(movement.getDirection())
                        .lineName(movement.getLine().getName())
                        .type(movement.getLine().getProduct())
                        .build()
                );
                // error
                if (trip == null)
                    return null;
            }
            
            trip = updateTrip(trip);
            // error
            if (trip == null)
                return null;

            trips.add(trip);
        }

        return trips;
    }
}
