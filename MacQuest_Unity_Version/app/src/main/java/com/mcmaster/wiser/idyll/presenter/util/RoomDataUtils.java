package com.mcmaster.wiser.idyll.presenter.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.widget.Filter;
import android.widget.Toast;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.model.Contracts;
import com.mcmaster.wiser.idyll.model.building.history.BuildingHistoryDbHelper;
import com.mcmaster.wiser.idyll.model.room.Room;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collection of functions to help with displaying room database information
 * Created by Eric on 7/05/17.
 */

public class RoomDataUtils {

    private static final String TAG = RoomDataUtils.class.getSimpleName();

    private static ArrayList<Room> buildingFullList;

    public static ArrayList<Room> fetchRooms(Context context, String buildingName, int outId, String roomPrefix) {

        ArrayList<Room> roomArrayList = new ArrayList<>();

        String[] projection = {
                Contracts.RoomContractEntry.COLUMN_ROOM_ID,
                Contracts.RoomContractEntry.COLUMN_OUT_ID,
                Contracts.RoomContractEntry.COLUMN_NAME,
                Contracts.RoomContractEntry.COLUMN_UTILITY,
                Contracts.RoomContractEntry.COLUMN_FLOOR,
                Contracts.RoomContractEntry.COLUMN_LOCATION,
                Contracts.RoomContractEntry.COLUMN_BUILDING_NAME,
                Contracts.RoomContractEntry.COLUMN_ROUTINGPOINT,
                Contracts.RoomContractEntry.COLUMN_NEARESTSTAIRCASE
        };

        String selection = Contracts.RoomContractEntry.COLUMN_UTILITY + " IS NULL "
                + " OR " + Contracts.RoomContractEntry.COLUMN_UTILITY + " = ? "
                + " AND " + Contracts.RoomContractEntry.COLUMN_LOCATION + " != ?"
                + " AND " + Contracts.RoomContractEntry.COLUMN_OUT_ID + " == ?"
                + " AND " + Contracts.RoomContractEntry.COLUMN_NAME + " LIKE \"" + roomPrefix + "%\"";


        String selectionArgs[] = {"", "\\N", Integer.toString(outId)};

        Cursor cursor = context.getContentResolver().query(Contracts.RoomContractEntry.CONTENT_URI, projection, selection, selectionArgs, null);

//        Cursor cursor = sd.query(Contracts.RoomContractEntry.TABLE_NAME_ROOM, projection, selection, selectionArgs, null, null, null);

        int roomIdColumnNum = cursor.getColumnIndex(Contracts.RoomContractEntry.COLUMN_ROOM_ID);
        int outIdColumnNum = cursor.getColumnIndex(Contracts.RoomContractEntry.COLUMN_OUT_ID);
        int nameColumnNum = cursor.getColumnIndex(Contracts.RoomContractEntry.COLUMN_NAME);
        int floorColumnNum = cursor.getColumnIndex(Contracts.RoomContractEntry.COLUMN_FLOOR);
        int rawLocationColumnNum = cursor.getColumnIndex(Contracts.RoomContractEntry.COLUMN_LOCATION);
        int routingPointColumnNum = cursor.getColumnIndex(Contracts.RoomContractEntry.COLUMN_ROUTINGPOINT);
        int neareststairColumnNum=cursor.getColumnIndex(Contracts.RoomContractEntry.COLUMN_NEARESTSTAIRCASE);
        while (cursor.moveToNext()) {
            roomArrayList.add(new Room(
                    Integer.parseInt(cursor.getString(roomIdColumnNum)),
                    Integer.parseInt(cursor.getString(outIdColumnNum)),
                    cursor.getString(nameColumnNum),
                    Integer.parseInt(cursor.getString(floorColumnNum)),
                    cursor.getString(rawLocationColumnNum),
                    buildingName,
                    cursor.getString(routingPointColumnNum),
                    cursor.getString(neareststairColumnNum)
            ));

        }
        cursor.close();
        //  sd.close();
        // db.close();
        return roomArrayList;
    }

