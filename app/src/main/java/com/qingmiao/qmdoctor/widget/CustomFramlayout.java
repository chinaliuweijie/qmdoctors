package com.qingmiao.qmdoctor.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.qingmiao.qmdoctor.R;

/**
 * Created by jokerlee on 16-3-14.
 */
public class CustomFramlayout extends FrameLayout {

    private static final String TAG = "CustomFramlayout";
    private static final boolean DEBUG = false;
    private LayoutParams layoutParams;


    public CustomFramlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        layoutParams = new LayoutParams(getContext(),attrs);
        return layoutParams;
    }

    public void setLayoutParamsMaxWidth(int maxWidth){
        if(layoutParams!=null){
            layoutParams.maxWidth = maxWidth;
        }
    }



    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams ? new LayoutParams((LayoutParams)p):new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        final int widthMode = MeasureSpec.getMode(widthSpec);
        final int heightMode = MeasureSpec.getMode(heightSpec);
        if (DEBUG && widthMode != MeasureSpec.AT_MOST) {
            Log.w(TAG, "onMeasure: widthSpec " + MeasureSpec.toString(widthSpec) +
                    " should be AT_MOST");
        }
        if (DEBUG && heightMode != MeasureSpec.AT_MOST) {
            Log.w(TAG, "onMeasure: heightSpec " + MeasureSpec.toString(heightSpec) +
                    " should be AT_MOST");
        }
        final int widthSize = MeasureSpec.getSize(widthSpec);
        final int heightSize = MeasureSpec.getSize(heightSpec);
        int maxWidth = widthSize;
        int maxHeight = heightSize;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            if (lp.maxWidth > 0 && lp.maxWidth < maxWidth) {
                maxWidth = lp.maxWidth;
            }
            if (lp.maxHeight > 0 && lp.maxHeight < maxHeight) {
                maxHeight = lp.maxHeight;
            }
        }

        final int wPadding = getPaddingLeft() + getPaddingRight();
        final int hPadding = getPaddingTop() + getPaddingBottom();
        maxWidth -= wPadding;
        maxHeight -= hPadding;

        int width = widthMode == MeasureSpec.EXACTLY ? widthSize : 0;
        int height = heightMode == MeasureSpec.EXACTLY ? heightSize : 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int childWidthSpec = makeChildMeasureSpec(maxWidth, lp.width);
            final int childHeightSpec = makeChildMeasureSpec(maxHeight, lp.height);

            child.measure(childWidthSpec, childHeightSpec);

            width = Math.max(width, Math.min(child.getMeasuredWidth(), widthSize - wPadding));
            height = Math.max(height, Math.min(child.getMeasuredHeight(), heightSize - hPadding));
        }
        setMeasuredDimension(width + wPadding, height + hPadding);
    }

    private int makeChildMeasureSpec(int maxSize, int childDimen) {
        final int mode;
        final int size;
        switch (childDimen) {
            case LayoutParams.WRAP_CONTENT:
                mode = MeasureSpec.AT_MOST;
                size = maxSize;
                break;
            case LayoutParams.MATCH_PARENT:
                mode = MeasureSpec.EXACTLY;
                size = maxSize;
                break;
            default:
                mode = MeasureSpec.EXACTLY;
                size = Math.min(maxSize, childDimen);
                break;
        }
        return MeasureSpec.makeMeasureSpec(size, mode);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        @ViewDebug.ExportedProperty(category = "layout")
        public int maxWidth;

        @ViewDebug.ExportedProperty(category = "layout")
        public int maxHeight;

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(LayoutParams other) {
            super(other);

            maxWidth = other.maxWidth;
            maxHeight = other.maxHeight;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.CustomFrameLayoutAttr, 0, 0);
            maxWidth = a.getDimensionPixelSize(
                    R.styleable.CustomFrameLayoutAttr_layout_maxWidth, 0);
            maxHeight = a.getDimensionPixelSize(
                    R.styleable.CustomFrameLayoutAttr_layout_maxHeight, 0);
            a.recycle();
        }
    }
}
