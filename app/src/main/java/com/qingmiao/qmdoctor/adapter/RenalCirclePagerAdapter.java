package com.qingmiao.qmdoctor.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.CircleCatePagerBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RenalCirclePagerAdapter extends BaseAdapter {
    private Activity activity;
    private List<CircleCatePagerBean.DataBean> mList;

    public RenalCirclePagerAdapter(Activity activity, List<CircleCatePagerBean.DataBean> mList) {
        this.activity = activity;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CircleCatePagerBean.DataBean getItem(int i) {
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
            view = View.inflate(activity, R.layout.renal_listview_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        CircleCatePagerBean.DataBean item = getItem(i);
        viewHolder.tvTitle.setText(item.title);
        viewHolder.tvFutitle.setText(item.description);
        viewHolder.tvContent.setText(item.content);
//        viewHolder.tvTitleTime.setText(item_contact.updatetime);
        return view;
    }


    static class ViewHolder {
        @BindView(R.id.renal_listView_iv)
        ImageView renalListViewIv;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.renal_listView_time)
        TextView tvTitleTime;
        @BindView(R.id.tv_futitle)
        TextView tvFutitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.renal_listView_iv1)
        ImageView renalListViewIv1;
        @BindView(R.id.renal_listView_iv2)
        ImageView renalListViewIv2;
        @BindView(R.id.renal_listView_iv3)
        ImageView renalListViewIv3;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
