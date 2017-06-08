package com.qingmiao.qmdoctor.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by: jpj
 * Created time: 17/2/23
 * Description:自定义，无法滚动的ViewPager
 */
public class NoScrollViewPager extends ViewPager {

	public NoScrollViewPager(Context context) {

		super(context);
	}

	public NoScrollViewPager(Context context, AttributeSet attrs) {

		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		return false;
	}
}
