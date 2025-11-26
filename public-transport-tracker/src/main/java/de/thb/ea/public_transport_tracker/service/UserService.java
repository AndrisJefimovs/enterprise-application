package de.thb.ea.public_transport_tracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.repository.UserRepository;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserService {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleService roleService;


    /**
     * Ths function returns the user with the specified username.
     * @param username Username of the user (must not be null).
     * @return user
     * @throws UsernameNotFoundException If no user with spedified username found.
     */
    public User getUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("No user with username '" + username + "' found.");
        }

        return user.get();
    }

    /**
     * This function returns the user assotiated with the email address.
     * 
     * @param email Email address of the user (must not be null).
     * @return user
     * @throws UsernameNotFoundException If no user with specified email address found.
     */
    public User getUserByEmail(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("No user with email '" + email + "' found.");
        }

        return user.get();
    }


    /**
     * This method returns a list of users that have a specified role.
     * 
     * @param roleName
     * @return List of users. If no role with given name exists an empty list is returned.
     */
    public List<User> getUsersByRole(String roleName) {
        return userRepository.findByRoles_Name(roleName);
    }

    /**
     * Get a list of users that have the given role.
     * 
     * @param role
     * @return List of users.
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRoles(role);
    } 

    /**
     * This function tries to add an user to the database. It also tries to create the roles.
     * 
     * @param user The user that should be added to the database (must not be null).
     * @return Ok.
     */
    public Boolean addNewUser(User user) {
        user.forgetId(); // prevent updating existing users
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
            for (Role role : user.getRoles()) {
                // dont't care if it worked
                roleService.addNewRole(role);
            }
            userRepository.save(user);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Delete a user from the database.
     * 
     * @param user The user to delete.
     * @return Ok.
     */
    public Boolean deleteUser(User user) {
        try {
            userRepository.delete(user);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * This function checks if a user with the given username exists.
     * 
     * @param username
     * @return true if the username exists; otherwise false
     * @throws IllegalArgumentException if username is null.
     */
    public Boolean usernameExists(String username) throws IllegalArgumentException {
        if (username == null)
            throw new IllegalArgumentException("username must not be null");
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * This function checks if a user with the given email already exists.
     * 
     * @param email
     * @return true if the email already exists.
     * @throws IllegalArgumentException if email is null.
     */
    public Boolean emailExists(String email) throws IllegalArgumentException {
        if (email == null)
            throw new IllegalArgumentException("email must not be null");
        return userRepository.findByEmail(email).isPresent();
    }

}
