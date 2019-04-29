package com.mcmaster.wiser.idyll.model.building.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mcmaster.wiser.idyll.model.Contracts;

/**
 * Class to create the SQLite database to store the building search history
 * Created by Eric on 6/14/17.
 */

public class BuildingHistoryDbHelper extends SQLiteOpenHelper {
    public static final int SYNC_STATUS_OK =0;
    public static final int SYNC_STATUS_FAILED =1;
    private static final String TAG = BuildingHistoryDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "history.db";
    private static String DB_PATH = "";

    private static final int DATABASE_VERSION = 1;

    public BuildingHistoryDbHelper(Context context){
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + Contracts.BuildingHistoryEntry.TABLE_NAME  + " ("
                + Contracts.BuildingHistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME + " TEXT, "
                + Contracts.BuildingHistoryEntry.COLUMN_BUILDING_SHORTNAME + " TEXT, "
                + Contracts.BuildingHistoryEntry.COLUMN_ROOM + " TEXT, "
                + Contracts.BuildingHistoryEntry.COLUMN_OUT_ID + " TEXT, "
                + Contracts.BuildingHistoryEntry.COLUMN_LOCATION + " TEXT, "
                //  + Contracts.BuildingHistoryEntry.COLUMN_UUID + " TEXT, "
                + Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS + " INTEGER, "
                + Contracts.BuildingHistoryEntry.COLUMN_UTILITY + " TEXT );";

        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void updateLocalDatabase(String bname, int bsync_status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contracts.BuildingHistoryEntry.COLUMN_SYNC_STATUS, bsync_status);
        String selection = Contracts.BuildingHistoryEntry.COLUMN_BUILDING_NAME+" LIKE ?";
        String[] selection_args = {bname};
        database.update(Contracts.BuildingHistoryEntry.TABLE_NAME,contentValues,selection,selection_args);
    }

    public int getDatabaseVersion(SQLiteDatabase database)
    {
        int did = database.getVersion();
        return did;
    }

    public String dbPath(Context context){

        DB_PATH = context.getDatabasePath(DATABASE_NAME).toString();
        return DB_PATH;
    }

}
