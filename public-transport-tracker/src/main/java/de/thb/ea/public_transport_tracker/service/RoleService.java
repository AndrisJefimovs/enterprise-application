package de.thb.ea.public_transport_tracker.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.repository.RoleRepository;

@Service
public class RoleService {

    private final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Get role instance by name.
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
     * Create and add new Role to the repository.
     * 
     * @param roleName
     * @return Ok.
     */
    public Boolean addNewRole(String roleName) {
        return addNewRole(Role.builder().name(roleName).build());
    }


    /**
     * Add a new role to the repository.
     * 
     * @param role
     * @return Ok.
     */
    public Boolean addNewRole(Role role) {
        role.forgetId();
        
        try {
            roleRepository.save(role);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to create new role with name '%s'", role.getName()));
            logger.debug(e.toString());
            return false;
        }
        logger.info(String.format("Successfully created new role '%s' with id %d", role.getName(),
                                    role.getId()));
        return true;
    }


    /**
     * Delete a specific role instance from database.
     * 
     * @param role
     * @return Ok.
     */
    public Boolean deleteRole(Role role) {
        try {
            roleRepository.delete(role);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to delete role '%s'", role.getName()));
            logger.debug(e.toString());
            return false;
        }
        logger.info(String.format("Successfully deleted role '%s'", role.getName()));
        return true;
    }


    /**
     * Check if a role exists by its name.
     * 
     * @param roleName
     * @return true if role exists, otherwise false.
     */
    public Boolean roleExists(String roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }
}
