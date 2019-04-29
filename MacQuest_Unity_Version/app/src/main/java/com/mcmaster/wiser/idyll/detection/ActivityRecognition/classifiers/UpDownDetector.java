package com.mcmaster.wiser.idyll.detection.ActivityRecognition.classifiers;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import com.mcmaster.wiser.idyll.detection.ActivityRecognition.FileUtils;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.SensorData;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature.UpDownFeatureGenerater;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature.UpDownFeatures;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by steve on 2017-04-08.
 */

public class UpDownDetector {

    private static final String TAG = "UpDownDetector";

    private Context context;

    //[ 1.         -4.7966816   9.20724238 -8.84036968  4.24578647 -0.81597668]
    private static double[] a_LPF = {1.0, -4.79668159982, 9.20724237509, -8.8403696825, 4.24578647329, -0.815976680024};
    //[  2.76887141e-08   1.38443571e-07   2.76887141e-07   2.76887141e-07  1.38443571e-07   2.76887141e-08]
    private static double[] b_LPF = {2.7688714124e-08, 1.3844357062e-07, 2.7688714124e-07, 2.7688714124e-07, 1.3844357062e-07, 2.7688714124e-08};

    /**
     * The threshold of the slope of the altitude.
     */
    //TOBEUPDATE, original 1.5
    private static final float UP_DOWN_SLPOE_THRESHOLD = 1.5f;

    /**
     * State types.
     */
    private static final int STATE_HORIZONTAL = 0;
    private static final int STATE_UP = 1;
    private static final int STATE_DOWN = 2;

    /**
     * State: 0=horizontal 1=up 2=down
     */
    private int currentState = STATE_HORIZONTAL;

    /**
     * Current timestamp for currentState
     */
    private long currentTimestamp = 0L;

    /**
     * For the increasing/decreasing detection, we need last slope.
     */
    private float lastSlope = 0f;

    private UpDownFeatureGenerater upDownFeatureGenerater;

    public UpDownDetector(Context context) {
        upDownFeatureGenerater = new UpDownFeatureGenerater(context);
        this.context = context;
    }

    private ArrayList<Float> slopeList = new ArrayList<>();

    private static final int MIN_SLOPE_LIST_SIZE = 3;

    /**
     * Count the slope that bigger than the threshold times.
     */
    private int biggerThanThresholdTimes = 0;
    /**
     * Count the slope that smaller than the threshold times.
     */
    private int smallerThanThresholdTimes = 0;
    /**
     * If more than TIMES_FOR_UPDATE_STATE times that the slope reading is bigger/smaller than the threshold, then update the state.
     */
    private static final int TIMES_FOR_UPDATE_STATE = 2;


