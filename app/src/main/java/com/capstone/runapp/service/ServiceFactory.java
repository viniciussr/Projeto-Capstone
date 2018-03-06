package com.capstone.runapp.service;

import com.capstone.runapp.util.AutoValueGsonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vinicius.rocha on 3/5/18.
 */

public class ServiceFactory {

    private  final static int CONNECTION_TIMEOUT = 10000;

    public static <T> T create(Class<T> serviceClass, String endpoint){

        OkHttpClient client = new OkHttpClient.Builder().
                writeTimeout(CONNECTION_TIMEOUT,
                        TimeUnit.MILLISECONDS).
                readTimeout(CONNECTION_TIMEOUT,
                        TimeUnit.MILLISECONDS).
                connectTimeout(CONNECTION_TIMEOUT,
                        TimeUnit.MILLISECONDS).build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(AutoValueGsonFactory.create())
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(serviceClass);

    }

}
