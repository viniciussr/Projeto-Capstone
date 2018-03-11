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
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.runapp.model.Event;
import com.capstone.runapp.model.Events;
import com.capstone.runapp.service.DisposableManager;
import com.capstone.runapp.service.EventService;
import com.capstone.runapp.service.ServiceFactory;
import com.capstone.runapp.util.Format;
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

import java.text.SimpleDateFormat;
import java.util.Map;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {


    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private final static int ZOOM_DEFAULT = 10;
    private Location mLastKnownLocation;


    @BindString(R.string.intent_event_detail)
    String pIntentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        checkPermission();
        if (isOnline()) {
            loadMapFragment();
        } else {
            showErrorMessage();
        }
    }

    @Override
    protected void onDestroy() {
        DisposableManager.dispose();
        super.onDestroy();
    }


    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showErrorMessage() {
        Snackbar.make(findViewById(android.R.id.content), "No internet conection", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
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

        EventService service = ServiceFactory.create(EventService.class, EventService.ENDPOINT);
        Observable<Events> observable = service.getEvents();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Events>() {

                               private Events postEvents;

                               @Override
                               public void onSubscribe(Disposable d) {
                                   DisposableManager.add(d);
                               }

                               @Override
                               public void onNext(Events events) {
                                   Log.d(TAG, "In onNext()");
                                   postEvents = events;
                               }

                               @Override
                               public void onError(Throwable e) {
                                   e.printStackTrace();
                                   Log.d(TAG, "In onError()");
                               }

                               @Override
                               public void onComplete() {
                                   addMarker(postEvents);
                               }
                           }
                );
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        loadEventsInformation();
        updateLocationUI();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker) {

        View display = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

        TextView title = display.findViewById(R.id.title);
        title.setText(marker.getTitle());
        TextView snippet = display.findViewById(R.id.snippet);
        snippet.setText(marker.getSnippet());

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {

//                Class detailActivity = DetailActivity.class;
//                Intent intentDetailActivity = new Intent(context, detailActivity);
//                intentDetailActivity.putExtra(getResources().getString(R.string.intent_detail_put_extra), movie);
//                startActivity(intentDetailActivity);

                startActivity((new Intent(getApplicationContext(), EventDetailActivity.class)).putExtra(pIntentEvent,(Event)marker.getTag()));



            }
        });

        return display;
    }

    private void addMarker(Events events) {

        for (Event event : events.items()) {
            LatLng location = new LatLng(event.latitude(), event.longitude());

            MarkerOptions markerOptions =
                    new MarkerOptions().position(location).title(event.nome()).snippet("Quando: " + Format.dateFormat(event.data()));
            Marker marker = mMap.addMarker(markerOptions);
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.run_icon));
            marker.setTag(event);
           // mapEvents.put(event.nome(),event);
        }

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
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), ZOOM_DEFAULT));
                        }
                    }
                });
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
