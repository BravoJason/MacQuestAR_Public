package com.mcmaster.wiser.idyll.collection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.google.gson.Gson;
import com.mcmaster.wiser.idyll.connection.ServerUtils;
import com.mcmaster.wiser.idyll.detection.ActivityRecognition.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by steve on 2017-07-14.
 */

public class DataCollectionManager implements SensorEventListener {

    private static final String TAG = "DataCollectionManager";

    private static final String DATA_PATH = FileUtils.getSDCardPath() + "/MacQuestData/SensorsWiFiData/";

    /**
     * Whether data is collecting
     */
    private boolean collecting = false;

    /**
     * How long does the collection last. (Unit: ms)
     */
    private static final int DATA_COLLECTION_TIME = 20000;

    // Sensors
    private Context context;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Sensor magFieldSensor;
    private Sensor lightSensor;
    // WIFI
    private WifiScanReceiver wifiScanReceiver;
    private WifiManager wifiManager;
    public List<ScanResult> scanResults = null;
    public ArrayList<HashMap<Integer, Integer>> dataRssi = new ArrayList<HashMap<Integer, Integer>>();
    public HashMap<String, Integer> dataBssid = new HashMap<String, Integer>();
    public ArrayList<String> dataWifiNames = new ArrayList<String>();
    public int dataCount = 0;
    public ArrayList<Long> measuretime = new ArrayList<Long>();
    WifiManager.WifiLock wifilock;

    // Data for storage
    private SensorsWiFiData sensorsWiFiData;

    public DataCollectionManager(Context context) {
        this.context = context;
        setupSensors();
        setupWiFi();
    }

    public void startCollectData(String buildingID, String floorLevel) {
        startCollectData(buildingID, floorLevel, "null");
    }

