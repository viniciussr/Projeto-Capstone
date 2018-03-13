package com.capstone.runapp.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.capstone.runapp.model.Event;

/**
 * Created by vinicius.rocha on 3/12/18.
 */

public class EventProvider extends ContentProvider{

    public static final int CODE_EVENT = 100;
    public static final int CODE_EVENT_ID = 101;
    private static final String UNKNOWN_URI = "Unknown uri: ";

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private EventDbHelper eventDbHelper;

    @Override
    public boolean onCreate() {
        eventDbHelper = new EventDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)){
            case CODE_EVENT_ID:
                builder.setTables(EventContract.EventEntry.TABLE_NAME);
                builder.appendWhere(EventContract.EventEntry.ID + " = " +
                        uri.getLastPathSegment());
                cursor = builder.query(eventDbHelper.getReadableDatabase(),
                        projection,selection,selectionArgs, null,null,sortOrder);
                break;
            case CODE_EVENT:
                builder.setTables(EventContract.EventEntry.TABLE_NAME);
                cursor = builder.query(eventDbHelper.getReadableDatabase(),projection,selection,selectionArgs, null,null,sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENT:
                return EventContract.EventEntry.CONTENT_TYPE;
            case CODE_EVENT_ID:
                return EventContract.EventEntry.CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = eventDbHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENT:
                long id = db.insert(EventContract.EventEntry.TABLE_NAME, null, contentValues);
                if (id != 0) {
                    returnUri = EventContract.EventEntry.buildEventUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = eventDbHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)){
            case CODE_EVENT:
                rowsDeleted = db.delete(EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(UNKNOWN_URI + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException(
                "Not implementing");
    }

    private static UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = EventContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,EventContract.PATH,CODE_EVENT);
        matcher.addURI(authority, EventContract.PATH + "/#", CODE_EVENT_ID);
        return matcher;
    }
}
