package com.qingmiao.qmdoctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class TagsListBean extends BaseBean {
    public int code;
    public String status;
    public List<Tags> data;

    public static class Tags{
        public String lab_id;
        public String did;
        public String tag_name;
        public String uids;
        public String time;
        public String count;
    }
}
