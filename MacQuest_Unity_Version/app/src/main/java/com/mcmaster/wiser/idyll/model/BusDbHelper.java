package com.mcmaster.wiser.idyll.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class to read the static hamilton GTFS database
 * Created by Eric on 6/26/17.
 * TODO: combine this class with the DbHelper class
 */

public class BusDbHelper extends SQLiteOpenHelper {
    private static final String TAG = BusDbHelper.class.getSimpleName();

    private static String DB_PATH = "";

    private static String DB_NAME = "hamiltonGTFS.db";

    private SQLiteDatabase mDataBase;

    private final Context mContext;

    public BusDbHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.mContext = context;
        DB_PATH = mContext.getDatabasePath(DB_NAME).toString();
    }

    public void createDataBase() throws IOException {
        //If the database does not exist, copy it from the assets.

        boolean mDataBaseExist = checkDataBase();

        if(!mDataBaseExist)
        {
            this.getReadableDatabase();
            this.close();
            try
            {
                //Copy the database from assests
                copyDataBase();
                Log.e(TAG, "createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLException e){
            // database doesn't exist yet.
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH ;


        OutputStream mOutput = new FileOutputStream(outFileName);

        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0) {
            mOutput.write(mBuffer, 0, mLength);
        }

        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    //Open the database, so we can query it
    public void openDataBase() throws SQLException {
        String mPath = DB_PATH;
        //Log.v("mPath", mPath);

        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.OPEN_READONLY);
        //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    @Override
    public synchronized void close() {
        if(mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "BusDbHelper created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
