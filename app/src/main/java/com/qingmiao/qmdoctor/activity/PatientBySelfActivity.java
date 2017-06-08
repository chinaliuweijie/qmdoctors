package com.qingmiao.qmdoctor.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedHashMap;

import butterknife.BindView;


public class PatientBySelfActivity extends BaseActivity implements ILibelInfoView {

    @BindView(R.id.tv_time)
    TextView tvTime ;
    @BindView(R.id.et_detail)
    EditText etDetail;
    @BindView(R.id.tv_length)
    TextView tvLength;
    private int maxlength = 200;
    private LibelInfoPresenter libelInfoPresenter;
    private String uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_describe);
        initView();

    }

    private void initView() {
        tvTime.setText(GetTime.getCurrentTime());
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发布");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etDetail.getText().toString();
                if(!TextUtils.isEmpty(content)){
                    LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
                    linkedHashMap.put("uid",uid);
                    linkedHashMap.put("did", did);
                    linkedHashMap.put("token", token);
                    linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                    linkedHashMap.put("sick_desc",content);
                    libelInfoPresenter.startLoad(UrlGlobal.SET_PATIENT_DESC,linkedHashMap);
                }else{
                    ToastUtils.showLongToast(PatientBySelfActivity.this,"请输入描述信息");
                }
            }
        });
        etDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = etDetail.getText().toString();
                tvLength.setText(content.length()+"/" + maxlength);
                if(content.length() >= maxlength){
                    ToastUtils.showLongToast(PatientBySelfActivity.this,"最多可编辑200个文字");
                }
            }
        });
        libelInfoPresenter = new LibelInfoPresenter(this);
        uid = getIntent().getStringExtra("uid");
    }


    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"提交中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        Result result = GsonUtil.getInstance().fromJson(data,Result.class);
        if(result.code == 0){
            EventBus.getDefault().post("updataPatient");
            finish();
        }else{
            ToastUtils.showLongToast(this,result.msg);
        }
    }
}
