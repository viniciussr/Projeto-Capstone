package com.capstone.runapp.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import com.capstone.runapp.R;
import com.capstone.runapp.model.Event;
import com.capstone.runapp.model.Events;
import com.capstone.runapp.service.DisposableManager;
import com.capstone.runapp.service.EventService;
import com.capstone.runapp.service.ServiceFactory;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


/**
 * Created by vinicius.rocha on 3/5/18.
 */

public class MapFragment extends Fragment {

    @BindView(R.id.error_message_display)
    TextView mErrorMessageDisplay;

    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isOnline()) {
            load();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showErrorMessage() {
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void load() {

        EventService service = ServiceFactory.create(EventService.class, EventService.ENDPOINT);
        Observable<Events> observable = service.getEvents();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Events>() {

                    Events postEvents ;
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
                                   Toast toast = Toast.makeText(getContext(),postEvents.items().get(0).nome(),Toast.LENGTH_LONG);
                                   toast.show();

                               }
                           }
                );
    }

}
