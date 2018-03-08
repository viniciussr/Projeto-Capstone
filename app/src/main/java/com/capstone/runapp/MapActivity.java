package com.capstone.runapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.runapp.model.Events;
import com.capstone.runapp.service.DisposableManager;
import com.capstone.runapp.service.EventService;
import com.capstone.runapp.service.ServiceFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,  GoogleMap.InfoWindowAdapter {


    private GoogleMap mMap;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final LatLng mDefaultLocation = new LatLng(40.7143528, -74.0059731);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        if (isOnline()) {
            load();

            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
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


    private void load() {

        EventService service = ServiceFactory.create(EventService.class, EventService.ENDPOINT);
        Observable<Events> observable = service.getEvents();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Events>() {

                               Events postEvents;

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
                                   Log.d(TAG, "In onCompleted()");
                                   Toast toast = Toast.makeText(getApplicationContext(), postEvents.items().get(0).nome(), Toast.LENGTH_LONG);
                                   toast.show();
                               }
                           }
                );
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        addMarker();
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
        TextView text = (TextView) display.findViewById(R.id.title);
        text.setText("teste");
        return display;
    }

    private void addMarker(){
        MarkerOptions markerOptions =
                new MarkerOptions().position(mDefaultLocation).title("test location");

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mDefaultLocation));

    }


}
