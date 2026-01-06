package de.thb.ea.public_transport_tracker.repository.remote.vbb.model;

import java.util.ArrayList;
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
    private List<VbbMovement> movements = new ArrayList<>();

    private Long realtimeDataUpdatedAt;

}
