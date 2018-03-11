package com.capstone.runapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.capstone.runapp.model.Event;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        Event event= getIntent().getParcelableExtra(pIntentDetail);
        title.setText(event.nome());
        date.setText(Format.dateFormat(event.data()));
        value.setText(new StringBuilder().append("R$").append(Format.numberFormat(event.valor())));
        description.setText(event.descricao());

    }

}
