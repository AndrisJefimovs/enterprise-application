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
    
}
