package com.qingmiao.qmdoctor.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/5/9.
 */

public class HXUserData extends DataSupport {
    // 患者id
    private String uid;
    // 与患者对应的医生的id
    private String doctordid;

    private String did;
    private String nickname;
    private String avatar;
    private String hx_name;

    private String mobile;
    private String remark_names;
    private String user_name;

    private String isFriend;
    //是否是标新好友
    private String isMarked;

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }

    public String getIsMarked() {
        return isMarked;
    }

    public void setIsMarked(String isMarked) {
        this.isMarked = isMarked;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }



    public String getHx_name() {
        return hx_name;
    }

    public void setHx_name(String hx_name) {
        this.hx_name = hx_name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemark_names() {
        return remark_names;
    }

    public void setRemark_names(String remark_names) {
        this.remark_names = remark_names;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getDoctordid() {
        return doctordid;
    }

    public void setDoctordid(String doctordid) {
        this.doctordid = doctordid;
    }
}
