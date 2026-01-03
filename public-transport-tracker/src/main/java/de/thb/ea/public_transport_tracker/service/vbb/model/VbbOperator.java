package de.thb.ea.public_transport_tracker.service.vbb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VbbOperator {

    private String id;
    private String name;
    
}
