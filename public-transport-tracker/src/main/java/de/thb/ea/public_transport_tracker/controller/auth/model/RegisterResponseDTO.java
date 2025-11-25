package de.thb.ea.public_transport_tracker.controller.auth.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDTO {
    private Integer statusCode;
    private String statusMessage;


    public static RegisterResponseDTO success() {
        return RegisterResponseDTO.builder()
                .statusCode(StatusCode.SUCCESS)
                .statusMessage("New user is registered.")
                .build();
    }


    public static RegisterResponseDTO usernameAlreadyTaken() {
        return RegisterResponseDTO.builder()
                .statusCode(StatusCode.USERNAME_ALREADY_TAKEN)
                .statusMessage("The username is already taken.")
                .build();
    }


    public static RegisterResponseDTO emailAlreadyTaken() {
        return RegisterResponseDTO.builder()
                .statusCode(StatusCode.EMAIL_ALREADY_TAKEN)
                .statusMessage("The email is already taken.")
                .build();
    }


}
