package com.mcmaster.wiser.idyll.collection;

public class CompassReading {


    public float getReading(float[] arr){
        int nBegin = arr.length / 3;
        int nEnd = arr.length / 3 * 2;

        float fAccumulate = 0;

        for(int i = nBegin; i < nEnd; i++){
            fAccumulate += arr[i];
        }

        return fAccumulate / (nEnd - nBegin);
    }
}
