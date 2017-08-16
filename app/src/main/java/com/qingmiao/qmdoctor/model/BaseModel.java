package com.qingmiao.qmdoctor.model;

import android.app.Activity;

import com.qingmiao.qmdoctor.bean.BaseBean;
import com.qingmiao.qmdoctor.view.IBaseView;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/4/14.
 */
public interface BaseModel {

    void loadData(String url,LinkedHashMap<String,String> linkedHashMap,OnLoadDataListener onLoadDataListener);

    void loadData(String url, LinkedHashMap<String,String> linkedHashMap,String fileKey, HashMap<String,File> fileHashMap, OnLoadDataListener onLoadDataListener);

    void setTag(Activity iBaseView);
}
