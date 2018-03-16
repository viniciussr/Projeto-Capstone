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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.capstone.runapp.model.Event;
import com.capstone.runapp.provider.EventContract;
import com.capstone.runapp.util.Format;


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

        loadEvents();
        eventFab();
        toggleFavorite();
        loadToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        StringBuilder textShare = new StringBuilder().append(event.nome()).append(" - ").append("R$").append(Format.numberFormat(event.valor()));
        if (id == R.id.share) {
            startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this)
                    .setType(getString(R.string.type))
                    .setText(textShare)
                    .getIntent(), getString(R.string.action_share)));
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadToolbar() {
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        toolbar.setTitle(event.nome());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void eventFab() {
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
    }

    private void loadEvents() {
        event = getIntent().getParcelableExtra(pIntentDetail);
        date.setText(Format.dateFormat(event.data()));
        value.setText(new StringBuilder().append(getString(R.string.money)).append(Format.numberFormat(event.valor())));
        description.setText(event.descricao());
    }

    private void addFavorites() {

        Uri uri = EventContract.EventEntry.CONTENT_URI;
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.clear();

        values.put(EventContract.EventEntry.NAME, event.nome());
        values.put(EventContract.EventEntry.DATE, Format.dateFormat(event.data()));
        values.put(EventContract.EventEntry.VALUE, event.valor());
        values.put(EventContract.EventEntry.DESCRIPTION, event.descricao());
        values.put(EventContract.EventEntry.LATITUDE, event.latitude());
        values.put(EventContract.EventEntry.LONGITUDE, event.longitude());

        resolver.insert(uri, values);
    }

    private void deleteFavorites() {

        Uri uri = EventContract.EventEntry.CONTENT_URI;
        ContentResolver resolver = getContentResolver();

        resolver.delete(uri, EventContract.EventEntry.NAME + " = ?", new String[]{event.nome()});

    }

    private boolean checkFavorites() {

        Uri uri = EventContract.EventEntry.CONTENT_URI;
        ContentResolver resolver = getContentResolver();
        Cursor cursor = null;
        try {
            cursor = resolver.query(uri, null, EventContract.EventEntry.NAME + " = ? ", new String[]{event.nome()}, null);
            if (cursor != null && cursor.moveToFirst())
                return true;
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return false;
    }

    private void toggleFavorite() {

        boolean inFavorites = checkFavorites();
        fab.setSelected(inFavorites);
    }
}
