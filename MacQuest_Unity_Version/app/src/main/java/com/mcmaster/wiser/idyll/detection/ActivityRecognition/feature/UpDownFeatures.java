package com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature;

/**
 * Created by steve on 2017-04-02.
 */

public class UpDownFeatures {

    public static final int NUMBER_OF_ATTRIBUTES = 3;

    public UpDownFeatures(String label) {
        this.label = label;
    }

    public float pre_slope;
    public float pre_slope_slope;

    /**
     * Label of the features
     */
    public String label;

    public float[] getFeatureArray() {
        return new float[]{pre_slope, pre_slope_slope};
    }

    public static String[] getFeatureNameArray() {
        return new String[]{"pre_slope", "pre_slope_slope"};
    }

    public String getFeatureString() {
        return pre_slope + ", " + pre_slope_slope + ", " + label;
    }

    @Override
    public String toString() {
        return pre_slope + ", " + pre_slope_slope + ", " + label;
    }
}
