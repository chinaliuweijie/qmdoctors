package com.qingmiao.qmdoctor.view;

/**
 * Created by Administrator on 2017/4/14.
 */

public interface IBaseView {
    void showProgress();
    void hideProgress();
    void initData(String data);
    void showLoadFailMsg(Exception e);
}
