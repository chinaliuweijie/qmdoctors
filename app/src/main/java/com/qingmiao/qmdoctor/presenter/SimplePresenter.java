package com.qingmiao.qmdoctor.presenter;

import android.app.Activity;
import android.widget.Toast;

import com.qingmiao.qmdoctor.bean.BaseBean;
import com.qingmiao.qmdoctor.model.BaseModel;
import com.qingmiao.qmdoctor.model.OnLoadDataListener;
import com.qingmiao.qmdoctor.model.SimpleModel;
import com.qingmiao.qmdoctor.view.IBaseView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/4/14.
 */

public class SimplePresenter implements BasePresenter,OnLoadDataListener {

   private IBaseView iBaseView;
   private BaseModel baseModel;


   public SimplePresenter(IBaseView iBaseView){
       this.iBaseView = iBaseView;
       baseModel = new SimpleModel();
   }


    @Override
    public void startLoad(String uri, LinkedHashMap<String,String> linkedHashMap) {
        this.iBaseView.showProgress();
        baseModel.setTag((Activity) iBaseView);
        baseModel.loadData(uri,linkedHashMap,this);
    }

    @Override
    public void loadFileParems(String uri, LinkedHashMap<String, String> linkedHashMap, String fileKey, HashMap<String, File> fileHashMap) {
        this.iBaseView.showProgress();
        baseModel.setTag((Activity) iBaseView);
        baseModel.loadData(uri,linkedHashMap,fileKey,fileHashMap,this);
    }


    @Override
    public void onSuccess(String uri,String baseBean) {
        try {
            this.iBaseView.hideProgress();
            this.iBaseView.initData(baseBean);
            //可以取消同一个tag的
            OkHttpUtils.getInstance().cancelTag(uri);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String msg, Exception e,String url) {
        this.iBaseView.showLoadFailMsg(e);
        //可以取消同一个tag的
        OkHttpUtils.getInstance().cancelTag(url);
    }
}
