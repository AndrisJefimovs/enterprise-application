package de.thb.ea.public_transport_tracker.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import de.thb.ea.public_transport_tracker.entity.Trip;;

@Repository
public interface TripRepository extends CrudRepository<Trip, Long>  {
    
    Optional<Trip> findByTripId(String tripId);

}
