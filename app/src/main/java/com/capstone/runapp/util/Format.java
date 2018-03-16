package com.capstone.runapp.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by vinicius.rocha on 3/11/18.
 */

public class Format {

    private static String TIMEZONE = "GMT";
    private static int MINIMUM_DIGITS = 2;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String numberFormat(Float value ){
        DecimalFormat decimalFormat = new java.text.DecimalFormat();
        decimalFormat.setMinimumFractionDigits(MINIMUM_DIGITS);
        return decimalFormat.format(value);
    }

    public static String dateFormat(Date date){
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        return dateFormat.format(date);
    }

    public static Date dateFormat(String date) throws ParseException {
        dateFormat.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        return dateFormat.parse(date);
    }

}
