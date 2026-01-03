package de.thb.ea.public_transport_tracker.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.repository.UserRepository;


@Service
public class UserService implements UserDetailsService {
    
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this. passwordEncoder = passwordEncoder;
    }


    /**
     * Get UserDetails by username.
     * Just for implementation of UserDetailsService.
     * 
     * @param username Username of the user (must not be null).
     * @return user
     * @throws UsernameNotFoundException If no user with specified username found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) {
            throw new UsernameNotFoundException("null is not a valid username.");
        }
        User user = getUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'",
                                                              username));
        }
        return user;
    }


    /**
     * Get all users from repository.
     * 
     * @return list of all users.
     */
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }


    /**
     * Get user by id.
     * 
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
     * Get user by username.
     * 
     * @param username Username of the user.
     * @return user or null if failed.
     */
    public User getUserByUsername(String username) {
        if (username == null)
            return null;

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }


    /**
     * This function returns the user assotiated with the email address.
     * 
     * @param email Email address of the user.
     * @return user of null if failed.
     */
    public User getUserByEmail(String email) {
        if (email == null)
            return null;

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return null;
        }
        return user.get();
    }

    /**
     * This function tries to add an user to the repository.
     * 
     * @param user The user that should be added to the repository.
     * @return The added user instance or null if failed.
     */
    public User addNewUser(User user) {
        if (user == null)
            return null;

        user.forgetId(); // prevent updating existing users
        user.setPassword(passwordEncoder.encode(user.getPassword())); // hash password
        try {
            user = userRepository.save(user);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to create new user '%s'", user.getUsername()));
            logger.debug(e.toString());
            return null;
        }
        logger.info(String.format("Successfully created new user '%s' with id %d",
                                  user.getUsername(), user.getId()));
        return user;
    }

    /**
     * Tries to update a user if it exists.
     * 
     * @param user
     * @return updated user instance or null if failed.
     */
    public User updateUser(User user) {
        if (user == null)
            return null;
        try {
            User oldUser = getUserById(user.getId());
            if (oldUser != null) {
                if (user.getPassword() != null && !user.getPassword().equals(oldUser.getPassword()))
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                user = userRepository.save(user);
            }
        }
        catch (Exception e) {
            return null;
        }
        return user;
    }

    /**
     * Delete a user from the repository.
     * 
     * @param user The user to delete.
     * @return The deleted user instance or null if failed.
     */
    public User deleteUser(User user) {
        if (user == null)
            return null;

        try {
            userRepository.delete(user);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to delete user '%s' with id %d", user.getUsername(),
                                        user.getId()));
            logger.debug(e.toString());
            return null;
        }
        logger.info(String.format("Deleted user '%s' with id %d", user.getUsername(),
                                    user.getId()));
        return user;
    }

    /**
     * Checks if a user with given id exists in repository.
     * 
     * @param userId
     * @return true if user exists; otherwise false
     */
    public boolean userIdExists(Long userId) {
        if (userId == null)
            return false;
        return userRepository.findById(userId).isPresent();
    }

    /**
     * This function checks if a user with the given username exists.
     * 
     * @param username
     * @return true if the username exists; otherwise false
     */
    public boolean usernameExists(String username) {
        if (username == null)
            return false;
        return userRepository.findByUsername(username).isPresent();
    }

    /**
     * This function checks if a user with the given email already exists.
     * 
     * @param email
     * @return true if the email already exists; otherwise false.
     */
    public boolean emailExists(String email) {
        if (email == null)
            return false;
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * This method updates and returns a new valid refresh version number of an user.
     * 
     * @param user
     * @return New refresh version or null if something went wrong.
     */
    public Integer nextRefreshVersion(User user) {
        user.setRefreshVersion(user.getRefreshVersion() + 1);
        if (updateUser(user) == null)
            return null;
        return user.getRefreshVersion();
    }

    /**
     * Check if user with id has username.
     * 
     * @param id
     * @param username
     * @return true if the user with id has the username.
     */
    public boolean isIdOfUser(Long id, String username) {
        User user = getUserById(id);
        if (user == null)
            return false;
        return user.getUsername().equals(username);
    }

}
