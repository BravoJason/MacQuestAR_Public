package com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature;

/**
 * Created by steve on 2017-04-02.
 */

public class DataWindow {

    private static long WINDOW_SIZE;

    DataWindow(long windowSize) {
        this.WINDOW_SIZE = windowSize;
    }

    private long startTime = 0;

    private long endTime = 0;

    public int inWindow(long time) {
        int result = 0;
        if (time >= startTime && time <= endTime) {
            result = 0;
        }
        if (time < startTime) {
            result = -1;
        }
        if (time > endTime) {
            result = 1;
        }
        return result;
    }

    public void moveWindow() {
        startTime += WINDOW_SIZE / 2;
        endTime += WINDOW_SIZE / 2;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
        this.endTime = startTime + WINDOW_SIZE;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "DataWindow[" + startTime + ", " + endTime + ']';
    }
}
