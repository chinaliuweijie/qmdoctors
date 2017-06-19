package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.github.jdsjlzx.ItemDecoration.DividerDecoration;
import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.github.jdsjlzx.recyclerview.ProgressStyle;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.adapter.PatientCaseDetailedAdapter;
import com.qingmiao.qmdoctor.bean.PatientAllInfoBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.TextUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/4/27.
 */

public class PatientCaseTestActivity extends BaseActivity {

    String  uid;
    @BindView(R.id.lRectvyeView)
    LRecyclerView lRecyclerView;
    PatientCaseDetailedAdapter patientCaseTestDetailedAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_case_test);
        getParameter();
        getInitData();
        initView();
    }

    private void initView() {
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        lRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        DividerDecoration divider = new DividerDecoration.Builder(this)
//                .setColorResource(R.color.text)
//                .build();
//        lRecyclerView.addItemDecoration(divider);
        patientCaseTestDetailedAdapter = new PatientCaseDetailedAdapter(this);
        LRecyclerViewAdapter mLRecyclerViewAdapter = new LRecyclerViewAdapter(patientCaseTestDetailedAdapter);
        lRecyclerView.setAdapter(mLRecyclerViewAdapter);
        lRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        lRecyclerView.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        lRecyclerView.setPullRefreshEnabled(false);
        mLRecyclerViewAdapter.removeFooterView();
        tvCenter.setText("病例检查");
        ivRightBig.setText(R.string.icons_data);
        ivRightBig.setVisibility(View.VISIBLE);
        ivRightBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(patientCaseTestDetailedAdapter.getDataList().size()>0){
                    Intent intent = new Intent(PatientCaseTestActivity.this, CaseIconActivity.class);
                    intent.putExtra("uid",uid);
                    startActivity(intent);
                }else{
                    ToastUtils.showLongToast(PatientCaseTestActivity.this,"当前患者没有病例");
                }
            }
        });


        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 跳转到详情界面
                List<PatientAllInfoBean.PatientData.PatientUserSick> dataList = patientCaseTestDetailedAdapter.getDataList();
                Intent intent = new Intent(PatientCaseTestActivity.this,CaseDataInfoActivity.class);
                intent.putExtra("bl_id",dataList.get(position).bl_id);
                intent.putExtra("uid",dataList.get(position).user_id);
                intent.putExtra("startPosition",dataList.get(0).bl_id);
                intent.putExtra("endPosition",dataList.get((dataList.size()-1)).bl_id);
                startActivity(intent);
            }
        });
    }

    private void getInitData() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did",did);
        linkedHashMap.put("token",token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("uid",uid);
        linkedHashMap.put("limit",3+"");
        startLoad(UrlGlobal.GET_PATIENT_INFO,linkedHashMap);
    }

    private void getParameter() {
        uid = getIntent().getStringExtra("uid");
    }

    @Override
    public void initData(String data) {
        super.initData(data);
        data = TextUtils.getEStr2CStr(data);
        PatientAllInfoBean patientInfoBean = GsonUtil.getInstance().fromJson(data, PatientAllInfoBean.class);
        if(patientInfoBean.code == 0){
            patientCaseTestDetailedAdapter.setDataList(patientInfoBean.data.user_sick);
        }else{
            ToastUtils.showLongToast(this,patientInfoBean.msg);
        }
    }
}
