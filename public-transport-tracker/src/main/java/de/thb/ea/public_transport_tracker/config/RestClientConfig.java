package de.thb.ea.public_transport_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
