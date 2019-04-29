package com.mcmaster.wiser.idyll.model;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import com.mcmaster.wiser.idyll.connection.ServerUtils;
import com.mcmaster.wiser.idyll.presenter.util.MapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class UploadUserLocation {
    private Timer uploadLocationTimer = null;
    private TimerTask uploadLocationTimerTask;
    private int jointParentEventID = -1;
    private MapUtils mapFragment;
    private String UUIDstr;

    public UploadUserLocation(){
        this.UUIDstr = UUID.randomUUID().toString();
    }

    public void setMapFragment(MapUtils mapFragment) {
        this.mapFragment = mapFragment;
    }

    public void setTimer() {
        uploadLocationTimer = new Timer();
    }

    private String generateUserLocation(UserLocation userlocation, int pid, String UUIDstr,MapUtils mapUtils) throws JSONException {

        //return "{\"coord\": \"POINT(" + userlocation.currentLocation.getLongitude() + " " +
        //       userlocation.currentLocation.getLatitude() + ")\"}";
        JSONObject userLocationJson = new JSONObject();
        userLocationJson.put("DeviceID", UUIDstr);
        userLocationJson.put("JoinedParentEventID", pid);
        userLocationJson.put("Location_lat", userlocation.currentLocation.getLatitude());
        userLocationJson.put("Location_Lng", userlocation.currentLocation.getLongitude());
        return userLocationJson.toString();


    }

    public void setJointParentEventID(int joindParentEventID) {
        this.jointParentEventID = joindParentEventID;
    }

    public void cleanJointParentEventID() {
        jointParentEventID = -1;
    }

    private void initialUploadTimer() {
        uploadLocationTimer = new Timer();
        uploadLocationTimerTask = new TimerTask() {
            @Override
            public void run() {
                String userLoc = null;
                try {
                    if (mapFragment.userLocation != null) {
                        userLoc = generateUserLocation(mapFragment.userLocation, jointParentEventID,
                                UUIDstr, mapFragment);
                        sendLocationToServer(ServerUtils.HEAT_MAP_URL, userLoc);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

    }

    private void uploadUserLocationToServer() {
        uploadLocationTimer.schedule(uploadLocationTimerTask, ServerUtils.HEAT_MAP_UPLOAD_DELAY_TIME, ServerUtils.HEAT_MAP_UPLOAD_PERIOD_TIME);
    }

    public void startUploadUserLocation() {
        try {
            if (jointParentEventID != -1) {
                endUploadUserLocation();
                initialUploadTimer();
                uploadUserLocationToServer();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void endUploadUserLocation() {
        try {
            if (uploadLocationTimer != null) {
                uploadLocationTimer.cancel();
            }
            uploadLocationTimer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLocationToServer(final String serverURL, final String userLoc) {
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

                    httpURLConnection = (HttpURLConnection) new URL(serverURL).openConnection();
                    httpURLConnection.setRequestMethod("POST");


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

                        in.close();
                        br.close();
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

        if (mapFragment.checkServerPermissions() && userLoc != null) {
            sendInfo.start();
        }
    }


}
