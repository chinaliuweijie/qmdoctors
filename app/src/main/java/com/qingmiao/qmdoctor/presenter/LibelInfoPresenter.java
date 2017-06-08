package com.qingmiao.qmdoctor.presenter;

import com.qingmiao.qmdoctor.model.OnLoadDataListener;
import com.qingmiao.qmdoctor.model.SimpleModel;
import com.qingmiao.qmdoctor.view.IBaseView;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.zhy.http.okhttp.OkHttpUtils;

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
        baseModel.loadData(uri,linkedHashMap,this);
    }

    @Override
    public void onSuccess(String uri,String baseBean) {
        iLibelInfoView.hideLibelProgress(uri);
        iLibelInfoView.getLibelData(uri,baseBean);
        OkHttpUtils.getInstance().cancelTag(uri);
    }

    @Override
    public void onFailure(String msg, Exception e,String url) {
        iLibelInfoView.showLoadFailMsg(e);
        //可以取消同一个tag的
        OkHttpUtils.getInstance().cancelTag(url);
    }
}
