package com.qingmiao.qmdoctor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

public class InitApkBroadCastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            System.out.println("监听到系统广播添加");

        }

        if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            System.out.println("监听到系统广播移除");

        }

        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            System.out.println("监听到系统广播替换");

        }
    }

}