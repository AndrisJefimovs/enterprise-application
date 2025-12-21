package de.thb.ea.public_transport_tracker.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.thb.ea.public_transport_tracker.controller.api.model.UserDTO;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.JwtService;
import de.thb.ea.public_transport_tracker.service.UserService;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@CrossOrigin(
    origins = "http://localhost:4200",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class RESTController {
    
    private UserService userService;
    private JwtService jwtService;

    /**
     * http://localhost:8080/api/v1/users
     * 
     * @return list of all users
     */
    @GetMapping("users")
    public List<UserDTO> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.stream().map(UserDTO::mapSparse).collect(Collectors.toList());
    }
    
    /**
     * http://localhost:8080/api/v1/users/{id}
     * 
     * @param token jwt auth token from header
     * @param userId user id of reqested user.
     * @return user with specified id
     * @throws ResponseStatusException 404 if user not found
     */
    @GetMapping("users/{id}")
    public UserDTO getMethodName(@RequestHeader("Authorization") String token,
                                 @PathVariable("id") Long userId)
            throws ResponseStatusException {
        User user = userService.getUserById(userId);
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        token = token.substring(7); // remove "Bearer " from token
        String clientUsername = jwtService.extractUsername(token);
        User clientUser = userService.getUserByUsername(clientUsername);

        // if user is admin or asks for its own data give all information
        if (clientUser.hasRole("ROLE_ADMIN") || clientUser.getId() == userId) {
            return UserDTO.mapFull(user);
        }
        
        // if user asks for other user give only sparse informaion
        return UserDTO.mapSparse(user);
    }
    

}
