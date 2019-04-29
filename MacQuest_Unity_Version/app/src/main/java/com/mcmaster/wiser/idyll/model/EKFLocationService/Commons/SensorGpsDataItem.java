package com.mcmaster.wiser.idyll.model.EKFLocationService.Commons;

/**
 * Reference: https://github.com/maddevsio/mad-location-manager
 */

import android.support.annotation.NonNull;

public class SensorGpsDataItem implements Comparable<SensorGpsDataItem> {
    double timestamp;
    double gpsLat;
    double gpsLon;
    double gpsAlt;
    double absNorthAcc;
    double absEastAcc;
    double absUpAcc;
    double speed;
    double course;
    double posErr;
    double velErr;
    boolean zuptStatus;

    public static final double NOT_INITIALIZED = 361.0;

    public SensorGpsDataItem(double timestamp,
                             double gpsLat,
                             double gpsLon,
                             double gpsAlt,
                             double absNorthAcc,
                             double absEastAcc,
                             double absUpAcc,
                             double speed,
                             double course,
                             double posErr,
                             double velErr,
                             double declination,
                             boolean zuptStatus) {
        this.timestamp = timestamp;
        this.gpsLat = gpsLat;
        this.gpsLon = gpsLon;
        this.gpsAlt = gpsAlt;
        this.absNorthAcc = absNorthAcc;
        this.absEastAcc = absEastAcc;
        this.absUpAcc = absUpAcc;
        this.speed = speed;
        this.course = course;
        this.posErr = posErr;
        this.velErr = velErr;
        this.zuptStatus = zuptStatus;

        this.absNorthAcc = absNorthAcc * Math.cos(declination) + absEastAcc * Math.sin(declination);
        this.absEastAcc = absEastAcc * Math.cos(declination) - absNorthAcc * Math.sin(declination);
    }

    public double getTimestamp() {
        return timestamp;
    }

    public double getGpsLat() {
        return gpsLat;
    }

    public double getGpsLon() {
        return gpsLon;
    }

    public double getGpsAlt() {
        return gpsAlt;
    }

    public double getAbsNorthAcc() {
        return absNorthAcc;
    }

    public double getAbsEastAcc() {
        return absEastAcc;
    }

    public double getAbsUpAcc() {
        return absUpAcc;
    }

    public double getSpeed() {
        return speed;
    }

    public double getCourse() {
        return course;
    }

    public double getPosErr() {
        return posErr;
    }

    public double getVelErr() {
        return velErr;
    }

    public boolean getZUPTstatus(){
        return zuptStatus;
    }

    @Override
    public int compareTo(@NonNull SensorGpsDataItem o) {
        return (int) (this.timestamp - o.timestamp);
    }

}
