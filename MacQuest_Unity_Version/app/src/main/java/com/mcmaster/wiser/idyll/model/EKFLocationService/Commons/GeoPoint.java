package com.mcmaster.wiser.idyll.model.EKFLocationService.Commons;

/**
 * Reference: https://github.com/maddevsio/mad-location-manager
 */

public class GeoPoint {
    public double Latitude;
    public double Longitude;

    public GeoPoint(double latitude, double longitude) {
        Latitude = latitude;
        Longitude = longitude;
    }
}
