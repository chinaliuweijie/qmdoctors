package com.qingmiao.qmdoctor.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.base.ListBaseAdapter;
import com.qingmiao.qmdoctor.base.SuperViewHolder;
import com.qingmiao.qmdoctor.bean.PatientAllInfoBean;
import com.qingmiao.qmdoctor.utils.DensityUtil;
import com.qingmiao.qmdoctor.utils.TimeUtils;

public class PatientCaseDetailedAdapter extends ListBaseAdapter<PatientAllInfoBean.PatientData.PatientUserSick> {

    public PatientCaseDetailedAdapter(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.patient_check_item;
    }

    @Override
    public void onBindItemHolder(SuperViewHolder holder, int position) {
        try {
            LinearLayout llItem = holder.getView(R.id.ll_patient_usersick);
            PatientAllInfoBean.PatientData.PatientUserSick patientUserSick = getDataList().get(position);
            llItem.removeAllViews();
            if(!TextUtils.isEmpty(patientUserSick.sick_msg)){
                String sick_msg = com.qingmiao.qmdoctor.utils.TextUtils.getEStr2CStr(patientUserSick.sick_msg);
                patientUserSick.sick_msg = sick_msg;
                String[] splits = patientUserSick.sick_msg.split(",");
                int iLength = splits.length +1 ;
                int margin = DensityUtil.dip2px(mContext,12);
                for (int i = 0; i < iLength/2; i++) {
                    // 创建横向的线性布局
                    LinearLayout linearLayout = new LinearLayout(mContext);
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayout.setLayoutParams(layoutParams);
                    layoutParams.setMargins(margin,margin,margin,margin);
                    // 设置两个TextView

                    LinearLayout linearLayout1 = new LinearLayout(mContext);
                    linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams layoutParamsChild = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsChild.weight = 1;
                    linearLayout1.setLayoutParams(layoutParamsChild);
                    linearLayout.addView(linearLayout1);

                    LinearLayout linearLayout1Child = new LinearLayout(mContext);
                    linearLayout1Child.setOrientation(LinearLayout.HORIZONTAL);
                    LinearLayout.LayoutParams l1Child = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    linearLayout1Child.setLayoutParams(l1Child);
                    linearLayout1.addView(linearLayout1Child);
                    // 添加 TextView
                    String msg = splits[i*2+0];
                    String [] count = msg.split(":");
                    TextView tv0 = new TextView(mContext);
                    LinearLayout.LayoutParams tv0Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tv0Params.weight = 1;
                    tv0.setLayoutParams(tv0Params);
                    tv0.setTextColor(mContext.getResources().getColor(R.color.character));
                    tv0.setTextSize(16);
                    tv0.setText(count[0]);
                    tv0.setSingleLine(true);
                    tv0.setEllipsize(TextUtils.TruncateAt.END);
                    linearLayout1Child.addView(tv0);

                    TextView tv1 = new TextView(mContext);
                    LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    tv1.setLayoutParams(tv1Params);
                    tv1.setTextColor(mContext.getResources().getColor(R.color.character));
                    tv1.setTextSize(16);
                    tv1.setText(":"+count[1]);
                    linearLayout1Child.addView(tv1);

                    if(splits[i*2+0].contains(":red")&& splits[i*2+0].endsWith(":red")){
                        // 变颜色
                        tv1.setTextColor(mContext.getResources().getColor(R.color.red));
                    }
                    if(i==(iLength/2-1) && (splits.length%2 ==1)){

                    }else{
                        LinearLayout linearLayout2 = new LinearLayout(mContext);
                        linearLayout2.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams layoutParamsChild2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsChild2.weight = 1;
                        layoutParamsChild2.leftMargin = 20;
                        linearLayout2.setLayoutParams(layoutParamsChild2);
                        linearLayout.addView(linearLayout2);

                        LinearLayout linearLayout2Child = new LinearLayout(mContext);
                        linearLayout2Child.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams l2Child = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        linearLayout2Child.setLayoutParams(l2Child);
                        linearLayout2.addView(linearLayout2Child);

                        String msg1 = splits[i*2+1];
                        String [] count1 = msg1.split(":");
                        TextView tv3 = new TextView(mContext);
                        LinearLayout.LayoutParams tv3Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        tv3Params.weight = 1;
                        tv3.setLayoutParams(tv3Params);
                        tv3.setTextColor(mContext.getResources().getColor(R.color.character));
                        tv3.setTextSize(16);
                        tv3.setText(count1[0]);
                        tv3.setSingleLine(true);
                        tv3.setEllipsize(TextUtils.TruncateAt.END);
                        linearLayout2Child.addView(tv3);

                        TextView tv4 = new TextView(mContext);
                        LinearLayout.LayoutParams tv4Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        tv4.setLayoutParams(tv4Params);
                        tv4.setTextColor(mContext.getResources().getColor(R.color.character));
                        tv4.setTextSize(16);
                        tv4.setText(":"+count1[1]);
                        linearLayout2Child.addView(tv4);

                        if(splits[i*2+1].contains(":red") && splits[i*2+1].endsWith(":red")){
                            // 变颜色
                            tv4.setTextColor(mContext.getResources().getColor(R.color.red));
                        }
                    }
                    llItem.addView(linearLayout);
                }
            }
            TextView time = holder.getView(R.id.tv_time);
            time.setText(TimeUtils.getStrTime(patientUserSick.time));
            TextView tvTitle = holder.getView(R.id.tv_title);
            tvTitle.setText(patientUserSick.sick_title);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
