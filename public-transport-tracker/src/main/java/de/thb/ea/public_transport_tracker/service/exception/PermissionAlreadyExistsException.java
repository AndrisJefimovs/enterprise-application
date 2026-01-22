package de.thb.ea.public_transport_tracker.service.exception;

public class PermissionAlreadyExistsException extends ServiceException {

    public PermissionAlreadyExistsException(String msg) {
        super(msg);
    }

    public static PermissionAlreadyExistsException fromName(String permissionName) {
        return new PermissionAlreadyExistsException(String.format(
            "permission with name '%s' already exists", permissionName)
        );
    }
    
}
