package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.PatientInfomationBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class AddPatientActivity extends BaseActivity implements ILibelInfoView {
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_default)
    IconFontTextview ivDefault;
    @BindView(R.id.tv_search_result)
    TextView tvSearchResult;
    private String did;
    private String token;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    private String username;
    private LibelInfoPresenter infoPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        did = PrefUtils.getString(AddPatientActivity.this,"did", "");
        token = PrefUtils.getString(AddPatientActivity.this, "token", "");
        username = PrefUtils.getString(this,"username","");
        tvPhone.setText("我的手机号:"+username);
        tvCenter.setText("添加患者");
        infoPresenter = new LibelInfoPresenter(this);
    }

    @OnClick({R.id.btn_search, R.id.tv_search_result, R.id.iv_default})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                if(TimeUtils.isMobile(etSearch.getText().toString().trim())){
                    initHttp();
                }else{
                    ToastUtils.showLongToast(this,"输入手机不合法");
                }
                break;
            case R.id.tv_search_result:
                break;
        }
    }


    public void initHttp() {
        String mobile = etSearch.getText().toString().trim();
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("mobile", mobile);
        linkedHashMap.put("did", did);
        linkedHashMap.put("token", token);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        infoPresenter.startLoad(UrlGlobal.GETUSERMESSAGE,linkedHashMap);
    }




    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"搜索好友中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        PatientInfomationBean patientInfomationBean = GsonUtil.getInstance().fromJson(data,PatientInfomationBean.class);
        if(patientInfomationBean.code == 0){
            Intent intent = new Intent(AddPatientActivity.this,PatientDataActivity.class);
            intent.putExtra("uid",patientInfomationBean.data.uid);
            intent.putExtra("isFriend",patientInfomationBean.data.is_friend);
            intent.putExtra("isMarked",patientInfomationBean.data.is_marked);
            startActivity(intent);
            // finish();
        }else{
            ToastUtils.showLongToast(AddPatientActivity.this,patientInfomationBean.msg);
        }
    }
}