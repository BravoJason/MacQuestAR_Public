package com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.util.Log;

import com.mcmaster.wiser.idyll.detection.ActivityRecognition.Constants;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.FileUtils;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.SensorData;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.classifiers.WekaWrapperWalkStand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by steve on 2017-03-19.
 */

public class WalkStandFeatureGenerater {

    private static final String TAG = "WSFeatureGenerater";

    private Context context;
    private List<String> dataFileList = new ArrayList<String>();
    private String featureFileName;
    private List<WalkStandFeatures> featureList = new ArrayList<>();

    private float last_acc_mav_x = Float.MAX_VALUE;
    private float last_acc_mav_y = Float.MAX_VALUE;
    private float last_acc_mav_z = Float.MAX_VALUE;
    private float last_gyro_mav_x = Float.MAX_VALUE;
    private float last_gyro_mav_y = Float.MAX_VALUE;
    private float last_gyro_mav_z = Float.MAX_VALUE;

    /**
     * Window size = 1 second = 10^9 Nanosecond
     */
    private static final int WINDOW_SIZE = 1000 * 1000 * 1000;

    private DataWindow dataWindow = new DataWindow(WINDOW_SIZE);

    /**
     * Threshold for zero corssing count
     */
    private static final float THRESHOLD_ZCC = 0.1f;
    /**
     * Threshold for slope_sign_changes
     */
    private static final float THRESHOLD_SSC = 0.1f;

    private RawData accRawData = new RawData();
    private RawData gyroRawData = new RawData();

    public WalkStandFeatureGenerater(Context context) {
        String path = FileUtils.getSDCardPath();
        dataFileList.add("data/android.sensor.accelerometer2.txt");
        dataFileList.add("data/android.sensor.gyroscope2.txt");
        featureFileName = path + "/ActivityRecognition/features/features_plan_b_ws2.arff";
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void generateFeatures() throws IOException {
        Log.d(TAG, "Start generate features...");
        InputStreamReader accIS = new InputStreamReader(context.getAssets().open(dataFileList.get(0)), "UTF-8");
        BufferedReader accBR = new BufferedReader(accIS);
        InputStreamReader gyroIS = new InputStreamReader(context.getAssets().open(dataFileList.get(1)), "UTF-8");
        BufferedReader gyroBR = new BufferedReader(gyroIS);

        // Handle Accelerometer
        while (true) {
            String acc_line = accBR.readLine();
            if (acc_line == null) {
                break;
            }
            String[] acc_values = acc_line.split(", ");
            float acc_x = Float.parseFloat(acc_values[0]);
            float acc_y = Float.parseFloat(acc_values[1]);
            float acc_z = Float.parseFloat(acc_values[2]);
            long acc_timestamp = Long.parseLong(acc_values[3]);
            String acc_label = acc_values[4];
            if (dataWindow.getStartTime() == 0) {
                dataWindow.setStartTime(acc_timestamp);
            }
            if (dataWindow.inWindow(acc_timestamp) > 0) {
                while (true) {
                    // Handle Gyroscope
                    String gyro_line = gyroBR.readLine();
                    if (gyro_line == null) {
                        break;
                    }
                    String[] gyro_values = gyro_line.split(", ");
                    float gyro_x = Float.parseFloat(gyro_values[0]);
                    float gyro_y = Float.parseFloat(gyro_values[1]);
                    float gyro_z = Float.parseFloat(gyro_values[2]);
                    long gyro_timestamp = Long.parseLong(gyro_values[3]);
                    String gyro_label = gyro_values[4];
                    if (dataWindow.inWindow(gyro_timestamp) > 0) {
                        WalkStandFeatures features = getFeatures(accRawData, gyroRawData);
                        dataWindow.moveWindow();
                        if (features != null) {
                            featureList.add(features);
                        }

                        int deleteSizeAcc = 0;
                        for (long time : accRawData.timeList) {
                            if (dataWindow.inWindow(time) < 0) {
                                deleteSizeAcc++;
                            }
                        }
                        for (int i = 0; i < deleteSizeAcc; i++) {
                            accRawData.moveToNextWindow();
                        }
                        int deleteSizeGyro = 0;
                        for (long time : gyroRawData.timeList) {
                            if (dataWindow.inWindow(time) < 0) {
                                deleteSizeGyro++;
                            }
                        }
                        for (int i = 0; i < deleteSizeGyro; i++) {
                            gyroRawData.moveToNextWindow();
                        }
                        addDataToWindow(gyro_x, gyro_y, gyro_z, gyro_timestamp, gyro_label, Sensor.TYPE_GYROSCOPE);
                        break;
                    }
                    addDataToWindow(gyro_x, gyro_y, gyro_z, gyro_timestamp, gyro_label, Sensor.TYPE_GYROSCOPE);
                }
            }
            addDataToWindow(acc_x, acc_y, acc_z, acc_timestamp, acc_label, Sensor.TYPE_ACCELEROMETER);
        }

        Log.d(TAG, "End generate features...");

        writeFeaturesFile();

    }

    private void addDataToWindow(float x, float y, float z, long timestamp, String label, int sensorType) {
        if (Constants.CLASS_LABEL_WALKING.equals(label) || Constants.CLASS_LABEL_UPSTAIRS.equals(label)
                || Constants.CLASS_LABEL_DOWNSTAIRS.equals(label)) {
            label = "walking";
        } else if (Constants.CLASS_LABEL_STANDING.equals(label) || Constants.CLASS_LABEL_ESCALATOR_UPSTAIRS.equals(label)
                || Constants.CLASS_LABEL_ESCALATOR_DOWNSTAIRS.equals(label)) {
            label = "standing";
        } else {
            label = "";
        }

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER: {
                accRawData.xList.add(x);
                accRawData.yList.add(y);
                accRawData.zList.add(z);
                accRawData.timeList.add(timestamp);
                accRawData.labelList.add(label);
                break;
            }
            case Sensor.TYPE_GYROSCOPE: {
                gyroRawData.xList.add(x);
                gyroRawData.yList.add(y);
                gyroRawData.zList.add(z);
                gyroRawData.timeList.add(timestamp);
                gyroRawData.labelList.add(label);
                break;
            }
        }
    }

