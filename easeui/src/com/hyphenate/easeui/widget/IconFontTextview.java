package com.hyphenate.easeui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * company : 青苗
 * Created by 杜新 on 2017/3/6.
 */

public class IconFontTextview extends TextView {
    public IconFontTextview(Context context) {
        this(context,null);
    }

    public IconFontTextview(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public IconFontTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface iconfont = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        setTypeface(iconfont);
    }
}
