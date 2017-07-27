package com.qingmiao.qmdoctor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

/**
 * company : 青苗
 * Created by 刘伟杰 on 2017/3/8.
 */

public class CustomLL extends LinearLayout implements AdapterView.OnItemClickListener {
    public CustomLL(Context context) {
        super(context);
    }

    public CustomLL(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLL(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    }
}
