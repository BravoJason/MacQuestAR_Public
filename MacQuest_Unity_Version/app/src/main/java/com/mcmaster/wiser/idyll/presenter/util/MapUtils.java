package com.mcmaster.wiser.idyll.presenter.util;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.annotations.Annotation;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mapbox.services.api.utils.turf.TurfException;
import com.mapbox.services.api.utils.turf.TurfJoins;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.Polygon;
import com.mapbox.services.commons.models.Position;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.collection.CompassReading;
import com.mcmaster.wiser.idyll.collection.DataCollectionManager;
import com.mcmaster.wiser.idyll.collection.QuickSort;
import com.mcmaster.wiser.idyll.model.event.EventUtils;
import com.mcmaster.wiser.idyll.connection.FetchEventData;
import com.mcmaster.wiser.idyll.connection.ServerUtils;
import com.mcmaster.wiser.idyll.model.MapConstants;
import com.mcmaster.wiser.idyll.model.UserLocation;
import com.mcmaster.wiser.idyll.model.event.Event;
import com.mcmaster.wiser.idyll.model.event.EventItem;
import com.mcmaster.wiser.idyll.model.event.EventListAdapter;
import com.mcmaster.wiser.idyll.model.event.EventViewTools;
import com.mcmaster.wiser.idyll.model.room.Room;
import com.mcmaster.wiser.idyll.model.routing.Routing;
import com.mcmaster.wiser.idyll.view.ARActivity;
import com.mcmaster.wiser.idyll.view.MainActivity;
import com.mcmaster.wiser.idyll.view.MapFragment;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import com.mcmaster.wiser.idyll.model.EKFLocationService.Interface.LocationNotifier;

import static android.content.Context.SENSOR_SERVICE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

/**
 * Class to store functions related to map rendering
 * Created by Eric on 6/26/17.
 * <p>
 * Quick FAQ:
 * How does MacQuest handle user location?
 * Well it kind of doesn't. MacQuest uses the (somewhat outdated) MyLocationLayer in mapbox.
 * This is toggled on by using the map.setMyLocationEnabled(bool) function.
 * What this layer does automatically handle finding and drawing the user location on the map.
 * It draws the marker according to how the setupLocationViewSettings function tells it to.
 * <p>
 * But what about the onLocationChange in UserLocation where it has functions to do things on the users location change?
 * Well that function actually NEVER gets called because the MyLocationLayer automatically handles user movement and never actually calls back to that function.
 * HOWEVER. In MapUtils, I've added a OnMyLocationChangeListener, which seperate from what is drawn on the map, tracks the users location.
 * So anyone actually wants to view the users location on change, it can be found there.
 * <p>
 * (Suggestion: Try updating the MapBox version MacQuest uses and start using the new LocationLayerPlugin they have. It seems to do everything a lot
 * more elegantly than we currently do but updating it requires reworking a bunch of the code here too)
 * <p>
 * -Daniel
 */

public class MapUtils implements SensorEventListener, LocationNotifier {

    private static final String TAG = MapUtils.class.getSimpleName();

    public static boolean RANDOM_ROUTING = false;

    private Routing routing = null;

    private boolean routeAR = false;

    public UserLocation userLocation;

    private int bookingSelection = 1;
    private boolean showpath = true;

    public boolean isPointsUpdate = true;

    //Merge
    public boolean endListener = false;
    public String buildingNameShort = "";
    public List<String> pastBulidingNames = new ArrayList<>();
    volatile WifiManager wifiManager;
    public boolean doneWifiLock;
    private List<Long> broadCastTimeList = new ArrayList<>();
    public int scanNum;
    public volatile String currentLocation = "";
    private boolean wipeMarker = true;
    public LatLng wifiLoc;
    private CountDownLatch latch = new CountDownLatch(1);
    private MarkerOptions userLocationMarker;
    private boolean useGPS = false;
    public boolean zoomOnce = true;


    public static ArrayList<ArrayList<String>> arEvents;
    public static ArrayList<ArrayList<String>> arPoints;
    public static ArrayList<ArrayList<String>> visibleARPoints = new ArrayList<>();
    public static String mode;
    public static String idSearch;

    public static HashMap<String, ArrayList<MarkerViewOptions>> markers = new HashMap<String, ArrayList<MarkerViewOptions>>();

    public boolean showCurrentandFuture = true;

    private MarkerViewOptions userClickMarker;
    public boolean routingDone;
    public boolean wifiThreadEnabled = false;
    public boolean currentlyRouting = false;


    public static ArrayList<Position> createCampusBoundingBox() {
        ArrayList<Position> campusBoundingBox = new ArrayList<>();
        campusBoundingBox.add(Position.fromCoordinates(MapConstants.CAMPUS_BOUNDS.getSouthWest().getLongitude(), MapConstants.CAMPUS_BOUNDS.getSouthWest().getLatitude()));
        campusBoundingBox.add(Position.fromCoordinates(MapConstants.CAMPUS_BOUNDS.getNorthWest().getLongitude(), MapConstants.CAMPUS_BOUNDS.getNorthWest().getLatitude()));
        campusBoundingBox.add(Position.fromCoordinates(MapConstants.CAMPUS_BOUNDS.getNorthEast().getLongitude(), MapConstants.CAMPUS_BOUNDS.getNorthEast().getLatitude()));
        campusBoundingBox.add(Position.fromCoordinates(MapConstants.CAMPUS_BOUNDS.getSouthEast().getLongitude(), MapConstants.CAMPUS_BOUNDS.getSouthEast().getLatitude()));
        return campusBoundingBox;
    }

    public static LatLng getLocationFromString(String rawLocation) {
        try {
            rawLocation = rawLocation.substring(6);
        } catch (Exception e) {
            return null;
        }
        String[] latlon = rawLocation.split(" ");
        Location roomLocation = new Location("");
        roomLocation.setLongitude(Double.parseDouble(latlon[0]));
        roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
        LatLng latLng = new LatLng(roomLocation.getLatitude(), roomLocation.getLongitude());
        return latLng;
    }

    public static String getLayerName(int choice, int floor) {
        switch (choice) {
            case MapConstants.MAPBOX_LAYER_CHOICE_ROOM:
                return MapConstants.MAPBOX_LAYER_STRING + floor + MapConstants.MAPBOX_ROOM_STRING;
            case MapConstants.MAPBOX_LAYER_CHOICE_LABELS:
                return MapConstants.MAPBOX_LAYER_STRING + floor + MapConstants.MAPBOX_LABELS_STRING;
            case MapConstants.MAPBOX_LAYER_CHOICE_FILL:
                return MapConstants.MAPBOX_LAYER_STRING + floor + MapConstants.MAPBOX_FILL_STRING;
            case MapConstants.MAPBOX_LAYER_CHOICE_WASHROOM:
                return MapConstants.MAPBOX_LAYER_STRING + floor + MapConstants.MAPBOX_WASHROOM;
            case MapConstants.MAPBOX_LAYER_CHOICE_STAIR:
                return MapConstants.MAPBOX_LAYER_STRING + floor + MapConstants.MAPBOX_STAIRCASE;
            case MapConstants.MAPBOX_LAYER_CHOICE_ELEVATOR:
                return MapConstants.MAPBOX_LAYER_STRING + floor + MapConstants.MAPBOX_ELEVATOR;
            default:
                return MapConstants.MAPBOX_LAYER_STRING + "1" + MapConstants.MAPBOX_ROOM_STRING;
        }
    }

