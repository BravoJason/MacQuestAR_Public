package com.mcmaster.wiser.idyll.model;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import com.mcmaster.wiser.idyll.presenter.util.MapUtils;
import com.getbase.floatingactionbutton.FloatingActionButton;

import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import android.content.Context;

import com.mcmaster.wiser.idyll.R;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationSource;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;



import java.util.List;

public class UserLocation implements PermissionsListener {

    private MapView mapView;
    private MapboxMap map;
    private boolean moveCamera;
    public LocationEngine locationEngine;
    public LocationEngineListener locationEngineListener;
    public FloatingActionButton floatingActionButton;
    private PermissionsManager permissionsManager;
    Context context;
    public static AppCompatActivity mainActivity; //context classes cannot be in static fields
    public static Location currentLocation;

    public UserLocation(Context context, MapboxMap map) {
        locationEngine = new LocationSource(context);
        floatingActionButton = (FloatingActionButton) mainActivity.findViewById(R.id.fab_toggle_location);

        locationEngine.activate();
        this.map = map;
        this.context = context;
    }


    public void toggleGps(boolean enableGps, boolean moveCamera) {
        Log.d("hehe", "hehe togglegps, bool: " + moveCamera);
        this.moveCamera = moveCamera;
        if (enableGps) {
            // Check if user has granted location permission
            permissionsManager = new PermissionsManager(this);
            if (!PermissionsManager.areLocationPermissionsGranted(context)) {
                permissionsManager.requestLocationPermissions(mainActivity);
            } else {
                enableLocation(true, moveCamera);
            }
        } else {
            enableLocation(false, moveCamera);
        }
    }

    private void enableLocation(boolean enabled, boolean moveCamera) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.v("TAG", "permission check 3");
                int MY_PERMISSIONS_REQUEST = 1;

                ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);

                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location lastLocation;
            if( currentLocation != null){
                lastLocation = currentLocation;
            }
            else{
                lastLocation = locationEngine.getLastLocation();
            }



            setupLocationViewSettings();

            if (lastLocation != null) {
                Log.d("lastLocation", lastLocation.toString());
                currentLocation = lastLocation;

                if (moveCamera) {
                    MapUtils.animateMap(map, new LatLng(lastLocation), MapConstants.ZOOM_LEVEL_BUILDING);
                }

                setupLocationViewSettings();
            }



            locationEngineListener = new LocationEngineListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        // Move the map camera to where the user location is and then remove the
                        // listener so the camera isn't constantly updating when the user location
                        // changes. When the user disables and then enables the location again, this
                        // listener is registered again and will adjust the camera once again.
                        Log.d("location", "location changed to :" + location.getLatitude() + " " + location.getLongitude());
                        setupLocationViewSettings();
                        MapUtils.animateMap(map, new LatLng(location), MapConstants.ZOOM_LEVEL_BUILDING);
                        locationEngine.removeLocationEngineListener(this);
                        currentLocation = location;
                    }
                }
            };
            locationEngine.addLocationEngineListener(locationEngineListener);

        } else {
            //setupLocationViewSettings();
        }
        // Enable or disable the location layer on the map
//    if(moveCamera){
        map.setMyLocationEnabled(enabled);

        //  }
    }

    public void setupLocationViewSettings() {
        //TODO: customize this marker so that it looks nice
        map.getMyLocationViewSettings().setEnabled(true);
        map.getMyLocationViewSettings().initialise(new MapboxMapOptions());
        map.getMyLocationViewSettings().setForegroundDrawable(context.getResources().getDrawable(R.drawable.mapbox_mylocation_icon_default), context.getResources().getDrawable(R.drawable.mapbox_mylocation_icon_default));
        map.getMyLocationViewSettings().setAccuracyTintColor(context.getResources().getColor(R.color.colorPrimary));
        map.getMyLocationViewSettings().setForegroundTintColor(context.getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(context, R.string.user_location_permission_explanation,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocation(true, moveCamera);
        } else {
            Toast.makeText(context, R.string.user_location_permission_not_granted,
                    Toast.LENGTH_LONG).show();
            mainActivity.finish();
        }
    }


    public void updateLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.v("TAG", "permission check 4");
            int MY_PERMISSIONS_REQUEST = 1;

            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location lastLocation;
        if (currentLocation != null){
            lastLocation = currentLocation;
        }
        else{
            lastLocation = locationEngine.getLastLocation();
        }
        if (lastLocation != null) {
            currentLocation=lastLocation;
        }
    }

}
