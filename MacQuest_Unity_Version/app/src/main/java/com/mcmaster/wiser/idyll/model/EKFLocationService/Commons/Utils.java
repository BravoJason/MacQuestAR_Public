package com.mcmaster.wiser.idyll.model.EKFLocationService.Commons;

/**
 * Reference: https://github.com/maddevsio/mad-location-manager
 */

public class Utils {

    public static int hertz2periodUs(double hz) { return (int) (1.0e6 / (1.0 / hz));}
    public static long nano2milli(long nano) {return (long) (nano / 1e6);}

    //todo move to some another better place
    public static double ACCELEROMETER_X_DEFAULT_DEVIATION = 0.60127198504;
    public static double ACCELEROMETER_Y_DEFAULT_DEVIATION = 0.39250477704;
    public static final int GPS_MIN_TIME = 2000;
    public static final int GPS_MIN_DISTANCE = 0;
    public static final int SENSOR_DEFAULT_FREQ_HZ = 50;
    public static final double DEFAULT_VEL_FACTOR = 1.0;
    public static final double DEFAULT_POS_FACTOR = 1.0;
    public static final int DEFAULT_ZUPT_DATA_WINDOW = 5;
    public static final float ZUPT_C1_MIN_THRESHOLD = 0.5f;
    public static final float ZUPT_C1_MAX_THRESHOLD = 2.0f;
    public static final float ZUPT_C2_THRESHOLD = 0.4f;
    public static final float ZUPT_C3_THRESHOLD = 3.0f;
    public static final boolean USE_ZUPT = true;
    public static final boolean USE_GPS_SPEED = false;
    public static final int ThREAD_SLEEP_TIME = 500;


    //!!

    public enum LogMessageType {
        KALMAN_ALLOC,
        KALMAN_PREDICT,
        KALMAN_UPDATE,
        GPS_DATA,
        ABS_ACC_DATA,
        FILTERED_GPS_DATA
    }
}
