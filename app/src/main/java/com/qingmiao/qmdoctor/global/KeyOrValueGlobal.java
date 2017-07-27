package com.qingmiao.qmdoctor.global;

import com.qingmiao.qmdoctor.utils.PrefUtils;

/**
 * company : 青苗
 * Created by 刘伟杰 on 2017/3/22.
 */

public class KeyOrValueGlobal {
    public static String DID = "did";
    public static String TOKEN = "token";
    public static String UID = "uid";

    //-----------------------------------
    public static String LOGINBEAN = "loginbean" ;

    public static String uid = PrefUtils.getString(MyApplication.context, UID, "");
    public static String did = PrefUtils.getString(MyApplication.context, DID, "");
    public static String token = PrefUtils.getString(MyApplication.context, TOKEN, "");

    public static final String KEY_BIAOXINHUANZHE = "标星患者" ;


    public static final String FIRST_OPEN = "first_open" ;







}
