package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/6/27.
 */

public class NewFriendBean extends BaseBean {
    public int code;
    public String status;
    public NomalData data;


    public static class NomalData{
        public List<UserFriendBean> nomal_u;
    }
}
