package com.mcmaster.wiser.idyll.connection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ahmed on 8/5/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "CREATE TABLE "+DbContract.TABLE_NAME+
            "(id integer primary key autoincrement," +DbContract.COLUMN_BUILDING_NAME+
            " text,"+DbContract.COLUMN_BUILDING_SHORTNAME+
            " text,"+DbContract.COLUMN_LOCATION+
            " text,"+DbContract.COLUMN_OUT_ID+
            " text,"+DbContract.COLUMN_ROOM+
            " text,"+DbContract.COLUMN_UTILITY+
            " text,"+DbContract.COLUMN_UUID+
            " text,"+DbContract.COLUMN_SYNC_STATUS+
            " integer);";

    private static final String DROP_TABLE = "drop table if exists "+DbContract.TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void saveToLocalDatabase(String bname, String bshortName, String blocation, String boutId, String broom, String butility, String buuId, int bsync_status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.COLUMN_BUILDING_NAME, bname);
        contentValues.put(DbContract.COLUMN_BUILDING_SHORTNAME, bshortName);
        contentValues.put(DbContract.COLUMN_LOCATION, blocation);
        contentValues.put(DbContract.COLUMN_OUT_ID, boutId);
        contentValues.put(DbContract.COLUMN_ROOM, broom);
        contentValues.put(DbContract.COLUMN_UTILITY, butility);
        contentValues.put(DbContract.COLUMN_UUID, buuId);
        contentValues.put(DbContract.COLUMN_SYNC_STATUS,bsync_status);

        database.insert(DbContract.TABLE_NAME,null,contentValues);
    }
    public Cursor readFromLocalDatabase(SQLiteDatabase database){

        String[] projection = {DbContract.COLUMN_BUILDING_NAME,DbContract.COLUMN_BUILDING_SHORTNAME,DbContract.COLUMN_LOCATION,DbContract.COLUMN_OUT_ID,DbContract.COLUMN_ROOM,DbContract.COLUMN_UTILITY,DbContract.COLUMN_UUID,DbContract.COLUMN_SYNC_STATUS};
        return(database.query(DbContract.TABLE_NAME,projection,null,null,null,null,null));

    }
    public void updateLocalDatabase(String bname, int bsync_status,SQLiteDatabase database){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.COLUMN_SYNC_STATUS, bsync_status);
        String selection = DbContract.COLUMN_BUILDING_NAME+" LIKE ?";
        String[] selection_args = {bname};
        database.update(DbContract.TABLE_NAME,contentValues,selection,selection_args);
    }
}
