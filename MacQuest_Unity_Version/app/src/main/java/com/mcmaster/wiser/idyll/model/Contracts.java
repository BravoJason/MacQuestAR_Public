package com.mcmaster.wiser.idyll.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wiserlab on 7/19/17.
 */

public class Contracts {

    public static final String CONTENT_AUTHORITY = "com.mcmaster.wiser.idyll";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BUILDING = "outline";
    public static final String PATH_BUS_ROUTES = "routes";
    public static final String PATH_BUS_STOP_TIMES = "stop_times";
    public static final String PATH_BUS_STOPS = "stops";
    public static final String PATH_BUS_TRIPS = "trips";
    public static final String PATH_BUILDING_HISTORY = "history";
    public static final String PATH_ROOM = "room";

    public static class BuildingContractEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUILDING;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BUILDING;

        public final static String TABLE_NAME_OUTLINE = "outline";
        public final static String COLUMN_OUT_ID = "outid"; //change this when we put the real database in!
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_SHORTNAME = "shortname";
        public final static String COLUMN_LOCATION = "location";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUILDING);
    }

    public static final class BuildingHistoryEntry implements BaseColumns {
        public final static String TABLE_NAME = "history";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BUILDING_NAME = "name";
        public final static String COLUMN_BUILDING_SHORTNAME = "shortname";
        public final static String COLUMN_ROOM = "room";
        public final static String COLUMN_OUT_ID = "outid";
        public final static String COLUMN_UTILITY = "utility";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_SYNC_STATUS = "sync_status";
        public static final String COLUMN_UUID = "uuid";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUILDING_HISTORY);
    }

    public static class BusDataEntry implements BaseColumns {
        public final static String TABLE_NAME_ROUTES = "routes";
        public final static String COLUMN_ROUTE_ID = "route_id";
        public final static String COLUMN_ROUTE_SHORT_NAME = "route_short_name";
        public final static String COLUMN_ROUTE_LONG_NAME = "route_long_name";

        public final static String TABLE_NAME_STOP_TIMES = "stop_times";
        public final static String COLUMN_TRIP_ID = "trip_id";
        public final static String COLUMN_ARRIVAL_TIME = "arrival_time";

        public final static String TABLE_NAME_STOPS = "stops";
        public final static String COLUMN_STOP_ID = "stop_id";
        public final static String COLUMN_STOP_CODE = "stop_code";
        public final static String COLUMN_STOP_NAME = "stop_name";
        public final static String COLUMN_STOP_LATITUDE = "stop_lat";
        public final static String COLUMN_STOP_LONGITUDE = "stop_lon";

        public final static String TABLE_NAME_TRIPS = "trips";
        public static final String COLUMN_SERVICE_ID = "service_id";

        public static final Uri BUS_ROUTE_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUS_ROUTES);
        public static final Uri BUS_STOP_TIMES_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUS_STOP_TIMES);
        public static final Uri BUS_STOP_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUS_STOPS);
        public static final Uri BUS_TRIPS_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BUS_TRIPS);
    }

    public static class RoomContractEntry implements BaseColumns {
        public final static String TABLE_NAME_ROOM = "roomsnew";
        public final static String COLUMN_ROOM_ID = "rid";
        public final static String COLUMN_OUT_ID = "outid";
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_FLOOR = "floor";
        public final static String COLUMN_LOCATION = "centroid";
        public final static String COLUMN_ROUTINGPOINT = "location";
        public final static String COLUMN_NEARESTSTAIRCASE = "NearestStaircase";
        public final static String COLUMN_BUILDING_NAME = "buildingname";
        public final static String COLUMN_UTILITY = "utility";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ROOM);
    }

}
