package com.qingmiao.qmdoctor.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.widget.IconFontTextview;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * company : 青苗
 * Created by  on 2017/3/21.
 */

public class PatientDocAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = View.inflate(viewGroup.getContext(), R.layout.patient_doc_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.renal_listView_iv)
        ImageView renalListViewIv;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_futitle)
        TextView tvFutitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.icontv_delete)
        IconFontTextview icontvDelete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
