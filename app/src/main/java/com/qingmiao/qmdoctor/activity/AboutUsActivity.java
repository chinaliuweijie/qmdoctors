package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mylhyl.superdialog.SuperDialog;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.VersionBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.DownloadUtils;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        tvCenter.setText("关于我们");
        ivLeft.setVisibility(View.VISIBLE);
        tvVersion.setText("肾一生 TVer." + getVersionStr());
    }

    @OnClick({R.id.ll_about_notification, R.id.ll_about_intro, R.id.ll_about_updata,R.id.tv_rule})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_about_notification:
                Intent sintent = new Intent(AboutUsActivity.this,UserRuleWebActivity.class);
                sintent.putExtra("title","系统通知");
                sintent.putExtra("url",UrlGlobal.SYSNOT);
                startActivity(sintent);
                break;
            case R.id.ll_about_intro:
                Intent intent = new Intent(AboutUsActivity.this,UserRuleWebActivity.class);
                intent.putExtra("title","功能介绍");
                intent.putExtra("url",UrlGlobal.FUNCINTRO);
           //     intent.putExtra("url","https://api.green-bud.cn/html_statics/medical_tools.html");
                startActivity(intent);
                break;
            //更新
            case R.id.ll_about_updata:

                OkHttpUtils.post()
                        .url(UrlGlobal.GET_VERSION)
                        .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                        .addParams("type","2")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                System.out.println(e.toString());
                                ToastUtils.showLongToast(AboutUsActivity.this,"当前已经是最新版本!");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    final VersionBean versionBean = GsonUtil.getInstance().fromJson(response,VersionBean.class);
                                    if(versionBean.code == 0){
                                        int mVersion = getVersion();
                                        int sVersion = Integer.parseInt(versionBean.data.get(0).android_version);
                                        if(sVersion>mVersion){
                                            showAlertDialog("提示", "发现新版本,是否更新!", "取消", new SuperDialog.OnClickNegativeListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            }, "确定", new SuperDialog.OnClickPositiveListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    DownloadUtils downloadUtils = new DownloadUtils(AboutUsActivity.this);
                                                    downloadUtils.download(versionBean.data.get(0).android_url);
                                                }
                                            });
                                        }else{
                                            ToastUtils.showLongToast(AboutUsActivity.this,"当前已经是最新版本!");
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                    ToastUtils.showLongToast(AboutUsActivity.this,"当前已经是最新版本!");
                                }
                            }
                        });

                break;
            case R.id.tv_rule:
                Intent rintent = new Intent(AboutUsActivity.this,UserRuleWebActivity.class);
                rintent.putExtra("title","用户协议");
                rintent.putExtra("url",UrlGlobal.USER_RULE);
                startActivity(rintent);
                break;

        }
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


    public String getVersionStr() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.0.0";
        }
    }

}
