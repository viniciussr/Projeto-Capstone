package com.capstone.runapp;

import com.capstone.runapp.util.Format;

import org.junit.Test;

import java.text.Normalizer;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by vinicius.rocha on 3/15/18.
 */

public class FormatTest {

    @Test
    public void formatNumber() throws Exception {
        assertEquals("100.00", Format.numberFormat(100f));
    }

    @Test
    public void formatDate() throws Exception {
        assertEquals("10/10/2018 08:00:00", Format.dateFormat(new Date("10/10/2018 08:00:00 GMT")));
    }
}
