package de.thb.ea.public_transport_tracker.service.exception;

public class TripNotFoundException extends ServiceException {
    
    public TripNotFoundException(String msg) {
        super(msg);
    }

    public static TripNotFoundException fromId(Long tripId) {
        return new TripNotFoundException(String.format(
            "no trip found with id %d", tripId
        ));
    }

    public static TripNotFoundException fromRemote(String remoteOrigin, String remoteId) {
        return new TripNotFoundException(String.format(
            "no trip found with remote origin '%s' and remote id '%s'", remoteOrigin, remoteId
        ));
    }

}
