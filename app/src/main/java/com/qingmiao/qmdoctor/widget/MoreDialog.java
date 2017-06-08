package com.qingmiao.qmdoctor.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.qingmiao.qmdoctor.R;

/**
 * company : 青苗
 * Created by 杜新 on 2017/3/21.
 */

public class MoreDialog extends Dialog {

    public MoreDialog(Context context) {
        super(context, R.style.dialog);

        setContentView(R.layout.patient_data_more);
        Window window = getWindow();//获取dialog所在的窗口对象
        WindowManager.LayoutParams attributes = window.getAttributes();//获取当前窗口的属性(布局参数)
        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;//靠下居中显示
        window.setAttributes(attributes);//重新设置布局参数
    }
}