    public static void staticShowCardView(CardView cardView, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.enter_from_bottom);
        anim.setDuration(250);
        cardView.startAnimation(anim);
        cardView.setVisibility(View.VISIBLE);
    }

    public static void staticHideCardView(CardView cardView, Context context) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.exit_to_bottom);
        anim.setDuration(250);
        cardView.startAnimation(anim);
        cardView.setVisibility(View.GONE);
    }

    /////////////////////////////////////POJO MODE

    private MapboxMap map;
    private Context context;

    private MapFragment mapFragment;

    private View levelButtons;
    private List<Button> floorButtonList;
    private List<Button> basementButtonList;

    private com.mapbox.mapboxsdk.annotations.Polygon selectedBuilding;

    private CardView routeInfoCardView;
    private CardView infoCardView;
    private CardView directionsCardView;
    private TextView infoRoomName;
    private TextView infoBuildingName;
    private TextView destinationTextView;
    private TextView bookThisRoomText;
    private ListView eventListView;

    private FloatingSearchView mFloat;
    private FloatingSearchView routingFloat;
    private FloatingActionButton navigationFAB;

    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;
    private PermissionsListener permissionsListener;
    public Activity activity;

    private int currentLevel = 1;
    public static int srcFloor = 1;
    public static int srcRid = 0;
    private int srcBid = 0;
    private int desFloor = 1;
    private LineLayer routeLayer1;
    private LineLayer routeLayer2;
    private LineLayer routeLayer3;
    private int floorNumberRoute1;
    private int floorNumberRoute2;
    private int floorNumberRoute3;
    public Room destRoom;
    public Room srcRoom;
    public Location destLocation;
    private LatLng latLngForRoutingSrc;
    private String[] routes = new String[3];

    private GeoJsonSource routeSourc;
    private GeoJsonSource routeGround;
    private GeoJsonSource routeDest;
    private Button clearRoutesButton;
    private Button toggleARButton;
    private CardView toggleCurrentEvents;
    private CardView eventListCardView;
    private Location userLastLocation;

    private boolean isEndNotified;
    private ProgressBar progressBar;
    public static boolean arFound = false;


    //Compass Variables
    //private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    QuickSort sortObj;
    CompassReading compassreading;
    float fFinalReading = 0;
    float pitch = 0;
    float azimut;
    float[] mGravity;
    float[] mGeomagnetic;
    boolean isCalibrated = false;
    Button startARButton;

    //public EKFLocationService ekfLocationService;
    private SensorManager mSensorManager;
    private Sensor gsensor_acc;
    private Sensor gsensor_gyro;
    private Sensor gsensor_rotation;
    private double m_magneticDeclination = 0.0;

    private float[] mRotationMatrix = new float[16];
    private float[] mOrientation = new float[9];

    public int heatMapID = -1;
    //public String heatMapURL = "http://macquest2.cas.mcmaster.ca/api/user-locs/";


    // Data collection.
    private DataCollectionManager dataCollectionManager;

    //////////PUT_TIMER
    //Timer to upload user current location to server.
    private Timer uploadLocationTimer = null;
    //Timer task to upload user location.
    private TimerTask uploadLocationTimerTask;
    ///////////PUT_TIMER

    // private String emptyGeojson = context.getResources().getString(R.string.empty_geojson); // this is a static string, put it in the strings.xml

    public MapUtils(MapboxMap map, Context context, View levelButtons, List<Button> floorButtonList, List<Button> basementButtonList, FloatingSearchView mFloat, FloatingSearchView rFloat, ListView eventListView, CardView eventListCardView, Activity activity) {
        this.map = map;
        this.context = context;
        this.levelButtons = levelButtons;
        this.floorButtonList = floorButtonList;
        this.basementButtonList = basementButtonList;
        this.mFloat = mFloat;
        this.routingFloat = rFloat;
        this.eventListCardView = eventListCardView;
        this.eventListView = eventListView;
        this.activity = activity;
        dataCollectionManager = new DataCollectionManager(activity);
    }

    ///////////PUT_TIMER
    //Function to initial timer and timer task for upload user current location to server.
    private void initialUploadTimer() {
        uploadLocationTimer = new Timer();
        uploadLocationTimerTask = new TimerTask() {
            @Override
            public void run() {
                String userLoc = generateLocationForServer();
                //To test break channel.
                //sendLocationToServer(ServerUtils.HEAT_MAP_URL, userLoc,"");
            }
        };

    }
    ///////////PUT_TIMER

    public void initializeMap() {
        try {
            setupMap();
            currentLevel = 1;

            setupMapCamera();
            setupMapClick();
            setupLongClickForRouting();
            setupRouteLayers();

            setVisibleMapboxLayer(1);
        } catch (Exception e) {

        }
    }

    //Setup the compass + ekf
    public void setupCompass() {
        //ekfLocationService = new EKFLocationService(this);

        sortObj = new QuickSort();

        compassreading = new CompassReading();

        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        gsensor_rotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(this, gsensor_rotation, SensorManager.SENSOR_DELAY_NORMAL);

        /*
        mSensorManager = (SensorManager)context.getSystemService(SENSOR_SERVICE);
        //accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gsensor_acc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        gsensor_gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);




        //Register the sensor listener.
        mSensorManager.registerListener((SensorEventListener) this, gsensor_acc, SensorManager.SENSOR_DELAY_NORMAL);

        //Register the sensor listener.
        mSensorManager.registerListener((SensorEventListener) this, gsensor_gyro, SensorManager.SENSOR_DELAY_NORMAL);

        //Register the sensor listener.

        */
    }

    //Starts AR Navigation
    public void startARNav() {
        final Intent myIntent = new Intent(context, ARActivity.class);

        myIntent.putExtra("Route", getRoute());

        if (destLocation != null) {
            myIntent.putExtra("DestLat", String.valueOf(destLocation.getLatitude()));
            myIntent.putExtra("DestLng", String.valueOf(destLocation.getLongitude()));
        }


        startAR(myIntent);


    }

    //Starts AR with no compass (used for QR scanning)
    public void startARNoCompass(Intent myIntent) {
        if (arPoints != null) {
            myIntent.putExtra("NumPoints", arPoints.size());
            for (int i = 0; i < arPoints.size(); i++) {
                myIntent.putStringArrayListExtra("Point" + String.valueOf(i), arPoints.get(i));
            }


        }
        if (userLocation.currentLocation != null) {
            myIntent.putExtra("OriginalLat", String.valueOf(userLocation.currentLocation.getLatitude()));
            myIntent.putExtra("OriginalLng", String.valueOf(userLocation.currentLocation.getLongitude()));
        }

        myIntent.putExtra("Heading", String.valueOf(0));
        myIntent.putExtra("Pitch", String.valueOf(0));

        context.startActivity(myIntent);
    }

    //Used in the process of starting AR
    public void startAR(Intent myIntent) {
        if (arPoints != null) {
            myIntent.putExtra("NumPoints", arPoints.size());
            for (int i = 0; i < arPoints.size(); i++) {
                myIntent.putStringArrayListExtra("Point" + String.valueOf(i), arPoints.get(i));
            }


        }
        if (userLocation.currentLocation != null) {
            myIntent.putExtra("OriginalLat", String.valueOf(userLocation.currentLocation.getLatitude()));
            myIntent.putExtra("OriginalLng", String.valueOf(userLocation.currentLocation.getLongitude()));
        }

        calibrateCompass(myIntent);
    }


    //Calibration sequence for the compass.
    public void calibrateCompass(final Intent intent) {
        ImageView image = new ImageView(context);
        image.setImageResource(R.drawable.compass_calibration_google);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context)
                        .setTitle("Calibrate your compass!")
                        .setMessage("Do 3 rotations with your phone as shown bellow, hold it in your hand parallel to the ground, and click calibrate!")
                        .setPositiveButton("Start AR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                pause();

                                intent.putExtra("Heading", String.valueOf(fFinalReading));
                                intent.putExtra("Pitch", String.valueOf(pitch));

                                context.startActivity(intent);

                            }
                        })
                        .setView(image)
                        .setNeutralButton("Calibrate", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });


        final AlertDialog dialog = builder.create();

        dialog.show();

        //Set button colors.
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(activity.getResources().getColor(R.color.colorPrimary));


        startARButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        startARButton.setVisibility(View.GONE);
        startARButton.setClickable(false);


        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isCalibrated = false;
                sortObj = new QuickSort();

                Toast.makeText(context, "Calibrating...", Toast.LENGTH_SHORT).show();

            }
        });
    }


    public void toggleSymbolLayer(int utility) {
        Layer tempLayer = map.getLayer(getLayerName(utility, currentLevel));
        try {
            if (VISIBLE.equals(tempLayer.getVisibility().getValue())) {
                tempLayer.setProperties(visibility(NONE));
            } else {
                tempLayer.setProperties(visibility(VISIBLE));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in toggling symbol layer");
        }
    }

    private void listSourcesInAndroidMonitor() {
        List<Source> mapSources = map.getSources();
        for (Source s : mapSources) {
            String ID = s.getId();
            Log.v(TAG, "SOURCE ID: " + ID);
        }
        List<Layer> mapLayers = map.getLayers();
        for (Layer l : mapLayers) {
            String ID = l.getId();
            Log.v(TAG, "LAYER ID: " + ID);
        }
        List<Annotation> annotationList = map.getAnnotations();
        for (Annotation a : annotationList) {
            long ID = a.getId();
            Log.v(TAG, "ANNOTATION ID: " + ID);
        }
    }

    //Clears the map
    public void clearMap() {
        map.clear();
    }

    //Checks if the MyLocationLayer is enabled.
    public boolean isLocationEnabled() {
        return map.isMyLocationEnabled();
    }

    public void setLocationElements(LocationEngine locationEngine, LocationEngineListener locationEngineListener, PermissionsManager permissionsManager, PermissionsListener permissionsListener) {
        this.locationEngine = locationEngine;
        this.locationEngineListener = locationEngineListener;
        this.permissionsManager = permissionsManager;
        this.permissionsListener = permissionsListener;
    }

    public void setupLocationEngine() {
        locationEngine = new LocationSource(context);
        locationEngine.activate();
        Log.v(TAG, " Location engine connected: " + locationEngine.isConnected());
    }

    //Sets up the map.
    private void setupMap() {
        map.getUiSettings().setCompassMargins(20, 200, 20, 20);
        map.setLatLngBoundsForCameraTarget(MapConstants.CAMPUS_BOUNDS);
        map.setMinZoomPreference(MapConstants.ZOOM_LEVEL_MINIMUM);
        map.setLocationSource(locationEngine);
    }

    //Assigns the elements MapUtils changes to their actual values from the fragment.
    public void setUiElements(CardView infoCardView, TextView infoTextView, ProgressBar progressBar,
                              TextView infoBuildingName,
                              com.getbase.floatingactionbutton.FloatingActionButton navigationFAB, CardView directionsCardView, CardView routeInfoCardView,
                              TextView destinationTextView, Button clearRoutesButton, Button toggleARButton, CardView toggleCurrentEvents, TextView bookThisRoomText) {

        this.infoCardView = infoCardView;
        this.infoRoomName = infoTextView;

        this.progressBar = progressBar;

        this.infoBuildingName = infoBuildingName;
        this.navigationFAB = navigationFAB;
        this.directionsCardView = directionsCardView;
        this.routeInfoCardView = routeInfoCardView;

        this.destinationTextView = destinationTextView;
        this.clearRoutesButton = clearRoutesButton;
        this.toggleARButton = toggleARButton;
        this.toggleCurrentEvents = toggleCurrentEvents;
        this.bookThisRoomText = bookThisRoomText;
        setCardViewClickListeners();
    }

    //Sets up the card view click listeners
    private void setCardViewClickListeners() {
        this.infoCardView.setClickable(true);
        this.directionsCardView.setClickable(true);
        this.infoCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, " inforcardview clicked");
            }
        });
        this.infoCardView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.v(TAG, "infocardview dragged");
                return false;
            }
        });

        this.directionsCardView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.v(TAG, "directionscardview dragged");
                return false;
            }
        });

        this.directionsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "directions card view clicked");
            }
        });

    }

    public void pinDestination() {
        if (desFloor == currentLevel) {
            if (destLocation != null) {
                Log.d("location", "location is " + destLocation.getLatitude() + " " + destLocation.getLongitude());
            } else {
                Log.d("Was null", "was null");
            }

        }

    }

    public List<CardView> getUICardviews() {
        ArrayList<CardView> cardViewArrayList = new ArrayList<>();
        cardViewArrayList.add(infoCardView);
        cardViewArrayList.add(directionsCardView);
        return cardViewArrayList;
    }

    //Merge
