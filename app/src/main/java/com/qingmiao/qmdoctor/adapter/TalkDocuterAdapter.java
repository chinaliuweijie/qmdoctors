package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.CircleCateBean;
import com.qingmiao.qmdoctor.bean.ContactModel;
import com.qingmiao.qmdoctor.bean.TalkDocuterBean;
import com.qingmiao.qmdoctor.fragment.ContactRecyclerFragment;
import com.qingmiao.qmdoctor.widget.SwipeMenuView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TalkDocuterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    List<TalkDocuterBean> talkDocuterBeen = new ArrayList<>();


    public enum ITEM_TYPE {
        ITEM_TYPE_TOOP,
        ITEM_TYPE_CONTACT
    }

    public TalkDocuterAdapter(Context context) {
        mContext = context;
    }

    public List<TalkDocuterBean> getAllDatas() {
        return talkDocuterBeen;
    }

    public void setAllDatas(List<TalkDocuterBean> talkDocuterBeen) {
        this.talkDocuterBeen.clear();
        this.talkDocuterBeen.addAll(talkDocuterBeen);
        notifyDataSetChanged();
    }

    public void addAll(Collection<TalkDocuterBean> list) {
        int lastIndex = this.talkDocuterBeen.size();
        if (this.talkDocuterBeen.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }


    public void updateListView(List<TalkDocuterBean> talkDocuterBeen){
        talkDocuterBeen.clear();
        talkDocuterBeen.addAll(talkDocuterBeen);
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_TOOP.ordinal()) {
            View view = View.inflate(mContext,R.layout.circle_list_top,null);
            return new CircleTopHolder(view);
        } else {
            View view = View.inflate(mContext,R.layout.circle_list_item,null);
            return new CirclrCountHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if (holder instanceof CircleTopHolder) {
            ((CircleTopHolder) holder).tvTitle.setText(talkDocuterBeen.get(position).title);
            if("推荐圈子".equals(talkDocuterBeen.get(position).title)){
                ((CircleTopHolder) holder).tvIcon.setText(R.string.icons_head);
            }else if("全部圈子".equals(talkDocuterBeen.get(position).title)){
                ((CircleTopHolder) holder).tvIcon.setText(R.string.icons_outher);
            }
        } else if (holder instanceof CirclrCountHolder) {
            CirclrCountHolder circlrCountHolder = (CirclrCountHolder) holder;
            circlrCountHolder.tvName.setText(talkDocuterBeen.get(position).itemDataBean.q_name);
            circlrCountHolder.rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onClickCircelItem!=null){
                        onClickCircelItem.onClickItem(talkDocuterBeen.get(position).itemDataBean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return talkDocuterBeen.get(position).type;
    }

    @Override
    public int getItemCount() {
        return talkDocuterBeen == null ? 0 : talkDocuterBeen.size();
    }

    public class CircleTopHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvIcon;
        CircleTopHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.tv_top_title);
            tvIcon = (TextView) view.findViewById(R.id.iv_icon);
        }
    }

    public class CirclrCountHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        RelativeLayout rlItem;
        CirclrCountHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.circle_list_item_name);
            rlItem = (RelativeLayout) view.findViewById(R.id.rl_item);
        }
    }

    private OnClickCircelItem onClickCircelItem;

    public void setOnClickCircelItem(OnClickCircelItem onClickCircelItem) {
        this.onClickCircelItem = onClickCircelItem;
    }

    public interface OnClickCircelItem{
        public void onClickItem(CircleCateBean.DataBean itemDataBean);
    }
}
