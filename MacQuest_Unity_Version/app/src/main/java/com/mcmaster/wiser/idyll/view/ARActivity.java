package com.mcmaster.wiser.idyll.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.collection.CompassReading;
import com.mcmaster.wiser.idyll.collection.QuickSort;
import com.mcmaster.wiser.idyll.model.event.EventUtils;
import com.mcmaster.wiser.idyll.detection.iodetection.IODetectionHandler;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.mcmaster.wiser.idyll.model.EKFLocationService.Commons.Utils;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Interface.LocationNotifier;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Service.EKFLocationService;

import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

/**
 * Created by Daniel
 * AR Activity
 * Contains all the code from the AR Activity. This activity takes values from the main activity, runs a unity app player, and displays a mini map for navigation.
 * <p>
 * How Navigation Works:
 * 1) The TrackerControl script checks if the phone has positional tracking (used for better tracking ie. ARCore,ARKit,Vuforia Fusion)
 * 2) The appropriate script is activated, either with or without ground detection.
 * 3) Android calls showRoute(true). This enables the route.
 * 4) Android calls getNavInfo() with the GeoJSON file info. This makes unity draw every point in the entire route showing only the ones within a distance threshold to the user.
 * 5) Android calls setUserLocationNav() with the users location.
 * 6) When gps movement is recognized, Android calls setUserLocationNav with the users new location info. This recalculates the visibility of each point in the route.
 * <p>
 * How Labels Work: (Needs to be cleaned up. Ideally switch this to being similar to how navigation works where unity does most of the work. Draws all the points and recalculates their visibility)
 * 1) Android gets the labels from the main activity.
 * 2) Calculate the visible labels.
 * 3) Send the labels individually to unity.
 * 4) When gps movement is recognized, either turn visibility of the individual point on or off.
 */

public class ARActivity extends UnityPlayerActivity implements OnMapReadyCallback, LocationEngineListener, SensorEventListener, LocationNotifier {

    //MiniMap
    private MapView mapView;
    private MapboxMap map;
    private LineLayer routeLayer;
    private Layer markerLayer;
    private GeoJsonSource routeSource;

    //Unity Layout
    private RelativeLayout scan;
    private View view;
    private static UnityPlayer unityPlayer;
    private View miniMap;

    //Android Location Services
    private LocationManager locationManager;
    private LocationListener locationListenerGPS;
    private double Lat;
    private double Lng;

    //Compass Readings
    QuickSort sortObj;
    CompassReading compassreading;
    float fFinalReading = 0;
    float azimuth;
    float[] mGravity;
    float[] mGeomagnetic;
    private float[] mRotationMatrix = new float[16];

    //EKF and Sensor variables
    EKFLocationService ekfLocationService;
    private SensorManager mSensorManager;
    private double m_magneticDeclination = 0.0;

    //Minimap values
    private LatLng originalLocation;
    private String routeCoords;
    private ArrayList<LatLng> mapRoutePoints = new ArrayList<>();
    private LatLng destLocation;

    //For inside/outside detection.
    private IODetectionHandler ioDetectionHandler;

    //Button for closing the activity.
    Button closeButton;

    //Button for scanning AR.
    Button scanButton;
    CardView scanCardView;

    //Marker for user location on the mini map.
    MarkerOptions userMarkerOptions;
    Marker userMarker;

    private boolean isOutdoor = true;
    private AlertDialog.Builder alertDialog;


    //Threshold for visible AR points.

    public static ArrayList<ArrayList<String>> arPoints = new ArrayList<>();
    public static ArrayList<ArrayList<String>> visibleARPoints = new ArrayList<>();
    private String route;


    //Close AR button H


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        // MulityPorcess test
        Mapbox.getInstance(this, getString(R.string.access_token_wiser));

        if (getIntent().getStringExtra("QR") != null) {
            activateVuforia("QR");

            String url = getIntent().getStringExtra("QRURL");

            if (url != null) {
                readFromURL(url);
            }
        }


        //MiniMap + Mapbox Setup

