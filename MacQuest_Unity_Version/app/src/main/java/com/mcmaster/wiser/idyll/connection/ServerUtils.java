package com.mcmaster.wiser.idyll.connection;

/**
 * Created by Ahmed on 7/28/2017.
 */

public class ServerUtils {
    public static final String CHECK_NEW_VERSION = "http://spdb.cas.mcmaster.ca/support/check_version";
    public static final String API_ROOT = "http://spdb.cas.mcmaster.ca/";
    public static final String API_SENSOR_DATA = "http://spdb.cas.mcmaster.ca/sensordata/";
    public static final String API_HISTORY_DB = "http://spdb.cas.mcmaster.ca/historydb/";
    public static final String API_DATA_UPLOAD_PARSER = "http://spdb.cas.mcmaster.ca/upload-data/   ";
    public static final String API_REGISTRATION = "http://spdb.cas.mcmaster.ca/install/";
    public static String API_LATEST_DB = " ";
    public static String ACCESS_USER = "aaq";
    public static String ACCESS_PASSWORD = "rameen11";
    public static String ACCESS_TOKEN = "ba3c0d97686c9a45d15133b7fee4db387a65157b";

    //Upload location timer parameter.
    //10 Second delay time.
    public static final long HEAT_MAP_UPLOAD_DELAY_TIME = 10 * 1000;
    //Upload location period.
    //For test purpose, the value is 1. In the production version, this value should be larger, like 5 minutes.
    public static final long HEAT_MAP_UPLOAD_PERIOD_TIME = 1 * 1000;

    //Heat MAP URL
    public static final String HEAT_MAP_URL = "http://35.183.49.87/eventapi/api/userlocation/";


    public static final String UPLOAD_DATA_URL = "http://spdb.cas.mcmaster.ca/sensordata/";



}
