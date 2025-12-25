package de.thb.ea.public_transport_tracker.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.config.SecurityContextFacade;
import de.thb.ea.public_transport_tracker.entity.User;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    
    private final SecurityContextFacade security;
    private final UserService userService;

    public boolean hasId(Long userId) {
        UserDetails userDetails = security.getUserDetails();
        User user = userService.getUserByUsername(userDetails.getUsername());
        return userId == user.getId();
    }

    public boolean hasRole(String roleName) {
        return security.hasAuthority("ROLE_" + roleName);
    }

    public boolean hasPermission(String permissionName) {
        return security.hasAuthority("PERM_" + permissionName);
    }

}
