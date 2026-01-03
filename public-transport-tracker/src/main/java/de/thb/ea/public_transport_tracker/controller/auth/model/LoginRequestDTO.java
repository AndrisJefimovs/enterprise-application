package de.thb.ea.public_transport_tracker.controller.auth.model;

import de.thb.ea.public_transport_tracker.controller.auth.model.enums.IdentifierType;
import lombok.Data;

@Data
public class LoginRequestDTO {
    private String identifier;
    private IdentifierType identifierType;
    private String password;
}
