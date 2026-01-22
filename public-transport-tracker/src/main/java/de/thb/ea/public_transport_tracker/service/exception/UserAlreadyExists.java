package de.thb.ea.public_transport_tracker.service.exception;

public abstract class UserAlreadyExists extends ServiceException {

    public UserAlreadyExists() {
        super();
    }

    public UserAlreadyExists(String message) {
      super(message);
    }

    public UserAlreadyExists(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExists(Throwable cause) {
        super(cause);
    }
    
}
