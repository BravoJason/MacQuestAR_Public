package com.mcmaster.wiser.idyll.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.MapConstants;
import com.mcmaster.wiser.idyll.model.UserLocation;
import com.mcmaster.wiser.idyll.presenter.ParentEventIDReceiver;
import com.mcmaster.wiser.idyll.presenter.util.MapUtils;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MapFragment extends BaseFragment {

    private MapboxMap map;
    private static final String TAG = "JASON DEBUG MAP FRAG" ;// MapFragment.class.getSimpleName();
    private FragmentActivity myContext;
    private MapUtils mapUtility;
    private boolean mAlreadyLoaded = false;

    private ParentEventIDReceiver parentEventIDChangedreceiver;

    public void setParentEventIDChangedreceiver(ParentEventIDReceiver receiver) {
        this.parentEventIDChangedreceiver = receiver;
    }

    //Mitchell merge in
    int srcFloor = 0;
    String floorNum = "1";

    MarkerOptions userLocationMarker;

    public volatile String currentLocation = "";
    boolean doneWifiLock = false;
    volatile WifiManager wifiManager;
    boolean locationOn = false;





    private LocationEngine locationEngine;
    private LocationEngineListener locationEngineListener;
    private PermissionsManager permissionsManager;
    private PermissionsListener permissionsListener;


    // Create supportMapFragment
    SupportMapFragment mapboxMapFragment;

    List<Button> floorButtonList;
    List<Button> basementButtonList;

    @BindView(R.id.floor_level_buttons)
    View levelButtons;

    @BindView(R.id.level_button_sub_zero)
    Button levelButtonSubZero;

    @BindView(R.id.level_button_zero)
    Button levelButtonZero;

    @BindView(R.id.level_button_one)
    Button levelButtonOne;

    @BindView(R.id.level_button_two)
    Button levelButtonTwo;

    @BindView(R.id.level_button_three)
    Button levelButtonThree;

    @BindView(R.id.level_button_four)
    Button levelButtonFour;

    @BindView(R.id.level_button_five)
    Button levelButtonFive;

    @BindView(R.id.level_button_six)
    Button levelButtonSix;

    @BindView(R.id.level_button_seven)
    Button levelButtonSeven;

    @BindView(R.id.map_floating_search_view)
    FloatingSearchView mSearchView;

    @BindView(R.id.routing_floating_search_view)
    FloatingSearchView routingSearchView;

    @BindView(R.id.info_cardview)
    CardView infoCardView;

    @BindView(R.id.directions_cardview)
    CardView directionsCardView;

    @BindView(R.id.route_info)
    CardView routeInfoCardView;

    @BindView(R.id.info_text_room_name)
    TextView infoTextview;

    @BindView(R.id.event_list)
    ListView eventListView;

    @BindView(R.id.event_list_card)
    CardView eventListCardView;

    @BindView(R.id.info_text_building_name)
    TextView infoBuildingName;

    // @BindView(R.id.info_directions)
    // ImageButton infoDirections;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.directions_go_button)
    Button directionsGoButton;

    @BindView(R.id.checkbox_routing_ar)
    CheckBox checkBoxRoutingAR;

    @BindView(R.id.checkbox_routing_criteria)
    CheckBox checkBoxRoutingCriteria;

    @BindView(R.id.book_this_room)
    TextView bookThisRoomText;

    @OnClick(R.id.book_this_room)
    public void bookRoomClicked() {
        if(mapUtility  == null){
            Log.d(TAG, "bookRoomClicked, mapUtility is null");

            return;
        }
        mapUtility.bookRoomButtonClicked();
    }



    @OnClick({R.id.level_button_sub_zero,
            R.id.level_button_zero,
            R.id.level_button_one,
            R.id.level_button_two,
            R.id.level_button_three,
            R.id.level_button_four,
            R.id.level_button_five,
            R.id.level_button_six,
            R.id.level_button_seven,
            R.id.info_directions,
            R.id.directions_go_button,
            R.id.map_clear_route_button})
    public void onLayerChange(Button button) {
        if(mapUtility == null)
        {
            Log.d(TAG, "onLayerChange, mapUtility is null");
            return;
        }

        switch (button.getId()) {
            case R.id.level_button_sub_zero:
                mapUtility.setVisibleMapboxLayer(-1);
                floorNum="B2";
                break;
            case R.id.level_button_zero:
                mapUtility.setVisibleMapboxLayer(0);
                floorNum="B1";

                break;
            case R.id.level_button_one:
                mapUtility.setVisibleMapboxLayer(1);
                floorNum="1";

                break;
            case R.id.level_button_two:
                mapUtility.setVisibleMapboxLayer(2);
                floorNum="2";
                break;
            case R.id.level_button_three:
                mapUtility.setVisibleMapboxLayer(3);
                floorNum="3";
                break;
            case R.id.level_button_four:
                mapUtility.setVisibleMapboxLayer(4);
                floorNum="4";
                break;
            case R.id.level_button_five:
                mapUtility.setVisibleMapboxLayer(5);
                floorNum="5";
                break;
            case R.id.level_button_six:
                mapUtility.setVisibleMapboxLayer(6);
                floorNum="6";
                break;
            case R.id.level_button_seven:
                mapUtility.setVisibleMapboxLayer(7);
                floorNum="7";
                break;
            //merge
//            case R.id.info_directions:
//                toggleARButton.setVisibility(View.GONE);
//                //toggleCurrentEvents.setVisibility(View.GONE);
//                mapUtility.showRoutingUI();
//                break;
//            case R.id.directions_go_button:
//                mapUtility.hideCardView(directionsCardView);
//                mapUtility.hideCardView(infoCardView);
//                progressBar.setVisibility(View.VISIBLE);
//                // TODO Remind user to select floor level if he is indoor.
//                // This dialog should only appear if the user choose to route from their current location.  Right now it's always asking the user, even if the user is routing from
//                // two distinct rooms
//                //MainActivity.isOutdoor=false;
//                if (checkBoxRoutingAR.isChecked()){
//
//                    //ASK CAMERA PERMISSIONS
//                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
//                    {
//                        int MY_PERMISSIONS_REQUEST = 1;
//                        ActivityCompat.requestPermissions(getActivity(), new String[]{com.mcmaster.wiser.idyll.Manifest.permission.SEND_LOCATION}, MY_PERMISSIONS_REQUEST);
//                    }
//
//                    mapUtility.calculatePathForAR();
//
//
//                }
//                else if (!MainActivity.isOutdoor && MapUtils.srcRid == 0) {
//                    int[] currentLocationNumFloors = mapUtility.getNumFloorsAtCurrentLocation();
//                    showFloorSelectDialog();
//                }
//
//                else {
//                    mapUtility.calculatePath();
//                }
//
//
//                //Not sure where to put this yet
//                //mapUtility.showCardView(routeInfoCardView);
//
//                break;
//            case R.id.map_clear_route_button:
//                mapUtility.clearRoutes();
////                directionsCardView.setVisibility(View.GONE);
////                infoCardView.setVisibility(View.GONE);
//                break;
            //Merge
            case R.id.info_directions:
                toggleARButton.setVisibility(View.GONE);
                //toggleCurrentEvents.setVisibility(View.GONE);
                floorNum = "1";
                mapUtility.showRoutingUI();
                break;
            case R.id.directions_go_button:
                mapUtility.hideCardView(directionsCardView);
                mapUtility.hideCardView(infoCardView);
                progressBar.setVisibility(View.VISIBLE);
                // TODO Remind user to select floor level if he is indoor.
                // This dialog should only appear if the user choose to route from their current location.  Right now it's always asking the user, even if the user is routing from
                // two distinct rooms
                //MainActivity.isOutdoor=false;
                mapUtility.endListener = false;
                if (checkBoxRoutingAR.isChecked()){

                    //ASK CAMERA PERMISSIONS
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        int MY_PERMISSIONS_REQUEST = 1;
                        ActivityCompat.requestPermissions(getActivity(), new String[]{com.mcmaster.wiser.idyll.Manifest.permission.SEND_LOCATION}, MY_PERMISSIONS_REQUEST);
                    }

                    mapUtility.calculatePathForAR();


                }else if (!MainActivity.isOutdoor && MapUtils.srcRid == 0) {
                    int[] currentLocationNumFloors = mapUtility.getNumFloorsAtCurrentLocation();
                    showFloorSelectDialog();
                } else {
                    mapUtility.calculatePath();
                }
                break;
            case R.id.map_clear_route_button:
                System.out.println("pressed clear route");
                try{
                    mapUtility.endListener = true;
                    mapUtility.currentlyRouting = false;
                    mapUtility.activity.unregisterReceiver(mapUtility.cycleWifiReceiver);
                }catch(Exception e){}
                mapUtility.clearRoutes();
                directionsCardView.setVisibility(View.GONE);
                infoCardView.setVisibility(View.GONE);
                break;
        }
    }

    //Merge
