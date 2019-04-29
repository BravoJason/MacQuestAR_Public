package com.mcmaster.wiser.idyll.presenter.util;

import android.content.Context;
import android.database.Cursor;
import android.widget.Filter;

import com.mcmaster.wiser.idyll.model.Contracts;
import com.mcmaster.wiser.idyll.model.building.Building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Collection of functions to help with displaying database information
 * Created by Eric on 6/14/17.
 */

public class BuildingDataUtils {

    private static ArrayList<Building> buildingFullList;

    public static ArrayList<Building> fetchRawHistory(Context context){
        ArrayList<Building> data = new ArrayList<>();

        String[] projection = {
                Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME,
                Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME,
                Contracts.BuildingHistoryEntry.COLUMN_ROOM,
                Contracts.BuildingHistoryEntry.COLUMN_OUT_ID,
                Contracts.BuildingHistoryEntry.COLUMN_LOCATION,
                Contracts.BuildingHistoryEntry.COLUMN_UTILITY,
                //  Contracts.BuildingHistoryEntry.COLUMN_UUID,
                Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS
        };

        Cursor cursor = context.getContentResolver().query(Contracts.BuildingHistoryEntry.CONTENT_URI, projection, null, null, null, null);


        int nameNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME);
        int shortNameNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME);
        int roomNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_ROOM);
        int outIdNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_OUT_ID);
        int locationNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_LOCATION);
        int utilityNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_UTILITY);
        //  int uuidNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_UUID);
        int syncNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS);


        while (cursor.moveToNext()){
            data.add(new Building(
                    cursor.getString(nameNum),
                    cursor.getString(shortNameNum),
                    cursor.getString(roomNum),
                    cursor.getString(outIdNum),
                    cursor.getString(locationNum),
                    cursor.getString(utilityNum),
                    //   cursor.getString(uuidNum),
                    cursor.getInt(syncNum)
            ));
        }
        cursor.close();
        return data;

    }

    public static ArrayList<Building> fetchHistory(Context context, int count) {
        ArrayList<Building> data = new ArrayList<>();
        String[] projection = {
                Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME,
                Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME,
                Contracts.BuildingHistoryEntry.COLUMN_OUT_ID,
                Contracts.BuildingHistoryEntry.COLUMN_LOCATION
        };
        Cursor cursor = context.getContentResolver().query(Contracts.BuildingHistoryEntry.CONTENT_URI, projection, null, null, null, null);

        int nameNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME);
        int shortNameNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME);
        int outIdNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_OUT_ID);
        int locationNum = cursor.getColumnIndex(Contracts.BuildingHistoryEntry.COLUMN_LOCATION);

        //reverse the cursor so that we look at most recent history to least recent
        cursor.moveToLast();
        cursor.moveToNext();
        while (cursor.moveToPrevious()) {
            data.add(new Building(cursor.getString(nameNum),
                    cursor.getString(shortNameNum),
                    cursor.getString(outIdNum),
                    cursor.getString(locationNum)));
            if (data.size() == count) {
                break;
            }
        }

        for (Building b : data) {
            b.setIsHistory(true);
        }
        Collections.reverse(data);
        cursor.close();
        return data;
    }

    public static void deleteHistory(Context context) {
        context.getContentResolver().delete(Contracts.BuildingHistoryEntry.CONTENT_URI, null,null);
    }

    public static ArrayList<Building> getBuildingsFromCursor(Cursor data) {
        ArrayList<Building> buildingArrayList = new ArrayList<>();

        int columnNameNum = data.getColumnIndex(Contracts.BuildingContractEntry.COLUMN_NAME);
        int columnShortNameNum = data.getColumnIndex(Contracts.BuildingContractEntry.COLUMN_SHORTNAME);
        int columnOutIdNum = data.getColumnIndex(Contracts.BuildingContractEntry.COLUMN_OUT_ID);
        int columnLocationNum = data.getColumnIndex(Contracts.BuildingContractEntry.COLUMN_LOCATION);

        while (data.moveToNext()) {
            buildingArrayList.add(new Building(data.getString(columnNameNum),
                    data.getString(columnShortNameNum),
                    data.getString(columnOutIdNum),
                    data.getString(columnLocationNum)));
        }
        data.close();
        return buildingArrayList;
    }

    public interface OnFindBuildingsListener {
        void onResults(ArrayList<Building> results);
    }

    public interface OnFindBuildingSuggestionsListener {
        void onResults(ArrayList<Building> results);
    }

    public static void findBuildingSuggestions(final ArrayList<Building> buildingData, final Context context, String query, final int limit, final OnFindBuildingSuggestionsListener listener) {
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                buildingFullList = buildingData;
                ArrayList<Building> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (Building b : buildingFullList) {
                        if (b.getBody().toUpperCase().contains(constraint.toString().toUpperCase())
                                || b.getShortName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                            suggestionList.add(b);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }
                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<Building>() {
                    @Override
                    public int compare(Building o1, Building o2) {
                        return o1.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (listener != null) {
                    listener.onResults((ArrayList<Building>) results.values);
                }
            }
        }.filter(query);
    }


}
