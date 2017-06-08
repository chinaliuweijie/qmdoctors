package com.qingmiao.qmdoctor.view;

/**
 * Created by Administrator on 2017/4/19.
 */

public interface ILibelInfoView {
    void showLibelProgress(String uri);
    void hideLibelProgress(String uri);
    void getLibelData(String uri,String data);
    void showLoadFailMsg(Exception e);
}
