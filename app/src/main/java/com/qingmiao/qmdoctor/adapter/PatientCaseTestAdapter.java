package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.PatientAllInfoBean;
import com.qingmiao.qmdoctor.utils.TimeUtils;

/**
 * Created by Administrator on 2017/4/26.
 */

public class PatientCaseTestAdapter extends ListBaseAdapter<PatientAllInfoBean.PatientData.PatientUserSick> {


    public PatientCaseTestAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.view_patient_sick_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        TextView tvSickTitle = holder.getView(R.id.tv_sick_title);
        tvSickTitle.setText(getDataList().get(position).sick_title);
        TextView tvSickTime =  holder.getView(R.id.tv_sick_time);
        tvSickTime.setText(TimeUtils.getStrTime(getDataList().get(position).time));
    }
}
