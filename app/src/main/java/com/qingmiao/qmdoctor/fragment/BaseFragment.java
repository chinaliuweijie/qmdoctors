package com.qingmiao.qmdoctor.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.activity.MainActivity;
import com.qingmiao.qmdoctor.global.MyApplication;

/**
 * Created by: jpj
 * Created time: 17/3/14
 * Description:
 */
public abstract class BaseFragment extends Fragment {

    public int startCount = 0;

    /** 主界面Activity对象 */
    public Activity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取Activity对象
        mActivity = getActivity();
    }

    @Override public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
    }
    /**
     * 初始化布局
     */
    public abstract void initView();

    /**
     * 初始化数据
     */
    public abstract void initData() ;




}
