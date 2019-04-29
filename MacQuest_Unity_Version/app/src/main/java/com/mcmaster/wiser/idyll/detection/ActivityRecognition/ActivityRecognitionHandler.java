package com.mcmaster.wiser.idyll.detection.ActivityRecognition;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


import com.mcmaster.wiser.idyll.detection.ActivityRecognition.classifiers.UpDownDetector;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by steve on 2017-03-28.
 */

public class ActivityRecognitionHandler implements SensorEventListener {

    private static final String TAG = "ActivityRecognition";
    private UpDownDetector upDownDetector;
    private ClassifyListener classifyListener;

    private ArrayBlockingQueue<SensorData> PressureQueue;

    private boolean stopClassify = false;

    public ActivityRecognitionHandler(Context context) {
        upDownDetector = new UpDownDetector(context);
        this.PressureQueue = new ArrayBlockingQueue<SensorData>(1000000);

        new Thread() {
            public void run() {
                try {
                    while (!stopClassify) {
                        /*
                        Log.d(TAG, "PressureQueue.size()=" + PressureQueue.size() +
                                ", AccQueue.size()=" + AccQueue.size() +
                                ", GyroQueue.size()=" + GyroQueue.size());
                        */
                        if (PressureQueue.size() > 0) {
                            SensorData pressureEvent = PressureQueue.take();
                            try {
                                if (classifyListener != null) {
                                    classifyListener.onClassify(classifySensorData(pressureEvent));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private ClassifyData classifySensorData(SensorData event) throws Exception {
        if (event == null) {
            return null;
        }
        int type = event.type;
        ClassifyData classifyData = new ClassifyData();
        if (type == Sensor.TYPE_PRESSURE) {
            //For clean Log
            //Log.d(TAG, "classifySensorData: " + event);
//            if (event.values[1] == 0) {
            float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);
            event.values[1] = altitude;

//            }
            classifyData.classifyType = ClassifyData.CLASSIFY_TYPE_UP_DOWN;
            classifyData.classifyResult = upDownDetector.classify(event);
            return classifyData;
        }
        return classifyData;
    }

    public void setClassifyListener(ClassifyListener classifyListener) {
        this.classifyListener = classifyListener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_PRESSURE) {
            try {
                this.PressureQueue.put(new SensorData(event));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface ClassifyListener {
        public void onClassify(ClassifyData classifyData);
    }

    public void stop() {
        stopClassify = true;
    }
}
