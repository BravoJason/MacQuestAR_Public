package com.mcmaster.wiser.idyll.collection;

import com.google.gson.Gson;

/**
 * Created by steve on 2017-07-27.
 */

public class SensorsWiFiDataDAO {

    public SensorsWiFiDataDAO(SensorsWiFiData sensorsWiFiData) {
        if (sensorsWiFiData != null) {
            Gson gson = new Gson();
            this.building_id = sensorsWiFiData.buildingId;
            this.entrance_id = sensorsWiFiData.entranceId;
            this.floor_level = sensorsWiFiData.floorLevel;
            this.light_sensor = gson.toJson(sensorsWiFiData.lightSensorDatas);
            this.magnetic_sensor = gson.toJson(sensorsWiFiData.magneticSensorDatas);
            this.pressure_sensor = gson.toJson(sensorsWiFiData.pressureSensorDatas);
            this.wifi_data = gson.toJson(sensorsWiFiData.magneticSensorDatas);
        }
    }

    private String building_id;

    private String entrance_id;

    private String floor_level;

    private String light_sensor;

    private String magnetic_sensor;

    private String pressure_sensor;

    private String wifi_data;

}
