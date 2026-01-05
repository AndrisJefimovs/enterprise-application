package de.thb.ea.public_transport_tracker.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextFacade {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UserDetails getUserDetails() {
        Authentication auth = getAuthentication();
        return auth != null
            ? (UserDetails) auth.getPrincipal()
            : null;
    }

    public boolean hasAuthority(String authority) {
        Authentication auth = getAuthentication();
        return auth != null
            && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(authority));
    }
}