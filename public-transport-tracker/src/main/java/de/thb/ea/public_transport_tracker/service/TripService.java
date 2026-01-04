package de.thb.ea.public_transport_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Trip;
import de.thb.ea.public_transport_tracker.service.vbb.VbbService;
import de.thb.ea.public_transport_tracker.service.vbb.model.VbbMovement;

@Service
public class TripService {

    @Autowired
    private VbbService vbbService;
    
    public List<Trip> getNearbyTrips(double latitude, double longitude, double radius) {
        List<VbbMovement> movements = vbbService.getNearbyMovements(latitude, longitude, radius, 64);

        if (movements == null)
            return null;

        return movements.stream()
                .map(e -> Trip.builder()
                            .tripId(e.getTripId())
                            .direction(e.getDirection())
                            .lineName(e.getLine().getName())
                            .type(e.getLine().getProduct())
                            .build()
                )
                .collect(Collectors.toList());
    }
}
