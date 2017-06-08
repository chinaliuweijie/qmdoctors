package com.qingmiao.qmdoctor.model;

import com.qingmiao.qmdoctor.bean.BaseBean;

import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/4/14.
 */
public interface BaseModel {

    void loadData(String url,LinkedHashMap<String,String> linkedHashMap,OnLoadDataListener onLoadDataListener);
}
