package com.mcmaster.wiser.idyll.collection;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by steve on 2017-07-15.
 */

public class WiFiData {
    public long timestamp;

    public List<ScanResult> scanResults;

    @Override
    public String toString() {
        return "WiFiData{" +
                "timestamp=" + timestamp +
                ", scanResults=" + scanResults +
                '}';
    }
}
