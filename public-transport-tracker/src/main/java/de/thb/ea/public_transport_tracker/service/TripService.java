package de.thb.ea.public_transport_tracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Trip;
import de.thb.ea.public_transport_tracker.repository.TripRepository;
import de.thb.ea.public_transport_tracker.repository.remote.vbb.VbbRepository;
import de.thb.ea.public_transport_tracker.repository.remote.vbb.model.VbbMovement;
import de.thb.ea.public_transport_tracker.service.exception.TripAlreadyExistsException;
import de.thb.ea.public_transport_tracker.service.exception.TripNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TripService {

    private final VbbRepository vbbService;
    private final TripRepository tripRepository;


    public Trip getTripByRemoteOriginAndRemoteId(String remoteOrigin, String remoteId)
        throws TripNotFoundException
    {
        if (remoteId == null) {
            throw new IllegalArgumentException("null is not a valid value for remoteId");
        }
        if (remoteOrigin == null) {
            throw new IllegalArgumentException("null is not a valid value for remoteOrigin");
        }

        Optional<Trip> trip = tripRepository.findTripByRemoteOriginAndRemoteId(
            remoteOrigin, remoteId
        );

        if (trip.isEmpty()) {
            throw TripNotFoundException.fromRemote(remoteOrigin, remoteId);
        }
        return trip.get();
    }


    public Trip addNewTrip(Trip trip) throws TripAlreadyExistsException {
        if (trip == null) {
            throw new IllegalArgumentException("null is not a valid value for trip");
        }

        if (remoteTripExists(trip.getRemoteOrigin(), trip.getRemoteId())) {
            throw TripAlreadyExistsException.fromRemote(trip.getRemoteOrigin(), trip.getRemoteId());
        }

        trip.forgetId(); // prevent updating existing trip
        try {
            trip = tripRepository.save(trip);
        }
        catch (Exception e) {
            throw e;
        }

        return trip;
    }


    public Trip updateTrip(Trip trip) throws TripNotFoundException {
        if (trip == null) {
            throw new IllegalArgumentException("null is not a valid value for trip");
        }

        if (!tripIdExists(trip.getId())) {
            throw TripNotFoundException.fromId(trip.getId());
        }

        try {
            trip = tripRepository.save(trip);
        }
        catch (Exception e) {
            throw e;
        }

        return trip;
    }

    
    public List<Trip> getNearbyTrips(double latitude, double longitude, double radius, int n) {
        List<VbbMovement> movements = vbbService.getNearbyMovements(latitude, longitude, radius, n);

        List<Trip> trips = new ArrayList<>();
        for (VbbMovement movement : movements) {
            try {
                // load trip from database
                trips.add(
                    getTripByRemoteOriginAndRemoteId("vbb", movement.getTripId())
                );
            }
            catch (TripNotFoundException e) {
                try {
                    // create new trip in database
                    trips.add(addNewTrip(
                        Trip.builder()
                            .remoteId(movement.getTripId())
                            .remoteOrigin("vbb")
                            .direction(movement.getDirection())
                            .lineName(movement.getLine().getName())
                            .type(movement.getLine().getProduct())
                            .build()
                    ));
                }
                catch (TripAlreadyExistsException _e) {
                    // should not happen since TripNotFoundException
                    // TODO: add logging and maybe throw RuntimeException
                }
            }
        }

        return trips;
    }

    public boolean remoteTripExists(String remoteOrigin, String remoteId) {
        return tripRepository.existsByRemoteOriginAndRemoteId(remoteOrigin, remoteId);
    }

    public boolean tripIdExists(Long tripId) {
        return tripRepository.existsById(tripId);
    }
}