//    private void showFloorSelectDialog() {
//        if (mapUtility == null) {
//            Log.d(TAG, "showFloorSelectDialog, mapUtility is null");
//            return;
//        }
//        int[] floorsAndBasements = mapUtility.getNumFloorsAtCurrentLocation();
//        if (floorsAndBasements.length < 2) {
//            return;
//        }
//
//        int floorNumber = floorsAndBasements[0];
//        final int basementNumber = floorsAndBasements[1];
//        String[] floors = new String[floorNumber + basementNumber];
//        for (int i = 0; i < basementNumber; i++) {
//            floors[i] = "Basement " + (basementNumber - i);
//        }
//        for (int i = 0; i < floorNumber; i++) {
//            floors[i + basementNumber] = "Floor " + (i + 1);
//        }
//        new AlertDialog.Builder(getContext()).setTitle("MacQuest has detected that you are indoors")  //.setMessage("Please select your current floor level:")
//                .setIcon(android.R.drawable.ic_dialog_alert).setSingleChoiceItems(
//                floors, 0, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        int srcFloor = 0;
//                        if (basementNumber == 0) {
//                            // If there is no basement
//                            srcFloor = which + 1;
//                        } else {
//                            // If there are basements
//                            if (which < basementNumber) {
//                                srcFloor = 0 - basementNumber + which;
//                            } else {
//                                srcFloor = which + 1 - basementNumber;
//                            }
//                        }
//                        mapUtility.srcFloor = srcFloor;
//                        mapUtility.calculatePath();
//
//                        //Switches to the floor the user chose.
//                        mapUtility.setVisibleMapboxLayer(srcFloor);
//                        //mapUtility.setVisibleRoutingLayer(srcFloor);
//
//                        Log.d(TAG, "User selected floor: " + srcFloor);
//                    }
//                }).setNegativeButton("Not indoors", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).show();
//    }
    private void showFloorSelectDialog() {
        if (mapUtility == null) {
            return;
        }
        int[] floorsAndBasements = mapUtility.getNumFloorsAtCurrentLocation();
        if (floorsAndBasements.length < 2) {
            return;
        }
        int floorNumber = floorsAndBasements[0];
        System.out.println("FLOOR NUM " + floorNumber);
        final int basementNumber = floorsAndBasements[1];
        System.out.println("BASEMENT NUM " + basementNumber);
        String[] floors = new String[floorNumber + basementNumber];
        for (int i = 0; i < basementNumber; i++) {
            floors[i] = "Basement " + (basementNumber - i);
            System.out.println("ADDED " + floors[i]);
        }
        for (int i = 0; i < floorNumber; i++) {
            floors[i + basementNumber] = "Floor " + (i + 1);
            System.out.println("ADDED " + floors[i+basementNumber]);
        }
        if(floorNumber == 0 || basementNumber == 0){
            mapUtility.calculatePath();
        }
        else {
            new AlertDialog.Builder(getContext()).setTitle("MacQuest has detected that you are indoors")  //.setMessage("Please select your current floor level:")
                    .setIcon(android.R.drawable.ic_dialog_alert).setSingleChoiceItems(
                    floors, 0, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            srcFloor = 0;
                            if (basementNumber == 0) {
                                // If there is no basement
                                srcFloor = which + 1;
                            } else {
                                // If there are basements
                                if (which < basementNumber) {
                                    srcFloor = 0 - basementNumber + which;
                                } else {
                                    srcFloor = which + 1 - basementNumber;
                                }
                            }
                            mapUtility.srcFloor = srcFloor;
                            mapUtility.calculatePath();
                            Log.d(TAG, "User selected floor: " + srcFloor);
                            floorNum = Integer.toString(srcFloor);
                        }
                    }).setNegativeButton("Not indoors", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }

    }

    @BindView(R.id.map_clear_route_button)
    Button clearRoutesButton;

    @BindView(R.id.map_toggle_ar)
    Button toggleARButton;

    @BindView(R.id.toggle_current_events)
    CardView toggleCurrentEvents;

    /*
    @BindView(R.id.checkbox_current_events)
    CheckBox checkBoxCurrentEvents;
    */

    @BindView(R.id.clear_events_button)
    Button clearEventsButton;

    @BindView(R.id.routing_text_destination)
    TextView destinationTextView;


    @BindView(R.id.fab_toggle_location)
    FloatingActionButton locationFab;

