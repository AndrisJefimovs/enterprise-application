package de.thb.ea.public_transport_tracker.controller.api.model;

import java.time.Instant;

import de.thb.ea.public_transport_tracker.entity.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripDTO {
    
    private Long id;
    private String remoteId;
    private String remoteOrigin;
    private String direction;
    private String lineName;
    private String type;
    private Instant updatedAt;


    public static TripDTO map(Trip trip) {
        return TripDTO.builder()
            .id(trip.getId())
            .remoteId(trip.getRemoteId())
            .remoteOrigin(trip.getRemoteOrigin())
            .direction(trip.getDirection())
            .lineName(trip.getLineName())
            .type(trip.getType())
            .updatedAt(trip.getUpdatedAt())
            .build();
    }

}
