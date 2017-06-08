package com.qingmiao.qmdoctor.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class PatientFriendListBean extends BaseBean {
    public int code;
    public String status;
    public FriendData data;

    public static class FriendData extends BaseBean{
        public List<UserFriendBean>  nomal_u ;
        public List<UserFriendBean>  marked_u;

    }

}
