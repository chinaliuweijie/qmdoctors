package com.qingmiao.qmdoctor.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.mylhyl.superdialog.SuperDialog;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.IfCollectionBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

public class PDFWebViewActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener {


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
    private PDFView pdfView;
    public static final String FILE_PATENT_NAME = "qm_document_files";
    private String filaName;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfview);
        pdfView = (PDFView) findViewById(R.id.pdfView);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        id = intent.getStringExtra("id");
        filaName = url.replace("/", "");
        String title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvCenter.setText(title);
        }
        getIsBXForService();
        if (!fileIsExists(filaName)) {
            downloadFile();
        } else {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FILE_PATENT_NAME, filaName);
            pdfView.fromFile(f)
                    .defaultPage(0)
                    .onPageChange(PDFWebViewActivity.this)
                    .enableAnnotationRendering(true)
                    .onLoad(PDFWebViewActivity.this)
                    .onError(new OnErrorListener() {
                        @Override
                        public void onError(Throwable t) {
                            LogUtil.LogShitou(t.toString());
                            if(deleteFile(filaName)){
                                downloadFile();
                            }
                        }
                    })
                    .onLoad(new OnLoadCompleteListener() {
                        @Override
                        public void loadComplete(int nbPages) {
                            LogUtil.LogShitou(nbPages+"----------------------");
                        }
                    })
                    .scrollHandle(new DefaultScrollHandle(PDFWebViewActivity.this))
                    .load();
        }

    }

    public boolean fileIsExists(String fileName) {
        try {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FILE_PATENT_NAME, fileName);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteFile(String fileName){
        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FILE_PATENT_NAME, fileName);
            if (file.isFile() && file.exists()) {
               return file.delete();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }




    public void downloadFile() {
        final SuperDialog.Builder progressDialog = new SuperDialog.Builder(this);
        progressDialog.setCanceledOnTouchOutside(false).setCancelable(false).setWidth(0.7f).setBackgroundColor(getResources().getColor(R.color.white))
                .setProgress(0).setTitle("正在下载", getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title))
                .setNegativeButton("取消", getResources().getColor(R.color.black_1), (int) getResources().getDimension(R.dimen.tv_sitem_title), -1, new SuperDialog.OnClickNegativeListener() {
                    @Override
                    public void onClick(View v) {
                          //   OkHttpUtils.getInstance().cancelTag(this);
                    }
                });
        final DialogFragment dialogFragment = progressDialog.build();

        OkHttpUtils//
                .get()//
                .url(url).tag(this)//
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FILE_PATENT_NAME, filaName)//
                {

                    @Override
                    public void onBefore(Request request, int id) {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        progressDialog.refreshProgress(100, (int) (progress * 100));
                        LogUtil.LogShitou(progress + " &&&&&&&&&&            " + total + "  " + progressDialog.toString());
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.LogShitou("onError :" + e.getMessage());
                        ToastUtils.showLongToast(PDFWebViewActivity.this, "下载失败");
                    }

                    @Override
                    public void onResponse(File file, int id) {
                        LogUtil.LogShitou("onResponse :" + file.getAbsolutePath());
                        if (dialogFragment != null) {
                            dialogFragment.dismiss();
                        }
                        pdfView.fromFile(file)
                                .defaultPage(0)
                                .onPageChange(PDFWebViewActivity.this)
                                .enableAnnotationRendering(true)
                                .onLoad(PDFWebViewActivity.this)
                                .scrollHandle(new DefaultScrollHandle(PDFWebViewActivity.this))
                                .load();
                    }
                });
    }


    private void getIsBXForService() {
        OkHttpUtils.post()
                .url(UrlGlobal.IF_COLLECTION).tag(this)
                .addParams("token", token)
                .addParams("did", did)
                .addParams("type", "2")
                .addParams("source_type", "1")
                .addParams("id", id)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(PDFWebViewActivity.this, "请求失败");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        IfCollectionBean ifCollectionBean = GsonUtil.getInstance().fromJson(response, IfCollectionBean.class);
                        PDFWebViewActivity.this.ifCollectionBean = ifCollectionBean;
                        if (ifCollectionBean.code == 0) {
                            if ("2".equals(ifCollectionBean.if_collect)) {
                                star.setTextColor(getResources().getColor(R.color.light_yellow));
                            } else if ("1".equals(ifCollectionBean.if_collect)) {
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
            ToastUtils.showLongToast(this, "未登录");
            return;
        }
        switch (view.getId()) {
            //确认
            case R.id.bottom_bnt:
                if (!flag) {
                    flag = true;
                    String sendText = webCommentEt.getText().toString();
                    if (TextUtils.isEmpty(sendText)) {
                        ToastUtils.showLongToast(this, "内容不能为空");
                        flag = false;
                        return;
                    }
                    OkHttpUtils.post()
                            .url(UrlGlobal.COMMENT_URL).tag(this)
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
                                    ToastUtils.showLongToast(PDFWebViewActivity.this, "网络异常");
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        int code = jsonObject.optInt("code");
                                        if (code == 0) {
                                            ToastUtils.showLongToast(PDFWebViewActivity.this, "评论成功");

                                            webCommentEt.setText("");
                                            flag = false;
                                        } else {
                                            ToastUtils.showLongToast(PDFWebViewActivity.this, "评论失败");
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
                if (!TextUtils.isEmpty(did) && !TextUtils.isEmpty(token)) {
                    OkHttpUtils.post()
                            .url(UrlGlobal.ADD_COLLECTION).tag(this)
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
                                    ToastUtils.showLongToast(PDFWebViewActivity.this, "网络异常");
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
                                        ToastUtils.showLongToast(PDFWebViewActivity.this, result.msg);
                                    } else {
                                        ToastUtils.showLongToast(PDFWebViewActivity.this, result.msg);
                                    }
                                }
                            });
                }
                break;
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

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
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
