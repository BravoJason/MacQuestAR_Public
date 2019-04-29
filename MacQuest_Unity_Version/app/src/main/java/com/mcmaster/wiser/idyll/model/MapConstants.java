package com.mcmaster.wiser.idyll.model;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

/**
 * Created by wiserlab on 8/17/17.
 */

public class MapConstants {
    public final static double ZOOM_LEVEL_BUILDING = 17.5;
    public final static double ZOOM_LEVEL_ROOM = 19;
    public final static double ZOOM_LEVEL_MINIMUM = 14;
    public final static double ZOOM_LEVEL_EVENT = 15;

    public final static int BOOKING_INNIS_KEY = 1;
    public final static int BOOKING_MILLS_KEY = 2;
    public final static int BOOKING_THODE_KEY = 3;

    public final static int NUMBER_OF_FLOORS = 8;

    public final static LatLng McMasterStartingPoint = new LatLng(43.2637557, -79.920935);

    public final static String MAPBOX_LAYER_STRING = "layer";
    public final static String MAPBOX_ROOM_STRING = "rooms";
    public final static String MAPBOX_LABELS_STRING = "labels";
    public final static String MAPBOX_FILL_STRING = "fill";
    public final static String MAPBOX_WASHROOM = "washroom";
    public final static String MAPBOX_STAIRCASE = "staircase";
    public final static String MAPBOX_ELEVATOR = "elevator";
    public final static int MAPBOX_LAYER_CHOICE_ROOM = 0;
    public final static int MAPBOX_LAYER_CHOICE_LABELS = 1;
    public final static int MAPBOX_LAYER_CHOICE_FILL = 2;
    public final static int MAPBOX_LAYER_CHOICE_STAIR = 3;
    public final static int MAPBOX_LAYER_CHOICE_WASHROOM = 4;
    public final static int MAPBOX_LAYER_CHOICE_ELEVATOR = 5;
    public final static String MAPBOX_CAMPUS_OUTLINE_LAYER = "campusoutline";

    public final static String BOOKING_URL = "https://library.mcmaster.ca/mrbs/day.php?year=2017&month=08&day=10&area=";

    public static final LatLngBounds CAMPUS_BOUNDS = new LatLngBounds.Builder()
            .include(new LatLng(43.2681979, -79.931807))
//    43.268374, -79.926535)) //top left
            .include(new LatLng(43.257190, -79.912382)) //bottom right
            .build();

}
