package de.thb.ea.public_transport_tracker.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Permission;
import de.thb.ea.public_transport_tracker.repository.PermissionRepository;
import de.thb.ea.public_transport_tracker.service.exception.PermissionAlreadyExistsException;
import de.thb.ea.public_transport_tracker.service.exception.PermissionNotFoundException;

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
     * @param permissionName    Name of the permission.
     * @return                  Permission instance
     * @throws PermissionNotFoundException if no permission with name exists in repository
     */
    public Permission getPermissionByName(String permissionName)
        throws PermissionNotFoundException
    {
        if (permissionName == null) {
            throw new IllegalArgumentException("null is not a valid value for permissionName");
        }

        Optional<Permission> permission = permissionRepository.findByName(permissionName);

        if (permission.isEmpty()) {
            throw PermissionNotFoundException.fromName(permissionName);
        }
        return permission.get();
    }


    /**
     * Create and add new permission by name.
     * 
     * @param permissionName
     * @return Permission instance
     * @throws PermissionAlreadyExists if there already is a permission with the given name
     */
    public Permission addNewPermission(String permissionName)
        throws PermissionAlreadyExistsException
    {
        if (permissionName == null) {
            throw new IllegalArgumentException("null is not a valid value for permissionName");
        }
        
        return addNewPermission(Permission.builder().name(permissionName).build());
    }


    /**
     * Add a new permission to the repository.
     * 
     * @param permission
     * @return Permission instance
     */
    public Permission addNewPermission(Permission permission)
        throws PermissionAlreadyExistsException
    {
        if (permission == null) {
            throw new IllegalArgumentException("null is not a valid value for permission");
        }
        if (permissionExists(permission.getName())) {
            throw PermissionAlreadyExistsException.fromName(permission.getName());
        }

        permission.forgetId();

        try {
            permissionRepository.save(permission);
        }
        catch (Exception e) {
            logger.info(
                "Failed to create new permission with name '%s'", permission.getName()
            );
            logger.debug(e.toString());
            throw e;
        }
        logger.info(
            "Successfully created new permission '%s' with id %d",
            permission.getName(), permission.getId()
        );
        return permission;
    }


    /**
     * Delete a specific permission from repository.
     * 
     * @param permission
     * @throws PermissionNotFoundException if permission doesn't exist in repository
     */
    public void deletePermission(Permission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("null is not a valid value for permission");
        }

        try {
            permissionRepository.delete(permission);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to delete permission '%s'", permission.getName()));
            logger.debug(e.toString());
            throw e;
        }

        logger.info(String.format("Successfully deleted permission '%s'", permission.getName()));
    }


    /**
     * Check if permission exists by name.
     * 
     * @param permissionName
     * @return true if permission exists; otherwise false.
     */
    public boolean permissionExists(String permissionName) {
        if (permissionName == null) {
            throw new IllegalArgumentException("null is not a valid value for permissionName");
        }
        return permissionRepository.existsByName(permissionName);
    }

}
