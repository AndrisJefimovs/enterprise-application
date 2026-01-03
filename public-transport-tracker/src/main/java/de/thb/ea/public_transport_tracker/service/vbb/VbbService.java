package de.thb.ea.public_transport_tracker.service.vbb;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.thb.ea.public_transport_tracker.entity.Trip;
import de.thb.ea.public_transport_tracker.service.vbb.model.VbbMovement;
import de.thb.ea.public_transport_tracker.service.vbb.model.VbbRadarResponse;
import de.thb.ea.public_transport_tracker.util.BoundingBox;
import de.thb.ea.public_transport_tracker.util.GeoUtils;

@Service
public class VbbService {
    
    @Autowired
    private RestTemplate restTemplate;

    private final String API = "https://v6.vbb.transport.rest";


    public List<Trip> getNearbyTrips(double latitude, double longitude, double radius, int n) {
        BoundingBox bbox = GeoUtils.getBbox(latitude, longitude, radius * 2.);

        URI uri = UriComponentsBuilder
                    .fromUriString(API + "/radar")
                    .queryParam("north", bbox.getNorthWest().getLatitude())
                    .queryParam("west", bbox.getNorthWest().getLongitude())
                    .queryParam("south", bbox.getSouthEast().getLatitude())
                    .queryParam("east", bbox.getSouthEast().getLongitude())
                    .queryParam("results", n)
                    .queryParam("duration", 0)
                    .queryParam("frames", 0)
                    .queryParam("polylines", false)
                    .queryParam("language", "de")
                    .build()
                    .encode()
                    .toUri();

        ResponseEntity<VbbRadarResponse> response = restTemplate.getForEntity(uri, VbbRadarResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            return null;
        }

        List<Trip> trips = new ArrayList<>();

        for (VbbMovement movement : response.getBody().getMovements()) {
            trips.add(
                Trip.builder()
                    .tripId(movement.getTripId())
                    .direction(movement.getDirection())
                    .lineName(movement.getLine().getName())
                    .build()
            );
        }

        return trips;
    }

}
