package com.qingmiao.qmdoctor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class GetTime {
    private static Date parse;
    private static String s;
    public static String getTimestamp() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String ee = dff.format(new Date());
        try {
            parse = dff.parse(ee);
            s = String.valueOf(parse.getTime()).substring(0, 10) + "QMKJ65474qmkj";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return s;
    }



    public static String getCurrentTime(){
        SimpleDateFormat  formatter = new SimpleDateFormat   ("yyyy-MM-dd HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String ee = formatter.format(new Date());
        return ee;
    }





}
