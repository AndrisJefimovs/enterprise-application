package de.thb.ea.public_transport_tracker.service.exception;

public class TripAlreadyExistsException extends ServiceException {

    public TripAlreadyExistsException(String msg) {
        super(msg);
    }

    public static TripAlreadyExistsException fromRemote(String remoteOrigin, String remoteId) {
        return new TripAlreadyExistsException(String.format(
            "a trip with remote origin '%s' and remote id '%s' already exists",
            remoteOrigin, remoteId
        ));
    }
    
}