//    private String getBuildingNameFromCameraPosition(CameraPosition position) {
//        //to use: Pass in map.getCameraPosition(); to get the current cameraposition.  The function will return the building name
//        LatLng targetLatLng = position.target;
//
//        PointF pointf = map.getProjection().toScreenLocation(targetLatLng);
//        RectF rectF = new RectF(pointf.x - 40, pointf.y - 40, pointf.x + 40, pointf.y + 40);
//
//        List<Feature> testFeatures = map.queryRenderedFeatures(rectF, map.getLayer(MapConstants.MAPBOX_CAMPUS_OUTLINE_LAYER).getId());
//
//        String buildingName = "";
//
//
//        for (Feature feature : testFeatures) {
//            try {
//
//                buildingName = feature.getProperties().get("showname").getAsString();
//            } catch (Exception e) {
//                Log.e(TAG, "Error with getting name and building name from queried point");
//                return "Unknown BuildingName";
//            }
//        }
//
//
//        return buildingName;
//    }
    //Merge
    //Used to get building name from where the gps marker lands
    private String getBuildingNameFromCameraPosition(CameraPosition position) {
        //to use: Pass in map.getCameraPosition(); to get the current cameraposition.  The function will return the building name
        LatLng targetLatLng = position.target;

        PointF pointf = map.getProjection().toScreenLocation(targetLatLng);
        RectF rectF = new RectF(pointf.x - 40, pointf.y - 40, pointf.x + 40, pointf.y + 40);

        List<Feature> testFeatures = map.queryRenderedFeatures(rectF, map.getLayer(MapConstants.MAPBOX_CAMPUS_OUTLINE_LAYER).getId());

        String buildingName = "";

        for (Feature feature : testFeatures) {
            try {

                buildingName = feature.getProperties().get("showname").getAsString();
            } catch (Exception e) {
                Log.e(TAG, "Error with getting name and building name from queried point");
                return "Unknown BuildingName";
            }
        }


        return buildingName;
    }

    private void setupMapCamera() {
        final List<Position> boundingBox = createCampusBoundingBox();
        map.setOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

                ((FragmentActivity) activity).getSupportFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);

                CameraPosition position = map.getCameraPosition();
                LatLng targetLatLng = position.target;

                PointF pointf = map.getProjection().toScreenLocation(targetLatLng);
                RectF rectF = new RectF(pointf.x - 40, pointf.y - 40, pointf.x + 40, pointf.y + 40);

                List<Feature> testFeatures = map.queryRenderedFeatures(rectF, map.getLayer(MapConstants.MAPBOX_CAMPUS_OUTLINE_LAYER).getId());
                for (Feature f : testFeatures) {
                    try {
                        int numFloors = f.getProperties().get("num_floor").getAsInt();
                        switch (numFloors) {
                            case 1:
                                showFloorButtonsUpTo(1);
                                break;
                            case 2:
                                showFloorButtonsUpTo(2);
                                break;
                            case 3:
                                showFloorButtonsUpTo(3);
                                break;
                            case 4:
                                showFloorButtonsUpTo(4);
                                break;
                            case 5:
                                showFloorButtonsUpTo(5);
                                break;
                            case 6:
                                showFloorButtonsUpTo(6);
                                break;
                            case 7:
                                showFloorButtonsUpTo(7);
                            default:
                                break;
                        }

                        int numBasements = f.getProperties().get("num_basement").getAsInt();
                        switch (numBasements) {
                            case 1:
                                showBasementButtonsUpTo(1);
                                break;
                            case 2:
                                showBasementButtonsUpTo(2);
                                break;
                            default:
                                showBasementButtonsUpTo(0);
                                break;
                        }
                    } catch (Exception e) {
                        Log.v(TAG, "Error with getting info from campus outline");
                    }
                }

                if (!infoCardView.isShown()) {
                    if (position.zoom > 16) {
                        try {
                            if (TurfJoins.inside(Position.fromCoordinates(
                                    position.target.getLongitude(),
                                    position.target.getLatitude()),
                                    boundingBox)) {
                                if (!levelButtons.isShown()) {
                                    showLevelButton();
                                }
                            } else {
                                if (levelButtons.isShown()) {
                                    hideLevelButton();
                                }
                            }
                        } catch (TurfException turfException) {
                            turfException.printStackTrace();
                        }
                    } else if (levelButtons.isShown()) {
                        hideLevelButton();
                    }
                }
            }
        });

        map.setOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (infoCardView.isShown()) {
                    navigationFAB.setVisibility(View.GONE);
                    levelButtons.setVisibility(View.GONE);
                } else {
                    navigationFAB.setVisibility(View.VISIBLE);

                }
                if (clearRoutesButton.isShown()) {
                    //showCardView(routeInfoCardView);
                }


            }
        });
    }

    private void setupMapClick() {
        map.setOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                //get the current zoom and if the zoom is too small, then don't register a click
                if (map.getCameraPosition().zoom >= 17) {
                    showRoomInfo(latLng);
                }


            }
        });
    }

    private void setupLongClickForRouting() {

        map.getMarkerViewManager().setOnMarkerViewClickListener(new MapboxMap.OnMarkerViewClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker, @NonNull View view, @NonNull MapboxMap.MarkerViewAdapter adapter) {
                ((FragmentActivity) activity).getSupportFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);
                LatLng markerPosition = marker.getPosition();
                animateMap(markerPosition, map.getCameraPosition().zoom);
                showCardView(infoCardView);

                infoCardView.bringToFront();

                if (marker.getTitle() == null) {
                    return false;
                }

                infoRoomName.setText("Routing to");

                if (marker.getTitle().isEmpty()) {
                    infoBuildingName.setText(Double.toString(markerPosition.getLatitude()) + "   " + Double.toString(markerPosition.getLongitude()));
                } else {
                    infoBuildingName.setText(marker.getTitle());
                }
                hideFabMenu(navigationFAB);
                hideLevelButton();
                Location destlatlon = new Location("");
                destlatlon.setLongitude(markerPosition.getLongitude());
                destlatlon.setLatitude(markerPosition.getLatitude());
                destLocation = destlatlon;
                RANDOM_ROUTING = true;
                desFloor = currentLevel;
                return false;
            }
        });


        map.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                //TODO: Figure out if you want to clear the map. Do we want to get rid of all markers?
                /*clearARPoints();
                MainActivity.arOn = false;
                MainActivity.washroomsOn = false;

                NavigationView navigationView = (NavigationView) activity.findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(0).setChecked(true);
                navigationView.getMenu().getItem(1).setTitle("Nearest Washroom");
                navigationView.getMenu().getItem(2).setTitle("Campus Events");*

                map.clear();*/

                try {
                    map.removeMarker(userClickMarker.getMarker());
                    hideCardView(infoCardView);
                } catch (Exception e) {
                }

                if (selectedBuilding != null) {
                    map.removePolygon(selectedBuilding);
                }

                ((FragmentActivity) activity).getSupportFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);

                animateMap(latLng, map.getCameraPosition().zoom);
                showCardView(infoCardView);

                infoCardView.bringToFront();
                infoRoomName.setText("Routing to");
                infoBuildingName.setText(Double.toString(latLng.getLatitude()) + "   " + Double.toString(latLng.getLongitude()));
                hideFabMenu(navigationFAB);
                hideLevelButton();
                Location destlatlon = new Location("");
                destlatlon.setLongitude(latLng.getLongitude());
                destlatlon.setLatitude(latLng.getLatitude());
                destLocation = destlatlon;
                RANDOM_ROUTING = true;
                desFloor = currentLevel;
                MarkerViewOptions markerViewOptions = new MarkerViewOptions().position(new LatLng(latLng.getLatitude(), latLng.getLongitude()));
                map.addMarker(markerViewOptions);


                userClickMarker = markerViewOptions;
            }
        });
    }

    public void hideCardView(CardView infoCardView) {
        if (infoCardView.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.exit_to_bottom);
            anim.setDuration(250);
            infoCardView.startAnimation(anim);
            infoCardView.setVisibility(View.GONE);
        }
    }

    public void showCardView(CardView infoCardView) {
        if (!infoCardView.isShown()) {
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.enter_from_bottom);
            anim.setDuration(250);
            infoCardView.startAnimation(anim);
            infoCardView.setVisibility(View.VISIBLE);
        }
    }

    private void showFloorButtonsUpTo(int i) {
        for (int j = 0; j < floorButtonList.size(); j++) {
            if (j < i) {
                floorButtonList.get(j).setVisibility(View.VISIBLE);
            } else {
                floorButtonList.get(j).setVisibility(View.GONE);
            }
        }
    }

    private void showBasementButtonsUpTo(int i) {
        for (int j = 0; j < basementButtonList.size(); j++) {
            if (j < i) {
                basementButtonList.get(j).setVisibility(View.VISIBLE);
            } else {
                basementButtonList.get(j).setVisibility(View.GONE);
            }
        }
    }

    private void showLevelButton() {
        // When the user moves inside our bounding box region or zooms in to a high enough zoom level,
        // the floor level buttons are faded out and hidden.
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        levelButtons.startAnimation(animation);
        levelButtons.setVisibility(View.VISIBLE);
    }

    private void hideLevelButton() {
        // When the user moves away from our bounding box region or zooms out far enough the floor level
        // buttons are faded out and hidden.
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        levelButtons.startAnimation(animation);
        levelButtons.setVisibility(View.GONE);
    }

    public void animateMap(LatLng latLng, double zoomLevel) {
        CameraPosition position = new CameraPosition.Builder()
                .zoom(zoomLevel) //17 is a good zoom level for buildings
                .target(latLng) // Sets the new camera position
                .build(); // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500);
    }

    public static void animateMap(MapboxMap map, LatLng latLng, double zoomLevel) { //used in the user location class
        CameraPosition position = new CameraPosition.Builder()
                .zoom(zoomLevel) //17 is a good zoom level for buildings
                .target(latLng) // Sets the new camera position
                .build(); // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 500);
    }

    public String getRoute() {
        return routes[1];
    }

    public static void showFabMenu(FloatingActionButton fabMenu) {
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        fabMenu.startAnimation(animation);
        fabMenu.setVisibility(View.VISIBLE);
    }

    public static void hideFabMenu(FloatingActionButton fabMenu) {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(500);
        fabMenu.startAnimation(animation);
        fabMenu.setVisibility(View.GONE);
    }

    public void setupSearchViews() {
        initializeSearchView(mFloat);
//        initializeSearchView(routingFloat);
        initializeRoutingSearchView(routingFloat);
    }

    public void initializeRoutingSearchView(final FloatingSearchView routingSearchView) {
        routingSearchView.bringToFront();
        routingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                queryChange(routingSearchView, oldQuery, newQuery);
            }
        });

        routingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                RANDOM_ROUTING = false;
                Room selectedRoom = (Room) searchSuggestion;
                LatLng roomLatLng = RoomDataUtils.getRoomLatLng(selectedRoom);
                animateMap(roomLatLng, MapConstants.ZOOM_LEVEL_ROOM);

                setVisibleMapboxLayer(selectedRoom.getFloor());
                srcFloor = selectedRoom.getFloor();
                srcRid = selectedRoom.getRid();
                srcBid = selectedRoom.getOutid();
                srcRoom = selectedRoom;
                latLngForRoutingSrc = roomLatLng;
            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

    }

    private void queryChange(final FloatingSearchView searchView, String oldQuery, String newQuery) {
        if (!oldQuery.equals("") && newQuery.equals("")) {
            searchView.clearSuggestions();

        } else {
            searchView.showProgress();
            RoomDataUtils.findRoomSuggestions(context, newQuery, 4, new RoomDataUtils.OnFindRoomSuggestionsListener() {
                @Override
                public void onResults(ArrayList<Room> results) {
                    // Since the size of routing search window is small, we only show two hints.
                    if (searchView == routingFloat && results.size() > 1) {
                        List<Room> sublist = results.subList(results.size() - 2, results.size());
                        results = new ArrayList<>(sublist);
                    }

                    searchView.swapSuggestions(results);
                    searchView.hideProgress();
                }
            });
        }
    }

    private void initializeSearchView(final FloatingSearchView searchView) {
        searchView.bringToFront();
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                queryChange(searchView, oldQuery, newQuery);
            }
        });

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                Room selectedRoom = (Room) searchSuggestion;
                LatLng roomLatLng = RoomDataUtils.getRoomLatLng(selectedRoom);

//                latLngfortouring = roomLatLng;
                int layerNum = selectedRoom.getFloor();

//                if (searchView != routingFloat) {
                setVisibleMapboxLayer(layerNum);
                desFloor = layerNum;

                //map.clear();

                animateMap(roomLatLng, MapConstants.ZOOM_LEVEL_ROOM);

                MarkerViewOptions markerViewOptions = new MarkerViewOptions().position(roomLatLng);
                map.addMarker(markerViewOptions);

                RoomDataUtils.saveRoomEntry(context, selectedRoom);

//                showRoomInfo(roomLatLng);

