package com.capstone.runapp.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by vinicius.rocha on 3/5/18.
 */

@AutoValue
public abstract class Events implements Parcelable {

    public abstract ArrayList<Event> items();

    public static Events create(ArrayList<Event> events) {
        return new AutoValue_Events(events);
    }

    public static TypeAdapter<Events> typeAdapter(Gson gson) {
        return new AutoValue_Events.GsonTypeAdapter(gson);
    }
}
