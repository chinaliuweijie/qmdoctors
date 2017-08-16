package com.qingmiao.qmdoctor.presenter;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/4/14.
 */
public interface BasePresenter {

    void startLoad(String uri, LinkedHashMap<String,String> linkedHashMap);


    void loadFileParems(String uri, LinkedHashMap<String,String> linkedHashMap,String fileKey, HashMap<String,File> fileHashMap);

}
