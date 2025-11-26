package de.thb.ea.public_transport_tracker.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.thb.ea.public_transport_tracker.entity.User;
import java.util.List;
import de.thb.ea.public_transport_tracker.entity.Role;



@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByRoles(Role role);
    List<User> findByRoles_Name(String roleName);

}
