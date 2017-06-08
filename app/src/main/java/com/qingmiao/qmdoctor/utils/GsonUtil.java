package com.qingmiao.qmdoctor.utils;

import com.google.gson.Gson;

/**
 * company : 青苗
 * Created by 杜新 on 2017/3/3.
 */

public class GsonUtil {

    private GsonUtil() {
    }

    private static final Gson single = new Gson();

    //静态工厂方法
    public static Gson getInstance() {
        return single;
    }
}
