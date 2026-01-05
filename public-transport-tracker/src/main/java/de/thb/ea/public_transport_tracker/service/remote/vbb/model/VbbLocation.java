package de.thb.ea.public_transport_tracker.service.remote.vbb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VbbLocation {
    
    private String id;
    private Double latitude;
    private Double longitude; 

}