    public static void saveRoomEntry(Context context , Room selectedRoom) {

        BuildingHistoryDbHelper mDbHelper = new BuildingHistoryDbHelper(context);

        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME, selectedRoom.getBuildingName()+ " " + selectedRoom.getRoomNumber());
        values.put(Contracts.BuildingHistoryEntry.COLUMN_ROOM, selectedRoom.getRoomNumber());
        values.put(Contracts.BuildingHistoryEntry.COLUMN_OUT_ID, selectedRoom.getOutid());
        values.put(Contracts.BuildingHistoryEntry.COLUMN_LOCATION, selectedRoom.getRawLocation());

        long newRowId = sqLiteDatabase.insert(Contracts.BuildingHistoryEntry.TABLE_NAME, null, values);

        if (newRowId == -1) { // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(context, "Error with saving", Toast.LENGTH_SHORT).show();
        } else { // Otherwise, the insertion was successful
            Log.v(TAG, "Room search click saved into history database");
//            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(c, "History saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }

    }


    public interface OnFindRoomSuggestionsListener {
        void onResults(ArrayList<Room> results);
    }


    public static ArrayList<Room> getBuildingListFull(Context context) {
        ArrayList<Room> buildNameList = new ArrayList<>();
        String[] builsNamesAbbr = context.getResources().getStringArray(R.array.building_name_abbr_list);
        String[] builsNamesFull = context.getResources().getStringArray(R.array.building_name_full_list);
        ArrayList<String> building_name_list = new ArrayList<>(Arrays.asList(builsNamesFull));
        building_name_list.addAll(new ArrayList<>(Arrays.asList(builsNamesAbbr)));

        for (String buildingName : building_name_list)
            buildNameList.add(new Room(
                    0,
                    Integer.parseInt(buildingName.substring(buildingName.indexOf("@") + 1, buildingName.indexOf("$"))),
                    "",
                    1,
                    buildingName.substring(buildingName.indexOf("$") + 1, buildingName.length()),
                    buildingName.substring(0, buildingName.indexOf("@")),
                    "",
                    ""
            ));

        return buildNameList;
    }


