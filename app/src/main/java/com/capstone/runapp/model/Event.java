package com.capstone.runapp.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.Date;

/**
 * Created by vinicius.rocha on 3/5/18.
 */

@AutoValue
public abstract class Event implements Parcelable {

    public abstract String nome();
    public abstract Date data();
    public abstract Float valor();
    public abstract String descricao();
    public abstract Float latitude();
    public abstract Float longitude();

    public static Event create(String nome, Date data, Float valor, String descricao, Float latitude, Float longitude) {
        return new AutoValue_Event(nome, data, valor, descricao, latitude, longitude);
    }

    public static TypeAdapter<Event> typeAdapter(Gson gson) {
        return new AutoValue_Event.GsonTypeAdapter(gson);
    }
}

