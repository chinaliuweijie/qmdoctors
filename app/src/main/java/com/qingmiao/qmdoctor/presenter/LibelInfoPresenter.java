package com.qingmiao.qmdoctor.presenter;

import android.app.Activity;

import com.qingmiao.qmdoctor.model.OnLoadDataListener;
import com.qingmiao.qmdoctor.model.SimpleModel;
import com.qingmiao.qmdoctor.view.IBaseView;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/4/19.
 */

public class LibelInfoPresenter implements BasePresenter,OnLoadDataListener {

    private ILibelInfoView iLibelInfoView;
    private SimpleModel baseModel;

    public LibelInfoPresenter(ILibelInfoView iBaseView){
        this.iLibelInfoView = iBaseView;
        baseModel = new SimpleModel();
    }


    @Override
    public void startLoad(String uri, LinkedHashMap<String, String> linkedHashMap) {
        iLibelInfoView.showLibelProgress(uri);
        baseModel.setTag((Activity) iLibelInfoView);
        baseModel.loadData(uri,linkedHashMap,this);
    }

    @Override
    public void loadFileParems(String uri, LinkedHashMap<String, String> linkedHashMap, String fileKey, HashMap<String, File> fileHashMap) {
        iLibelInfoView.showLibelProgress(uri);
        baseModel.setTag((Activity) iLibelInfoView);
        baseModel.loadData(uri,linkedHashMap,fileKey,fileHashMap,this);
    }

    @Override
    public void onSuccess(String uri,String baseBean) {
        iLibelInfoView.hideLibelProgress(uri);
        iLibelInfoView.getLibelData(uri,baseBean);
    }

    @Override
    public void onFailure(String msg, Exception e,String url) {
        iLibelInfoView.showLoadFailMsg(e);
    }
}
