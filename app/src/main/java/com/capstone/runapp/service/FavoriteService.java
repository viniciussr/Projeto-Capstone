package com.capstone.runapp.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;

import com.capstone.runapp.model.Event;
import com.capstone.runapp.provider.EventContract;
import com.capstone.runapp.util.Format;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vinicius.rocha on 3/14/18.
 */

public class FavoriteService {

    public ArrayList<Event> loadFromDB(Context context){
        Uri uri = EventContract.EventEntry.CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        ArrayList<Event> events = new ArrayList<Event>();
        try {
            cursor = resolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()){
                for (int i = 0; i < cursor.getCount(); i++) {
                    Event event = Event.create(cursor.getString(1),Format.dateFormat(cursor.getString(2)),
                            cursor.getFloat(3), cursor.getString(4), cursor.getFloat(5), cursor.getFloat(6));
                    events.add(event);
                    cursor.moveToNext();
                    };
                }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return events;
    }
}
