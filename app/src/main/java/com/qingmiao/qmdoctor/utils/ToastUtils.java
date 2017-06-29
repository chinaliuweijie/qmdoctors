package com.qingmiao.qmdoctor.utils;

import android.content.Context;
import android.text.*;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qingmiao.qmdoctor.R;

/**
 * Created by Administrator on 2017/4/11.
 */
public class ToastUtils {
    private static Toast toast;

    private static View view;

    private ToastUtils() {
    }

    private static void getToast(Context context) {
        if (toast == null) {
            toast = new Toast(context);
        }
        if (view == null) {
            view = Toast.makeText(context, "", Toast.LENGTH_SHORT).getView();
            TextView tv = (TextView) view.findViewById(android.R.id.message);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.tv_sitem_title));
        }
        toast.setView(view);
    }

    public static void showShortToast(Context context, CharSequence msg) {
        if(context!=null  && context.getApplicationContext()!=null)
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, int resId) {
        if(context!=null  && context.getApplicationContext()!=null)
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, CharSequence msg) {
        if(!android.text.TextUtils.isEmpty(msg) && context!=null  && context.getApplicationContext()!=null){
        showToast(context.getApplicationContext(), msg, Toast.LENGTH_SHORT);
        }
    }

    public static void showLongToast(Context context, int resId) {
        if(context!=null  && context.getApplicationContext()!=null)
        showToast(context.getApplicationContext(), resId, Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, CharSequence msg, int duration) {
        try {
            getToast(context);
            toast.setText(msg);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 10);
            toast.show();
        } catch (Exception e) {
            LogUtil.LogShitou(e.getMessage());
        }
    }

    private static void showToast(Context context, int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            getToast(context);
            toast.setText(resId);
            toast.setDuration(duration);
          //  toast.setGravity(Gravity.BOTTOM, 0, 20);
            toast.show();
        } catch (Exception e) {
            LogUtil.LogShitou(e.getMessage());
        }
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

}