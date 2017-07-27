package com.qingmiao.qmdoctor.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.hyphenate.util.DensityUtil;

/**
 * company : 青苗
 * Created by 刘伟杰 on 2017/3/9.
 */

public class DeleteListView extends SwipeMenuListView {
    private Context context;

    public DeleteListView(Context context) {
        this(context, null);
    }

    public DeleteListView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DeleteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();

    }

    private void initView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item_contact
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                // set item_contact background
                openItem.setBackground(new ColorDrawable(Color.RED));
                // set item_contact width
                openItem.setWidth(DensityUtil.dip2px(context,78));
                // set item_contact title
                openItem.setTitle("删除");
                // set item_contact title fontsize
                openItem.setTitleSize(16);
                // set item_contact title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

            }
        };

        this.setMenuCreator(creator);
        this.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteItem(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }


    public interface OnDeleteListener {
        public void onDelete(int position);
    }

    public void setParams(String uId, OnDeleteListener onDeleteListener) {

    }

    private void deleteItem(final int position) {

    }
}
