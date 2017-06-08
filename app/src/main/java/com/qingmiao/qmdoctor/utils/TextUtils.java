package com.qingmiao.qmdoctor.utils;

/**
 * Created by Administrator on 2017/4/28.
 */

public class TextUtils {
    public static String getEStr2CStr(String str){
        String[] regs = { "！",  "，",  "。",  "；",  "：", "!",  ",",  ".",  ";",  ":"};
        for ( int i = 0; i < regs.length / 2; i++ )
        {
            str = str.replaceAll (regs[i], regs[i + regs.length / 2]);
        }
        return str;
    }

}
