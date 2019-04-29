package com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature;

/**
 * Created by steve on 2017-04-02.
 */

public class WalkStandFeatures {

    public static final int NUMBER_OF_ATTRIBUTES = 36;

    public WalkStandFeatures(String label) {
        this.label = label;
    }

    /**
     * zero_crossing_count
     */
    public float acc_zcc_x;
    public float acc_zcc_y;
    public float acc_zcc_z;
    public float gyro_zcc_x;
    public float gyro_zcc_y;
    public float gyro_zcc_z;

    /**
     * mean_absolute_value
     */
    public float acc_mav_x;
    public float acc_mav_y;
    public float acc_mav_z;
    public float gyro_mav_x;
    public float gyro_mav_y;
    public float gyro_mav_z;

    /**
     * mean_absolute_value_slope
     */
    public float acc_mavs_x;
    public float acc_mavs_y;
    public float acc_mavs_z;
    public float gyro_mavs_x;
    public float gyro_mavs_y;
    public float gyro_mavs_z;

    /**
     * slope_sign_changes
     */
    public float acc_ssc_x;
    public float acc_ssc_y;
    public float acc_ssc_z;
    public float gyro_ssc_x;
    public float gyro_ssc_y;
    public float gyro_ssc_z;

    /**
     * waveform_length
     */
    public float acc_wl_x;
    public float acc_wl_y;
    public float acc_wl_z;
    public float gyro_wl_x;
    public float gyro_wl_y;
    public float gyro_wl_z;

    /**
     * root_mean_square
     */
    public float acc_rms_x;
    public float acc_rms_y;
    public float acc_rms_z;
    public float gyro_rms_x;
    public float gyro_rms_y;
    public float gyro_rms_z;

    /**
     * Label of the features
     */
    public String label;

    public float[] getFeatureArray() {
        return new float[]{acc_zcc_x, acc_zcc_y, acc_zcc_z, gyro_zcc_x, gyro_zcc_y, gyro_zcc_z,
                acc_mav_x, acc_mav_y, acc_mav_z, gyro_mav_x, gyro_mav_y, gyro_mav_z,
                acc_mavs_x, acc_mavs_y, acc_mavs_z, gyro_mavs_x, gyro_mavs_y, gyro_mavs_z,
                acc_ssc_x, acc_ssc_y, acc_ssc_z, gyro_ssc_x, gyro_ssc_y, gyro_ssc_z,
                acc_wl_x, acc_wl_y, acc_wl_z, gyro_wl_x, gyro_wl_y, gyro_wl_z,
                acc_rms_x, acc_rms_y, acc_rms_z, gyro_rms_x, gyro_rms_y, gyro_rms_z
        };
    }

    public static String[] getFeatureNameArray() {
        return new String[]{"acc_zcc_x", "acc_zcc_y", "acc_zcc_z", "gyro_zcc_x", "gyro_zcc_y", "gyro_zcc_z",
                "acc_mav_x", "acc_mav_y", "acc_mav_z", "gyro_mav_x", "gyro_mav_y", "gyro_mav_z",
                "acc_mavs_x", "acc_mavs_y", "acc_mavs_z", "gyro_mavs_x", "gyro_mavs_y", "gyro_mavs_z",
                "acc_ssc_x", "acc_ssc_y", "acc_ssc_z", "gyro_ssc_x", "gyro_ssc_y", "gyro_ssc_z",
                "acc_wl_x", "acc_wl_y", "acc_wl_z", "gyro_wl_x", "gyro_wl_y", "gyro_wl_z",
                "acc_rms_x", "acc_rms_y", "acc_rms_z", "gyro_rms_x", "gyro_rms_y", "gyro_rms_z"
        };
    }

    public String getFeatureString() {
        return acc_zcc_x + ", " + acc_zcc_y + ", " + acc_zcc_z +
                ", " + gyro_zcc_x + ", " + gyro_zcc_y + ", " + gyro_zcc_z +
                ", " + acc_mav_x + ", " + acc_mav_y + ", " + acc_mav_z +
                ", " + gyro_mav_x + ", " + gyro_mav_y + ", " + gyro_mav_z +
                ", " + acc_mavs_x + ", " + acc_mavs_y + ", " + acc_mavs_z +
                ", " + gyro_mavs_x + ", " + gyro_mavs_y + ", " + gyro_mavs_z +
                ", " + acc_ssc_x + ", " + acc_ssc_y + ", " + acc_ssc_z +
                ", " + gyro_ssc_x + ", " + gyro_ssc_y + ", " + gyro_ssc_z +
                ", " + acc_wl_x + ", " + acc_wl_y + ", " + acc_wl_z +
                ", " + gyro_wl_x + ", " + gyro_wl_y + ", " + gyro_wl_z +
                ", " + acc_rms_x + ", " + acc_rms_y + ", " + acc_rms_z +
                ", " + gyro_rms_x + ", " + gyro_rms_y + ", " + gyro_rms_z +
                ", " + label;
    }

}
