package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.util.DateUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.CircleCatePagerBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.utils.TimeUtils;

import java.util.Date;

/**
 * Created by Administrator on 2017/5/5.
 */

public class RenalCircleAdapter extends ListBaseAdapter<CircleCatePagerBean.DataBean> {


    public RenalCircleAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.renal_listview_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        ImageView ivRenal = holder.getView(R.id.renal_listView_iv);
        if(TextUtils.isEmpty(getDataList().get(position).avatar)){
            GlideUtils.LoadNullCircleImage(mContext,ivRenal);
        }else{
            GlideUtils.LoadCircleImage(mContext,getDataList().get(position).avatar,ivRenal);
        }
        TextView tvTitle = holder.getView(R.id.tv_title);
        tvTitle.setText(getDataList().get(position).user_name);
        TextView tvFutitle = holder.getView(R.id.tv_futitle);
        tvFutitle.setText(getDataList().get(position).title);
        TextView tvContent = holder.getView(R.id.tv_content);
        tvContent.setText(getDataList().get(position).description);
        LinearLayout llPhoto = holder.getView(R.id.ll_photo);
        if(getDataList().get(position).thumbs!=null&&getDataList().get(position).thumbs.size()>0){
            llPhoto.setVisibility(View.VISIBLE);
            ImageView iv1 = holder.getView(R.id.renal_listView_iv1);
            ImageView iv2 = holder.getView(R.id.renal_listView_iv2);
            ImageView iv3 = holder.getView(R.id.renal_listView_iv3);
            if(getDataList().get(position).thumbs.size()==0){
                iv1.setVisibility(View.INVISIBLE);
                iv2.setVisibility(View.INVISIBLE);
                iv3.setVisibility(View.INVISIBLE);
            }else if(getDataList().get(position).thumbs.size()==1){
                GlideUtils.LoadImage(mContext,getDataList().get(position).thumbs.get(0),iv1);
             //   GlideUtils.LoadImage(mContext,UrlGlobal.SERVER_URL+ getDataList().get(position).thumbs.get(1),iv2);
                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.INVISIBLE);
                iv3.setVisibility(View.INVISIBLE);
            }else if(getDataList().get(position).thumbs.size()==2){
                GlideUtils.LoadImage(mContext,getDataList().get(position).thumbs.get(0),iv1);
                GlideUtils.LoadImage(mContext, getDataList().get(position).thumbs.get(1),iv2);
             //   GlideUtils.LoadImage(mContext,UrlGlobal.SERVER_URL+ getDataList().get(position).thumbs.get(2),iv3);
                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.VISIBLE);
                iv3.setVisibility(View.INVISIBLE);
            }else if(getDataList().get(position).thumbs.size()>2){
                iv1.setVisibility(View.VISIBLE);
                iv2.setVisibility(View.VISIBLE);
                iv3.setVisibility(View.VISIBLE);
                GlideUtils.LoadImage(mContext,getDataList().get(position).thumbs.get(0),iv1);
                GlideUtils.LoadImage(mContext,getDataList().get(position).thumbs.get(1),iv2);
                GlideUtils.LoadImage(mContext, getDataList().get(position).thumbs.get(2),iv3);
            }
        }else{
            llPhoto.setVisibility(View.GONE);
        }
        TextView tvTime = holder.getView(R.id.renal_listView_time);
        if(!TextUtils.isEmpty(getDataList().get(position).updatetime)) {
            long l = Long.parseLong(getDataList().get(position).updatetime + "000");
            String timestampString = DateUtils.getTimestampString(new Date(l));
            tvTime.setText(timestampString);
        }
    }
}
