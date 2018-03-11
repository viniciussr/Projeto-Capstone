package com.capstone.runapp.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vinicius.rocha on 3/11/18.
 */

public class Format {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static DecimalFormat decimalFormat = new java.text.DecimalFormat("###,####.##");


    public static String numberFormat(Float value ){
        decimalFormat.setMinimumFractionDigits(2);
        return decimalFormat.format(value);
    }

    public static String dateFormat(Date date){
       return dateFormat.format(date);
    }

}
