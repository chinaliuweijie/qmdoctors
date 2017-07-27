package com.qingmiao.qmdoctor.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.CircleCateBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * company : 青苗
 * Created by  on 2017/2/27.
 */

public class RenalCircleListAdapter extends BaseAdapter {
    private Activity activity;
    private List<CircleCateBean.DataBean> mList;

    public RenalCircleListAdapter(Activity activity, List<CircleCateBean.DataBean> mList) {
        this.activity = activity;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CircleCateBean.DataBean getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(activity, R.layout.circle_list_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        String q_name = getItem(i).q_name;
        viewHolder.circleListItemName.setText(q_name);
        return view;
    }

    public static class ViewHolder {
        @BindView(R.id.circle_list_item_name)
        TextView circleListItemName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
