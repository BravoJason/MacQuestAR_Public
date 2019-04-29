package com.mcmaster.wiser.idyll.detection.ActivityRecognition;

import android.hardware.SensorEvent;

import java.util.Arrays;

/**
 * Created by Qiang Xu on 3/30/2017.
 */

public class SensorData {
    public float[] values;
    public long timestamp;
    public int type;

    public SensorData(SensorEvent event) {
        this.values = event.values.clone();
        this.timestamp = event.timestamp;
        this.type = event.sensor.getType();
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "values=" + Arrays.toString(values) +
                ", timestamp=" + timestamp +
                ", type=" + type +
                '}';
    }
}
