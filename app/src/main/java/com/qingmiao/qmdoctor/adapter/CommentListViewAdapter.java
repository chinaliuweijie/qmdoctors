package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.util.DateUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.CommentBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.utils.TimeUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/5/8.
 */

public class CommentListViewAdapter extends ListBaseAdapter<CommentBean.DataBean> {


    public CommentListViewAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.comment_list_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        TextView tvName = holder.getView(R.id.circleDetail_name_tv);
        tvName.setText(getDataList().get(position).user_name);
        TextView tvContent = holder.getView(R.id.content);
        tvContent.setText(getDataList().get(position).content);
        String time = getDataList().get(position).time;
        TextView tvTime = holder.getView(R.id.time);
        long l = Long.parseLong(time + "000");
        String timestampString = DateUtils.getTimestampString(new Date(l));
        tvTime.setText(timestampString);
        ImageView iv_icon = holder.getView(R.id.circleDetail_icon_iv);
        if(!TextUtils.isEmpty(getDataList().get(position).avatar)){
            GlideUtils.LoadCircleImage(mContext, getDataList().get(position).avatar,iv_icon);
        }else{
            GlideUtils.LoadNullCircleImage(mContext,iv_icon);
        }
    }
}
