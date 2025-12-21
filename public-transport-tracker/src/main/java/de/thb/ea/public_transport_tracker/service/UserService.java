package de.thb.ea.public_transport_tracker.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * Get all users.
     * @return list of all users.
     */
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    /**
     * Get user by id.
     * @param id User id.
     * @return user or null if no user with this id exists.
     */
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent())
            return user.get();
        
        return null;
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
     * This function tries to add an user to the database. It also creates the roles.
     * 
     * @param user The user that should be added to the database (must not be null).
     * @return Ok.
     * @throws IllegalArgumentException if user is null.
     */
    public Boolean addNewUserWithRoles(User user) throws IllegalArgumentException {
        if (user == null)
            throw new IllegalArgumentException("user must not be null");

        user.forgetId(); // prevent updating existing users
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // create not existing roles
        Set<Role> newRoles = new HashSet<>();

        for (Role role : user.getRoles()) {
            Role r = roleService.getRoleByName(role.getName());
            if (r == null) {
                if (!roleService.addNewRole(role)) {
                    logger.warn(String.format("User '%s' was not created because the role '%s'"+
                                " could not be created", user.getUsername(), role.getName()));
                    return false;
                }
                else {
                    newRoles.add(role);
                }
            }
            else {
                newRoles.add(r);
            }
        }
        user.setRoles(newRoles);

        try {
            userRepository.save(user);
        }
        catch (Exception e) {
            logger.warn(String.format("Failed to create new user '%s'", user.getUsername()));
            logger.debug(e.toString());
            return false;
        }
        logger.info(String.format("Successfully created new user '%s' with id %d",
                                    user.getUsername(), user.getId()));
        return true;
    }

    /**
     * This function tries to add an user to the database.
     * 
     * @param user The user that should be added to the database (must not be null).
     * @return Ok.
     * @throws IllegalArgumentException if user is null.
     */
    public Boolean addNewUser(User user) throws IllegalArgumentException {
        if (user == null)
            throw new IllegalArgumentException("user must not be null");

        Set<Role> newRoles = new HashSet<>();

        for (Role role : user.getRoles()) {
            Role r = roleService.getRoleByName(role.getName());
            if (r == null) {
                logger.warn(String.format("Failed to add user '%s' because role '%s' does not" +
                            " exist", user.getUsername(), role.getName()));
                return false;
            }
            newRoles.add(r);
        }
        user.setRoles(newRoles);

        user.forgetId(); // prevent updating existing users
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try {
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
