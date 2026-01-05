package de.thb.ea.public_transport_tracker.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "application.admin")
@Getter
@Setter
public class AdminProperties {
    
    String username;
    String email;
    String password;

}
