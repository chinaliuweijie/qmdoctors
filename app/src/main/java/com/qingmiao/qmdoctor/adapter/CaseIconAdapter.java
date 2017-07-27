package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.CheckItemDataBean;
import com.qingmiao.qmdoctor.widget.LineView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * company : 青苗
 * Created by  on 2017/4/10.
 */

public class CaseIconAdapter extends BaseAdapter {
    private Context context;
    private List<CheckItemDataBean.CheckItemBean> mList;

    public CaseIconAdapter(Context context, List<CheckItemDataBean.CheckItemBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CheckItemDataBean.CheckItemBean getItem(int i) {
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
            view = View.inflate(context, R.layout.caseicon_list_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            viewHolder.lineView.setDate();
        }
        ArrayList<Double> integerArrayList = new ArrayList<>();
        ArrayList<String> timeArr = new ArrayList<>();
        CheckItemDataBean.CheckItemBean item = getItem(i);
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd");
        for (String s : item.time) {

            long l = Long.parseLong(s + "000");
            String time = sdr.format(new Date(l));
            timeArr.add(time);

        }

        viewHolder.lineView.setTimeText(timeArr);
        viewHolder.textView.setText(item.field_cn);

        String[] demos = item.demo.split(",");

        if (demos.length >= 2) {

            viewHolder.lineView.setMaxScore(30);
            viewHolder.lineView.setMinScore(10);
            for (String s : item.data) {
                switch (s) {
                    case "阴性":
                        integerArrayList.add(30.0);
                        break;
                    case "可疑阳性(±)":
                        integerArrayList.add(20.0);
                        break;
                    case "阳性":
                        integerArrayList.add(10.0);
                        break;
                }
            }
            viewHolder.lineView.setScore(integerArrayList);
        } else {

            String[] v = item.demo.split("-");
            for (String s : item.data) {
                integerArrayList.add(Double.parseDouble(s));
            }

            double minValue = Double.parseDouble(v[0]);
            double maxValue = Double.parseDouble(v[1]);

            viewHolder.lineView.setMaxScore(maxValue);
            viewHolder.lineView.setMinScore(minValue);
            double max = (double) Collections.max(integerArrayList);
            viewHolder.lineView.setMaxValue(max);
            viewHolder.lineView.setScore(integerArrayList);
        }
        viewHolder.lineView.setDate();
        viewHolder.lineView.invalidate();
        return view;
    }

    static class ViewHolder {
        @BindView(R.id.line_view)
        LineView lineView;
        @BindView(R.id.caseicon_tv)
        TextView textView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
