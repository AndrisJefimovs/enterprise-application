package de.thb.ea.public_transport_tracker.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.thb.ea.public_transport_tracker.entity.Permission;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);
    
}