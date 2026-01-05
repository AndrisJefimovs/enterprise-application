package de.thb.ea.public_transport_tracker.service.vbb;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import de.thb.ea.public_transport_tracker.service.vbb.model.VbbMovement;
import de.thb.ea.public_transport_tracker.service.vbb.model.VbbRadarResponse;
import de.thb.ea.public_transport_tracker.util.BoundingBox;
import de.thb.ea.public_transport_tracker.util.GeoUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VbbService {
    
    public static final String API = "https://v6.vbb.transport.rest";
    private final RestTemplate restTemplate;


    public List<VbbMovement> getNearbyMovements(
        double latitude, double longitude, double radius, int n
    ) {
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

        
        VbbRadarResponse response;
        
        try {
           response = restTemplate.getForObject(uri, VbbRadarResponse.class);
        }
        catch (Exception e) {
            return null;
        }

        return response
            .getMovements()
            .stream()
            .filter(
                // filter for vehicles that are actually in radius
                e -> GeoUtils.distanceInMeters(
                    e.getLocation().getLatitude(), e.getLocation().getLongitude(),
                    latitude, longitude
                ) <= radius
            )
            .collect(Collectors.toList());
    }

}
