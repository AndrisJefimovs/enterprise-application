package de.thb.ea.public_transport_tracker.service.vbb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VbbLine {
    
    private String id;
    private String name;
    private String productName;
    private String mode;
    private String product;
    private VbbOperator operator;
    
    @JsonProperty("public")
    private Boolean isPublic;

}
