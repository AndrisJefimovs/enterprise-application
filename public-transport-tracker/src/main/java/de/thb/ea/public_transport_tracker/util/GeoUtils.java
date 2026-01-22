package de.thb.ea.public_transport_tracker.util;

import de.thb.ea.public_transport_tracker.util.model.BoundingBox;
import de.thb.ea.public_transport_tracker.util.model.Location;

/**
 * This class provides common functions used for geo location.
 */
public class GeoUtils {
    
    private static final double EARTH_RADIUS_M = 6371000; // meters


    /**
     * This function calculates the two geolocations defining a square around a given center.
     * 
     * Note: because of the earth being a sphere it is actually not a sqare but i dont realy know
     * how to fix it or if it is even possible with this kind of representation.
     * 
     * @param latitude center lat
     * @param longitude center long
     * @param sideLength length of the square sides in meters
     * @return The top left and bottom right corner of the square
     */
    public static BoundingBox getBbox(double centerLat, double centerLon, double sideLength) {

        double halfSide = sideLength / 2.;

        // calculate delta degrees
        double latDelta = Math.toDegrees(halfSide / EARTH_RADIUS_M);
        double lonDelta = Math.toDegrees(
            halfSide / (EARTH_RADIUS_M * Math.cos(Math.toRadians(centerLat)))
        );

        // north-west
        double northLat = centerLat + latDelta;
        double westLon = centerLon - lonDelta;

        // south-east
        double southLat = centerLat - latDelta;
        double eastLon = centerLon + lonDelta;

        return new BoundingBox(
            new Location(northLat, westLon),
            new Location(southLat, eastLon)
        );
    }


    /**
     * Calculate the distance between to locations in meter.
     * 
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     * @return distance in meter
     */
    public static double distanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2.) * Math.sin(dLat / 2.) + Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2.) * Math.sin(dLon / 2.);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_M * c;
    }

}
