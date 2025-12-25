package de.thb.ea.public_transport_tracker.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.thb.ea.public_transport_tracker.controller.api.model.UserDTO;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.AuthService;
import de.thb.ea.public_transport_tracker.service.UserService;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * http://localhost:8080/api/v1/users
     * 
     * This gives only sparse informations about users.
     * 
     * @return list of all users
     */
    @GetMapping("users")
    @PreAuthorize("@authService.hasPermission('READ_USER')")
    public List<UserDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream().map(UserDTO::mapSparse).collect(Collectors.toList());
    }
    
    /**
     * http://localhost:8080/api/v1/users/{id}
     * 
     * Gives detailed information if the user is authorized for it (admin or own user);
     * otherwise gives only sparse information.
     * 
     * @param userId user id of reqested user.
     * @return user with specified id
     * @throws ResponseStatusException 404 if user not found; 401 if token is invalid
     */
    @GetMapping("users/{id}")
    @PreAuthorize("@authService.hasPermission('READ_USER')")
    public UserDTO getUser(@PathVariable("id") Long userId)
            throws ResponseStatusException {

        User user = userService.getUserById(userId);
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // if user is admin or user asks for its own data give all information
        if (authService.hasRole("SUPER_ADMIN") || authService.hasId(userId)) {
            return UserDTO.mapFull(user);
        }
        
        // if user asks for other user give only sparse informaion
        return UserDTO.mapSparse(user);
    }

}
