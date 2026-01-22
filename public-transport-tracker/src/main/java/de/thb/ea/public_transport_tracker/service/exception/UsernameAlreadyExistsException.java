package de.thb.ea.public_transport_tracker.service.exception;

public class UsernameAlreadyExistsException extends UserAlreadyExists {
    
    public UsernameAlreadyExistsException(String msg) {
        super(msg);
    }

    public static UsernameAlreadyExistsException fromUsername(String username) {
        return new UsernameAlreadyExistsException(String.format(
            "A user with username '%s' already exists", username
        ));
    }

}
