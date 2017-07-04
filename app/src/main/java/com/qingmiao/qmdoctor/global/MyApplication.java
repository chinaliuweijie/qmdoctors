package com.qingmiao.qmdoctor.global;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.GlideUtils;
import com.qingmiao.qmdoctor.activity.ChatActivity;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.bean.LoginBean;
import com.qingmiao.qmdoctor.widget.MyConnectionListener;
import com.qingmiao.qmdoctor.factory.FragmentFactory;
import com.qingmiao.qmdoctor.fragment.ConversationListFragment;
import com.qingmiao.qmdoctor.utils.ACache;
import com.qingmiao.qmdoctor.utils.HXUserCache;
import com.qingmiao.qmdoctor.utils.PrefUtils;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

public class MyApplication extends LitePalApplication {

    private OkHttpClient okHttpClient;
    public static ACache aCache;
   // private RefWatcher refWatcher;
    private LoginBean login = null;
    public static Context context = null;
    private EMMessageListener emMessageListener;
    private String did ;

    public void saveLogin(LoginBean login){
        this.login = login;
        PrefUtils.putBean(this,KeyOrValueGlobal.LOGINBEAN,login);
    }

    public LoginBean getLogin() {
        return login;
    }

//    public static RefWatcher getRefWatcher(Context context) {
//        MyApplication application = (MyApplication) context
//                .getApplicationContext();
//        return application.refWatcher;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
    //    refWatcher = LeakCanary.install(this);
        try {
            InputStream[] inputStreams = {getAssets().open("qmcer.cer")};
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(inputStreams, null, null);
            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                    .readTimeout(5000L, TimeUnit.MILLISECONDS)
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .build();
            OkHttpUtils.initClient(okHttpClient);
        } catch (IOException e) {
            e.printStackTrace();
        }
        aCache = ACache.get(this);
        context = this;
        //初始化 easyui
        EMOptions options = new EMOptions();
       // options.setMipushConfig("2882303761517500800", "5371750035800");//小米推送的
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(true);
        //...注：如果你的 APP 中有第三方的服务启动，请在初始化 SDK（EMClient.getInstance().init(applicationContext, options)）方法的前面添加以下相关代码（相应代码也可参考 Demo 的 application），使用 EaseUI 库的就不用理会这个。
        //初始化
        EaseUI.getInstance().init(this, options);
      //  EMClient.getInstance().init(this, options);
      //  EMClient.getInstance().updateCurrentUserNick(getSharedPreferences(GlobalField.USERINFO_FILENAME, MODE_PRIVATE).getString("username", "hdl"));//设置推送的昵称
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
      //  EMClient.getInstance().setDebugMode(true);
        // 设置全局的消息监听
        emMessageListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息----刷新一下当前页面喽
                try {
                    ConversationListFragment conversationListFragment = (ConversationListFragment) FragmentFactory.getFragment(1);
                    conversationListFragment.refresh();
                    EMClient.getInstance().chatManager().importMessages(messages);//保存到数据库
                    EMMessage emMessage = messages.get(0);
                    if(TextUtils.isEmpty(did)){
                        if(login!=null) {
                            did = login.did;
                        }else{
                            did = PrefUtils.getString(MyApplication.this,"did","");
                        }
                    }
                    HXUserCache.getInstance().updataDB(getContext(),emMessage,did);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageRead(List<EMMessage> list) {
                //收到已读回执
            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {
                //收到已送达回执
            }


            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        try{
            EMClient.getInstance().addConnectionListener(new MyConnectionListener(this));
            EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
        }catch (Exception e){
            e.printStackTrace();
        }
        EaseUI.getInstance().setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {
            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });
        //set notification options, will use default if you don't set it
        try {
            EaseUI.getInstance().getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

                @Override
                public String getTitle(EMMessage message) {
                    //you can update title here
                    return null;
                }

                @Override
                public int getSmallIcon(EMMessage message) {
                    //you can update icon here
                    return 0;
                }

                @Override
                public String getDisplayedText(EMMessage message) {
                    // be used on notification bar, different text according the message type.
                    String ticker = EaseCommonUtils.getMessageDigest(message, context);
                    if(message.getType() == EMMessage.Type.TXT){
                        ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                    }
                    EaseUser user = getUserInfo(message.getFrom());
                    if(user != null){
                        if(EaseAtMessageHelper.get().isAtMeMsg(message)){
                            return String.format("%s在群聊中@了你", user.getNick());
                        }
                        return user.getNick() + ":" + ticker;
                    }else{
                        if(EaseAtMessageHelper.get().isAtMeMsg(message)){
                            return String.format("%s在群聊中@了你", message.getFrom());
                        }
                        return message.getFrom() + ":" + ticker;
                    }
                }

                @Override
                public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                    // here you can customize the text.
                    // return fromUsersNum + "contacts send " + messageNum + "messages to you";
                    return null;
                }

                @Override
                public Intent getLaunchIntent(EMMessage message) {
                    // you can set what activity you want display when user click the notification
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("hx", message.getFrom());
                    return intent;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        // 添加友盟 各个平台的配置
//        static NSString *const WXKeyID = @"wxf81ca300a8b0e8fa";
//        static NSString *const WXKeySecret = @"cd0932e15f75ab006331067b80a3eb9d";
//        static NSString *const WXRedirectURL = @"https://api.green-bud.cn/wechat/ticket.php";
        //http://open.weibo.com/apps/2419969020/privilege/oauth
        Config.DEBUG = true ;
        PlatformConfig.setWeixin("wxf81ca300a8b0e8fa","cd0932e15f75ab006331067b80a3eb9d");
        PlatformConfig.setQQZone("1106135146","xzYhJN6SkZ9GXkct");
        PlatformConfig.setSinaWeibo("3147166331","25947efb7e676285c7428ed8d271a9f4","http://open.weibo.com/apps/2419969020/privilege/oauth");
        // 极光推送初始化
        JPushInterface.setDebugMode(true);
       // JPushInterface.setAlias();
        JPushInterface.init(this);
    }

    private EaseUser getUserInfo(String username){
        try{
            if(login!=null) {
                did = login.did;
            }else{
                did = PrefUtils.getString(this,"did","");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        EaseUser user = null;
        String doctorHXName = null;
        if(login == null) {
            login = (LoginBean) PrefUtils.getBean(this, KeyOrValueGlobal.LOGINBEAN);
        }
        if(login!=null){
            doctorHXName = login.hx_uname;
        }
        List<HXUserData> userDatas = null;
        if(username.equals(doctorHXName)){
            userDatas = DataSupport.where("hx_name = ? and did=?", username,did).find(HXUserData.class);
        }else{
            userDatas = DataSupport.where("hx_name = ? and doctordid=?", username,did).find(HXUserData.class);
        }
        if(userDatas!=null && userDatas.size()>0){
            user = new EaseUser(username);
            if(!userDatas.get(0).getAvatar().contains("http")){
                user.setAvatar(GlideUtils.SERVER_URL+userDatas.get(0).getAvatar());
            }else{
                user.setAvatar(userDatas.get(0).getAvatar());
            }
            if(!TextUtils.isEmpty(userDatas.get(0).getRemark_names())){
                user.setNick(userDatas.get(0).getRemark_names());
                user.setNickname(userDatas.get(0).getRemark_names());
            }else if(!TextUtils.isEmpty(userDatas.get(0).getUser_name())){
                user.setNick(userDatas.get(0).getUser_name());
                user.setNickname(userDatas.get(0).getUser_name());
            }else if(!TextUtils.isEmpty(userDatas.get(0).getNickname())){
                user.setNick(userDatas.get(0).getNickname());
                user.setNickname(userDatas.get(0).getNickname());
            }else if(!TextUtils.isEmpty(userDatas.get(0).getMobile())){
                user.setNick(userDatas.get(0).getMobile());
                user.setNickname(userDatas.get(0).getMobile());
            }
        }
        return user;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
    }

}
