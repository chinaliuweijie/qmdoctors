package com.qingmiao.qmdoctor.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.mylhyl.superdialog.SuperDialog;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.VersionBean;
import com.qingmiao.qmdoctor.factory.FragmentFactory;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.DownloadUtils;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Request;

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
        // 自动检测新版本
        OkHttpUtils.post()
                .url(UrlGlobal.GET_VERSION)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .addParams("type","2")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response, int id) {
                        final VersionBean versionBean = GsonUtil.getInstance().fromJson(response,VersionBean.class);
                        try {
                            if(versionBean.code == 0){
                                int mVersion = getVersion();
                                final int sVersion = Integer.parseInt(versionBean.data.get(0).android_version);
                                if(sVersion>mVersion){
                                    // 提示更新
                                    AlertDialog.Builder normalDialog =
                                            new AlertDialog.Builder(MainActivity.this);
                                    normalDialog.setTitle("新版本更新");
                                    normalDialog.setCancelable(false);
                                    normalDialog.setMessage(Html.fromHtml(versionBean.data.get(0).android_desc));
                                    normalDialog.setPositiveButton("确定",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String fileName = "shenyishengyishengduan.apk";
                                                  //  upApp(versionBean, fileName, sVersion);
                                                }
                                            });
                                    if(!isFinishing()){
                                        normalDialog.show();
                                    }else{
                                        return;
                                    }
//                                    showAlertDialog("提示",""+Html.fromHtml(versionBean.data.get(0).android_desc),"更新",new SuperDialog.OnClickPositiveListener(){
//                                        @Override
//                                        public void onClick(View v) {
//                                            String fileName = "shenyishengyishengduan.apk";
//                                            upApp(versionBean, fileName, sVersion);
//                                        }
//                                    });
                                }else{
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                            String fileName = "shenyishengyishengduan.apk";
                            File file=new File(directory,fileName);
                            if(file.exists()){
                                file.delete();
                            }
                            downloadFile(versionBean.data.get(0).android_url,fileName);
                        }
                            }
                        });
    }

    private void upApp(VersionBean versionBean, String fileName, int sVersion) {
        if (!fileIsExists(fileName)) {
            downloadFile(versionBean.data.get(0).android_url,fileName);
        }else{
            // 文件存在
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            int version  = getVersionNameFromApk(MainActivity.this,f.getAbsolutePath());
            if(version < sVersion){
                // 删除原来的文件
                String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file=new File(directory,fileName);
                if(file.exists()){
                    file.delete();
                }
                downloadFile(versionBean.data.get(0).android_url,fileName);
            }else{
                // 安装
                DownloadUtils downloadUtils = new DownloadUtils(MainActivity.this);
                downloadUtils.installAPK();
                finish();
            }
        }
    }


    /**
     * 从一个apk文件去获取该文件的版本信息
     *
     * @param context
     *            本应用程序上下文
     * @param archiveFilePath
     * APK文件的路径。如：/sdcard/download/XX.apk
     * @return
     */
    public static int getVersionNameFromApk(Context context, String archiveFilePath) {
        try{
            PackageManager pm = context.getPackageManager();
            PackageInfo packInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
            int version = packInfo.versionCode;
            return version;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }


    public void downloadFile(String url ,String fileName){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
       // dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
        progressDialog.setTitle("版本更新");
        progressDialog.setMax(100);

/*        final SuperDialog.Builder progressDialog = new SuperDialog.Builder(this);
        progressDialog.setCanceledOnTouchOutside(false).setCancelable(false).setWidth(0.7f).setBackgroundColor(getResources().getColor(R.color.white))
                .setProgress(0).setTitle("正在下载", getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title))
//                .setNegativeButton("取消", getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title), -1, new SuperDialog.OnClickNegativeListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //   OkHttpUtils.getInstance().cancelTag(this);
//                    }
//                })
        ;*/
    //    final DialogFragment dialogFragment = progressDialog.build();
        if(!isFinishing()){
            progressDialog.show();
        }else{
            return;
        }
       OkHttpUtils//
                .get()//
                .url(url).tag(this)//
                .build()
                .execute(new FileCallBack(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(), fileName)//
                {

                    @Override
                    public void onBefore(Request request, int id) {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        progressDialog.setProgress((int) (progress*100));
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.LogShitou("onError :" + e.getMessage());
                        ToastUtils.showLongToast(MainActivity.this, "下载失败");
                        // 删除原来的文件
                        String directory= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                        File file=new File(directory,"shenyishengyishengduan.apk");
                        if(file.exists()){
                            file.delete();
                        }
                    }

                    @Override
                    public void onResponse(File file, int id) {
                        LogUtil.LogShitou("onResponse :" + file.getAbsolutePath());
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        // 安装
                        DownloadUtils downloadUtils = new DownloadUtils(MainActivity.this);
                        downloadUtils.installAPK();
                        finish();
                    }
                });
    }











    public boolean fileIsExists(String fileName) {
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public int getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            int version = info.versionCode;
            return  version;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
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