    /**
     * Detect current state.
     */
    private void detectState(float slope, float slope_slope, long time) {
        Log.d(TAG, "slope=" + slope + ", slope_slope=" + slope_slope + ", currentState=" + currentState);
        switch (currentState) {
            case STATE_HORIZONTAL: {
                if (slope > UP_DOWN_SLPOE_THRESHOLD) {
                    if (slope_slope > 0) {
                        biggerThanThresholdTimes++;
                        Log.d(TAG, "biggerThanThresholdTimes=" + biggerThanThresholdTimes);
                    } else {
                        biggerThanThresholdTimes = 0;
                    }
                    smallerThanThresholdTimes = 0;
                    if (biggerThanThresholdTimes > TIMES_FOR_UPDATE_STATE) {
                        Log.d(TAG, "state_change: STATE_HORIZONTAL->STATE_UP biggerThanThresholdTimes=" + biggerThanThresholdTimes);
                        currentState = STATE_UP;
                        biggerThanThresholdTimes = 0;
                    }
                } else if (slope < -UP_DOWN_SLPOE_THRESHOLD) {
                    if (slope_slope < 0) {
                        smallerThanThresholdTimes++;
                        Log.d(TAG, "smallerThanThresholdTimes=" + smallerThanThresholdTimes);
                    } else {
                        smallerThanThresholdTimes = 0;
                    }
                    biggerThanThresholdTimes = 0;
                    if (smallerThanThresholdTimes > TIMES_FOR_UPDATE_STATE) {
                        Log.d(TAG, "state_change: STATE_HORIZONTAL->STATE_DOWN smallerThanThresholdTimes=" + smallerThanThresholdTimes);
                        currentState = STATE_DOWN;
                        smallerThanThresholdTimes = 0;
                    }
                } else {
                    biggerThanThresholdTimes = 0;
                    smallerThanThresholdTimes = 0;
                }
                break;
            }
            case STATE_UP: {
//                if (Math.abs(slope) < UP_DOWN_SLPOE_THRESHOLD) {
                if (false) {
                    currentState = STATE_HORIZONTAL;
                } else {
//                    ArrayList<Integer> peaks = null;
//                    if (slopeList.size() > MIN_SLOPE_LIST_SIZE) {
//                        double[] slopeArray = new double[slopeList.size()];
//                        int i = 0;
//                        for (Float f : slopeList) {
//                            slopeArray[i++] = (f != null ? f : Float.NaN);
//                        }
////                        slopeArray = Filter.filtfilt(b_LPF, a_LPF, slopeArray);
//                        peaks = PeakDetector.findPeaks(slopeArray, false);
//                    }
                    int maxIndex = findMaxIndex();
                    if (slopeList.size() > MIN_SLOPE_LIST_SIZE && maxIndex < slopeList.size() - TIMES_FOR_UPDATE_STATE) {
                        currentState = STATE_HORIZONTAL;
                        Log.d(TAG, "state_change: STATE_UP->STATE_HORIZONTAL maxIndex=" + maxIndex);
                        Log.d(TAG, "state_change: STATE_UP->STATE_HORIZONTAL slopeList=" + slopeList);
                        slopeList.clear();
                    } else {
                        slopeList.add(slope);
                    }
                }
                break;
            }
            case STATE_DOWN: {
//                if (Math.abs(slope) < UP_DOWN_SLPOE_THRESHOLD) {
                if (false) {
                    currentState = STATE_HORIZONTAL;
                } else {
//                    ArrayList<Integer> valleys = null;
//                    if (slopeList.size() > MIN_SLOPE_LIST_SIZE) {
//                        double[] slopeArray = new double[slopeList.size()];
//                        int i = 0;
//                        for (Float f : slopeList) {
//                            slopeArray[i++] = (f != null ? f : Float.NaN);
//                        }
////                        slopeArray = Filter.filtfilt(b_LPF, a_LPF, slopeArray);
//                        valleys = PeakDetector.findPeaks(slopeArray, true);
//                    }
                    int minIndex = findMinIndex();
                    if (slopeList.size() > MIN_SLOPE_LIST_SIZE && minIndex < slopeList.size() - TIMES_FOR_UPDATE_STATE) {
                        currentState = STATE_HORIZONTAL;
                        Log.d(TAG, "state_change: STATE_DOWN->STATE_HORIZONTAL minIndex=" + minIndex);
                        Log.d(TAG, "state_change: STATE_DOWN->STATE_HORIZONTAL slopeList=" + slopeList);
                        slopeList.clear();
                    } else {
                        slopeList.add(slope);
                    }
                }
                break;
            }
        }
        lastSlope = slope;
        currentTimestamp = time;
    }

    private int findMaxIndex() {
        int index = 0;
        if (slopeList == null || slopeList.size() <= 0) {
            return index;
        }
        float max = slopeList.get(0);
        for (int i = 0; i < slopeList.size(); i++) {
            if (slopeList.get(i) > max) {
                max = slopeList.get(i);
                index = i;
            }
        }
        return index;
    }

    private int findMinIndex() {
        int index = 0;
        if (slopeList == null || slopeList.size() <= 0) {
            return index;
        }
        float min = slopeList.get(0);
        for (int i = 0; i < slopeList.size(); i++) {
            if (slopeList.get(i) < min) {
                min = slopeList.get(i);
                index = i;
            }
        }
        return index;
    }

    private File plotFile = null;

    private void writePlotDataFile(float value, float slope, float pre_slope_slope, long currentTime) {
        try {
            if (plotFile == null) {
                String path = FileUtils.getSDCardPath();
                String fileName = path + "/ActivityRecognition/plot/pressure.txt";
                if (FileUtils.fileExists(fileName)) {
                    FileUtils.deleteFile(fileName);
                }
                FileUtils.createFile(fileName);
                plotFile = new File(fileName);
            }
            FileWriter fileWriter = new FileWriter(plotFile, true);
            fileWriter.write(value + ", " + slope + ", " + pre_slope_slope + ", " + +currentTime + "\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Do the classification.
     *
     * @param sensorData
     * @return current state
     */
    public int classify(SensorData sensorData) {
        if (sensorData == null) {
            return currentState;
        }
        long currentTime = System.currentTimeMillis();
        int sensorType = sensorData.type;
        if (sensorType != Sensor.TYPE_PRESSURE) {
            return currentState;
        }
        try {
            UpDownFeatures features = upDownFeatureGenerater.getFeatures(sensorData);
            if (features != null) {
                detectState(features.pre_slope, features.pre_slope_slope, sensorData.timestamp);
                writePlotDataFile(sensorData.values[1], features.pre_slope, features.pre_slope_slope, currentTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentState;
    }

    public float getSlope() {
        return lastSlope;
    }

}
