package com.qingmiao.qmdoctor.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 用户协议界面
 */
public class UserRuleWebActivity extends BaseActivity {

    @BindView(R.id.test_webView)
    WebView testWebView;
    @BindView(R.id.progressBar)
    ProgressBar progressbar;
    private int progress = 0;
    private MyTimer mTimer;
    private String title,url;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_simple);
        initData();
        initView();
        // 设置字体
        testWebView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        // 启用javascript
        testWebView.getSettings().setJavaScriptEnabled(true);
        testWebView.setWebViewClient(new WeiboWebViewClient());
        testWebView.setWebChromeClient(new WebChromeClient());
        // 从assets目录下面的加载html
        testWebView.loadUrl(url);
        JSObject jsObject = new JSObject(this);
        testWebView.addJavascriptInterface(jsObject, "wst");
    }

    private void initData() {
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
    }

    private void initView() {
        ivLeft.setVisibility(View.VISIBLE);
        tvCenter.setText(title);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        testWebView.removeAllViews();
        testWebView.destroy();
    }
}
