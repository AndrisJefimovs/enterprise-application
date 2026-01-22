package de.thb.ea.public_transport_tracker.repository.remote.exception;

public abstract class RemoteRepositoryException extends RuntimeException {
    public RemoteRepositoryException() {
        super();
    }

    public RemoteRepositoryException(String message) {
      super(message);
    }

    public RemoteRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteRepositoryException(Throwable cause) {
        super(cause);
    }
}
