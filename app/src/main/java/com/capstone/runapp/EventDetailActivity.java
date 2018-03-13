package com.capstone.runapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.capstone.runapp.model.Event;
import com.capstone.runapp.provider.EventContract;
import com.capstone.runapp.util.Format;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EventDetailActivity extends AppCompatActivity {

    @BindView(R.id.titulo)
    TextView title;

    @BindString(R.string.intent_event_detail)
    String pIntentDetail;

    @BindView(R.id.date)
    TextView date;

    @BindView(R.id.value)
    TextView value;

    @BindView(R.id.description)
    TextView description;

    @BindView(R.id.favorite)
    FloatingActionButton fab;

    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        event = getIntent().getParcelableExtra(pIntentDetail);
        title.setText(event.nome());
        date.setText(Format.dateFormat(event.data()));
        value.setText(new StringBuilder().append("R$").append(Format.numberFormat(event.valor())));
        description.setText(event.descricao());

       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setSelected(!button.isSelected());
                if (button.isSelected()) {
                    addFavorites();
                } else {
                    deleteFavorites();
                }
            }
        });
        toggleFavorite();

    }

    private void addFavorites() {

        Uri uri = EventContract.EventEntry.CONTENT_URI;
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.clear();

        values.put(EventContract.EventEntry.NAME, event.nome());
        values.put(EventContract.EventEntry.DATE, event.data().toString());
        values.put(EventContract.EventEntry.VALUE, event.valor());
        values.put(EventContract.EventEntry.DESCRIPTION, event.descricao());
        values.put(EventContract.EventEntry.LATITUDE, event.latitude());
        values.put(EventContract.EventEntry.LONGITUDE, event.longitude());

        resolver.insert(uri, values);
    }

    private void deleteFavorites() {

        Uri uri = EventContract.EventEntry.CONTENT_URI;
        ContentResolver resolver = getContentResolver();

        resolver.delete(uri,EventContract.EventEntry.NAME + " = ?",new String[]{event.nome()});

    }

    private boolean checkFavorites(){

        Uri uri = EventContract.EventEntry.CONTENT_URI;
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, null, EventContract.EventEntry.NAME + " = ? ", new String[]{event.nome()}, null);
            if (cursor != null && cursor.moveToFirst())
                return true;
        } finally {
            if(cursor != null)
                cursor.close();
        }
        return false;
    }

    private void toggleFavorite(){

        boolean inFavorites = checkFavorites();
        fab.setSelected(inFavorites);
    }
}
