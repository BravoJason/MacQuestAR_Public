package com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.util.Log;

import com.mcmaster.wiser.idyll.detection.ActivityRecognition.Constants;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.FileUtils;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.SensorData;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.classifiers.WekaWrapperUpDown;

import org.apache.commons.math3.stat.regression.SimpleRegression;

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

public class UpDownFeatureGenerater {

    private static final String TAG = "UDFeatureGenerater";
    private static final long SLOPE_SCALE = 10000000000l;

    private Context context;
    private List<String> dataFileList = new ArrayList<String>();
    private String featureFileName;
    private List<UpDownFeatures> featureList = new ArrayList<>();

    /**
     * Window size = 1 second = 10^9 Nanosecond
     */
    public static final int WINDOW_SIZE = 1000 * 1000 * 1000;

    // TOBEUPDATE, 5
    private static final long WINDOW_SIZE_PRESSURE = 5 * 1000000000L;

    private DataWindow dataWindow = new DataWindow(WINDOW_SIZE);

    //TOBEUPDATE, origianl 0.1
    private static final float THRESHOLD_PRESSURE_SLOPE = 0.1f;

    private RawData preRawData = new RawData();

    private List<Float> altitudeList = new ArrayList<>();
    private List<Float> slopeList = new ArrayList<>();
    private List<Long> preTimestampList = new ArrayList<>();
    private List<Long> slopeTimeList = new ArrayList<>();

