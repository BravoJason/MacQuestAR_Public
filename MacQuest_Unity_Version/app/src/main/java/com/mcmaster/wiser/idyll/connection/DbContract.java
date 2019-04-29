package com.mcmaster.wiser.idyll.connection;

/**
 * Created by Ahmed on 8/5/2017.
 */

public class DbContract {
    public static final int SYNC_STATUS_OK =0;
    public static final int SYNC_STATUS_FAILED =1;
    public static final String DATABASE_NAME = "sample";

    public final static String TABLE_NAME = "activity";
    public static String SERVER_URL_HISTORY_DATA ="http://spdb.cas.mcmaster.ca/historydb/";

    public final static String COLUMN_SYNC_STATUS = "sync_status";

    //public final static String COLUMN_ID =
    public final static String COLUMN_UUID = "uuid";
    public final static String COLUMN_BUILDING_NAME = "name";
    public final static String COLUMN_BUILDING_SHORTNAME = "shortname";
    public final static String COLUMN_ROOM = "room";
    public final static String COLUMN_OUT_ID = "outid";
    public final static String COLUMN_UTILITY = "utility";
    public static final String COLUMN_LOCATION = "location";

}
