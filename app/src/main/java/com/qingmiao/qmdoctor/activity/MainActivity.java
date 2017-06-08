package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.factory.FragmentFactory;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    private int currentIndex = 0;
    @BindView(R.id.test_icon0)
    RadioButton test_icon0;
    @BindView(R.id.test_icon1)
    RadioButton test_icon1;
    @BindView(R.id.test_icon2)
    RadioButton test_icon2;
    @BindView(R.id.test_icon3)
    RadioButton test_icon3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivLeft.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        int flag = intent.getIntExtra("flag", 0);
        if(flag>0) {
            switchFragment(2);
        }
        final LinearLayout radioBnt = (LinearLayout) findViewById(R.id.RadioGroup);
        for (int i = 0; i < radioBnt.getChildCount(); i++) {
            final View childAt = radioBnt.getChildAt(i);
            childAt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int indexOfChild = radioBnt.indexOfChild(childAt);
                    if(currentIndex == indexOfChild){
                        return ;
                    }
                    switch (indexOfChild) {
                        case 0:
                            tvCenter.setText("首页");
                            switchFragment(0);
                            break;
                        case 1:
                            tvCenter.setText("消息");
                            switchFragment(1);
                            break;
                        case 2:
                            tvCenter.setText("患者列表");
                            switchFragment(2);
                            break;
                        case 3:
                            tvCenter.setText("个人中心");
                            switchFragment(3);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
        initFragment();
        switchFragment(0);
        tvCenter.setText("首页");
    }
    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fl_realcontent, FragmentFactory.getFragment(0));
        transaction.add(R.id.fl_realcontent, FragmentFactory.getFragment(1));
        transaction.add(R.id.fl_realcontent, FragmentFactory.getFragment(2));
        transaction.add(R.id.fl_realcontent, FragmentFactory.getFragment(3));
        transaction.commit();
    }
    private void switchFragment(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment fragment = FragmentFactory.getFragment(position);
        //当切换Fragment的时候，先把所有的Fragment隐藏起来
        transaction.hide(FragmentFactory.getFragment(0));
        transaction.hide(FragmentFactory.getFragment(1));
        transaction.hide(FragmentFactory.getFragment(2));
        transaction.hide(FragmentFactory.getFragment(3));
        //显示点中的Fragment对象
        transaction.show(fragment);
        transaction.commit();
        currentIndex = position;
        setBottomCheck(position);
    }

    private void setBottomCheck(int position){
        switch (position){
            case 0:
                test_icon1.setChecked(false);
                test_icon2.setChecked(false);
                test_icon3.setChecked(false);
                test_icon0.setChecked(true);
            break;
            case 1:
                test_icon2.setChecked(false);
                test_icon3.setChecked(false);
                test_icon0.setChecked(false);
                test_icon1.setChecked(true);
            break;
            case 2:
                test_icon3.setChecked(false);
                test_icon0.setChecked(false);
                test_icon1.setChecked(false);
                test_icon2.setChecked(true);
            break;
            case 3:
                test_icon0.setChecked(false);
                test_icon1.setChecked(false);
                test_icon2.setChecked(false);
                test_icon3.setChecked(true);
            break;
        }

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobclickAgent.onKillProcess(this);
    }
}
