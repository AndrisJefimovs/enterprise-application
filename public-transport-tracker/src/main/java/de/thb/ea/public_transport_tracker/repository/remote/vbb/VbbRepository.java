package de.thb.ea.public_transport_tracker.repository.remote.vbb;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.thb.ea.public_transport_tracker.repository.remote.vbb.model.VbbMovement;
import de.thb.ea.public_transport_tracker.repository.remote.vbb.model.VbbRadarResponse;
import de.thb.ea.public_transport_tracker.util.GeoUtils;
import de.thb.ea.public_transport_tracker.util.model.BoundingBox;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class VbbRepository {

    private final Logger logger = LoggerFactory.getLogger(VbbRepository.class);
    
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
            return List.of();
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            logger.info(String.format(
                "Request '%s' failed with HttpStatus %d", uri.toString(), response.getStatusCode()
            ));
            return List.of();
        }

        // check if response is empty
        if (response.getBody() == null || response.getBody().equals("[]")) {
            return List.of();
        }

        VbbRadarResponse radarResponse;
        try {
            radarResponse = objectMapper.readValue(
                response.getBody(), VbbRadarResponse.class
            );
        }
        catch (Exception e) {
            logger.error(
                "Failed to map '%s' to VbbRadarResponse: %s",
                response.getBody(), e.getMessage()
            );
            // TODO: throw a RemoteRepositoryException
            return List.of();
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
