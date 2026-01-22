package de.thb.ea.public_transport_tracker.util.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BoundingBox {
    
    Location northWest;
    Location southEast;

}
