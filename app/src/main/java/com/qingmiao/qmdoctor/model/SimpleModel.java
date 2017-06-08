package com.qingmiao.qmdoctor.model;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/4/14.
 */

public class SimpleModel implements BaseModel {

    @Override
    public void loadData(final String url, LinkedHashMap<String, String> linkedHashMap, final OnLoadDataListener onLoadDataListener) {
        OkHttpUtils.post()
                .url(url).tag(url)
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
}
