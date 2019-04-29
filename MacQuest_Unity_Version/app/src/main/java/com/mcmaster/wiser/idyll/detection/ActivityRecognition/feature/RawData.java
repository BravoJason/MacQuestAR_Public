package com.mcmaster.wiser.idyll.detection.ActivityRecognition.feature;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by steve on 2017-03-19.
 */

public class RawData {
    public List<Float> xList = new ArrayList<>();
    public List<Float> yList = new ArrayList<>();
    public List<Float> zList = new ArrayList<>();
    public List<Long> timeList = new ArrayList<>();
    public List<String> labelList = new ArrayList<>();

    public void moveToNextWindow() {
        if (xList != null && xList.size() > 0)
            xList.remove(0);
        if (yList != null && yList.size() > 0)
            yList.remove(0);
        if (zList != null && zList.size() > 0)
            zList.remove(0);
        if (timeList != null && timeList.size() > 0)
            timeList.remove(0);
        if (labelList != null && labelList.size() > 0)
            labelList.remove(0);

    }

    @Override
    public String toString() {
        return "RawData{" +
                "xList=" + xList +
                ", yList=" + yList +
                ", zList=" + zList +
                ", timeList=" + timeList +
                ", labelList=" + labelList +
                '}';
    }

}
