package com.qingmiao.qmdoctor.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class FindPwdActivity extends BaseActivity {
    @BindView(R.id.register_phone_et)
    EditText etNumber;
    @BindView(R.id.register_verify_et)
    EditText etverify;
    @BindView(R.id.register_pwd_et)
    EditText etpassword;
    @BindView(R.id.register_bnt_getVerify)
    TextView registerBntGetVerify;
    @BindView(R.id.find_bnt)
    Button findBnt;
    @BindView(R.id.iv_cancel)
    IconFontTextview cancel;
    @BindView(R.id.iv_eye)
    IconFontTextview ivEye;
    private boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);
        initView();
    }

    private void initView() {
        ivLeft.setVisibility(View.VISIBLE);
        tvCenter.setText("找回密码");

        etverify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    cancel.setVisibility(View.VISIBLE);
                } else {
                    cancel.setVisibility(View.GONE);
                }
            }
        });

    }

    @OnClick({R.id.iv_eye, R.id.iv_cancel, R.id.register_bnt_getVerify, R.id.find_bnt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_eye:
                if (isChecked) {
                    etpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    isChecked = false;
                } else {
                    //明文
                    etpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    isChecked = true;
                }
                etpassword.setSelection(etpassword.getText().length());
                break;
            case R.id.iv_cancel:
                etverify.setText("");
                break;
            //获取验证码
            case R.id.register_bnt_getVerify:
                String phoneNum = etNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(phoneNum)) {
                    if (TimeUtils.isMobile(phoneNum)) {
                        sendMessage(phoneNum);
                    } else {
                        ToastUtils.showLongToast(FindPwdActivity.this, "手机格式不正确");
                    }
                } else {
                    ToastUtils.showLongToast(FindPwdActivity.this, "手机号码为空");
                }

                break;
            case R.id.find_bnt:

                String password = etpassword.getText().toString().trim();
                String number = etNumber.getText().toString().trim();
                String etverifyCode = etverify.getText().toString().trim();

                if (TextUtils.isEmpty(number)) {
                    ToastUtils.showLongToast(FindPwdActivity.this, "手机号码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(etverifyCode)) {
                    ToastUtils.showLongToast(FindPwdActivity.this, "验证码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.showLongToast(FindPwdActivity.this, "密码不能为空");
                    return;
                }
                initHttp(number, password, etverifyCode);
                break;
        }
    }

    //发送验证码
    public void sendMessage(String phone) {

        registerBntGetVerify.setEnabled(false);
        handler.postDelayed(runnable, 0);
        OkHttpUtils.post()
                .url(UrlGlobal.SENDMESSAGE)
                .addParams("mobile", phone)
                .addParams("type", "2")
                .addParams("source", "2")
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(FindPwdActivity.this, "获取验证码失败!");
                        handler.removeCallbacksAndMessages(null);
                        registerBntGetVerify.setText("获取验证码");
                        registerBntGetVerify.setEnabled(true);
                        time = 60;
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject json = new JSONObject(response);
                            int code = json.optInt("code");
                            if (code != 0) {
                                ToastUtils.showLongToast(FindPwdActivity.this, "获取验证码失败!");
                                handler.removeCallbacksAndMessages(null);
                                registerBntGetVerify.setText("获取验证码");
                                registerBntGetVerify.setEnabled(true);
                                time = 60;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private boolean flag;

    //更改密码
    public void initHttp(String phone, String pwd, String code) {
        if (!flag) {
            flag = true;
            OkHttpUtils.post()
                    .url(UrlGlobal.RENEW_PASSWORD)
                    .addParams("mobile", phone)
                    .addParams("password", MD5Util.MD5(pwd))
                    .addParams("code", code)
                    .addParams("type", "2")
                    .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            flag = false;
                            ToastUtils.showLongToast(FindPwdActivity.this, "网络异常!");
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            flag = false;
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                int code_ = jsonObject.optInt("code");
                                String msg = jsonObject.optString("msg");
                                if (code_ == 0) {
                                    ToastUtils.showLongToast(FindPwdActivity.this, "密码修改成功!");
                                    finish();
                                } else {
                                    ToastUtils.showLongToast(FindPwdActivity.this, msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        }
    }

    private int time = 60;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            if (time <= 0) {
                handler.removeCallbacksAndMessages(null);
                registerBntGetVerify.setText("获取验证码");
                registerBntGetVerify.setEnabled(true);
                time = 60;
                return;
            }
            registerBntGetVerify.setText(time + "秒");
            handler.postDelayed(this, 1000);
        }
    };

}
