package com.mcmaster.wiser.idyll.collection;

/**
 * Created by steve on 2017-07-15.
 */

public class LightSensorData {

    public float light;

    public long timestamp;

    @Override
    public String toString() {
        return "LightSensorData{" +
                "light=" + light +
                ", timestamp=" + timestamp +
                '}';
    }
}
