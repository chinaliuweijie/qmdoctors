package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.CircleCateBean;

/**
 * Created by Administrator on 2017/5/4.
 */

public class RenalCirclrAdapter extends ListBaseAdapter<CircleCateBean.DataBean>{


    public RenalCirclrAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.circle_list_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        TextView circleName = holder.getView(R.id.circle_list_item_name);
        circleName.setText(getDataList().get(position).q_name);
    }
}
