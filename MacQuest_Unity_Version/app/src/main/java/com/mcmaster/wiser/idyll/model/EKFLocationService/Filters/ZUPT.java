package com.mcmaster.wiser.idyll.model.EKFLocationService.Filters;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class ZUPT {


    private Queue<Float> accQueue;

    static final String TAG = "ZUPT";
    private double accQueueSum;

    private int windowSize;

    private int maxQueueSize;


    public ZUPT(int windowSize) {
        accQueue = new LinkedList<Float>();

        accQueueSum = 0;

        this.windowSize = windowSize;

        this.maxQueueSize = windowSize * 2 + 1;
    }




    private float calcVectorLength(float[] accReading) {
        return (float) Math.sqrt(accReading[0] * accReading[0] + accReading[1] * accReading[1] + accReading[2] * accReading[2]);
    }

    //Decrease AccQueueSum when remove the reading data from queue.
    private void decreaseAccQueueSum(float accReading) {
        accQueueSum -= accReading;
    }

    //Increase the AccQueueSUm when add one reading into queue.
    private void increaseAccQueueSum(float accReading) {
        accQueueSum += accReading;
    }

    private double calcMean() {
        return accQueueSum / accQueue.size();

    }


    //Put reading into queue.
    public void addAccReading(float[] accReading) {


        float accVecLen = calcVectorLength(accReading);

        //Verify whether the size of queue hits the upper limit.
        if (accQueue.size() >= maxQueueSize) {
            decreaseAccQueueSum(accQueue.poll());
        }

        //Add reading data into queue.
        accQueue.add(accVecLen);

        //Calc the sum of each axis.
        increaseAccQueueSum(accVecLen);
    }

    //Calc local variance of the accelerations.
    private double calcVariance() {

        double accVariance = 0;
        double mean = calcMean();
        for (float accReading : accQueue) {
            accVariance += Math.pow(accReading - mean, 2);
        }
        accVariance /= (2 * windowSize + 1);


        return accVariance;
    }

    /*

      C1 =  true, threshMin < ak < threshMax
            false, otherwise.
 */
    public boolean C1(float[] accReading, float minThreshold, float maxThreshold, String from) {
        double ak = calcVectorLength(accReading);

        boolean retValue = false;

        if (minThreshold < ak && ak < maxThreshold) {
            retValue = true;
        } else {
            retValue = false;
        }

        Log.i(TAG, String.format("%s, [%f, %f, %f]",from, minThreshold, ak, maxThreshold));

        return retValue;
    }

    public boolean C2(float threshold) {

        boolean retValue = false;
        if (accQueue.size() == windowSize * 2 + 1) {

            retValue = calcVariance() > threshold ? true : false;

        } else {
            retValue = true;
        }

        Log.i(TAG, String.format("C2, [%f, %f]", calcVariance(), threshold));

        return retValue;
    }


    public boolean C3(float[] gyroscopeReading, float threshold) {
        return C1(gyroscopeReading, -1, threshold, "C3");
    }

    public void reset() {
        accQueueSum = 0;
        accQueue.clear();
    }


}
