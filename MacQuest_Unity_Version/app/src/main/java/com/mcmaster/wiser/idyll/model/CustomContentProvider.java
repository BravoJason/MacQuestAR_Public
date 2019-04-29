package com.mcmaster.wiser.idyll.model;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.mcmaster.wiser.idyll.model.building.history.BuildingHistoryDbHelper;

import java.io.File;


/**
 * Created by wiserlab on 7/18/17.
 */

public class CustomContentProvider extends ContentProvider {

    private final static String TAG = CustomContentProvider.class.getSimpleName();

    private DbHelper buildingDbHelper;
    private BusDbHelper busDbHelper;
    private BuildingHistoryDbHelper historyDbHelper;


    private static final int BUILDING_HISTORY = 99;
    private static final int BUILDINGS = 100;
    private static final int BUS_STOPS = 101;
    private static final int BUS_STOP_TIMES = 102;
    private static final int BUS_TRIPS = 103;
    private static final int BUS_ROUTES = 104;
    private static final int ROOMS = 105;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{
        sUriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.PATH_BUILDING_HISTORY, BUILDING_HISTORY);
        sUriMatcher.addURI(Contracts.CONTENT_AUTHORITY,Contracts.PATH_BUILDING, BUILDINGS );
        sUriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.PATH_BUS_STOP_TIMES, BUS_STOP_TIMES);
        sUriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.PATH_BUS_STOPS, BUS_STOPS);
        sUriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.PATH_BUS_TRIPS, BUS_TRIPS);
        sUriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.PATH_BUS_ROUTES, BUS_ROUTES);
        sUriMatcher.addURI(Contracts.CONTENT_AUTHORITY, Contracts.PATH_ROOM, ROOMS);
    }

    private static final String LOG = CustomContentProvider.class.getSimpleName();

    @Override
    public boolean onCreate() {
        buildingDbHelper = new DbHelper(getContext());
        busDbHelper = new BusDbHelper(getContext());
        historyDbHelper = new BuildingHistoryDbHelper(getContext());

        try {
            buildingDbHelper.createDataBase();
            buildingDbHelper.openDataBase();
            busDbHelper.createDataBase();
            busDbHelper.openDataBase();

            File myFile = getContext().getDatabasePath(DbHelper.DB_NAME);
            long lastModified = myFile.lastModified();
            Log.v(TAG, "Last modified: " + lastModified);




        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase sd;

        Cursor cursor;

        Log.v(TAG, "URI: " + uri);

        int match = sUriMatcher.match(uri);

        Log.v(TAG, "MATCH INT: " + match);
        switch (match){
            case BUILDING_HISTORY:
                sd = historyDbHelper.getReadableDatabase();
                cursor = sd.query(Contracts.BuildingHistoryEntry.TABLE_NAME,
                        projection, selection , selectionArgs, null, null, null, null);
                break;
            case BUILDINGS:
                sd = buildingDbHelper.getReadableDatabase();
                cursor = sd.query(Contracts.BuildingContractEntry.TABLE_NAME_OUTLINE,
                        projection, selection, selectionArgs, null, null, null);
                break;
            case BUS_STOPS:
                sd = busDbHelper.getReadableDatabase();
                cursor = sd.query(Contracts.BusDataEntry.TABLE_NAME_STOPS,
                        projection, selection, selectionArgs, null, null, null, null);
                break;
            case BUS_ROUTES:
                sd = busDbHelper.getReadableDatabase();
                cursor = sd.query(Contracts.BusDataEntry.TABLE_NAME_ROUTES,
                        projection,selection,selectionArgs, null,null,null,null);
                break;
            case BUS_STOP_TIMES:
                sd = busDbHelper.getReadableDatabase();
                cursor = sd.query(Contracts.BusDataEntry.TABLE_NAME_STOP_TIMES,
                        projection,selection,selectionArgs, null,null,null,null);
                break;
            case BUS_TRIPS:
                sd = busDbHelper.getReadableDatabase();
                cursor = sd.query(Contracts.BusDataEntry.TABLE_NAME_TRIPS,
                        projection,selection,selectionArgs, null,null,null,null);
                break;
            case ROOMS:
                sd = buildingDbHelper.getReadableDatabase();
                cursor = sd.query(Contracts.RoomContractEntry.TABLE_NAME_ROOM,
                        projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase sd;

        Log.v(TAG, "URI: " + uri);

        int pupdate;

        int match = sUriMatcher.match(uri);

        Log.v(TAG, "MATCH INT: " + match);
        switch (match){
            case BUILDING_HISTORY:
                sd = historyDbHelper.getWritableDatabase();
                pupdate = sd.delete(Contracts.BuildingHistoryEntry.TABLE_NAME, null,null);
                if (pupdate != 0){
                    Toast.makeText(getContext(), "History Deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No History to Delete", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalArgumentException("Cannot delete unknown URI " + uri);
        }
        return pupdate;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
