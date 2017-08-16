package com.qingmiao.qmdoctor.model;

import android.app.Activity;

import com.qingmiao.qmdoctor.view.IBaseView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/4/14.
 */

public class SimpleModel implements BaseModel {
    private Activity tagContext;


    @Override
    public void loadData(final String url, LinkedHashMap<String, String> linkedHashMap, final OnLoadDataListener onLoadDataListener) {
        OkHttpUtils.post()
                .url(url).tag(tagContext)
                .params(linkedHashMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onLoadDataListener.onFailure(id+"",e,url);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        onLoadDataListener.onSuccess(url,response);
                    }
                });
    }

    @Override
    public void loadData(final String url, LinkedHashMap<String, String> linkedHashMap, String fileKey, HashMap<String, File> fileHashMap, final OnLoadDataListener onLoadDataListener) {
        OkHttpUtils.post()
                .url(url).tag(tagContext)
                .params(linkedHashMap).files(fileKey,fileHashMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        onLoadDataListener.onFailure(id+"",e,url);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        onLoadDataListener.onSuccess(url,response);
                    }
                });
    }

    @Override
    public void setTag(Activity iBaseView) {
        this.tagContext = (Activity) iBaseView;
    }
}
