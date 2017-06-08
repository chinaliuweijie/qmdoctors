package com.qingmiao.qmdoctor.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class FeedBackActivity extends BaseActivity {
    @BindView(R.id.et_opinion)
    EditText etOpinion;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.tv_hint)
    TextView tvHint;
    private String opinion;
    private String phone;
    String telRegex = "1[34578]\\d{9}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ivLeft.setVisibility(View.VISIBLE);
        tvCenter.setText("意见反馈");
        SpannableString spannableString = new SpannableString("* 请填写您的手机号码,以便及时与您沟通!");
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvHint.setText(spannableString);

    }

    @OnClick(R.id.btn_submit)
    public void onClick() {
        opinion = etOpinion.getText().toString().trim();
        phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(opinion) || TextUtils.isEmpty(phone)) {

            ToastUtils.showLongToast(this, "手机和反馈不能为空");
            return;
        } else {
            if (!phone.matches(telRegex)) {
                ToastUtils.showLongToast(this, "请输入正确的手机号");
                return;
            } else {
                OkHttpUtils.post()
                        .url(UrlGlobal.SUGGESTION_ADD)
                        .addParams("did", did)
                        .addParams("token", token)
                        .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                        .addParams("content", opinion)
                        .addParams("mobile", phone)
                        .addParams("type", "2")
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    int code = jsonObject.optInt("code");
                                    if (code == 0) {
                                        ToastUtils.showLongToast(FeedBackActivity.this, "反馈成功");
                                        finish();
                                    }else{
                                        String msg = jsonObject.optString("msg");
                                        ToastUtils.showLongToast(FeedBackActivity.this, msg);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        }
    }
}
