package de.thb.ea.public_transport_tracker.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Permission;
import de.thb.ea.public_transport_tracker.repository.PermissionRepository;

@Service
public class PermissionService {
    
    private final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Return list of all permissions.
     * 
     * @return List of permissions
     */
    public List<Permission> getAllPermissions() {
        return (List<Permission>) permissionRepository.findAll();
    }

    /**
     * Get permission instance by name.
     * 
     * @param permissionName
     * @return Permission or null if no permission with name exists.
     */
    public Permission getPermissionByName(String permissionName) {
        Optional<Permission> permission = permissionRepository.findByName(permissionName);

        if (permission.isPresent()) {
            return permission.get();
        }
        return null;
    }


    /**
     * Create and add new permission by name.
     * 
     * @param permissionName
     * @return Permission or null if failed.
     */
    public Permission addNewPermission(String permissionName) {
        if (permissionName == null) {
            return null;
        }
        return addNewPermission(Permission.builder().name(permissionName).build());
    }


    /**
     * Add a new permission to the repository.
     * 
     * @param permission
     * @return Permission or null if failed.
     */
    public Permission addNewPermission(Permission permission) {
        if (permission == null) {
            return null;
        }
        permission.forgetId();

        try {
            permissionRepository.save(permission);
        }
        catch (Exception e) {
            logger.info(String.format(
                "Failed to create new permission with name '%s'", permission.getName()
            ));
            logger.debug(e.toString());
            return null;
        }
        logger.info(String.format(
            "Successfully created new permission '%s' with id %d",
            permission.getName(), permission.getId()
        ));
        return permission;
    }


    /**
     * Delete a specific permission from repository.
     * 
     * @param permission
     * @return The deleted permission or null if failed.
     */
    public Permission deletePermission(Permission permission) {
        if (permission == null) {
            return null;
        }

        try {
            permissionRepository.delete(permission);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to delete permission '%s'", permission.getName()));
            logger.debug(e.toString());
            return permission;
        }

        logger.info(String.format("Successfully deleted permission '%s'", permission.getName()));
        return permission;
    }


    /**
     * Check if permission exists by name.
     * 
     * @param permissionName
     * @return true if permission exists; otherwise false.
     */
    public boolean permissionExists(String permissionName) {
        if (permissionName == null) {
            return false;
        }
        return permissionRepository.existsByName(permissionName);
    }

}
