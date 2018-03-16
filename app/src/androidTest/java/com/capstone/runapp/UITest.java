package com.capstone.runapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by vinicius.rocha on 3/15/18.
 */

public class UITest {

    @Rule
    public ActivityTestRule<MapActivity> map = new ActivityTestRule<MapActivity>(MapActivity.class);


    @Test
    public void test() throws UiObjectNotFoundException {
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

}
