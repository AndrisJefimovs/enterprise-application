package de.thb.ea.public_transport_tracker.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.Role;
import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.repository.UserRepository;


@Service
public class UserService {
    
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this. passwordEncoder = passwordEncoder;
        this.roleService = roleService;
    }

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
            // TODO: add parameter to disable role creation
            for (Role role : user.getRoles()) {
                // dont't care if it worked
                // if it does not work the role probably already exists 
                roleService.addNewRole(role);
            }
            userRepository.save(user);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to create new user '%s'", user.getUsername()));
            logger.debug(e.toString());
            return false;
        }
        logger.info(String.format("Successfully created new user '%s' with id %d",
                                    user.getUsername(), user.getId()));
        return true;
    }

    /**
     * Tries to update a user if it exists.
     * 
     * @param user
     * @return true if user was successfully updated; oterwise false
     */
    public Boolean updateUser(User user) {
        try {
            if (userIdExists(user.getId()))
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
            logger.info(String.format("Failed to delete user '%s' with id %d", user.getUsername(),
                                        user.getId()));
            logger.debug(e.toString());
            return false;
        }
        logger.info(String.format("Deleted user '%s' with id %d", user.getUsername(),
                                    user.getId()));
        return true;
    }

    /**
     * Checks if a user with given id exists in repository.
     * 
     * @param userId
     * @return true if user exists; otherwise false
     * @throws IllegalArgumentException if userId is null.
     */
    public Boolean userIdExists(Long userId) throws IllegalArgumentException {
        if (userId == null)
            throw new IllegalArgumentException("User Id must not be null");
        return userRepository.findById(userId).isPresent();
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
