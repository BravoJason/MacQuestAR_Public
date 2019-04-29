package com.mcmaster.wiser.idyll.connection;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * Created by daniel on 2018-05-28.
 */

public class ARLocationService extends Service {
    public MapboxMap.OnMyLocationChangeListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){

    }

    @Override
    public void onDestroy(){
    }



}
