package com.mcmaster.wiser.idyll.view;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.collection.DataCollectionManager;
import com.mcmaster.wiser.idyll.collection.NetworkUtils;
import com.mcmaster.wiser.idyll.connection.Contact;
import com.mcmaster.wiser.idyll.connection.DbContract;
import com.mcmaster.wiser.idyll.connection.DbHelper;
import com.mcmaster.wiser.idyll.connection.MySingleton;
import com.mcmaster.wiser.idyll.connection.ServerUtils;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.ActivityRecognitionHandler;
import com.mcmaster.wiser.idyll.detection.iodetection.IODetectionHandler;
import com.mcmaster.wiser.idyll.model.CheckNewVersion;
import com.mcmaster.wiser.idyll.model.Contracts;
import com.mcmaster.wiser.idyll.model.UploadUserLocation;
import com.mcmaster.wiser.idyll.model.UserLocation;
import com.mcmaster.wiser.idyll.model.building.Building;
import com.mcmaster.wiser.idyll.model.building.history.BuildingHistoryDbHelper;
import com.mcmaster.wiser.idyll.model.event.Event;
import com.mcmaster.wiser.idyll.model.routing.Routing;
import com.mcmaster.wiser.idyll.presenter.NoScanResultException;
import com.mcmaster.wiser.idyll.presenter.ParentEventIDReceiver;
import com.mcmaster.wiser.idyll.presenter.QRScanResultReceiver;
import com.mcmaster.wiser.idyll.presenter.util.BuildingDataUtils;
import com.mcmaster.wiser.idyll.presenter.util.MapUtils;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.mcmaster.wiser.idyll.Manifest;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BaseFragment.BaseFragmentCallbacks, QRScanResultReceiver, ParentEventIDReceiver {

    private MapUtils fragmentMapUtil;
    private CheckNewVersion checkNewVersion;
    private UpdateDialog updateDialog;

    public static boolean washroomsOn = false;
    public static boolean arOn = false;

    //User Current participate parent event ID
    public int currentJoinEvent = 0;


    public void setFragmentMapUtil(MapUtils fragmentMapUtil) {
        this.fragmentMapUtil = fragmentMapUtil;
    }

    private String rawLocation;

    public String getRawLocation() {
        return rawLocation;
    }

    public void setRawLocation(String rawLocation) {
        this.rawLocation = rawLocation;
    }

    public void drawARPoints() {
        fragmentMapUtil.drawARPoints();
        fragmentMapUtil.visibleARPoints = new ArrayList<>();
        fragmentMapUtil.arCheck(fragmentMapUtil.userLocation.currentLocation);
        //fragmentMapUtil.getLocation(false);
    }

    private LatLng busStopLatLng;

    public LatLng getBusStopLatLng() {
        return busStopLatLng;
    }

    public void setBusStopLatLng(LatLng busStopLatLng) {
        this.busStopLatLng = busStopLatLng;
    }

    private static final String TAG = "JASON DEBUG"; //MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private IODetectionHandler ioDetectionHandler;
    public static boolean isOutdoor = true;

    private ActivityRecognitionHandler activityRecognitionHandler;
    private SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Crash bug logger.
        //LogcatHelper.getInstance(this).start();
        ButterKnife.bind(this);
        FirebaseCrash.log("Activity created");
        //Crashlytics.getInstance().crash();


        //Thread to open the introActivity the first time the user starts the app
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {
                    final Intent i = new Intent(MainActivity.this, IntroActivity.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(i);
                        }
                    });
                    SharedPreferences.Editor e = getPrefs.edit();
                    e.putBoolean("firstStart", false);
                    e.apply();
