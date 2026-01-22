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
import de.thb.ea.public_transport_tracker.service.exception.EmailAlreadyExistsException;
import de.thb.ea.public_transport_tracker.service.exception.UserNotFoundException;
import de.thb.ea.public_transport_tracker.service.exception.UsernameAlreadyExistsException;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
            throw new IllegalArgumentException("null is not a valid value for username");
        }

        User user;
        try {
            user = getUserByUsername(username);
        }
        catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'", username)
            );
        }

        return user;
    }


    /**
     * Get all users from repository.
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        List<User> users;

        try {
            users = (List<User>) userRepository.findAll();
        }
        catch (Exception e) {
            logger.error("Failed to get users from repository.");
            throw e;
        }

        return users;
    }


    /**
     * Get user by id.
     * 
     * @param id    User id.
     * @return      user
     * @throws UserNotFoundException if user is not present in repository
     */
    public User getUserById(Long id) throws UserNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("null is not a valid value for id");
        }

        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw UserNotFoundException.fromUserId(id);
        }

        return user.get();
    }


    /**
     * Get user by username.
     * 
     * @param username  Username of the user.
     * @return          user
     * @throws UserNotFoundException if user is not present in repository
     */
    public User getUserByUsername(String username) throws UserNotFoundException {
        if (username == null) {
            throw new IllegalArgumentException("null is not a valid value for username");
        }

        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isEmpty()) {
            throw UserNotFoundException.fromUsername(username);
        }

        return user.get();
    }


    /**
     * This function returns the user assotiated with the email address.
     * 
     * @param email Email address of the user.
     * @return      user
     * @throws UserNotFoundException if user is not present in repository
     */
    public User getUserByEmail(String email) throws UserNotFoundException {
        if (email == null) {
            throw new IllegalArgumentException("null is not a valid value for email");
        }

        Optional<User> user = userRepository.findByEmail(email);
        
        if (user.isEmpty()) {
            throw UserNotFoundException.fromEmail(email);
        }

        return user.get();
    }

    /**
     * This function tries to add an user to the repository.
     * 
     * @param user  The user that should be added to the repository.
     * @return      The added user instance
     * @throws UsernameAlreadyExistsException if username is already used by another user
     * @throws EmailAlreadyExistsException if email is already used by another user
     */
    public User addNewUser(User user)
        throws UsernameAlreadyExistsException, EmailAlreadyExistsException
    {
        if (user == null) {
            throw new IllegalArgumentException("null is not a valid value for user");
        }
        if (usernameExists(user.getUsername())) {
            throw UsernameAlreadyExistsException.fromUsername(user.getUsername());
        }
        if (user.getEmail() != null && emailExists(user.getEmail())) {
            throw EmailAlreadyExistsException.fromEmail(user.getEmail());
        }

        user.forgetId(); // prevent updating existing users
        user.setPassword(passwordEncoder.encode(user.getPassword())); // hash password
        try {
            user = userRepository.save(user);
        }
        catch (Exception e) {
            logger.info(String.format("Failed to create new user '%s'", user.getUsername()));
            logger.debug(e.toString());
            throw e;
        }
        logger.info(String.format(
            "Successfully created new user '%s' with id %d", user.getUsername(), user.getId()
        ));
        return user;
    }

    /**
     * Update an existing user.
     * <p>
     * To update the password just set the passord attribute to the new cleartext password
     * 
     * @param user  user instance with changed properties; it is identified by id
     * @return      updated user instance
     * @throws UserNotFoundException if the user doesn't exist
     * TODO: fix problem of new password equals old password hash
     */
    public User updateUser(User user) throws UserNotFoundException {
        if (user == null) {
            throw new IllegalArgumentException("null is not a valid value for user");
        }

        User originalUser = getUserById(user.getId());

        // check if password has changed
        if (
            originalUser.getPassword() != null
            && !originalUser.getPassword().equals(user.getPassword())
        )
        {
            // encode the new password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        try {
            user = userRepository.save(user);
        }
        catch (Exception e) {
            // TODO: do some logging
            throw e;
        }
        return user;
    }

    /**
     * Delete a user from the repository.
     * 
     * @param user  The user to delete.
     * @throws UserNotFoundException if user wasn't found
     */
    public void deleteUser(User user) throws UserNotFoundException {
        if (user == null) {
            throw new IllegalArgumentException("Cannot delete user when it is null");
        }

        if (!userIdExists(user.getId())) {
            throw UserNotFoundException.fromUserId(user.getId());
        }

        try {
            userRepository.delete(user);
        }
        catch (Exception e) {
            logger.info(
                "Failed to delete user '%s' with id %d", user.getUsername(), user.getId()
            );
            logger.debug(e.toString());
            throw e;
        }

        logger.info("Deleted user '%s' with id %d", user.getUsername(), user.getId());
    }

    /**
     * Checks if a user with given id exists in repository.
     * 
     * @param userId
     * @return <code>true</code> if user exists; otherwise <code>false</code>.
     */
    public boolean userIdExists(Long userId) {
        return userRepository.existsById(userId);
    }

    /**
     * This function checks if a user with the given username exists.
     * 
     * @param username
     * @return <code>true</code> if user exists; otherwise <code>false</code>.
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * This function checks if a user with the given email already exists.
     * 
     * @param email
     * @return <code>true</code> if user exists; otherwise <code>false</code>.
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * This method updates and returns a new valid refresh version number of an user.
     * 
     * @param user  The user you want to get the new refresh version of.
     * @return      New refresh version
     * @throws UserNotFoundException if user doesn't exist in repository
     */
    public Integer nextRefreshVersion(User user) throws UserNotFoundException {
        user.setRefreshVersion(user.getRefreshVersion() + 1);
        user = updateUser(user);
        return user.getRefreshVersion();
    }

    /**
     * Check if user with id has username.
     * 
     * @param id
     * @param username
     * @return          true if the user with given id has the username.
     */
    public boolean isIdOfUser(Long id, String username) throws UserNotFoundException {
        User user = getUserById(id);
        return user.getUsername().equals(username);
    }

}
