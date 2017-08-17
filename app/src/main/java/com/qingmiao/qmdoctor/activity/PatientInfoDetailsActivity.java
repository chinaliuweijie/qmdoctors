package com.qingmiao.qmdoctor.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.PatientAllInfoBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashMap;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/4/26.
 */

public class PatientInfoDetailsActivity extends BaseActivity implements ILibelInfoView {
    @BindView(R.id.et_tags)
    EditText etTags;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_address)
    TextView tv_address;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_relation)
    TextView tvRelation;
    @BindView(R.id.tv_jiuzhen)
    TextView tvJiuzhen;
    private LibelInfoPresenter infoPresenter;
    String uid ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info_details);
        initView();
        getInitData();
    }


    private void getInitData() {
        uid = getIntent().getStringExtra("uid");
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("did",did);
        linkedHashMap.put("token",token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        linkedHashMap.put("uid",uid);
        linkedHashMap.put("limit",3+"");
        startLoad(UrlGlobal.GET_PATIENT_INFO,linkedHashMap);

    }

    private void initView() {
        tvCenter.setText("资料详情");
        infoPresenter = new LibelInfoPresenter(this);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("保存");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remark_name = etTags.getText().toString();
                if(!TextUtils.isEmpty(remark_name)){
                    LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                    linkedHashMap.put("did",did);
                    linkedHashMap.put("token",token);
                    linkedHashMap.put("sign",MD5Util.MD5(GetTime.getTimestamp()));
                    linkedHashMap.put("uid",uid);
                    linkedHashMap.put("remark_name",remark_name);
                    infoPresenter.startLoad(UrlGlobal.REMARK_PATIENT,linkedHashMap);
                }else{
                    ToastUtils.showLongToast(PatientInfoDetailsActivity.this,"请填写备注信息");
                }
            }
        });
    }

    @Override
    public void initData(String data) {
        super.initData(data);
        PatientAllInfoBean patientInfoBean = GsonUtil.getInstance().fromJson(data, PatientAllInfoBean.class);
        if(patientInfoBean.code == 0){
            etTags.setText(patientInfoBean.data.u_info.remark_names);
            tvSex.setText(patientInfoBean.data.u_info.sex.equals("0") ?"男":"女");
            tv_address.setText((TextUtils.isEmpty(patientInfoBean.data.u_info.province) ? "" : patientInfoBean.data.u_info.province) + " "+ (TextUtils.isEmpty(patientInfoBean.data.u_info.city)?"":patientInfoBean.data.u_info.city));
            tvBirthday.setText(TimeUtils.getStrTime(patientInfoBean.data.u_info.birth_date,TimeUtils.BIRTHDAY_FORMAT));
            tvRelation.setText(patientInfoBean.data.u_info.relation.equals("0")?"本人":"非本人");
            tvJiuzhen.setText(patientInfoBean.data.u_info.if_vis.equals("0")?"未就诊":"就诊");
        }
    }

    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"添加备注");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        Result result = GsonUtil.getInstance().fromJson(data,Result.class);
        if(result.code == 0){
            // 通知患者界面刷新
            EventBus.getDefault().post("updata_patient");
            finish();
        }else{
            ToastUtils.showLongToast(this,result.msg);
        }
    }
}