    private boolean isLabelChanged(String currentLabel, String newLabel) {
        if (currentLabel == null) {
            return newLabel != null;
        } else {
            return !currentLabel.equals(newLabel);
        }
    }

    private void writeFeaturesFile() throws IOException {
        Log.d(TAG, "start write features file");
        if (FileUtils.fileExists(featureFileName)) {
            FileUtils.deleteFile(featureFileName);
        }

        FileUtils.createFile(featureFileName);
        File mFeatureFile = new File(featureFileName);
        FileWriter fileWriter = new FileWriter(mFeatureFile);
        fileWriter.write("@relation features\n" +
                "\n" +
                "@attribute acc_zcc_x numeric\n" +
                "@attribute acc_zcc_y numeric\n" +
                "@attribute acc_zcc_z numeric\n" +
                "@attribute gyro_zcc_x numeric\n" +
                "@attribute gyro_zcc_y numeric\n" +
                "@attribute gyro_zcc_z numeric\n" +
                "@attribute acc_mav_x numeric\n" +
                "@attribute acc_mav_y numeric\n" +
                "@attribute acc_mav_z numeric\n" +
                "@attribute gyro_mav_x numeric\n" +
                "@attribute gyro_mav_y numeric\n" +
                "@attribute gyro_mav_z numeric\n" +
                "@attribute acc_mavs_x numeric\n" +
                "@attribute acc_mavs_y numeric\n" +
                "@attribute acc_mavs_z numeric\n" +
                "@attribute gyro_mavs_x numeric\n" +
                "@attribute gyro_mavs_y numeric\n" +
                "@attribute gyro_mavs_z numeric\n" +
                "@attribute acc_ssc_x numeric\n" +
                "@attribute acc_ssc_y numeric\n" +
                "@attribute acc_ssc_z numeric\n" +
                "@attribute gyro_ssc_x numeric\n" +
                "@attribute gyro_ssc_y numeric\n" +
                "@attribute gyro_ssc_z numeric\n" +
                "@attribute acc_wl_x numeric\n" +
                "@attribute acc_wl_y numeric\n" +
                "@attribute acc_wl_z numeric\n" +
                "@attribute gyro_wl_x numeric\n" +
                "@attribute gyro_wl_y numeric\n" +
                "@attribute gyro_wl_z numeric\n" +
                "@attribute acc_rms_x numeric\n" +
                "@attribute acc_rms_y numeric\n" +
                "@attribute acc_rms_z numeric\n" +
                "@attribute gyro_rms_x numeric\n" +
                "@attribute gyro_rms_y numeric\n" +
                "@attribute gyro_rms_z numeric\n" +
                "@attribute label {standing,walking}\n" +
                "\n" +
                "@data\n");
        for (WalkStandFeatures features : featureList) {
            fileWriter.write(features.getFeatureString() + "\n");
        }
        fileWriter.close();
        Log.d(TAG, "end write features file");
    }

