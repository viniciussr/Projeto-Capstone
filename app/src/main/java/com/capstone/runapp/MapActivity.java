package com.capstone.runapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.capstone.runapp.model.Event;
import com.capstone.runapp.model.Events;
import com.capstone.runapp.service.DisposableManager;
import com.capstone.runapp.service.EventService;
import com.capstone.runapp.service.FavoriteService;
import com.capstone.runapp.service.ServiceFactory;
import com.capstone.runapp.util.Format;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

import static android.content.ContentValues.TAG;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {


    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private final static int ZOOM_DEFAULT = 10;
    private final static LatLng DEFAULT_LOCATION = new LatLng(-22.98558491, -43.17618223);
    private Events postEvents;
    private AdView mAdView;

    @BindString(R.string.intent_event_detail)
    String pIntentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        checkPermission();
        loadToolbar();
        if (isOnline()) {
            loadEventsInformation();
            loadAdmob();
        } else {
            showErrorMessage(getString(R.string.message_without_internet));
        }
    }

    @Override
    protected void onDestroy() {
        DisposableManager.dispose();
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showErrorMessage(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.message_retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        }).show();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            mLocationPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void loadMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void loadEventsInformation() {
        FrameLayout progressBar = (FrameLayout)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        try {
            EventService service = ServiceFactory.create(EventService.class, EventService.ENDPOINT);
            Observable<Events> observable = service.getEvents();

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Events>() {

                                   @Override
                                   public void onSubscribe(Disposable d) {
                                       DisposableManager.add(d);
                                   }

                                   @Override
                                   public void onNext(Events events) {
                                       postEvents = events;
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       progressBar.setVisibility(View.GONE);
                                       showErrorMessage(getString(R.string.message_fail_load_info));
                                   }

                                   @Override
                                   public void onComplete() {
                                       progressBar.setVisibility(View.GONE);
                                       loadMapFragment();
                                   }
                               }
                    );
        } catch (HttpException e) {
            showErrorMessage(getString(R.string.message_fail_load_info));
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        updateLocationUI();
        loadMarker(postEvents);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return prepareInfoView(marker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.all:
                loadEventsInformation();
            case R.id.favorites:
                loadFavorites();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    private View prepareInfoView(Marker marker) {

        View display = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

        TextView title = display.findViewById(R.id.title);
        title.setText(marker.getTitle());
        TextView snippet = display.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {
                startActivity((new Intent(getApplicationContext(), EventDetailActivity.class)).putExtra(pIntentEvent, (Event) marker.getTag()));
            }
        });

        return display;
    }

    private void loadMarker(Events events) {
        mMap.clear();
        for (Event event : events.items()) {
            addMarker(event);
        }
    }

    private void loadMarker(ArrayList<Event> events) {
        mMap.clear();
        for (Event event : events) {
            addMarker(event);
        }
    }

    private void addMarker(Event event) {
        LatLng location = new LatLng(event.latitude(), event.longitude());

        MarkerOptions markerOptions =
                new MarkerOptions().position(location).title(event.nome()).snippet(getString(R.string.message_snippet)+ Format.dateFormat(event.data()));
        Marker marker = mMap.addMarker(markerOptions);
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.run_icon));
        marker.setTag(event);
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            Location mLastKnownLocation = (Location) task.getResult();
                            if(mLastKnownLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), ZOOM_DEFAULT));
                            }else{
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        DEFAULT_LOCATION, ZOOM_DEFAULT));
                            }
                        }
                    }
                });
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("%s", e.getMessage());
        }
    }

    private void loadToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void loadFavorites() {
        FavoriteService service = new FavoriteService();
        Disposable subscription = Observable.fromArray(service.loadFromDB(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                    loadMarker(events);
                });
    }

    private void loadAdmob(){
        MobileAds.initialize(this,getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

}
