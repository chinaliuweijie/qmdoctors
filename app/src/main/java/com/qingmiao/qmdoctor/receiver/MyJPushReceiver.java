package com.qingmiao.qmdoctor.receiver;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.qingmiao.qmdoctor.bean.JpushBean;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/6/1.
 */

public class MyJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
       // ToastUtils.showLongToast(context,"收到极光的消息"+intent.getAction());
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();


        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            LogUtil.LogShitou(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            LogUtil.LogShitou(TAG, "接受到推送下来的自定义消息");
            processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            LogUtil.LogShitou(TAG, "接受到推送下来的通知");
            receivingNotification(context,bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            LogUtil.LogShitou(TAG, "用户点击打开了通知");

            openNotification(context,bundle);

        } else {
            LogUtil.LogShitou(TAG, "qingmiao +++ Unhandled i ntent - " + intent.getAction());

        }
    }


    private void processCustomMessage(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);

    }

    // When received message, increase unread number for Recent Chat
    private void unreadMessage(final String friend, final String channel) {

    }


    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        LogUtil.LogShitou(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        LogUtil.LogShitou(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        LogUtil.LogShitou(TAG, "extras : " + extras);
        if(!TextUtils.isEmpty(extras)){
            JpushBean jpushBean = GsonUtil.getInstance().fromJson(extras, JpushBean.class);


        }else{
            // 添加好友的推送
            // 通知患者界面刷新
            EventBus.getDefault().post("updata_patient");
        }
    }

    private void openNotification(Context context, Bundle bundle){
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String myValue = "";
        try {
            JSONObject extrasJson = new JSONObject(extras);
            myValue = extrasJson.optString("myKey");
        } catch (Exception e) {
            LogUtil.LogShitou(TAG, "Unexpected: extras is not a valid json");
            return;
        }
//        if (TYPE_THIS.equals(myValue)) {
//            Intent mIntent = new Intent(context, ThisActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mIntent);
//        } else if (TYPE_ANOTHER.equals(myValue)){
//            Intent mIntent = new Intent(context, AnotherActivity.class);
//            mIntent.putExtras(bundle);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(mIntent);
//        }
    }
}