    public Instances getDataset(boolean addLabel) {
        ArrayList<Attribute> allAttr = new ArrayList<Attribute>();
        String[] featureNames = WalkStandFeatures.getFeatureNameArray();
        for (String featureName : featureNames) {
            allAttr.add(new Attribute(featureName));
        }
        if (addLabel) {
            ArrayList<String> labelItems = new ArrayList<String>();
            labelItems.add(Constants.CLASS_LABEL_STANDING);
            labelItems.add(Constants.CLASS_LABEL_WALKING);
            Attribute mClassAttribute = new Attribute(Constants.CLASS_LABEL_KEY, labelItems);
            allAttr.add(mClassAttribute);
        }
        return new Instances(Constants.FEAT_SET_NAME, allAttr, Constants.FEATURE_SET_CAPACITY);
    }

    public WalkStandFeatures getFeatures(RawData accRawData, RawData gyroRawData) {
        Map<String, Integer> labelTimes = new HashMap<>();
        for (String l : accRawData.labelList) {
            int times = 0;
            if (labelTimes.get(l) != null) {
                times = labelTimes.get(l);
            }
            times++;
            labelTimes.put(l, times);
        }
        for (String l : gyroRawData.labelList) {
            int times = 0;
            if (labelTimes.get(l) != null) {
                times = labelTimes.get(l);
            }
            times++;
            labelTimes.put(l, times);
        }
        int maxTimes = 0;
        String label = "";
        for (Map.Entry<String, Integer> entry : labelTimes.entrySet()) {
            String l = entry.getKey();
            int times = entry.getValue();
            if (times > maxTimes) {
                label = l;
            }
        }
        WalkStandFeatures features = new WalkStandFeatures(label);
        WalkStandFeatures lastFeatures = null;
        if (featureList != null && featureList.size() > 0) {
            lastFeatures = featureList.get(featureList.size() - 1);
        }
        // Accelerometer
        features.acc_zcc_x = getZeroCrossingCount(accRawData.xList, THRESHOLD_ZCC);
        features.acc_zcc_y = getZeroCrossingCount(accRawData.yList, THRESHOLD_ZCC);
        features.acc_zcc_z = getZeroCrossingCount(accRawData.zList, THRESHOLD_ZCC);
        features.acc_mav_x = getMeanAbsoluteValue(accRawData.xList);
        features.acc_mav_y = getMeanAbsoluteValue(accRawData.yList);
        features.acc_mav_z = getMeanAbsoluteValue(accRawData.zList);
        if (lastFeatures != null && last_acc_mav_x != Float.MAX_VALUE) {
            lastFeatures.acc_mavs_x = getMeanAbsoluteValueSlop(last_acc_mav_x, features.acc_mav_x);
            lastFeatures.acc_mavs_y = getMeanAbsoluteValueSlop(last_acc_mav_y, features.acc_mav_y);
            lastFeatures.acc_mavs_z = getMeanAbsoluteValueSlop(last_acc_mav_z, features.acc_mav_z);
        }
        last_acc_mav_x = features.acc_mav_x;
        last_acc_mav_y = features.acc_mav_y;
        last_acc_mav_z = features.acc_mav_z;
        features.acc_ssc_x = getSlopeSignChanges(accRawData.xList, THRESHOLD_SSC);
        features.acc_ssc_y = getSlopeSignChanges(accRawData.yList, THRESHOLD_SSC);
        features.acc_ssc_z = getSlopeSignChanges(accRawData.zList, THRESHOLD_SSC);
        features.acc_wl_x = getWaveformLength(accRawData.xList);
        features.acc_wl_y = getWaveformLength(accRawData.yList);
        features.acc_wl_z = getWaveformLength(accRawData.zList);
        features.acc_rms_x = getRootMeanSquare(accRawData.xList);
        features.acc_rms_y = getRootMeanSquare(accRawData.yList);
        features.acc_rms_z = getRootMeanSquare(accRawData.zList);
        // Gyroscope
        features.gyro_zcc_x = getZeroCrossingCount(gyroRawData.xList, THRESHOLD_ZCC);
        features.gyro_zcc_y = getZeroCrossingCount(gyroRawData.yList, THRESHOLD_ZCC);
        features.gyro_zcc_z = getZeroCrossingCount(gyroRawData.zList, THRESHOLD_ZCC);
        features.gyro_mav_x = getMeanAbsoluteValue(gyroRawData.xList);
        features.gyro_mav_y = getMeanAbsoluteValue(gyroRawData.yList);
        features.gyro_mav_z = getMeanAbsoluteValue(gyroRawData.zList);
        if (lastFeatures != null && last_gyro_mav_x != Float.MAX_VALUE) {
            lastFeatures.gyro_mavs_x = getMeanAbsoluteValueSlop(last_gyro_mav_x, features.gyro_mav_x);
            lastFeatures.gyro_mavs_y = getMeanAbsoluteValueSlop(last_gyro_mav_y, features.gyro_mav_y);
            lastFeatures.gyro_mavs_z = getMeanAbsoluteValueSlop(last_gyro_mav_z, features.gyro_mav_z);
        }
        last_gyro_mav_x = features.gyro_mav_x;
        last_gyro_mav_y = features.gyro_mav_y;
        last_gyro_mav_z = features.gyro_mav_z;
        features.gyro_ssc_x = getSlopeSignChanges(gyroRawData.xList, THRESHOLD_SSC);
        features.gyro_ssc_y = getSlopeSignChanges(gyroRawData.yList, THRESHOLD_SSC);
        features.gyro_ssc_z = getSlopeSignChanges(gyroRawData.zList, THRESHOLD_SSC);
        features.gyro_wl_x = getWaveformLength(gyroRawData.xList);
        features.gyro_wl_y = getWaveformLength(gyroRawData.yList);
        features.gyro_wl_z = getWaveformLength(gyroRawData.zList);
        features.gyro_rms_x = getRootMeanSquare(gyroRawData.xList);
        features.gyro_rms_y = getRootMeanSquare(gyroRawData.yList);
        features.gyro_rms_z = getRootMeanSquare(gyroRawData.zList);
        return features;
    }

