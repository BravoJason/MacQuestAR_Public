package com.mcmaster.wiser.idyll.presenter.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.Contracts;
import com.mcmaster.wiser.idyll.model.bus.BusStop;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Class to store helper functions for database queries
 * Created by wiserlab on 6/26/17.
 */

public class BusStopDataUtils {

    private static final String TAG = BusStopDataUtils.class.getSimpleName();

    public static Date getArrivalTime(String nextBusTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date arrivalTime = new Date();
        try {
            arrivalTime = df.parse(nextBusTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return arrivalTime;
    }

    public static int getDifference(Date arrivalTime, Date currentTime) {
        long difference = arrivalTime.getTime() - currentTime.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        return min;
    }

    public static String getCurrentHourTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        return df.format(c.getTime());
    }

    public static Date getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String temp = df.format(c.getTime());
        Date currentTime = new Date();
        try {
            currentTime = df.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentTime;
    }

    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY:
            case Calendar.TUESDAY:
            case Calendar.WEDNESDAY:
            case Calendar.THURSDAY:
            case Calendar.FRIDAY:
                return 1;
            case Calendar.SATURDAY:
                return 2;
            case Calendar.SUNDAY:
                return 3;
        }
        return day;
    }

    public static ArrayList<BusStop> fetchData(String stopFilter, Context context) {

        String stopID = getStopID(context, stopFilter);


        String formattedDate = getCurrentHourTime();
        int tempFormatted = Integer.parseInt(formattedDate);
        String fixedFormatted;
        if (tempFormatted < 10) {
            fixedFormatted = " " + Integer.toString(tempFormatted);
        } else {
            fixedFormatted = Integer.toString(tempFormatted);
        }

        int tempDate = Integer.parseInt(formattedDate);
        tempDate += 1;
        String timePlus;
        if (tempDate < 10) {
            timePlus = " " + Integer.toString(tempDate);
        } else {
            timePlus = Integer.toString(tempDate);
        }

        String[] projection = {
                Contracts.BusDataEntry.COLUMN_TRIP_ID,
                Contracts.BusDataEntry.COLUMN_ARRIVAL_TIME,
                Contracts.BusDataEntry.COLUMN_STOP_ID
        };

        String selection = Contracts.BusDataEntry.COLUMN_STOP_ID + " = ?"
                + " AND (" + Contracts.BusDataEntry.COLUMN_ARRIVAL_TIME
                + " LIKE " + "'" + fixedFormatted + "%'"
                + " OR " + Contracts.BusDataEntry.COLUMN_ARRIVAL_TIME
                + " LIKE " + "'" + timePlus + "%')";

        String[] selectionArgs = {stopID};

        Cursor cursor = context.getContentResolver().query(Contracts.BusDataEntry.BUS_STOP_TIMES_URI, projection, selection, selectionArgs, null);

        int tripIdNum = cursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_TRIP_ID);
        int arrivalNum = cursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_ARRIVAL_TIME);
        int stopIdNum = cursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_STOP_ID);

        ArrayList<String> tripIdList = new ArrayList<>();
        ArrayList<String> arrivalTimeList = new ArrayList<>();
        ArrayList<String> stopIdList = new ArrayList<>();

        while (cursor.moveToNext()) {
            tripIdList.add(cursor.getString(tripIdNum));
            arrivalTimeList.add(cursor.getString(arrivalNum));
            stopIdList.add(cursor.getString(stopIdNum));
        }
        cursor.close();

        ArrayList<BusStop> busRouteItemArrayList = new ArrayList<>();

        Log.v(TAG, "SIZE: " + tripIdList.size());

        for (int i = 0; i < tripIdList.size(); i++) {

            String nextBusTime = arrivalTimeList.get(i);

            Date arrivalTime = getArrivalTime(nextBusTime);
            Date currentTime = getCurrentTime();
            int minDifference = getDifference(arrivalTime, currentTime);

            if (minDifference > 0) { //the bus is coming in the future

                int desiredStopId = Integer.parseInt(stopIdList.get(i));

                String[] stopProjection = {
                        Contracts.BusDataEntry.COLUMN_STOP_NAME,
                };

                String stopSelection = Contracts.BusDataEntry.COLUMN_STOP_ID + " = ?";

                String[] stopSelectionArgs = {Integer.toString(desiredStopId)};

                Cursor stopCursor = context.getContentResolver().query(Contracts.BusDataEntry.BUS_STOP_URI, stopProjection, stopSelection, stopSelectionArgs, null);

                int stopNum = stopCursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_STOP_NAME);
                String stopName = "";
                while (stopCursor.moveToNext()) {
                    stopName = stopCursor.getString(stopNum);
                }
                stopCursor.close();

                int desiredTripId = Integer.parseInt(tripIdList.get(i));
                String[] tripProjection = {
                        Contracts.BusDataEntry.COLUMN_ROUTE_ID,
                        Contracts.BusDataEntry.COLUMN_SERVICE_ID
                };

                String tripSelection = Contracts.BusDataEntry.COLUMN_TRIP_ID + " = ?"
                        + " AND " + Contracts.BusDataEntry.COLUMN_SERVICE_ID + " = ?";

                String[] tripSelectionArgs = {Integer.toString(desiredTripId), Integer.toString(getDayOfWeek())};

                Cursor tripCursor = context.getContentResolver().query(Contracts.BusDataEntry.BUS_TRIPS_URI, tripProjection, tripSelection, tripSelectionArgs, null);

                int desiredRouteIdNum = tripCursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_ROUTE_ID);
                int serviceIdNum = tripCursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_SERVICE_ID);

                String desiredRouteId = "";
                String serviceID = "5";

                while (tripCursor.moveToNext()) {
                    desiredRouteId = tripCursor.getString(desiredRouteIdNum);
                    serviceID = tripCursor.getString(serviceIdNum);
                }
                tripCursor.close();

                // Checks if the serviceID ( ID that describes the routes for the day, ie. 1= weekdays , 2 = saturday) equals the current dayofweekID
                if (Integer.parseInt(serviceID) == getDayOfWeek()) {

                    String[] routeProjection = {
                            Contracts.BusDataEntry.COLUMN_ROUTE_SHORT_NAME,
                            Contracts.BusDataEntry.COLUMN_ROUTE_LONG_NAME
                    };

                    String routeSelection = Contracts.BusDataEntry.COLUMN_ROUTE_ID + " = ?";
                    String[] routeSelectionArgs = {desiredRouteId};

                    Cursor routeCursor = context.getContentResolver().query(Contracts.BusDataEntry.BUS_ROUTE_URI, routeProjection, routeSelection, routeSelectionArgs, null);

                    int routeShortNameIndex = routeCursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_ROUTE_SHORT_NAME);
                    int routeLongNameIndex = routeCursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_ROUTE_LONG_NAME);

                    String routeShortName = "";
                    String routeLongName = "";

                    while (routeCursor.moveToNext()) {
                        routeShortName = routeCursor.getString(routeShortNameIndex);
                        routeLongName = routeCursor.getString(routeLongNameIndex);
                    }
                    routeCursor.close();

                    String finalArrivalTime = Integer.toString(minDifference);

                    busRouteItemArrayList.add(new BusStop(stopName, routeShortName, routeLongName, finalArrivalTime));
                }
            }
        }

        // Sort bus stop times to show the next earliest arrival first
        busRouteItemArrayList = busStopArrivalTimeBubbleSort(busRouteItemArrayList);

        return busRouteItemArrayList;

    }

    private static ArrayList<BusStop> busStopArrivalTimeBubbleSort(ArrayList<BusStop> busRouteItemArrayList) { //Sort the array by closest arrival time
        for (int i = 0; i < busRouteItemArrayList.size(); i++) {
            for (int j = 1; j < busRouteItemArrayList.size() - i; j++) {
                if (Integer.parseInt(busRouteItemArrayList.get(j - 1).getNextBusArrival()) > Integer.parseInt(busRouteItemArrayList.get(j).getNextBusArrival())) {
                    swapBusStops(busRouteItemArrayList.get(j - 1), busRouteItemArrayList.get(j));
                }
            }
        }
        return busRouteItemArrayList;
    }

    private static void swapBusStops(BusStop busStop1, BusStop busStop2) {
        String temp = busStop1.getStopName();
        busStop1.setStopName(busStop2.getStopName());
        busStop2.setStopName(temp);

        temp = busStop1.getRouteShortName();
        busStop1.setRouteShortName(busStop2.getRouteShortName());
        busStop2.setRouteShortName(temp);

        temp = busStop1.getRouteLongName();
        busStop1.setRouteLongName(busStop2.getRouteLongName());
        busStop2.setRouteLongName(temp);

        temp = busStop1.getNextBusArrival();
        busStop1.setNextBusArrival(busStop2.getNextBusArrival());
        busStop2.setNextBusArrival(temp);
    }


    // Used to find the ID for a bus stop from it's name
    public static String getStopID(Context context, String stopName) {

        String[] projection = {
                Contracts.BusDataEntry.COLUMN_STOP_ID,
        };

        String selection = Contracts.BusDataEntry.COLUMN_STOP_NAME + " = ? ";
        String[] selectionArgs = {stopName};
        Cursor cursor = context.getContentResolver().query(Contracts.BusDataEntry.BUS_STOP_URI, projection, selection, selectionArgs, null);
        int stopCodeNum = cursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_STOP_ID);

        String stopID = "";
        while (cursor.moveToNext()) {
            stopID = cursor.getString(stopCodeNum);
        }
        cursor.close();

        return stopID;
    }

    public static ArrayList<String> CreateSpinnerSuggestionsList(Context context) { //TODO: POPULATE THE LIST WITH STOP NAMES
        ArrayList<String> stopNames = new ArrayList<>();

        String[] projection = {
                Contracts.BusDataEntry.COLUMN_STOP_NAME,
        };

        String selection = Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? "
                + " OR " + Contracts.BusDataEntry.COLUMN_STOP_ID + " = ? ";

        String[] selectionArgs = context.getResources().getStringArray(R.array.bus_stop_id_list);
        Cursor cursor = context.getContentResolver().query(Contracts.BusDataEntry.BUS_STOP_URI, projection, selection, selectionArgs, null);
        int stopNameNum = cursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_STOP_NAME);

        while (cursor.moveToNext()) {
            stopNames.add(cursor.getString(stopNameNum));
        }
        cursor.close();

        return stopNames;
    }

    public static LatLng getLocationOfStop(String selectedStop, Context context) {
        LatLng stopLocation = new LatLng();

        String[] projection = {
                Contracts.BusDataEntry.COLUMN_STOP_LATITUDE,
                Contracts.BusDataEntry.COLUMN_STOP_LONGITUDE
        };

        String selection = Contracts.BusDataEntry.COLUMN_STOP_NAME + " = ? ";
        String[] selectionArgs = {selectedStop};
        Cursor cursor = context.getContentResolver().query(Contracts.BusDataEntry.BUS_STOP_URI, projection, selection, selectionArgs, null);

        int latitudeNum = cursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_STOP_LATITUDE);
        int longitudeNum = cursor.getColumnIndex(Contracts.BusDataEntry.COLUMN_STOP_LONGITUDE);

        while (cursor.moveToNext()) {
            stopLocation.setLatitude(cursor.getDouble(latitudeNum));
            stopLocation.setLongitude(cursor.getDouble(longitudeNum));
        }
        cursor.close();


        return stopLocation;
    }

    public static BusStop getBlankBusArray() {
        return new BusStop("No buses coming in the next hour!", null, null, null);
    }
}
