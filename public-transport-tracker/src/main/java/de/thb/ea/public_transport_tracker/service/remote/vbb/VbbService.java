package de.thb.ea.public_transport_tracker.service.remote.vbb;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thb.ea.public_transport_tracker.service.remote.vbb.model.VbbMovement;
import de.thb.ea.public_transport_tracker.service.remote.vbb.model.VbbRadarResponse;
import de.thb.ea.public_transport_tracker.util.BoundingBox;
import de.thb.ea.public_transport_tracker.util.GeoUtils;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VbbService {

    private final Logger logger = LoggerFactory.getLogger(VbbService.class);
    
    public static final String API = "https://v6.vbb.transport.rest";
    private final WebClient webClient;
    private final ObjectMapper objectMapper;


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


        ResponseEntity<String> response;
        try {
            response = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(String.class)
                .block();
        }
        catch (Exception e) {
            logger.warn(String.format(
                "Request '%s' failed with error: %s", uri.toString(), e.toString()
            ));
            return null;
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            logger.info(String.format(
                "Request '%s' failed with HttpStatus %d", uri.toString(), response.getStatusCode()
            ));
            return null;
        }

        // check if response is empty list
        if ("[]".equals(response.getBody())) {
            return new ArrayList<>();
        }

        VbbRadarResponse radarResponse;
        try {
            radarResponse = objectMapper.readValue(
                response.getBody(), VbbRadarResponse.class
            );
        }
        catch (Exception e) {
            logger.warn(
                "Failed to map '%s' to VbbRadarResponse: %s",
                response.getBody(), e.getMessage()
            );
            return null;
        }
        
        return radarResponse
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
