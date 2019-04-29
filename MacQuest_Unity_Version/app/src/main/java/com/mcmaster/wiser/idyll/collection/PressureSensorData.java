package com.mcmaster.wiser.idyll.collection;

/**
 * Created by steve on 2017-07-15.
 */

public class PressureSensorData {
    public float pressure;
    public long timestamp;

    @Override
    public String toString() {
        return "PressureSensorData{" +
                "pressure=" + pressure +
                ", timestamp=" + timestamp +
                '}';
    }
}