    public void startCollectData(String buildingID, String floorLevel, String entranceId) {
        if (collecting) {
            return;
        }
        Log.d(TAG, "startCollectData...");
        collecting = true;
        // Sensors
        sensorsWiFiData = new SensorsWiFiData(buildingID, floorLevel, entranceId);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        // WiFi
        wifiManager.startScan();
        context.registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(DATA_COLLECTION_TIME);
                } catch (InterruptedException e) {
                    Log.e(TAG, "DataCollectionTimer", e);
                }
                stopCollectData();
            }
        }, "DataCollectionTimer").start();
    }

    private void stopCollectData() {
        try {
            if (sensorManager == null || context == null || wifiScanReceiver == null) {
                return;
            }
            Log.d(TAG, "stopCollectData...");
            // Sensors
            sensorManager.unregisterListener(this);
            // WIFI
            context.unregisterReceiver(wifiScanReceiver);
        } catch (Exception e) {
            Log.e(TAG, "stopCollectData error!", e);
        }

        // Save data file
        saveDataFile();
        // Reset data
        sensorsWiFiData = null;
        collecting = false;
    }

    private void saveDataFile() {
        Gson gson = new Gson();
        if (sensorsWiFiData == null) {
            return;
        }
        SensorsWiFiDataDAO sensorsWiFiDataDAO = new SensorsWiFiDataDAO(sensorsWiFiData);
        String jsonData = gson.toJson(sensorsWiFiDataDAO);
        String fileName = DATA_PATH + sensorsWiFiData.buildingId + "_" + sensorsWiFiData.floorLevel +
                "_" + sensorsWiFiData.entranceId + "_" + System.currentTimeMillis() + ".json";
        Log.d(TAG, "save data to file:" + fileName);
        try {
            File dataFile = FileUtils.createFileIfNotExits(fileName);
            if (dataFile == null) {
                return;
            }
            FileWriter fileWriter = new FileWriter(dataFile);
            fileWriter.write(jsonData);
            fileWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "saveDataFile", e);
        }
    }

    private void setupSensors() {
        if(context != null){
            sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            magFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }else
        {
            Log.d(TAG, "SetupSensors, Context is null.");
        }

    }

    private void setupWiFi() {
        if(context != null)
        {
            wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiScanReceiver = new WifiScanReceiver();
            setFrequencyBand2Hz(true, wifiManager);
            Log.d(TAG, "setupWiFi getFrequencyBand: " + getFrequencyBand(wifiManager));
        }else
        {
            Log.d(TAG, "setupWiFi, context is null");
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT: {
                LightSensorData lightSensorData = new LightSensorData();
                lightSensorData.light = event.values[0];
                lightSensorData.timestamp = System.currentTimeMillis();
                if (sensorsWiFiData != null && sensorsWiFiData.lightSensorDatas != null) {
                    sensorsWiFiData.lightSensorDatas.add(lightSensorData);
                }
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD: {
                MagneticSensorData magneticSensorData = new MagneticSensorData();
                magneticSensorData.x = event.values[0];
                magneticSensorData.y = event.values[1];
                magneticSensorData.z = event.values[2];
                magneticSensorData.timestamp = System.currentTimeMillis();
                if (sensorsWiFiData != null && sensorsWiFiData.magneticSensorDatas != null) {
                    sensorsWiFiData.magneticSensorDatas.add(magneticSensorData);
                }
                break;
            }
            case Sensor.TYPE_PRESSURE: {
                PressureSensorData pressureSensorData = new PressureSensorData();
                pressureSensorData.pressure = event.values[0];
                pressureSensorData.timestamp = System.currentTimeMillis();
                if (sensorsWiFiData != null && sensorsWiFiData.pressureSensorDatas != null) {
                    sensorsWiFiData.pressureSensorDatas.add(pressureSensorData);
                }
                break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

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
            e.printStackTrace();
        }
    }

    public String getFrequencyBand(WifiManager mWifiManager) {
        int band; //WIFI_FREQUENCY_BAND_AUTO = 0,  WIFI_FREQUENCY_BAND_2GHZ = 2
        String sband = "WIFI_FREQUENCY_BAND_AUTO";
        try {
            Field field = Class.forName(WifiManager.class.getName())
                    .getDeclaredField("mService");
            field.setAccessible(true);
            Object obj = field.get(mWifiManager);
            Class myClass = Class.forName(obj.getClass().getName());

            Method method = myClass.getDeclaredMethod("getFrequencyBand");
            method.setAccessible(true);

            band = (Integer) method.invoke(obj);

            if (band == 0) {
                sband = "WIFI_FREQUENCY_BAND_AUTO";
            } else if (band == 1) {
                sband = "WIFI_FREQUENCY_BAND_5GHZ";
            } else if (band == 2) {
                sband = "WIFI_FREQUENCY_BAND_2GHZ";
            } else {
                sband = "WIFI_FREQUENCY_BAND_Failure";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sband;
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (wifiManager == null) {
                    return;
                }
                scanResults = wifiManager.getScanResults();
                WiFiData wiFiData = new WiFiData();
                wiFiData.timestamp = System.currentTimeMillis();
                wiFiData.scanResults = scanResults;
                if (sensorsWiFiData != null && sensorsWiFiData.wiFiDatas != null) {
                    sensorsWiFiData.wiFiDatas.add(wiFiData);
                }
            } catch (SecurityException e) {
                Log.e(TAG, "WifiScanReceiver SecurityException", e);
            }
        }
    }

    public static void uploadAndDeleteData() {
        // 1. Read data files.
        File directory = new File(DATA_PATH);
        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            return;
        } else {
            for (File file : files) {
                String jsonData = null;
                try {
                    jsonData = FileUtils.getStringFromFile(file);
                } catch (Exception e) {
                    Log.d(TAG, "", e);
                }
                // 2. Upload data.
                boolean success = uploadToServer(jsonData);
                // 3. Delete data file.
                if (success) {
                    FileUtils.deleteFile(file.getPath());
                }
            }
        }
    }

    private static boolean uploadToServer(String jsonData) {
        boolean result = false;
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(ServerUtils.UPLOAD_DATA_URL);
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setConnectTimeout(3000);//连接的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setReadTimeout(3000);//响应的超时时间
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据
            urlConnection.setRequestMethod("POST");//设置请求的方式
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型
            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
            //-------------使用字节流发送数据--------------
            //OutputStream out = urlConnection.getOutputStream();
            //BufferedOutputStream bos = new BufferedOutputStream(out);//缓冲字节流包装字节流
            //byte[] bytes = jsonstr.getBytes("UTF-8");//把字符串转化为字节数组
            //bos.write(bytes);//把这个字节数组的数据写入缓冲区中
            //bos.flush();//刷新缓冲区，发送数据
            //out.close();
            //bos.close();
            //------------字符流写入数据------------
            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            bw.write(jsonData);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            out.close();
            bw.close();//使用完关闭

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK
                    || urlConnection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {//得到服务端的返回码是否连接成功
                //------------字节流读取服务端返回的数据------------
                //InputStream in = urlConnection.getInputStream();//用输入流接收服务端返回的回应数据
                //BufferedInputStream bis = new BufferedInputStream(in);//高效缓冲流包装它，这里用的是字节流来读取数据的，当然也可以用字符流
                //byte[] b = new byte[1024];
                //int len = -1;
                //StringBuffer buffer = new StringBuffer();//用来接收数据的StringBuffer对象
                //while((len=bis.read(b))!=-1){
                //buffer.append(new String(b, 0, len));//把读取到的字节数组转化为字符串
                //}
                //in.close();
                //bis.close();
                //Log.d("zxy", buffer.toString());//{"json":true}
                //JSONObject rjson = new JSONObject(buffer.toString());//把返回来的json编码格式的字符串数据转化成json对象
                //------------字符流读取服务端返回的数据------------
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {//BufferedReader特有功能，一次读取一行数据
                    buffer.append(str);
                }
                in.close();
                br.close();
                Log.d(TAG, "Response result: " + buffer.toString());
                result = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "uploadAndDeleteData error!", e);
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
        return result;
    }

}
