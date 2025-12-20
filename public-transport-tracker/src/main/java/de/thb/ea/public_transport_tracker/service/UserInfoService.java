package de.thb.ea.public_transport_tracker.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.thb.ea.public_transport_tracker.entity.User;
import de.thb.ea.public_transport_tracker.repository.UserRepository;
import lombok.AllArgsConstructor;


/**
 * This service is used by JwtAuthFilter.
 * 
 * The UserDetailsService implementation is seperated from UserService to prevent circular
 * dependencies.
 */
@Service
@AllArgsConstructor
public class UserInfoService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty())
            throw new UsernameNotFoundException("No user with username '" + username + "' found.");
        return user.get();
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
    
}
