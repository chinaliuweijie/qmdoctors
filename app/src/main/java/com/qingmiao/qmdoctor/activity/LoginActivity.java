package com.qingmiao.qmdoctor.activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.bean.LoginBean;
import com.qingmiao.qmdoctor.bean.RegisterUserBean;
import com.qingmiao.qmdoctor.bean.Result;
import com.qingmiao.qmdoctor.global.KeyOrValueGlobal;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.net.BeanCallBack;
import com.qingmiao.qmdoctor.net.Code;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

public class LoginActivity extends BaseActivity implements ILibelInfoView {
    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.etpassword)
    EditText etpassword;
    private String phone;
    private String pwd;
    private String telRegex = "^1[34578]\\d{9}$";
    private boolean isChecked;
    boolean isLoginLocal  = false;
    boolean isLoginHX = false;
    private static final int MSG_LOGIN = 0x00;
    private LoginBean response;
    @BindView(R.id.login_iv_weixin)
    IconFontTextview ivWeixin;
    @BindView(R.id.login_iv_sina)
    IconFontTextview ivSina;
    @BindView(R.id.login_iv_qq)
    IconFontTextview ivQQ;
    private LibelInfoPresenter libelInfoPresenter;
    private boolean isGetOpenID = true;
    private Map<String,String> OpenIdMap;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_LOGIN:
                    if(isLoginHX){
                        // 登录成功
                        isLoginLocal = false;
                        isLoginHX = false;
                        app.saveLogin(response);
                        PrefUtils.putString(LoginActivity.this, "did",response.did);
                        PrefUtils.putString(LoginActivity.this, "token",response.token);
                        if(TextUtils.isEmpty(phone)){
                            try{
                                phone = response.hx_uname.split("_")[1] ;
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        PrefUtils.putString(LoginActivity.this, "username",phone);
                        ToastUtils.showLongToast(LoginActivity.this,"登录成功");
                        dismissLoadDialog();
                        EventBus.getDefault().post("changeData");
                        // 通知患者界面刷新
                        EventBus.getDefault().post("updata_patient");
                        finish();
                        // 设置极光的别名和标签
                        Set<String> strings = new HashSet<String>();
                        strings.add("did");
                        JPushInterface.setAliasAndTags(LoginActivity.this, response.did, strings, new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                LogUtil.LogShitou(i + s + set);
                            }
                        });
                    }
                  break;
            }
        }
    };


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        libelInfoPresenter = new LibelInfoPresenter(this);
        EventBus.getDefault().register(this);
        String tellphone = PrefUtils.getString(this,"username","");
        etNumber.setText(tellphone);
    }



    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String response) {
        // 登录服务器，登录环信
        if(response!=null){
            if(response.equals("loginSuccess")){
                finish();
            }
        }
    }

    @OnClick({R.id.btn_login, R.id.login_register, R.id.tv_forget, R.id.iveye,R.id.login_iv_weixin,R.id.login_iv_sina,R.id.login_iv_qq})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                phone = etNumber.getText().toString().trim();
                pwd = etpassword.getText().toString().trim();
                if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd)) {
                    if(phone.matches(telRegex)) {
                        showLoadingDialog(UrlGlobal.LOGIN_URL,"登录中");
                        initHttp();
                    } else {
                        ToastUtils.showLongToast(LoginActivity.this,R.string.text_phone);
                    }
                } else {
                    ToastUtils.showLongToast(LoginActivity.this,R.string.text_numberpassword);
                }
                break;
            case R.id.login_register:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

                break;
            case R.id.tv_forget:
                startActivity(new Intent(LoginActivity.this, FindPwdActivity.class));
                break;
            case R.id.iveye:
                if (isChecked) {
                    etpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etpassword.setSelection(etpassword.getText().length());
                    isChecked = false;
                } else {
                    etpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    etpassword.setSelection(etpassword.getText().length());
                    isChecked = true;
                }
                break;
            case R.id.login_iv_weixin:
                if(!isGetOpenID){
                    return ;
                }
                UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.WEIXIN,null);
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, umAuthListener);
                isGetOpenID = false;
                break;
            case R.id.login_iv_sina:
                if(!isGetOpenID){
                    return ;
                }
                UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.SINA,null);
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.SINA, umAuthListener);
                isGetOpenID = false;
                break;
            case R.id.login_iv_qq:
                if(!isGetOpenID){
                    return ;
                }
                UMShareAPI.get(this).deleteOauth(this, SHARE_MEDIA.QQ,null);
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.QQ, umAuthListener);
                isGetOpenID = false;
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
        }
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            isGetOpenID = true;
            OpenIdMap = data;
         //   ToastUtils.showLongToast(getApplicationContext(), "Authorize succeed");
            if(platform.equals(SHARE_MEDIA.WEIXIN)){
                // 调用登录 接口  判断你是否需要用户注册
                String openid = data.get("openid");
                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                linkedHashMap.put("type","1");
                linkedHashMap.put("openid",openid);
                libelInfoPresenter.startLoad(UrlGlobal.LOGIN_URL,linkedHashMap);
                OpenIdMap.put("qmtype","weixin");
            }else if(platform.equals(SHARE_MEDIA.SINA)){
                String openid = data.get("uid");
                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                linkedHashMap.put("type","3");
                linkedHashMap.put("openid",openid);
                libelInfoPresenter.startLoad(UrlGlobal.LOGIN_URL,linkedHashMap);
                OpenIdMap.put("qmtype","sina");
            }else if(platform.equals(SHARE_MEDIA.QQ)){
                String openid = data.get("openid");
                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                linkedHashMap.put("type","2");
                linkedHashMap.put("openid",openid);
                libelInfoPresenter.startLoad(UrlGlobal.LOGIN_URL,linkedHashMap);
                OpenIdMap.put("qmtype","qq");
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            isGetOpenID = true;
            ToastUtils.showLongToast(getApplicationContext(), "授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            isGetOpenID = true;
            ToastUtils.showLongToast( getApplicationContext(), "取消授权");
        }
    };


    /**
     * 登录环信的服务器
     * @param userName 用户名
     * @param pwd      密码
     */
    private void loginHX(final String userName, String pwd) {
        EMClient.getInstance().getInstance().login(userName, pwd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                /**
                 * 登录成功就调用加载数据
                 */
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.e("main", "登录聊天服务器成功！");
                isLoginHX = true;
                mHandler.sendEmptyMessage(MSG_LOGIN);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                Log.e("main", "登录聊天服务器失败！" + message + " code = " + code);
                isLoginHX =false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLongToast(LoginActivity.this,"登录即时通信失败:"+message+"请重新登录");
                        dismissLoadDialog();
                        onLogout();
                    }
                });

            }
        });
    }


    /**
     * 下线按钮
     *
     * @param
     */
    public void onLogout() {
        // 环信下线
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {
                Log.e("main", "下线失败了！" + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });//下线
    }



    public void initHttp() {
        OkHttpUtils.post()
                .url(UrlGlobal.LOGIN_URL)
                .addParams("mobile",phone)
                .addParams("password", MD5Util.MD5(pwd))
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(LoginActivity.this, e.getMessage());
                        isLoginLocal = false;
                        dismissLoadDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LoginBean loginBean = GsonUtil.getInstance().fromJson(response,LoginBean.class);
                        if(loginBean.code == Code.SUCCEED){
                            LoginActivity.this.response = loginBean;
                            isLoginLocal = true;
                            loginHX(loginBean.hx_uname,MD5Util.MD5(pwd));
                        }else if(loginBean.code == Code.EXCEED){
                            ToastUtils.showLongToast(LoginActivity.this,"登录信息过时，请重新登录");
                            isLoginLocal = false;
                            dismissLoadDialog();
                        }else if(loginBean.code == Code.FAIL){
                            ToastUtils.showLongToast(LoginActivity.this,loginBean.msg);
                            isLoginLocal = false;
                            dismissLoadDialog();
                        }else{
                            ToastUtils.showLongToast(LoginActivity.this,loginBean.msg);
                            dismissLoadDialog();
                        }
                    }
                });
    }


    @Override
    public void showLibelProgress(String uri) {
        if(uri.equals(UrlGlobal.LOGIN_URL)){
            showLoadingDialog(uri,"登录中");
        }
    }

    @Override
    public void hideLibelProgress(String uri) {
        if(uri.equals(UrlGlobal.LOGIN_URL)){
         //   dismissLoadDialog();
        }
    }

    @Override
    public void getLibelData(String uri, String data) {
        LogUtil.LogShitou(data);
        LoginBean result = GsonUtil.getInstance().fromJson(data,LoginBean.class);
        if(result.code == 410){
            // 绑定手机号码
            dismissLoadDialog();
            Intent intent =  new Intent(LoginActivity.this, RegisterActivity.class);
            intent.putExtra("data", (Serializable) OpenIdMap);
            startActivity(intent);
        }else if(result.code == 0){
            //  登录环信的服务器
            LoginActivity.this.response = result;
            loginHX(result.hx_uname,result.pwd);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }
}
