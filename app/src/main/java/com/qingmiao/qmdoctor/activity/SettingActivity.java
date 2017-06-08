package com.qingmiao.qmdoctor.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.mylhyl.superdialog.SuperDialog;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.LoginBean;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.MyApplication;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
import okhttp3.Call;
/**
 * Created by: jpj
 * Created time: 17/3/7
 * Description:
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener{
    private String did;
    private String token;
    private RelativeLayout passwordAnew;
    private RelativeLayout opinion;
    private RelativeLayout quitLogin;
    private RelativeLayout clearCache;
    private RelativeLayout evaluateWe;
    private RelativeLayout correlationWe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ivLeft.setVisibility(View.VISIBLE);
        tvCenter.setText("设置");
        passwordAnew = (RelativeLayout) findViewById(R.id.rl_password_anew);
        opinion = (RelativeLayout) findViewById(R.id.rl_opinion);
        quitLogin = (RelativeLayout) findViewById(R.id.rl_quit_login);
        clearCache = (RelativeLayout) findViewById(R.id.rl_clear_cache);
        evaluateWe = (RelativeLayout) findViewById(R.id.rl_evaluate_we);
        correlationWe = (RelativeLayout) findViewById(R.id.rl_correlation_we);
        LoginBean login = (LoginBean) PrefUtils.getBean(this, KeyOrValueGlobal.LOGINBEAN);
        if(login!=null) {
            did = login.did;
            token = login.token;
        }
        passwordAnew.setOnClickListener(this);
        opinion.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        evaluateWe.setOnClickListener(this);
        correlationWe.setOnClickListener(this);
        quitLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.rl_password_anew :
                if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)){
                    startActivity(new Intent(SettingActivity.this,LoginActivity.class));
                }else{
                    startActivity(new Intent(SettingActivity.this,ResetPwdActivity.class));
                }
                break;
            case R.id.rl_opinion :
                if(TextUtils.isEmpty(did) || TextUtils.isEmpty(token)){
                    startActivity(new Intent(SettingActivity.this,LoginActivity.class));
                }else{
                    startActivity(new Intent(SettingActivity.this,FeedBackActivity.class));
                }
                break;
            case R.id.rl_clear_cache :
                int[] contentPadding = {20, 0, 20, 20};
                new SuperDialog.Builder(this).setTitle("提示",getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title))
                        .setBackgroundColor(getResources().getColor(R.color.white)).setMessage("是否清理缓存!",getResources().getColor(R.color.black_1),(int) getResources().getDimension(R.dimen.tv_sitem_content),contentPadding)
                        .setNegativeButton("确定", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickNegativeListener(){

                            @Override
                            public void onClick(View v) {
                                deleteCacheFile();
                            }
                        }).setWidth(0.7f)
                        .setPositiveButton("取消", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickPositiveListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).build();
                break;
            case R.id.rl_evaluate_we :
                try{
                    Uri uri = Uri.parse("market://details?id="+getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch(Exception e){
                    ToastUtils.showLongToast(this,"您手机中没有安装应用市场");
                    e.printStackTrace();
                }
                break;
            case R.id.rl_correlation_we :
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.rl_quit_login :
                if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(did)){
                    int[] contentPaddings = {20, 0, 20, 20};
                    new SuperDialog.Builder(this).setTitle("提示",getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title))
                            .setBackgroundColor(getResources().getColor(R.color.white)).setMessage("您确定退出登录吗!",getResources().getColor(R.color.black_1),(int) getResources().getDimension(R.dimen.tv_sitem_content),contentPaddings)
                            .setNegativeButton("确定", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickNegativeListener(){

                                @Override
                                public void onClick(View v) {
                                    initHttp();
                                }
                            }).setWidth(0.7f)
                            .setPositiveButton("取消", getResources().getColor(R.color.green),(int) getResources().getDimension(R.dimen.tv_sitem_title),-1,new SuperDialog.OnClickPositiveListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).build();
                }else{
                    ToastUtils.showLongToast(SettingActivity.this,"当前的状态未登录");
                }
            break;
        }
    }


    //递归删除文件
    private void deleteCacheFile() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                Glide.get(getApplicationContext()).clearDiskCache();
                MyApplication.aCache.clear();
                EventBus.getDefault().post("changeData");
                File f=new File(Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator+ PDFWebViewActivity.FILE_PATENT_NAME);
                deleteAllFiles(f);
                clearWebViewCache();
            }
        }.start();
    }

    static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }



    public void clearWebViewCache() {

        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath() + "/webcache");

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {


        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    private void initHttp(){
        OkHttpUtils.post()
                .url(UrlGlobal.LOGOUT_URL)
                .addParams("did", did)
                .addParams("token", token)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(SettingActivity.this,"网络错误");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        onLogout();
                    }
                });
    }


    /**
     * 下线按钮
     *
     * @param
     */
    public void onLogout() {
        // 环信下线
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.e("main", "下线成功了");
                app.saveLogin(null);
                PrefUtils.putString(SettingActivity.this,"did","");
                PrefUtils.putString(SettingActivity.this,"token","");
                PrefUtils.putBean(SettingActivity.this,KeyOrValueGlobal.LOGINBEAN,null);
                EventBus.getDefault().post("changeData");
                // 通知患者界面刷新
                EventBus.getDefault().post("updata_patient");
                finish();
            }

            @Override
            public void onError(int i, String s) {
                Log.e("main", "下线失败了！" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });//下线
    }
}