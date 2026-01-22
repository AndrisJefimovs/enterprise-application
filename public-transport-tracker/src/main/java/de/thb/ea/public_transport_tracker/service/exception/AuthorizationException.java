package de.thb.ea.public_transport_tracker.service.exception;

public class AuthorizationException extends ServiceRuntimeException {

    public AuthorizationException() {
        super();
    }
    
    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }

}
