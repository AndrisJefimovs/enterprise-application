package de.thb.ea.public_transport_tracker.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.thb.ea.public_transport_tracker.controller.api.model.UserDTO;
import de.thb.ea.public_transport_tracker.entity.Permission;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.AuthorizationService;
import de.thb.ea.public_transport_tracker.service.PermissionService;
import de.thb.ea.public_transport_tracker.service.UserService;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * GET http://localhost:8080/api/v1/users
     * 
     * @return list of all users
     */
    @GetMapping("users")
    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : userService.getAllUsers()) {
            if (authService.canReadUser(user.getId()))
                userDTOs.add(UserDTO.map(user));
        }

        return userDTOs;
    }
    
    /**
     * GET http://localhost:8080/api/v1/users/{id}
     * 
     * @param userId user id of reqested user.
     * @return user with specified id
     * @throws ResponseStatusException 404 if user not found
     */
    @GetMapping("users/{id}")
    @PreAuthorize("@authorizationService.canReadUser(#userId)")
    public UserDTO getUser(@PathVariable("id") Long userId)
            throws ResponseStatusException {

        User user = userService.getUserById(userId);
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        
        return UserDTO.map(user);
    }


    /**
     * POST http://localhost:8080/api/v1/users
     * 
     * Creates a new user. Note that id, createdBy, createdAt,
     * updatedAt and refreshVersion are being ignored.
     * 
     * @param userDTO
     * @return created user
     * @throws ResponseStatusException
     */
    @PostMapping("users")
    @PreAuthorize("@authorizationService.canCreateUser()")
    public UserDTO addNewUser(@RequestBody UserDTO userDTO) throws ResponseStatusException {
        if (userDTO.getUsername() == null ||
            userDTO.getEmail() == null ||
            userDTO.getPassword() == null ||
            userDTO.getPermissions() == null ||
            userDTO.getLoginEnabled() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (userService.usernameExists(userDTO.getUsername()) ||
            userService.emailExists(userDTO.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Set<Permission> permissions = new HashSet<>();
        for (String permissionName : userDTO.getPermissions()) {
            Permission permission = permissionService.getPermissionByName(permissionName);
            if (permission == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            // user can only give permissions it has itself
            if (!authService.hasPermission(permissionName))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        User newUser = User.builder()
                           .username(userDTO.getUsername())
                           .email(userDTO.getEmail())
                           .password(userDTO.getPassword())
                           .permissions(permissions)
                           .createdBy(authService.getUser())
                           .loginEnabled(userDTO.getLoginEnabled())
                           .build();
        
        newUser = userService.addNewUser(newUser);

        if (newUser == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return UserDTO.map(newUser);
    }
    

    /**
     * PUT http://localhost:8080/api/v1/users/{id}
     * 
     * update a specific user
     * 
     * @param userId
     * @param userDTO
     * @return updated user
     */
    @PutMapping("users/{id}")
    @PreAuthorize("@authorizationService.canUpdateUser(#userId)")
    public UserDTO updateUser(@PathVariable("id") Long userId, @RequestBody UserDTO userDTO)
        throws ResponseStatusException {

        if (userDTO.getId() != null && userId != userDTO.getId())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        User user = userService.getUserById(userId);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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
            Set<Permission> originalPermissions = user.getPermissions();
            for (String permissionName : userDTO.getPermissions()) {
                Permission permission = permissionService.getPermissionByName(permissionName);
                if (permission == null)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
                // user can only add permissions it has itself
                if (!originalPermissions.contains(permission) &&
                    !authService.hasPermission(permissionName)) {
                    logger.info("Failed to add permission");
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
                permissions.add(permission);
            }
            for (Permission permission : originalPermissions) {
                // user can only remove permissions it has itself
                if (!permissions.contains(permission) &&
                    !authService.hasPermission(permission.getName())) {
                    logger.info("Failed to remove permission");
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN);
                }
            }
            user.setPermissions(permissions);
        }
        if (userDTO.getLoginEnabled() != null) {
            user.setLoginEnabled(userDTO.getLoginEnabled());
        }

        user = userService.updateUser(user);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        
        return UserDTO.map(user);
    }


    @DeleteMapping("users/{id}")
    @PreAuthorize("@authorizationService.canDeleteUser(#userId)")
    public UserDTO deleteUser(@PathVariable("id") Long userId) throws ResponseStatusException {
        User user = userService.getUserById(userId);
        
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        user = userService.deleteUser(user);

        if (user == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

        return UserDTO.map(user);
    }
}
