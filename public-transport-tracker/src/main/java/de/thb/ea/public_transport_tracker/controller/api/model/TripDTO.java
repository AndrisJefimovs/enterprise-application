package de.thb.ea.public_transport_tracker.controller.api.model;

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
    private String tripId;
    private String direction;
    private String lineName;
    private String type;


    public static TripDTO map(Trip trip) {
        return TripDTO.builder()
                .id(trip.getId())
                .tripId(trip.getTripId())
                .direction(trip.getDirection())
                .lineName(trip.getLineName())
                .type(trip.getType())
                .build();
    }

}
