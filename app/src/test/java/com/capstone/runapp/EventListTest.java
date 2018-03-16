package com.capstone.runapp;

import com.capstone.runapp.model.Events;
import com.capstone.runapp.service.EventService;
import com.capstone.runapp.service.ServiceFactory;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;

/**
 * Created by vinicius.rocha on 3/15/18.
 */

public class EventListTest {

    @Test
    public void eventList() {

        EventService service = ServiceFactory.create(EventService.class, EventService.ENDPOINT);
        Observable<Events> observable = service.getEvents();

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events -> {
                            assertEquals(events.items().isEmpty(), false);
                        }
                );
    }
}
