package de.thb.ea.public_transport_tracker.service.exception;

public abstract class ServiceException extends Exception {
    
    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
      super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

}