    private int getZeroCrossingCount(List<Float> signal, float threshold) {
        if (signal == null || signal.size() == 0) {
            return 0;
        }
        List<Float> newSignal = new ArrayList<Float>(signal.size());
        for (Float s : signal) {
            if (s > threshold || s < -threshold) {
                newSignal.add(s);
            }
        }
        int result = 0;
        for (int i = 0; i < newSignal.size() - 1; i++) {
            if ((newSignal.get(i) > 0 && newSignal.get(i + 1) <= 0) ||
                    (newSignal.get(i) < 0 && newSignal.get(i + 1) >= 0)) {
                result++;
            }
        }
        return result;
    }

    private float getMeanAbsoluteValue(List<Float> signal) {
        float addResult = 0;
        for (Float s : signal) {
            addResult += Math.abs(s);
        }
        return addResult / signal.size();
    }

    private float getMeanAbsoluteValueSlop(float current_mav_x, float next_mav_x) {
        return next_mav_x - current_mav_x;
    }

    private int getSlopeSignChanges(List<Float> signal, float threshold) {
        if (signal == null || signal.size() <= 0) {
            return 0;
        }
        int times = 0;
        for (int i = 0; i < signal.size(); i++) {
            float xi = signal.get(i);
            float xi_1 = xi;
            float xi1 = xi;
            if (i > 0) {
                xi_1 = signal.get(i - 1);
            }
            if (i < signal.size() - 1) {
                xi1 = signal.get(i + 1);
            }
            if (xi > Math.max(xi_1, xi1) || xi < Math.min(xi_1, xi1)) {
                if (Math.max(Math.abs(xi - xi_1), Math.abs(xi - xi1)) > threshold) {
                    times++;
                }
            }
        }
        return times;
    }

