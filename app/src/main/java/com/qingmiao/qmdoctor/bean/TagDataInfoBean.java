package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public class TagDataInfoBean extends BaseBean {
    public int code;
    public String status;
    public DataInfo data;

    public class DataInfo{
        public TagInfo tag_info;
        public List<UserFriendBean> user_info;

        public class TagInfo{
            public String count;
            public String tag_name;
            public String uids;

        }
    }
}
