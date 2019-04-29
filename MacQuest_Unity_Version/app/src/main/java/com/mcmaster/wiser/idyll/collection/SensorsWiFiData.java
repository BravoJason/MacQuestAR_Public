package com.mcmaster.wiser.idyll.collection;

import java.util.ArrayList;

/**
 * Created by steve on 2017-07-15.
 */

public class SensorsWiFiData {

    public String buildingId;

    public String floorLevel;

    public String entranceId;

    public SensorsWiFiData(String buildingID, String floorLevel, String entranceId) {
        this.buildingId = buildingID;
        this.floorLevel = floorLevel;
        this.entranceId = entranceId;
        lightSensorDatas = new ArrayList<>();
        magneticSensorDatas = new ArrayList<>();
        pressureSensorDatas = new ArrayList<>();
        wiFiDatas = new ArrayList<>();
    }

    public ArrayList<LightSensorData> lightSensorDatas;

    public ArrayList<MagneticSensorData> magneticSensorDatas;

    public ArrayList<PressureSensorData> pressureSensorDatas;

    public ArrayList<WiFiData> wiFiDatas;

    @Override
    public String toString() {
        return "SensorsWiFiData{" +
                "buildingId='" + buildingId + '\'' +
                ", floorLevel='" + floorLevel + '\'' +
                ", entranceId='" + entranceId + '\'' +
                ", lightSensorDatas=" + lightSensorDatas +
                ", magneticSensorDatas=" + magneticSensorDatas +
                ", pressureSensorDatas=" + pressureSensorDatas +
                ", wiFiDatas=" + wiFiDatas +
                '}';
    }
}
