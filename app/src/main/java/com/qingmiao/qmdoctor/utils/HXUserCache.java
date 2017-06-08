package com.qingmiao.qmdoctor.utils;

import android.content.ContentValues;
import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.qingmiao.qmdoctor.bean.HXUserData;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/10.
 */

public class HXUserCache {
    // 内存缓存
    private Map<String,HXUserData> dataMap = new HashMap<>();

    private static  HXUserCache cache = new HXUserCache();

    private HXUserCache(){

    }

    public static HXUserCache getInstance(){
        if(cache == null){
            synchronized (HXUserCache.class){
                if(cache == null){
                    cache = new HXUserCache();
                }
            }
        }
        return cache;
    }



    public void updataDB(Context context,EMMessage emMessage,String did){
        try {
            String uid = emMessage.getStringAttribute("uid");
            String nickname = emMessage.getStringAttribute("nickname");
            String avatar = emMessage.getStringAttribute("avatar");
            List<HXUserData> userDatas = DataSupport.where("uid = ?", uid).find(HXUserData.class);
            if(userDatas!=null && userDatas.size()>0){
                HXUserData userData = userDatas.get(0);
                if(!nickname.equals(userData.getNickname()) || !avatar.equals(userData.getAvatar())){
                    // 更新数据库，刷新界面头像和昵称
                    userData.setNickname(nickname);
                    userData.setAvatar(avatar);
                    userData.setHx_name(emMessage.getFrom());
                    userData.updateAll("uid = ?", uid);
                    //  发广播刷新界面
                    EventBus.getDefault().post("hx_ui_updata");
                }
            }else{
                HXUserData userData = new HXUserData();
                userData.setUid(uid);
                userData.setDoctordid(did);
                userData.setNickname(nickname);
                userData.setAvatar(avatar);
                userData.setHx_name(emMessage.getFrom());
                userData.save();
                //  发广播刷新界面
                EventBus.getDefault().post("hx_ui_updata");
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }






    }











}
