package com.qingmiao.qmdoctor.bean;

import android.text.TextUtils;

/**
 * Created by Administrator on 2017/4/25.
 */

public class UserFriendBean extends BaseBean {
    public String avatar;
    public String hx_uname;
    public String mobile;
    public String nickname;
    public String remark_names;
    public String uid;
    public String user_name;
    public Object tags;

    public String getShowName(){
        if(!TextUtils.isEmpty(remark_names)){
            return remark_names;
        }else if(!TextUtils.isEmpty(user_name)){
            return user_name;
        }else if(!TextUtils.isEmpty(nickname)){
            return nickname;
        }else if(!TextUtils.isEmpty(mobile)){
            return mobile;
        }
        return "";
    }



}
