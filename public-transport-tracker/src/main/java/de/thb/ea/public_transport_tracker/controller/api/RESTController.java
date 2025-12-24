package de.thb.ea.public_transport_tracker.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.thb.ea.public_transport_tracker.controller.api.model.UserDTO;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.JwtService;
import de.thb.ea.public_transport_tracker.service.UserService;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;



@RestController
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class RESTController {
    
    private UserService userService;
    private JwtService jwtService;

    /**
     * http://localhost:8080/api/v1/users
     * 
     * This gives only sparse informations about users.
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
     * Gives detailed information if the user is authorized for it (admin or own user);
     * otherwise gives only sparse information.
     * 
     * @param token jwt auth token from header
     * @param userId user id of reqested user.
     * @return user with specified id
     * @throws ResponseStatusException 404 if user not found; 401 if token is invalid
     */
    @GetMapping("users/{id}")
    public UserDTO getUser(@RequestHeader("Authorization") String token,
                                 @PathVariable("id") Long userId)
            throws ResponseStatusException {
        User user = userService.getUserById(userId);
        
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        User clientUser = getUserFromToken(token);

        // if user is admin or user asks for its own data give all information
        if (clientUser.hasRole("ROLE_ADMIN") || clientUser.getId() == userId) {
            return UserDTO.mapFull(user);
        }
        
        // if user asks for other user give only sparse informaion
        return UserDTO.mapSparse(user);
    }


    /**
     * http://localhost:8080/api/v1/users/me
     * 
     * @param token jwt auth token from header
     * @return own user object
     * @throws ResponseStatusException 401 token is invalid
     */
    @GetMapping("users/me")
    public UserDTO getClientUser(@RequestHeader("Authorization") String token)
        throws ResponseStatusException {

        User user = getUserFromToken(token);
        return UserDTO.mapFull(user);
    }
    


    /**
     * Retruns user associated with token.
     * @param token if form of "Bearer <TOKEN>"
     * @return user
     * @throws ResponseStatusException 401 if token is expired or user not found.
     */
    private User getUserFromToken(String token) throws ResponseStatusException {
        token = token.substring(7); // cut of "Bearer "
        String username;
        User user;
        try {
            username = jwtService.extractUsername(token);
            user = userService.loadUserByUsername(username);
        } 
        catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return user;
    }

}
