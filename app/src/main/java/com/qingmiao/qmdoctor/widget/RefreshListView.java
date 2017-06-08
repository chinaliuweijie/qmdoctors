package com.qingmiao.qmdoctor.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.qingmiao.qmdoctor.R;

/**
 * company : 青苗
 * Created by 杜新 on 2017/3/14.
 */
public class RefreshListView extends ListView {
    private View footerView;
    private int footerViewHeight;
    private boolean isLoadingMore = false;
    private float y;
    private int v1;

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RefreshListView(Context context) {
        this(context, null);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFooterView();
    }


    private void initFooterView() {
        footerView = View.inflate(getContext(), R.layout.foot_refresh_listview, null);
        footerView.measure(0, 0);
        footerViewHeight = footerView.getMeasuredHeight();
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        addFooterView(footerView);
    }

    public void setRefreshed() {
        footerView.setPadding(0, -footerViewHeight, 0, 0);
        isLoadingMore = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                float v = y - moveY;
                if (v > 0 && getLastVisiblePosition() == (getCount() - 1) && !isLoadingMore) {
                    v1 = (int) (v - footerViewHeight);
                    if (v1 >= 0) {
                        v1 = 0;
                    }
                    footerView.setPadding(0, v1, 0, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (v1 >= -footerViewHeight / 2 && getLastVisiblePosition() == (getCount() - 1)) {
                    isLoadingMore = true;
                    footerView.setPadding(0, 0, 0, 0);
                    setSelection(getCount());
                    if (listener != null) {
                        listener.onLoadingMore();
                    }
                } else {
                    footerView.setPadding(0, -footerViewHeight, 0, 0);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private OnRefreshListener listener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.listener = listener;
    }

    public interface OnRefreshListener {
        void onLoadingMore();
    }
}
