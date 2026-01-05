package de.thb.ea.public_transport_tracker.controller.api;

import org.springframework.web.bind.annotation.RestController;

import de.thb.ea.public_transport_tracker.controller.api.model.TripDTO;
import de.thb.ea.public_transport_tracker.entity.Trip;
import de.thb.ea.public_transport_tracker.service.TripService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/api/v1/")
@RestController
public class TripController {

    @Autowired
    private TripService tripService;
    
    @GetMapping("trips/nearby")
    public ResponseEntity<List<TripDTO>> getMethodName(
        @RequestParam Double latitude, @RequestParam Double longitude
    ) {
        if (latitude == null || longitude == null)
            return ResponseEntity.badRequest().build();

        List<Trip> trips = tripService.getNearbyTrips(latitude, longitude, 500, 64);

        if (trips == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.ok().body(
            trips.stream().map(e -> TripDTO.map(e)).collect(Collectors.toList())
        );
    }
    

}
