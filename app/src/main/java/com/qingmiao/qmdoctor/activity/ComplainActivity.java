package com.qingmiao.qmdoctor.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import butterknife.BindView;
import okhttp3.Call;
/*投诉页面*/
public class ComplainActivity extends BaseActivity {

    @BindView(R.id.complain_title_et)
    EditText complainTitleEt;
    @BindView(R.id.complain_content_et)
    EditText complainContentEt;
    @BindView(R.id.complain_number_tv)
    TextView complainNumberTv;
    private StringBuffer stringBuffer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);
        tvCenter.setText("投诉");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("发送");
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = getIntent().getStringExtra("uid");
            //    String title = complainTitleEt.getText().toString();
                String content = complainContentEt.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showLongToast(ComplainActivity.this, "标题或者内容不能为空!");
                    return;
                }

                OkHttpUtils.post()
                        .url(UrlGlobal.PATIENT_COMPLAIN)
                        .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                        .addParams("did", did)
                        .addParams("token", token)
                        .addParams("uid", uid)
                        .addParams("content", content)
                     //   .addParams("title", title)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ToastUtils.showLongToast(ComplainActivity.this, "网络异常");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Result result = GsonUtil.getInstance().fromJson(response,Result.class);
                                if(result.code == 0){
                                    ToastUtils.showLongToast(ComplainActivity.this, "投诉成功");
                                    finish();
                                }else{
                                    ToastUtils.showLongToast(ComplainActivity.this, result.msg);
                                }
                            }
                        });
            }
        });

        stringBuffer = new StringBuffer();
        complainContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                stringBuffer.setLength(0);
                String text = editable.toString();
                int length = text.length();
                if (length > 200) {
                    ToastUtils.showLongToast(ComplainActivity.this, "超出字数限制");
                    return;
                }
                stringBuffer.append("" + length);
                stringBuffer.append("/200");
                complainNumberTv.setText(stringBuffer);
            }
        });
    }
}
