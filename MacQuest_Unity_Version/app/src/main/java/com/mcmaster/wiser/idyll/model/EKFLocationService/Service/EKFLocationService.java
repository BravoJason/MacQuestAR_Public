package com.mcmaster.wiser.idyll.model.EKFLocationService.Service;

/**
 * Reference: https://github.com/maddevsio/mad-location-manager
 */


import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.mcmaster.wiser.idyll.model.EKFLocationService.Commons.Coordinates;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Commons.GeoPoint;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Commons.SensorGpsDataItem;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Commons.Utils;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Filters.GPSAccEKF;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Filters.ZUPT;
import com.mcmaster.wiser.idyll.model.EKFLocationService.Interface.LocationNotifier;


public class EKFLocationService {


    private ZUPT m_ZUPT;

    private float[] rotationMatrix = new float[16];
    private float[] rotationMatrixInv = new float[16];
    private float[] absAcceleration = new float[4];
    private float[] linearAcceleration = new float[4];
    private float[] gyroscopeReading = new float[3];

    private SensorDataEventLoopTask m_eventLoopTask;

    protected Location m_lastLocation;

    private double m_magneticDeclination = 0.0;
    private float[] rotationMatrixIdentity = {
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1};
    private float[] rotationAngleinEachAxis = new float[3];

    private final String TAG = "EKFLocationService";

    private Queue<SensorGpsDataItem> m_sensorDataQueue =
            new PriorityBlockingQueue<>();


    private GPSAccEKF m_EKF;

    LocationNotifier Owner;

    public EKFLocationService(LocationNotifier Owner) {
        m_ZUPT = new ZUPT(Utils.DEFAULT_ZUPT_DATA_WINDOW);
        this.Owner = Owner;
        m_eventLoopTask = new SensorDataEventLoopTask(Utils.ThREAD_SLEEP_TIME, Owner);
        m_eventLoopTask.needTerminate = false;
        m_eventLoopTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }





    private boolean hasInitGPSLocation = false;


    public void reset() {
        hasInitGPSLocation = false;
        m_sensorDataQueue.clear();
    }


    //Sensor type


