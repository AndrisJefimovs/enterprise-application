package de.thb.ea.public_transport_tracker.controller.auth.model;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String identifier;
    // should be either "username" or "password"
    private String identifierType;
    private String password;
}