    public static void findRoomSuggestions(final Context context, String query, final int limit, final OnFindRoomSuggestionsListener listener) {
        new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Room> suggestionList = new ArrayList<>();
                ArrayList<Room> distance0List = new ArrayList<>();
                ArrayList<Room> distance1List = new ArrayList<>();
                ArrayList<Room> distance2List = new ArrayList<>();

                if (buildingFullList == null) {
                    buildingFullList = getBuildingListFull(context);
                }
                if (constraint != null && constraint.length() != 0) {
                    if (!constraint.toString().contains(" ")) {
                        for (Room r : buildingFullList) {
                            int result = unlimitedCompare(r.getBuildingName().toUpperCase(), constraint.toString().toUpperCase());
                            //calculate distance
                            switch (result) {
                                case 1:
                                    distance1List.add(r);
                                    break;
                                case 2:
                                    distance2List.add(r);
                                    break;
                            }
                            //checks if contain
                            if (r.getBuildingName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                                distance0List.add(r);
                            }
                            if (limit != -1 && (distance1List.size() >= limit || distance2List.size() > limit)) {
                                break;
                            }
                        }
                        suggestionList.addAll(distance2List);
                        suggestionList.addAll(distance1List);
                        suggestionList.addAll(distance0List);


                    } else {
                        String input = constraint.toString().trim().toUpperCase();
                        String inputBuildingName = input.replaceAll("\\d", "").trim();
                        String[] splitedStr = input.split(" ");
                        String roomPrefix;
                        if (splitedStr.length > 1 &&(splitedStr[splitedStr.length - 1].startsWith("A") || splitedStr[splitedStr.length - 1].startsWith("L") ||
                                splitedStr[splitedStr.length - 1].startsWith("W") || splitedStr[splitedStr.length - 1].startsWith("B"))) {

                            roomPrefix = splitedStr[splitedStr.length - 1];
                            // ignore the name part for the char match part
                            inputBuildingName = input.substring(0, input.indexOf(roomPrefix, input.lastIndexOf(" ")) - 1);
                        } else {
                            roomPrefix = input.replaceAll("[^\\d.]", "").trim();
                        }

                        for (Room r : buildingFullList) {

                            if (r.getBuildingName().trim().toUpperCase().contains(inputBuildingName)) {
                                suggestionList = fetchRooms(context, r.getBuildingName(), r.getOutid(), roomPrefix);
                                break;
                            }
//                            if(input.matches(".*\\d+.*")){
//                                String roomPrefix = input.replaceAll("\\D+","");
//                            }


                        }


                    }
                }

                //Remove unneed results.
                if (suggestionList.size() > limit) {
                    List<Room> filteredroom = suggestionList.subList(suggestionList.size() - limit, suggestionList.size());
                    suggestionList = new ArrayList<Room>(filteredroom);
                }

                FilterResults results = new FilterResults();

                results.values = suggestionList;
                results.count = suggestionList.size();



                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (listener != null) {

                    listener.onResults((ArrayList<Room>) results.values);
                }
            }
        }.filter(query);
    }


    /**
     * <p>Find the Levenshtein distance between two Strings.</p>
     * <p>
     * <p>A higher score indicates a greater distance.</p>
     * <p>
     * <p>The previous implementation of the Levenshtein distance algorithm
     * was from <a href="https://web.archive.org/web/20120526085419/http://www.merriampark.com/ldjava.htm">
     * https://web.archive.org/web/20120526085419/http://www.merriampark.com/ldjava.htm</a></p>
     * <p>
     * <p>This implementation only need one single-dimensional arrays of length s.length() + 1</p>
     * <p>
     * <pre>
     * unlimitedCompare(null, *)             = IllegalArgumentException
     * unlimitedCompare(*, null)             = IllegalArgumentException
     * unlimitedCompare("","")               = 0
     * unlimitedCompare("","a")              = 1
     * unlimitedCompare("aaapppp", "")       = 7
     * unlimitedCompare("frog", "fog")       = 1
     * unlimitedCompare("fly", "ant")        = 3
     * unlimitedCompare("elephant", "hippo") = 7
     * unlimitedCompare("hippo", "elephant") = 7
     * unlimitedCompare("hippo", "zzzzzzzz") = 8
     * unlimitedCompare("hello", "hallo")    = 1
     * </pre>
     */
    private static int unlimitedCompare(CharSequence left, CharSequence right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
           This implementation use two variable to record the previous cost counts,
           So this implementation use less memory than previous impl.
         */

        int n = left.length(); // length of left
        int m = right.length(); // length of right

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            final CharSequence tmp = left;
            left = right;
            right = tmp;
            n = m;
            m = right.length();
        }

        int[] p = new int[n + 1];

        // indexes into strings left and right
        int i; // iterates through left
        int j; // iterates through right
        int upperLeft;
        int upper;

        char rightJ; // jth character of right
        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            upperLeft = p[0];
            rightJ = right.charAt(j - 1);
            p[0] = j;

            for (i = 1; i <= n; i++) {
                upper = p[i];
                cost = left.charAt(i - 1) == rightJ ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                p[i] = Math.min(Math.min(p[i - 1] + 1, p[i] + 1), upperLeft + cost);
                upperLeft = upper;
            }
        }

        return p[n];
    }


    public static LatLng getRoomLatLng(Room selectedRoom) {
        //TODO: if the room doesn't contain a latlng or a proper latlng this crashes.  add a try catch block
        String rawLocation = selectedRoom.getRawLocation();
        rawLocation = rawLocation.substring(6);
        String[] latlon = rawLocation.split(" ");
        Location roomLocation = new Location("");
        roomLocation.setLongitude(Double.parseDouble(latlon[0]));
        roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
        LatLng latLng = new LatLng(roomLocation.getLatitude(), roomLocation.getLongitude());

        return latLng;
    }

    public static Location getRoomRoutingPoints(Room selectedRoom) {
        //TODO: if the room doesn't contain a latlng or a proper latlng this crashes.  add a try catch block
        String rawLocation = selectedRoom.getroutingLocation();
        rawLocation = rawLocation.substring(6);
        String[] latlon = rawLocation.split(" ");
        Location roomLocation = new Location("");
        roomLocation.setLongitude(Double.parseDouble(latlon[0]));
        roomLocation.setLatitude(Double.parseDouble(latlon[1].substring(0, latlon[1].length() - 1)));
        return roomLocation;
    }

}