//                } else {
//                    srcFloor = selectedRoom.getFloor();
//                    srcRid = selectedRoom.getRid();
//                    srcBid = selectedRoom.getOutid();
//                    latLngForRoutingSrc = roomLatLng;
//                }

                //TODO: Simulate a click at this location, call the onClicked function passing in roomLatLng
                //TODO: Change the room database to use the label coordinates found in the geoJSON file
            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                ((FragmentActivity) activity).getSupportFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);
            }

            @Override
            public void onFocusCleared() {
            }
        });
    }

    private void showRoomInfo(LatLng latLng) {
        //TODO: Check if this clear is necessary. If I have washrooms showing and i accidentally click, do I want them getting cleared?
        //map.clear();

        try {
            map.removeMarker(userClickMarker.getMarker());
            hideCardView(infoCardView);
        } catch (Exception e) {
        }

        // if you're past room zoom level, don't change the zoom level
        // if you're less than room zoom, then zoom into room zoom
        if (map.getCameraPosition().zoom <= MapConstants.ZOOM_LEVEL_ROOM) {
            animateMap(latLng, MapConstants.ZOOM_LEVEL_ROOM);
        } else {
            animateMap(latLng, map.getCameraPosition().zoom);
        }

        if (selectedBuilding != null) {
            map.removePolygon(selectedBuilding);
        }

        ((FragmentActivity) activity).getSupportFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);
        final PointF pointf = map.getProjection().toScreenLocation(latLng);
        RectF rectF = new RectF(pointf.x - 5, pointf.y - 5, pointf.x + 5, pointf.y + 5);
        List<Feature> fillFeatureList = map.queryRenderedFeatures(rectF, map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_FILL, currentLevel)).getId());

        String roomName = "room";
        String buildingName = "building";
        for (com.mapbox.services.commons.geojson.Feature feature : fillFeatureList) {
            try {
                roomName = feature.getProperties().get("name").getAsString();
                buildingName = feature.getProperties().get("building_name").getAsString();
            } catch (Exception e) {
                Log.e(TAG, "Error with getting name and building name from queried point");
            }
        }

        try {
            for (Feature feature : fillFeatureList) {
                int isBookable = feature.getProperties().get("bookable").getAsInt();
                if (isBookable == MapConstants.BOOKING_INNIS_KEY ||
                        isBookable == MapConstants.BOOKING_MILLS_KEY ||
                        isBookable == MapConstants.BOOKING_THODE_KEY) {
                    bookThisRoomText.setVisibility(View.VISIBLE);
                    bookingSelection = isBookable;
                } else {
                    bookThisRoomText.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            Log.v(TAG, "The selected room is not bookable");
            bookThisRoomText.setVisibility(View.GONE);
        }

        // 0 = fill layer
        // 1 = line layer
        // 2 = symbol layer
        Location destlatlon = new Location("");
        destlatlon.setLongitude(latLng.getLongitude());
        destlatlon.setLatitude(latLng.getLatitude());
        destLocation = destlatlon;
        RANDOM_ROUTING = false;
        desFloor = currentLevel;

        if (fillFeatureList.size() > 0) {

            hideCardView(directionsCardView);
            showCardView(infoCardView);

            infoCardView.bringToFront();
            infoRoomName.setText(roomName);
            infoBuildingName.setText(buildingName);

            hideFabMenu(navigationFAB);
            hideLevelButton();

            String featureId = fillFeatureList.get(0).getId();

            MarkerViewOptions markerViewOptions = new MarkerViewOptions().position(new LatLng(latLng.getLatitude(), latLng.getLongitude()));
            map.addMarker(markerViewOptions);

            userClickMarker = markerViewOptions;

            for (int a = 0; a < fillFeatureList.size(); a++) {
                if (featureId.equals(fillFeatureList.get(a).getId())) {
                    if (fillFeatureList.get(a).getGeometry() instanceof Polygon) {

                        List<LatLng> list = new ArrayList<>();

                        for (int i = 0; i < ((Polygon) fillFeatureList.get(a).getGeometry()).getCoordinates().size(); i++) {
                            for (int j = 0;
                                 j < ((Polygon) fillFeatureList.get(a).getGeometry()).getCoordinates().get(i).size(); j++) {
                                list.add(new LatLng(
                                        ((Polygon) fillFeatureList.get(a).getGeometry()).getCoordinates().get(i).get(j).getLatitude(),
                                        ((Polygon) fillFeatureList.get(a).getGeometry()).getCoordinates().get(i).get(j).getLongitude()
                                ));
                            }
                        }
                        selectedBuilding = map.addPolygon(new PolygonOptions()
                                .addAll(list)
                                .fillColor(context.getResources().getColor(R.color.colorPrimaryLight))
                        );
                    }
                }
            }
        } else {
            hideCardView(infoCardView);
            hideCardView(directionsCardView);
            showFabMenu(navigationFAB);
            showLevelButton();
        }
    }

    //Merge
//    public void getLocation(boolean withCameraMove,boolean postLocation) {
//        if (userLocation == null) {
//            userLocation = new UserLocation(context, map);
//        }
//        userLocation.toggleGps(!map.isMyLocationEnabled(), false);
//
//        fillVisiblePoints();
//
//
//        String userLoc = generateLocationForServer();
//
//        if (postLocation){
//            boolean check = checkServerPermissions();
//            if (check){
//                if(uploadLocationTimer == null)
//                {
//                    initialUploadTimer();
//                    uploadLocationTimer.schedule(uploadLocationTimerTask, ServerUtils.HEAT_MAP_UPLOAD_DELAY_TIME, ServerUtils.HEAT_MAP_UPLOAD_PERIOD_TIME);
//
//                }
//                //sendLocationToServer(heatMapURL,userLoc,"");
//            }
//        }
//        else{
//            ///////////PUT_TIMER
//            if(uploadLocationTimer != null)
//            {
//                uploadLocationTimer.cancel();
//                uploadLocationTimer = null;
//            }
//            ///////////PUT_TIMER
//        }
//
//        if (isLocationEnabled()){
//
//            MapboxMap.OnMyLocationChangeListener onMyLocationChangeListener = (new MapboxMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(@Nullable Location location) {
//                    //ekfLocationService.inputLocationInfo(location);
//
//                    userLocation.currentLocation = location;
//                    //Log.d ("EKF", "Location changed");
//                    arCheck(location);
//
//
//                    if (heatMapID != -1){
//                        String userLoc = generateLocationForServer();
//
//                        if (ActivityCompat.checkSelfPermission(context, com.mcmaster.wiser.idyll.Manifest.permission.SEND_LOCATION) == PackageManager.PERMISSION_GRANTED){
//                            //To test break channel.
//                            //sendLocationToServer(ServerUtils.HEAT_MAP_URL,userLoc,"");
//                        }
//                    }
//                }
//            });
//
//            map.setOnMyLocationChangeListener(onMyLocationChangeListener);
//        }
//        else{
//            map.setOnMyLocationChangeListener(null);
//        }
//        Location myLocation = userLocation.currentLocation;
//
//        if (!(myLocation == null)) {
//            if (withCameraMove && isLocationOnCampus(myLocation)) {
//                animateMap(new LatLng(myLocation), MapConstants.ZOOM_LEVEL_BUILDING);
//            } else if (!withCameraMove && isLocationOnCampus(myLocation)) {
//            } else {
//                Toast.makeText(context, "MacQuest has detected that you are not currently within campus bounds", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(context, "Current location cannot be found", Toast.LENGTH_SHORT).show();
//        }
//    }

    //Merge
    public void getLocation(boolean withCameraMove, boolean postLocation) {
        if (MainActivity.isOutdoor) {
            if (userLocation == null) {
                userLocation = new UserLocation(context, map);
            }
            //userLocation.toggleGps(!map.isMyLocationEnabled(), false);
            //map.setMyLocationEnabled(true);
            Location myLocation = userLocation.currentLocation;

            if (!(myLocation == null)) {
                if (withCameraMove && isLocationOnCampus(myLocation)) {
                    animateMap(new LatLng(myLocation), MapConstants.ZOOM_LEVEL_BUILDING);
                } else if (!withCameraMove && isLocationOnCampus(myLocation)) {
                } else {
                    Toast.makeText(context, "MacQuest has detected that you are not currently within campus bounds", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Current location cannot be found", Toast.LENGTH_SHORT).show();
            }
        }
        if (userLocation == null) {
            userLocation = new UserLocation(context, map);
        }
        //userLocation.toggleGps(!map.isMyLocationEnabled(), false);

        fillVisiblePoints();


        String userLoc = generateLocationForServer();

        if (postLocation) {
            boolean check = checkServerPermissions();
            if (check) {
                if (uploadLocationTimer == null) {
                    initialUploadTimer();
                    uploadLocationTimer.schedule(uploadLocationTimerTask, ServerUtils.HEAT_MAP_UPLOAD_DELAY_TIME, ServerUtils.HEAT_MAP_UPLOAD_PERIOD_TIME);

                }
                //sendLocationToServer(heatMapURL,userLoc,"");
            }
        } else {
            ///////////PUT_TIMER
            if (uploadLocationTimer != null) {
                uploadLocationTimer.cancel();
                uploadLocationTimer = null;
            }
            ///////////PUT_TIMER
        }

        if (isLocationEnabled()) {

            MapboxMap.OnMyLocationChangeListener onMyLocationChangeListener = (new MapboxMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(@Nullable Location location) {
                    //ekfLocationService.inputLocationInfo(location);

                    userLocation.currentLocation = location;
                    //Log.d ("EKF", "Location changed");
                    arCheck(location);


                    if (heatMapID != -1) {
                        String userLoc = generateLocationForServer();

                        if (ActivityCompat.checkSelfPermission(context, com.mcmaster.wiser.idyll.Manifest.permission.SEND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            //To test break channel.
                            //sendLocationToServer(ServerUtils.HEAT_MAP_URL,userLoc,"");
                        }
                    }
                }
            });

            map.setOnMyLocationChangeListener(onMyLocationChangeListener);
        } else {
            map.setOnMyLocationChangeListener(null);
        }
        Location myLocation = userLocation.currentLocation;

        if (!(myLocation == null)) {
            if (withCameraMove && isLocationOnCampus(myLocation)) {
                animateMap(new LatLng(myLocation), MapConstants.ZOOM_LEVEL_BUILDING);
            } else if (!withCameraMove && isLocationOnCampus(myLocation)) {
            } else {
                Toast.makeText(context, "MacQuest has detected that you are not currently within campus bounds", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Current location cannot be found", Toast.LENGTH_SHORT).show();
        }
    }


    public String generateLocationForServer() {
        return "{\"coord\": \"POINT(" + userLocation.currentLocation.getLongitude() + " " +
                userLocation.currentLocation.getLatitude() + ")\"}";
    }

    public boolean checkServerPermissions() {
        if (ActivityCompat.checkSelfPermission(context, com.mcmaster.wiser.idyll.Manifest.permission.SEND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST = 1;
            ActivityCompat.requestPermissions(activity, new String[]{com.mcmaster.wiser.idyll.Manifest.permission.SEND_LOCATION}, MY_PERMISSIONS_REQUEST);
            return false;
        }
        return true;
    }


    public void sendLocationToServer(final String serverURL, final String userLoc, final String mode) {
        /*if (!mode.equals("DELETE")){
            if (checkServerPermissions()){
                Log.d("sendLocationToServer","Permission Checked");
            }
        }*/

        //TODO: FIX THIS THING SO IF YOU SAY NO IT DOESN'T DO IT
        Thread sendInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    if (heatMapID == -1 && !mode.equals("DELETE")) {
                        httpURLConnection = (HttpURLConnection) new URL(serverURL).openConnection();
                        httpURLConnection.setRequestMethod("POST");
                    } else {
                        httpURLConnection = (HttpURLConnection) new URL(serverURL + heatMapID + "/").openConnection();
                        if (mode.equals("DELETE")) {
                            httpURLConnection.setRequestMethod("DELETE");
                        } else {
                            httpURLConnection.setRequestMethod("PATCH");
                        }

                    }


                    String userCredentials = "access-name:wiserlab1835";
                    String basicAuth = "Basic " + new String(Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP));
                    httpURLConnection.setRequestProperty("Authorization", basicAuth);


                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();
                    OutputStream out = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                    bw.write(userLoc);
                    bw.flush();
                    out.close();
                    bw.close();
                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED || httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = httpURLConnection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String str = null;
                        StringBuffer buffer = new StringBuffer();
                        while ((str = br.readLine()) != null) {
                            buffer.append(str);
                        }

                        Log.d("Server response: ", buffer.toString());

                        if (heatMapID == -1) {
                            JSONObject jsonObject = new JSONObject(buffer.toString());
                            heatMapID = jsonObject.getInt("id");
                        }
                        in.close();
                        br.close();
                    }

                    if (mode.equals("DELETE")) {
                        heatMapID = -1;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }

            }
        });

        if (ActivityCompat.checkSelfPermission(context, com.mcmaster.wiser.idyll.Manifest.permission.SEND_LOCATION) == PackageManager.PERMISSION_GRANTED || mode.equals("DELETE")) {
            sendInfo.start();
        }
    }


    public void arCheck(Location location) {
        int eventCount = 0;

        if (location != null) {
            Toast.makeText(context, String.format("lat:%f, lon:%f, isOutDoor:%s", location.getLatitude(), location.getLongitude(), MainActivity.isOutdoor ? "True" : "False"), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"location is null", Toast.LENGTH_SHORT).show();
        }

        if (arPoints != null && location != null && MainActivity.isOutdoor) {
        //if (arPoints != null && location != null ) {
            //Check for visible AR points.
            //for (ArrayList<String> point : arPoints){


            for (int i = 0; i < arPoints.size(); i++) {
                ArrayList<String> point = arPoints.get(i);
                //Finds the lat/lng values of the point.
                Double latP = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LAT));
                Double lngP = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LON));
                Integer floorP = Integer.parseInt(point.get(EventUtils.EVENT_OBJECT_INDEX_FLOOR));


                // Checks to see if the point is within the visible threshold.
                if (inVisibleThreshold(latP, lngP, location.getLatitude(), location.getLongitude(), floorP)) {

                    changeEventListItemTextColor(eventCount);
                    if(point.get(EventUtils.EVENT_OBJECT_INDEX_HAS_ACTION).equals(EventUtils.EVENT_ACTION_TRUE)) {
                        Event.pointsActionDialog(context, point);
                    }


                    if (!visibleARPoints.contains(point)) {

                        visibleARPoints.add(point);
                        toggleARButton.setVisibility(View.VISIBLE);

                        Log.d("Found new visible POI ", point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_PID));
                    }

                    userLastLocation = location;

                } else {
                    if (visibleARPoints.contains(point)) {

                        visibleARPoints.remove(point);
                        Log.d("Removed visible POI: ", point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_PID));
                    }
                }

                //
                eventCount++;
            }

            if (visibleARPoints.isEmpty()) {
                toggleARButton.setVisibility(View.GONE);
            }

        } else {
            toggleARButton.setVisibility(View.GONE);
        }
    }

    public boolean inVisibleThreshold(double latP, double lngP, double latU, double lngU, int floor) {
        if (floor != 1) {
            return false;
        }
        double R = 6378.137 * 1000; // Radius of earth in meters
        double dLat = latU * Math.PI / 180 - latP * Math.PI / 180;
        double dLon = lngU * Math.PI / 180 - lngP * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(latP * Math.PI / 180) * Math.cos(latU * Math.PI / 180) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance;
        distance = R * c;

        Log.d("inVisibleThreshold: ", "Distance - " + distance);

        if (distance <= EventUtils.EVENT_VISIBLE_THRESHOLD) {
            return true;
        } else {
            return false;
        }


    }

    //Function to change EventList item color when the user reach it.
    private void changeEventListItemTextColor(int index) {
        if (eventListView != null) {
            /*
            //Already visit event. Change the text color to gray.
            if(index >= eventListView.getDividerHeight()){
                eventListView.setSelection(index);
                index -= (eventListView.getDividerHeight() + 1);
            }
            TextView view = (TextView) eventListView.getChildAt(index);

            if(view != null)
            {
                ToolsLab colorTools = new ToolsLab();
                if(colorTools.getBackgroundColor(view) != context.getResources().getColor(R.color.visitedEvent))
                {
                    Log.i("EventItem", String.format("Current index:%d", index));
                    view.setTextColor(context.getResources().getColor(R.color.visitedEvent));
                }

            }
            */

            EventListAdapter adapter = (EventListAdapter) eventListView.getAdapter();

            adapter.getItem(index).setVisit(true);

            adapter.notifyDataSetChanged();

        }

    }


    public void showFabMenus() {
        clearRoutes();
        showFabMenu(navigationFAB);
    }
