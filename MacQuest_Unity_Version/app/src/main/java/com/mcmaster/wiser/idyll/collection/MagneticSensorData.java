package com.mcmaster.wiser.idyll.collection;

/**
 * Created by steve on 2017-07-15.
 */

public class MagneticSensorData {

    public float x;
    public float y;
    public float z;

    public long timestamp;

    @Override
    public String toString() {
        return "MagneticSensorData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", timestamp=" + timestamp +
                '}';
    }
}
