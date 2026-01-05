package de.thb.ea.public_transport_tracker.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.thb.ea.public_transport_tracker.controller.auth.model.AuthResponseDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.LoginRequestDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.RefreshRequestDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.RegisterRequestDTO;
import de.thb.ea.public_transport_tracker.controller.auth.model.RegisterResponseDTO;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.service.JwtService;
import de.thb.ea.public_transport_tracker.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    
    @PostMapping("register")
    public ResponseEntity<RegisterResponseDTO> postMethodName(@RequestBody RegisterRequestDTO request) {
        if (request.getEmail() == null ||
            request.getUsername() == null ||
            request.getPassword() == null)
            return ResponseEntity.badRequest().build();

        if (userService.usernameExists(request.getUsername()))
            return ResponseEntity.ok().body(RegisterResponseDTO.usernameAlreadyTaken());
        
        if (userService.emailExists(request.getEmail()))
            return ResponseEntity.ok().body(RegisterResponseDTO.emailAlreadyTaken());

        User user = userService.addNewUser(
            User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build()
        );

        if (user == null)
            return ResponseEntity.internalServerError().build();
            
        return ResponseEntity.ok().body(RegisterResponseDTO.success());
    }


    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody LoginRequestDTO request) {
        if (request.getIdentifier() == null ||
            request.getIdentifierType() == null ||
            request.getPassword() == null)
            return ResponseEntity.badRequest().build();

        User user;
        switch (request.getIdentifierType()) {
            case USERNAME:
                user = userService.getUserByUsername(request.getIdentifier());
                break;
            case EMAIL:
                user = userService.getUserByEmail(request.getIdentifier());
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        if (user == null)
            return ResponseEntity.ok().body(AuthResponseDTO.userNotFound());

        if (!user.isLoginEnabled())
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

            return ResponseEntity.ok().body(
                AuthResponseDTO.success(user.getId(), token, refreshToken)
            );
        }
            
        
        return ResponseEntity.ok().body(AuthResponseDTO.invalidCredentials());
    }


    @PostMapping("refresh")
    public ResponseEntity<AuthResponseDTO> reauthenticateUser(@RequestBody RefreshRequestDTO request) {
        
        if (request.getRefreshToken() == null)
            return ResponseEntity.badRequest().build();

        String username;

        try {
            username = jwtService.extractUsername(request.getRefreshToken());
        }
        catch (ExpiredJwtException e) {
            return ResponseEntity.ok().body(AuthResponseDTO.invalidRefreshToken());
        }

        User user = userService.getUserByUsername(username);

        if (user == null)
            return ResponseEntity.ok().body(AuthResponseDTO.userNotFound());

        if (jwtService.validateRefreshToken(request.getRefreshToken(), user)) {
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            if (token == null || refreshToken == null)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

            return ResponseEntity.ok().body(
                AuthResponseDTO.success(user.getId(), token, refreshToken)
            );
        }
        
        return ResponseEntity.ok().body(AuthResponseDTO.invalidCredentials());
    }
}