//Merge
//    public void getLocationForRouting() {
//        if (userLocation == null) {
//            userLocation = new UserLocation(context, map);
//            userLocation.toggleGps(true, true);
//            navigationFAB.setIconDrawable(context.getResources().getDrawable(R.drawable.ic_location_disabled_white_24dp));
//        } else {
//            userLocation.updateLocation();
//        }
//    }

    //merge
    public void getLocationForRouting() {
        if (userLocation == null) {
            userLocation = new UserLocation(context, map);
            userLocation.toggleGps(true, true);
            navigationFAB.setIconDrawable(context.getResources().getDrawable(R.drawable.ic_location_disabled_white_24dp));
        } else {
            userLocation.updateLocation();
        }
    }

    private boolean isLocationOnCampus(Location location) {
        LatLng northWest = MapConstants.CAMPUS_BOUNDS.getNorthWest();
        LatLng southEast = MapConstants.CAMPUS_BOUNDS.getSouthEast();
        LatLng southWest = MapConstants.CAMPUS_BOUNDS.getSouthWest();

        LatLng currentLocation = new LatLng(location);

        if (currentLocation.getLatitude() >= southWest.getLatitude()
                && currentLocation.getLatitude() <= northWest.getLatitude() &&
                currentLocation.getLongitude() >= southWest.getLongitude() &&
                currentLocation.getLongitude() <= southEast.getLongitude()
                ) {
            return true;
        } else {
            return false;
        }
    }

    //Merge
//    public void showRoutingUI() {
//        //TODO : Find error in the textbox here. The To: textbox always has routing to after it.
//        String destinationText = (String) infoBuildingName.getText();
//        if (!infoRoomName.getText().equals("Routing to")){
//            destinationText += " " + infoRoomName.getText();
//        }
//        destinationTextView.setText(destinationText);
//
//        routingFloat.clearQuery();
//
//        infoCardView.setVisibility(View.GONE);
//        showCardView(directionsCardView);
//
//
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            int MY_PERMISSIONS_REQUEST = 1;
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
//        }
//
//        try {
//            getLocationForRouting();
//            latLngForRoutingSrc = new LatLng(userLocation.currentLocation);
//        } catch (Exception e) {
//            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                int MY_PERMISSIONS_REQUEST = 1;
//                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
//                return;
//            }
//        }
//
//    }

    public void showRoutingUI() {
        //TODO freezes here
        //TODO : Find error in the textbox here. The To: textbox always has routing to after it.

        String destinationText = (String) infoBuildingName.getText();

        if (!infoRoomName.getText().equals("Routing to")) {

            destinationTextView.setText(infoBuildingName.getText() + " " + infoRoomName.getText());

            destinationText += " " + infoRoomName.getText();

        }
        destinationTextView.setText(infoBuildingName.getText() + " " + infoRoomName.getText());

        routingFloat.clearQuery();

        infoCardView.setVisibility(View.GONE);
        showCardView(directionsCardView);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST = 1;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
        }

        try {
            getLocationForRouting();
            latLngForRoutingSrc = new LatLng(userLocation.currentLocation);
        } catch (Exception e) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                int MY_PERMISSIONS_REQUEST = 1;
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
                return;
            }
        }

    }

