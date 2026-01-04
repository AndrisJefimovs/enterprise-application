package de.thb.ea.public_transport_tracker.controller.api;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.thb.ea.public_transport_tracker.controller.api.model.TripDTO;
import de.thb.ea.public_transport_tracker.entity.Trip;
import de.thb.ea.public_transport_tracker.service.TripService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/api/v1/")
@RestController
public class TripController {

    @Autowired
    private TripService tripService;
    
    @GetMapping("trips/nearby")
    public List<TripDTO> getMethodName(@RequestParam Double latitude, @RequestParam Double longitude)
        throws ResponseStatusException {
        if (latitude == null || longitude == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        List<Trip> trips = tripService.getNearbyTrips(latitude, longitude, 500);

        if (trips == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        return trips.stream().map(e -> TripDTO.map(e)).collect(Collectors.toList());
    }
    

}
