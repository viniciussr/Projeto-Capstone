package com.capstone.runapp.service;


import com.capstone.runapp.model.Events;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by vinicius.rocha on 3/5/18.
 */

public interface EventService {

    String ENDPOINT = "https://capstone-run-app-1.appspot.com/api/";

    @GET("evento/v1/listaEvento")
    Observable<Events> getEvents();
}

