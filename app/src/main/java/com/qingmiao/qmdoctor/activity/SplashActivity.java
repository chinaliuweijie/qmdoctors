package com.qingmiao.qmdoctor.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.utils.PrefUtils;

/**
 * @desc 启动屏
 * Created by devilwwj on 16/1/23.
 */
public class SplashActivity extends Activity {
    Splashhandler splashhandler;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_splash);

        showSplash();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(mHandler!=null&&splashhandler!=null){
            mHandler.removeCallbacks(splashhandler);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mHandler!=null&&splashhandler!=null){
            mHandler.postDelayed(splashhandler, 500);
        }
    }


     private void showSplash(){
         mHandler = new Handler();
         splashhandler = new Splashhandler();
     }


    class Splashhandler implements Runnable{

        public void run() {
            // 判断是否是第一次开启应用
            boolean isFirstOpen = PrefUtils.getBoolean(SplashActivity.this, KeyOrValueGlobal.FIRST_OPEN,false);
            // 如果是第一次启动，则先进入功能引导页
            if (!isFirstOpen) {
                Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}
