package de.thb.ea.public_transport_tracker.controller.auth;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.thb.ea.public_transport_tracker.controller.auth.model.AuthResponseDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.LoginRequestDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.RefreshRequestDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.RegisterRequestDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.RegisterResponseDTO;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.JwtService;
import de.thb.ea.public_transport_tracker.service.RoleService;
import de.thb.ea.public_transport_tracker.service.UserService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth/")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    
    @PostMapping("register")
    public RegisterResponseDTO postMethodName(@RequestBody RegisterRequestDTO request) {
        if (request.getEmail() == null ||
            request.getUsername() == null ||
            request.getPassword() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        if (userService.usernameExists(request.getUsername()))
            return RegisterResponseDTO.usernameAlreadyTaken();
        
        if (userService.emailExists(request.getEmail()))
            return RegisterResponseDTO.emailAlreadyTaken();

        User user = userService.addNewUser(
            User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .roles(new HashSet<>(Arrays.asList(roleService.getRoleByName("USER"))))
                .build()
        );

        if (user == null)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            
        return RegisterResponseDTO.success();
    }


    @PostMapping("login")
    public AuthResponseDTO authenticateUser(@RequestBody LoginRequestDTO request) {
        if (request.getIdentifier() == null ||
            request.getIdentifierType() == null ||
            request.getPassword() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        User user;
        switch (request.getIdentifierType()) {
            case USERNAME:
                user = userService.getUserByUsername(request.getIdentifier());
                break;
            case EMAIL:
                user = userService.getUserByEmail(request.getIdentifier());
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (user == null)
            return AuthResponseDTO.userNotFound();

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                request.getPassword()
            )
        );

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            if (token == null || refreshToken == null)
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

            return AuthResponseDTO.success(user.getId(), token, refreshToken);
        }
            
        
        return AuthResponseDTO.invalidCredentials();
    }


    @PostMapping("refresh")
    public AuthResponseDTO reauthenticateUser(@RequestBody RefreshRequestDTO request) {
        if (request.getRefreshToken() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        User user = userService.getUserByUsername(
            jwtService.extractUsername(request.getRefreshToken())
        );

        if (user == null)
            return AuthResponseDTO.userNotFound();

        if (jwtService.validateRefreshToken(request.getRefreshToken(), user)) {
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            if (token == null || refreshToken == null)
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);

            return AuthResponseDTO.success(user.getId(), token, refreshToken);
        }
        
        return AuthResponseDTO.invalidCredentials();
    }
}
