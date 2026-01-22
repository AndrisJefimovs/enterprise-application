package de.thb.ea.public_transport_tracker.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import de.thb.ea.public_transport_tracker.config.property.ApplicationProperties;
import de.thb.ea.public_transport_tracker.controller.api.model.UserDTO;
import de.thb.ea.public_transport_tracker.entity.Permission;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.AuthorizationService;
import de.thb.ea.public_transport_tracker.service.PermissionService;
import de.thb.ea.public_transport_tracker.service.UserService;
import de.thb.ea.public_transport_tracker.service.exception.PermissionNotFoundException;
import de.thb.ea.public_transport_tracker.service.exception.UserAlreadyExists;
import de.thb.ea.public_transport_tracker.service.exception.UserNotFoundException;
import lombok.AllArgsConstructor;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final AuthorizationService authService;
    private final PermissionService permissionService;
    private final ApplicationProperties applicationProperties;

    /**
     * GET http://localhost:8080/api/v1/users
     * 
     * @return  list of all users
     */
    @GetMapping("users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOs = new ArrayList<>();
        List<User> users = userService.getAllUsers();

        if (users == null) {
            return ResponseEntity.internalServerError().build();
        }

        for (User user : users) {
            if (authService.canReadUser(user.getId())) {
                userDTOs.add(UserDTO.map(user));
            }
        }

        return ResponseEntity.ok().body(userDTOs);
    }
    
    /**
     * GET http://localhost:8080/api/v1/users/{id}
     * 
     * @param userId    user id of reqested user.
     * @return          user with specified id
     */
    @GetMapping("users/{id}")
    @PreAuthorize("@authorizationService.canReadUser(#userId)")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") Long userId) {
        if (!userService.userIdExists(userId)) {
            return ResponseEntity.notFound().build();
        }

        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok().body(UserDTO.map(user));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * POST http://localhost:8080/api/v1/users
     * 
     * Creates a new user. Note that id, createdBy, createdAt,
     * updatedAt and refreshVersion are being ignored.
     * 
     * @param userDTO   the new user
     * @return          created user
     */
    @PostMapping("users")
    @PreAuthorize("@authorizationService.canCreateUser(#userDTO)")
    public ResponseEntity<UserDTO> addNewUser(@RequestBody UserDTO userDTO) {
        if (
            userDTO.getUsername() == null
            || userDTO.getEmail() == null
            || userDTO.getPassword() == null
            || userDTO.getPermissions() == null
        ) {
            return ResponseEntity.unprocessableContent().build();
        }

        if (
            userService.usernameExists(userDTO.getUsername())
            || userService.emailExists(userDTO.getEmail())
        ) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Set<Permission> permissions = new HashSet<>();
        for (String permissionName : userDTO.getPermissions()) {
            try {
                Permission permission = permissionService.getPermissionByName(permissionName);
                permissions.add(permission);
            }
            catch (PermissionNotFoundException e) {
                return ResponseEntity.internalServerError().build();
            }
        }
        
        User newUser = User.builder()
            .username(userDTO.getUsername())
            .email(userDTO.getEmail())
            .password(userDTO.getPassword())
            .permissions(permissions)
            .createdBy(authService.getUser())
            .build();
        
        try {
            newUser = userService.addNewUser(newUser);
        }
        catch (UserAlreadyExists e) {
            return ResponseEntity.internalServerError().build();
        }
        
        URI uri = UriComponentsBuilder.fromUriString(applicationProperties.getHttpUrl())
            .pathSegment("/api/v1/users", newUser.getId().toString()).build().toUri();

        return ResponseEntity.created(uri).body(UserDTO.map(newUser));
    }
    

    /**
     * PUT http://localhost:8080/api/v1/users/{id}
     * 
     * replace a specific user
     * 
     * @param userId
     * @param userDTO
     * @return updated user
     */
    @PutMapping("users/{id}")
    @PreAuthorize("@authorizationService.canUpdateUser(#userId, #userDTO)")
    public ResponseEntity<UserDTO> updateUser(
        @PathVariable("id") Long userId, @RequestBody UserDTO userDTO
    ) {
        if (!userService.userIdExists(userId)) {
            return ResponseEntity.notFound().build();
        }

        if (
            userDTO.getId() == null
            || userDTO.getUsername() == null
            || userDTO.getEmail() == null
            || userDTO.getPassword() == null
            || userDTO.getPermissions() == null
            || userDTO.getCreatedBy() == null
            || userDTO.getCreatedAt() == null
            || userDTO.getUpdatedAt() == null
            || userDTO.getRefreshVersion() == null
        ) {
            logger.info("validation error");
            return ResponseEntity.unprocessableContent().build();
        }

        if (userDTO.getId() != userId) {
            return ResponseEntity.unprocessableContent().build();
        }

        User user;
        try {
            user = userService.getUserById(userId);
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        Set<Permission> permissions = new HashSet<>();
        try {
            for (String permissionName : userDTO.getPermissions()) {
                permissions.add(permissionService.getPermissionByName(permissionName));
            }
        }
        catch (PermissionNotFoundException e) {
            return ResponseEntity.unprocessableContent().build();
        }
        user.setPermissions(permissions);

        try {
            user = userService.updateUser(user);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().body(UserDTO.map(user));
    }


    /**
     * PATCH http://localhost:8080/api/v1/users/{id}
     * 
     * replace a specific user
     * 
     * @param userId
     * @param userDTO
     * @return updated user
     */
    @PatchMapping("users/{id}")
    @PreAuthorize("@authorizationService.canUpdateUser(#userId, #userDTO)")
    public ResponseEntity<UserDTO> patchUser(
        @PathVariable("id") Long userId, @RequestBody UserDTO userDTO
    ) {
        if (userId == null || userDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        if (userDTO.getId() != userId) {
            return ResponseEntity.unprocessableContent().build();
        }

        User user;
        try {
            user = userService.getUserById(userId);
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(userDTO.getPassword());
        }

        if (userDTO.getPermissions() != null) {
            Set<Permission> permissions = new HashSet<>();
            for (String permissionName : userDTO.getPermissions()) {
                try {
                    permissions.add(permissionService.getPermissionByName(permissionName));
                }
                catch (PermissionNotFoundException e) {
                    return ResponseEntity.unprocessableContent().build();
                }
            }
            user.setPermissions(permissions);
        }

        try {
            user = userService.updateUser(user);
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().body(UserDTO.map(user));
    }


    /**
     * DELETE http://localhost:8080/api/v1/users/{id}
     * 
     * @param userId
     * @return
     */
    @DeleteMapping("users/{id}")
    @PreAuthorize("@authorizationService.canDeleteUser(#userId)")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable("id") Long userId) {
        try {
            userService.deleteUser(userService.getUserById(userId));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        
        return ResponseEntity.noContent().build();
    }
}
