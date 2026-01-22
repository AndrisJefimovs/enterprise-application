package de.thb.ea.public_transport_tracker.service.exception;

public class EmailAlreadyExistsException extends UserAlreadyExists {

    public EmailAlreadyExistsException(String msg) {
        super(msg);
    }

    public static EmailAlreadyExistsException fromEmail(String email) {
        return new EmailAlreadyExistsException(String.format(
            "A user with email '%s' already exists", email
        ));
    }
    
}
