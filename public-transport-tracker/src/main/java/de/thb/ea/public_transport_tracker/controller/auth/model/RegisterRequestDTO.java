package de.thb.ea.public_transport_tracker.controller.auth.model;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    private String username;
    private String email;
    private String password;
}