//    Merge
//    public String[] CalculateRoutes() {
//
//        String[] Queryresult = new String[3];
//        Location sourceLocation;
//
//        showpath = true;
//        try {
//            if (routing == null) {
//                routing = new Routing(context);
//            }
//            //Log.d("routingtime","start to calculate route");
//            //Queryresult = dbHandler.getroutingr2latlon(3, 2245, "-79.92120891357", "43.25864643861");
//            // Queryresult = dbHandler.getroutingr2latlon_crossfloor(3, 2245,Double.toString(latLngfortouring.getLongitude()),Double.toString(latLngfortouring.getLatitude()),currentLevel);
//
//            // Get Souurce location
//            if (srcRid == 0 && srcBid == 0) {
//                srcRid = Integer.parseInt(routing.findRid(Double.toString(UserLocation.currentLocation.getLongitude()), Double.toString(UserLocation.currentLocation.getLatitude()), srcFloor));
//                sourceLocation = UserLocation.currentLocation;
//                latLngForRoutingSrc = new LatLng(userLocation.currentLocation);
//            } else {
//                sourceLocation = RoomDataUtils.getRoomRoutingPoints(srcRoom);
//            }
//
//
//            if (RANDOM_ROUTING) {
//                Queryresult = routing.getroutingr2latlon_ramdon(srcFloor, srcRid, srcBid, destLocation);
//            } else {
//                Log.d("routing", "srcFloor" + Integer.toString(srcFloor) + "destfloor" + desFloor);
//                Log.d("routing", "latLngForRoutingDest" + Double.toString(destLocation.getLongitude()) + "latLngForRoutingDest" + Double.toString(destLocation.getLatitude()) + "desFloor" + Integer.toString(desFloor));
//                Queryresult = routing.getroutingr2latlon_crossfloor(srcFloor, srcRid, srcBid, sourceLocation, destLocation, desFloor);
//            }
//            RANDOM_ROUTING = false;
//            srcRid = 0;
//            srcBid = 0;
//            srcRoom = null;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//       // Log.d("routingtime","finished to calculate route");
//        return Queryresult;
//
//    }

    public String[] CalculateRoutes() {

        String[] Queryresult = new String[3];
        Location sourceLocation;

        showpath = true;
        try {
            if (routing == null) {
                routing = new Routing(context);
            }
            //Log.d("routingtime","start to calculate route");
            //Queryresult = dbHandler.getroutingr2latlon(3, 2245, "-79.92120891357", "43.25864643861");
            // Queryresult = dbHandler.getroutingr2latlon_crossfloor(3, 2245,Double.toString(latLngfortouring.getLongitude()),Double.toString(latLngfortouring.getLatitude()),currentLevel);

            // Get Souurce location
            if (srcRid == 0 && srcBid == 0) {
                srcRid = Integer.parseInt(routing.findRid(Double.toString(UserLocation.currentLocation.getLongitude()), Double.toString(UserLocation.currentLocation.getLatitude()), srcFloor));
                sourceLocation = UserLocation.currentLocation;
                latLngForRoutingSrc = new LatLng(userLocation.currentLocation);

            } else {
                sourceLocation = RoomDataUtils.getRoomRoutingPoints(srcRoom);
            }


            if (RANDOM_ROUTING) {
                Queryresult = routing.getroutingr2latlon_ramdon(srcFloor, srcRid, srcBid, destLocation);
            } else {
                Log.d("routing", "srcFloor" + Integer.toString(srcFloor) + "destfloor" + desFloor);
                Log.d("routing", "latLngForRoutingDest" + Double.toString(destLocation.getLongitude()) + "latLngForRoutingDest" + Double.toString(destLocation.getLatitude()) + "desFloor" + Integer.toString(desFloor));
                Queryresult = routing.getroutingr2latlon_crossfloor(srcFloor, srcRid, srcBid, sourceLocation, destLocation, desFloor);
            }
            RANDOM_ROUTING = false;
            srcRid = 0;
            srcBid = 0;
            srcRoom = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Log.d("routingtime","finished to calculate route");
        return Queryresult;

    }

    public void setVisibleMapboxLayer(int choice) {

        if (choice != currentLevel) {
            map.clear();
            if (selectedBuilding != null) {
                map.removePolygon(selectedBuilding);
            }

            if (markers.get(String.valueOf(choice)) != null) {
                for (MarkerViewOptions marker : markers.get(String.valueOf(choice))) {
                    map.addMarker(marker);
                }
            }
            if (markers.get("None Available") != null) {
                for (MarkerViewOptions marker : markers.get("None Available")) {
                    map.addMarker(marker);
                }
            }

        }

        for (int i = -1; i < MapConstants.NUMBER_OF_FLOORS; i++) {
            if (i == choice) {
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_FILL, i)).setProperties(visibility(VISIBLE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_ROOM, i)).setProperties(visibility(VISIBLE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_LABELS, i)).setProperties(visibility(VISIBLE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_WASHROOM, i)).setProperties(visibility(VISIBLE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_STAIR, i)).setProperties(visibility(VISIBLE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_ELEVATOR, i)).setProperties(visibility(VISIBLE));
            } else {
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_FILL, i)).setProperties(visibility(NONE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_ROOM, i)).setProperties(visibility(NONE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_LABELS, i)).setProperties(visibility(NONE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_WASHROOM, i)).setProperties(visibility(NONE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_STAIR, i)).setProperties(visibility(NONE));
                map.getLayer(getLayerName(MapConstants.MAPBOX_LAYER_CHOICE_ELEVATOR, i)).setProperties(visibility(NONE));
            }
        }
        if (showpath) {
            Log.d("showpath", "start3");
            setVisibleRoutingLayer(choice);
        }
        currentLevel = choice;

        // Data collection.
        String buildID = "";
        if (map != null) {
            buildID = getBuildingNameFromCameraPosition(map.getCameraPosition());
        }
        if (dataCollectionManager != null && buildID != "") {
            dataCollectionManager.startCollectData(buildID, currentLevel + "");
        }

        changeSelectedLayerButton(choice);

        //  setVisibleRoutingLayer(choice);
    }

    private Button getButtonFromChoice(int choice) {
        if (choice > 0) { //finds button from the floorButtonList
            for (int i = 0; i < floorButtonList.size(); i++) {
                if (i + 1 == choice) {
                    return floorButtonList.get(i);
                }
            }
        } else { //finds button from basementButtonList
            for (int i = 0; i < basementButtonList.size(); i++) {
                if (-i == choice) {
                    return basementButtonList.get(i);
                }
            }
        }

        return null;
    }

    private void changeSelectedLayerButton(int choice) {
        Button button = getButtonFromChoice(choice);

        for (Button b : floorButtonList) {
            if (b.getId() == button.getId()) {
                button.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            } else {
                b.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }
        for (Button b : basementButtonList) {
            if (b.getId() == button.getId()) {
                button.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            } else {
                b.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            }
        }


    }

    public void setVisibleRoutingLayer(int mylevel) { //this is just use for the routing right? maybe rename to something more descriptive
        if (floorNumberRoute1 == mylevel) {
            routeLayer1.setProperties(visibility(VISIBLE));
        } else {
            routeLayer1.setProperties(visibility(NONE));
        }
        if (floorNumberRoute2 == mylevel) {
            routeLayer2.setProperties(visibility(VISIBLE));
        } else {
            routeLayer2.setProperties(visibility(NONE));
        }
        if (floorNumberRoute3 == mylevel) {
            routeLayer3.setProperties(visibility(VISIBLE));
        } else {
            routeLayer3.setProperties(visibility(NONE));
        }
    }

    //Merge
//    public void showNearestWashroom() {
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            int MY_PERMISSIONS_REQUEST = 1;
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
//            return;
//        }
//
//
//
//        //Check if the user location is enabled on the map and if not, then enable it.
//        if (!isLocationEnabled()){
//            //getLocation(true);
//            navigationFAB.callOnClick();
//        }
//
//
//
//        Log.v(TAG, "showing nearest washrooms");
//        Routing routing = new Routing(context);
//
//        map.clear();
//
//        getLocationForRouting();
//        Location myLocation = UserLocation.currentLocation;
//        if(myLocation==null){
//            return;
//        }
//        //TODO: move the routing washroom search to an async task
//        ArrayList<Location> washroomLocations = routing.findWashrooms(
//                Double.toString(myLocation.getLongitude()),
//                Double.toString(myLocation.getLatitude()),
//                currentLevel);
//
//        userLocation.setupLocationViewSettings();
//
//        for (Location location : washroomLocations) {
//            MarkerViewOptions newMarker = new MarkerViewOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
//            map.addMarker(newMarker);
//
//            if(!markers.containsKey(String.valueOf(currentLevel))){
//                markers.put(String.valueOf(currentLevel),new ArrayList<MarkerViewOptions>());
//
//            }
//
//            markers.get(String.valueOf(currentLevel)).add(newMarker);
//
//
//        }
//
//        if (washroomLocations.size() == 0){
//            MainActivity.washroomsOn = false;
//            Toast.makeText(context, "No nearby washrooms", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void showNearestWashroom() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST = 1;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            return;
        }


        //Check if the user location is enabled on the map and if not, then enable it.
        if (!isLocationEnabled()) {
            //getLocation(true);
            navigationFAB.callOnClick();
        }


        Log.v(TAG, "showing nearest washrooms");
        Routing routing = new Routing(context);

        map.clear();

        getLocationForRouting();
        Location myLocation = UserLocation.currentLocation;
        if (myLocation == null) {
            return;
        }
        //TODO: move the routing washroom search to an async task
        ArrayList<Location> washroomLocations = routing.findWashrooms(
                Double.toString(myLocation.getLongitude()),
                Double.toString(myLocation.getLatitude()),
                currentLevel);

        userLocation.setupLocationViewSettings();

        for (Location location : washroomLocations) {
            MarkerViewOptions newMarker = new MarkerViewOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
            map.addMarker(newMarker);

            if (!markers.containsKey(String.valueOf(currentLevel))) {
                markers.put(String.valueOf(currentLevel), new ArrayList<MarkerViewOptions>());

            }

            markers.get(String.valueOf(currentLevel)).add(newMarker);


        }

        if (washroomLocations.size() == 0) {
            MainActivity.washroomsOn = false;
            Toast.makeText(context, "No nearby washrooms", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean fillEventList() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            mode = "event";
            new FetchEventData().execute().get();
            if (arFound) {
                arFound = false;
                return true;
            } else {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static void showEventsWithID(String id) {
        try {
            mode = "sub-event";
            idSearch = id;

            //TODO: SEND ID VALUE TO SERVER FOR HEATMAP

            new FetchEventData().execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        idSearch = null;
    }

    public void showNearestARPoints() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST = 1;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            return;

        }

        startProgress();
        progressBar.setVisibility(View.VISIBLE);
        //TODO: Add in a loading circle thing for this so it doesn't feel like I froze the app.

        //Move to user location. Optional to add in, mostly depends how we want AR to work.
        //getLocation(true);

        //navigationFAB.setIconDrawable(ResourcesCompat.getDrawable(R.drawable.ic_location_disabled_24dp));


        // Execute the data fetcher and it sets the arPoints value.
        // The get ensures that you finish the AsyncTask before moving on.
        // This is required as arPoints needs to be set for the AR POI's to be drawn.
        try {
            mode = "sub-event";
            new FetchEventData().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //Draws the AR points on the map. Problem is Async tasks don't go fast enough.
        if (drawARPoints()) {
            endProgress("Found AR points");
            fillVisiblePoints();
        } else {
            endProgress("No AR points found");
        }

        Log.d("AR", "AR points set.");
        progressBar.setVisibility(View.GONE);
    }

    private void fillVisiblePoints() {
        if (isLocationEnabled() && arPoints != null) {
            //for (ArrayList<String> point : arPoints){
            for (int i = 0; i < arPoints.size(); i++) {
                ArrayList<String> point = arPoints.get(i);
                Double latP = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LAT));
                Double lngP = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LON));
                Integer floorP = Integer.parseInt(point.get(EventUtils.EVENT_OBJECT_INDEX_FLOOR));
                if (inVisibleThreshold(latP, lngP, map.getMyLocation().getLatitude(), map.getMyLocation().getLongitude(), floorP)) {

                    //if(MainActivity.isOutdoor){
                    visibleARPoints.add(point);
                    toggleARButton.setVisibility(View.VISIBLE);
                    //}

                    userLastLocation = map.getMyLocation();
                }
            }
        }
    }

    public void clearMarkers() {
        for (String s : markers.keySet()) {
            for (MarkerViewOptions markerViewOptions : markers.get(s)) {
                map.removeMarker(markerViewOptions.getMarker());
            }
        }
    }

    public boolean drawARPoints() {
        toggleCurrentEvents.setVisibility(View.VISIBLE);
        eventListCardView.setVisibility(View.VISIBLE);
        LatLng eventMapCenter = MapConstants.McMasterStartingPoint;
        double eventMapCenterLat = MapConstants.McMasterStartingPoint.getLatitude();
        double eventMapCenterLng = MapConstants.McMasterStartingPoint.getLongitude();
        if (arPoints != null) {
            ArrayList<LatLng> arPointLocations = new ArrayList<>();
            //for (ArrayList<String> point : arPoints){
            for (int i = 0; i < arPoints.size(); i++) {
                ArrayList<String> point = arPoints.get(i);

                Double lat = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LAT));
                Double lng = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LON));
                arPointLocations.add(new LatLng(lat, lng));
                eventMapCenterLat += lat;
                eventMapCenterLng += lng;


            }

            //Find the map center according to the event data.
            eventMapCenterLat /= (arPoints.size() + 1);
            eventMapCenterLng /= (arPoints.size() + 1);
            eventMapCenter = new LatLng(eventMapCenterLat, eventMapCenterLng);


            // Initializing a new String Array
            // ArrayList<String> eventNames = new ArrayList<>();
            ArrayList<EventItem> eventNames = new ArrayList<>();


            //EventListAdapter eventListItem = new EventListAdapter<EventItem>();


            for (int i = 0; i < arPointLocations.size(); i++) {

                String building = arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_BUILDING);
                String location_room = arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_ROOM);
                String floor = arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_FLOOR);


                //Handle the start time and end time. Make it user friendly.
                String startDate = arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_START_TIME);
                String endDate = arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_END_TIME);
                startDate = startDate.replace("T", ", ");
                endDate = endDate.replace("T", ", ");

                String description = "";

                /*
                //Verify whether it has Event Description web URL.
                if(!arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL).equals(EventUtils.EVENT_WEB_URL_NONE))
                {
                    //Yes it has. Add it into info card.

                    //Description format: Description<w>URL</w>
                    description = String.format("%s%s%s%s",
                            arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION),
                            EventUtils.EVENT_WEB_URL_MARKER_BEGIN,
                            arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL),
                            EventUtils.EVENT_WEB_URL_MARKER_END
                            );
                }else
                {
                    description = arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION);
                }
                */


                description = arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION);
                String temp_snippet = "";

                //Handle building info.
                if (building.equals("Outdoor")) {
                    //It is outdoor event.
                    temp_snippet = "Description: " + description + "\n" +
                            "Floor: " + floor + "\n" +
                            "Start Date: " + startDate + "\n" +
                            "End Date: " + endDate;
                } else {
                    //It is indoor event.
                    temp_snippet = "Description: " + description + "\n" +
                            "Building: " + building + " " + location_room + "\n" +
                            "Floor: " + floor + "\n" +
                            "Start Date: " + startDate + "\n" +
                            "End Date: " + endDate;

                }


                MarkerViewOptions newMarker = new MarkerViewOptions().position(arPointLocations.get(i))
                        .title(arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_SUB_TITLE))
                        .snippet(temp_snippet);
                map.addMarker(newMarker);

                //eventNames.add(arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_SUB_TITLE));
                eventNames.add(new EventItem(arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_SUB_TITLE), false, false));

                if (!markers.containsKey(floor)) {
                    markers.put(floor, new ArrayList<MarkerViewOptions>());
                }


                markers.get(floor).add(newMarker);

            }

            //final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,R.layout.list_item_1, eventNames);

            final EventListAdapter arrayAdapter = new EventListAdapter(context, R.layout.list_item_1, eventNames);
            eventListView.setAdapter(arrayAdapter);

            //Zooming to the marker + opening it's info bubble.
            eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    EventItem event = (EventItem) eventListView.getItemAtPosition(i);
                    String s = event.getEventName();
                    ToolsLab viewTools = new ToolsLab();
                    /*
                    //Get current color.
                    int currentBackGroundColor = viewTools.getBackgroundColor(view);
                    //If the current color is blue then we know that we need change it to yellow, otherwise.
                    if(currentBackGroundColor != view.getResources().getColor(R.color.colorGreen)){
                        view.setBackgroundColor(view.getResources().getColor(R.color.colorGreen));
                    }else {
                        view.setBackgroundColor(view.getResources().getColor(R.color.transparent));
                    }
                    */

                    EventViewTools.changeClickViewColor(eventListView, i);

                    for (String floor : markers.keySet()) {

                        for (MarkerViewOptions marker : markers.get(floor)) {
                            if (marker.getTitle().equals(s)) {


                                if (!floor.isEmpty() && Integer.parseInt(floor) != currentLevel) {
                                    setVisibleMapboxLayer(Integer.parseInt(floor));
                                }


                                LatLng pointLocation = marker.getPosition();
                                animateMap(pointLocation, MapConstants.ZOOM_LEVEL_BUILDING);

                                map.selectMarker(marker.getMarker());

                                /*
                                //Grab URL from
                                String[] snippetInfo = FetchEventData.getWebURLFromString(
                                        marker.getSnippet(),
                                        new String[]{EventUtils.EVENT_WEB_URL_MARKER_BEGIN, EventUtils.EVENT_WEB_URL_MARKER_END}
                                        );

                                String cardInfo = marker.getSnippet();


                                boolean hasURL = false;

                                if(snippetInfo[0] != null){
                                    //URL in the description.

                                    marker.snippet(snippetInfo[1]);
                                    hasURL = true;

                                }
                                //Add Marker on the screen.
                                map.selectMarker(marker.getMarker());


                                if(hasURL == true && view.getResources().getColor(R.color.colorGreen) == viewTools.getBackgroundColor(view))
                                {
                                    Event.pointsDescriptionURLDialog(context, arPoints.get(i));

                                }
                                */


                                //Verify whether the
                                if (!arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL).equals(EventUtils.EVENT_WEB_URL_NONE) &&
                                        arPoints.get(i).get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL_SHOW).equals(EventUtils.EVENT_URL_NOT_SHOW)) {
                                    Event.pointsDescriptionURLDialog(context, arPoints.get(i));
                                    arPoints.get(i).set(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION_URL_SHOW, EventUtils.EVENT_URL_SHOWED);
                                }


                            }
                        }
                    }

                    /*for (ArrayList<String> point : arPoints){
                        if (point.get(3).equals(s)){
                            String floor = arPoints.get(i).get(4);
                            if(!floor.isEmpty() && Integer.parseInt(floor) != currentLevel){
                                setVisibleMapboxLayer(Integer.parseInt(floor));
                            }
                            LatLng pointLocation = new LatLng (Double.parseDouble(point.get(6)),Double.parseDouble(point.get(5)));
                            animateMap(pointLocation,map.getCameraPosition().zoom);

                            for(ArrayList<MarkerViewOptions> markersByFloor : markers.values()){
                                for(MarkerViewOptions marker : markersByFloor){
                                    if (marker.getTitle() == s){
                                        map.selectMarker(marker.getMarker());
                                    }
                                }
                            }
                        }
                    }*/
                }
            });

            arrayAdapter.notifyDataSetChanged();

            MainActivity.arOn = true;

            if (userLocation.currentLocation != null) {
                //animateMap(new LatLng(userLocation.currentLocation.getLatitude(),userLocation.currentLocation.getLongitude()),17);
                animateMap(eventMapCenter, MapConstants.ZOOM_LEVEL_EVENT);
            } else {
                //animateMap(map.getCameraPosition().target,map.getCameraPosition().zoom);
                animateMap(eventMapCenter, MapConstants.ZOOM_LEVEL_EVENT);
            }

            return true;
        }

        return false;

    }

    public void clearEventList() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, new ArrayList<String>());
        eventListView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        eventListCardView.setVisibility(View.GONE);
    }

    public void clearARPoints() {
        arPoints = null;
        toggleARButton.setVisibility(View.GONE);
        toggleCurrentEvents.setVisibility(View.GONE);
        clearEventList();
    }

    public boolean foundARPoints() {
        if (arPoints == null) {
            return false;
        } else {
            return true;
        }
    }


    public void pause() {
        mSensorManager.unregisterListener(this);

        ///////////PUT_TIMER
        if (uploadLocationTimer != null) {
            uploadLocationTimer.cancel();
            uploadLocationTimer = null;
        }
        ///////////PUT_TIMER
    }

    public void resume() {


        //Register the sensor listener.
        mSensorManager.registerListener((SensorEventListener) this, gsensor_rotation, SensorManager.SENSOR_DELAY_NORMAL);

        if (isLocationEnabled() == true) {
            ///////////PUT_TIMER
            if (uploadLocationTimer == null) {
                initialUploadTimer();
                uploadLocationTimer.schedule(uploadLocationTimerTask, ServerUtils.HEAT_MAP_UPLOAD_DELAY_TIME, ServerUtils.HEAT_MAP_UPLOAD_PERIOD_TIME);
            }
            ///////////PUT_TIMER
        }
    }

    public void downloadOfflineMap() {
        new AlertDialog.Builder(context)
                .setIcon(activity.getResources().getDrawable(R.drawable.ic_warning_black_24dp))
                .setTitle("Download Offline Map")
                .setMessage("The offline map download is 60MB. Proceed to download?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMapDownload();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void startMapDownload() {

        Toast.makeText(context, "Map downloading in background", Toast.LENGTH_SHORT).show();
        // Set up the OfflineManager
        OfflineManager offlineManager = OfflineManager.getInstance(context);

        // Create a bounding box for the offline region
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(43.267207, -79.914207)) // Northeast
                .include(new LatLng(43.257738, -79.922705)) // Southwest
                .build();

        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                map.getStyleUrl(),
                latLngBounds,
                10,
                20,
                context.getResources().getDisplayMetrics().density);

        // Set the metadata
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Offline Map", "Offline Map");
            String json = jsonObject.toString();
            metadata = json.getBytes("UTF-8");
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        // Create the region asynchronously
        offlineManager.createOfflineRegion(
                definition,
                metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                        // Display the download progress bar
                        startProgress();

                        // Monitor the download progress using setObserver
                        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                            @Override
                            public void onStatusChanged(OfflineRegionStatus status) {

                                // Calculate the download percentage and update the progress bar
                                double percentage = status.getRequiredResourceCount() >= 0
                                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                        0.0;

                                if (status.isComplete()) {
                                    // Download complete
                                    endProgress("Map downloaded successfully.");
                                } else if (status.isRequiredResourceCountPrecise()) {
                                    // Switch to determinate state
                                    setPercentage((int) Math.round(percentage));
                                }
                            }

                            @Override
                            public void onError(OfflineRegionError error) {
                                // If an error occurs, print to logcat
                                Log.e(TAG, "onError reason: " + error.getReason());
                                Log.e(TAG, "onError message: " + error.getMessage());
                            }

                            @Override
                            public void mapboxTileCountLimitExceeded(long limit) {
                                // Notify if offline region exceeds maximum tile count
                                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });

    }

    private void setPercentage(final int percentage) {
        progressBar.setIndeterminate(false);
        progressBar.setProgress(percentage);
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // Progress bar methods
    private void startProgress() {
        // Start and show the progress bar
        isEndNotified = false;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void calculatePath() {
        routeSourc.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeGround.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeDest.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        new CalculatePathAsSync().execute();
    }

    public void calculatePathForAR() {
        routeSourc.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeGround.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeDest.setGeoJson(context.getResources().getString(R.string.empty_geojson));

        routeAR = true;

        new CalculatePathAsSync().execute();
    }

    public String[] calculatePathWithWait() {
        routeSourc.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeGround.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeDest.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        String[] result = new String[3];

        new CalculatePathAsSync().execute();

        return routes;
    }


    public void clearRoutes() {
        routeSourc.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeGround.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeDest.setGeoJson(context.getResources().getString(R.string.empty_geojson));
        routeLayer1.setProperties(visibility(NONE));
        routeLayer2.setProperties(visibility(NONE));
        routeLayer3.setProperties(visibility(NONE));
        clearRoutesButton.setVisibility(View.GONE);
        //hideCardView(routeInfoCardView);
        showpath = false;
    }

    public void bookRoomButtonClicked() {
        String url = MapConstants.BOOKING_URL + Integer.toString(bookingSelection);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        activity.startActivity(i);
    }

    public void hideAllUIElements() {
        navigationFAB.setVisibility(View.GONE);
        levelButtons.setVisibility(View.GONE);
        infoCardView.setVisibility(View.GONE);
        routeInfoCardView.setVisibility(View.GONE);
        directionsCardView.setVisibility(View.GONE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    //Merge
//    public int[] getNumFloorsAtCurrentLocation() {
//        int[] floorNums = new int[2];
//
//
//        getLocation(false,false);
//        if (userLocation.currentLocation==null){
//            return floorNums;
//        }
//        latLngForRoutingSrc = new LatLng(userLocation.currentLocation);
//
//        PointF pointf = map.getProjection().toScreenLocation(latLngForRoutingSrc);
//        RectF rectF = new RectF(pointf.x - 40, pointf.y - 40, pointf.x + 40, pointf.y + 40);
//
//        List<Feature> testFeatures = map.queryRenderedFeatures(rectF, map.getLayer(MapConstants.MAPBOX_CAMPUS_OUTLINE_LAYER).getId());
//        for (Feature f : testFeatures) {
//            try {
//                floorNums[0] = f.getProperties().get("num_floor").getAsInt();
//                floorNums[1] = f.getProperties().get("num_basement").getAsInt();
//            } catch (Exception e) {
//                Log.v(TAG, "Error with getting info from campus outline");
//                floorNums[0] = 1;
//                floorNums[1] = 0;
//            }
//        }
//
//        return floorNums;
//
//    }

    public int[] getNumFloorsAtCurrentLocation() {
        int[] floorNums = new int[2];
        MapFragment.removeUserLocationMarker(map);
        map.setMyLocationEnabled(true);
        userLocation = new UserLocation(activity, map);
        userLocation.toggleGps(true, false);
        userLocation.updateLocation();
        latLngForRoutingSrc = new LatLng(userLocation.currentLocation);

        PointF pointf = map.getProjection().toScreenLocation(latLngForRoutingSrc);
        RectF rectF = new RectF(pointf.x - 40, pointf.y - 40, pointf.x + 40, pointf.y + 40);

        List<Feature> testFeatures = map.queryRenderedFeatures(rectF, map.getLayer(MapConstants.MAPBOX_CAMPUS_OUTLINE_LAYER).getId());
        for (Feature f : testFeatures) {
            buildingNameShort = f.getProperties().get("shortname").getAsString();
            try {
                floorNums[0] = f.getProperties().get("num_floor").getAsInt();
                floorNums[1] = f.getProperties().get("num_basement").getAsInt();
            } catch (Exception e) {
                Log.v(TAG, "Error with getting info from campus outline");
                floorNums[0] = 1;
                floorNums[1] = 0;
            }
        }
        if (!MainActivity.isOutdoor) {
            map.setMyLocationEnabled(false);
            //map.clear();
        }

        return floorNums;

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        //ekfLocationService.inputSensorData(sensorEvent);

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {


            SensorManager.getRotationMatrixFromVector(mRotationMatrix, sensorEvent.values);
            float orientation[] = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float azimut = orientation[0]; // orientation contains: azimut, pitch and roll
            azimut = (float) Math.toDegrees(azimut);
            azimut = (azimut + 360) % 360;

            //Log.d("Orientation", String.valueOf(Math.toDegrees(orientation[1])));

            pitch = (float) Math.toDegrees(orientation[1]);
            fFinalReading = (float) (azimut + m_magneticDeclination);


            if (startARButton != null && !isCalibrated) {
                startARButton.setVisibility(View.VISIBLE);
                startARButton.setClickable(true);

                Toast.makeText(context, "Calibrated to " + fFinalReading, Toast.LENGTH_SHORT).show();

                isCalibrated = true;
            }

            //Log.d("Heading", String.valueOf(fFinalReading));
        }

        /*if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {


            SensorManager.getRotationMatrixFromVector(mRotationMatrix, sensorEvent.values);
                float orientation[] = new float[3];
                SensorManager.getOrientation(mRotationMatrix, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                azimut = (float) Math.toDegrees(azimut);
                azimut = (azimut + 360) % 360;

                //Log.d("Heading", Float.toString(azimut));
                if(sortObj.saveValue(azimut) == true && !isCalibrated){
                    fFinalReading = compassreading.getReading(sortObj.getReadingArray());
                    isCalibrated = true;

                    if (startARButton != null){
                        startARButton.setVisibility(View.VISIBLE);
                        startARButton.setClickable(true);

                        Toast.makeText(context, "Calibrated to " + fFinalReading, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Heading Final: ", String.valueOf(fFinalReading));
                }

        }*/


        /*if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = sensorEvent.values;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = sensorEvent.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = orientation[0]; // orientation contains: azimut, pitch and roll
                azimut = (float) Math.toDegrees(azimut);
                azimut = (azimut + 360) % 360;

                //Log.d("Heading", Float.toString(azimut));
                if(sortObj.saveValue(azimut) == true && !isCalibrated){
                    fFinalReading = compassreading.getReading(sortObj.getReadingArray());
                    isCalibrated = true;

                    if (startARButton != null){
                        startARButton.setVisibility(View.VISIBLE);
                        startARButton.setClickable(true);

                        Toast.makeText(context, "Calibrated to " + fFinalReading, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("Heading Final: ", String.valueOf(fFinalReading));
                }
            }
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void getLocationChanged(Location loc) {

    }

    public static class ARPointService extends IntentService {


        public ARPointService() {
            super("ARPointService");
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            Log.d("Service running: ", "AR Point Service");

        }


        @Override
        public void onCreate() {
            Log.d("Service Created: ", "AR Point Service");
        }


    }

    private class CalculatePathAsSync extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... voids) {
            String[] jsonResponse = new String[3];
            jsonResponse = CalculateRoutes();
            currentlyRouting = true;
            setupRapidLocalization();
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String[] result) {
            //TODO: place a marker at the spot on the route where the user must toggle / switch visible floor layer
            //TODO: calculate an estimated time of arrival (ETA) based on the pathway length, and display it on the UI for the user
            progressBar.setVisibility(View.GONE);
            setRoutes(result);
            clearRoutesButton.setVisibility(View.VISIBLE);

            animateMapForRouting();

            if (routeAR) {
                routeAR = false;

                startARNav();
            }

            //showCardView(routeInfoCardView);
            //      Log.d("routingtime","route is rendered");
        }
    }

    //Merge
    private void setupRapidLocalization() {
        routingDone = false;
        if (MainActivity.isOutdoor) {
            pastBulidingNames = new ArrayList<>();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!map.isMyLocationEnabled()) {
                        MapFragment.removeUserLocationMarker(map);
                        map.setMyLocationEnabled(true);
                    }
                    checkToRegisterReceiver();
                    UserLocation myLocation = new UserLocation(activity, map);
                    myLocation.toggleGps(true, true);
                    myLocation.updateLocation();
                }
            });
        } else {
            System.out.println("HERE at make thread");
            makeWifiThread();
        }
    }

    //Merge
    void makeWifiThread() {
        wifiThreadEnabled = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                setFrequencyBand2Hz(true, wifiManager);
                if (!doneWifiLock) {
                    WifiManager.WifiLock wifilock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "MyLock");
                    wifilock.setReferenceCounted(true);
                    wifilock.acquire();
                    if (!wifilock.isHeld()) {
                        wifilock.acquire();
                    }
                    doneWifiLock = true;
                }

                //Start time of first broadcast
                broadCastTimeList.add(System.currentTimeMillis());
                activity.registerReceiver(cycleWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                checkToRegisterReceiver();
            }
        }).start();
    }

    //Merge
    //Used to set a lock to 2.4GHz networks. This speeds up the frequency of scanning
    public void setFrequencyBand2Hz(boolean enable, WifiManager mWifiManager) {
        int band; //WIFI_FREQUENCY_BAND_AUTO = 0,  WIFI_FREQUENCY_BAND_2GHZ = 2
        try {
            Field field = Class.forName(WifiManager.class.getName())
                    .getDeclaredField("mService");
            field.setAccessible(true);
            Object obj = field.get(mWifiManager);
            Class myClass = Class.forName(obj.getClass().getName());

            Method method = myClass.getDeclaredMethod("setFrequencyBand", int.class, boolean.class);
            method.setAccessible(true);
            if (enable) {
                band = 2;
            } else {
                band = 0;
            }
            method.invoke(obj, band, false);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //Merge
    //Checks to see if receiever should be re-registered to prevent freezing
    void checkToRegisterReceiver() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!endListener) {
                    System.out.println("HERE running check");
                    //This ends all location display
                    if (routingDone) {
                        if (!MainActivity.isOutdoor) {
                            try {
                                activity.unregisterReceiver(cycleWifiReceiver);
                            } catch (Exception e) {
                            }
                            wifiThreadEnabled = false;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MapFragment.removeUserLocationMarker(map);
                                }
                            });
                            routingDone = false;
                            break;
                        } else {
                            map.setMyLocationEnabled(false);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MapFragment.removeUserLocationMarker(map);
                                }
                            });
                            routingDone = false;
                            break;
                        }
                    }
                    if (useGPS) {
                        useGPS = false;
                        UserLocation myLocation = new UserLocation(activity, map);
                        map.setMyLocationEnabled(true);
                        myLocation.toggleGps(true, true);
                        myLocation.updateLocation();
                    }
                    try {
                        Thread.sleep(1000);
                        long lastTime = (broadCastTimeList.get(broadCastTimeList.size() - 1)) - broadCastTimeList.get(broadCastTimeList.size() - 2);
                        //Will re-register if time exceeds twice the past broadcast time
                        System.out.println("last time " + lastTime);
                        if (System.currentTimeMillis() - broadCastTimeList.get(broadCastTimeList.size() - 1) > lastTime * 2) {
                            activity.unregisterReceiver(cycleWifiReceiver);
                            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                            setFrequencyBand2Hz(true, wifiManager);
                            WifiManager.WifiLock wifilock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "MyLock");
                            wifilock.setReferenceCounted(true);
                            wifilock.acquire();
                            if (!wifilock.isHeld()) {
                                wifilock.acquire();
                            }
                            activity.registerReceiver(cycleWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                        }
                    } catch (final Exception e) {
                    }
                    //Switches outdoor to indoor
                    if (!MainActivity.isOutdoor && map.isMyLocationEnabled() && wipeMarker && !wifiThreadEnabled) {
                        System.out.println("HERE switching to wifi");
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                map.setMyLocationEnabled(false);
                            }
                        });
                        makeWifiThread();
                    }
                }
            }
        }).start();
    }


    //Merge
    public final BroadcastReceiver cycleWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            map.setMyLocationEnabled(false);
            scanNum += 1;
            String strToPost = "";
            String buildingName = setBuildingNameBasedOnGPS();
            //Weather to flip between one of two items in the start time array. Needed to time a full cycle of wifi scans
            broadCastTimeList.add(System.currentTimeMillis());
            System.out.println("BROADCAST TIME: " + (broadCastTimeList.get(broadCastTimeList.size() - 1) - broadCastTimeList.get(broadCastTimeList.size() - 2)));
            if (wifiManager.isWifiEnabled()) {
                wifiManager.startScan();
                StringBuffer stringBuffer = new StringBuffer();
                //List contains all the important wifi information -> all the scan results and their respective fields
                List<ScanResult> list = wifiManager.getScanResults();
                strToPost += "{";
                strToPost += "\"building\":\"";
                if (buildingName.equals("not valid") && pastBulidingNames.size() > 0) {
                    buildingName = pastBulidingNames.get(pastBulidingNames.size() - 1);
                }
                strToPost += buildingName;
                strToPost += "\",\"floor\":\"";
                strToPost += Integer.toString(currentLevel);
                strToPost += "\",\"fp\":";
                strToPost += "{";
                //updateScanTimes();
                int itemCount = 0;
                for (ScanResult scanResult : list) {
                    if (list.indexOf(scanResult) > 0) {
                        strToPost = strToPost + "\"" + scanResult.BSSID + "\":";
                        strToPost = strToPost + Integer.toString(scanResult.level) + ",";
                        String prevMAC = list.get(list.indexOf(scanResult) - 1).BSSID;
                        String currentMAC = scanResult.BSSID;
                        prevMAC = prevMAC.substring(0, prevMAC.length() - 1);
                        currentMAC = currentMAC.substring(0, currentMAC.length() - 1);
                        if (!currentMAC.equals(prevMAC)) {
                            itemCount++;
                        }
                    }
                }
                System.out.println("HERE in thread ");
                //Check to see if user has moved outdoors
                if (MainActivity.isOutdoor) {
                    System.out.println("HERE switching outdoor");
                    pastBulidingNames = new ArrayList<>();
                    activity.unregisterReceiver(cycleWifiReceiver);
                    wifiThreadEnabled = false;
                    map.setMyLocationEnabled(true);
                    userLocation = new UserLocation(activity, map);
                    userLocation.toggleGps(true, false);
                    userLocation.updateLocation();
                    MapFragment.removeUserLocationMarker(map);
                    //makeLocationListener();
                }
                if (wifiThreadEnabled) {
                    strToPost = strToPost.substring(0, strToPost.length() - 1);
                    strToPost = strToPost + "}}";
                    postToServer(strToPost, false);
                }
            }
        }
    };

    //Merge
    void postToServer(final String postJSON, final boolean enableCountdownLatch) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    long startTime = System.nanoTime();
                    httpURLConnection = (HttpURLConnection) new URL("http://macquest2.cas.mcmaster.ca/ilos/fp_loc/").openConnection();
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setReadTimeout(3000);
                    httpURLConnection.setConnectTimeout(3000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();
                    OutputStream out = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));
                    System.out.println(postJSON);
                    bw.write(postJSON);
                    bw.flush();
                    out.close();
                    bw.close();
                    InputStream in = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String str = null;
                    StringBuffer buffer = new StringBuffer();
                    while ((str = br.readLine()) != null) {
                        buffer.append(str);
                    }
                    in.close();
                    br.close();
                    double lat = 0;
                    double lon = 0;
                    String start = "{\"status\": 0, \"ret\": [";
                    currentLocation = buffer.toString().substring(start.length(), buffer.toString().length() - 2);
                    System.out.println(currentLocation);
                    lat = Double.parseDouble(currentLocation.split(",")[0]);
                    lon = Double.parseDouble(currentLocation.split(",")[1].substring(1, currentLocation.split(",")[1].length() - 1));
                    wipeMarker = true;
                    System.out.println("HERE + " + MainActivity.isOutdoor);
                    if (!MainActivity.isOutdoor) {
                        makeIcon(true, lat, lon);
                    }
                    long endTime = System.nanoTime();
                    Log.i("TIME TO POST:", Long.toString((endTime - startTime) / 1000000));
                    if (enableCountdownLatch) {
                        wifiLoc = new LatLng(lat, lon);
                        latch.countDown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    wipeMarker = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UserLocation myLocation = new UserLocation(activity, map);

                            MapFragment.removeUserLocationMarker(map);
                            map.setMyLocationEnabled(true);
                            if (zoomOnce) {
                                myLocation.toggleGps(true, true);
                                zoomOnce = false;
                            } else {
                                myLocation.toggleGps(true, false);
                            }
                            myLocation.updateLocation();
                        }
                    });
                }
            }
        }).start();
    }

    //Merge
    void makeIcon(boolean draw, final double lat, final double lon) {
        if (draw) {
            MapFragment.removeUserLocationMarker(map);
            Location currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);
            IconFactory iconFactory = IconFactory.getInstance(activity);
            Icon icon = iconFactory.fromResource(R.drawable.wifi_marker);
            //Location here will be the one from the server processing
            userLocationMarker = new MarkerOptions().position(new LatLng(lat, lon))
                    .icon(icon);
            map.addMarker(userLocationMarker);
            if (zoomOnce) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animateMap(new LatLng(lat, lon), 17.75);
                    }
                });
                zoomOnce = false;
            }
            if (routingDone) {
                MapFragment.removeUserLocationMarker(map);
            }
        } else {
            MapFragment.removeUserLocationMarker(map);
        }
    }

    private void animateMapForRouting() { //moves the camera to show the starting and ending point
        if (latLngForRoutingSrc == null) {
            new AlertDialog.Builder(context).setTitle("Unable to get your Location, Please give permission and wait for a while to get your location.")  //.setMessage("Please select your current floor level:")
                    .setIcon(android.R.drawable.ic_dialog_alert).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

            return;
        }
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(latLngForRoutingSrc)
                .include(new LatLng(destLocation))
                .build();
        map.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 200), 1000);
    }

    public void setRoutes(String[] queryresult) {
        this.routes = queryresult;
        if (routes[0] != null) {
            Log.d("Chen2", "route0： " + routes[0]);
            floorNumberRoute1 = Integer.parseInt(routes[0].substring(0, routes[0].indexOf('{')));
            routeSourc.setGeoJson(routes[0].substring(routes[0].indexOf('{')));
            routeLayer1.setProperties(visibility(VISIBLE));
        } else {
            floorNumberRoute1 = 999;
            routeLayer1.setProperties(visibility(NONE));
        }

        if (routes[2] != null) {
            Log.d("Chen2", "route2： " + routes[2]);
            floorNumberRoute3 = Integer.parseInt(routes[2].substring(0, routes[2].indexOf('{')));
            routeDest.setGeoJson(routes[2].substring(routes[2].indexOf('{')));
            //Log.d("Chen2", "route3： " + routes[2]);
            routeLayer3.setProperties(visibility(VISIBLE));
        } else {
            floorNumberRoute3 = 999;
            routeLayer3.setProperties(visibility(NONE));
        }

        if (routes[1] != null) {
            Log.d("Chen2", "route1： " + routes[1]);
            floorNumberRoute2 = Integer.parseInt(routes[1].substring(0, routes[1].indexOf('{')));
            routeGround.setGeoJson(routes[1].substring(routes[1].indexOf('{')));
            //  Log.d("Chen2", "route2： " + routes[1]);
            routeLayer2.setProperties(visibility(VISIBLE));

        } else {
            floorNumberRoute2 = 999;
            routeLayer2.setProperties(visibility(NONE));
        }
    }


    private void setupRouteLayers() {
        routeSourc = new GeoJsonSource("routeSourc", context.getResources().getString(R.string.empty_geojson)); //avoid hardcoding any strings
        routeGround = new GeoJsonSource("routeGround", context.getResources().getString(R.string.empty_geojson));
        routeDest = new GeoJsonSource("routeDest", context.getResources().getString(R.string.empty_geojson));

        map.addSource(routeSourc);
        map.addSource(routeGround);
        map.addSource(routeDest);

        loadRoute();
    }


    private void loadRoute() {
        routeLayer1 = new LineLayer("route-line-layer-src", "routeSourc").withProperties( // ( string layer id, string source id)
                lineColor(Color.parseColor("#ffa500")), //do not hard-code any colours, add them to the colors xml and reference them from there
                lineWidth(2f));
        routeLayer1.setProperties(visibility(VISIBLE));
        map.addLayer(routeLayer1);

        routeLayer3 = new LineLayer("route-line-layer-dest", "routeDest").withProperties( // ( string layer id, string source id)
                lineColor(Color.parseColor("#df5a3e")),
                lineWidth(2f));
        routeLayer3.setProperties(visibility(VISIBLE));

        map.addLayer(routeLayer3);

        routeLayer2 = new LineLayer("route-line-layer-ground", "routeGround").withProperties( // ( string layer id, string source id)
                lineColor(Color.parseColor("#3bb2d0")),
                lineWidth(2f));
        routeLayer2.setProperties(visibility(VISIBLE));
        map.addLayer(routeLayer2);

    }

    //Function to show event requirement dialog.
    public void showEventActionRequireDialog(String QRURL) {
        //TODO finish the function of event requirement dialog.
        if (QRURL != null && QRURL.length() > 0) {
            String subventID = Event.phraseSubeventIDFromURL(QRURL);
            //Validate the event url.
            if (subventID == null) {
                return;
            }

            //Validate whether the event is in the current event list or not.
            ArrayList<String> subeventPoint = Event.findEventPointByID(arPoints, subventID);
            if (subeventPoint == null) {
                return;
            }

            //Change event list color
            changeEventListItemTextColor(arPoints.indexOf(subeventPoint));

            //Show action.
            if(subeventPoint.get(EventUtils.EVENT_OBJECT_INDEX_HAS_ACTION).equals(EventUtils.EVENT_ACTION_TRUE)){
                Event.pointsActionDialog(context, subeventPoint);
            }



        }
    }

    public String setBuildingNameBasedOnGPS() {
        if (userLocation == null) {
            userLocation = new UserLocation(context, map);
        }
        userLocation.toggleGps(true, false);
        Location myLocation = userLocation.currentLocation;
        LatLng temp = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        PointF pointf = map.getProjection().toScreenLocation(temp);
        RectF rectF = new RectF(pointf.x - 50, pointf.y - 50, pointf.x + 50, pointf.y + 50);

        List<Feature> testFeatures = map.queryRenderedFeatures(rectF, map.getLayer(MapConstants.MAPBOX_CAMPUS_OUTLINE_LAYER).getId());
        for (Feature f : testFeatures) {
            buildingNameShort = f.getProperties().get("shortname").getAsString();
        }
        userLocation.toggleGps(false, false);
        int[] floors = getNumFloorsAtCurrentLocation();
        if (floors[0] == 0 && floors[1] == 0) {
            return "not valid";
        } else {
            pastBulidingNames.add(buildingNameShort);
            return buildingNameShort;
        }
    }


}

