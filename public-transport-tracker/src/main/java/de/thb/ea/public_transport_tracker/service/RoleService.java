package de.thb.ea.public_transport_tracker.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.repository.RoleRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RoleService {

    private RoleRepository roleRepository;

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
            return false;
        }

        return true;
    }


    public Boolean roleExists(String roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }
}
