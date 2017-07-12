package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.LoginBean;
import com.qingmiao.qmdoctor.bean.MessageUserBean;
import com.qingmiao.qmdoctor.bean.RegisterUserBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.net.BeanCallBack;
import com.qingmiao.qmdoctor.net.Code;
import com.qingmiao.qmdoctor.utils.CountDownTimerUtils;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.qingmiao.qmdoctor.utils.TimeUtils;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.widget.IconFontTextview;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.register_phone_et)
    EditText etNumber;
    @BindView(R.id.register_verify_et)
    EditText etverify;
    @BindView(R.id.register_pwd_et)
    EditText etpassword;
    @BindView(R.id.iv_verify)
    IconFontTextview ivVerify;
    @BindView(R.id.register_bnt_getVerify)
    TextView registerBntGetVerify;
    @BindView(R.id.register_bnt)
    Button findBnt;
    @BindView(R.id.iv_cancel)
    IconFontTextview cancel;
    @BindView(R.id.iv_eye)
    IconFontTextview ivEye;
    private boolean isChecked = false;
    private String password;
    private String usernames;
    private String data;
    private CountDownTimerUtils mCountDownTimerUtils;
    private String type;
    private String sex,avatar,nickname,openid;
    private Map<String,String> mapData;
    private LoginBean loginBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }
    private void initView() {
        mapData = (Map<String, String>) getIntent().getSerializableExtra("data");
        if(mapData!=null) {
            type = mapData.get("qmtype");
        }
        if(!TextUtils.isEmpty(type)){
            tvCenter.setText("绑定账号");
            findBnt.setText("绑定");
        }else{
            tvCenter.setText("注册账号");
            findBnt.setText("注册");
        }
        ivLeft.setVisibility(View.VISIBLE);
    }
   @OnClick({R.id.register_bnt_getVerify, R.id.register_bnt, R.id.iv_eye,R.id.iv_cancel,R.id.register_tvBnt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_bnt_getVerify:
                usernames = etNumber.getText().toString().trim();
                if((usernames.length() >11 || usernames.length() <11) || !TimeUtils.isMobile(usernames)) {
                    ToastUtils.showLongToast(RegisterActivity.this,"手机格式不正确");
                    return;
                }
                sendMessage();
                mCountDownTimerUtils = new CountDownTimerUtils(registerBntGetVerify, 60000, 1000); //倒计时1分钟
                mCountDownTimerUtils.start();
                break;
            case R.id.register_bnt:
                password = etpassword.getText().toString().trim();
                if(!TextUtils.isEmpty(usernames) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(data)) {
                    if(!TimeUtils.isMobile(usernames)){
                        ToastUtils.showLongToast(RegisterActivity.this,"手机格式不正确");
                        return ;
                    }
                    if(password.length()<6){
                        ToastUtils.showLongToast(RegisterActivity.this,"密码必须大于等于6位");
                        return ;
                    }
                    initHttp();
                }else{
                    ToastUtils.showLongToast(RegisterActivity.this,"请输入内容");
                }
                break;
            case R.id.iv_eye:
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
            case R.id.iv_cancel:
                etverify.setText("");
                break;
            case R.id.register_tvBnt:
                Intent intent = new Intent(RegisterActivity.this,UserRuleWebActivity.class);
                intent.putExtra("title","用户协议");
                intent.putExtra("url",UrlGlobal.USER_RULE);
                startActivity(intent);
                break;
        }
    }

    public void sendMessage(){
        OkHttpUtils.post()
                .url(UrlGlobal.SENDMESSAGE)
                .addParams("mobile", usernames)
                .addParams("type", ""+2)
                .addParams("source",""+1)
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new BeanCallBack<MessageUserBean>(){
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        LogUtil.LogShitou(e.toString());
                        if(mCountDownTimerUtils!=null){
                           mCountDownTimerUtils.cancel();
                           mCountDownTimerUtils.onFinish();
                       }
                    }

                    @Override
                    public void onResponse(MessageUserBean response, int id) {
                        if(response.code == Code.SUCCEED){
                            data = response.data;
                   //         ToastUtils.showLongToast(RegisterActivity.this,data);
                            LogUtil.LogShitou("---------------------------------"+data);
                        }else if(response.code == Code.FAIL){
                            ToastUtils.showLongToast(RegisterActivity.this,response.msg);
                            if(mCountDownTimerUtils!=null){
                               mCountDownTimerUtils.cancel();
                               mCountDownTimerUtils.onFinish();
                            }
                        }
                    }
                });
    }

    public void initHttp() {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
        if("weixin".equals(type)){
            String ssex = mapData.get("gender");
            if("男".equals(ssex)){
                sex = "0" ;
            }else if("女".equals(ssex)){
                sex = "1" ;
            }
            avatar = mapData.get("iconurl");
            nickname = mapData.get("name");
            openid = mapData.get("uid");
            linkedHashMap.put("sex",sex);
            linkedHashMap.put("avatar",avatar);
            linkedHashMap.put("nickname",nickname);
            linkedHashMap.put("wx_openid",openid);
        }else if("qq".equals(type)){
            String ssex = mapData.get("gender");
            if("男".equals(ssex)){
                sex = "0" ;
            }else if("女".equals(ssex)){
                sex = "1" ;
            }
            avatar = mapData.get("iconurl");
            nickname = mapData.get("name");
            openid = mapData.get("uid");
            linkedHashMap.put("sex",sex);
            linkedHashMap.put("avatar",avatar);
            linkedHashMap.put("nickname",nickname);
            linkedHashMap.put("qq_openid",openid);
        }else if("sina".equals(type)){
            String ssex = mapData.get("gender");
            if("男".equals(ssex)){
                sex = "0" ;
            }else if("女".equals(ssex)){
                sex = "1" ;
            }
            avatar = mapData.get("iconurl");
            nickname = mapData.get("name");
            openid = mapData.get("uid");
            linkedHashMap.put("sex",sex);
            linkedHashMap.put("avatar",avatar);
            linkedHashMap.put("nickname",nickname);
            linkedHashMap.put("sina_openid",openid);
        }
        linkedHashMap.put("mobile", usernames);
        linkedHashMap.put("password", MD5Util.MD5(password));
        linkedHashMap.put("code", data);
        linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
        String url = UrlGlobal.REGISTER_URL;
        if(!TextUtils.isEmpty(type)){
            url = UrlGlobal.AUTH_REGISTER_URL;
            showLoadingDialog(url,"绑定账号中");
        }else{
            showLoadingDialog(url,"注册中");
        }
        OkHttpUtils.post()
                .url(url)
                .params(linkedHashMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(RegisterActivity.this, e.getMessage());
                    }

                    @Override
                    public void onResponse(String code, int id) {
                        RegisterUserBean response = GsonUtil.getInstance().fromJson(code,RegisterUserBean.class);
                        if(response.code == Code.SUCCEED){
                          //  Toast.makeText(RegisterActivity.this, "注册成功,请登录", Toast.LENGTH_SHORT).show();
                            if(TextUtils.isEmpty(type)){
                                setLoadingText("登录中");
                                login();
                            }else{
                                setLoadingText("登录中");
                                login();
                            }
                            //  发广播刷新界面
                          //  EventBus.getDefault().post(response);
                        }else if(response.code == Code.FAIL){
                            ToastUtils.showLongToast(RegisterActivity.this, response.msg);
                            dismissLoadDialog();
                        }
                    }
                });
    }



    public void login() {
        OkHttpUtils.post()
                .url(UrlGlobal.LOGIN_URL)
                .addParams("mobile",usernames)
                .addParams("password", MD5Util.MD5(password))
                .addParams("sign", MD5Util.MD5(GetTime.getTimestamp()))
                .build()
                .execute(new BeanCallBack<LoginBean>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showLongToast(RegisterActivity.this, e.getMessage());
                        dismissLoadDialog();
                    }

                    @Override
                    public void onResponse(LoginBean response, int id) {
                        if(response.code == Code.SUCCEED){
                            loginBean = response;
                            loginHX(response.hx_uname,MD5Util.MD5(password));
                        }else if(response.code == Code.EXCEED){
                            ToastUtils.showLongToast(RegisterActivity.this,"登录信息过时，请重新登陆");
                            dismissLoadDialog();
                        }else if(response.code == Code.FAIL){
                            ToastUtils.showLongToast(RegisterActivity.this,response.msg);
                            dismissLoadDialog();
                        }else{
                            ToastUtils.showLongToast(RegisterActivity.this,response.msg);
                            dismissLoadDialog();
                        }
                    }
                });
    }


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
                // 登录成功  销毁登录activity
                EventBus.getDefault().post("loginSuccess");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoadDialog();
                        if(loginBean!=null){
                            app.saveLogin(loginBean);
                            PrefUtils.putString(RegisterActivity.this, "did",loginBean.did);
                            PrefUtils.putString(RegisterActivity.this, "token",loginBean.token);
                            PrefUtils.putString(RegisterActivity.this, "username",usernames);
                            // 设置极光的别名和标签
                            Set<String> strings = new HashSet<String>();
                            strings.add("did");
                            JPushInterface.setAliasAndTags(RegisterActivity.this, loginBean.did, strings, new TagAliasCallback() {
                                @Override
                                public void gotResult(int i, String s, Set<String> set) {
                                    LogUtil.LogShitou(i + s + set);
                                }
                            });
                        }
                    }
                });
                EventBus.getDefault().post("changeData");
                // 通知患者界面刷新
                EventBus.getDefault().post("updata_patient");
                finish();
                // 跳转到医生信息界面
                startActivity(new Intent(RegisterActivity.this,DoctorDataActivity.class));
            }
            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, final String message) {
                Log.e("main", "登录聊天服务器失败！" + message + " code = " + code);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showLongToast(RegisterActivity.this,message);
                        dismissLoadDialog();
                    }
                });
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCountDownTimerUtils!=null){
            mCountDownTimerUtils.cancel();
        }
    }
}