//                    Routing routing = new Routing(MainActivity.this);
                }
            }
        });
        t.start();


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            try {
                MapFragment temp_mapFragment = new MapFragment();
                temp_mapFragment.setParentEventIDChangedreceiver(this);
                showFragment(temp_mapFragment);
                UserLocation.mainActivity = this;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            //When the sidebar closes, you want it to reset the highlights to whatever is being shown on the map.
            // The user basically only needs to see a highlight for either map or nearest washroom.
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                resetSidebarHighlight();
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        // Indoor/Outdoor Detection
        ioDetectionHandler = new IODetectionHandler(this);
        ioDetectionHandler.setOnIOChangeListener(new IODetectionHandler.OnIOChangeListener() {
            @Override
            public void onIOChange(boolean isOut) {
                isOutdoor = isOut;
                Log.d("IsOutdoor", String.valueOf(isOutdoor));
                if (isOutdoor) {
                    Toast.makeText(getBaseContext(), "Outdoor", Toast.LENGTH_SHORT).show();
                }
            }
        });


        /*
        // Up/Down Detection
        registerActivityRecognitionSensors();
        */

        uploadSensorsDataIfWiFi();

        // ----------------- Code from Arooj start -----------------
        IntentFilter connfilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkConnectivity, connfilter);
        //TODO: Check why it leaks the intent reciever
        // ----------------- Code from Arooj end -----------------

        // Check whether we have new app version on the server.
        checkNewVersion();
    }


    /**
     * Check whether we have new app version on the server.
     */

    private void checkNewVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String TAG = "TAG_CHECK_NEW_VERSION";

                HttpURLConnection urlConnection = null;
                URL url = null;
                try {
                    url = new URL(ServerUtils.CHECK_NEW_VERSION + "?clientVersion=1.0");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setConnectTimeout(15000);
                    urlConnection.setDoOutput(true);
                    urlConnection.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder sb = new StringBuilder();

                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    String jsonString = sb.toString();
                    final Gson gson = new Gson();
                    final CheckNewVersion checkNewVersion = gson.fromJson(jsonString, CheckNewVersion.class);

                    Log.d(TAG, "Response: " + checkNewVersion);
                    if (checkNewVersion.changed) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateUIForNewVersion(checkNewVersion);
                            }
                        });
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "checkNewVersion Thread").start();
    }


    /**
     * Show the update UI to remind the new version of the application.
     *
     * @param checkNewVersion
     */
    private void updateUIForNewVersion(final CheckNewVersion checkNewVersion) {
        this.checkNewVersion = checkNewVersion;
        navigationView.getMenu().findItem(R.id.new_version).setVisible(true);
        showUpdateNewVersionDialog();
    }

    /**
     * Show the update dialog to remind the new version of the application.
     */
    private void showUpdateNewVersionDialog() {
        if (checkNewVersion != null) {
            updateDialog = new UpdateDialog(MainActivity.this, R.layout.dialog_updataversion,
                    new int[]{R.id.dialog_sure, R.id.quxiao});
            updateDialog.setOnCenterItemClickListener(new UpdateDialog.OnCenterItemClickListener() {
                @Override
                public void OnCenterItemClick(UpdateDialog dialog, View view) {
                    switch (view.getId()) {
                        case R.id.dialog_sure:
                            updateDialog.dismiss();
                            break;

                        case R.id.quxiao:
                            updateDialog.dismiss();
                            break;
                    }
                }
            });
            updateDialog.setNewVersionCode(checkNewVersion.latestVersion);
            updateDialog.setUpdateMessage("Please check the new version from:\n\n" + checkNewVersion.link);
            updateDialog.show();
        } else {
            updateDialog = new UpdateDialog(MainActivity.this, R.layout.dialog_updataversion,
                    new int[]{R.id.dialog_sure, R.id.quxiao});
            updateDialog.setOnCenterItemClickListener(new UpdateDialog.OnCenterItemClickListener() {
                @Override
                public void OnCenterItemClick(UpdateDialog dialog, View view) {
                    switch (view.getId()) {
                        case R.id.dialog_sure:
                            updateDialog.dismiss();
                            break;

                        case R.id.quxiao:
                            updateDialog.dismiss();
                            break;
                    }
                }
            });
            updateDialog.setUpdateMessage("Please check the new version from GooglePlay!");
            updateDialog.show();
        }
    }

    private void uploadSensorsDataIfWiFi() {
        int connectedType = NetworkUtils.getConnectedType(this);
        if (connectedType == ConnectivityManager.TYPE_WIFI) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataCollectionManager.uploadAndDeleteData();
                }
            }, "UploadSensorsDataThread").start();
        }
    }


    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(drawer);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentMapUtil != null &&
                (fragmentMapUtil.getUICardviews().get(0).getVisibility() == View.VISIBLE || fragmentMapUtil.getUICardviews().get(1).getVisibility() == View.VISIBLE)) {
            fragmentMapUtil.hideCardView(fragmentMapUtil.getUICardviews().get(0));
            fragmentMapUtil.hideCardView(fragmentMapUtil.getUICardviews().get(1));
        } else {
            int k = getSupportFragmentManager().getBackStackEntryCount();
            if (k == 1) {
                finish();
            } else {
                super.onBackPressed();
            }
        }

        // When you close any cardview, you want to highlighter either the washroom option
        // if you have that filter on or the map option if the filter is off.
        //resetSidebarHighlight();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (fragmentMapUtil == null) {
            return true;
        }
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Clears the pins on the map. When you click the map option
        // you want to reset the map to default.
        if (id == R.id.nav_map) {
            washroomsOn = false;
            arOn = false;
            fragmentMapUtil.clearMap();
            popToMapFragment();
        } else if (id == R.id.nav_building_directory) {
            fragmentMapUtil.hideAllUIElements();
            showFragment(new BuildingDirectoryFragment());
        }

        // When you choose the washroom filter, you want to
        // either toggle the washroom pins on or off.
        else if (id == R.id.nav_nearest_washroom) {

            if (!item.isChecked()) {
                washroomsOn = true;
                arOn = false;
                popToMapFragment();
                fragmentMapUtil.clearMarkers();
                fragmentMapUtil.markers = new HashMap<String, ArrayList<MarkerViewOptions>>();
                fragmentMapUtil.showNearestWashroom();

            } else {
                washroomsOn = false;
                fragmentMapUtil.clearMarkers();
                fragmentMapUtil.markers = new HashMap<String, ArrayList<MarkerViewOptions>>();
                item.setChecked(false);
            }
        } else if (id == R.id.nav_event) {
            if (!item.isChecked()) {
                fragmentMapUtil.clearMarkers();
                washroomsOn = false;
                fragmentMapUtil.markers = new HashMap<String, ArrayList<MarkerViewOptions>>();
                if (fragmentMapUtil.fillEventList()) {

                    fragmentMapUtil.hideAllUIElements();
                    fragmentMapUtil.hideProgressBar();

                    EventListFragment eventListFragment = new EventListFragment();
                    eventListFragment.setReceiver(this);
                    eventListFragment.eventData = fragmentMapUtil.arEvents;
                    showFragment(eventListFragment);
                } else {
                    //TODO: Toast a message and reset highlight
                    fragmentMapUtil.hideProgressBar();
                    Toast.makeText(this, "Error Finding Campus Events", Toast.LENGTH_SHORT).show();
                }


            } else {
                arOn = false;
                fragmentMapUtil.clearMarkers();
                fragmentMapUtil.markers = new HashMap<String, ArrayList<MarkerViewOptions>>();
                fragmentMapUtil.clearARPoints();
                item.setChecked(false);
            }
        } else if (id == R.id.scan_qrcode) {


//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFragment(new QRScanFragment());

//            }
//            else{
//                //TODO: REMOVE CALIBRATION
//                Intent myIntent = new Intent(this,ARActivity.class);
//                myIntent.putExtra("QR","true");
//                fragmentMapUtil.startARNoCompass(myIntent);
//            }


        } else if (id == R.id.nav_bus_schedule) {
            fragmentMapUtil.hideAllUIElements();
            showFragment(new BusFragment());
        } else if (id == R.id.nav_about) {
            fragmentMapUtil.hideAllUIElements();
            showFragment(new InfoFragment());
        } else if (id == R.id.nav_download) {
            fragmentMapUtil.downloadOfflineMap();
        } else if (id == R.id.nav_emergency_services) {
            fragmentMapUtil.hideAllUIElements();
            showFragment(new EmergencyServicesFragment());
        } else if (id == R.id.db_update) {
            updataDatabase();
        } else if (id == R.id.new_version) {
            showUpdateNewVersionDialog();
        }

        drawer.closeDrawer(GravityCompat.START);

        // When you close any cardview, you want to highlighter either the washroom option
        // if you have that filter on or the map option if the filter is off.

        //resetSidebarHighlight();

        return true;
    }

    public void resetSidebarHighlight() {
        if (washroomsOn) {
            navigationView.getMenu().getItem(1).setChecked(true);
            navigationView.getMenu().getItem(1).setTitle("Clear Washroom Points");
            navigationView.getMenu().getItem(2).setTitle("Campus Events");
        } else if (arOn) {
            navigationView.getMenu().getItem(2).setTitle("Clear Event Points");
            navigationView.getMenu().getItem(1).setTitle("Nearest Washroom");
            navigationView.getMenu().getItem(2).setChecked(true);
        } else {
            navigationView.getMenu().getItem(0).setChecked(true);
            navigationView.getMenu().getItem(1).setTitle("Nearest Washroom");
            navigationView.getMenu().getItem(2).setTitle("Campus Events");
        }

        if (!arOn) {
            fragmentMapUtil.clearARPoints();
        }

    }

    private void updataDatabase() {
        if (checkNetworkConnection()) {
            new StaticFileCheck().execute(ServerUtils.API_DATA_UPLOAD_PARSER);
        } else {

            Log.v(TAG, "Please connect to the internet and then check again for the updates");
        }
    }

    private void popToMapFragment() {
        setRawLocation(null);
        setBusStopLatLng(null);
        getSupportFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);
    }

    public void showFragment(Fragment fragment) {
        setRawLocation(null);
        setBusStopLatLng(null);
        getSupportFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom)
                .addToBackStack(fragment.getClass().getSimpleName())
                .add(R.id.fragment_container, fragment, fragment.getTag())
                .commit();


    }

    public void resetFabMenu() {
        fragmentMapUtil.showFabMenus();
    }

    @Override
    protected void onStart() {

        //Initiate multidex for APIs less than 20
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this);
        }

        super.onStart();
        // Load check box values
        CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkbox_routing_criteria);
        boolean checked = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("checkbox_routing_criteria", false);
        checkBox1.setChecked(checked);


    }

    @Override
    protected void onResume() {

        Log.v(TAG, "MAIN ACTIVITY ON RESUME");

        super.onResume();
        if (ioDetectionHandler != null) {
            ioDetectionHandler.onResume();
        }

        if (fragmentMapUtil != null) {
            fragmentMapUtil.resume();
        } else {
            Log.d(TAG, "MainActivity, OnResume, fragmentMapUtil == null");
        }

        m_uploadUserLocation.startUploadUserLocation();


    }

    @Override
    protected void onStop() {

        Log.v(TAG, "MAIN ACTIVITY ON STOP");

        super.onStop();
        if (ioDetectionHandler != null) {
            ioDetectionHandler.onStop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            int index = 0;
            Map<String, Integer> PermissionsMap = new HashMap<String, Integer>();
            for (String permission : permissions) {
                PermissionsMap.put(permission, grantResults[index]);
                index++;
            }

            if ((PermissionsMap.get(Manifest.permission.SEND_LOCATION) == 0)) {
                //To test break channel.
                //fragmentMapUtil.sendLocationToServer(ServerUtils.HEAT_MAP_URL,fragmentMapUtil.generateLocationForServer(),"");
            }
        } catch (Exception e) {
        }


    }

    @Override
    protected void onPause() {
        Log.v(TAG, "MAIN ACTIVITY ON PAUSE");
        super.onPause();

        if (fragmentMapUtil != null) {
            if (fragmentMapUtil.heatMapID != -1) {
                //To test break channel.
                //fragmentMapUtil.sendLocationToServer(ServerUtils.HEAT_MAP_URL,fragmentMapUtil.generateLocationForServer(),"DELETE");

            }
            m_uploadUserLocation.endUploadUserLocation();
            fragmentMapUtil.pause();

        }


//        try{
//
//
//        }
//        catch (Exception e){
//
//        }
    }


    @Override
    protected void onDestroy() {
        Log.v(TAG, "MAIN ACTIVITY ON DESTROY");
        super.onDestroy();
        unregisterReceiver(checkConnectivity);
//        // Store check box values
//        CheckBox checkBox1 = (CheckBox) findViewById(R.id.checkbox_routing_criteria);
//        PreferenceManager.getDefaultSharedPreferences(this).edit()
//                .putBoolean("checkbox_routing_criteria", checkBox1.isChecked()).apply();

        //LogcatHelper.getInstance(this).stop();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch (view.getId()) {
            case R.id.checkbox_routing_criteria:
                if (checked) {
                    Log.d("check", "checked");
                    Routing.avoidIndoorPathways = true;
                } else {
                    Log.d("check", "not checked");
                    Routing.avoidIndoorPathways = false;
                }
                break;
            case R.id.checkbox_show_dest:
                if (checked) {
                    Log.d("showdest", "checked");
                    fragmentMapUtil.pinDestination();
                } else {
                    Log.d("showdest", "not checked");

                }

        }


    }

    private void save(final boolean isChecked) {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("check", isChecked);
        editor.commit();
    }

    private boolean load() {
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("check", false);
    }

    /*
    private void registerActivityRecognitionSensors() {
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        activityRecognitionHandler = new ActivityRecognitionHandler(getApplicationContext());
        mSensorManager.registerListener(activityRecognitionHandler,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(activityRecognitionHandler,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(activityRecognitionHandler,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST);
        activityRecognitionHandler.setClassifyListener(new ActivityRecognitionHandler.ClassifyListener() {

            // Get classification results
            @Override
            public void onClassify(ClassifyData classifyData) {
                //For Clean Data Info
                //Log.d("UpDownDetection", classifyData.toString());
                switch (classifyData.classifyResult) {
                    case 1:
                        Toast.makeText(getApplicationContext(), "Detect you are going up, just a remind to switch floor level.", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "Detect you are going down, just a remind to switch floor level.", Toast.LENGTH_SHORT).show();
                        break;
                }
                // classifyResult: 0 horizontal
                // classifyResult: 1 up
                // classifyResult: 2 down
            }
        });
    }
    */

    // INTERNET CONNECTIVITY
    ArrayList<Contact> arrayList = new ArrayList<>();

    public boolean checkNetworkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    ////// --------------- READING FROM THE LOCAL DATABASE DB3.SQLITE -----------------------

    private void readFromLocalStorage(Context context) {
        arrayList.clear();
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = dbHelper.readFromLocalDatabase(database);

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(DbContract.COLUMN_BUILDING_NAME));
            String shortname = cursor.getString(cursor.getColumnIndex(DbContract.COLUMN_BUILDING_SHORTNAME));
            String location = cursor.getString(cursor.getColumnIndex(DbContract.COLUMN_LOCATION));
            String outid = cursor.getString(cursor.getColumnIndex(DbContract.COLUMN_OUT_ID));
            String room = cursor.getString(cursor.getColumnIndex(DbContract.COLUMN_ROOM));
            String utility = cursor.getString(cursor.getColumnIndex(DbContract.COLUMN_UTILITY));
            String uuid = cursor.getString(cursor.getColumnIndex(DbContract.COLUMN_UUID));
            int syncstatus = cursor.getInt(cursor.getColumnIndex(DbContract.COLUMN_SYNC_STATUS));
            arrayList.add(new Contact(name, shortname, location, outid, room, utility, uuid, syncstatus));
        }
        cursor.close();
        dbHelper.close();

    }

    ////// ----- SAVE TO APPLICATION SERVER DATABASE DB3.SQLITE -----
    private void saveToAppServer(final String bname, final String bshortName, final String blocation, final String boutId, final String broom, final String butility) {
        final String id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);


        if (checkNetworkConnection()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, DbContract.SERVER_URL_HISTORY_DATA, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    saveToLocalStorage(bname, bshortName, blocation, boutId, broom, butility, id, DbContract.SYNC_STATUS_OK);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    saveToLocalStorage(bname, bshortName, blocation, boutId, broom, butility, id, DbContract.SYNC_STATUS_FAILED);
                }
            }) {
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("name", bname);
                    params.put("shortname", bshortName);
                    params.put("location", blocation);
                    params.put("outid", boutId);
                    params.put("room", broom);
                    params.put("utility", butility);
                    params.put("uuid", id);
                    return params;

                }

            };
            MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);

        } else {
            //  final String id = Settings.Secure.getString(mainContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            saveToLocalStorage(bname, bshortName, blocation, boutId, broom, butility, id, DbContract.SYNC_STATUS_FAILED);
        }

    }

    //// ----- SAVE TO LOCAL STORAGE ------
    private void saveToLocalStorage(String bname, String bshortName, String blocation, String boutId, String broom, String butility, String buuId, int bsync_status) {
        DbHelper dbHelper = new DbHelper(MainActivity.this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        dbHelper.saveToLocalDatabase(bname, bshortName, blocation, boutId, broom, butility, id, bsync_status, database);
        readFromLocalStorage(MainActivity.this);
        dbHelper.close();

    }

    ///// ----- BROADCAST RECEIVER ---------- /////////////////////
    private BroadcastReceiver checkConnectivity = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {

            Log.d(TAG, "checkConnectivityBroadcastReceiver!");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String id = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = manager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isAvailable()) {
                        final BuildingHistoryDbHelper mDbHelper = new BuildingHistoryDbHelper(MainActivity.this);
                        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
                        String path = mDbHelper.dbPath(context);
                        int version = mDbHelper.getDatabaseVersion(db);
                        File dbpath = MainActivity.this.getDatabasePath(BuildingHistoryDbHelper.DATABASE_NAME);

                        long lastModified = dbpath.lastModified();
                        Log.d(TAG, "Database Details" + path + "--" + version + " ---- : " + lastModified);
                        // Cursor cursor=dbHelper.readFromLocalDatabase(sqLiteDatabase);
                        ArrayList<Building> buildingDataList = BuildingDataUtils.fetchRawHistory(MainActivity.this);
                        for (Building building : buildingDataList) {
                            int Sync_Status = building.getSync_status();
                            if (Sync_Status == BuildingHistoryDbHelper.SYNC_STATUS_FAILED) {
                                final String N = building.getName();
                                final String SN = building.getShortName();
                                final String R = building.getRoom();
                                final String O = building.getOutId();
                                final String L = building.getLocation();
                                //   final String UI = building.getUuid();
                                final String U = building.getUtility();

                                StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtils.API_HISTORY_DB, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.v(TAG, "UPDATELOCAL-DATABASE-WORKS!");

                                        ContentValues values = new ContentValues();
                                        values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME, N);
                                        values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME, SN);
                                        values.put(Contracts.BuildingHistoryEntry.COLUMN_OUT_ID, O);
                                        values.put(Contracts.BuildingHistoryEntry.COLUMN_LOCATION, rawLocation);
                                        values.put(Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS, BuildingHistoryDbHelper.SYNC_STATUS_OK);
                                        db.insert(Contracts.BuildingHistoryEntry.TABLE_NAME, null, values);

                                        mDbHelper.updateLocalDatabase(N, BuildingHistoryDbHelper.SYNC_STATUS_OK, db);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "onErrorResponse");
                                    }
                                }) {
                                    protected Map<String, String> getParams() throws AuthFailureError {

                                        Map<String, String> params = new HashMap<>();
                                        params.put("name", N);
                                        params.put("shortname", SN);
                                        params.put("location", L);
                                        params.put("outid", O);
                                        params.put("room", R);
                                        params.put("utility", U);
                                        params.put("uuid", id);
                                        return params;

                                    }

                                };
                                MySingleton.getInstance(MainActivity.this).addToRequestQue(stringRequest);
                            }
                        }
                        //  mDbHelper.close();
                    }
                }
            }, "connectivity receiver working").start();


        }
    };

    @Override
    public void QRScanResultData(String codeFormat, String codeContent) {
        Log.d("QR_Result:", String.format("%s, %s", codeFormat, codeContent));


        //TODO: REMOVE CALIBRATION
        if (codeContent != null) {


            //Show event requirement.
            fragmentMapUtil.showEventActionRequireDialog(codeContent);

            Intent myIntent = new Intent(this, ARActivity.class);
            myIntent.putExtra("QR", "true");
            myIntent.putExtra("QRURL", codeContent);
            fragmentMapUtil.startARNoCompass(myIntent);
        }

    }

    @Override
    public void QRScanResultData(NoScanResultException noScanData) {
        Toast toast = Toast.makeText(this, noScanData.getMessage(), Toast.LENGTH_SHORT);
        toast.show();
    }

    UploadUserLocation m_uploadUserLocation = new UploadUserLocation();

    @Override
    public void parentIDChanged(int PID, boolean isJoined) {


        //TODO: Update Userlocation with PID;

        try {
            if(fragmentMapUtil.checkServerPermissions()) {
                if (isJoined) {
                    m_uploadUserLocation.setMapFragment(fragmentMapUtil);
                    m_uploadUserLocation.setJointParentEventID(PID);
                    m_uploadUserLocation.startUploadUserLocation();
                } else {
                    m_uploadUserLocation.cleanJointParentEventID();
                    m_uploadUserLocation.endUploadUserLocation();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    /*
THIS IS MAIN CODE. IT HAS THE FOLLOWING FUNCTIONALITIES
1) PARSE INFORMATION FROM THE SERVER.
   - GET DYNAMIC URL OF UPDATED DB3-DATABASE.
   - GET UPDATED DB3-DATABASE TIME.
2) GETS INFORMATION OF A LOCAL APPLICATION.
   - TIME OF INSTALLATION AND CONVERTS IT INTO A DATE FORMAT.
   - TIME OF APPLICATION UPDATE.
3) COMPARES TIME OF INSTALLATION WITH TIME OF SERVER UPDATED DB.
   - IF APP UPDATE TIME IS AFTER DB UPDATE ON THE SERVER.
       - IT WILL DO NOTHING.
   - IF ITS THE OTHER WAY:
       - IT WILL GENERATE A NOTIFICATION.
       - WHEN USER CLICKS IT, IT WILL ASK USER TO UPDATE THE DATABASE.
       - PREVIOUS DB3 WILL BE DELETED
       - NEW DB3 WILL BE DOWNLOADED.
    */
    private class StaticFileCheck extends AsyncTask<String, String, String> {
        private static final String TAG = "StaticTasks";

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                java.net.URL url = new URL(params[0]);
                // connects to the server
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // InputStream
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = " ";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "  ");
                }
                // JSON-OBJECT data will be returned here as a String.
                String finalJSON = buffer.toString();
                JSONArray jsonDateArray = new JSONArray(finalJSON);
                JSONObject jdate = jsonDateArray.getJSONObject(0);
                String databaseDate = jdate.getString("Upload_date");
                String databaseURL = jdate.getString("file");
                ServerUtils.API_LATEST_DB = databaseURL;
                return databaseDate;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            final Date serverDate;
            ///// SERVER DB3 UPDATE TIME ////
            serverDate = stringToDate(s);
            if (serverDate == null) {
                return;
            }


            Log.v(TAG, "SERVER DATE: date-format: " + serverDate);
            new Thread(new Runnable() {
                @Override
                public void run() {

                    // LOCAL DB3 INSTALLATION TIME //
                    PackageInfo packageInfo = null;
                    String packageName = "com.mcmaster.wiser.idyll";
                    try {
                        packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    Date installTime, updateTime;
                    installTime = new Date(packageInfo.firstInstallTime);
                    Log.d(TAG, "Installed: " + installTime.toString());
                    updateTime = new Date(packageInfo.lastUpdateTime);
                    Log.d(TAG, "Updated: " + updateTime.toString());
                    boolean alert = updateTime.after(serverDate);
                    ////// COMPARINGG BOTH TIMES
                    if (alert == true) {
                        Log.v(TAG, "No Update required");
                    } else {
                        // Builds the notification.
                        NotificationCompat.Builder notification;
                        final int notificationIdStaticFile = 25845;
                        notification = new NotificationCompat.Builder(MainActivity.this);
                        notification.setAutoCancel(true);
                        notification.setSmallIcon(R.drawable.logo);
                        notification.setTicker("Your application is not update! Please update");
                        notification.setWhen(System.currentTimeMillis());
                        notification.setContentTitle("MACQUEST UPDATES");
                        notification.setContentText("Your application is updated. Please restart your app");
                        downloadStaticFile();
                        ///// this will initiate when user clicks it for the updates //////
                        Intent intent = null;
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pendingIntent);
                        Log.v(TAG, "Update Required");

                        /// Builds notification and issues it to the device
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(notificationIdStaticFile, notification.build());
                    }

                    Log.v(TAG, "ServerDate:    " + serverDate + "  LocalDbDate:    " + installTime);

                }
            }, "File_Download").start();
        }


        ///////////   CONVERTS STRING INTO A SPECIFIC DATE FORMAT. ///////////////////////
        public Date stringToDate(String s) {
            if (s == null || s.equals("")) {
                return null;
            }
            String dtStart = s;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = null;
            try {
                date = format.parse(dtStart);
                System.out.println(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            return date;
        }
    }

    ///// CODE FOR DOWNLOADING DB3 FROM THE SERVER /////////
    public String downloadStaticFile() {
        boolean fileStatus;
        // String fileFullName = DB_PATH + "/"+fileName;
        //String fileFullNameVersion = DB_PATH + "/"+fileName+"-1";
        String fileFullName = com.mcmaster.wiser.idyll.model.DbHelper.DB_NAME;
        String path = FileUtils.getUserDirectoryPath();
        String fileFullNameVersion = com.mcmaster.wiser.idyll.model.DbHelper.DB_NAME + "-1";
        File desFile = new File(com.mcmaster.wiser.idyll.model.DbHelper.DB_PATH + "/" + fileFullName);
        File verFile = new File(com.mcmaster.wiser.idyll.model.DbHelper.DB_PATH + "/" + fileFullNameVersion);
        if (desFile.exists()) {
            try {
                FileUtils.forceDelete(desFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (verFile.exists()) {
            try {
                FileUtils.forceDelete(verFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DownloadManager downloadManager;
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(ServerUtils.API_LATEST_DB));
        long dbStatusId;
        /// HERE DB3.SQLITE LOCAL PATH (DB_PATH) from Eric Code is required.
        request.setDestinationInExternalFilesDir(MainActivity.this, Environment.DIRECTORY_DOWNLOADS, "db3.sqlite");
        Log.d(TAG, "destination path : " + fileFullName);
        dbStatusId = downloadManager.enqueue(request);
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
        return "download comeplete";
    }

    /////// END OF DB3 DOWNLOAD CODE. BROADCAST RECEIVER TO MONITOR DOWNLOAD IS ABOVE.


}