    private float getWaveformLength(List<Float> signal) {
        if (signal == null || signal.size() <= 0) {
            return 0;
        }
        float result = 0f;
        for (int i = 1; i < signal.size(); i++) {
            result += Math.abs(signal.get(i) - signal.get(i - 1));
        }
        return result;
    }

    private float getRootMeanSquare(List<Float> signal) {
        if (signal == null || signal.size() <= 0) {
            return 0;
        }
        float square_value = 0f;
        for (Float s : signal) {
            square_value += s * s;
        }
        square_value = square_value / signal.size();
        return (float) Math.sqrt(square_value);
    }

    public Instance getInstanceFromFeature(Instances dataset, WalkStandFeatures features, Attribute mClassAttribute) {
        if (features == null) {
            return null;
        }
        Instance instance = new DenseInstance(WalkStandFeatures.NUMBER_OF_ATTRIBUTES);
        instance.setDataset(dataset);
        float[] featureList = features.getFeatureArray();
        for (int i = 0; i < featureList.length; i++) {
            instance.setValue(i, featureList[i]);
        }
        if (mClassAttribute != null) {
            instance.setValue(mClassAttribute, features.label);
        }
        dataset.add(instance);
        return instance;
    }

    private double lastClassify = 0.0;

    public double classify(SensorData event) throws Exception {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        int type = event.type;
        if (type != Sensor.TYPE_GYROSCOPE && type != Sensor.TYPE_ACCELEROMETER) {
            return lastClassify;
        }

        if (dataWindow.getStartTime() == 0) {
            dataWindow.setStartTime(event.timestamp);
        }

        WalkStandFeatures features = null;

        if (event.timestamp > dataWindow.getEndTime()) {
            features = getFeatures(accRawData, gyroRawData);
            dataWindow.moveWindow();

            int deleteSizeAcc = 0;
            for (long time : accRawData.timeList) {
                if (dataWindow.inWindow(time) < 0) {
                    deleteSizeAcc++;
                }
            }
            for (int i = 0; i < deleteSizeAcc; i++) {
                accRawData.moveToNextWindow();
            }
            int deleteSizeGyro = 0;
            for (long time : gyroRawData.timeList) {
                if (dataWindow.inWindow(time) < 0) {
                    deleteSizeGyro++;
                }
            }
            for (int i = 0; i < deleteSizeGyro; i++) {
                gyroRawData.moveToNextWindow();
            }
        }

        addDataToWindow(x, y, z, event.timestamp, null, type);

        if (features == null) {
            return lastClassify;
        }
        Instances dataset = getDataset(false);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        Instance inst = getInstanceFromFeature(dataset, features, null);
        WekaWrapperWalkStand wekaWrapperWalkStand = new WekaWrapperWalkStand();
        lastClassify = wekaWrapperWalkStand.classifyInstance(inst);
        return lastClassify;
    }
}
