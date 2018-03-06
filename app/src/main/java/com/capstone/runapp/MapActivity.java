package com.capstone.runapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.capstone.runapp.service.DisposableManager;

import butterknife.ButterKnife;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        DisposableManager.dispose();
        super.onDestroy();
    }
}