        mapView = new MapView(this);
        mapView.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT
        ));
        ((CardView) findViewById(R.id.card_view)).addView(mapView);


        mapView.onCreate(savedInstanceState);
        final View viewById = mapView.findViewById(R.id.surfaceView);
        ((SurfaceView) viewById).setZOrderMediaOverlay(true);

        //Bring the map to front of the view.
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            findViewById(R.id.card_view).bringToFront();
//            mapView.bringChildToFront(mapView.findViewById(R.id.surfaceView));
            findViewById(R.id.card_view).invalidate();

            Log.i("BringFront", "Yes bring, front");
        }


        //Dialog box for if the user is indoors and trying to use AR
        alertDialog = new AlertDialog.Builder(this).setTitle("AR is not available indoors")
                .setMessage("Please go outdoors and set your AR again.")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        closeButton.callOnClick();
                        dialogInterface.dismiss();
                    }
                });


        // Indoor/Outdoor check
        ioDetectionHandler = new IODetectionHandler(this);
        ioDetectionHandler.setOnIOChangeListener(new IODetectionHandler.OnIOChangeListener() {
            @Override
            public void onIOChange(boolean isOut) {
                isOutdoor = isOut;

                // TODO: Test this
                //if (!isOutdoor){
                //    alertDialog.show();
                //}
            }
        });


        //Setting up AR points.
        arPoints = new ArrayList<>();
        visibleARPoints = new ArrayList<>();

        //Checking the number of points needed
        int numPoints = getIntent().getIntExtra("NumPoints", 0);
        for (int i = 0; i < numPoints; i++) {
            ArrayList<String> point = getIntent().getStringArrayListExtra("Point" + String.valueOf(i));
            arPoints.add(point);
        }

        //Setting up user initial location
        String originalLat = getIntent().getStringExtra("OriginalLat");
        String originalLng = getIntent().getStringExtra("OriginalLng");

        //Setting up user heading / pitch
        String heading = getIntent().getStringExtra("Heading");
        String pitch = getIntent().getStringExtra("Pitch");
        sendHeading(String.format("{heading:%s, pitch:%s}", heading, pitch));

        //Setting up the routing information
        if (getIntent().getStringExtra("Route") != null) {
            showRoute("true");
            route = (getIntent().getStringExtra("Route"));

            Double destlat = Double.parseDouble(getIntent().getStringExtra("DestLat"));
            Double destlng = Double.parseDouble(getIntent().getStringExtra("DestLng"));
            destLocation = new LatLng(destlat, destlng);

            route = route.substring(route.indexOf('{'));

            try {
                JSONObject jsonObject = new JSONObject(route);
                JSONArray jsonArray = new JSONArray(jsonObject.getString("features"));
                jsonObject = (jsonArray.getJSONObject(0));
                jsonObject = new JSONObject(jsonObject.getString("geometry"));
                routeCoords = (jsonObject.getString("coordinates"));
                sendRoute("{ \'coordinates\':" + routeCoords + "}");

                String[] coord = routeCoords.replace("[", "").replace("]", "").split(",");
                for (int i = 0; i < coord.length; i = i + 2) {
                    mapRoutePoints.add(new LatLng(Double.parseDouble(coord[i]), Double.parseDouble(coord[i + 1])));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            setLocationNav("{ \'longitude\':" + originalLng +
                    ", \'latitude\':" + originalLat + "}");


            mapView.setVisibility(View.VISIBLE);
        }


        try {
            originalLocation = new LatLng(Double.parseDouble(originalLat), Double.parseDouble(originalLng));
        } catch (Exception e) {

        }

        //Unity setup
        unityPlayer = mUnityPlayer;
        scan = (RelativeLayout) findViewById(R.id.scan);
        view = unityPlayer.getView();
        scan.addView(view);


        // MulityPorcess test
        //Called to start setting up the mini map.
        mapView.getMapAsync(this);


        //Setup the buttons, make sure they are visible over the AR camera.
        closeButton = (Button) findViewById(R.id.close_ar);
        scanButton = (Button) findViewById(R.id.scan_qr);
        scanCardView = (CardView) findViewById(R.id.scan_qr_card_view);

        closeButton.bringToFront();
        scanButton.bringToFront();


        //Hide the close button for a few seconds to allow vuforia to initialize, otherwise if the user closes it, it may mess up vuforia until the app is restarted.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeButton.setVisibility(View.VISIBLE);
            }
        }, 5000);


        //Close button setup
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Close activity here.
                locationManager.removeUpdates(locationListenerGPS);
                stopTrackers("");
                if (ekfLocationService != null) {
                    ekfLocationService.stop();
                }


                finish();


            }
        });


        //Currently hides the QR scan button if the OS version is high enough to use ARCore.
        //This is because ARCore doesn't have camera focus options yet and so QR codes won't be clear enough to recognize.
        //if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        scanCardView.setVisibility(View.GONE);
        scanButton.setVisibility(View.GONE);
        //}


        //Initializes all sensors.
        init();

        //Needed to set location for labels.
        setOrigin("{ \'longitude\':" + originalLng +
                ", \'latitude\':" + originalLat + "}");


        //Look for all visible points
        if (numPoints > 0) {
            if (originalLocation != null) {
                fillVisiblePoints(originalLocation.getLatitude(), originalLocation.getLongitude());
            }

        }


        Log.d("Total AR Points: ", String.valueOf(arPoints.size()));
        Log.d("Visible AR Points: ", String.valueOf(visibleARPoints.size()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MulityPorcess test
        mapView.onDestroy();

        scan.removeAllViews();
        //unityPlayer.quit();
        android.os.Process.killProcess(android.os.Process.myPid());


    }

    @Override
    protected void onPause() {
        super.onPause();
        // MulityPorcess test
        mapView.onPause();

        mSensorManager.unregisterListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // MulityPorcess test
        mapView.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);

        registerSensor();

        if (ioDetectionHandler != null) {
            ioDetectionHandler.onResume();
        }

        if (ekfLocationService != null) {
            ekfLocationService.resume();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // MulityPorcess test

        if (mapView != null) {
            mapView.onStart();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // MulityPorcess test
        mapView.onStop();
        if (ekfLocationService != null) {
            ekfLocationService.stop();
        }

        if (ioDetectionHandler != null) {
            ioDetectionHandler.onStop();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // MulityPorcess test
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // MulityPorcess test
        mapView.onLowMemory();
    }

    //Initialize gps + sensors
    private void init() {


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        ekfLocationService = new EKFLocationService(this);
        setupGPS();
        setupSensors();
    }

    //Setup the location listener. Pass values into ekf.
    private void setupGPS() {

        locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                ekfLocationService.inputLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }


    Sensor gsensor_acc, gsensor_gyro, gsensor_rotation;

    //Setup the sensors to be used.
    private void setupSensors() {


        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        //accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gsensor_acc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        gsensor_gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        gsensor_rotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        registerSensor();
    }

    //Function to register sensor listener
    private void registerSensor() {
        int FREQ = 50;

        //Register the sensor listener.
        mSensorManager.registerListener((SensorEventListener) this, gsensor_acc, Utils.hertz2periodUs(FREQ));

        //Register the sensor listener.
        mSensorManager.registerListener((SensorEventListener) this, gsensor_gyro, Utils.hertz2periodUs(FREQ));

        //Register the sensor listener.
        mSensorManager.registerListener((SensorEventListener) this, gsensor_rotation, Utils.hertz2periodUs(FREQ));
    }


    //Check if AR point is within the visible threshold and in the floor 1. This might be easier to do in unity.
    public boolean inVisibleThreshold(double latP, double lngP, double latU, double lngU, int floor) {

        //If the event point isn't in the first floor, because it might in the building and have the same lat and long with the outdoor event points.
        if (floor != 1) {
            return false;
        }
        double R = 6378.137 * 1000; // Radius of earth in KM
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

    //Fill the list of points that are meant to be visible.
    private void fillVisiblePoints(double userLat, double userLong) {
        //When the user is in outdoor then we find the visible ar points.
        if (arPoints != null && isOutdoor) {

            //for (ArrayList<String> point : arPoints){
            for (int i = 0; i < arPoints.size(); i++) {
                ArrayList<String> point = arPoints.get(i);
                Double latP = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LAT));
                Double lngP = Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LON));
                Integer floorP = Integer.parseInt(point.get(EventUtils.EVENT_OBJECT_INDEX_FLOOR));
                if (inVisibleThreshold(latP, lngP, userLat, userLong, floorP)) {
                    // If the point is within the visible threshold but already in the
                    // visibleARPoints array, there is no need to send a message,
                    // just ensure that the AR activation button is visible.
                    // Send a unity message to add the point.
                    if (!visibleARPoints.contains(point)) {
                        //TODO: Send unity message to add.
                        boolean addPoint = true;
                        visibleARPoints.add(point);
                        Log.d("Added AR: ", point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_ID));
                        if (addPoint) {
                            JSONObject label = new JSONObject();
                            try {
                                //0
                                label.put("id", Integer.parseInt(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_ID)));
                                //1
                                label.put("url", point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_URL));
                                //2
                                label.put("pid", Integer.parseInt(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_PID)));
                                //3
                                label.put("event_name", point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_TITLE));
                                //4
                                label.put("floor", Integer.parseInt(point.get(EventUtils.EVENT_OBJECT_INDEX_FLOOR)));
                                //5
                                label.put("longitude", Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LON)));
                                //6
                                label.put("latitude", Double.parseDouble(point.get(EventUtils.EVENT_OBJECT_INDEX_SUB_LAT)));
                                //7
                                label.put("building", point.get(EventUtils.EVENT_OBJECT_INDEX_BUILDING) + " "
                                        + point.get(EventUtils.EVENT_OBJECT_INDEX_ROOM));
                                //8
                                label.put("description", point.get(EventUtils.EVENT_OBJECT_INDEX_DESCRIPTION));
                                label.put("isVisible", true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            ARActivity.addLabel(label.toString());
                        }

                    } else {
                        showLabel(point.get(0));
                        Log.d("Label Shown: ", point.get(0));
                    }
                }
                // Send a unity message to remove the point.
                else {
                    if (visibleARPoints.contains(point)) {

                        //TODO: Send unity message to remove.
                        ARActivity.hideLabel(point.get(0));

                        visibleARPoints.remove(point);
                        Log.d("Remove AR: ", point.get(2));
                    }

                }


            }

            showLabels("info");
        }
    }


    //Unity Commands
    public static void addLabel(String m) {
        unityPlayer.UnitySendMessage("Plane", "setLabelObjectList", m);
    }

    public static void showLabels(String m) {
        unityPlayer.UnitySendMessage("Plane", "createLabels", m);
    }

    public static void hideLabel(String m) {
        unityPlayer.UnitySendMessage("Plane", "setVisibilityToFalse", m);
    }

    public static void showLabel(String m) {
        unityPlayer.UnitySendMessage("Plane", "setVisibilityToTrue", m);
    }

    public static void setLocation(String m) {
        unityPlayer.UnitySendMessage("Plane", "setUserLocation", m);
    }

    public static void setOrigin(String m) {
        unityPlayer.UnitySendMessage("Plane", "setUserOrigin", m);
    }

    public static void setLocationNav(String m) {
        unityPlayer.UnitySendMessage("Plane", "setUserLocationNav", m);
    }

    public static void setOriginNav(String m) {
        unityPlayer.UnitySendMessage("Plane", "setUserNavOrigin", m);
    }

    public static void sendHeading(String m) {
        unityPlayer.UnitySendMessage("ARCamera", "setHeading", m);
    }

    public static void updateHeading(String m) {
        unityPlayer.UnitySendMessage("ARCamera", "setHeading", m);
    }

    public static void sendRoute(String m) {
        unityPlayer.UnitySendMessage("Plane", "getNavInfo", m);
    }

    public static void showRoute(String m) {
        unityPlayer.UnitySendMessage("Plane", "showRoute", m);
    }

    public static void scanQR(String m) {
        unityPlayer.UnitySendMessage("ARCamera", "scanQR", m);
    }

    public static void stopTrackers(String m) {
        unityPlayer.UnitySendMessage("Plane", "stopTrackers", m);
    }

    public static void activateVuforia(String m) {
        unityPlayer.UnitySendMessage("Plane", "InitializeTracker", m);
    }

    public static void readFromURL(String m) {
        unityPlayer.UnitySendMessage("ARCamera", "readFromExternalUrl", m);
    }


    @Override
    public void onConnected() {
        Log.d("Daniel: ", "Location Listener");
        //locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            Log.d("Daniel: ", "Location Change");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        ekfLocationService.inputSensorData(sensorEvent);

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {


            SensorManager.getRotationMatrixFromVector(mRotationMatrix, sensorEvent.values);
            float orientation[] = new float[3];
            SensorManager.getOrientation(mRotationMatrix, orientation);
            float azimut = orientation[0]; // orientation contains: azimut, pitch and roll
            azimut = (float) Math.toDegrees(azimut);
            azimut = (azimut + 360) % 360;

            Log.d("Heading changed:", String.valueOf(azimut));

            azimut += m_magneticDeclination;

            float pitch = (float) Math.toDegrees(orientation[1]);

            if (routeSource != null) {
                if (pitch >= -60 && pitch <= 60) {
                    findViewById(R.id.pitch_text_view).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.pitch_text_view).setVisibility(View.VISIBLE);
                }
            }

            Log.d("Declination:", String.valueOf(m_magneticDeclination));

            sendHeading(String.format("{heading:%f, pitch:%f}", azimut, pitch));
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //MiniMap setup
    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;


        map.setMyLocationEnabled(true);

        Location userLocation = map.getMyLocation();

        if (userLocation == null) {
            return;
        }

        IconFactory iconFactory = IconFactory.getInstance(this);
        Icon icon = iconFactory.fromResource(R.drawable.minimap_location_marker);

        userMarkerOptions = new MarkerOptions().position(new LatLng(userLocation))
                .icon(icon);

        userMarker = map.addMarker(userMarkerOptions);

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation), 17));

        map.setMyLocationEnabled(false);

        if (route != null) {
            routeSource = new GeoJsonSource("routeSource", route);

            map.addSource(routeSource);

            routeLayer = new LineLayer("route-line-layer-src", "routeSource").withProperties( // ( string layer id, string source id)
                    lineColor(getResources().getColor(R.color.colorPrimaryDark)), //do not hard-code any colours, add them to the colors xml and reference them from there
                    lineWidth(2f));
            routeLayer.setProperties(visibility(VISIBLE));
            map.addLayer(routeLayer);

            map.addMarker(new MarkerViewOptions().position(destLocation));


        }

        if (arPoints != null) {
            for (ArrayList<String> point : arPoints) {
                Double latP = Double.parseDouble(point.get(6));
                Double lngP = Double.parseDouble(point.get(5));

                map.addMarker(new MarkerOptions().position(new LatLng(latP, lngP)));
            }
        }
    }

    @Override
    public void getLocationChanged(Location loc) {
        Lat = loc.getLatitude();
        Lng = loc.getLongitude();

        Log.d("Location Changed (EKF):", "locationListenerNetwork: " + Lat + " " + Lng);

        setLocation("{ \'longitude\':" + Lng +
                ", \'latitude\':" + Lat + "}");

        if (userMarkerOptions != null) {
            setLocationNav("{ \'longitude\':" + Lng +
                    ", \'latitude\':" + Lat + "}");

            //map.clear();

            map.removeMarker(userMarker);

            userMarkerOptions.position(new LatLng(loc));

            userMarker = map.addMarker(userMarkerOptions);

            map.easeCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc), 17));

            Log.d("Minimap moved: ", loc.toString());
        }

        long timeStamp = Utils.nano2milli(loc.getElapsedRealtimeNanos());

        GeomagneticField f = new GeomagneticField(
                (float) loc.getLatitude(),
                (float) loc.getLongitude(),
                (float) loc.getAltitude(),
                timeStamp);
        m_magneticDeclination = f.getDeclination();

        originalLocation = new LatLng(Lat, Lng);

        fillVisiblePoints(Lat, Lng);
    }
}
