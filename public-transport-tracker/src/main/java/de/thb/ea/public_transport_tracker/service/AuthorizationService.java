package de.thb.ea.public_transport_tracker.service;

import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.config.AdminProperties;
import de.thb.ea.public_transport_tracker.config.SecurityContextFacade;
import de.thb.ea.public_transport_tracker.entity.User;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthorizationService {
    
    private final SecurityContextFacade security;
    private final UserService userService;
    private final AdminProperties adminProperties;

    public User getUser() {
        return userService.getUserByUsername(security.getUserDetails().getUsername());
    }

    public boolean hasId(Long userId) {
        return userId == getUser().getId();
    }

    public boolean hasPermission(String permissionName) {
        return security.hasAuthority("PERM_" + permissionName.toUpperCase());
    }

    public boolean canCreateUser() {
        return hasPermission("CREATE_USER");
    }

    public boolean canReadUser(Long userId) {
        return hasPermission("READ_USER") ||
               hasId(userId);
    }

    public boolean canUpdateUser(Long userId) {
        if (userService.isIdOfUser(userId, "SYSTEM") ||
            userService.isIdOfUser(userId, adminProperties.getUsername()))
            return false;
        return hasPermission("UPDATE_USER") ||
               hasId(userId);
    }

    public boolean canDeleteUser(Long userId) {
        if (userService.isIdOfUser(userId, "SYSTEM") ||
            userService.isIdOfUser(userId, adminProperties.getUsername()))
            return false;
        return hasPermission("DELETE_USER") ||
               hasId(userId);
    }
}
