package com.qingmiao.qmdoctor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.Type;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.exceptions.HyphenateException;
import com.mylhyl.superdialog.SuperDialog;
import com.nanchen.compresshelper.CompressHelper;
import com.qingmiao.qmdoctor.R;
import com.qingmiao.qmdoctor.bean.HXUserData;
import com.qingmiao.qmdoctor.bean.PicBean;
import com.qingmiao.qmdoctor.bean.SoundBean;
import com.qingmiao.qmdoctor.global.UrlGlobal;
import com.qingmiao.qmdoctor.presenter.LibelInfoPresenter;
import com.qingmiao.qmdoctor.utils.FileUtil;
import com.qingmiao.qmdoctor.utils.GetTime;
import com.qingmiao.qmdoctor.utils.GsonUtil;
import com.qingmiao.qmdoctor.utils.LogUtil;
import com.qingmiao.qmdoctor.utils.MD5Util;
import com.qingmiao.qmdoctor.utils.ToastUtils;
import com.qingmiao.qmdoctor.view.ILibelInfoView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

import static com.hyphenate.chat.EMMessage.Type.*;

public class ChatActivity extends BaseActivity implements ILibelInfoView {

    private EaseChatFragment chatFragment;
    private String hx_name;
    private String uid;
    private List<HXUserData> userDatas;
    private List<HXUserData> docuterDatas;
    private LibelInfoPresenter libelInfoPresenter ;
    EMImageMessageBody emImageMessageBody;
    EMVoiceMessageBody emVoiceMessageBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        hx_name = intent.getStringExtra("hx");
        uid = intent.getStringExtra("uid");
        ivLeft.setVisibility(View.VISIBLE);
        userDatas = DataSupport.where("hx_name = ? and doctordid=?", hx_name,did).find(HXUserData.class);
        docuterDatas = DataSupport.where("did=?",did).find(HXUserData.class);
        if(TextUtils.isEmpty(uid) && userDatas.size()>0){
            uid = userDatas.get(0).getUid();
        }
        if (userDatas.size() != 0) {
            if(!TextUtils.isEmpty(userDatas.get(0).getRemark_names())){
                tvCenter.setText(userDatas.get(0).getRemark_names());
            }else if(!TextUtils.isEmpty(userDatas.get(0).getUser_name())){
                tvCenter.setText(userDatas.get(0).getUser_name());
            }else if(!TextUtils.isEmpty(userDatas.get(0).getNickname())){
                tvCenter.setText(userDatas.get(0).getNickname());
            }else if(!TextUtils.isEmpty(userDatas.get(0).getMobile())){
                tvCenter.setText(userDatas.get(0).getMobile());
            }else{
                tvCenter.setText(hx_name);
            }
        } else {
            tvCenter.setText(hx_name);
        }
        ivRightBig2.setVisibility(View.VISIBLE);
        ivRightBig2.setText(R.string.icons_me);
        ivRightBig2.setBackgroundResource(R.drawable.shape_circle_bg_null);
        ivRightBig2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(uid != null) {
                    Intent intent = new Intent(ChatActivity.this, PatientDataActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                }else{
                    ToastUtils.showLongToast(ChatActivity.this,"该好友不存在");
                }
            }
        });
        initChatView();
    }

    private void initChatView() {
        libelInfoPresenter = new LibelInfoPresenter(this);
        chatFragment = new EaseChatFragment();
        final Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        args.putString(EaseConstant.EXTRA_USER_ID, hx_name);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.chat_FL, chatFragment).commit();
        chatFragment.setonOpenPhotoListener(new EaseChatFragment.OnOpenPhotoListener() {
            @Override
            public void onOpenPhoto() {
                PhotoPicker.builder()
                        .setShowCamera(true)
                        .setShowGif(true)
                        .setPreviewEnabled(false)
                        .start(ChatActivity.this, PhotoPicker.REQUEST_CODE);
            }

            @Override
            public void onOpenFile() {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,3);
            }
        });

        chatFragment.setChatFragmentListener(new EaseChatFragment.EaseChatFragmentHelper() {
            @Override
            public void onSetMessageAttributes(EMMessage message) {

            }

            @Override
            public void onEnterToChatDetails() {

            }

            @Override
            public void onAvatarClick(String username) {
                if (username.equals(hx_name)) {
                    if(uid != null) {
                        Intent intent = new Intent(ChatActivity.this, PatientDataActivity.class);
                        intent.putExtra("uid", uid);
                        startActivity(intent);
                    }else{
                        ToastUtils.showLongToast(ChatActivity.this,"该好友不存在");
                    }
                } else {
                    startActivity(new Intent(ChatActivity.this, DoctorDataActivity.class));
                }
            }

            @Override
            public void onAvatarLongClick(String username) {

            }

            @Override
            public boolean onMessageBubbleClick(EMMessage message) {
                return false;
            }

            @Override
            public void onMessageBubbleLongClick(EMMessage message) {
                // 消息的长按事件
                switch (message.getType()){
                    case TXT:
                    case VOICE:
                    case IMAGE:
                        final EMMessage mMSG = message;
                        showAlertDialog("温馨提示", "是否保存信息至患者描述内?", "取消", new SuperDialog.OnClickNegativeListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, "确定", new SuperDialog.OnClickPositiveListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    switch (mMSG.getType()){
                                        case TXT:
                                            Intent intent = new Intent(ChatActivity.this,PatientDescribeActivity.class);
                                            String nickName = docuterDatas.get(0).getShowName();
                                            EMTextMessageBody emTextMessageBody = (EMTextMessageBody) mMSG.getBody();
                                            intent.putExtra("data",emTextMessageBody.getMessage());
                                            intent.putExtra("avatar",docuterDatas.get(0)==null?"":docuterDatas.get(0).getAvatar());
                                            intent.putExtra("nickname",nickName);
                                            intent.putExtra("uid",uid);
                                            intent.putExtra("type",0);
                                            startActivity(intent);
                                            break;
                                        case VOICE:
                                            emVoiceMessageBody = (EMVoiceMessageBody) mMSG.getBody();
                                            String path = emVoiceMessageBody.getLocalUrl();
                                            File file = new File(path);
                                            if(!file.exists()){
                                                ToastUtils.showLongToast(ChatActivity.this,"当前文件不存在!");
                                                return ;
                                            }
                                            LinkedHashMap<String,String> linkedMap = new LinkedHashMap<String, String>();
                                            linkedMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                                            HashMap<String,File> hashFile = new HashMap<String, File>();
                                            hashFile.put(file.getName(),file);
                                            libelInfoPresenter.loadFileParems(UrlGlobal.UPLOAD_SOUND,linkedMap,"file",hashFile);
                                            break;
                                        case IMAGE:
                                            // 上传数据
                                            emImageMessageBody = (EMImageMessageBody) mMSG.getBody();
                                            File iFile = new File(emImageMessageBody.getLocalUrl());
                                            if(!iFile.exists()){
                                                ToastUtils.showLongToast(ChatActivity.this,"当前文件不存在!");
                                                return ;
                                            }
                                            LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<String, String>();
                                            linkedHashMap.put("sign", MD5Util.MD5(GetTime.getTimestamp()));
                                            HashMap<String,File> hashMap = new HashMap<String, File>();
                                            iFile = CompressHelper.getDefault(ChatActivity.this).compressToFile(iFile);
                                            hashMap.put(iFile.getName(),iFile);
                                            libelInfoPresenter.loadFileParems(UrlGlobal.UPLOAD_PIC,linkedHashMap,"file",hashMap);
                                            break;
                                        default:
                                            break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        break;
                    case FILE:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public boolean onExtendMenuItemClick(int itemId, View view) {
                return false;
            }

            @Override
            public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
                return null;
            }
        });

        List<HXUserData> mData = DataSupport.where("did = ?", did).find(HXUserData.class);

        if(mData!=null && mData.size()>0){
            // 消息体中设置了自己的信息
            Bundle attribute = new Bundle();
            attribute.putString("uid",did);
            attribute.putString("nickname",mData.get(0).getNickname());
            attribute.putString("avatar",mData.get(0).getAvatar());
            chatFragment.setMessageAttribute(attribute);

            // 设置自己和对方的信息  刷新界面
            Map<String,String> mapData = new HashMap<>();
            mapData.put("did",did);
            mapData.put("dNickname",mData.get(0).getNickname());
            mapData.put("dAvatar",mData.get(0).getAvatar());
            if(userDatas!=null  && userDatas.size()>0){
                mapData.put("uid",userDatas.get(0).getUid());
                mapData.put("uNickname",userDatas.get(0).getNickname());
                mapData.put("uAvatar",userDatas.get(0).getAvatar());
            }
            chatFragment.setMapData(mapData);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PhotoPicker.REQUEST_CODE:
                    if (data != null) {
                        ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        for (String photo : photos) {
                            EMMessage message = EMMessage.createImageSendMessage(photo, false, hx_name);
                            EMClient.getInstance().chatManager().sendMessage(message);
                            chatFragment.messageList.refreshSelectLast();
                        }
                    }
                    break;
                case 3:
                    if (data != null) {
                        Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                        String path = FileUtil.getFileAbsolutePath(ChatActivity.this, uri);
                        File file = new File(path);
                        if (!file.exists()) {
                            ToastUtils.showLongToast(ChatActivity.this,com.hyphenate.easeui.R.string.File_does_not_exist);
                            return;
                        }
                        //limit the size < 10M
                        if (file.length() > 10 * 1024 * 1024) {
                            ToastUtils.showLongToast(ChatActivity.this,com.hyphenate.easeui.R.string.The_file_is_not_greater_than_10_m);
                            return;
                        }
                        if(!TextUtils.isEmpty(path)){
                            chatFragment.sendFileMessage(path);
                        }
                    }
                    break;
            }

        }

    }


    @Override
    public void showLibelProgress(String uri) {
        showLoadingDialog(uri,"上传中");
    }

    @Override
    public void hideLibelProgress(String uri) {
        dismissLoadDialog();
    }

    @Override
    public void getLibelData(String uri, String data) {
        LogUtil.LogShitou(data);
        if(UrlGlobal.UPLOAD_PIC.equals(uri)){
            PicBean picBean = GsonUtil.getInstance().fromJson(data,PicBean.class);
            if(picBean.code!=0){
                ToastUtils.showLongToast(ChatActivity.this,"上传失败,请重新上传."+picBean.msg);
                return;
            }
            Intent intent = new Intent(ChatActivity.this,PatientDescribeActivity.class);
            intent.putExtra("data",emImageMessageBody.getLocalUrl());
            intent.putExtra("avatar",docuterDatas.get(0)==null?"":docuterDatas.get(0).getAvatar());
            intent.putExtra("nickname",docuterDatas.get(0).getShowName());
            intent.putExtra("type",2);
            intent.putExtra("uid",uid);
            intent.putExtra("pic",picBean);
            startActivity(intent);
        }else if(UrlGlobal.UPLOAD_SOUND.equals(uri)){
            SoundBean soundBean = GsonUtil.getInstance().fromJson(data,SoundBean.class);
            if(soundBean.code!=0){
                ToastUtils.showLongToast(ChatActivity.this,"上传失败,请重新上传."+soundBean.msg);
                return;
            }
            Intent intent = new Intent(ChatActivity.this,PatientDescribeActivity.class);
            intent.putExtra("data",emVoiceMessageBody.getLocalUrl());
            intent.putExtra("avatar",docuterDatas.get(0)==null?"":docuterDatas.get(0).getAvatar());
            intent.putExtra("nickname",docuterDatas.get(0).getShowName());
            intent.putExtra("type",1);
            intent.putExtra("uid",uid);
            intent.putExtra("sound",soundBean);
            startActivity(intent);
        }

    }
}
