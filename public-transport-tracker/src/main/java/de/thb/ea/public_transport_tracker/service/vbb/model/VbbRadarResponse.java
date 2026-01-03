package de.thb.ea.public_transport_tracker.service.vbb.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VbbRadarResponse {
    
    @JsonProperty("movements")
    private List<VbbMovement> movements;

    private Long realtimeDataUpdatedAt;

}
