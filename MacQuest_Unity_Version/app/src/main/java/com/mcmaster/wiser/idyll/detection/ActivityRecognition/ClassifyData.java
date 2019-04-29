package com.mcmaster.wiser.idyll.detection.ActivityRecognition;

import android.util.SparseArray;

/**
 * Created by steve on 2017-04-03.
 */

public class ClassifyData {

    /**
     * ID-Name corresponding relation for walking and standing activities.
     */
    private static final SparseArray<String> WALK_STAND_ID_NAME_MAP = new SparseArray<>();

    /**
     * ID-Name corresponding relation for up and down activities.
     */
    private static final SparseArray<String> UP_DOWN_ID_NAME_MAP = new SparseArray<>();

    ClassifyData() {
        WALK_STAND_ID_NAME_MAP.put(0, "Standing");
        WALK_STAND_ID_NAME_MAP.put(1, "Walking");
        UP_DOWN_ID_NAME_MAP.put(0, "Horizontal");
        UP_DOWN_ID_NAME_MAP.put(1, "Up");
        UP_DOWN_ID_NAME_MAP.put(2, "Down");
    }

    public static final int CLASSIFY_TYPE_WALK_STAND = 1;
    public static final int CLASSIFY_TYPE_UP_DOWN = 2;

    public int classifyType;
    public int classifyResult;

    public String getActivityName() {
        if (classifyType == CLASSIFY_TYPE_WALK_STAND) {
            return WALK_STAND_ID_NAME_MAP.get(classifyResult);
        } else {
            return UP_DOWN_ID_NAME_MAP.get(classifyResult);
        }
    }

    @Override
    public String toString() {
        return "ClassifyData{" +
                "classifyType=" + classifyType +
                ", classifyResult=" + classifyResult +
                '}';
    }
}
