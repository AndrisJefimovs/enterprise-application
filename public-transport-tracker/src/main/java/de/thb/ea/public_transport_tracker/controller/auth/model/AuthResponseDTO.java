package de.thb.ea.public_transport_tracker.controller.auth.model;

import lombok.Builder;
import lombok.Data;

/**
 * This class will be sent to the client after a LoginRequest or a RefreshRequest.
 */
@Data
@Builder
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
    private Integer statusCode;
    private String statusMessage;


    public static AuthResponseDTO success(String authToken, String refreshToken) {
        return AuthResponseDTO.builder()
                .token(authToken)
                .refreshToken(refreshToken)
                .statusCode(StatusCode.SUCCESS.index())
                .statusMessage("Successfully logged in.")
                .build();
    }

    public static AuthResponseDTO userNotFound() {
        return AuthResponseDTO.builder()
                .statusCode(StatusCode.USER_NOT_FOUND.index())
                .statusMessage("User not found.")
                .build();
    }

    public static AuthResponseDTO invalidCredentials() {
        return AuthResponseDTO.builder()
                .statusCode(StatusCode.INVALID_CREDENTIALS.index())
                .statusMessage("Invalid credentials.")
                .build();
    }
}
