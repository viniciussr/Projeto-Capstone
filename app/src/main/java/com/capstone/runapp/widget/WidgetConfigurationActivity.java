package com.capstone.runapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.widget.RadioGroup;


import com.capstone.runapp.R;
import com.capstone.runapp.model.Event;
import com.capstone.runapp.service.FavoriteService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WidgetConfigurationActivity extends AppCompatActivity {

    @BindView(R.id.radioGroup)
    RadioGroup namesRadioGroup;

    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private ArrayList<Event> events;
    private static final int START_INDEX = 0;
    private static final int MIN_SIZE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_activity_configuration);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if(extras != null){
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
            }
        }


        FavoriteService service = new FavoriteService();
        Disposable subscription = Observable.fromArray(service.loadFromDB(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                    this.events = events;
                    if(events.isEmpty()){
                        finish();
                    } else {
                        int currentIndex = START_INDEX;
                        for (Event event : events) {
                            AppCompatRadioButton button = new AppCompatRadioButton(this);
                            button.setText(event.nome());
                            button.setId(currentIndex++);
                            namesRadioGroup.addView(button);
                        }
                        if (namesRadioGroup.getChildCount() > MIN_SIZE) {
                            ((AppCompatRadioButton) namesRadioGroup.getChildAt(START_INDEX)).setChecked(true);
                        }
                    }

                });
    }

    @OnClick(R.id.button)
    public void onOkButtonClick() {
        int checkedItemId = namesRadioGroup.getCheckedRadioButtonId();
        Event recipe = events.get(checkedItemId);

        WidgetProvider.updateAppWidget(getApplicationContext(),AppWidgetManager.getInstance(getApplicationContext()), mAppWidgetId, recipe);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

}
