package de.thb.ea.public_transport_tracker.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "application")
@Getter
@Setter
public class ApplicationProperties {
    
    String httpUrl;

}