//Merge
//    @OnClick({R.id.fab_toggle_location})
//    public void onNavigationFabClick(FloatingActionButton button) {
//        if(mapUtility == null)
//        {
//            Log.d(TAG, "onNavigationFabClick, mapUtility is null");
//            return;
//        }
//        switch (button.getId()) {
//            case R.id.fab_toggle_location:
//
//                // check here for permission first before doing anything else
//                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    Log.v(TAG, "permission check 1");
//                    int MY_PERMISSIONS_REQUEST = 1;
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
//                    return;
//                }
//
//                Log.v(TAG, "Location enabled: " + mapUtility.isLocationEnabled());
//
//                if (mapUtility.isLocationEnabled()) {
//                    locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp));
//                    mapUtility.getLocation(false, false);
//
//                    //mapUtility.sendLocationToServer(ServerUtils.HEAT_MAP_URL,mapUtility.generateLocationForServer(),"DELETE") ;
//                    /*
//                    if (mapUtility.ekfLocationService != null){
//                        mapUtility.ekfLocationService.stop();
//                    }
//                    */
//                } else {
//                    locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_location_disabled_white_24dp));
//                    /*
//                    if (mapUtility.ekfLocationService != null){
//                        mapUtility.ekfLocationService.resume();
//                    }
//                    */
//                    mapUtility.getLocation(true, true);
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    @OnClick({R.id.fab_toggle_location})
    public void onNavigationFabClick(FloatingActionButton button) {
        switch (button.getId()) {
            case R.id.fab_toggle_location:
                boolean canLocationbeAccessed = false;

                if(!mapUtility.currentlyRouting) { //If map utils is currently routing, no need to get location as it is already being displayed
                    //TODO implement checking that the user is within campus bounds
                    // check here for permission first before doing anything else
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.v(TAG, "permission check 1");
                        int MY_PERMISSIONS_REQUEST = 1;
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
                        return;
                    }
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        canLocationbeAccessed = true;
                    }
                    try {
                        mapUtility.activity.unregisterReceiver(mapUtility.cycleWifiReceiver);
                    } catch (Exception e) {
                    }
                    Log.v(TAG, "Location enabled: " + mapUtility.isLocationEnabled());
                    //For outdoor gps navigation
                    if (canLocationbeAccessed) {




                        if (MainActivity.isOutdoor) {
                            if(map.isMyLocationEnabled()){

                                map.setMyLocationEnabled(false);
                                locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_location_disabled_white_24dp));
                                Toast.makeText(myContext, String.format("canLocationbeAccessed: %s, map.isMyLocationEnabled():%s", canLocationbeAccessed?"True":"False", map.isMyLocationEnabled()?"True":"False"), Toast.LENGTH_SHORT).show();
                            }
                            else{

                                removeUserLocationMarker(map);
                                mapUtility.pastBulidingNames = new ArrayList<>();
                                UserLocation myLocation = new UserLocation(getActivity(), map);
                                map.setMyLocationEnabled(true);
                                myLocation.toggleGps(true, true);
                                myLocation.updateLocation();

                                mapUtility.getLocation(false, false);
                                locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp));
                                Toast.makeText(myContext, String.format("canLocationbeAccessed: %s, map.isMyLocationEnabled():%s", canLocationbeAccessed?"True":"False", map.isMyLocationEnabled()?"True":"False"), Toast.LENGTH_SHORT).show();
                            }



                        } else {
                            //TODO ADD WIFI CHECK
                            //TODO if wifi check is false, run the code located directly above under MainActvity.isOurood
                            //TODO IF(WIFIENABLED) then onescan
                            //ELSE
                            locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp));
                            if(!wifiManager.isWifiEnabled()){
                                removeUserLocationMarker(map);
                                mapUtility.pastBulidingNames = new ArrayList<>();
                                UserLocation myLocation = new UserLocation(getActivity(), map);
                                map.setMyLocationEnabled(true);
                                myLocation.toggleGps(true, true);
                                myLocation.updateLocation();
                            }else{
                                map.setMyLocationEnabled(false);
                                oneScan();
                            }

                        }
                    } else {
                        map.setMyLocationEnabled(false);
                        locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_location_disabled_white_24dp));
                        removeUserLocationMarker(map);
                    }
                }
                else{
                    //If button is pressed during routing. I.e. location is already being tracked, only check switching button logo
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        canLocationbeAccessed = true;
                    }
                    //Zooms in on GPS marker during navigation
                    if(canLocationbeAccessed){
                        locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_my_location_white_24dp));
                        if(MainActivity.isOutdoor){
                            map.setMyLocationEnabled(true);
                            UserLocation myLocation = new UserLocation(getActivity(), map);
                            myLocation.toggleGps(true, true);
                            myLocation.updateLocation();
                        }
                        else{
                            mapUtility.zoomOnce = true;
                        }
                    }
                    else{
                        locationFab.setIconDrawable(getResources().getDrawable(R.drawable.ic_location_disabled_white_24dp));
                    }
                }
                //Debug the GPS map
                break;
            default:
                break;
        }
    }



    public MapFragment() {
    }// Required empty public constructor

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.v(TAG, "MAP FRAGMENT ON CREATE");

        if (savedInstanceState == null) {
            Log.v(TAG, "ON CREATE: SAVED INSTANCE STATE NULL");

            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } else {
            mAlreadyLoaded = savedInstanceState.getBoolean("alreadyLoaded");
        }
        wifiManager = (WifiManager)myContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);


    }





    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(TAG, "MAP FRAGMENT ON SAVE INSTANCE STATE ");

        outState.putBoolean("alreadyLoaded", mAlreadyLoaded);
        mapboxMapFragment.onSaveInstanceState(outState);
        Log.v(TAG, "ON SAVE INSTANCE STATE CALLED");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        Log.v(TAG, "MAP FRAGMENT ON CREATE VIEW");

        setupDrawer();
        populateFloorButtonList();
        populateBasementButtonList();

        toggleARButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapUtility == null){
                    Log.d(TAG, "onCreateView, mapUtility is null");
                    return;
                }
                Intent myIntent = new Intent(getContext(),ARActivity.class);
                mapUtility.startAR(myIntent);
            }
        });

        /*
        checkBoxCurrentEvents.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mapUtility.showCurrent = checkBoxCurrentEvents.isChecked();
                map.clear();
                mapUtility.drawARPoints();
            }
        });*/

        clearEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapUtility == null){
                    Log.d(TAG, "clearEventsButton.setOnClickListener, mapUtility is null");
                    return;
                }
                mapUtility.clearMarkers();
                mapUtility.markers = new HashMap<String,ArrayList<MarkerViewOptions>>();
                MainActivity.arOn = false;
                ((MainActivity) getActivity()).resetSidebarHighlight();
                parentEventIDChangedreceiver.parentIDChanged(-1, false);

            }
        });

        checkBoxRoutingAR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (checkBoxRoutingAR.isChecked()){
                    checkBoxRoutingCriteria.setChecked(true);
                }
            }
        });



        infoCardView.setVisibility(View.INVISIBLE);
        progressBar.bringToFront();
        progressBar.setVisibility(View.VISIBLE);

        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                levelButtons.setVisibility(View.INVISIBLE);

                try {
                    String rawLocation = ((MainActivity) getActivity()).getRawLocation(); //used for the building directory
                    if (rawLocation != null) {
                        try {
                            LatLng selectedBuildingLocation = MapUtils.getLocationFromString(rawLocation);
                            mapUtility.animateMap(selectedBuildingLocation, MapConstants.ZOOM_LEVEL_BUILDING);
                        } catch (Exception e) {
                            Log.e(TAG, "Error with getting raw location");
                        }
                    }
                    LatLng busStopLocation = ((MainActivity) getActivity()).getBusStopLatLng();
                    if (busStopLocation != null) {
                        map.clear();
                        map.addMarker(new MarkerViewOptions().position(busStopLocation));
                        mapUtility.animateMap(busStopLocation, MapConstants.ZOOM_LEVEL_BUILDING);

                        ((MainActivity) getActivity()).showFragment(new BusFragment()); //TODO: fix so that the card stays visible at the desired spinner

                    }
                } catch (Exception e) {
                    Log.v(TAG, "EXCEPTION CAUGHT TRYING TO GET BUS STOP LOCATION");
                }
            }
        });

        // API access token used to use the mapbox map
        Mapbox.getInstance(getActivity(), getString(R.string.access_token_wiser));

        // Build mapboxMap
        MapboxMapOptions options = new MapboxMapOptions();

        // Set custom map style
        options.styleUrl(getString(R.string.style_mac_wiser));

        options.camera(new CameraPosition.Builder()
                .target(MapConstants.McMasterStartingPoint)
                .zoom(MapConstants.ZOOM_LEVEL_MINIMUM)
                .build());

        mapboxMapFragment = SupportMapFragment.newInstance(options); // Create map fragment

        if (savedInstanceState == null && !mAlreadyLoaded) {

            Log.v(TAG, "MAP FRAGMENT SAVED INSTANCE IS NULL, AND NOT ALREADY LOADED");

            // Create fragment
            final FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();

            // Add map fragment to parent container
            transaction.replace(R.id.container, mapboxMapFragment);
            transaction.commit();

        } else {
            try {
                mapboxMapFragment = (SupportMapFragment) myContext.getSupportFragmentManager().findFragmentByTag("com.mapbox.map"); // this is returning null!
                //returns null when there's a bunch of apps open at once and you go back to macquest.  it will attempt to restart and cause a null pointer exception

                //TODO: fix the null return

                if (mapboxMapFragment == null){
                    Log.v(TAG, "MAP FRAGMENT IS NULL!");

                    replaceMapboxFragment(options);
                    restartApplication();
                }

                Log.v(TAG, "MAP FRAGMENT SUCESSFUL TRY STATEMENT");

            } catch (Exception e ){
                Log.v(TAG, "MAP FRAGMENT IN THE CATCH CLAUSE");
                replaceMapboxFragment(options);
                restartApplication();

            }
        }

        mapboxMapFragment.getMapAsync(new OnMapReadyCallback() { //complains that this is null
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                Log.v(TAG, "MAP FRAGMENT ON MAP READY");

                if (!mAlreadyLoaded) {

                    Log.v(TAG, "MAP FRAGMENT ON MAP READY, MAP ALREADY LOADED");

                    mapUtility = new MapUtils(map, myContext, levelButtons, floorButtonList,
                            basementButtonList, mSearchView, routingSearchView, eventListView, eventListCardView, getActivity());

                    //Try to fix the getActivity is null.
                    //((MainActivity) getActivity()).setFragmentMapUtil(mapUtility);
                    ((MainActivity)myContext).setFragmentMapUtil(mapUtility);


                    mapUtility.initializeMap();
                    mapUtility.setupCompass();
                    mapUtility.setupSearchViews();
                    mapUtility.setUiElements(infoCardView, infoTextview, progressBar, infoBuildingName,
                            locationFab, directionsCardView, routeInfoCardView, destinationTextView, clearRoutesButton, toggleARButton, toggleCurrentEvents, bookThisRoomText);
                }

                mAlreadyLoaded = true;
                progressBar.setVisibility(View.GONE);
                levelButtons.setVisibility(View.VISIBLE);

            }
        });
        return view;
    }

    private void replaceMapboxFragment(MapboxMapOptions options) {
        mapboxMapFragment = SupportMapFragment.newInstance(options);

        // Create fragment
        final FragmentTransaction transaction = myContext.getSupportFragmentManager().beginTransaction();

        // Add map fragment to parent container
        transaction.replace(R.id.container, mapboxMapFragment);
        transaction.commit();
    }


    private void restartApplication() {
        Intent i = getContext().getPackageManager().getLaunchIntentForPackage( getContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().finish();
        startActivity(i);
    }

    private void populateFloorButtonList() {
        floorButtonList = new ArrayList<>();
        floorButtonList.add(levelButtonOne);
        floorButtonList.add(levelButtonTwo);
        floorButtonList.add(levelButtonThree);
        floorButtonList.add(levelButtonFour);
        floorButtonList.add(levelButtonFive);
        floorButtonList.add(levelButtonSix);
        floorButtonList.add(levelButtonSeven);
    }

    private void populateBasementButtonList() {
        basementButtonList = new ArrayList<>();
        basementButtonList.add(levelButtonZero);
        basementButtonList.add(levelButtonSubZero);
    }

    public void setLocationListener(MapboxMap.OnMyLocationChangeListener l){
        map.setOnMyLocationChangeListener(l);
    }

    private void setupDrawer() {
        attachSearchViewActivityDrawer(mSearchView);
    }





    @Override
    public void onAttach(Context context) {
        myContext = (FragmentActivity) context;
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "ON RESUME CALLED");

        locationFab.setVisibility(View.VISIBLE);



        //Used for making sure the user location marker resets properly on restart.
        if (mapUtility!= null){


            if (mapUtility.isLocationEnabled()){

                if(mapUtility.userLocation != null)
                {
                    mapUtility.userLocation.setupLocationViewSettings();

                }else{
                    Log.d(TAG, "onResume, mapUtility.userLocation is null");
                }

            }
            /*
            if(mapUtility.ekfLocationService != null){
                mapUtility.ekfLocationService.resume();
            }*/
        }else
        {
            Log.d(TAG, "onResume, mapUtility is null");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapboxMapFragment.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mapboxMapFragment != null)
        {
            mapboxMapFragment.onPause();
        }

        Log.v(TAG, "ON PAUSE CALLED");



    }

    @Override
    public void onStop() {
        super.onStop();
        if(mapboxMapFragment != null){
            mapboxMapFragment.onStop();
        }

        if(mapUtility != null)
        {
            /*
            if(mapUtility.ekfLocationService != null){
                mapUtility.ekfLocationService.stop();
            }
            */
        }else
        {
            Log.d(TAG, "onStop, mapUtility is null");
        }

        Log.v(TAG, "ON STOP CALLED");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v(TAG, "MAP FRAGMENT ON DESTROY");
        mapboxMapFragment.onDestroy();
        mAlreadyLoaded = false;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapboxMapFragment.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.v(TAG, "MAP FRAGMENT ON DETACH");

        mapboxMapFragment.onDetach();

    }

    @Override
    public boolean onActivityBackPress() {
        Toast.makeText(myContext, "back button pressed", Toast.LENGTH_SHORT).show();
        //if mSearchView.setSearchFocused(false) causes the focused search
        //to close, then we don't want to close the activity. if mSearchView.setSearchFocused(false)
        //returns false, we know that the search was already closed so the call didn't change the focus
        //state and it makes sense to call supper onBackPressed() and close the activity
        return mSearchView.setSearchFocused(false);
    }


    //Merge
    void oneScan() {
        map.setMyLocationEnabled(false);

        setFrequencyBand2Hz(true, wifiManager);
        WifiManager.WifiLock wifilock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY, "MyLock");
        wifilock.setReferenceCounted(true);
        if(!doneWifiLock) {
            wifilock.acquire();
            if (!wifilock.isHeld()) {
                wifilock.acquire();
            }
            doneWifiLock = true;
        }
        String strToPost = "";
        if (wifiManager.isWifiEnabled()) {
            wifiManager.startScan();
            String buildingName = mapUtility.setBuildingNameBasedOnGPS();
            StringBuffer stringBuffer = new StringBuffer();
            //List contains all the important wifi information -> all the scan results and their respective fields
            List<ScanResult> list = wifiManager.getScanResults();
            strToPost += "{";
            strToPost += "\"building\":\"";
            if(buildingName.equals("not valid")&& mapUtility.pastBulidingNames.size()>0){
                buildingName = mapUtility.pastBulidingNames.get(mapUtility.pastBulidingNames.size()-1);
            }
            strToPost+=buildingName;
            strToPost += "\",\"floor\":\"";
            strToPost += floorNum;
            strToPost += "\",\"fp\":";
            strToPost += "{";
            //updateScanTimes();
            for (ScanResult scanResult : list) {
                if (list.indexOf(scanResult) > 0) {
                    strToPost = strToPost + "\"" + scanResult.BSSID + "\":";
                    strToPost = strToPost + Integer.toString(scanResult.level) + ",";
                }
            }
            strToPost = strToPost.substring(0, strToPost.length() - 1);
            strToPost = strToPost + "}}";
            postToServer(strToPost);
        }
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
    void postToServer(final String postJSON) {
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
                    String start = "{\"status\": 0, \"ret\": [";
                    currentLocation = buffer.toString().substring(start.length(), buffer.toString().length() - 2);
                    System.out.println(currentLocation);
                    double lat = Double.parseDouble(currentLocation.split(",")[0]);
                    double lon = Double.parseDouble(currentLocation.split(",")[1].substring(1, currentLocation.split(",")[1].length() - 1));
                    makeIcon(true, lat, lon);
                    long endTime = System.nanoTime();
                    Log.i("TIME TO POST:", Long.toString((endTime - startTime) / 1000000));
                } catch (Exception e) {
                    e.printStackTrace();
                    //If floor not fingerprinted
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UserLocation myLocation = new UserLocation(getActivity(), map);
                            map.setMyLocationEnabled(true);
                            myLocation.toggleGps(true, true);
                            myLocation.updateLocation();

                        }
                    });
                }
            }
        }).start();
    }

    void makeIcon(boolean draw, final double lat, final double lon) {
        if (draw) {
            //map.clear();
            Location currentLocation = new Location("");
            currentLocation.setLatitude(lat);
            currentLocation.setLongitude(lon);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CameraPosition position = new CameraPosition.Builder()
                            .zoom(17.75)
                            .target(new LatLng(lat, lon)) // Sets the new camera position
                            .build(); // Creates a CameraPosition from the builder
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 200);
                }
            });
            removeUserLocationMarker(map);
            IconFactory iconFactory = IconFactory.getInstance(getActivity());

            Icon icon = iconFactory.fromResource(R.drawable.wifi_marker);
            if(userLocationMarker!=null){
                userLocationMarker.position(new LatLng(lat, lon));
            }
            else {
                //Location here will be the one from the server processing
                userLocationMarker = new MarkerOptions().position(new LatLng(lat, lon))
                        .icon(icon);
            }

            map.addMarker(userLocationMarker);


            //After once scan is shown stop the receiver
        } else {
            //map.clear();
        }
    }


    public static void removeUserLocationMarker(MapboxMap map){
        List<Marker> mapMarkers = map.getMarkers();
        for(int i = 0; i < mapMarkers.size(); i++){
            if(mapMarkers.get(i).getTitle() == null) {
                map.removeMarker(mapMarkers.get(i));
            }
        }
    }
}

