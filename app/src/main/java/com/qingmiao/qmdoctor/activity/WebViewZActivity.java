package com.qingmiao.qmdoctor.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.IfCollectionBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.JSObject;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONException;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

public class WebViewZActivity extends BaseActivity {

    @BindView(R.id.test_webView)
    WebView testWebView;
    @BindView(R.id.progressBar)
    ProgressBar progressbar;
    @BindView(R.id.bottom_ed)
    EditText webCommentEt;
    @BindView(R.id.bottom_bnt)
    Button webCommentBnt;
    @BindView(R.id.comment_bottom_ll)
    LinearLayout commentBottomLl;
    @BindView(R.id.star)
    IconFontTextview star;
    private String id;
    private String url;
    private int progress = 0;
    private MyTimer mTimer;
    private boolean flag;
    private IfCollectionBean ifCollectionBean;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        id = intent.getStringExtra("id");
        testWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        // 启用javascript
        testWebView.getSettings().setJavaScriptEnabled(true);
        testWebView.setWebViewClient(new WeiboWebViewClient());
        testWebView.setWebChromeClient(new WebChromeClient());
        // 从assets目录下面的加载html
        testWebView.loadUrl(url);
        JSObject js = new JSObject(this);
        js.setId(id);
        testWebView.addJavascriptInterface(js, "wst");
        getIsBXForService();
        boolean isShowBottom = getIntent().getBooleanExtra("isShowBottom",true);
        if(!isShowBottom){
            View bottom = findViewById(R.id.comment_bottom_ll);
            bottom.setVisibility(View.GONE);
        }
        String title = getIntent().getStringExtra("title");
        if(!TextUtils.isEmpty(title)){
            tvCenter.setText(title);
        }
    }

    private void getIsBXForService() {
        OkHttpUtils.post()
                .url(UrlGlobal.IF_COLLECTION)
                .addParams("token", token)
                .addParams("did", did)
                .addParams("type","2")
                .addParams("source_type","1")
                 .addParams("id",id)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(WebViewZActivity.this,"请求失败");
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        IfCollectionBean ifCollectionBean = GsonUtil.getInstance().fromJson(response,IfCollectionBean.class);
                        WebViewZActivity.this.ifCollectionBean = ifCollectionBean;
                        if(ifCollectionBean.code == 0){
                            if("2".equals(ifCollectionBean.if_collect)){
                                star.setTextColor(getResources().getColor(R.color.light_yellow));
                            }else if("1".equals(ifCollectionBean.if_collect)){
                                star.setTextColor(getResources().getColor(R.color.text));
                            }
                        }
                    }
                });
    }






    @OnClick({R.id.bottom_bnt, R.id.star_rl})
    public void onClick(View view) {
        if (TextUtils.isEmpty(did) || TextUtils.isEmpty(token)) {
            //UIutil.showDialog(this);
            ToastUtils.showLongToast(this,"未登录");
            return;
        }
        switch (view.getId()) {
            //确认
            case R.id.bottom_bnt:
                if (!flag) {
                    flag = true;
                    String sendText = webCommentEt.getText().toString();
                    if (TextUtils.isEmpty(sendText)) {
                        ToastUtils.showLongToast(this,"内容不能为空");
                        flag = false;
                        return;
                    }
                    OkHttpUtils.post()
                            .url(UrlGlobal.COMMENT_URL)
                            .addParams("did", did)
                            .addParams("token", token)
                            .addParams("id", id)
                            .addParams("content", sendText)
                            .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                            .addParams("type", "2")
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    flag = false;
                                    ToastUtils.showLongToast(WebViewZActivity.this,"网络异常");
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        int code = jsonObject.optInt("code");
                                        if (code == 0) {
                                            ToastUtils.showLongToast(WebViewZActivity.this,"评论成功");
                                            testWebView.reload();
                                            webCommentEt.setText("");
                                            flag = false;
                                        } else {
                                            ToastUtils.showLongToast(WebViewZActivity.this,"评论失败");
                                            webCommentEt.setText("");
                                            flag = false;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        flag = false;
                                    }
                                }
                            });
                }

                break;
            //标星
            case R.id.star_rl:

                if(!TextUtils.isEmpty(did) && !TextUtils.isEmpty(token)) {
                    OkHttpUtils.post()
                            .url(UrlGlobal.ADD_COLLECTION)
                            .addParams("did", did)
                            .addParams("token", token)
                            .addParams("id", id)
                            .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                            .addParams("type", "2")
                            .addParams("source_type", "1")
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    ToastUtils.showLongToast(WebViewZActivity.this, "网络异常");
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    Result result = GsonUtil.getInstance().fromJson(response, Result.class);
                                    if (result.code == 0) {
                                        if (ifCollectionBean != null) {
                                            if ("2".equals(ifCollectionBean.if_collect)) {
                                                star.setTextColor(getResources().getColor(R.color.text));
                                                ifCollectionBean.if_collect = "1";
                                            } else if ("1".equals(ifCollectionBean.if_collect)) {
                                                star.setTextColor(getResources().getColor(R.color.light_yellow));
                                                ifCollectionBean.if_collect = "2";
                                            }
                                        }
                                        ToastUtils.showLongToast(WebViewZActivity.this, result.msg);
                                    } else {
                                        ToastUtils.showLongToast(WebViewZActivity.this, result.msg);
                                    }
                                }
                            });
                }
                break;
        }
    }




    /* 定义一个倒计时的内部类 */
    private class MyTimer extends CountDownTimer {
        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            progress = 100;
            progressbar.setVisibility(View.GONE);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (progress == 100) {
                progressbar.setVisibility(View.GONE);
            } else {
                progressbar.setProgress(progress++);
            }
        }
    }


    private class WeiboWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            testWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (mTimer == null) {
                mTimer = new MyTimer(15000, 50);
            }
            mTimer.start();
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!testWebView.getSettings().getLoadsImagesAutomatically()) {
                testWebView.getSettings().setLoadsImagesAutomatically(true);
            }
            mTimer.cancel();
            progress = 0;
            progressbar.setProgress(100);
            progressbar.setVisibility(View.GONE);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        testWebView.removeAllViews();
        testWebView.destroy();
    }
}
