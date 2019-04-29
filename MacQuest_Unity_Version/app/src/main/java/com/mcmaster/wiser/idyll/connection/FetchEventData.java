package com.mcmaster.wiser.idyll.connection;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.mcmaster.wiser.idyll.model.event.EventUtils;
import com.mcmaster.wiser.idyll.presenter.util.MapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by daniel on 2018-05-23.
 * <p>
 * Used for fetching JSON data from the server for AR related events / points of interest.
 */

public class FetchEventData extends AsyncTask<Void, Void, Void> {
    String data = "";
    ArrayList<ArrayList<String>> dataParsed = new ArrayList<>();
    ArrayList<String> singleParsed;

    String unParsedObject;
    ArrayList<String> unParsedData;


    public String mode = "sub-event";
    public String id;


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            //URL with the desired JSON file.
            URL url;
            mode = MapUtils.mode;
            id = MapUtils.idSearch;
            if (mode == "sub-event") {
                //sub event URL.
                //url = new URL("http://macquest2.cas.mcmaster.ca/api/subevents/");
                url = new URL(EventUtils.EVENT_SUBEVENT_URL);
            } else {
                //parent event URL.
                //url = new URL("http://macquest2.cas.mcmaster.ca/api/parentevents/");
                url = new URL(EventUtils.EVENT_PARENTEVENT_URL);
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(1000);

            String userCredentials = "access-name:wiserlab1835";
            String basicAuth = "Basic " + new String(Base64.encodeToString(userCredentials.getBytes(), Base64.NO_WRAP));
            httpURLConnection.setRequestProperty("Authorization", basicAuth);

            int code = httpURLConnection.getResponseCode();
            Log.d("Code", String.valueOf(code));


            //String basicAuth = "Basic " + new String(new Base64().encode(url.getUserInfo().getBytes()));
            //httpURLConnection.setRequestProperty("Authorization", "Basic " + basicAuth);

            InputStream inputStream = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";

            StringBuilder stringBuilder = new StringBuilder();

            while (line != null) {
                line = bufferedReader.readLine();
                stringBuilder.append(line + "\n");
            }

            data = stringBuilder.toString();

            if (mode.equals("sub-event")) {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (id != null && id.equals(jsonObject.getString("parentEvent_id"))) {
                        singleParsed = new ArrayList<>();
                        //0
                        singleParsed.add(jsonObject.getString("id"));
                        //1
                        singleParsed.add(EventUtils.EVENT_SUBEVENT_URL + jsonObject.getString("id") + "/");
                        //2
                        singleParsed.add(jsonObject.getString("parentEvent_id"));
                        //3
                        singleParsed.add(jsonObject.getString("name"));
                        //4
                        singleParsed.add(jsonObject.getString("floor"));
                        //5
                        singleParsed.add(jsonObject.getString("location_lng"));
                        //6
                        singleParsed.add(jsonObject.getString("location_lat"));
                        //7
                        singleParsed.add(jsonObject.getString("location_building"));
                        /*
                        //singleParsed.add(jsonObject.getString("description"));
                        //Get raw description, which include the urls.
                        //String rawDescription = jsonObject.getString("desc_show_in_app");
                        //index 0: url info. index1: rest of data
                        //Grab the event URL description.
                        String[] eventURLInfo = getWebURLFromString(rawDescription, new String[]{EventUtils.EVENT_WEB_URL_MARKER_BEGIN, EventUtils.EVENT_WEB_URL_MARKER_END});
                        //Grab the visit requirement action URL.
                        String[] visitURLInfo = getWebURLFromString(eventURLInfo[1], new String[]{EventUtils.EVENT_WEB_VISIT_URL_MARKR_BEGIN, EventUtils.EVENT_WEB_VISIT_URL_MARKER_END});
                        //Grab the visit requirement action title
                        String[] visitWindowContent = getWebURLFromString(visitURLInfo[0], new String[]{EventUtils.EVENT_WEB_VISIT_TITLE_BEGIN, EventUtils.EVENT_WEB_VISIT_TITLE_END});
                        */


                        //Add the pure event description into event array.
                        //8 Pure event Description.
                        singleParsed.add(jsonObject.getString("desc_show_in_website"));

                        //9 Start time
                        singleParsed.add(jsonObject.getString("start_time"));
                        //10 End time.
                        singleParsed.add(jsonObject.getString("end_time"));


                        //11 Description URL
                        /*
                        if(eventURLInfo[0] != null){
                            //Has description URL.
                            singleParsed.add(eventURLInfo[0]);
                        }else
                        {
                            singleParsed.add(EventUtils.EVENT_WEB_URL_NONE);
                        }*/

                        boolean hasDescriptionURL = jsonObject.getBoolean("desc_has_url");
                        if (hasDescriptionURL) {
                            singleParsed.add(jsonObject.getString("desc_url"));
                        } else {
                            singleParsed.add(EventUtils.EVENT_WEB_URL_NONE);
                        }

                        //12 Action Requirement URL.
                        /*
                        if(visitWindowContent[1] != null){
                            //Has visit action URL.
                            singleParsed.add(visitWindowContent[1]);
                        }else
                        {
                            singleParsed.add(EventUtils.EVENT_WEB_URL_NONE);
                        }*/
                        boolean hasVisitActionURL = jsonObject.getBoolean("action_has_url");
                        if (hasVisitActionURL) {
                            singleParsed.add(jsonObject.getString("action_url"));
                        } else {
                            singleParsed.add(EventUtils.EVENT_WEB_URL_NONE);
                        }

                        //13 Whether the event action requirement URL is shown.
                        singleParsed.add(EventUtils.EVENT_URL_NOT_SHOW);

                        //14 Whether the discription URL is shown.
                        singleParsed.add(EventUtils.EVENT_URL_NOT_SHOW);


                        //15 Action Requirement window Title
                        /*
                        if(visitWindowContent[0] == null){
                            singleParsed.add("Could you please do this action?");
                        }else
                        {
                            singleParsed.add(visitWindowContent[0]);
                        }
                        */

                        singleParsed.add(jsonObject.getString("action_title"));
                        //16 Room number
                        singleParsed.add(jsonObject.getString("room"));
                        //17 Has Action boolean variable.
                        boolean hasAction = jsonObject.getBoolean("action_has_action");
                        if(hasAction){
                            singleParsed.add(EventUtils.EVENT_ACTION_TRUE);
                            //18 EVENT_OBJECT_INDEX_SUBEVENT_ACTION_FLAG
                            singleParsed.add(EventUtils.EVENT_ACTION_TRUE);
                        }else{
                            singleParsed.add(EventUtils.EVENT_ACTION_FALSE);
                            //18 EVENT_OBJECT_INDEX_SUBEVENT_ACTION_FLAG
                            singleParsed.add(EventUtils.EVENT_ACTION_FALSE);
                        }

                        //19 Action Desc.
                        singleParsed.add(jsonObject.getString("action_desc"));



                        dataParsed.add(singleParsed);

                    }

                }
                MapUtils.arPoints = dataParsed;
            } else if (mode.equals("event")) {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    singleParsed = new ArrayList<>();
                    singleParsed.add(jsonObject.getString("id"));
                    singleParsed.add(EventUtils.EVENT_PARENTEVENT_URL + jsonObject.getString("id") + "/");
                    singleParsed.add(jsonObject.getString("name"));
                    singleParsed.add(jsonObject.getString("owner_full_name"));
                    singleParsed.add(jsonObject.getString("authentication"));
                    singleParsed.add(jsonObject.getString("private_password"));
                    singleParsed.add(jsonObject.getString("description"));

                    dataParsed.add(singleParsed);
                }
                MapUtils.arEvents = dataParsed;
                MapUtils.arFound = true;
            }

            httpURLConnection.disconnect();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //MapUtils.arPoints = dataParsed;
    }


    /*
    Input value1: any string.
    Input value2: marker array. {"<w>", "</w>"}
    Return value: String array. String[0]: Current URL Address. String[1]: Remove URL string.
     */
    private static String[] getWebURLFromString(String str, String[] markers) {

        boolean hasWebURL = false;
        String url = null;
        if (str == null) {
            return new String[]{null, null};
        }
        String str_ = str;
        String strLowerCase = str.toLowerCase();
        //Search the beginning of the web marker.
        //Format: <w>Http://www.google.ca/</w>This is the description.
        int webMarkerBegin = strLowerCase.indexOf(markers[0]);
        int webMarkerEnd = strLowerCase.indexOf(markers[1]);
        if (webMarkerBegin != -1 && webMarkerEnd != -1) {
            //Find the web URL location in the description.
            url = strLowerCase.substring(webMarkerBegin + markers[0].length(), webMarkerEnd);
            url = url.trim();
            hasWebURL = true;

        }

        if (hasWebURL) {
            //Remove web URL from description.
            String pureDescription = str_.substring(0, webMarkerBegin);
            pureDescription += str_.substring(webMarkerEnd + markers[1].length(), str_.length());
            str_ = pureDescription;
        }

        return new String[]{url, str_};

    }


}



