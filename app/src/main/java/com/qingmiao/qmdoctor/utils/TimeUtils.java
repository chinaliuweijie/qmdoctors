package com.qingmiao.qmdoctor.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.text.TextUtils;

public class TimeUtils {
    /**
     * 时间戳转为时间(年月日，时分秒)
     *
     * @param cc_time 时间戳
     * @return
     */
    public static String getStrTime(String cc_time) {
        String re_StrTime = null;
        try {
            //同理也可以转为其它样式的时间格式.例如："yyyy/MM/dd HH:mm"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            // 例如：cc_time=1291778220
            long lcc_time = Long.valueOf(cc_time);
            re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        }catch (Exception e){
            e.printStackTrace();
        }
        return re_StrTime;
    }

    public static final String BIRTHDAY_FORMAT = "yyyy/MM/dd" ;

    public static String getStrTime(String cc_time,String format) {
        String re_StrTime = null;
        try {
            //同理也可以转为其它样式的时间格式.例如："yyyy/MM/dd HH:mm"
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            // 例如：cc_time=1291778220
            long lcc_time = Long.valueOf(cc_time);
            re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        }catch (Exception e){
            e.printStackTrace();
        }
        return re_StrTime;
    }




    /**
     * 时间转换为时间戳
     *
     * @param timeStr 时间 例如: 2016-03-09
     * @param format  时间对应格式  例如: yyyy-MM-dd
     * @return
     */
    public static long getTimeStamp(String timeStr, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeStr);
            long timeStamp = date.getTime();
            return timeStamp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }



    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定，34578 其他位置的可以为0-9
    */
        String num = "[1][34578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }
}