    public void inputSensorData(SensorEvent event) {

        //If there is no GPS inital location, we can not do predict step.
        if (hasInitGPSLocation == false) {
            return;
        }

        final int east = 0;
        final int north = 1;
        final int up = 2;


        long now = android.os.SystemClock.elapsedRealtimeNanos();
        long nowMs = Utils.nano2milli(now);

        boolean zuptC1Result = false;
        boolean zuptC2Result = false;
        boolean zuptC3Result = false;

        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:

                System.arraycopy(event.values, 0, linearAcceleration, 0, event.values.length);
                m_ZUPT.addAccReading(linearAcceleration);

                zuptC1Result = m_ZUPT.C1(linearAcceleration, Utils.ZUPT_C1_MIN_THRESHOLD, Utils.ZUPT_C1_MAX_THRESHOLD, "C1");
                zuptC2Result = m_ZUPT.C2(Utils.ZUPT_C2_THRESHOLD);
                zuptC3Result = m_ZUPT.C3(gyroscopeReading, Utils.ZUPT_C3_THRESHOLD);

                boolean zuptResult = zuptC1Result && zuptC2Result && zuptC3Result;

                android.opengl.Matrix.multiplyMV(absAcceleration, 0, rotationMatrixInv,
                        0, linearAcceleration, 0);

                if (m_EKF == null) {
                    break;
                }

                //TODO:Add msg into queue.
                SensorGpsDataItem sdi = new SensorGpsDataItem(
                        nowMs,
                        SensorGpsDataItem.NOT_INITIALIZED,
                        SensorGpsDataItem.NOT_INITIALIZED,
                        SensorGpsDataItem.NOT_INITIALIZED,
                        absAcceleration[north],
                        absAcceleration[east],
                        absAcceleration[up],
                        SensorGpsDataItem.NOT_INITIALIZED,
                        SensorGpsDataItem.NOT_INITIALIZED,
                        SensorGpsDataItem.NOT_INITIALIZED,
                        SensorGpsDataItem.NOT_INITIALIZED,
                        m_magneticDeclination,
                        zuptResult);
                m_sensorDataQueue.add(sdi);
                break;


            case Sensor.TYPE_ROTATION_VECTOR:
                Log.i("ROTATION_VECTOR_ERROR", String.valueOf(event.values[0]) + ", "
                        + String.valueOf(event.values[1]) + ", "
                        + String.valueOf(event.values[2]) + ", "
                        + String.valueOf(event.values[3]) + ", "
                        + String.valueOf(event.values[4]) + ", ");

                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                SensorManager.getAngleChange(rotationAngleinEachAxis, rotationMatrix, rotationMatrixIdentity);


                String debugStr = String.format("Rotation angle: x:%f, y:%f, z:%f", rotationAngleinEachAxis[0], rotationAngleinEachAxis[1], rotationAngleinEachAxis[2]);

                Log.i(TAG, debugStr);

                android.opengl.Matrix.invertM(rotationMatrixInv, 0, rotationMatrix, 0);
                break;

            case Sensor.TYPE_GYROSCOPE:
                System.arraycopy(event.values, 0, gyroscopeReading, 0, event.values.length);
                break;
        }

    }


    public void inputLocationInfo(Location loc) {
        if (loc == null) {
            return;
        }

        if (hasInitGPSLocation == false) {
            hasInitGPSLocation = true;
        }

        double x, y, xVel, yVel, posDev, course, speed;
        long timeStamp;
        speed = loc.getSpeed();
        course = loc.getBearing();
        x = loc.getLongitude();
        y = loc.getLatitude();
        xVel = speed * Math.cos(course);
        yVel = speed * Math.sin(course);
        posDev = loc.getAccuracy();
        timeStamp = Utils.nano2milli(loc.getElapsedRealtimeNanos());
        String logStr = "";

        double velErr = 0;

        velErr = loc.getAccuracy() * 0.1;



        String courseLogStr = String.format("theta | [%d, %f]", timeStamp, course);
        Log.i(TAG, courseLogStr);


        logStr = String.format("%d%d GPS : pos lat=%f, lon=%f, alt=%f, hdop=%f, speed=%f, bearing=%f, sa=%f",
                Utils.LogMessageType.GPS_DATA.ordinal(),
                timeStamp, loc.getLatitude(),
                loc.getLongitude(), loc.getAltitude(), loc.getAccuracy(),
                loc.getSpeed(), loc.getBearing(), velErr);
        //log2File(logStr);

        GeomagneticField f = new GeomagneticField(
                (float) loc.getLatitude(),
                (float) loc.getLongitude(),
                (float) loc.getAltitude(),
                timeStamp);
        m_magneticDeclination = f.getDeclination();

        if (m_EKF == null) {
            String locationString = String.format("%d%d KalmanAlloc : lon=%f, lat=%f, speed=%f, course=%f, m_accXDev=%f, m_accYDev=%f,posDev=%f",
                    Utils.LogMessageType.KALMAN_ALLOC.ordinal(),
                    timeStamp, x, y, speed, course, Utils.ACCELEROMETER_X_DEFAULT_DEVIATION, Utils.ACCELEROMETER_Y_DEFAULT_DEVIATION, posDev);
            //log2File(locationString);

            //double[] position = Coordinates.convertLongLatToMeters(0,0, x, y);
            m_EKF = new GPSAccEKF(
                    Utils.USE_GPS_SPEED, //todo move to settings
                    Coordinates.longitudeToMeters(x),
                    Coordinates.latitudeToMeters(y),
                    xVel,
                    yVel,
                    Utils.ACCELEROMETER_X_DEFAULT_DEVIATION,
                    Utils.ACCELEROMETER_Y_DEFAULT_DEVIATION,
                    posDev,
                    timeStamp,
                    Utils.DEFAULT_VEL_FACTOR,
                    Utils.DEFAULT_POS_FACTOR,
                    Utils.USE_ZUPT);

            Log.i(TAG, String.format("location: lon:%f, lat:%f", Coordinates.longitudeToMeters(x), Coordinates.latitudeToMeters(y)));
            return;
        }

        SensorGpsDataItem sdi = new SensorGpsDataItem(
                timeStamp, loc.getLatitude(), loc.getLongitude(), loc.getAltitude(),
                SensorGpsDataItem.NOT_INITIALIZED,
                SensorGpsDataItem.NOT_INITIALIZED,
                SensorGpsDataItem.NOT_INITIALIZED,
                loc.getSpeed(),
                loc.getBearing(),
                loc.getAccuracy(),
                velErr,
                m_magneticDeclination,
                true);
        m_sensorDataQueue.add(sdi);
        //String strLog = String.format("GPSINFO  | [%f, %f]", sdi.getSpeed(), velErr);

        //log2File(strLog);


    }


    public Location getLastLocation(){
        return m_lastLocation;
    }

    //When stop EKF, stop function should be called.
    public void stop(){
        if(m_eventLoopTask != null)
        {
            m_eventLoopTask.needTerminate = true;
            m_eventLoopTask.cancel(true);
            m_eventLoopTask = null;
        }

        m_sensorDataQueue.clear();

        hasInitGPSLocation = false;


    }

    public void resume(){
        m_eventLoopTask = new SensorDataEventLoopTask(Utils.ThREAD_SLEEP_TIME, Owner);
        m_eventLoopTask.needTerminate = false;
        m_eventLoopTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }
    class SensorDataEventLoopTask extends AsyncTask {
        boolean needTerminate = false;
        long deltaTMs;
        LocationNotifier owner;

        private static final String TAG = "SensorDataEventLoopTask";


        public SensorDataEventLoopTask(long deltaTMs, LocationNotifier owner){
            this.deltaTMs = deltaTMs;
            this.owner = owner;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            while (!needTerminate) {
                try {
                    Thread.sleep(deltaTMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue; //bad
                }

                SensorGpsDataItem sdi;
                double lastTimeStamp = 0.0;
                while ((sdi = m_sensorDataQueue.poll()) != null) {
                    if (sdi.getTimestamp() < lastTimeStamp) {
                        continue;
                    }
                    lastTimeStamp = sdi.getTimestamp();

                    //warning!!!
                    if (sdi.getGpsLat() == SensorGpsDataItem.NOT_INITIALIZED) {
                        handlePredict(sdi);
                    } else {
                        handleUpdate(sdi);
                        Location loc = locationAfterUpdateStep(sdi);
                        publishProgress(loc);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            onLocationChangedImp((Location) values[0]);
        }

        void onLocationChangedImp(Location location) {

            m_lastLocation = location;
            owner.getLocationChanged(m_lastLocation);

        }




        private void handlePredict(SensorGpsDataItem sdi) {
            /*
            log2File("%d%d KalmanPredict : accX=%f, accY=%f",
                    Utils.LogMessageType.KALMAN_PREDICT.ordinal(),
                    (long)sdi.getTimestamp(),
                    sdi.getAbsEastAcc(),
                    sdi.getAbsNorthAcc());
                    */

            String courseLogStr = String.format("acc | [%f, %f]", sdi.getAbsEastAcc(), sdi.getAbsNorthAcc());
            Log.i(TAG, courseLogStr);
            m_EKF.predict(sdi.getTimestamp(), sdi.getAbsEastAcc(), sdi.getAbsNorthAcc(), sdi.getZUPTstatus());
        }

        private void handleUpdate(SensorGpsDataItem sdi) {
            double xVel = sdi.getSpeed() * Math.cos(sdi.getCourse());
            double yVel = sdi.getSpeed() * Math.sin(sdi.getCourse());
            /*log2File("%d%d KalmanUpdate : pos lon=%f, lat=%f, xVel=%f, yVel=%f, posErr=%f, velErr=%f",
                    Utils.LogMessageType.KALMAN_UPDATE.ordinal(),
                    (long)sdi.getTimestamp(),
                    sdi.getGpsLon(),
                    sdi.getGpsLat(),
                    xVel,
                    yVel,
                    sdi.getPosErr(),
                    sdi.getVelErr()
            );*/

            //double[] position = Coordinates.convertLongLatToMeters(0,0, sdi.getGpsLon(), sdi.getGpsLat());

            m_EKF.update(
                    sdi.getTimestamp(),
                    Coordinates.longitudeToMeters(sdi.getGpsLon()),
                    Coordinates.latitudeToMeters(sdi.getGpsLat()),
                    xVel,
                    yVel,
                    sdi.getPosErr(),
                    sdi.getVelErr()
            );

            String logGPSData = String.format("GPSRAWDATA | [%f, %f, %f, %f]", Coordinates.longitudeToMeters(sdi.getGpsLon()),
                    Coordinates.latitudeToMeters(sdi.getGpsLat()), m_EKF.getCurrentX(), m_EKF.getCurrentY());
            Log.d(TAG, logGPSData);

        }


        private Location locationAfterUpdateStep(SensorGpsDataItem sdi) {
            double xVel, yVel;
            Location loc = new Location(TAG);
            GeoPoint pp = Coordinates.metersToGeoPoint(m_EKF.getCurrentX(),
                    m_EKF.getCurrentY());
            loc.setLatitude(pp.Latitude);
            loc.setLongitude(pp.Longitude);
            loc.setAltitude(sdi.getGpsAlt());
            xVel = m_EKF.getCurrentXVel();
            yVel = m_EKF.getCurrentYVel();
            double speed = Math.sqrt(xVel*xVel + yVel*yVel); //scalar speed without bearing
            loc.setBearing((float)sdi.getCourse());
            loc.setSpeed((float) speed);
            loc.setTime(System.currentTimeMillis());
            loc.setElapsedRealtimeNanos(System.nanoTime());
            loc.setAccuracy((float) sdi.getPosErr());

            return loc;
        }
    }


}