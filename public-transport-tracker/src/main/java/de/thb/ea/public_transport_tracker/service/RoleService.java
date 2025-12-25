package de.thb.ea.public_transport_tracker.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.repository.RoleRepository;

@Service
public class RoleService {

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<Role> getAllRoles() {
        return (List<Role>) roleRepository.findAll();
    }

    /**
     * Get role instance by name.
     * 
     * @param roleName
     * @return Role or null if no role with role name exists.
     */
    public Role getRoleByName(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        
        if (role.isPresent())
            return role.get();

        return null;
    }


    /**
     * Create and add new role by name.
     * 
     * @param roleName
     * @return Role or null if failed.
     */
    public Role addNewRole(String roleName) {
        if (roleName == null)
            return null;
        return addNewRole(Role.builder().name(roleName).build());
    }


    /**
     * Add a new role to the repository.
     * 
     * @param role
     * @return Role or null if failed.
     */
    public Role addNewRole(Role role) {
        if (role == null)
            return null;

        role.forgetId();
        
        try {
            role = roleRepository.save(role);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to create new role with name '%s'", role.getName()));
            logger.debug(e.toString());
            return null;
        }
        logger.info(String.format("Successfully created new role '%s' with id %d", role.getName(),
                                  role.getId()));
        return role;
    }


    /**
     * Delete a specific role from repository.
     * 
     * @param role
     * @return The deleted role or null if failed.
     */
    public Role deleteRole(Role role) {
        if (role == null)
            return null;

        try {
            roleRepository.delete(role);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to delete role '%s'", role.getName()));
            logger.debug(e.toString());
            return null;
        }
        logger.info(String.format("Successfully deleted role '%s'", role.getName()));
        return role;
    }


    /**
     * Update an existing role.
     * 
     * @param role
     * @return updated role instance or null if failed.
     */
    public Role updateRole(Role role) {
        if (role == null)
            return null;

        try {
            if (roleExists(role.getName()))
                role = roleRepository.save(role);
        }
        catch (Exception e) {
            return null;
        }
        return role;
    }


    /**
     * Check if a role exists by name.
     * 
     * @param roleName
     * @return true if role exists; otherwise false.
     */
    public boolean roleExists(String roleName) {
        if (roleName == null)
            return false;
        return roleRepository.findByName(roleName).isPresent();
    }
}
