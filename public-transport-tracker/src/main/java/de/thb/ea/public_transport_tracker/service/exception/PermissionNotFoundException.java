package de.thb.ea.public_transport_tracker.service.exception;

public class PermissionNotFoundException extends ServiceException {
    
    public PermissionNotFoundException(String msg) {
        super(msg);
    }

    public static PermissionNotFoundException fromId(Long permissionId) {
        return new PermissionNotFoundException(String.format(
            "No permission found with id %d", permissionId
        ));
    }

    public static PermissionNotFoundException fromName(String permissionName) {
        return new PermissionNotFoundException(String.format(
            "No permission found with name '%s'", permissionName
        ));
    }

}