    public UpDownFeatureGenerater(Context context) {
        String path = FileUtils.getSDCardPath();
        dataFileList.add("data/android.sensor.pressure.txt");
        featureFileName = path + "/ActivityRecognition/features/features_plan_b_ud1.arff";
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void generateFeatures() throws IOException {
        Log.d(TAG, "Start generate features...");
        InputStreamReader preIS = new InputStreamReader(context.getAssets().open(dataFileList.get(0)), "UTF-8");
        BufferedReader preBR = new BufferedReader(preIS);

        while (true) {
            String pre_line = preBR.readLine();
            if (pre_line == null) {
                break;
            }
            String[] values = pre_line.split(", ");
            float x = Float.parseFloat(values[0]);
            float y = Float.parseFloat(values[1]);
            float z = Float.parseFloat(values[2]);
            long timestamp = Long.parseLong(values[3]);
            String label = values[4];
            if (dataWindow.getStartTime() == 0) {
                dataWindow.setStartTime(timestamp);
            }
            if (dataWindow.inWindow(timestamp) > 0) {
                addDataToWindow(label);
                UpDownFeatures features = getFeatures();
                dataWindow.moveWindow();

                if (features != null) {
                    featureList.add(features);
                }

                int deleteSize = 0;
                long newStartTime = dataWindow.getEndTime() - WINDOW_SIZE_PRESSURE;
                for (long time : preTimestampList) {
                    if (time < newStartTime) {
                        deleteSize++;
                    }
                }
                for (int i = 0; i < deleteSize; i++) {
                    preTimestampList.remove(0);
                    altitudeList.remove(0);
                }
            }
            addPressureSlopeWindow(y, timestamp);
        }
        Log.d(TAG, "End generate features...");

        writeFeaturesFile();

    }

    private void addDataToWindow(String label) {
        if (Constants.CLASS_LABEL_WALKING.equals(label) || Constants.CLASS_LABEL_STANDING.equals(label)) {
            label = "horizontal";
        } else if (Constants.CLASS_LABEL_UPSTAIRS.equals(label) || Constants.CLASS_LABEL_ESCALATOR_UPSTAIRS.equals(label)) {
            label = "up";
        } else if (Constants.CLASS_LABEL_DOWNSTAIRS.equals(label) || Constants.CLASS_LABEL_ESCALATOR_DOWNSTAIRS.equals(label)) {
            label = "down";
        } else {
            label = "";
        }

        if (slopeTimeList == null || slopeTimeList.size() <= 0) {
            return;
        }
        List<Float> yList = new ArrayList<>();
        List<Long> tList = new ArrayList<>();
        List<String> labelList = new ArrayList<>();
        int deleteSize = 0;
        for (int i = 0; i < slopeTimeList.size(); i++) {
            long time = slopeTimeList.get(i);
            if (dataWindow.inWindow(time) == 0) {
                yList.add(slopeList.get(i));
                tList.add(slopeTimeList.get(i));
                labelList.add(label);
            } else if (dataWindow.inWindow(time) < 0) {
                deleteSize++;
            }
        }
        for (int i = 0; i < deleteSize; i++) {
            slopeList.remove(0);
            slopeTimeList.remove(0);
        }
        preRawData.yList = yList;
        preRawData.timeList = tList;
        preRawData.labelList = labelList;
    }

    private void addPressureSlopeWindow(float altitude, long timestamp) {
        altitudeList.add(altitude);
        preTimestampList.add(timestamp);

        SimpleRegression regression = new SimpleRegression();

        if (preTimestampList.size() > 1) {
            for (int i = 0; i < preTimestampList.size(); i++) {
                regression.addData(preTimestampList.get(i), altitudeList.get(i));
            }
            double slope = regression.getSlope() * SLOPE_SCALE;
            // Add threshold
            if (Math.abs(slope) < THRESHOLD_PRESSURE_SLOPE) {
                slope = 0;
            }
            slopeList.add((float) slope);
            slopeTimeList.add(timestamp);
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
                "@attribute pre_slope numeric\n" +
                "@attribute pre_slope_slope numeric\n" +
                "@attribute label {horizontal,up,down}\n" +
                "\n" +
                "@data\n");
        for (UpDownFeatures features : featureList) {
            fileWriter.write(features.getFeatureString() + "\n");
        }
        fileWriter.close();
        Log.d(TAG, "end write features file");
    }

    public Instances getDataset(boolean addLabel) {
        ArrayList<Attribute> allAttr = new ArrayList<Attribute>();
        String[] featureNames = UpDownFeatures.getFeatureNameArray();
        for (String featureName : featureNames) {
            allAttr.add(new Attribute(featureName));
        }
        if (addLabel) {
            ArrayList<String> labelItems = new ArrayList<String>(2);
            labelItems.add(Constants.CLASS_LABEL_HORIZONTAL);
            labelItems.add(Constants.CLASS_LABEL_UP);
            labelItems.add(Constants.CLASS_LABEL_DOWN);
            Attribute mClassAttribute = new Attribute(Constants.CLASS_LABEL_KEY, labelItems);
            allAttr.add(mClassAttribute);
        }
        return new Instances(Constants.FEAT_SET_NAME, allAttr, Constants.FEATURE_SET_CAPACITY);
    }

    private UpDownFeatures getFeatures() {
        Map<String, Integer> labelTimes = new HashMap<>();
        for (String l : preRawData.labelList) {
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
        UpDownFeatures features = new UpDownFeatures(label);
        // Barometre
        if (preRawData.timeList.size() > 1) {
            SimpleRegression regression = new SimpleRegression();
            for (int i = 0; i < preRawData.yList.size(); i++) {
                regression.addData(preRawData.timeList.get(i), preRawData.yList.get(i));
            }
            double slope = regression.getSlope();
            features.pre_slope_slope = (float) slope * SLOPE_SCALE;

            if (preRawData.yList != null && preRawData.yList.size() > 0) {
                features.pre_slope = preRawData.yList.get(preRawData.yList.size() - 1);
            }
        }
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

    public Instance getInstanceFromFeature(Instances dataset, UpDownFeatures features, Attribute mClassAttribute) {
        if (features == null) {
            return null;
        }
        Instance instance = new DenseInstance(UpDownFeatures.NUMBER_OF_ATTRIBUTES);
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

    private long lastClassifyTime = 0;

    public double classify(SensorData event) throws Exception {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        int type = event.type;
        if (type != Sensor.TYPE_PRESSURE) {
            return lastClassify;
        }

        if (dataWindow.getStartTime() == 0) {
            dataWindow.setStartTime(event.timestamp);
        }

        UpDownFeatures features = null;
        if (dataWindow.inWindow(event.timestamp) > 0) {
            addDataToWindow(null);
            features = getFeatures();
            dataWindow.moveWindow();

            int deleteSize = 0;
            long newStartTime = dataWindow.getEndTime() - WINDOW_SIZE_PRESSURE;
            for (long time : preTimestampList) {
                if (time < newStartTime) {
                    deleteSize++;
                }
            }
            for (int i = 0; i < deleteSize; i++) {
                preTimestampList.remove(0);
                altitudeList.remove(0);
            }
        }
        addPressureSlopeWindow(y, event.timestamp);

        if (features == null) {
            return lastClassify;
        }
        Instances dataset = getDataset(false);
        dataset.setClassIndex(dataset.numAttributes() - 1);
        Instance inst = getInstanceFromFeature(dataset, features, null);
        WekaWrapperUpDown wekaWrapper = new WekaWrapperUpDown();
        lastClassify = wekaWrapper.classifyInstance(inst);
        float timeInterval = (float) ((event.timestamp - lastClassifyTime) / 1000000000f);
        Log.d(TAG, "New Features:" + features + ", time interval=" + timeInterval + ", classify=" + lastClassify);
        lastClassifyTime = event.timestamp;
        return lastClassify;
    }

    public UpDownFeatures getFeatures(SensorData event) throws Exception {
        UpDownFeatures features = null;
        float x = event.values[0];
        float y = event.values[1];

        float z = event.values[2];




        int type = event.type;
        if (type != Sensor.TYPE_PRESSURE) {
            return features;
        }

        if (dataWindow.getStartTime() == 0) {
            dataWindow.setStartTime(event.timestamp);
        }

        if (dataWindow.inWindow(event.timestamp) > 0) {
            addDataToWindow(null);
            features = getFeatures();
            dataWindow.moveWindow();

            int deleteSize = 0;
            long newStartTime = dataWindow.getEndTime() - WINDOW_SIZE_PRESSURE;
            for (long time : preTimestampList) {
                if (time < newStartTime) {
                    deleteSize++;
                }
            }
            for (int i = 0; i < deleteSize; i++) {
                preTimestampList.remove(0);
                altitudeList.remove(0);
            }
        }
        addPressureSlopeWindow(y, event.timestamp);

        return features;
    }

}
