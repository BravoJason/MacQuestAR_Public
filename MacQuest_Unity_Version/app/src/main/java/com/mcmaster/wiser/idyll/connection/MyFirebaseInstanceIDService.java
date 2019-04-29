package com.mcmaster.wiser.idyll.connection;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseInsIDService";

    @Override
    public void onTokenRefresh() {
        //Get updated token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "New Token: " + refreshedToken);
        FirebaseMessaging.getInstance().subscribeToTopic("com.mcmaster.wiser.idyll");
        Log.d(TAG,"Subscribed to com.mcmaster.wiser.idyll topic");
        //You can save the token into third party server to do anything you want
    }
}
