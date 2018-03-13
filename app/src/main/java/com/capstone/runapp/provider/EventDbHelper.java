package com.capstone.runapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.capstone.runapp.provider.EventContract.EventEntry;

/**
 * Created by vinicius.rocha on 3/12/18.
 */

public class EventDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String DATE_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                    EventEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT " + COMMA_SEP +
                    EventEntry.NAME + TEXT_TYPE + COMMA_SEP +
                    EventEntry.DATE + DATE_TYPE + COMMA_SEP +
                    EventEntry.VALUE + REAL_TYPE + COMMA_SEP +
                    EventEntry.DESCRIPTION + TEXT_TYPE + COMMA_SEP +
                    EventEntry.LATITUDE + REAL_TYPE +  COMMA_SEP +
                    EventEntry.LONGITUDE + REAL_TYPE +" )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movie.db";

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
