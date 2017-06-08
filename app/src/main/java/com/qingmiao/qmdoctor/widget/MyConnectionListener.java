package com.qingmiao.qmdoctor.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.NetUtils;
import com.qingmiao.qmdoctor.activity.LoginActivity;
import com.qingmiao.qmdoctor.activity.SettingActivity;
import com.qingmiao.qmdoctor.adapter.Contact;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.MyApplication;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.utils.UIHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * 实现ConnectionListener接口-----监听网络状态---在另外一台手机登录----账号被移除
 */
public class MyConnectionListener implements EMConnectionListener {
    Context context;

    @Override
    public void onConnected() {
    }

    public MyConnectionListener(Context context) {
        this.context = context;
    }

    @Override
    public void onDisconnected(final int error) {
        UIHandler.get().post(new Runnable() {
            @Override
            public void run() {
                if (error == EMError.USER_REMOVED) {
                    // 显示帐号已经被移除
                    ToastUtils.showLongToast(context, "帐号已经被移除");
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((MyApplication)MyApplication.context).saveLogin(null);
                    PrefUtils.putString(context,"did","");
                    PrefUtils.putString(context,"token","");
                    PrefUtils.putBean(context, KeyOrValueGlobal.LOGINBEAN,null);
                    EventBus.getDefault().post("changeData");
                    // 通知患者界面刷新
                    EventBus.getDefault().post("updata_patient");
                } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                    // 显示帐号在其他设备登录
                    ToastUtils.showLongToast(context, "帐号在其他设备登录,被迫下线");
                    EMClient.getInstance().logout(true, new EMCallBack() {
                        @Override
                        public void onSuccess() {
                            Log.e("main", "下线成功了");
                        }

                        @Override
                        public void onError(int i, String s) {
                            Log.e("main", "下线失败了！" + s);
                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((MyApplication)MyApplication.context).saveLogin(null);
                    PrefUtils.putString(context,"did","");
                    PrefUtils.putString(context,"token","");
                    PrefUtils.putBean(context, KeyOrValueGlobal.LOGINBEAN,null);
                    EventBus.getDefault().post("changeData");
                    // 通知患者界面刷新
                    EventBus.getDefault().post("updata_patient");
                } else {
                    if (NetUtils.hasNetwork(context)) {
//                        //连接不到聊天服务器
                     //  Toast.makeText(context, "连接不到聊天服务器", Toast.LENGTH_SHORT).show();
                    } else {
                        //当前网络不可用，请检查网络设置
                     //   ToastUtils.showLongToast(context, "当前网络不可用，请检查网络设置");
                    }
                }
            }
        });
    }
}