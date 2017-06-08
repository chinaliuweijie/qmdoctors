package com.qingmiao.qmdoctor.adapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.TagsListBean;
import java.util.List;


public class LibelInfoMenuAdapter  extends RecyclerView.Adapter<LibelInfoMenuAdapter.DefaultViewHolder> {

    private List<TagsListBean.Tags> tagses;

    public LibelInfoMenuAdapter(List<TagsListBean.Tags> tagses){
        this.tagses = tagses;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }



    @Override
    public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_libel_info, parent, false);
        DefaultViewHolder viewHolder = new DefaultViewHolder(view);
        viewHolder.mOnItemClickListener = mOnItemClickListener;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.setTitle(tagses.get(position).tag_name);
        holder.setRemark(tagses.get(position).uids);
    }

    @Override
    public int getItemCount() {
        return tagses == null ? 0 : tagses.size();
    }

    static class DefaultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvRemark;
        OnItemClickListener mOnItemClickListener;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.title);
            tvRemark = (TextView) itemView.findViewById(R.id.remark);
        }

        public void setTitle(String title) {
            this.tvTitle.setText(title);
        }

        public void setRemark(String remark){
            this.tvRemark.setText(remark);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);

    }

}
