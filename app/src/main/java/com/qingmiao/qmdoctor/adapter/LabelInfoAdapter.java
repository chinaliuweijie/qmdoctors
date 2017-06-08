package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.TagsListBean;
import com.qingmiao.qmdoctor.widget.SwipeMenuView;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class LabelInfoAdapter extends ListBaseAdapter<TagsListBean.Tags> {


    public LabelInfoAdapter(Context context, List<TagsListBean.Tags> tagses) {
        super(context);
        setDataList(tagses);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_libel_info;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, final int position) {
        SwipeMenuView swipe = holder.getView(R.id.swipe_content);
        swipe.setSwipeEnable(true);
        swipe.setIos(false).setLeftSwipe(true);
        TextView title = holder.getView(R.id.title);
        title.setText(getDataList().get(position).tag_name+"("+getDataList().get(position).count+")");
      //  TextView tvRemark = holder.getView(R.id.remark);
       // tvRemark.setText(getDataList().get(position).uids);
        Button btnDelete = holder.getView(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onItemMenuClick(getDataList().get(position),position);
                }
            }
        });

        LinearLayout llContent = holder.getView(R.id.ll_content);
        llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(getDataList().get(position),position);
                }
            }
        });

    }


    private OnItemClickListener mOnItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onItemClick(TagsListBean.Tags  tags ,int position);
        void onItemMenuClick(TagsListBean.Tags  tags ,int position);
    }
}
