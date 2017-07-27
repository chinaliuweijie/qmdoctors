package com.qingmiao.qmdoctor.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.util.DateUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.CommunityListDean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.hyphenate.easeui.utils.GlideUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * company : 青苗
 * Created by  on 2017/2/24.
 */

public class CommentAdapter extends BaseAdapter {
    private Activity activity;
    private List<CommunityListDean.DataBean> mList;

    public CommentAdapter(Activity activity, List<CommunityListDean.DataBean> mList) {
        this.activity = activity;
        this.mList = mList;
        System.out.println(mList.size());
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CommunityListDean.DataBean getItem(int i) {
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
            view = View.inflate(activity, R.layout.home_list_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        CommunityListDean.DataBean item = getItem(i);
        if (item != null) {
//            Glide.with(activity)
//                    .load(UrlGlobal.SERVER_URL + "/" + item.thumb)
//                    .placeholder(R.drawable.icon_photo_placeholder)
//                    .error(R.drawable.icon_photo_placeholder)
//                    .into(viewHolder.homeListItemIv);
            GlideUtils.LoadImage(activity, item.thumb,viewHolder.homeListItemIv);
            viewHolder.homeListItemTitle.setText(item.title);
            viewHolder.homeListItemContent.setText(item.description);
            long l = Long.parseLong(item.updatetime + "000");
            String timestampString = DateUtils.getTimestampString(new Date(l));
            viewHolder.homeListItemTime.setText(timestampString);
        }
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.home_list_item_iv)
        ImageView homeListItemIv;
        @BindView(R.id.home_list_item_title)
        TextView homeListItemTitle;
        @BindView(R.id.home_list_item_content)
        TextView homeListItemContent;
        @BindView(R.id.home_list_item_time)
        TextView homeListItemTime;
        @BindView(R.id.home_list_item_rightTv)
        TextView homeListItemRightTv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
